(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('LotCtrl', LotCtrl);

    function LotCtrl($location, $scope, $timeout, $q, $routeParams, gettextCatalog, HistorySrvc, LotSrvc, StringTools, NumahopStorageService, NumaHopInitializationSrvc) {
        $scope.applyFilter = applyFilter;
        $scope.clearSelection = clearSelection;
        $scope.create = create;
        $scope.doFilter = doFilter;
        $scope.doFilterLibrary = doFilter;
        $scope.doFilterProject = doFilter;
        $scope.filterInitial = filterInitial;
        $scope.unfilterInitial = unfilterInitial;
        $scope.reinitFilters = reinitFilters;
        $scope.edit = edit;
        $scope.getFirstLetter = getFirstLetter;
        $scope.nextPage = nextPage;

        $scope.navigate = navigate;

        $scope.selectedIndex = null;
        $scope.selectedInNew = false;

        $scope.lotInclude = 'scripts/app/lot/lotEdit.html';
        $scope.lot = null;

        var FILTER_STORAGE_SERVICE_KEY = 'lots';

        $scope.loaded = false;

        /**
         * Liste des options pour les listes déroulantes
         */
        $scope.options = {
            categories: [
                { identifier: 'PROVIDER', label: gettextCatalog.getString('Prestataire') },
                { identifier: 'OTHER', label: gettextCatalog.getString('Autre') },
            ],
            projects: [],
            providers: [],
            statuses: LotSrvc.config.status,
            fileFormat: LotSrvc.config.fileFormat,
        };

        $scope.pagination = {
            items: [],
            totalItems: 0,
            busy: false,
            last: false,
            page: 0,
        };
        $scope.newLots = []; // liste des lots récemment créés

        $scope.filters = {
            libraries: [],
            projects: [],
            inactive: false,
            available: false,
        };
        $scope.listFilters = {
            inactive_filter: true,
            library_filter: true,
            project_filter: true,
            status_filter: true,
            file_format_filter: true,
            resolution_filter: true,
            doc_number_filter: true,
        };
        $scope.filterLabels = {
            inactive: 'Voir les lots inactifs',
        };

        init();

        /** Initialisation */
        function init() {
            HistorySrvc.add(gettextCatalog.getString('Lots'));
            reinitFilters(false);
            loadOptionsAndFilters();

            $scope.$on('$routeUpdate', function ($currentRoute, $previousRoute) {
                $timeout(function () {
                    $scope.lotInclude = null;
                    $scope.$apply();
                    $scope.lotInclude = 'scripts/app/lot/lotEdit.html';
                });
            });
        }

        /****************************************************************/
        /** Init  *******************************************************/
        /****************************************************************/
        function handleRedirect() {
            if (angular.isDefined($routeParams.project)) {
                $scope.filters.projects.push(_.find($scope.options.projects, { identifier: $routeParams.project }));
            } else {
                loadFilters();
            }
        }

        /****************************************************************/
        /** Options *****************************************************/
        /****************************************************************/
        function loadOptionsAndFilters() {
            $q.all([NumaHopInitializationSrvc.loadProjects(), NumaHopInitializationSrvc.loadProviders(), NumaHopInitializationSrvc.loadLibraries()]).then(function (data) {
                $scope.options.projects = data[0];
                $scope.options.providers = data[1];
                $scope.options.libraries = data[2];

                handleRedirect();

                nextPage().then(function () {
                    $scope.loaded = true;
                });
            });
        }

        // CRUD
        function create() {
            if ($scope.lot) {
                $scope.lot._selected = false;
                $scope.lot = null;
            }
            $location.path('/lot/lot').search({ new: true });
        }
        function edit(lot, index, selectedInNew) {
            clearSelection();
            $scope.selectedIndex = index;
            $scope.selectedInNew = selectedInNew;

            var search = {};

            if (angular.isDefined(lot)) {
                $scope.lot = lot;
                lot._selected = true;
                search = { id: lot.identifier };
            }

            $location.path('/lot/lot').search(search);
        }

        function filterLots() {
            saveFilters();

            var searchParams = {
                page: $scope.pagination.page,
                search: $scope.filterWith || '',
                active: !$scope.filters.inactive,
            };

            if ($scope.filters.libraries) {
                searchParams['libraries'] = _.pluck($scope.filters.libraries, 'identifier');
            }
            if ($scope.filters.projects) {
                searchParams['projects'] = _.pluck($scope.filters.projects, 'identifier');
            }
            if ($scope.filters.statuses) {
                searchParams['statuses'] = _.pluck($scope.filters.statuses, 'identifier');
            }
            if ($scope.filters.docNumber !== null) {
                searchParams['docNumber'] = $scope.filters.docNumber;
            }
            if ($scope.filters.fileFormat) {
                searchParams['fileFormat'] = _.pluck($scope.filters.fileFormat, 'identifier');
            }
            return LotSrvc.search(searchParams).$promise;
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

            filterLots().then(function (value, responseHeaders) {
                $scope.pagination.page = 1;
                $scope.pagination.items = [];
                $scope.pagination.totalItems = value.totalElements;

                var idNewLots = _.pluck($scope.newLots, 'identifier');

                for (var i = 0; i < value.content.length; i++) {
                    if (idNewLots.indexOf(value.content[i].identifier) < 0) {
                        if (afterUpdate && value.content[i].identifier === $scope.lot.identifier) {
                            $scope.lot._selected = true;
                            $scope.pagination.items.push($scope.lot);
                        } else {
                            $scope.pagination.items.push(value.content[i]);
                        }
                    } else {
                        // On ne compte pas 2 fois les nouveaux lots rechargés
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
                projects: [],
                statuses: [],
                categories: [],
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

            return filterLots().then(function (value, responseHeaders) {
                $scope.pagination.page = value.number + 1;
                $scope.pagination.totalItems = value.totalElements;
                $scope.pagination.items = $scope.pagination.items.concat(value.content);
                $scope.pagination.last = value.last;
                $scope.pagination.busy = false;
            });
        }

        function clearSelection() {
            _.union($scope.pagination.items, $scope.newLots).forEach(function (elt, i) {
                elt._selected = false;
            });
        }
        function getFirstLetter(lot) {
            return StringTools.getFirstLetter(lot.label, 'OTHER');
        }
        function moveUp() {
            var index;
            if ($scope.selectedIndex > 0) {
                index = $scope.selectedIndex - 1;
                if ($scope.selectedInNew) {
                    edit($scope.newLots[index], index, true);
                } else {
                    edit($scope.pagination.items[index], index, false);
                }
            } else {
                if (!$scope.selectedInNew) {
                    index = $scope.newLots.length - 1;
                    if (index >= 0) {
                        edit($scope.newLots[index], index, true);
                    }
                }
            }
        }
        function moveDown() {
            var index;
            if ($scope.selectedInNew) {
                if ($scope.selectedIndex < $scope.newLots.length - 1) {
                    index = $scope.selectedIndex + 1;
                    edit($scope.newLots[index], index, true);
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
