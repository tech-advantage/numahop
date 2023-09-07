(function () {
    'use strict';

    angular.module('numaHopApp.directive').directive('semEditableField', function ($timeout, StringTools, NumahopUrlService) {
        /**  Ajouter le paramètre param à un appel de fonction simple, qui ne contient aucun paramètre   */
        var addFnParam = function (fnCall, param) {
            if (angular.isString(fnCall) && fnCall !== '') {
                var noParam = angular.isUndefined(param);

                if (fnCall.indexOf('(') < 0) {
                    return noParam ? fnCall + '()' : fnCall + '(' + param + ')';
                }
                if (!noParam && fnCall.indexOf('()') >= 0) {
                    return fnCall.replace(/\(\)/g, '(' + param + ')');
                }
            }
            return fnCall;
        };

        return {
            restrict: 'E',
            transclude: true,
            scope: {
                fieldclass: '@semFieldClass',
                labelclass: '@semLabelClass',
                tooltip: '@semTooltip',
                error: '@semServerError',
                hideIfEmpty: '@semHideIfEmpty',
                label: '@semLabel',
                model: '=semModel',
                onaftersave: '&onaftersave',
                onbeforesave: '&onbeforesave',
                onchange: '&semOnchange',
                ondelete: '&ondelete',
                onadd: '&onadd',
                options: '=semSelectOptions',
                // pour les select, désactivation d'une entrée:
                //      sem-option-disabled="myElementIsDisabled(element, someOtherValue)"
                //      e-ng-options="e.label disable when optionDisabled({element: e}) for e in options track by e.identifier"
                optionDisabled: '&semOptionDisabled',
                readonly: '=ngReadonly',
                refForm: '=semForm',
                type: '@semType',
                showadd: '=showadd',
                showdelete: '=showdelete',
                removable: '=semRemovable',
                mandatory: '<',
                displayProperty: '@semDisplayProperty',
                // pour ajouter des liens
                linkType: '@semLinkType',
                linkParameters: '=semLinkParameters',
                // URL liens
                urlLink: '=semUrl',
                // mailto
                mailLink: '=semMail',
                // gestion placeholder personnalisable
                placeholder: '@semPlaceholder',
            },
            templateUrl: function (tElement, tAttrs) {
                switch (tAttrs.semType) {
                    case 'checkbox':
                        return '/scripts/components/directives/semEditableField/template-checkbox.html';
                    case 'select':
                        return '/scripts/components/directives/semEditableField/template-select.html';
                    case 'semradiolist':
                        return '/scripts/components/directives/semEditableField/template-select.html';
                    case 'select2':
                        return '/scripts/components/directives/semEditableField/template-select2.html';
                    case 'uiselect':
                    case 'ui-select':
                        tAttrs.semType = 'uiselect';
                        return '/scripts/components/directives/semEditableField/template-uiselect.html';
                    default:
                        return '/scripts/components/directives/semEditableField/template-default.html';
                }
            },
            controller: 'SemEditableFieldCtrl',
            require: ['?^semEditableForm', '?^semEditableBlock'],
            compile: function compile(tElement, tAttrs, transclude) {
                // champ principal, xeditable
                var modelElt = tElement.find('[main-field]');

                // extraction du champ xeditable en mode unwrap
                if (angular.isDefined(tAttrs.semUnwrap)) {
                    tElement.children().remove();
                    tElement.append(modelElt);
                }
                // cacher le champ si vide
                if (angular.isDefined(tAttrs.semHideIfEmpty)) {
                    tAttrs.semHideIfEmpty = 'true';
                }

                modelElt
                    .attr('editable-' + tAttrs.semType, 'model')
                    // mode readonly géré comme décrit dans https://github.com/vitalets/angular-xeditable/issues/73
                    .attr('ng-click', 'showEditableForm()')
                    // si le champ est dans un formulaire en mode edit: disabled
                    .attr('e-ng-disabled', 'readonly');

                if (tAttrs.semType !== 'select') {
                    if (angular.isDefined(tAttrs.semPlaceholder)) {
                        modelElt.attr('e-placeholder', tAttrs.semPlaceholder);
                    } else {
                        modelElt.attr('e-placeholder', tAttrs.semLabel);
                    }
                }

                // onbeforesave pour la validation
                if (angular.isDefined(tAttrs.onbeforesave)) {
                    tAttrs.onbeforesave = addFnParam(tAttrs.onbeforesave, '$data');
                }

                // le champ fait partie d'un formulaire => le aftersave est géré au niveau du formulaire
                /* Note: on regarde $$destroyed sur le formulaire, parce qu'il est possible qu'un ancien formulaire
                 *       en cours de destruction existe à cet endroit (lors du passage du mode edit au mode vue par ex.,
                 *       les animations sur ng-view font que 2 blocs ng-view coexistent momentanément)
                 */
                var semFormElt;
                if (angular.isDefined(tAttrs.semForm)) {
                    semFormElt = angular.element("form[name='" + tAttrs.semForm + "']");
                }
                if (angular.isDefined(semFormElt) && semFormElt.length > 0 && !semFormElt.scope().$$destroyed) {
                    modelElt.attr('e-form', 'refForm');
                    delete tAttrs.onaftersave;
                } else {
                    // définition d'un e-form, utilisé pour la gestion du mode readonly
                    tAttrs.semForm = 'innerForm';
                    modelElt.attr('e-form', tAttrs.semForm);

                    //  onaftersave au niveau du champ si il ne fait pas partie d'un formulaire
                    if (angular.isDefined(tAttrs.onaftersave)) {
                        tAttrs.onaftersave = addFnParam(tAttrs.onaftersave);
                    }
                }

                // onchange
                if (angular.isDefined(tAttrs.semOnchange)) {
                    tAttrs.semOnchange = addFnParam(tAttrs.semOnchange, '$data');
                }

                // ajout du keypress pour gérer la touche Entrée, sauf sur les textarea
                if (tAttrs.semType !== 'textarea') {
                    modelElt.attr('e-ng-keypress', 'onKeyPressed({event: $event})');
                }

                // before/after save sur les wrapper

                modelElt.attr('onbeforesave', 'beforesaveWrapper($data)');
                modelElt.attr('onaftersave', 'aftersaveWrapper()');
                modelElt.attr('oncancel', 'oncancelWrapper()');
                modelElt.attr('e-ng-change', 'onchangeWrapper($data)');

                // recopie des attributs xeditable (e-***) dans le champ éditable
                var attrObj = _.pick(tAttrs.$attr, function (value, key, object) {
                    return value.substring(0, 2) === 'e-' && value !== 'e-form';
                });
                _.each(_.pairs(attrObj), function (pair) {
                    if (pair[1] === 'e-placeholder' && StringTools.isBlank(tAttrs[pair[0]])) {
                        modelElt.removeAttr(pair[1]);
                    } else {
                        modelElt.attr(pair[1], tAttrs[pair[0]]);
                    }
                });
                // css
                modelElt.attr('ng-class', "{'editable editable-click': !readonly}");

                // postLink
                return {
                    post: function (scope, iElement, iAttrs, controllers) {
                        var editableFormCtrl = controllers[0];
                        var editableBlockCtrl = controllers[1];

                        // Enregistrement du champ
                        if (editableBlockCtrl !== null) {
                            editableBlockCtrl.registerField(iElement);
                        }

                        // Annulation du sem-formulaire
                        scope.formCancelAll = editableFormCtrl !== null && angular.isFunction(editableFormCtrl.cancelAll) ? editableFormCtrl.cancelAll : angular.noop;

                        // Soumission du formulaire
                        scope.onKeyPressed = function (event) {
                            if (editableFormCtrl === null) {
                                return;
                            }
                            if (event.event.keyCode === 13 || event.event.which === 13) {
                                editableFormCtrl.onSubmit(event.event);
                            }
                        };

                        $timeout(function () {
                            scope.isModeEdition = editableFormCtrl !== null ? editableFormCtrl.isModeEdition() : false;

                            // mise à jour du modèle interne de xeditable ($data) à partir du modèle du champ sem-field
                            if (scope.isModeEdition) {
                                var editableScope = iElement.find('[main-field]').scope();
                                scope.$watch('model', function (newValue, oldValue) {
                                    // le modèle a été modifié depuis l'extérieur
                                    if (editableScope.$data !== newValue) {
                                        editableScope.$editable.setLocalValue();
                                    }
                                });
                            }
                            // Gestion des liens
                            if (angular.isDefined(scope.linkType) && angular.isDefined(scope.linkParameters)) {
                                scope.$watch('linkParameters', function (newValue, oldValue) {
                                    scope.entityLink = NumahopUrlService.getUrlForTypeAndParameters(scope.linkType, newValue);
                                });
                            }
                        });
                    },
                };
            },
        };
    });
})();
