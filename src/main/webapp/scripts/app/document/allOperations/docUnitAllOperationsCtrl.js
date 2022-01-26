(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('DocUnitAllOperationsCtrl', DocUnitAllOperationsCtrl);

    function DocUnitAllOperationsCtrl($q, $routeParams, $scope, $timeout, gettext,
        codeSrvc, LockSrvc, MessageSrvc, NumaHopInitializationSrvc) {

        var self = this;
        $scope.semCodes = codeSrvc;
        self.cancel = cancel;
        self.saveEntity = saveEntity;
        self.onchangeProject = onchangeProject;
        self.options = [];
        self.options.pacs = [];
        self.options.ia = [];
        self.options.projects = [];
        self.options.lots = [];
        self.options.trains = [];
        self.libraryId = undefined;

        /**
         * accordions
         */
        self.accordions = {
            archiv: true,
            distrib: true,
            control: true,
            other: true
        };


        self.init = function (parentCtrl) {
            LockSrvc.applyOnCtrl(self, $scope, "entityForm", gettext("L'unité documentaire est verrouillée par {{name}} jusqu'à {{date}}"));

            self.parent = parentCtrl;
            self.currentTab = parentCtrl.tabs.DOCUNIT;
            self.docUnitId = $routeParams.identifier;

            self.iaArchived = parentCtrl.iaArchived;
            self.cinesArchived = parentCtrl.cinesArchived;
            self.facileResults = parentCtrl.facileResults;
            self.dateFacileResults = parentCtrl.dateFacileResults;
            self.omekaDistribStatus = parentCtrl.omekaDistribStatus;

            self.libraryId = parentCtrl.docUnit.library.identifier;

            loadOptions(parentCtrl.docUnit.library.identifier,
                parentCtrl.docUnit.project ? parentCtrl.docUnit.project.identifier : null);

            afterLoadingEntity(parentCtrl.docUnit);
        };


        function loadOptions(libraryId, projectId) {
            var omekaConf;
            if(self.parent.docUnit.omekaConfiguration != null){
                omekaConf = self.parent.docUnit.omekaConfiguration.identifier;
            }
            $q.all([NumaHopInitializationSrvc.loadPACS(libraryId),
            NumaHopInitializationSrvc.loadCollections(libraryId),
            NumaHopInitializationSrvc.loadProjects(libraryId),
            NumaHopInitializationSrvc.loadLots(libraryId, projectId),
            NumaHopInitializationSrvc.loadTrains(libraryId, projectId),
            NumaHopInitializationSrvc.loadOmekaCollections(libraryId, projectId),
            NumaHopInitializationSrvc.loadOmekaItems(libraryId),

            NumaHopInitializationSrvc.loadOcrLanguagesForLibrary(libraryId)])
                .then(function (data) {
                    // PAC
                    _.each(data[0], function (d) {
                        delete d.library;
                    });
                    self.options.pacs = data[0];

                    // IA
                    _.each(data[1], function (d) {
                        delete d.library;
                    });
                    self.options.ia = data[1];
                    // Projets
                    self.options.projects.length = 0;
                    _.each(data[2], function (d) {
                        delete d.library;
                        if (d.status !== 'CLOSED') {
                            self.options.projects.push(d);
                        }
                    });
                    // Lots
                    self.options.lots.length = 0;
                    _.each(data[3], function (l) {
                        if ((l.type === 'PHYSICAL' && l.status === 'CREATED')
                            || (self.entity.lot && self.entity.lot.identifier === l.identifier)) {
                            self.options.lots.push(l);
                        }
                    });
                    // Trains
                    self.options.trains.length = 0;
                    _.each(data[4], function (t) {
                        delete t.project;
                        delete t.physicalDocuments;
                        self.options.trains.push(t);
                    });

                    self.options.omekaCollections = data[5];
                    self.options.omekaItems = data[6];
                    self.options.languagesOcr = data[7];
                });
        }


        /**
         * Annulation de l'édition du formulaire
         */
        function cancel() {
            self.unlock(self.entity);
            if ($scope.entityForm) {
                $scope.entityForm.$cancel();
            }
        }

        /****************************************************************/
        /** Fonctions ***************************************************/
        /****************************************************************/
        /** Sauvegarde une entité **/
        function saveEntity(entity) {
            delete self.errors;

            $timeout(function () {
                entity.$save({},
                    function (value) {
                        MessageSrvc.addSuccess(gettext("L'unité documentaire {{name}} a été sauvegardée"), { name: value.label });
                        self.unlock(entity);
                        onSuccess(value);
                    },
                    function (response) {
                        if (response.data.type !== "PgcnLockException") {
                            self.errors = _.chain(response.data.errors)
                                .groupBy("field")
                                .mapObject(function (list) {
                                    return _.pluck(list, "code");
                                })
                                .value();

                            openForm();
                        }
                    });
            });
        }
        // Ouverture du formulaire et des sous formulaires
        function openForm() {
            $timeout(function () {
                if (angular.isDefined($scope.entityForm)) {
                    $scope.entityForm.$show();
                }
            });
        }
        // Gestion de l'entité renvoyée par le serveur
        function onSuccess(value) {
            self.entity = value;

            displayMessages();

            if (self.entity.records.length > 0) {
                self.parent.recordId = self.entity.records[0].identifier;
            }
        }

        function displayMessages() {
            // On commence par vider les messages précédents...
            MessageSrvc.clearMessages();
            // Affichage pour un temps limité à l'ouverture
            MessageSrvc.initPanel();
        }

        // Initialisation une fois qu'on a reçu toutes les données du serveur
        function loadAll(value) {
            onSuccess(value);
            self.loaded = true;
        }

        function afterLoadingEntity(entity) {
            loadAll(entity);
        }

        /**
         * Réinitialisation de la liste de lots au changement de projet
         *
         * @param {any} newValue
         */
        function onchangeProject(newValue) {
            var newProj = newValue || null;
            loadOptions(self.libraryId, newProj.identifier);
        }

    }
})();
