(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('UserCtrl', UserCtrl);

    function UserCtrl($location, $scope, $timeout, $q, $routeParams,
        UserSrvc, gettextCatalog, HistorySrvc, AuthenticationSharedService, USER_ROLES,
        StringTools, NumahopStorageService, NumaHopInitializationSrvc) {

        $scope.applyFilter = applyFilter;
        $scope.clearSelection = clearSelection;
        $scope.create = create;
        $scope.doFilter = doFilter;
        $scope.doFilterLibrary = doFilter;
        $scope.reinitFilters = reinitFilters;
        $scope.edit = edit;
        $scope.filterInitial = filterInitial;
        $scope.getFirstLetter = getFirstLetter;
        $scope.nextPage = nextPage;
        $scope.unfilterInitial = unfilterInitial;

        $scope.navigate = navigate;
        $scope.selectedIndex = null;
        $scope.selectedInNew = false;

        $scope.userInclude = "scripts/app/user/userEdit.html";
        $scope.user = null;

        var FILTER_STORAGE_SERVICE_KEY = "users";

        $scope.filters = {
            libraries: [],
            available: false
        };
        $scope.listFilters = {
            initial_filter: true,
            inactive_filter: true,
            library_filter: true,
            category_filter: true,
            role_filter: true
        };
        $scope.filterLabels = {
            inactive: "Voir les utilisateurs inactifs"
        };

        /**
         * Liste des options pour les listes déroulantes
         */
        $scope.options = {
            categories: [{ identifier: "PROVIDER", label: gettextCatalog.getString("Prestataire") },
            { identifier: "OTHER", label: gettextCatalog.getString("Autre") }
            ]
        };

        $scope.pagination = {
            items: [],
            totalItems: 0,
            busy: false,
            last: false,
            page: 0
        };
        $scope.newUsers = []; // liste des usagers récemment créés

        init();


        /** Initialisation */
        function init() {
            if(AuthenticationSharedService.isAuthorized(USER_ROLES.USER_HAB0)){
                HistorySrvc.add(gettextCatalog.getString("Utilisateurs"));
                reinitFilters(false);
                loadOptionsAndFilters();

                $scope.$on("$routeUpdate",
                    function ($currentRoute, $previousRoute) {
                        $timeout(function () {
                            $scope.userInclude = null;
                            $scope.$apply();
                            $scope.userInclude = "scripts/app/user/userEdit.html";
                        });
                    }
                );
            }
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
            $q.all([NumaHopInitializationSrvc.loadRoles(), NumaHopInitializationSrvc.loadLibraries()])
                .then(function (data) {
                    $scope.options.roles = _.sortBy(data[0], "label");
                    $scope.options.libraries = _.sortBy(data[1], "name");
                    handleRedirect();
                    nextPage();
                });
        }

        // CRUD
        function create() {
            if ($scope.user) {
                $scope.user._selected = false;
                $scope.user = null;
            }
            $location.path("/user/user").search({ id: null, new: true });
        }
        function edit(user, index, selectedInNew) {
            clearSelection();
            $scope.selectedIndex = index;
            $scope.selectedInNew = selectedInNew;

            var search = {};

            if (angular.isDefined(user)) {
                $scope.user = user;
                user._selected = true;
                search = { id: user.identifier };
            }

            $location.path("/user/user").search(search);
        }

        function filterUsers() {
            saveFilters();

            var searchParams = {
                page: $scope.pagination.page,
                search: $scope.filterWith || "",
                active: !$scope.filters.inactive
            };

            if ($scope.filters.initiale) {
                searchParams["initiale"] = $scope.filters.initiale;
            }

            if ($scope.filters.categories) {
                var categoriesIds = _.pluck($scope.filters.categories, "identifier");
                searchParams["categories"] = categoriesIds;
            }

            if ($scope.filters.libraries) {
                var librariesIds = _.pluck($scope.filters.libraries, "identifier");
                searchParams["libraries"] = librariesIds;
            }

            if ($scope.filters.roles) {
                var rolesIds = _.pluck($scope.filters.roles, "identifier");
                searchParams["roles"] = rolesIds;
            }

            return UserSrvc.search(searchParams).$promise;
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

            filterUsers().then(function (value, responseHeaders) {
                $scope.pagination.page = 1;
                $scope.pagination.items = [];
                $scope.pagination.totalItems = value.totalElements;

                var idNewUsers = _.pluck($scope.newUsers, 'identifier');

                for (var i = 0; i < value.content.length; i++) {
                    if (idNewUsers.indexOf(value.content[i].identifier) < 0) {
                        if (afterUpdate && value.content[i].identifier === $scope.user.identifier) {
                            $scope.user._selected = true;
                            $scope.pagination.items.push($scope.user);
                        } else {
                            $scope.pagination.items.push(value.content[i]);
                        }
                    }
                    else {
                        // On ne compte pas 2 fois les nouvelles configurations rechargées
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
                delete $scope.filters.initiale;
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
                categories: [],
                libraries: [],
                roles: [],
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

            filterUsers().then(function (value, responseHeaders) {
                $scope.pagination.page = value.number + 1;
                $scope.pagination.totalItems = value.totalElements;
                $scope.pagination.items = $scope.pagination.items.concat(value.content);
                $scope.pagination.last = value.last;
                $scope.pagination.busy = false;
            });
        }
        function clearSelection() {
            _.union($scope.pagination.items, $scope.newUsers)
                .forEach(function (elt, i) {
                    elt._selected = false;
                });
        }
        function getFirstLetter(user) {
            return StringTools.getFirstLetter(user.surname, "OTHER");
        }
        function moveUp() {
            var index;

            if ($scope.selectedIndex > 0) {
                index = $scope.selectedIndex - 1;
                if ($scope.selectedInNew) {
                    edit($scope.newUsers[index], index, true);
                } else {
                    edit($scope.pagination.items[index], index, false);
                }
            } else {
                if (!$scope.selectedInNew) {
                    index = $scope.newUsers.length - 1;
                    if (index >= 0) {
                        edit($scope.newUsers[index], index, true);
                    }
                }
            }
        }
        function moveDown() {
            var index;

            if ($scope.selectedInNew) {
                if ($scope.selectedIndex < $scope.newUsers.length - 1) {
                    index = $scope.selectedIndex + 1;
                    edit($scope.newUsers[index], index, true);
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
