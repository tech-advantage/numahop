(function () {
    "use strict";

    angular.module('numaHopApp')
        .controller('NavbarController', function ($scope, $state, Auth, Principal) {
            $scope.isAuthenticated = Principal.isAuthenticated;
            $scope.$state = $state;

            $scope.logout = function () {
                Auth.logout();
                $state.go('home');
            };
        });
})();