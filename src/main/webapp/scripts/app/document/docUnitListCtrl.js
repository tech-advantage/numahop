(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('DocUnitListCtrl', DocUnitListCtrl);

    function DocUnitListCtrl($http, $httpParamSerializer, $q, $location, $routeParams, CONFIGURATION,
        DocUnitBaseService, DocUnitSrvc, DtoService, ErreurSrvc, FileSaver, gettext, gettextCatalog, HistorySrvc,
        NumahopUrlService, MessageSrvc, ModalSrvc, NumaHopInitializationSrvc, NumahopStorageService, ExportCinesSrvc,
        SelectionSrvc, ExportInternetArchiveSrvc, ExportOmekaSrvc, ExportDigitalLibrarySrvc, ExportHandlerSrvc) {

        var mainCtrl = this;

        /** check actions **/
        mainCtrl.changeItem = changeItem;
        mainCtrl.checkAll = checkAll;
        mainCtrl.uncheckAll = uncheckAll;
        /** others actions **/
        mainCtrl.addSelectionToDocUnit = addSelectionToDocUnit;
        mainCtrl.addSelectionToProject = addSelectionToProject;
        mainCtrl.addSelectionToNewProject = addSelectionToNewProject;
        mainCtrl.addSelectionToNewLot = addSelectionToNewLot;
        mainCtrl.addSelectionToNewTrain = addSelectionToNewTrain;
        mainCtrl.updateSelection = updateSelection;
        mainCtrl.deleteSelection = deleteSelection;

        mainCtrl.getPage = getPage;
        mainCtrl.getUrl = NumahopUrlService.getUrlDocUnit;
        mainCtrl.getUrlProject = NumahopUrlService.getUrlProject;
        mainCtrl.getUrlLot = NumahopUrlService.getUrlLot;
        mainCtrl.getUrlCreateProject = NumahopUrlService.getUrlCreateProject;
        mainCtrl.docs = DtoService.getDocs();
        mainCtrl.isFilterSelected = isFilterSelected;
        mainCtrl.reinitFilters = reinitFilters;
        mainCtrl.search = search;
        mainCtrl.searchLibrary = searchLibrary;
        mainCtrl.searchProject = searchProject;

        mainCtrl.canRemoveProject = DocUnitBaseService.canRemoveProject;
        mainCtrl.canRemoveLot = DocUnitBaseService.canRemoveLot;
        mainCtrl.canRemoveTrain = DocUnitBaseService.canRemoveTrain;
        mainCtrl.removeProject = DocUnitBaseService.removeProject;
        mainCtrl.removeLot = DocUnitBaseService.removeLot;
        mainCtrl.removeTrain = DocUnitBaseService.removeTrain;
        mainCtrl.unlink = unlink;

        mainCtrl.downloadCondReportTemplate = downloadCondReportTemplate;
        mainCtrl.uploadCondReport = uploadCondReport;

        mainCtrl.changePageSize = changePageSize;

        mainCtrl.massExport = massExport;
        mainCtrl.massCines = massCines;
        mainCtrl.massIA = massIA;
        mainCtrl.massOmeka = massOmeka;
        mainCtrl.massDigitalLibrary = massDigitalLibrary;

        var PAGE_START = 1;
        var FILTER_STORAGE_SERVICE_KEY = "doc_unit_list";

        /**
         * Recherche
         */
        mainCtrl.searchRequest = null;

        /**
         * Sélection
         */
        mainCtrl.selection = {};
        mainCtrl.selectedLength = 0;

        mainCtrl.sizeOptions = [
            { value: 10, label: "10" },
            { value: 20, label: "20" },
            { value: 50, label: "50" },
            { value: 100, label: "100" }
        ];
        /**
         * Objet de pagination
         * @type {Object}
         */
        mainCtrl.pagination = {
            items: [],
            totalItems: 0,
            busy: false,
            page: PAGE_START
        };
        /**
         * Filtres
         * @type {Object}
         */
        mainCtrl.filters = {
            libraries: [],
            available: true,
            active: true,
            projects: [],
            trains: [],
            wkf_statuses: [],
            archivable: true,
            nonArchivable: true,
            diffusable: true,
            nonDiffusable: true
        };

        /**
         * Filtres actifs
         * @type {Object}
         */
        mainCtrl.listFilters = {
            has_digital_documents_filter: true,
            inactive_filter: true,
            library_filter: true,
            last_modified_date_filter: true,
            project_filter: true,
            lot_filter: true,
            train_filter: true,
            wkf_status_filter: true,
            created_date_filter: true,
            archive_filter: true,
            diffusion_filter: true
        };

        /**
         * Modèle pour le tri
         * @type {Object}
         */
        mainCtrl.sortModel = ["-label"];

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
            lots: [],
            trains:[],
            statuses: []
        };

        init();

        /**
         * Initialisation du controleur
         * @return {[type]} [description]
         */
        function init() {
            HistorySrvc.add(gettextCatalog.getString("Gestion des unités documentaires"));
            mainCtrl.isFilteredByIds = !!$routeParams.searchresult;

            // Opérations groupées sur les résultats de recherche
            if (mainCtrl.isFilteredByIds) {
                initFromSearchResults();
            }

            // Chargement des données
            $q.all([
                NumaHopInitializationSrvc.loadLibraries(),
                NumaHopInitializationSrvc.loadProjects(),
                NumaHopInitializationSrvc.loadLots(),
                NumaHopInitializationSrvc.loadTrains(),
                DocUnitSrvc.getConfigFilterStatuses()])

                .then(function (data) {
                    mainCtrl.options.libraries = data[0];
                    mainCtrl.options.projects = data[1];
                    mainCtrl.options.lots = data[2];
                    mainCtrl.options.trains = data[3];
                    mainCtrl.options.statuses = data[4];

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

            // callback création projet / lot / train
            if ($routeParams["project"]) {
                addSelectionToNewProjectCallback($routeParams["project"], $routeParams["lot"], $routeParams["train"]);
                $location.search({});   // suppression du paramètre
            }
            // sélection d'UD pour ajout à un projet / lot / train
            else if ($routeParams["action"]) {
                buildAction();
            }
            // restauration de la sélection
            else {
                _.each(SelectionSrvc.get(SelectionSrvc.keys.DOC_UNIT_LIST),
                    function (sel) {
                        sel.checked = true;
                        changeItem(sel);
                    });
            }
        }

        /**
         * Chargement des résultats de recherche sélectionnés
         */
        function initFromSearchResults() {
            var searchSelection = SelectionSrvc.get("SEARCH_RESULT_DOCUNIT");
            // Sélection
            _.each(searchSelection, function (s) {
                mainCtrl.selection[s.identifier] = s;
            });
            // Filtre
            mainCtrl.filteredIds = _.pluck(searchSelection, "identifier");
        }

        function buildAction() {
            mainCtrl.action = {
                code: $routeParams["action"],
                projectId: $routeParams["toProject"],
                lotId: $routeParams["toLot"],
                trainId: $routeParams["toTrain"]
            };
            switch ($routeParams["action"]) {
                case "add_to_project":
                    mainCtrl.action.label = gettextCatalog.getString("Ajouter la sélection au projet");
                    mainCtrl.action.cancelLabel = gettextCatalog.getString("Revenir au projet");
                    break;
                case "add_to_lot":
                    mainCtrl.action.label = gettextCatalog.getString("Ajouter la sélection au lot");
                    mainCtrl.action.cancelLabel = gettextCatalog.getString("Revenir au lot");
                    break;
                case "add_to_train":
                    mainCtrl.action.label = gettextCatalog.getString("Ajouter la sélection au train");
                    mainCtrl.action.cancelLabel = gettextCatalog.getString("Revenir au train");
                    break;
            }
            if ($routeParams.callback) {
                mainCtrl.action.callback = function () {
                    addDocUnitsToProject(mainCtrl.selection, mainCtrl.action.projectId, mainCtrl.action.lotId, mainCtrl.action.trainId)
                        .then(function () {
                            $location.url($routeParams.callback);
                        });
                };
                mainCtrl.action.cancel = function () {
                    $location.url($routeParams.callback);
                };
            }
        }

        function refreshFilterLists() {
            var librariesIds = _.pluck(mainCtrl.filters.libraries, "identifier");
            var projectsIds = _.pluck(mainCtrl.filters.projects, "identifier");
            NumaHopInitializationSrvc.loadProjects(librariesIds)
                .then(function (data) {
                    mainCtrl.options.projects = data;
                    NumaHopInitializationSrvc.loadLots(librariesIds, projectsIds)
                        .then(function (data) {
                            mainCtrl.options.lots = data;
                        });
                    NumaHopInitializationSrvc.loadTrains(librariesIds, projectsIds)
                    .then(function (data) {
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

            mainCtrl.filters.libraries = [];
            mainCtrl.filters.projects = [];
            mainCtrl.filters.lots = [];
            mainCtrl.filters.trains = [];
            mainCtrl.filters.wkf_statuses = [];
            mainCtrl.filters.hasDigitalDocuments = false;
            mainCtrl.filters.inactive = false;
            mainCtrl.filters.lastModifiedDateFrom = null;
            mainCtrl.filters.lastModifiedDateTo = null;
            mainCtrl.filters.createdDateFrom = null;
            mainCtrl.filters.createdDateTo = null;
            mainCtrl.filters.available = true;
            mainCtrl.filters.archivable = true;
            mainCtrl.filters.nonArchivable = true;
            mainCtrl.filters.diffusable = true;
            mainCtrl.filters.nonDiffusable = true;
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
            params["page"] = mainCtrl.pagination.page - 1;
            params["size"] = mainCtrl.pagination.size;
            params["sorts"] = mainCtrl.sortModel;

            var body = getSearchParams();

            return DocUnitSrvc.searchAsList(params, body).$promise.then(handlePageOfItems);
        }

        /**
         * handlePageOfItems - Gestion d'une page d'entités
         *
         * @param  {type} pageOfReserves la page avec les entités
         */
        function handlePageOfItems(pageOfItems) {
            mainCtrl.pagination.totalItems = pageOfItems.totalElements;
            mainCtrl.pagination.totalPages = pageOfItems.totalPages;
            mainCtrl.pagination.items = pageOfItems.content;

            _.each(mainCtrl.pagination.items, function (item) {
                // l'item est sélectionné
                item.checked = angular.isDefined(mainCtrl.selection[item.identifier]);
                // mise à jour de la sélection
                if (item.checked) {
                    mainCtrl.selection[item.identifier] = item;
                }
            });
            mainCtrl.pagination.busy = false;
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
            savePageSize();
            saveFilters(newValue, field);


            var params = {};
            params["page"] = mainCtrl.pagination.page - 1;
            params["size"] = mainCtrl.pagination.size;
            params["sorts"] = mainCtrl.sortModel;

            var body = getSearchParams(newValue, field);

            DocUnitSrvc.searchAsList(params, body).$promise.then(handlePageOfItems);
        }

        /**
         * Construction des paramètres de la recherche en fonction des filtres sélectionnés
         * @return {[type]} [description]
         */
        function getSearchParams(newValue, field) {
            // Filtrage à partir des résultats de recherche
            if (mainCtrl.isFilteredByIds && mainCtrl.filteredIds) {
                return {
                    filter: mainCtrl.filteredIds
                };
            }

            var params = {};
            params["search"] = mainCtrl.searchRequest || "";
            params["available"] = mainCtrl.filters.available;
            params["hasDigitalDocuments"] = mainCtrl.filters.hasDigitalDocuments;
            params["active"] = !mainCtrl.filters.inactive;
            params["archived"] = mainCtrl.filters.archived;
            params["nonArchived"] = mainCtrl.filters.nonArchived;
            params["archivable"] = mainCtrl.filters.archivable;
            params["nonArchivable"] = mainCtrl.filters.nonArchivable;
            params["distributed"] = mainCtrl.filters.distributed;
            params["nonDistributed"] = mainCtrl.filters.nonDistributed;
            params["diffusable"] = mainCtrl.filters.diffusable;
            params["nonDiffusable"] = mainCtrl.filters.nonDiffusable;

            // Bibliothèque
            if (mainCtrl.filters.libraries) {
                var librariesIds = _.pluck(mainCtrl.filters.libraries, "identifier");
                params["libraries"] = librariesIds;
            }
            // Projet
            if (mainCtrl.filters.projects) {
                var projectsIds = _.pluck(mainCtrl.filters.projects, "identifier");
                params["projects"] = projectsIds;
            }
            // Lot
            if (mainCtrl.filters.lots) {
                var lotsIds = _.pluck(mainCtrl.filters.lots, "identifier");
                params["lots"] = lotsIds;
            }
            // train
            if (mainCtrl.filters.trains) {
                var trainsIds = _.pluck(mainCtrl.filters.trains, "identifier");
                params["trains"] = trainsIds;
            }
            // statut
            if (mainCtrl.filters.wkf_statuses) {
                var statuses = _.pluck(mainCtrl.filters.wkf_statuses, "identifier");
                params["statuses"] = statuses;
            }
            if (mainCtrl.filters.createdDateFrom) {
                params["createdDateFrom"] = mainCtrl.filters.createdDateFrom;
            }
            if (mainCtrl.filters.createdDateTo) {
                params["createdDateTo"] = mainCtrl.filters.createdDateTo;
            }
            if (mainCtrl.filters.lastModifiedDateFrom) {
                params["lastModifiedDateFrom"] = mainCtrl.filters.lastModifiedDateFrom;
            }
            if (mainCtrl.filters.lastModifiedDateTo) {
                params["lastModifiedDateTo"] = mainCtrl.filters.lastModifiedDateTo;
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
         * Ajoute la sélection à une unité documentaire mère
         */
        function addSelectionToDocUnit() {
            if (mainCtrl.selectedLength === 0) {
                MessageSrvc.addWarn(gettext("La sélection est vide"), {}, false);
                return;
            }
            var docs = _.chain(mainCtrl.selection)
                .values()
                .pluck("identifier")
                .value();

            // Sélection de la notice parente
            var params = { multiple: false, disabled: docs };
            ModalSrvc.selectDocUnit(params)
                .then(function (result) {
                    if (result.length > 0) {
                        return DocUnitSrvc.save({ parent: result[0].identifier, addchild: true }, docs).$promise;
                    }
                    return $q.reject();
                })
                .then(function (parentUnit) {
                    MessageSrvc.addSuccess(gettext("Les unités documentaires sélectionnées ont été rattachées à {{pgcnId}} {{label}}"), parentUnit);
                    search();
                });
        }

        /**
         * Modifier un projet/ lot/ train.
         **/
        function addSelectionToProject() {
            var docs = getValidSelection("project");
            var ud = _.find(mainCtrl.selection, function (item) { return item.project; });
            var proj = angular.isDefined(ud) && ud.project ? ud.project : undefined;

            // Preselection lot et/ou train ?
            if (angular.isDefined(proj)) {
                var lot = getPreSelectedLot(ud.lot);
                var train = undefined;
                if (ud.train !== null) {
                    train = getPreSelectedTrain(ud.train);
                }

            }
            if (docs) {
                ModalSrvc.integrateToProject(docs, "sm", proj, lot, train)
                    .then(function () {
                        search();
                    });
            }
        }

        /**
         * Retourne le lot à preselectionner s'il y a lieu.
         */
        function getPreSelectedLot(oneLot) {
            var lot = undefined;
            if (oneLot !== null && angular.isDefined(oneLot) && angular.isDefined(oneLot.identifier)) {
                var anotherLot = _.find(mainCtrl.selection, function (item) {
                    return item.lot && item.lot.identifier && item.lot.identifier !== oneLot.identifier;
                });
                if (angular.isUndefined(anotherLot)) {
                    // ok, tous les docs appartiennent au mm lot.
                    lot = oneLot;
                }
            }
            return lot;
        }

        /**
         * Retourne le train à preselectionne s'il y a lieu.
         */
        function getPreSelectedTrain(oneTrain) {
            var train = undefined;
            if (angular.isDefined(oneTrain) && angular.isDefined(oneTrain.identifier)) {
                var anotherTrain = _.find(mainCtrl.selection, function (item) {
                    return item.train && item.train.identifier && item.train.identifier !== oneTrain.identifier;
                });
                if (angular.isUndefined(anotherTrain)) {
                    // ok, tous les docs appartiennent au mm train.
                    train = oneTrain;
                }
            }
            return train;
        }

        /**
         * Ajout de la sélection à un nouveau projet
         * On sauvegarde la sélection courante dans SelectionSrvc,
         * avec de basculer sur la page de création de projet.
         */
        function addSelectionToNewProject() {
            var docs = getValidSelectionForProject();
            var searchParams = { new: true, callback: "/document/docunit_list" };
            if (docs) {
                SelectionSrvc.set(SelectionSrvc.keys.DOC_UNIT_LIST, docs);
                $location.path("/project/project").search(searchParams);
            }
        }

        /**
         * Ajout de la sélection à un nouveau lot
         * On sauvegarde la sélection courante dans SelectionSrvc,
         * avec de basculer sur la page de création de lot.
         */
        function addSelectionToNewLot() {
            addSelectionToNewObject("/lot/lot", "lot");
        }

        /**
         * Ajout de la sélection à un nouveau train
         * On sauvegarde la sélection courante dans SelectionSrvc,
         * avec de basculer sur la page de création de train.
         */
        function addSelectionToNewTrain() {
            addSelectionToNewObject("/train/train", "train");
        }

        function addSelectionToNewObject(path, type) {
            var docs = getValidSelection(type);
            var searchParams = { new: true, callback: "/document/docunit_list" };

            var ud = _.find(mainCtrl.selection, function (item) { return item.project; });
            var projId = angular.isDefined(ud) && ud.project && ud.project.identifier ? ud.project.identifier : undefined;
            if (angular.isDefined(projId)) {
                searchParams.project = projId;
            }

            if (docs) {
                SelectionSrvc.set(SelectionSrvc.keys.DOC_UNIT_LIST, docs);
                $location.path(path).search(searchParams);
            }
        }

        /**
         * Ajout de la sélection à un nouveau projet / lot / train
         * Une fois le projet créé, on revient ici pour y lier la sélection
         *
         * @param {any} projectId
         * @returns
         */
        function addSelectionToNewProjectCallback(projectId, lotId, trainId) {
            var selection = SelectionSrvc.get(SelectionSrvc.keys.DOC_UNIT_LIST);

            addDocUnitsToProject(selection, projectId, lotId, trainId)
                .then(function () {
                    search();   // rafraichissement de la liste
                });
        }

        /**
         * Ajout de la sélection à un projet / lot / train
         *
         * @param {any} docUnits
         * @param {any} projectId
         * @param {any} lotId
         * @param {any} trainId
         * @returns
         */
        function addDocUnitsToProject(docUnits, projectId, lotId, trainId) {
            if (docUnits.length === 0) {
                MessageSrvc.addWarn(gettext("Aucune unité documentaire n'est sélectionnée"), {}, false);
                return $q.when();
            }
            var params = {};
            if (projectId) {
                params.project = projectId;
            }
            if (lotId) {
                params.lot = lotId;
            }
            if (trainId) {
                params.train = trainId;
            }
            var body = _.pluck(docUnits, "identifier");

            var defer = $q.defer();
            DocUnitSrvc.addToProjectAndLot(params, body,
                function () { defer.resolve(); },
                function () { defer.reject(); });
            return defer.promise;
        }

        /**
         * Retourne les UD sélectionnées et valides pour l'ajout à un lot/train
         *
         * @returns
         */
        function getValidSelection(type) {
            if (mainCtrl.selectedLength === 0) {
                MessageSrvc.addWarn(gettext("La sélection est vide"), {}, false);
                return;
            }

            // On autorise la selection d'UDs appartenant ttes au mm projet, ou alors toutes sans projet.
            var testValue = _.find(mainCtrl.selection, function (item) { return item.project; });
            var filteredDocs = [];
            if (angular.isDefined(testValue)) {
                filteredDocs = _.filter(mainCtrl.selection, function (item) {
                    return item.project && item.project.identifier === testValue.project.identifier;
                });
                if ('CLOSED' === testValue.project.status) {
                    MessageSrvc.addWarn(gettext("Les unités documentaires sélectionnées ne peuvent pas être traitées car le projet est au statut 'Terminé'"), {}, false);
                    return;
                }
            } else {
                filteredDocs = _.filter(mainCtrl.selection, function (item) {
                    return !item.project;
                });
            }
            if (mainCtrl.selectedLength !== filteredDocs.length) {
                MessageSrvc.addWarn(gettext("Les unités documentaires sélectionnées ne peuvent pas être traitées car elles appartiennent à des projets différents"), {}, false);
                return;
            }

            // On autorise la sélection d'UDs :
            //   - avec les lots s'ils sont au statut Créé (CREATED),
            //   - ou sans lot.
            if(type == "lot"){
                filteredDocs = _.filter(mainCtrl.selection, function (item) {
                    return !item.lot || mainCtrl.canRemoveLot(item);
                });
                if (mainCtrl.selectedLength !== filteredDocs.length) {
                    MessageSrvc.addWarn(gettext("Les unités documentaires sélectionnées ne peuvent pas être traitées car certains lots ont un statut 'En cours' ou 'Terminé'"), {}, false);
                    return;
                }
            }

            var docs = _.chain(mainCtrl.selection)
                .values()
                .map(function (item) {
                    return _.pick(item, "identifier", "label", "lot");
                })
                .value();
            return docs;
        }

        /**
         * Retourne les UD sélectionnées et valides pour l'ajout à un projet
         *
         * @returns
         */
        function getValidSelectionForProject() {
            if (mainCtrl.selectedLength === 0) {
                MessageSrvc.addWarn(gettext("La sélection est vide"), {}, false);
                return;
            }
            var docs = _.chain(mainCtrl.selection)
                .values()
                .filter(function (item) {
                    return !item.project;
                })
                .map(function (item) {
                    return _.pick(item, "identifier", "label");
                })
                .value();

            if (docs.length === 0) {
                MessageSrvc.addWarn(gettext("Les unités documentaires sélectionnées ne peuvent pas être traitées car elles appartiennent déjà à un projet existant"), {}, false);
                return;
            }
            return docs;
        }


        /**
         * Modification des UD selectionnées.
         */
        function updateSelection() {
            if (mainCtrl.selectedLength === 0) {
                MessageSrvc.addWarn(gettext("La sélection est vide"), {}, false);
                return;
            }

            ModalSrvc.modalUpdateDocUnitResults()
                .then(function (updates) {

                    updates.condReportType = updates.reportType['identifier'];
                    updates.docUnitIds = _.chain(mainCtrl.selection).values().pluck("identifier").value();

                    var deferred = $q.defer();
                    DocUnitSrvc.updateSelection(updates).$promise
                        .then(function (values) {
                            deferred.resolve(values);
                            if (values.length && values.length > 0) {
                                _.each(values, function (val) {
                                    MessageSrvc.addWarn(gettext("Le document [{{id}}] - {{label}} n'a pas été modifié: {{msg}}."),
                                        { id: val.identifier, label: val.label, msg: val.message }, false, 30000);
                                });
                            }
                            if (values.length < updates.docUnitIds.length) {
                                MessageSrvc.addSuccess(gettext("Les unités documentaires ont été mises à jour"));
                            }

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
                MessageSrvc.addWarn(gettext("La sélection est vide"), {}, false);
                return;
            }
            ModalSrvc.confirmDeletion(gettextCatalog.getPlural(mainCtrl.selectedLength,
                "l'unité documentaire sélectionnée",
                "les {{n}} unités documentaires sélectionnées", { n: mainCtrl.selectedLength }), 'xl', gettext("Les notices correspondantes seront également supprimées."))
                .then(function () {
                    DocUnitSrvc.deleteSelection({}, _.keys(mainCtrl.selection), function (value) {
                        if (value.length > 0) {
                            ModalSrvc.modalDeleteDocUnitResults(value, 'xl');
                        } else {
                            MessageSrvc.addSuccess(gettext("Aucune unité documentaire n'a été supprimée"));
                        }
                        search();
                    });
                });
        }

        /**
         * Détache les UD selectionnees des projet/lot/train concernés.
         */
        function unlink() {
            if (mainCtrl.selectedLength === 0) {
                MessageSrvc.addWarn(gettext("La sélection est vide"), {}, false);
                return;
            }
            DocUnitSrvc.unlink({}, _.keys(mainCtrl.selection), function (value) {
                if (value.length > 0) {
                    ModalSrvc.modalUnlinkDocUnitResults(value, 'xl');
                } else {
                    MessageSrvc.addSuccess(gettext("Les unités documentaires ont été détachées du projet / lot / train"));
                }
                search();
            });
        }


        /**
         * Téléchargement d'un modèle d'import de constats d'état pour les unités documentaires sélectionnées
         */
        function downloadCondReportTemplate() {
            if (mainCtrl.selectedLength === 0) {
                MessageSrvc.addWarn(gettext("La sélection est vide"), {}, false);
                return;
            }
            var params = {
                "import-template": _.pluck(mainCtrl.selection, "identifier"),
                "format": "XLSX",
                "sortAttributes": mainCtrl.sortModel
            };
            var url = 'api/rest/condreport?' + $httpParamSerializer(params);

            // on met la réponse dans un arraybuffer pour conserver l'encodage original dans le fichier sauvegardé
            $http.get(url, { responseType: 'arraybuffer' })
                .then(function (response) {
                    var filename = "condition_report_import.xlsx";
                    var blob = new Blob([response.data], { type: response.headers("content-type") });
                    FileSaver.saveAs(blob, filename);
                });
        }

        /**
         * Téléchargement d'une archive pour l'export de masse
         */
        function massExport() {
            ModalSrvc.selectExportTypes()
                .then(function (types) {
                    if (mainCtrl.selectedLength === 0) {
                        MessageSrvc.addWarn(gettext("La sélection est vide"), {}, false);
                        return;
                    }

                    var filteredWithoutRecord = _.filter(mainCtrl.selection, function (sel) {
                        return sel.hasRecord === false;
                    });
                    if (filteredWithoutRecord.length > 0) {
                        MessageSrvc.addWarn(gettext(filteredWithoutRecord.length + " unité(s) documentaire(s) sélectionnée(s) sans notice. Les métadonnées (Mets) ne seront pas exportées."), {}, false);
                    }
                    ExportHandlerSrvc.massExport(
                        _.pluck(mainCtrl.selection, "identifier"),
                        {
                            types: types
                        });
                });
        }

        /**
         * Archivage CINES
         */
        function massCines() {
            if (mainCtrl.selectedLength === 0) {
                MessageSrvc.addWarn(gettext("La sélection est vide"), {}, false);
                return;
            }
            var notValids = _.filter(mainCtrl.selection, function (ud) {
                return ud.digitalDocumentStatus !== 'VALIDATED';
            });
            var params = {
                "docs": _.pluck(_.difference(mainCtrl.selection, notValids), "identifier")
            };

            if (notValids.length > 0) {
                // seuls les docs validés peuvent etre archivés.
                if (params.docs.length === 0) {
                    MessageSrvc.addWarn(gettext("Aucun document n'est validé. L'archivage Cines est impossible."), {}, false, 30000);
                    return;
                } else {
                    _.each(notValids, function (nv) {
                        MessageSrvc.addWarn(gettext("Le document [{{id}}]{{name}} n'est pas validé. Il ne sera pas archivé au Cines."), { id: nv.pgcnId, name: nv.label }, false, 30000);
                    });
                }
            }
            MessageSrvc.addSuccess(gettext("Lancement de l'archivage CINES des documents"), {}, false, 10000);
            ExportCinesSrvc.massExport(params).$promise
                .then(function () {
                    MessageSrvc.addSuccess(gettext("L'archivage CINES des documents a été effectué. Pour plus de détails, consultez la page des documents concernés"));
                });
        }

        function massOmeka() {
            if (mainCtrl.selectedLength === 0) {
                MessageSrvc.addWarn(gettext("La sélection est vide"), {}, false);
                return;
            }
            var notValids = _.filter(mainCtrl.selection, function (ud) {
                return ud.digitalDocumentStatus !== 'VALIDATED';
            });
            var params = {
                "docs": _.pluck(_.difference(mainCtrl.selection, notValids), "identifier")
            };
            if (notValids.length > 0) {
                // seuls les docs validés peuvent etre archivés.
                if (params.docs.length === 0) {
                    MessageSrvc.addWarn(gettext("Aucun document n'est validé. La diffusion vers Omeka est impossible."), {}, false, 30000);
                    return;
                } else {
                    _.each(notValids, function (nv) {
                        MessageSrvc.addWarn(gettext("Le document [{{id}}]{{name}} n'est pas validé. Il ne sera pas diffusé."), { id: nv.pgcnId, name: nv.label }, false, 30000);
                    });
                }
            }
            MessageSrvc.addSuccess(gettext("Lancement de la diffusion vers Omeka des documents"), {}, false, 10000);
            ExportOmekaSrvc.massExport(params).$promise
            .then(function () {
                MessageSrvc.addSuccess(gettext("La diffusion vers Omeka des documents a été effectuée. Pour plus de détails, consultez la page des documents concernés"));
            });
        }

        function massIA() {
            if (mainCtrl.selectedLength === 0) {
                MessageSrvc.addWarn(gettext("La sélection est vide"), {}, false);
                return;
            }
            var notValids = _.filter(mainCtrl.selection, function (ud) {
                return ud.digitalDocumentStatus !== 'VALIDATED';
            });
            var params = {
                "docs": _.pluck(_.difference(mainCtrl.selection, notValids), "identifier")
            };
            if (notValids.length > 0) {
                // seuls les docs validés peuvent etre archivés.
                if (params.docs.length === 0) {
                    MessageSrvc.addWarn(gettext("Aucun document n'est validé. La diffusion IA est impossible."), {}, false, 30000);
                    return;
                } else {
                    _.each(notValids, function (nv) {
                        MessageSrvc.addWarn(gettext("Le document [{{id}}]{{name}} n'est pas validé. Il ne sera pas diffusé."), { id: nv.pgcnId, name: nv.label }, false, 30000);
                    });
                }
            }
            MessageSrvc.addSuccess(gettext("Lancement de la diffusion IA des documents"), {}, false, 10000);
            ExportInternetArchiveSrvc.massExport(params).$promise
                .then(function () {
                    MessageSrvc.addSuccess(gettext("La diffusion IA des documents a été effectuée. Pour plus de détails, consultez la page des documents concernés"));
                });
        }

        /**
        * diffusion sur la bibliothèque numérique
        */
        function massDigitalLibrary(){
            if (mainCtrl.selectedLength === 0) {
                MessageSrvc.addWarn(gettext("La sélection est vide"), {}, false);
                return;
            }
            var notValids = _.filter(mainCtrl.selection, function (ud) {
                return ud.digitalDocumentStatus !== 'VALIDATED';
            });
            var params = {
                "docs": _.pluck(_.difference(mainCtrl.selection, notValids), "identifier")
            };
            if (notValids.length > 0) {
                // seuls les docs validés peuvent etre archivés.
                if (params.docs.length === 0) {
                    MessageSrvc.addWarn(gettext("Aucun document n'est validé. La diffusion sur la bibliothèque numérique est impossible."), {}, false, 30000);
                    return;
                } else {
                    _.each(notValids, function (nv) {
                        MessageSrvc.addWarn(gettext("Le document [{{id}}]{{name}} n'est pas validé. Il ne sera pas diffusé."), { id: nv.pgcnId, name: nv.label }, false, 30000);
                    });
                }
            }
            MessageSrvc.addSuccess(gettext("Lancement de la diffusion des documents sur la bibliothèque numérique"), {}, false, 10000);
            ExportDigitalLibrarySrvc.massExport(params).$promise
                .then(function () {
                    MessageSrvc.addSuccess(gettext("La diffusion des documents sur la bibliothèque numérique a été effectuée. Pour plus de détails, consultez la page des documents concernés"));
                });
        }

        /**
         * Téléversement des constats d'état, complétés à partir d'un modèle d'import
         */
        function uploadCondReport() {
            ModalSrvc.selectFile()
                .then(function (files) {
                    var url = CONFIGURATION.numahop.url + 'api/rest/condreport';

                    var formData = new FormData();
                    formData.append("import-report", true);

                    // Emplacement du/des fichiers à importer
                    _.each(files, function (file) {
                        formData.append("file", file);
                    });

                    var config = {
                        transformRequest: angular.identity,
                        headers: {
                            'Content-Type': undefined
                        }
                    };
                    $http.post(url, formData, config)
                        .success(function (data) {
                            showErrorMessage(data);
                            showSuccessMessage(data);
                        })
                        .error(function () {
                            MessageSrvc.addError(gettext("Échec lors du téléversement du fichier"));
                        });
                });
        }

        function showErrorMessage(response) {
            _.chain(response)
                // Filtrage des constats comportant des erreurs
                .filter(function (data) {
                    return data.errors && data.errors.length;
                })
                // récupération des erreurs + pgcnId
                .map(function (data) {
                    return _.chain(data.errors)
                        .pluck("code")
                        .uniq()
                        .map(function (error) {
                            return {
                                error: error,
                                pgcnId: data.pgcnId
                            };
                        })
                        .value();
                })
                .flatten()
                // Regroupement des pgcnId par code d'erreur
                .groupBy("error")
                .pairs()
                // Pour chaque paire [code erreur; liste de pgcnId], afficher un message d'erreur
                .each(function (pair) {
                    MessageSrvc.addError(ErreurSrvc.getMessage(pair[0]) + ": "
                        + _.chain(pair[1])
                            .pluck("pgcnId")
                            .reduce(function (a, b) {
                                return a + "<br/>" + b;
                            }, "")
                            .value());
                });
        }

        function showSuccessMessage(response) {
            var message = _.chain(response)
                .filter(function (data) {
                    return !data.errors || !data.errors.length;
                })
                .pluck("pgcnId")
                .reduce(function (a, b) {
                    return a + "<br/>" + b;
                }, "")
                .value();

            MessageSrvc.addSuccess(gettext("L'import s'est correctement exécuté pour les unités documentaires:{{ud}}"), { ud: message }, true);
        }


    }
})();
