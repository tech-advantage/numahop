(function () {
    'use strict';

    angular.module('numaHopApp')
        .config(function ($routeProvider, gettext, USER_ROLES) {

            $routeProvider
                .when('/statistics/doc_published', {
                    templateUrl: 'scripts/app/statistics/doc_published.html',
                    controller: 'StatisticsDocPublishedCtrl',
                    controllerAs: 'statCtrl',
                    title: gettext("Statistiques: Documents publiés"),
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
                .when('/statistics/doc_rejected', {
                    templateUrl: 'scripts/app/statistics/doc_rejected.html',
                    controller: 'StatisticsDocRejectedCtrl',
                    controllerAs: 'statCtrl',
                    title: gettext("Statistiques: Documents rejetés"),
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
                .when('/statistics/project', {
                    templateUrl: 'scripts/app/statistics/projects.html',
                    controller: 'StatisticsProjectCtrl',
                    controllerAs: 'statProjectCtrl',
                    title: gettext("Statistiques: Projets"),
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                }).when('/statistics/workflow_average', {
                    templateUrl: 'scripts/app/statistics/workflow/workflowAvg.html',
                    controller: 'StatisticsWorkflowAverageCtrl',
                    controllerAs: 'statCtrl',
                    title: gettext("Statistiques: moyennes"),
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                }).when('/statistics/workflow_doc', {
                    templateUrl: 'scripts/app/statistics/workflow/workflowDoc.html',
                    controller: 'StatisticsWorkflowDocCtrl',
                    controllerAs: 'statCtrl',
                    title: gettext("Statistiques: états d'avancement des unités documentaires"),
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                }).when('/statistics/workflow_dlv', {
                    templateUrl: 'scripts/app/statistics/workflow/workflowDlv.html',
                    controller: 'StatisticsWorkflowDeliveryCtrl',
                    controllerAs: 'statCtrl',
                    title: gettext("Statistiques: états d'avancement des livraisons"),
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                }).when('/statistics/workflow_provider', {
                    templateUrl: 'scripts/app/statistics/workflow/workflowProvider.html',
                    controller: 'StatisticsWorkflowProviderCtrl',
                    controllerAs: 'statCtrl',
                    title: gettext("Statistiques: prestataires"),
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                }).when('/statistics/workflow_state', {
                    templateUrl: 'scripts/app/statistics/workflow/workflowState.html',
                    controller: 'StatisticsWorkflowStateCtrl',
                    controllerAs: 'statCtrl',
                    title: gettext("Statistiques: ?"),
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                }).when('/statistics/workflow_user', {
                    templateUrl: 'scripts/app/statistics/workflow/workflowUser.html',
                    controller: 'StatisticsWorkflowUserCtrl',
                    controllerAs: 'statCtrl',
                    title: gettext("Statistiques: moyennes sur les utilisateurs"),
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                });
        });
})();