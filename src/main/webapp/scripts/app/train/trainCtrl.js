(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('TrainCtrl', TrainCtrl);

    function TrainCtrl($location, $scope, $timeout, $q, codeSrvc, gettextCatalog, HistorySrvc,
        StringTools, NumahopStorageService, NumaHopInitializationSrvc, TrainSrvc) {

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
        $scope.updateTrain = updateTrain;

        $scope.navigate = navigate;
        $scope.selectedIndex = null;
        $scope.selectedInNew = false;

        $scope.trainInclude = "scripts/app/train/trainEdit.html";
        $scope.train = null;

        var FILTER_STORAGE_SERVICE_KEY = "trains";

        $scope.filters = {
            libraries: [],
            projects: [],
            inactive: false
        };
        $scope.listFilters = {
            inactive_filter: true,
            library_filter: true,
            project_filter: true,
            status_filter: true,
            doc_number_filter: true,
            provider_sending_date_filter: true,
            return_date_filter: true
        };
        $scope.filterLabels = {
            inactive: "Voir les trains inactifs"
        };
        $scope.loaded = false;

        /**
         * Liste des options pour les listes déroulantes
         */
        $scope.options = {
            categories: [
                { identifier: "PROVIDER", label: gettextCatalog.getString("Prestataire") },
                { identifier: "OTHER", label: gettextCatalog.getString("Autre") }
            ],
            projects: [],
            users: [],
            statuses: [
                { identifier: "CREATED", label: codeSrvc["train.status.CREATED"] },
                { identifier: "IN_PREPARATION", label: codeSrvc["train.status.IN_PREPARATION"] },
                { identifier: "IN_DIGITIZATION", label: codeSrvc["train.status.IN_DIGITIZATION"] },
                { identifier: "RECEIVING_PHYSICAL_DOCUMENTS", label: codeSrvc["train.status.RECEIVING_PHYSICAL_DOCUMENTS"] },
                { identifier: "CLOSED", label: codeSrvc["train.status.CLOSED"] },
                { identifier: "CANCELED", label: codeSrvc["train.status.CANCELED"] }
            ]
        };

        $scope.pagination = {
            items: [],
            totalItems: 0,
            busy: false,
            last: false,
            page: 0
        };
        $scope.newTrains = []; // liste des trains récemment créés

        init();


        /** Initialisation */
        function init() {
            HistorySrvc.add(gettextCatalog.getString("Trains"));
            reinitFilters(false);
            loadOptionsAndFilters();

            $scope.$on("$routeUpdate",
                function ($currentRoute, $previousRoute) {
                    $timeout(function () {
                        $scope.trainInclude = null;
                        $scope.$apply();
                        $scope.trainInclude = "scripts/app/train/trainEdit.html";
                    });
                }
            );
        }

        /****************************************************************/
        /** Options *****************************************************/
        /****************************************************************/
        function loadOptionsAndFilters() {
            $q.all([
                NumaHopInitializationSrvc.loadDocUnits(),
                NumaHopInitializationSrvc.loadProjects(),
                NumaHopInitializationSrvc.loadLibraries()])
                .then(function (data) {
                    $scope.options.docUnits = data[0];
                    $scope.options.projects = data[1];
                    $scope.options.libraries = data[2];
                    loadFilters();

                    nextPage().then(function () {
                        $scope.loaded = false;
                    });
                });
        }

        // CRUD
        function create() {
            if ($scope.train) {
                $scope.train._selected = false;
                $scope.train = null;
            }
            $location.path("/train/train").search({ new: 'true' });
        }
        function edit(train, index, selectedInNew) {
            clearSelection();
            $scope.selectedIndex = index;
            $scope.selectedInNew = selectedInNew;

            var search = {};

            if (angular.isDefined(train)) {
                $scope.train = train;
                train._selected = true;
                search = { id: train.identifier };
            }

            $location.path("/train/train").search(search);
        }

        function filterTrains(newValue, field) {
            saveFilters(newValue, field);

            var searchParams = {
                page: $scope.pagination.page,
                search: $scope.filterWith || "",
                active: !$scope.filters.inactive
            };

            if ($scope.filters.libraries) {
                searchParams["libraries"] = _.pluck($scope.filters.libraries, "identifier");
            }
            if ($scope.filters.projects) {
                var projectsIds = _.pluck($scope.filters.projects, "identifier");
                searchParams["projects"] = projectsIds;
            }
            //
            //            if ($scope.filters.docUnits) {
            //                var docUnitsIds = _.pluck($scope.filters.docUnits, "identifier");
            //                searchParams["docUnits"] = docUnitsIds;
            //            }

            if ($scope.filters.statuses) {
                var statusesIds = _.pluck($scope.filters.statuses, "identifier");
                searchParams["statuses"] = statusesIds;
            }
            if ($scope.filters.providerSendingDateFrom) {
                searchParams["providerSendingDateFrom"] = $scope.filters.providerSendingDateFrom;
            }
            if ($scope.filters.providerSendingDateTo) {
                searchParams["providerSendingDateTo"] = $scope.filters.providerSendingDateTo;
            }
            if ($scope.filters.returnDateFrom) {
                searchParams["returnDateFrom"] = $scope.filters.returnDateFrom;
            }
            if ($scope.filters.returnDateTo) {
                searchParams["returnDateTo"] = $scope.filters.returnDateTo;
            }
            if ($scope.filters.docNumber !== null) {
                searchParams["docNumber"] = $scope.filters.docNumber;
            }
            if (field) {
                if (newValue) {
                    searchParams[field] = newValue;
                } else {
                    delete searchParams[field];
                }
            }

            return TrainSrvc.search(searchParams).$promise;
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

            filterTrains(newValue, field).then(function (value, responseHeaders) {
                $scope.pagination.page = value.number + 1;
                $scope.pagination.items = [];
                $scope.pagination.totalItems = value.totalElements;

                var idNewTrains = _.pluck($scope.newTrains, 'identifier');

                for (var i = 0; i < value.content.length; i++) {
                    if (idNewTrains.indexOf(value.content[i].identifier) < 0) {
                        if (afterUpdate && value.content[i].identifier === $scope.train.identifier) {
                            $scope.train._selected = true;
                            $scope.pagination.items.push($scope.train);
                        } else {
                            $scope.pagination.items.push(value.content[i]);
                        }
                    }
                    else {
                        // On ne compte pas 2 fois les nouveaux trains rechargés
                        $scope.pagination.totalItems--;
                    }
                }
                $scope.pagination.last = value.last;
                $scope.pagination.busy = false;

                if (afterUpdate) {
                    return;
                }

                // réinitialisation de la fiche de droite
                edit();
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
                projects: [],
                docUnits: [],
                statuses: [],
                categories: [],
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

            return filterTrains().then(function (value, responseHeaders) {
                $scope.pagination.page = value.number + 1;
                $scope.pagination.totalItems = value.totalElements;
                $scope.pagination.items = $scope.pagination.items.concat(value.content);
                $scope.pagination.last = value.last;
                $scope.pagination.busy = false;
            });
        }
        function updateTrain(identifier, train) {
            if (identifier) {
                _.union($scope.pagination.items, $scope.newTrains).forEach(function (elt, i) {
                    if (elt.identifier === identifier) {
                        elt.label = train.label;
                        $scope.train = elt;
                        doFilter(true);
                        return;
                    }
                });
            }
        }
        function clearSelection() {
            _.union($scope.pagination.items, $scope.newTrains)
                .forEach(function (elt, i) {
                    elt._selected = false;
                });
        }
        function getFirstLetter(train) {
            return StringTools.getFirstLetter(train.label, "OTHER");
        }
        function moveUp() {
            var index;
            if ($scope.selectedIndex > 0) {
                index = $scope.selectedIndex - 1;
                if ($scope.selectedInNew) {
                    edit($scope.newTrains[index], index, true);
                } else {
                    edit($scope.pagination.items[index], index, false);
                }
            } else {
                if (!$scope.selectedInNew) {
                    index = $scope.newTrains.length - 1;
                    if (index >= 0) {
                        edit($scope.newTrains[index], index, true);
                    }
                }
            }
        }
        function moveDown() {
            var index;
            if ($scope.selectedInNew) {
                if ($scope.selectedIndex < $scope.newTrains.length - 1) {
                    index = $scope.selectedIndex + 1;
                    edit($scope.newTrains[index], index, true);
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
