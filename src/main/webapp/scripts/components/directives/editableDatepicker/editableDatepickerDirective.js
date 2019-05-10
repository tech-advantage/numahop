(function () {
    "use strict";

    angular.module('numaHopApp.directive')
        .directive('editableDatepicker',
            function (editableDirectiveFactory) {
                return editableDirectiveFactory({
                    directiveName: 'editableDatepicker',
                    inputTpl: '<sem-datepicker></sem-datepicker>',
                    render: function () {
                        this.parent.render.call(this);

                        // Pour fonctionner correctement, le focus doit se faire sur le champ input, et non sur l'élément sem-datepicker
                        var inputEl0 = this.inputEl[0];
                        this.inputEl[0].focus = function () {
                            inputEl0.getElementsByTagName("input")[0].focus();
                        };
                    }
                });
            });
})();