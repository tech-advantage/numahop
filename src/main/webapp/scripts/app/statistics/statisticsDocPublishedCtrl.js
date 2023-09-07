(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('StatisticsDocPublishedCtrl', StatisticsDocPublishedCtrl);

    function StatisticsDocPublishedCtrl($q, codeSrvc, HistorySrvc, gettextCatalog, LibrarySrvc, NumaHopInitializationSrvc, NumahopStorageService, Principal, ProjectSrvc, LotSrvc, StatisticsSrvc, USER_ROLES) {
        var statCtrl = this;

        /** others actions **/
        statCtrl.changePageSize = changePageSize;
        statCtrl.codes = codeSrvc;
        statCtrl.getExportUrl = getExportUrl;
        statCtrl.getPage = getPage;
        statCtrl.reinitFilters = reinitFilters;
        statCtrl.search = search;
        statCtrl.searchValue = searchValue;

        var PAGE_START = 1;
        var FILTER_STORAGE_SERVICE_KEY = 'stat_doc_published';

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
            collections: {
                text: 'name',
                placeholder: gettextCatalog.getString('Regroupements'),
                trackby: 'identifier',
                // Chargement avec mise en cache du résultat
                refresh: function () {
                    if (!statCtrl.config.collections.data) {
                        return $q.all([NumaHopInitializationSrvc.loadCollections(), NumaHopInitializationSrvc.loadOmekaCollections()]).then(function (data) {
                            var collIA = _.map(data[0], function (coll) {
                                coll.group = gettextCatalog.getString('Internet Archive');
                                return coll;
                            });
                            var collOmeka = _.map(data[1], function (coll) {
                                coll.group = gettextCatalog.getString('Omeka');
                                return coll;
                            });
                            return _.chain(collIA)
                                .union(collOmeka)
                                .map(function (l) {
                                    return _.pick(l, 'identifier', 'name', 'group');
                                })
                                .value();
                        });
                    } else {
                        return $q.when(statCtrl.config.collections.data);
                    }
                },
                'refresh-delay': 0, // pas de refresh-delay, car on lit les données en cache après le 1er chargement
                groupby: function (item) {
                    if (item.group) {
                        return item.group;
                    }
                },
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
                        return LibrarySrvc.query({ dto: true }).$promise.then(function (lib) {
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
            types: {
                text: 'label',
                placeholder: gettextCatalog.getString('Type de document'),
                trackby: 'identifier',
                refresh: function ($select) {
                    var searchParams = {
                        page: 0,
                        size: 20,
                        search: $select.search,
                    };
                    return StatisticsSrvc.docUnitTypes(searchParams).$promise.then(function (types) {
                        return _.map(types.content, function (type) {
                            return {
                                identifier: type,
                                label: type,
                            };
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
            HistorySrvc.add(gettextCatalog.getString('Statistiques: documents publiés'));
            statCtrl.loaded = false;
            statCtrl.showLib = Principal.isInRole(USER_ROLES.SUPER_ADMIN) || Principal.isInRole(USER_ROLES.ADMINISTRATION_LIB);

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

            statCtrl.filters.library = [];
            statCtrl.filters.project = [];
            statCtrl.filters.lot = [];
            statCtrl.filters.collection = [];
            statCtrl.filters.type = [];
            statCtrl.filters.inactive = false;
            delete statCtrl.filters.publishedFrom;
            delete statCtrl.filters.publishedTo;

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

            return StatisticsSrvc.docPublished(params, handlePageOfItems).$promise;
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
                lot: _.pluck(statCtrl.filters.lot, 'identifier'),
                type: _.pluck(statCtrl.filters.type, 'identifier'),
                collection: _.pluck(statCtrl.filters.collection, 'identifier'),
                from: statCtrl.filters.publishedFrom,
                to: statCtrl.filters.publishedTo,
            };
            return params;
        }

        /**
         * handlePageOfItems - Gestion d'une page d'entités
         *
         * @param  {type} pageOfReserves la page avec les entités
         */
        function handlePageOfItems(pageOfItems) {
            statCtrl.pagination.totalItems = pageOfItems.totalElements;
            statCtrl.pagination.totalPages = pageOfItems.totalPages;
            statCtrl.pagination.items = pageOfItems.content;
            statCtrl.pagination.busy = false;
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
            params.doc_published = true;
            return StatisticsSrvc.getExportUrl(params, 'docunit');
        }
    }
})();
