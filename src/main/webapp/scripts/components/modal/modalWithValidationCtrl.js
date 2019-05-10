(function() {
    'use strict';

    angular.module("numaHopApp.controller")
           .controller("ModalWithValidationCtrl", function($uibModalInstance, $q, $scope, options) {

                $scope.validationFns = [];  //  à utiliser dans les sem-field: sem-register-validation="validationFns"

                _.extend($scope, options);

                $scope.ok = function() {
                    if($scope.validationFns.length > 0) {
                        var promises = _.map($scope.validationFns, function(validationFn) {
                            return validationFn();
                        });
                        $q.all(promises).then(function(results) {
                            var validated = _.every(results, function(errmsg) { return !errmsg; });
                            if(validated) {
                                $uibModalInstance.close("ok");
                            }
                        });
                    }
                    else {
                        $uibModalInstance.close("ok");
                    }
                };

                $scope.cancel = function() {
                    $uibModalInstance.dismiss("cancel");
                };

           });
})();
