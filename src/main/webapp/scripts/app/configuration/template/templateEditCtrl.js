(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('TemplateEditCtrl', TemplateEditCtrl);

    function TemplateEditCtrl($location, $q, $route, $scope, $timeout, gettext, gettextCatalog, MessageSrvc, ModalSrvc, TemplateSrvc, ValidationSrvc) {
        var editCtrl = this;
        editCtrl.init = init;
        editCtrl.deleteTemplate = deleteTemplate;
        editCtrl.onchangeName = onchangeName;
        editCtrl.saveTemplate = saveTemplate;
        editCtrl.setFiles = setFiles;
        editCtrl.templateNames = TemplateSrvc.templateNames;
        editCtrl.validation = ValidationSrvc;

        function init(parentCtrl) {
            editCtrl.loaded = false;

            if (parentCtrl.editedTemplate) {
                // Édition
                if (parentCtrl.editedTemplate.identifier) {
                    editCtrl.template = angular.copy(parentCtrl.editedTemplate);
                    onchangeName(editCtrl.template.name);

                    $location.search({ id: editCtrl.template.identifier });
                    editCtrl.loaded = true;
                }
                // Création
                else {
                    editCtrl.template = new TemplateSrvc();

                    $location.search({});
                    editCtrl.loaded = true;
                    openForm();
                }
            }
        }

        /**
         * Ouverture du formulaire et des sous formulaires
         */
        function openForm() {
            $timeout(function () {
                if (angular.isDefined($scope.templateForm)) {
                    $scope.templateForm.$show();
                }
            });
        }

        /**
         * Sauvegarde de la configuration
         */
        function saveTemplate() {
            $timeout(function () {
                // Sauvegarde du template
                editCtrl.template
                    .$save()
                    // Téléversement du fichier
                    .then(function (value) {
                        if (editCtrl.files && editCtrl.files.length > 0) {
                            return TemplateSrvc.uploadTemplate(value, editCtrl.files);
                        } else {
                            return $q.when(value);
                        }
                    })
                    // Sauvegarde Ok
                    .then(function (value) {
                        delete editCtrl.files;
                        MessageSrvc.addSuccess(gettext('Le template {{name}} a été sauvegardé'), { name: value.name });
                        $location.path('/administration/appconfiguration/template').search({ id: value.identifier });
                        $route.reload();
                    })
                    // Sauvegarde Ko
                    .catch(function (response) {
                        editCtrl.errors = _.chain(response.data.errors)
                            .groupBy('field')
                            .mapObject(function (list) {
                                return _.pluck(list, 'code');
                            })
                            .value();

                        openForm();
                    });
            });
        }

        /**
         * Suppression d'un template
         */
        function deleteTemplate() {
            if (editCtrl.template && editCtrl.template.identifier) {
                var label = TemplateSrvc.displayTemplateName(editCtrl.template.name);

                ModalSrvc.confirmDeletion(gettextCatalog.getString('le template {{label}}', { label: label })).then(function () {
                    editCtrl.template.$delete(function (value) {
                        MessageSrvc.addSuccess(gettext('La template {{label}} a été supprimé'), { label: label });
                        $location.path('/administration/appconfiguration/template').search({});
                        $route.reload();
                    });
                });
            }
        }

        /**
         * Sélection des fichiers à uploader
         *
         * @param {any} element
         */
        function setFiles(element) {
            if (element.files.length > 0) {
                $scope.$apply(function (scope) {
                    // Turn the FileList object into an Array
                    editCtrl.files = _.map(element.files, angular.identity);
                });
            }
        }

        /**
         * Mise à jour du format attendu, au changement de nom
         *
         * @param {any} value
         */
        function onchangeName(value) {
            editCtrl._engine = TemplateSrvc.getFormat(value);
        }
    }
})();
