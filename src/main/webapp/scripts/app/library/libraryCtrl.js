(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('LibraryCtrl', LibraryCtrl);

    function LibraryCtrl($location, $scope, $timeout, LibrarySrvc, gettextCatalog, HistorySrvc, NumahopStorageService, NumahopEditService) {
        $scope.applyFilter = applyFilter;
        $scope.clearSelection = clearSelection;
        $scope.create = create;
        $scope.doFilter = doFilter;
        $scope.reinitFilters = reinitFilters;
        $scope.edit = edit;
        $scope.filterInitial = filterInitial;
        $scope.getFirstLetter = NumahopEditService.getFirstLetter;
        $scope.nextPage = nextPage;
        $scope.unfilterInitial = unfilterInitial;
        $scope.updateLibrary = updateLibrary;

        $scope.navigate = navigate;

        $scope.selectedIndex = null;
        $scope.selectedInNew = false;

        $scope.libraryInclude = 'scripts/app/library/libraryEdit.html';
        $scope.library = null;

        var FILTER_STORAGE_SERVICE_KEY = 'libraries';

        $scope.filters = {
            institutions: [],
            inactive: false,
        };
        $scope.listFilters = {
            initial_filter: true,
            inactive_filter: true,
        };
        $scope.filterLabels = {
            inactive: 'Voir les bibliothèques inactives',
        };

        /**
         * Liste des options pour les listes déroulantes
         */
        $scope.options = {
            institutions: [],
        };
        $scope.pagination = {
            items: [],
            totalItems: 0,
            busy: false,
            last: false,
            page: 0,
        };
        $scope.newLibraries = []; // liste des bibliothèques récemment créés

        init();

        /** Initialisation */
        function init() {
            HistorySrvc.add(gettextCatalog.getString('Bibliothèques'));
            reinitFilters(false);
            loadFilters();
            nextPage();

            $scope.$on('$routeUpdate', function ($currentRoute, $previousRoute) {
                $timeout(function () {
                    $scope.libraryInclude = null;
                    $scope.$apply();
                    $scope.libraryInclude = 'scripts/app/library/libraryEdit.html';
                });
            });
        }

        // CRUD
        function create() {
            if ($scope.library) {
                $scope.library._selected = false;
                $scope.library = null;
            }
            $location.path('/library/library').search({ new: true });
        }
        function edit(library, index, selectedInNew) {
            clearSelection();
            $scope.selectedIndex = index;
            $scope.selectedInNew = selectedInNew;

            var search = {};

            if (angular.isDefined(library)) {
                $scope.library = library;
                library._selected = true;
                search = { id: library.identifier };
            }

            $location.path('/library/library').search(search);
        }

        function filterLibraries() {
            saveFilters();

            var searchParams = {
                page: $scope.pagination.page,
                search: $scope.filterWith || '',
                isActive: !$scope.filters.inactive,
            };

            if ($scope.filters.initiale) {
                searchParams['initiale'] = $scope.filters.initiale;
            }

            if ($scope.filters.institutions) {
                var institutionsIds = _.pluck($scope.filters.institutions, 'identifier');
                searchParams['institutions'] = institutionsIds;
            }

            return LibrarySrvc.search(searchParams).$promise;
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
            if (event.type === 'keypress' && event.keyCode === 13) {
                doFilter();
            }
        }
        function doFilter(afterUpdate) {
            $scope.pagination.page = 0;
            $scope.pagination.last = false;
            $scope.pagination.busy = true;

            filterLibraries().then(function (value, responseHeaders) {
                $scope.pagination.page = 1;
                $scope.pagination.items = [];
                $scope.pagination.totalItems = value.totalElements;

                var idNewLibraries = _.pluck($scope.newLibraries, 'identifier');

                for (var i = 0; i < value.content.length; i++) {
                    if (idNewLibraries.indexOf(value.content[i].identifier) < 0) {
                        if (afterUpdate && value.content[i].identifier === $scope.library.identifier) {
                            $scope.library._selected = true;
                            $scope.pagination.items.push($scope.library);
                        } else {
                            $scope.pagination.items.push(value.content[i]);
                        }
                    } else {
                        // On ne compte pas 2 fois les nouvelles bibliothèques rechargées
                        $scope.pagination.totalItems--;
                    }
                }
                $scope.pagination.last = value.last;
                $scope.pagination.busy = false;

                if (afterUpdate) {
                    return;
                }

                if ($scope.pagination.items.length === 1) {
                    edit($scope.pagination.items[0], 0);
                    $scope.selectedIndex = 0;
                } else {
                    // réinitialisation de la fiche de droite
                    edit();
                    $scope.selectedIndex = null;
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
        function saveFilters() {
            var filters = {};
            filters.filters = $scope.filters;
            filters.sortModel = $scope.sortModel;
            NumahopStorageService.saveFilter(FILTER_STORAGE_SERVICE_KEY, filters);
        }

        function reinitFilters(reload) {
            $scope.filters = {
                inactive: false,
            };
            if (reload) {
                doFilter();
            }
        }
        // liste
        function nextPage() {
            if ($scope.pagination.busy || $scope.pagination.last) {
                return;
            }
            $scope.pagination.busy = true;

            filterLibraries().then(function (value, responseHeaders) {
                $scope.pagination.page = value.number + 1;
                $scope.pagination.totalItems = value.totalElements;
                $scope.pagination.items = $scope.pagination.items.concat(value.content);
                $scope.pagination.last = value.last;
                $scope.pagination.busy = false;
            });
        }
        function updateLibrary(identifier, library) {
            if (identifier) {
                _.union($scope.pagination.items, $scope.newLibraries).forEach(function (elt, i) {
                    if (elt.identifier === identifier) {
                        elt.name = library.name;
                        $scope.library = elt;
                        doFilter(true);
                        return;
                    }
                });
            }
        }
        function clearSelection() {
            _.union($scope.pagination.items, $scope.newLibraries).forEach(function (elt, i) {
                elt._selected = false;
            });
        }
        function moveUp() {
            var index;
            if ($scope.selectedIndex > 0) {
                index = $scope.selectedIndex - 1;
                if ($scope.selectedInNew) {
                    edit($scope.newLibraries[index], index, true);
                } else {
                    edit($scope.pagination.items[index], index, false);
                }
            } else {
                if (!$scope.selectedInNew) {
                    index = $scope.newLibraries.length - 1;
                    if (index >= 0) {
                        edit($scope.newLibraries[index], index, true);
                    }
                }
            }
        }
        function moveDown() {
            var index;
            if ($scope.selectedInNew) {
                if ($scope.selectedIndex < $scope.newLibraries.length - 1) {
                    index = $scope.selectedIndex + 1;
                    edit($scope.newLibraries[index], index, true);
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
