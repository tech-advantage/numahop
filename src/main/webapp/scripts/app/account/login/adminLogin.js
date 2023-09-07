(function () {
    'use strict';

    angular.module('numaHopApp').config(function ($stateProvider) {
        $stateProvider.state('login', {
            parent: 'account',
            url: '/admin_login',
            data: {
                roles: [],
                pageTitle: 'Authentication',
            },
            views: {
                'content@': {
                    templateUrl: 'scripts/app/account/login/Adminlogin.html',
                    controller: 'AdminLoginController',
                },
            },
            resolve: {},
        });
    });
})();
