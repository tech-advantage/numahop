(function () {
    "use strict";

    angular.module('numaHopApp.directive')
        .directive('editableSemradiolist',
            function (editableDirectiveFactory, editableNgOptionsParser) {
                return editableDirectiveFactory({
                    directiveName: 'editableSemradiolist',
                    inputTpl: '<span></span>',
                    render: function () {
                        this.parent.render.call(this);
                        var parsed = editableNgOptionsParser(this.attrs.eNgOptions);
                        var ngChange = this.inputEl.attr("ng-change");
                        var ngReadonly = this.inputEl.attr("ng-readonly");

                        var inputId = this.inputEl.attr("id") || "";
                        if (inputId) {
                            inputId += "-";
                        }
                        var radioId = "radio-" + inputId + "{{" + parsed.locals.valueFn + "}}";

                        var html = '<div class="radio radio-sid-main radio-inline" ng-repeat="' + parsed.ngRepeat + '">' +
                            '<input type="radio" id="' + radioId + '" ' +
                            'ng-model="$parent.$parent.$data" ng-value="{{' + parsed.locals.valueFn + '}}" ';

                        // ng-change sur l'input, et non sur le span
                        if (angular.isDefined(ngChange)) {
                            html += 'ng-change="' + ngChange + '" ';
                        }
                        if (ngReadonly) {
                            html += 'ng-disabled="{{' + ngReadonly + '}}" ';
                        }
                        html += '/>' +
                            '<label for="' + radioId + '" ng-bind="' + parsed.locals.displayFn + '"></label>' +
                            '</div>';

                        this.inputEl.removeAttr('ng-model');
                        this.inputEl.removeAttr('ng-options');
                        this.inputEl.removeAttr('ng-change');
                        this.inputEl.html(html);
                        this.editorEl.addClass("editable-radiolist")
                            .removeClass("editable-semradiolist");
                    }
                });
            });
})();