(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('RecordCtrl', RecordCtrl);

    function RecordCtrl($location, $scope, $timeout, $q,
        RecordSrvc, gettextCatalog, HistorySrvc,DocUnitSrvc,
        NumahopEditService, NumahopStorageService, NumaHopInitializationSrvc) {

        $scope.applyFilter = applyFilter;
        $scope.clearSelection = clearSelection;
        $scope.create = create;
        $scope.doFilter = doFilter;
        $scope.doFilterLibrary = doFilterLibrary;
        $scope.doFilterProject = doFilterProject;
        $scope.reinitFilters = reinitFilters;
        $scope.edit = edit;
        $scope.filterInitial = filterInitial;
        $scope.getFirstLetter = NumahopEditService.getFirstLetter;
        $scope.nextPage = nextPage;
        $scope.unfilterInitial = unfilterInitial;
        $scope.showLibraries = NumahopEditService.showFilterLibraries;

        $scope.navigate = navigate;
        $scope.selectedIndex = null;
        $scope.selectedInNew = false;

        $scope.entityInclude = "scripts/app/document/recordEdit.html";
        $scope.record = null;

        var FILTER_STORAGE_SERVICE_KEY = "bibliographic_record";

        $scope.filters = {
        };
        $scope.listFilters = {
            // state_filter: true,
            has_docunit_filter: true,
            // archived_filter: true,
            // distributed_filter: true,
            library_filter: true,
            last_modified_date_filter: true,
            project_filter: true,
            lot_filter: true,
            created_date_filter: true,
            wkf_status_filter: true
        };


        /**
         * Liste des options pour les listes déroulantes
         */
        $scope.options = {
        };
        $scope.pagination = {
            items: [],
            totalItems: 0,
            busy: false,
            last: false,
            page: 0
        };
        $scope.newEntities = []; // liste des entités récemment créés

        init();

        /** Initialisation */
        function init() {
            HistorySrvc.add(gettextCatalog.getString("Notices"));
            reinitFilters(false);
            loadOptionsAndFilters();

            $scope.$on("$routeUpdate",
                function ($currentRoute, $previousRoute) {
                    $timeout(function () {
                        $scope.entityInclude = null;
                        $scope.$apply();
                        $scope.entityInclude = "scripts/app/document/recordEdit.html";
                    });
                }
            );
        }

        function loadOptionsAndFilters() {
            $q.all([NumaHopInitializationSrvc.loadLibraries(),
            NumaHopInitializationSrvc.loadProjects(),
            NumaHopInitializationSrvc.loadLots(),
            DocUnitSrvc.getConfigFilterStatuses()])
                .then(function (data) {
                    $scope.options.libraries = data[0];
                    $scope.options.projects = data[1];
                    $scope.options.lots = data[2];
                    $scope.options.statuses = data[3];
                    loadFilters();
                    nextPage();
                });
        }

        function refreshFilterLists() {
            var librariesIds = _.pluck($scope.filters.libraries, "identifier");
            var projectsIds = _.pluck($scope.filters.projects, "identifier");
            NumaHopInitializationSrvc.loadProjects(librariesIds)
                .then(function (data) {
                    $scope.options.projects = data;
                    NumaHopInitializationSrvc.loadLots(librariesIds, projectsIds)
                        .then(function (data) {
                            $scope.options.lots = data;
                        });
                });
        }

        function doFilterLibrary() {
            refreshFilterLists();
            doFilter();
        }

        function doFilterProject() {
            refreshFilterLists();
            doFilter();
        }

        // CRUD
        function create() {
            if ($scope.entity) {
                $scope.entity._selected = false;
                $scope.entity = null;
            }
            $location.path("/document/record").search({ id: null, new: "true" });
        }
        function edit(entity, index, selectedInNew) {
            clearSelection();
            $scope.selectedIndex = index;
            $scope.selectedInNew = selectedInNew;

            var search = {};

            if (angular.isDefined(entity)) {
                $scope.entity = entity;
                entity._selected = true;
                search = { id: entity.identifier };
            }

            $location.path("/document/record").search(search);
        }

        function filterEntities(newValue, field) {
            saveFilters();

            var searchParams = {
                page: $scope.pagination.page,
                orphan: $scope.filters.orphan,
                search: $scope.filterWith || ""
            };
            if ($scope.filters.initiale) {
                searchParams["initiale"] = $scope.filters.initiale;
            }
            if ($scope.filters.libraries) {
                var librariesIds = _.pluck($scope.filters.libraries, "identifier");
                searchParams["libraries"] = librariesIds;
            }
            if ($scope.filters.projects) {
                var projectsIds = _.pluck($scope.filters.projects, "identifier");
                searchParams["projects"] = projectsIds;
            }
            if ($scope.filters.lots) {
                var lotsIds = _.pluck($scope.filters.lots, "identifier");
                searchParams["lots"] = lotsIds;
            }
            if ($scope.filters.wkf_statuses) {
                var statusesIds = _.pluck($scope.filters.wkf_statuses, "identifier");
                searchParams["statuses"] = statusesIds;
            }
            if ($scope.filters.createdDateFrom) {
                searchParams["createdDateFrom"] = $scope.filters.createdDateFrom;
            }
            if ($scope.filters.createdDateTo) {
                searchParams["createdDateTo"] = $scope.filters.createdDateTo;
            }
            if ($scope.filters.lastModifiedDateFrom) {
                searchParams["lastModifiedDateFrom"] = $scope.filters.lastModifiedDateFrom;
            }
            if ($scope.filters.lastModifiedDateTo) {
                searchParams["lastModifiedDateTo"] = $scope.filters.lastModifiedDateTo;
            }
            if (field) {
                if (newValue) {
                    searchParams[field] = newValue;
                } else {
                    delete searchParams[field];
                }
            }
            /**
             * search entity
             */
            return RecordSrvc.search(searchParams).$promise;
        }
        function filterInitial(initial) {
            $scope.filters.initiale = initial;
            doFilter();
        }
        function unfilterInitial() {
            if (angular.isDefined($scope.filters.initiale)) {
                delete $scope.filters.initiale;
            }
            doFilter();
        }
        function applyFilter(filterWith, event) {
            if (event.type === "keypress" && event.keyCode === 13) {
                doFilter();
            }
        }
        function doFilter(afterUpdate, newValue, field) {
            $scope.pagination.page = 0;
            $scope.pagination.last = false;
            $scope.pagination.busy = true;

            filterEntities(newValue, field).then(function (value, responseHeaders) {
                $scope.pagination.page = 1;
                $scope.pagination.items = [];
                $scope.pagination.totalItems = value.totalElements;

                var idNewEntities = _.pluck($scope.newEntities, 'identifier');

                for (var i = 0; i < value.content.length; i++) {
                    if (idNewEntities.indexOf(value.content[i].identifier) < 0) {
                        if (afterUpdate && value.content[i].identifier === $scope.entity.identifier) {
                            $scope.entity._selected = true;
                            $scope.pagination.items.push($scope.entity);
                        } else {
                            $scope.pagination.items.push(value.content[i]);
                        }
                    }
                    else {
                        // On ne compte pas 2 fois les nouvelles notices rechargées
                        $scope.pagination.totalItems--;
                    }
                }
                $scope.pagination.last = value.last;
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
            }
            return !!filters;
        }
        function saveFilters(newValue, field) {
            var filters = {};
            filters.filters = $scope.filters;
            filters.sortModel = $scope.sortModel;
            if (field) {
                if (newValue) {
                    filters.filters[field] = newValue;
                } else {
                    delete filters.filters[field];
                }
            }
            NumahopStorageService.saveFilter(FILTER_STORAGE_SERVICE_KEY, filters);
        }

        function reinitFilters(reload) {
            $scope.filters = {
                inactive: false
            };
            if (reload) {
                doFilter();
            }
        }
        // liste
        function nextPage() {
            if ($scope.pagination.busy || $scope.pagination.last) { return; }
            $scope.pagination.busy = true;

            filterEntities().then(function (value, responseHeaders) {
                $scope.pagination.page = value.number + 1;
                $scope.pagination.totalItems = value.totalElements;
                $scope.pagination.items = $scope.pagination.items.concat(value.content);
                $scope.pagination.last = value.last;
                $scope.pagination.busy = false;
            });
        }
        function clearSelection() {
            _.union($scope.pagination.items, $scope.newEntities)
                .forEach(function (elt, i) {
                    elt._selected = false;
                });
        }

        function moveUp() {
            var index;
            if ($scope.selectedIndex > 0) {
                index = $scope.selectedIndex - 1;
                if ($scope.selectedInNew) {
                    edit($scope.newEntities[index], index, true);
                } else {
                    edit($scope.pagination.items[index], index, false);
                }
            } else {
                if (!$scope.selectedInNew) {
                    index = $scope.newEntities.length - 1;
                    if (index >= 0) {
                        edit($scope.newEntities[index], index, true);
                    }
                }
            }
        }
        function moveDown() {
            var index;
            if ($scope.selectedInNew) {
                if ($scope.selectedIndex < $scope.newEntities.length - 1) {
                    index = $scope.selectedIndex + 1;
                    edit($scope.newEntities[index], index, true);
                } else {
                    edit($scope.pagination.items[0], 0, false);
                }
            } else {
                if ($scope.selectedIndex < $scope.pagination.items.length - 1) {
                    index = $scope.selectedIndex + 1;
                    edit($scope.pagination.items[index], index, false);
                }
            }
        }
        function navigate(event) {
            if (event.which === 38) {
                moveUp();
            } else if (event.which === 40) {
                moveDown();
            }
        }
    }
})();
