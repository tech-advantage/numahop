(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('ViewerCtrl', ViewerCtrl);


    function ViewerCtrl($routeParams, $location, $scope, $q, $http, FileSaver, DeliverySrvc, WorkflowHandleSrvc, $timeout,
        DigitalDocumentSrvc, MessageSrvc, PageCheckSrvc, gettextCatalog, codeSrvc, SampleSrvc, ModalSrvc, CondreportSrvc) {

        $scope.url_viewer = undefined;
        $scope.digitalDocument = undefined;
        $scope.delivery = undefined;

        $scope.metadata = {};
        $scope.options = {};
        $scope.rejected = false;
        $scope.accConstat = true;
        $scope.accMd1 = false;
        $scope.accMd2 = false;
        $scope.accToc1 = true;
        $scope.accToc2 = true;
        $scope.accOcr1 = false;
        $scope.accOcr2 = false;
        
        $scope.sample = undefined;
        $scope.sampling = false;

        $scope.goToPageCheck = goToPageCheck;
        $scope.groupErrorLabels = groupErrorLabels;
        $scope.downloadMaster = downloadMaster;
        $scope.downloadCheckSlip = downloadCheckSlip;
        $scope.downloadToc = downloadToc;
        $scope.changeSelectedDelivery = changeSelectedDelivery;

        $scope.select = {};
        $scope.select.selectedErrors = [];
        $scope.select.selectedErrors2 = [];
        $scope.nbMinErr = 0;
        $scope.nbMajErr = 0;
        $scope.options.errors = PageCheckSrvc.checkErrors;
        $scope.data = {};
        $scope.data.filesWithErrors = [];
        $scope.data.metadataFile = [];
        $scope.data.metadataFile2 = [];  // pour 2eme page bookView
        $scope.pdfExtracted = false;

        // variables from pageCheckCtrl
        $scope.previousDelivs = [];
        $scope.selPreviousDelivery = "";
        $scope.deliveryNotes = "";
        $scope.data.digitizingNotes = "";
        $scope.data.checkNotes = "";
        $scope.data.checkNotes2 = "";
        $scope.data.pagesToControl = [];
        $scope.data.totalPages = 0;
        $scope.detailsConstat = [];

        $scope.loaded = false;
        $scope.imgDoublePaged = true;
        $scope.bookView = false;
        $scope.typeView = undefined;
        $scope.complete = false;
        //
        $scope.ascending = true;
        $scope.isLastPage = false;
        $scope.currentPage = 0;

        init();

        function init() {

            if ($routeParams.sampling) {
                $scope.sampling = $routeParams.sampling;
            } else {
                $scope.sampling = false;
            }
            if ($routeParams.pdfExtracted) {
                $scope.pdfExtracted = $routeParams.pdfExtracted;
            }

            var params = {
                id: $routeParams.id,
                sampling: $scope.sampling
            };

            if ($routeParams.deliveryId) {
                var parms = {
                    id: $routeParams.deliveryId
                };
                DeliverySrvc.getDeliveryForViewer(parms, function (delivery) {
                    $scope.delivery = delivery;
                    $scope.deliveryNotes = $scope.delivery.digitizingNotes;
                });
                $scope.delivLoaded = true;
            }

            // Récupération du nombre de pièces
            DigitalDocumentSrvc.getPiecesNb(params, function (val) {
                $scope.nbPieces = val.nbPieces;
            });

            if ($scope.sampling) {
                // MODE ECHANTILLONNAGE

                // chargement du sample avec ses pages (1->n docs possibles). 
                SampleSrvc.get(params, function (value) {

                    $scope.sample = value;
                    $scope.data.totalPages = $scope.sample.pages.length;
                    $scope.deliveryNotes = $scope.sample.delivery.digitizingNotes;
                    $scope.imgDoublePaged = false;

                    PageCheckSrvc.getErrorsForSample(params, function (errors) {
                        $scope.select.selectedErrors = _.filter($scope.options.errors, function (error) {
                            return _.contains(errors, error.key);
                        });
                    });

                    PageCheckSrvc.getSampleErrors({ id: $scope.sample.identifier }, function (value) {
                        $scope.nbMinErr = value.nbMinorErrors;
                        $scope.nbMajErr = value.nbMajorErrors;
                        MessageSrvc.clearMessages("warning");
                        if (value.minorErrorRateExceeded || value.majorErrorRateExceeded) {
                            if (value.minorErrorRateExceeded) {
                                MessageSrvc.addWarn(gettextCatalog.getString("Taux d'erreur mineures dépassé par rapport à la taille de l'échantillon"), {}, true);
                            }
                            if (value.majorErrorRateExceeded) {
                                MessageSrvc.addWarn(gettextCatalog.getString("Taux d'erreur majeures dépassé par rapport à la taille de l'échantillon"), {}, true);
                            }
                        }
                    });

                    DigitalDocumentSrvc.getMetadataForSample(params, function (metadata) {
                        $scope.metadata = metadata;
                    });
                });


            } else {
                // MODE COMPLET
                DigitalDocumentSrvc.get(params, function (value) {

                    $scope.digitalDocument = value;
                    $scope.data.totalPages = $scope.digitalDocument.nbPages;

                    if (value.status === "REJECTED") {
                        MessageSrvc.addFailure(gettextCatalog.getString("Statut du document : {{status}}", { status: codeSrvc['digitalDocument.' + value.status] }), {}, true);
                        $scope.rejected = true;
                    } else {
                        MessageSrvc.addInfo(gettextCatalog.getString("Statut du document : {{status}}", { status: codeSrvc['digitalDocument.' + value.status] }), {}, true);
                    }

                    // Synthese constat d'etat
                    CondreportSrvc.getSummary({ docUnit: value.docUnit.identifier }, function (summaries) {
                        $scope.detailsConstat = summaries;
                    });

                    // Affichage pour un temps limité à l'ouverture
                    MessageSrvc.initPanel();

                });

                PageCheckSrvc.getDocumentErrors(params, function (value) {
                    MessageSrvc.clearMessages("warning");
                    $scope.nbMinErr = value.nbMinorErrors;
                    $scope.nbMajErr = value.nbMajorErrors;
                    if (value.minorErrorRateExceeded || value.majorErrorRateExceeded) {
                        if (value.minorErrorRateExceeded) {
                            MessageSrvc.addWarn(gettextCatalog.getString("Taux d'erreur mineures dépassé"), {}, true);
                        }
                        if (value.majorErrorRateExceeded) {
                            MessageSrvc.addWarn(gettextCatalog.getString("Taux d'erreur majeures dépassé"), {}, true);
                        }
                    }
                });

                DigitalDocumentSrvc.getMetadataForFiles(params, function (metadata) {
                    $scope.metadata = metadata;
                });

                PageCheckSrvc.getErrors(params, function (errors) {
                    $scope.select.selectedErrors = _.filter($scope.options.errors, function (error) {
                        return _.contains(errors, error.key);
                    });
                });

                // Historique des resultats si relivraison
                var pars = {
                    digitalDocIdentifier: $routeParams.id
                };
                DeliverySrvc.getPreviousCheckSlips(pars, function (val) {
                    $scope.previousDelivs = val;
                    if ($scope.previousDelivs.length === 1) {
                        $scope.selPreviousDelivery = val[0].identifier;
                    }
                });

            }

            /* parametres visionneuse Mirador */
            var typeView = 'ThumbnailsView';
            $scope.currentPage = 1;
            if (angular.isDefined($routeParams.page)) {
                $scope.currentPage = $routeParams.page;
            }
            if (angular.isDefined($routeParams.typeView)) {
                typeView = $routeParams.typeView;
            }

            $scope.url_viewer = "scripts/app/viewer/mirador.html?";
            if ($scope.sampling) {
                // Echantillon
                $scope.url_viewer += 'idSample=' + params.id + '&typeView=' + typeView;
            } else {
                // Complet
                $scope.url_viewer += 'idDocument=' + params.id + '&typeView=' + typeView + '&checking=true';
            }

            // Listener => detecte les changements de page de Mirador.
            document.addEventListener('canvasIdChanged', canvasIdChanged);

            // Listener => detecte les changements de typeView de Mirador.
            document.addEventListener('typeViewChanged', typeViewChanged);

            // Listener => detecte les images forcees a 'non-paged'.
            document.addEventListener('imgViewing', imgViewing);
        }

        /* -----------------------*/
        /* Events from Mirador*/
        /* -----------------------*/
        function canvasIdChanged(e) {
            synchronizePage(e.detail.currentId);
        }

        function typeViewChanged(e) {

            var prevTypeView = $scope.typeView;
            if ($scope.typeView === e.detail.currentTypeView) {
                return;
            }
            $scope.typeView = e.detail.currentTypeView;
            $scope.currentPage = e.detail.canvasId;
            $scope.bookView = 'BookView' === e.detail.currentTypeView;
            if (prevTypeView === 'ThumbnailsView') {
                // entree en controle
                initializeCheck();
            } else if ($scope.typeView === 'ThumbnailsView') {
                // sortie du controle
                exitFromCheck();
                return;
            }
            loadPage($scope.currentPage, true);
        }

        function imgViewing(e) {
            $scope.imgDoublePaged = !e.detail.imgNonPaged;
        }

        // Il faut forcer le destroy des events (merci Seb!!)
        $scope.$on("$destroy", function () {
            document.removeEventListener("canvasIdChanged", canvasIdChanged);
            document.removeEventListener("typeViewChanged", typeViewChanged);
            document.removeEventListener("imgViewing", imgViewing);
        });


        /**
         * Click / btn Controle.
         */
        function goToPageCheck() {
            if ($scope.sampling) {
                // send event pour changement de vue ds le viewer   (Passage en mode ImageView - echantillon)
                self.frames['viewer_iframe'].document.dispatchEvent(new CustomEvent("changeViewMirador", { "detail": { "typeView": "ImageView" } }));

            } else {
                // send event pour changement de vue ds le viewer   (Passage en mode BookView - document)
                self.frames['viewer_iframe'].document.dispatchEvent(new CustomEvent("changeViewMirador", { "detail": { "typeView": "BookView" } }));
            }
        }

        /**
         * Passage en mode controle.
         */
        function initializeCheck() {

            saveErrors();
            if (!$scope.sampling) {
                var digitNotes = $scope.digitalDocument.docUnit.digitizingNotes;
                if (digitNotes !== null && digitNotes.size > 0) {
                    MessageSrvc.addInfo(gettextCatalog.getString("Informations de contrôle: {{infos}}"),
                        { infos: $scope.digitalDocument.docUnit.digitizingNotes }, true);
                }
            } else {
                if ($scope.data.totalPages === 1) {
                    nextPage(1);
                }
            }
            if ($scope.deliveryNotes !== null && $scope.deliveryNotes.size > 0) {
                MessageSrvc.addInfo(gettextCatalog.getString("Notes de livraison: {{notes}}"),
                    { notes: $scope.deliveryNotes }, true);
            }
        }

        /**
         * Sortie du mode controle.
         * (recharge les erreurs globales)
         */
        function exitFromCheck() {

            // recharger erreurs globales
            $scope.select.selectedErrors = [];
            $scope.select.selectedErrors2 = [];

            var params = {
                id: $routeParams.id
            };
            if ($scope.sampling) {
                PageCheckSrvc.getErrorsForSample(params, function (errors) {
                    $scope.select.selectedErrors = _.filter($scope.options.errors, function (error) {
                        return _.contains(errors, error.key);
                    });
                });
            } else {
                PageCheckSrvc.getErrors(params, function (errors) {
                    $scope.select.selectedErrors = _.filter($scope.options.errors, function (error) {
                        return _.contains(errors, error.key);
                    });
                });
            }
            // affichage vue à droite => retour vue initiale
            $scope.loaded = false;
        }

        function groupErrorLabels(error) {
            return error.isMajor ? "Erreurs majeures" : "Erreurs mineures";
        }

        function loadPage(pageNumber, msgView) {
            
            var numPage = parseInt(pageNumber, 10);
            // En mode Book, on se repositionne sur la 1ere image affichée (page de gauche)
            if ($scope.bookView && numPage > 1 && !isLastPage() && $scope.imgDoublePaged) {
                if (numPage % 2 !== 0) {
                    numPage = numPage - 1;
                }
            }
            loadPageMetaData(numPage);

            if ($scope.sampling) {
                /*Echantillon*/
                var page = $scope.sample.pages[numPage - 1];
                if (page) {
                    $scope.data.checkNotes = page.checkNotes;
                    var pageparams = {
                        id: $routeParams.id,
                        pageId: page.identifier
                    };
                    PageCheckSrvc.getErrorsForSampledPage(pageparams, function (errors) {
                        $scope.select.selectedErrors = _.filter($scope.options.errors, function (error) {
                            return _.contains(errors, error.key);
                        });
                    });
                }

            } else {
                /* Document complet */
                var params = {
                              id: $routeParams.id,
                              pageNumber: numPage
                          };
                DigitalDocumentSrvc.getPage(params, function (page) {
                    $scope.data.checkNotes = page.checkNotes;
                    PageCheckSrvc.getErrorsForPage(params, function (errors) {
                        $scope.select.selectedErrors = _.filter($scope.options.errors, function (error) {
                            return _.contains(errors, error.key);
                        });
                    });

                    // mode bookview
                    if ($scope.bookView && !isLastPage() && numPage > 1 && $scope.imgDoublePaged) {
                        var params2 = {
                            id: $routeParams.id,
                            pageNumber: numPage + 1
                        };
                        DigitalDocumentSrvc.getPage(params2, function (page2) {
                            $scope.data.checkNotes2 = page2.checkNotes;
                            PageCheckSrvc.getErrorsForPage(params2, function (errors2) {
                                $scope.select.selectedErrors2 = _.filter($scope.options.errors, function (error) {
                                    return _.contains(errors2, error.key);
                                });
                            });
                        });
                    }

                });

            }
            $scope.loaded = true;
            // Affichage pour un temps limité à l'ouverture
            if (msgView) {
                MessageSrvc.initPanel();
            }
        }

        /**
         * Chargement metadonnees de chaque page.
         */
        function loadPageMetaData(pageNumber) {
            $scope.data.metadataFile = $scope.metadata[pageNumber - 1];
            if ($scope.bookView && $scope.imgDoublePaged && pageNumber > 1) {
                if ($scope.ascending) {
                    $scope.data.metadataFile2 = $scope.metadata[pageNumber];
                } else {
                    $scope.data.metadataFile = $scope.metadata[pageNumber - 1];
                    $scope.data.metadataFile2 = $scope.metadata[pageNumber];
                }
            }
        }

        /*--- NAVIGATION ---*/
        function synchronizePage(canvasId) {
            var numId = parseInt(canvasId, 10);
            if ($scope.currentPage < numId + 1) {
                $scope.ascending = true;
                nextPage(canvasId);
            } else {
                $scope.ascending = false;
                previousPage(canvasId);
            }
        }

        /* Nav. vers l'avant */
        function nextPage(canvasId) {

            var pageToUpdate = $scope.currentPage;
            $scope.currentPage = parseInt(canvasId, 10);
            var params = {};
            var body = {};
            var lastPage = isLastPage();

            // sauvegardes 1er bloc
            if (lastPage || $scope.currentPage !== parseInt(pageToUpdate, 10)) { // pas de maj au 1er passage.
                if ($scope.sampling) {
                    var page = $scope.sample.pages[pageToUpdate - 1];
                    if (page && page.digitalDocument) {
                        params = {
                            id: page.digitalDocument.identifier,
                            pageNumber: page.number,
                            pageOrder: pageToUpdate,
                            deliveryId: $scope.delivery.identifier
                        };
                        body = {
                            failedChecks: _.pluck($scope.select.selectedErrors, "key"),
                            checkNotes: $scope.data.checkNotes
                        };
                    }
                } else {
                    params = {
                        id: $routeParams.id,
                        pageNumber: pageToUpdate,
                        deliveryId: $scope.delivery.identifier
                    };
                    if (!$scope.data.metadataFile) {
                        $scope.data.metadataFile = $scope.metadata[pageToUpdate];
                    }
                    body = {
                        failedChecks: _.pluck($scope.select.selectedErrors, "key"),
                        checkNotes: $scope.data.checkNotes,
                        typeToc: $scope.data.metadataFile.typeTOC,
                        orderToc: $scope.data.metadataFile.orderTOC,
                        titleToc: $scope.data.metadataFile.nameTOC
                    };
                }
                setPageErrors(params, body, $scope.sampling);

                // sauvegardes 2nd bloc
                if (!lastPage && $scope.bookView && $scope.imgDoublePaged && pageToUpdate !== 1) {
                    var params2 = {
                        id: $routeParams.id,
                        pageNumber: pageToUpdate + 1,
                        deliveryId: $scope.delivery.identifier
                    };

                    if (!$scope.data.metadataFile2 && $scope.ascending) {
                        $scope.data.metadataFile2 = $scope.metadata[pageToUpdate + 1];
                    }
                    if ($scope.data.metadataFile2) {
                        var body2 = {
                            failedChecks: _.pluck($scope.select.selectedErrors2, "key"),
                            checkNotes: $scope.data.checkNotes2,
                            typeToc: $scope.data.metadataFile2.typeTOC,
                            orderToc: $scope.data.metadataFile2.orderTOC,
                            titleToc: $scope.data.metadataFile2.nameTOC
                        };
                        setPageErrors(params2, body2, false);
                    }
                }
            }

            if (lastPage) {
                $scope.complete = true;
            }
            loadPage($scope.currentPage, false);
        }
        $scope.nextPage = nextPage;


        function setPageErrors(params, body, sampling) {
            if (sampling) {
                PageCheckSrvc.setErrorsForSampledPage(params, body, function (value) {
                    $scope.nbMinErr = value.nbMinorErrors;
                    $scope.nbMajErr = value.nbMajorErrors;
                    if (value.minorErrorRateExceeded || value.majorErrorRateExceeded) {
                        if (value.minorErrorRateExceeded) {
                            MessageSrvc.addWarn(gettextCatalog.getString("Taux d'erreur mineures dépassé par rapport à la taille de l'échantillon"), {}, true);
                        }
                        if (value.majorErrorRateExceeded) {
                            MessageSrvc.addWarn(gettextCatalog.getString("Taux d'erreur majeures dépassé par rapport à la taille de l'échantillon"), {}, true);
                        }
                    } else {
                        MessageSrvc.clearMessages("warning");
                    }
                });
            } else {
                PageCheckSrvc.setErrorsForPage(params, body, function (value) {
                    $scope.nbMinErr = value.nbMinorErrors;
                    $scope.nbMajErr = value.nbMajorErrors;
                    if (value.minorErrorRateExceeded || value.majorErrorRateExceeded) {
                        if (value.minorErrorRateExceeded) {
                            MessageSrvc.addWarn(gettextCatalog.getString("Taux d'erreur mineures dépassé"), {}, true);
                        }
                        if (value.majorErrorRateExceeded) {
                            MessageSrvc.addWarn(gettextCatalog.getString("Taux d'erreur majeures dépassé"), {}, true);
                        }
                    } else {
                        MessageSrvc.clearMessages("warning");
                    }
                });
            }
            // remonte un event => mirador pour badge checkedOK / KO
            var checkedOk = body.failedChecks.length === 0;
            var page = sampling ? params.pageOrder : params.pageNumber;
            self.frames['viewer_iframe'].document.dispatchEvent(new CustomEvent("changeCheckingStatus", { "detail": { "checkedOK": checkedOk, "page": page } }));
        }


        /* Nav. => arriere */
        function previousPage(canvasId) {

            var pageToUpdate = $scope.currentPage;
            var lastPage = isLastPage();
            $scope.currentPage = parseInt(canvasId, 10);
            var params = {};
            var body = {};
            if ($scope.sampling) {
                var page = $scope.sample.pages[pageToUpdate - 1];
                if (page && page.digitalDocument) {
                    params = {
                        id: page.digitalDocument.identifier,
                        pageNumber: page.number,
                        pageOrder: pageToUpdate,
                        deliveryId: $scope.delivery.identifier
                    };
                    body = {
                        failedChecks: _.pluck($scope.select.selectedErrors, "key"),
                        checkNotes: $scope.data.checkNotes
                    };
                }
            } else {
                params = {
                    id: $routeParams.id,
                    pageNumber: pageToUpdate,
                    deliveryId: $scope.delivery.identifier
                };
                body = {
                    failedChecks: _.pluck($scope.select.selectedErrors, "key"),
                    checkNotes: $scope.data.checkNotes,
                    typeToc: $scope.data.metadataFile.typeTOC,
                    orderToc: $scope.data.metadataFile.orderTOC,
                    titleToc: $scope.data.metadataFile.nameTOC
                };
            }

            setPageErrors(params, body, $scope.sampling);

            if (!lastPage && $scope.bookView && $scope.imgDoublePaged) {
                var params2 = {
                    id: $routeParams.id,
                    pageNumber: pageToUpdate + 1,
                    deliveryId: $scope.delivery.identifier
                };

                if (!$scope.data.metadataFile2 && !$scope.ascending) {
                    $scope.data.metadataFile2 = $scope.metadata[pageToUpdate + 1];
                }
                if ($scope.data.metadataFile2) {
                    var body2 = {
                        failedChecks: _.pluck($scope.select.selectedErrors2, "key"),
                        checkNotes: $scope.data.checkNotes2,
                        typeToc: $scope.data.metadataFile2.typeTOC,
                        orderToc: $scope.data.metadataFile2.orderTOC,
                        titleToc: $scope.data.metadataFile2.nameTOC
                    };
                    setPageErrors(params2, body2, false);
                }
            }

            loadPage($scope.currentPage, false);
        }
        $scope.previousPage = previousPage;


        function isLastPage() {

            if ($scope.data.totalPages === 1) {
                return true;
            }
            var toTest;
            var curPg = parseInt($scope.currentPage);
            if ($scope.bookView && $scope.imgDoublePaged && $scope.ascending) {
                toTest = curPg + 2;
            } else {
                toTest = curPg + 1;
            }
            return toTest > $scope.data.totalPages;
        }
        $scope.isLastPage = isLastPage;


        /**
         * (Pre)Rejet du document selon modele de workflow.
         * 
         */
        function reject(noConfirm) {
            var params = {
                id: $routeParams.id,
                sampling: $scope.sampling
            };
            saveErrors();
            var deferred = $q.defer();

            $timeout(function () {
                var promise = WorkflowHandleSrvc.isRejectDefinitive(params.id);
                promise.then(function (definitive) {
                    deferred.resolve(definitive);
                    if (definitive.done) {
                        
                        if (noConfirm) {
                            DigitalDocumentSrvc.reject(params, {}, function () {
                                $location.path("/checks/checks");
                                $location.search("");
                                MessageSrvc.addSuccess(gettextCatalog.getString("Document numérique rejeté"));
                            });
                        } else {
                            var label1 = " cette opération est définitive.";
                            var confirmLabel = "Êtes-vous sûr de vouloir rejeter le document ["
                                    + $scope.digitalDocument.docUnit.pgcnId + "] " + $scope.digitalDocument.docUnit.label + " ?";
                            
                            ModalSrvc.confirmAcceptReject("Rejet Document", label1, confirmLabel, "Rejeter").then(function () {
                                DigitalDocumentSrvc.reject(params, {}, function () {
                                    $location.path("/checks/checks");
                                    $location.search("");
                                    MessageSrvc.addSuccess(gettextCatalog.getString("Document numérique rejeté"));
                                });
                            }); 
                        }
                        
                    } else {  
                        if (noConfirm) {
                            DigitalDocumentSrvc.reject(params, {}, function () {
                                $location.path("/checks/checks");
                                $location.search("");
                                MessageSrvc.addSuccess(gettextCatalog.getString("Document numérique pré-rejeté"));
                            });
                        } else {
                            var confirmLabel = "Êtes-vous sûr de vouloir pré-rejeter le document ["
                                + $scope.digitalDocument.docUnit.pgcnId + "] " + $scope.digitalDocument.docUnit.label + "?";
                            
                            ModalSrvc.confirmAcceptReject("Pré-rejet Document", undefined, confirmLabel, "Rejeter").then(function () {
                                DigitalDocumentSrvc.reject(params, {}, function () {
                                    $location.path("/checks/checks");
                                    $location.search("");
                                    MessageSrvc.addSuccess(gettextCatalog.getString("Document numérique pré-rejeté"));
                                });
                            });   
                        }
                           
                    }
                }).catch(function (definitive) {
                    deferred.reject(definitive);
                    MessageSrvc.addError(gettextCatalog.getString("Erreur au rejet du document numérique"));
                });
            });
        }
        $scope.reject = reject;


        function accept(noConfirm) {
            var params = {
                id: $routeParams.id,
                sampling: $scope.sampling
            };
            saveErrors();
            
            if (noConfirm) {
                DigitalDocumentSrvc.accept(params, {}, function () {
                    $location.path("/checks/checks");
                    $location.search("");
                    MessageSrvc.addSuccess(gettextCatalog.getString("Document numérique validé"));
                });
                
            } else {
                var label1 = " cette opération est définitive.";
                var confirmLabel = "Êtes-vous sûr de vouloir valider le document ["
                    + $scope.digitalDocument.docUnit.pgcnId + "] " + $scope.digitalDocument.docUnit.label + " ?";
                
                ModalSrvc.confirmAcceptReject("Validation document", label1, confirmLabel, "Valider").then(function () {
                    DigitalDocumentSrvc.accept(params, {}, function () {
                        $location.path("/checks/checks");
                        $location.search("");
                        MessageSrvc.addSuccess(gettextCatalog.getString("Document numérique validé"));
                    });
                });    
            }
            
            
        }
        $scope.accept = accept;

        /* Enregistre les erreurs globales du doc/sample */
        function saveErrors() {
            var params = {};
            var body = {};

            if ($scope.sampling) {
                params = { id: $scope.sample.identifier };
                body = {
                    failedChecks: _.pluck($scope.select.selectedErrors, "key"),
                    checkNotes: $scope.sample.delivery.controlNotes
                };
                PageCheckSrvc.setErrorsForSample(params, body, function (value) {
                    if (value && value.minorErrorRateExceeded) {
                        MessageSrvc.addWarn(gettextCatalog.getString("Taux d'erreurs mineures dépassé par rapport à la taille de l'échantillon"), {}, true);
                    }
                    if (value && value.majorErrorRateExceeded) {
                        MessageSrvc.addWarn(gettextCatalog.getString("Taux d'erreurs majeures dépassé par rapport à la taille de l'échantillon"), {}, true);
                    }
                });

            } else {
                params = { id: $routeParams.id };
                body = {
                    failedChecks: _.pluck($scope.select.selectedErrors, "key"),
                    checkNotes: $scope.digitalDocument.checkNotes
                };
                PageCheckSrvc.setErrors(params, body, function (value) {
                    if (value && value.minorErrorRateExceeded) {
                        MessageSrvc.addWarn(gettextCatalog.getString("Taux d'erreurs mineures dépassé"), {}, true);
                    }
                    if (value && value.majorErrorRateExceeded) {
                        MessageSrvc.addWarn(gettextCatalog.getString("Taux d'erreurs majeures dépassé"), {}, true);
                    }
                });
            }

        }

        /**
         * Retour accueil visionneuse (vue vignettes- sortie du controle)
         */
        function back() {

            // simule une navigation pour enregistrer une eventuelle erreur.
            if (isLastPage()) {
                synchronizePage('' + ($scope.currentPage - 1));
            } else {
                synchronizePage('' + ($scope.currentPage + 1));
            }
            // send event => Mirador pour changement de vue. 
            self.frames['viewer_iframe'].document.dispatchEvent(new CustomEvent("changeViewMirador", { "detail": { "typeView": "ThumbnailsView" } }));
            // affichage vue à droite => retour vue initiale
            $scope.loaded = false;
        }
        $scope.back = back;

        /**
         * Sortie controle (click/Btn "Terminer")
         */
        function done() {

            synchronizePage($scope.currentPage);
            var params = {};
            var promise;
            if ($scope.sampling) {
                params = { id: $scope.sample.identifier };
                promise = PageCheckSrvc.getSampleErrors(params, function (value) {
                    $scope.nbMinErr = value.nbMinorErrors;
                    $scope.nbMajErr = value.nbMajorErrors;
                    if (value.minorErrorRateExceeded) {
                        MessageSrvc.addWarn(gettextCatalog.getString("Taux d'erreurs mineures dépassé par rapport à la taille de l'échantillon"), {}, true);
                    }
                    if (value.majorErrorRateExceeded) {
                        MessageSrvc.addWarn(gettextCatalog.getString("Taux d'erreurs majeures dépassé par rapport à la taille de l'échantillon"), {}, true);
                    }
                }).$promise;
            } else {
                params = { id: $routeParams.id };
                promise = PageCheckSrvc.getDocumentErrors(params, function (value) {
                    $scope.nbMinErr = value.nbMinorErrors;
                    $scope.nbMajErr = value.nbMajorErrors;
                    if (value.majorErrorRateExceeded) {
                        MessageSrvc.addWarn(gettextCatalog.getString("Taux d'erreur majeures dépassé"), {}, true);
                    }
                    if (value.minorErrorRateExceeded) {
                        MessageSrvc.addWarn(gettextCatalog.getString("Taux d'erreur mineures dépassé"), {}, true);
                    }
                }).$promise;
            }

            promise.then(function (value) {
                ModalSrvc.confirmCheckTerminated(value.nbMinorErrors,
                    value.nbMajorErrors,
                    value.minorErrorRateExceeded,
                    value.majorErrorRateExceeded)
                    .then(function () {
                        if (value.majorErrorRateExceeded || value.minorErrorRateExceeded) {
                            reject(true);
                        } else {
                            accept(true);
                        }
                    });
            });


            // affichage vue à droite => retour vue initiale
            $scope.loaded = false;
        }
        $scope.done = done;


        function changeSelectedDelivery(deliveryId) {
            $scope.selPreviousDelivery = deliveryId;
        }

        /**
         *  Chargement du fichier master.
         *  
         * @returns
         */
        function downloadMaster(pageNumber, meta) {

            var url;
            if (angular.isDefined($scope.digitalDocument)) {
                url = 'api/rest/viewer/document/' + $scope.digitalDocument.identifier + '/master/';
                if (!$scope.pdfExtracted || $scope.pdfExtracted === 'false') {
                    url = url + pageNumber;
                }
            } else {
                var page = $scope.sample.pages[pageNumber - 1];
                if (page) {
                    url = 'api/rest/viewer/document/' + page.digitalDocument.identifier + '/master/';
                    if (!$scope.pdfExtracted || $scope.pdfExtracted === 'false') {
                        url = url + page.number;
                    }
                }
            }
            // arraybuffer pour conserver l'encodage original dans le fichier sauvegardé
            $http.get(url, { responseType: 'arraybuffer' })
                .then(function (response) {
                    var blob = new Blob([response.data], { type: response.headers("content-type") });
                    var fileName = $scope.pdfExtracted && $scope.pdfExtracted === 'true' ? 'master.pdf' : meta.fileName;
                    FileSaver.saveAs(blob, fileName);
                });
        }

        /**
         * Téléchargement du bordereau de contrôle
         **/
        function downloadCheckSlip() {
            if ($scope.selPreviousDelivery) {
                var url = 'api/rest/check/pdf/' + $scope.selPreviousDelivery;
                // on met la réponse dans un arraybuffer pour conserver l'encodage original dans le fichier sauvegardé
                $http.get(url, { responseType: 'arraybuffer' })
                    .then(function (response) {
                        var filename = "bordereau.pdf";
                        var blob = new Blob([response.data], { type: response.headers("content-type") });
                        FileSaver.saveAs(blob, filename);
                    });
            }
        }
        
        /**
         * Téléchargement de la table des matières.
         **/
        function downloadToc() {
            if ($routeParams.deliveryId) {
                var url = 'api/rest/viewer/document/' + $scope.digitalDocument.identifier + '/toc/';
                // on met la réponse dans un arraybuffer pour conserver l'encodage original dans le fichier sauvegardé
                $http.get(url, {responseType: 'arraybuffer'} )
                    .then(function (response) {
                        if (response.data.byteLength === 0) {
                            MessageSrvc.addWarn("Aucune table des matières initiale", null, false);
                        } else {
                            var filename = $scope.digitalDocument.docUnit.pgcnId;
                            if(response.headers("content-type") != "application/xml"){
                                filename = filename + ".xlsx"
                            }
                            var blob = new Blob([response.data], { type: response.headers("content-type") });
                            FileSaver.saveAs(blob, filename);
                        }
                    });
            }
        }
    }

})();


