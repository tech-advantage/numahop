(function () {
    'use strict';

    angular
        .module('numaHopApp')
        .config(function (dashboardProvider, gettext) {
            dashboardProvider.widget('docUnitList', {
                /* Recopie de la clé dans le widget, necessaire car le support des catégories lors de l'ajout des widgets 
                est mal fait : la fonction qui crée les catégories (createCategories) depuis les widgets "perd" la clé,
                voir le template widget-add.html */
                key: 'docUnitList',
                title: gettext('Unités documentaires'),
                category: gettext('UD'),
                description: gettext('Liste des unités documentaires'),
                templateUrl: 'scripts/app/dashboard/widgets/docUnitList/docUnitListWidget.html',
                controller: 'DocUnitListWidgetCtrl',
                controllerAs: 'mainCtrl',
                edit: {
                    templateUrl: 'scripts/app/dashboard/widgets/docUnitList/docUnitListEditWidget.html',
                    controller: 'DocUnitListEditWidgetCtrl',
                    controllerAs: 'mainCtrl',
                },
                authority: 'W_DOC_UNIT',
                tableContent: true, // le contenu est un tableau => pas de panel-body
            });
        })
        .controller('DocUnitListWidgetCtrl', function ($scope, config, StatisticsSrvc) {
            var mainCtrl = this;
            mainCtrl.config = config;
            mainCtrl.getPage = getPage;
            mainCtrl.isConfigured = isConfigured;

            var searchParams = {};

            getData();

            /**
             * Le widget est-il configuré ?
             * @return {Boolean} retourne vrai si le widget est configuré correctement, faux sinon
             */
            function isConfigured() {
                return true;
            }

            /**
             * Chargement d'une page de données
             */
            function getPage() {
                searchParams.page = mainCtrl.pagination.page - 1;
                getData();
            }

            /**
             * Chargement des données
             */
            function getData() {
                var params = {
                    page: searchParams && searchParams.page ? searchParams.page : 0,
                    size: 10,
                };
                if (config.project) {
                    params.project = config.project.identifier;
                }
                if (config.lot) {
                    params.lot = config.lot.identifier;
                }

                searchParams.page = params.page;

                return StatisticsSrvc.docUnitList(params).$promise.then(function (data) {
                    $scope.model.titleBadge = data.totalElements;

                    mainCtrl.pagination = {
                        items: data.content,
                        totalItems: data.totalElements,
                        totalPages: data.totalPages,
                        page: data.number + 1,
                        size: data.size,
                    };

                    return data;
                });
            }
        });
})();
