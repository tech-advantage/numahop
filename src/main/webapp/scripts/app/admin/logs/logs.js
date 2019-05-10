(function () {
    'use strict';

    angular.module('numaHopApp')
        .config(function ($routeProvider, gettext, USER_ROLES) {

            $routeProvider.when('/admin/logs', {
                templateUrl: 'scripts/app/admin/logs/logs.html',
                controller: 'LogsController',
                title: gettext("Logs"),
                reloadOnSearch: false,
                access: {
                    authorizedRoles: [USER_ROLES.SUPER_ADMIN]
                }
            });
        });
})();