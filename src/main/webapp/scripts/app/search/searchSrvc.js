(function () {
    'use strict';

    angular.module('numaHopApp.service').factory('SearchSrvc', SearchSrvc);

    function SearchSrvc($resource, CONFIGURATION) {
        var service = $resource(
            CONFIGURATION.numahop.url + 'api/rest/search',
            {},
            {
                search: {
                    method: 'GET',
                    isArray: false,
                    params: {
                        search: '',
                    },
                },
                index: {
                    method: 'GET',
                    params: {
                        index: true,
                    },
                },
            }
        );

        service.getSearch = getSearch;
        service.setSearch = setSearch;

        var variables = {};

        function getSearch() {
            return variables.search || {};
        }

        function setSearch(search) {
            variables.search = search;

            if (_.isEmpty(variables.search.search)) {
                variables.search.search = [''];
            }
        }

        return service;
    }
})();
