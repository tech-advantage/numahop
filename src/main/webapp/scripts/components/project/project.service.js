(function () {
    "use strict";

    angular.module('numaHopApp').factory('Project', function ($resource) {
        return $resource('api_int/projects/:login', {}, {
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