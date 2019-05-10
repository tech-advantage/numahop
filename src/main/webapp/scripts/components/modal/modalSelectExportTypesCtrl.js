(function () {
    'use strict';

    angular.module("numaHopApp.controller")
        .controller("ModalSelectExportTypesCtrl", function ($scope, $uibModalInstance) {

            var mainCtrl = this;

            mainCtrl.ftpExport = false;
            mainCtrl.mets = true;
            mainCtrl.aip = false;
            mainCtrl.metaEad = false;
            mainCtrl.master = true;
            mainCtrl.view = false;
            mainCtrl.thumb = false;
            mainCtrl.pdf = false;

            mainCtrl.cancel = cancel;
            mainCtrl.ok = ok;

            function ok() {
                var exportTypes = [];
                if(mainCtrl.mets) {exportTypes.push("METS");}
                if(mainCtrl.aip) {exportTypes.push("AIP");}
                if(mainCtrl.metaEad) { 
                    exportTypes.push("EAD");
                } 
                if(mainCtrl.view) {exportTypes.push("VIEW");}
                if(mainCtrl.master) {exportTypes.push("MASTER");}
                if(mainCtrl.pdf) {exportTypes.push("PDF");}
                if(mainCtrl.thumb) {exportTypes.push("THUMBNAIL");}
                
                var objExportTypes = {
                  ftpExport: mainCtrl.ftpExport,
                  exportTypes: exportTypes
                };
                
                $uibModalInstance.close(objExportTypes);
            }

            function cancel() {
                $uibModalInstance.dismiss("cancel");
            }
        });
})();
