(function () {
    'use strict';

    angular.module('numaHopApp.service').factory('SelectionSrvc', SelectionSrvc);

    function SelectionSrvc() {
        var selections = {};

        /**
         * Ajoute une liste d'éléments à la sélection courante
         * @param {*} code
         * @param {*} selection
         */
        function add(code, selection) {
            if (!selection) {
                return;
            }
            if (!selections[code]) {
                clear(code);
            }
            selection = _.filter(selection, function (s) {
                return indexOf(code, s) < 0;
            });
            if (selection) {
                selections[code] = selections[code].concat(selection);
            }
        }

        /**
         * Vide la sélection courante
         * @param {*} code
         */
        function clear(code) {
            selections[code] = [];
        }

        /**
         * @returns Le nombre d'entités sélectionnées
         */
        function count(code) {
            return selections[code] ? selections[code].length : 0;
        }

        /**
         * Retire une liste d'éléments de la sélection courante
         * @param {*} code
         * @param {*} unselection
         */
        function del(code, unselection) {
            if (!selections[code] || !unselection) {
                clear(code);
                return;
            }
            _.each(unselection, function (u) {
                var idx = indexOf(code, u);
                if (idx >= 0) {
                    selections[code].splice(idx, 1);
                }
            });
        }

        /**
         * Retourne la sélection courante
         * @param {*} code
         */
        function get(code) {
            if (!selections[code]) {
                clear(code);
            }
            return selections[code];
        }

        /**
         * Position d'un élément dans la liste de sélection
         * @param {*} code
         * @param {*} element
         */
        function indexOf(code, element) {
            var idx = -1;
            _.find(selections[code], function (s, i) {
                if (s.identifier === element.identifier) {
                    idx = i;
                    return true;
                }
                return false;
            });
            return idx;
        }

        /**
         * Initialise la sélection courante avec une liste d'éléments
         * @param {*} code
         * @param {*} selection
         */
        function set(code, selection) {
            clear(code);

            if (selection) {
                selections[code] = selections[code].concat(selection);
            }
        }

        /**
         * Inverse la sélection de l'élément
         * @param {*} code
         * @param {*} element
         */
        function toggle(code, element) {
            if (!selections[code]) {
                clear(code);
            }
            var idx = indexOf(code, element);
            if (idx >= 0) {
                selections[code].splice(idx, 1);
            } else {
                selections[code].push(element);
            }
        }

        /**
         * Tous les éléments passés en paramètres sont-ils sélectionnés ?
         * @param {*} code
         * @param {*} elements
         */
        function allSelected(code, elements) {
            if (!selections[code]) {
                return false;
            }
            if (!elements) {
                return true;
            }
            return _.every(elements, function (e) {
                return isSelected(code, e);
            });
        }

        /**
         * L'élément passé en paramètre est-il sélectionné ?
         * @param {*} code
         * @param {*} element
         */
        function isSelected(code, element) {
            return selections[code] && indexOf(code, element) >= 0;
        }

        return {
            add: add,
            allSelected: allSelected,
            clear: clear,
            count: count,
            del: del,
            get: get,
            isSelected: isSelected,
            keys: {
                CHECK_LIST: 'checks_checks',
                DOC_UNIT_LIST: 'doc_unit_list',
            },
            set: set,
            toggle: toggle,
        };
    }
})();
