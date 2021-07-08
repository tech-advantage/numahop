(function () {
    'use strict';

    angular.module('numaHopApp')
        .config(function ($routeProvider, gettext, USER_ROLES) {

            $routeProvider.when('/admin/utilsprojectlot', {
                templateUrl: 'scripts/app/admin/utilsprojectlot/utilsProjectLot.html',
                controller: 'UtilsProjectLotCtrl',
                title: gettext("Outils"),
                reloadOnSearch: false,
                access: {
                    authorizedRoles: [USER_ROLES.SUPER_ADMIN]
                }
            });
        });
})();