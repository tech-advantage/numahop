(function() {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('todoTableCtrl', todoTableCtrl)

    function todoTableCtrl(codeSrvc) {
        var ctrl = this;
        ctrl.code = codeSrvc;
        ctrl.const = {
            QUALITY_CONTROL_IN_PROGRESS: 'CONTROLE_QUALITE_EN_COURS',
            DOCUMENT_VALIDATION: 'VALIDATION_DOCUMENT'
        };

        ctrl.isQualityControlOrDocumentValidation = function(state) {
            return ctrl.const.QUALITY_CONTROL_IN_PROGRESS===state || ctrl.const.DOCUMENT_VALIDATION===state;
        };

        ctrl.isQualityControlInProgress = function(state) {
            return ctrl.const.QUALITY_CONTROL_IN_PROGRESS===state;
        };
    }
})();
