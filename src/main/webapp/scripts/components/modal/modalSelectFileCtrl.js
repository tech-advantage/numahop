(function () {
    'use strict';

    angular.module("numaHopApp.controller")
        .controller("ModalSelectFileCtrl", function ($scope, $uibModalInstance, options) {

            var mainCtrl = this;
            mainCtrl.accept = ".xls, .xlsx";    // valeurs par défaut
            _.extend(mainCtrl, options);

            mainCtrl.cancel = cancel;
            mainCtrl.ok = ok;
            mainCtrl.setFiles = setFiles;


            /** Sélection des fichiers à uploader */
            function setFiles(element) {
                if (element.files.length > 0) {
                    $scope.$apply(function (scope) {
                        // Turn the FileList object into an Array
                        mainCtrl.files = _.map(element.files, angular.identity);
                    });
                }
            }

            function ok() {
                $uibModalInstance.close(mainCtrl.files);
            }

            function cancel() {
                $uibModalInstance.dismiss("cancel");
            }
        });
})();
