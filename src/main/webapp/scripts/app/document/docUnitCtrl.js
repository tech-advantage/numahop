(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('DocUnitCtrl', DocUnitCtrl);

    function DocUnitCtrl($location, $q, $routeParams, $scope, $timeout, DocUnitSrvc, gettext, gettextCatalog,
        HistorySrvc, NumaHopInitializationSrvc, NumahopStorageService, StringTools) {

        $scope.applyFilter = applyFilter;
        $scope.clearSelection = clearSelection;
        $scope.create = create;
        $scope.doFilter = doFilter;
        $scope.doFilterLibrary = doFilterLibrary;
        $scope.doFilterProject = doFilterProject;
        $scope.reinitFilters = reinitFilters;
        $scope.edit = edit;
        $scope.getFirstLetter = getFirstLetter;
        $scope.nextPage = nextPage;

        $scope.navigate = navigate;

        $scope.selectedIndex = null;
        $scope.selectedInNew = false;

        $scope.docUnitInclude = "scripts/app/document/docUnitEdit.html";
        $scope.docUnit = null;

        $scope.sortOptions = {
            'label': 'Libellé',
            'pgcnId': 'Identifiant PGCN'
        };

        var FILTER_STORAGE_SERVICE_KEY = "doc_unit";
        $scope.filters = {
            sortModel: "label",
            libraries: [],
            wkf_statuses: [],
            available: false,
            archived: false,
            nonArchived: false,
            archivable: false,
            nonArchivable: false,
            distributed: false,
            nonDistributed: false,
            distributable: false,
            nonDistributable: false
        };
        $scope.listFilters = {
            sort_criterion: true,
            has_digital_documents_filter: true,
            library_filter: true,
            last_modified_date_filter: true,
            project_filter: true,
            lot_filter: true,
            wkf_status_filter: true,
            created_date_filter: true,
            archive_filter: true,
            distribution_filter: true,
            inactive_filter: true
        };
        $scope.filterLabels = {
            inactive: "Voir les unités documentaires inactives"
        };

        /**
         * Liste des options pour les listes déroulantes
         */
        $scope.options = {
            libraries: [],
            projects: [],
            lots: [],
            statuses: []
        };
        $scope.pagination = {
            items: [],
            totalItems: 0,
            busy: false,
            page: 0,
            last: false
        };
        $scope.newEntities = []; // liste des unités documentaires récemment créés

        init();

        /** Initialisation */
        function init() {
            HistorySrvc.add(gettextCatalog.getString("Unités documentaires"));
            reinitFilters(false);
            loadOptionsAndFilters();

            $scope.$on("$routeUpdate",
                function ($currentRoute, $previousRoute) {
                    $timeout(function () {
                        $scope.docUnitInclude = null;
                        $scope.$apply();
                        $scope.docUnitInclude = "scripts/app/document/docUnitEdit.html";
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
            if ($scope.docUnit) {
                $scope.docUnit._selected = false;
                $scope.docUnit = null;
            }
            $location.path("/document/docunit").search({ new: true });
        }
        function edit(docUnit, index, selectedInNew) {
            clearSelection();
            $scope.selectedIndex = index;
            $scope.selectedInNew = selectedInNew;

            var search = {};

            if (angular.isDefined(docUnit)) {
                $scope.docUnit = docUnit;
                docUnit._selected = true;
                search = { id: docUnit.identifier };
            }

            $location.path("/document/docunit").search(search);
        }

        function filterDocUnits(newValue, field) {
            saveFilters(newValue, field);

            var searchParams = {
                page: $scope.pagination.page,
                search: $scope.filterWith || "",
                hasDigitalDocuments: $scope.filters.hasDigitalDocuments,
                active: !$scope.filters.inactive,
                archived: $scope.filters.archived,
                nonArchived: $scope.filters.nonArchived,
                archivable: $scope.filters.archivable,
                nonArchivable: $scope.filters.nonArchivable,
                distributed: $scope.filters.distributed,
                nonDistributed: $scope.filters.nonDistributed,
                distributable: $scope.filters.distributable,
                nonDistributable: $scope.filters.nonDistributable,
                sorts: $scope.filters.sortModel
            };

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
                var statuses = _.pluck($scope.filters.wkf_statuses, "identifier");
                searchParams["statuses"] = statuses;
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
            return DocUnitSrvc.search(searchParams).$promise/*.then(function (data) {

                var list = data.content;

                _.each(list, function(docUnit) {
                    console.log(docUnit);
                    //update docUnit.children
                });

            });*/
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

            filterDocUnits(newValue, field).then(function (value, responseHeaders) {
                $scope.pagination.page = 1;
                $scope.pagination.items = [];
                $scope.pagination.totalItems = value.totalElements;
                $scope.pagination.last = value.last;

                var idNewEntities = _.pluck($scope.newEntities, 'identifier');

                var docUnitIdentifiers = _.pluck(value.content, 'identifier');

                //var docUnitIdAlreadyDisplayed = _.pluck($scope.pagination.items, 'identifier');

                for (var i = 0; i < value.content.length; i++) {
                    if (idNewEntities.indexOf(value.content[i].identifier) < 0) {
                        if (afterUpdate && value.content[i].identifier === $scope.docUnit.identifier) {
                            $scope.docUnit._selected = true;
                            $scope.pagination.items.push($scope.docUnit);
                        } else {
                            if(value.content[i].parentIdentifier != null && !_.contains(docUnitIdentifiers, value.content[i].parentIdentifier)){
                                value.content[i].parentIdentifier = null;
                            }
                            $scope.pagination.items.push(value.content[i]);
                        }
                    }
                    else {
                        // On ne compte pas 2 fois les nouvelles uc rechargées
                        $scope.pagination.totalItems--;
                    }
                }
                $scope.pagination.busy = false;

                if (afterUpdate) {
                    return;
                }

                if ($scope.pagination.items.length === 1) {
                    edit($scope.pagination.items[0], 0);
                    $scope.selectedIndex = 0;
                }
                else {
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
                refreshFilterLists();
            }
            if ($routeParams.search) {
                $scope.filterWith = $routeParams.search;
            }
            return !!filters;
        }

        function saveFilters(newValue, field) {
            var filters = {};
            filters.filters = $scope.filters;
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
                libraries: [],
                available: false,
                inactive: false
            };
            $scope.filterWith = null;

            if (reload) {
                doFilter();
            }
        }
        // liste
        function nextPage() {
            if ($scope.pagination.busy || $scope.pagination.last) { return; }
            $scope.pagination.busy = true;

            filterDocUnits().then(function (value, responseHeaders) {
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
                    elt.children.forEach(function(child, i) {
                       child._selected = false;
                    });
                });
        }
        function getFirstLetter(docUnit) {
            if (angular.isUndefined($scope.filters.sortModel)) {
                $scope.filters.sortModel = 'pgcnId';
            }
            return StringTools.getFirstLetter(docUnit[$scope.filters.sortModel], "OTHER");
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
