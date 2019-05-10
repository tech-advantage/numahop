(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('ProjectDetailsEditWidgetCtrl', ProjectDetailsEditWidgetCtrl);

    function ProjectDetailsEditWidgetCtrl(projects, config, ValidationSrvc) {

        var mainCtrl = this;

        mainCtrl.isConfigured = isConfigured;

        mainCtrl.validation = {
            required: ValidationSrvc.required
        };

        mainCtrl.options = {
            projects: {
                text: "name",
                multiple: false,
                data: projects
            }
        };

        /**
         * Le widget est-il configuré
         * @return {Boolean} retourne vrai si le widget est configuré correctement, faux sinon
         */
        function isConfigured() {
            return !!config.project;
        }
    }
})();
