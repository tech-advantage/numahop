(function () {
    'use strict';

    angular.module('numaHopApp').config(function ($routeProvider, gettext, USER_ROLES) {
        $routeProvider.when('/administration/appconfiguration/imagesmetadata', {
            templateUrl: 'scripts/app/configuration/imagesmetadata/imagesmetadata.html',
            title: gettext('Métadonnées des images'),
            controller: 'ImagesMetadataCtrl',
            controllerAs: 'mainCtrl',
            access: {
                authorizedRoles: [USER_ROLES.DOC_UNIT_HAB5],
            },
        });
    });
})();
