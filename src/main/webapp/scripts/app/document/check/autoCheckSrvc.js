(function () {
    'use strict';

    angular.module('numaHopApp.service').factory('AutoCheckSrvc', AutoCheckSrvc);

    function AutoCheckSrvc(CONFIGURATION, $resource) {
        return $resource(
            CONFIGURATION.numahop.url + 'api/rest/check/auto:id',
            {
                id: '@identifier',
            },
            {
                facile: {
                    method: 'GET',
                    isArray: false,
                    params: {
                        facile: true,
                    },
                },
            }
        );
    }
})();
