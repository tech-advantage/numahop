(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('TemplateCtrl', TemplateCtrl);

    function TemplateCtrl($q, $routeParams, $scope, $timeout, NumaHopInitializationSrvc, TemplateSrvc) {
        var mainCtrl = this;
        mainCtrl.create = create;
        mainCtrl.displayTemplateName = TemplateSrvc.displayTemplateName;
        mainCtrl.edit = edit;
        mainCtrl.sortLibrary = sortLibrary;

        init();

        /**
         * Initialisation du contrôleur
         */
        function init() {
            mainCtrl.loaded = false;

            $q.all([loadTemplates(), loadLibraries()]).then(function () {
                mainCtrl.loaded = true;

                if (angular.isDefined($routeParams.id)) {
                    select($routeParams.id);
                }
                refreshTemplate();
            });
        }

        /**
         * Chargement de la liste des templates
         *
         */
        function loadTemplates() {
            mainCtrl.templates = TemplateSrvc.query();
            return mainCtrl.templates.$promise;
        }

        /**
         * Chargement de la liste des bibliothèques
         *
         * @returns
         */
        function loadLibraries() {
            return NumaHopInitializationSrvc.loadLibraries().then(function (libs) {
                mainCtrl.libraries = _.sortBy(libs, sortLibrary);
            });
        }

        /**
         * Critère de tri des bibliothèques
         *
         * @param {any} library
         * @returns
         */
        function sortLibrary(library) {
            return library.name.toLowerCase();
        }

        /**
         * Création d'un nouveau template
         *
         */
        function create() {
            mainCtrl.editedTemplate = {};
            setSelection();
            refreshTemplate();
        }

        /**
         * Édition d'une configuration existante
         *
         * @param {any} template
         */
        function edit(template) {
            mainCtrl.editedTemplate = template;
            setSelection(template);
            // Écran d'édition
            refreshTemplate();
        }

        function refreshTemplate() {
            return $timeout(function () {
                mainCtrl.configurationInclude = null;
                $scope.$apply();
                mainCtrl.configurationInclude = 'scripts/app/configuration/template/templateEdit.html';
            });
        }

        /**
         * Sélection d'un template à partir de son identifiant
         *
         * @param {any} id
         */
        function select(id) {
            if (id) {
                var found = _.find(mainCtrl.templates, function (template) {
                    return template.identifier === id;
                });
                if (angular.isDefined(found)) {
                    edit(found);
                } else {
                    edit({ identifier: id });
                }
            } else {
                setSelection();
            }
        }

        /**
         * Mise à jour des flags _selected
         *
         * @param {any} selTemplate
         */
        function setSelection(selTemplate) {
            _.each(mainCtrl.templates, function (template) {
                delete template._selected;
            });
            if (selTemplate) {
                selTemplate._selected = true;
            }
        }
    }
})();
