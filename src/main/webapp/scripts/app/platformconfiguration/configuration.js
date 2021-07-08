(function () {
    'use strict';

    angular.module('numaHopApp')
        .config(function ($routeProvider, gettext, USER_ROLES) {

            $routeProvider.when('/platformconfiguration/ftpconfiguration', {
                templateUrl: 'scripts/app/platformconfiguration/ftpconfigurations.html',
                controller: 'FTPConfigurationCtrl',
                title: gettext("Configurations FTP"),
                reloadOnSearch: false,
                access: {
                    authorizedRoles: [USER_ROLES.FTP_HAB0]
                }
            });
            
            $routeProvider.when('/platformconfiguration/exportftpconfiguration', {
                templateUrl: 'scripts/app/platformconfiguration/exportftpconfiguration.html',
                controller: 'ExportFTPConfigurationCtrl',
                title: gettext("Configurations d'export FTP"),
                reloadOnSearch: false,
                access: {
                    authorizedRoles: [USER_ROLES.EXP_FTP_HAB0]
                }
            });

            $routeProvider.when('/platformconfiguration/sftpconfiguration', {
                templateUrl: 'scripts/app/platformconfiguration/sftpconfigurations.html',
                controller: 'SFTPConfigurationCtrl',
                title: gettext("Configurations CINES"),
                reloadOnSearch: false,
                access: {
                    authorizedRoles: [USER_ROLES.SFTP_HAB0]
                }
            });

            $routeProvider.when('/platformconfiguration/iaconfiguration', {
                templateUrl: 'scripts/app/platformconfiguration/iaconfigurations.html',
                controller: 'IAConfigurationCtrl',
                title: gettext("Configurations Internet Archive"),
                reloadOnSearch: false,
                access: {
                    authorizedRoles: [USER_ROLES.CONF_INTERNET_ARCHIVE_HAB0]
                }
            });
            
            $routeProvider.when('/platformconfiguration/omekaconfiguration', {
                templateUrl: 'scripts/app/platformconfiguration/omekaconfiguration.html',
                controller: 'OmekaConfigurationCtrl',
                title: gettext("Configurations OMEKA"),
                reloadOnSearch: false,
                access: {
                    authorizedRoles: [USER_ROLES.CONF_DIFFUSION_OMEKA_HAB0]
                }
            });

            $routeProvider.when('/platformconfiguration/digitallibraryconfiguration', {
                templateUrl: 'scripts/app/platformconfiguration/digitallibraryconfiguration/digitalLibraryConfiguration.html',
                controller: 'DigitalLibraryConfigurationCtrl',
                title: gettext("Configurations diffusion sur une biblothèque numérique"),
                reloadOnSearch: false,
                access: {
                    authorizedRoles: [USER_ROLES.CONF_DIFFUSION_DIGITAL_LIBRARY_HAB0]
                }
            });
            
        });
})();