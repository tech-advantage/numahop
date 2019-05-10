(function () {
    "use strict";

    angular.module('numaHopApp.directive')
        .directive('semEditableBlock', function ($compile) {
            return {
                restrict: 'EA',
                scope: false,
                controller: "SemEditableBlockCtrl",
                require: "?^semEditableForm",
                terminal: true,
                compile: function compile(tElement, tAttrs, transclude) {
                    return {
                        post: function (scope, iElement, iAttrs, editableFormCtrl) {
                            var hasParentForm = editableFormCtrl !== null;
                            scope.isModeEdition = hasParentForm && !editableFormCtrl.isModeEdition();
                            scope.editableFormCtrl = editableFormCtrl || {};

                            // Enregistrement du bloc
                            if (hasParentForm) {
                                editableFormCtrl.registerBlock(iElement);
                            }

                            // Création du formulaire x-éditable
                            if (scope.isModeEdition) {
                                scope.formName = iAttrs.semFormName;

                                var form = angular.element("<form editable-form>").attr("name", scope.formName).attr("onaftersave", "onaftersave()");
                                if (iAttrs.onshow) {
                                    form.attr("onshow", iAttrs.onshow);
                                }
                                iElement.append(form);
                            }
                            // ou utilisation du formulaire global
                            else if (hasParentForm) {
                                scope.formName = editableFormCtrl.getFormName();
                            }

                            // Champs du block x-editable
                            if (angular.isDefined(scope.formName) && scope.formName !== "") {
                                var field = iElement.find("sem-editable-field")
                                    .attr("sem-form", scope.formName);
                            }

                            // la directive est "terminale" => $compile manuellement
                            $compile(iElement.children())(scope);
                        }
                    };
                }
            };
        });
})();