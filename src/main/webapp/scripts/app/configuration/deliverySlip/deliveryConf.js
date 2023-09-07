(function () {
    'use strict';

    angular.module('numaHopApp').config(function ($routeProvider, gettext, USER_ROLES) {
        $routeProvider.when('/administration/appconfiguration/deliveryconf', {
            templateUrl: 'scripts/app/configuration/deliverySlip/deliveryConf.html',
            title: gettext('Bordereau de livraison : paramètres'),
            reloadOnSearch: false,
            controller: 'DeliveryConfCtrl',
            controllerAs: 'mainCtrl',
            access: {
                authorizedRoles: [USER_ROLES.DEL_HAB4],
            },
        });
    });
})();
