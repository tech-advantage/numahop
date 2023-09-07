(function () {
    'use strict';

    angular.module('numaHopApp').factory('AccountAdmin', function AccountAdmin($resource) {
        return $resource(
            'api_int/account',
            {},
            {
                get: {
                    method: 'GET',
                    params: {},
                    isArray: false,
                    interceptor: {
                        response: function (response) {
                            // expose response
                            return response;
                        },
                    },
                },
            }
        );
    });
})();
