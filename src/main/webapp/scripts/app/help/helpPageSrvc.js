(function () {
    'use strict';

    angular.module('numaHopApp.service').factory('HelpPageSrvc', function ($resource, CONFIGURATION) {
        var service = $resource(
            CONFIGURATION.numahop.url + 'api/rest/help/:id',
            {
                id: '@identifier',
            },
            {
                search: {
                    method: 'GET',
                    isArray: true,
                },
                findAllModules: {
                    method: 'GET',
                    isArray: true,
                    params: {
                        modulelist: true,
                    },
                },
                searchByTag: {
                    method: 'GET',
                    isArray: false,
                },
            }
        );
        return service;
    });
})();
