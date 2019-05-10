(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('StatusChartEditWidgetCtrl', StatusChartEditWidgetCtrl);

    function StatusChartEditWidgetCtrl($q, config, gettextCatalog, LibrarySrvc, LotSrvc, ProjectSrvc) {

        var mainCtrl = this;
        mainCtrl.changeProject = changeProject;
        mainCtrl.isConfigured = isConfigured;

        // critères disponibles par type de graphique
        mainCtrl.controlConfig = {
            getProjectGroupByStatus: {},
            getLotGroupByStatus: {
                project: true
            },
            getTrainGroupByStatus: {
                project: true
            },
            getDeliveryGroupByStatus: {
                project: true,
                lot: true
            },
            getDocUnitsGroupByStatus: {
                project: true,
                lot: true
            }
        };

        mainCtrl.config = {
            dataFormats: [{
                identifier: "doughnut",
                label: gettextCatalog.getString("Anneau")
            }, {
                identifier: "pie",
                label: gettextCatalog.getString("Camembert")
            }, {
                identifier: "hbar",
                label: gettextCatalog.getString("Histogramme")
            }, {
                identifier: "list",
                label: gettextCatalog.getString("Liste")
            }],
            dataTypes: [{
                identifier: "getProjectGroupByStatus",  // id = nom du service dans StatisticsSrvc
                label: "Projets par statut"
            }, {
                identifier: "getLotGroupByStatus",
                label: "Lots par statut"
            }, {
                identifier: "getTrainGroupByStatus",
                label: "Trains par statut"
            }, {
                identifier: "getDeliveryGroupByStatus",
                label: "Livraisons par statut"
            }, {
                identifier: "getDocUnitsGroupByStatus",
                label: "UD par statut"
            }],
            libraries: {
                text: "name",
                placeholder: gettextCatalog.getString("Bibliothèque"),
                trackby: "identifier",
                // Chargement avec mise en cache du résultat
                refresh: function ($select) {
                    if (!mainCtrl.config.libraries.data) {
                        mainCtrl.config.libraries.data = LibrarySrvc.query({ dto: true });
                        return mainCtrl.config.libraries.data.$promise
                            .then(function (lib) {
                                return _.map(lib, function (l) {
                                    return _.pick(l, "identifier", "name");
                                });
                            });
                    }
                    else {
                        return $q.when(mainCtrl.config.libraries.data);
                    }
                },
                'refresh-delay': 0, // pas de refresh-delay, car on lit les données en cache après le 1er chargement
                'allow-clear': true,
                multiple: true
            },
            lots: {
                text: "label",
                placeholder: gettextCatalog.getString("Lot"),
                trackby: "identifier",
                refresh: function ($select) {
                    mainCtrl.lotsSelect = $select;
                    // Gestion du cas où la liste est réinitialisée manuellement (search est indéfini)
                    if (angular.isUndefined($select.search)) {
                        return $q.when([]);
                    }
                    var searchParams = {
                        page: 0,
                        search: $select.search,
                        active: true
                    };
                    if (config.project) {
                        searchParams["projects"] = _.pluck(config.project, "identifier");
                    }
                    return LotSrvc.search(searchParams).$promise
                        .then(function (lots) {
                            return _.map(lots.content, function (lot) {
                                return _.pick(lot, "identifier", "label");
                            });
                        });
                },
                'refresh-delay': 300,
                'allow-clear': true,
                multiple: true
            },
            projects: {
                text: "name",
                placeholder: gettextCatalog.getString("Projet"),
                trackby: "identifier",
                refresh: function ($select) {
                    var searchParams = {
                        page: 0,
                        search: $select.search,
                        active: true
                    };
                    return ProjectSrvc.search(searchParams).$promise
                        .then(function (projects) {
                            return _.map(projects.content, function (project) {
                                return _.pick(project, "identifier", "name");
                            });
                        });
                },
                'refresh-delay': 300,
                'allow-clear': true,
                multiple: true
            }
        };

        /**
         * Le widget est-il configuré
         * @return {Boolean} retourne vrai si le widget est configuré correctement, faux sinon
         */
        function isConfigured() {
            return angular.isDefined(config.dataType) && angular.isDefined(config.format);
        }

        /**
         * 
         * Changement de projet
         * 
         * @param {any} project 
         */
        function changeProject() {
            config.lot = {};

            if (mainCtrl.lotsSelect) {
                delete mainCtrl.lotsSelect.selected;
                delete mainCtrl.lotsSelect.search;
                mainCtrl.lotsSelect.items = [];
                // mainCtrl.lotsSelect.activate(false, true);
                mainCtrl.lotsSelect.activeIndex = 0;
                mainCtrl.lotsSelect.open = false;
            }
        }
    }
})();
