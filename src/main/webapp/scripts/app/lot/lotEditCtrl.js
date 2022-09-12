(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('LotEditCtrl', LotEditCtrl);

    function LotEditCtrl($http, $httpParamSerializer, $location, $q, $routeParams, $scope, $timeout, codeSrvc,
        CONFIGURATION, DeliverySrvc, DocUnitBaseService, DocUnitSrvc, DtoService, FileSaver, gettext, gettextCatalog,
        HistorySrvc, ListTools, LotSrvc, MessageSrvc, ModalSrvc, NumahopEditService, NumaHopInitializationSrvc, Principal,
        ValidationSrvc, ProjectSrvc) {

        $scope.semCodes = codeSrvc;
        $scope.reloadSelects = reloadSelects;
        $scope.loadTypeCompr = loadTypeCompr;
        $scope.configRules = undefined;
        $scope.validation = ValidationSrvc;
        $scope.sel2Docs = DtoService.getDocs();
        $scope.exportCSV = exportCSV;
        $scope.openForm = openForm;
        $scope.delete = deleteLot;
        $scope.saveLot = saveLot;
        $scope.validateLot = validateLot;

        $scope.displayBoolean = DocUnitBaseService.displayBoolean;
        $scope.displayStatus = displayStatus;
        $scope.displayType = displayType;
        $scope.displayFormat = displayFormat;
        $scope.displayColorspace = displayColorspace;
        $scope.getTypeTooltip = getTypeTooltip;
        $scope.downloadSlip = downloadSlip;
        $scope.downloadCheckSlip = downloadCheckSlip;
        $scope.downloadDeliverySlip = downloadDeliverySlip;
        $scope.onChangeOmekaConf= onChangeOmekaConf;
        $scope.confExportFtpChanged = confExportFtpChanged;
        $scope.isDeliveryFolderDisplayed = isDeliveryFolderDisplayed;

        // Définition des listes déroulantes
        $scope.options = {
            boolean: LotSrvc.config.boolean,
            category: LotSrvc.config.category,
            type: LotSrvc.config.type,
            status: LotSrvc.config.status,
            format: LotSrvc.config.fileFormat,
            colorspace: LotSrvc.config.colorspace,
        };


        function displayStatus(status) {
            return $scope.options.status[status] || status;
        }

        function displayType(type) {
            return $scope.options.type[type] || type;
        }

        function displayFormat(format) {
            return $scope.options.format[format] || format;
        }

        function displayColorspace(colorspace) {
            return $scope.options.colorspace[colorspace] || colorspace;
        }

        $scope.binding = { resp: "" };
        $scope.loaded = false;

        $scope.accordions = {
            lot: true,
            delivery: false,
            docUnit: false
        };

        init();


        /****************************************************************/
        /** Initialisation **********************************************/
        /****************************************************************/
        function init() {
            loadProjectSelect().then(loadLot);

            Principal.identity().then(function (id) {
                $scope.currentUser = id;
            });

            // callback sur les listes d'UD
            if ($routeParams.callback) {
                $scope.saveCallback = function (projId, lotId) {
                    var params = {};
                    if (projId) {
                        params["project"] = projId;
                    }
                    if (lotId) {
                        params["lot"] = lotId;
                    }
                    $location.path($routeParams.callback).search(params);
                };
            }
        }

        function loadLot() {
            // Duplication
            if ('duplicate' in $routeParams && angular.isDefined($routeParams.id)) {
                $scope.lot = LotSrvc.duplicate({
                    id: $routeParams.id
                }, function (lot) {
                    afterLoadingLot(lot);
                });
            }
            // Chargement lot
            else if (angular.isDefined($routeParams.id)) {
                $scope.lot = LotSrvc.get({
                    id: $routeParams.id
                }, function (lot) {
                    afterLoadingLot(lot);
                });
                $scope.deliveries = loadDeliveries($routeParams.id);
                $scope.docUnits = loadDocUnits($routeParams.id);
            }
            // Création d'un nouveau lot
            else if (angular.isDefined($routeParams.new)) {
                HistorySrvc.add(gettext("Nouveau lot"));
                $scope.lot = new LotSrvc();
                $scope.lot.active = true;
                $scope.lot.category = "OTHER";
                $scope.lot.type = "PHYSICAL";
                $scope.lot.status = "CREATED";

                // pré-sélection par rapport aux paramètres de l'URL
                if ($routeParams.requiredFormat) {
                    $scope.lot.requiredFormat = $routeParams.requiredFormat;
                    loadTypeCompr($routeParams.requiredFormat);
                }
                if ($routeParams.requiredTauxCompression) {
                    $scope.lot.requiredTauxCompression = $routeParams.requiredTauxCompression;
                }
                if ($routeParams.requiredResolution) {
                    $scope.lot.requiredResolution = $routeParams.requiredResolution;
                }
                if ($routeParams.requiredColorspace) {
                    $scope.lot.requiredColorspace = $routeParams.requiredColorspace;
                }
                if ($routeParams.project) {
                    $scope.lot.project = _.find($scope.sel2Projects, function (pj) {
                        return pj.identifier === $routeParams.project;
                    });
                }
                afterLoadingLot($scope.lot);
                openForm();
            }
        }

        function loadAll(value) {
            onSuccess(value);
            $scope.loaded = true;
        }

        function afterLoadingLot(lot) {
            if (lot.project) {
                reloadSelects(lot.project);

                // pré-sélection
                if ($routeParams.activeCheckConfiguration) {
                    lot.activeCheckConfiguration = _.find($scope.options.check, function (conf) { return conf.identifier === $routeParams.activeCheckConfiguration; });
                }
                if ($routeParams.activeFTPConfiguration) {
                    lot.activeFTPConfiguration = _.find($scope.options.ftp, function (conf) { return conf.identifier === $routeParams.activeFTPConfiguration; });
                }
                if ($routeParams.activeFormatConfiguration) {
                    lot.activeFormatConfiguration = _.find($scope.options.imgFormat, function (conf) { return conf.identifier === $routeParams.activeFormatConfiguration; });
                }
                setSelectedDeleveryFolder();
            }
            else {
                $scope.options.providers = [];
                $scope.options.ftp = [];
                $scope.options.check = [];
                $scope.options.pacs = [];
                $scope.options.collections = [];
                $scope.options.workflowModels = [];
                $scope.options.imgFormat = [];
                $scope.options.languagesOcr = [];
                $scope.options.omekaConfigurations = [];
                $scope.options.omekaCollections = [];
                $scope.options.omekaItems = [];

            }
            loadAll(lot);
            if (lot.activeCheckConfiguration) {
                $scope.configRules = lot.activeCheckConfiguration.automaticCheckRules;
            }
        }

        function loadDeliveries(lotId) {
            return DeliverySrvc.findByProjectIdsLotsIds({ filteredLots: [lotId] });
        }

        function loadDocUnits(lotId) {
            return DocUnitSrvc.query({ lot: lotId });
        }

        function loadOmekaCollections(omekaConf) {
            if (!omekaConf) {
                omekaConf = $scope.lot.omekaConfiguration;
            }
            if (!omekaConf) {
                $scope.options.omekaCollections = [];
                return;
            }
            NumaHopInitializationSrvc.loadOmekaCollections(omekaConf.identifier)
                .then(function (data) {
                    $scope.options.omekaCollections = data;
                });
        }

        function loadOmekaItems(omekaConf) {
            if (!omekaConf) {
                omekaConf = $scope.lot.omekaConfiguration;
            }
            if (!omekaConf) {
                $scope.options.omekaItems = [];
                return;
            }
            NumaHopInitializationSrvc.loadOmekaItems(omekaConf.identifier)
                .then(function (data) {
                    $scope.options.omekaItems = data;
                });
        }

        function reloadSelects(project) {
            if (project) {
                $q.all([NumaHopInitializationSrvc.loadProvidersForLibrary(project.library.identifier),
                NumaHopInitializationSrvc.loadFTPConfigurationForProject(project.identifier),
                NumaHopInitializationSrvc.loadCheckConfigurationForProject(project.identifier),
                NumaHopInitializationSrvc.loadPACS(project.library.identifier, project.identifier),
                NumaHopInitializationSrvc.loadCollections(project.library.identifier, project.identifier),
                NumaHopInitializationSrvc.loadWorkflowModels(project.library.identifier, project.identifier),
                NumaHopInitializationSrvc.loadFormatConfigurationForProject(project.identifier),
                NumaHopInitializationSrvc.loadOmekaConfigurations(project.library.identifier, project.identifier),
                NumaHopInitializationSrvc.loadOcrLanguagesForLibrary(project.library.identifier)
                ]).then(function (data) {
                    $scope.options.providers = data[0];
                    $scope.options.ftp = data[1];
                    $scope.options.check = data[2];
                    $scope.options.pacs = data[3];
                    $scope.options.collections = data[4];
                    $scope.options.workflowModels = data[5];
                    $scope.options.imgFormat = data[6];
                    $scope.options.omekaConfigurations = data[7];
                    $scope.options.languagesOcr = data[8];
                    $scope.options.providers.forEach(function (provider) {
                        provider.fullName = provider.firstname + " " + provider.surname;
                    });
                });
                ProjectSrvc.get({
                    id: project.identifier
                }, function (projectDto) {
                    if(!$scope.lot.provider && projectDto.provider){
                        $scope.lot.provider = projectDto.provider;
                    }
                    if(!$scope.lot.activeFTPConfiguration && projectDto.activeFTPConfiguration){
                        $scope.lot.activeFTPConfiguration = projectDto.activeFTPConfiguration;
                    }
                    if(!$scope.lot.activeCheckConfiguration && projectDto.activeCheckConfiguration){
                        $scope.lot.activeCheckConfiguration = projectDto.activeCheckConfiguration;
                    }
                    if(!$scope.lot.activeFormatConfiguration && projectDto.activeFormatConfiguration){
                        $scope.lot.activeFormatConfiguration = projectDto.activeFormatConfiguration;
                    }
                    if(!$scope.lot.workflowModel && projectDto.workflowModel){
                        $scope.lot.workflowModel = projectDto.workflowModel;
                    }
                    if(!$scope.lot.planClassementPAC && projectDto.planClassementPAC){
                       $scope.lot.planClassementPAC = projectDto.planClassementPAC;
                    }
                    if(!$scope.lot.collectionIA && projectDto.collectionIA){
                       $scope.lot.collectionIA = projectDto.collectionIA;
                    }
                    if(!$scope.lot.omekaConfiguration  && projectDto.omekaConfiguration){
                       $scope.lot.omekaConfiguration = projectDto.omekaConfiguration;
                       $scope.lot.omekaItem = projectDto.omekaItem;
                       $scope.lot.omekaCollection = projectDto.omekaCollection;
                    }
                    loadOmekaCollections();
                    loadOmekaItems();
                });
                loadExportFTPConf(project.library);
            }
            else {
                $scope.options.providers = [];
                $scope.options.ftp = [];
                $scope.options.exportftp = [];
                $scope.options.check = [];
                $scope.options.pacs = [];
                $scope.options.collections = [];
                $scope.options.workflowModels = [];
                $scope.options.imgFormat = [];
                $scope.options.languagesOcr = [];
            }
        }

        function loadExportFTPConf(library) {
            if(!library) {
                $scope.options.exportftp = [];
                return;
            }

            NumaHopInitializationSrvc.loadExportFtpConf(library.identifier)
                .then(function (data) {
                    $scope.options.exportftp = data;
                });
        }

        function loadProjectSelect() {
            var deferred = $q.defer();
            $timeout(function () {
                var promise = NumaHopInitializationSrvc.loadProjects();
                promise.then(function (value) {
                    $scope.sel2Projects = value;
                    deferred.resolve(value);
                }).catch(function (value) {
                    deferred.reject(value);
                });
            });
            return deferred.promise;
        }

        function loadTypeCompr(format) {
            switch (format) {
                case "JP2":
                    $scope.lot.requiredTypeCompression = 'JPEG2000';
                    break;
                case "JPEG":
                case "JPG":
                    $scope.lot.requiredTypeCompression = 'JPEG';
                    break;
                case "PNG":
                    $scope.lot.requiredTypeCompression = 'ZIP';
                    break;
                case "TIFF":
                case "TIF":
                    $scope.lot.requiredTypeCompression = 'None | LZW | CCITT | JPEG';
                    break;
                case "GIF":
                    $scope.lot.requiredTypeCompression = 'LZW';
                    break;
                case "PDF":
                    $scope.lot.requiredTypeCompression = 'PDF';
                    break;
                case "SVG":
                    $scope.lot.requiredTypeCompression = 'Aucun';
                    break;
                default:
                    $scope.lot.requiredTypeCompression = '';
                    break;
            }
        }

        /****************************************************************/
        /** Services ****************************************************/
        /****************************************************************/

        /****************************************************************/
        /** Actions *****************************************************/
        /****************************************************************/
        $scope.duplicate = function () {
            if ($scope.lot) {
                $scope.loaded = false;
                $scope.lot._selected = false;
                var identifier = $scope.lot.identifier;
                $scope.lot = null;
                $location.path("/lot/lot").search({ id: identifier, duplicate: true });
            }
        };
        $scope.cancel = function () {
            if ($scope.saveCallback) {
                $scope.saveCallback();
            }
            else {
                delete $scope.errors;
                $scope.lotForm.$cancel();
            }
        };

        $scope.goToAllOperations = function () {
            $location.path("/lot/all_operations");
        };

        /****************************************************************/
        /** Fonctions ***************************************************/
        /****************************************************************/
        function getTypeTooltip(model) {
            switch (model) {
                case "PHYSICAL":
                    return gettextCatalog.getString("Lot catalogué avant numérisation");
                case "DIGITAL":
                    return gettextCatalog.getString("Lot numérisé avant catalogage");
            }
        }

        /**
         * Sélection d'une regle à partir de son type.
         * Vrai si la regle est active.
         *
         * @param {any} type
         */
        function selectRuleActive(type) {
            if (type) {
                var found = _.find($scope.configRules, function (rule) {
                    return rule.automaticCheckType.type === type;
                });
                return found && found.active;
            }
        }

        // Sauvegarde un lot
        function saveLot(lot) {
            delete $scope.errors;
            $timeout(function () {
                var creation = angular.isUndefined(lot.identifier) || lot.identifier === null;

                lot.$save({},
                    function (value) {
                        // Si un callbacke est défini, on l'appelle
                        if ($scope.saveCallback) {
                            $scope.saveCallback(value.project.identifier, value.identifier);
                        }
                        // Sinon on rafraichit l'affichage
                        else {
                            MessageSrvc.addSuccess(gettext("Le lot {{label}} a été sauvegardé"), { label: $scope.lot.label });

                            // warnings si incohérence prérequis / config contrôles.
                            if (selectRuleActive('FILE_TAUX_COMPR')
                                && (!$scope.lot.requiredTauxCompression || $scope.lot.requiredTauxCompression === '')) {
                                MessageSrvc.addWarn(gettext("Le contrôle du taux de compression lié à la configuration sélectionnée implique de renseigner le taux de compression attendu."), {}, false);
                            }
                            if (selectRuleActive('FILE_RESOLUTION')
                                && (!$scope.lot.requiredResolution || $scope.lot.requiredResolution === '')) {
                                MessageSrvc.addWarn(gettext("Le contrôle de la résolution lié à la configuration sélectionnée implique de renseigner la résolution minimale attendue."), {}, false);
                            }
                            if (selectRuleActive('FILE_COLORSPACE')
                                && (!$scope.lot.requiredColorspace || $scope.lot.requiredColorspace === '')) {
                                MessageSrvc.addWarn(gettext("Le contrôle du profil lié à la configuration sélectionnée implique de renseigner le profil colorimétrique attendu."), {}, false);
                            }
                            onSuccess(value);
                            // si création, on ajoute à la liste, sinon, on essaye de MAJ les infos dans la colonne du milieu
                            if (creation) {
                                $scope.clearSelection();
                                NumahopEditService.addNewEntityToList(value, $scope.newLots, $scope.pagination.items, ["label"]);
                                $location.search({ id: value.identifier }); // suppression des paramètres
                            } else {
                                NumahopEditService.updateMiddleColumn($scope.lot, ["label"],
                                    $scope.pagination.items, $scope.newLots);
                            }
                        }
                    },
                    function (response) {
                        $scope.errors = _.chain(response.data.errors)
                            .groupBy("field")
                            .mapObject(function (list) {
                                return _.pluck(list, "code");
                            })
                            .value();

                        openForm();
                    });
            });
        }

        // Gestion de lot renvoyé par le serveur
        function onSuccess(value) {
            $scope.lot = value;
            HistorySrvc.add(gettextCatalog.getString("Lot {{label}}", $scope.lot));
            displayMessages($scope.lot);
        }

        // Ouverture du formulaire et des sous formulaires
        function openForm() {
            $timeout(function () {
                if (angular.isDefined($scope.lotForm)) {
                    $scope.lotForm.$show();
                }
            });
        }

        function onChangeOmekaConf(omekaConf) {
            loadOmekaCollections(omekaConf);
            loadOmekaItems(omekaConf);
        }

        /**
         * Suppression d'un lot
         *
         * @param {any} lot
         */
        function deleteLot(lot) {
            ModalSrvc.confirmDeletion(gettextCatalog.getString("le lot {{label}}", lot))
                .then(function () {
                    lot.$delete(function () {
                        MessageSrvc.addSuccess(gettext("Le lot {{label}} a été supprimé"), lot);
                        ListTools.findAndRemoveItemFromLists(lot, $scope.pagination.items, $scope.newLots);
                        $location.search({}); // suppression des paramètres
                    });
                });
        }

        // Messages
        function displayMessages(entity) {
            // On commence par vider les messages précédents...
            MessageSrvc.clearMessages();
            // ... puis on affiche les infos de modification ...
            if (angular.isDefined(entity.lastModifiedDate)) {
                var dateModif = new Date(entity.lastModifiedDate);
                MessageSrvc.addInfo(gettext("Dernière modification le {{date}} par {{author}}"),
                    { date: dateModif.toLocaleString(), author: entity.lastModifiedBy }, true);
            }
            // ... puis on affiche les infos de création ...
            if (angular.isDefined(entity.createdDate)) {
                var dateCreated = new Date(entity.createdDate);
                MessageSrvc.addInfo(gettext("Créé le {{date}}"),
                    { date: dateCreated.toLocaleString() }, true);
            }
            // Affichage pour un temps limité à l'ouverture
            MessageSrvc.initPanel();
        }

        // export
        function exportCSV() {
            ModalSrvc.configureCsvExport()
                .then(function (params) {
                    params.lot = $scope.lot.identifier;
                    var url = 'api/rest/export/csv?' + $httpParamSerializer(params);

                    // on met la réponse dans un arraybuffer pour conserver l'encodage original dans le fichier sauvegardé
                    $http.get(url, { responseType: 'arraybuffer' })
                        .then(function (response) {
                            var filename = "lot-" + $scope.lot.label.replace(/\W+/g, "_") + ".csv";
                            var blob = new Blob([response.data], { type: response.headers("content-type") });
                            FileSaver.saveAs(blob, filename);
                        });
                });
        }

        /**
         * Téléchargement du bordereau de livraison
         */
        function downloadSlip(format) {

            var url = 'api/rest/lot/' + format + '/' + $scope.lot.identifier;
            // on met la réponse dans un arraybuffer pour conserver l'encodage original dans le fichier sauvegardé
            $http.get(url, { responseType: 'arraybuffer' })
                .then(function (response) {
                    var filename = "bordereau." + format;
                    var blob = new Blob([response.data], { type: response.headers("content-type") });
                    FileSaver.saveAs(blob, filename);
                });
        }

        /**
         * Téléchargement du bordereau de contrôle
         **/
        function downloadCheckSlip(format) {
            if ($scope.lot) {
                var url = 'api/rest/check/lot_' + format + '/' + $scope.lot.identifier;

                // on met la réponse dans un arraybuffer pour conserver l'encodage original dans le fichier sauvegardé
                $http.get(url, { responseType: 'arraybuffer' })
                    .then(function (response) {
                        var filename = "bordereau." + format;
                        var blob = new Blob([response.data], { type: response.headers("content-type") });
                        FileSaver.saveAs(blob, filename);
                    });
            }
        }

        /**
         * Téléchargement du bordereau de livraison
         **/
        function downloadDeliverySlip(format) {
            if ($scope.lot) {
                var url = 'api/rest/delivery/lot_' + format + '/' + $scope.lot.identifier;

                // on met la réponse dans un arraybuffer pour conserver l'encodage original dans le fichier sauvegardé
                $http.get(url, { responseType: 'arraybuffer' })
                    .then(function (response) {
                        var filename = "bordereau." + format;
                        var blob = new Blob([response.data], { type: response.headers("content-type") });
                        FileSaver.saveAs(blob, filename);
                    });
            }
        }

        function validateLot(lot) {
            ModalSrvc.confirmAction(gettextCatalog.getString("valider le lot suivant :  {{label}}", { label: lot.label }))
                .then(function () {
                    LotSrvc.validate(lot).$promise.then(function () {
                        MessageSrvc.addSuccess(gettextCatalog.getString("Le lot {{label}} a été validé", { label: lot.label }));
                        lot.status = 'ONGOING';
                    });
                });
        }

        function confExportFtpChanged(value) {
            if(value.label != null && value.label != "") {
                $scope.displayDeliveriesFolder = true;
                $scope.options.exportftp.forEach(function (conf) {
                    if(conf.identifier === value.identifier) {
                        $scope.lot.activeExportFTPConfiguration = conf;
                        $scope.lot.activeExportFTPConfiguration.deliveryFolders = conf.deliveryFolders;
                    }
                })
            } else {
                $scope.displayDeliveriesFolder = false;
            }
        }

        function isDeliveryFolderDisplayed() {
            return $scope.displayDeliveriesFolder;
        }

        function setSelectedDeleveryFolder() {
            var exportConfig = $scope.lot.activeExportFTPConfiguration;
            $scope.displayDeliveriesFolder = exportConfig != null && $scope.lot.activeExportFTPDeliveryFolder != null;
        }
    }
})();
