(function () {
    'use strict';

    angular.module('numaHopApp')
        .config(function ($routeProvider, gettext, USER_ROLES) {

            $routeProvider.when('/train/train', {
                templateUrl: 'scripts/app/train/trains.html',
                controller: 'TrainCtrl',
                title: gettext("Trains"),
                reloadOnSearch: false,
                access: {
                    authorizedRoles: [USER_ROLES.TRA_HAB3]
                }
            });

            $routeProvider.when('/train/all_operations', {
                templateUrl: 'scripts/app/train/allOperations.html',
                controller: 'TrainAllOperationsCtrl',
                title: gettext("Trains"),
                reloadOnSearch: false,
                access: {
                    authorizedRoles: [USER_ROLES.TRA_HAB3]
                }
            });
        });
})();