(function () {
    'use strict';

    angular.module('numaHopApp.directive').directive('input', semnumber);

    function semnumber(gettextCatalog, $filter) {
        return {
            restrict: 'E',
            require: '?ngModel',
            link: function (scope, element, attr, ngModel) {
                if (attr.type !== 'semnumber') {
                    return;
                }

                scope.precision = attr.semPrecision;

                element.tooltip({
                    placement: 'bottom',
                    title: gettextCatalog.getString('Veuillez saisir un nombre'),
                    trigger: 'manual',
                });

                // view -> model
                ngModel.$parsers.unshift(function (viewValue) {
                    var isValid = false;
                    var parsedValue;

                    if (viewValue !== null) {
                        viewValue = viewValue.replace(/,/g, '.').replace(/\s+/g, '');
                        parsedValue = parseFloat(viewValue);
                        isValid = isFinite(parsedValue);
                    }
                    if (!isValid) {
                        ngModel.$setValidity('semnumber', false);
                        return;
                    } else {
                        ngModel.$setValidity('semnumber', true);
                        var value = Math.round(parsedValue * 100) / 100; // 2 décimales
                        return value;
                    }
                });
                // model -> view
                ngModel.$formatters.unshift(function (modelValue) {
                    if (modelValue === null || !isFinite(modelValue)) {
                        return '';
                    }
                    var precision = scope.precision || 2;
                    var display = Math.round(modelValue * Math.pow(10, precision)) / Math.pow(10, precision); // 2 décimales
                    if (scope.precision) {
                        display = $filter('number')(display, precision);
                    }
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
