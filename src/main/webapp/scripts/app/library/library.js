(function () {
    'use strict';

    angular.module('numaHopApp').config(function ($routeProvider, gettext, USER_ROLES) {
        $routeProvider.when('/library/library', {
            templateUrl: 'scripts/app/library/libraries.html',
            controller: 'LibraryCtrl',
            title: gettext('Bibliothèques'),
            reloadOnSearch: false,
            access: {
                authorizedRoles: [USER_ROLES.LIB_HAB5],
            },
        });
    });
})();
