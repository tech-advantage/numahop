(function () {
    'use strict';

    angular.module('numaHopApp').config(function ($stateProvider) {
        $stateProvider.state('admin', {
            abstract: true,
            parent: 'site',
        });
    });
})();
