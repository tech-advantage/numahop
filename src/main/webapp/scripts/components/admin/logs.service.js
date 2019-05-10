(function () {
    'use strict';

    angular.module('numaHopApp').factory('LogsService', function ($resource) {
        return $resource('api_int/logs', {}, {
            'findAll': {
                method: 'GET',
                isArray: true
            },
            'changeLevel': {
                method: 'PUT'
            }
        });
    });
})();