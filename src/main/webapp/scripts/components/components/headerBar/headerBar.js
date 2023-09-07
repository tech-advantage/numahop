(function () {
    'use strict';

    angular.module('numaHopApp.component').component('headerBar', {
        bindings: {
            delivery: '<',
            docUnit: '<',
            library: '<',
            lot: '<',
            project: '<',
            train: '<',
        },
        controller: 'HeaderBarController',
        templateUrl: '/scripts/components/components/headerBar/headerBar.html',
    });
})();
