(function () {
    "use strict";

    angular.module('numaHopApp.controller')
        .controller('SemEditableFormCtrl', function ($scope, $element, $compile, $parse, $q, ErreurSrvc, ModalSrvc) {

            var blocks = [];

            // erreurs du serveur
            $scope.$watch("error", function (value) {
                var error = $parse(value)();

                if (angular.isArray(error) && error.length > 0) {
                    $scope.$parent.errText = _.map(error, function (err) {
                        if (angular.isUndefined(err.message) || err.message === null) {
                            err.message = ErreurSrvc.getMessage(err.code);
                        }
                        return err.message || err.code;
                    }).join("<br/>");
                }
                else {
                    $scope.$parent.errText = "";
                }
            });

            // Création du formulaire x-éditable
            if ($scope.isModeEdition === "true") {
                var form = angular.element("<form editable-form>").attr("name", $scope.name).attr("onaftersave", "onaftersave()");
                $element.wrap(form);
                $compile(form)($scope.$parent);
            }

            this.registerBlock = function (block) {
                if (blocks.indexOf(block.get(0)) >= 0) {
                    return;
                }
                blocks.push(block.get(0));

                var self = this;
                var aBlock = angular.element(block);

                aBlock.on("$destroy", function () {
                    self.unregisterBlock(aBlock);
                });
            };
            this.unregisterBlock = function (block) {
                var idx = blocks.indexOf(block.get(0));
                if (idx < 0) {
                    return;
                }
                blocks.splice(idx, 1);
            };
            this.cancelAll = function () {
                var promise;

                // Affichage de la popup de confirmation si nécessaire
                for (var i = 0; i < blocks.length; i++) {
                    var block = angular.element(blocks[i]);

                    if (block.find("sem-editable-field [ng-model].ng-dirty").length > 0) {
                        promise = ModalSrvc.confirmCancel();
                        break;
                    }
                }
                // init promise
                if (angular.isUndefined(promise)) {
                    promise = $q.when();
                }
                // cancel blocks
                promise.then(function () {
                    _.each(blocks, function (block) {
                        angular.element(block).controller("semEditableBlock").cancelBlock();
                    });
                });
                return promise;
            };
            this.getFormName = function () {
                return $scope.name;
            };
            this.isModeEdition = function () {
                return $scope.isModeEdition === "true";
            };
            this.onSubmit = function (event) {
                return $scope.onkeypressed({ event: event });
            };
        });
})();