(function () {
    "use strict";

    angular.module('numaHopApp.component')
        .component("numaResultCondreport", {
            require: {
                "listCtrl": "^numaResultList"
            },
            bindings: {
                "isSelected": "&numaIsSelected",
                "modeSelect": "<numaModeSelect",
                "toggleSelection": "&numaToggleSelection"
            },
            controller: function (CondreportSrvc) {
                var ctrl = this;

                ctrl.types = CondreportSrvc.types;

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
            templateUrl: "/scripts/components/components/numaResultList/numaResultCondreport.html"
        });
})();