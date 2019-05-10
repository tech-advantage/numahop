(function () {
    "use strict";

    angular.module('numaHopApp').factory('Lot', function ($resource) {
        return $resource('api_int/lots/:login', {}, {
            'query': {
                method: 'GET',
                isArray: true
            },
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            }
        });
    });
})();