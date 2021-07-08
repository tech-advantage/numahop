(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('StatisticsDocRejectedCtrl', StatisticsDocRejectedCtrl);

    function StatisticsDocRejectedCtrl($q, codeSrvc, HistorySrvc, gettextCatalog, LibrarySrvc,
        NumahopStorageService, Principal, ProjectSrvc, StatisticsSrvc, USER_ROLES) {

        var statCtrl = this;

        /** others actions **/
        statCtrl.changePageSize = changePageSize;
        statCtrl.codes = codeSrvc;
        statCtrl.getExportUrl = getExportUrl;
        statCtrl.getPage = getPage;
        statCtrl.reinitFilters = reinitFilters;
        statCtrl.searchValue = searchValue;
        statCtrl.search = search;

        var PAGE_START = 1;
        var FILTER_STORAGE_SERVICE_KEY = "stat_doc_rejected";

        /**
         * Objet de pagination
         * @type {Object}
         */
        statCtrl.pagination = {
            items: [],
            totalItems: 0,
            busy: false,
            page: PAGE_START
        };

        /**
         * Taille pagination
         * @type {Object}
         */
        statCtrl.sizeOptions = [
            { value: 10, label: "10" },
            { value: 20, label: "20" },
            { value: 50, label: "50" },
            { value: 100, label: "100" },
            { value: 5000, label: "Tout" }
        ];

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
            projects: {
                text: "name",
                placeholder: gettextCatalog.getString("Projet"),
                trackby: "identifier",
                refresh: function ($select) {
                    var searchParams = {
                        page: 0,
                        search: $select.search,
                        active: false
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
            HistorySrvc.add(gettextCatalog.getString("Statistiques: documents rejetés"));
            statCtrl.loaded = false;
            statCtrl.showLib = Principal.isInRole(USER_ROLES.SUPER_ADMIN) || Principal.isInRole(USER_ROLES.ADMINISTRATION_LIB);

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
            statCtrl.pagination.items = [];
            statCtrl.pagination.totalItems = 0;
            statCtrl.pagination.busy = false;
            statCtrl.pagination.page = PAGE_START;
            statCtrl.pagination.size = statCtrl.pageSize;

            statCtrl.filters.library = [];
            statCtrl.filters.project = [];
            delete statCtrl.filters.importedFrom;
            delete statCtrl.filters.importedTo;

            search();
        }

        /**
         * Recherche d'entités
         * @return {[type]} [description]
         */
        function search() {
            statCtrl.pagination.page = 1;
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
            statCtrl.pagination.busy = true;
            var params = getSearchParams();

            return StatisticsSrvc.docRejected(params, handlePageOfItems).$promise;
        }

        /**
         * Construction des paramètres de la recherche en fonction des filtres sélectionnés
         * @return {[type]} [description]
         */
        function getSearchParams() {
            var params = {
                page: statCtrl.pagination.page - 1,
                size: statCtrl.pagination.size,
                library: _.pluck(statCtrl.filters.library, "identifier"),
                project: _.pluck(statCtrl.filters.project, "identifier"),
                from: statCtrl.filters.importedFrom,
                to: statCtrl.filters.importedTo
            };
            return params;
        }

        /**
         * handlePageOfItems - Gestion d'une page d'entités
         *
         * @param  {type} pageOfReserves la page avec les entités
         */
        function handlePageOfItems(pageOfItems) {
            statCtrl.pagination.totalItems = pageOfItems.totalElements;
            statCtrl.pagination.totalPages = pageOfItems.totalPages;
            statCtrl.pagination.items = pageOfItems.content;
            statCtrl.pagination.busy = false;
        }

        function changePageSize() {
            statCtrl.pagination.size = statCtrl.pageSize;
            search();
        }

        function loadSearch() {
            statCtrl.filters = NumahopStorageService.getFilter(FILTER_STORAGE_SERVICE_KEY);
            if (!statCtrl.filters) {
                statCtrl.filters = {};
            }
            var savedSize = NumahopStorageService.getPageSize(FILTER_STORAGE_SERVICE_KEY);
            statCtrl.pageSize = savedSize ? savedSize : 10;
            statCtrl.pagination.size = statCtrl.pageSize;
        }

        function saveSearch() {
            NumahopStorageService.savePageSize(FILTER_STORAGE_SERVICE_KEY, statCtrl.pageSize);
            NumahopStorageService.saveFilter(FILTER_STORAGE_SERVICE_KEY, statCtrl.filters);
        }

        function getExportUrl() {
            var params = getSearchParams();
            params.doc_rejected = true;
            return StatisticsSrvc.getExportUrl(params, "docunit");
        }
    }
})();
