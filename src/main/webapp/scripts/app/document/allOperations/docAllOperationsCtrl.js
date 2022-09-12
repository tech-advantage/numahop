(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('DocAllOperationsCtrl', DocAllOperationsCtrl);

    function DocAllOperationsCtrl($http, $httpParamSerializer, $location, $q, $routeParams, $scope, $timeout, Principal, codeSrvc,
        CondreportSrvc, DeliverySrvc, DocUnitBaseService, DocUnitSrvc, ErreurSrvc, ExportSrvc, FileSaver, gettext, gettextCatalog,
        HistorySrvc, MessageSrvc, ModalSrvc, NumaHopInitializationSrvc, NumahopEditService, USER_ROLES, ValidationSrvc,
        NumahopAutoCheckService, WorkflowHandleSrvc, WorkflowSrvc, ExportHandlerSrvc, VIEW_MODES) {

        $scope.validation = ValidationSrvc;

        var mainCtrl = this;

        mainCtrl.code = codeSrvc;
        mainCtrl.changeTab = changeTab;
        mainCtrl.delete = deleteDoc;
        mainCtrl.deleteCondreport = deleteCondreport;
        mainCtrl.refreshMessages = refreshMessages;
        mainCtrl.viewModes = VIEW_MODES;
        mainCtrl.export = exportDocUnit;
        mainCtrl.exportLocal = exportLocal;
        mainCtrl.exportCSV = exportCSV;
        mainCtrl.checkFacile = checkFacile;
        mainCtrl.canValidateRecord = false;
        mainCtrl.canValidateCondReport = true;
        mainCtrl.validateRecord = validateRecord;
        mainCtrl.validateCondReport = validateCondReport;
        mainCtrl.iaArchived = false;
        mainCtrl.cinesArchived = false;
        mainCtrl.facileResults = 'Non effectuée';
        // Workflow
        mainCtrl.isConstatNotLocked = isConstatNotLocked;

        mainCtrl.tabs = {
            DOCUNIT: {
                code: "DOCUNIT",
                label: gettextCatalog.getString("Unité documentaire"),
                url: 'scripts/app/document/allOperations/docUnit.html'
            },
            RECORD: {
                code: "RECORD",
                label: gettextCatalog.getString("Notice"),
                url: 'scripts/app/document/allOperations/record.html'
            },
            CHECK: {
                code: "CHECK",
                label: gettextCatalog.getString("Document numérique"),
                url: 'scripts/app/document/allOperations/check.html'
            },
            EXPORT: {
                code: "EXPORT",
                label: gettextCatalog.getString("Diffusion"),
                url: 'scripts/app/document/allOperations/export.html',
                role: USER_ROLES.EXPORT_INTERNET_ARCHIVE_HAB0
            },
            ARCHIVE: {
                code: "ARCHIVE",
                label: gettextCatalog.getString("Archivage"),
                url: 'scripts/app/document/allOperations/archive.html',
                role: USER_ROLES.DOC_UNIT_HAB4
            },
            RELATIONS: {
                code: "RELATIONS",
                label: gettextCatalog.getString("Relations"),
                url: 'scripts/app/document/allOperations/relations.html'
            },
            CONDREPORT: {
                code: "CONDREPORT",
                label: gettextCatalog.getString("Constat d'état"),
                url: 'scripts/app/document/allOperations/condreport.html',
                role: USER_ROLES.COND_REPORT_HAB0
            },
            WORKFLOW: {
                code: "WORKFLOW",
                label: gettextCatalog.getString("Workflow"),
                url: 'scripts/app/document/allOperations/workflow.html'
            }
        };
        mainCtrl.taburl = {
            docUnit: null,
            record: null,
            check: null,
            export: null,
            relations: null,
            workflow: null
        };
        // Définition des listes déroulantes
        mainCtrl.options = DocUnitBaseService.options;
        mainCtrl.displayBoolean = DocUnitBaseService.displayBoolean;
        mainCtrl.displayCondreportType = DocUnitBaseService.displayCondreportType;
        mainCtrl.displayRight = DocUnitBaseService.displayRight;
        mainCtrl.displayProgressStatus = DocUnitBaseService.displayProgressStatus;

        mainCtrl.binding = {};
        mainCtrl.binding.override = false;
        mainCtrl.binding.loaded = false;
        mainCtrl.filterWith = "";

        mainCtrl.getHeader = getHeader;

        init();

        // Initialisation du contrôleur
        function init() {

            Principal.identity().then(function (usr) {
                mainCtrl.user = usr;
                mainCtrl.isUserPresta = usr.category === "PROVIDER";
            });

            loadOptions();
        }

        function loadEntity() {
            mainCtrl.docUnitId = $routeParams.identifier;

            // Chargement de l'unité documentaire
            mainCtrl.docUnit = DocUnitSrvc.get({ id: mainCtrl.docUnitId });
            mainCtrl.delivery = DeliverySrvc.get({ docUnit: mainCtrl.docUnitId, latest: true });

            mainCtrl.docUnit.$promise
                .then(function (entity) {
                    onSuccess(entity);
                    var currentTab = $routeParams.tab;

                    if (angular.isUndefined(currentTab)) {
                        // Onglet unité doc par défaut
                        setTab(mainCtrl.tabs.DOCUNIT);
                    } else {
                        setTab(mainCtrl.tabs[currentTab]);
                    }
                });
        }

        function loadOptions() {
            $q.all([NumaHopInitializationSrvc.loadLibraries(),
            NumaHopInitializationSrvc.loadProjects()])
                .then(function (data) {
                    mainCtrl.sel2Libraries = data[0];
                    mainCtrl.sel2Projects = data[1];

                    // Add default case
                    mainCtrl.sel2Projects.unshift({ name: "" });
                    loadEntity();
                });
        }

        /** Charge l'onglet tab */
        function setTab(tab) {
            mainCtrl.currentTab = tab;
        }

        /** Changement d'onglet + rafraichissement des données */
        function changeTab(tab) {
            if (mainCtrl.currentTab !== tab) {
                setTab(tab);
            }
        }

        function onSuccess(entity) {
            mainCtrl.binding.loaded = true;
            HistorySrvc.add(gettextCatalog.getString("{{pgcnId}} {{label}}", entity));
            canRecordBeValidated(entity);
            canCondReportBeValidated(entity);
            displayMessages(entity);
            mainCtrl.expectedProviderLogin = entity.lot ? entity.lot.providerLogin : '';
        }

        function checkFacile() {
            //mainCtrl.autoCheck('facile', mainCtrl.docUnit.identifier);
            NumahopAutoCheckService.autoCheck('facile', mainCtrl.docUnit.identifier);
            loadFacileResults( mainCtrl.docUnit)

        }

        /**
         * Affichage du resultat de la validation FACILE.
         */
        function loadFacileResults(docUnit) {
            if (!docUnit || !docUnit.automaticCheckResults || docUnit.automaticCheckResults.length === 0
                || !docUnit.digitalDocuments || docUnit.digitalDocuments.length === 0
                || !docUnit.digitalDocuments[0].deliveries || docUnit.digitalDocuments[0].deliveries.length === 0) {
                return;
            }
            if (docUnit.automaticCheckResults.length < docUnit.digitalDocuments[0].deliveries[0].nbPages) {
                NumahopAutoCheckService.autoCheck('facile', docUnit.identifier);
                mainCtrl.facileResults = "Vérifications en cours";
                return;
            }
            mainCtrl.dateFacileResults = undefined;
            var resKo = _.find(docUnit.automaticCheckResults, function (res) {
                if (!mainCtrl.dateFacileResults) {
                    mainCtrl.dateFacileResults = res.createdDate;
                }
                return res.check.type === 'FACILE' && res.result !== 'OK';
            });
            if (!resKo) {
                MessageSrvc.addSuccess(gettext("La validation FACILE s'est terminée sans erreur."), {}, true);
                mainCtrl.facileResults = "Succès";
            } else {
                MessageSrvc.addError(gettext("La validation facile s'est terminée en erreur."));
                mainCtrl.facileResults = "Echec";
            }
        }

        function displayMessages(entity) {
            MessageSrvc.clearMessages();

            //  gère la validation facile
            loadFacileResults(entity);

            // Affichage pour un temps limité à l'ouverture
            // on gère les plateformes de diffusion & archivage
            if (entity.omekaExportStatus) {
                if (entity.omekaExportStatus === 'IN_PROGRESS') {
                    mainCtrl.canExportToOmeka = false;
                    mainCtrl.omekaDistribStatus = 'En cours';
                } else if (entity.omekaExportStatus === 'SENT') {
                    mainCtrl.canExportToOmeka = true;
                    mainCtrl.omekaDistribStatus = 'Envoyé';
                } else if (entity.omekaExportStatus === 'FAILED') {
                    mainCtrl.canExportToOmeka = true;
                    mainCtrl.omekaDistribStatus = 'Envoi en échec';
                }
            } else {
                mainCtrl.canExportToOmeka = true;
                mainCtrl.omekaDistribStatus = 'Non';
            }
            if (entity.digLibExportStatus) {
                if (entity.digLibExportStatus === 'IN_PROGRESS') {
                    mainCtrl.canExportToDigitalLibrary = false;
                } else if (entity.digLibExportStatus === 'SENT') {
                    mainCtrl.canExportToDigitalLibrary = true;
                } else if (entity.digLibExportStatus === 'FAILED') {
                    mainCtrl.canExportToDigitalLibrary = true;
                }
            } else {
                mainCtrl.canExportToDigitalLibrary = true;
            }
            if (angular.isDefined(entity.cinesReports) && entity.cinesReports.length > 0) {
                var reportCinesValue = _.find(entity.cinesReports, function (report) {
                    if (report.certificate !== null) {
                        MessageSrvc.addSuccess(gettext("Archivé au Cines"), {}, true);
                        mainCtrl.cinesArchived = true;
                        return true;
                    }
                    return false;
                });
                if (angular.isUndefined(reportCinesValue)) {
                    MessageSrvc.addInfo(gettext("Dernier export CINES : {{status}} "), { status: mainCtrl.code[entity.cinesReports[0].status] }, true);
                    if (entity.cinesReports[0].status === 'SENDING') {
                        MessageSrvc.addSuccess(gettext("Export Cines en cours"), {}, true);
                    } else {
                        mainCtrl.canExportToCines = true;
                    }
                } else {
                    mainCtrl.canExportToCines = true;
                }
            } else {
                mainCtrl.canExportToCines = true;
            }
            if (angular.isDefined(entity.iaReports) && entity.iaReports.length > 0) {
                var reportIAValue = _.find(entity.iaReports, function (report) {
                    if (report.dateArchived !== null) {
                        MessageSrvc.addSuccess(gettext("Diffusé sur Internet Archive"), {}, true);
                        mainCtrl.iaArchived = true;
                        return true;
                    }
                    return false;
                });
                if (angular.isUndefined(reportIAValue)) {
                    if (entity.iaReports[0].status === 'FAILED') {
                        MessageSrvc.addFailure(gettext("Dernier export Internet Archive : {{status}}"), { status: mainCtrl.code[entity.iaReports[0].status] }, true);
                        mainCtrl.canExportToInternetArchive = true;
                    } else if (entity.iaReports[0].status === 'SENDING') {
                        MessageSrvc.addSuccess(gettext("Export Internet Archive en cours"), {}, true);
                    } else {
                        MessageSrvc.addInfo(gettext("Dernier export Internet Archive : {{status}}"), { status: mainCtrl.code[entity.iaReports[0].status] }, true);
                        mainCtrl.canExportToInternetArchive = true;
                    }
                } else {
                    mainCtrl.canExportToInternetArchive = true;
                }
            } else {
                mainCtrl.canExportToInternetArchive = true;
            }

            MessageSrvc.initPanel();
        }

        function refreshMessages() {
            displayMessages();
        }

        function getHeader() {
            if (mainCtrl.docUnit) {
                var header = mainCtrl.docUnit.label;

                if (mainCtrl.docUnit.physicalDocuments
                    && mainCtrl.docUnit.physicalDocuments[0]
                    && mainCtrl.docUnit.physicalDocuments[0].train) {
                    header = mainCtrl.docUnit.physicalDocuments[0].train.label + " > " + header;
                }



                return header;
            }
        }

        /**
         * Suppression de l'UD
         */
        function deleteDoc(docUnit) {
            ModalSrvc.confirmDeletion(docUnit.pgcnId)
                .then(function () {
                    DocUnitSrvc.delete({ id: docUnit.identifier }, {}).$promise
                        .then(function (value) {
                            // pb de suppression
                            if (value.errors && value.errors.length) {
                                ModalSrvc.modalDeleteDocUnitResults([value], 'xl');
                            }
                            else {
                                MessageSrvc.addSuccess(gettext("L'unité documentaire {{id}} a été supprimée"), { id: value.pgcnId });
                                $location.path("/document/docunit").search({});
                            }
                        });
                });
        }

        /** Export de l'unité documentaire */
        function exportDocUnit(svc, docUnit) {
            switch (svc) {
                case "cines":
                    ModalSrvc.exportCines(docUnit.cinesVersion, docUnit.identifier, docUnit.eadExport, docUnit.planClassementPAC, "xl")
                        .then(function (result) {
                            var params = {
                                docUnit: docUnit.identifier,
                                reversion: docUnit.cinesVersion ? result.reversion : true
                            };
                            if (result.dc) {
                                params.dc = true;
                            }
                            else if (result.ead) {
                                params.ead = true;
                            }
                            ExportSrvc.toCines(params, result.dc || {}).$promise
                                .then(function () {
                                    MessageSrvc.addSuccess(gettext("L'export est en cours"));
                                })
                                .catch(function (error) {
                                    MessageSrvc.addFailure(ErreurSrvc.getMessage(error.data.status));
                                    MessageSrvc.addError(gettext(error.data.message));
                                });
                        });
                    break;
                case "internetarchive":
                    ModalSrvc.createItemInternetArchive(docUnit.identifier, 'xl');
                    break;
                case "omeka":
                    var params = {
                                  docUnit: docUnit.identifier
                              };
                    ExportSrvc.toOmeka(params, {}, null, null).$promise
                        .then(function () {
                            MessageSrvc.addSuccess(gettext("L'export Omeka est en cours"));
                    })
                    .catch(function (error) {
                        MessageSrvc.addFailure(ErreurSrvc.getMessage(error.data.status));
                        MessageSrvc.addError(gettext(error.data.message));
                    });
                    break;
                case "digitallibrary":
                    var params = {docUnit: docUnit.identifier};
                    ExportSrvc.toDigitalLibrary(params, {}, null, null).$promise
                        .then(function () {
                            MessageSrvc.addSuccess(gettext("L'export vers la bibliothèque numérique est en cours"));
                    })
                    .catch(function (error) {
                        MessageSrvc.addFailure(ErreurSrvc.getMessage(error.data.status));
                        MessageSrvc.addError(gettext(error.data.message));
                    });
                    break;
            }
        }

        /**
         * Téléchargement d'une archive pour l'export local / SFTP d'une UD.
         */
        function exportLocal() {
            ModalSrvc.selectExportTypes()
                .then(function (types) {
                    ExportHandlerSrvc.massExport(mainCtrl.docUnit.identifier,
                        {
                            types: types,
                            pgcnId: mainCtrl.docUnit.pgcnId
                        }
                    );
                });
        }

        function exportCSV() {
            ModalSrvc.configureCsvExport()
                .then(function (params) {
                    params.docUnit = mainCtrl.docUnit.identifier;
                    var url = 'api/rest/export/csv?' + $httpParamSerializer(params);

                    // on met la réponse dans un arraybuffer pour conserver l'encodage original dans le fichier sauvegardé
                    $http.get(url, { responseType: 'arraybuffer' })
                        .then(function (response) {
                            var filename = "docunit-" + mainCtrl.docUnit.pgcnId.replace(/\W+/g, "_") + ".csv";
                            var blob = new Blob([response.data], { type: response.headers("content-type") });
                            FileSaver.saveAs(blob, filename);
                        });
                });
        }

        /**
         * Gestion du workflow : validation de notice
         */
        function canRecordBeValidated(entity) {
            if (angular.isDefined(entity)) {
                mainCtrl.canValidateRecord = WorkflowHandleSrvc.canRecordBeValidated(entity.workflow);
            }
        }
        /**
         * Gestion du workflow : validation de constat
         */
        function canCondReportBeValidated(entity) {
            if (angular.isDefined(entity)) {
                mainCtrl.canValidateCondReport = WorkflowHandleSrvc.canCondReportBeValidated(entity.workflow);
            }
        }

        /**
         * Validation workflow (notice)
         */
        function validateRecord() {
            if (angular.isDefined(mainCtrl.docUnit)) {
                WorkflowSrvc.process({ docUnitId: mainCtrl.docUnit.identifier, key: 'VALIDATION_NOTICES' }).$promise
                    .then(function () {
                        MessageSrvc.addSuccess(gettextCatalog.getString("L'étape {{name}} a été validée"), { name: mainCtrl.code['workflow.VALIDATION_NOTICES'] });
                        mainCtrl.canValidateRecord = false;
                    });
            }
        }

        /**
         * Validation workflow (constat)
         */
        function validateCondReport(stateKey) {
            if (angular.isDefined(mainCtrl.docUnit)) {
                WorkflowSrvc.process({ docUnitId: mainCtrl.docUnit.identifier, key: stateKey }).$promise
                    .then(function () {
                        MessageSrvc.addSuccess(gettextCatalog.getString("L'étape {{name}} a été validée"), { name: mainCtrl.code['workflow.'+stateKey] });
                        mainCtrl.canValidateCondReport = false;
                    });
            }
        }

        /**
         * Suppression d'un constat d'état
         *
         * @param {any} reportId
         */
        function deleteCondreport(reportId) {
            ModalSrvc.confirmDeletion(gettextCatalog.getString("le constat d'état de l'unité documentaire {{pgcnId}} {{label}}", mainCtrl.docUnit))
                .then(function () {
                    CondreportSrvc.delete({ id: reportId }, {}).$promise
                        .then(function () {
                            MessageSrvc.addSuccess(gettext("le constat d'état de l'unité documentaire {{pgcnId}} {{label}}"), mainCtrl.docUnit);
                            // Rechargement de l'onglet
                            setTab();
                            $timeout(function () {
                                setTab(mainCtrl.tabs.CONDREPORT);
                            });
                        });
                });
        }

        function isConstatNotLocked() {
            return WorkflowHandleSrvc.canConstatBeDeleted(mainCtrl.docUnit.workflow);
        }
    }
})();
