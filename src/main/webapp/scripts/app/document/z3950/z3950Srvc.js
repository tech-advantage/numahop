(function () {
    'use strict';

    angular.module('numaHopApp.service').factory('Z3950Srvc', function ($resource, CONFIGURATION) {
        return $resource(
            CONFIGURATION.numahop.url + 'api/rest/z3950/:id',
            {
                id: '@identifier',
            },
            {
                search: {
                    method: 'POST',
                },
                import: {
                    method: 'POST',
                    isArray: false,
                    params: {
                        import: true,
                    },
                },
            }
        );
    });
})();
