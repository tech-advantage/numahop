(function () {
    'use strict';

    angular
        .module('numaHopApp')
        .config(function (dashboardProvider, gettext) {
            dashboardProvider.widget('statusChart', {
                /* Recopie de la clé dans le widget, necessaire car le support des catégories lors de l'ajout des widgets 
                est mal fait : la fonction qui crée les catégories (createCategories) depuis les widgets "perd" la clé,
                voir le template widget-add.html */
                key: 'statusChart',
                title: gettext('Décomptes des projets'),
                category: gettext('Décomptes'),
                description: gettext('Décomptes des projets, lots, trains et livraisons par statut'),
                templateUrl: 'scripts/app/dashboard/widgets/statusChart/statusChartWidget.html',
                controller: 'StatusChartWidgetCtrl',
                controllerAs: 'mainCtrl',
                edit: {
                    templateUrl: 'scripts/app/dashboard/widgets/statusChart/statusChartEditWidget.html',
                    controller: 'StatusChartEditWidgetCtrl',
                    controllerAs: 'mainCtrl',
                },
            });
        })
        .controller('StatusChartWidgetCtrl', function ($q, $scope, config, StatisticsSrvc) {
            var mainCtrl = this;
            mainCtrl.isConfigured = isConfigured;
            mainCtrl.config = config;
            mainCtrl.getStatusLabel = getStatusLabel;

            init();

            /**
             * Initialisation du contrôleur
             */
            function init() {
                if (isConfigured()) {
                    getChartData().then(function (chartData) {
                        // données du graphique
                        mainCtrl.chartData = chartData;
                        mainCtrl.data = _.pluck(chartData, 'count');
                        mainCtrl.labels = _.chain(chartData)
                            .pluck('status')
                            .map(function (v) {
                                return StatisticsSrvc.getLabel(config.dataType.identifier, v);
                            })
                            .value();

                        if (mainCtrl.config.format.identifier === 'doughnut' || mainCtrl.config.format.identifier === 'pie') {
                            mainCtrl.options = {
                                responsive: true,
                                legend: {
                                    display: true,
                                    position: 'bottom',
                                },
                            };
                        } else if (mainCtrl.config.format.identifier === 'hbar') {
                            mainCtrl.options = {
                                responsive: true,
                                scales: {
                                    xAxes: [
                                        {
                                            ticks: {
                                                beginAtZero: true,
                                            },
                                        },
                                    ],
                                },
                            };
                        }
                    });
                }
            }

            function getStatusLabel(v) {
                return StatisticsSrvc.getLabel(config.dataType.identifier, v);
            }

            /**
             * Le widget est-il configuré ?
             * @return {Boolean} retourne vrai si le widget est configuré correctement, faux sinon
             */
            function isConfigured() {
                return angular.isDefined(config.dataType) && angular.isDefined(config.format);
            }

            /**
             * Chargement des données
             */
            function getChartData() {
                if (config.dataType && config.dataType.identifier) {
                    var service = StatisticsSrvc[config.dataType.identifier];
                    if (service) {
                        var params = {};
                        if (config.libraries) {
                            params.libraries = _.pluck(config.libraries, 'identifier');
                        }
                        if (config.project) {
                            params.project = _.pluck(config.project, 'identifier');
                        }
                        if (config.lot) {
                            params.lot = _.pluck(config.lot, 'identifier');
                        }
                        return service(params).$promise;
                    } else {
                        return $q.reject('Erreur de chargement (service ' + config.dataType.identifier + ')');
                    }
                }
            }
        });
})();
