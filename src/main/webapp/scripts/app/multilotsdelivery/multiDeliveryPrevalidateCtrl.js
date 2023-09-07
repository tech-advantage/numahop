(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('MultiDeliveryPrevalidateCtrl', MultiDeliveryPrevalidateCtrl);

    function MultiDeliveryPrevalidateCtrl($routeParams, $scope, MultiDeliverySrvc, codeSrvc, gettextCatalog, MessageSrvc, ModalSrvc, ValidationSrvc) {
        $scope.semCodes = codeSrvc;
        $scope.validation = ValidationSrvc;
        $scope.authorizedDelivery = true;
        $scope.prefixToExclude = [];

        // Gestion des vues
        $scope.viewModes = {
            VIEW: 'view', // Visualisation, Édition rapide
            EDIT: 'edit',
        }; // Création, Modification
        $scope.viewMode = $routeParams.mode || $scope.viewModes.VIEW;

        $scope.binding = { resp: '' };
        $scope.loaded = false;
        $scope.save = save;
        $scope.cancel = cancel;
        $scope.getCommentForStatus = getCommentForStatus;
        $scope.emptyDocs = [];

        $scope.accordions = {
            digitalDoc: true,
            lockedDoc: false,
            metadata: true,
        };

        $scope.fileType = {
            NO_ROLE: gettextCatalog.getString(''),
            METS: gettextCatalog.getString('Fichier METS'),
            EXCEL: gettextCatalog.getString('Table des matières Excel'),
            PDF_MULTI: gettextCatalog.getString('Fichier PDF/A (Muticouches)'),
            OTHER: gettextCatalog.getString('Autre'),
        };

        // toggle switch label ON/OFF
        $scope.onLabelActiv = gettextCatalog.getString('Livrer');
        $scope.offLabelActiv = gettextCatalog.getString('Exclure');

        $scope.docUnits = [];

        init();

        /** Initialisation */
        function init() {
            loadPreDelivery();
        }

        function save() {
            if ($scope.authorizedDelivery) {
                var lockedDoc = false;
                _.each($scope.predelivery, function (value, key) {
                    if (value.lockedDigitalDocuments && value.lockedDigitalDocuments.length > 0) {
                        lockedDoc = true;
                        return;
                    }
                });

                if (lockedDoc) {
                    ModalSrvc.confirmAction(gettextCatalog.getString('effectuer la livraison incomplète')).then(function () {
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
            MessageSrvc.addWarn(gettextCatalog.getString('Initialisation de la livraison...'));

            var lockedDocs = [];
            _.each($scope.predelivery, function (value, key) {
                if (value.lockedDigitalDocuments) {
                    var obj = {
                        deliveryLabel: key,
                        docs: _.pluck(value.lockedDigitalDocuments, 'identifier'),
                    };

                    lockedDocs.push(obj);
                }
            });

            var metadatas = loadMetadataFilesByDoc();
            var params = {
                id: $routeParams.id,
                prefixToExclude: $scope.prefixToExclude,
            };

            var wrapper = {
                lockedDocs: lockedDocs,
                metadatas: metadatas,
            };

            MultiDeliverySrvc.deliver(params, wrapper).$promise.then(function () {
                MessageSrvc.addSuccess(gettextCatalog.getString('La livraison est en cours.'));
                $scope.$emit('backToEdit', true);
            });
        }

        function cancel() {
            $scope.$emit('backToEdit');
        }

        function loadPreDelivery() {
            MultiDeliverySrvc.predeliver({ id: $routeParams.id }, function (predelivery) {
                $scope.predelivery = predelivery;

                _.each(predelivery, function (value, key) {
                    if (value.errors && value.errors.length > 0) {
                        for (var i = 0; i < value.errors.length; i++) {
                            if ('DELIVERY_NO_MASTER_FOUND' === value.errors[i].code) {
                                $scope.authorizedDelivery = false;
                                MessageSrvc.addWarn(gettextCatalog.getString('Aucun document à livrer ne correspond à la livraison {}', key));
                            }
                        }
                    } else if (value.lockedDigitalDocuments && value.lockedDigitalDocuments.length > 0) {
                        MessageSrvc.addWarn(gettextCatalog.getString('Certains documents ne peuvent pas être livrés'));
                        $scope.accordions.digitalDoc = false;
                        $scope.accordions.lockedDoc = true;
                    }

                    _.each(value.documents, function (doc) {
                        //detection de documents à livrer mais vides !
                        if (doc.pageNumber === 0) {
                            $scope.emptyDocs.push(doc);
                            doc.includeDoc = false;
                            $scope.prefixToExclude.push(doc.digitalId);
                        } else {
                            doc.includeDoc = true;
                            // Remontée ds la liste des fichiers avec role pre-renseigne.
                            var metaWithRoles = _.filter(doc.metaDataFiles, function (f) {
                                return f.role !== null;
                            });
                            doc.metaDataFiles = _.union(metaWithRoles, doc.metaDataFiles);
                        }
                    });
                });

                $scope.loaded = true;
                // Affichage pour un temps limité à l'ouverture
                MessageSrvc.initPanel();
            });
        }

        function loadMetadataFilesByDoc() {
            var metadatasDoc = [];
            var doc;

            _.each($scope.predelivery, function (value, key) {
                if (value.documents) {
                    for (var i = 0; i < value.documents.length; i++) {
                        doc = value.documents[i];
                        var metadataFiles = [];
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
                            metaDataFiles: metadataFiles,
                        };
                        metadatasDoc.push(obj);
                    }
                }
            });

            return metadatasDoc;
        }

        function getCommentForStatus(status) {
            switch (status) {
                case 'CREATING':
                    return gettextCatalog.getString('Une livraison est déjà en cours sur ce document, veuillez attendre la fin du traitement.');
                case 'DELIVERING':
                    return gettextCatalog.getString('Une livraison est déjà en cours sur ce document, veuillez attendre la fin du traitement.');
                default:
                    return gettextCatalog.getString('Aucun commentaire pour ce statut.');
            }
        }
    }
})();
