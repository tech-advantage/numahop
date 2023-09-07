(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('FilesGestionConfigEditCtrl', FilesGestionConfigEditCtrl);

    function FilesGestionConfigEditCtrl($scope, $timeout, gettext, gettextCatalog, FilesGestionConfigSrvc, MessageSrvc, NumaHopInitializationSrvc) {
        $scope.toggleChoice = toggleChoice;
        $scope.saveConfiguration = saveConfiguration;
        // toggle switch labels
        $scope.onLabelActiv = gettextCatalog.getString('Oui');
        $scope.offLabelActiv = gettextCatalog.getString('Non');
        $scope.displayTypeDeclench = displayTypeDeclench;
        $scope.confExportFtpChanged = confExportFtpChanged;
        $scope.isDeliveryFolderDisplayed = isDeliveryFolderDisplayed;
        $scope.conf = {};

        function displayTypeDeclench(typeDeclench) {
            return $scope.options.typeDeclench[typeDeclench] || typeDeclench;
        }

        init();

        /**
         * Initialisation du contrôleur
         */
        function init() {
            // charge la config si 1 seule bib, sinon attendre choix....
            if (angular.isDefined($scope.selectedLibrary)) {
                loadConf($scope.selectedLibrary);
                loadExportFTPConf($scope.selectedLibrary);
            }
        }

        /**
         * Chargement des types de propriété
         */
        function loadConf(library) {
            if (angular.isDefined(library)) {
                // Chargement configuration
                $scope.configuration = FilesGestionConfigSrvc.get(
                    {
                        id: library.identifier,
                        library: true,
                    },
                    function (config) {
                        if (!config.identifier) {
                            $scope.conf = getDefaultConfig();
                        } else {
                            $scope.conf = config;
                        }
                        $scope.loaded = true;
                        setSelectedDeleveryFolder();
                    }
                );
            }
        }

        function saveConfiguration() {
            $timeout(function () {
                MessageSrvc.clearMessages();
                FilesGestionConfigSrvc.saveOrUpdate({}, $scope.conf, function (value) {
                    if (value.identifier) {
                        MessageSrvc.addSuccess(gettext('La configuration a été sauvegardée'));
                    }
                });
            });
        }

        $scope.changeTypeDeclench = function (val) {
            $scope.conf.triggerType = val;
        };

        function getDefaultConfig() {
            var config = {
                triggerType: '',
                delay: 0,
                deleteMaster: false,
                deletePdf: false,
                deletePrint: false,
                deleteView: false,
                deleteThumb: false,
                saveMaster: false,
                savePdf: false,
                savePrint: false,
                saveView: false,
                saveThumb: false,
                saveAipSip: false,
                useExportFtp: false,
                destinationDir: '',
                library: $scope.selectedLibrary,
            };

            return config;
        }

        /**
         * Reglage des switchs d'action.
         */
        function toggleChoice(val, field) {
            switch (field) {
                case 'delMaster':
                    $scope.conf.deleteMaster = val.deleteMaster;
                    break;
                case 'delPdf':
                    $scope.conf.deletePdf = val.deletePdf;
                    break;
                case 'delPrint':
                    $scope.conf.deletePrint = val.deletePrint;
                    break;
                case 'delView':
                    $scope.conf.deleteView = val.deleteView;
                    break;
                case 'delThumb':
                    $scope.conf.deleteThumb = val.deleteThumb;
                    break;
                case 'savMaster':
                    $scope.conf.saveMaster = val.saveMaster;
                    break;
                case 'savPdf':
                    $scope.conf.savePdf = val.savePdf;
                    break;
                case 'savPrint':
                    $scope.conf.savePrint = val.savePrint;
                    break;
                case 'savView':
                    $scope.conf.saveView = val.saveView;
                    break;
                case 'savThumb':
                    $scope.conf.saveThumb = val.saveThumb;
                    break;
                case 'savAipSip':
                    $scope.conf.saveAipSip = val.saveAipSip;
                    break;
                case 'useExportFtp':
                    $scope.conf.useExportFtp = val.useExportFtp;
                    break;
                default:
                // nothing
            }
        }

        function loadExportFTPConf(library) {
            if (!library) {
                $scope.options.exportftp = [];
                return;
            }

            NumaHopInitializationSrvc.loadExportFtpConf(library.identifier).then(function (data) {
                $scope.options.exportftp = data;
            });
        }

        function confExportFtpChanged(value) {
            if (value.label != null && value.label != '') {
                $scope.displayDeliveriesFolder = true;
                $scope.options.exportftp.forEach(function (conf) {
                    if (conf.identifier === value.identifier) {
                        $scope.conf.activeExportFTPConfiguration = conf;
                        $scope.conf.activeExportFTPConfiguration.deliveryFolders = conf.deliveryFolders;
                    }
                });
            } else {
                $scope.displayDeliveriesFolder = false;
            }
        }

        function isDeliveryFolderDisplayed() {
            return $scope.displayDeliveriesFolder;
        }

        function setSelectedDeleveryFolder() {
            var exportConfig = $scope.conf;
            $scope.displayDeliveriesFolder = exportConfig != null && $scope.conf.activeExportFTPDeliveryFolder != null;
        }
    }
})();
