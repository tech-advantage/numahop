(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('WorkflowGroupCtrl', WorkflowGroupCtrl);

    function WorkflowGroupCtrl($location, $scope, $timeout, NumaHopInitializationSrvc, $q,
        WorkflowGroupSrvc, gettextCatalog, HistorySrvc, StringTools, NumahopStorageService) {

        $scope.applyFilter = applyFilter;
        $scope.changeFuzzySearch = changeFuzzySearch;
        $scope.clearSelection = clearSelection;
        $scope.create = create;
        $scope.doFilter = doFilter;
        $scope.reinitFilters = reinitFilters;
        $scope.edit = edit;
        $scope.filterInitial = filterInitial;
        $scope.fuzzySearch = true;
        $scope.getFirstLetter = getFirstLetter;
        $scope.nextPage = nextPage;
        $scope.unfilterInitial = unfilterInitial;

        $scope.detail = "scripts/app/workflow/groupEdit.html";
        $scope.group = null;

        var FILTER_STORAGE_SERVICE_KEY = "workflow_group";

        $scope.filters = {
            libraries: []
        };
        $scope.listFilters = {
            library_filter: true,
            initial_filter: true
        };

        /**
         * Liste des options pour les listes déroulantes
         */
        $scope.options = {
            libraries: []
        };
        $scope.pagination = {
            items: [],
            totalItems: 0,
            busy: false,
            page: 0
        };
        $scope.newEntities = [];

        init();

        /** Initialisation */
        function init() {
            HistorySrvc.add(gettextCatalog.getString("Groupes de workflow"));
            reinitFilters(false);
            loadOptionsAndFilters();

            $scope.$on("$routeUpdate",
                function ($currentRoute, $previousRoute) {
                    $timeout(function () {
                        $scope.detail = null;
                        $scope.$apply();
                        $scope.detail = "scripts/app/workflow/groupEdit.html";
                    });
                }
            );
        }
        function loadOptionsAndFilters() {
            $q.all([NumaHopInitializationSrvc.loadLibraries()])
                .then(function (data) {
                    $scope.options.libraries = data[0];
                    loadFilters();
                    nextPage();
                });
        }

        // CRUD
        function create() {
            if ($scope.group) {
                $scope.group._selected = false;
                $scope.group = null;
            }
            $location.path("/workflow/group").search({ new: true });
        }
        function edit(entity) {
            clearSelection();

            var search = {};

            if (angular.isDefined(entity)) {
                $scope.group = entity;
                entity._selected = true;
                search = { id: entity.identifier };
            }

            $location.path("/workflow/group").search(search);
        }

        function filterGroups() {
            saveFilters();

            var searchParams = {
                page: $scope.pagination.page,
                search: $scope.filterWith || "",
                fuzzySearch: $scope.fuzzySearch
            };

            if ($scope.filters.initiale) {
                searchParams["initiale"] = $scope.filters.initiale;
            }
            if ($scope.filters.libraries) {
                var librariesIds = _.pluck($scope.filters.libraries, "identifier");
                searchParams["libraries"] = librariesIds;
            }
            return WorkflowGroupSrvc.search(searchParams).$promise;
        }
        function filterInitial(initial) {
            $scope.filters.initiale = initial;
            doFilter();
        }
        function unfilterInitial() {
            if (angular.isDefined($scope.filters.initiale)) {
                delete $scope.filters.initiale;
                doFilter();
            }

        }
        function applyFilter(filterWith, event) {
            if (event.type === "keypress" && event.keyCode === 13) {
                doFilter();
            }
        }
        function doFilter(afterUpdate) {
            $scope.pagination.page = 0;
            $scope.pagination.busy = true;

            filterGroups().then(function (value, responseHeaders) {
                $scope.pagination.page = 1;
                $scope.pagination.items = [];
                $scope.pagination.totalItems = value.totalElements;

                var idNewEntities = _.pluck($scope.newEntities, 'identifier');

                for (var i = 0; i < value.content.length; i++) {
                    if (idNewEntities.indexOf(value.content[i].identifier) < 0) {
                        if (afterUpdate && value.content[i].identifier === $scope.group.identifier) {
                            $scope.group._selected = true;
                            $scope.pagination.items.push($scope.group);
                        } else {
                            $scope.pagination.items.push(value.content[i]);
                        }
                    }

                }
                $scope.pagination.busy = false;

                if (afterUpdate) {
                    return;
                }

                if ($scope.pagination.items.length === 1) {
                    edit($scope.pagination.items[0]);
                } else {
                    // réinitialisation de la fiche de droite
                    edit();
                }


            });
        }
        function loadFilters() {
            var filters = NumahopStorageService.getFilter(FILTER_STORAGE_SERVICE_KEY);
            if (filters) {
                $scope.filters = filters.filters;
                if (filters.sortModel) {
                    $scope.sortModel = filters.sortModel;
                }
                unfilterInitial();
            }
            return !!filters;
        }
        function saveFilters() {
            var filters = {};
            filters.filters = $scope.filters;
            filters.sortModel = $scope.sortModel;
            NumahopStorageService.saveFilter(FILTER_STORAGE_SERVICE_KEY, filters);
        }

        function reinitFilters(reload) {
            if (reload) {
                doFilter();
            }
        }
        // liste
        function nextPage() {
            if ($scope.pagination.busy) { return; }
            $scope.pagination.busy = true;

            filterGroups().then(function (value, responseHeaders) {
                $scope.pagination.page = value.number + 1;
                $scope.pagination.totalItems = value.totalElements;
                $scope.pagination.items = $scope.pagination.items.concat(value.content);
                $scope.pagination.busy = false;
            });
        }
        function clearSelection() {
            _.union($scope.pagination.items, $scope.newEntities)
                .forEach(function (elt, i) {
                    elt._selected = false;
                });
        }
        function getFirstLetter(entity) {
            return StringTools.getFirstLetter(entity.name, "OTHER");
        }
        function changeFuzzySearch() {
            $scope.fuzzySearch = !$scope.fuzzySearch;
            if (angular.isDefined($scope.filterWith)) {
                doFilter();
            }
        }
    }
})();
