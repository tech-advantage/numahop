(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('DocUnitListEditWidgetCtrl', DocUnitListEditWidgetCtrl);

    function DocUnitListEditWidgetCtrl($q, config, gettextCatalog, LotSrvc, ProjectSrvc) {
        var mainCtrl = this;
        mainCtrl.changeProject = changeProject;

        mainCtrl.options = {
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
                        searchParams['projects'] = config.project.identifier;
                    }
                    return LotSrvc.search(searchParams).$promise.then(function (lots) {
                        return _.map(lots.content, function (lot) {
                            return _.pick(lot, 'identifier', 'label');
                        });
                    });
                },
                'refresh-delay': 300,
                'allow-clear': true,
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
            },
        };

        /**
         *
         * Changement de projet
         */
        function changeProject() {
            config.lot = {};

            if (mainCtrl.lotsSelect) {
                delete mainCtrl.lotsSelect.selected;
                delete mainCtrl.lotsSelect.search;
                mainCtrl.lotsSelect.items = [];
                // mainCtrl.lotsSelect.activate(false, true);
                mainCtrl.lotsSelect.activeIndex = 0;
                mainCtrl.lotsSelect.open = false;
            }
        }
    }
})();
