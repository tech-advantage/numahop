(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('FTPConfigurationEditCtrl', FTPConfigurationEditCtrl);

    function FTPConfigurationEditCtrl($location, $routeParams, $scope, $timeout, codeSrvc,
        DocUnitBaseService, FTPConfigurationSrvc, gettext, gettextCatalog, HistorySrvc, ListTools,
        MessageSrvc, ModalSrvc, NumaHopInitializationSrvc, ValidationSrvc) {

        $scope.backToList = backToList;
        $scope.cancel = cancel;
        $scope.delete = deleteConf;
        $scope.duplicate = duplicate;
        $scope.semCodes = codeSrvc;
        $scope.saveConfiguration = saveConfiguration;
        $scope.validation = ValidationSrvc;

        // Définition des listes déroulantes
        $scope.options = {
            boolean: DocUnitBaseService.options.booleanObj,
            libraries: []
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
                    $scope.options.libraries = libs;
                });
        }

        /****************************************************************/
        /** Actions *****************************************************/
        /****************************************************************/
        function deleteConf(configuration) {
            ModalSrvc.confirmDeletion(gettextCatalog.getString("la configuration FTP {{label}}", configuration))
                .then(function () {
                    configuration.$delete(function (value) {
                        MessageSrvc.addSuccess(gettext("La configuration {{label}} a été supprimée"), value);
                        var removed = ListTools.findAndRemoveItemFromList(configuration, $scope.pagination.items);
                        if (removed) {
                            $scope.pagination.totalItems--;
                        }
                        else {
                            ListTools.findAndRemoveItemFromList(configuration, $scope.newConfigurations);
                        }
                        $scope.backToList();
                    });
                });
        }

        function duplicate() {
            if ($scope.configuration) {
                $scope.loaded = false;
                $scope.configuration._selected = false;
                var identifier = $scope.configuration.identifier;
                $scope.configuration = null;
                $location.path("/platformconfiguration/ftpconfiguration").search({ id: identifier, duplicate: true });
            }
        }

        function cancel() {
            if ($routeParams.new || $routeParams.duplicate) {
                backToList();
            }
            else {
                $scope.ftpConfigurationForm.$cancel();
            }
        }

        function backToList() {
            $location.path("/platformconfiguration/ftpconfiguration").search({});
        }

        /****************************************************************/
        /** Fonctions ***************************************************/
        /****************************************************************/
        // Sauvegarde une configuration
        function saveConfiguration() {
            delete $scope.errors;

            $timeout(function () {
                var creation = !$scope.configuration.identifier;

                $scope.configuration.$save({},
                    function (value) {
                        MessageSrvc.addSuccess(gettext("La configuration {{name}} a été sauvegardée"), { name: value.name });
                        onSuccess();
                        // si création, on ajoute à la liste, sinon, on essaye de MAJ les infos dans la colonne du milieu
                        if (creation) {
                            $scope.clearSelection();
                            addNewConfigurationToList(value, $scope.newConfigurations, $scope.pagination.items);
                        } else {
                            $scope.updateConfiguration($scope.configuration.identifier, $scope.configuration);
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
                label: configuration.label
            };
            var i = 0;
            for (i = 0; i < newConfigurations.length; i++) {
                var b = newConfigurations[i];
                if (b.name.localeCompare(configuration.name) > 0) {
                    break;
                }
            }
            newConfigurations.splice(i, 0, newConfiguration);
        }
        // Gestion de la configuration renvoyée par le serveur
        function onSuccess() {
            HistorySrvc.add(gettextCatalog.getString("Configuration {{label}}", $scope.configuration));
            displayMessages();
        }
        // Ouverture du formulaire et des sous formulaires
        function openForm() {
            $timeout(function () {
                if (angular.isDefined($scope.ftpConfigurationForm)) {
                    $scope.ftpConfigurationForm.$show();
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
            $scope.loaded = true;
        }

        function loadConfiguration() {
            $scope.loaded = false;

            if ('duplicate' in $routeParams && angular.isDefined($routeParams.id)) {
                // Duplication
                $scope.configuration = FTPConfigurationSrvc.duplicate({
                    id: $routeParams.id
                }, function () {
                    afterLoadingConfiguration();
                    openForm();
                });
            } else if (angular.isDefined($routeParams.id)) {
                // Chargement confgiuration
                $scope.configuration = FTPConfigurationSrvc.get({
                    id: $routeParams.id
                }, function () {
                    afterLoadingConfiguration();
                });
            } else if (angular.isDefined($routeParams.new)) {
                // Création d'une nouvelle configuration
                HistorySrvc.add(gettext("Nouvelle configuration"));
                $scope.configuration = new FTPConfigurationSrvc();
                $scope.configuration.active = true;
                afterLoadingConfiguration();
                openForm();
            }
        }
    }
})();
