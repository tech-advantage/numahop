(function () {
    'use strict';

    angular.module('numaHopApp').factory('Sessions', function ($resource) {
        return $resource(
            'api_int/account/sessions/:series',
            {},
            {
                getAll: {
                    method: 'GET',
                    isArray: true,
                },
            }
        );
    });
})();
