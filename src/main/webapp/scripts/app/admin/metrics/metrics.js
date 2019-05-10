(function () {
    'use strict';

    angular.module('numaHopApp')
        .config(function ($routeProvider, gettext, USER_ROLES) {

            $routeProvider.when('/admin/metrics', {
                templateUrl: 'scripts/app/admin/metrics/metrics.html',
                controller: 'MetricsController',
                title: gettext("Application Metrics"),
                reloadOnSearch: false,
                access: {
                    authorizedRoles: [USER_ROLES.SUPER_ADMIN]
                }
            });
        });
})();