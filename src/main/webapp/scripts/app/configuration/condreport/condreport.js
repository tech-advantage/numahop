(function () {
    'use strict';

    angular.module('numaHopApp')
        .config(function ($routeProvider, gettext, USER_ROLES) {

            $routeProvider.when('/administration/appconfiguration/condreportdesc', {
                templateUrl: 'scripts/app/configuration/condreport/condreportDesc.html',
                title: gettext("Constat d'état: propriétés"),
                reloadOnSearch: false,
                controller: 'CondreportDescCtrl',
                controllerAs: 'mainCtrl',
                access: {
                    authorizedRoles: [USER_ROLES.COND_REPORT_HAB5]
                }
            });
        });
})();
