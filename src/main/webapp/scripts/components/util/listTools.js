(function () {
    'use strict';

    angular.module('numaHopApp.utils').factory('ListTools', ListTools);

    function ListTools() {
        /**
         * Enlève un élément d'une liste à partir d'un attribut donné (ou basé sur une égalité stricte si non précisé)
         * Un élément de remplacement peut être défini
         */
        var removeBasedOnAttribute = function (toBeRemoved, list, attribute, replaceWith) {
            // Recherche de correspondance
            var findItem;
            if (angular.isString(attribute)) {
                // recherche sur l'attribut
                findItem = function (item) {
                    return item[attribute] === toBeRemoved[attribute];
                };
            } else {
                // recherche d'égalité stricte sur l'élément
                findItem = function (item) {
                    return item === toBeRemoved;
                };
            }
            // Index de correspondance
            var index = -1;
            // Recherche de l'élément à supprimer / remplacer
            var found = _.find(list, function (item, i) {
                if (findItem(item)) {
                    index = i;
                    return true;
                } else {
                    return false;
                }
            });
            if (angular.isDefined(found)) {
                if (angular.isDefined(replaceWith)) {
                    list.splice(index, 1, replaceWith);
                } else {
                    list.splice(index, 1);
                }
            }
            return index;
        };
        /**
         * Enlève un élément d'une liste à partir de son identifiant
         * Un élément de remplacement peut être défini
         */
        var findAndRemoveItemFromList = function (toBeRemoved, list, replaceWith) {
            var index = removeBasedOnAttribute(toBeRemoved, list, 'identifier', replaceWith);
            return index >= 0;
        };
        /**
         *  Enlève un élément de plusieurs listes à partir de son identifiant
         */
        var findAndRemoveItemFromLists = function (toBeRemoved) {
            var i = 1;
            for (i = 1; i < arguments.length; i++) {
                var list = arguments[i];
                findAndRemoveItemFromList(toBeRemoved, list);
            }
        };

        return {
            findAndRemoveItemFromList: findAndRemoveItemFromList,
            findAndRemoveItemFromLists: findAndRemoveItemFromLists,
            removeBasedOnAttribute: removeBasedOnAttribute,
        };
    }
})();
