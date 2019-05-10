(function () {
    'use strict';

    angular.module('numaHopApp.service').factory('DashboardSrvc', function ($http) {

        /**
         * Le service
         * @type {Object}
         */
        var service = {
            setDashboard: setDashboard,
            getDashboard: getDashboard,
            saveDashboard: saveDashboard
        };

        /**
         * Le tableau de bord
         * @type {Object}
         */
        var dashboard = {};

        /**
         * Mise à jour du tableau de bord
         * @param {Object} newDash le nouveau tableau de bord
         */
        function setDashboard(newDash) {
            if (angular.isDefined(newDash) && newDash !== null) {
                if (angular.isString(newDash)) {
                    dashboard = angular.fromJson(newDash);
                    dashboard = JSOG.decode(dashboard);
                } else {
                    dashboard = newDash;
                }
            } else {
                dashboard = {};
            }
        }

        /**
         * Obtention du tableau de bord
         * @return {Object} le tableau de bord
         */
        function getDashboard() {
            return dashboard;
        }

        /**
         * Enregistrement d'un tableau de bord
         * @param  {Object} newDash le nouveau tableau de bord
         */
        function saveDashboard(newDash) {
            $http.post('/api/rest/user/dashboard', dashboard).success(function () {
                service.setDashboard(newDash);
            });
        }

        return service;
    });
})();
