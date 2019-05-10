(function () {
    'use strict';

    angular.module('numaHopApp')
        .config(function (dashboardProvider, gettext) {
            dashboardProvider.widget('userChart', {
                /* Recopie de la clé dans le widget, necessaire car le support des catégories lors de l'ajout des widgets 
                est mal fait : la fonction qui crée les catégories (createCategories) depuis les widgets "perd" la clé,
                voir le template widget-add.html */
                key: "userChart",
                title: gettext('Décomptes des utilisateurs'),
                category: gettext('Décomptes'),
                description: gettext('Décomptes des utilisateurs par bibliothèque'),
                templateUrl: 'scripts/app/dashboard/widgets/userChart/userChartWidget.html',
                controller: 'UserChartWidgetCtrl',
                controllerAs: 'mainCtrl',
                edit: {
                    templateUrl: 'scripts/app/dashboard/widgets/userChart/userChartEditWidget.html',
                    controller: 'UserChartEditWidgetCtrl',
                    controllerAs: 'mainCtrl'
                }
            });
        })
        .controller('UserChartWidgetCtrl', function ($q, $scope, config, StatisticsSrvc) {

            var mainCtrl = this;
            mainCtrl.isConfigured = isConfigured;
            mainCtrl.config = config;


            init();

            /**
             * Initialisation du contrôleur
             */
            function init() {
                if (isConfigured()) {
                    getChartData().then(function (chartData) {
                        // données du graphique
                        mainCtrl.chartData = chartData;
                        mainCtrl.data = _.pluck(chartData, "nbUsers");
                        mainCtrl.labels = _.pluck(chartData, "libraryName");

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

            /**
             * Le widget est-il configuré ?
             * @return {Boolean} retourne vrai si le widget est configuré correctement, faux sinon
             */
            function isConfigured() {
                return angular.isDefined(config.format);
            }

            /**
             * Chargement des données
             */
            function getChartData() {
                var params = {};
                if (config.libraries) {
                    params.libraries = _.pluck(config.libraries, "identifier");
                }
                return StatisticsSrvc.getUsersGroupByLibrary(params).$promise;
            }
        });
})();
