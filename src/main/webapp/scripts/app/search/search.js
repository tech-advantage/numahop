(function () {
    'use strict';

    angular.module('numaHopApp')
        .config(function ($routeProvider, gettext, USER_ROLES) {

            $routeProvider
                .when('/search/advanced', {
                    templateUrl: 'scripts/app/search/advSearch.html',
                    controller: 'AdvSearchCtrl',
                    controllerAs: 'searchCtrl',
                    title: gettext("Recherche avancée"),
                    access: {
                        authorizedRoles: [USER_ROLES.DOC_UNIT_HAB0]
                    }
                }).when('/search/results', {
                    templateUrl: 'scripts/app/search/searchResults.html',
                    controller: 'SearchResultsCtrl',
                    controllerAs: 'searchCtrl',
                    title: gettext("Résultats de la recherche"),
                    access: {
                        authorizedRoles: [USER_ROLES.DOC_UNIT_HAB0]
                    }
                });
        });
})();