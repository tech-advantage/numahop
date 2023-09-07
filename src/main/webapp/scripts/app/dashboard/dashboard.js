(function () {
    'use strict';

    angular.module('numaHopApp').config(function ($routeProvider, gettext, USER_ROLES, dashboardProvider) {
        $routeProvider.when('/dashboard', {
            templateUrl: 'scripts/app/dashboard/dashboard.html',
            controller: 'DashboardCtrl',
            title: gettext('Tableau de bord'),
            access: {
                authorizedRoles: [USER_ROLES.all],
            },
        });

        dashboardProvider.structure('4-2-2', {
            rows: [
                {
                    columns: [
                        {
                            styleClass: 'col-md-3',
                        },
                        {
                            styleClass: 'col-md-3',
                        },
                        {
                            styleClass: 'col-md-3',
                        },
                        {
                            styleClass: 'col-md-3',
                        },
                    ],
                },
                {
                    columns: [
                        {
                            styleClass: 'col-md-6',
                        },
                        {
                            styleClass: 'col-md-6',
                        },
                    ],
                },
                {
                    columns: [
                        {
                            styleClass: 'col-md-6',
                        },
                        {
                            styleClass: 'col-md-6',
                        },
                    ],
                },
            ],
        });
    });
})();
