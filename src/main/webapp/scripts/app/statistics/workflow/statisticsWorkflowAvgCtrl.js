(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('StatisticsWorkflowAverageCtrl', StatisticsWorkflowAverageCtrl);

    function StatisticsWorkflowAverageCtrl($q, codeSrvc, DeliverySrvc, gettextCatalog, HistorySrvc,
        LibrarySrvc, LotSrvc, NumahopStorageService, ProjectSrvc, StatisticsSrvc) {

        var statCtrl = this;

        statCtrl.code = codeSrvc;
        statCtrl.getExportUrl = getExportUrl;
        statCtrl.reinitFilters = reinitFilters;
        statCtrl.search = search;
        statCtrl.searchValue = searchValue;

        var FILTER_STORAGE_SERVICE_KEY = "stat_workflow_average";

        /**
         * Listes déroulantes
         */
        statCtrl.config = {
            groupby: [
                { identifier: "PROJECT", label: gettextCatalog.getString("Projet") },
                { identifier: "LOT", label: gettextCatalog.getString("Lot") },
                { identifier: "DELIVERY", label: gettextCatalog.getString("Livraison") },
            ],
            deliveries: {
                text: "label",
                placeholder: gettextCatalog.getString("Livraison"),
                trackby: "identifier",
                refresh: function ($select) {
                    statCtrl.delvSelect = $select;
                    // Gestion du cas où la liste est réinitialisée manuellement (search est indéfini)
                    if (angular.isUndefined($select.search)) {
                        return $q.when([]);
                    }
                    var searchParams = {
                        page: 0,
                        search: $select.search
                    };
                    if (statCtrl.filters.project) {
                        searchParams["projects"] = _.pluck(statCtrl.filters.project, "identifier");
                    }
                    if (statCtrl.filters.lot) {
                        searchParams["lots"] = _.pluck(statCtrl.filters.lot, "identifier");
                    }
                    return DeliverySrvc.search(searchParams).$promise;
                },
                'refresh-delay': 300,
                'allow-clear': true,
                multiple: true
            },
            libraries: {
                text: "name",
                placeholder: gettextCatalog.getString("Bibliothèque"),
                trackby: "identifier",
                // Chargement avec mise en cache du résultat
                refresh: function () {
                    if (!statCtrl.config.libraries.data) {
                        statCtrl.config.libraries.data = LibrarySrvc.query({ dto: true });
                        return statCtrl.config.libraries.data.$promise
                            .then(function (lib) {
                                return _.map(lib, function (l) {
                                    return _.pick(l, "identifier", "name");
                                });
                            });
                    }
                    else {
                        return $q.when(statCtrl.config.libraries.data);
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
                    statCtrl.lotsSelect = $select;
                    // Gestion du cas où la liste est réinitialisée manuellement (search est indéfini)
                    if (angular.isUndefined($select.search)) {
                        return $q.when([]);
                    }
                    var searchParams = {
                        page: 0,
                        search: $select.search,
                        active: true
                    };
                    if (statCtrl.filters.project) {
                        searchParams["projects"] = _.pluck(statCtrl.filters.project, "identifier");
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
                multiple: true,
                'allow-clear': true
            }
        };

        init();


        /**
         * Initialisation du controleur
         * @return {[type]} [description]
         */
        function init() {
            HistorySrvc.add(gettextCatalog.getString("Statistiques des workflows"));
            statCtrl.loaded = false;

            loadSearch();
            getPage().then(function () {
                statCtrl.loaded = true;
            });
        }

        /**
         * Réinitialise l'ensemble des filtres et lance une nouvelle recherche
         * @return {[type]} [description]
         */
        function reinitFilters() {
            statCtrl.filters = {
                groupby: statCtrl.filters.groupby
            };
            search();
        }

        /**
         * Recherche d'entité
         * @return {[type]} [description]
         */
        function search() {
            saveSearch();
            getPage();
        }

        /**
         * Recherche  d'entités sur un changement de période
         * @param {*} from 
         * @param {*} to 
         */
        function searchValue(updatedField, updatedValue) {
            statCtrl.filters[updatedField] = updatedValue;
            search();
        }

        /**
         * Chargement de la prochaine page d'items
         * @return {[type]} [description]
         */
        function getPage() {
            var params = getSearchParams();
            statCtrl.items = StatisticsSrvc.docUnitAverage(params);
            return statCtrl.items.$promise;
        }

        /**
         * Construction des paramètres de la recherche en fonction des filtres sélectionnés
         * @return {[type]} [description]
         */
        function getSearchParams() {
            var params = {
                delivery: _.pluck(statCtrl.filters.delivery, "identifier"),
                library: _.pluck(statCtrl.filters.library, "identifier"),
                lot: _.pluck(statCtrl.filters.lot, "identifier"),
                project: _.pluck(statCtrl.filters.project, "identifier"),
                from: statCtrl.filters.from,
                to: statCtrl.filters.to,
                groupby: statCtrl.filters.groupby
            };
            return params;
        }

        function loadSearch() {
            statCtrl.filters = NumahopStorageService.getFilter(FILTER_STORAGE_SERVICE_KEY);
            if (!statCtrl.filters) {
                statCtrl.filters = {};
            }
            if (!statCtrl.filters.groupby) {
                statCtrl.filters.groupby = "PROJECT";
            }
        }

        function saveSearch() {
            NumahopStorageService.saveFilter(FILTER_STORAGE_SERVICE_KEY, statCtrl.filters);
        }

        function getExportUrl() {
            var params = getSearchParams();
            params.average = true;
            return StatisticsSrvc.getExportUrl(params, "docunit");
        }
    }
})();
