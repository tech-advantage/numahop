(function () {
    "use strict";

    angular.module('numaHopApp')
        .config(function ($routeProvider, gettext, USER_ROLES) {

            $routeProvider.when('/ocrlangconfiguration/ocrlangconfiguration', {
                templateUrl: 'scripts/app/ocrlangconfiguration/configurations.html',
                controller: 'OcrLangConfigurationCtrl',
                title: gettext("Configurations des langages d'OCR"),
                reloadOnSearch: false,
                access: {
                    authorizedRoles: [USER_ROLES.OCR_LANG_HAB0]
                }
            });
        });
})();