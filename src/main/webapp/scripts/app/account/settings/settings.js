(function () {
    'use strict';

    angular.module('numaHopApp').config(function ($stateProvider) {
        $stateProvider.state('settings', {
            parent: 'account',
            url: '/settings',
            data: {
                roles: ['ROLE_USER'],
                pageTitle: 'Settings',
            },
            views: {
                'content@': {
                    templateUrl: 'scripts/app/account/settings/settings.html',
                    controller: 'SettingsController',
                },
            },
            resolve: {},
        });
    });
})();
