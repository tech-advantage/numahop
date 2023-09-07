(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('ModalConfirmCheckTerminatedCtrl', function ($uibModalInstance, gettextCatalog, options) {
        var mainCtrl = this;
        mainCtrl.ok = ok;
        mainCtrl.cancel = cancel;
        mainCtrl.nbMinErr = options.nbMinErr;
        mainCtrl.nbMajErr = options.nbMajErr;
        mainCtrl.minErrRateExceeded = options.minErrRateExceeded;
        mainCtrl.majErrRateExceeded = options.majErrRateExceeded;

        function ok() {
            $uibModalInstance.close('ok');
        }

        function cancel() {
            $uibModalInstance.dismiss('cancel');
        }
    });
})();
