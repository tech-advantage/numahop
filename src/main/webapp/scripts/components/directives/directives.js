(function () {
    "use strict";

    angular.module('numaHopApp.directive')
        .directive('semDefinition', function (CatalogingUtils) {
            var setIcons = function (values, authName) {
                if (authName === "language" || authName === "originalLanguage") {
                    for (var i = 0; i < values.length; i++) {
                        switch (values[i].code.toLowerCase()) {
                            case "eng":
                                values[i].icon = "famfamfam-flag-gb";
                                break;
                            case "fre":
                                values[i].icon = "famfamfam-flag-fr";
                                break;
                            case "ita":
                                values[i].icon = "famfamfam-flag-it";
                                break;
                        }
                    }
                }
            };
            return {
                restrict: 'E',
                scope: {
                    term: '@',
                    description: '=',
                    authvalue: "&",
                    dyndescription: '&'
                },
                template: '<dt ng-if="value.length > 0 && term">{{::term}}</dt>\n\
<dd ng-if="value.length > 0" ng-repeat="(index, desc) in value track by index">\n\
    <span ng-if="desc.icon" class="{{::desc.icon}}" style="margin-right: 5px;"></span><span>{{::desc.value || desc}}</span>\n\
</dd>',
                link: {
                    pre: function preLink(scope, iElement, iAttrs, controller) {
                        if (angular.isDefined(iAttrs.description)) {
                            scope.$watch('description', function (description) {
                                if (angular.isArray(description)) {
                                    scope.value = description;
                                } else {
                                    if (description) {
                                        scope.value = [description];
                                    } else {
                                        scope.value = [];
                                    }
                                }
                            });
                        } else if (angular.isDefined(iAttrs.authvalue)) {
                            scope.$watch('authvalue', function (authvalueFn) {
                                var authvalue = authvalueFn();
                                if (angular.isDefined(authvalue)) {
                                    scope.value = CatalogingUtils.getAuthorisedValue(authvalue.element,
                                        authvalue.field,
                                        authvalue.reference);
                                    setIcons(scope.value, authvalue.field);
                                }
                            });
                        }
                    }
                }
            };
        })
        .directive('semEditDefinition', function () {
            var getElement = function (argComponent) {
                var component = angular.isDefined(argComponent) ? argComponent : "text";
                switch (component) {
                    case "textarea":
                        component = "textarea";
                        break;
                    default:
                        component = "input type='" + component + "'";
                        break;
                }
                var html = '<' + component + ' class="form-control" ng-model="model[index]" />';
                return angular.element(html);
            };
            return {
                restrict: 'E',
                scope: {
                    term: '@',
                    model: '=',
                    component: '@',
                    multiple: '@'
                },
                template: '<dt>{{term}}</dt>\n\
<dd ng-if="!isMultiple" ng-init="index=0">\n\
    <div class="input-group">\n\
        <span class="input-group-btn">\n\
            <a class="btn btn-sem1" title="Réinitialiser le champ {{term}}" ng-click="model.splice(index, 1)">\n\
                <span class="glyphicon-regular remove-2"></span>\n\
            </a>\n\
        </span>\n\
    </div>\n\
</dd>\n\
<dd ng-if="isMultiple" ng-repeat="(index, item) in model track by index">\n\
    <div class="input-group">\n\
        <span class="input-group-btn">\n\
            <a class="btn btn-sem1" title="Supprimer un {{term}}" ng-click="model.splice(index, 1)">\n\
                <span class="glyphicon-halflings glyphicon-trash"></span>\n\
            </a>\n\
        </span>\n\
    </div>\n\
</dd>\n\
<dd ng-if="isMultiple">\n\
    <a class="btn btn-sem1" title="Ajouter un {{term}}" ng-click="model.push(\'\')">\n\
        <span class="glyphicon-halflings glyphicon-plus" />\n\
    </a>\n\
</dd>',
                compile: function compile(tElement, tAttrs, transclude) {
                    tElement.find("dd>div.input-group").prepend(getElement(tAttrs.component));

                    return {
                        pre: function preLink(scope, iElement, iAttrs, controller) {
                            scope.isMultiple = angular.isDefined(iAttrs.multiple);
                            scope.$watch("model", function (model) {
                                if (angular.isUndefined(model)) {
                                    scope.model = [];
                                } else if (!angular.isArray(model)) {
                                    scope.model = [model];
                                }
                            });
                        },
                        post: function postLink(scope, iElement, iAttrs, controller) {
                        }
                    };
                }
            };
        });
})();