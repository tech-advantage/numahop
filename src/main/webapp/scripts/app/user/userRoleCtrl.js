(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('UserRoleCtrl', function ($location, $scope, $timeout, UserRoleSrvc, gettextCatalog,
            HistorySrvc, NumaHopInitializationSrvc, NumahopStorageService, StringTools, $q) {

            $scope.roleInclude = "scripts/app/user/roleEdit.html";

            $scope.newRoles = [];

            $scope.listLoaded = false;

            $scope.doFilter = doFilter;
            $scope.applyFilter = applyFilter;
            $scope.reinitFilters = reinitFilters;
            $scope.create = create;
            $scope.edit = edit;
            $scope.updateRole = updateRole;
            $scope.getFirstLetter = getFirstLetter;
            $scope.navigate = navigate;

            $scope.filters = {
                authorizations: null
            };

            $scope.options = {
                authorizations: []
            };

            var FILTER_STORAGE_SERVICE_KEY = "user_role";

            $scope.$on("$routeUpdate",
                function ($currentRoute, $previousRoute) {
                    $timeout(function () {
                        $scope.roleInclude = null;
                        $scope.$apply();
                        $scope.roleInclude = "scripts/app/user/roleEdit.html";
                    });
                });

            init();

            function init() {
                HistorySrvc.add(gettextCatalog.getString("Rôles"));
                reinitFilters(false);
                loadOptionsAndFilters();
            }

            // CRUD

            function create() {
                if ($scope.role) {
                    $scope.role._selected = false;
                    $scope.role = null;
                }
                $location.path("/user/role").search({ new: true });
            }
            function edit(role, index, selectedInNew) {
                $scope.selectedIndex = index;
                $scope.selectedInNew = selectedInNew;
                if ($scope.role) {
                    $scope.role._selected = false;
                }

                var search = {};

                if (angular.isDefined(role)) {
                    $scope.role = role;
                    role._selected = true;
                    search = { id: role.identifier };
                }

                $location.path("/user/role").search(search);
            }

            /**
             * Mise à jour du libellé d'un rôle dans la liste des rôles
             * A appeler lors de l'édition du rôle
             */
            function updateRole(identifier, role) {
                if (identifier) {
                    _.union($scope.roles, $scope.newRoles).forEach(function (elt, i) {
                        if (elt.identifier === identifier) {
                            elt.label = role.label;
                            $scope.role = elt;
                            return;
                        }
                    });
                }
            }

            // FILTRES

            /**
             * Fonction UI à appeler pour recharger la liste des rôles filtrés
             */
            function doFilter() {
                filterRoles().then(function (value, responseHeaders) {
                    $scope.roles = value;
                    $scope.listLoaded = true;
                    // réinitialisation de la fiche de droite
                    $scope.edit();
                });
            }

            /**
             * Récupération des rôles filtrés depuis le serveur
             */
            function filterRoles() {
                saveFilters();

                var searchParams = {
                    search: $scope.filterWith || "",
                    sorts: "label"
                };

                if ($scope.filters.authorizations) {
                    var authorizationsIds = _.pluck($scope.filters.authorizations, "identifier");
                    searchParams["authorizations"] = authorizationsIds;
                }

                return UserRoleSrvc.search(searchParams).$promise;
            }

            /**
             * Récupération des filtres dans le local storage au chargement de la page
             */
            function loadFilters() {
                var filters = NumahopStorageService.getFilter(FILTER_STORAGE_SERVICE_KEY);
                if (filters) {
                    $scope.filters = filters.filters;
                }
                doFilter();
                return !!filters;
            }

            /**
             * Stockage des filtres dans le local storage
             */
            function saveFilters() {
                var filters = {};
                filters.filters = $scope.filters;
                NumahopStorageService.saveFilter(FILTER_STORAGE_SERVICE_KEY, filters);
            }

            /**
             * Suppression de tous les filtres
             * @param {boolean} reload true si on doit recharger les rôles, false sinon
             */
            function reinitFilters(reload) {
                $scope.filters = {
                    authorizations: []
                };
                $scope.filterWith = "";
                if (reload) {
                    doFilter();
                }
            }

            /**
             * Handler pour le champ de recherche par libellé de rôle
             */
            function applyFilter(event) {
                if (event.type === "keypress" && event.keyCode === 13) {
                    doFilter();
                }
            }

            /**
             * Chargement de la liste des autorisations pour les filtres, puis des filtres enregistrés dans le local storage
             */
            function loadOptionsAndFilters() {
                $q.all([NumaHopInitializationSrvc.loadAuthorizations()])
                    .then(function (data) {
                        $scope.options.authorizations = data[0];
                        loadFilters();
                    });
            }

            /**
             * Récupération de l'initiale pour l'affichage dans la liste
             */
            function getFirstLetter(role) {
                return StringTools.getFirstLetter(role.label, "OTHER");
            }
            function moveUp() {
                var index;
                if ($scope.selectedIndex > 0) {
                    index = $scope.selectedIndex - 1;
                    if ($scope.selectedInNew) {
                        edit($scope.newRoles[index], index, true);
                    } else {
                        edit($scope.roles[index], index, false);
                    }
                } else {
                    if (!$scope.selectedInNew) {
                        index = $scope.newRoles.length - 1;
                        if (index >= 0) {
                            edit($scope.newRoles[index], index, true);
                        }
                    }
                }
            }
            function moveDown() {
                var index;
                if ($scope.selectedInNew) {
                    if ($scope.selectedIndex < $scope.newRoles.length - 1) {
                        index = $scope.selectedIndex + 1;
                        edit($scope.newRoles[index], index, true);
                    } else {
                        edit($scope.roles[0], 0, false);
                    }
                } else {
                    if ($scope.selectedIndex < $scope.roles.length - 1) {
                        index = $scope.selectedIndex + 1;
                        edit($scope.roles[index], index, false);
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

        });
})();