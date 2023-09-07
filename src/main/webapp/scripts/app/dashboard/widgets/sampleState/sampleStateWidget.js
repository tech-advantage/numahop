(function () {
    'use strict';

    angular
        .module('numaHopApp')
        .config(function (dashboardProvider, gettext) {
            dashboardProvider.widget('sampleState', {
                /* Recopie de la clé dans le widget, necessaire car le support des catégories lors de l'ajout des widgets 
                est mal fait : la fonction qui crée les catégories (createCategories) depuis les widgets "perd" la clé,
                voir le template widget-add.html */
                key: 'sampleState',
                title: gettext('Contrôle des livraisons échantillonnées'),
                category: gettext('Livraisons'),
                description: gettext('Contrôle des livraisons échantillonnées'),
                templateUrl: 'scripts/app/dashboard/widgets/sampleState/sampleStateWidget.html',
                controller: 'SampleStateWidgetCtrl',
                controllerAs: 'mainCtrl',
                edit: {
                    templateUrl: 'scripts/app/dashboard/widgets/sampleState/sampleStateEditWidget.html',
                    controller: 'SampleStateEditWidgetCtrl',
                    controllerAs: 'mainCtrl',
                },
                authority: 'W_DELIVERY_STATE',
                tableContent: true, // le contenu est un tableau => pas de panel-body
            });
        })
        .controller('SampleStateWidgetCtrl', function ($scope, config, DeliverySrvc) {
            var mainCtrl = this;
            mainCtrl.isConfigured = isConfigured;
            mainCtrl.config = config;
            mainCtrl.setTitleBadge = setTitleBadge;
            mainCtrl.loadDeliveries = loadDeliveries;

            /**
             * Le widget est-il configuré ?
             * @return {Boolean} retourne vrai si le widget est configuré correctement, faux sinon
             */
            function isConfigured() {
                return angular.isDefined(config.from);
            }

            function setTitleBadge(value) {
                $scope.model.titleBadge = value;
            }

            function loadDeliveries(params) {
                return DeliverySrvc.loadSampledDeliveries(params).$promise.then(function (results) {
                    return results;
                });
            }
        });
})();
