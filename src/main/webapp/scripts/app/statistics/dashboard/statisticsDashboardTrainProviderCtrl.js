(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('StatisticsDashboardTrainProviderCtrl', StatisticsDashboardTrainProviderCtrl);

    function StatisticsDashboardTrainProviderCtrl($q, codeSrvc, gettextCatalog, HistorySrvc, LibrarySrvc, TrainSrvc, NumahopStorageService, Principal, ProjectSrvc, StatisticsSrvc, USER_ROLES) {
        var statCtrl = this;

        statCtrl.code = codeSrvc;
        statCtrl.getExportUrl = getExportUrl;
        statCtrl.reinitFilters = reinitFilters;
        statCtrl.search = search;
        statCtrl.searchValue = searchValue;

        var FILTER_STORAGE_SERVICE_KEY = 'stat_dashboard_train_provider';

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
            trains: {
                text: 'label',
                placeholder: gettextCatalog.getString('Train'),
                trackby: 'identifier',
                refresh: function ($select) {
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
                    return TrainSrvc.search(searchParams).$promise.then(function (lots) {
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
                multiple: true,
                'allow-clear': true,
            },
            status: {
                text: 'label',
                placeholder: gettextCatalog.getString('Statut'),
                trackby: 'identifier',
                data: getStatus(),
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
            HistorySrvc.add(gettextCatalog.getString('Statistiques des trains'));
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
            statCtrl.filters = {};
            search();
        }

        /**
         * Recherche d'entités
         * @return {[type]} [description]
         */
        function search() {
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
            var params = getSearchParams();
            statCtrl.items = StatisticsSrvc.providerTrain(params);
            return statCtrl.items.$promise.then(function (data) {
                _.each(data, function (d) {
                    if (d.sendingDate) {
                        d._sendingDate = moment(d.sendingDate);
                    }
                    if (d.returnDate) {
                        d._returnDate = moment(d.returnDate);
                    }
                });
                return data;
            });
        }

        /**
         * Construction des paramètres de la recherche en fonction des filtres sélectionnés
         * @return {[type]} [description]
         */
        function getSearchParams() {
            var params = {
                library: _.pluck(statCtrl.filters.library, 'identifier'),
                project: _.pluck(statCtrl.filters.project, 'identifier'),
                train: _.pluck(statCtrl.filters.train, 'identifier'),
                status: _.pluck(statCtrl.filters.status, 'identifier'),
            };
            if (statCtrl.filters.sendFrom) {
                params.sendFrom = statCtrl.filters.sendFrom;
            }
            if (statCtrl.filters.sendTo) {
                params.sendTo = statCtrl.filters.sendTo;
            }
            if (statCtrl.filters.returnFrom) {
                params.returnFrom = statCtrl.filters.returnFrom;
            }
            if (statCtrl.filters.returnTo) {
                params.returnTo = statCtrl.filters.returnTo;
            }
            if (statCtrl.filters.insuranceFrom) {
                params.insuranceFrom = statCtrl.filters.insuranceFrom;
            }
            if (statCtrl.filters.insuranceTo) {
                params.insuranceTo = statCtrl.filters.insuranceTo;
            }
            return params;
        }

        function loadSearch() {
            statCtrl.filters = NumahopStorageService.getFilter(FILTER_STORAGE_SERVICE_KEY);
            if (!statCtrl.filters) {
                statCtrl.filters = {};
            }
        }

        function saveSearch() {
            NumahopStorageService.saveFilter(FILTER_STORAGE_SERVICE_KEY, statCtrl.filters);
        }

        function getExportUrl() {
            var params = getSearchParams();
            params.provider_train = true;
            return StatisticsSrvc.getExportUrl(params);
        }

        function getStatus() {
            return _.chain(TrainSrvc.config.status)
                .pairs()
                .map(function (p) {
                    return {
                        identifier: p[0],
                        label: p[1],
                    };
                })
                .value();
        }
    }
})();
