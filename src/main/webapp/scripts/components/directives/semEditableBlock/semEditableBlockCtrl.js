(function () {
    "use strict";

    angular.module('numaHopApp.controller')
        .controller('SemEditableBlockCtrl', function ($scope) {

            var fields = [];

            this.registerField = function (field) {
                if (fields.indexOf(field.get(0)) >= 0) {
                    return;
                }
                fields.push(field.get(0));

                var self = this;
                var aField = angular.element(field);

                aField.on("$destroy", function () {
                    self.unregisterField(aField);
                });
            };
            this.unregisterField = function (field) {
                var idx = fields.indexOf(field.get(0));
                if (idx < 0) {
                    return;
                }
                fields.splice(idx, 1);
            };
            this.submitBlock = function () {
                var formEditable = $scope[$scope.formName];
                if (angular.isDefined(formEditable) && formEditable.$visible === true) {
                    formEditable.$submit();

                    if (angular.isFunction(this.afterSaveBlock)) {
                        this.afterSaveBlock();
                    }
                }
            };
            this.cancelBlock = function () {
                var formEditable = $scope[$scope.formName];
                if (angular.isDefined(formEditable) && formEditable.$visible === true) {
                    formEditable.$cancel();

                    if (angular.isFunction(this.afterCancelBlock)) {
                        this.afterCancelBlock();
                    }
                }
            };
            this.openBlock = function () {
                var formEditable = $scope[$scope.formName];
                if (angular.isDefined(formEditable) && formEditable.$visible === false) {
                    formEditable.$show();
                }
            };
            this.isEdited = function () {
                if (!$scope.isModeEdition) {
                    return false;
                }
                var formEditable = $scope[$scope.formName];
                return angular.isDefined(formEditable) && formEditable.$visible === true;
            };
        });
})();