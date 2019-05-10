(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('StatisticsProjectCtrl', StatisticsProjectCtrl);

    function StatisticsProjectCtrl(codeSrvc, gettextCatalog, HistorySrvc, NumahopStorageService, StatisticsSrvc) {

        var statCtrl = this;

        /** others actions **/
        statCtrl.changePageSize = changePageSize;
        statCtrl.codes = codeSrvc;
        statCtrl.getPage = getPage;
        statCtrl.reinitFilters = reinitFilters;
        statCtrl.searchValue = searchValue;
        statCtrl.search = search;

        var PAGE_START = 1;
        var FILTER_STORAGE_SERVICE_KEY = "stat_project_list";


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
         * Modèle pour le tri
         * @type {Object}
         */
        statCtrl.sortModel = ["name"];

        init();

        /**
         * Initialisation du controleur
         * @return {[type]} [description]
         */
        function init() {
            HistorySrvc.add(gettextCatalog.getString("Statistiques des projets"));
            statCtrl.loaded = false;

            loadSearch();
            return getPage().then(function () {
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

            delete statCtrl.filters.search;
            delete statCtrl.filters.from;
            delete statCtrl.filters.to;

            search();
        }

        /**
         * Recherche d'entité
         * @return {[type]} [description]
         */
        function search(sortModel) {
            if (angular.isDefined(sortModel)) {
                statCtrl.sortModel = sortModel;
            }
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

            return StatisticsSrvc.projectList(params, handlePageOfItems).$promise;
        }

        /**
         * Construction des paramètres de la recherche en fonction des filtres sélectionnés
         * @return {[type]} [description]
         */
        function getSearchParams() {
            var params = {
                page: statCtrl.pagination.page - 1,
                size: statCtrl.pagination.size,
                search: statCtrl.filters.search || "",
                from: statCtrl.filters.from,
                to: statCtrl.filters.to,
                sorts: statCtrl.sortModel
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
    }
})();
