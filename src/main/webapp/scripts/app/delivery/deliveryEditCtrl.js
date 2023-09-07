(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('DeliveryEditCtrl', DeliveryEditCtrl);

    function DeliveryEditCtrl(
        $location,
        $q,
        $routeParams,
        $scope,
        $timeout,
        codeSrvc,
        DeliverySrvc,
        gettext,
        gettextCatalog,
        HistorySrvc,
        ListTools,
        MessageSrvc,
        ModalSrvc,
        NumaHopInitializationSrvc,
        NumaHopStatusService,
        ValidationSrvc,
        $http,
        $httpParamSerializer,
        FileSaver,
        WebsocketSrvc,
        WorkflowHandleSrvc,
        cfpLoadingBar,
        Principal,
        NumahopStorageService
    ) {
        $scope.canDigitalDocBeViewed = NumaHopStatusService.isDigitalDocAvailable;
        $scope.codes = codeSrvc;
        $scope.delete = deleteDelivery;
        $scope.displayPayment = displayPayment;
        $scope.isDeliveryLocked = NumaHopStatusService.isDeliveryLocked;
        $scope.isUnexpectedError = false;
        $scope.saveDelivery = saveDelivery;
        $scope.validation = ValidationSrvc;
        $scope.downloadDelivReport = downloadDelivReport;
        $scope.downloadSlip = downloadSlip;
        $scope.downloadCheckSlip = downloadCheckSlip;
        $scope.sampleDelivery = sampleDelivery;
        $scope.deliver = deliver;
        $scope.delivered = false;
        $scope.duplicate = duplicate;
        $scope.checkConfiguration = undefined;
        $scope.deliverySample = undefined;
        $scope.detachFromDelivery = detachFromDelivery;
        $scope.checkExpectedFormat = checkExpectedFormat;
        $scope.pdfExtracted = false;
        $scope.canChangeLot = true;
        $scope.deliveryNotChecked = false;
        $scope.pagination = {
            size: 20,
            page: 1,
        };

        $scope.finishedStatus = ['TO_BE_CONTROLLED', 'AUTOMATICALLY_REJECTED', 'REJECTED', 'DELIVERED', 'TREATED', 'VALIDATED', 'DELIVERING_ERROR', 'CLOSED'];
        $scope.toCheckStatus = ['TO_CHECK', 'CHECKING', 'PRE_REJECTED', 'PRE_VALIDATED'];

        // Définition des listes déroulantes
        $scope.options = {
            boolean: {
                true: gettextCatalog.getString('Oui'),
                false: gettextCatalog.getString('Non'),
            },
            payment: DeliverySrvc.config.payment,
            sampleMode: DeliverySrvc.config.sampleMode,
        };

        $scope.currentUser = Principal.identity().then(function (usr) {
            return usr;
        });

        $scope.accordions = {
            docunit: true,
        };

        init();

        /** Initialisation */
        function init() {
            $scope.loaded = false;
            $scope.digitalDocuments = [];

            MessageSrvc.clearMessages();

            loadDelivery();
            loadLotSelect();
            loadCheckConfiguration();
        }

        /** Chargement/Création de la livraison **/
        function loadDelivery() {
            if ('duplicate' in $routeParams && angular.isDefined($routeParams.id)) {
                // Duplication
                $scope.delivery = DeliverySrvc.duplicate(
                    {
                        id: $routeParams.id,
                    },
                    function (delivery) {
                        $scope.canChangeLot = false;
                        afterLoadingDelivery(delivery);
                        openForm();
                    }
                );
            } else if (angular.isDefined($routeParams.id)) {
                // Chargement livraison
                $scope.delivery = DeliverySrvc.get(
                    {
                        id: $routeParams.id,
                    },
                    function (delivery) {
                        $scope.delivering = delivery.status === 'DELIVERING';
                        if ($scope.delivering) {
                            subscribeDelivery(delivery);
                        }

                        loadDigitalDocuments();
                        if (!$scope.delivering) {
                            loadSample();
                        }
                        $scope.canChangeLot = false;
                        afterLoadingDelivery(delivery);
                    }
                );
            } else if (angular.isDefined($routeParams.new)) {
                // Création d'une nouvelle livraison
                HistorySrvc.add(gettextCatalog.getString('Nouvelle livraison'));
                $scope.delivery = new DeliverySrvc();
                $scope.delivery.active = true;
                $scope.delivery.status = 'SAVED';
                $scope.delivery.category = 'OTHER';
                $scope.delivery.payment = 'UNPAID';
                $scope.delivery.method = 'FTP';
                $scope.delivery.receptionDate = new Date();
                $scope.canChangeLot = true;
                $scope.delivered = false;
                afterLoadingDelivery($scope.delivery);
                openForm();
            }
        }

        function addMessagesForLoadedDelivery() {
            switch ($scope.delivery.status) {
                case 'TO_BE_CONTROLLED':
                    MessageSrvc.addSuccess(gettext('Contrôles automatiques validés, en attente des contrôles manuels'), null, true);
                    break;
                case 'AUTOMATICALLY_REJECTED':
                    autoControlMessages();
                    break;
                case 'REJECTED':
                    manualControlMessages();
                    break;
                case 'VALIDATED':
                    MessageSrvc.addSuccess(gettext('Livraison validée'), null, true);
                    break;
                case 'DELIVERED':
                    MessageSrvc.addSuccess(gettext('Livraison effectuée, en attente des contrôles'), null, true);
                    break;
                case 'DELIVERING':
                    MessageSrvc.addInfo(gettext('Livraison en cours'), null, true);
                    break;
                case 'DELIVERING_ERROR':
                    MessageSrvc.addFailure(gettext('Livraison en erreur'), null, true);
                    break;
            }
            // Format attendu
            if (angular.isDefined($scope.delivery.lot)) {
                MessageSrvc.addInfo(gettext('Format attendu : *.{{format}}'), { format: $scope.delivery.lot.requiredFormat }, true);
            }
        }

        function messageForCheck(check, successMsg, failureMsg) {
            if (angular.isDefined(check)) {
                if (check) {
                    MessageSrvc.addSuccess(successMsg, null, true);
                } else {
                    MessageSrvc.addFailure(failureMsg, null, true);
                }
            }
        }

        function autoControlMessages() {
            messageForCheck($scope.delivery.fileRadicalOK, gettext('Radical des fichiers correct'), gettext('Radical des fichiers incorrect'));
            messageForCheck($scope.delivery.numberOfFilesOK, gettext('Nombre de fichiers correct'), gettext('Nombre de fichiers incorrect'));
            messageForCheck($scope.delivery.fileFormatOK, gettext('Format des fichiers correct'), gettext('Format des fichiers incorrect'));
            messageForCheck($scope.delivery.sequentialNumbers, gettext('Séquence des fichiers correcte'), gettext('Séquence des fichiers incorrecte'));
        }

        function manualControlMessages() {}

        function loadLotSelect() {
            var deferred = $q.defer();
            $timeout(function () {
                var promise = NumaHopInitializationSrvc.loadLots({}, {}, 'delivery');
                promise
                    .then(function (value) {
                        deferred.resolve(value);
                        $scope.sel2Lots = value;
                        // Si le lot est précisé, il est automatiquement sélectionné et verrouillé...
                        if (angular.isDefined($routeParams.lot)) {
                            if (angular.isDefined($scope.delivery.identifier)) {
                                // #2164 - on ne verrouille pas si on est en creation, par contre...
                                $scope.canChangeLot = false;
                            }

                            $scope.lockedLot = _.filter($scope.sel2Lots, function (lot) {
                                return lot.identifier === $routeParams.lot;
                            });
                            $scope.delivery.lot = $scope.lockedLot[0];
                        }
                    })
                    .catch(function (value) {
                        deferred.reject(value);
                    });
            });
            return deferred.promise;
        }

        $scope.start = function () {
            cfpLoadingBar.start();
        };

        /****************************************************************/
        /** Suivi progression livraison *********************************************/
        /****************************************************************/

        /** WebSocket du suivi de la progression de la livraison */
        function subscribeDelivery(delivery) {
            // Définition du callback sur un appel du websocket
            var onWebsocketResp = function (data) {
                // Désabonnement quand le traitement est terminé
                if ($scope.subscriber && $scope.finishedStatus.indexOf(delivery.status) > -1) {
                    unsubscribeDelivery();
                }
                // Mise à jour du statut
                return updateProgress(data, delivery);
            };

            // Websocket de suivi
            var subscriberPromise = WebsocketSrvc.subscribeObject(delivery.identifier, onWebsocketResp);
            // Une fois la connexion établie
            subscriberPromise
                // initialisation de l'abonnement en cours
                .then(function (s) {
                    $scope.subscriber = s;
                });

            $scope.$on('$locationChangeStart', unsubscribeDelivery);
        }

        /** Désabonnement au websocket de suivi de la livraison */
        function unsubscribeDelivery() {
            if ($scope.subscriber) {
                $scope.delivering = undefined;

                WebsocketSrvc.unsubscribeObject($scope.subscriber);
                MessageSrvc.clearMessages();
                delete $scope.subscriber;
            }
        }

        /** Suivi de la progression de l'import */
        function updateProgress(data, delivery) {
            $timeout(function () {
                var forceReload = data.reload || delivery.status !== data.status;
                var splashTime = 15000;

                if (data.progress > 0) {
                    _.each($scope.digitalDocuments, function (doc) {
                        if (!data.idDoc || doc.digitalId === data.idDoc) {
                            doc.progress = data.progress;
                        }
                    });
                }

                if (data.stage !== '') {
                    switch (data.typeMsg) {
                        case 'INFO':
                            MessageSrvc.addInfo(data.stage, null, false, splashTime);
                            break;
                        case 'WARN':
                            MessageSrvc.addWarn(data.stage, null, false, splashTime);
                            break;
                        case 'SUCCESS':
                            MessageSrvc.addSuccess(data.stage, null, false, splashTime);
                            break;
                        default:
                            break;
                    }
                }
                delivery.status = data.status;
                if (forceReload) {
                    // rechargement infos
                    if ($scope.finishedStatus.indexOf(delivery.status) > -1) {
                        loadDelivery();
                    } else {
                        loadDigitalDocuments();
                    }
                }
            });
        }

        function loadDeliveryProgress(digitalId) {
            return DeliverySrvc.getDeliveryProgress({ id: $routeParams.id, digitalId: digitalId }).$promise.then(function (value) {
                return value;
            });
        }

        /****************************************************************/
        /** Actions *****************************************************/
        /****************************************************************/
        $scope.cancel = function () {
            $scope.deliveryForm.$cancel();
        };
        $scope.backToList = function () {
            $scope.loaded = false;
            // supprimer tous les paramètres
            $location.search({});
            $location.path('/delivery/delivery');
        };

        function deliver(delivery, createDocs) {
            $scope.$emit('predeliver', { createDocs: !!createDocs });
        }

        $scope.goToAllOperations = function (documentId, tab) {
            if (tab) {
                $location.path('/document/all_operations/' + documentId).search({ tab: tab });
            } else {
                $location.path('/document/all_operations/' + documentId).search('');
            }
        };

        function duplicate() {
            if ($scope.delivery) {
                $scope.loaded = false;
                $scope.delivery._selected = false;
                var identifier = $scope.delivery.identifier;
                $scope.delivery = null;
                $location.path('/delivery/delivery').search({ id: identifier, duplicate: true });
            }
        }

        function sampleDelivery() {
            // recup le sample
            var params = [];
            params.id = $scope.deliverySample.identifier;
            params.deliveryId = $scope.delivery.identifier;
            params.sampling = true;
            params.pdfExtracted = $scope.pdfExtracted;

            $location.path('/viewer/viewer').search($httpParamSerializer(params));
        }

        function downloadDelivReport() {
            if (angular.isDefined($routeParams.id)) {
                var params = [];
                params.deliveryReport = true;
                var url = 'api/rest/delivery/' + $routeParams.id + '?' + $httpParamSerializer(params);
                // pour conserver l'encodage original dans le fichier sauvegardé
                $http.get(url, { responseType: 'arraybuffer' }).then(function (response) {
                    var filename = 'rapport_Livraison_' + $scope.delivery.label + '.txt';
                    var blob = new Blob([response.data], { type: response.headers('content-type') });
                    FileSaver.saveAs(blob, filename);
                });
            }
        }

        /**
         * Téléchargement du bordereau de livraison
         **/
        function downloadSlip(format) {
            if ($scope.delivery) {
                var url = 'api/rest/delivery/' + format + '/' + $scope.delivery.identifier;

                // on met la réponse dans un arraybuffer pour conserver l'encodage original dans le fichier sauvegardé
                $http.get(url, { responseType: 'arraybuffer' }).then(function (response) {
                    var filename = 'bordereau_livraison _' + $scope.delivery.label + '.' + format;
                    var blob = new Blob([response.data], { type: response.headers('content-type') });
                    FileSaver.saveAs(blob, filename);
                });
            }
        }

        /**
         * Téléchargement du bordereau de contrôle
         **/
        function downloadCheckSlip(format) {
            if ($scope.delivery) {
                var url = 'api/rest/check/' + format + '/' + $scope.delivery.identifier;

                // on met la réponse dans un arraybuffer pour conserver l'encodage original dans le fichier sauvegardé
                $http.get(url, { responseType: 'arraybuffer' }).then(function (response) {
                    var filename = 'bordereau.' + format;
                    var blob = new Blob([response.data], { type: response.headers('content-type') });
                    FileSaver.saveAs(blob, filename);
                });
            }
        }
        /****************************************************************/
        /** Fonctions ***************************************************/
        /****************************************************************/
        // Sauvegarde une livraison
        function saveDelivery(delivery) {
            if ($scope.delivering) {
                return;
            }
            delete $scope.errors;
            $timeout(function () {
                var creation = angular.isUndefined(delivery.identifier) || delivery.identifier === null;

                delivery.$save(
                    {},
                    function (value) {
                        MessageSrvc.addSuccess(gettextCatalog.getString('La livraison {{label}} a été sauvegardée'), { label: $scope.delivery.label });
                        onSuccess(value);
                        // si création, on ajoute à la liste et on passe en mode livraison, sinon, on essaye de MAJ les infos dans la colonne du milieu
                        if (creation) {
                            $scope.clearSelection();
                            addNewDeliveryToList(value, $scope.newDeliveries, $scope.pagination.items);
                            $location.search({ id: value.identifier }); // suppression des paramètres
                        } else {
                            $scope.updateDelivery($scope.delivery.identifier, $scope.delivery);
                        }
                    },
                    function (response) {
                        $scope.errors = _.chain(response.data.errors)
                            .groupBy('field')
                            .mapObject(function (list) {
                                return _.pluck(list, 'code');
                            })
                            .value();

                        openForm();
                    }
                );
            });
        }

        // Met à jour la liste avec la nouvelle livraison
        function addNewDeliveryToList(delivery, newDeliveries, deliveries) {
            var found = _.find(newDeliveries, function (b) {
                return b.identifier === delivery.identifier;
            });
            if (found) {
                /** Si cette livraison existe déjà, ne rien faire */
                return;
            }
            found = _.find(deliveries, function (b) {
                return b.identifier === delivery.identifier;
            });
            if (found) {
                /** Si cette livraison existe déjà, ne rien faire */
                return;
            }

            var newDelivery = {
                _selected: true,
                identifier: delivery.identifier,
                label: delivery.label,
            };
            newDeliveries.push(newDelivery);
        }

        // Gestion de delivery renvoyé par le serveur
        function onSuccess(value) {
            $scope.delivery = value;
            HistorySrvc.add(gettextCatalog.getString('Livraison {{label}}', $scope.delivery));
            displayMessages($scope.delivery);
        }

        // Ouverture du formulaire et des sous formulaires
        function openForm() {
            $timeout(function () {
                if (angular.isDefined($scope.deliveryForm)) {
                    $scope.deliveryForm.$show();
                }
            });
        }

        function displayMessages(entity) {
            // On commence par vider les messages précédents...
            MessageSrvc.clearMessages();
            // ... puis on affiche les infos de modification ...
            if (angular.isDefined(entity.lastModifiedDate)) {
                var dateModif = new Date(entity.lastModifiedDate);
                MessageSrvc.addInfo(gettext('Dernière modification le {{date}} par {{author}}'), { date: dateModif.toLocaleString(), author: entity.lastModifiedBy }, true);
            }
            // ... puis on affiche les infos de création ...
            if (angular.isDefined(entity.createdDate)) {
                var dateCreated = new Date(entity.createdDate);
                MessageSrvc.addInfo(gettext('Créé le {{date}}'), { date: dateCreated.toLocaleString() }, true);
            }
            // Affichage pour un temps limité à l'ouverture
            MessageSrvc.initPanel();
        }

        function afterLoadingDelivery(delivery) {
            onSuccess(delivery);
            $scope.loaded = true;
            $scope.delivered = delivery.status !== 'SAVED';
            var notCheckedStatus = ['TO_BE_CONTROLLED', 'SAVED', 'DELIVERING'];
            $scope.deliveryNotChecked = notCheckedStatus.indexOf(delivery.status) > -1;

            $scope.userIsPresta = $scope.currentUser.category === 'PROVIDER' && $scope.isAuthorized($scope.userRoles.DEL_HAB2);
            // un presta n'a plus la possibilité de modifier une livraison demarrée.
            $scope.formRO = $scope.delivery.status !== 'SAVED' && ($scope.userIsPresta || !$scope.isAuthorized($scope.userRoles.DEL_HAB8));

            // un presta peut supprimer une livraison si créée par lui-meme et non demarrée.
            $scope.canDeleteDelivery = $scope.isAuthorized($scope.userRoles.DEL_HAB3) && (!$scope.userIsPresta || ($scope.userIsPresta && $scope.currentUser.login === delivery.createdBy));

            addMessagesForLoadedDelivery();
            if (angular.isDefined($scope.delivery.lot)) {
                $scope.pdfExtracted = $scope.delivery.lot.requiredFormat === 'PDF';
                $scope.canChangeLot = $scope.delivery.status === 'SAVED';
            }
        }

        /* Chargement Config de controle active */
        function loadCheckConfiguration() {
            if (angular.isDefined($routeParams.id)) {
                DeliverySrvc.getActiveCheckConfig(
                    {
                        id: $routeParams.id,
                    },
                    function (config) {
                        if (angular.isDefined(config.sampleMode)) {
                            config.sampleModeLabel = displaySampleMode(config.sampleMode);
                            if ('NO_SAMPLE' !== config.sampleMode) {
                                config.sampleModeLabel += ' (' + config.sampleRate * 100 + '%)';
                            }
                            $scope.checkConfiguration = config;
                        } else {
                            MessageSrvc.addWarn(gettext('Aucune configuration de contrôles paramétrée sur le lot', null, true));
                        }
                    }
                );
            }
        }

        function checkExpectedFormat(lot) {
            $scope.pdfExtracted = lot.requiredFormat === 'PDF';
        }

        function loadDigitalDocuments() {
            DeliverySrvc.getDigitalDocuments(
                {
                    id: $routeParams.id,
                },
                function (digitalDocuments) {
                    $scope.digitalDocuments = digitalDocuments;
                    var totalPages = 0;
                    if ($scope.delivering) {
                        loadDeliveryProgress().then(function (values) {
                            _.each(digitalDocuments, function (doc) {
                                if (doc.status === 'TO_CHECK' || doc.status === 'CHECKING' || doc.status === 'PRE_REJECTED' || doc.status === 'REJECTED') {
                                    doc.progress = 100;
                                } else {
                                    doc.progress = values[doc.digitalId] || 0;
                                }
                            });
                        });
                    }
                    _.each(digitalDocuments, function (doc) {
                        // Récupération de la présence de workflow (isWorkflowStarted.done est true si un workflow est démarré)
                        doc.isWorkflowStarted = $scope.delivery.lot.status === 'ONGOING';
                        totalPages += doc.nbPages;
                    });
                    $scope.delivery.totalPages = totalPages;
                    initStatusForDigitalDocument($scope.digitalDocuments);
                    $scope.isUnexpectedError = isDeliveryInUnexpectedError();
                    $scope.canDeleteDelivery = $scope.canDeleteDelivery && $scope.digitalDocuments.length == 0;
                }
            );
        }

        function loadSample() {
            DeliverySrvc.getSample(
                {
                    id: $routeParams.id,
                },
                function (sample) {
                    $scope.deliverySample = sample;
                    initStatusForDigitalDocument(sample.documents);
                }
            );
        }

        function initStatusForDigitalDocument(documents) {
            _.each(documents, function (doc) {
                doc.isFailed = false;
                doc.isWarned = false;
                if (angular.isDefined(doc.automaticCheckResults) && doc.automaticCheckResults.length > 0) {
                    doc.filteredAutoCheckResults = {};
                    var lists = _.groupBy(doc.automaticCheckResults, function (result) {
                        return result.check.identifier;
                    });
                    _.each(lists, function (groupedList, idx) {
                        // Tri par date ascendante
                        var sortedList = _.sortBy(lists[idx], 'createdDate');
                        // Récupération du dernier uniquement
                        doc.filteredAutoCheckResults[idx] = _.last(sortedList);
                    });
                    // Concaténation des résultats et affichage des warnings
                    var warns = _.filter(doc.filteredAutoCheckResults, function (result) {
                        return result.result === 'OTHER';
                    });
                    if (warns.length > 0) {
                        // Affichage dans le détail
                        doc.isWarned = true;
                        // Affichage de messages détaillés
                        _.each(warns, function (error) {
                            MessageSrvc.addInfo(gettextCatalog.getString('Avertissement - {{label}}: {{document}}'), { label: error.check.label, document: doc.digitalId, message: error.message }, true);
                        });
                    }
                    // Concaténation des résultats et affichage des erreurs
                    var errors = _.filter(doc.filteredAutoCheckResults, function (result) {
                        return result.result === 'KO';
                    });
                    if (errors.length > 0) {
                        // Affichage dans le détail
                        doc.isFailed = true;
                        // Affichage de messages détaillés
                        _.each(errors, function (error) {
                            MessageSrvc.addFailure(gettextCatalog.getString('Erreur - {{label}}: {{document}}'), { label: error.check.label, document: doc.digitalId, message: error.message }, true);
                        });
                    }
                }
            });
        }

        /**
         * Détache un digitalDocument resté au statut 'en cours de livraison' de la livraison.
         *  => débloquer la livraison
         *  => permettre une relivraison du doc incriminé
         */
        function detachFromDelivery(doc) {
            var docUnit = {};
            docUnit.identifier = doc.docUnitId;
            doc.docUnit = docUnit;
            DeliverySrvc.detachDigitalDoc({ id: $routeParams.id }, doc, function (deliv) {
                $scope.delivery = deliv;
                loadDigitalDocuments();
            });
        }

        function deleteDelivery(delivery) {
            ModalSrvc.confirmDeletion(gettextCatalog.getString('la livraison {{label}}', delivery)).then(function () {
                delivery.$delete(function () {
                    MessageSrvc.addSuccess(gettext('La livraison {{label}} a été supprimée'), delivery);
                    ListTools.findAndRemoveItemFromLists(delivery, $scope.pagination.items, $scope.newDeliveries);
                    $location.search({}); // suppression des paramètres
                });
            });
        }

        function isDeliveryInUnexpectedError() {
            if ($scope.delivery.status === 'DELIVERING' || $scope.delivery.status === 'SAVED') {
                var isInError = undefined;
                var sinceLastModif = Date.now() - new Date($scope.delivery.lastModifiedDate).getTime();
                var hours = Math.floor(sinceLastModif / 3600000);
                if (hours > 24) {
                    if ($scope.digitalDocuments && $scope.digitalDocuments.length > 0) {
                        // on tente de detecter une anomalie de livraison non trappée ...
                        isInError = _.find($scope.digitalDocuments, function (doc) {
                            // doc en livraison depuis + de 24h => erreur
                            return doc.status === 'CREATING' || doc.status === 'DELIVERING';
                        });
                    }
                }
                if (angular.isUndefined(isInError)) {
                    return false;
                } else {
                    // on a bien un probleme ds ce cas...
                    return true;
                }
            } else {
                return false;
            }
        }

        function displayPayment(payment) {
            return $scope.options.payment[payment] || payment;
        }

        function displaySampleMode(sampleMode) {
            return $scope.options.sampleMode[sampleMode] || sampleMode;
        }
    }
})();
