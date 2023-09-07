(function () {
    'use strict';

    angular.module('numaHopApp').config(function ($routeProvider, gettext, USER_ROLES) {
        $routeProvider.when('/admin/utilsworkflow', {
            templateUrl: 'scripts/app/admin/utilsworkflow/utilsWorkflow.html',
            controller: 'UtilsWorkflowCtrl',
            title: gettext('Outils'),
            reloadOnSearch: false,
            access: {
                authorizedRoles: [USER_ROLES.SUPER_ADMIN],
            },
        });
    });
})();
