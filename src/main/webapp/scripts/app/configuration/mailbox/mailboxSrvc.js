(function () {
    'use strict';

    angular.module('numaHopApp.service')
        .factory('MailboxSrvc', MailboxSrvc);

    function MailboxSrvc(CONFIGURATION, $resource) {
        return $resource(CONFIGURATION.numahop.url + 'api/rest/conf_mail/:id', { id: '@identifier' }, {
            duplicate: {
                method: 'GET',
                isArray: false,
                params: {
                    'duplicate': true
                }
            }
        });
    }
})();
