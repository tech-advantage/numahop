(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('RecordListCtrl', RecordListCtrl);

    function RecordListCtrl(
        $q,
        $scope,
        DocUnitBaseService,
        gettext,
        gettextCatalog,
        HistorySrvc,
        NumahopUrlService,
        MessageSrvc,
        ModalSrvc,
        NumaHopInitializationSrvc,
        NumahopStorageService,
        RecordSrvc,
        DocUnitSrvc,
        WorkflowSrvc
    ) {
        $scope.doFilter = search;
        $scope.doFilterLibrary = searchLibrary;
        $scope.doFilterProject = searchProject;

        /**
         * Filtres actifs
         * @type {Object}
         */
        $scope.listFilters = {
            // state_filter: true,
            has_docunit_filter: true,
            // archived_filter: true,
            // distributed_filter: true,
            library_filter: true,
            last_modified_date_filter: true,
            project_filter: true,
            lot_filter: true,
            train_filter: true,
            created_date_filter: true,
            wkf_status_filter: true,
        };

        var mainCtrl = this;

        /** check actions **/
        mainCtrl.changeItem = changeItem;
        mainCtrl.checkAll = checkAll;
        mainCtrl.uncheckAll = uncheckAll;
        /** others actions **/
        mainCtrl.updateSelection = updateSelection;
        mainCtrl.deleteSelection = deleteSelection;
        mainCtrl.validateSelection = validateSelection;

        mainCtrl.getPage = getPage;
        mainCtrl.getUrl = NumahopUrlService.getUrlRecord;
        mainCtrl.reinitFilters = reinitFilters;
        mainCtrl.search = search;

        mainCtrl.canRemoveProject = DocUnitBaseService.canRemoveProject;
        mainCtrl.canRemoveLot = DocUnitBaseService.canRemoveLot;
        mainCtrl.canRemoveTrain = DocUnitBaseService.canRemoveTrain;
        mainCtrl.removeProject = DocUnitBaseService.removeProject;
        mainCtrl.removeLot = DocUnitBaseService.removeLot;
        mainCtrl.removeTrain = DocUnitBaseService.removeTrain;

        mainCtrl.changePageSize = changePageSize;

        var PAGE_START = 1;
        var FILTER_STORAGE_SERVICE_KEY = 'record_list';

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
        };

        mainCtrl.sizeOptions = [
            { value: 10, label: '10' },
            { value: 20, label: '20' },
            { value: 50, label: '50' },
            { value: 100, label: '100' },
        ];

        /**
         * Filtres
         * @type {Object}
         */
        $scope.filters = mainCtrl.filters = {};

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
        mainCtrl.options = {};
        $scope.options = mainCtrl.options;

        init();

        /**
         * Initialisation du controleur
         * @return {[type]} [description]
         */
        function init() {
            HistorySrvc.add(gettextCatalog.getString('Gestion des notices'));
            // Chargement des données
            $q.all([
                NumaHopInitializationSrvc.loadLibraries(),
                NumaHopInitializationSrvc.loadProjects(),
                NumaHopInitializationSrvc.loadLots(),
                NumaHopInitializationSrvc.loadTrains(),
                DocUnitSrvc.getConfigFilterStatuses(),
            ]).then(function (data) {
                mainCtrl.options.libraries = data[0];
                mainCtrl.options.projects = data[1];
                mainCtrl.options.lots = data[2];
                mainCtrl.options.trains = data[3];
                mainCtrl.options.statuses = data[4];
                loadFilters();
                loadPageSize();
                getPage().then(function () {
                    mainCtrl.loaded = true;
                });
                // Affichage pour un temps limité à l'ouverture
                MessageSrvc.initPanel();
            });
        }

        function refreshFilterLists() {
            var librariesIds = _.pluck(mainCtrl.filters.libraries, 'identifier');
            var projectsIds = _.pluck(mainCtrl.filters.projects, 'identifier');
            NumaHopInitializationSrvc.loadProjects(librariesIds).then(function (data) {
                mainCtrl.options.projects = data;
                NumaHopInitializationSrvc.loadLots(librariesIds, projectsIds).then(function (data) {
                    mainCtrl.options.lots = data;
                });
                NumaHopInitializationSrvc.loadTrains(librariesIds, projectsIds).then(function (data) {
                    mainCtrl.options.trains = data;
                });
            });
        }

        function searchLibrary() {
            refreshFilterLists();
            search();
        }

        function searchProject() {
            refreshFilterLists();
            search();
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

            $scope.filters = mainCtrl.filters = {
                inactive: false,
            };
            mainCtrl.sortModel = [];
            search();
        }

        /**
         * Chargement de la prochaine page d'items
         * @return {[type]} [description]
         */
        function getPage() {
            mainCtrl.pagination.busy = true;
            var params = getSearchParams();
            return RecordSrvc.searchAsList(params, handlePageOfItems).$promise;
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

            mainCtrl.selection = {};
            savePageSize();
            saveFilters(newValue, field);
            getPage();
        }

        /**
         * Construction des paramètres de la recherche en fonction des filtres sélectionnés
         * @return {[type]} [description]
         */
        function getSearchParams(newValue, field) {
            var params = {};
            params['page'] = mainCtrl.pagination.page - 1;
            params['size'] = mainCtrl.pagination.size;
            //params["searchAsList"] = mainCtrl.filterWith || "";
            params['orphan'] = $scope.filters.orphan;

            // Bibliothèque
            if (mainCtrl.filters.libraries) {
                var librariesIds = _.pluck(mainCtrl.filters.libraries, 'identifier');
                params['libraries'] = librariesIds;
            }
            // Projet
            if (mainCtrl.filters.projects) {
                var projectsIds = _.pluck(mainCtrl.filters.projects, 'identifier');
                params['projects'] = projectsIds;
            }
            // Lot
            if (mainCtrl.filters.lots) {
                var lotsIds = _.pluck(mainCtrl.filters.lots, 'identifier');
                params['lots'] = lotsIds;
            }
            // Train
            if (mainCtrl.filters.trains) {
                var trainsIds = _.pluck(mainCtrl.filters.trains, 'identifier');
                params['trains'] = trainsIds;
            }
            // Statuses
            if ($scope.filters.wkf_statuses) {
                var statusesIds = _.pluck(mainCtrl.filters.wkf_statuses, 'identifier');
                params['statuses'] = statusesIds;
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

            params['searchAsList'] = mainCtrl.searchRequest || '';
            params['sorts'] = mainCtrl.sortModel;

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
                $scope.filters = mainCtrl.filters = filters.filters;
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
         * Ajoute la sélection à une unité documentaire mère
         */
        function updateSelection() {
            if (mainCtrl.selectedLength === 0) {
                MessageSrvc.addWarn(gettext('La sélection est vide'), {}, false);
                return;
            }

            // Sélection de la notice parente
            ModalSrvc.updateRecords().then(function (updates) {
                updates.recordIds = _.chain(mainCtrl.selection).values().pluck('identifier').value();
                RecordSrvc.update(updates).$promise.then(function () {
                    MessageSrvc.addSuccess(gettext('Les notices ont été mises à jour'));
                    search();
                });
            });
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
            ModalSrvc.confirmDeletion(gettextCatalog.getPlural(mainCtrl.selectedLength, 'la notice sélectionnée', 'les {{n}} notices sélectionnées', { n: mainCtrl.selectedLength })).then(function () {
                RecordSrvc.deleteSelection({}, _.keys(mainCtrl.selection), function () {
                    MessageSrvc.addSuccess(gettext('Les notices ont été supprimées'));
                    search();
                });
            });
        }

        function validateSelection() {
            if (mainCtrl.selectedLength === 0) {
                MessageSrvc.addWarn(gettext('La sélection est vide'), {}, false);
                return;
            }
            mainCtrl.selection = _.filter(mainCtrl.selection, function (selection) {
                return selection.lot != null;
            });
            if (mainCtrl.selection.length == 0) {
                MessageSrvc.addWarn(gettext("Aucun des documents n'appartient à un lot"), {}, false);
                return;
            }
            ModalSrvc.confirmAction(
                gettextCatalog.getString('valider ') + gettextCatalog.getPlural(mainCtrl.selectedLength, 'la notice sélectionnée', 'les {{n}} notices sélectionnées', { n: mainCtrl.selectedLength })
            ).then(function () {
                var docUnitIds = _.map(mainCtrl.selection, function (selection) {
                    return selection.docUnit.identifier;
                });
                var lotStatus = _.map(mainCtrl.selection, function (selection) {
                    return selection.lot.status;
                });
                WorkflowSrvc.massValidateRecords({ massValidateRecords: true }, docUnitIds).$promise.then(function () {
                    if (_.contains(lotStatus, 'CREATED')) {
                        MessageSrvc.addWarn(gettextCatalog.getString("Certains des documents n'appartiennent à un lot en cours"));
                    }
                    MessageSrvc.addSuccess(gettextCatalog.getString('La sélection a été validée'));
                    search();
                });
            });
        }
    }
})();
