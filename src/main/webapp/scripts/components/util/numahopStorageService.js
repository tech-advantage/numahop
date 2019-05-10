(function () {
    'use strict';

    angular.module('numaHopApp.service')
        .factory('NumahopStorageService', NumahopStorageService);

    function NumahopStorageService(localStorageService) {

        var LOCAL_STORAGE_NAME_FILTERS = 'filters';
        var LOCAL_STORAGE_NAME_COLUMNS = 'columns';
        var LOCAL_STORAGE_NAME_PAGESIZES = 'pageSizes';


        function getColumns(page) {
            var columns = localStorageService.get(LOCAL_STORAGE_NAME_COLUMNS + "." + page);
            return angular.isObject(columns) ? columns : false;
        }

        function getFilter(page) {
            var filter = localStorageService.get(LOCAL_STORAGE_NAME_FILTERS + "." + page);
            return angular.isObject(filter) ? filter : false;
        }

        function getPageSize(page) {
            var pagesize = localStorageService.get(LOCAL_STORAGE_NAME_PAGESIZES + "." + page);
            return pagesize;
        }

        function saveColumns(page, columns) {
            if (!columns) {
                columns = [];
            }
            localStorageService.set(LOCAL_STORAGE_NAME_COLUMNS + "." + page, columns);
        }

        function saveFilter(page, filter) {
            if (!filter) {
                filter = {};
            }
            localStorageService.set(LOCAL_STORAGE_NAME_FILTERS + "." + page, filter);
        }

        function savePageSize(page, size) {
            localStorageService.set(LOCAL_STORAGE_NAME_PAGESIZES + "." + page, size);
        }

        return {
            getColumns: getColumns,
            getFilter: getFilter,
            getPageSize: getPageSize,
            saveColumns: saveColumns,
            saveFilter: saveFilter,
            savePageSize: savePageSize
        };
    }
})();
