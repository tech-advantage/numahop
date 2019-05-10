(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('MultiDeliveryEditCtrl', MultiDeliveryEditCtrl);

    function MultiDeliveryEditCtrl($location, $routeParams, $scope, $timeout, codeSrvc, DeliverySrvc, MultiDeliverySrvc,
        gettext, gettextCatalog, HistorySrvc, ListTools, MessageSrvc, ModalSrvc, NumaHopInitializationSrvc,
        NumaHopStatusService, ValidationSrvc, $http, $httpParamSerializer, FileSaver, WebsocketSrvc, Principal,
        WorkflowHandleSrvc, cfpLoadingBar, DocUnitBaseService) {

        $scope.canDigitalDocBeViewed = NumaHopStatusService.isDigitalDocAvailable;
        $scope.codes = codeSrvc;
        $scope.delete = deleteDelivery;
        $scope.displayPayment = displayPayment;
        $scope.displayMethod = displayMethod;
        $scope.isDeliveryLocked = NumaHopStatusService.isDeliveryLocked;
        $scope.isUnexpectedError = false;
        $scope.saveDelivery = saveDelivery;
        $scope.validation = ValidationSrvc;
        $scope.downloadDelivReport = downloadDelivReport;
        $scope.deliver = deliver;
        $scope.delivered = false;
        $scope.checkConfiguration = undefined;
        $scope.deliverySample = undefined;
        $scope.pdfExtracted = false;
        $scope.canChangeLot = true;
        $scope.deliveryNotChecked = false;
        $scope.dofilterLots = dofilterLots;
        $scope.onchangeSelType = onchangeSelType;
        $scope.onchangeSelTrain = onchangeSelTrain;
        $scope.displaySelectionType = displaySelectionType;

        $scope.finishedStatus = ["TO_BE_CONTROLLED", "AUTOMATICALLY_REJECTED", "REJECTED", "DELIVERED", "TREATED", "VALIDATED", "DELIVERING_ERROR"];
        $scope.toCheckStatus = ["TO_CHECK", "CHECKING", "PRE_REJECTED"];
        $scope.progValues = [];
        $scope.sel2Lots = [];
        $scope.sel2Trains = [];
        $scope.delivLabels = {};
        $scope.creation = false;
        $scope.selectedByTrain = false;
        

        // Définition des listes déroulantes
        $scope.options = {
            selections: [
                { value: true, text: gettextCatalog.getString('Lots') },
                { value: false, text: gettextCatalog.getString('Train') }
            ],
            method: DeliverySrvc.config.method,
            payment: DeliverySrvc.config.payment,
            sampleMode: DeliverySrvc.config.sampleMode
        };        
        
        $scope.accordions = {
            docunit: true
        };

        init();

        /** Initialisation */
        function init() {

            $scope.loaded = false;
            $scope.digitalDocuments = [];

            MessageSrvc.clearMessages();

            loadTrainSelect()
                .then(loadLotSelect())
                .then(loadDelivery());
                //.then(setSelectedTrain());
        }

        /** Chargement/Création de la livraison **/
        function loadDelivery() {
            if (angular.isDefined($routeParams.id)) {
                // Chargement livraison
                $scope.multiDelivery = MultiDeliverySrvc.get({
                    id: $routeParams.id
                }, function (multiDelivery) {

                    if (!$scope.delivering) {
                        $scope.delivering = multiDelivery.status === 'DELIVERING';
                    }
                    if ($scope.delivering) {
                        subscribeDelivery(multiDelivery);
                    }
                    loadDigitalDocuments();
                    afterLoadingDelivery(multiDelivery);
                });
            } else if (angular.isDefined($routeParams.new)) {
                // Création d'une nouvelle livraison
                HistorySrvc.add(gettextCatalog.getString("Nouvelle livraison multi-lots"));
                $scope.multiDelivery = new MultiDeliverySrvc();
                $scope.multiDelivery.active = true;
                $scope.multiDelivery.status = "SAVED";
                $scope.multiDelivery.category = "OTHER";
                $scope.multiDelivery.payment = "UNPAID";
                $scope.multiDelivery.method = "FTP";
                $scope.multiDelivery.typSelection = true;
                $scope.multiDelivery.selectedByTrain = false;
                $scope.multiDelivery.receptionDate = new Date();
                $scope.multiDelivery.lots = [];
                $scope.multiDelivery.train = undefined;
                $scope.delivered = false;
                $scope.creation = true;
                afterLoadingDelivery($scope.multiDelivery);
                openForm();
            }
        }

        function addMessagesForLoadedDelivery() {
            switch ($scope.multiDelivery.status) {
                case "TO_BE_CONTROLLED":
                    MessageSrvc.addSuccess("Contrôles automatiques validés, en attente des contrôles manuels", null, true);
                    break;
                case "AUTOMATICALLY_REJECTED":
                    autoControlMessages();
                    break;
                case "REJECTED":
                    manualControlMessages();
                    break;
                case "VALIDATED":
                    MessageSrvc.addSuccess("Livraison validée", null, true);
                    break;
                case "DELIVERED":
                    MessageSrvc.addSuccess("Livraison effectuée, en attente des contrôles", null, true);
                    break;
                case "DELIVERING":
                    MessageSrvc.addInfo("Livraison en cours", null, true);
                    break;
                case "DELIVERING_ERROR":
                    MessageSrvc.addFailure("Livraison en erreur", null, true);
                    break;
            }
        }


        function loadLotSelect() {
            return NumaHopInitializationSrvc.loadLots({}, {}, 'multilotsdelivery').then(function (lots) {
                $scope.sel2Lots = lots;
                $scope.filteredLots = lots;
                return lots;
            });
        }

        function dofilterLots() {
            $scope.filteredLots = [];
            if ($scope.multiDelivery.lots.length === 0) {
                $scope.filteredLots = $scope.sel2Lots;
            } else {

                var projId = $scope.multiDelivery.lots[0].projectIdentifier;
                // on restreint la liste aux lots du mm project
                var filtLots = _.filter($scope.sel2Lots, function (lo) {
                    return lo.projectIdentifier === projId;
                });
                $scope.filteredLots = filtLots;
            }
        }
        
        function loadTrainSelect() {
            return NumaHopInitializationSrvc.loadTrains({}, {}, 'multilotsdelivery').then(function (trains) {
                $scope.sel2Trains = trains;
                return trains;
            });
        }
        
        function onchangeSelTrain(train) {
            $scope.multiDelivery.trainId = train.identifier;
        }
        
        function displaySelectionType(value) {
            var found = _.find($scope.options.selections, function (b) {
                return b.value === value;
            });
            if (angular.isDefined(found)) {
                return found.text;
            }
        }
        
        function onchangeSelType(type) { 
            if (type) {
                // selection classique des lots
                $scope.multiDelivery.selectedByTrain = false;
                $scope.multiDelivery.train = [];
            } else {
                // selection des lots du train
                $scope.multiDelivery.selectedByTrain = true;
                $scope.multiDelivery.lots = [];
            }
        }
        
        function setSelectedTrain(multi) {
            if (multi.selectedByTrain) {
                $scope.multiDelivery.typSelection = false;
                var selectedTrain = _.find($scope.sel2Trains, function (train) {
                    return train.identifier === $scope.multiDelivery.trainId;
                });
                if (angular.isDefined(selectedTrain)) {
                    $scope.multiDelivery.train = selectedTrain;
                }
            } else {
                $scope.multiDelivery.typSelection = true;
            }
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
                // Annulation de la vérification du statut
                if (angular.isDefined($scope.subscrCheckPromise)) {
                    $timeout.cancel($scope.subscrCheckPromise);
                    delete $scope.subscrCheckPromise;
                }
                // Désabonnement quand le traitement est terminé
                if ($scope.subscriber
                    && $scope.finishedStatus.indexOf(delivery.status) > -1) {
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
                .then(function (s) { $scope.subscriber = s; })
                .then(function () {
                    // Mise à jour livraison et désabonnement au websocket après 1mn35 sans réponse
                    $scope.subscrCheckPromise = $timeout(function () {

                        DeliverySrvc.getDeliveryStatus({ id: delivery.identifier }).$promise
                            .then(function (data) {
                                // Désabonnement
                                if ($scope.finishedStatus.indexOf(delivery.status) > -1) {
                                    unsubscribeDelivery();
                                }
                                // Mise à jour du statut
                                updateProgress(data, delivery);
                            });
                    }, 95000);
                });
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
                // console.log(data);

                if (data.progress > 0) {
                    var digitalDocuments = $scope.digitalDocuments;
                    var cpt = 0;
                    var oneDocToCheck = false;

                    _.each(digitalDocuments, function (doc) {
                        if ('TO_CHECK' === doc.status || 'CHECKING' === doc.status) {
                            oneDocToCheck = true;
                        }
                        if (data.idDoc) {
                            if (doc.digitalId === data.idDoc) {
                                $scope.progValues[cpt] = data.progress;
                            }
                        } else {
                            $scope.progValues[cpt] = data.progress;
                        }
                        cpt++;
                    });
                    forceReload = true;
                }

                if (data.stage !== '') {
                    switch (data.typeMsg) {
                        case "INFO":
                            MessageSrvc.addInfo(data.stage, null, false, splashTime);
                            break;
                        case "WARN":
                            MessageSrvc.addWarn(data.stage, null, false, splashTime);
                            break;
                        case "SUCCESS":
                            MessageSrvc.addSuccess(data.stage, null, false, splashTime);
                            break;
                        default:
                            break;
                    }
                }
                delivery.status = data.status;
                if (forceReload) {
                    cpt = 0;
                    if (oneDocToCheck) {
                        _.each(digitalDocuments, function () {
                            if ($scope.progValues[cpt] === 1) {
                                $scope.progValues[cpt] = 20;
                            }
                            cpt++;
                        });
                    }

                    // rechargement infos
                    if ($scope.finishedStatus.indexOf(delivery.status) > -1) {
                        loadDelivery();
                    } else {
                        loadDigitalDocuments();
                    }
                }
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
            $location.path("/multilotsdelivery/multidelivery");
        };

        function deliver() {
            $scope.$emit("predeliver");
        }

        $scope.goToAllOperations = function (documentId, tab) {
            if (tab) {
                $location.path("/document/all_operations/" + documentId).search({ tab: tab });
            } else {
                $location.path("/document/all_operations/" + documentId).search("");
            }
        };

        function downloadDelivReport() {
            // TODO boucle sur les livraisons !!

            if (angular.isDefined($routeParams.id)) {
                var params = [];
                params.deliveryReport = true;
                var url = 'api/rest/delivery/' + $routeParams.id + '?' + $httpParamSerializer(params);
                // pour conserver l'encodage original dans le fichier sauvegardé
                $http.get(url, { responseType: 'arraybuffer' })
                    .then(function (response) {
                        var filename = 'rapport_Livraison_' + $scope.multiDelivery.label + '.txt';
                        var blob = new Blob([response.data], { type: response.headers("content-type") });
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

                delivery.$save({},
                    function (value) {
                        MessageSrvc.addSuccess(gettextCatalog.getString("La livraison {{label}} a été sauvegardée"), { label: $scope.multiDelivery.label });
                        onSuccess(value);
                        // si création, on ajoute à la liste et on passe en mode livraison, sinon, on essaye de MAJ les infos dans la colonne du milieu
                        if (creation) {
                            $scope.clearSelection();
                            addNewDeliveryToList(value, $scope.newDeliveries, $scope.pagination.items);
                            $location.search({ id: value.identifier }); // suppression des paramètres
                        } else {
                            $scope.updateDelivery($scope.multiDelivery.identifier, $scope.multiDelivery);
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
                label: delivery.label
            };
            newDeliveries.push(newDelivery);
        }

        // Gestion de delivery renvoyé par le serveur
        function onSuccess(value) {
            $scope.multiDelivery = value;
            HistorySrvc.add(gettextCatalog.getString("Livraison multi-lots {{label}}", $scope.multiDelivery));
            displayMessages($scope.multiDelivery);
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
                MessageSrvc.addInfo("Dernière modification le {{date}} par {{author}}",
                    { date: dateModif.toLocaleString(), author: entity.lastModifiedBy }, true);
            }
            // ... puis on affiche les infos de création ...
            if (angular.isDefined(entity.createdDate)) {
                var dateCreated = new Date(entity.createdDate);
                MessageSrvc.addInfo("Créé le {{date}}",
                    { date: dateCreated.toLocaleString() }, true);
            }
            // Affichage pour un temps limité à l'ouverture
            MessageSrvc.initPanel();
        }

        function afterLoadingDelivery(multi) {
            onSuccess(multi);
            
            $scope.delivered = multi.status !== "SAVED";
            var notCheckedStatus = ["TO_BE_CONTROLLED", "SAVED", "DELIVERING"];
            $scope.deliveryNotChecked = notCheckedStatus.indexOf(multi.status) > -1;
            
            addMessagesForLoadedDelivery();
            dofilterLots();

            // changement lot possible si aucune livraison demarree.
            var oneDelivStarted = _.find(multi.deliveries, function (del) {
                return del.status !== 'SAVED';
            });
            $scope.canChangeLot = angular.isUndefined(oneDelivStarted);
            loadUserInfos();
            
            $scope.formRO = multi.status !== 'SAVED' 
                && ($scope.userIsPresta || !$scope.isAuthorized($scope.userRoles.DEL_HAB8));
            
         // un presta peut supprimer une livraison si créée par lui-meme et non demarrée.
            $scope.canDeleteDelivery = multi.status === 'SAVED' && $scope.isAuthorized($scope.userRoles.DEL_HAB3)
                                && ( !$scope.userIsPresta ||
                                        ($scope.userIsPresta && $scope.currentUser.login === multi.createdBy)
                                   );
            
            setSelectedTrain(multi);
            $scope.loaded = true;
        }
        
        
        function loadUserInfos() {
            var currentUser = Principal.identity();
            if (angular.isDefined(currentUser)) {
                currentUser.then(function (result) {
                    $scope.currentUser = result;
                    $scope.userIsPresta = result.category === "PROVIDER" 
                                    && $scope.isAuthorized($scope.userRoles.DEL_HAB2);
                });
            }
        }


        function loadDigitalDocuments() {
            MultiDeliverySrvc.getDigitalDocuments({
                id: $routeParams.id
            }, function (docsByDeliv) {

                $scope.digitalDocuments = docsByDeliv;
                var totalPages = 0;
                var cpt = 0;

                _.each(_.keys(docsByDeliv), function (key) {

                    var values = $scope.digitalDocuments[key];
                    _.each(values, function (doc) {

                        // Récupération de la présence de workflow (isWorkflowStarted.done est true si un workflow est démarré)
                        if (doc.identifier) {
                            doc.isWorkflowStarted = WorkflowHandleSrvc.isWorkflowStarted(doc.identifier);

                            $scope.delivLabels[doc.delivery.identifier] = doc.delivery.label;
                        }


                        totalPages += doc.nbPages;
                        if ($scope.delivering && !$scope.progValues[cpt]) {
                            if (doc.status === 'TO_CHECK' || doc.status === 'CHECKING' || doc.status === 'REJECTED') {
                                $scope.progValues[cpt] = 100;
                            } else {
                                $scope.progValues[cpt] = 2;
                            }
                        }
                        cpt++;
                    });

                });

                $scope.multiDelivery.totalPages = totalPages;
                initStatusForDigitalDocument($scope.digitalDocuments);
                $scope.isUnexpectedError = isDeliveryInUnexpectedError();
            });
        }


        // TODO voir pour fetcher les checkResults !!!!
        function initStatusForDigitalDocument(documentsByDeliv) {

            _.each(_.keys(documentsByDeliv), function (key) {

                var documents = documentsByDeliv[key];

                _.each(documents, function (document) {
                    document.isFailed = false;
                    document.isWarned = false;
                    if (angular.isDefined(document.automaticCheckResults) && document.automaticCheckResults.length > 0) {
                        document.filteredAutoCheckResults = {};
                        var lists = _.groupBy(document.automaticCheckResults, function (result) { return result.check.identifier; });
                        _.each(lists, function (groupedList, idx) {
                            // Tri par date ascendante
                            var sortedList = _.sortBy(lists[idx], "createdDate");
                            // Récupération du dernier uniquement
                            document.filteredAutoCheckResults[idx] = _.last(sortedList);
                        });
                        // Concaténation des résultats et affichage des warnings
                        var warns = _.filter(document.filteredAutoCheckResults, function (result) {
                            return result.result === "OTHER";
                        });
                        if (warns.length > 0) {
                            // Affichage dans le détail
                            document.isWarned = true;
                            // Affichage de messages détaillés
                            _.each(warns, function (error) {
                                MessageSrvc.addInfo(gettextCatalog.getString("Avertissement - {{label}}: {{document}}"),
                                    { label: error.check.label, document: document.digitalId, message: error.message }, true);
                            });
                        }
                        // Concaténation des résultats et affichage des erreurs
                        var errors = _.filter(document.filteredAutoCheckResults, function (result) {
                            return result.result === "KO";
                        });
                        if (errors.length > 0) {
                            // Affichage dans le détail
                            document.isFailed = true;
                            // Affichage de messages détaillés
                            _.each(errors, function (error) {
                                MessageSrvc.addFailure(gettextCatalog.getString("Erreur - {{label}}: {{document}}"),
                                    { label: error.check.label, document: document.digitalId, message: error.message }, true);
                            });
                        }

                    }
                });
            });

        }


        function deleteDelivery(delivery) {
            ModalSrvc.confirmDeletion(gettextCatalog.getString("la livraison {{label}}", delivery))
                .then(function () {
                    delivery.$delete(function () {
                        MessageSrvc.addSuccess(gettext("La livraison {{label}} a été supprimée"), delivery);
                        ListTools.findAndRemoveItemFromLists(delivery, $scope.pagination.items, $scope.newDeliveries);
                        $location.search({}); // suppression des paramètres
                    });
                });
        }

        /**
         * Detection d'erreurs de livraisons non trappées.
         */
        function isDeliveryInUnexpectedError() {

            _.each($scope.multiDelivery.deliveries, function (delivery) {

                if (delivery.status === "DELIVERING_ERROR") {
                    return true;
                } else if (delivery.status === "DELIVERING" || delivery.status === "SAVED") {
                    var isInError = undefined;
                    var sinceLastModif = Date.now() - (new Date(delivery.lastModifiedDate)).getTime();
                    var hours = Math.floor(sinceLastModif / 3600000);
                    if (hours > 24) {
                        if ($scope.digitalDocuments[delivery.label] && $scope.digitalDocuments[delivery.label].length > 0) {
                            // on tente de detecter une anomalie de livraison non trappée ...
                            isInError = _.find($scope.digitalDocuments[delivery.label], function (doc) {
                                // doc en livraison depuis + de 24h => erreur
                                return doc.status === "CREATING" || doc.status === "DELIVERING";
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
            });

        }


        function displayPayment(payment) {
            return $scope.options.payment[payment] || payment;
        }

        function displayMethod(method) {
            return $scope.options.method[method] || method;
        }
    }
})();
