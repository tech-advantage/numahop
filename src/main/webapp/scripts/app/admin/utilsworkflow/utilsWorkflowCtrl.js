(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('UtilsWorkflowCtrl', UtilsWorkflowCtrl);

    function UtilsWorkflowCtrl($location, $q, $scope, $timeout, DocUnitSrvc, gettext, gettextCatalog, WorkflowSrvc, NumaHopInitializationSrvc, TrainSrvc) {
        //$scope.clearSelection = clearSelection;
        $scope.doFilter = doFilter;
        $scope.doFilterLibrary = doFilterLibrary;
        $scope.doFilterProject = doFilterProject;
        $scope.doFilterLot = doFilterLot;
        $scope.doFilterTrain = doFilterTrain;
        $scope.reinitFilters = reinitFilters;

        $scope.endAllDocWorkflows = endAllDocWorkflows;
        $scope.validDocWorkflowState = validDocWorkflowState;
        $scope.reinitDocWorkflowState = reinitDocWorkflowState;

        $scope.selectStateToValid = selectStateToValid;
        $scope.selectStateToReinit = selectStateToReinit;

        $scope.wkfFilters = {
            validState: {
                label: undefined,
                identifier: undefined,
            },
            reinitState: {
                label: undefined,
                identifier: undefined,
            },
        };

        /**
         * accordions
         */
        $scope.accordions = {
            selection_UD: true,
            end_wkf: false,
            valid_state: false,
            reinit_state: false,
        };

        var FILTER_STORAGE_SERVICE_KEY = 'utils_workflow';
        $scope.filters = {
            libraries: [],
            docUnits: [],
        };

        /**
         * Liste des options pour les listes déroulantes
         */
        $scope.options = {
            libraries: [],
            projects: [],
            lots: [],
            trains: [],
            docUnits: [],
            statuses: [],
            statesToValid: {
                data: WorkflowSrvc.getAdminStatesToValid(),
                text: 'label',
                placeholder: gettextCatalog.getString('Etat'),
                trackby: 'identifier',
                multiple: true,
                'allow-clear': true,
            },
            statesToReinit: {
                data: WorkflowSrvc.getAdminStatesToReinit(),
                text: 'label',
                placeholder: gettextCatalog.getString('Etat'),
                trackby: 'identifier',
                multiple: true,
                'allow-clear': true,
            },
        };

        init();

        /** Initialisation */
        function init() {
            reinitFilters(false);
            loadOptionsAndFilters();
        }

        function loadOptionsAndFilters() {
            $q.all([
                NumaHopInitializationSrvc.loadLibraries(),
                NumaHopInitializationSrvc.loadProjects(),
                NumaHopInitializationSrvc.loadLots(),
                NumaHopInitializationSrvc.loadTrains(),
                DocUnitSrvc.getConfigFilterStatuses(),
            ]).then(function (data) {
                $scope.options.libraries = data[0];
                $scope.options.projects = data[1];
                $scope.options.lots = data[2];
                $scope.options.trains = data[3];
                $scope.options.statuses = data[4];
            });
        }

        function refreshFilterLists() {
            if (!$scope.filters.libraries || $scope.filters.libraries.length === 0) {
                reinitFilters(true);
                return;
            }

            var librariesIds = _.pluck($scope.filters.libraries, 'identifier');
            var projectsIds = _.pluck($scope.filters.projects, 'identifier');
            NumaHopInitializationSrvc.loadProjects(librariesIds).then(function (data) {
                $scope.options.projects = data;

                NumaHopInitializationSrvc.loadLots(librariesIds, projectsIds).then(function (data) {
                    $scope.options.lots = data;
                });

                if (projectsIds.length > 0) {
                    var params = { projectsIds: projectsIds };
                    TrainSrvc.findByProjectIds(params).$promise.then(function (data) {
                        $scope.options.trains = data;
                    });
                }
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
                search: $scope.filterWith || '',
                sorts: $scope.filters.sortModel,
            };

            if (field) {
                if (newValue) {
                    searchParams[field] = newValue;
                } else {
                    delete searchParams[field];
                }
            }

            if ($scope.filters.libraries) {
                var librariesIds = _.pluck($scope.filters.libraries, 'identifier');
                searchParams['libraries'] = librariesIds;
            }
            if ($scope.filters.projects) {
                var projectsIds = _.pluck($scope.filters.projects, 'identifier');
                searchParams['projects'] = projectsIds;
            }

            if (searchParams['projects'].length === 0) {
                return;
            }

            var lotsIds = [];
            var trainsIds = [];

            if ($scope.filters.lots && $scope.filters.lots.length > 0) {
                searchParams['trains'] = [];
                lotsIds = _.pluck($scope.filters.lots, 'identifier');
                searchParams['lots'] = lotsIds;
            } else if ($scope.filters.trains && $scope.filters.trains.length > 0) {
                searchParams['lots'] = [];
                trainsIds = _.pluck($scope.filters.trains, 'identifier');
                searchParams['trains'] = trainsIds;
            }

            if ($scope.filters.statuses) {
                var statuses = _.pluck($scope.filters.statuses, 'identifier');
                searchParams['statuses'] = statuses;
            }

            return DocUnitSrvc.searchAsMinList(searchParams).$promise;
        }

        function doFilter(field) {
            if (!$scope.filters.projects || $scope.filters.projects.length === 0) {
                return;
            }
            filterDocUnits(field).then(function (data) {
                $scope.options.docUnits = data;
            });
        }

        function reinitFilters(reload) {
            $scope.filters = {
                libraries: [],
                projects: [],
                lots: [],
                trains: [],
                docUnits: [],
            };
            $scope.filterWith = null;

            if (reload) {
                doFilter();
            }
        }

        function clearSelection() {
            _.union($scope.pagination.items, $scope.newEntities).forEach(function (elt, i) {
                elt._selected = false;
                elt.children.forEach(function (child, i) {
                    child._selected = false;
                });
            });
        }

        /**
         * Termine completement le workflow de chaque UD sélectionnée.
         */
        function endAllDocWorkflows() {
            if (!$scope.filters.docUnits || $scope.filters.docUnits.length === 0) {
                return;
            }
            var docUnitIds = _.pluck($scope.filters.docUnits, 'identifier');
            var params_wkf = { endAllDocWorkflows: true };
            WorkflowSrvc.endAllDocWorkflows(params_wkf, docUnitIds);
        }

        function selectStateToValid(value) {
            $scope.wkfFilters.validState = value;
        }

        function selectStateToReinit(value) {
            $scope.wkfFilters.reinitState = value;
        }

        /**
         *  Force la validation d'une étape du workflow.
         */
        function validDocWorkflowState() {
            if (!$scope.filters.docUnits || $scope.filters.docUnits.length === 0 || !$scope.wkfFilters.validState.identifier) {
                return;
            }
            var docUnitIds = _.pluck($scope.filters.docUnits, 'identifier');
            docUnitIds.push($scope.wkfFilters.validState.identifier);
            var params_wkf = { validDocWorkflowState: true };
            WorkflowSrvc.validDocWorkflowState(params_wkf, docUnitIds);
        }

        /**
         *  Force l'annulation d'une étape du workflow.
         */
        function reinitDocWorkflowState() {
            if (!$scope.filters.docUnits || $scope.filters.docUnits.length === 0 || !$scope.wkfFilters.reinitState.identifier) {
                return;
            }
            var docUnitIds = _.pluck($scope.filters.docUnits, 'identifier');
            docUnitIds.push($scope.wkfFilters.reinitState.identifier);
            var params_wkf = { reinitDocWorkflowState: true };
            WorkflowSrvc.reinitDocWorkflowState(params_wkf, docUnitIds);
        }
    }
})();
