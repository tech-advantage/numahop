(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('ModalEditModelStateCtrl', function ($uibModalInstance, gettextCatalog, WorkflowHandleSrvc, codeSrvc, options) {
        var mainCtrl = this;
        mainCtrl.ok = ok;
        mainCtrl.cancel = cancel;

        init();

        /** Initialisation du contrôleur */
        function init() {
            _.extend(mainCtrl, options);

            mainCtrl.config = {
                types: WorkflowHandleSrvc.getStateTypeForStateKey(mainCtrl.state.key),
                groups: mainCtrl.groups,
            };

            mainCtrl.title = mainCtrl.state.identifier ? gettextCatalog.getString("Édition de l'étape") : gettextCatalog.getString('Nouvelle étape');
            mainCtrl.title += ' : ' + codeSrvc['workflow.' + mainCtrl.state.key];
        }

        /** Validation de la fenêtre modale */
        function ok(value) {
            $uibModalInstance.close(value);
        }
        /** Annulation de la fenêtre modale */
        function cancel() {
            $uibModalInstance.dismiss('cancel');
        }
    });
})();
