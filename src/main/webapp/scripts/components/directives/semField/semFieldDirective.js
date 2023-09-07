(function () {
    'use strict';

    /**
     * @name sem-field
     * @description
     * Champ de saisie préformatté, gérant entre autre les messages de validation
     *
     *  @param onbeforesave: function exécutée lors de la validation. Si un résultat est retourné,
     *  le champ et considéré comme non validé, et un message d'erreur est affiché.
     *  @param sem-register-validation: tableau dans lequel le sem-field enregistre sa fonction de validation,
     *  afin qu'elle puisse être appelée par la suite pour gérer la validation globale du formulaire
     *
     * @example
     *      <sem-field sem-type="text" sem-label="Test" sem-model="test.value"
     *                 sem-tooltip="Type" ng-readonly="isReadOnly" sem-errors="{{errors}}"
     *                 onbeforesave="validation" sem-register-validation="fieldValidations"
     *                 sem-onchange="log()"></sem-field>
     *
     * Validation du formulaire:
     *   $scope.validateForm = function() {
     *       var promises = _.map($scope.fieldValidations, function(fieldValidation) {
     *           return fieldValidation();
     *       });
     *       return $q.all(promises).then(function(results) {
     *           return _.every(results, function(result) { return !result; });
     *       });
     *   };
     */
    angular.module('numaHopApp.directive').directive('semField', semField);

    function semField($compile, $log, $timeout) {
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
        /** Initialisation d'un champ Ui-Select */
        function renderUiSelect(modelElt, optionData) {
            if (angular.isUndefined(optionData)) {
                $log.error('Attribute optionData not found');
                return;
            }

            //            <ui-select main-field theme="select2" ng-model="model" ng-disabled="readonly" append-to-body="true">
            //                <ui-select-match></ui-select-match>
            //                <ui-select-choices>
            //                    <span ng-bind-html="getText(d) | highlight: $select.search"></span>
            //                </ui-select-choices>
            //            </ui-select>
            modelElt.append(
                '<ui-select class="sem-filter" theme="select2" ng-model="wrapper.model" ng-disabled="readonly" append-to-body="true" ' +
                    'on-select="onchangeWrapper(wrapper.model)" on-remove="onchangeWrapper(wrapper.model)">' +
                    '<ui-select-match></ui-select-match>' +
                    '<ui-select-choices>' +
                    '<span ng-bind-html="getText(d) | highlight: $select.search"></span>' +
                    '</ui-select-choices>' +
                    '</ui-select>'
            );
            var uiSelect = modelElt.find('ui-select');
            var uiSelectChoices = uiSelect.find('ui-select-choices');
            var uiSelectMatch = uiSelect.find('ui-select-match');
            var isAsync = angular.isDefined(optionData.refresh);

            // Liste de choix
            var repeat = 'd in optionData.data';
            if (angular.isDefined(optionData.orderBy)) {
                repeat += " | orderBy: '" + optionData.orderBy + "'";
            }
            var trackByField = optionData.trackby || 'identifier';

            var filterExpr = isAsync || optionData.filter ? 'filterData' : angular.isFunction(optionData.text) ? '$select.search' : '{' + optionData.text + ': $select.search}';
            repeat += ' | filter: ' + filterExpr + " track by d['" + trackByField + "']";

            uiSelectChoices.attr('repeat', repeat);

            // async
            if (isAsync) {
                uiSelectChoices.attr('refresh', 'refreshData($select)');

                if (angular.isDefined(optionData['refresh-delay'])) {
                    uiSelectChoices.attr('refresh-delay', optionData['refresh-delay']);
                }
            }
            // désactivation d'une entrée
            if (angular.isDefined(optionData['disable-choice'])) {
                uiSelectChoices.attr('ui-disable-choice', "optionData['disable-choice'](d)");
            }
            // regroupement des entrées
            if (angular.isDefined(optionData.groupby)) {
                uiSelectChoices.attr('group-by', 'optionData.groupby');
            }
            // placeholder
            if (angular.isDefined(optionData.placeholder)) {
                uiSelectMatch.attr('placeholder', optionData.placeholder);
            }
            // allow-clear
            if (angular.isDefined(optionData['allow-clear'])) {
                uiSelectMatch.attr('allow-clear', optionData['allow-clear']);
            }
            // Sélection multiple ?
            if (optionData.multiple) {
                uiSelect.attr('multiple', '');
                uiSelectMatch.text('{{getText($item)}}');
            } else {
                uiSelectMatch.text('{{getText($select.selected)}}');
            }
        }

        return {
            restrict: 'E',
            scope: {
                errors: '@semErrors',
                fieldclass: '@semFieldClass',
                label: '@semLabel',
                labelclass: '@semLabelClass',
                model: '=semModel',
                modelOptions: '<semModelOptions',
                onbeforesave: '&onbeforesave',
                onchange: '&semOnchange',
                options: '@semSelectOptions',
                optionData: '=semOptionData',
                readonly: '=ngReadonly',
                register: '=semRegisterValidation',
                tooltip: '@semTooltip',
                type: '@semType',
                step: '@semStep',
                min: '@semMin',
                max: '@semMax',
                offline: '=semOffline',
                placeholder: '@semPlaceholder',
                semAutofocus: '@semAutofocus',
                precision: '@semPrecision',
                mandatory: '<',
            },
            templateUrl: function (tElement, tAttrs) {
                var url = '';
                switch (tAttrs.semType) {
                    case 'checkbox':
                        url = '/scripts/components/directives/semField/template-checkbox.html';
                        break;
                    case 'datepicker':
                        url = '/scripts/components/directives/semField/template-datepicker.html';
                        break;
                    case 'select':
                        url = '/scripts/components/directives/semField/template-select.html';
                        break;
                    case 'semradiolist':
                        url = '/scripts/components/directives/semField/template-select.html';
                        break;
                    case 'select2':
                        url = '/scripts/components/directives/semField/template-select2.html';
                        break;
                    case 'textarea':
                        url = '/scripts/components/directives/semField/template-textarea.html';
                        break;
                    case 'uiselect':
                    case 'ui-select':
                        tAttrs.semType = 'uiselect';
                        url = '/scripts/components/directives/semField/template-uiselect.html';
                        break;
                    default:
                        url = '/scripts/components/directives/semField/template-default.html';
                }
                if (tAttrs.semOffline === 'true' || tAttrs.semOffline === true) {
                    url = '/offline' + url;
                }
                return url;
            },
            controller: 'SemFieldCtrl',
            terminal: true,
            compile: function compile(tElement, tAttrs, transclude) {
                // cacher le champ si vide
                if (angular.isDefined(tAttrs.semHideIfEmpty)) {
                    tAttrs.semHideIfEmpty = 'true';
                }
                // onchange
                if (angular.isDefined(tAttrs.semOnchange)) {
                    tAttrs.semOnchange = addFnParam(tAttrs.semOnchange, 'value');
                }
                // validation
                if (angular.isDefined(tAttrs.onbeforesave)) {
                    tAttrs.onbeforesave = addFnParam(tAttrs.onbeforesave, 'value');
                }
                // wrapper
                if (tAttrs.semType !== 'uiselect') {
                    var modelElt = tElement.find('[main-field]');

                    if (tAttrs.semType === 'datepicker') {
                        modelElt.attr('ng-change', 'onchangeWrapper($data)');
                    } else {
                        modelElt.attr('ng-change', 'onchangeWrapper(model)');
                    }
                }
                // postLink
                return {
                    pre: function preLink(scope, iElement, iAttrs, controllers) {
                        scope.innerModelOptions = scope.modelOptions ? scope.modelOptions : {};
                    },
                    post: function postLink(scope, iElement, iAttrs, controllers) {
                        // initialisation uiselect
                        if (scope.type === 'uiselect') {
                            var modelElt = iElement.find('[main-field]');
                            renderUiSelect(modelElt, scope.optionData);

                            // wrapper pour résoudre le pb de mise à jour du modèle
                            scope.wrapper = {
                                model: scope.model,
                            };
                            // liens entre le wrapper et le modèle
                            scope.$watch('wrapper.model', function (value) {
                                scope.model = value;
                            });
                            scope.$watch('model', function (value) {
                                scope.wrapper.model = value;
                            });
                        }
                        if (iAttrs.semAutofocus) {
                            scope.$watch('semAutofocus', function (value) {
                                if (value) {
                                    $timeout(function () {
                                        iElement.find('[main-field]').focus();
                                    });
                                }
                            });
                        }

                        // Cette étape de compilation est nécessaire pour le composant ui-select
                        // Elle bogue avec le composant select, en doublant les options
                        if (tAttrs.semType === 'uiselect') {
                            $compile(iElement.children())(scope);
                        }
                    },
                };
            },
        };
    }
})();
