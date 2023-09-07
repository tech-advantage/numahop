(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('InitialFilterCtrl', function ($scope, gettext) {
        $scope.filterInitial = filterInitial;
        $scope.unfilterInitial = unfilterInitial;

        init();

        function init() {
            $scope.letters = gettext('ABCDEFGHIJKLMNOPQRSTUVWXYZ').split('');

            if (angular.isUndefined($scope.filters)) {
                $scope.filters = {};
            }
        }

        function filterInitial(initiale) {
            $scope.filters.initiale = initiale;
            $scope.doFilter(); // doFilter défini dans le scope parent
        }

        function unfilterInitial() {
            delete $scope.filters.initiale;
            $scope.doFilter(); // doFilter défini dans le scope parent
        }
    });
})();
