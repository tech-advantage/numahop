(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('ModalCheckTextOcrCtrl', ModalCheckTextOcrCtrl);

    function ModalCheckTextOcrCtrl($scope, $uibModalInstance, gettextCatalog, options) {
        var mainCtrl = this;

        mainCtrl.close = close;
        mainCtrl.page = options.page;
        mainCtrl.value = options.txtValue;

        function close() {
            $uibModalInstance.dismiss();
        }
    }
})();
