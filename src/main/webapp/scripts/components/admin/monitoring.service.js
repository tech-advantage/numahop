(function () {
    'use strict';

    angular.module('numaHopApp')
        .factory('MonitoringService', function ($http) {
            return {
                getMetrics: function () {
                    return $http.get('api_int/jhi-metrics').then(function (response) {
                        return response.data;
                    });
                },

                checkHealth: function () {
                    return $http.get('api/rest/health').then(function (response) {
                        return response.data;
                    });
                }


            };
        });
})();