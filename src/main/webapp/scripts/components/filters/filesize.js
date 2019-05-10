(function () {
    "use strict";

angular.module('numaHopApp.filter')
    .filter("filesize", function ($filter) {
        return function (input) {
            var nb = Number(input);
            if (!angular.isNumber(nb)) {
                return "";
            }
            if (nb === 0) {
                return " - ";
            }
            else if (nb < 1024) {
                return $filter("number")(nb) + " o";
            }
            else if (nb < 1024 * 1024) {
                return $filter("number")(nb / 1024, 2) + " ko";
            }
            else if (nb < 1024 * 1024 * 1024) {
                return $filter("number")(nb / 1024 / 1024, 2) + " Mo";
            }
            else {
                return $filter("number")(nb / 1024 / 1024 / 1024, 2) + " Go";
            }
        };
    });
})();