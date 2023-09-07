(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('StatisticsWorkflowDocCtrl', StatisticsWorkflowDocCtrl);

    function StatisticsWorkflowDocCtrl($q, codeSrvc, gettextCatalog, HistorySrvc, LibrarySrvc, NumahopStorageService, ProjectSrvc, LotSrvc, TrainSrvc, StatisticsSrvc, WorkflowSrvc) {
        var statCtrl = this;

        statCtrl.changePageSize = changePageSize;
        statCtrl.code = codeSrvc;
        statCtrl.getExportUrl = getExportUrl;
        statCtrl.getPage = getPage;
        statCtrl.getStatus = getStatus;
        statCtrl.getStatusClass = getStatusClass;
        statCtrl.isVisible = isVisible;
        statCtrl.reinitFilters = reinitFilters;
        statCtrl.saveColumns = saveColumns;
        statCtrl.search = search;
        statCtrl.setAndSearch = setAndSearch;

        var PAGE_START = 1;
        var FILTER_STORAGE_SERVICE_KEY = 'stat_workflow_docunit';

        /**
         * Objet de pagination
         * @type {Object}
         */
        statCtrl.pagination = {
            items: [],
            totalItems: 0,
            busy: false,
            page: PAGE_START,
        };

        /**
         * Taille pagination
         * @type {Object}
         */
        statCtrl.sizeOptions = [
            { value: 10, label: '10' },
            { value: 20, label: '20' },
            { value: 50, label: '50' },
            { value: 100, label: '100' },
            { value: 5000, label: 'Tout' },
        ];

        /**
         * Listes déroulantes
         */
        statCtrl.config = {
            libraries: {
                text: 'name',
                placeholder: gettextCatalog.getString('Bibliothèque'),
                trackby: 'identifier',
                // Chargement avec mise en cache du résultat
                refresh: function () {
                    if (!statCtrl.config.libraries.data) {
                        statCtrl.config.libraries.data = LibrarySrvc.query({ dto: true });
                        return statCtrl.config.libraries.data.$promise.then(function (lib) {
                            return _.map(lib, function (l) {
                                return _.pick(l, 'identifier', 'name');
                            });
                        });
                    } else {
                        return $q.when(statCtrl.config.libraries.data);
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
                    statCtrl.lotsSelect = $select;
                    // Gestion du cas où la liste est réinitialisée manuellement (search est indéfini)
                    if (angular.isUndefined($select.search)) {
                        return $q.when([]);
                    }
                    var searchParams = {
                        page: 0,
                        search: $select.search,
                        active: !statCtrl.filters.inactive,
                    };
                    if (statCtrl.filters.project) {
                        searchParams['projects'] = _.pluck(statCtrl.filters.project, 'identifier');
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
            trains: {
                text: 'label',
                placeholder: gettextCatalog.getString('Train'),
                trackby: 'identifier',
                refresh: function ($select) {
                    statCtrl.trainsSelect = $select;
                    // Gestion du cas où la liste est réinitialisée manuellement (search est indéfini)
                    if (angular.isUndefined($select.search)) {
                        return $q.when([]);
                    }
                    var searchParams = {
                        page: 0,
                        search: $select.search,
                        active: true,
                    };
                    if (statCtrl.filters.project) {
                        searchParams['projects'] = _.pluck(statCtrl.filters.project, 'identifier');
                    }
                    return TrainSrvc.search(searchParams).$promise.then(function (trains) {
                        return _.map(trains.content, function (train) {
                            return _.pick(train, 'identifier', 'label');
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
                        active: false,
                    };
                    return ProjectSrvc.search(searchParams).$promise.then(function (projects) {
                        return _.map(projects.content, function (project) {
                            return _.pick(project, 'identifier', 'name');
                        });
                    });
                },
                'refresh-delay': 300,
                multiple: true,
                'allow-clear': true,
            },
            states: {
                data: WorkflowSrvc.getConfigWorkflow(),
                text: 'label',
                placeholder: gettextCatalog.getString('État'),
                trackby: 'identifier',
                multiple: true,
                'allow-clear': true,
            },
        };

        init();

        /**
         * Initialisation du controleur
         * @return {[type]} [description]
         */
        function init() {
            HistorySrvc.add(gettextCatalog.getString('Statistiques des workflows'));
            statCtrl.loaded = false;

            loadColumns();
            loadSearch();
            getPage().then(function () {
                statCtrl.loaded = true;
            });
        }

        /**
         * Réinitialise l'ensemble des filtres et lance une nouvelle recherche
         * @return {[type]} [description]
         */
        function reinitFilters() {
            statCtrl.pagination.items = [];
            statCtrl.pagination.totalItems = 0;
            statCtrl.pagination.busy = false;
            statCtrl.pagination.page = PAGE_START;
            statCtrl.pagination.size = statCtrl.pageSize;

            statCtrl.filters = {};

            search();
        }

        /**
         * Chargement de la prochaine page d'items
         * @return {[type]} [description]
         */
        function getPage() {
            statCtrl.pagination.busy = true;
            var params = getSearchParams();

            return StatisticsSrvc.workflowDocUnit(params, handlePageOfItems).$promise;
        }

        /**
         * handlePageOfItems - Gestion d'une page d'entités
         *
         * @param  {type} pageOfReserves la page avec les entités
         */
        function handlePageOfItems(pageOfItems) {
            statCtrl.pagination.totalItems = pageOfItems.totalElements;
            statCtrl.pagination.totalPages = pageOfItems.totalPages;
            statCtrl.pagination.busy = false;
            statCtrl.pagination.items = pageOfItems.content;
        }

        /**
         * Recherche d'entité
         * @return {[type]} [description]
         */
        function search() {
            statCtrl.pagination.page = 1;
            saveSearch();
            getPage();
        }

        /**
         * Met à jour le filtre de recherche field, et lance la recherche
         *
         * @param {*} field
         * @param {*} value
         */
        function setAndSearch(field, value) {
            statCtrl.filters[field] = value;
            search();
        }

        /**
         * Construction des paramètres de la recherche en fonction des filtres sélectionnés
         * @return {[type]} [description]
         */
        function getSearchParams() {
            var params = {
                page: statCtrl.pagination.page - 1,
                size: statCtrl.pagination.size,
                library: _.pluck(statCtrl.filters.library, 'identifier'),
                pgcnid: statCtrl.filters.pgcnid,
                project: _.pluck(statCtrl.filters.project, 'identifier'),
                lot: _.pluck(statCtrl.filters.lot, 'identifier'),
                train: _.pluck(statCtrl.filters.train, 'identifier'),
                state: _.pluck(statCtrl.columns, 'identifier'),
                from: statCtrl.filters.from,
                to: statCtrl.filters.to,
            };
            return params;
        }

        function changePageSize() {
            statCtrl.pagination.size = statCtrl.pageSize;
            search();
        }

        function loadSearch() {
            statCtrl.filters = NumahopStorageService.getFilter(FILTER_STORAGE_SERVICE_KEY);
            if (!statCtrl.filters) {
                statCtrl.filters = {};
            }
            var savedSize = NumahopStorageService.getPageSize(FILTER_STORAGE_SERVICE_KEY);
            statCtrl.pageSize = savedSize ? savedSize : 10;
            statCtrl.pagination.size = statCtrl.pageSize;
        }

        function saveSearch() {
            NumahopStorageService.savePageSize(FILTER_STORAGE_SERVICE_KEY, statCtrl.pageSize);
            NumahopStorageService.saveFilter(FILTER_STORAGE_SERVICE_KEY, statCtrl.filters);
        }

        function getStatus(item, state) {
            var found = _.find(item.workflow, function (w) {
                return w.key === state;
            });
            return found && found.status;
        }

        function getStatusClass(status) {
            switch (status) {
                case 'NOT_STARTED': // Tâche non commencée
                    return 'label-not-started';
                case 'PENDING': // Tâche en cours
                    return 'label-pending';
                case 'FINISHED': // Tâche accomplie
                    return 'label-success';
                case 'FAILED': // Tâche échouée
                    return 'label-failure';
                case 'TO_WAIT': // Tâche qui sera à attendre
                case 'WAITING': // En attente d'action système
                    return 'label-waiting';
                case 'CANCELED': // Tâche annulée
                case 'TO_SKIP': // Tâche qui ne sera pas accomplie (optionnelle uniquement)
                case 'SKIPPED': // Tâche optionnelle qui a été passée
                default:
                    return 'label-ignore';
            }
        }

        /**
         * Réinitialisation des colonnes
         */
        function loadColumns() {
            statCtrl.columns = NumahopStorageService.getColumns(FILTER_STORAGE_SERVICE_KEY);
            if (!statCtrl.columns) {
                statCtrl.columns = [];
            }
        }

        /**
         * Sauvegarde des colonnes sélectionnées
         */
        function saveColumns() {
            NumahopStorageService.saveColumns(FILTER_STORAGE_SERVICE_KEY, statCtrl.columns);
        }

        /**
         * Visibilité d'une étape
         */
        function isVisible(value) {
            return (
                statCtrl.columns.length === 0 ||
                _.some(statCtrl.columns, function (c) {
                    return c.identifier === value.identifier;
                })
            );
        }

        function getExportUrl() {
            var params = getSearchParams();
            params.wdocunit = true;
            return StatisticsSrvc.getExportUrl(params, 'workflow');
        }
    }
})();
