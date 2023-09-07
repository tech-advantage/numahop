(function () {
    'use strict';

    angular.module('numaHopApp.service').factory('DocPropertyTypeSrvc', function ($resource, CONFIGURATION) {
        var service = $resource(
            CONFIGURATION.numahop.url + 'api/rest/docpropertytype/:id',
            {
                id: '@identifier',
            },
            {
                dto: {
                    method: 'GET',
                    isArray: true,
                    params: {
                        dto: true,
                    },
                },
                dtoCustom: {
                    method: 'GET',
                    isArray: true,
                    params: {
                        customOnly: true,
                    },
                },
            }
        );
        return service;
    });
})();
