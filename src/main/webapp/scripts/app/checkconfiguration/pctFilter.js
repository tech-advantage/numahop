(function () {
    "use strict";

    angular.module('numaHopApp.controller')
        .filter('percentage', ['$filter', function ($filter) {
            return function (input, decimals) {
                return $filter('number')(input * 100, decimals) + '%';
            };
        }]);
})();