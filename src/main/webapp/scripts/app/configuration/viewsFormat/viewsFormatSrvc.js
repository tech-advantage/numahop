(function () {
    'use strict';

    angular.module('numaHopApp.service').factory('ViewsFormatSrvc', function ($resource, CONFIGURATION) {
        var service = $resource(
            CONFIGURATION.numahop.url + 'api/rest/viewsformat/:id',
            {
                id: '@identifier',
            },
            {
                dto: {
                    method: 'GET',
                    isArray: false,
                    params: {
                        dto: true,
                    },
                },
                search: {
                    method: 'GET',
                    isArray: false,
                    params: {
                        search: true,
                        size: 50,
                    },
                },
            }
        );
        return service;
    });
})();
