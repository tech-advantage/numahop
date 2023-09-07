(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('SemTableCtrl', function ($scope) {
        $scope.sortClass = {
            default: '',
            down: 'glyphicon-halflings glyphicon-triangle-top x05',
            up: 'glyphicon-halflings glyphicon-triangle-bottom x05',
        };

        $scope.init = function (sortModel) {
            $scope.internalSort = {};
            _.each(sortModel, function (sortOn) {
                var reverse = sortOn.charAt(0) === '-';
                var field = reverse ? sortOn.substring(1) : sortOn;
                $scope.internalSort[field] = reverse;
            });
        };

        // Tri
        $scope.sortOn = function (sortOn, sortModel) {
            if (angular.isUndefined($scope.internalSort[sortOn])) {
                $scope.internalSort[sortOn] = false;
            } else {
                if ($scope.internalSort[sortOn] === false) {
                    $scope.internalSort[sortOn] = true;
                } else {
                    delete $scope.internalSort[sortOn];
                }
            }
            updateSortModel(sortModel);
            $scope.customSort({ sortModel: sortModel });
        };
        $scope.getSortClass = function (sortOn) {
            var currentSort = $scope.internalSort[sortOn];
            if (angular.isDefined(currentSort)) {
                return currentSort ? $scope.sortClass.up : $scope.sortClass.down;
            }
            return $scope.sortClass.default;
        };
        $scope.getSortIndex = function (sortOn) {
            var currentSort = $scope.internalSort[sortOn];
            if (angular.isDefined(currentSort)) {
                var lookFor = currentSort ? '-' + sortOn : sortOn;
                return $scope.sortModel.indexOf(lookFor) + 1;
            }
            return -1;
        };
        $scope.isSorted = function (sortOn) {
            return angular.isDefined($scope.internalSort[sortOn]);
        };

        function updateSortModel(sortModel) {
            sortModel.length = 0;
            _.each(_.pairs($scope.internalSort), function (value) {
                sortModel.push(value[1] ? '-' + value[0] : value[0]);
            });
        }
    });
})();
