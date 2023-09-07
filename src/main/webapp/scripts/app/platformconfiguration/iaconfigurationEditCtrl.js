(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('IAConfigurationEditCtrl', IAConfigurationEditCtrl);

    function IAConfigurationEditCtrl(
        $location,
        $q,
        $routeParams,
        $scope,
        $timeout,
        IAConfigurationSrvc,
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
        VIEW_MODES
    ) {
        $scope.semCodes = codeSrvc;
        $scope.preventDefault = NumahopEditService.preventDefault;
        $scope.validation = ValidationSrvc;
        //only mandatory because we needed view_modes in HTML
        $scope.VIEW_MODES = VIEW_MODES;

        // Définition des listes déroulantes
        $scope.options = {
            boolean: {
                true: gettext('Oui'),
                false: gettext('Non'),
            },
        };

        $scope.viewMode = $routeParams.mode || VIEW_MODES.VIEW;

        $scope.binding = { resp: '' };
        $scope.loaded = false;

        $scope.accordions = {
            collections: true,
        };
        $scope.addCollection = function () {
            var newCollection = {
                confIa: $scope.configuration,
            };
            if ($scope.configuration.collections) {
                $scope.configuration.collections.push(newCollection);
            } else {
                $scope.configuration.collections = [newCollection];
            }
        };
        $scope.deleteCollection = function (index) {
            $scope.configuration.collections.splice(index, 1);
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

                if ($scope.viewMode === VIEW_MODES.EDIT) {
                    savePromise
                        .then(function (value) {
                            $scope.setViewMode(VIEW_MODES.VIEW);
                        })
                        .catch(function (value) {
                            openForm();
                        });
                }
            });

            return deferred.promise;
        };
        $scope.showAdd = function (index, collection) {
            return index === collection.length - 1 && ($scope.viewMode === VIEW_MODES.EDIT || (index >= 0 && angular.isDefined(collection[collection.length - 1].identifier)));
        };

        /****************************************************************/
        /** Actions *****************************************************/
        /****************************************************************/
        $scope.save = function () {
            if (angular.isDefined($scope.configurationForm)) {
                $scope.configurationForm.$submit();
            }
        };
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
                $location.path('/platformconfiguration/iaconfiguration').search({ id: identifier, mode: 'edit', duplicate: true });
            }
        };
        $scope.cancel = function () {
            $scope.setViewMode(VIEW_MODES.VIEW);
        };
        $scope.backToList = function () {
            $scope.loaded = false;
            // supprimer tous les paramètres
            $location.search({});
            $location.path('/platformconfiguration/iaconfiguration');
        };
        $scope.setViewMode = function (mode) {
            if (angular.isDefined($scope.configuration.identifier)) {
                $scope.loaded = false;
                $location.search({ id: $scope.configuration.identifier, mode: mode });
            } else {
                $scope.backToList();
            }
        };

        /****************************************************************/
        /** Fonctions ***************************************************/
        /****************************************************************/
        // Sauvegarde une configuration
        function saveConfiguration(configuration) {
            // console.log(configuration);
            // console.log($scope.configuration);
            var creation = angular.isUndefined(configuration.identifier) || configuration.identifier === null;
            var deferred = $q.defer();

            configuration.$save(
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

        // Initialisation une fois qu'on a reçu toutes les données du serveur
        function loadAll(value) {
            onSuccess(value);

            openForm();
            $scope.loaded = true;
        }

        function afterLoadingConfiguration(configuration) {
            loadAll(configuration);
        }

        function loadConfiguration() {
            if ('duplicate' in $routeParams && angular.isDefined($routeParams.id)) {
                // Duplication
                $scope.configuration = IAConfigurationSrvc.duplicate(
                    {
                        id: $routeParams.id,
                    },
                    function (configuration) {
                        afterLoadingConfiguration(configuration);
                    }
                );
            } else if (angular.isDefined($routeParams.id)) {
                // Chargement confgiuration
                $scope.configuration = IAConfigurationSrvc.get(
                    {
                        id: $routeParams.id,
                    },
                    function (configuration) {
                        afterLoadingConfiguration(configuration);
                    }
                );
            } else if ($scope.viewMode === VIEW_MODES.EDIT) {
                // Création d'une nouvelle configuration
                HistorySrvc.add(gettext('Nouvelle configuration'));
                $scope.configuration = new IAConfigurationSrvc();
                $scope.configuration.active = true;
                afterLoadingConfiguration($scope.configuration);
            }
        }

        // Clean
        $scope.$on('$destroy', function () {});
    }
})();
