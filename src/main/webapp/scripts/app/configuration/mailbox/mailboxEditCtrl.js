(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('MailboxEditCtrl', MailboxEditCtrl);

    function MailboxEditCtrl($location, $route, $scope, $timeout, DocUnitBaseService, gettext, gettextCatalog, MailboxSrvc, MessageSrvc, ModalSrvc, ValidationSrvc) {
        var editCtrl = this;
        editCtrl.deleteMailbox = deleteMailbox;
        editCtrl.displayBoolean = DocUnitBaseService.displayBoolean;
        editCtrl.loadDto = loadDto;
        editCtrl.newProperty = newProperty;
        editCtrl.removeProperty = removeProperty;
        editCtrl.saveMailbox = saveMailbox;
        editCtrl.validation = ValidationSrvc;

        editCtrl.options = {
            boolean: DocUnitBaseService.options.booleanObj,
        };

        /**
         * Chargement du DTO à éditer
         *
         * @param {any} dto
         */
        function loadDto(dto) {
            if (dto) {
                // Édition
                if (dto.identifier) {
                    editCtrl.box = MailboxSrvc.get({ id: dto.identifier });
                    editCtrl.box.$promise.then(function () {
                        $location.search({ id: editCtrl.box.identifier });
                        editCtrl.loaded = true;
                    });
                }
                // Création
                else {
                    $location.search({});
                    editCtrl.box = new MailboxSrvc();
                    editCtrl.box.properties = [];
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
                if (angular.isDefined($scope.mailboxForm)) {
                    $scope.mailboxForm.$show();
                }
            });
        }

        /**
         * Sauvegarde de la configuration
         */
        function saveMailbox() {
            $timeout(function () {
                editCtrl.box
                    .$save()
                    .then(function (value) {
                        MessageSrvc.addSuccess(gettext('La configuration {{name}} a été sauvegardée'), { name: value.name });
                        $location.path('/administration/appconfiguration/mailbox').search({ id: value.identifier });
                        $route.reload();
                    })
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
         * Suppression d'une configuration
         */
        function deleteMailbox() {
            if (editCtrl.box && editCtrl.box.identifier) {
                ModalSrvc.confirmDeletion(gettextCatalog.getString('la configuration {{label}}', editCtrl.box)).then(function () {
                    editCtrl.box.$delete(function (value) {
                        MessageSrvc.addSuccess(gettext('La configuration {{label}} a été supprimée'), editCtrl.box);
                        $location.path('/administration/appconfiguration/mailbox').search({});
                        $route.reload();
                    });
                });
            }
        }

        /**
         * Ajoute une nouvelle propriété à la configuration courante
         */
        function newProperty() {
            if (editCtrl.box) {
                editCtrl.box.properties.push({ name: '', value: '' });
            }
        }

        /**
         * Suppression d'une propriété de la configuration courante
         *
         * @param {any} property
         */
        function removeProperty(property) {
            var idx = editCtrl.box.properties.indexOf(property);
            if (idx >= 0) {
                editCtrl.box.properties.splice(idx, 1);
            }
        }
    }
})();
