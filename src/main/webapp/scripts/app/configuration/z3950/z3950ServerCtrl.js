(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('Z3950ServerCtrl', function ($filter, $location, $routeParams, $scope, $timeout, gettextCatalog, HistorySrvc, StringTools, Z3950ServerSrvc) {
        HistorySrvc.add(gettextCatalog.getString('Serveurs Z39.50'));

        $scope.binding = {};
        $scope.binding.filterWith = $routeParams.filter || undefined;

        $scope.orderBy = $routeParams.sort || ['name'];
        $scope.newServers = [];

        $scope.listLoaded = false;

        $scope.z3950ServerInclude = 'scripts/app/configuration/z3950/z3950ServerEdit.html';
        $scope.z3950Server = null;

        // filtres sélectionnés
        $scope.filter = {};
        // filtres disponibles
        $scope.availableFilters = {};

        // Chargement des servers
        $scope.servers = Z3950ServerSrvc.dto({}, function (value, responseHeaders) {
            _.each(value, function (z3950Server) {
                z3950Server.initial = $scope.getFirstLetter(z3950Server);
            });
            $scope.refreshFilters(true);
            $scope.listLoaded = true;
        });

        /**
         * Création d'un serveur Z39.50
         */
        $scope.create = function () {
            if ($scope.z3950Server) {
                $scope.z3950Server._selected = false;
                $scope.z3950Server = null;
            }
            $location.path('/administration/appconfiguration/z3950Server').search({ new: 'true' });
        };

        /**
         * Modification d'un serveur Z39.50
         * @param z3950Server
         */
        $scope.edit = function (z3950Server) {
            if ($scope.z3950Server) {
                $scope.z3950Server._selected = false;
            }

            var search = {};

            if (angular.isDefined(z3950Server)) {
                $scope.z3950Server = z3950Server;
                z3950Server._selected = true;
                search = { id: z3950Server.identifier };
            }
            $location.path('/administration/appconfiguration/z3950Server').search(search);
        };

        $scope.filterInitial = function (initial) {
            $scope.refreshFilters();
        };

        $scope.unfilterInitial = function () {
            $scope.refreshFilters();
        };

        $scope.getFilteredServers = function (filter) {
            var filters = {};
            if (angular.isDefined(filter)) {
                filters.name = filter;
            }
            var filtered = $filter('filter')($scope.servers, filters);
            filters = {};
            if (angular.isDefined($scope.filter.initialFilter)) {
                filters.initial = $scope.filter.initialFilter;
            }
            filtered = $filter('filter')(filtered, filters, true);
            return filtered;
        };

        $scope.getDisplayedServers = function (filter, orderBy) {
            var displayedServer = $scope.getFilteredServers(filter);
            displayedServer = $filter('orderBy')(displayedServer, orderBy);
            return displayedServer;
        };

        $scope.$on('$routeUpdate', function ($currentRoute, $previousRoute) {
            $timeout(function () {
                $scope.z3950ServerInclude = null;
                $scope.$apply();
                $scope.z3950ServerInclude = 'scripts/app/configuration/z3950/z3950ServerEdit.html';
            });
        });

        $scope.updateZ3950Server = function (identifier, z3950Server) {
            if (identifier) {
                _.union($scope.servers, $scope.newServers).forEach(function (elt) {
                    if (elt.identifier === identifier) {
                        elt.name = z3950Server.name;
                        elt.initial = $scope.getFirstLetter(z3950Server);
                        $scope.z3950Server = elt;
                        return;
                    }
                });
            }
        };

        $scope.deleteInList = function (z3950Server) {
            $scope.servers = _.reject($scope.servers, function (s) {
                return s.identifier === z3950Server.identifier;
            });
            $scope.refreshFilters();
        };

        $scope.$watch('binding.filterWith', function (value) {
            if (angular.isDefined(value)) {
                $scope.refreshFilters();
            }
        });

        $scope.refreshFilters = function (noEdit) {
            $scope.filteredServers = $scope.getFilteredServers($scope.binding.filterWith);
            $scope.displayedServers = $scope.getDisplayedServers($scope.binding.filterWith, $scope.orderBy);
            if (noEdit) {
                return;
            }
            if ($scope.filteredServers.length === 1) {
                $scope.edit($scope.filteredServers[0]);
            } else {
                // réinitialisation de la fiche de droite
                $scope.edit();
            }
        };

        $scope.getFirstLetter = function (z3950Server) {
            return StringTools.getFirstLetter(z3950Server.name, 'OTHER');
        };
    });
})();
