(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('OmekaConfigurationEditCtrl', OmekaConfigurationEditCtrl);

    function OmekaConfigurationEditCtrl(
        $location,
        $q,
        $routeParams,
        $scope,
        $timeout,
        OmekaConfigurationSrvc,
        codeSrvc,
        gettext,
        gettextCatalog,
        HistorySrvc,
        ListTools,
        NumahopEditService,
        MessageSrvc,
        ModalSrvc,
        NumaHopInitializationSrvc,
        ValidationSrvc,
        DocUnitBaseService,
        VIEW_MODES
    ) {
        $scope.semCodes = codeSrvc;
        $scope.preventDefault = NumahopEditService.preventDefault;
        $scope.viewModes = VIEW_MODES;
        $scope.validation = ValidationSrvc;
        $scope.displayBoolean = DocUnitBaseService.displayBoolean;
        $scope.saveConfiguration = saveConfiguration;

        // Définition des listes déroulantes
        $scope.options = {
            boolean: DocUnitBaseService.options.booleanObj,
        };

        $scope.viewMode = $routeParams.mode || $scope.viewModes.VIEW;

        $scope.binding = { resp: '' };
        $scope.loaded = false;

        $scope.accordions = {
            collections: true,
            items: true,
        };
        $scope.addCollection = function () {
            var newCollection = {
                type: 'COLLECTION',
            };
            if ($scope.configuration.omekaCollections) {
                $scope.configuration.omekaCollections.push(newCollection);
            } else {
                $scope.configuration.omekaCollections = [newCollection];
            }
        };
        $scope.deleteCollection = function (index) {
            $scope.configuration.omekaCollections.splice(index, 1);
        };

        $scope.addItem = function () {
            var newItem = {
                type: 'ITEM',
            };
            if ($scope.configuration.omekaItems) {
                $scope.configuration.omekaItems.push(newItem);
            } else {
                $scope.configuration.omekaItems = [newItem];
            }
        };
        $scope.deleteItem = function (index) {
            $scope.configuration.omekaItems.splice(index, 1);
        };

        init();

        /** Initialisation */
        function init() {
            loadLibrarySelect();
            loadConfiguration();
        }

        $scope.onaftersave = function () {
            var deferred = $q.defer();

            $timeout(function () {
                var savePromise = saveConfiguration($scope.configuration);
                savePromise
                    .then(function (value) {
                        deferred.resolve(value);
                    })
                    .catch(function (value) {
                        deferred.reject(value);
                    });

                if ($scope.viewMode === $scope.viewModes.EDIT) {
                    savePromise
                        .then(function (value) {
                            $scope.configurationForm.$cancel();
                        })
                        .catch(function (value) {
                            openForm();
                        });
                }
            });

            return deferred.promise;
        };
        //        $scope.showAdd = function (index, collection) {
        //            return index === (collection.length - 1) && ($scope.viewMode === $scope.viewModes.EDIT || index >= 0 && angular.isDefined(collection[collection.length - 1].identifier));
        //        };

        /****************************************************************/
        /** Actions *****************************************************/
        /****************************************************************/
        $scope.delete = function (configuration) {
            ModalSrvc.confirmDeletion(configuration.name).then(function () {
                configuration.$delete(function (value) {
                    MessageSrvc.addSuccess(gettext('La configuration {{name}} a été supprimée'), { name: value.name });
                    var removed = ListTools.findAndRemoveItemFromList(configuration, $scope.pagination.items);
                    if (removed) {
                        $scope.pagination.totalItems--;
                    } else {
                        ListTools.findAndRemoveItemFromList(configuration, $scope.newConfigurations);
                    }
                    $scope.backToList();
                });
            });
        };
        $scope.duplicate = function () {
            if ($scope.configuration) {
                $scope.loaded = false;
                $scope.configuration._selected = false;
                var identifier = $scope.configuration.identifier;
                $scope.configuration = null;
                $location.path('/platformconfiguration/omekaconfiguration').search({ id: identifier, mode: 'edit', duplicate: true });
            }
        };
        $scope.cancel = function () {
            if ($routeParams.new) {
                backToList();
            } else {
                $scope.configurationForm.$cancel();
            }
        };
        $scope.backToList = function () {
            $scope.loaded = false;
            // supprimer tous les paramètres
            $location.search({});
            $location.path('/platformconfiguration/omekaconfiguration');
        };

        /****************************************************************/
        /** Fonctions ***************************************************/
        /****************************************************************/
        // Sauvegarde une configuration
        function saveConfiguration() {
            delete $scope.errors;

            $timeout(function () {
                var creation = angular.isUndefined($scope.configuration.identifier) || $scope.configuration.identifier === null;
                var deferred = $q.defer();

                $scope.configuration.$save(
                    {},
                    function (value) {
                        MessageSrvc.addSuccess(gettext('La configuration {{name}} a été sauvegardée'), { name: value.name });
                        onSuccess(value);
                        deferred.resolve($scope.configuration);
                        // si création, on ajoute à la liste, sinon, on essaye de MAJ les infos dans la colonne du milieu
                        if (creation) {
                            $scope.clearSelection();
                            NumahopEditService.addNewEntityToList(value, $scope.newConfigurations, $scope.pagination.items, ['label']);
                        } else {
                            NumahopEditService.updateMiddleColumn($scope.configuration, $scope.pagination.items, $scope.newConfigurations);
                        }
                    },
                    function (httpResponse) {
                        ObjectTools.setObjectErrors($scope.configuration, httpResponse.data);
                        deferred.reject(httpResponse.data);
                    }
                );
                return deferred.promise;
            });
        }

        function loadLibrarySelect() {
            var deferred = $q.defer();
            $timeout(function () {
                var promise = NumaHopInitializationSrvc.loadLibraries();
                promise
                    .then(function (value) {
                        deferred.resolve(value);
                        $scope.options.libraries = value;
                    })
                    .catch(function (value) {
                        deferred.reject(value);
                    });
            });
            return deferred.promise;
        }

        // Gestion de la configuration renvoyée par le serveur
        function onSuccess(value) {
            $scope.configuration = value;

            HistorySrvc.add(gettextCatalog.getString('Configuration {{label}}', $scope.configuration));

            displayMessages();
        }
        // Ouverture du formulaire et des sous formulaires
        function openForm() {
            $timeout(function () {
                if (angular.isDefined($scope.configurationForm)) {
                    $scope.configurationForm.$show();
                }
            });
        }
        function displayMessages() {
            // On commence par vider les messages précédents...
            MessageSrvc.clearMessages();
            // ... puis rien pour l'instant
        }

        function afterLoadingConfiguration(configuration) {
            onSuccess(configuration);
            $scope.loaded = true;
        }

        function loadConfiguration() {
            if ('duplicate' in $routeParams && angular.isDefined($routeParams.id)) {
                // Duplication
                $scope.configuration = OmekaConfigurationSrvc.duplicate(
                    {
                        id: $routeParams.id,
                    },
                    function (configuration) {
                        afterLoadingConfiguration(configuration);
                    }
                );
            } else if (angular.isDefined($routeParams.id)) {
                // Chargement confgiuration
                $scope.configuration = OmekaConfigurationSrvc.get(
                    {
                        id: $routeParams.id,
                    },
                    function (configuration) {
                        afterLoadingConfiguration(configuration);
                    }
                );
            } else if ($scope.viewMode === $scope.viewModes.EDIT) {
                // Création d'une nouvelle configuration
                HistorySrvc.add(gettext('Nouvelle configuration'));
                $scope.configuration = new OmekaConfigurationSrvc();
                $scope.configuration.active = true;
                $scope.configuration.omekas = true;
                $scope.configuration.exportView = true;
                afterLoadingConfiguration($scope.configuration);
                openForm();
            }
        }

        // Clean
        $scope.$on('$destroy', function () {});
    }
})();
