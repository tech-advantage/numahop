(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('AdvSearchCtrl', AdvSearchCtrl);

    function AdvSearchCtrl($location, gettextCatalog, HistorySrvc, SearchSrvc, $scope, USER_ROLES) {

        var searchCtrl = this;
        searchCtrl.addSearch = addSearch;
        searchCtrl.removeSearch = removeSearch;
        searchCtrl.search = search;
        searchCtrl.setTarget = setTarget;


        init();

        /**
         * Initialisation du contrôleur
         */
        function init() {
            HistorySrvc.add(gettextCatalog.getString("Recherche avancée"));
            initTargets();
            initSearch();
        }

        /**
         * Configuration des critères de recherche
         */
        function initTargets() {
            searchCtrl.config = {
                target: []
            };

            if ($scope.isAuthorized(USER_ROLES.COND_REPORT_HAB0)) {
                searchCtrl.config.target.push({
                    identifier: "CONDREPORT",
                    label: gettextCatalog.getString("Constat d'état")
                });
            }
            if ($scope.isAuthorized(USER_ROLES.DEL_HAB0)) {
                searchCtrl.config.target.push({
                    identifier: "DELIVERY",
                    label: gettextCatalog.getString("Livraison")
                });
            }
            if ($scope.isAuthorized(USER_ROLES.DOC_UNIT_HAB0)) {
                searchCtrl.config.target.push({
                    identifier: "DOCUNIT",
                    label: gettextCatalog.getString("Unité documentaire")
                });
            }
            if ($scope.isAuthorized(USER_ROLES.LOT_HAB3)) {
                searchCtrl.config.target.push({
                    identifier: "LOT",
                    label: gettextCatalog.getString("Lot")
                });
            }
            if ($scope.isAuthorized(USER_ROLES.PROJ_HAB7)) {
                searchCtrl.config.target.push({
                    identifier: "PROJECT",
                    label: gettextCatalog.getString("Projet")
                });
            }
            if ($scope.isAuthorized(USER_ROLES.TRA_HAB3)) {
                searchCtrl.config.target.push({
                    identifier: "TRAIN",
                    label: gettextCatalog.getString("Train")
                });
            }
        }

        /**
         * Initialisation de la recherche, reprise de la recherche précédente
         */
        function initSearch() {
            var lastSearch = SearchSrvc.getSearch();
            searchCtrl.query = {};

            // Reconstruction de la recherche précédente
            if (lastSearch.search) {
                searchCtrl.query.search = parseSearch(lastSearch.search);
                searchCtrl.query.get = lastSearch.get && lastSearch.get.length === 1 ? lastSearch.get[0] : "DOCUNIT";
                searchCtrl.query.fuzzy = lastSearch.fuzzy;
            }
            // Nouvelle recherche
            else {
                newSearch("DOCUNIT");
            }
        }

        /**
         * Nouvelle recherche
         * @param {*} get 
         */
        function newSearch(get) {
            searchCtrl.query.search = [];
            searchCtrl.query.get = get;
            searchCtrl.query.fuzzy = true;
            addSearch();
        }

        /**
         * Exécute la recherche
         */
        function search() {
            SearchSrvc.setSearch({
                get: [searchCtrl.query.get],
                search: formatSearch(searchCtrl.query.search),
                fuzzy: !!searchCtrl.query.fuzzy
            });
            $location.path("/search/results").search({});
        }

        /**
         * Ajout d'un critère de recherche
         */
        function addSearch() {
            searchCtrl.query.search.push({});
        }

        /**
         * Suppression d'un critère de recherche
         * 
         * @param {*} search 
         */
        function removeSearch(search) {
            var idx = searchCtrl.query.search.indexOf(search);
            if (idx >= 0) {
                searchCtrl.query.search.splice(idx, 1);
            }
        }

        function setTarget(get) {
            searchCtrl.query.get = get.identifier;
            newSearch(get.identifier);
        }

        /**
         * Construit un objet à partir de la chaine de recherche
         * @param {*} search 
         */
        function parseSearch(search) {
            return _.map(search, function (s) {
                // s: MUST=docunit-default=recherche
                var pos1 = s.indexOf("=");
                var pos2 = s.indexOf("=", pos1 + 1);
                var q = {};

                if (pos1 >= 0) {
                    q.operator = s.substring(0, pos1);
                }
                if (pos2 >= 0) {
                    q.index = s.substring(pos1 + 1, pos2);
                }
                q.text = s.substring(Math.max(pos1, pos2) + 1);

                return q;
            });
        }

        /**
         * Construit une chaine de caractères à partir d'un objet de recherche
         * @param {*} search 
         */
        function formatSearch(search) {
            return _.chain(search)
                .filter(function (q) {
                    return !!q.text;
                })
                .map(function (q) {
                    return q.operator + "=" + q.index + "=" + q.text;
                })
                .value();
        }
    }
})();