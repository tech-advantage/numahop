(function () {
    'use strict';

    angular.module('numaHopApp.component').component('numaResultDelivery', {
        require: {
            listCtrl: '^numaResultList',
        },
        bindings: {
            modeSelect: '<numaModeSelect',
        },
        controller: function () {
            var ctrl = this;

            ctrl.$onInit = function () {
                ctrl.pagination = ctrl.listCtrl.pagination;
            };
            ctrl.clickItem = function (event) {
                if (ctrl.modeSelect) {
                    event.preventDefault();
                }
            };
        },
        templateUrl: '/scripts/components/components/numaResultList/numaResultDelivery.html',
    });
})();
