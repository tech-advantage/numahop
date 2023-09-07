(function () {
    'use strict';

    angular.module('numaHopApp').config(function ($routeProvider, gettext) {
        $routeProvider.when('/administration/appconfiguration', {
            templateUrl: 'scripts/app/configuration/appconfiguration.html',
            title: gettext("Configuration de l'application"),
        });
    });
})();
