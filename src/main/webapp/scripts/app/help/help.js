(function () {
    'use strict';

    angular.module('numaHopApp').config(function ($routeProvider, gettext, USER_ROLES) {
        $routeProvider.when('/help', {
            templateUrl: 'scripts/app/help/helpPages.html',
            controller: 'HelpPageCtrl',
            title: gettext('Aide'),
            reloadOnSearch: false,
            access: {
                authorizedRoles: [USER_ROLES.all],
            },
        });
    });
})();
