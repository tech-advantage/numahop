(function () {
    "use strict";

    angular.module('numaHopApp')
        .config(function ($routeProvider, gettext, USER_ROLES) {

            $routeProvider.when('/checkconfiguration/checkconfiguration', {
                templateUrl: 'scripts/app/checkconfiguration/configurations.html',
                controller: 'CheckConfigurationCtrl',
                title: gettext("Configurations des contrôles"),
                reloadOnSearch: false,
                access: {
                    authorizedRoles: [USER_ROLES.CHECK_HAB0]
                }
            });
        });
})();