(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('DocStateRatioChartEditWidgetCtrl', DocStateRatioChartEditWidgetCtrl);

    function DocStateRatioChartEditWidgetCtrl($q, config, gettextCatalog, LotSrvc, ProjectSrvc, WorkflowSrvc) {
        var mainCtrl = this;
        mainCtrl.isConfigured = isConfigured;

        mainCtrl.config = {
            dataFormats: [
                {
                    identifier: 'doughnut',
                    label: gettextCatalog.getString('Anneau'),
                },
                {
                    identifier: 'pie',
                    label: gettextCatalog.getString('Camembert'),
                },
                {
                    identifier: 'hbar',
                    label: gettextCatalog.getString('Histogramme'),
                },
                {
                    identifier: 'list',
                    label: gettextCatalog.getString('Liste'),
                },
            ],
            lots: {
                text: 'label',
                placeholder: gettextCatalog.getString('Lot'),
                trackby: 'identifier',
                refresh: function ($select) {
                    mainCtrl.lotsSelect = $select;
                    // Gestion du cas où la liste est réinitialisée manuellement (search est indéfini)
                    if (angular.isUndefined($select.search)) {
                        return $q.when([]);
                    }
                    var searchParams = {
                        page: 0,
                        search: $select.search,
                        active: true,
                    };
                    if (config.project) {
                        searchParams['projects'] = _.pluck(config.project, 'identifier');
                    }
                    return LotSrvc.search(searchParams).$promise.then(function (lots) {
                        return _.map(lots.content, function (lot) {
                            return _.pick(lot, 'identifier', 'label');
                        });
                    });
                },
                'refresh-delay': 300,
                'allow-clear': true,
                multiple: false,
            },
            projects: {
                text: 'name',
                placeholder: gettextCatalog.getString('Projet'),
                trackby: 'identifier',
                refresh: function ($select) {
                    var searchParams = {
                        page: 0,
                        search: $select.search,
                        active: true,
                    };
                    return ProjectSrvc.search(searchParams).$promise.then(function (projects) {
                        return _.map(projects.content, function (project) {
                            return _.pick(project, 'identifier', 'name');
                        });
                    });
                },
                'refresh-delay': 300,
                'allow-clear': true,
                multiple: false,
            },
            states: {
                data: WorkflowSrvc.getConfigWorkflow(),
                text: 'label',
                placeholder: gettextCatalog.getString('État'),
                trackby: 'identifier',
                'allow-clear': true,
                multiple: false,
            },
        };

        /**
         * Le widget est-il configuré
         * @return {Boolean} retourne vrai si le widget est configuré correctement, faux sinon
         */
        function isConfigured() {
            return angular.isDefined(config.format) && angular.isDefined(config.state) && (angular.isDefined(config.project) || angular.isDefined(config.lot));
        }
    }
})();
