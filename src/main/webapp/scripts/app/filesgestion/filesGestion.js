(function () {
    'use strict';

    angular.module('numaHopApp')
        .config(function ($routeProvider, gettext) {

            $routeProvider.when('/filesgestion/config', {
                templateUrl: 'scripts/app/filesgestion/filesGestionConfig.html',
                controller: 'FilesGestionConfigCtrl',
                controllerAs: 'mainCtrl',
                title: gettext("Gestion des fichiers après archivage"),
                reloadOnSearch: false
            });

        });
})();