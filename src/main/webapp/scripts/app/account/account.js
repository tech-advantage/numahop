(function () {
    'use strict';

    angular.module('numaHopApp').config(function ($stateProvider) {
        $stateProvider.state('account', {
            abstract: true,
            parent: 'site',
        });
    });
})();
