(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('SFTPConfigurationEditCtrl', SFTPConfigurationEditCtrl);

    function SFTPConfigurationEditCtrl(
        $http,
        $location,
        $q,
        $routeParams,
        $scope,
        $timeout,
        SFTPConfigurationSrvc,
        codeSrvc,
        CONFIGURATION,
        gettext,
        gettextCatalog,
        HistorySrvc,
        ListTools,
        LibraryParameterSrvc,
        CinesLangCodeSrvc,
        NumahopEditService,
        MessageSrvc,
        ModalSrvc,
        NumaHopInitializationSrvc,
        ValidationSrvc,
        VIEW_MODES
    ) {
        $scope.semCodes = codeSrvc;
        $scope.preventDefault = NumahopEditService.preventDefault;
        $scope.viewModes = VIEW_MODES;
        $scope.validation = ValidationSrvc;

        $scope.viewMode = $routeParams.mode || $scope.viewModes.VIEW;

        $scope.binding = { resp: '' };
        $scope.loaded = false;

        $scope.files = [];
        $scope.setFile = setFile;
        $scope.loadPacs = loadPacs;
        $scope.cinesDefaultValues = {};
        $scope.cinesLangCodes = [];

        $scope.accordions = {
            pacs: false,
            defaultValues: false,
            langCodes: false,
        };

        $scope.addPac = function () {
            var newPac = {
                confPac: $scope.configuration,
            };
            if ($scope.configuration.pacs) {
                $scope.configuration.pacs.push(newPac);
            } else {
                $scope.configuration.pacs = [newPac];
            }
        };
        $scope.deletePac = function (index) {
            $scope.configuration.pacs.splice(index, 1);
        };

        $scope.addCodeLang = function () {
            var newCodeLang = {
                langDC: '',
                label: '',
            };
            if ($scope.cinesLangCodes) {
                $scope.cinesLangCodes.push(newCodeLang);
            } else {
                $scope.cinesLangCodes = [newCodeLang];
            }
        };
        $scope.deleteCodeLang = function (index) {
            $scope.cinesLangCodes.splice(index, 1);
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
                            $scope.setViewMode($scope.viewModes.VIEW);
                        })
                        .catch(function (value) {
                            openForm();
                        });
                }
            });
            return deferred.promise;
        };

        $scope.showAdd = function (index, collection) {
            return index === collection.length - 1 && ($scope.viewMode === $scope.viewModes.EDIT || (index >= 0 && angular.isDefined(collection[collection.length - 1].identifier)));
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
                $location.path('/platformconfiguration/sftpconfiguration').search({
                    id: identifier,
                    mode: 'edit',
                    duplicate: true,
                });
            }
        };
        $scope.cancel = function () {
            $scope.setViewMode($scope.viewModes.VIEW);
        };
        $scope.backToList = function () {
            $scope.loaded = false;
            // supprimer tous les paramètres
            $location.search({});
            $location.path('/platformconfiguration/sftpconfiguration');
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
            // sauvegarde des valeurs defaut pour cines.
            if ($scope.configuration.cinesDefaultValues && $scope.configuration.cinesDefaultValues.identifier) {
                LibraryParameterSrvc.save({ sftpConfig: $scope.configuration.cinesDefaultValues.identifier }, $scope.configuration.cinesDefaultValues, function (value) {
                    loadParams();
                });
            }

            // sauvegarde des codes lang Cines.
            CinesLangCodeSrvc.update({}, $scope.cinesLangCodes, function (value) {
                $scope.cinesLangCodes = value;
            });

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
            HistorySrvc.add(gettextCatalog.getString('Configuration {{label}}', $scope.configuration.label));
            displayMessages();
        }

        // Ouverture du formulaire et des sous formulaires
        function openForm() {
            $timeout(function () {
                if (angular.isDefined($scope.configurationForm)) {
                    $scope.configurationForm.enctype = 'multipart/form-data';
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
            loadConfPacs(configuration);
            loadParams();
            loadAll(configuration);
        }

        /**
         * Chargement des modules parametres defaut et codes lang. cines.
         */
        function loadParams() {
            if (angular.isDefined($routeParams.id)) {
                $q.all([NumaHopInitializationSrvc.loadCinesParamsDefaultValues($routeParams.id), CinesLangCodeSrvc.loadActiveCinesCodes().$promise]).then(function (data) {
                    $scope.configuration.cinesDefaultValues = data[0];
                    $scope.cinesLangCodes = data[1];
                });
            }
        }

        function loadConfiguration() {
            if ('duplicate' in $routeParams && angular.isDefined($routeParams.id)) {
                // Duplication
                $scope.configuration = SFTPConfigurationSrvc.duplicate(
                    {
                        id: $routeParams.id,
                    },
                    function (configuration) {
                        afterLoadingConfiguration(configuration);
                    }
                );
            } else if (angular.isDefined($routeParams.id)) {
                // Chargement confgiuration
                $scope.configuration = SFTPConfigurationSrvc.get(
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
                $scope.configuration = new SFTPConfigurationSrvc();
                $scope.configuration.active = true;
                afterLoadingConfiguration($scope.configuration);
            }
        }

        /**
         * Charge les Pacs depuis le fichier ppdi uploadé (multipart).
         */
        function loadPacs() {
            if ($scope.files[0]) {
                var formData = new FormData();
                formData.append('upload', 'true');
                formData.append('file', $scope.files[0]);

                $timeout(function () {
                    SFTPConfigurationSrvc.uploadDpdi({ id: $routeParams.id }, formData, function (value) {
                        $scope.configuration = value;
                    });
                });
            }
        }

        /** Sélection du fichier dpdi à charger */
        function setFile(element) {
            if (element.files.length > 0) {
                $scope.$apply(function (scope) {
                    // Turn the FileList object into an Array
                    $scope.files = _.map(element.files, angular.identity);
                });
            }
        }

        function loadConfPacs(configuration) {
            SFTPConfigurationSrvc.confPacs({ configuration: configuration.identifier }).$promise.then(function (pacs) {
                _.map(pacs, function (pac) {
                    return (pac.confPac = configuration);
                });
                $scope.configuration.pacs = pacs;
            });
        }

        // Clean
        $scope.$on('$destroy', function () {});
    }
})();
