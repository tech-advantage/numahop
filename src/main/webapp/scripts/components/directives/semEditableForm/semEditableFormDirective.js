(function () {
    "use strict";

    angular.module('numaHopApp.directive')
        .directive('semEditableForm', function () {
            return {
                restrict: 'E',
                scope: {
                    isModeEdition: '@semModeEdition',
                    error: '@semServerError',
                    name: '@semName',
                    onkeypressed: '&semOnKeyPressed'
                },
                controller: "SemEditableFormCtrl",
                compile: function compile(tElement, tAttrs, transclude) {
                    // les champs qui ne sont pas dans des blocs appartiennent au formulaire
                    tElement.find("sem-editable-field:not(sem-editable-block sem-editable-field)")
                        .attr("sem-form", tAttrs.semName);

                    // Affichage des erreurs
                    var errorElt = angular.element("<blockquote ng-if=\"errText\" class=\"col-sm-offset-3 col-sm-9 alert-danger\">" +
                        "<small class=\"row has-error\">" +
                        "<div class=\"col-sm-12 help-block\" ng-bind-html=\"errText\"></div>" +
                        "</small><" +
                        "/blockquote>");
                    tElement.prepend(errorElt);

                    // Lecture seule
                    if (angular.isDefined(tAttrs.ngReadonly)) {
                        tElement.find("sem-editable-field:not(sem-editable-field[ng-readonly])").attr("ng-readonly", tAttrs.ngReadonly);
                        tElement.find("sem-editable-block-header:not(sem-editable-block-header[ng-readonly])").attr("ng-readonly", tAttrs.ngReadonly);

                        tElement.find("sem-editable-field[ng-readonly], sem-editable-block-header[ng-readonly]")
                            .each(function (index, element) {
                                var $this = $(this);
                                var attrRo = "(" + $this.attr("ng-readonly") + ") || " + tAttrs.ngReadonly;
                                $this.attr("ng-readonly", attrRo);
                            });
                    }
                }
            };
        });
})();