(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('DigitalDocumentAllOperationsCtrl', DigitalDocumentAllOperationsCtrl);

    function DigitalDocumentAllOperationsCtrl($scope, $q, $http, codeSrvc, DigitalDocumentSrvc, gettext, MessageSrvc, NumaHopStatusService, PageCheckSrvc, FileSaver) {
        var mainCtrl = this;

        var typeView = 'ThumbnailsView';
        $scope.url_viewer = undefined;
        mainCtrl.downloadMasterPdf = downloadMasterPdf;
        mainCtrl.downloadCheckSlip = downloadCheckSlip;
        mainCtrl.init = init;

        function init(parentCtrl) {
            mainCtrl.parentCtrl = parentCtrl;
            mainCtrl.digitalDocuments = parentCtrl.docUnit.digitalDocuments;
            mainCtrl.deliveryId = parentCtrl.delivery.identifier;

            if (!mainCtrl.digitalDocuments || !mainCtrl.digitalDocuments.length || mainCtrl.digitalDocuments.length < 1) {
                MessageSrvc.addWarn(gettext('Aucun document numérique rattaché'), {}, true);
            } else if (!NumaHopStatusService.isDigitalDocAvailable(mainCtrl.digitalDocuments[0])) {
                MessageSrvc.addWarn(gettext('Des opérations sont en cours sur le document. Il sera disponible ultérieurement'), {}, true);
            } else {
                mainCtrl.digitalDocument = mainCtrl.digitalDocuments[0];
                var params = {
                    id: mainCtrl.digitalDocument.identifier,
                };

                // Hauteur de l'iframe
                var container = angular.element('#tab-container');
                $scope.height_viewer = container && container.length && container[0].clientHeight ? container[0].clientHeight - 75 + 'px' : '98%';

                DigitalDocumentSrvc.get(params, function (value) {
                    mainCtrl.digitalDocument = value;
                    $scope.url_viewer = 'scripts/app/viewer/mirador.html?idDocument=' + params.id + '&typeView=' + typeView;

                    loadResults();
                    afterFetch();

                    MessageSrvc.addInfo(gettext('Statut du document numérique : {{status}}'), { status: codeSrvc['digitalDocument.' + value.status] }, true);
                    // Affichage pour un temps limité à l'ouverture
                    MessageSrvc.initPanel();
                });
            }
        }

        function afterFetch() {
            var params = {
                id: mainCtrl.digitalDocument.identifier,
            };
            DigitalDocumentSrvc.getMasterPdfInfos(params, function (masterPdfInfos) {
                mainCtrl.masterPdfInfos = masterPdfInfos;
            });
        }

        function loadResults() {
            var params = {
                id: mainCtrl.digitalDocument.identifier,
                deliveryId: mainCtrl.parentCtrl.delivery.identifier,
            };
            PageCheckSrvc.getSummary(params).$promise.then(function (value) {
                mainCtrl.checkResults = value;
            });
        }

        /**
         * Téléchargement du master pdf.
         **/
        function downloadMasterPdf(filename) {
            if (!filename || angular.isUndefined(mainCtrl.digitalDocument.identifier)) {
                return;
            }
            window.open('api/rest/viewer/document/' + mainCtrl.digitalDocument.identifier + '/master/', '_parent', '');
        }

        /**
         * Téléchargement du bordereau de contrôle
         **/
        function downloadCheckSlip(deliveryId) {
            if (!deliveryId) {
                deliveryId = mainCtrl.deliveryId;
            }

            if (deliveryId) {
                var url = 'api/rest/check/pdf/' + deliveryId;

                // on met la réponse dans un arraybuffer pour conserver l'encodage original dans le fichier sauvegardé
                $http.get(url, { responseType: 'arraybuffer' }).then(function (response) {
                    var filename = 'bordereau.pdf';
                    var blob = new Blob([response.data], { type: response.headers('content-type') });
                    FileSaver.saveAs(blob, filename);
                });
            }
        }
    }
})();
