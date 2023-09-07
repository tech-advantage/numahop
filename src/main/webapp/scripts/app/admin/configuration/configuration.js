(function () {
    'use strict';

    angular.module('numaHopApp').config(function ($routeProvider, gettext, USER_ROLES) {
        $routeProvider.when('/admin/configuration', {
            templateUrl: 'scripts/app/admin/configuration/configuration.html',
            controller: 'ConfigurationController',
            title: gettext('Configuration'),
            reloadOnSearch: false,
            access: {
                authorizedRoles: [USER_ROLES.SUPER_ADMIN],
            },
        });
    });
})();
