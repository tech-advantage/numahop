(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('DocUnitEditCtrl', DocUnitEditCtrl);

    function DocUnitEditCtrl(
        $http,
        $httpParamSerializer,
        $location,
        $q,
        $routeParams,
        $scope,
        $timeout,
        codeSrvc,
        WebsocketSrvc,
        DocUnitBaseService,
        DocUnitSrvc,
        ErreurSrvc,
        ExportSrvc,
        FileSaver,
        gettext,
        gettextCatalog,
        HistorySrvc,
        ListTools,
        LockSrvc,
        ModalSrvc,
        NumahopEditService,
        NumahopAutoCheckService,
        MessageSrvc,
        NumaHopStatusService,
        NumaHopInitializationSrvc,
        ValidationSrvc,
        WorkflowHandleSrvc,
        ExportHandlerSrvc
    ) {
        $scope.semCodes = codeSrvc;
        $scope.checkFacile = checkFacile;
        $scope.getRevisions = getRevisions;
        $scope.openForm = openForm;
        $scope.onchangeProject = onchangeProject;
        $scope.onchangeLibrary = onchangeLibrary;
        $scope.export = exportDocUnit;
        $scope.saveEntity = saveEntity;
        $scope.validation = ValidationSrvc;
        // Définition des listes déroulantes
        $scope.displayBoolean = DocUnitBaseService.displayBoolean;
        $scope.displayCondreportType = DocUnitBaseService.displayCondreportType;
        $scope.displayRight = DocUnitBaseService.displayRight;
        $scope.displayProgressStatus = DocUnitBaseService.displayProgressStatus;
        $scope.options = DocUnitBaseService.options;
        $scope.extendDeliv = false;
        $scope.iaArchived = false;
        $scope.cinesArchived = false;
        $scope.workflowState = 'Non démarré';
        $scope.facileAuthorized = false;
        $scope.canInactiveDocUnit = false;
        $scope.facileResults = 'Non effectuée';
        $scope.dateFacileResults = undefined;
        $scope.facileErrors = [];
        $scope.getFacileErrors = getFacileErrors;

        // Exports
        $scope.massExport = massExport;
        $scope.exportCSV = exportCSV;
        $scope.canExportToCines = false;
        $scope.canExportToInternetArchive = false;
        $scope.canExportToOmeka = false;
        $scope.canExportToDigitalLibrary = false;
        // Statut du doc num
        $scope.canDigitalDocBeViewed = NumaHopStatusService.isDigitalDocAvailable;
        $scope.downloadCheckSlip = downloadCheckSlip;

        $scope.sel2Projects = [];
        $scope.sel2Lots = [];
        $scope.sel2Libraries = [];
        $scope.sel2Trains = [];

        $scope.finishedStatus = ['VALIDATED', 'REJECTED', 'DELIVERING_ERROR'];

        /**
         * accordions
         */
        $scope.accordions = {
            docunit: true,
            project: true,
            record: {},
            archiv: true,
            distrib: true,
            control: true,
            other: true,
            delivery: true,
        };

        $scope.loaded = false;

        init();

        /****************************************************************/
        /** Initialisation **********************************************/
        /****************************************************************/
        function init() {
            LockSrvc.applyOnScope($scope, 'entityForm', gettext("L'unité documentaire est verrouillée par {{name}} jusqu'à {{date}}"));
            loadEntity();
        }

        function loadEntity() {
            if (angular.isDefined($routeParams.id)) {
                /** Chargement de l'entité **/
                $scope.docUnit = DocUnitSrvc.get(
                    {
                        id: $routeParams.id,
                    },
                    function (entity) {
                        afterLoadingEntity(entity);
                    }
                );
            } else if (angular.isDefined($routeParams.new)) {
                /** Création d'une nouvelle entité **/
                HistorySrvc.add(gettext('Nouvelle unité documentaire'));
                $scope.docUnit = new DocUnitSrvc();
                $scope.docUnit.archivable = true;
                $scope.docUnit.distributable = true;
                $scope.docUnit.foundRefAuthor = false;
                $scope.docUnit.rights = 'TO_CHECK';
                $scope.docUnit.progressStatus = 'NOT_AVAILABLE';
                $scope.docUnit.physicalDocuments = [{}];

                initEntityFromParent($scope.docUnit);
                afterLoadingEntity($scope.docUnit);
                openForm();
            }
        }

        function loadOptions(project, library) {
            var libraryId = library ? [library.identifier] : $scope.docUnit.library ? [$scope.docUnit.library.identifier] : [];
            var projectId = project ? [project.identifier] : $scope.docUnit.project ? [$scope.docUnit.project.identifier] : [];

            if (project && !library) {
                libraryId = project.libraryId;
            }

            if (libraryId) {
                loadOcrLanguages(libraryId);
            }

            var omekaConf;
            if ($scope.docUnit.omekaConfiguration != null) {
                omekaConf = $scope.docUnit.omekaConfiguration.identifier;
            }

            $q.all([
                NumaHopInitializationSrvc.loadLibraries(),
                NumaHopInitializationSrvc.loadProjects(),
                NumaHopInitializationSrvc.loadLots(libraryId, projectId),
                NumaHopInitializationSrvc.loadTrains(libraryId, projectId),
                NumaHopInitializationSrvc.loadPACS(libraryId, projectId),
                NumaHopInitializationSrvc.loadCollections(libraryId, projectId),
                NumaHopInitializationSrvc.loadOmekaCollections(omekaConf, projectId),
                NumaHopInitializationSrvc.loadOmekaItems(omekaConf, projectId),
            ]).then(function (data) {
                $scope.sel2Libraries.length = 0;
                [].push.apply($scope.sel2Libraries, data[0]);

                // Suppression des références inutiles, qui font planter la sauvegarde
                // Projets
                $scope.sel2Projects.length = 0;
                _.each(data[1], function (d) {
                    d.libraryId = d.library.identifier;
                    delete d.library;
                    if (d.status !== 'CLOSED') {
                        $scope.sel2Projects.push(d);
                    }
                });

                // Lots
                $scope.sel2Lots.length = 0;
                $scope.sel2Lots = _.filter(data[2], function (lot) {
                    return (lot.type === 'PHYSICAL' && lot.status === 'CREATED') || (angular.isDefined($scope.docUnit.lot) && $scope.docUnit.lot !== null && $scope.docUnit.lot.identifier === lot.identifier);
                });

                // Trains
                $scope.sel2Trains.length = 0;
                _.each(data[3], function (d) {
                    delete d.project;
                    delete d.physicalDocuments;
                    $scope.sel2Trains.push(d);
                });

                // PAC
                _.each(data[4], function (d) {
                    delete d.library;
                });
                $scope.options.pacs = data[4];

                // IA
                _.each(data[5], function (d) {
                    delete d.library;
                });
                $scope.options.collections = data[5];
                $scope.options.omekaCollections = data[6];
                $scope.options.omekaItems = data[7];
            });
        }

        function loadOcrLanguages(libraryId) {
            var deferred = $q.defer();
            $timeout(function () {
                var promise = NumaHopInitializationSrvc.loadOcrLanguagesForLibrary(libraryId);
                promise
                    .then(function (value) {
                        $scope.options.languagesOcr = [];
                        if (angular.isDefined(value)) {
                            $scope.options.languagesOcr = value;
                        }
                        deferred.resolve(value);
                    })
                    .catch(function (value) {
                        deferred.reject(value);
                    });
            });
            return deferred.promise;
        }

        function onchangeProject(project) {
            $scope.docUnit.lot = null;
            if ($scope.docUnit.physicalDocuments && $scope.docUnit.physicalDocuments.length) {
                delete $scope.docUnit.physicalDocuments[0].train;
            }
            loadOptions(project);
        }

        function onchangeLibrary(library) {
            $scope.docUnit.collectionIA = null;
            $scope.docUnit.planClassementPAC = null;
            $scope.docUnit.omekaCollection = null;
            $scope.docUnit.omekaItem = null;
            $scope.docUnit.activeOcrLanguage = null;
            $scope.docUnit.project = null;
            $scope.docUnit.lot = null;

            if ($scope.docUnit.physicalDocuments && $scope.docUnit.physicalDocuments.length) {
                delete $scope.docUnit.physicalDocuments[0].train;
            } else {
                $scope.docUnit.physicalDocuments = [{}];
            }
            loadOptions(null, library);
        }

        /**
         * Initialise des champs de l'entité à partir de ceux de son parent
         *
         * @param {any} entity
         * @returns
         */
        function initEntityFromParent(entity) {
            if ($routeParams.parent) {
                $scope.parentId = $routeParams.parent;

                DocUnitSrvc.get({ id: $scope.parentId }).$promise.then(function (parent) {
                    entity.parentIdentifier = parent.identifier;
                    entity.parentPgcnId = parent.pgcnId;
                    entity.parentLabel = parent.label;

                    if (parent.library) {
                        entity.library = angular.copy(parent.library);
                    }
                    if (parent.project) {
                        entity.project = angular.copy(parent.project);
                    }
                });
            }
        }

        /**
         * Ordonne les étapes du workflow.
         */
        function sortStates(states) {
            var sortedStates = [];
            sortedStates.push(_.where(states, { key: 'INITIALISATION_DOCUMENT' })[0]);
            sortedStates.push(_.where(states, { key: 'GENERATION_BORDEREAU' })[0]);
            sortedStates.push(_.where(states, { key: 'VALIDATION_CONSTAT_ETAT' })[0]);
            sortedStates.push(_.where(states, { key: 'VALIDATION_BORDEREAU_CONSTAT_ETAT' })[0]);
            sortedStates.push(_.where(states, { key: 'CONSTAT_ETAT_AVANT_NUMERISATION' })[0]);
            sortedStates.push(_.where(states, { key: 'NUMERISATION_EN_ATTENTE' })[0]);
            sortedStates.push(_.where(states, { key: 'CONSTAT_ETAT_APRES_NUMERISATION' })[0]);
            sortedStates.push(_.where(states, { key: 'LIVRAISON_DOCUMENT_EN_COURS' })[0]);
            sortedStates.push(_.where(states, { key: 'RELIVRAISON_DOCUMENT_EN_COURS' })[0]);
            sortedStates.push(_.where(states, { key: 'CONTROLES_AUTOMATIQUES_EN_COURS' })[0]);
            sortedStates.push(_.where(states, { key: 'CONTROLE_QUALITE_EN_COURS' })[0]);
            sortedStates.push(_.where(states, { key: 'PREREJET_DOCUMENT' })[0]);
            sortedStates.push(_.where(states, { key: 'PREVALIDATION_DOCUMENT' })[0]);
            sortedStates.push(_.where(states, { key: 'VALIDATION_DOCUMENT' })[0]);
            sortedStates.push(_.where(states, { key: 'VALIDATION_NOTICES' })[0]);
            sortedStates.push(_.where(states, { key: 'RAPPORT_CONTROLES' })[0]);
            sortedStates.push(_.where(states, { key: 'ARCHIVAGE_DOCUMENT' })[0]);
            sortedStates.push(_.where(states, { key: 'DIFFUSION_DOCUMENT' })[0]);
            sortedStates.push(_.where(states, { key: 'DIFFUSION_DOCUMENT_OMEKA' })[0]);
            sortedStates.push(_.where(states, { key: 'DIFFUSION_DOCUMENT_DIGITAL_LIBRARY' })[0]);
            sortedStates.push(_.where(states, { key: 'DIFFUSION_DOCUMENT_LOCALE' })[0]);
            sortedStates.push(_.where(states, { key: 'CLOTURE_DOCUMENT' })[0]);
            return sortedStates;
        }

        /**
         * Etat workflow : récupère le 1er statut en cours s'il y a lieu.
         */
        function loadWorkflowState(docUnit) {
            if (!docUnit) {
                return;
            }
            if (!docUnit.workflow) {
                if (docUnit.state === 'CANCELED') {
                    $scope.workflowState += ' (Unité documentaire annulée)';
                }
                return;
            }
            var sortedStates = sortStates(docUnit.workflow.states);
            var found = _.find(sortedStates, function (s) {
                return s && s.status === 'PENDING';
            });
            if (!found) {
                found = _.find(sortedStates, function (s) {
                    return s && s.status === 'WAITING';
                });
            }
            if (found) {
                $scope.workflowState = $scope.semCodes['workflow.' + found.key];
            } else {
                $scope.workflowState = 'Terminé';
            }
            if (docUnit.project !== null && docUnit.project.status === 'CANCELED') {
                $scope.workflowState += ' (Projet Annulé)';
                $scope.canInactiveDocUnit = false;
            } else if (docUnit.state === 'CANCELED') {
                $scope.workflowState += ' (Unité documentaire annulée)';
            }

            // On verifie aussi que les controles auto soient passes pour FACILE.
            found = _.find(sortedStates, function (s) {
                return s && s.key === 'CONTROLES_AUTOMATIQUES_EN_COURS';
            });
            $scope.facileAuthorized = found && found.status === 'FINISHED';
            return;
        }

        /**
         *  Inactivation docUnit possible ?
         */
        function loadFlagInactivation(docUnit) {
            if (!docUnit || docUnit.state === 'CANCELED' || (docUnit.project && docUnit.project.status === 'CANCELED')) {
                return;
            }
            $scope.canInactiveDocUnit =
                !docUnit.workflow ||
                !docUnit.digitalDocuments ||
                docUnit.digitalDocuments.length === 0 ||
                !docUnit.digitalDocuments[0].deliveries ||
                docUnit.digitalDocuments[0].deliveries.length === 0 ||
                docUnit.digitalDocuments[0].deliveries[0].status === 'SAVED';
        }

        /*****************************************************************/
        /** Suivi progression validation FACILE **************************/
        /*****************************************************************/
        function loadFacileResults(docUnit) {
            if (
                !docUnit ||
                !docUnit.automaticCheckResults ||
                docUnit.automaticCheckResults.length === 0 ||
                !docUnit.digitalDocuments ||
                docUnit.digitalDocuments.length === 0 ||
                !docUnit.digitalDocuments[0].deliveries ||
                docUnit.digitalDocuments[0].deliveries.length === 0
            ) {
                return;
            }
            if (docUnit.automaticCheckResults.length < docUnit.digitalDocuments[0].deliveries[0].nbPages) {
                NumahopAutoCheckService.autoCheck('facile', $scope.docUnit.identifier);
                $scope.facileResults = 'Vérifications en cours';
                return;
            }

            var resKo = _.filter(docUnit.automaticCheckResults, function (res) {
                if (!$scope.dateFacileResults) {
                    $scope.dateFacileResults = res.createdDate;
                }
                return res.check.type === 'FACILE' && res.result !== 'OK';
            });
            if (!resKo || resKo.length === 0) {
                MessageSrvc.addSuccess(gettext("La validation FACILE s'est terminée sans erreur."), null, true);
                $scope.facileResults = 'Succès';
            } else {
                $scope.facileErrors = _.pluck(resKo, 'message');
                MessageSrvc.addError(gettext("La validation facile s'est terminée en erreur."));
                $scope.facileResults = 'Echec';
            }
        }

        function getFacileErrors() {
            if ($scope.facileErrors) {
                return $scope.facileErrors.join(', ');
            }
        }

        /**
         * Lance validation FACILE.
         */
        function checkFacile() {
            NumahopAutoCheckService.autoCheck('facile', $scope.docUnit.identifier);
            loadFacileResults($scope.docUnit);
        }

        /****************************************************************/
        /** Actions *****************************************************/
        /****************************************************************/
        $scope.delete = function (docUnit) {
            ModalSrvc.confirmDeletion(docUnit.pgcnId).then(function () {
                DocUnitSrvc.delete({ id: docUnit.identifier }, {}).$promise.then(function (value) {
                    // pb de suppression
                    if (value.errors && value.errors.length) {
                        ModalSrvc.modalDeleteDocUnitResults([value], 'xl');
                    } else {
                        MessageSrvc.addSuccess(gettext("L'unité documentaire {{id}} a été supprimée"), { id: value.pgcnId });

                        var removed = ListTools.findAndRemoveItemFromList(docUnit, $scope.pagination.items);
                        if (removed) {
                            $scope.pagination.totalItems--;
                        } else {
                            ListTools.findAndRemoveItemFromList(docUnit, $scope.newEntities);
                        }
                        $scope.backToList();
                    }
                });
            });
        };

        /**
         * Annule définitivement l'unite documentaire.
         */
        $scope.inactive = function (docUnit) {
            ModalSrvc.confirmCancelWithComment(gettextCatalog.getString("l'unité documentaire {{pgcnId}}", docUnit)).then(function (comment) {
                docUnit.cancelingComment = comment;
                docUnit.state = 'CANCELED';

                DocUnitSrvc.inactiveDocUnit(docUnit).$promise.then(function (doc) {
                    MessageSrvc.addSuccess(gettext("L'unité documentaire {{pgcnId}} a été annulée"), doc);
                    $scope.docUnit = doc;
                    $location.search({ active: false, id: doc.identifier });
                });
            });
        };

        $scope.cancel = function () {
            // Fin de l'édition du formulaire: on repasse en mode vue
            if (angular.isUndefined($scope.parentId)) {
                $scope.unlock($scope.docUnit);
                $scope.entityForm.$cancel();
            }
            // Appel depuis la création d'une unité parente: on retourne sur les relations du parent
            else {
                $location.path('/document/all_operations/' + $scope.parentId).search({ tab: 'RELATIONS' });
            }
        };
        $scope.backToList = function () {
            $scope.loaded = false;
            // supprimer tous les paramètres
            $location.search({});
            $location.path('/document/docunit');
        };
        $scope.goToAllOperations = function (tab) {
            if (tab) {
                $location.path('/document/all_operations/' + $scope.docUnit.identifier).search({ tab: tab });
            } else {
                $location.path('/document/all_operations/' + $scope.docUnit.identifier).search('');
            }
        };

        /****************************************************************/
        /** Fonctions ***************************************************/
        /****************************************************************/
        /** Sauvegarde une entité **/
        function saveEntity(entity) {
            delete $scope.errors;

            $timeout(function () {
                var creation = !entity.identifier;

                entity.$save(
                    {},
                    function (value) {
                        MessageSrvc.addSuccess(gettext("L'unité documentaire {{name}} a été sauvegardée"), { name: value.label });
                        onSuccess(value);

                        // si création, on ajoute à la liste, sinon, on essaye de MAJ les infos dans la colonne du milieu
                        if (creation) {
                            $scope.clearSelection();
                            NumahopEditService.addNewEntityToList(value, $scope.newEntities, $scope.pagination.items, ['label']);
                            $location.search({ id: value.identifier });
                        } else {
                            $scope.unlock(entity);
                            NumahopEditService.updateMiddleColumn($scope.docUnit, ['pgcnId', 'label'], $scope.pagination.items, $scope.newEntities);
                        }
                    },
                    function (response) {
                        if (response.data.type !== 'PgcnLockException') {
                            $scope.errors = _.chain(response.data.errors)
                                .groupBy('field')
                                .mapObject(function (list) {
                                    return _.pluck(list, 'code');
                                })
                                .value();

                            openForm();
                        }
                    }
                );
            });
        }
        // Gestion de l'entité renvoyée par le serveur
        function onSuccess(value) {
            $scope.docUnit = value;

            _.each($scope.docUnit.digitalDocuments, function (doc) {
                // Récupération de la présence de workflow (isWorkflowStarted.done est true si un workflow est démarré)
                $scope.docUnit.isWorkflowStarted = WorkflowHandleSrvc.isWorkflowStarted(doc.identifier);
            });

            HistorySrvc.add(gettextCatalog.getString('Unité documentaire {{pgcnId}}', $scope.docUnit));
            customOrderProperty($scope.docUnit);
            displayMessages($scope.docUnit);
        }

        // Ouverture du formulaire et des sous formulaires
        function openForm() {
            $timeout(function () {
                if (angular.isDefined($scope.entityForm)) {
                    $scope.entityForm.$show();
                }
            });
        }
        function displayMessages(entity) {
            // On commence par vider les messages précédents...
            MessageSrvc.clearMessages();

            // ... puis on affiche un warning si aucune notice n'a été créée sur cette UD
            if (entity.identifier && entity.records.length === 0) {
                var msgParam = {
                    startLink: '<a href="#/document/all_operations/' + entity.identifier + '?tab=RECORD">',
                    endLink: '</a>',
                };
                MessageSrvc.addWarn(gettext('Cette unité documentaire ne définit pas de notices.<br/>{{startLink}}&nbsp;Ajouter une notice{{endLink}}'), msgParam, true);
            }

            // ... puis on gère la validation facile
            loadFacileResults($scope.docUnit);

            // ... puis on gère les plateformes de diffusion & archivage
            if (entity.omekaExportStatus) {
                if (entity.omekaExportStatus === 'IN_PROGRESS') {
                    $scope.canExportToOmeka = false;
                    $scope.omekaDistribStatus = 'En cours';
                } else if (entity.omekaExportStatus === 'SENT') {
                    $scope.canExportToOmeka = true;
                    $scope.omekaDistribStatus = 'Envoyé';
                } else if (entity.omekaExportStatus === 'FAILED') {
                    $scope.canExportToOmeka = true;
                    $scope.omekaDistribStatus = 'Envoi en échec';
                }
            } else {
                $scope.canExportToOmeka = true;
                $scope.omekaDistribStatus = 'Non';
            }
            if (entity.digLibExportStatus) {
                if (entity.digLibExportStatus === 'IN_PROGRESS') {
                    $scope.canExportToDigitalLibrary = false;
                } else if (entity.digLibExportStatus === 'SENT') {
                    $scope.canExportToDigitalLibrary = true;
                } else if (entity.digLibExportStatus === 'FAILED') {
                    $scope.canExportToDigitalLibrary = true;
                }
            } else {
                $scope.canExportToDigitalLibrary = true;
            }
            if (angular.isDefined(entity.cinesReports) && entity.cinesReports.length > 0) {
                var reportCinesValue = _.find(entity.cinesReports, function (report) {
                    if (report.certificate !== null) {
                        MessageSrvc.addSuccess(gettext('Archivé au Cines'), {}, true);
                        $scope.cinesArchived = true;
                        return true;
                    }
                    return false;
                });
                if (angular.isUndefined(reportCinesValue)) {
                    MessageSrvc.addInfo(gettext('Dernier export CINES : {{status}} '), { status: $scope.semCodes[entity.cinesReports[0].status] }, true);
                    if (entity.cinesReports[0].status === 'SENDING') {
                        MessageSrvc.addSuccess(gettext('Export Cines en cours'), {}, true);
                    } else {
                        $scope.canExportToCines = true;
                    }
                } else {
                    $scope.canExportToCines = true;
                }
            } else {
                $scope.canExportToCines = true;
            }
            if (angular.isDefined(entity.iaReports) && entity.iaReports.length > 0) {
                var reportIAValue = _.find(entity.iaReports, function (report) {
                    if (report.dateArchived !== null) {
                        MessageSrvc.addSuccess(gettext('Diffusé sur Internet Archive'), {}, true);
                        $scope.iaArchived = true;
                        return true;
                    }
                    return false;
                });
                if (angular.isUndefined(reportIAValue)) {
                    if (entity.iaReports[0].status === 'FAILED') {
                        MessageSrvc.addFailure(gettext('Dernier export Internet Archive : {{status}}'), { status: $scope.semCodes[entity.iaReports[0].status] }, true);
                        $scope.canExportToInternetArchive = true;
                    } else if (entity.iaReports[0].status === 'SENDING') {
                        MessageSrvc.addSuccess(gettext('Export Internet Archive en cours'), {}, true);
                    } else {
                        MessageSrvc.addInfo(gettext('Dernier export Internet Archive : {{status}}'), { status: $scope.semCodes[entity.iaReports[0].status] }, true);
                        $scope.canExportToInternetArchive = true;
                    }
                } else {
                    $scope.canExportToInternetArchive = true;
                }
            } else {
                $scope.canExportToInternetArchive = true;
            }

            // ... puis on affiche les infos de modification ...
            if (angular.isDefined(entity.lastModifiedDate)) {
                var dateModif = new Date(entity.lastModifiedDate);
                MessageSrvc.addInfo(gettext('Dernière modification le {{date}} par {{author}}'), { date: dateModif.toLocaleString(), author: entity.lastModifiedBy }, true);
            }
            if (angular.isDefined(entity.createdDate)) {
                var dateCreated = new Date(entity.createdDate);
                MessageSrvc.addInfo(gettext('Créé le {{date}}'), { date: dateCreated.toLocaleString() }, true);
            }
            // ... et annulation éventuelle
            if (angular.isDefined(entity.project) && entity.project !== null && angular.isDefined(entity.project.status) && entity.project.status !== 'CANCELED' && entity.state === 'CANCELED') {
                var dateCanceling = new Date(entity.lastModifiedDate);
                MessageSrvc.addInfo(gettext('Annulé le {{date}} : {{comment}}'), { date: dateCanceling.toLocaleDateString(), comment: entity.cancelingComment }, true);
            }

            // Affichage pour un temps limité à l'ouverture
            MessageSrvc.initPanel();
        }

        // Initialisation une fois qu'on a reçu toutes les données du serveur
        function loadAll(value) {
            onSuccess(value);
            $scope.loaded = true;
        }

        function afterLoadingEntity(entity) {
            loadWorkflowState(entity);
            loadFlagInactivation(entity);
            loadOptions();
            loadAll(entity);
        }

        /**
         * Réorganise les propriétés
         */
        function customOrderProperty(unit) {
            _.each(unit.records, function (record) {
                // initialisation des accordions du record
                if (!$scope.accordions.record[record.identifier]) {
                    $scope.accordions.record[record.identifier] = {
                        dc: false,
                        dcq: false,
                        custom: false,
                    };
                }
                record.dc = [];
                record.dcq = [];
                record.custom = [];
                _.each(record.properties, function (property) {
                    switch (property.type.superType) {
                        case 'DC':
                            NumahopEditService.insertBasedOnRank(record.dc, property, 'weightedRank');
                            break;
                        case 'DCQ':
                            NumahopEditService.insertBasedOnRank(record.dcq, property, 'weightedRank');
                            break;
                        default:
                            NumahopEditService.insertBasedOnRank(record.custom, property, 'weightedRank');
                    }
                });
            });
        }

        /** Export de l'unité documentaire */
        function exportDocUnit(svc, docUnit) {
            switch (svc) {
                case 'cines':
                    ModalSrvc.exportCines(docUnit.cinesVersion, docUnit.identifier, docUnit.eadExport, docUnit.planClassementPAC, 'xl').then(function (result) {
                        var params = {
                            docUnit: docUnit.identifier,
                            reversion: docUnit.cinesVersion ? result.reversion : true,
                        };
                        if (result.dc) {
                            params.dc = true;
                        } else if (result.ead) {
                            params.ead = true;
                        }
                        ExportSrvc.toCines(params, result.dc || {}, null, null)
                            .$promise.then(function () {
                                MessageSrvc.addSuccess(gettext("L'export Cines est en cours"));
                            })
                            .catch(function (error) {
                                MessageSrvc.addFailure(ErreurSrvc.getMessage(error.data.status));
                                MessageSrvc.addError(gettext(error.data.message));
                            });
                    });
                    break;
                case 'internetarchive':
                    ModalSrvc.createItemInternetArchive(docUnit.identifier, 'xl');
                    break;
                case 'omeka':
                    var params = {
                        docUnit: docUnit.identifier,
                    };
                    ExportSrvc.toOmeka(params, {}, null, null)
                        .$promise.then(function () {
                            MessageSrvc.addSuccess(gettext("L'export Omeka est en cours"));
                        })
                        .catch(function (error) {
                            MessageSrvc.addFailure(ErreurSrvc.getMessage(error.data.status));
                            MessageSrvc.addError(gettext(error.data.message));
                        });
                    break;
                case 'digitallibrary':
                    var params = {
                        docUnit: docUnit.identifier,
                    };
                    ExportSrvc.toDigitalLibrary(params, {}, null, null)
                        .$promise.then(function () {
                            MessageSrvc.addSuccess(gettext("L'export sur bibliothèque numérique est en cours"));
                        })
                        .catch(function (error) {
                            MessageSrvc.addFailure(ErreurSrvc.getMessage(error.data.status));
                            MessageSrvc.addError(gettext(error.data.message));
                        });
                    break;
            }
        }

        function exportCSV() {
            ModalSrvc.configureCsvExport().then(function (params) {
                params.docUnit = $scope.docUnit.identifier;
                var url = 'api/rest/export/csv?' + $httpParamSerializer(params);

                // on met la réponse dans un arraybuffer pour conserver l'encodage original dans le fichier sauvegardé
                $http.get(url, { responseType: 'arraybuffer' }).then(function (response) {
                    var filename = 'docunit-' + $scope.docUnit.pgcnId.replace(/\W+/g, '_') + '.csv';
                    var blob = new Blob([response.data], { type: response.headers('content-type') });
                    FileSaver.saveAs(blob, filename);
                });
            });
        }

        /**
         * Téléchargement d'une archive pour l'export local / SFTP d'une UD.
         */
        function massExport() {
            ModalSrvc.selectExportTypes().then(function (types) {
                ExportHandlerSrvc.massExport($scope.docUnit.identifier, {
                    types: types,
                    pgcnId: $scope.docUnit.pgcnId,
                });
            });
        }

        /**
         * Affichage de la liste des révisions
         */
        function getRevisions() {
            ModalSrvc.getRevisions('docunit', $scope.docUnit.identifier, gettextCatalog.getString('Modifications du lot'), function (rev) {
                if (!rev.lotLabel) {
                    rev.lotLabel = gettextCatalog.getString('Non renseigné');
                }
                return gettextCatalog.getString('<b>{{lotLabel}}</b>, par {{username}} le {{date}}', rev);
            });
        }

        /**
         * Téléchargement du bordereau de contrôle
         **/
        function downloadCheckSlip(deliveryId) {
            if (deliveryId) {
                var url = 'api/rest/check/pdf/' + deliveryId;

                // on met la réponse dans un arraybuffer pour conserver l'encodage original dans le fichier sauvegardé
                $http.get(url, { responseType: 'arraybuffer' }).then(function (response) {
                    var filename = 'bordereau.pdf';
                    var blob = new Blob([response.data], { type: response.headers('content-type') });
                    FileSaver.saveAs(blob, filename);
                });
            }
        }
    }
})();
