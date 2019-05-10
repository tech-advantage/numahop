(function () {
    'use strict';

    angular.module('numaHopApp')
        .config(function ($routeProvider, gettext, USER_ROLES) {

            $routeProvider.when('/administration/appconfiguration/docpropertytype', {
                templateUrl: 'scripts/app/configuration/docPropertyType/docPropertyType.html',
                controller: 'DocPropertyTypeCtrl',
                controllerAs: 'mainCtrl',
                title: gettext("Configuration des types de propriété"),
                access: {
                    authorizedRoles: [USER_ROLES.DOC_UNIT_HAB0]
                }
            });
        });
})();
