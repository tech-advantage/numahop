(function () {
    'use strict';

    angular.module('numaHopApp')
        .config(function (dashboardProvider, gettext) {
            dashboardProvider.widget('docStateRatioChart', {
                /* Recopie de la clé dans le widget, necessaire car le support des catégories lors de l'ajout des widgets 
                est mal fait : la fonction qui crée les catégories (createCategories) depuis les widgets "perd" la clé,
                voir le template widget-add.html */
                key: "docStateRatioChart",
                title: gettext('UD par statut'),
                category: gettext('Décomptes'),
                description: gettext('Nombre d\'UD dans le statut par rapport au projet / lot'),
                templateUrl: 'scripts/app/dashboard/widgets/docStateRatioChart/docStateRatioChartWidget.html',
                controller: 'DocStateRatioChartWidgetCtrl',
                controllerAs: 'mainCtrl',
                edit: {
                    templateUrl: 'scripts/app/dashboard/widgets/docStateRatioChart/docStateRatioChartEditWidget.html',
                    controller: 'DocStateRatioChartEditWidgetCtrl',
                    controllerAs: 'mainCtrl'
                }
            });
        })
        .controller('DocStateRatioChartWidgetCtrl', function ($q, codeSrvc, config, gettextCatalog, StatisticsSrvc) {

            var mainCtrl = this;
            mainCtrl.isConfigured = isConfigured;
            mainCtrl.config = config;
            mainCtrl.getStateLabel = getStateLabel;


            init();

            /**
             * Initialisation du contrôleur
             */
            function init() {
                if (isConfigured()) {
                    getChartData().then(function (chartData) {
                        // données du graphique
                        mainCtrl.chartData = chartData;
                        mainCtrl.data = [chartData.nbDocOnState, chartData.nbDoc - chartData.nbDocOnState];
                        mainCtrl.labels = [getStateLabel(), gettextCatalog.getString("Autre")];

                        if (mainCtrl.config.format.identifier === "doughnut" || mainCtrl.config.format.identifier === "pie") {
                            mainCtrl.options = {
                                responsive: true,
                                legend: {
                                    display: true,
                                    position: "bottom"
                                }
                            };
                        }
                        else if (mainCtrl.config.format.identifier === "hbar") {
                            mainCtrl.options = {
                                responsive: true,
                                scales: {
                                    xAxes: [{
                                        ticks: {
                                            beginAtZero: true
                                        }
                                    }]
                                }
                            };
                        }
                    });
                }
            }

            function getStateLabel() {
                return codeSrvc["workflow." + mainCtrl.chartData.state] || mainCtrl.chartData.state;
            }

            /**
             * Le widget est-il configuré ?
             * @return {Boolean} retourne vrai si le widget est configuré correctement, faux sinon
             */
            function isConfigured() {
                return angular.isDefined(config.format) && angular.isDefined(config.state) && (angular.isDefined(config.project) || angular.isDefined(config.lot));
            }

            /**
             * Chargement des données
             */
            function getChartData() {
                var params = {};
                if (config.project) {
                    params.project = config.project.identifier;
                }
                if (config.lot) {
                    params.lot = config.lot.identifier;
                }
                if (config.state) {
                    params.state = config.state.identifier;
                }
                if (params.state && (params.project || params.lot)) {
                    return StatisticsSrvc.getDocUnitStatusRatio(params).$promise;
                }
                else {
                    return $q.when([]);
                }
            }
        });
})();
