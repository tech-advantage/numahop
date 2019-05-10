(function () {
    "use strict";

    angular.module('numaHopApp.filter')
        .filter("initial", function (StringTools, codeSrvc) {
            return function (input, defaultValue) {
                defaultValue = codeSrvc[defaultValue] || defaultValue;
                return StringTools.getFirstLetter(input, defaultValue);
            };
        });
})();