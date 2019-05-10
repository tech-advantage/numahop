(function () {
    "use strict";

    angular.module('numaHopApp.directive')
        .directive('semEditableBlockHeader', function ($parse, ErreurSrvc) {
            return {
                restrict: 'E',
                scope: {
                    error: '@semServerError',
                    label: '@semLabel',
                    ondelete: '&',
                    onadd: '&',
                    oncancel: '&',
                    onsave: '&',
                    showadd: '@',
                    hidedelete: '@',
                    readonly: '@'
                },
                templateUrl: '/scripts/components/directives/semEditableBlockHeader/template.html',
                require: "^semEditableBlock",
                compile: function compile(tElement, tAttrs, transclude) {
                    return {
                        post: function (scope, iElement, iAttrs, editableBlockCtrl) {

                            // erreurs du serveur
                            scope.$watch("error", function (value) {
                                var error = $parse(value)();
                                if (angular.isArray(error) && error.length > 0) {
                                    scope.errText = _.map(error, function (err) {
                                        if (angular.isUndefined(err.message) || err.message === null) {
                                            err.message = ErreurSrvc.getMessage(err.code);
                                        }
                                        return err.message || err.code;
                                    }).join("<br/>");
                                }
                                else {
                                    scope.errText = "";
                                }
                            });

                            // save
                            scope.save = function () {
                                editableBlockCtrl.afterSaveBlock = scope.onsave;
                                editableBlockCtrl.submitBlock();
                            };
                            // cancel
                            if (angular.isDefined(scope.oncancel)) {
                                editableBlockCtrl.afterCancelBlock = scope.oncancel;
                            }
                            scope.cancel = function () {
                                editableBlockCtrl.cancelBlock();
                            };
                            // add
                            scope.add = function () {
                                scope.onadd();
                            };
                            // delete
                            scope.delete = function () {
                                scope.ondelete();
                            };
                            // is edited
                            scope.isEdited = function () {
                                return editableBlockCtrl.isEdited();
                            };
                        }
                    };
                }
            };
        });
})();