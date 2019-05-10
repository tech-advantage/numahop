(function () {
    'use strict';

    angular.module('numaHopApp')
        .config(function ($stateProvider) {
            $stateProvider
                .state('sessions', {
                    parent: 'account',
                    url: '/sessions',
                    data: {
                        roles: ['ROLE_USER'],
                        pageTitle: 'Sessions'
                    },
                    views: {
                        'content@': {
                            templateUrl: 'scripts/app/account/sessions/sessions.html',
                            controller: 'SessionsController'
                        }
                    },
                    resolve: {

                    }
                });
        });
})();