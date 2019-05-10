(function () {
    'use strict';

    angular.module('numaHopApp')
        .config(function (dashboardProvider, gettext) {

            dashboardProvider.widget('docUnitProcessed', {
                /* Recopie de la clé dans le widget, necessaire car le support des catégories lors de l'ajout des widgets 
                est mal fait : la fonction qui crée les catégories (createCategories) depuis les widgets "perd" la clé,
                voir le template widget-add.html */
                key: "docUnitProcessed",
                title: gettext('UD archivées / diffusées'),
                category: gettext('UD'),
                description: gettext('Dernières unités documentaires archivées ou diffusées'),
                templateUrl: 'scripts/app/dashboard/widgets/docUnitProcessed/docUnitProcessedWidget.html',
                controller: 'DocUnitProcessedWidgetCtrl',
                controllerAs: 'mainCtrl',
                edit: {
                    templateUrl: 'scripts/app/dashboard/widgets/docUnitProcessed/docUnitProcessedEditWidget.html',
                    controller: 'DocUnitProcessedEditWidgetCtrl',
                    controllerAs: 'mainCtrl'
                },
                authority: 'W_DOC_UNIT',
                tableContent: true  // le contenu est un tableau => pas de panel-body
            });
        })
        .controller('DocUnitProcessedWidgetCtrl', function ($q, $scope, config, StatisticsSrvc) {

            var mainCtrl = this;
            mainCtrl.config = config;
            mainCtrl.isConfigured = isConfigured;


            init();

            function init() {
                getData().then(function (data) {
                    if (data) {
                        $scope.model.titleBadge = data.length;
                        mainCtrl.docunits = data;
                    }
                });
            }

            /**
             * Le widget est-il configuré ?
             * @return {Boolean} retourne vrai si le widget est configuré correctement, faux sinon
             */
            function isConfigured() {
                return angular.isDefined(config.type) && angular.isDefined(config.failures) && angular.isDefined(config.from);
            }

            /**
             * Chargement des données
             */
            function getData() {
                var params = {
                    docUnit: true,
                    from: moment().add(-config.from, 'day').format("YYYY-MM-DD"),
                    failures: config.failures
                };
                if (config.type === 'ARCHIVE') {
                    return StatisticsSrvc.docUnitArchive(params).$promise;
                }
                else if (config.type === 'EXPORT') {
                    return StatisticsSrvc.docUnitExport(params).$promise;
                }
                else if (config.type === 'EXPORT_OMEKA') {
                    return $q.when(); // todo StatisticsSrvc.docUnitExport(params).$promise;
                }
                else if (config.type === 'EXPORT_LOCAL') {
                    return $q.when(); // todo StatisticsSrvc.docUnitExport(params).$promise;
                }
                else {
                    return $q.when();
                }
            }
        });
})();
