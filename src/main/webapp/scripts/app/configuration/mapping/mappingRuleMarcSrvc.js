(function () {
    'use strict';

    angular.module('numaHopApp.service').factory('MappingRuleMarcSrvc', MappingRuleMarcSrvc);

    function MappingRuleMarcSrvc(MappingRuleCtxMenuSrvc) {
        var service = {};
        service.ctxCondition = ctxCondition;
        service.ctxConditionConf = ctxConditionConf;
        service.ctxExpression = ctxExpression;
        service.ctxExpressionConf = ctxExpressionConf;
        service.getFields = getFields;

        function ctxCondition(rule) {
            return [
                [
                    "Existence d'un champ / sous-champ",
                    function ($itemScope, $event) {
                        rule.condition = MappingRuleCtxMenuSrvc.wrapWith(rule.condition, $event, 'exists(record, ', "'<tag>', '<code, optionnel>'", ')');
                    },
                ],
                null, // délimiteur
                // Sous-menu de sélection des champs du leader
                ['Leader', angular.noop, ctxLeader(rule, 'condition')],
            ];
        }
        function ctxConditionConf(rule) {
            return [];
        }
        function ctxExpression(rule) {
            return [
                [
                    'Collection',
                    function ($itemScope, $event) {
                        rule.expression = MappingRuleCtxMenuSrvc.wrapWith(rule.expression, $event, 'collection', '\\<tag>');
                    },
                ],
                [
                    'Collectivité',
                    function ($itemScope, $event) {
                        rule.expression = MappingRuleCtxMenuSrvc.wrapWith(rule.expression, $event, 'corporate', '\\<tag>');
                    },
                ],
                [
                    'Personne',
                    function ($itemScope, $event) {
                        rule.expression = MappingRuleCtxMenuSrvc.wrapWith(rule.expression, $event, 'person', '\\<tag>');
                    },
                ],
                [
                    'Title',
                    function ($itemScope, $event) {
                        rule.expression = MappingRuleCtxMenuSrvc.wrapWith(rule.expression, $event, 'title', '\\<tag>');
                    },
                ],
                null, // délimiteur
                [
                    'Sous-champs (concaténation)',
                    function ($itemScope, $event) {
                        rule.expression = MappingRuleCtxMenuSrvc.wrapWith(rule.expression, $event, 'concatWithSep', '\\<tag>');
                    },
                ],
                [
                    'Sous-champs (regroupements)',
                    function ($itemScope, $event) {
                        rule.expression = MappingRuleCtxMenuSrvc.wrapWith(rule.expression, $event, 'subfields', '\\<tag>');
                    },
                ],
                null, // délimiteur
                // Sous-menu de sélection des champs du leader
                ['Leader', angular.noop, ctxLeader(rule, 'expression')],
            ];
        }
        function ctxExpressionConf(rule) {
            return [
                [
                    'Collection: filtrage des sous-champs',
                    function ($itemScope, $event) {
                        rule.expressionConf = MappingRuleCtxMenuSrvc.wrapWith(rule.expressionConf, $event, 'collectionFilter', "'<code>'...");
                    },
                ],
                [
                    'Collectivité: filtrage des sous-champs',
                    function ($itemScope, $event) {
                        rule.expressionConf = MappingRuleCtxMenuSrvc.wrapWith(rule.expressionConf, $event, 'corporateFilter', "'<code>'...");
                    },
                ],
                [
                    'Personne: filtrage des sous-champs',
                    function ($itemScope, $event) {
                        rule.expressionConf = MappingRuleCtxMenuSrvc.wrapWith(rule.expressionConf, $event, 'personFilter', "'<code>'...");
                    },
                ],
                [
                    'Title: filtrage des sous-champs',
                    function ($itemScope, $event) {
                        rule.expressionConf = MappingRuleCtxMenuSrvc.wrapWith(rule.expressionConf, $event, 'titleFilter', "'<code>'...");
                    },
                ],
                null, // délimiteur
                [
                    'Sous-champs (concaténation): définition des sous-champs',
                    function ($itemScope, $event) {
                        rule.expressionConf = MappingRuleCtxMenuSrvc.wrapWith(rule.expressionConf, $event, 'concatWithSepCodes', "'<code>'...");
                    },
                ],
                [
                    'Sous-champs (concaténation): séparateur entre codes identiques',
                    function ($itemScope, $event) {
                        rule.expressionConf = MappingRuleCtxMenuSrvc.wrapWith(rule.expressionConf, $event, 'concatWithSepInner', "'<separateur>'");
                    },
                ],
                [
                    'Sous-champs (concaténation): séparateur entre codes différents',
                    function ($itemScope, $event) {
                        rule.expressionConf = MappingRuleCtxMenuSrvc.wrapWith(rule.expressionConf, $event, 'concatWithSepOuter', "'<separateur>'");
                    },
                ],
                [
                    "Sous-champs (regroupements): ajout d'un sous-champ",
                    function ($itemScope, $event) {
                        rule.expressionConf = MappingRuleCtxMenuSrvc.wrapWith(rule.expressionConf, $event, 'subfieldsAdd', "'<code>', '<préfixe, optionnel>', '<suffixe, optionnel>'");
                    },
                ],
                [
                    "Sous-champs (regroupements): ajout d'un groupe de sous-champs",
                    function ($itemScope, $event) {
                        rule.expressionConf = MappingRuleCtxMenuSrvc.wrapWith(rule.expressionConf, $event, 'subfieldsAddGroup', "'<préfixe du groupe>', '<suffixe du groupe>', '<préfixe ou code du sous-champ>'...");
                    },
                ],
                [
                    'Sous-champs (regroupements): filtrage des sous-champs',
                    function ($itemScope, $event) {
                        rule.expressionConf = MappingRuleCtxMenuSrvc.wrapWith(rule.expressionConf, $event, 'subfieldsFilter', "'<code>'...");
                    },
                ],
                [
                    'Sous-champs (regroupements): séparateur par défaut',
                    function ($itemScope, $event) {
                        rule.expressionConf = MappingRuleCtxMenuSrvc.wrapWith(rule.expressionConf, $event, 'subfieldsSeparator', "'<séparateur>'");
                    },
                ],
            ];
        }
        // Propriétés du leader, pour la norme UNIMARC (cf. http://multimedia.bnf.fr/unimarcb_trad/Bsection5-Label_notice-6-2010.pdf)
        function ctxLeader(rule, field) {
            return [
                [
                    'Longueur de la notice (0-4)',
                    function ($itemScope, $event) {
                        rule[field] = MappingRuleCtxMenuSrvc.replaceWith(rule[field], $event, 'leader.getRecordLength()');
                    },
                ],
                [
                    'Statut de la notice (5)',
                    function ($itemScope, $event) {
                        rule[field] = MappingRuleCtxMenuSrvc.replaceWith(rule[field], $event, '(String)leader.getRecordStatus()');
                    },
                ],
                [
                    'Type de notice (6)',
                    function ($itemScope, $event) {
                        rule[field] = MappingRuleCtxMenuSrvc.replaceWith(rule[field], $event, '(String)leader.getTypeOfRecord()');
                    },
                ],
                [
                    'Niveau bibliographique (7)',
                    function ($itemScope, $event) {
                        rule[field] = MappingRuleCtxMenuSrvc.replaceWith(rule[field], $event, '(String)leader.getImplDefined1()[0]');
                    },
                ],
                [
                    'Code de niveau hiérarchique (8)',
                    function ($itemScope, $event) {
                        rule[field] = MappingRuleCtxMenuSrvc.replaceWith(rule[field], $event, '(String)leader.getImplDefined1()[1]');
                    },
                ],
                // ["Encodage des caractères (9)", function ($itemScope, $event) {
                //     rule[field] = MappingRuleCtxMenuSrvc.replaceWith(rule[field], $event, "(String)leader.getCharCodingScheme()");
                // }],
                [
                    "Longueur de l'indicateur (10)",
                    function ($itemScope, $event) {
                        rule[field] = MappingRuleCtxMenuSrvc.replaceWith(rule[field], $event, 'leader.getIndicatorCount()');
                    },
                ],
                [
                    'Longueur du code de sous-zone (11)',
                    function ($itemScope, $event) {
                        rule[field] = MappingRuleCtxMenuSrvc.replaceWith(rule[field], $event, 'leader.getSubfieldCodeLength()');
                    },
                ],
                [
                    'Adresse de base des données (12-16)',
                    function ($itemScope, $event) {
                        rule[field] = MappingRuleCtxMenuSrvc.replaceWith(rule[field], $event, 'leader.getBaseAddressOfData()');
                    },
                ],
                [
                    "Niveau d'encodage (17)",
                    function ($itemScope, $event) {
                        rule[field] = MappingRuleCtxMenuSrvc.replaceWith(rule[field], $event, '(String)leader.getImplDefined2()[0]');
                    },
                ],
                [
                    'Forme du catalogage descriptif (18)',
                    function ($itemScope, $event) {
                        rule[field] = MappingRuleCtxMenuSrvc.replaceWith(rule[field], $event, '(String)leader.getImplDefined2()[1]');
                    },
                ],
                // ["Non défini (19)", function ($itemScope, $event) {
                //     rule[field] = MappingRuleCtxMenuSrvc.replaceWith(rule[field], $event, "(String)leader.getImplDefined2()[2]");
                // }],
                [
                    'Longueur de la partie "longueur de zone" (20)',
                    function ($itemScope, $event) {
                        rule[field] = MappingRuleCtxMenuSrvc.replaceWith(rule[field], $event, '(String)leader.getEntryMap()[0]');
                    },
                ],
                [
                    'Longueur de la partie "position du premier caractère" (21)',
                    function ($itemScope, $event) {
                        rule[field] = MappingRuleCtxMenuSrvc.replaceWith(rule[field], $event, '(String)leader.getEntryMap()[1]');
                    },
                ],
                [
                    'Longueur de la partie "partie relative à l\'application" (22)',
                    function ($itemScope, $event) {
                        rule[field] = MappingRuleCtxMenuSrvc.replaceWith(rule[field], $event, '(String)leader.getEntryMap()[2]');
                    },
                ],
                // ["Non défini (23)", function ($itemScope, $event) {
                //     rule[field] = MappingRuleCtxMenuSrvc.replaceWith(rule[field], $event, "(String)leader.getEntryMap()[3]");
                // }]
            ];
        }
        /** Extraction des champs MARC présents dans le texte passé en paramètre */
        function getFields(text, ignorePrefix) {
            var allMatches = [];
            var marcRegexp = ignorePrefix ? /([x\d]{3}(?!\d))(?:(?!\d)\$([0-9a-zA-Z]))?/g : /(?:\\)([x\d]{3}(?!\d))(?:(?!\d)\$([0-9a-zA-Z]))?/g;
            var match = marcRegexp.exec(text);

            while (match !== null) {
                var tag = match[1];
                var code = match[2];
                allMatches.push(tag + (code ? '$' + code : ''));

                match = marcRegexp.exec(text);
            }
            return _.chain(allMatches).uniq().sortBy().value();
        }
        return service;
    }
})();
