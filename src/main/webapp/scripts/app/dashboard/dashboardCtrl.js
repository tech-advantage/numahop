(function () {
    'use strict';

    angular.module('numaHopApp').value('adfTemplatePath', 'scripts/app/dashboard/templates/');
    angular.module('numaHopApp.controller').controller('DashboardCtrl', DashboardCtrl);

    function DashboardCtrl($scope, gettextCatalog, DashboardSrvc, HistorySrvc, Principal) {

        Principal.identity().then(init);

        function init() {
            HistorySrvc.add(gettextCatalog.getString("Tableau de bord"));

            $scope.model = DashboardSrvc.getDashboard();
            $scope.model.getNumberOfWidgets = function () {
                var numberOfWidgets = 0;
                _(this.rows).each(function (row, key, list) {
                    _(row.columns).each(function (column, key, list) {
                        if (angular.isDefined(column.widgets)) {
                            numberOfWidgets += column.widgets.length;
                        }
                    });
                });
                return numberOfWidgets;

            };
            $scope.$on('adfDashboardChanged', function (event, name, model) {
                DashboardSrvc.saveDashboard($scope.model);
            });
        }

        $scope.widgetFilter = function (widget, type, model) {
            return true;
        };
    }
})();
