(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('WorkflowModelEditCtrl', WorkflowModelEditCtrl);

    function WorkflowModelEditCtrl($location, $q, $routeParams, $scope, $timeout, codeSrvc,
        gettext, gettextCatalog, HistorySrvc, ListTools, Principal, NumahopEditService,
        DocUnitBaseService, MessageSrvc, ModalSrvc, ValidationSrvc, WorkflowModelSrvc, NumaHopInitializationSrvc) {

        $scope.codes = codeSrvc;
        $scope.validation = ValidationSrvc;
        $scope.fullName = getFullName;
        $scope.onChangeLibrary = onChangeLibrary;
        $scope.saveEntity = saveEntity;
        $scope.loaded = false;
        $scope.displayBoolean = DocUnitBaseService.displayBoolean;

        $scope.canvas = angular.element('#workflowModelCanvas');
        $scope.stateChoices = [];
        $scope.editState = editState;
        $scope.getType = getType;
        $scope.getState = getState;
        $scope.stateExist = stateExist;

        $scope.options = {
            booleanValue: [
                { value: true, text: gettextCatalog.getString('Oui') },
                { value: false, text: gettextCatalog.getString('Non') }
            ]
        };

        init();


        /****************************************************************/
        /** Initialisation **********************************************/
        /****************************************************************/
        function init() {
            loadOptions();

            // Message d'avertissement si l'utilisateur quitte la page alors que des modifications sont en cours
            $scope.$on("$locationChangeStart", checkModificationsOnLocationChange);
        }

        function loadEntity() {
            if (angular.isDefined($routeParams.id)) {
                /** Chargement de l'entité **/
                $scope.model = WorkflowModelSrvc.get({
                    id: $routeParams.id
                }, function (entity) {
                    afterLoadingEntity(entity);
                });
            } else if (angular.isDefined($routeParams.new)) {
                /** Création d'une nouvelle entité **/
                HistorySrvc.add(gettext("Nouveau modèle de workflow"));
                $scope.model = new WorkflowModelSrvc();
                $scope.model.active = true;
                initStates();
                var currentUser = Principal.identity();
                if (angular.isDefined(currentUser)) {
                    currentUser.then(function (result) {
                        $scope.model.library = $scope.sel2Libraries.find(function (v) {
                            return v.identifier === result.library;
                        });
                    });
                }
                afterLoadingEntity($scope.model);
                openForm();
            }
        }

        function loadOptions() {
            var currentUser = Principal.identity();
            if (angular.isDefined(currentUser)) {
                currentUser.then(function (result) {
                    if (result.library) {
                        $q.all([NumaHopInitializationSrvc.loadLibraries(), NumaHopInitializationSrvc.loadWorkflowGroups(result.library)])
                            .then(function (data) {
                                $scope.sel2Libraries = data[0];
                                $scope.sel2Groups = data[1];
                                loadEntity();
                            });
                    } else {
                        $q.all([NumaHopInitializationSrvc.loadLibraries()])
                            .then(function (data) {
                                $scope.sel2Libraries = data[0];
                                loadEntity();
                            });
                    }
                });
            }
        }

        function initStates() {
            $scope.model.states = [];
            $scope.model.states.push({ key: "INITIALISATION_DOCUMENT", type: "OTHER" });
            $scope.model.states.push({ key: "CLOTURE_DOCUMENT", type: "OTHER" });
            $scope.model.states.push({ key: "ARCHIVAGE_DOCUMENT", type: "TO_SKIP" });
            $scope.model.states.push({ key: "CONSTAT_ETAT_APRES_NUMERISATION", type: "TO_SKIP" });
            $scope.model.states.push({ key: "CONSTAT_ETAT_AVANT_NUMERISATION", type: "TO_SKIP" });            
            $scope.model.states.push({ key: "DIFFUSION_DOCUMENT", type: "TO_SKIP" });
            $scope.model.states.push({ key: "DIFFUSION_DOCUMENT_OMEKA", type: "TO_SKIP" });
            $scope.model.states.push({ key: "DIFFUSION_DOCUMENT_DIGITAL_LIBRARY", type: "TO_SKIP" });
            $scope.model.states.push({ key: "DIFFUSION_DOCUMENT_LOCALE", type: "TO_SKIP" });
            $scope.model.states.push({ key: "GENERATION_BORDEREAU", type: "TO_SKIP" });
            $scope.model.states.push({ key: "PREREJET_DOCUMENT", type: "TO_SKIP" });
            $scope.model.states.push({ key: "PREVALIDATION_DOCUMENT", type: "TO_SKIP" });
            $scope.model.states.push({ key: "VALIDATION_BORDEREAU_CONSTAT_ETAT", type: "TO_SKIP" });
            $scope.model.states.push({ key: "CONTROLES_AUTOMATIQUES_EN_COURS", type: "TO_WAIT" });
            $scope.model.states.push({ key: "LIVRAISON_DOCUMENT_EN_COURS", type: "REQUIRED" });
            $scope.model.states.push({ key: "NUMERISATION_EN_ATTENTE", type: "TO_WAIT" });
            $scope.model.states.push({ key: "RAPPORT_CONTROLES", type: "TO_WAIT" });
            $scope.model.states.push({ key: "CONTROLE_QUALITE_EN_COURS", type: "REQUIRED" });
            $scope.model.states.push({ key: "VALIDATION_CONSTAT_ETAT", type: "TO_SKIP" });
            $scope.model.states.push({ key: "VALIDATION_DOCUMENT", type: "REQUIRED" });
            $scope.model.states.push({ key: "VALIDATION_NOTICES", type: "REQUIRED" });
            
        }

        /****************************************************************/
        /** Actions *****************************************************/
        /****************************************************************/
        $scope.save = function () {
            if (angular.isDefined($scope.entityForm)) {
                $scope.entityForm.$submit();
            }
        };
        $scope.delete = function (entity) {
            ModalSrvc.confirmDeletion(entity.name)
                .then(function () {
                    entity.$delete(function (value) {
                        MessageSrvc.addSuccess(gettext("Le modèle de workflow {{id}} a été supprimé"), { id: value.name });
                        $location.search({});
                        $scope.entityForm.$cancel();
                        var removed = ListTools.findAndRemoveItemFromList(entity, $scope.pagination.items);
                        if (!removed) {
                            ListTools.findAndRemoveItemFromList(entity, $scope.newEntities);
                        }
                        $scope.modified = false;
                    });
                });
        };
        $scope.cancel = function () {
            if ($scope.saveCallback) {
                $scope.saveCallback();
            }
            else {
                $scope.entityForm.$cancel();
            }
        };

        /****************************************************************/
        /** Liens *******************************************************/
        /****************************************************************/

        function editState(model, key) {
            return ModalSrvc.editModelState(angular.copy(_.where(model.states, { key: key })[0]), $scope.sel2Groups)
                .then(function (editedState) {
                    $scope.modified = true;
                    _.extend(_.findWhere(model.states, { key: editedState.key }), editedState);
                    openForm();
                    return editedState;
                });
        }

        /**
         * Retourne le type d'étape
         */
        function getType(model, key) {
            if (angular.isDefined(model) && model !== null) {
                var state = _.where(model.states, { key: key })[0];
                if (angular.isDefined(state)) {
                    return state.type;
                }
            }
            return "OTHER";
        }

        /**
         * Détermine si l'étape est correctement complétée au besoin
         */
        function getState(model, key) {
            if (angular.isDefined(model) && model !== null) {
                var state = _.where(model.states, { key: key })[0];
                if (angular.isDefined(state)) {
                    if (state.type === "REQUIRED" && angular.isDefined(state.group) && state.group !== null) {
                        return "STATE_OK";
                    } else if (state.type === "REQUIRED") {
                        return "STATE_KO";
                    }
                }
            }
            return "";
        }

        /****************************************************************/
        /** Fonctions ***************************************************/
        /****************************************************************/
        function onChangeLibrary(library) {
            $q.all([NumaHopInitializationSrvc.loadWorkflowGroups(library.identifier)])
                .then(function (data) {
                    $scope.sel2Groups = data[0];
                });
        }
        function getFullName(user) {
            return user.surname + " " + user.firstname;
        }
        /** Sauvegarde une entité **/
        function saveEntity(entity) {
            delete $scope.errors;
            $timeout(function () {
                var creation = angular.isUndefined(entity.identifier) || entity.identifier === null;

                entity.$save({},
                    function (value) {
                        MessageSrvc.addSuccess(gettext("Le modèle de workflow {{name}} a été sauvegardé"), { name: value.name });
                        onSuccess(value);
                        // si création, on ajoute à la liste, sinon, on essaye de MAJ les infos dans la colonne du milieu
                        if (creation) {
                            $scope.clearSelection();
                            NumahopEditService.addNewEntityToList(value, $scope.newEntities, $scope.pagination.items, ["name"]);
                            $location.search({ id: value.identifier }); // suppression des paramètres
                        } else {
                            NumahopEditService.updateMiddleColumn($scope.model, ["name"],
                                $scope.pagination.items, $scope.newEntities);
                        }
                    },
                    function (response) {
                        $scope.errors = _.chain(response.data.errors)
                            .groupBy("field")
                            .mapObject(function (list) {
                                return _.pluck(list, "code");
                            })
                            .value();
                        if (creation){
                            
                        }
                        openForm();
                    });
            });
        }
        // Gestion de l'entité renvoyée par le serveur
        function onSuccess(value) {
            $scope.model = value;
            $scope.modified = false;
            if ((!$scope.sel2Groups || $scope.sel2Groups.length === 0) && $scope.model.library) {
                $q.all([NumaHopInitializationSrvc.loadWorkflowGroups($scope.model.library.identifier)])
                    .then(function (data) {
                        $scope.sel2Groups = data[0];
                    });
            }

            HistorySrvc.add(gettextCatalog.getString("Modèle de workflow") + ": " + $scope.model.name);

            displayMessages($scope.model);
        }
        // Ouverture du formulaire et des sous formulaires
        function openForm() {
            $timeout(function () {
                if (angular.isDefined($scope.entityForm)) {
                    $scope.entityForm.$show();
                }
            });
        }
        // Initialisation des formulaires
        function displayMessages(entity) {
            // On commence par vider les messages précédents...
            MessageSrvc.clearMessages();

            // ... puis on affiche les infos de création ...
            if (angular.isDefined(entity.createdDate)) {
                var dateCreated = new Date(entity.createdDate);
                MessageSrvc.addInfo("Créé le {{date}}",
                    { date: dateCreated.toLocaleString() }, true);
            }
            // ... puis on affiche les infos de modification ...
            if (angular.isDefined(entity.lastModifiedDate)) {
                var dateModif = new Date(entity.lastModifiedDate);
                MessageSrvc.addInfo("Dernière modification le {{date}} par {{author}}",
                    { date: dateModif.toLocaleString(), author: entity.lastModifiedBy }, true);
            }

            // Affichage pour un temps limité à l'ouverture
            MessageSrvc.initPanel();
        }

        // Initialisation une fois qu'on a reçu toutes les données du serveur
        function loadAll(value) {
            onSuccess(value);
            $scope.loaded = true;
            $scope.modified = false;
        }

        function afterLoadingEntity(entity) {
            loadAll(entity);
        }

        /** Validation */

        // Clean
        $scope.$on("$destroy", function () {
        });

        /**
         * Vérifications
         */
        /**
         * Contrôle si des modifications sont en cours, et alerte l'utilisateur
         * sur un évènement ($locationChangeStart)
         * 
         * @param {any} event 
         */
        function checkModificationsOnLocationChange(event) {
            // Dans le cas d'une restauration sans modification, on prévient l'utilisateur s'il change de page
            if ($scope.modified) {
                var url = $location.url();

                // Annulation de l'action en cours, dans l'attente d'une confirmation de l'utilisateur
                event.preventDefault();

                ModalSrvc.confirmAction(gettextCatalog.getString("continuer alors que vous avez des modifications non sauvegardées"))
                    .then(function () {
                        $scope.modified = false;

                        // on force le changement de page
                        $location.url(url);
                    });
            }
        }

        function stateExist(model, key){
            return _.where(model.states, { key: key }).length > 0;
        }
    }
})();
