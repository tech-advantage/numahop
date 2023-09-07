(function () {
    'use strict';

    angular
        .module('numaHopApp')
        .config(function (dashboardProvider, gettext) {
            dashboardProvider.widget('spaceChart', {
                /* Recopie de la clé dans le widget, necessaire car le support des catégories lors de l'ajout des widgets
                est mal fait : la fonction qui crée les catégories (createCategories) depuis les widgets "perd" la clé,
                voir le template widget-add.html */
                key: 'spaceChart',
                title: gettext('Espace disque disponible'),
                category: gettext('Décomptes'),
                description: gettext('Espace disque utilisé/disponible par bibliothèque'),
                templateUrl: 'scripts/app/dashboard/widgets/diskSpace/spaceChartWidget.html',
                controller: 'SpaceChartWidgetCtrl',
                controllerAs: 'mainCtrl',
                edit: {
                    templateUrl: 'scripts/app/dashboard/widgets/diskSpace/spaceChartEditWidget.html',
                    controller: 'SpaceChartEditWidgetCtrl',
                    controllerAs: 'mainCtrl',
                },
            });
        })
        .controller('SpaceChartWidgetCtrl', function ($q, $scope, config, DeliverySrvc, gettextCatalog) {
            var mainCtrl = this;
            mainCtrl.isConfigured = isConfigured;
            mainCtrl.config = config;
            mainCtrl.colours = ['#c92a2a', '#599623', '#717984', '#F1C40F'];

            init();

            /**
             * Initialisation du contrôleur
             */
            function init() {
                if (isConfigured()) {
                    getDiskUsageInfos().then(function (chartData) {
                        // données du graphique
                        mainCtrl.chartData = chartData;
                        mainCtrl.data = [chartData['occupe'], chartData['disponible']];
                        mainCtrl.labels = [gettextCatalog.getString('Utilisé') + ' ' + bytesToSize(chartData['occupe']), gettextCatalog.getString('Disponible') + ' ' + bytesToSize(chartData['disponible'])];

                        if (mainCtrl.config.format.identifier === 'doughnut' || mainCtrl.config.format.identifier === 'pie') {
                            mainCtrl.options = {
                                responsive: true,
                                legend: {
                                    display: true,
                                    position: 'right',
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

            function bytesToSize(bytes) {
                var sizes = ['B', 'Ko', 'Mo', 'Go', 'To', 'Po'];
                for (var i = 0; i < sizes.length; i++) {
                    if (bytes <= 1024) {
                        return bytes + ' ' + sizes[i];
                    } else {
                        bytes = parseFloat(bytes / 1024).toFixed(2);
                    }
                }
                return bytes + ' P';
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
            function getDiskUsageInfos() {
                var params = {};
                if (config.libraries) {
                    params.libraries = _.pluck(config.libraries, 'identifier');
                }
                return DeliverySrvc.getDiskUsageInfos(params).$promise;
            }
        });
})();
