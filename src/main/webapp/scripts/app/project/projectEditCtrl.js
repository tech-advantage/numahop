(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('ProjectEditCtrl', ProjectEditCtrl);

    function ProjectEditCtrl($location, $routeParams, $scope, $timeout, codeSrvc,
        DtoService, DocUnitBaseService, gettext, gettextCatalog, HistorySrvc, LibrarySrvc, ListTools,
        LotSrvc, MessageSrvc, ModalSrvc, NumaHopInitializationSrvc, Principal, ProjectSrvc, TrainSrvc,
        UserSrvc, ValidationSrvc, DeliverySrvc) {

        $scope.addLibrary = addLibrary;
        $scope.addUser = addUser;
        $scope.cancel = cancel;
        $scope.deleteLibrary = deleteLibrary;
        $scope.delete = deleteProject;
        $scope.cancelProject = cancelProject;
        $scope.suspendProject = suspendProject;
        $scope.reactivateProject = reactivateProject;
        $scope.deleteUser = deleteUser;
        $scope.duplicate = duplicate;
        $scope.filterAssociatedLibraries = filterAssociatedLibraries;
        $scope.filterAssociatedUsers = filterAssociatedUsers;
        $scope.libraryReadOnly = libraryReadOnly;
        $scope.listOtherProviders = listOtherProviders;
        $scope.onChangeLibrary = onChangeLibrary;
        $scope.semCodes = codeSrvc;
        $scope.validation = ValidationSrvc;
        $scope.openForm = openForm;
        $scope.saveProject = saveProject;
        $scope.displayBoolean = DocUnitBaseService.displayBoolean;
        $scope.displayStatus = displayStatus;


        // Définition des listes déroulantes
        $scope.options = {
            boolean: [
                { value: true, text: "Oui" },
                { value: false, text: "Non" }
            ],
            status: ProjectSrvc.config.status,
            sel2Libraries: {
                disable: isLibraryDisabled
            },
            sel2AssociatedUsers: {
                refresh: function (select) {
                    var libs = _.pluck($scope.project.associatedLibraries, "identifier");
                    if ($scope.project.library) {
                        libs.push($scope.project.library.identifier);
                    }
                    return UserSrvc.search({ search: select.search, libraries: libs }).$promise
                        .then(function (page) {
                            $scope.options.sel2AssociatedUsers.data = page.content;
                        });
                },
                "refresh-delay": 300
            }
        };

        $scope.translate = {
            trainStatus: {
                CREATED: codeSrvc["train.status.CREATED"],
                IN_PREPARATION: codeSrvc["train.status.IN_PREPARATION"],
                IN_DIGITIZATION: codeSrvc["train.status.IN_DIGITIZATION"],
                RECEIVING_PHYSICAL_DOCUMENTS: codeSrvc["train.status.RECEIVING_PHYSICAL_DOCUMENTS"],
                CLOSED: codeSrvc["train.status.CLOSED"],
                CANCELED: codeSrvc["train.status.CANCELED"]
            },
            lotStatus: {
                CREATED: codeSrvc["lot.status.CREATED"],
                ONGOING: codeSrvc["lot.status.ONGOING"],
                PENDING: codeSrvc["lot.status.PENDING"],
                CANCELED: codeSrvc["lot.status.CANCELED"],
                CLOSED: codeSrvc["lot.status.CLOSED"]
            },
            lotType: {
                PHYSICAL: gettextCatalog.getString("Physique"),
                DIGITAL: gettextCatalog.getString("Numérique")
            },
            boolean: {
                true: gettextCatalog.getString("Oui"),
                false: gettextCatalog.getString("Non")
            }
        };

        $scope.binding = { resp: "" };
        $scope.display = $location.search().display;
        $scope.loaded = false;

        $scope.accordions = {
            library: false,
            lot: false,
            project: true,
            train: false,
            users: false,
            delivery: false
        };

        // Filtrage des status
        $scope.canDisableProj = $scope.isAuthorized($scope.userRoles.PROJ_HAB5);
        $scope.canCancelProj = $scope.isAuthorized($scope.userRoles.PROJ_HAB6);

        init();


        /****************************************************************/
        /** Initialisation **********************************************/
        /****************************************************************/
        function init() {
            $scope.sel2Docs = DtoService.getDocs();
            Principal.identity().then(function (usr) {
                $scope.currentUser = usr;
            });
            // callback sur les listes d'UD
            if ($routeParams.callback) {
                $scope.saveCallback = function (projId) {
                    var params = {};
                    if (projId) {
                        params["project"] = projId;
                    }
                    $location.path($routeParams.callback).search(params);
                };
            }
            loadProject();
        }

        function loadProject() {
            // Duplication
            if ('duplicate' in $routeParams && angular.isDefined($routeParams.id)) {
                $scope.project = ProjectSrvc.duplicate({
                    id: $routeParams.id
                }, function (project) {
                    afterLoadingProject(project);
                });
            }
            // Chargement project
            else if (angular.isDefined($routeParams.id)) {
                $scope.project = ProjectSrvc.get({
                    id: $routeParams.id
                }, function (project) {
                    afterLoadingProject(project);
                });
                $scope.lots = loadLots($routeParams.id);
                $scope.trains = loadTrains($routeParams.id);
                $scope.deliveries = loadDeliveries($routeParams.id);
            }
            // Création d'un nouveau projet
            else if (angular.isDefined($routeParams.new)) {
                HistorySrvc.add(gettext("Nouveau projet"));
                $scope.project = new ProjectSrvc();
                $scope.project.active = true;
                $scope.project.status = "CREATED";
                afterLoadingProject($scope.project);
                openForm();

                if (angular.isDefined($scope.sel2Docs)) {
                    $scope.project.docUnits = $scope.sel2Docs;
                }
            }
        }

        function afterLoadingProject(project) {
            NumaHopInitializationSrvc.loadLibraries()
                .then(function (data) {
                    _.each(data, function (d) {
                        delete d['@id'];
                    });
                    $scope.options.sel2Libraries.data = data;
                    $scope.options.sel2AssociatedLibraries = data;

                    if ($scope.options.sel2Libraries.data.length === 1 && !$scope.project.library) {
                        $scope.project.library = $scope.options.sel2Libraries.data[0];
                    }

                    // Load other
                    loadPACS($scope.project.library);
                    loadCollections($scope.project.library);
                    loadOmekaCollections($scope.project.library);
                    loadOmekaItems($scope.project.library);
                    loadProviders($scope.project.library);
                    loadWorkflowModels($scope.project.library);
                    loadConfigurationSelect();
                    loadAll(project);
                });

        }

        // Initialisation une fois qu'on a reçu toutes les données du serveur
        function loadAll(value) {
            onSuccess(value);
            $scope.loaded = true;
        }

        function loadConfigurationSelect(library) {
            if (!library) {
                library = $scope.project.library;
            }
            if (!library) {
                $scope.options.ftp = [];
                $scope.options.check = [];
                $scope.options.format = [];
                return;
            }
            LibrarySrvc.get({
                id: library.identifier
            }, function (library) {
                $scope.options.ftp = library.ftpConfigurations;
                $scope.options.check = library.checkConfigurations;
                $scope.options.format = library.viewsFormatConfigurations;
            });
        }

        function displayStatus(status) {
            return $scope.options.status[status] || status;
        }

        /**
         * Chargement des lots
         */
        function loadLots(projectId) {
            return LotSrvc.query({ project: projectId });
        }

        /**
         * Chargement des trains
         */
        function loadTrains(projectId) {
            return TrainSrvc.query({ project: projectId });
        }

        /**
         * Chargement des livraisons
         */
        function loadDeliveries(projectId) {

            if ($scope.canCancelProj &&
                ($scope.project.status === 'CANCELED' || $scope.project.status === 'CLOSED')) {
                $scope.canCancelProj = false;
                $scope.canDisableProj = false;
            } else if ($scope.canDisableProj && $scope.project.status === 'PENDING') {
                $scope.canDisableProj = false;
            }

            var deliveries = DeliverySrvc.query({ project: projectId },
                function (delivs) {
                    if ($scope.canCancelProj) {
                        // Droits d'annulation : une livraison est commencée => niet !
                        var startedDeliverie = _.find(delivs, function (deliv) {
                            return deliv.status !== "SAVED";
                        });
                        if (angular.isDefined(startedDeliverie)) {
                            $scope.canCancelProj = false;
                        }
                    }
                    if ($scope.canDisableProj) {
                        // Droits mise en attente : une livraison en cours => niet !
                        var onGoingDelivery = _.find(delivs, function (deliv) {
                            return deliv.status === "DELIVERING";
                        });
                        if (angular.isDefined(onGoingDelivery)) {
                            $scope.canDisableProj = false;
                        }
                    }
                });
            return deliveries;
        }

        function loadProviders(library) {
            if (!library) {
                library = $scope.project.library;
            }
            if (!library) {
                $scope.options.providers = [];
                return;
            }
            NumaHopInitializationSrvc.loadProvidersForLibrary(library.identifier)
                .then(function (data) {
                    $scope.options.providers = data;
                });
        }

        function loadPACS(library) {
            if (!library) {
                library = $scope.project.library;
            }
            if (!library) {
                $scope.options.pacs = [];
                return;
            }
            NumaHopInitializationSrvc.loadPACS(library.identifier)
                .then(function (data) {
                    $scope.options.pacs = data;
                });
        }

        function loadCollections(library) {
            if (!library) {
                library = $scope.project.library;
            }
            if (!library) {
                $scope.options.collections = [];
                return;
            }
            NumaHopInitializationSrvc.loadCollections(library.identifier)
                .then(function (data) {
                    $scope.options.collections = data;
                });
        }
        
        function loadOmekaCollections(library) {
            if (!library) {
                library = $scope.project.library;
            }
            if (!library) {
                return;
            }
            NumaHopInitializationSrvc.loadOmekaCollections(library.identifier)
                .then(function (data) {
                    $scope.options.omekaCollections = data;
                });
        }
        
        function loadOmekaItems(library) {
            if (!library) {
                library = $scope.project.library;
            }
            if (!library) {
                return;
            }
            NumaHopInitializationSrvc.loadOmekaItems(library.identifier)
                .then(function (data) {
                    $scope.options.omekaItems = data;
                });
        }

        function loadWorkflowModels(library) {
            if (!library) {
                library = $scope.project.library;
            }
            if (!library) {
                $scope.options.workflowModels = [];
                return;
            }
            NumaHopInitializationSrvc.loadWorkflowModels(library.identifier)
                .then(function (data) {
                    $scope.options.workflowModels = data;
                });
        }

        function onChangeLibrary(library) {
            loadProviders(library);
            loadConfigurationSelect(library);
            loadPACS(library);
            loadCollections(library);
            loadOmekaCollections(library);
            loadOmekaItems(library);
            loadWorkflowModels(library);
        }

        function addLibrary() {
            if (angular.isDefined($scope.options.libraryAdded)) {
                if (angular.isUndefined($scope.project.associatedLibraries)) {
                    $scope.project.associatedLibraries = [];
                }
                $scope.options.libraryAdded.forEach(function (librarySelect) {
                    $scope.project.associatedLibraries.push(librarySelect);
                });
            }
        }

        function deleteLibrary(project, library) {
            if (angular.isDefined(project.associatedLibraries)) {
                var idx = project.associatedLibraries.indexOf(library);
                if (idx >= 0) {
                    project.associatedLibraries.splice(idx, 1);
                }
                if (!$scope.projectForm.$visible) {
                    saveProject($scope.project);
                }
            }
        }

        function addUser() {
            if (angular.isDefined($scope.options.userAdded)) {
                if (angular.isUndefined($scope.project.associatedUsers)) {
                    $scope.project.associatedUsers = [];
                }
                $scope.options.userAdded.forEach(function (userSelect) {
                    $scope.project.associatedUsers.push(userSelect);
                });
            }
        }

        function deleteUser(project, user) {
            if (angular.isDefined(project.associatedUsers)) {
                var idx = project.associatedUsers.indexOf(user);
                if (idx >= 0) {
                    project.associatedUsers.splice(idx, 1);
                }
                if (!$scope.projectForm.$visible) {
                    saveProject($scope.project);
                }
            }
        }

        /****************************************************************/
        /** Actions *****************************************************/
        /****************************************************************/

        function duplicate() {
            if ($scope.project) {
                $scope.loaded = false;
                $scope.project._selected = false;
                var identifier = $scope.project.identifier;
                $scope.project = null;
                $location.path("/project/project").search({ id: identifier, duplicate: true });
            }
        }

        function cancel() {
            if ($scope.saveCallback) {
                $scope.saveCallback();
            }
            else {
                $scope.projectForm.$cancel();
            }
        }

        /****************************************************************/
        /** Fonctions ***************************************************/
        /****************************************************************/
        // Sauvegarde un Projet
        function saveProject(project) {
            delete $scope.errors;

            $timeout(function () {
                var creation = angular.isUndefined(project.identifier) || project.identifier === null;

                project.$save({},
                    function (value) {
                        // Si un callback est défini, on l'appelle
                        if ($scope.saveCallback) {
                            $scope.saveCallback(value.identifier);
                        }
                        // Sinon on rafraichit l'affichage
                        else {
                            MessageSrvc.addSuccess(gettext("Le projet {{name}} a été sauvegardé"), { name: value.name });
                            onSuccess(value);
                            // si création, on ajoute à la liste, sinon, on essaye de MAJ les infos dans la colonne du milieu
                            if (creation) {
                                $scope.clearSelection();
                                addNewProjectToList(value, $scope.newProjects, $scope.pagination.items);
                                $location.search({ id: value.identifier }); // suppression des paramètres
                            } else {
                                $scope.updateProject($scope.project.identifier, $scope.project);
                            }
                        }
                    },
                    function (response) {
                        $scope.errors = _.chain(response.data.errors)
                            .groupBy("field")
                            .mapObject(function (list) {
                                return _.pluck(list, "code");
                            })
                            .value();

                        openForm();
                    });
            });
        }

        // Met à jour la liste avec le nouveau projet
        function addNewProjectToList(project, newProjects, libraries, lots, docs) {
            var found = _.find(newProjects, function (b) {
                return b.identifier === project.identifier;
            });
            if (found) {
                /** Si cette project existe déjà, ne rien faire */
                return;
            }
            found = _.find(libraries, function (b) {
                return b.identifier === project.identifier;
            });
            if (found) {
                /** Si cette project existe déjà, ne rien faire */
                return;
            }
            found = _.find(lots, function (b) {
                return b.identifier === lots.identifier;
            });
            if (found) {
                /** Si cette project existe déjà, ne rien faire */
                return;
            }
            found = _.find(docs, function (b) {
                return b.identifier === docs.identifier;
            });
            if (found) {
                /** Si cette project existe déjà, ne rien faire */
                return;
            }

            var newProject = {
                _selected: true,
                identifier: project.identifier,
                name: project.name
            };
            var i = 0;
            for (i = 0; i < newProjects.length; i++) {
                var b = newProjects[i];
                if (b.name.localeCompare(project.name) > 0) {
                    break;
                }
            }
            newProjects.splice(i, 0, newProject);
        }

        // Gestion du project renvoyée par le serveur
        function onSuccess(value) {
            HistorySrvc.add(gettextCatalog.getString("Projet {{name}}", $scope.project));
            $scope.project = value;
            displayMessages($scope.project);
        }

        /**
         * Suppression du projet
         * @param {*} project 
         */
        function deleteProject(project) {
            ModalSrvc.confirmDeletion(gettextCatalog.getString("du projet {{name}}", project))
                .then(function () {

                    project.$delete()
                        .then(function () {
                            MessageSrvc.addSuccess(gettext("Le projet {{name}} a été supprimé"), project);
                            $location.search({});
                            $scope.projectForm.$cancel();

                            var removed = ListTools.findAndRemoveItemFromList(project, $scope.pagination.items);
                            if (!removed) {
                                ListTools.findAndRemoveItemFromList(project, $scope.newProjects);
                            }
                        });
                });
        }

        /**
         * Annulation du projet.
         * @param {*} project 
         */
        function cancelProject(project) {

            ModalSrvc.confirmCancelWithComment(gettextCatalog.getString("Projet {{name}}", project))
                .then(function (comment) {

                    project.cancelingComment = comment;
                    project.status = 'CANCELED';
                    ProjectSrvc.cancelProject(project).$promise
                        .then(function (proj) {
                            MessageSrvc.addSuccess(gettext("Le projet {{name}} a été annulé"), proj);
                            $scope.project = proj;
                            $location.search({ active: false, id: proj.identifier });
                        });
                });
        }

        /**
         * Mise en attente du projet/lots.
         * @param {*} project
         */
        function suspendProject(project) {
            project.status = 'PENDING';
            ProjectSrvc.suspendProject(project).$promise
                .then(function (proj) {
                    MessageSrvc.addSuccess(gettext("Le projet {{name}} a été suspendu"), proj);
                    $scope.project = proj;
                    $scope.lots = loadLots($routeParams.id);
                });
        }

        /**
         * Reactive le projet en attente.
         * @param {*} project
         */
        function reactivateProject(project) {
            ProjectSrvc.reactivateProject(project).$promise
                .then(function (proj) {
                    MessageSrvc.addSuccess(gettext("Le projet {{name}} a été réactivé"), proj);
                    $scope.project = proj;
                    $scope.lots = loadLots($routeParams.id);
                });
        }

        // Ouverture du formulaire et des sous formulaires
        function openForm() {
            $timeout(function () {
                if (angular.isDefined($scope.projectForm)) {
                    $scope.projectForm.$show();
                }
            });
        }

        function displayMessages(entity) {
            // On commence par vider les messages précédents...
            MessageSrvc.clearMessages();
            // ... puis on affiche les infos de modification ...
            if (entity.active && angular.isDefined(entity.lastModifiedDate)) {
                var dateModif = new Date(entity.lastModifiedDate);
                MessageSrvc.addInfo("Dernière modification le {{date}} par {{author}}",
                    { date: dateModif.toLocaleString(), author: entity.lastModifiedBy }, true);
            }
            // ... puis on affiche les infos de création ...
            if (angular.isDefined(entity.createdDate)) {
                var dateCreated = new Date(entity.createdDate);
                MessageSrvc.addInfo("Créé le {{date}}",
                    { date: dateCreated.toLocaleString() }, true);
            }
            // ... et annulation éventuelle
            if (!entity.active && angular.isDefined(entity.realEndDate)) {
                var dateCanceling = new Date(entity.realEndDate);
                MessageSrvc.addInfo("Annulé le {{date}} : {{comment}}",
                    { date: dateCanceling.toLocaleDateString(), comment: entity.cancelingComment }, true);
            }
            // Affichage pour un temps limité à l'ouverture
            MessageSrvc.initPanel();
        }

        function libraryReadOnly() {
            return !$scope.options.sel2Libraries.data || $scope.options.sel2Libraries.data.length < 2;
        }

        function listOtherProviders() {
            var names = _.map($scope.project.otherProviders, function (provider) {
                return provider.firstname + " " + provider.surname;
            });
            return names.join(", ");
        }

        /**
         * La librairie est-elle désactivée dans la liste des librairies principales ?
         */
        function isLibraryDisabled(lib) {
            return _.some($scope.project.associatedLibraries, function (oth) {
                return lib.identifier === oth.identifier;
            });
        }

        /**
         * Filtrage de la liste des bibliothèques partenaires par rapport à celles déjà sélectionnées
         * 
         * @param {any} value 
         * @param {any} index 
         * @param {any} array 
         * @returns 
         */
        function filterAssociatedLibraries(value) {
            // la bibliothèque n'est pas celle du projet
            return (!$scope.project.library || $scope.project.library.identifier !== value.identifier)
                // elle n'est pas non plus sélectionnées
                && _.every($scope.project.associatedLibraries, function (lib) {
                    return lib.identifier !== value.identifier;
                });
        }

        /**
         * Filtrage de la liste des intervenants par rapport à ceux déjà sélectionnés
         * 
         * @param {any} value 
         * @param {any} index 
         * @param {any} array 
         * @returns 
         */
        function filterAssociatedUsers(value) {
            // l'intervenant n'est pas déjà sélectionné
            return _.every($scope.project.associatedUsers, function (user) {
                return user.identifier !== value.identifier;
            });
        }
    }
})();
