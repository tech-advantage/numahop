(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('UserAuthorizationCtrl', function ($filter, $routeParams, $scope, UserAuthorizationSrvc, codeSrvc, gettextCatalog, HistorySrvc, uibPaginationConfig) {
        HistorySrvc.add(gettextCatalog.getString('Habilitations'));

        // initialisation
        $scope.pagination = { currentPage: 1, itemsPerPage: uibPaginationConfig.itemsPerPage, totalItems: 0 };
        $scope.filterWith = $routeParams.filter || undefined;
        $scope.orderBy = $routeParams.sort || ['module', 'code'];
        if (!angular.isArray($scope.orderBy)) {
            $scope.orderBy = [$scope.orderBy];
        }

        $scope.semCodes = codeSrvc;

        // Chargement des habilitations
        $scope.authorizations = UserAuthorizationSrvc.query({}, function (value, responseHeaders) {
            $scope.pagination.currentPage = 1;
            $scope.pagination.totalItems = value.length;

            $scope.filteredAuthorizations = $scope.getFilteredAuthorizations($scope.filterWith);
            $scope.displayedAuthorizations = $scope.getDisplayedAuthorizations($scope.filterWith, $scope.orderBy, $scope.pagination);
        });

        // Events
        $scope.pageChanged = function () {
            $scope.setCheckAll($scope.authorizations, false);

            $scope.filteredAuthorizations = $scope.getFilteredAuthorizations($scope.filterWith);
            $scope.displayedAuthorizations = $scope.getDisplayedAuthorizations($scope.filterWith, $scope.orderBy, $scope.pagination);
        };
        $scope.$watch('filterWith', function (value) {
            if ($scope.pagination.totalItems > $scope.pagination.itemsPerPage) {
                $scope.setCheckAll($scope.authorizations, false);
            }
            $scope.filteredAuthorizations = $scope.getFilteredAuthorizations($scope.filterWith);
            $scope.displayedAuthorizations = $scope.getDisplayedAuthorizations($scope.filterWith, $scope.orderBy, $scope.pagination);
        });
        $scope.$watch(
            'orderBy',
            function (value) {
                if ($scope.pagination.totalItems > $scope.pagination.itemsPerPage) {
                    $scope.setCheckAll($scope.authorizations, false);
                }
                $scope.displayedAuthorizations = $scope.getDisplayedAuthorizations($scope.filterWith, $scope.orderBy, $scope.pagination);
            },
            true
        ); // comparaison par valeur et non par référence
        $scope.getFilteredAuthorizations = function (filter) {
            return $filter('filter')($scope.authorizations, filter);
        };
        $scope.getDisplayedAuthorizations = function (filter, orderBy, pagination) {
            var displayedAuthorization = $scope.getFilteredAuthorizations(filter);
            displayedAuthorization = $filter('orderBy')(displayedAuthorization, orderBy);
            displayedAuthorization = $filter('paginate')(displayedAuthorization, pagination.currentPage, pagination.itemsPerPage);
            return displayedAuthorization;
        };
    });
})();
