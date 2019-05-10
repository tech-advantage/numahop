(function () {
    'use strict';

    angular.module('numaHopApp.service')
        .factory('NumahopAutoCheckService', NumahopAutoCheckService);

    function NumahopAutoCheckService(gettext, MessageSrvc, AutoCheckSrvc) {

        var service = {};
        service.autoCheck = autoCheck;

        /** Contrôles automatiques */
        function autoCheck(type, identifier) {
            switch (type) {
                case "facile": checkFacile(identifier);
                    break;
                default: MessageSrvc.addWarn(gettext("Contrôle non reconnu"));
            }

        }

        /**
         * Vérification FACILE (cines)
         */
        function checkFacile(identifier) {
            AutoCheckSrvc.facile({ docUnit: identifier }).$promise
                .then(function () {
                    MessageSrvc.addInfo(gettext("Vérification FACILE en cours. La durée de cette opération dépend du nombre et de la taille des documents vérifiés."), null, true);
                });
        }

        return service;
    }
})();
