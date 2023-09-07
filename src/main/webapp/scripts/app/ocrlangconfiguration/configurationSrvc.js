(function () {
    'use strict';

    angular.module('numaHopApp.service').factory('OcrLangConfigurationSrvc', function ($resource, CONFIGURATION) {
        var service = $resource(
            CONFIGURATION.numahop.url + 'api/rest/ocrlangconfiguration/:id',
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
            }
        );
        return service;
    });

    angular.module('numaHopApp.service').factory('OcrLanguageSrvc', function ($resource, CONFIGURATION) {
        var service = $resource(
            CONFIGURATION.numahop.url + 'api/rest/ocrlanguages/:id',
            {
                id: '@identifier',
            },
            {
                languages: {
                    method: 'GET',
                    isArray: true,
                    params: {
                        languages: true,
                    },
                },
                langs: {
                    method: 'GET',
                    isArray: true,
                    params: {
                        langs: true,
                    },
                },
            }
        );
        return service;
    });
})();
