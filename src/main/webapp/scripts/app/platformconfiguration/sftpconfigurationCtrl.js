(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('SFTPConfigurationCtrl', SFTPConfigurationCtrl);

    function SFTPConfigurationCtrl($location, $scope, $timeout, $q, NumahopStorageService, NumaHopInitializationSrvc, SFTPConfigurationSrvc, StringTools) {

        $scope.applyFilter = applyFilter;
        $scope.clearSelection = clearSelection;
        $scope.create = create;
        $scope.doFilter = doFilter;
        $scope.reinitFilters = reinitFilters;
        $scope.edit = edit;
        $scope.filterInitial = filterInitial;
        $scope.getFirstLetter = getFirstLetter;
        $scope.nextPage = nextPage;
        $scope.unfilterInitial = unfilterInitial;

        $scope.configurationInclude = "scripts/app/platformconfiguration/sftpconfigurationEdit.html";
        $scope.configuration = null;

        var FILTER_STORAGE_SERVICE_KEY = "sftp_configurations";

        $scope.filters = {
        };

        /**
         * Liste des options pour les listes déroulantes
         */
        $scope.options = {
            institutions: [],
            libraries: []
        };
        $scope.pagination = {
            items: [],
            totalItems: 0,
            busy: false,
            last: false,
            page: 0
        };
        $scope.newConfigurations = []; // liste des configurations récemment créées

        init();

        /** Initialisation */
        function init() {
            reinitFilters(false);
            loadFilters();
            nextPage();

            $scope.$on("$routeUpdate",
                function ($currentRoute, $previousRoute) {
                    $timeout(function () {
                        $scope.configurationInclude = null;
                        $scope.$apply();
                        $scope.configurationInclude = "scripts/app/platformconfiguration/sftpconfigurationEdit.html";
                    });
                }
            );
        }

        // CRUD
        function create() {
            if ($scope.configuration) {
                $scope.configuration._selected = false;
                $scope.configuration = null;
            }
            $location.path("/platformconfiguration/sftpconfiguration").search({ id: null, mode: "edit" });
        }
        function edit(configuration) {
            clearSelection();

            var search = {};

            if (angular.isDefined(configuration)) {
                $scope.configuration = configuration;
                configuration._selected = true;
                search = { id: configuration.identifier };
            }
            $location.path("/platformconfiguration/sftpconfiguration").search(search);
        }

        function filterConfigurations() {
            saveFilters();

            var searchParams = {
                page: $scope.pagination.page,
                search: $scope.filterWith || ""
            };

            if ($scope.filters.libraries) {
                var librariesIds = _.pluck($scope.filters.libraries, "identifier");
                searchParams["libraries"] = librariesIds;
            }

            return SFTPConfigurationSrvc.search(searchParams).$promise;
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
        function doFilter(afterUpdate) {
            $scope.pagination.page = 0;
            $scope.pagination.last = false;
            $scope.pagination.busy = true;

            filterConfigurations().then(function (value, responseHeaders) {
                $scope.pagination.page = 1;
                $scope.pagination.items = [];
                $scope.pagination.totalItems = value.totalElements;

                var idNewConfigurations = _.pluck($scope.newConfigurations, 'identifier');

                for (var i = 0; i < value.content.length; i++) {
                    if (idNewConfigurations.indexOf(value.content[i].identifier) < 0) {
                        if (afterUpdate && value.content[i].identifier === $scope.configuration.identifier) {
                            $scope.configuration._selected = true;
                            $scope.pagination.items.push($scope.configuration);
                        } else {
                            $scope.pagination.items.push(value.content[i]);
                        }
                    }
                    else {
                        // On ne compte pas 2 fois les nouvelles configurations rechargées
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

            $q.all([NumaHopInitializationSrvc.loadLibraries()])
                .then(function (data) {
                    $scope.options.libraries = data[0];
                    nextPage();
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
            $scope.filters = {
            };
            if (reload) {
                doFilter();
            }
        }
        // liste
        function nextPage() {
            if ($scope.pagination.busy || $scope.pagination.last) { return; }
            $scope.pagination.busy = true;

            filterConfigurations().then(function (value, responseHeaders) {
                $scope.pagination.page = value.number + 1;
                $scope.pagination.totalItems = value.totalElements;
                $scope.pagination.items = $scope.pagination.items.concat(value.content);
                $scope.pagination.last = value.last;
                $scope.pagination.busy = false;
            });
        }
        function clearSelection() {
            _.union($scope.pagination.items, $scope.newConfigurations)
                .forEach(function (elt, i) {
                    elt._selected = false;
                });
        }
        function getFirstLetter(conf) {
            return StringTools.getFirstLetter(conf.label, "OTHER");
        }
    }
})();
