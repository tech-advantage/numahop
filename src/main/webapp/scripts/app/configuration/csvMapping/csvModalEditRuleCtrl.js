(function () {
    'use strict';

    angular.module("numaHopApp.controller")
        .controller("CSVModalEditRuleCtrl", function ($uibModalInstance, MappingSrvc, DocPropertyTypeSrvc, gettextCatalog, options) {

            var mainCtrl = this;
            mainCtrl.ok = ok;
            mainCtrl.cancel = cancel;
            
            mainCtrl.populateRule = populateRule;

            /** Configuration des listes */
            mainCtrl.config = {
                docUnitFields: MappingSrvc.docUnitFields,
                bibRecordFields: MappingSrvc.bibRecordFields
            };

            init();


            /** Initialisation du contrôleur */
            function init() {
                _.extend(mainCtrl, options);

                // Chargement des types de propriété
                mainCtrl.config.properties = loadDocPropertyTypes();

                mainCtrl.title = mainCtrl.rule.identifier ? gettextCatalog.getString("Édition de la règle") : gettextCatalog.getString("Nouvelle règle");
            }
            /** Chargement des types de propriété */
            function loadDocPropertyTypes() {
                var result = DocPropertyTypeSrvc.query({ customOnly: "true" });
                result.$promise
                    .then(function (propTypes) {
                        propTypes.unshift({ label: "", rank: 0 });    // sélection vide
                        return propTypes;
                    });
                return result;
            }
            
            function populateRule(ppty) {
                if(ppty.superType == "CUSTOM"){
                    mainCtrl.rule.csvField = ppty.label;
                } else {
                    mainCtrl.rule.csvField = ppty.identifier;
                }
            }

            /** Validation de la fenêtre modale */
            function ok(value) {
                if (value.property && !value.property.identifier) {
                    value.property = null;
                }
                $uibModalInstance.close(value);
            }
            
            /** Annulation de la fenêtre modale */
            function cancel() {
                $uibModalInstance.dismiss("cancel");
            }
        });
})();
