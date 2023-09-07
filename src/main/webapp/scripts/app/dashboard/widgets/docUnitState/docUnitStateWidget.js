(function () {
    'use strict';

    angular
        .module('numaHopApp')
        .config(function (dashboardProvider, gettext) {
            dashboardProvider.widget('docUnitState', {
                /* Recopie de la clé dans le widget, necessaire car le support des catégories lors de l'ajout des widgets 
                est mal fait : la fonction qui crée les catégories (createCategories) depuis les widgets "perd" la clé,
                voir le template widget-add.html */
                key: 'docUnitState',
                title: gettext('Activité des UD'),
                category: gettext('Projets'),
                description: gettext('Dernières unités documentaires modifiées'),
                templateUrl: 'scripts/app/dashboard/widgets/docUnitState/docUnitStateWidget.html',
                controller: 'DocUnitStateWidgetCtrl',
                controllerAs: 'mainCtrl',
                edit: {
                    templateUrl: 'scripts/app/dashboard/widgets/docUnitState/docUnitStateEditWidget.html',
                    controller: 'DocUnitStateEditWidgetCtrl',
                    controllerAs: 'mainCtrl',
                },
                authority: 'W_DOCUNIT_STATE',
                tableContent: true, // le contenu est un tableau => pas de panel-body
            });
        })
        .controller('DocUnitStateWidgetCtrl', function ($scope, config, StatisticsSrvc, codeSrvc) {
            var mainCtrl = this;
            mainCtrl.config = config;
            mainCtrl.isConfigured = isConfigured;
            mainCtrl.loadDocUnits = loadDocUnits;
            mainCtrl.setTitleBadge = setTitleBadge;
            mainCtrl.code = codeSrvc;

            mainCtrl.columns = [
                { name: 'PGCN Id', field: 'pgcnId' },
                { name: 'Nb.pages', field: 'nbPages' },
                { name: 'Etape', field: 'stage' },
            ];

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
                params.wdocunit = true;
                params.stype = 'workflow';

                return StatisticsSrvc.query(params).$promise.then(function (results) {
                    return _.chain(results)
                        .map(function (r) {
                            return _.map(r.workflow, function (w) {
                                return {
                                    identifier: r.docIdentifier,
                                    pgcnId: r.docPgcnId,
                                    nbPages: r.totalPage,
                                    stage: mainCtrl.code['workflow.' + w.key],
                                    status: mainCtrl.code['workflow.status.' + w.status],
                                    timestamp: w.endDate || w.startDate,
                                };
                            });
                        })
                        .flatten()
                        .sortBy('timestamp')
                        .value();
                });
            }
        });
})();
