(function () {
    'use strict';

    angular.module('numaHopApp').config(function ($routeProvider, gettext, USER_ROLES) {
        $routeProvider.when('/administration/appconfiguration/viewsformat', {
            templateUrl: 'scripts/app/configuration/viewsFormat/viewsFormat.html',
            title: gettext('Images: réglages des formats'),
            reloadOnSearch: false,
            controller: 'ViewsFormatCtrl',
            access: {
                authorizedRoles: [USER_ROLES.IMG_FORMAT_HAB0],
            },
        });
    });
})();
