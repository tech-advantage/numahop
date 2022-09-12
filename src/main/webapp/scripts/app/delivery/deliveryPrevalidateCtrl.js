(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('DeliveryPrevalidateCtrl', DeliveryPrevalidateCtrl);

    function DeliveryPrevalidateCtrl($routeParams, $scope, DeliverySrvc, codeSrvc,
        gettextCatalog, MessageSrvc, ModalSrvc, ValidationSrvc, ErreurSrvc) {

        $scope.semCodes = codeSrvc;
        $scope.validation = ValidationSrvc;
        $scope.authorizedDelivery = true;
        $scope.availableSpaceOnDisk = true;
        $scope.prefixToExclude = [];
        $scope.getError = ErreurSrvc.getMessage;

        // Gestion des vues
        $scope.viewModes = {
            VIEW: "view",     // Visualisation, Édition rapide
            EDIT: "edit"
        };   // Création, Modification
        $scope.viewMode = $routeParams.mode || $scope.viewModes.VIEW;

        $scope.binding = { resp: "" };
        $scope.loaded = false;
        $scope.save = save;
        $scope.cancel = cancel;
        $scope.getCommentForStatus = getCommentForStatus;
        $scope.emptyDocs = [];

        $scope.accordions = {
            digitalDoc: true,
            lockedDoc: false,
            metadata: true
        };

        $scope.fileType = {
            "NO_ROLE": gettextCatalog.getString(''),
            "METS": gettextCatalog.getString('Fichier METS'),
            "EXCEL": gettextCatalog.getString('Table des matières Excel'),
            "PDF_MULTI": gettextCatalog.getString('Fichier PDF/A (Muticouches)'),
            "OTHER": gettextCatalog.getString('Autre')
        };

        // toggle switch label ON/OFF
        $scope.onLabelActiv = gettextCatalog.getString("Livrer");
        $scope.offLabelActiv = gettextCatalog.getString("Exclure");

        $scope.docUnits = [];

        init();


        /** Initialisation */
        function init() {
            loadPreDelivery();
        }

        function save() {
            if ($scope.authorizedDelivery && $scope.availableSpaceOnDisk) {
                if ($scope.predelivery.lockedDigitalDocuments.length > 0) {
                    ModalSrvc.confirmAction(gettextCatalog.getString("effectuer la livraison incomplète")).then(function () {
                        deliver();
                    });
                } else {
                    deliver();
                }
            }
        }

        /**
         * Exclure/livrer les docs sans master.
         */
        $scope.updateIncludeDoc = function (doc) {
            if (doc.includeDoc) {
                $scope.prefixToExclude = _.filter($scope.prefixToExclude, function (item) {
                    return item !== doc.digitalId;
                });

            } else {
                $scope.prefixToExclude.push(doc.digitalId);
            }
        };

        function deliver() {
            MessageSrvc.addWarn(gettextCatalog.getString("Initialisation de la livraison..."));
            var lockedDocs = _.pluck($scope.predelivery.lockedDigitalDocuments, "identifier");
            var metadatas = loadMetadataFilesByDoc();
            var params = {
                id: $routeParams.id,
                lockedDocs: lockedDocs,
                create_docs: $scope._createDocs,
                prefixToExclude: $scope.prefixToExclude
            };

            DeliverySrvc.deliver(params, metadatas).$promise
                .then(function () {
                    MessageSrvc.addSuccess(gettextCatalog.getString("La livraison est en cours."));
                    $scope.$emit("backToEdit", true);
                });
        }

        function cancel() {
            $scope.$emit("backToEdit");
        }

        function loadPreDelivery() {
            DeliverySrvc.predeliver({ id: $routeParams.id, create_docs: $scope._createDocs }, function (predelivery) {
                $scope.predelivery = predelivery;
                if (predelivery.errors && predelivery.errors.length > 0) {
                    $scope.emptyDelivery = _.filter(predelivery.errors,
                    function(error){ return error.code == "DELIVERY_NO_MATCHING_PREFIX" || error.code == "DELIVERY_NO_MASTER_FOUND"; }).length > 0;
                    $scope.availableSpaceOnDisk = !_.filter(predelivery.errors,
                    function(error){ return error.code == "DELIVERY_NOT_ENOUGH_AVAILABLE_SPACE" ; }).length > 0;
                }
                if (predelivery.lockedDigitalDocuments.length > 0) {
                    MessageSrvc.addWarn(gettextCatalog.getString("Certains documents ne peuvent pas être livrés"));
                    $scope.accordions.lockedDoc = true;
                }
                if (predelivery.undeliveredDocuments.length > 0) {
                    $scope.accordions.lockedDoc = true;
                }

                _.each(predelivery.documents, function (doc) {
                    //detection de documents à livrer mais vides !
                    if (doc.pageNumber === 0) {
                        $scope.emptyDocs.push(doc);
                        doc.includeDoc = false;
                        $scope.prefixToExclude.push(doc.digitalId);
                    }
                    else {
                        doc.includeDoc = true;
                        // Remontée ds la liste des fichiers avec role pre-renseigne.
                        var metaWithRoles = _.filter(doc.metaDataFiles, function (f) {
                            return f.role !== null;
                        });
                        doc.metaDataFiles = _.union(metaWithRoles, doc.metaDataFiles);
                    }
                });
                $scope.loaded = true;
                // Affichage pour un temps limité à l'ouverture
                MessageSrvc.initPanel();
            });
        }

        function loadMetadataFilesByDoc() {
            var metadataFiles = [];
            var metadatasDoc = [];
            var doc;

            for (var i = 0; i < $scope.predelivery.documents.length; i++) {

                doc = $scope.predelivery.documents[i];
                metadataFiles = [];
                var mf;

                for (var j = 0; j < doc.metaDataFiles.length; j++) {
                    mf = doc.metaDataFiles[j];
                    if (mf.role !== 'NO_ROLE') {
                        metadataFiles.push(mf);
                    }
                }

                var obj = {
                    digitalId: doc.digitalId,
                    pageNumber: doc.pageNumber,
                    metaDataFiles: metadataFiles
                };
                metadatasDoc.push(obj);
            }
            return metadatasDoc;
        }

        function getCommentForStatus(status) {
            switch (status) {
                case "CREATING": return gettextCatalog.getString('Une livraison est déjà en cours sur ce document, veuillez attendre la fin du traitement.');
                case "DELIVERING": return gettextCatalog.getString('Une livraison est déjà en cours sur ce document, veuillez attendre la fin du traitement.');
                default: return gettextCatalog.getString('Aucun commentaire pour ce statut.');
            }
        }
    }
})();
