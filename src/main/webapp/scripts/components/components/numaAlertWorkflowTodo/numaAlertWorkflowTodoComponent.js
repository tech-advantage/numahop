(function () {
    'use strict';

    angular.module('numaHopApp.component').component('numaAlertWorkflowTodo', {
        bindings: {
            topClass: '@',
            projects: '<',
            lots: '<',
        },
        controller: 'NumaAlertWorkflowTodoCtrl',
        templateUrl: '/scripts/components/components/numaAlertWorkflowTodo/numaAlertWorkflowTodo.html',
    });
})();
