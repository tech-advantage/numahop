(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('ModalCtrl', function ($scope, $uibModalInstance, options) {
        _.extend($scope, options);

        $scope.ok = ok;
        $scope.cancel = cancel;

        function ok(value) {
            $uibModalInstance.close(value);
        }

        function cancel() {
            $uibModalInstance.dismiss('cancel');
        }
    });
})();
