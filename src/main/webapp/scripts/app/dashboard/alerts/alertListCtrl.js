(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('AlertListCtrl', AlertListCtrl);

    function AlertListCtrl(AuthenticationSharedService, USER_ROLES) {
        var ctrl = this;

        ctrl.isAuthorized = function (role) {
            return AuthenticationSharedService.isAuthorized(USER_ROLES[role]);
        };
    }
})();
