(function () {
    'use strict';

    angular.module('numaHopApp.service').factory('MappingRuleEadSrvc', MappingRuleEadSrvc);

    function MappingRuleEadSrvc(MappingRuleCtxMenuSrvc) {
        var service = {};
        service.ctxCondition = ctxCondition;
        service.ctxConditionConf = ctxConditionConf;
        service.ctxExpression = ctxExpression;
        service.ctxExpressionConf = ctxExpressionConf;
        service.getFields = getFields;

        function ctxCondition(rule) {
            return [
                menuItem(rule, 'condition', '<strong>Object composant</strong> (c)', '\\c'),
                menuItem(rule, 'condition', '<strong>Object composant du 1er niveau</strong>', '\\root'),
                null, // délimiteur
                menuItem(rule, 'condition', '<strong>Description identification</strong> (did)', '\\did'),
                menuItem(rule, 'condition', 'unitid.type', '\\did.unitid.type == "<type>"'),
                menuItem(rule, 'condition', 'physfacet.type', '\\did.physdesc.physfacet.type == "<type>"'),
                null, // délimiteur
                menuItem(rule, 'condition', '<strong>Scope and content</strong> (scopecontent)', '\\scopecontent'),
                menuItem(rule, 'condition', 'corpname.role', '\\scopecontent.p.corpname.role == "<role>"'),
                menuItem(rule, 'condition', 'famname.role', '\\scopecontent.p.famname.role == "<role>"'),
                menuItem(rule, 'condition', 'geogname.role', '\\scopecontent.p.geogname.role == "<role>"'),
                menuItem(rule, 'condition', 'persname.role', '\\scopecontent.p.persname.role == "<role>"'),
                null, // délimiteur
                menuItem(rule, 'condition', '<strong>Controlled access headings</strong> (controlaccess)', '\\controlaccess'),
                menuItem(rule, 'condition', 'corpname.role', '\\controlaccess.corpname.role == "<role>"'),
                menuItem(rule, 'condition', 'famname.role', '\\controlaccess.famname.role == "<role>"'),
                menuItem(rule, 'condition', 'geogname.role', '\\controlaccess.geogname.role == "<role>"'),
                menuItem(rule, 'condition', 'persname.role', '\\controlaccess.persname.role == "<role>"'),
            ];
        }
        function ctxConditionConf(rule) {
            return [];
        }
        function ctxExpression(rule) {
            return [
                menuItem(rule, 'expression', '<strong>Entête EAD</strong>', '\\eadheader'),
                menuItem(rule, 'expression', 'publication', '\\eadheader.filedesc.publicationstmt.publisher.content'),
                menuItem(rule, 'expression', 'langue', '\\eadheader.profiledesc.langusage.language.content'),
                null, // délimiteur
                menuItem(rule, 'expression', '<strong>Object composant</strong> (c)', '\\c'),
                menuItem(rule, 'expression', 'identifiant', '\\id'),
                menuItem(rule, 'expression', 'relatedmaterial', 'text(\\relatedmaterial)'),
                menuItem(rule, 'expression', 'separatedmaterial', 'text(\\separatedmaterial)'),
                menuItem(rule, 'expression', '<strong>Object composant du 1er niveau</strong>', '\\root'),
                null, // délimiteur
                menuItem(rule, 'expression', '<strong>Description identification</strong> (did)', '\\did'),
                menuItem(rule, 'expression', 'unitid', '\\did.unitid.content'),
                menuItem(rule, 'expression', 'unitdate', 'normal(\\did.unitdate)'),
                menuItem(rule, 'expression', 'unittitle', 'text(\\did.unittitle)'),
                menuItem(rule, 'expression', 'title', '\\did.unittitle.title.content'),
                menuItem(rule, 'expression', 'language', '\\did.langmaterial.language.content'),
                menuItem(rule, 'expression', 'dimensions', '\\did.physdesc.dimensions.content'),
                menuItem(rule, 'expression', 'extent', '\\did.physdesc.extent.content'),
                menuItem(rule, 'expression', 'physfacet', '\\did.physdesc.physfacet.content'),
                null, // délimiteur
                menuItem(rule, 'expression', '<strong>Scope and content</strong> (scopecontent)', 'text(\\scopecontent)'),
                menuItem(rule, 'expression', 'corpname', 'normal(\\scopecontent.p.corpname)'),
                menuItem(rule, 'expression', 'famname', 'normal(\\scopecontent.p.famname)'),
                menuItem(rule, 'expression', 'geogname', 'normal(\\scopecontent.p.geogname)'),
                menuItem(rule, 'expression', 'name', 'normal(\\scopecontent.p.name)'),
                menuItem(rule, 'expression', 'persname', 'normal(\\scopecontent.p.persname)'),
                menuItem(rule, 'expression', 'subject', 'normal(\\scopecontent.p.subject)'),
                null, // délimiteur
                menuItem(rule, 'expression', '<strong>Controlled access headings</strong> (controlaccess)', 'text(\\controlaccess)'),
                menuItem(rule, 'expression', 'corpname', 'normal(\\controlaccess.corpname)'),
                menuItem(rule, 'expression', 'famname', 'normal(\\controlaccess.famname)'),
                menuItem(rule, 'expression', 'geogname', 'normal(\\controlaccess.geogname)'),
                menuItem(rule, 'expression', 'name', 'normal(\\controlaccess.name)'),
                menuItem(rule, 'expression', 'persname', 'normal(\\controlaccess.persname)'),
                menuItem(rule, 'expression', 'subject', 'normal(\\controlaccess.subject)'),
                null, // délimiteur
                ['<strong>Fonctions</strong>', angular.noop],
                [
                    'normal',
                    function ($itemScope, $event) {
                        rule.expression = MappingRuleCtxMenuSrvc.wrapWith(rule.expression, $event, 'normal', '\\<champ>');
                    },
                ],
                [
                    'text',
                    function ($itemScope, $event) {
                        rule.expression = MappingRuleCtxMenuSrvc.wrapWith(rule.expression, $event, 'text', '\\<champ>');
                    },
                ],
                [
                    'transliterate (fonction)',
                    function ($itemScope, $event) {
                        rule.expression = MappingRuleCtxMenuSrvc.wrapWith(rule.expression, $event, 'transliterate.getFunction', '\\<champ>');
                    },
                ],
            ];
        }
        function ctxExpressionConf(rule) {
            return [];
        }

        /** Extraction des champs EAD présents dans le texte passé en paramètre */
        function getFields(text, ignorePrefix) {
            var allMatches = [];
            var eadRegexp = ignorePrefix ? new RegExp('((?:[\\w\\.]+|(?:\\{)[\\w\\.]+(?:\\})))', 'g') : new RegExp('(?<!\\\\)(?:\\\\)((?:[\\w\\.\\:]+|(?:\\{)[\\w\\.\\:]+(?:\\})))', 'g');
            var match = eadRegexp.exec(text);

            while (match !== null) {
                var field = match[1];
                allMatches.push(field);
                match = eadRegexp.exec(text);
            }
            return _.chain(allMatches).uniq().sortBy().value();
        }

        function menuItem(rule, field, label, expression) {
            return [
                label,
                function ($itemScope, $event) {
                    rule[field] = MappingRuleCtxMenuSrvc.replaceWith(rule[field], $event, expression);
                },
            ];
        }

        return service;
    }
})();
