(function () {
    'use strict';

    angular.module('numaHopApp.directive').directive('editableSelect2', function (editableDirectiveFactory) {
        return editableDirectiveFactory({
            directiveName: 'editableSelect2',
            inputTpl: '<input type="hidden" ui-select2="options" />',
            render: function () {
                this.parent.render.call(this);
                this.editorEl.find('.editable-error').before('<div class="clearfix" style="height: 0px"></div>');

                // Pour fonctionner correctement, le focus doit se faire sur le champ input, et non sur l'élément input:hidden
                var editorEl = this.editorEl;
                this.inputEl[0].focus = function () {
                    $(editorEl).find('input.select2-focusser').focus();
                };
            },
        });
    });
})();
