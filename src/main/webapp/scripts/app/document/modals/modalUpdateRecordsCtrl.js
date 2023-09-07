(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('ModalUpdateRecordsCtrl', ModalUpdateRecordsCtrl);

    function ModalUpdateRecordsCtrl($uibModalInstance, gettextCatalog, MappingSrvc, NumaHopInitializationSrvc, options) {
        var mainCtrl = this;
        mainCtrl.addField = addField;
        mainCtrl.deleteField = deleteField;
        mainCtrl.addProperty = addProperty;
        mainCtrl.deleteProperty = deleteProperty;
        mainCtrl.applyUpdates = applyUpdates;
        mainCtrl.close = close;

        init();

        /**
         * Initialisation du contrôleur
         */
        function init() {
            mainCtrl.updates = {
                fields: [],
                properties: [],
            };
            mainCtrl.fields = angular.copy(MappingSrvc.bibRecordFields);
            mainCtrl.fields.shift(); // on supprime le 1er élément -> valeur vide
            var pTypes = NumaHopInitializationSrvc.loadDocPropertyTypes();

            pTypes.then(function (types) {
                mainCtrl.types = types;
                mainCtrl.loaded = true;
            });
        }

        /**
         * Ajout d'un champ
         */
        function addField() {
            mainCtrl.updates.fields.push({});
        }

        /**
         * Suppression d'un champ existant
         *
         * @param {any} field
         */
        function deleteField(field) {
            var idx = mainCtrl.updates.fields.indexOf(field);
            if (idx >= 0) {
                mainCtrl.updates.fields.splice(idx, 1);
            }
        }

        /**
         * Ajout d'une propriété
         */
        function addProperty() {
            mainCtrl.updates.properties.push({});
        }

        /**
         * Suppression d'une propriété existante
         *
         * @param {any} property
         */
        function deleteProperty(property) {
            var idx = mainCtrl.updates.properties.indexOf(property);
            if (idx >= 0) {
                mainCtrl.updates.properties.splice(idx, 1);
            }
        }

        /**
         * Fermeture/Annulation de la fenêtre modale
         */
        function close() {
            $uibModalInstance.dismiss('cancel');
        }

        /**
         * Fermeture/Validation de la fenêtre modale
         */
        function applyUpdates() {
            var updates = {};
            updates.fields = _.chain(mainCtrl.updates.fields)
                .filter(function (f) {
                    return angular.isDefined(f.type);
                })
                .map(function (f) {
                    return _.pick(f, 'type', 'value');
                })
                .value();
            updates.properties = _.chain(mainCtrl.updates.properties)
                .filter(function (p) {
                    return angular.isDefined(p.type);
                })
                .map(function (p) {
                    return {
                        type: p.type.identifier,
                        value: p.value,
                    };
                })
                .value();
            $uibModalInstance.close(updates);
        }
    }
})();
