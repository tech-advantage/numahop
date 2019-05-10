(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('StatisticsWorkflowProviderCtrl', StatisticsWorkflowProviderCtrl);

    function StatisticsWorkflowProviderCtrl($q, codeSrvc, gettextCatalog, HistorySrvc, LibrarySrvc,
        NumaHopInitializationSrvc, NumahopStorageService, StatisticsSrvc) {

        var statCtrl = this;

        statCtrl.code = codeSrvc;
        statCtrl.getExportUrl = getExportUrl;
        statCtrl.reinitFilters = reinitFilters;
        statCtrl.search = search;
        statCtrl.searchValue = searchValue;

        var FILTER_STORAGE_SERVICE_KEY = "stat_workflow_provider";

        /**
         * Listes déroulantes
         */
        statCtrl.config = {
            libraries: {
                text: "name",
                placeholder: gettextCatalog.getString("Bibliothèque"),
                trackby: "identifier",
                // Chargement avec mise en cache du résultat
                refresh: function () {
                    if (!statCtrl.config.libraries.data) {
                        statCtrl.config.libraries.data = LibrarySrvc.query({ dto: true });
                        return statCtrl.config.libraries.data.$promise
                            .then(function (lib) {
                                return _.map(lib, function (l) {
                                    return _.pick(l, "identifier", "name");
                                });
                            });
                    }
                    else {
                        return $q.when(statCtrl.config.libraries.data);
                    }
                },
                'refresh-delay': 0, // pas de refresh-delay, car on lit les données en cache après le 1er chargement
                'allow-clear': true,
                multiple: true
            },
            providers: {
                text: "fullName",
                placeholder: gettextCatalog.getString("Prestataire"),
                trackby: "identifier",
                refresh: function ($select) {
                    // Gestion du cas où la liste est réinitialisée manuellement (search est indéfini)
                    if (angular.isUndefined($select.search)) {
                        return $q.when([]);
                    }
                    if (statCtrl.filters.library && statCtrl.filters.library.length > 0) {
                        var promises = _.map(statCtrl.filters.library, function (lib) {
                            return NumaHopInitializationSrvc.loadProvidersForLibrary(lib.identifier);
                        });
                        return $q.all(promises).then(_.union).then(_.flatten);
                    }
                    else {
                        return NumaHopInitializationSrvc.loadProviders();
                    }
                },
                'refresh-delay': 300,
                'allow-clear': true,
                multiple: true
            }
        };

        init();


        /**
         * Initialisation du controleur
         * @return {[type]} [description]
         */
        function init() {
            HistorySrvc.add(gettextCatalog.getString("Statistiques des prestataires"));
            statCtrl.loaded = false;

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
         * Recherche d'entité
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
            statCtrl.items = StatisticsSrvc.providerDelivery(params);
            return statCtrl.items.$promise;
        }

        /**
         * Construction des paramètres de la recherche en fonction des filtres sélectionnés
         * @return {[type]} [description]
         */
        function getSearchParams() {
            var params = {
                delivery: _.pluck(statCtrl.filters.delivery, "identifier"),
                library: _.pluck(statCtrl.filters.library, "identifier"),
                lot: _.pluck(statCtrl.filters.lot, "identifier"),
                provider: _.pluck(statCtrl.filters.provider, "identifier"),
                from: statCtrl.filters.deliveredFrom,
                to: statCtrl.filters.deliveredTo
            };
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
            params.provider_delivery = true;
            return StatisticsSrvc.getExportUrl(params, "delivery");
        }
    }
})();
