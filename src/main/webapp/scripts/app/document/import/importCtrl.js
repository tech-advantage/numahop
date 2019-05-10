(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('ImportCtrl', ImportCtrl);

    function ImportCtrl($location, $routeParams, $scope, $timeout, gettextCatalog, HistorySrvc, ImportReportSrvc, Principal, USER_ROLES, NumaHopInitializationSrvc,
        $q, NumahopStorageService) {
        var mainCtrl = this;
        mainCtrl.create = create;
        mainCtrl.edit = edit;
        mainCtrl.getGroupLabel = getGroupLabel;
        mainCtrl.getPage = getPage;
        mainCtrl.synchro = synchro;
        mainCtrl.synchroDeletion = synchroDeletion;
        mainCtrl.toString = ImportReportSrvc.toString;
        mainCtrl.doFilter = doFilter;
        mainCtrl.reinitFilters = reinitFilters;

        var FILTER_STORAGE_SERVICE_KEY = "importReports";

        mainCtrl.pagination = {
            totalItems: 0,
            page: 0,
            itemsByPage: 30,
            busy: false
        };
        mainCtrl.reports = [];
        mainCtrl.loaded = false;

        mainCtrl.filters = {
            statuses: null,
            users: null
        };
        mainCtrl.options = {
            statuses: [
                { identifier: "PENDING", label: gettextCatalog.getString('En attente') },
                { identifier: "PRE_IMPORTING", label: gettextCatalog.getString('Pré-import') },
                { identifier: "DEDUPLICATING", label: gettextCatalog.getString('Recherche de doublons') },
                { identifier: "USER_VALIDATION", label: gettextCatalog.getString('Attente validation utilisateur') },
                { identifier: "IMPORTING", label: gettextCatalog.getString('Import des unités documentaires') },
                { identifier: "INDEXING", label: gettextCatalog.getString('Indexation des unités documentaires') },
                { identifier: "COMPLETED", label: gettextCatalog.getString('Import réussi') },
                { identifier: "FAILED", label: gettextCatalog.getString('Import échoué') }
            ]
        };

        init();

        /** Initialisation du contrôleur */
        function init() {
            HistorySrvc.add(gettextCatalog.getString("Import d'unités documentaires"));
            loadFilters();

            mainCtrl.isSuperAdmin = Principal.isInRole(USER_ROLES.SUPER_ADMIN);
            mainCtrl.library = Principal.library();

            // Vue de gestion de l'import sélectionné
            mainCtrl.importInclude = "scripts/app/document/import/importEdit.html";
            $scope.$on("$routeUpdate", reloadTemplate);

            // Appels du scope enfant
            $scope.$on("editCtrl:create", createWrapper);
            $scope.$on("editCtrl:synchro", synchro);
            $scope.$on("editCtrl:synchroDeletion", synchroDeletion);

            loadOptions();

            // Chargement de la première page d'imports
            getPage();
        }

        /** Rechargement du template d'édition */
        function reloadTemplate() {
            $timeout(function () {
                mainCtrl.importInclude = null;
                $scope.$apply();
                mainCtrl.importInclude = "scripts/app/document/import/importEdit.html";
            });
        }

        /** Chargement utilisateurs pour filtre */
        function loadOptions() {
            $q.all([NumaHopInitializationSrvc.loadUsers()])
                .then(function (data) {
                    mainCtrl.options.users = data[0];
                });
        }

        /** Chargement des imports */
        function getPage() {
            if (mainCtrl.pagination.busy || (mainCtrl.loaded && mainCtrl.pagination.page >= mainCtrl.pagination.totalPages)) {
                return;
            }
            mainCtrl.pagination.busy = true;

            filterImports().then(function (page) {
                mainCtrl.reports = mainCtrl.reports.concat(page.content);
                mainCtrl.pagination.totalItems = page.totalElements;
                mainCtrl.pagination.page = page.number + 1;
                mainCtrl.pagination.totalPages = page.totalPages;
                mainCtrl.pagination.busy = false;
                mainCtrl.loaded = true;
            });
        }

        function filterImports() {
            saveFilters();

            var searchParams = {
                page: mainCtrl.pagination.page,
                size: mainCtrl.pagination.itemsByPage
            };

            if (mainCtrl.filters.users) {
                var usersIds = _.pluck(mainCtrl.filters.users, "login");
                searchParams["users"] = usersIds;
            }

            if (mainCtrl.filters.statuses) {
                var statusesIds = _.pluck(mainCtrl.filters.statuses, "identifier");
                searchParams["status"] = statusesIds;
            }
            return ImportReportSrvc.search(searchParams).$promise;
        }

        function doFilter() {
            mainCtrl.pagination.page = 0;
            mainCtrl.pagination.last = false;
            mainCtrl.pagination.busy = true;

            filterImports().then(function (value) {
                mainCtrl.pagination.page = 1;
                mainCtrl.pagination.items = [];
                mainCtrl.pagination.totalItems = value.totalElements;
                mainCtrl.pagination.last = value.last;

                for (var i = 0; i < value.content.length; i++) {
                    mainCtrl.pagination.items.push(value.content[i]);
                }
                mainCtrl.pagination.busy = false;
                mainCtrl.reports = value.content;
            });
        }

        function loadFilters() {
            var filters = NumahopStorageService.getFilter(FILTER_STORAGE_SERVICE_KEY);
            if (filters) {
                mainCtrl.filters = filters.filters;
            }
            return !!filters;
        }

        function saveFilters() {
            var filters = {};
            filters.filters = mainCtrl.filters;
            NumahopStorageService.saveFilter(FILTER_STORAGE_SERVICE_KEY, filters);
        }

        function reinitFilters(reload) {
            mainCtrl.filters = {
                statuses: [],
                users: []
            };
            if (reload) {
                doFilter();
            }
        }

        /** Création d'un nouvel import */
        function create(source, parent) {
            // Désélection de l'import dans la liste
            _.each(mainCtrl.reports, function (oth) {
                oth._selected = false;
            });
            var params = {
                format: $routeParams.format,
                source: source,
                parent: parent,
                ts: moment().unix() // Force le rechargement
            };
            $location.path("/document/import").search(params);
        }

        /** Édition d'un import existant */
        function edit(report) {
            // Sélection de l'import dans la liste
            _.each(mainCtrl.reports, function (oth) {
                oth._selected = (report === oth);
            });
            // Ouverture de l'import sélectionné
            var params = {
                id: report.identifier
            };
            $location.path("/document/import").search(params);
        }

        /** Libellé de regroupement des rapports d'import */
        function getGroupLabel(report) {
            if (report.start) {
                return moment(report.start).format("L");
            }
            return "";
        }

        /** Synchro de la liste des imports */
        function createWrapper(event, report) {
            event.stopPropagation();    // ce contrôleur est l'unique destinataire de l'évènement
            create(null, report.identifier);
        }

        /** Synchro de la liste des imports */
        function synchro(event, report) {
            event.stopPropagation();    // ce contrôleur est l'unique destinataire de l'évènement

            var found = _.find(mainCtrl.reports, function (oth) {
                return oth.identifier === report.identifier;
            });
            // Création
            if (angular.isUndefined(found)) {
                mainCtrl.reports.unshift(report);
                found = report;
            }
            // Mise à jour
            else {
                found.status = report.status;
                found.start = report.start;
                found.end = report.end;
                found.nbImp = report.nbImp;
            }
            // Mise à jour de la sélection
            if (!found._selected) {
                _.each(mainCtrl.reports, function (oth) {
                    oth._selected = (found === oth);
                });
            }
        }

        /** Synchro suppression */
        function synchroDeletion(event, report) {
            event.stopPropagation();    // ce contrôleur est l'unique destinataire de l'évènement

            mainCtrl.reports = _.filter(mainCtrl.reports, function (oth) {
                return oth.identifier !== report.identifier;
            });
            create();
        }
    }
})();
