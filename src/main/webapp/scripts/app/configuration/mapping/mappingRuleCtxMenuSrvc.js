(function () {
    'use strict';

    angular.module('numaHopApp.service')
        .factory('MappingRuleCtxMenuSrvc', MappingRuleCtxMenuSrvc);

    function MappingRuleCtxMenuSrvc() {
        var service = {};
        service.replaceWith = replaceWith;
        service.wrapWith = wrapWith;

        /** 
         * Menu contextuel: insertion d'une fonction autour de la sélection courante
         * 
         * -> fnPrefix: nom de la fonction, sans parenthèse ouvrante si fnSuffix n'est pas défini
         * -> defaultFnArgs: arguments de la fonction insérée, pour informer l'utilisateur des paramètres attendus
         * -> fnSuffix: suffixe ajouté à la fin de l'appel inséré, généralement une parenthèse fermante
         */
        function wrapWith(modelValue, $event, fnPrefix, defaultFnArgs, fnSuffix) {
            // paramètres par défaut
            if (!fnSuffix) {
                fnPrefix += "(";
                fnSuffix = ")";
            }
            if (!defaultFnArgs) {
                defaultFnArgs = "";
            }

            // Sélection
            var start = $event.target ? $event.target.selectionStart : modelValue.length;
            var end = $event.target ? $event.target.selectionEnd : modelValue.length;

            if(!modelValue) {
                modelValue = "";
            }
            // le curseur est à la fin de la sélection
            if (start === modelValue.length) {
                modelValue += fnPrefix + defaultFnArgs + fnSuffix;
            }
            // il n'y a pas de sélection 
            else if (start === end) {
                modelValue = modelValue.substring(0, start) + fnPrefix + defaultFnArgs + fnSuffix + modelValue.substring(start);
            }
            // du texte est sélectionné: on le wrap dans la fonction
            else {
                modelValue = modelValue.substring(0, start)
                    + fnPrefix
                    + modelValue.substring(start, end)
                    + fnSuffix
                    + modelValue.substring(end);
            }
            return modelValue;
        }
        /**
         * Menu contextuel: insertion d'une expression à la place de la sélection courante
         */
        function replaceWith(modelValue, $event, expression) {
            // Sélection
            var start = $event.target ? $event.target.selectionStart : modelValue.length;
            var end = $event.target ? $event.target.selectionEnd : modelValue.length;

            if(!modelValue) {
                modelValue = "";
            }
            // le curseur est à la fin de la sélection
            if (start === modelValue.length) {
                modelValue += expression;
            }
            else {
                modelValue = modelValue.substring(0, start) + expression + modelValue.substring(end);
            }
            return modelValue;
        }
        return service;
    }
})();
