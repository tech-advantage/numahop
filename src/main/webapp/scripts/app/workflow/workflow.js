(function () {
    'use strict';

    angular.module('numaHopApp')
        .config(function ($routeProvider, gettext) {

            $routeProvider.when('/workflow/group', {
                templateUrl: 'scripts/app/workflow/groups.html',
                controller: 'WorkflowGroupCtrl',
                title: gettext("Groupes de workflow"),
                reloadOnSearch: false
            });

            $routeProvider.when('/workflow/model', {
                templateUrl: 'scripts/app/workflow/models.html',
                controller: 'WorkflowModelCtrl',
                title: gettext("Modèles de workflow"),
                reloadOnSearch: false
            });
        });
})();