(function () {
    "use strict";

    angular.module('numaHopApp.component')
        .component("todoTable", {
            bindings: {
                "todoList": "<",
                "state": "<"
            },
            controller: "todoTableCtrl",
            templateUrl: "/scripts/components/components/numaAlertWorkflowTodo/todoTable/todoTable.html"
        });
})();
