(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('CheckConfigurationEditCtrl', CheckConfigurationEditCtrl);

    function CheckConfigurationEditCtrl($location, $routeParams, $scope, $timeout,
        CheckConfigurationSrvc, DocUnitBaseService, gettext, gettextCatalog, HistorySrvc,
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

        $scope.displaySampleMode = displaySampleMode;

        // Définition des listes déroulantes
        $scope.options = {
            boolean: DocUnitBaseService.options.booleanObj,
            libraries: [],
            sampleMode: {
                "NO_SAMPLE": gettextCatalog.getString('Pas d\'échantillonnage'),
                "SAMPLE_DOC_DELIV": gettextCatalog.getString('Documents dans la livraison'),
                "SAMPLE_PAGE_ONE_DOC": gettextCatalog.getString('Pages dans chaque document'),
                "SAMPLE_PAGE_ALL_DOC": gettextCatalog.getString('Pages dans tous les documents')
            }
        };

        function displaySampleMode(sampleMode) {
            return $scope.options.sampleMode[sampleMode] || sampleMode;
        }

        // toggle switch label ON/OFF
        $scope.onLabelActiv = gettextCatalog.getString("Activé");
        $scope.offLabelActiv = gettextCatalog.getString("Désactivé");
        $scope.onLabelBlock = gettextCatalog.getString("Oui");
        $scope.offLabelBlock = gettextCatalog.getString("Non");

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

        function duplicate() {
            if ($scope.configuration) {
                $scope.loaded = false;
                $scope.configuration._selected = false;
                var identifier = $scope.configuration.identifier;
                $scope.configuration = null;
                $location.path("/checkconfiguration/checkconfiguration").search({ id: identifier, duplicate: true });
            }
        }

        function cancel() {
            if ($routeParams.new || $routeParams.duplicate) {
                backToList();
            }
            else {
                $scope.checkConfigurationForm.$cancel();
            }
        }

        function backToList() {
            $location.path("/checkconfiguration/checkconfiguration").search({});
        }

        $scope.showForm = function () {
            openForm();
        };

        $scope.updateActivRule = function (rule) {
            if (!rule.active) {
                rule.blocking = false;
                if (rule.automaticCheckType.type === 'WITH_MASTER') {
                    getAutoChecks().forEach(function (regle) {
                        regle.readOnly = true;
                    });
                    $scope.readOnlyCheck = true;
                }
            } else {
                if (rule.automaticCheckType.type === 'WITH_MASTER') {
                    $scope.readOnlyCheck = false;
                } else if (rule.automaticCheckType.type === 'GENER_PDF_OCR') {
                    disableRule('FILE_PDF_MULTI');
                } else if (rule.automaticCheckType.type === 'FILE_PDF_MULTI') {
                    disableRule('GENER_PDF_OCR');
                }
            }
        };

        $scope.updateBlockRule = function (rule) {
            if (rule.blocking) {
                rule.active = true;
                if (rule.automaticCheckType.type === 'GENER_PDF_OCR') {
                    disableRule('FILE_PDF_MULTI');
                } else if (rule.automaticCheckType.type === 'FILE_PDF_MULTI') {
                    disableRule('GENER_PDF_OCR');
                }
            }
        };
        
        /**
         * Desactivation d'une regle.
         */
        function disableRule(ruleType) {
            var rule = _.find($scope.configuration.automaticCheckRules, function(r) {
                return r.automaticCheckType.type === ruleType;
            });
            rule.active = false;
            rule.blocking = false;
        }

        /**
         * Initialisation des regles au passage en modif.
         */
        function initStateRules() {
            var withMasterRule = $scope.configuration.automaticCheckRules
                .find(theMaster);
            if (withMasterRule && !withMasterRule.active) {

                withMasterRule.readOnly = false;

                getAutoChecks().forEach(function (regle) {
                    regle.readOnly = true;
                });
                $scope.readOnlyCheck = true;
            } else {
                $scope.readOnlyCheck = false;
            }
        }
        /* pour les find juste au dessus */
        function theMaster(rule) {
            return rule.automaticCheckType.type === 'WITH_MASTER';
        }


        /**
         * Retourne les regles de controle auto.
         * (dépendant du controle Presence master)
         */
        function getAutoChecks() {
            return $scope.configuration.automaticCheckRules
                .filter(function (rule) {
                    return rule.automaticCheckType.type !== 'WITH_MASTER'
                        && rule.automaticCheckType.type !== 'FACILE'
                        && rule.automaticCheckType.type !== 'METADATA_FILE';
                });
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
                initStateRules();
                if (angular.isDefined($scope.checkConfigurationForm)) {
                    $scope.checkConfigurationForm.$show();
                }
            });
        }

        // Messages
        function displayMessages(configuration) {
            // On commence par vider les messages précédents...
            MessageSrvc.clearMessages();
            // ... puis rien pour l'instant
        }

        /**
         * trie les rules par checkType.order asc
         */
        function sortCheckRules(toSort) {
            $scope.configuration.automaticCheckRules = toSort.sort(function (r1, r2) {
                return r1.automaticCheckType.order - r2.automaticCheckType.order;
            });
        }

        // Initialisation une fois qu'on a reçu toutes les données du serveur
        function afterLoadingConfiguration(configuration) {
            sortCheckRules(configuration.automaticCheckRules);
            onSuccess(configuration);
            $scope.loaded = true;
        }

        function loadConfiguration() {
            if ('duplicate' in $routeParams && angular.isDefined($routeParams.id)) {
                // Duplication
                $scope.configuration = CheckConfigurationSrvc.duplicate({
                    id: $routeParams.id
                }, function (checkConfiguration) {
                    afterLoadingConfiguration(checkConfiguration);
                    openForm();
                });
            } else if (angular.isDefined($routeParams.id)) {
                // Chargement configuration
                $scope.configuration = CheckConfigurationSrvc.get({
                    id: $routeParams.id
                }, function (configuration) {
                    afterLoadingConfiguration(configuration);
                });
            } else if (angular.isDefined($routeParams.new)) {
                // Création d'une nouvelle configuration
                HistorySrvc.add(gettext("Nouvelle configuration"));
                $scope.configuration = new CheckConfigurationSrvc();
                $scope.configuration.active = true;
                loadCheckRules();
                afterLoadingConfiguration($scope.configuration);
                openForm();
            }

        }

        function loadCheckRules() {
            $scope.configuration.automaticCheckRules = CheckConfigurationSrvc.rules({
                id: $routeParams.id
            });
        }

    }
})();
