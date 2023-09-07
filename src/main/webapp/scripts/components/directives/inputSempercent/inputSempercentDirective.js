(function () {
    'use strict';

    angular.module('numaHopApp.directive').directive('input', sempercent);

    function sempercent(gettextCatalog) {
        return {
            restrict: 'E',
            require: '?ngModel',
            link: function (scope, element, attr, ngModel) {
                if (attr.type !== 'sempercent') {
                    return;
                }

                element.tooltip({
                    placement: 'bottom',
                    title: gettextCatalog.getString('Veuillez saisir un pourcentage'),
                    trigger: 'manual',
                });

                // view -> model
                ngModel.$parsers.unshift(function (viewValue) {
                    var isValid = false;
                    var perc;

                    if (viewValue !== null) {
                        viewValue = viewValue.replace(/,/g, '.').replace(/\s+/g, '');
                        perc = parseFloat(viewValue);
                        isValid = isFinite(perc);
                    }
                    if (!isValid) {
                        ngModel.$setValidity('sempercent', false);
                        return;
                    } else {
                        ngModel.$setValidity('sempercent', true);
                        var value = Math.round(perc * 100) / 10000; // /100, 4 décimales
                        return value;
                    }
                });
                // model -> view
                ngModel.$formatters.unshift(function (modelValue) {
                    if (modelValue === null || !isFinite(modelValue)) {
                        return '';
                    }
                    var display = Math.round(modelValue * 10000) / 100; // x100, 2 décimales
                    return String(display);
                });

                // Override the input event and add custom 'path' logic
                element.off('input');
                element.on('input', function () {
                    ngModel.$setViewValue(this.value);
                    toggleTooltip(ngModel.$modelValue);
                });
                element.on('blur', function () {
                    element.tooltip('hide');
                });

                function toggleTooltip(value) {
                    if (angular.isUndefined(value)) {
                        element.tooltip('show');
                    } else {
                        element.tooltip('hide');
                    }
                }
            },
        };
    }
})();
