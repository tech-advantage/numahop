(function () {
    'use strict';

    angular.module('numaHopApp.service')
        .factory('CSVMappingSrvc', CSVMappingSrvc);

    function CSVMappingSrvc($resource, CONFIGURATION, gettextCatalog) {
        var service = $resource(CONFIGURATION.numahop.url + 'api/rest/csvmapping/:id', { id: '@identifier' }, {
            duplicate: {
                method: 'GET',
                isArray: false,
                params: {
                    'duplicate': true
                }
            }
        });
        return service;
    }
})();
