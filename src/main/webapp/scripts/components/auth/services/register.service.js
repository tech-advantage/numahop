(function () {
    'use strict';

    angular.module('numaHopApp').factory('Register', function ($resource) {
        return $resource('api_int/register', {}, {});
    });
})();