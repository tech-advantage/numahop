(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('ControlStateEditWidgetCtrl', ControlStateEditWidgetCtrl);

    function ControlStateEditWidgetCtrl($q, config, gettextCatalog, LibrarySrvc, LotSrvc, ProjectSrvc, DigitalDocumentSrvc) {
        var mainCtrl = this;
        mainCtrl.isConfigured = isConfigured;

        mainCtrl.options = {
            libraries: {
                text: 'name',
                placeholder: gettextCatalog.getString('Bibliothèque'),
                trackby: 'identifier',
                // Chargement avec mise en cache du résultat
                refresh: function ($select) {
                    if (!mainCtrl.options.libraries.data) {
                        mainCtrl.options.libraries.data = LibrarySrvc.query({ dto: true });
                        return mainCtrl.options.libraries.data.$promise.then(function (lib) {
                            return _.map(lib, function (l) {
                                return _.pick(l, 'identifier', 'name');
                            });
                        });
                    } else {
                        return $q.when(mainCtrl.options.libraries.data);
                    }
                },
                'refresh-delay': 0, // pas de refresh-delay, car on lit les données en cache après le 1er chargement
                'allow-clear': true,
                multiple: true,
            },
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
                multiple: true,
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
                multiple: true,
            },
            states: {
                data: DigitalDocumentSrvc.getConfigControlStatus(),
                text: 'label',
                placeholder: gettextCatalog.getString('Statut du document'),
                trackby: 'identifier',
                multiple: true,
                'allow-clear': true,
            },
        };

        init();

        /**
         * Initialisation du contrôleur
         */
        function init() {
            config.from = config.from || 10;
        }

        /**
         * Le widget est-il configuré
         * @return {Boolean} retourne vrai si le widget est configuré correctement, faux sinon
         */
        function isConfigured() {
            return angular.isDefined(config.from);
        }
    }
})();
