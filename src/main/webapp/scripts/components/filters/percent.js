(function () {
    "use strict";

    angular.module('numaHopApp.filter')
        .filter("percent", function ($filter) {
            return function (input, fractionSize) {
                if (angular.isUndefined(input) || input === null) {
                    return input;
                }
                if (angular.isUndefined(fractionSize)) {
                    fractionSize = 0;
                }
                var output = $filter('number')(input * 100, fractionSize);
                return output ? output + "%" : "";
            };
        });
})();