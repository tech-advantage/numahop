(function () {
    'use strict';

    angular.module('numaHopApp')
        .config(function ($routeProvider, gettext, USER_ROLES) {

            $routeProvider.when('/administration/appconfiguration/csvmapping', {
                templateUrl: 'scripts/app/configuration/csvMapping/csvMapping.html',
                controller: 'CSVMappingCtrl',
                controllerAs: 'mainCtrl',
                title: gettext("Configuration des mappings CSV"),
                access: {
                    authorizedRoles: [USER_ROLES.MAP_HAB0]
                }
            });
        });
})();
