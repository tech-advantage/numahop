(function () {
    'use strict';

    angular.module('numaHopApp')
        .config(function ($routeProvider, gettext, USER_ROLES) {

            $routeProvider.when('/checks/checks/', {
                templateUrl: 'scripts/app/checks/checks.html',
                controller: 'CheckCtrl',
                title: gettext("Contrôles"),
                controllerAs: 'mainCtrl',
                reloadOnSearch: false,
                access: {
                    authorizedRoles: [USER_ROLES.CHECK_HAB3]
                }
            });
        });
})();