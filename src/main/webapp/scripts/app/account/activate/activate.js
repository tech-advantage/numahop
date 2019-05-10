(function () {
    'use strict';

    angular.module('numaHopApp')
        .config(function ($stateProvider) {
            $stateProvider
                .state('activate', {
                    parent: 'account',
                    url: '/activate?key',
                    data: {
                        roles: [],
                        pageTitle: 'Activation'
                    },
                    views: {
                        'content@': {
                            templateUrl: 'scripts/app/account/activate/activate.html',
                            controller: 'ActivationController'
                        }
                    },
                    resolve: {

                    }
                });
        });

})();