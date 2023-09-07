(function () {
    'use strict';

    angular.module('numaHopApp.service').factory('CondReportSlipConfSrvc', CondReportSlipConfSrvc);

    function CondReportSlipConfSrvc(CONFIGURATION, $resource) {
        var service = $resource(CONFIGURATION.numahop.url + 'api/rest/condreportslip_configuration/:id', { id: '@identifier' }, {});
        return service;
    }
})();
