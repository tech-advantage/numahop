(function () {
    "use strict";

    angular.module('numaHopApp.component')
        .component("numaResultList", {
            bindings: {
                "getPage": "&",
                "pagination": "<",
                "pageSize": "<",
                "selectTitle": "&",
                "sortOptions": "<",
                "title": "@"
            },
            transclude: {
                "selection": "?numaResultListSelection" // numaResultListSelection est à la fois un slot pour le transclude, et un composant (optionnel)
            },
            templateUrl: "/scripts/components/components/numaResultList/numaResultList.html",
            controller: function () {
                var ctrl = this;
                ctrl.reinitPage = reinitPage;

                function reinitPage() {
                    ctrl.pagination.page = 1;
                    ctrl.getPage();
                }
            }
        });
})();