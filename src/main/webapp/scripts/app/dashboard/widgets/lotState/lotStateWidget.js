(function () {
    'use strict';

    angular
        .module('numaHopApp')
        .config(function (dashboardProvider, gettext) {
            dashboardProvider.widget('lotState', {
                /* Recopie de la clé dans le widget, necessaire car le support des catégories lors de l'ajout des widgets
                est mal fait : la fonction qui crée les catégories (createCategories) depuis les widgets "perd" la clé,
                voir le template widget-add.html */
                key: 'lotState',
                title: gettext('Activité de lots'),
                category: gettext('Lots'),
                description: gettext('Derniers lots modifiés'),
                templateUrl: 'scripts/app/dashboard/widgets/lotState/lotStateWidget.html',
                controller: 'LotStateWidgetCtrl',
                controllerAs: 'mainCtrl',
                edit: {
                    templateUrl: 'scripts/app/dashboard/widgets/lotState/lotStateEditWidget.html',
                    controller: 'LotStateEditWidgetCtrl',
                    controllerAs: 'mainCtrl',
                },
                authority: 'W_LOT_STATE',
                tableContent: true, // le contenu est un tableau => pas de panel-body
            });
        })
        .controller('LotStateWidgetCtrl', function ($scope, config, LotSrvc) {
            var mainCtrl = this;
            mainCtrl.isConfigured = isConfigured;
            mainCtrl.config = config;
            mainCtrl.setTitleBadge = setTitleBadge;
            mainCtrl.loadLots = loadLots;

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

            function loadLots(params) {
                return LotSrvc.loadLots(params).$promise.then(function (results) {
                    return results;
                });
            }
        });
})();
