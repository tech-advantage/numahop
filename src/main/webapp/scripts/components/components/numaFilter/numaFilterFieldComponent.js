(function () {
    "use strict";
    
    angular.module('numaHopApp.component')
        .component("numaFilterField", {
            bindings: {
                "title": "@"
            },
            require: {
                "filterCtrl": "^numaFilter"
            },
            transclude: true,
            templateUrl: "/scripts/components/components/numaFilter/numaFilterField.html"
        });
})();