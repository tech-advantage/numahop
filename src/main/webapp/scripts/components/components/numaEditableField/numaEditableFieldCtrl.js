(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('NumaEditableFieldCtrl', NumaEditableFieldCtrl);

    function NumaEditableFieldCtrl($parse, ErreurSrvc, gettextCatalog) {
        var ctrl = this;
        ctrl.getDisplayModel = getDisplayModel;
        ctrl.getMessage = getMessage;
        ctrl.getTooltip = getTooltip;
        ctrl.onchange = onchange;
        ctrl.onchangeUiselect = onchangeUiselect;

        init();

        function init() {
            if (angular.isUndefined(ctrl.placeholder)) {
                ctrl.placeholder = " ";
            }
            if (angular.isUndefined(ctrl.defaultText)) {
                ctrl.defaultText = gettextCatalog.getString("Non renseigné");
            }
            ctrl.currentModel = ctrl.model;
        }

        /**
         * Génération du libellé affiché quand le champ xeditable est fermé
         * (utilisé pour les listes)
         * 
         */
        function getDisplayModel() {
            var display;
            if (angular.isDefined(ctrl.model) && ctrl.model !== null) {

                if (angular.isArray(ctrl.model)) {
                    display = _.chain(ctrl.model)
                        .map(function (value) {
                            return getDisplayValue(value, ctrl.config);
                        })
                        .filter(function (value) {
                            return !!value;
                        })
                        .reduce(function (a, b) {
                            return a ? a + ", " + b : b;
                        }, false)
                        .value();
                }
                else {
                    display = getDisplayValue(ctrl.model, ctrl.config);
                }
            }
            return display || ctrl.defaultText;
        }

        function getDisplayValue(value, config) {
            var display = "";

            // affichage du champ
            if (config.display) {
                display = value[config.display];
            }
            // affichage par une fonction
            else if (config.displayFn) {
                display = config.displayFn(value);
            }
            // affichage du modèle
            else {
                display = value;
            }
            return display;
        }

        /**
         * Génération du message d'erreur
         * 
         * @returns 
         */
        function getMessage() {
            if (ctrl.errors && !ctrl.errMsg) {
                var pErrors = $parse(ctrl.errors)();
                ctrl.errMsg = _.reduce(pErrors, function (a, b) {
                    var errMsg = ErreurSrvc.getMessage(b) || b;
                    return a.length === 0 ? errMsg : a + "<br/>" + errMsg;
                }, "");
            }
            if (!ctrl.errors) {
                ctrl.errMsg = "";
            }
            return ctrl.errMsg;
        }

        /**
         * Tooltip
         */
        function getTooltip() {
            return ctrl.tooltipFn({ model: ctrl.currentModel }) || ctrl.tooltip;
        }

        /**
         * Wrapper de onchange
         */
        function onchange(data) {
            ctrl.currentModel = data;
            return ctrl.numaOnchange({ $data: data });
        }

        /**
         * Wrapper de onchange pour uiselect
         */
        function onchangeUiselect(data) {
            var fn = onchange();
            if (angular.isFunction(fn)) {
                fn(data);
            }
        }
    }
})();