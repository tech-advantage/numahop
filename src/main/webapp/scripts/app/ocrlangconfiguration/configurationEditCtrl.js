(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('OcrLangConfigurationEditCtrl', OcrLangConfigurationEditCtrl);

    function OcrLangConfigurationEditCtrl($location, $routeParams, $scope, $timeout,
        OcrLangConfigurationSrvc, OcrLanguageSrvc, DocUnitBaseService, gettext, gettextCatalog, HistorySrvc,
        ListTools, MessageSrvc, ModalSrvc, NumahopEditService, NumaHopInitializationSrvc,
        ValidationSrvc) {

        $scope.backToList = backToList;
        $scope.cancel = cancel;
        $scope.delete = deleteConf;
        $scope.duplicate = duplicate;
        $scope.saveConfiguration = saveConfiguration;
        $scope.validation = ValidationSrvc;
        $scope.readOnlyCheck = false;
        $scope.configuration = undefined;
        $scope.displayBoolean = DocUnitBaseService.displayBoolean;

        // toggle switch label ON/OFF
        $scope.onLabelActiv = gettextCatalog.getString("Activé");
        $scope.offLabelActiv = gettextCatalog.getString("Désactivé");
        
        $scope.options = 
            { boolean: {
                      "true": gettextCatalog.getString('Oui'),
                      "false": gettextCatalog.getString('Non')
                    }
            };
        init();


        /** Initialisation */
        function init() {
            $scope.languages = OcrLanguageSrvc.languages();
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

        function duplicate() {
            if ($scope.configuration) {
                $scope.loaded = false;
                $scope.configuration._selected = false;
                var identifier = $scope.configuration.identifier;
                $scope.configuration = null;
                $location.path("/ocrlangconfiguration/ocrlangconfiguration").search({ id: identifier, duplicate: true });
            }
        }

        function cancel() {
            if ($routeParams.new || $routeParams.duplicate) {
                backToList();
            }
            else {
                $scope.ocrConfigForm.$cancel();
            }
        }

        function backToList() {
            $location.path("/ocrlangconfiguration/ocrlangconfiguration").search({});
        }

        $scope.showForm = function () {
            openForm();
        };

        $scope.updateActivLang = function (lang) {
            if (lang.active) {
                $scope.configuration.ocrLanguages.push(lang);
            } else {
                var idx = $scope.configuration.ocrLanguages.map(function(l) { return l.identifier; }).indexOf(lang.identifier);
                if (idx > -1) {
                    $scope.configuration.ocrLanguages.splice(idx, 1);
                }
            }
        };

        /**
         * Initialisation des langues activées en modif.
         */
        function initActivatedLangs() {
            
            _.each($scope.configuration.ocrLanguages, function(actif) {
                var found = _.find($scope.languages, function(lang) {
                    return actif.identifier === lang.identifier;
                });
                found.active = true;
            })
            $scope.initialized = true;
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
            displayMessages($scope.configuration);
        }

        // Ouverture du formulaire et des sous formulaires
        function openForm() {
            $timeout(function () {
                initActivatedLangs();
                if (angular.isDefined($scope.ocrConfigForm)) {
                    $scope.ocrConfigForm.$show();
                }
            });
        }

        // Messages
        function displayMessages(configuration) {
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
                $scope.configuration = OcrLangConfigurationSrvc.get({
                    id: $routeParams.id
                }, function (configuration) {
                    initActivatedLangs();
                    afterLoadingConfiguration(configuration);
                });
            } else if (angular.isDefined($routeParams.new)) {
                // Création d'une nouvelle configuration
                HistorySrvc.add(gettext("Nouvelle configuration"));
                $scope.configuration = new OcrLangConfigurationSrvc();
                $scope.configuration.active = true;
                $scope.configuration.ocrLanguages = [];
                afterLoadingConfiguration($scope.configuration);
                $scope.initialized = true;
                openForm();
            }

        }


    }
})();
