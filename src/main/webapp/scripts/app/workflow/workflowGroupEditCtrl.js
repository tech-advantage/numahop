(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('WorkflowGroupEditCtrl', WorkflowGroupEditCtrl);

    function WorkflowGroupEditCtrl($location, $q, $routeParams, $scope, $timeout, codeSrvc,
        gettext, gettextCatalog, HistorySrvc, ListTools, Principal, NumahopEditService,
        MessageSrvc, ModalSrvc, ValidationSrvc, WorkflowGroupSrvc, NumaHopInitializationSrvc, VIEW_MODES) {

        $scope.semCodes = codeSrvc;
        $scope.preventDefault = NumahopEditService.preventDefault;
        $scope.viewModes = VIEW_MODES;
        $scope.validation = ValidationSrvc;
        $scope.fullName = getFullName;
        $scope.saveEntity = saveEntity;
        $scope.onChangeLibrary = onChangeLibrary;
        $scope.addUser = addUser;
        $scope.deleteUser = deleteUser;
        $scope.cancel = cancel;

        // Gestion des vues
        $scope.viewMode = $routeParams.mode || $scope.viewModes.VIEW;
        $scope.loaded = false;

        init();


        /****************************************************************/
        /** Initialisation **********************************************/
        /****************************************************************/
        function init() {
            loadOptions();
        }

        function loadEntity() {
            if (angular.isDefined($routeParams.id)) {
                /** Chargement de l'entité **/
                $scope.group = WorkflowGroupSrvc.get({
                    id: $routeParams.id
                }, function (entity) {
                    afterLoadingEntity(entity);
                });
            }
            // Création d'un nouveau projet
            else if (angular.isDefined($routeParams.new)) {
                HistorySrvc.add(gettext("Nouveau groupe de workflow"));
                $scope.group = new WorkflowGroupSrvc();
                var currentUser = Principal.identity();
                if (angular.isDefined(currentUser)) {
                    currentUser.then(function (result) {
                        $scope.group.library = $scope.sel2Libraries.find(function (v) {
                            return v.identifier === result.library;
                        });
                    });
                }
                afterLoadingEntity($scope.group);
                openForm();
            }
        }

        function loadOptions() {
            var currentUser = Principal.identity();
            if (angular.isDefined(currentUser)) {
                currentUser.then(function (result) {
                    if (result.library) {
                        $q.all([NumaHopInitializationSrvc.loadLibraries(), NumaHopInitializationSrvc.loadUsersForLibrary(result.library)])
                            .then(function (data) {
                                $scope.sel2Libraries = data[0];
                                $scope.sel2Users = data[1];
                                loadEntity();
                            });
                    } else {
                        $q.all([NumaHopInitializationSrvc.loadLibraries()])
                            .then(function (data) {
                                $scope.sel2Libraries = data[0];
                                loadEntity();
                            });
                    }
                });
            }
        }

        /****************************************************************/
        /** Actions *****************************************************/
        /****************************************************************/
        $scope.delete = function (entity) {
            ModalSrvc.confirmDeletion(entity.name)
                .then(function () {
                    entity.$delete(function (value) {
                        MessageSrvc.addSuccess(gettext("Le groupe de workflow {{id}} a été supprimé"), { id: value.name });
                        $location.search({});
                        $scope.entityForm.$cancel();
                        var removed = ListTools.findAndRemoveItemFromList(entity, $scope.pagination.items);
                        if (!removed) {
                            ListTools.findAndRemoveItemFromList(entity, $scope.newEntities);
                        }
                    });
                });
        };

        function cancel() {
            if ($scope.saveCallback) {
                $scope.saveCallback();
            }
            else {
                $scope.entityForm.$cancel();
            }
        }

        /****************************************************************/
        /** Liens *******************************************************/
        /****************************************************************/

        /****************************************************************/
        /** Fonctions ***************************************************/
        /****************************************************************/
        function onChangeLibrary(library) {
            $q.all([NumaHopInitializationSrvc.loadUsersForLibrary(library.identifier)])
                .then(function (data) {
                    $scope.sel2Users = data[0];
                    $scope.group.users = [];
                    $scope.options.userAdded = null;
                });
        }
        function getFullName(user) {
            return user.surname + " " + user.firstname;
        }
        /**
         * Utilisateurs
         */
        function addUser() {
            if (angular.isDefined($scope.options.userAdded)) {
                if (angular.isUndefined($scope.group.users)) {
                    $scope.group.users = [];
                }
                $scope.options.userAdded.forEach(function (user) {
                    $scope.group.users.push(user);
                });
            }
        }
        function deleteUser(group, user) {
            if (angular.isDefined(group.users)) {
                var idx = group.users.indexOf(user);
                if (idx >= 0) {
                    group.users.splice(idx, 1);
                }
                if (!$scope.entityForm.$visible) {
                    saveEntity($scope.group);
                }
            }
        }
        /** Sauvegarde une entité **/
        function saveEntity(entity) {
            delete $scope.errors;
            if (entity.users.length === 0) {
                MessageSrvc.addError(gettext("Le groupe de workflow {{name}} doit contenir au moins 1 utilisateur"), { name: entity.name });
            } else {
                $timeout(function () {
                    var creation = angular.isUndefined(entity.identifier) || entity.identifier === null;

                    entity.$save({},
                        function (value) {
                            MessageSrvc.addSuccess(gettext("Le groupe de workflow {{name}} a été sauvegardé"), { name: value.name });
                            onSuccess(value);
                            // si création, on ajoute à la liste, sinon, on essaye de MAJ les infos dans la colonne du milieu
                            if (creation) {
                                $scope.clearSelection();
                                NumahopEditService.addNewEntityToList(value, $scope.newEntities, $scope.pagination.items, ["name"]);
                                $location.search({ id: value.identifier }); // suppression des paramètres
                            } else {
                                NumahopEditService.updateMiddleColumn($scope.group, ["name"],
                                    $scope.pagination.items, $scope.newEntities);
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
        }

        // Gestion de l'entité renvoyée par le serveur
        function onSuccess(value) {
            $scope.group = value;
            if ((!$scope.sel2Users || $scope.sel2Users.length === 0) && $scope.group.library) {
                $q.all([NumaHopInitializationSrvc.loadUsersForLibrary($scope.group.library.identifier)])
                    .then(function (data) {
                        $scope.sel2Users = data[0];
                    });
            }

            HistorySrvc.add(gettextCatalog.getString("Groupe de workflow") + ": " + $scope.group.name);

            displayMessages($scope.group);
        }
        // Ouverture du formulaire et des sous formulaires
        function openForm() {
            $timeout(function () {
                if (angular.isDefined($scope.entityForm)) {
                    $scope.entityForm.$show();
                }
            });
        }
        // Initialisation des formulaires
        function displayMessages(entity) {
            // On commence par vider les messages précédents...
            MessageSrvc.clearMessages();

            // ... puis on affiche les infos de création ...
            if (angular.isDefined(entity.createdDate)) {
                var dateCreated = new Date(entity.createdDate);
                MessageSrvc.addInfo(gettext("Créé le {{date}}"),
                    { date: dateCreated.toLocaleString() }, true);
            }
            // ... puis on affiche les infos de modification ...
            if (angular.isDefined(entity.lastModifiedDate)) {
                var dateModif = new Date(entity.lastModifiedDate);
                MessageSrvc.addInfo(gettext("Dernière modification le {{date}} par {{author}}"),
                    { date: dateModif.toLocaleString(), author: entity.lastModifiedBy }, true);
            }

            // Affichage pour un temps limité à l'ouverture
            MessageSrvc.initPanel();
        }

        // Initialisation une fois qu'on a reçu toutes les données du serveur
        function loadAll(value) {
            onSuccess(value);
            $scope.loaded = true;
        }

        function afterLoadingEntity(entity) {
            loadAll(entity);
        }
    }
})();
