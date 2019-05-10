(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('ProjectCtrl', ProjectCtrl);

    function ProjectCtrl($location, $scope, $timeout, $q, $routeParams,
        ProjectSrvc, gettext, gettextCatalog, HistorySrvc, StringTools,
        NumahopStorageService, NumaHopInitializationSrvc, NumahopEditService) {

        $scope.applyFilter = applyFilter;
        $scope.clearSelection = clearSelection;
        $scope.create = create;
        $scope.showDetailUnitDoc = showDetailUnitDoc;
        $scope.doFilter = doFilter;
        $scope.doFilterLibrary = doFilter;
        $scope.reinitFilters = reinitFilters;
        $scope.edit = edit;
        $scope.filterInitial = filterInitial;
        $scope.getFirstLetter = getFirstLetter;
        $scope.nextPage = nextPage;
        $scope.unfilterInitial = unfilterInitial;
        $scope.updateProject = updateProject;
        $scope.showLibraries = NumahopEditService.showFilterLibraries;

        $scope.navigate = navigate;
        $scope.selectedIndex = null;
        $scope.selectedInNew = false;

        $scope.projectInclude = "scripts/app/project/projectEdit.html";
        $scope.project = null;

        var FILTER_STORAGE_SERVICE_KEY = "projects";

        $scope.loaded = false;

        /**
         * Liste des options pour les listes déroulantes
         */
        $scope.options = {
            statuses: [
                { identifier: "CREATED", label: gettext('Créé') },
                { identifier: "ONGOING", label: gettext('En cours') },
                { identifier: "PENDING", label: gettext('En attente') },
                { identifier: "CANCELED", label: gettext('Annulé') },
                { identifier: "CLOSED", label: gettext('Clôturé') }]
        };
        $scope.pagination = {
            items: [],
            totalItems: 0,
            busy: false,
            page: 0
        };
        $scope.newProjects = []; // liste des projets récemment créés

        $scope.filters = {
            libraries: [],
            available: false
        };
        $scope.listFilters = {
            inactive_filter: true,
            library_filter: true,
            initial_filter: true,
            provider_filter: true,
            status_filter: true
        };
        $scope.filterLabels = {
            inactive: "Voir les projets inactifs"
        };

        init();

        /** Initialisation */
        function init() {
            HistorySrvc.add(gettextCatalog.getString("Projets"));
            reinitFilters(false);
            loadOptionsAndFilters();

            $scope.$on("$routeUpdate",
                function ($currentRoute, $previousRoute) {
                    $timeout(function () {
                        $scope.projectInclude = null;
                        $scope.$apply();
                        $scope.projectInclude = "scripts/app/project/projectEdit.html";
                    });
                }
            );
        }

        /****************************************************************/
        /** Init  *******************************************************/
        /****************************************************************/
        function handleRedirect() {
            if (angular.isDefined($routeParams.library)) {
                $scope.filters.libraries.push(_.find($scope.options.libraries, { identifier: $routeParams.library }));
            } else {
                loadFilters();
            }
        }

        /****************************************************************/
        /** Options *****************************************************/
        /****************************************************************/
        function loadOptionsAndFilters() {
            $q.all([NumaHopInitializationSrvc.loadLibraries(),
            NumaHopInitializationSrvc.loadProviders()])
                .then(function (data) {
                    $scope.options.libraries = data[0];
                    $scope.options.providers = data[1];
                    handleRedirect();

                    return nextPage();
                })
                .then(function () {
                    $scope.loaded = true;
                });
        }

        // CRUD
        function create() {
            if ($scope.project) {
                $scope.project._selected = false;
                $scope.project = null;
            }
            $location.path("/project/project").search({ new: true });
        }

        function showDetailUnitDoc() {
            $location.path("/project/all_operations");
        }

        function edit(project, index, selectedInNew) {
            clearSelection();
            $scope.selectedIndex = index;
            $scope.selectedInNew = selectedInNew;

            var search = {};

            if (angular.isDefined(project)) {
                $scope.project = project;
                project._selected = true;
                search = { id: project.identifier };
            }

            $location.path("/project/project").search(search);
        }

        function filterProjects() {
            saveFilters();

            var searchParams = {
                page: $scope.pagination.page,
                search: $scope.filterWith || "",
                active: !$scope.filters.inactive
            };

            if ($scope.filters.initiale) {
                searchParams["initiale"] = $scope.filters.initiale;
            }

            if ($scope.filters.statuses) {
                var statusesIds = _.pluck($scope.filters.statuses, "identifier");
                searchParams["status"] = statusesIds;
            }

            if ($scope.filters.providers) {
                var providersIds = _.pluck($scope.filters.providers, "identifier");
                searchParams["provider"] = providersIds;
            }

            if ($scope.filters.libraries) {
                var librariesIds = _.pluck($scope.filters.libraries, "identifier");
                searchParams["libraries"] = librariesIds;
            }

            return ProjectSrvc.search(searchParams).$promise;
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
            $scope.pagination.busy = true;
            $scope.pagination.last = false;

            filterProjects().then(function (value, responseHeaders) {
                $scope.pagination.page = 1;
                $scope.pagination.items = [];
                $scope.pagination.last = value.last;
                $scope.pagination.totalItems = value.totalElements;

                var idNewProjects = _.pluck($scope.newProjects, 'identifier');

                for (var i = 0; i < value.content.length; i++) {
                    if (idNewProjects.indexOf(value.content[i].identifier) < 0) {
                        if (afterUpdate && value.content[i].identifier === $scope.project.identifier) {
                            $scope.project._selected = true;
                            $scope.pagination.items.push($scope.project);
                        } else {
                            $scope.pagination.items.push(value.content[i]);
                        }
                    }
                    else {
                        // On ne compte pas 2 fois les nouveaux projets rechargés
                        $scope.pagination.totalItems--;
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
                libraries: [],
                users: [],
                statuses: []
            };
            if (reload) {
                doFilter();
            }
        }
        // liste
        function nextPage() {
            if ($scope.pagination.busy || $scope.pagination.last) { return; }
            $scope.pagination.busy = true;

            return filterProjects().then(function (value, responseHeaders) {
                $scope.pagination.page = value.number + 1;
                $scope.pagination.last = value.last;
                $scope.pagination.totalItems = value.totalElements;
                $scope.pagination.items = $scope.pagination.items.concat(value.content);
                $scope.pagination.busy = false;
            });
        }
        function updateProject(identifier, project) {
            if (identifier) {
                _.union($scope.pagination.items, $scope.newProjects).forEach(function (elt, i) {
                    if (elt.identifier === identifier) {
                        elt.name = project.name;
                        $scope.project = elt;
                        doFilter(true);
                        return;
                    }
                });
            }
        }
        function clearSelection() {
            _.union($scope.pagination.items, $scope.newProjects)
                .forEach(function (elt, i) {
                    elt._selected = false;
                });
        }
        function getFirstLetter(project) {
            return StringTools.getFirstLetter(project.name, "OTHER");
        }
        function moveUp() {
            var index;
            if ($scope.selectedIndex > 0) {
                index = $scope.selectedIndex - 1;
                if ($scope.selectedInNew) {
                    edit($scope.newProjects[index], index, true);
                } else {
                    edit($scope.pagination.items[index], index, false);
                }
            } else {
                if (!$scope.selectedInNew) {
                    index = $scope.newProjects.length - 1;
                    if (index >= 0) {
                        edit($scope.newProjects[index], index, true);
                    }
                }
            }
        }
        function moveDown() {
            var index;
            if ($scope.selectedInNew) {
                if ($scope.selectedIndex < $scope.newProjects.length - 1) {
                    index = $scope.selectedIndex + 1;
                    edit($scope.newProjects[index], index, true);
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
