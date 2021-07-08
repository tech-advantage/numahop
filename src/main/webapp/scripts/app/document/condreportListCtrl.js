(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('CondreportListCtrl', CondreportListCtrl);

    function CondreportListCtrl($q, $location, $http, $httpParamSerializer, $routeParams, $scope, CondreportSrvc, CondreportDescPropertySrvc,
        CondreportDescValueSrvc, CondreportPropertyConfSrvc, DocUnitBaseService, FileSaver, gettext, gettextCatalog, HistorySrvc,
        LibrarySrvc, MessageSrvc, ModalSrvc, NumahopStorageService, SelectionSrvc, Principal, WorkflowHandleSrvc, WorkflowSrvc,
        NumaHopInitializationSrvc, DtoService) {

        var mainCtrl = this;
        $scope.mainCtrl = mainCtrl;

        mainCtrl.addFilter = addFilter;
        mainCtrl.addSelectionToTrain = addSelectionToTrain;
        //mainCtrl.addSelectionToNewTrain = addSelectionToNewTrain;
        mainCtrl.descPropertyConfig = descPropertyConfig;
        mainCtrl.descValueConfig = descValueConfig;
        mainCtrl.canRemoveProject = DocUnitBaseService.canRemoveProject;
        mainCtrl.canRemoveLot = DocUnitBaseService.canRemoveLot;
        mainCtrl.canRemoveTrain = DocUnitBaseService.canRemoveTrain;
        mainCtrl.checkAll = checkAll;
        mainCtrl.changeItem = changeItem;
        mainCtrl.getPage = getPage;
        mainCtrl.getProperty = getProperty;
        mainCtrl.getSelectionDimensions = getSelectionDimensions;
        mainCtrl.getSelectionInsurance = getSelectionInsurance;
        mainCtrl.getSelectionLength = getSelectionLength;
        mainCtrl.reinitFilters = reinitFilters;
        mainCtrl.removeFilter = removeFilter;
        mainCtrl.removeProject = DocUnitBaseService.removeProject;
        mainCtrl.removeLot = DocUnitBaseService.removeLot;
        mainCtrl.removeTrain = DocUnitBaseService.removeTrain;
        mainCtrl.search = search;
        mainCtrl.uncheckAll = uncheckAll;
        mainCtrl.changePageSize = changePageSize;
        mainCtrl.downloadSlip = downloadSlip;
        mainCtrl.changeValidateOnly = changeValidateOnly;
        mainCtrl.validSelection = validSelection;
        mainCtrl.trainProject = undefined;

        var PAGE_START = 1;
        var FILTER_STORAGE_SERVICE_KEY = "cond_report_list";

        mainCtrl.config = {
            _cache: {}, // mise en cache des listes déroulantes
            properties_desc: {
                text: "label",
                placeholder: gettextCatalog.getString("Propriété"),
                trackby: "identifier",
                'refresh-delay': 300,
                'allow-clear': true
            },
            /**
             * modèle de config pour les listes de valeurs des descriptions
             * refresh est défini dans descValueConfig
             */
            values_desc: {
                text: "label",
                placeholder: gettextCatalog.getString("Valeur"),
                trackby: "identifier",
                multiple: true,
                'refresh-delay': 300,
                'allow-clear': true
            },
            dim_ops: {
                data: [{
                    code: "EQ",
                    label: gettextCatalog.getString("Égales à")
                }, {
                    code: "LTE",
                    label: gettextCatalog.getString("Inférieures à")
                }, {
                    code: "GTE",
                    label: gettextCatalog.getString("Supérieures à")
                }]
            },
            libraries: {
                text: "name",
                placeholder: gettextCatalog.getString("Bibliothèque"),
                trackby: "identifier",
                multiple: true,
                // Chargement avec mise en cache du résultat
                refresh: function () {
                    if (!mainCtrl.config.libraries.data) {
                        mainCtrl.config.libraries.data = LibrarySrvc.query({ dto: true });
                        return mainCtrl.config.libraries.data.$promise;
                    }
                    else {
                        return $q.when(mainCtrl.config.libraries.data);
                    }
                },
                'refresh-delay': 0, // pas de refresh-delay, car on lit les données en cache après le 1er chargement
                'allow-clear': true
            },
            projects: {
                text: "name",
                placeholder: gettextCatalog.getString("Projet"),
                trackby: "identifier",
                multiple: true,
                // Chargement avec mise en cache du résultat
                refresh: function () {
                    if (!mainCtrl.config.projects.data) {
                       mainCtrl.config.projects.data = NumaHopInitializationSrvc.loadProjects();
                       return mainCtrl.config.projects.data.$promise;
                    }
                    else {
                        return $q.when(mainCtrl.config.projects.data);
                    }
                },
                'refresh-delay': 0, // pas de refresh-delay, car on lit les données en cache après le 1er chargement
                'allow-clear': true
            },
            lots: {
                text: "label",
                placeholder: gettextCatalog.getString("Lot"),
                trackby: "identifier",
                multiple: true,
                // Chargement avec mise en cache du résultat
                refresh: function () {
                    if (!mainCtrl.config.lots.data) {
                        mainCtrl.config.lots.data = NumaHopInitializationSrvc.loadLots();
                        return mainCtrl.config.lots.data.$promise;
                    }
                    else {
                        return $q.when(mainCtrl.config.lots.data);
                    }
                },
                'refresh-delay': 0, // pas de refresh-delay, car on lit les données en cache après le 1er chargement
                'allow-clear': true
            }
        };

        mainCtrl.options = {
            libraries: []
        };

        mainCtrl.filters = {
            libraries: [],
            descriptions: []
        };

        mainCtrl.accordions = {
            dimensions: false,
            DESCRIPTION: false,
            NUMBERING: false,
            BINDING: false,
            VIGILANCE: false,
            TYPE: false,
            STATE: false
        };

        mainCtrl.pagination = {
            items: [],
            totalItems: 0,
            busy: false,
            page: PAGE_START
        };
        mainCtrl.sizeOptions = [
            { value: 10, label: "10" },
            { value: 20, label: "20" },
            { value: 50, label: "50" },
            { value: 100, label: "100" }
        ];
        mainCtrl.sortModel = ["docUnit.label"];
        mainCtrl.loaded = false;

        mainCtrl.selection = {};
        mainCtrl.types = CondreportSrvc.types;
        mainCtrl.propTypes = CondreportPropertyConfSrvc.types;

        init();

        function init() {
            HistorySrvc.add(gettextCatalog.getString("Gestion des constats d'état"));
            mainCtrl.isFilteredByIds = !!$routeParams.searchresult;

            // Opérations groupées sur les résultats de recherche
            if (mainCtrl.isFilteredByIds) {
                initFromSearchResults();
            }

            Principal.identity().then(function (usr) {
                mainCtrl.user = usr;
                mainCtrl.isUserPresta = usr.category === "PROVIDER";
            });
            
            loadPageSize();
            loadOptions().then(function () {
                // auto-sélection
                if (mainCtrl.isFilteredByIds) {
                    mainCtrl.checkAll();
                }
                mainCtrl.loaded = true;
            });
        }

        /**
         * Chargement des résultats de recherche sélectionnés
         */
        function initFromSearchResults() {
            var searchSelection = SelectionSrvc.get("SEARCH_RESULT_CONDREPORT");
            // Sélection
            _.each(searchSelection, function (s) {
                mainCtrl.selection[s.identifier] = s;
            });
            // Filtre
            mainCtrl.filteredIds = _.pluck(searchSelection, "identifier");
        }

        /*****************************
         *         Recherche         *
         *****************************/
        function loadOptions() {
            // Chargement des données
            return $q.all([NumaHopInitializationSrvc.loadLibraries(),
            NumaHopInitializationSrvc.loadProjects(),
            NumaHopInitializationSrvc.loadLots(),
            NumaHopInitializationSrvc.loadTrains()])
                .then(function (data) {
                    mainCtrl.options.libraries = data[0];
                    mainCtrl.options.projects = data[1];
                    mainCtrl.options.lots = data[2];
                    loadFilters();

                    return getPage();
                });
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
                // Accordions deplies ou non?
                if (filters.filters.dim1 || filters.filters.dim2 || filters.filters.dim3) {
                    mainCtrl.accordions.dimensions = true;
                }            
                _.each(filters.filters.descriptions, function(desc) {
                   switch (desc.type) {
                       case 'DESCRIPTION':
                           mainCtrl.accordions.DESCRIPTION = true;
                           break;
                       case 'NUMBERING':
                           mainCtrl.accordions.NUMBERING = true;
                           break;
                       case 'BINDING':
                           mainCtrl.accordions.BINDING = true;
                           break;
                       case 'VIGILANCE':
                           mainCtrl.accordions.VIGILANCE = true;
                           break;
                       case 'TYPE':
                           mainCtrl.accordions.TYPE = true;
                           break;
                       case 'STATE':
                           mainCtrl.accordions.STATE = true;
                           break;
                       default: 
                           break;
                   }
                });
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

            mainCtrl.filters = {
                libraries: [],
                descriptions: []
            };
            mainCtrl.sortModel = [];
            search();
        }

        /**
         * Construction des paramètres de la recherche en fonction des filtres sélectionnés
         * @return {[type]} [description]
         */
        function getSearchParams() {
            // Filtrage à partir des résultats de recherche
            if (mainCtrl.isFilteredByIds && mainCtrl.filteredIds) {
                return {
                    filter: mainCtrl.filteredIds
                };
            }
            
            var params = {};

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
            // Dates
            params["from"] = mainCtrl.filters.from;
            params["to"] = mainCtrl.filters.to;

            // Dimensions
            params["dim1"] = mainCtrl.filters.dim1;
            params["dim2"] = mainCtrl.filters.dim2;
            params["dim3"] = mainCtrl.filters.dim3;

            if (params["dim1"] !== null || params["dim2"] !== null || params["dim3"] !== null) {
                params["op"] = mainCtrl.filters.dimop;
            }
            
            params["validateOnly"] = mainCtrl.filters.validateOnly;
            
            // Descriptions
            params["descriptions"] = _.chain(mainCtrl.filters.descriptions)
                .filter(function (desc) {
                    return desc.property && desc.property.identifier && desc.value && desc.value.length > 0;
                })
                .map(function (desc) {
                    return _.map(desc.value, function (val) {
                        return desc.property.identifier + "=" + val.identifier;
                    });
                })
                .flatten()
                .value();

            return params;
        }

        /**
         * Chargement de la prochaine page de résultats
         */
        function getPage() {
            mainCtrl.pagination.busy = true;

            var params = {};
            params["page"] = mainCtrl.pagination.page - 1;
            params["size"] = mainCtrl.pagination.size;
            params["sorts"] = mainCtrl.sortModel;

            var body = getSearchParams();

            return CondreportSrvc.search(params, body).$promise.then(handlePageOfItems);
        }

        /**
         * Recherche de constats d'état
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

            var params = {};
            params["page"] = mainCtrl.pagination.page - 1;
            params["size"] = mainCtrl.pagination.size;
            params["sorts"] = mainCtrl.sortModel;

            var body = getSearchParams();
            CondreportSrvc.search(params, body).$promise.then(handlePageOfItems);
        }

        /**
         * handlePageOfItems - Gestion d'une page de résultats
         *
         * @param  {type} page la page de résultats
         */
        function handlePageOfItems(page) {
            mainCtrl.pagination.totalItems = page.totalElements;
            mainCtrl.pagination.totalPages = page.totalPages;
            mainCtrl.pagination.items = page.content;

            _.each(mainCtrl.pagination.items, function (item) {
                item.checked = angular.isDefined(mainCtrl.selection[item.identifier]);
                item.lastDetail = _.max(item.report.details, function (detail) {
                    return mainCtrl.types[detail.type].pos;
                });
            });
            mainCtrl.pagination.busy = false;
        }
        
        function changeValidateOnly() {
            if (mainCtrl.filters.validateOnly) {
                mainCtrl.search(mainCtrl.sortModel, {validateOnly: mainCtrl.filters.validateOnly});
            } else {
                mainCtrl.search(mainCtrl.sortModel, {validateOnly: false});
            }
            
        }

        /*****************************
         *  Gestion de la sélection  *
         *****************************/
        function changeItem(item) {
            var key = item.docUnit.identifier;
            if (angular.isDefined(mainCtrl.selection[key]) && !item.checked) {
                delete mainCtrl.selection[key];
            } else if (item.checked) {
                mainCtrl.selection[key] = item;
            }
        }

        function checkAll() {
            _.each(mainCtrl.pagination.items, function (item) {
                mainCtrl.selection[item.docUnit.identifier] = item;
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
         * Ajoute la sélection à un train existant.
         * 
         * - ts les constats doivent dependre du mm projet non terminé
         * - le wkf des docs doit etre sur 1 étape < 'En attente de numérisation' 
         **/
        function addSelectionToTrain() {
            
            if (validSelectionForTrain()) {
                ModalSrvc.integrateToTrain(mainCtrl.selection, mainCtrl.trainProject, "sm")
                .then(function () {
                    search();
                });
            }

        }
        
        /**
         * addSelectionToNewTrain - Création d'un nouveau train à partir de tous les exemplaires sélectionnés.
         **/
//        function addSelectionToNewTrain() {
//            
//            if (validSelectionForTrain()) {
//                
//                var docIdentifiers = _.pluck(_.pluck(mainCtrl.selection, "docUnit"), "identifier");
//                DtoService.addDocs(docIdentifiers);
//                $location.path("/train/train").search({ id: null, mode: "edit", new: true, project: mainCtrl.trainProject.identifier});
//            }
//        }
        
        /**
         * Validation avant d'affecter la selection dans un train.
         */
        function validSelectionForTrain() {
            
            // On autorise la selection de constats/UDs appartenant ttes au mm projet en cours. 
            var testValue = _.find(mainCtrl.selection, function (item) { return item.docUnit && item.docUnit.project; });
            var selectionLength = 0;
            var filteredDocs = [];
            if (angular.isDefined(testValue)) {
                filteredDocs = _.filter(mainCtrl.selection, function (item) {
                    selectionLength++;
                    return item.docUnit && item.docUnit.project 
                                && item.docUnit.project.identifier === testValue.docUnit.project.identifier;
                });
                if (selectionLength !== filteredDocs.length) {
                    MessageSrvc.addWarn(gettext("Les unités documentaires sélectionnées ne peuvent pas être traitées car elles appartiennent à des projets différents"), {}, false);
                    return false;
                }
                if ('CLOSED' === testValue.docUnit.project.status) {
                    MessageSrvc.addWarn(gettext("La sélection ne peut pas être traitée car le projet est au statut 'Terminé'"), {}, false);
                    return false;
                }
                // le wkf ne doit pas avoir atteint l'etape 'En attente de numerisation'
                var notAuthorized = _.find(filteredDocs, function (item) {
                    return !item.docUnit.changeTrainAuthorized;
                });
                if (notAuthorized) {
                    MessageSrvc.addWarn(gettext("La sélection ne peut pas être traitée, le workflow est trop avancé"), {}, false);
                    return false;
                }
                mainCtrl.trainProject = testValue.docUnit.project;
                return true;
            }
            return false;
        }
        
        
        /**
         * Validation des constats sélectionnés.
         */
        function validSelection() {
            if (mainCtrl.isUserPresta) {
                return;
            }
            if (mainCtrl.selection.length === 0) {
                MessageSrvc.addWarn(gettext("La sélection est vide"), {}, false);
                return;
            }
            
            var docUnitIds = [];
            var docUnits = _.chain(mainCtrl.selection)
                .values()
                .filter(function (item) {
                    return item.docUnit;
                })
                .map(function (item) {
                    return _.pick(item.docUnit, "identifier", "label");
                })
                .value();
            
            // on ne conserve que les docs avec 1 constat à valider
            var promises = [];
            _.each(docUnits, function (doc) { 
                  promises.push(WorkflowHandleSrvc.isReportToValidate(doc.identifier).then(function(res) {
                      return {doc : doc, done : res.done};
                  }));
            });
            
            $q.all(promises).then(function(promiseResults) {
                _.each(promiseResults, function (res) { 
                    if (res.done) {
                        docUnitIds.push(res.doc.identifier);
                  } else {
                      MessageSrvc.addWarn(gettext("L'unité documentaire {{label}} ne contient pas de constat à valider."), {label: res.doc.label}, false); 
                  }
                });
                return docUnitIds;
            }).then(function (docUnitIds) {
                // validation des etapes de workflow des constats d'etat pour les docs eligibles.
                WorkflowSrvc.massValidateCondReports({massValidate: true}, docUnitIds).$promise
                    .then(function () {
                        MessageSrvc.addSuccess(gettextCatalog.getString("La sélection a été validée"));
                });
            });
           
        } 

        /**
         * Téléchargement du bordereau de livraison
         */
        function downloadSlip(format) {

            if (mainCtrl.selection.length === 0) {
                MessageSrvc.addWarn(gettext("La sélection est vide"), {}, false);
                return;
            }
            var params = {};
            params.reports = _.pluck(_.pluck(mainCtrl.selection, "detail"), "identifier");

            var url = 'api/rest/condreport/' + format + '?' + $httpParamSerializer(params);

            // on met la réponse dans un arraybuffer pour conserver l'encodage original dans le fichier sauvegardé
            $http.get(url, { responseType: 'arraybuffer' })
                .then(function (response) {
                    var filename = "bordereau." + format;
                    var blob = new Blob([response.data], { type: response.headers("content-type") });
                    FileSaver.saveAs(blob, filename);
                });
        }


        /*************************************************
         *  Configuration des contrôles de la recherche  *
         *************************************************/

        function addFilter(filters, type) {
            var newFilter = {};
            if (type) {
                newFilter.type = type;
            }
            filters.push(newFilter);
        }

        function removeFilter(filter, filters) {
            var idx = filters.indexOf(filter);
            if (idx >= 0) {
                filters.splice(idx, 1);
                search();
            }
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

        /**
         * Configuration des listes déroulantes de propriétés de descriptions
         * 
         * @param {any} type
         */
        function descPropertyConfig(type) {
            var config = angular.copy(mainCtrl.config.properties_desc);

            config.refresh = function ($select) {
                // Pas de liste de propriétés à charger
                if (!type) {
                    return $q.when([]);
                }

                // Gestion du cas où la liste est réinitialisée manuellement (search est indéfini)
                if (angular.isUndefined($select.search)) {
                    return $q.when([]);
                }

                // Chargement de la liste en cache
                if (angular.isUndefined(mainCtrl.config._cache["__descProperty__"])) {
                    mainCtrl.config._cache["__descProperty__"] = CondreportDescPropertySrvc.query();
                }
                return mainCtrl.config._cache["__descProperty__"].$promise
                    .then(function (values) {
                        return _.filter(values, function (v) {
                            return v.type === type.code;
                        });
                    });
            };
            return config;
        }

        /**
         * Configuration des listes déroulantes de valeur de descriptions
         * 
         * @param {any} desc 
         */
        function descValueConfig(desc) {
            var config = angular.copy(mainCtrl.config.values_desc);

            config.refresh = function ($select) {
                // Pas de liste de valeurs à charger
                var property = desc.property;
                if (!property || angular.isString(property)) {
                    return $q.when([]);
                }

                // Gestion du cas où la liste est réinitialisée manuellement (search est indéfini)
                if (angular.isUndefined($select.search)) {
                    return $q.when([]);
                }

                // Chargement de la liste en cache
                if (angular.isUndefined(mainCtrl.config._cache[property.identifier])) {
                    mainCtrl.config._cache[property.identifier] = CondreportDescValueSrvc.query({ property: property.identifier });
                }
                return mainCtrl.config._cache[property.identifier];
            };
            return config;
        }

        /**
         * Extrait une propriété d'un détail de constat d'état
         * 
         * @param {any} code 
         * @param {any} properties 
         */
        function getProperty(code, properties, field) {
            var found = _.find(properties, function (p) {
                return p.propertyCode === code;
            });
            if (found) {
                return field ? found[field] : found;
            }
        }

        /**
         * Calcule les dimensions maximales de la sélection
         */
        function getSelectionDimensions() {
            return _.chain(["dim1", "dim2", "dim3"])
                .map(function (dim) {
                    return _.chain(mainCtrl.selection).pluck("detail").pluck(dim).max().value() || 0;
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
                .pluck("detail")
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
