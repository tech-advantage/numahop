(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('DeliveryConfEditCtrl', DeliveryConfEditCtrl);

    function DeliveryConfEditCtrl($location, $q, $route, $routeParams, $scope, $timeout,
        DeliveryConfSrvc, DocUnitBaseService, gettext, gettextCatalog, MessageSrvc, ValidationSrvc) {

        var editCtrl = this;
        editCtrl.cancel = cancel;
        editCtrl.displayBoolean = DocUnitBaseService.displayBoolean;
        editCtrl.init = loadValues;
        editCtrl.openForm = openForm;
        editCtrl.saveValues = saveValues;
        editCtrl.validation = ValidationSrvc;

        editCtrl.options = {                
            boolean: [
                { value: true, text: "Oui" },
                { value: false, text: "Non" }
            ]
        };

        /**
         * Chargement des valeurs de la bibliothèque library
         * 
         * @param {any} library 
         * @returns 
         */
        function loadValues(library) {
            editCtrl.loaded = false;

            if (library) {
                editCtrl.library = library;
                DeliveryConfSrvc.get({ id: library.identifier }).$promise.then(function (config) {
                    editCtrl.loaded = true;
                    editCtrl.config = config;
                    if (editCtrl.isNew) {
                        openForm();
                    }
                });
            }
            return $q.when();
        }

        /**
         * Annulation de l'édition en cours
         * 
         */
        function cancel() {
            loadValues(editCtrl.library).then(function () {
                $scope.valuesForm.$cancel();
            });
        }

        /**
         * Sauvegarde des modifications apportées dans l'édition en cours
         * 
         * @returns 
         */
        function saveValues() {
            $timeout(function () {
                // Mise à jour de la propriété
                editCtrl.config.$save().then(function() {
                        MessageSrvc.addSuccess(gettext("Les modifications ont été sauvegardées"));
                        $location.search({ library: editCtrl.library.identifier });
                });
            });
        }

        /**
         * Ouverture du formulaire d'édition
         */
        function openForm() {
            $timeout(function () {
                if (angular.isDefined($scope.valuesForm)) {
                    $scope.valuesForm.$show();
                }
            });
        }
    }
})();
