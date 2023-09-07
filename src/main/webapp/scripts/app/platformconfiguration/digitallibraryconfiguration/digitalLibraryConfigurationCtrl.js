(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('DigitalLibraryConfigurationCtrl', DigitalLibraryConfigurationCtrl);

    function DigitalLibraryConfigurationCtrl($location, $scope, $timeout, $q, DigitalLibraryConfigurationSrvc, StringTools, NumahopStorageService, NumaHopInitializationSrvc) {
        $scope.applyFilter = applyFilter;
        $scope.clearSelection = clearSelection;
        $scope.create = create;
        $scope.doFilter = doFilter;
        $scope.reinitFilters = reinitFilters;
        $scope.edit = edit;
        $scope.nextPage = nextPage;
        $scope.updateConfiguration = updateConfiguration;

        $scope.configurationInclude = 'scripts/app/platformconfiguration/digitallibraryconfiguration/digitalLibraryConfigurationEdit.html';
        $scope.configuration = null;

        var FILTER_STORAGE_SERVICE_KEY = 'digital_library_configurations';

        $scope.filters = {};

        /**
         * Liste des options pour les listes déroulantes
         */
        $scope.options = {
            libraries: [],
        };
        $scope.pagination = {
            items: [],
            totalItems: 0,
            busy: false,
            last: false,
            page: 0,
        };

        init();

        /** Initialisation */
        function init() {
            reinitFilters(false);
            loadFilters();
            doFilter();

            $scope.$on('$routeUpdate', function ($currentRoute, $previousRoute) {
                $timeout(function () {
                    $scope.configurationInclude = null;
                    $scope.$apply();
                    $scope.configurationInclude = 'scripts/app/platformconfiguration/digitallibraryconfiguration/digitalLibraryConfigurationEdit.html';
                });
            });
        }

        // CRUD
        function create() {
            if ($scope.configuration) {
                $scope.configuration._selected = false;
                $scope.configuration = null;
            }
            $location.path('/platformconfiguration/digitallibraryconfiguration').search({ id: null, new: true });
        }
        function edit(configuration) {
            clearSelection();

            var search = {};

            if (angular.isDefined(configuration)) {
                $scope.configuration = configuration;
                configuration._selected = true;
                search = { id: configuration.identifier };
            }
            $location.path('/platformconfiguration/digitallibraryconfiguration').search(search);
        }

        function search() {
            saveFilters();

            var searchParams = {
                page: $scope.pagination.page,
                search: $scope.filterWith || '',
            };

            if ($scope.filters.libraries) {
                var librariesIds = _.pluck($scope.filters.libraries, 'identifier');
                searchParams['libraries'] = librariesIds;
            }

            return DigitalLibraryConfigurationSrvc.search(searchParams).$promise;
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

            search().then(function (value, responseHeaders) {
                $scope.pagination.page = value.number + 1;
                $scope.pagination.totalItems = value.totalElements;
                $scope.pagination.items = value.content;
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

            $q.all([NumaHopInitializationSrvc.loadLibraries()]).then(function (data) {
                $scope.options.libraries = data[0];
            });

            return !!filters;
        }
        function saveFilters() {
            var filters = {};
            filters.filters = $scope.filters;
            filters.sortModel = $scope.sortModel;
            NumahopStorageService.saveFilter(FILTER_STORAGE_SERVICE_KEY, filters);
        }

        function reinitFilters(reload) {
            $scope.filters = {};
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

            search().then(function (value, responseHeaders) {
                $scope.pagination.page = value.number + 1;
                $scope.pagination.totalItems = value.totalElements;
                $scope.pagination.items = $scope.pagination.items.concat(value.content);
                $scope.pagination.last = value.last;
                $scope.pagination.busy = false;
            });
        }
        function updateConfiguration(identifier, configuration) {
            if (identifier) {
                $scope.pagination.items.forEach(function (elt, i) {
                    if (elt.identifier === identifier) {
                        elt.label = configuration.label;
                        $scope.configuration = elt;
                        doFilter(true);
                        return;
                    }
                });
            }
        }
        function clearSelection() {
            $scope.pagination.items.forEach(function (elt, i) {
                elt._selected = false;
            });
        }
    }
})();
