(function () {
    'use strict';

    angular.module('numaHopApp.component').component('numaResultFacet', {
        bindings: {
            aggs: '<',
            filters: '<',
            onChange: '&',
        },
        templateUrl: '/scripts/components/components/numaResultFacet/numaResultFacet.html',
        controller: 'NumaResultFacetCtrl',
    });
})();
