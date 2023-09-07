(function () {
    'use strict';

    angular.module('numaHopApp').config(function ($routeProvider, gettext, USER_ROLES) {
        $routeProvider
            .when('/administration/appconfiguration/z3950Server', {
                templateUrl: 'scripts/app/configuration/z3950/z3950Server.html',
                title: gettext('Serveurs Z39.50'),
                reloadOnSearch: false,
                controller: 'Z3950ServerCtrl',
                controllerAs: 'mainCtrl',
                access: {
                    authorizedRoles: [USER_ROLES.Z3950_HAB0],
                },
            })
            .when('/administration/appconfiguration/z3950Server/:mode/:identifier?', {
                templateUrl: 'scripts/app/configuration/z3950/z3950Server.html',
                title: gettext('Serveur Z39.50'),
                controller: 'Z3950ServerCtrl',
                controllerAs: 'mainCtrl',
                access: {
                    authorizedRoles: [USER_ROLES.Z3950_HAB0],
                },
            });
    });
})();
