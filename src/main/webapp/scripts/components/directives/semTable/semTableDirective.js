(function () {
    'use strict';

    /**
     * Ajoute le tri par colonne sur un tableau
     *
     * Exemple #1: tri basique non personnalisé
     *
     * <table class="table table-condensed table-hover" sem-table>
     *  <thead>
     *    <tr>
     *      <th sem-table-sorton="code"><span translate>Code</span></th>
     *      <th sem-table-sorton="name"><span translate>Nom</span></th>
     *      <th><span translate>Colonne non triée</span></th>
     *    </tr>
     *  </thead>
     *  <tbody>...</tbody>
     * </table>
     *
     *
     * Exemple #2: initialisation du tri par la variable orderBy, tri personnalisé avec la fonction sortBudget
     *
     * dans le controlleur:
     *  $scope.orderBy = ["code", "-name"]
     *  $scope.sortBudget = function(param1, param2, param3) { ... }
     *
     * <table class="table table-condensed table-hover" sem-table="orderBy" sem-table-onsort="sortBudget(a, b, sortModel)"> <!-- <=== le paramètre "sortModel" est important dans l'appel à sortBudget ! -->
     *  <thead>
     *    <tr>
     *      <th sem-table-sorton="code"><span translate>Code</span></th>
     *      <th sem-table-sorton="name"><span translate>Nom</span></th>
     *      <th><span translate>Colonne non triée</span></th>
     *    </tr>
     *  </thead>
     *  <tbody>...</tbody>
     * </table>
     */
    angular.module('numaHopApp.directive').directive('semTable', function ($compile) {
        return {
            restrict: 'A',
            scope: {
                sortModel: '=semTable',
                customSort: '&semTableOnsort',
            },
            controller: 'SemTableCtrl',
            compile: function compile(tElement, tAttrs, transclude) {
                if (!tAttrs.semTable) {
                    tAttrs.semTable = 'orderBy';
                }
                return {
                    pre: function preLink(scope, iElement, iAttrs, controller) {
                        if (angular.isUndefined(scope.sortModel)) {
                            scope.sortModel = [];
                        }
                        scope.init(scope.sortModel);

                        iElement.find('th[sem-table-sorton]').each(function (index, value) {
                            var _element = angular.element(value);
                            var sortOn = _element.attr('sem-table-sorton');
                            var sortIcon = angular.element('<span></span>').attr('ng-class', "getSortClass('" + sortOn + "')");
                            var sortCounter = angular
                                .element('<span></span>')
                                .addClass('counter')
                                .attr('ng-show', 'sortModel.length > 1')
                                .attr('ng-bind', "getSortIndex('" + sortOn + "')");
                            var sortBadge = angular
                                .element('<span></span>')
                                .addClass('font10')
                                .attr('ng-show', "sortModel.length > 0 && isSorted('" + sortOn + "')");
                            sortBadge.append(sortIcon).append(sortCounter);

                            _element.attr('ng-click', "sortOn('" + sortOn + "', sortModel)").append(sortBadge);

                            $compile(_element)(scope);
                        });
                    },
                };
            },
        };
    });
})();
