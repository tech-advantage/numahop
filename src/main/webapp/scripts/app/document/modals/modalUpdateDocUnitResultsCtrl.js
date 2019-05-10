(function() {
    'use strict';

    angular.module("numaHopApp.controller")
        .controller("modalUpdateDocUnitResultsCtrl", ModalUpdateDocUnitResultsCtrl);

    function ModalUpdateDocUnitResultsCtrl($uibModalInstance, options, gettextCatalog, DocUnitBaseService) {

        var mainCtrl = this;
        mainCtrl.update = update;
        mainCtrl.close = close;
        mainCtrl.values = {};
        
     // Définition des listes déroulantes
        mainCtrl.options = {
            condreportTypes: DocUnitBaseService.options.condreportTypes
        };
        
        init();
        
        function init() { 
            mainCtrl.values.distributable = false;
            mainCtrl.values.archivable = false;
            mainCtrl.values.reportType = {};
            mainCtrl.loaded = true;
        }

        /**
         * Fermeture de la fenêtre
         * @return {[type]} [description]
         */
        function update() {
            $uibModalInstance.close(mainCtrl.values);
        }

        function close() {
            $uibModalInstance.dismiss("cancel");
        }
        
        
        mainCtrl.updateDistributable = function() {
            mainCtrl.values.distributable = !mainCtrl.values.distributable;
        };
        
        mainCtrl.updateArchivable = function() {
            mainCtrl.values.archivable = !mainCtrl.values.archivable;
        };

    }

})();
