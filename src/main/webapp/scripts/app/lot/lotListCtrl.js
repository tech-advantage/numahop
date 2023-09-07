(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('LotListCtrl', LotListCtrl);

    function LotListCtrl($q, $routeParams, DtoService, gettext, gettextCatalog, HistorySrvc, LotSrvc, NumahopUrlService, MessageSrvc, ModalSrvc, NumaHopInitializationSrvc, NumahopStorageService, SelectionSrvc) {
        var mainCtrl = this;

        /** check actions **/
        mainCtrl.changeItem = changeItem;
        mainCtrl.checkAll = checkAll;
        mainCtrl.uncheckAll = uncheckAll;
        /** others actions **/
        mainCtrl.deleteSelection = deleteSelection;

        mainCtrl.getPage = getPage;
        mainCtrl.getUrlLot = getUrlLot;
        mainCtrl.getUrlProject = getUrlProject;
        mainCtrl.docs = DtoService.getDocs();
        mainCtrl.isFilterSelected = isFilterSelected;
        mainCtrl.reinitFilters = reinitFilters;
        mainCtrl.search = search;

        mainCtrl.addSelectionToNewProject = addSelectionToNewProject;
        mainCtrl.removeProject = removeProject;

        mainCtrl.changePageSize = changePageSize;
        mainCtrl.pageSize = null;

        mainCtrl.displayStatus = displayStatus;
        mainCtrl.displayType = displayType;

        function displayStatus(status) {
            return mainCtrl.options.status[status] || status;
        }

        function displayType(type) {
            return mainCtrl.options.type[type] || type;
        }

        var PAGE_START = 1;
        var FILTER_STORAGE_SERVICE_KEY = 'lot_list';

        /**
         * Recherche
         */
        mainCtrl.searchRequest = null;

        /**
         * Sélection
         */
        mainCtrl.selection = {};
        mainCtrl.selectedLength = 0;

        /**
         * Objet de pagination
         * @type {Object}
         */
        mainCtrl.pagination = {
            items: [],
            totalItems: 0,
            busy: false,
            page: PAGE_START,
            size: mainCtrl.pageSize,
        };

        mainCtrl.sizeOptions = [
            { value: 10, label: '10' },
            { value: 20, label: '20' },
            { value: 50, label: '50' },
            { value: 100, label: '100' },
            { value: null, label: 'Tout' },
        ];

        /**
         * Filtres
         * @type {Object}
         */
        mainCtrl.filters = {
            libraries: [],
            available: true,
            projects: [],
        };

        /**
         * Filtres actifs
         * @type {Object}
         */
        mainCtrl.listFilters = {
            has_digital_documents_filter: true,
            library_filter: true,
            last_modified_date_filter: true,
            project_filter: true,
            lot_filter: true,
            created_date_filter: true,
        };

        /**
         * Modèle pour le tri
         * @type {Object}
         */
        mainCtrl.sortModel = [];

        /**
         * La liste a déjà été chargé une première fois
         */
        mainCtrl.loaded = false;

        /**
         * Liste des options pour les listes déroulantes
         */
        mainCtrl.options = {
            libraries: [],
            projects: [],
            type: {
                PHYSICAL: gettextCatalog.getString('Physique'),
                DIGITAL: gettextCatalog.getString('Numérique'),
            },
            status: {
                CREATED: gettextCatalog.getString('Créé'),
                ONGOING: gettextCatalog.getString('En cours'),
                PENDING: gettextCatalog.getString('En attente'),
                CANCELED: gettextCatalog.getString('Annulé'),
                CLOSED: gettextCatalog.getString('Clôturé'),
            },
        };

        init();

        /**
         * Initialisation du controleur
         * @return {[type]} [description]
         */
        function init() {
            HistorySrvc.add(gettextCatalog.getString('Catalogue toutes opérations'));
            mainCtrl.isFilteredByIds = !!$routeParams.searchresult;

            // Opérations groupées sur les résultats de recherche
            if (mainCtrl.isFilteredByIds) {
                initFromSearchResults();
            }

            // Chargement des données
            $q.all([NumaHopInitializationSrvc.loadProjects()]).then(function (data) {
                mainCtrl.options.projects = data[0];

                loadFilters();
                loadPageSize();

                getPage().then(function () {
                    // auto-sélection
                    if (mainCtrl.isFilteredByIds) {
                        mainCtrl.checkAll();
                    }
                    mainCtrl.loaded = true;
                });
                // Affichage pour un temps limité à l'ouverture
                MessageSrvc.initPanel();
            });
        }

        /**
         * => Lien vers le detail du lot.
         */
        function getUrlLot(lotId) {
            var params = { id: lotId };
            return NumahopUrlService.getUrlForTypeAndParameters('lot', params);
        }

        /**
         * => Lien vers le detail du projet.
         */
        function getUrlProject(projId) {
            var params = { id: projId };
            return NumahopUrlService.getUrlForTypeAndParameters('project', params);
        }

        /**
         * Chargement des résultats de recherche sélectionnés
         */
        function initFromSearchResults() {
            var searchSelection = SelectionSrvc.get('SEARCH_RESULT_LOT');
            // Sélection
            _.each(searchSelection, function (s) {
                mainCtrl.selection[s.identifier] = s;
            });
            // Filtre
            mainCtrl.filteredIds = _.pluck(searchSelection, 'identifier');
        }

        /**
         * Réinitialise l'ensemble des filtres et lance une nouvelle recherche
         * @return {[type]} [description]
         */
        function reinitFilters() {
            mainCtrl.pagination.items = [];
            mainCtrl.pagination.totalItems = 0;
            mainCtrl.pagination.busy = false;
            mainCtrl.pagination.page = PAGE_START;
            mainCtrl.pagination.size = mainCtrl.pageSize;

            mainCtrl.filters.libraries = [];
            mainCtrl.filters.projects = [];
            mainCtrl.filters.lots = [];
            mainCtrl.filters.hasDigitalDocuments = false;
            mainCtrl.filters.lastModifiedDateFrom = null;
            mainCtrl.filters.lastModifiedDateTo = null;
            mainCtrl.filters.createdDateFrom = null;
            mainCtrl.filters.createdDateTo = null;
            mainCtrl.filters.available = true;
            mainCtrl.sortModel = [];
            search();
        }

        /**
         * Est-ce que le filtre ayant pour code filterCode est sélectionné ?
         */
        function isFilterSelected(filters, filterCode) {
            return _.any(filters, function (filter) {
                return filter.code === filterCode;
            });
        }

        /**
         * Chargement de la prochaine page d'items
         * @return {[type]} [description]
         */
        function getPage() {
            mainCtrl.pagination.busy = true;

            var params = {};
            params['page'] = mainCtrl.pagination.page - 1;
            params['size'] = mainCtrl.pageSize;
            params['sorts'] = mainCtrl.sortModel;

            var body = getSearchParams();

            return LotSrvc.postSearch(params, body).$promise.then(handlePageOfItems);
        }

        /**
         * handlePageOfItems - Gestion d'une page d'entités
         *
         * @param  {type} pageOfReserves la page avec les entités
         */
        function handlePageOfItems(pageOfItems) {
            mainCtrl.pagination.totalItems = pageOfItems.totalElements;
            mainCtrl.pagination.totalPages = pageOfItems.totalPages;
            mainCtrl.pagination.busy = false;
            mainCtrl.pagination.items = pageOfItems.content;
            _.each(mainCtrl.pagination.items, function (item) {
                item.checked = angular.isDefined(mainCtrl.selection[item.identifier]);
            });
        }

        /**
         * Recherche d'entité
         * @return {[type]} [description]
         */
        function search(sortModel, newValue, field) {
            if (angular.isDefined(sortModel)) {
                mainCtrl.sortModel = sortModel;
            }

            mainCtrl.pagination.busy = true;
            mainCtrl.selection = {};

            savePageSize();
            saveFilters(newValue, field);

            var params = {};
            params['page'] = mainCtrl.pagination.page - 1;
            params['size'] = mainCtrl.pageSize;
            params['sorts'] = mainCtrl.sortModel;

            var body = getSearchParams(newValue, field);

            return LotSrvc.postSearch(params, body).$promise.then(handlePageOfItems);
        }

        /**
         * Construction des paramètres de la recherche en fonction des filtres sélectionnés
         * @return {[type]} [description]
         */
        function getSearchParams(newValue, field) {
            // Filtrage à partir des résultats de recherche
            if (mainCtrl.isFilteredByIds && mainCtrl.filteredIds) {
                return {
                    filter: mainCtrl.filteredIds,
                };
            }

            var params = {};
            params['active'] = true;
            params['search'] = mainCtrl.searchRequest || '';

            // Projet
            if (mainCtrl.filters.projects) {
                var projectsIds = _.pluck(mainCtrl.filters.projects, 'identifier');
                params['projects'] = projectsIds;
            }
            if (mainCtrl.filters.createdDateFrom) {
                params['createdDateFrom'] = mainCtrl.filters.createdDateFrom;
            }
            if (mainCtrl.filters.createdDateTo) {
                params['createdDateTo'] = mainCtrl.filters.createdDateTo;
            }
            if (mainCtrl.filters.lastModifiedDateFrom) {
                params['lastModifiedDateFrom'] = mainCtrl.filters.lastModifiedDateFrom;
            }
            if (mainCtrl.filters.lastModifiedDateTo) {
                params['lastModifiedDateTo'] = mainCtrl.filters.lastModifiedDateTo;
            }

            if (field) {
                if (newValue) {
                    params[field] = newValue;
                } else {
                    delete params[field];
                }
            }

            return params;
        }

        /**
         * loadFilters - Chargement des filtres depuis le local Storage
         *
         * @return {type}  description
         */
        function loadFilters() {
            var filters = NumahopStorageService.getFilter(FILTER_STORAGE_SERVICE_KEY);
            if (filters) {
                mainCtrl.filters = filters.filters;
                if (filters.sortModel) {
                    mainCtrl.sortModel = filters.sortModel;
                }
            }
            return !!filters;
        }

        /**
         * saveFilters - Enregistrement des filtres dans le local Storage
         */
        function saveFilters(newValue, field) {
            var filters = {};
            filters.filters = mainCtrl.filters;
            filters.sortModel = mainCtrl.sortModel;
            if (field) {
                if (newValue) {
                    filters.filters[field] = newValue;
                } else {
                    delete filters.filters[field];
                }
            }
            NumahopStorageService.saveFilter(FILTER_STORAGE_SERVICE_KEY, filters);
        }

        function changePageSize() {
            mainCtrl.pagination.size = mainCtrl.pageSize;
            search();
        }

        function loadPageSize() {
            var savedSize = NumahopStorageService.getPageSize(FILTER_STORAGE_SERVICE_KEY);
            mainCtrl.pageSize = savedSize ? savedSize : 10;
            mainCtrl.pagination.size = mainCtrl.pageSize;
        }

        function savePageSize() {
            NumahopStorageService.savePageSize(FILTER_STORAGE_SERVICE_KEY, mainCtrl.pageSize);
        }

        function changeItem(item) {
            if (angular.isDefined(mainCtrl.selection[item.identifier]) && !item.checked) {
                delete mainCtrl.selection[item.identifier];
                mainCtrl.selectedLength--;
            } else if (item.checked) {
                mainCtrl.selection[item.identifier] = item;
                mainCtrl.selectedLength++;
            }
        }

        function checkAll() {
            _.each(mainCtrl.pagination.items, function (item) {
                mainCtrl.selection[item.identifier] = item;
                item.checked = true;
            });
            updateSelectLength();
        }

        function uncheckAll() {
            mainCtrl.selection = {};
            mainCtrl.selectedLength = 0;
            _.each(mainCtrl.pagination.items, function (item) {
                item.checked = false;
            });
        }

        function updateSelectLength() {
            mainCtrl.selectedLength = _.keys(mainCtrl.selection).length;
        }

        /**
         * Suppression des unités documentaires sélectionnées
         *
         */
        function deleteSelection() {
            if (mainCtrl.selectedLength === 0) {
                MessageSrvc.addWarn(gettext('La sélection est vide'), {}, false);
                return;
            }
            ModalSrvc.confirmDeletion(gettextCatalog.getPlural(mainCtrl.selectedLength, 'le lot sélectionné', 'les {{n}} lots sélectionnés', { n: mainCtrl.selectedLength })).then(function () {
                LotSrvc.deleteSelection({}, _.keys(mainCtrl.selection), function (value) {
                    if (value.length > 0) {
                        ModalSrvc.modalDeleteDocUnitResults(value, 'xl');
                    } else {
                        MessageSrvc.addSuccess(gettext("Aucun lot n'a été supprimé"));
                    }
                    search();
                });
            });
        }

        function removeProject(lot) {
            LotSrvc.unlinkProject(lot).$promise.then(search);
        }

        function addLotsToProject(lots, projectId) {
            if (lots.length === 0) {
                MessageSrvc.addWarn(gettext("Aucune unité documentaire n'est sélectionnée"), {}, false);
                return;
            }
            var params = {
                project: projectId,
            };
            var body = _.pluck(lots, 'identifier');

            var defer = $q.defer();
            LotSrvc.addToProject(
                params,
                body,
                function () {
                    defer.resolve();
                },
                function () {
                    defer.reject();
                }
            );
            return defer.promise;
        }

        function addSelectionToNewProject() {
            ModalSrvc.createProject().then(function (project) {
                addLotsToProject(mainCtrl.selection, project.identifier).then(search);
            });
        }
    }
})();
