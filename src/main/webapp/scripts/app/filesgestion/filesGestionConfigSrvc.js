(function () {
    'use strict';

    angular.module('numaHopApp.service')
        .factory('FilesGestionConfigSrvc', function (CONFIGURATION, $resource) {

            var service = $resource(CONFIGURATION.numahop.url + 'api/rest/filesgestionconfig/:id', {
                id: '@identifier'
            }, {
                    findByLibraryId: {
                        method: 'GET',
                        isArray: false,
                        params: {}
                    },
                    saveOrUpdate: {
                        method: 'POST',
                        isArray: false,
                        params: {}
                    }
                });


            return service;
        });
})();