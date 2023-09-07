(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('ExportFTPConfigurationEditCtrl', ExportFTPConfigurationEditCtrl);

    function ExportFTPConfigurationEditCtrl(
        $location,
        $routeParams,
        $timeout,
        codeSrvc,
        DocUnitBaseService,
        ExportFTPConfigurationSrvc,
        gettext,
        gettextCatalog,
        HistorySrvc,
        ListTools,
        MessageSrvc,
        ModalSrvc,
        NumaHopInitializationSrvc,
        ValidationSrvc,
        VIEW_MODES,
        $scope
    ) {
        var ctrl = this;

        ctrl.semCodes = codeSrvc;
        ctrl.validation = ValidationSrvc;
        ctrl.VIEW_MODES = VIEW_MODES;

        // Définition des listes déroulantes
        ctrl.options = {
            boolean: DocUnitBaseService.options.booleanObj,
            libraries: [],
        };

        ctrl.viewMode = $routeParams.mode || VIEW_MODES.VIEW;

        init();

        /** Initialisation */
        function init() {
            loadLibrarySelect();
            loadConfiguration();
            ctrl.isFileTypeOpened = true;
            ctrl.isDeliveryConfOpened = true;
        }

        function loadLibrarySelect() {
            return NumaHopInitializationSrvc.loadLibraries().then(function (libs) {
                ctrl.options.libraries = libs;
            });
        }

        /****************************************************************/
        /** Actions *****************************************************/
        /****************************************************************/
        ctrl.delete = function deleteConf(configuration) {
            ModalSrvc.confirmDeletion(gettextCatalog.getString("la configuration d'export FTP {{label}}", configuration)).then(function () {
                configuration.$delete(function (value) {
                    MessageSrvc.addSuccess(gettext('La configuration {{label}} a été supprimée'), value);
                    var removed = ListTools.findAndRemoveItemFromList(configuration, $scope.pagination.items);
                    if (removed) {
                        $scope.pagination.totalItems--;
                    } else {
                        ListTools.findAndRemoveItemFromList(configuration, $scope.newConfigurations);
                    }
                    ctrl.backToList();
                });
            });
        };

        ctrl.cancel = function cancel() {
            if ($routeParams.new) {
                ctrl.backToList();
            } else {
                ctrl.exportFtpConfigForm.$cancel();
            }
        };

        ctrl.backToList = function backToList() {
            $location.path('/platformconfiguration/exportftpconfiguration').search({});
        };

        /****************************************************************/
        /** Fonctions ***************************************************/
        /****************************************************************/
        // Sauvegarde une configuration
        ctrl.saveConfiguration = function saveConfiguration() {
            delete ctrl.errors;

            $timeout(function () {
                var creation = !ctrl.configuration.identifier;

                ctrl.configuration.$save(
                    {},
                    function (value) {
                        MessageSrvc.addSuccess(gettext('La configuration {{name}} a été sauvegardée'), { name: value.name });
                        onSuccess();
                        // si création, on ajoute à la liste, sinon, on essaye de MAJ les infos dans la colonne du milieu
                        if (creation) {
                            $scope.clearSelection();
                            addNewConfigurationToList(value, $scope.newConfigurations, $scope.pagination.items);
                            $location.search({ id: value.identifier }); // suppression des paramètres
                        } else {
                            $scope.updateConfiguration(ctrl.configuration.identifier, ctrl.configuration);
                        }
                    },
                    function (response) {
                        ctrl.errors = _.chain(response.data.errors)
                            .groupBy('field')
                            .mapObject(function (list) {
                                return _.pluck(list, 'code');
                            })
                            .value();

                        openForm();
                    }
                );
            });
        };

        // Met à jour la liste avec la nouvelle config
        function addNewConfigurationToList(configuration, newConfigurations, configurations) {
            var found = _.find(newConfigurations, function (b) {
                return b.identifier === configuration.identifier;
            });
            if (found) {
                /** Si cette configuration existe déjà, ne rien faire */
                return;
            }
            found = _.find(configurations, function (b) {
                return b.identifier === configuration.identifier;
            });
            if (found) {
                /** Si cette configuration existe déjà, ne rien faire */
                return;
            }

            var newConfiguration = {
                _selected: true,
                identifier: configuration.identifier,
                label: configuration.label,
            };
            for (var i = 0; i < newConfigurations.length; i++) {
                var b = newConfigurations[i];
                if (b.label.localeCompare(configuration.label) > 0) {
                    break;
                }
            }
            newConfigurations.splice(i, 0, newConfiguration);
        }
        // Gestion de la configuration renvoyée par le serveur
        function onSuccess() {
            HistorySrvc.add(gettextCatalog.getString('Configuration {{label}}', ctrl.configuration));
            displayMessages();
        }
        // Ouverture du formulaire et des sous formulaires
        function openForm() {
            $timeout(function () {
                if (angular.isDefined(ctrl.exportFtpConfigForm)) {
                    ctrl.exportFtpConfigForm.$show();
                }
            });
        }
        // Affichage asynchrone d'un formulaire
        function displayMessages() {
            // On commence par vider les messages précédents...
            MessageSrvc.clearMessages();
            // ... puis rien pour l'instant
        }

        // Initialisation une fois qu'on a reçu toutes les données du serveur
        function afterLoadingConfiguration() {
            onSuccess();
            ctrl.loaded = true;
        }

        function loadConfiguration() {
            ctrl.loaded = false;

            if (angular.isDefined($routeParams.id)) {
                // Chargement configuration
                ctrl.configuration = ExportFTPConfigurationSrvc.get(
                    {
                        id: $routeParams.id,
                    },
                    function () {
                        afterLoadingConfiguration();
                    }
                );
            } else if (angular.isDefined($routeParams.new)) {
                // Création d'une nouvelle configuration
                HistorySrvc.add(gettext('Nouvelle configuration'));
                ctrl.configuration = new ExportFTPConfigurationSrvc();
                ctrl.configuration.active = true;
                afterLoadingConfiguration();
                openForm();
            }
        }

        /***
         * Configuration des dossiers de livraison
         */
        ctrl.addDeleveryFolder = function () {
            var newCollection = {
                confExportFTP: ctrl.configuration,
            };
            if (ctrl.configuration.deliveryFolders) {
                ctrl.configuration.deliveryFolders.push(newCollection);
            } else {
                ctrl.configuration.deliveryFolders = [newCollection];
            }
        };

        ctrl.deleteDeliveryFolder = function (index) {
            ctrl.configuration.deliveryFolders.splice(index, 1);
        };
    }
})();
