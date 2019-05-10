(function () {
    "use strict";

    angular.module('numaHopApp.component')
        .component("numaResultListSelection", {
            bindings: {
                baseUrl: "<",
                clearSelection: "&",
                isPageSelected: "&",
                selectPage: "&",
                selectionCount: "&",
                unselectPage: "&"
            },
            templateUrl: "/scripts/components/components/numaResultList/numaResultListSelection.html",
        });
})();