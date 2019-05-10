(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('LibraryEditCtrl', LibraryEditCtrl);

    function LibraryEditCtrl($http, $location, $q, $routeParams, $scope, $timeout, codeSrvc, CONFIGURATION,
        DocUnitBaseService, gettext, gettextCatalog, HistorySrvc, LibrarySrvc, ListTools, NumahopEditService,
        MessageSrvc, ModalSrvc, NumaHopInitializationSrvc, ValidationSrvc) {

        $scope.semCodes = codeSrvc;
        $scope.deleteLogo = deleteLogo;
        $scope.setLogo = setLogo;
        $scope.validateLogin = validateLogin;
        $scope.validation = ValidationSrvc;
        $scope.openForm = openForm;
        $scope.saveLibrary = saveLibrary;
        $scope.displayBoolean = DocUnitBaseService.displayBoolean;

        // Définition des listes déroulantes
        $scope.options = {
            boolean: [
                { value: true, text: "Oui" },
                { value: false, text: "Non" }
            ]
        };

        $scope.binding = { resp: "" };
        $scope.loaded = false;

        init();


        /** Initialisation */
        function init() {
            loadLibrary();
            loadRoleSelect();
            //FIXME loadInstitutions si implémenté
        }

        function loadRoleSelect() {
            var deferred = $q.defer();
            $timeout(function () {
                var promise = NumaHopInitializationSrvc.loadRoles();
                promise.then(function (value) {
                    deferred.resolve(value);
                    $scope.options.role = value;
                }).catch(function (value) {
                    deferred.reject(value);
                });
            });
            return deferred.promise;
        }

        $scope.getAddressType = function (address) {
            if ($scope.viewMode === $scope.viewModes.EDIT) {
                return gettext("Adresse") + " " + ($scope.borrower.addresses.indexOf(address) + 1);
            }
            if (angular.isDefined(address.type) && angular.isDefined(address.identifier)) {
                if (address.type === CONFIGURATION.semantecSid.valueOtherCode) {
                    return address.otherType;
                } else {
                    return $scope.options.addressType[address.type];
                }
            }
        };
        $scope.showAdd = function (index, collection) {
            return index === (collection.length - 1) && ($scope.viewMode === $scope.viewModes.EDIT || index >= 0 && angular.isDefined(collection[collection.length - 1].identifier));
        };
        $scope.registerLibrary = function (category) {
            var search = { id: $scope.borrower.identifier, mode: "edit", category: category.identifier };
            $location.path("/library/library").search(search);
        };

        /****************************************************************/
        /** Actions *****************************************************/
        /****************************************************************/

        $scope.delete = function (library) {
            ModalSrvc.confirmDeletion(library.name)
                .then(function () {

                    library.$delete(function (value) {
                        MessageSrvc.addSuccess(gettext("La bibliothèque {{name}} a été supprimée"), { name: value.name });

                        var removed = ListTools.findAndRemoveItemFromList(library, $scope.pagination.items);
                        if (removed) {
                            $scope.pagination.totalItems--;
                        }
                        else {
                            ListTools.findAndRemoveItemFromList(library, $scope.newLibraries);
                        }
                        $scope.backToList();
                    });
                });
        };
        $scope.duplicate = function () {
            if ($scope.library) {
                $scope.loaded = false;
                $scope.library._selected = false;
                var identifier = $scope.library.identifier;
                $scope.library = null;
                $location.path("/library/library").search({ id: identifier, mode: "edit", duplicate: true });
            }
        };
        $scope.cancel = function () {
            // Fin de l'édition du formulaire: on repasse en mode vue
            $scope.libraryForm.$cancel();
        };
        $scope.backToList = function () {
            $scope.loaded = false;
            // supprimer tous les paramètres
            $location.search({});
            $location.path("/library/library");
        };
        $scope.goToAllUsers = function () {
            $location.path("/user/user").search({ library: $scope.library.identifier });
        };
        $scope.goToAllProjects = function () {
            $location.path("/project/project").search({ library: $scope.library.identifier });
        };

        /****************************************************************/
        /** Fonctions ***************************************************/
        /****************************************************************/
        // Sauvegarde une bibliothèque
        function saveLibrary(library) {
            delete $scope.errors;

            $timeout(function () {
                var creation = angular.isUndefined(library.identifier) || library.identifier === null;

                library.$save({},
                    function (value) {
                        MessageSrvc.addSuccess(gettext("La bibliothèque {{name}} a été sauvegardée"), { name: value.name });
                        onSuccess(value);
                        // si création, on ajoute à la liste, sinon, on essaye de MAJ les infos dans la colonne du milieu
                        if (creation) {
                            $scope.clearSelection();
                            addNewLibraryToList(value, $scope.newLibraries, $scope.pagination.items);
                            $location.search({ id: value.identifier }); // suppression des paramètres
                        } else {
                            $scope.updateLibrary($scope.library.identifier, $scope.library);
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
        // Met à jour la liste avec la nouvelle bibliothèque
        function addNewLibraryToList(library, newLibraries, libraries) {
            var found = _.find(newLibraries, function (b) {
                return b.identifier === library.identifier;
            });
            if (found) {
                /** Si cette bibliothèque existe déjà, ne rien faire */
                return;
            }
            found = _.find(libraries, function (b) {
                return b.identifier === library.identifier;
            });
            if (found) {
                /** Si cette bibliothèque existe déjà, ne rien faire */
                return;
            }

            var newLibrary = {
                _selected: true,
                identifier: library.identifier,
                name: library.name
            };
            var i = 0;
            for (i = 0; i < newLibraries.length; i++) {
                var b = newLibraries[i];
                if (b.name.localeCompare(library.name) > 0) {
                    break;
                }
            }
            newLibraries.splice(i, 0, newLibrary);
        }
        // Gestion de la bibliothèque renvoyée par le serveur
        function onSuccess(value) {
            $scope.library = value;
            HistorySrvc.add(gettextCatalog.getString("Bibliothèque {{name}}", $scope.library));
            displayMessages($scope.library);
        }
        // Ouverture du formulaire et des sous formulaires
        function openForm() {
            $timeout(function () {
                if (angular.isDefined($scope.libraryForm)) {
                    $scope.libraryForm.$show();
                }
            });
        }
        function displayMessages(entity) {
            // On commence par vider les messages précédents...
            MessageSrvc.clearMessages();
            // ... puis on affiche les infos de modification ...
            if (angular.isDefined(entity.lastModifiedDate)) {
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
            // Affichage pour un temps limité à l'ouverture
            MessageSrvc.initPanel();
        }

        // Initialisation une fois qu'on a reçu toutes les données du serveur
        function loadAll(value) {
            onSuccess(value);
            $scope.loaded = true;
        }

        function afterLoadingLibrary(library) {
            $scope.options.ftp = library.ftpConfigurations;
            $scope.options.check = library.checkConfigurations;
            $scope.options.format = library.viewsFormatConfigurations;
            $scope.options.ocrLang = library.ocrLangConfigurations;

            loadAll(library);

            $scope.hasLogo = {};
            if ($routeParams.id) {
                LibrarySrvc.hasLogo({}, library).$promise
                    .then(function (result) {
                        $scope.hasLogo = result[library.identifier];
                        refreshThumbnail();
                    });
            }
        }

        function loadLibrary() {
            $scope.hasLogo = false;

            if ('duplicate' in $routeParams && angular.isDefined($routeParams.id)) {
                // Duplication
                $scope.library = LibrarySrvc.duplicate({
                    id: $routeParams.id
                }, function (library) {
                    afterLoadingLibrary(library);
                });
            } else if (angular.isDefined($routeParams.id)) {
                // Chargement bibliothèque
                $scope.library = LibrarySrvc.get({
                    id: $routeParams.id
                }, function (library) {
                    afterLoadingLibrary(library);
                });
            } else if (angular.isDefined($routeParams.new)) {
                // Création d'une nouvelle bibliothèque
                HistorySrvc.add(gettext("Nouvelle bibliothèque"));
                $scope.library = new LibrarySrvc();
                $scope.library.active = true;
                afterLoadingLibrary($scope.library);
                openForm();
            }
        }

        /** Validation */
        function validateLogin(value, borrower) {
            return !borrower.cardId ? ValidationSrvc.required(value) : true;
        }

        /** Empêcher la sauvegarde du formulaire lors d'un clic sur entrée */
        function preventDefault(event) {
            if ($scope.viewMode === $scope.viewModes.EDIT) {
                event.preventDefault();
            }
        }

        /**
         * Sélection du logo de la bibliothèque
         */
        function setLogo(element) {
            if (element.files.length > 0) {
                $scope.$apply(function (scope) {
                    var logo = element.files[0];
                    var url = CONFIGURATION.numahop.url + 'api/rest/library/' + $scope.library.identifier;

                    var formData = new FormData();
                    formData.append("logo", true);
                    formData.append("file", logo);

                    var config = {
                        transformRequest: angular.identity,
                        headers: {
                            'Content-Type': undefined
                        }
                    };
                    $http.post(url, formData, config)
                        .success(function (data, status) {
                            MessageSrvc.addSuccess(gettext("Le logo a été mis à jour"));
                            $scope.hasLogo = true;
                            refreshThumbnail();
                        })
                        .error(function (data, status) {
                            MessageSrvc.addError(gettext("Échec lors du téléversement du logo"));
                        });
                });
            }
        }

        /**
         * Suppression du logo
         * 
         * @param {any} library 
         */
        function deleteLogo(library) {
            LibrarySrvc.deleteLogo({ id: library.identifier }).$promise
                .then(function () {
                    MessageSrvc.addSuccess(gettext("Le logo a été supprimé"));
                    $scope.hasLogo = false;
                    refreshThumbnail();
                })
                .catch(function () {
                    MessageSrvc.addError(gettext("Échec lors de la suppression du logo"));
                });

        }

        /**
         * Rafraichissement de l'aperçu du logo
         */
        function refreshThumbnail() {
            $scope._library_thumbnail = "/api/rest/library/" + $scope.library.identifier + "?thumbnail=true&ts=" + new Date().getTime();
        }
    }
})();
