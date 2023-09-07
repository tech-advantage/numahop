(function () {
    'use strict';

    angular.module('numaHopApp.service').factory('WorkflowLotHandleSrvc', function () {
        /************************************
         * Fonctions sur le lot en paramètres
         ************************************/
        var isLotLocked = function (lot) {
            if (!lot || lot.status === 'CLOSED') {
                return true;
            }
            if (lot.status === 'CREATED') {
                return false;
            }
            if (!lot.docUnits || lot.docUnits.length === 0) {
                return false;
            }
            return true;
        };

        var isDocFromLotLocked = function (doc) {
            if (!doc) {
                return true;
            }
            return isLotLocked(doc.lot);
        };

        return {
            isLotLocked: isLotLocked,
            isDocFromLotLocked: isDocFromLotLocked,
        };
    });
})();
