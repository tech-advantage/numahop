(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('StatisticsWorkflowStateCtrl', StatisticsWorkflowStateCtrl);

    function StatisticsWorkflowStateCtrl($q, codeSrvc, gettextCatalog, HistorySrvc, LibrarySrvc,
        NumahopStorageService, StatisticsSrvc, WorkflowModelSrvc, WorkflowSrvc) {

        var statCtrl = this;

        statCtrl.code = codeSrvc;
        statCtrl.getExportUrl = getExportUrl;
        statCtrl.reinitFilters = reinitFilters;
        statCtrl.search = search;
        statCtrl.searchValue = searchValue;

        var FILTER_STORAGE_SERVICE_KEY = "stat_workflow_state";

        /**
         * Listes déroulantes
         */
        statCtrl.config = {
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
            states: {
                data: WorkflowSrvc.getConfigWorkflow(),
                text: "label",
                placeholder: gettextCatalog.getString("État"),
                trackby: "identifier",
                multiple: true,
                'allow-clear': true
            },
            workflows: {
                text: "name",
                placeholder: gettextCatalog.getString("Workflow"),
                trackby: "identifier",
                refresh: function ($select) {
                    var searchParams = {
                        page: 0,
                        search: $select.search
                    };
                    return WorkflowModelSrvc.search(searchParams).$promise;
                },
                'refresh-delay': 300,
                multiple: true,
                'allow-clear': true
            },
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
            statCtrl.filters = {};
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
            statCtrl.items = StatisticsSrvc.workflowState(params);
            return statCtrl.items.$promise;
        }

        /**
         * Construction des paramètres de la recherche en fonction des filtres sélectionnés
         * @return {[type]} [description]
         */
        function getSearchParams() {
            var params = {
                library: _.pluck(statCtrl.filters.library, "identifier"),
                state: _.pluck(statCtrl.filters.state, "identifier"),
                workflow: _.pluck(statCtrl.filters.workflow, "identifier"),
                from: statCtrl.filters.from,
                to: statCtrl.filters.to
            };
            return params;
        }

        function loadSearch() {
            statCtrl.filters = NumahopStorageService.getFilter(FILTER_STORAGE_SERVICE_KEY);
            if (!statCtrl.filters) {
                statCtrl.filters = {};
            }
        }

        function saveSearch() {
            NumahopStorageService.saveFilter(FILTER_STORAGE_SERVICE_KEY, statCtrl.filters);
        }

        function getExportUrl() {
            var params = getSearchParams();
            params.wstate = true;
            return StatisticsSrvc.getExportUrl(params, "workflow");
        }
    }
})();
