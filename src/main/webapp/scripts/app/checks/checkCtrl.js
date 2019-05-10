(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('CheckCtrl', CheckCtrl);

    function CheckCtrl($q, $location, $routeParams, codeSrvc, DeliverySrvc, DigitalDocumentSrvc, DocUnitSrvc, DtoService,
        gettext, gettextCatalog, HistorySrvc, LotSrvc, MessageSrvc, NumahopUrlService, NumahopStorageService,
        NumaHopInitializationSrvc, SelectionSrvc, TrainSrvc, WorkflowSrvc, WorkflowHandleSrvc, CondreportDescValueSrvc) {

        var mainCtrl = this;

        mainCtrl.getPage = getPage;
        mainCtrl.getUrl = NumahopUrlService.getUrlDocUnit;
        mainCtrl.getUrlProject = NumahopUrlService.getUrlProject;
        mainCtrl.getUrlLot = NumahopUrlService.getUrlLot;
        mainCtrl.getUrlTrain = NumahopUrlService.getUrlTrain;
        mainCtrl.getUrlCreateProject = NumahopUrlService.getUrlCreateProject;
        mainCtrl.docs = DtoService.getDocs();
        mainCtrl.isFilterSelected = isFilterSelected;
        mainCtrl.reinitFilters = reinitFilters;
        mainCtrl.search = search;
        mainCtrl.reloadDependencies = reloadDependencies;
        mainCtrl.reloadLivraisons = reloadLivraisons;
        mainCtrl.goToCheck = goToCheck;
        mainCtrl.codes = codeSrvc;

        mainCtrl.createLot = createLot;
        mainCtrl.createTrain = createTrain;
        mainCtrl.checkAll = checkAll;
        mainCtrl.uncheckAll = uncheckAll;
        mainCtrl.changeItem = changeItem;
        mainCtrl.changePageSize = changePageSize;
        mainCtrl.getSelectionLength = getSelectionLength;
        mainCtrl.getSelectionInsurance = getSelectionInsurance;
        mainCtrl.getSelectionDimensions = getSelectionDimensions;
        mainCtrl.getSelectionPages = getSelectionPages;

        mainCtrl.accSearchObj = false;
        mainCtrl.accSearchText = false;
        mainCtrl.accSearchDate = false;
        mainCtrl.accSearchPages = false;

        var PAGE_START = 1;
        var FILTER_STORAGE_SERVICE_KEY = "doc_unit_list";
        mainCtrl.pageSize = null;

        /**
         * Recherche
         */
        mainCtrl.searchRequest = null;

        /**
         * Objet de pagination
         * @type {Object}
         */
        mainCtrl.pagination = {
            items: [],
            totalItems: 0,
            busy: false,
            page: PAGE_START,
            size: mainCtrl.pageSize
        };
        mainCtrl.sizeOptions = [
            { value: 10, label: "10" },
            { value: 20, label: "20" },
            { value: 50, label: "50" },
            { value: 100, label: "100" },
            { value: null, label: "Tout" }
        ];
        /**
         * Filtres
         * @type {Object}
         */
        mainCtrl.filters = {
            validated: false,
            libraries: [],
            projects: [],
            lots: [],
            trains: [],
            livraisons: [],
            statuses: [],
            dateFrom: '',
            dateTo: '',
            dateLimitFrom: '',
            dateLimitTo: '',
            searchPgcnId: '',
            searchTitre: '',
            searchRadical: '',
            searchFormatDocument: [],
            searchMaxAngle: [],
            searchPageFrom: '',
            searchPageTo: '',
            searchPageCheckFrom: '',
            searchPageCheckTo: '',
            searchMinSize: '',
            searchMaxSize: ''
        };

        mainCtrl.selection = {};

        /**
         * Modèle pour le tri
         * @type {Object}
         */
        mainCtrl.sortModel = ["pgcnId"];

        /**
         * Liste des options pour les listes déroulantes
         */
        mainCtrl.options = {
            libraries: [],
            projects: [],
            lots: [],
            trains: [],
            livraisons: [],
            formatDocuments: [],
            maxAngles: []
        };

        mainCtrl.options.statuses = ["TO_CHECK", "CHECKING", "VALIDATED", "PRE_REJECTED", "REJECTED", "DELIVERING_ERROR", "WAITING_FOR_REPAIR"];

        init();

        /**
         * Initialisation du controleur
         * @return {[type]} [description]
         */
        function init() {
            HistorySrvc.add(gettextCatalog.getString("Catalogue toutes opérations"));
            loadPageSize();

            $q.all([NumaHopInitializationSrvc.loadLibraries(), NumaHopInitializationSrvc.loadProjects(), 
                    CondreportDescValueSrvc.query({ property: 'MAX_ANGLE' })])
                .then(function (data) {
                    mainCtrl.options.libraries = data[0];
                    mainCtrl.options.projects = data[1];
                    // charge la liste des ouvertures maximales.
                    data[2].$promise.then(function(prom) {
                        mainCtrl.options.maxAngles = prom;
                    });
                    // types de fichiers
                    mainCtrl.options.formatDocuments = LotSrvc.config.fileFormat;
                    
                    loadFilters();
                    reloadDependencies(false);
                    if ($routeParams["reinitStatusFilter"]) {
                        mainCtrl.filters.statuses = [];
                    }
                    if ($routeParams["radical"]) {
                        mainCtrl.filters.searchRadical = $routeParams["radical"];
                    }
                    getPage();
                });

            // callback création lot
            if ($routeParams["project"]) {
                addSelectionToNewLotCallback($routeParams["project"], $routeParams["lot"], $routeParams["train"]);  // train est optionnel
                $location.search({});   // suppression du paramètre
            }
        }

        function changePageSize() {
            mainCtrl.pagination.size = mainCtrl.pageSize;
            search();
        }

        function loadPageSize() {
            mainCtrl.pageSize = NumahopStorageService.getPageSize(FILTER_STORAGE_SERVICE_KEY);
            mainCtrl.pagination.size = mainCtrl.pageSize;
        }

        function savePageSize() {
            NumahopStorageService.savePageSize(FILTER_STORAGE_SERVICE_KEY, mainCtrl.pageSize);
        }

        /**
         * Restriction de la liste des lots aux projets sélectionnés.
         */
        function restrictLotsByProjects(projectsIds) {
            return LotSrvc.query({ filterByProjects: "", projectIds: projectsIds }).$promise;
        }

        /**
         * Restriction de la liste des trains aux projets sélectionnés.
         */
        function restrictTrainsByProjects(projectsIds) {
            return TrainSrvc.query({ filterByProjects: "", projectIds: projectsIds }).$promise;
        }

        /**
         * Gere les rechargements des lots / trains dependant du (des) projet(s).
         */
        function reloadDependencies(reload) {
            var filteredProjects = undefined;
            if (mainCtrl.filters.projects) {
                filteredProjects = _.pluck(mainCtrl.filters.projects, "identifier");
            }
            if (filteredProjects && filteredProjects.length > 0) {
                // Filtre sur les projets selectionnes.
                restrictLotsByProjects(filteredProjects).then(function (res) {
                    mainCtrl.options.lots = res;
                });
                restrictTrainsByProjects(filteredProjects).then(function (res) {
                    mainCtrl.options.trains = res;
                });
            } else {
                NumaHopInitializationSrvc
                    .loadLots()
                    .then(function (res) {
                        mainCtrl.options.lots = res;
                    });
                NumaHopInitializationSrvc
                    .loadTrains()
                    .then(function (res) {
                        mainCtrl.options.trains = res;
                    });
            }
            reloadLivraisons(reload);
        }

        /**
         * Gere le rechargement des livraisons en fonction des projets/lots selectionnés.
         */
        function reloadLivraisons(reload) {
            var filteredProjects = undefined;
            var filteredLots = undefined;
            if (mainCtrl.filters.projects) {
                filteredProjects = _.pluck(mainCtrl.filters.projects, "identifier");
            }
            if (mainCtrl.filters.lots) {
                filteredLots = _.pluck(mainCtrl.filters.lots, "identifier");
            }

            if ((!filteredProjects || filteredProjects.length === 0)
                && (!filteredLots || filteredLots.length === 0)) {
                DeliverySrvc.query({ dto: "" }).$promise
                    .then(function (res) {
                        mainCtrl.options.livraisons = res;
                    });
            } else {
                DeliverySrvc.query({
                    filterByProjectsLots: "",
                    filteredProjects: filteredProjects,
                    filteredLots: filteredLots
                })
                    .$promise
                    .then(function (res) {
                        mainCtrl.options.livraisons = res;
                    });
            }
            if (reload) {
                search();
            }
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

            mainCtrl.filters.validated = false;
            mainCtrl.filters.libraries = [];
            mainCtrl.filters.projects = [];
            mainCtrl.filters.lots = [];
            mainCtrl.filters.trains = [];
            mainCtrl.filters.livraisons = [];
            mainCtrl.filters.dateFrom = '';
            mainCtrl.filters.dateTo = '';
            mainCtrl.filters.dateLimitFrom = '';
            mainCtrl.filters.dateLimitTo = '';
            mainCtrl.filters.searchPgcnId = '';
            mainCtrl.filters.searchTitre = '';
            mainCtrl.filters.searchRadical = '';
            mainCtrl.filters.searchFormatDocument = [];
            mainCtrl.filters.searchMaxAngle =  [];
            mainCtrl.filters.searchPageFrom = '';
            mainCtrl.filters.searchPageTo = '';
            mainCtrl.filters.searchPageCheckFrom = '';
            mainCtrl.filters.searchPageCheckTo = '';
            mainCtrl.filters.searchMinSize = '';
            mainCtrl.filters.searchMaxSize = '';

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
            var params = getSearchParams();
            DigitalDocumentSrvc.search(params, handlePageOfItems);
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
                item.checked = angular.isDefined(mainCtrl.selection[item.identifier]);
                
                // Récupération de la présence de workflow
                item.isCheckStarted = WorkflowHandleSrvc.isCheckStarted(item.docUnit.identifier);
            });
            mainCtrl.pagination.busy = false;
        }

        /**
         * Recherche d'entité
         * @return {[type]} [description]
         */
        function search(sortModel, updatedFilters) {
            if (angular.isDefined(sortModel)) {
                mainCtrl.sortModel = sortModel;
            }

            // si onchange est appelé avant la mise à jour du modèle, on récupère le filtre dans updateFilters
            if (updatedFilters) {
                _.extend(mainCtrl.filters, updatedFilters);
            }

            mainCtrl.pagination.busy = true;
            mainCtrl.pagination.page = 1;
            mainCtrl.selection = {};

            savePageSize();
            saveFilters();
            var params = getSearchParams();

            DigitalDocumentSrvc.search(params, handlePageOfItems);
        }

        /**
         * Construction des paramètres de la recherche en fonction des filtres sélectionnés
         * @return {[type]} [description]
         */
        function getSearchParams() {
            var params = {};
            params["page"] = mainCtrl.pagination.page - 1;
            params["size"] = mainCtrl.pagination.size;
            params["validated"] = mainCtrl.filters.validated;

            if (mainCtrl.filters.statuses) {
                params["status"] = mainCtrl.filters.statuses;
            }
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
            if (mainCtrl.filters.lots) {
                var lotsIds = _.pluck(mainCtrl.filters.lots, "identifier");
                params["lots"] = lotsIds;
            }
            if (mainCtrl.filters.trains) {
                var trainsIds = _.pluck(mainCtrl.filters.trains, "identifier");
                params["trains"] = trainsIds;
            }
            if (mainCtrl.filters.livraisons) {
                var livraisonsIds = _.pluck(mainCtrl.filters.livraisons, "identifier");
                params["deliveries"] = livraisonsIds;
            }
            // Dates de livraison
            if (mainCtrl.filters.dateFrom) {
                params["dateFrom"] = mainCtrl.filters.dateFrom;
            }
            if (mainCtrl.filters.dateTo) {
                params["dateTo"] = mainCtrl.filters.dateTo;
            }
            // Dates limites de controle
            if (mainCtrl.filters.dateLimitFrom) {
                params["dateLimitFrom"] = mainCtrl.filters.dateLimitFrom;
            }
            if (mainCtrl.filters.dateLimitTo) {
                params["dateLimitTo"] = mainCtrl.filters.dateLimitTo;
            }

            // Recherche bandeau haut : A voir sur quoi on doit brancher ? ...
            params["search"] = mainCtrl.searchRequest || "";
            // Recherche textuelle
            params["searchPgcnId"] = mainCtrl.filters.searchPgcnId || "";
            params["searchTitre"] = mainCtrl.filters.searchTitre || "";
            params["searchRadical"] = mainCtrl.filters.searchRadical || "";
            
            if (mainCtrl.filters.searchFormatDocument) {
                var formatIds = _.pluck(mainCtrl.filters.searchFormatDocument, "identifier");
                params["fileFormats"] = formatIds;
            }
            if (mainCtrl.filters.searchMaxAngle) {
                var angleIds = _.pluck(mainCtrl.filters.searchMaxAngle, "identifier");
                params["maxAngles"] = angleIds;
            }
            
            // Recherche pages
            params["searchPageFrom"] = mainCtrl.filters.searchPageFrom || 0;
            params["searchPageTo"] = mainCtrl.filters.searchPageTo || 0;
            params["searchPageCheckFrom"] = mainCtrl.filters.searchPageCheckFrom || 0;
            params["searchPageCheckTo"] = mainCtrl.filters.searchPageCheckTo || 0;
            params["searchMinSize"] = mainCtrl.filters.searchMinSize || 0;
            params["searchMaxSize"] = mainCtrl.filters.searchMaxSize || 0;

            params["sorts"] = mainCtrl.sortModel;

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
                // filtre initial sur docs a controler.
                if (!filters.filters.statuses || !filters.filters.statuses.length) {
                    filters.filters.statuses = ["TO_CHECK","CHECKING"];
                }
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
        function saveFilters() {
            var filters = {};
            filters.filters = mainCtrl.filters;
            filters.sortModel = mainCtrl.sortModel;
            NumahopStorageService.saveFilter(FILTER_STORAGE_SERVICE_KEY, filters);
        }

        function goToCheck(digitalDocument) {
            var delivId = digitalDocument.deliveries.length > 0 ? 
                                                digitalDocument.deliveries[0].deliveryId
                                                : ''; 
            var params = {
                   "id": digitalDocument.identifier,
                   "pdfExtracted": digitalDocument.lot.requiredFormat === 'PDF',
                   "deliveryId": delivId
            };
            
            $location.path('/viewer/viewer').search(params);
        }

        /*****************************
         *  Gestion de la sélection  *
         *****************************/
        function changeItem(item) {
            var key = item.identifier;
            if (angular.isDefined(mainCtrl.selection[key]) && !item.checked) {
                delete mainCtrl.selection[key];
            } else if (item.checked) {
                mainCtrl.selection[key] = item;
            }
        }

        function checkAll() {
            _.each(mainCtrl.pagination.items, function (item) {
                mainCtrl.selection[item.identifier] = item;
                item.checked = true;
            });
        }

        function uncheckAll() {
            mainCtrl.selection = {};
            _.each(mainCtrl.pagination.items, function (item) {
                item.checked = false;
            });
        }

        function getSelectionLength() {
            return _.keys(mainCtrl.selection).length;
        }

        /**
         * Retourne un tableau contenant les éléments sélectionnés au statut "rejeté"
         */
        function getRejectedSelection() {
            var filteredSel = _.filter(mainCtrl.selection, function (sel) {
                return sel.status === "REJECTED" || sel.status === "DELIVERING_ERROR";
            });
            if (filteredSel.length === 0) {
                MessageSrvc.addWarn(gettext("Aucune des unités documentaires sélectionnées n'est rejetée"), {}, false);
                return;
            }
            return filteredSel;
        }
        
        /**
         * Pour chaque doc rejeté, la derniere livraison doit etre traitée, rejetee automatiquemnt ou en erreur.
         * Si non, on bloque...
         */
        function checkStatusDeliveries(filteredSel) {
            
            var notTreated = _.find(filteredSel, function (sel) {
                // on trie pour avoir la derniere livraison en tete...
                var sortedDeliveries = _.sortBy(sel.deliveries, function (deliv) {
                                            return -[new Date(deliv.deliveryDate).getTime()];
                                        });
        
                return !sortedDeliveries[0] 
                        || (sortedDeliveries[0].deliveryStatus !== 'TREATED' 
                            && sortedDeliveries[0].deliveryStatus !== 'AUTOMATICALLY_REJECTED'
                            && sortedDeliveries[0].deliveryStatus !== 'DELIVERING_ERROR');
            });
            return angular.isUndefined(notTreated) 
                    || notTreated.status === 'DELIVERING_ERROR' 
                        || notTreated.status === 'AUTOMATICALLY_REJECTED';
        }

        /**
         * Création d'un lot à partir de la sélection
         */
        function createLot() {
            // on créé un lot à partir des UD rejetées
            var filteredSel = getRejectedSelection();
            if (!filteredSel) {
                return;
            }
            
            // un doc dans une livraison non traitée, on bloque la creation du lot de renum.
            var authorized = checkStatusDeliveries(filteredSel);
            if (! authorized) {
                MessageSrvc.addWarn(gettext("Tous les documents sélectionnés doivent appartenir à une livraison traitée."), {}, false);
                return;
            }
            
            // Contrôle des lots des UD sélectionnées  // #2029 NON, plusieurs lots possibles maintenant
            var lotIds = _.chain(filteredSel)
                .values()
                .filter(function (item) {
                    return item.lot;
                })
                .map(function (item) {
                    return item.lot.identifier;
                })
                .uniq()
                .value();

            checkLots(lotIds)
                .then(function (params) {
                    // Ok: traitement
                    var docs = _.chain(filteredSel)
                        .values()
                        .map(function (item) {
                            return _.pick(item.docUnit, "identifier", "label");
                        })
                        .value();
                    SelectionSrvc.set(SelectionSrvc.keys.CHECK_LIST, docs);

                    params.new = true;
                    params.callback = "/checks/checks";
                    $location.path("/lot/lot").search(params);
                });
        }

        function checkLots(lotIds) {
            return LotSrvc.query({ dto: true, lot: lotIds }).$promise
                .then(function (lots) {
                    var valid = true;

                    // Contrôle des projets
                    var projects = _.chain(lots)
                        .values()
                        .filter(function (item) {
                            return item.project;
                        })
                        .map(function (item) {
                            return item.project.identifier;
                        })
                        .uniq()
                        .value();
                    if (projects.length > 1) {
                        MessageSrvc.addWarn(gettext("Les unités documentaires sélectionnées ne font pas partie du même projet"), {}, false);
                        valid = false;
                    }

                    // Contrôle des formats
                    var formats = _.chain(lots)
                        .values()
                        .filter(function (item) {
                            return item.identifier; // item = Resource
                        })
                        .map(function (item) {
                            return item.requiredFormat;
                        })
                        .uniq()
                        .sortBy()
                        .value();
                    if (formats.length > 1) {
                        MessageSrvc.addWarn(gettext("Les unités documentaires sélectionnées ont des formats différents: {{formats}}"), { formats: formats.join(", ") }, false);
                        valid = false;
                    }

                    // Contrôle des configs de controle
                    var checks = _.chain(lots)
                        .values()
                        .filter(function (item) {
                            return item.activeCheckConfiguration;
                        })
                        .map(function (item) {
                            return item.activeCheckConfiguration.identifier;
                        })
                        .uniq()
                        .value();
                    if (checks.length > 1) {
                        MessageSrvc.addWarn(gettext("Les unités documentaires sélectionnées ont des configurations de contrôle différentes"), {}, false);
                        valid = false;
                    }

                    // Contrôle des configs FTP
                    var ftps = _.chain(lots)
                        .values()
                        .filter(function (item) {
                            return item.activeFTPConfiguration;
                        })
                        .map(function (item) {
                            return item.activeFTPConfiguration.identifier;
                        })
                        .uniq()
                        .value();
                    if (ftps.length > 1) {
                        MessageSrvc.addWarn(gettext("Les unités documentaires sélectionnées ont des configurations FTP différentes"), {}, false);
                        valid = false;
                    }

                    if (valid) {
                        var result = {};
                        if (projects.length) {
                            result.project = projects[0];
                        }
                        if (formats.length) {
                            result.requiredFormat = formats[0];
                        }
                        if (checks.length) {
                            result.activeCheckConfiguration = checks[0];
                        }
                        if (ftps.length) {
                            result.activeFTPConfiguration = ftps[0];
                        }
                        result.requiredColorspace = _.chain(lots)
                            .pluck("requiredColorspace")
                            .find() // 1e valeur définie
                            .value();

                        var requiredResolutions = _.chain(lots)
                            .pluck("requiredResolution")
                            .map(Number)
                            .filter(function (tx) { return tx && _.isNumber(tx); })
                            .value();
                        if (requiredResolutions.length) {
                            result.requiredResolution = _.max(requiredResolutions);
                        }

                        var requiredTauxCompressions = _.chain(lots)
                            .pluck("requiredTauxCompression")
                            .filter()
                            .value();
                        if (requiredTauxCompressions.length) {
                            result.requiredTauxCompression = _.max(requiredTauxCompressions);
                        }
                        return $q.when(result);
                    }
                    else {
                        return $q.reject();
                    }
                });
        }
        
        
        function getProjectIds(rejectedDocs) {
            
            var projectIds = _.chain(rejectedDocs)
                .values()
                .filter(function (item) {
                    return item.project;
                })
                .map(function (item) {
                    return item.project.identifier;
                })
                .uniq()
                .value();  
            
          return projectIds; 
        }
        

        /**
         * Création d'un train à partir de la sélection
         */
        function createTrain() {
            // on ne conserve que les UD rejetées
            var filteredSel = getRejectedSelection();
            if (!filteredSel) {
                return;
            }
            
            // #2029 : plusieurs lots possibles, mais appartenant au mm projet.
            var projects = getProjectIds(filteredSel);
            if (projects.length > 1) {
                MessageSrvc.addWarn(gettext("Les unités documentaires sélectionnées ne font pas partie du même projet"), {}, false);
                return;
            }
            
            // Ok: traitement
            var docUnitIds =  _.chain(filteredSel)
                        .values()
                        .filter(function (item) {
                            return item.docUnit != null && item.lot != null;
                        })
                        .map(function (item) {
                            return item.docUnit.identifier;
                        })
                        .value();
            // On remonte ds les etapes de workflow => ATTENTE RELIVRAISON
            var params_wkf = {
                          resetNumWaiting: true
                      };
            WorkflowSrvc.resetToNumWaiting(params_wkf, docUnitIds);

            var params = {
                new: true,
                fromReject: true
            };
            if (projects.length) {
                params.project = projects[0];
            }
            if (docUnitIds.length) {
                params.docs = docUnitIds;
            }
            $location.path("/train/train").search(params);
        }

        /**
         * Ajout de la sélection à un nouveau lot
         * Une fois le lot créé, on revient ici pour y lier la sélection
         * 
         * @param {any} projectId 
         * @param {any} lotId 
         * @returns 
         */
        function addSelectionToNewLotCallback(projectId, lotId, trainId) {
            var selection = SelectionSrvc.get(SelectionSrvc.keys.CHECK_LIST);

            addDocUnitsToLot(selection, projectId, lotId, trainId)
                .then(function () {
                    SelectionSrvc.clear(SelectionSrvc.keys.CHECK_LIST);  // vidage de la sélection
                    search();   // rafraichissement de la liste
                });
        }

        /**
         * Ajout de la sélection à un projet / lot
         * 
         * @param {any} docUnits 
         * @param {any} projectId 
         * @param {any} lotId 
         * @returns 
         */
        function addDocUnitsToLot(docUnits, projectId, lotId, trainId) {
            if (docUnits.length === 0) {
                MessageSrvc.addWarn(gettext("Aucune unité documentaire n'est sélectionnée"), {}, false);
                return $q.when();
            }
            var params = {
                project: projectId,
                lot: lotId
            };
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
         * Somme le nbre de pages.
         */
        function getSelectionPages() {
            return _.chain(mainCtrl.selection)
            .pluck("reportDetail")
            .map(function (det) {
                if (det && det.nbViewTotal ) {
                    return Number(det.nbViewTotal) || 0;
                }
            })
            .filter(function (i) {
                return !!i;
            })
            .reduce(function (a, b) {
                return a + b;
            }, 0)
            .value();
        }
        
        /**
         * Calcule les dimensions maximales de la sélection
         */
        function getSelectionDimensions() {
            return _.chain(["dim1", "dim2", "dim3"])
                .map(function (dim) {
                    var retValue =  _.chain(mainCtrl.selection).pluck("reportDetail").pluck(dim).max().value() || 0;
                    if (isFinite(retValue)) {
                        return retValue;
                    } else {
                        return 0;
                    }
                })
                .reduce(function (a, b) {
                    return angular.isDefined(a) ? a + " x " + b : b;
                }, undefined)
                .value();
        }
        
        /**
         * Somme des valeurs d'assurance de la sélection
         */
        function getSelectionInsurance() {
            return _.chain(mainCtrl.selection)
                .pluck("reportDetail")
                .map(function (det) {
                    if (det && det.insurance) {
                        return Number(det.insurance.replace(/,/, ".").replace(/[^0-9.]/g, ""));
                    }
                })
                .filter(function (i) {
                    return !!i;
                })
                .reduce(function (a, b) {
                    return a + b;
                }, 0)
                .value();
        }
    }
})();
