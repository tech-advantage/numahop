(function () {
    'use strict';

    angular.module('numaHopApp.filter').filter('moment', function () {
        return function (input, format) {
            if (!input) {
                return '';
            }
            format = format || 'L';
            return moment(input).format(format);
        };
    });
})();
