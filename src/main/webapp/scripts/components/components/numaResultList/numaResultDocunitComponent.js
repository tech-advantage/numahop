(function () {
    "use strict";

    angular.module('numaHopApp.component')
        .component("numaResultDocunit", {
            require: {
                "listCtrl": "^numaResultList"
            },
            bindings: {
                "isSelected": "&numaIsSelected",
                "modeSelect": "<numaModeSelect",
                "toggleSelection": "&numaToggleSelection"
            },
            controller: function () {
                var ctrl = this;

                ctrl.$onInit = function () {
                    ctrl.pagination = ctrl.listCtrl.pagination;
                };
                ctrl.clickItem = function (item, event) {
                    if (ctrl.modeSelect) {
                        ctrl.toggleSelection({ $item: item });
                        event.preventDefault();
                    }
                };
            },
            templateUrl: "/scripts/components/components/numaResultList/numaResultDocunit.html"
        });
})();