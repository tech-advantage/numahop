(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('TrainEditCtrl', TrainEditCtrl);

    function TrainEditCtrl($location, $q, $routeParams, $scope, $timeout, $http, FileSaver, codeSrvc,
        DocUnitBaseService, gettext, gettextCatalog, HistorySrvc, ListTools, MessageSrvc,
        ModalSrvc, NumaHopInitializationSrvc, PhysicalDocumentSrvc, TrainSrvc, ValidationSrvc) {

        $scope.cancel = cancel;
        $scope.delete = deleteTrain;
        $scope.displayBoolean = DocUnitBaseService.displayBoolean;
        $scope.duplicate = duplicate;
        $scope.goToAllOperations = goToAllOperations;
        $scope.saveTrain = saveTrain;
        $scope.semCodes = codeSrvc;
        $scope.validation = ValidationSrvc;
        $scope.downloadSlip = downloadSlip;
        $scope.fromReject = false;

        // Définition des listes déroulantes
        $scope.options = {
            projects: [],
            boolean: {
                "true": gettextCatalog.getString('Oui'),
                "false": gettextCatalog.getString('Non')
            },
            status: TrainSrvc.config.status
        };

        /**
         * accordions
         */
        $scope.accordions = {
            train: true,
            physDoc: false
        };

        init();


        /** Initialisation */
        function init() {
            $scope.loaded = false;
            loadProjectSelect().then(loadTrain);

            // callback sur les listes d'UD
            if ($routeParams.callback) {
                $scope.saveCallback = function (projId, trainId) {
                    var params = {};
                    if (projId) {
                        params["project"] = projId;
                    }
                    if (trainId) {
                        params["train"] = trainId;
                    }
                    $location.path($routeParams.callback).search(params);
                };
            }
        }

        /**
         * Chargement des documents
         */
        function loadPhysDocs(trainId) {
            return PhysicalDocumentSrvc.query({ train: trainId });
        }

        function loadPhysDocsByDocUnits(docUnitIds) {
            var deferred = $q.defer();
            $timeout(function () {
                var promise = PhysicalDocumentSrvc.loadByDocUnitIds({ docUnitIds: docUnitIds }).$promise;
                promise.then(function (value) {
                    $scope.physDocs = value;
                    deferred.resolve(value);
                }).catch(function (value) {
                    deferred.reject(value);
                });
            });
            return deferred.promise;
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

        function loadTrain() {
            if ('duplicate' in $routeParams && angular.isDefined($routeParams.id)) {
                // Duplication
                $scope.train = TrainSrvc.duplicate({
                    id: $routeParams.id
                }, function (train) {
                    afterLoadingTrain(train);
                });
            } else if (angular.isDefined($routeParams.id)) {
                // Chargement train
                $scope.train = TrainSrvc.get({
                    id: $routeParams.id
                }, function (train) {
                    afterLoadingTrain(train);
                    $scope.physDocs = loadPhysDocs($routeParams.id);
                });
            } else if (angular.isDefined($routeParams.new)) {

                 // Création d'un nouveau train
                    HistorySrvc.add(gettext("Nouveau train"));
                    $scope.train = new TrainSrvc();
                    $scope.train.active = true;
                    $scope.train.category = "OTHER";

                    if ($routeParams.project) {
                        $scope.train.project = _.find($scope.sel2Projects, function (pj) {
                            return pj.identifier === $routeParams.project;
                        });
                    }
                    // Train de renumerisation (créé depuis les rejets de l'ecran Controles)
                    // on affiche direct les docs
                    if ($routeParams.docs) {
                        loadPhysDocsByDocUnits($routeParams.docs);
                    }

                    if (angular.isDefined($routeParams.fromReject)) {
                        $scope.fromReject = true;
                    }

                    afterLoadingTrain($scope.train);
                    openForm();
            }
        }

        // Initialisation une fois qu'on a reçu toutes les données du serveur
        function afterLoadingTrain(train) {
            onSuccess(train);
            $scope.loaded = true;
        }

        /****************************************************************/
        /** Actions *****************************************************/
        /****************************************************************/
        function duplicate() {
            if ($scope.train) {
                $scope.loaded = false;
                $scope.train._selected = false;
                var identifier = $scope.train.identifier;
                $scope.train = null;
                $location.path("/train/train").search({ id: identifier, mode: "edit", duplicate: true });
            }
        }

        function cancel() {
            if ($scope.saveCallback) {
                $scope.saveCallback();
            }
            else {
                $scope.trainForm.$cancel();
            }
        }

        function goToAllOperations() {
            $location.path("/train/all_operations");
        }

        /****************************************************************/
        /** Fonctions ***************************************************/
        /****************************************************************/
        // Sauvegarde un train
        function saveTrain(train) {
            delete $scope.errors;
            $timeout(function () {
                var creation = angular.isUndefined(train.identifier) || train.identifier === null;
                if ($scope.fromReject) {
                    train.physicalDocuments = $scope.physDocs;
                } else {
                    train.physicalDocuments = [];
                }

                train.$save({},
                    function (value) {
                        // Si un callback est défini, on l'appelle
                        if ($scope.saveCallback) {
                            $scope.saveCallback(value.project.identifier, value.identifier);
                        }
                        // Sinon on rafraichit l'affichage
                        else {
                            MessageSrvc.addSuccess(gettext("Le train {{label}} a été sauvegardé"), { label: $scope.train.label });
                            onSuccess(value);
                            // si création, on ajoute à la liste, sinon, on essaye de MAJ les infos dans la colonne du milieu
                            if (creation) {
                                $scope.clearSelection();
                                addNewTrainToList(value, $scope.newTrains, $scope.pagination.items);
                                $location.search({ id: value.identifier }); // suppression des paramètres
                            } else {
                                $scope.updateTrain($scope.train.identifier, $scope.train);
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
        // Met à jour la liste avec le nouvel train
        function addNewTrainToList(train, newTrains, trains) {
            var found = _.find(newTrains, function (b) {
                return b.identifier === train.identifier;
            });
            if (found) {
                /** Si cet train existe déjà, ne rien faire */
                return;
            }
            found = _.find(trains, function (b) {
                return b.identifier === train.identifier;
            });
            if (found) {
                /** Si cet train existe déjà, ne rien faire */
                return;
            }

            var newTrain = {
                _selected: true,
                identifier: train.identifier,
                label: train.label
            };
            newTrains.push(newTrain);
        }

        // Gestion de train renvoyé par le serveur
        function onSuccess(value) {
            $scope.train = value;

            HistorySrvc.add(gettextCatalog.getString("Train {{label}}", $scope.train));

            displayMessages($scope.train);
        }

        /**
         * Suppression d'un train
         *
         * @param {any} train
         */
        function deleteTrain(train) {
            ModalSrvc.confirmDeletion(gettextCatalog.getString("le train {{label}}", train))
                .then(function () {
                    train.$delete(function (value) {
                        MessageSrvc.addSuccess(gettext("Le train {{label}} a été supprimé"), train);
                        ListTools.findAndRemoveItemFromLists(train, $scope.pagination.items, $scope.newTrains);
                        $location.search({}); // suppression des paramètres
                    });
                });
        }

        // Ouverture du formulaire et des sous formulaires
        function openForm() {
            $timeout(function () {
                if (angular.isDefined($scope.trainForm)) {
                    $scope.trainForm.$show();
                }
            });
        }

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

        /**
         * Téléchargement du bordereau de livraison
         */
        function downloadSlip(format) {

            var url = 'api/rest/train/' + format + '/' + $scope.train.identifier;
            // on met la réponse dans un arraybuffer pour conserver l'encodage original dans le fichier sauvegardé
            $http.get(url, { responseType: 'arraybuffer' })
                .then(function (response) {
                    var filename = "bordereau." + format;
                    var blob = new Blob([response.data], { type: response.headers("content-type") });
                    FileSaver.saveAs(blob, filename);
                });
        }
    }
})();
