(function () {
    'use strict';

    angular.module('numaHopApp').factory('AuditsService', function ($http) {
        return {
            findAll: function () {
                return $http.get('api_int/audits/all').then(function (response) {
                    return response.data;
                });
            },
            findByDates: function (fromDate, toDate) {
                var formatDate = function (dateToFormat) {
                    if (angular.isDefined(dateToFormat) && !angular.isString(dateToFormat)) {
                        return dateToFormat.getYear() + '-' + dateToFormat.getMonth() + '-' + dateToFormat.getDay();
                    }
                    return dateToFormat;
                };

                return $http
                    .get('api_int/audits/byDates', {
                        params: {
                            fromDate: formatDate(fromDate),
                            toDate: formatDate(toDate),
                        },
                    })
                    .then(function (response) {
                        return response.data;
                    });
            },
        };
    });
})();
