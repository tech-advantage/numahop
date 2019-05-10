(function () {
    'use strict';

    angular.module('numaHopApp.service')
        .factory('AuditSrvc', AuditSrvc);

    function AuditSrvc(CONFIGURATION, $resource) {
        return $resource(CONFIGURATION.numahop.url + 'api/rest/audit/:type/:id', { id: '@identifier', type: '@type' }, {});
    }
})();
