(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('DigitalLibraryConfigurationEditCtrl', DigitalLibraryConfigurationEditCtrl);

    function DigitalLibraryConfigurationEditCtrl($location, $route, $routeParams, $timeout, $scope, codeSrvc,
        DocUnitBaseService, DigitalLibraryConfigurationSrvc, gettext, gettextCatalog, HistorySrvc, ListTools,
        MessageSrvc, ModalSrvc, NumaHopInitializationSrvc, ValidationSrvc) {

        var ctrl = this;
        ctrl.backToList = backToList;
        ctrl.delete = deleteConf;
        ctrl.cancel = cancel;
        ctrl.saveConfiguration = saveConfiguration;
        ctrl.validation = ValidationSrvc;
        ctrl.displayBoolean = DocUnitBaseService.displayBoolean;

        // Définition des listes déroulantes
        ctrl.options = {
            boolean: DocUnitBaseService.options.booleanObj,
            libraries: [],
            recordFormats: DigitalLibraryConfigurationSrvc.config.recordFormats
        };

        init();
        /** Initialisation */
        function init() {
            loadLibrarySelect();
            loadConfiguration();
        }

        function loadLibrarySelect() {
            return NumaHopInitializationSrvc.loadLibraries()
                .then(function (libs) {
                    ctrl.options.libraries = libs;
                });
        }

        /****************************************************************/
        /** Actions *****************************************************/
        /****************************************************************/
        // Sauvegarde une configuration
        function saveConfiguration() {
            $timeout(function () {
                var creation = !ctrl.configuration.identifier;

                ctrl.configuration.$save({},
                    function (value) {
                        MessageSrvc.addSuccess(gettext("La configuration {{name}} a été sauvegardée"), { name: value.name });
                        onSuccess();
                        // si création, on ajoute à la liste, sinon, on essaye de MAJ les infos dans la colonne du milieu
                        if (creation) {
                            addNewConfigurationToList(value, $scope.pagination.items);
                            $location.search({ id: value.identifier });
                            $route.reload();
                        } else {
                            ctrl.updateConfiguration(ctrl.configuration.identifier, ctrl.configuration);
                        }
                    },
                    function (response) {
                        ctrl.errors = _.chain(response.data.errors)
                            .groupBy("field")
                            .mapObject(function (list) {
                                return _.pluck(list, "code");
                            })
                            .value();

                        openForm();
                    });
            });
        }

        function deleteConf(configuration) {
            ModalSrvc.confirmDeletion(gettextCatalog.getString("la configuration de diffusion {{label}}", configuration))
                .then(function () {
                    configuration.$delete(function (value) {
                        MessageSrvc.addSuccess(gettext("La configuration {{label}} a été supprimée"), value);
                        var removed = ListTools.findAndRemoveItemFromList(configuration, $scope.pagination.items);
                        $location.search({ });
                        $route.reload();
                    });
                });
        }


        function loadConfiguration() {
            ctrl.loaded = false;

            if (angular.isDefined($routeParams.id)) {
                // Chargement configuration
                ctrl.configuration = DigitalLibraryConfigurationSrvc.get({
                    id: $routeParams.id
                }, function () {
                    afterLoadingConfiguration();
                });
            } else if (angular.isDefined($routeParams.new)) {
                // Création d'une nouvelle configuration
                HistorySrvc.add(gettext("Nouvelle configuration"));
                ctrl.configuration = new DigitalLibraryConfigurationSrvc();
                ctrl.configuration.active = true;
                afterLoadingConfiguration();
                openForm();
            }
        }

        /****************************************************************/
        /** Fonctions ***************************************************/
        /****************************************************************/
        function cancel() {
            if ($routeParams.new) {
                backToList();
            }
            else {
                $scope.digitalLibraryConfigForm.$cancel();
            }
        }

        // Gestion de la configuration renvoyée par le serveur
        function onSuccess() {
            HistorySrvc.add(gettextCatalog.getString("Configuration {{label}}", ctrl.configuration));
            displayMessages();
        }
        // Ouverture du formulaire et des sous formulaires
        function openForm() {
            $timeout(function () {
                if (angular.isDefined($scope.digitalLibraryConfigForm)) {
                    $scope.digitalLibraryConfigForm.$show();
                }
            });
        }

        function backToList() {
            $location.path("/platformconfiguration/digitallibraryconfiguration").search({});
        }

        // Met à jour la liste avec la nouvelle config
        function addNewConfigurationToList(configuration, configurations) {
            var found = _.find(configurations, function (b) {
                return b.identifier === configuration.identifier;
            });
            if (found) {
                /** Si cette configuration existe déjà, ne rien faire */
                return;
            }

            var newConfiguration = {
                _selected: true,
                identifier: configuration.identifier,
                label: configuration.label
            };
            var i = 0;
            for (i = 0; i < configurations.length; i++) {
                var b = configurations[i];
                if (b.label.localeCompare(configuration.label) > 0) {
                    break;
                }
            }
            configurations.splice(i, 0, newConfiguration);
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
    }
})();
