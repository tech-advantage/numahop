(function () {
    'use strict';

    angular.module('numaHopApp.directive').directive('semDatepicker', function (gettextCatalog) {
        function resolveTemplate(tElement, tAttrs) {
            if (tAttrs['filter'] === 'true') {
                return (
                    '<div class="input-group input-group-sm datepicker-container">' +
                    '<input type="text" class="form-control" ng-model="modelDt" ng-model-options="innerModelOptions" ' +
                    'uib-datepicker-popup="dd/MM/yyyy" datepicker-append-to-body="true" ' +
                    'show-button-bar="true" close-on-date-selection="true" is-open="opened" ' +
                    'placeholder="{{::placeholder}}" ng-disabled="disabled" />' +
                    '<span class="input-group-btn">' +
                    '<button type="button" class="btn btn-default" ng-click="open($event)" ng-disabled="disabled">' +
                    '<span class="glyphicon-regular calendar"></span>' +
                    '</button>' +
                    '</span>' +
                    '</div>'
                );
            } else {
                return (
                    '<div class="input-group input-group-sm datepicker-container">' +
                    '<input type="text" class="form-control" ng-model="modelDt" ng-model-options="innerModelOptions"  ' +
                    'uib-datepicker-popup="dd/MM/yyyy" datepicker-append-to-body="true" ' +
                    'show-button-bar="true" close-on-date-selection="true" is-open="opened" ' +
                    'placeholder="{{::placeholder}}" ng-disabled="disabled" />' +
                    '<span class="input-group-btn">' +
                    '<button type="button" class="btn btn-default" ng-click="open($event)" ng-disabled="disabled">' +
                    '<span class="glyphicon-regular calendar"></span>' +
                    '</button>' +
                    '</span>' +
                    '</div>'
                );
            }
        }

        return {
            restrict: 'E',
            require: 'ngModel',
            scope: {
                model: '=ngModel',
                disabled: '=ngDisabled',
                change: '&ngChange',
                placeholder: '@semPlaceholder',
                modelOptions: '<semModelOptions',
            },
            template: resolveTemplate,
            compile: function compile(tElement, tAttrs, transclude) {
                var datepicker = tElement.find('input[uib-datepicker-popup]');
                datepicker.attr('current-text', gettextCatalog.getString("Aujourd'hui")).attr('clear-text', gettextCatalog.getString('Effacer')).attr('close-text', gettextCatalog.getString('Fermer'));

                // recopie des attributs data-* (sans le "data-")
                _.chain(tAttrs.$attr)
                    .pick(function (value, key, object) {
                        return value.substring(0, 5) === 'data-';
                    })
                    .pairs()
                    .each(function (pair) {
                        datepicker.attr(pair[0], tAttrs[pair[0]]);
                    });

                return {
                    pre: function preLink(scope, iElement, iAttrs, controllers) {
                        scope.innerModelOptions = scope.modelOptions ? scope.modelOptions : {};
                    },
                    post: function postLink(scope, iElement, iAttrs, controllers) {
                        // deregister des anciens watchers
                        //                            if(scope.watchers) {
                        //                                _.each(scope.watchers, function(w) { w(); });
                        //                            }
                        //                            scope.watchers = [];

                        // model (string) -> model (date)
                        var modelWatcher = scope.$watch('model', function (newValue, oldValue) {
                            if (newValue) {
                                scope.modelDt = moment(newValue, 'YYYY-MM-DD').toDate();
                            } else {
                                scope.modelDt = undefined;
                            }
                        });
                        //                            scope.watchers.push(modelWatcher);

                        // model (date) -> model (string)
                        var modelDtWatcher = scope.$watch('modelDt', function (newValue, oldValue) {
                            if (newValue) {
                                scope.model = moment(newValue).format('YYYY-MM-DD');

                                // onchange
                                if (angular.isFunction(scope.change)) {
                                    var newTime = angular.isDate(newValue) ? newValue.getTime() : 0;
                                    var oldTime = angular.isDate(oldValue) ? oldValue.getTime() : 0;

                                    if (newTime !== oldTime) {
                                        scope.change({ $data: scope.model });
                                    }
                                }
                            } else {
                                scope.model = '';

                                // onchange
                                if (angular.isFunction(scope.change) && !!oldValue) {
                                    scope.change({ $data: scope.model });
                                }
                            }
                        });
                        //                            scope.watchers.push(modelDtWatcher);

                        scope.open = function (event) {
                            scope.opened = true;
                        };
                    },
                };
            },
        };
    });
})();
