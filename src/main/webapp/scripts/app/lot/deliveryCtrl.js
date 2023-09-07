(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('LotDeliveryCtrl', LotDeliveryCtrl);

    function LotDeliveryCtrl(codeSrvc, DeliverySrvc) {
        var dCtrl = this;
        dCtrl.init = loadDeliveries;
        dCtrl.semCodes = codeSrvc;

        /**
         * Chargement de la liste des livraisons appartenant à ce lot
         *
         * @param {any} lotId
         */
        function loadDeliveries(lotId) {
            if (angular.isDefined(lotId)) {
                dCtrl.deliveries = DeliverySrvc.query({ lot: lotId });
            }
        }
    }
})();
