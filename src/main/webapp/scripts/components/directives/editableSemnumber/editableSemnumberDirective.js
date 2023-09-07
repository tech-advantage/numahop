(function () {
    'use strict';

    angular.module('numaHopApp.directive').directive('editableSemnumber', function (editableDirectiveFactory) {
        return editableDirectiveFactory({
            directiveName: 'editableSemnumber',
            inputTpl: '<input type="semnumber"></input>',
            render: function () {
                this.parent.render.call(this);
                this.inputEl.addClass('form-control');
            },
        });
    });
})();
