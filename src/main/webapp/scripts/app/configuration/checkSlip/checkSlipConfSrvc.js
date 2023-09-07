(function () {
    'use strict';

    angular.module('numaHopApp.service').factory('CheckSlipConfSrvc', CheckSlipConfSrvc);

    function CheckSlipConfSrvc(CONFIGURATION, $resource) {
        var service = $resource(CONFIGURATION.numahop.url + 'api/rest/checkslip_configuration/:id', { id: '@identifier' }, {});
        return service;
    }
})();
