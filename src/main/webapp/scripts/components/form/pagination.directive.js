/* globals $ */
(function () {
    "use strict";

    angular.module('numaHopApp')
        .directive('sidServerAppPagination', function () {
            return {
                templateUrl: 'scripts/components/form/pagination.html'
            };
        });
})();