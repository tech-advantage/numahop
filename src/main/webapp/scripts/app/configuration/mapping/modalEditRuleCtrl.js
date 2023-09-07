(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('ModalEditRuleCtrl', function ($uibModalInstance, DocPropertyTypeSrvc, gettextCatalog, MappingRuleEadSrvc, MappingRuleMarcSrvc, MappingSrvc, options) {
        var mainCtrl = this;
        mainCtrl.ok = ok;
        mainCtrl.cancel = cancel;

        /** Configuration des listes */
        mainCtrl.config = {
            docUnitFields: MappingSrvc.docUnitFields,
            bibRecordFields: MappingSrvc.bibRecordFields,
        };

        init();

        /** Initialisation du contrôleur */
        function init() {
            _.extend(mainCtrl, options);

            switch (options.type) {
                case 'EAD':
                    /** Menus contextuels */
                    mainCtrl.ctxCondition = MappingRuleEadSrvc.ctxCondition;
                    mainCtrl.ctxConditionConf = MappingRuleEadSrvc.ctxConditionConf;
                    mainCtrl.ctxExpression = MappingRuleEadSrvc.ctxExpression;
                    mainCtrl.ctxExpressionConf = MappingRuleEadSrvc.ctxExpressionConf;
                    mainCtrl.getFields = MappingRuleEadSrvc.getFields;
                    break;
                case 'MARC':
                    /** Menus contextuels */
                    mainCtrl.ctxCondition = MappingRuleMarcSrvc.ctxCondition;
                    mainCtrl.ctxConditionConf = MappingRuleMarcSrvc.ctxConditionConf;
                    mainCtrl.ctxExpression = MappingRuleMarcSrvc.ctxExpression;
                    mainCtrl.ctxExpressionConf = MappingRuleMarcSrvc.ctxExpressionConf;
                    mainCtrl.getFields = MappingRuleMarcSrvc.getFields;
                    break;
            }

            // Chargement des types de propriété
            mainCtrl.config.properties = loadDocPropertyTypes();
            // Accordions dépliés si le champ conf est renseigné
            mainCtrl.accordionCondition = !!mainCtrl.rule.conditionConf;
            mainCtrl.accordionExpression = !!mainCtrl.rule.expressionConf;
            // Détails des champs MARC des scripts; les champs de config n'en contiennent pas
            mainCtrl.expressionFields = mainCtrl.getFields(mainCtrl.rule.expression);
            mainCtrl.conditionFields = mainCtrl.getFields(mainCtrl.rule.condition);

            mainCtrl.title = mainCtrl.rule.identifier ? gettextCatalog.getString('Édition de la règle') : gettextCatalog.getString('Nouvelle règle');
        }
        /** Chargement des types de propriété */
        function loadDocPropertyTypes() {
            var result = DocPropertyTypeSrvc.query({ dto: 'true' });
            result.$promise.then(function (propTypes) {
                propTypes.unshift({ label: '', rank: 0 }); // sélection vide
                return propTypes;
            });
            return result;
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
            $uibModalInstance.dismiss('cancel');
        }
    });
})();
