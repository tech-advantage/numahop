(function () {
    'use strict';

    angular.module('numaHopApp').config(function ($routeProvider, gettext, USER_ROLES) {
        $routeProvider
            .when('/statistics/dashboard_profile', {
                templateUrl: 'scripts/app/statistics/dashboard/profileActivity.html',
                controller: 'StatisticsDashboardProfileActivityCtrl',
                controllerAs: 'statCtrl',
                title: gettext('Tableau de bord des profils'),
                access: {
                    authorizedRoles: [USER_ROLES.all],
                },
            })
            .when('/statistics/dashboard_progress', {
                templateUrl: 'scripts/app/statistics/dashboard/projectProgress.html',
                controller: 'StatisticsDashboardProjectProgressCtrl',
                controllerAs: 'statCtrl',
                title: gettext("Tableau de bord d'avancement des projets"),
                access: {
                    authorizedRoles: [USER_ROLES.all],
                },
            })
            .when('/statistics/dashboard_user', {
                templateUrl: 'scripts/app/statistics/dashboard/userActivity.html',
                controller: 'StatisticsDashboardUserActivityCtrl',
                controllerAs: 'statCtrl',
                title: gettext('Tableau de bord des utilisateurs'),
                access: {
                    authorizedRoles: [USER_ROLES.all],
                },
            })
            .when('/statistics/dashboard_train', {
                templateUrl: 'scripts/app/statistics/dashboard/trainProvider.html',
                controller: 'StatisticsDashboardTrainProviderCtrl',
                controllerAs: 'statCtrl',
                title: gettext('Tableau de bord des trains'),
                access: {
                    authorizedRoles: [USER_ROLES.all],
                },
            });
    });
})();
