(function () {
    'use strict';

    angular.module('numaHopApp.service')
        .factory('Z3950ServerSrvc', function ($resource, CONFIGURATION) {
            return $resource(CONFIGURATION.numahop.url + 'api/rest/z3950Server/:id',
                {
                    id: '@identifier'
                }, {
                    dto: {
                        method: 'GET',
                        isArray: true,
                        params: {
                            dto: true
                        }
                    }
                });
        });
})();
