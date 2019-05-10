(function () {
    'use strict';

    angular.module("numaHopApp.controller")
        .controller("modalDeleteDocUnitResultsCtrl", ModalDeleteDocUnitResultsCtrl);

    function ModalDeleteDocUnitResultsCtrl($uibModalInstance, options, gettextCatalog, $location) {

        var mainCtrl = this;
        mainCtrl.entities = options.entities;
        mainCtrl.ok = ok;
        mainCtrl.redirect = redirect;

        init();

        /**
         * Traduction des codes d'erreur
         */
        function init() {
            _.each(mainCtrl.entities, function (entity) {
                if (angular.isUndefined(entity.errors)) {
                    entity.result = gettextCatalog.getString('Supprimé avec succès');
                    entity.deleted = true;
                } else {
                    entity.deleted = false;
                    entity.result = "";
                    _.each(entity.errors, function (error) {
                        entity.result += gettextCatalog.getString('Suppression impossible : ');
                        switch (error.code) {
                            case "DOC_UNIT_IN_PROJECT": entity.result += gettextCatalog.getString("L'unité documentaire appartient à un projet");
                                break;
                        }
                        entity.result += "\n";
                    });
                }
            });
        }

        /**
         * Fermeture de la fenêtre
         * @return {[type]} [description]
         */
        function ok() {
            $uibModalInstance.dismiss("cancel");
        }

        function redirect(path) {
            ok();
            $location.path(path);
        }

    }

})();
