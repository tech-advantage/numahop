(function () {
    "use strict";

angular.module('numaHopApp.filter')
    .filter("boolean", function (gettextCatalog) {
        return function (input, strict) {
            if (strict) {
                return (input === true ? gettextCatalog.getString("Oui") : gettextCatalog.getString("Non"));
            }
            else {
                return (input ? gettextCatalog.getString("Oui") : gettextCatalog.getString("Non"));
            }
        };
    });
})();