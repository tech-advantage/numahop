(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('ViewsFormatEditCtrl', ViewsFormatEditCtrl);

    function ViewsFormatEditCtrl($location, $routeParams, $scope, $timeout,
        ViewsFormatSrvc, gettext, gettextCatalog, HistorySrvc,
        ListTools, MessageSrvc, ModalSrvc, NumahopEditService, NumaHopInitializationSrvc,
        ValidationSrvc) {

        $scope.backToList = backToList;
        $scope.cancel = cancel;
        $scope.delete = deleteConf;
        $scope.saveConfiguration = saveConfiguration;
        $scope.validation = ValidationSrvc;
        $scope.readOnlyCheck = false;
        $scope.configuration = undefined;

        // Définition des listes déroulantes
        $scope.options = {
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
            ModalSrvc.confirmDeletion(gettextCatalog.getString("la configuration {{label}}", configuration))
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

        function cancel() {
            if ($routeParams.new || $routeParams.duplicate) {
                backToList();
            }
            else {
                $scope.formatConfigurationForm.$cancel();
            }
        }

        function backToList() {
            $location.path("/administration/appconfiguration/viewsformat").search({});
        }

        $scope.showForm = function () {
            openForm();
        };

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
                        onSuccess(value);

                        // si création, on ajoute à la liste, sinon, on essaye de MAJ les infos dans la colonne du milieu
                        if (creation) {
                            $scope.clearSelection();
                            NumahopEditService.addNewEntityToList(value, $scope.newConfigurations, $scope.pagination.items, ["label"]);
                        } else {
                            NumahopEditService.updateMiddleColumn($scope.configuration, ["label"], $scope.pagination.items, $scope.newConfigurations);
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

        // Gestion de la configuration renvoyée par le serveur
        function onSuccess(value) {
            HistorySrvc.add(gettextCatalog.getString("Configuration {{label}}", $scope.configuration));
            displayMessages();
        }

        // Ouverture du formulaire et des sous formulaires
        function openForm() {
            $timeout(function () {
                if (angular.isDefined($scope.formatConfigurationForm)) {
                    $scope.formatConfigurationForm.$show();
                }
            });
        }

        // Messages
        function displayMessages() {
            // On commence par vider les messages précédents...
            MessageSrvc.clearMessages();
            // ... puis rien pour l'instant
        }

        // Initialisation une fois qu'on a reçu toutes les données du serveur
        function afterLoadingConfiguration(configuration) {
            onSuccess(configuration);
            $scope.loaded = true;
        }

        function loadConfiguration() {
            if (angular.isDefined($routeParams.id)) {
                // Chargement configuration
                $scope.configuration = ViewsFormatSrvc.get({
                    id: $routeParams.id
                }, function (configuration) {
                    afterLoadingConfiguration(configuration);
                });
            } else if (angular.isDefined($routeParams.new)) {
                // Création d'une nouvelle configuration
                HistorySrvc.add(gettext("Nouvelle configuration format des images"));
                $scope.configuration = new ViewsFormatSrvc();
                $scope.configuration.active = true;
                afterLoadingConfiguration($scope.configuration);
                openForm();
            }
        }


    }
})();
