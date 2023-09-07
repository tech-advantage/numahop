(function () {
    'use strict';

    angular.module('numaHopApp').config(function ($routeProvider, gettext, USER_ROLES) {
        $routeProvider.when('/delivery/delivery', {
            templateUrl: 'scripts/app/delivery/deliveries.html',
            controller: 'DeliveryCtrl',
            title: gettext('Livraisons'),
            reloadOnSearch: false,
            access: {
                authorizedRoles: [USER_ROLES.DEL_HAB0],
            },
        });
    });
})();
