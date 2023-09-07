(function () {
    'use strict';

    angular.module('numaHopApp').config(function ($routeProvider, gettext, USER_ROLES) {
        $routeProvider.when('/administration/appconfiguration/mailbox', {
            templateUrl: 'scripts/app/configuration/mailbox/mailbox.html',
            controller: 'MailboxCtrl',
            controllerAs: 'mainCtrl',
            reloadOnSearch: false,
            title: gettext('Configuration des comptes email'),
            access: {
                authorizedRoles: [USER_ROLES.MAIL_HAB0],
            },
        });
    });
})();
