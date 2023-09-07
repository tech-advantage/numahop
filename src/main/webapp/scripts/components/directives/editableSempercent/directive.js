(function () {
    'use strict';

    angular.module('numaHopApp.directive').directive('editableSempercent', function (editableDirectiveFactory) {
        return editableDirectiveFactory({
            directiveName: 'editableSempercent',
            inputTpl: '<input type="sempercent"></input>',
            render: function () {
                this.parent.render.call(this);
                this.inputEl.addClass('form-control');
            },
        });
    });
})();
