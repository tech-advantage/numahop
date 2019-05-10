(function () {
    'use strict';

    angular.module('numaHopApp').factory('Activate', function ($resource) {
        return $resource('api_int/activate', {}, {
            'get': {
                method: 'GET',
                params: {},
                isArray: false
            }
        });
    });
})();