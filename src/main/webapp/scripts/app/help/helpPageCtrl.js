(function() {
    'use strict';

    angular.module('numaHopApp.controller').controller('HelpPageCtrl', NotificationCtrl);

    function NotificationCtrl($location, $scope, $timeout, $routeParams, HelpPageSrvc, gettextCatalog, HistorySrvc, codeSrvc, USER_ROLES) {

        $scope.applyFilter = applyFilter;
        $scope.doFilter = doFilter;
        $scope.create = create;
        $scope.edit = edit;
        $scope.filterOn = filterOn;
        $scope.initFilters = initFilters;

        $scope.helpInclude = "scripts/app/help/helpPageEdit.html";
        $scope.helpPage = undefined;
        $scope.getHelpPageTypeLabel = function(value) {
            return codeSrvc['helpPageType.' + value];
        };

        // filtres sélectionnés
        $scope.filter = {
            modules : [],
            types : [],
            search : undefined
        };
        // filtres disponibles
        initFilters();

        init();

        /** Initialisation */
        function init() {
            HistorySrvc.add(gettextCatalog.getString("Aide"));

            doFilter();

            $scope.$on("$routeUpdate", function($currentRoute, $previousRoute) {
                $timeout(function() {
                    $scope.helpInclude = null;
                    $scope.$apply();
                    $scope.helpInclude = "scripts/app/help/helpPageEdit.html";
                });
            });
        }

        function create() {
            if ($scope.helpPage) {
                $scope.helpPage._selected = false;
                $scope.helpPage = null;
            }
            $location.path("/help").search({
                id : null,
                mode : "edit"
            });
        }

        function edit(helpPage) {
            if ($scope.helpPage) {
                $scope.helpPage._selected = false;
            }

            var search = {};

            if (angular.isDefined(helpPage)) {
                $scope.helpPage = helpPage;
                $scope.helpPage._selected = true;
                search = {
                    id : helpPage.identifier
                };
            }

            $location.path("/help").search(search);
        }

        function filterHelpPages() {
            var searchParams = {};
            angular.forEach($scope.filter, function(value, key) {
                if (angular.isArray(value)) {
                    searchParams[key] = [];
                    for (var i = 0; i < value.length; i++) {
                        searchParams[key].push(value[i]);
                    }
                } else {
                    searchParams[key] = value;
                }
            });

            return HelpPageSrvc.search(searchParams).$promise;
        }
        function applyFilter(filterWith, event) {
            if (event.type === "keypress" && event.keyCode === 13) {
                doFilter();
            }
        }
        function doFilter() {
            $scope.busy = true;

            filterHelpPages().then(function(values, responseHeaders) {
                $scope.modules = values;
                $scope.helpPages = [];

                var selectedHelpPageId = $routeParams.id;

                if ($scope.helpPage) {
                    selectedHelpPageId = $scope.helpPage.identifier;
                }

                $scope.helpPage = undefined;
                for (var i = 0; i < values.length; i++) {
                    visitPages(values[i].pages, function(page) {
                        if ($scope.isAuthorized(USER_ROLES.SUPER_ADMIN)) {
                            $scope.helpPages.push(page);
                        } else if (page.type === 'CUSTOM') {
                            $scope.helpPages.push(page);
                        }
                    });
                    $scope.helpPage = visitPages(values[i].pages, function(page) {
                        if (page.identifier === selectedHelpPageId) {
                            return page;
                        }
                    });
                }

                if ($scope.modules.length === 1 && $scope.modules[0].pages.length === 1) {
                    edit($scope.modules[0].pages[0]);
                } else if (angular.isDefined($scope.helpPage)) {
                    edit($scope.helpPage);
                } else if (angular.isDefined($routeParams.tag)) {
                    HelpPageSrvc.searchByTag({
                        tag : $routeParams.tag
                    }, function(page) {
                        var found = undefined;
                        for (var i = 0; i < $scope.modules.length; i++) {
                            found = visitPages($scope.modules[i].pages, function(p) {
                                if (p.identifier === page.identifier) {
                                    return p;
                                }
                            });
                        }
                        edit(found);
                    });
                } else {
                    edit();
                }
                $scope.busy = false;
            });
        }

        function visitPages(pages, doExec) {
            for (var i = 0; i < pages.length; i++) {
                var result = doExec(pages[i]);
                if (angular.isDefined(result)) {
                    return result;
                }
                if (angular.isDefined(pages[i].children)) {
                    result = visitPages(pages[i].children, doExec);
                    if (angular.isDefined(result)) {
                        return result;
                    }
                }
            }
        }

        function filterOn(field, value) {
            var filters = $scope.filter[field];
            if (angular.isDefined(value)) {
                if (_.contains(filters, value)) {
                    filters.splice(filters.indexOf(value), 1);
                } else {
                    filters.push(value);
                }
            } else {
                $scope.filter[field] = [];
            }
            doFilter();
        }

        function initFilters() {
            $scope.filtersAvailable = {};
            $scope.filtersAvailable.modules = HelpPageSrvc.findAllModules();
            $scope.filtersAvailable.helpPageTypes = [{
                code : 'PGCN',
                label : codeSrvc['helpPageType.PGCN']
            }, {
                code : 'CUSTOM',
                label : codeSrvc['helpPageType.CUSTOM']
            }];
        }

    }
})();
