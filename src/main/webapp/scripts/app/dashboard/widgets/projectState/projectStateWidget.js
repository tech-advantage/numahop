(function () {
    'use strict';

    angular
        .module('numaHopApp')
        .config(function (dashboardProvider, gettext) {
            dashboardProvider.widget('projectState', {
                /* Recopie de la clé dans le widget, necessaire car le support des catégories lors de l'ajout des widgets 
                est mal fait : la fonction qui crée les catégories (createCategories) depuis les widgets "perd" la clé,
                voir le template widget-add.html */
                key: 'projectState',
                title: gettext('Activité des projets'),
                category: gettext('Projets'),
                description: gettext('Derniers projets modifiés'),
                templateUrl: 'scripts/app/dashboard/widgets/projectState/projectStateWidget.html',
                controller: 'ProjectStateWidgetCtrl',
                controllerAs: 'mainCtrl',
                edit: {
                    templateUrl: 'scripts/app/dashboard/widgets/projectState/projectStateEditWidget.html',
                    controller: 'ProjectStateEditWidgetCtrl',
                    controllerAs: 'mainCtrl',
                },
                authority: 'W_PROJECT_STATE',
                tableContent: true, // le contenu est un tableau => pas de panel-body
            });
        })
        .controller('ProjectStateWidgetCtrl', function ($scope, config, ProjectSrvc) {
            var mainCtrl = this;
            mainCtrl.isConfigured = isConfigured;
            mainCtrl.config = config;
            mainCtrl.setTitleBadge = setTitleBadge;
            mainCtrl.loadProjects = loadProjects;

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

            function loadProjects(params) {
                return ProjectSrvc.loadProjects(params).$promise.then(function (results) {
                    return results;
                });
            }
        });
})();
