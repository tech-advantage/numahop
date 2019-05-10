(function () {
    'use strict';

    angular.module('numaHopApp')
        .config(function ($routeProvider, gettext, USER_ROLES) {

            $routeProvider.when('/user/user', {
                templateUrl: 'scripts/app/user/users.html',
                controller: 'UserCtrl',
                title: gettext("Utilisateurs"),
                reloadOnSearch: false,
                access: {
                    authorizedRoles: [USER_ROLES.USER_HAB0]
                }
            }).when('/user/authorization', {
                templateUrl: 'scripts/app/user/authorizations.html',
                controller: 'UserAuthorizationCtrl',
                title: gettext("Habilitations"),
                reloadOnSearch: false,
                access: {
                    authorizedRoles: [USER_ROLES.ROLE_HAB0]
                }
            }).when('/user/role', {
                templateUrl: 'scripts/app/user/roles.html',
                controller: 'UserRoleCtrl',
                title: gettext("Rôles"),
                reloadOnSearch: false,
                access: {
                    authorizedRoles: [USER_ROLES.ROLE_HAB0]
                }
            });
        });
})();