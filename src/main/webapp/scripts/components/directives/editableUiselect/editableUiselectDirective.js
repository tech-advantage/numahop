(function () {
    "use strict";

    /**
     *  UiSelect, version classique
     *      <div class="row vertical-align">
     *          <label class="text-right col-sm-4">ui-select</label>
     *          <div class="model-container col-sm-8">
     *              <ui-select ng-model="model.value">
     *                  <ui-select-match placeholder="model.value">{{$select.selected.label}}</ui-select-match>
     *                  <ui-select-choices repeat="d in data | filter: {label: $select.search}">
     *                      <span ng-bind-html="d.label | highlight: $select.search"></span>
     *                  </ui-select-choices>
     *              </ui-select>
     *          </div>
     *      </div>
     *
     *  UiSelect, version xeditable
     *      <div class="row vertical-align">
     *          <label class="text-right col-sm-4">xeditable</label>
     *          <div class="model-container col-sm-8">
     *              <span editable-uiselect="model.value" e-options="options">{{model.value.label}}</span>
     *          </div>
     *      </div>
     *
     * UiSelect, version sem-editable-field
     *      <sem-editable-field sem-label="sem-field" sem-type="uiselect" sem-model="model.value"
     *                          sem-select-options="options" ng-readonly="isReadOnly"></sem-editable-field>
     *
     *
     * Avec la configuration suivante:
     *
     * $scope.data = [{id: 1, label: "Paris"}, {id: 2, label: "Marseille"}, {id: 3, label: "Lyon"},
     *                {id: 4, label: "Toulouse"}, {id: 5, label: "Nice"}, {id: 6, label: "Nantes"},
     *                {id: 7, label: "Strasbourg"}, {id: 8, label: "Montpellier"},
     *                {id: 9, label: "Bordeaux"}, {id: 10, label: "Lille"}];
     *  $scope.options = {
     *      data: $scope.data,          // données de la liste déroulante
     *      text: "label",              // champ (ou fonction) contenant la donnée à afficher
     *      multiple: true,             // Choix simple ou multiple (defaut: false)
     *      allow-clear: true,          // Autoriser le vidage du champ, dans le cas d'un choix simple (défaut: false)
     *      refresh:                    // fonction: chargement asynchrone des données, retourne la liste de résultats
     *      refresh-delay:              // délai min entre 2 appels asynchrones
     *      disable-choice:             // expression définissant quand une entrée est désactivée
     *   };
     */
    angular.module('numaHopApp.directive')
        .directive('editableUiselect',
            function (editableDirectiveFactory) {
                return editableDirectiveFactory({
                    directiveName: 'editableUiselect',
                    inputTpl: '<ui-select theme="select2" style="min-width:150px" ng-disabled="readonly()">' +
                        '<ui-select-match></ui-select-match>' +
                        '<ui-select-choices>' +
                        '<span ng-bind-html="getText(d) | highlight: $select.search"></span>' +
                        '</ui-select-choices>' +
                        '</ui-select>',
                    addListeners: function () {
                        var myself = this;

                        if (myself.scope.options.multiple) {
                            myself.scope.innerData = angular.copy(myself.scope.$data);
                            myself.scope.eventFlag = false;
                        }
                        /* sélection simple */
                        myself.scope.updateItem = function ($item, $model) {
                            myself.scope.$data = $model;

                            if (myself.scope.numaOnchange) {
                                myself.scope.numaOnchange($model);
                            }
                        };
                        /* sélection multiple */
                        myself.scope.addItem = function ($item, $model) {
                            initEvent();
                            if (getIndex(myself.scope.innerData, $model) < 0) {
                                myself.scope.innerData.push($model);

                                if (myself.scope.numaOnchange) {
                                    myself.scope.numaOnchange($model);
                                }
                            }
                        };
                        myself.scope.removeItem = function ($item, $model) {
                            initEvent();
                            var idx = getIndex(myself.scope.innerData, $model);
                            if (idx >= 0) {
                                myself.scope.innerData.splice(idx, 1);

                                if (myself.scope.numaOnchange) {
                                    myself.scope.numaOnchange();
                                }
                            }
                        };
                        /* Lecture seule */
                        myself.scope.readonly = function () {
                            return !!myself.scope.numaReadonly;
                        };
                        /* chargement async */
                        myself.scope.refreshData = function (select) {
                            var refresh = myself.scope.options.refresh(select);
                            var promise = refresh.$promise ? refresh.$promise : refresh;
                            return promise.then(function (result) {
                                var values = angular.isArray(result) ? result : angular.isArray(result.content) ? result.content : [];
                                // data = values, sans les éléments déjà sélectionnés, trié
                                myself.scope.options.data = _.chain(values)
                                    .reject(function (d) {
                                        return _.find(myself.scope.innerData, function (innerd) {
                                            return innerd.identifier === d.identifier;
                                        });
                                    })
                                    .sortBy(myself.scope.getText)
                                    .value();
                            });
                        };
                        /* Affichage */
                        myself.scope.getText = function (d) {
                            if (!d) {
                                return "";
                            }
                            var options = myself.scope.options;

                            if (options.display) {
                                return d[options.display];
                            } else if (options.displayFn) {
                                return options.displayFn(d);
                            } else {
                                return d;
                            }
                        };
                        /* Recopie du modèle avant la sauvegarde */
                        function initEvent() {
                            if (!myself.scope.eventFlag) {
                                myself.scope.eventFlag = true;
                                var origOnbeforeSave = myself.onbeforesave;

                                myself.onbeforesave = function () {
                                    myself.scope.$data = myself.scope.innerData;

                                    if (angular.isFunction(origOnbeforeSave)) {
                                        origOnbeforeSave();
                                    }
                                };
                            }
                        }

                        function getIndex(array, item) {
                            var idx = -1;
                            var found = _.find(array, function (elt, index) {
                                idx = index;
                                return elt.identifier === item.identifier;
                            });
                            return found ? idx : -1;
                        }
                    },
                    render: function () {
                        this.parent.render.call(this);

                        var uiSelectChoices = this.inputEl.find("ui-select-choices");
                        var uiSelectMatch = this.inputEl.find("ui-select-match");
                        var options = this.scope.options;
                        var isAsync = angular.isDefined(options.refresh);

                        // Liste de choix
                        var repeat = "d in options.data";
                        if (angular.isDefined(options.orderby)) {
                            repeat += " | orderBy: '" + options.orderby + "'";
                        }
                        var trackByField = options.trackby || "identifier";
                        var filterExpr = isAsync || options.filter
                            ? "filterData"
                            : options.displayFn
                                ? "$select.search"
                                : "{" + options.display + ": $select.search}";
                        repeat += " | filter: " + filterExpr + " track by d['" + trackByField + "']";

                        uiSelectChoices.attr("repeat", repeat);

                        // async
                        if (isAsync) {
                            uiSelectChoices.attr("refresh", "refreshData($select)");

                            if (angular.isDefined(options["refresh-delay"])) {
                                uiSelectChoices.attr("refresh-delay", options["refresh-delay"]);
                            }
                            if (angular.isDefined(options["minimum-input-length"])) {
                                uiSelectChoices.attr("minimum-input-length", options["minimum-input-length"]);
                            }
                        }
                        // désactivation d'une entrée
                        if (angular.isDefined(options["disable-choice"])) {
                            uiSelectChoices.attr("ui-disable-choice", "options['disable-choice'](d)");
                        }

                        // placeholder
                        if (angular.isDefined(this.inputEl.attr("placeholder"))) {
                            uiSelectMatch.attr("placeholder", this.inputEl.attr("placeholder"));
                            this.inputEl.removeAttr("placeholder");
                        }
                        // allow-clear
                        if (angular.isDefined(options["allow-clear"])) {
                            uiSelectMatch.attr("allow-clear", options["allow-clear"]);
                        }

                        // Sélection multiple ?
                        if (options.multiple) {
                            this.inputEl.attr("multiple", "");
                            this.inputEl.attr("on-select", "addItem($item, $model)");
                            this.inputEl.attr("on-remove", "removeItem($item, $model)");
                            uiSelectMatch.text("{{getText($item)}}");
                        } else {
                            this.inputEl.attr("on-select", "updateItem($item, $model)");
                            uiSelectMatch.text("{{getText($select.selected)}}");
                        }
                    }
                });
            });
})();