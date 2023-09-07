(function () {
    'use strict';

    angular.module('numaHopApp.service').factory('CinesLangCodeSrvc', function ($resource, CONFIGURATION) {
        var service = $resource(
            CONFIGURATION.numahop.url + 'api/rest/conf/cineslangcode/:id',
            {
                id: '@identifier',
            },
            {
                update: {
                    method: 'POST',
                    isArray: true,
                    params: {},
                },
                loadActiveCinesCodes: {
                    method: 'GET',
                    isArray: true,
                    params: {},
                },
            }
        );

        return service;
    });
})();
