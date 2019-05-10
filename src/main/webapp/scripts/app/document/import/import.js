(function () {
    'use strict';

    angular.module('numaHopApp')
        .config(function ($routeProvider, gettext, USER_ROLES) {

            $routeProvider.when('/document/import/:identifier?', {
                templateUrl: 'scripts/app/document/import/import.html',
                controller: 'ImportCtrl',
                controllerAs: 'mainCtrl',
                reloadOnSearch: false,
                title: gettext("Import d'unités documentaires"),
                access: {
                    authorizedRoles: [USER_ROLES.EXC_HAB0]
                }
            });
        });
})();