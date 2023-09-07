(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('ActionListCtrl', ActionListCtrl);

    function ActionListCtrl(AuthenticationSharedService, USER_ROLES, NumaHopInitializationSrvc, NumahopStorageService, LotSrvc, $q, $scope) {
        var ctrl = this;

        ctrl.isAuthorized = function (role) {
            return AuthenticationSharedService.isAuthorized(USER_ROLES[role]);
        };

        ctrl.resetFilters = resetFilters;
        ctrl.doFilterProject = doFilterProject;
        ctrl.filters = {
            projects: [],
            lots: [],
        };

        var FILTER_STORAGE_SERVICE_KEY = 'filter_dashboard_actions';

        /**
         * Liste des options pour les listes déroulantes
         */
        ctrl.options = {
            projects: [],
            lots: [],
        };

        $scope.$on('$destroy', function () {
            saveFilters();
        });

        init();

        function init() {
            $q.all([NumaHopInitializationSrvc.loadProjects()]).then(function (data) {
                ctrl.options.projects = data[0];
                reloadLots(false);
            });

            // Récupération des filtres de recherches précédents
            var filters = NumahopStorageService.getFilter(FILTER_STORAGE_SERVICE_KEY);
            if (!filters) {
                ctrl.filters = { projects: [], lots: [] };
            } else {
                ctrl.filters = filters.filters;
            }
            ctrl.loaded = true;
        }

        function reloadLots(reload) {
            var filteredProjects = undefined;
            if (ctrl.filters.projects) {
                filteredProjects = _.pluck(ctrl.filters.projects, 'identifier');
            }
            if (filteredProjects && filteredProjects.length > 0) {
                // Filtre sur les projets selectionnes.
                restrictLotsByProjects(filteredProjects).then(function (res) {
                    ctrl.options.lots = res;
                });
            } else {
                NumaHopInitializationSrvc.loadLots().then(function (res) {
                    ctrl.options.lots = res;
                });
            }
        }

        /**
         * Restriction de la liste des lots aux projets sélectionnés.
         */
        function restrictLotsByProjects(projectsIds) {
            return LotSrvc.query({ filterByProjects: '', projectIds: projectsIds }).$promise;
        }

        function resetFilters() {
            ctrl.filters = {};
            var filters = {};
            filters.filters = {};
            NumahopStorageService.saveFilter(FILTER_STORAGE_SERVICE_KEY, filters);
        }

        /**
         * saveFilters - Enregistrement des filtres dans le local Storage
         */
        function saveFilters() {
            var filters = {};
            filters.filters = ctrl.filters;
            NumahopStorageService.saveFilter(FILTER_STORAGE_SERVICE_KEY, filters);
        }

        function doFilterProject() {
            reloadLots(true);
        }
    }
})();
