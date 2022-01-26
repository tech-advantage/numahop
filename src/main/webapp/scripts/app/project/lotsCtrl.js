(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('ProjectLotsCtrl', ProjectLotsCtrl);

    function ProjectLotsCtrl(LotSrvc, WorkflowLotHandleSrvc, gettextCatalog, MessageSrvc, ModalSrvc) {

        var lotCtrl = this;
        lotCtrl.init = loadLots;
        lotCtrl.validate = validateLot;
        lotCtrl.isLotLocked = WorkflowLotHandleSrvc.isLotLocked;


        function loadLots(projectId) {
            lotCtrl.projectId = projectId;
            lotCtrl.lots = LotSrvc.findSimpleByProject({ project: projectId });
        }

        function validateLot(lot) {
            ModalSrvc.confirmAction(gettextCatalog.getString("valider le lot suivant :  {{label}}", { label: lot.label }))
                .then(function () {
                    LotSrvc.validate(lot).$promise.then(function () {
                        MessageSrvc.addSuccess(gettextCatalog.getString("Le lot {{label}} a été validé", { label: lot.label }));
                        lot.status = 'ONGOING';
                    });
                });
        }
    }
})();