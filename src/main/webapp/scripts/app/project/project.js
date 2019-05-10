(function () {
    'use strict';

    angular.module('numaHopApp')
        .config(function ($routeProvider, gettext, USER_ROLES) {

            $routeProvider.when('/project/project', {
                templateUrl: 'scripts/app/project/projects.html',
                controller: 'ProjectCtrl',
                title: gettext("Projet"),
                reloadOnSearch: false,
                access: {
                    authorizedRoles: [USER_ROLES.PROJ_HAB7]
                }
            });

            $routeProvider.when('/project/all_operations', {
                templateUrl: 'scripts/app/project/allOperations.html',
                controller: 'AllOperationsCtrl',
                title: gettext("Création d'un projet"),
                reloadOnSearch: false,
                access: {
                    authorizedRoles: [USER_ROLES.PROJ_HAB7]
                }
            });
        });
})();