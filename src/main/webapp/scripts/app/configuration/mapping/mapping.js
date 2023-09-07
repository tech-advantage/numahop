(function () {
    'use strict';

    angular.module('numaHopApp').config(function ($routeProvider, gettext, USER_ROLES) {
        $routeProvider.when('/administration/appconfiguration/mapping/:type', {
            templateUrl: 'scripts/app/configuration/mapping/mapping.html',
            controller: 'MappingCtrl',
            controllerAs: 'mainCtrl',
            title: gettext('Configuration des mappings'),
            access: {
                authorizedRoles: [USER_ROLES.MAP_HAB0],
            },
        });
    });
})();
