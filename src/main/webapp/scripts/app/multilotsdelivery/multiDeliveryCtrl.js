(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('MultiDeliveryCtrl', MultiDeliveryCtrl);

    function MultiDeliveryCtrl($location, $scope, $timeout, $q, $routeParams, gettextCatalog, HistorySrvc,
        StringTools, NumahopStorageService, NumaHopInitializationSrvc,
        MultiDeliverySrvc, DeliverySrvc) {

        $scope.applyFilter = applyFilter;
        $scope.clearSelection = clearSelection;
        $scope.create = create;
        $scope.doFilter = doFilter;
        $scope.doFilterProject = doFilter;
        $scope.doFilterLibrary = doFilter;
        $scope.reinitFilters = reinitFilters;
        $scope.edit = edit;
        $scope.getFirstLetter = getFirstLetter;
        $scope.nextPage = nextPage;
        $scope.updateDelivery = updateDelivery;

        $scope.navigate = navigate;

        $scope.selectedIndex = null;
        $scope.selectedInNew = false;

        $scope.deliveryInclude = "scripts/app/multilotsdelivery/multiDeliveryEdit.html";
        $scope.multiDelivery = null;

        var FILTER_STORAGE_SERVICE_KEY = "multi_deliveries";

        $scope.open1 = function () {
            $scope.date1.opened = true;
        };

        $scope.open2 = function () {
            $scope.date2.opened = true;
        };

        $scope.date1 = {
            opened: false
        };

        $scope.date2 = {
            opened: false
        };

        $scope.listFilters = {
            library_filter: true,
            project_filter: true,
            status_filter: true,
            delivery_date_filter: true,
            provider_filter: true
        };

        /**
         * Liste des options pour les listes déroulantes
         */
        $scope.options = {
            statuses: DeliverySrvc.config.status
        };

        $scope.pagination = {
            items: [],
            totalItems: 0,
            busy: false,
            last: false,
            page: 0
        };
        $scope.newDeliveries = []; // liste des livraisons récemment créées

        init();


        /** Initialisation */
        function init() {
            HistorySrvc.add(gettextCatalog.getString("Livraisons multi-lots"));
            reinitFilters(false);
            loadOptionsAndFilters();

            $scope.$on("$routeUpdate",
                function ($currentRoute, $previousRoute) {
                    $timeout(function () {
                        $scope.deliveryInclude = null;
                        $scope.$apply();
                        $scope.deliveryInclude = "scripts/app/multilotsdelivery/multiDeliveryEdit.html";
                    });
                }
            );
            $scope.$on("predeliver", function (event, params) {
                $scope.deliveryInclude = "scripts/app/multilotsdelivery/multiDeliveryPrevalidate.html";

            });
            $scope.$on("backToEdit", function (event, delivering) {
                $scope.delivering = delivering;
                $scope.deliveryInclude = "scripts/app/multilotsdelivery/multiDeliveryEdit.html";
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
            $q.all([
                NumaHopInitializationSrvc.loadProjects(), NumaHopInitializationSrvc.loadProviders(), NumaHopInitializationSrvc.loadLibraries()])
                .then(function (data) {
                    $scope.options.projects = data[0];
                    $scope.options.providers = data[1];
                    $scope.options.libraries = data[2];
                    handleRedirect();
                    nextPage();
                });
        }

        // CRUD
        function create() {
            if ($scope.multiDelivery) {
                $scope.multiDelivery._selected = false;
                $scope.multiDelivery = null;
            }
            $location.path("/multilotsdelivery/multidelivery").search({ new: true });
        }
        function edit(multiDelivery, index, selectedInNew) {
            clearSelection();
            $scope.selectedIndex = index;
            $scope.selectedInNew = selectedInNew;

            var search = {};

            if (angular.isDefined(multiDelivery)) {
                $scope.multiDelivery = multiDelivery;
                multiDelivery._selected = true;
                search = { id: multiDelivery.identifier };
            }

            $location.path("/multilotsdelivery/multidelivery").search(search);
        }

        function filterDeliveries(newValue, field) {
            saveFilters(newValue, field);

            var searchParams = {
                page: $scope.pagination.page,
                search: $scope.filterWith || "",
                active: !$scope.filters.inactive
            };

            if ($scope.filters.libraries) {
                var librariesIds = _.pluck($scope.filters.libraries, "identifier");
                searchParams["libraries"] = librariesIds;
            }

            if ($scope.filters.projects) {
                var projectsIds = _.pluck($scope.filters.projects, "identifier");
                searchParams["projects"] = projectsIds;
            }

            if ($scope.filters.providers) {
                var providersIds = _.pluck($scope.filters.providers, "identifier");
                searchParams["providers"] = providersIds;
            }

            if ($scope.filters.statuses) {
                var statusesIds = _.pluck($scope.filters.statuses, "identifier");
                searchParams["status"] = statusesIds;
            }

            if ($scope.filters.categories) {
                var categoriesIds = _.pluck($scope.filters.categories, "identifier");
                searchParams["categories"] = categoriesIds;
            }

            if ($scope.filters.deliveryDateFrom) {
                searchParams["deliveryDateFrom"] = $scope.filters.deliveryDateFrom;
            }

            if ($scope.filters.deliveryDateTo) {
                searchParams["deliveryDateTo"] = $scope.filters.deliveryDateTo;
            }
            if (field) {
                if (newValue) {
                    searchParams[field] = newValue;
                } else {
                    delete searchParams[field];
                }
            }
            return MultiDeliverySrvc.search(searchParams).$promise;
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

            filterDeliveries(newValue, field).then(function (value, responseHeaders) {
                $scope.pagination.page = 1;
                $scope.pagination.items = [];
                $scope.pagination.totalItems = value.totalElements;

                var idNewDeliveries = _.pluck($scope.newDeliveries, 'identifier');

                for (var i = 0; i < value.content.length; i++) {
                    if (idNewDeliveries.indexOf(value.content[i].identifier) < 0) {
                        if (afterUpdate && value.content[i].identifier === $scope.multiDelivery.identifier) {
                            $scope.multiDelivery._selected = true;
                            $scope.pagination.items.push($scope.multiDelivery);
                        } else {
                            $scope.pagination.items.push(value.content[i]);
                        }
                    }
                    else {
                        // On ne compte pas 2 fois les nouvelles livraisons rechargées
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
                delete $scope.filters.initiale;
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

            filterDeliveries().then(function (value, responseHeaders) {
                $scope.pagination.page = value.number + 1;
                $scope.pagination.totalItems = value.totalElements;
                $scope.pagination.items = $scope.pagination.items.concat(value.content);
                $scope.pagination.last = value.last;
                $scope.pagination.busy = false;
            });
        }
        function updateDelivery(identifier, multiDelivery) {
            if (identifier) {
                _.union($scope.pagination.items, $scope.newDeliveries).forEach(function (elt, i) {
                    if (elt.identifier === identifier) {
                        elt.label = multiDelivery.label;
                        $scope.multiDelivery = elt;
                        doFilter(true);
                        return;
                    }
                });
            }
        }
        function clearSelection() {
            _.union($scope.pagination.items, $scope.newDeliveries)
                .forEach(function (elt, i) {
                    elt._selected = false;
                });
        }
        function getFirstLetter(multiDelivery) {
            return StringTools.getFirstLetter(multiDelivery.label, "OTHER");
        }
        function moveUp() {
            var index;
            if ($scope.selectedIndex > 0) {
                index = $scope.selectedIndex - 1;
                if ($scope.selectedInNew) {
                    edit($scope.newDeliveries[index], index, true);
                } else {
                    edit($scope.pagination.items[index], index, false);
                }
            } else {
                if (!$scope.selectedInNew) {
                    index = $scope.newDeliveries.length - 1;
                    if (index >= 0) {
                        edit($scope.newDeliveries[index], index, true);
                    }
                }
            }
        }
        function moveDown() {
            var index;
            if ($scope.selectedInNew) {
                if ($scope.selectedIndex < $scope.newDeliveries.length - 1) {
                    index = $scope.selectedIndex + 1;
                    edit($scope.newDeliveries[index], index, true);
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
