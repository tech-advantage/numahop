(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('SearchResultsCtrl', SearchResultsCtrl);

    function SearchResultsCtrl($q, $timeout, $window, gettextCatalog, HistorySrvc, NumahopStorageService, SearchSrvc, SelectionSrvc) {
        var searchCtrl = this;

        searchCtrl.clearSelection = clearSelection;
        searchCtrl.getClass = getClass;
        searchCtrl.getPage = getPage;
        searchCtrl.isEnabled = isEnabled;
        searchCtrl.isSelected = isSelected;
        searchCtrl.isPageSelected = isPageSelected;
        searchCtrl.reinitPage = reinitPage;
        searchCtrl.search = search;
        searchCtrl.selectionCount = selectionCount;
        searchCtrl.selectPage = selectPage;
        searchCtrl.selectSearch = selectSearch;
        searchCtrl.toggleSelection = toggleSelection;
        searchCtrl.toggleSelectionMode = toggleSelectionMode;
        searchCtrl.unselectPage = unselectPage;

        searchCtrl.selection = SelectionSrvc;

        searchCtrl.searchTypes = ['CONDREPORT', 'DOCUNIT', 'PROJECT', 'LOT', 'TRAIN', 'DELIVERY'];
        searchCtrl.pagination = {
            busy: false,
            size: 5,
        };
        searchCtrl.sizeOptions = [
            { value: 5, label: '5' },
            { value: 10, label: '10' },
            { value: 15, label: '15' },
            { value: 20, label: '20' },
        ];
        searchCtrl.sortOptions = {
            DOCUNIT: [
                { value: 'DOCUNIT:_score=DESC', label: 'Pertinence' },
                { value: 'DOCUNIT:pgcnId=ASC', label: 'PgcnId ', group: 'PgcnId' }, // &#xf160;
                { value: 'DOCUNIT:pgcnId=DESC', label: 'PgcnId ', group: 'PgcnId' }, // &#xf161;
                { value: 'DOCUNIT:label.raw=ASC', label: 'Libellé ', group: 'Libellé' },
                { value: 'DOCUNIT:label.raw=DESC', label: 'Libellé ', group: 'Libellé' },
            ],
            CONDREPORT: [
                { value: 'CONDREPORT:_score=DESC', label: 'Pertinence' },
                { value: 'CONDREPORT:details.sortedType=ASC', label: 'Type ', group: 'Type' },
                { value: 'CONDREPORT:details.sortedType=DESC', label: 'Type ', group: 'Type' },
                { value: 'CONDREPORT:docUnitPgcnId=ASC', label: 'PgcnId ', group: 'UD: PgcnId' },
                { value: 'CONDREPORT:docUnitPgcnId=DESC', label: 'PgcnId ', group: 'UD: PgcnId' },
                { value: 'CONDREPORT:docUnitLabel=ASC', label: 'Libellé ', group: 'UD: Libellé' },
                { value: 'CONDREPORT:docUnitLabel=DESC', label: 'Libellé ', group: 'UD: Libellé' },
            ],
            PROJECT: [
                { value: 'PROJECT:_score=DESC', label: 'Pertinence' },
                { value: 'PROJECT:name.raw=ASC', label: 'Nom ', group: 'Nom' },
                { value: 'PROJECT:name.raw=DESC', label: 'Nom ', group: 'Nom' },
            ],
            LOT: [
                { value: 'LOT:_score=DESC', label: 'Pertinence' },
                { value: 'LOT:label.raw=ASC', label: 'Libellé ', group: 'Libellé' },
                { value: 'LOT:label.raw=DESC', label: 'Libellé ', group: 'Libellé' },
            ],
            TRAIN: [
                { value: 'TRAIN:_score=DESC', label: 'Pertinence' },
                { value: 'TRAIN:label.raw=ASC', label: 'Libellé ', group: 'Libellé' },
                { value: 'TRAIN:label.raw=DESC', label: 'Libellé ', group: 'Libellé' },
            ],
            DELIVERY: [
                { value: 'DELIVERY:_score=DESC', label: 'Pertinence' },
                { value: 'DELIVERY:label.raw=ASC', label: 'Libellé ', group: 'Libellé' },
                { value: 'DELIVERY:label.raw=DESC', label: 'Libellé ', group: 'Libellé' },
            ],
        };
        searchCtrl.loaded = false;

        var FILTER_STORAGE_SERVICE_KEY = 'SearchResultsCtrl';

        init();

        /**
         * Initialisation du contrôleur
         */
        function init() {
            HistorySrvc.add(gettextCatalog.getString('Résultats de la recherche'));
            searchCtrl.params = SearchSrvc.getSearch(); // paramètres de la recherche
            searchCtrl.typesEnabled = searchCtrl.params.get || searchCtrl.searchTypes; // types de données affichées
            searchCtrl.facet = searchCtrl.typesEnabled.length === 1;
            delete searchCtrl.aggs;
            initPaginationCfg(); // initialisation des paginations

            getPage().then(function () {
                searchCtrl.loaded = true;
            });
        }

        /**
         * Initialisation de la pagination
         */
        function initPaginationCfg() {
            var sorts = NumahopStorageService.getFilter(FILTER_STORAGE_SERVICE_KEY);

            _.each(searchCtrl.typesEnabled, function (type) {
                searchCtrl.pagination[type] = {
                    items: [],
                    page: 1,
                    totalItems: 0,
                    // Tri à partir du localStorage, ou par défaut le 1er (pertinence)
                    sort: sorts && sorts[type] ? sorts[type] : searchCtrl.sortOptions[type][0].value,
                };
            });
        }

        /**
         * Recherche sur la page courante
         *
         * @returns
         */
        function getPage(type) {
            if (!searchCtrl.pagination.busy) {
                searchCtrl.pagination.busy = true;
                searchCtrl.params.get = type || searchCtrl.typesEnabled;
                searchCtrl.params.page = type ? searchCtrl.pagination[type].page - 1 : 0;
                searchCtrl.params.size = searchCtrl.pagination.size;
                searchCtrl.params.sort = getParamSort();
                searchCtrl.params.facet = searchCtrl.facet && !searchCtrl.aggs;

                return SearchSrvc.search(searchCtrl.params).$promise.then(function (results) {
                    _.each(searchCtrl.typesEnabled, function (searchType) {
                        var page = results[searchType];
                        if (page) {
                            highlightContent(page.content, page.highlights);

                            searchCtrl.pagination[searchType].items = page.content;
                            searchCtrl.pagination[searchType].totalItems = page.totalElements;
                            searchCtrl.pagination[searchType].totalPages = page.totalPages;

                            if (!searchCtrl.aggs) {
                                searchCtrl.aggs = page.aggs;

                                if (!searchCtrl.aggs) {
                                    searchCtrl.facet = false;
                                }
                            }
                        }
                    });
                    searchCtrl.pagination.busy = false;

                    $timeout(function () {
                        $window.scrollTo(0, 0);
                    });
                    return results;
                });
            } else {
                return $q.when();
            }
        }

        /**
         * Calcul des paramètres de tri, et sauvegarde dans le localStorage
         */
        function getParamSort() {
            var typeArr = angular.isArray(searchCtrl.params.get) ? searchCtrl.params.get : [searchCtrl.params.get];
            var sorts = NumahopStorageService.getFilter(FILTER_STORAGE_SERVICE_KEY);
            if (!sorts) {
                sorts = {};
            }

            var paramSorts = _.chain(typeArr)
                .filter(function (t) {
                    return searchCtrl.pagination[t] && searchCtrl.pagination[t].sort;
                })
                .map(function (t) {
                    var sort = searchCtrl.pagination[t].sort;
                    sorts[t] = sort;
                    return sort;
                })
                .value();

            NumahopStorageService.saveFilter(FILTER_STORAGE_SERVICE_KEY, sorts);

            return paramSorts;
        }

        /**
         * Remplace les champs des résultats de recherche par leur valeur "highlight"
         * @param {*} content
         * @param {*} highlights
         */
        function highlightContent(content, highlights) {
            if (highlights && highlights.length) {
                var rg = new RegExp('<\\/span>\\s*<span class="result-match">', 'g');

                _.each(highlights, function (h) {
                    var found = _.find(content, function (c) {
                        return c.identifier === h.id;
                    });
                    if (angular.isDefined(found)) {
                        found[h.field] = h.text.replace(rg, ' ');
                    }
                });
            }
        }

        /**
         * Relance de la recherche
         */
        function search() {
            _.each(searchCtrl.typesEnabled, function (type) {
                searchCtrl.pagination[type].page = 1;
            });
            getPage();
        }

        /**
         * Relance la recherche, en réinitialisant la pagination
         */
        function reinitPage(filter) {
            searchCtrl.pagination.page = 1;
            searchCtrl.params.filter = filter;
            searchCtrl.getPage();
        }

        /**
         * Recharge la recherche, uniquement sur le type donné
         * @param {*} type
         */
        function selectSearch(type) {
            if (angular.isArray(searchCtrl.params.get) && searchCtrl.params.get.length > 1) {
                searchCtrl.params.get = [type];
            } else {
                delete searchCtrl.params.get;
            }
            delete searchCtrl.params.filter;
            SearchSrvc.setSearch(searchCtrl.params);

            searchCtrl.loaded = false;
            init();
        }

        /**
         * Affichage des blocs
         */
        function getClass() {
            switch (searchCtrl.typesEnabled.length) {
                case 1:
                    return searchCtrl.facet ? 'col-xs-12 col-md-10 col-lg-8 col-md-offset-1 col-lg-offset-2' : 'col-xs-12 col-lg-6 col-lg-offset-3';
                case 2:
                    return 'col-xs-12 col-lg-6';
                case 3:
                    return 'col-xs-12 col-sm-6 col-md-4';
                default:
                    return 'col-xs-12 col-sm-6 col-md-4 col-lg-3';
            }
        }

        /**
         * Le type de recherche est actif
         * @param {*} type
         */
        function isEnabled(type) {
            return searchCtrl.typesEnabled.indexOf(type) >= 0;
        }

        /**
         * Sélection de la page des résultats de recherche
         *
         * @param {*} searchType
         */
        function selectPage(searchType) {
            if (!searchCtrl.pagination[searchType] || !searchCtrl.pagination[searchType].items) {
                return;
            }
            var results = searchCtrl.pagination[searchType].items;
            results = mapResults(results, searchType);

            if (searchCtrl.modeSelect && results) {
                searchCtrl.selection.add('SEARCH_RESULT_' + searchType, results);
            }
        }

        /**
         * Désélection de la page de résultats de recherche
         *
         * @param {*} searchType
         */
        function unselectPage(searchType) {
            if (!searchCtrl.pagination[searchType] || !searchCtrl.pagination[searchType].items) {
                return;
            }
            var results = searchCtrl.pagination[searchType].items;
            results = mapResults(results, searchType);

            if (searchCtrl.modeSelect && results) {
                searchCtrl.selection.del('SEARCH_RESULT_' + searchType, results);
            }
        }

        /**
         * Sélection / Désélection d'un élément
         * @param {*} searchType
         * @param {*} element
         */
        function toggleSelection(searchType, element) {
            if (searchCtrl.modeSelect) {
                searchCtrl.selection.toggle('SEARCH_RESULT_' + searchType, mapResult(element, searchType));
            }
        }

        /**
         * Désélection de tous les éléments
         * @param {*} searchType
         */
        function clearSelection(searchType) {
            if (searchCtrl.modeSelect) {
                searchCtrl.selection.clear('SEARCH_RESULT_' + searchType);
            }
        }

        /**
         * La page est-elle sélectionnée ?
         *
         * @param {*} searchType
         */
        function isPageSelected(searchType) {
            if (!searchCtrl.pagination[searchType] || !searchCtrl.pagination[searchType].items) {
                return;
            }
            var results = searchCtrl.pagination[searchType].items;
            results = mapResults(results, searchType);

            if (searchCtrl.modeSelect && results) {
                return searchCtrl.selection.allSelected('SEARCH_RESULT_' + searchType, results);
            }
        }

        /**
         * L'élément est-il sélectionné ?
         * @param {*} identifier
         */
        function isSelected(searchType, item) {
            return searchCtrl.modeSelect && searchCtrl.selection.isSelected('SEARCH_RESULT_' + searchType, mapResult(item, searchType));
        }

        /**
         * Gestion des sélections
         */
        function toggleSelectionMode() {
            searchCtrl.modeSelect = !searchCtrl.modeSelect;

            _.each(searchCtrl.searchTypes, function (searchType) {
                searchCtrl.selection.clear('SEARCH_RESULT_' + searchType);
            });
        }

        /**
         * Taille de la sélection
         * @param {*} searchType
         */
        function selectionCount(searchType) {
            return searchCtrl.selection.count('SEARCH_RESULT_' + searchType);
        }

        function mapResult(result, searchType) {
            switch (searchType) {
                case 'CONDREPORT':
                    return {
                        identifier: result.docUnitId,
                    };
                default:
                    return _.pick(result, 'identifier');
            }
        }

        function mapResults(results, searchType) {
            switch (searchType) {
                case 'CONDREPORT':
                    return _.map(results, function (r) {
                        return {
                            identifier: r.docUnitId,
                        };
                    });
                default:
                    return _.map(results, function (r) {
                        return _.pick(r, 'identifier');
                    });
            }
        }
    }
})();
