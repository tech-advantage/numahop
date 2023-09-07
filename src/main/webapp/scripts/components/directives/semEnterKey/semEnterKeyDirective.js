(function () {
    'use strict';

    angular.module('numaHopApp.directive').directive('semEnterKey', function () {
        return {
            restrict: 'A',
            link: function ($scope, element, attrs) {
                element.bind('keydown keypress', function (event) {
                    var keyCode = event.which || event.keyCode;
                    // 13 : Enter key
                    if (keyCode === 13) {
                        $scope.$apply(function () {
                            $scope.$eval(attrs.semEnterKey);
                        });
                        event.preventDefault();
                    }
                });
            },
        };
    });
})();
