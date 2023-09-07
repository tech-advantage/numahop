(function () {
    'use strict';

    angular.module('numaHopApp').config(function ($routeProvider, gettext, USER_ROLES) {
        $routeProvider.when('/multilotsdelivery/multidelivery', {
            templateUrl: 'scripts/app/multilotsdelivery/multiDeliveries.html',
            controller: 'MultiDeliveryCtrl',
            title: gettext('Livraisons multi-lots'),
            reloadOnSearch: false,
            access: {
                authorizedRoles: [USER_ROLES.DEL_HAB0],
            },
        });
    });
})();
