(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('DocUnitProcessedEditWidgetCtrl', DocUnitProcessedEditWidgetCtrl);

    function DocUnitProcessedEditWidgetCtrl(config, DocUnitBaseService, gettextCatalog) {

        var mainCtrl = this;
        mainCtrl.isConfigured = isConfigured;

        mainCtrl.options = {
            boolean: DocUnitBaseService.options.booleanObj,
            types: [
                { value: "ARCHIVE", text: gettextCatalog.getString('UD archivées') },
                { value: "EXPORT", text: gettextCatalog.getString('UD diffusées') },
                { value: "EXPORT_OMEKA", text: gettextCatalog.getString('UD diffusées sur OMEKA') },
                { value: "EXPORT_LOCAL", text: gettextCatalog.getString('UD diffusées localement') }
            ]
        };


        init();

        /**
         * Initialisation du contrôleur
         */
        function init() {
            config.from = config.from || 10;
            config.failures = angular.isDefined(config.failures) ? config.failures : false;
        }

        /**
         * Le widget est-il configuré
         * @return {Boolean} retourne vrai si le widget est configuré correctement, faux sinon
         */
        function isConfigured() {
            return angular.isDefined(config.type) && angular.isDefined(config.failures) && angular.isDefined(config.from);
        }
    }
})();
