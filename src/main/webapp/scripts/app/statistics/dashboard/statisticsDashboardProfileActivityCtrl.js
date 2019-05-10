(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('StatisticsDashboardProfileActivityCtrl', StatisticsDashboardProfileActivityCtrl);

    function StatisticsDashboardProfileActivityCtrl($q, codeSrvc, gettextCatalog, HistorySrvc, LibrarySrvc, LotSrvc,
        NumahopStorageService, Principal, ProjectSrvc, StatisticsSrvc, USER_ROLES, UserRoleSrvc,
        WorkflowSrvc) {

        var statCtrl = this;

        statCtrl.code = codeSrvc;
        statCtrl.getExportUrl = getExportUrl;
        statCtrl.isVisible = isVisible;
        statCtrl.reinitFilters = reinitFilters;
        statCtrl.saveColumns = saveColumns;
        statCtrl.search = search;
        statCtrl.searchDates = searchDates;

        var FILTER_STORAGE_SERVICE_KEY = "stat_dashboard_profile_activity";

        /**
         * Listes déroulantes
         */
        statCtrl.config = {
            columns: {
                data: [
                    { "identifier": "role", "label": gettextCatalog.getString("Profil") },
                    { "identifier": "project", "label": gettextCatalog.getString("Projet") },
                    { "identifier": "lot", "label": gettextCatalog.getString("Lot") },
                    { "identifier": "pgcnId", "label": gettextCatalog.getString("PGCN Id") },
                    { "identifier": "state", "label": gettextCatalog.getString("Étape") },
                    { "identifier": "start", "label": gettextCatalog.getString("Début") },
                    { "identifier": "duration", "label": gettextCatalog.getString("Durée") }
                ],
                text: "label",
                placeholder: gettextCatalog.getString("Colonne"),
                trackby: "identifier",
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
            },
            roles: {
                text: "label",
                placeholder: gettextCatalog.getString("Profil"),
                trackby: "identifier",
                refresh: function ($select) {
                    var searchParams = {
                        page: 0,
                        search: $select.search
                    };
                    return UserRoleSrvc.search(searchParams).$promise;
                },
                'refresh-delay': 300,
                multiple: true,
                'allow-clear': true
            },
            states: {
                data: WorkflowSrvc.getConfigWorkflow(),
                text: "label",
                placeholder: gettextCatalog.getString("État"),
                trackby: "identifier",
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
            HistorySrvc.add(gettextCatalog.getString("Statistiques des profils"));
            statCtrl.loaded = false;
            statCtrl.showLib = Principal.isInRole(USER_ROLES.SUPER_ADMIN) || Principal.isInRole(USER_ROLES.ADMINISTRATION_LIB);

            loadColumns();
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
         * Recherche d'entités
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
        function searchDates(from, to) {
            statCtrl.filters.from = from;
            statCtrl.filters.to = to;
            search();
        }

        /**
         * Chargement de la prochaine page d'items
         * @return {[type]} [description]
         */
        function getPage() {
            var params = getSearchParams();
            statCtrl.items = StatisticsSrvc.workflowProfileActivity(params);
            return statCtrl.items.$promise.then(function (data) {
                _.each(data, function (d) {
                    if (d.startDate) {
                        d._start = moment(d.startDate);
                    }
                    if (d.endDate) {
                        d._end = moment(d.endDate);
                    }
                });
                return data;
            });
        }

        /**
         * Construction des paramètres de la recherche en fonction des filtres sélectionnés
         * @return {[type]} [description]
         */
        function getSearchParams() {
            var params = {
                library: _.pluck(statCtrl.filters.library, "identifier"),
                lot: _.pluck(statCtrl.filters.lot, "identifier"),
                project: _.pluck(statCtrl.filters.project, "identifier"),
                role: _.pluck(statCtrl.filters.role, "identifier"),
                state: _.pluck(statCtrl.filters.state, "identifier"),
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
            params.wprofile_activity = true;
            return StatisticsSrvc.getExportUrl(params, "workflow");
        }

        /**
         * Réinitialisation des colonnes
         */
        function loadColumns() {
            if (statCtrl.showLib) {
                statCtrl.config.columns.data.unshift({ "identifier": "library", "label": gettextCatalog.getString("Bibliothèque") });
            }
            statCtrl.columns = NumahopStorageService.getColumns(FILTER_STORAGE_SERVICE_KEY);
            if (!statCtrl.columns) {
                statCtrl.columns = [];
            }
        }

        /**
         * Sauvegarde des colonnes sélectionnées
         */
        function saveColumns() {
            NumahopStorageService.saveColumns(FILTER_STORAGE_SERVICE_KEY, statCtrl.columns);
        }

        /**
         * Visibilité d'une étape
         */
        function isVisible(value) {
            return statCtrl.columns.length === 0 || _.some(statCtrl.columns, function (c) {
                return c.identifier === value.identifier;
            });
        }
    }
})();
