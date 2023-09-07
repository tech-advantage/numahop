(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('ExportAllOperationsCtrl', function ($filter, gettextCatalog, codeSrvc, MessageSrvc, ExportSrvc, FileSaver) {
        var self = this;
        self.code = codeSrvc;
        self.accCines = false;
        self.accIA = false;
        self.accCinesCertificate = false;
        self.downloadAIP = downloadAIP;
        self.downloadSIP = downloadSIP;
        self.downloadMETS = downloadMETS;

        /**
         * Fonctionne uniquement pour une notice unique attachée à l'unité doc
         * Revoir en cas de notices multiples
         */
        self.init = function (parentCtrl) {
            self.currentTab = parentCtrl.tabs.EXPORT;
            self.docUnit = parentCtrl.docUnit;
            self._cinesReports = [];

            // Cines
            if (angular.isDefined(self.docUnit.cinesReports)) {
                self._cinesReports = $filter('orderBy')(self.docUnit.cinesReports, '-lastModifiedDate');
                self.firstCines = self._cinesReports.shift();

                var certificatedReport = _.find(self.docUnit.cinesReports, function (report) {
                    return report.certificate !== null;
                });
                if (angular.isDefined(certificatedReport)) {
                    MessageSrvc.addSuccess(gettextCatalog.getString('Archivé au Cines'), {}, true);
                    self.certificate = certificatedReport.certificate;
                }
            }

            // Internet Archive
            if (angular.isDefined(self.docUnit.iaReports)) {
                self._iaReports = $filter('orderBy')(self.docUnit.iaReports, '-lastModifiedDate');
                self.firstIA = self._iaReports.shift();

                if (self.firstIA) {
                    self.iaurl = 'https://archive.org/details/' + self.firstIA.internetArchiveIdentifier;
                }
                _.find(self.docUnit.iaReports, function (report) {
                    if (report.status === 'SENT') {
                        // report.dateArchived
                        MessageSrvc.addSuccess(gettextCatalog.getString('Diffusé sur Internet Archive'), {}, true);
                        return true;
                    }
                    return false;
                });
            }

            if (self.docUnit.omekaExportStatus) {
                if (self.docUnit.omekaExportStatus === 'IN_PROGRESS') {
                    self.omekaDistribStatus = 'En cours';
                } else if (self.docUnit.omekaExportStatus === 'SENT') {
                    self.omekaDistribStatus = 'Envoyé';
                } else if (self.docUnit.omekaExportStatus === 'FAILED') {
                    self.omekaDistribStatus = 'Envoi en échec';
                }
            } else {
                self.omekaDistribStatus = 'Non envoyé';
            }
            if (self.docUnit.digLibExportStatus) {
                if (self.docUnit.digLibExportStatus === 'IN_PROGRESS') {
                    self.digLibDistribStatus = 'En cours';
                } else if (self.docUnit.digLibExportStatus === 'SENT') {
                    self.digLibDistribStatus = 'Envoyé';
                } else if (self.docUnit.digLibExportStatus === 'FAILED') {
                    self.digLibDistribStatus = 'Envoi en échec';
                }
            } else {
                self.digLibDistribStatus = 'Non';
            }

            // Affichage pour un temps limité à l'ouverture
            MessageSrvc.initPanel();
        };

        function downloadAIP() {
            MessageSrvc.addSuccess(gettextCatalog.getString('Téléchargement du fichier aip.xml'));
            ExportSrvc.getAip({ docUnit: self.docUnit.identifier }).$promise.then(
                function (data) {
                    FileSaver.saveAs(data.response, 'aip.xml');
                },
                function () {
                    MessageSrvc.addError(gettextCatalog.getString('Téléchargement du fichier impossible'));
                }
            );
        }

        function downloadSIP(status) {
            MessageSrvc.addSuccess(gettextCatalog.getString('Téléchargement du fichier sip.xml'));
            ExportSrvc.getSip({ docUnit: self.docUnit.identifier, cinesStatus: status }).$promise.then(
                function (data) {
                    FileSaver.saveAs(data.response, 'sip.xml');
                },
                function () {
                    MessageSrvc.addError(gettextCatalog.getString('Téléchargement du fichier impossible'));
                }
            );
        }

        function downloadMETS(status) {
            MessageSrvc.addSuccess(gettextCatalog.getString('Téléchargement du fichier mets.xml'));
            ExportSrvc.getMets({ docUnit: self.docUnit.identifier, cinesStatus: status }).$promise.then(
                function (data) {
                    FileSaver.saveAs(data.response, 'mets.xml');
                },
                function () {
                    MessageSrvc.addError(gettextCatalog.getString('Téléchargement du fichier impossible'));
                }
            );
        }
    });
})();
