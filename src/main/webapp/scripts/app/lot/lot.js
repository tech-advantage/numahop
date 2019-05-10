(function () {
    'use strict';

    angular.module('numaHopApp')
        .config(function ($routeProvider, gettext, USER_ROLES) {

            $routeProvider.when('/lot/lot', {
                templateUrl: 'scripts/app/lot/lots.html',
                controller: 'LotCtrl',
                title: gettext("Lots"),
                reloadOnSearch: false,
                access: {
                    authorizedRoles: [USER_ROLES.LOT_HAB3]
                }
            });

            $routeProvider.when('/lot/all_operations', {
                templateUrl: 'scripts/app/lot/allOperations.html',
                controller: 'LotAllOperationsCtrl',
                title: gettext("Lots"),
                reloadOnSearch: false,
                access: {
                    authorizedRoles: [USER_ROLES.LOT_HAB3]
                }
            });

            $routeProvider.when('/lot/lot_list', {
                templateUrl: 'scripts/app/lot/lotList.html',
                controller: 'LotListCtrl',
                controllerAs: 'mainCtrl',
                title: gettext("Lots"),
                reloadOnSearch: false,
                access: {
                    authorizedRoles: [USER_ROLES.LOT_HAB3]
                }
            });
        });
})();