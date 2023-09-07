(function () {
    'use strict';

    angular.module('numaHopApp.service').factory('LockSrvc', LockSrvc);

    function LockSrvc() {
        var service = {};
        service.applyOnCtrl = applyOnCtrl;
        service.applyOnScope = applyOnScope;

        /**
         * Renvoie les fonctions de verrouillage / déverrouillage.
         * La ressource verrouillée doit définir les 2 fonctions $lock et $unlock.
         *
         * @param {any} scope scope sur lequel est placé le listener, et est écouté $destroy
         * @param {any} form  formulaire xeditable de l'objet édité
         * @returns
         */
        function applyOnScope(scope, form) {
            scope.lock = lockFn(scope, scope, form);
            scope.unlock = unlockFn(scope);
        }

        /**
         * Renvoie les fonctions de verrouillage / déverrouillage.
         * La ressource verrouillée doit définir les 2 fonctions $lock et $unlock.
         *
         * @param {any} ctrl controlleur (ou scope) sur lequel est placé le listener
         * @param {any} scope écoute de $destroy, accès au formulaire xeditable
         * @param {any} form  formulaire xeditable de l'objet édité
         * @returns
         */
        function applyOnCtrl(ctrl, scope, form) {
            ctrl.lock = lockFn(ctrl, scope, form);
            ctrl.unlock = unlockFn(ctrl);
        }

        /**
         * Verrouillage de l'unité documentaire, à placer sur le onshow du formulaire xeditable, par ex. onshow="lock(docUnit)"
         *
         * @param {any} object
         */
        function lockFn(ctrl, scope, form) {
            return function (object) {
                if (object && object.identifier) {
                    object
                        .$lock()
                        .then(function () {
                            ctrl._lockListener = scope.$on('$destroy', function () {
                                unlockFn(ctrl)(object);
                            });
                        })
                        .catch(function (err) {
                            if (scope[form]) {
                                scope[form].$cancel(); // on force la fermeture du formulaire
                            }
                        });
                }
            };
        }

        /**
         * Suppression du verrou sur l'unité documentaire,
         * à appeler lors de la sauvegarde et l'annulation du formulaire xeditable
         *
         * @param {*} object
         */
        function unlockFn(ctrl) {
            return function (object) {
                if (ctrl._lockListener) {
                    if (object) {
                        object.$unlock();
                    }
                    ctrl._lockListener(); // remove listener
                    delete ctrl._lockListener;
                }
            };
        }

        return service;
    }
})();
