(function () {
    'use strict';

    angular.module('numaHopApp.service').factory('CheckConfigurationSrvc', function ($resource, CONFIGURATION) {
        var service = $resource(
            CONFIGURATION.numahop.url + 'api/rest/checkconfiguration/:id',
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
                rules: {
                    method: 'GET',
                    isArray: true,
                    params: {
                        rules: true,
                    },
                },
                duplicate: {
                    method: 'GET',
                    isArray: false,
                    params: {
                        duplicate: true,
                    },
                },
                getByDocUnit: {
                    method: 'GET',
                    isArray: false,
                    params: {
                        docUnit: true,
                    },
                },
                getForEdition: {
                    method: 'GET',
                    isArray: false,
                    params: {
                        edition: true,
                    },
                },
            }
        );
        return service;
    });
})();
