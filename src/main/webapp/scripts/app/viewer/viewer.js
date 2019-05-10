(function () {
    'use strict';

    angular.module('numaHopApp')
        .config(function ($routeProvider, gettext) {

            $routeProvider.when('/viewer/viewer', {
                templateUrl: 'scripts/app/viewer/viewer.html',
                controller: 'ViewerCtrl',
                title: gettext("Visionneuse"),
                reloadOnSearch: false
            });
        });
})();