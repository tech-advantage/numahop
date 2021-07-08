(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('UtilsProjectLotCtrl', UtilsProjectLotCtrl);

    function UtilsProjectLotCtrl($location, $q, $scope, $timeout, DocUnitSrvc, gettext, gettextCatalog,
                                 MessageSrvc, LotSrvc, NumaHopInitializationSrvc, TrainSrvc) {

        $scope.doFilter = doFilter;
        $scope.doFilterLibrary = doFilterLibrary;
        $scope.doFilterProject = doFilterProject;
        $scope.doFilterLot = doFilterLot;
        $scope.doFilterTrain = doFilterTrain;
        $scope.reinitFilters = reinitFilters;
        
        $scope.closeLot = closeLot;
        $scope.declotureLot = declotureLot;
        $scope.results = [];
        
        /**
         * accordions
         */
        $scope.accordions = {
            selection_UD: true,
            close_lot: false,
            unclose_lot: false
        };

        var FILTER_STORAGE_SERVICE_KEY = "utils_project_lot";
        $scope.filters = {
            libraries: [],
            lots: []
        };

        /**
         * Liste des options pour les listes déroulantes
         */
        $scope.options = {
            libraries: [],
            projects: [],
            lots: []
        };

        init();

        /** Initialisation */
        function init() {
            reinitFilters(false);
            loadOptionsAndFilters();
        }
        
        function loadOptionsAndFilters() {
            $q.all([NumaHopInitializationSrvc.loadLibraries(),
            NumaHopInitializationSrvc.loadCompleteProjects(),
            NumaHopInitializationSrvc.loadCompleteLots()])
                .then(function (data) {
                    $scope.options.libraries = data[0];
                    $scope.options.projects = data[1];
                    $scope.options.lots = data[2];
                });
        }

        function refreshFilterLists() {
            
            if (! $scope.filters.libraries || $scope.filters.libraries.length === 0) {
                reinitFilters(true);
                return;
            } 
            
            var librariesIds = _.pluck($scope.filters.libraries, "identifier");
            var projectsIds = _.pluck($scope.filters.projects, "identifier");
            NumaHopInitializationSrvc.loadCompleteProjects(librariesIds)
                .then(function (data) {
                    $scope.options.projects = data;
                    
                    NumaHopInitializationSrvc.loadCompleteLots(librariesIds, projectsIds)
                    .then(function (data) {
                        $scope.options.lots = data;
                    });
                });
        }

        function doFilterLibrary() {
            refreshFilterLists();
        }

        function doFilterProject() {
            refreshFilterLists();
            doFilter();
        }
        
        function doFilterLot() {
            doFilter();
        }
        
        function doFilterTrain() {
            doFilter();
        }

        function filterDocUnits(field) {

            var searchParams = {
                search: $scope.filterWith || "",
                sorts: $scope.filters.sortModel
            };
            
            if (field) {
                if (newValue) {
                    searchParams[field] = newValue;
                } else {
                    delete searchParams[field];
                }
            }

            if ($scope.filters.libraries) {
                var librariesIds = _.pluck($scope.filters.libraries, "identifier");
                searchParams["libraries"] = librariesIds;
            }
            if ($scope.filters.projects) {
                var projectsIds = _.pluck($scope.filters.projects, "identifier");
                searchParams["projects"] = projectsIds;
            }
            
            if (searchParams["projects"].length === 0) {
                return;
            }
            
            var lotsIds = [];
            var trainsIds = []; 
                
            if ($scope.filters.lots && $scope.filters.lots.length > 0) {
                searchParams["trains"] = [];
                lotsIds = _.pluck($scope.filters.lots, "identifier");
                searchParams["lots"] = lotsIds;
                
            } 
            return;
        }


        function doFilter(field) {
            
            if (! $scope.filters.projects || $scope.filters.projects.length === 0) {
                return;
            }
            filterDocUnits(field);
        }

        function reinitFilters(reload) {
            $scope.filters = {
                libraries: [],
                projects: [],
                lots: []
            };
            $scope.filterWith = null;

            if (reload) {
                doFilter();
            }
        }

        function clearSelection() {
            _.union($scope.pagination.items, $scope.newEntities)
                .forEach(function (elt, i) {
                    elt._selected = false;
                    elt.children.forEach(function(child, i) {
                       child._selected = false; 
                    });
                });
        }
        
        /**
         * 
         */
        function closeLot() {
            
            if (!$scope.filters.lots || $scope.filters.lots.length === 0) {
                return;
            }
            var lotsIds = _.pluck($scope.filters.lots, "identifier");
            var params = {closelot: true};
            LotSrvc.closeLot(params, lotsIds).$promise
                    .then(function(res) {
                        $scope.results = res;
                    });
        }
        
        
        function declotureLot() {
            
            if (!$scope.filters.lots || $scope.filters.lots.length === 0) {
                return;
            }
            var lotsIds = _.pluck($scope.filters.lots, "identifier");
            var params = {uncloselot: true};
            LotSrvc.uncloseLot(params, lotsIds).$promise
                .then(function(res) {
                    $scope.results = res;
                });
        }
 
    }
})();
