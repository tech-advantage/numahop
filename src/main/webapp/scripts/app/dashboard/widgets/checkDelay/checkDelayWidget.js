(function () {
    'use strict';

    angular.module('numaHopApp').config(function (dashboardProvider, gettext) {
        dashboardProvider.widget('checkDelay', {
            /* Recopie de la clé dans le widget, necessaire car le support des catégories lors de l'ajout des widgets 
            est mal fait : la fonction qui crée les catégories (createCategories) depuis les widgets "perd" la clé,
            voir le template widget-add.html */
            key: "checkDelay",
            title: gettext('Délai de contrôle restant'),
            category: gettext('Unités documentaires'),
            description: gettext('Délai de contrôle restant'),
            templateUrl: 'scripts/app/dashboard/widgets/checkDelay/checkDelayWidget.html',
            controller: 'CheckDelayWidgetCtrl',
            controllerAs: 'mainCtrl',
            edit: {
                templateUrl: 'scripts/app/dashboard/widgets/checkDelay/checkDelayEditWidget.html',
                controller: 'CheckDelayEditWidgetCtrl',
                controllerAs: 'mainCtrl'
            },
            authority: 'W_DOCUNIT'
        });
    }).controller('CheckDelayWidgetCtrl', function (config, StatisticsSrvc) {

        var mainCtrl = this;
        mainCtrl.isConfigured = isConfigured;
        mainCtrl.isDelayValid = isDelayValid;

        /**
         * Un objet pour la barre de progression
        */
        mainCtrl.progressBar = { color: "#ff5555" };


        init();

        /**
         * Construteur
         */
        function init() {
            if (isConfigured()) {
                getStatsData().then(function (statsData) {
                    mainCtrl.maxSize = _.chain(statsData).pluck("maxCheckDelay").max().value();
                    mainCtrl.minRemainingCheckDelay = _.chain(statsData).pluck("minRemainingCheckDelay").min().value();
                    mainCtrl.size = mainCtrl.maxSize - mainCtrl.minRemainingCheckDelay;

                    if (mainCtrl.size > mainCtrl.maxSize * 0.75) {
                        mainCtrl.progressBar.color = "#c92a2a";
                    } else if (mainCtrl.size > mainCtrl.maxSize * 0.5) {
                        mainCtrl.progressBar.color = "#f59f00";
                    } else {
                        mainCtrl.progressBar.color = "#5cb85c";
                    }

                    // liens
                    if (config.delivery.length) {
                        mainCtrl.links = getLinks("delivery", config.delivery);
                    }
                    else if (config.lot.length) {
                        mainCtrl.links = getLinks("lot", config.lot);
                    }
                    else if (config.project.length) {
                        mainCtrl.links = getLinks("project", config.project);
                    }
                    else if (config.libraries.length) {
                        mainCtrl.links = getLinks("library", config.libraries);
                    }
                });
            }
        }

        function getLinks(type, list) {
            return _.map(list, function (e) {
                return {
                    type: type,
                    identifier: e.identifier,
                    label: e.label || e.name
                };
            });
        }

        /**
         * Le widget est-il configuré
         * @return {Boolean} retourne vrai si le widget est configuré correctement, faux sinon
         */
        function isConfigured() {
            return config.libraries || config.project;
        }

        function isDelayValid() {
            return Number.isFinite(mainCtrl.size) && Number.isFinite(mainCtrl.maxSize);
        }

        /**
         * Chargement des données
         */
        function getStatsData() {
            var params = {
                groupby: config.delivery.length ? "DELIVERY" : config.lot.length ? "LOT" : "PROJECT"
            };
            if (config.libraries) {
                params.libraries = _.pluck(config.libraries, "identifier");
            }
            if (config.project) {
                params.project = _.pluck(config.project, "identifier");
            }
            if (config.lot) {
                params.lot = _.pluck(config.lot, "identifier");
            }
            if (config.delivery) {
                params.delivery = _.pluck(config.delivery, "identifier");
            }
            return StatisticsSrvc.docUnitCheckDelay(params).$promise;
        }
    });
})();
