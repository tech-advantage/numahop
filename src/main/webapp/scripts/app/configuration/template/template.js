(function () {
    'use strict';

    angular.module('numaHopApp')
        .config(function ($routeProvider, gettext, USER_ROLES) {
            $routeProvider.when('/administration/appconfiguration/template', {
                templateUrl: 'scripts/app/configuration/template/template.html',
                controller: 'TemplateCtrl',
                controllerAs: 'mainCtrl',
                reloadOnSearch: false,
                title: gettext("Configuration des templates"),
                access: {
                    authorizedRoles: [USER_ROLES.TPL_HAB0]
                }
            });
        });
})();
