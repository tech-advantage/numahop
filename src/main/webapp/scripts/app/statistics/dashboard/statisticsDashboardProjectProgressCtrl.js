(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('StatisticsDashboardProjectProgressCtrl', StatisticsDashboardProjectProgressCtrl);

    function StatisticsDashboardProjectProgressCtrl($q, codeSrvc, gettextCatalog, HistorySrvc, LibrarySrvc, NumahopStorageService, Principal, ProjectSrvc, StatisticsSrvc, USER_ROLES) {
        var statCtrl = this;

        statCtrl.changePageSize = changePageSize;
        statCtrl.code = codeSrvc;
        statCtrl.getExportUrl = getExportUrl;
        statCtrl.getPage = getPage;
        statCtrl.isVisible = isVisible;
        statCtrl.reinitFilters = reinitFilters;
        statCtrl.saveColumns = saveColumns;
        statCtrl.search = search;
        statCtrl.searchValue = searchValue;
        statCtrl.status = ProjectSrvc.config.status;

        var PAGE_START = 1;
        var FILTER_STORAGE_SERVICE_KEY = 'stat_dashboard_progress';

        // Objet de pagination
        statCtrl.pagination = {
            items: [],
            totalItems: 0,
            busy: false,
            page: PAGE_START,
        };

        // Taille pagination
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
            columns: {
                data: [
                    { identifier: 'nbDocUnits', label: gettextCatalog.getString('Nb docs'), type: 'number' },
                    { identifier: 'nbDigitalDocs', label: gettextCatalog.getString('Nb numérisés'), type: 'number' },
                    { identifier: 'pctDigitalDocs', label: gettextCatalog.getString('% numérisés'), type: 'percent' },
                    { identifier: 'nbDlvControlled', label: gettextCatalog.getString('Nb contrôlés'), type: 'number' },
                    { identifier: 'pctDlvControlled', label: gettextCatalog.getString('% contrôlés'), type: 'percent' },
                    { identifier: 'nbDlvValidated', label: gettextCatalog.getString('Nb validés (livr.)'), type: 'number' },
                    { identifier: 'pctDlvValidated', label: gettextCatalog.getString('% validés (livr.)'), type: 'percent' },
                    { identifier: 'nbDlvRejected', label: gettextCatalog.getString('Nb rejetés (livr.)'), type: 'number' },
                    { identifier: 'pctDlvRejected', label: gettextCatalog.getString('% rejetés (livr.)'), type: 'percent' },
                    { identifier: 'avgDocDlv', label: gettextCatalog.getString('Moy. livraisons'), type: 'average' },
                    { identifier: 'nbWorkflowValidated', label: gettextCatalog.getString('Nb validés (workflow)'), type: 'number' },
                    { identifier: 'pctWorkflowValidated', label: gettextCatalog.getString('% validés (workflow)'), type: 'percent' },
                    { identifier: 'nbDocDistributable', label: gettextCatalog.getString('Nb diffusables'), type: 'number' },
                    { identifier: 'pctDocDistributable', label: gettextCatalog.getString('% diffusables'), type: 'percent' },
                    { identifier: 'nbDocDistributed', label: gettextCatalog.getString('Nb diffusés'), type: 'number' },
                    { identifier: 'pctDocDistributed', label: gettextCatalog.getString('% diffusés'), type: 'percent' },
                    { identifier: 'nbDocArchivable', label: gettextCatalog.getString('Nb archivables'), type: 'number' },
                    { identifier: 'pctDocArchivable', label: gettextCatalog.getString('% archivables'), type: 'percent' },
                    { identifier: 'nbDocArchived', label: gettextCatalog.getString('Nb archivés'), type: 'number' },
                    { identifier: 'pctDocArchived', label: gettextCatalog.getString('% archivés'), type: 'percent' },
                ],
                text: 'label',
                placeholder: gettextCatalog.getString('Colonne'),
                trackby: 'identifier',
                'allow-clear': true,
                multiple: true,
            },
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
            HistorySrvc.add(gettextCatalog.getString("Statistiques de l'avancement des projets"));
            statCtrl.loaded = false;
            statCtrl.showLib = Principal.isInRole(USER_ROLES.SUPER_ADMIN) || Principal.isInRole(USER_ROLES.ADMINISTRATION_LIB);

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
         * Recherche d'entités
         * @return {[type]} [description]
         */
        function search() {
            statCtrl.pagination.page = 1;
            saveSearch();
            getPage();
        }

        /**
         * Recherche  d'entités sur un changement de période
         * @param {*} from
         * @param {*} to
         */
        function searchValue(updatedField, updatedValue) {
            statCtrl.filters[updatedField] = updatedValue;
            search();
        }

        /**
         * Chargement de la prochaine page d'items
         * @return {[type]} [description]
         */
        function getPage() {
            statCtrl.pagination.busy = true;
            var params = getSearchParams();

            if (statCtrl.filters.lotProgress) {
                return getPageOfLots(params);
            } else {
                return getPageOfProjects(params);
            }
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
                project: _.pluck(statCtrl.filters.project, 'identifier'),
                from: statCtrl.filters.from,
                to: statCtrl.filters.to,
            };
            return params;
        }

        function getPageOfLots(params) {
            // Chargement des stats sur les lots
            return (
                StatisticsSrvc.lotProgress(params)
                    .$promise // Chargement des stats sur les projets
                    .then(function (pageOfLots) {
                        var projectIds = _.chain(pageOfLots.content).pluck('projectIdentifier').uniq().value();

                        return loadProjectByIds(projectIds).then(function (pageOfProjects) {
                            return {
                                lots: pageOfLots,
                                projects: pageOfProjects,
                            };
                        });
                    })
                    // Ajout des stats sur les projets dans les stats par lots
                    .then(function (obj) {
                        mergeProjects(obj.lots, obj.projects);
                        return obj.lots;
                    })
                    // Mise à jour du modèle
                    .then(handlePageOfItems)
            );
        }

        function getPageOfProjects(params) {
            return StatisticsSrvc.projectProgress(params, handlePageOfItems).$promise;
        }

        /**
         * Chargement des projets par identifiants
         */
        function loadProjectByIds(projectIds) {
            if (projectIds.length === 0) {
                return $q.when();
            }
            var params = {
                page: 0,
                size: statCtrl.pagination.size,
                project: projectIds,
            };
            return StatisticsSrvc.projectProgress(params).$promise;
        }

        /**
         * Insertion des données projets dans la page de lots
         * @param {*} pageOfLots
         * @param {*} pageOfProjects
         */
        function mergeProjects(pageOfLots, pageOfProjects) {
            if (pageOfProjects) {
                _.each(pageOfProjects.content, function (project) {
                    // Récupération du 1er lot appartenant au projet
                    var idx = -1;
                    _.find(pageOfLots.content, function (lot, i) {
                        if (lot.projectIdentifier && lot.projectIdentifier === project.projectIdentifier) {
                            idx = i;
                            return true;
                        }
                        return false;
                    });
                    // Insertion de la stat projet juste avant ce 1er lot
                    if (idx >= 0) {
                        pageOfLots.content.splice(idx, 0, project);
                    }
                });
            }
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

        function getExportUrl() {
            var params = getSearchParams();
            params[statCtrl.filters.lotProgress ? 'lotProgress' : 'projectProgress'] = true;
            return StatisticsSrvc.getExportUrl(params);
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
    }
})();
