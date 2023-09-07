(function () {
    'use strict';

    angular.module('numaHopApp').config(function ($routeProvider, gettext, USER_ROLES) {
        $routeProvider.when('/admin/health', {
            templateUrl: 'scripts/app/admin/health/health.html',
            controller: 'HealthController',
            title: gettext('Health checks'),
            reloadOnSearch: false,
            access: {
                authorizedRoles: [USER_ROLES.SUPER_ADMIN],
            },
        });
    });
})();
