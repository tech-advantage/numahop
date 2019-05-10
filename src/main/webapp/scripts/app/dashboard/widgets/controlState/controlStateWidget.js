(function () {
    'use strict';

    angular.module('numaHopApp')
        .config(function (dashboardProvider, gettext) {
            dashboardProvider.widget('controlState', {
                /* Recopie de la clé dans le widget, necessaire car le support des catégories lors de l'ajout des widgets 
                est mal fait : la fonction qui crée les catégories (createCategories) depuis les widgets "perd" la clé,
                voir le template widget-add.html */
                key: "controlState",
                title: gettext('Activité des contrôles'),
                category: gettext('Projets'),
                description: gettext('Dernières unités documentaires contrôlées'),
                templateUrl: 'scripts/app/dashboard/widgets/controlState/controlStateWidget.html',
                controller: 'ControlStateWidgetCtrl',
                controllerAs: 'mainCtrl',
                edit: {
                    templateUrl: 'scripts/app/dashboard/widgets/controlState/controlStateEditWidget.html',
                    controller: 'ControlStateEditWidgetCtrl',
                    controllerAs: 'mainCtrl'
                },
                authority: 'W_DOCUNIT_STATE',
                tableContent: true  // le contenu est un tableau => pas de panel-body
            });
        })
        .controller('ControlStateWidgetCtrl', function ($scope, config, StatisticsSrvc) {

            var mainCtrl = this;
            mainCtrl.config = config;
            mainCtrl.isConfigured = isConfigured;
            mainCtrl.loadDocUnits = loadDocUnits;
            mainCtrl.setTitleBadge = setTitleBadge;

            mainCtrl.columns = [{ 'name': 'PGCN Id', 'field': 'pgcnId'},
                                {'name': 'Nb.pages', 'field': 'nbPages' }];

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

            function loadDocUnits(params) {
                params.current = true;
                params.wcontrol = true;
                params.stype = "workflow";

                return StatisticsSrvc.query(params).$promise.then(function (results) {
                    return _.chain(results)
                        .map(function (r) {
                            return _.map(r.workflow, function (w) {
                                return {
                                    identifier: r.docIdentifier,
                                    pgcnId: r.docPgcnId,
                                    nbPages: r.totalPage,
                                    status: r.docStatus,
                                    timestamp: w.startDate
                                };
                            });
                        })
                        .flatten()
                        .sortBy("timestamp")
                        .value();
                });
            }
        });
})();
