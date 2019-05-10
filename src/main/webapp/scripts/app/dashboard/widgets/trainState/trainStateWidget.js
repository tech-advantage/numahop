(function () {
    'use strict';

    angular.module('numaHopApp')
        .config(function (dashboardProvider, gettext) {
            dashboardProvider.widget('trainState', {
                /* Recopie de la clé dans le widget, necessaire car le support des catégories lors de l'ajout des widgets 
                est mal fait : la fonction qui crée les catégories (createCategories) depuis les widgets "perd" la clé,
                voir le template widget-add.html */
                key: "trainState",
                title: gettext('Activité des trains'),
                category: gettext('Trains'),
                description: gettext('Derniers trains modifiés'),
                templateUrl: 'scripts/app/dashboard/widgets/trainState/trainStateWidget.html',
                controller: 'TrainStateWidgetCtrl',
                controllerAs: 'mainCtrl',
                edit: {
                    templateUrl: 'scripts/app/dashboard/widgets/trainState/trainStateEditWidget.html',
                    controller: 'TrainStateEditWidgetCtrl',
                    controllerAs: 'mainCtrl'
                },
                authority: 'W_TRAIN_STATE',
                tableContent: true  // le contenu est un tableau => pas de panel-body
            });
        })
        .controller('TrainStateWidgetCtrl', function ($scope, config) {

            var mainCtrl = this;
            mainCtrl.isConfigured = isConfigured;
            mainCtrl.config = config;
            mainCtrl.setTitleBadge = setTitleBadge;

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
        });
})();
