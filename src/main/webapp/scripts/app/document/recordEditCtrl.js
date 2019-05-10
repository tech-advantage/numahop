(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('RecordEditCtrl', RecordEditCtrl);

    function RecordEditCtrl($location, $q, $routeParams, $scope, $timeout, codeSrvc,
        gettext, gettextCatalog, HistorySrvc, ListTools, LockSrvc, Principal, NumahopEditService,
        MessageSrvc, ModalSrvc, ValidationSrvc, RecordSrvc, NumaHopInitializationSrvc, WorkflowSrvc) {

        $scope.addProperty = addProperty;
        $scope.backToList = backToList;
        $scope.cancel = cancel;
        $scope.delete = deleteEntity;
        $scope.deleteProperty = deleteProperty;
        $scope.duplicate = duplicate;
        $scope.getPropertyType = getPropertyType;
        $scope.saveEntity = saveEntity;
        $scope.selectDocUnit = selectDocUnit;
        $scope.semCodes = codeSrvc;
        $scope.validation = ValidationSrvc;
        $scope.validateRecord = validateRecord;

        $scope.setPropertyFieldType = setPropertyFieldType;

        // Définition des listes déroulantes
        $scope.options = {
            propertyType: {}
        };

        /**
         * visibilité des types de propriétés
         */
        $scope.showType = {
            dc: true,
            dcq: true,
            custom: true
        };

        $scope.loaded = false;
        $scope.canRecordBeValidated = false;

        init();


        /****************************************************************/
        /** Initialisation **********************************************/
        /****************************************************************/
        function init() {
            LockSrvc.applyOnScope($scope, "entityForm", gettext("La notice est verrouillée par {{name}} jusqu'à {{date}}"));

            loadOptions();
        }

        function loadEntity() {
            // Édition
            if (angular.isDefined($routeParams.id)) {
                /** Chargement de l'entité **/
                $scope.entity = RecordSrvc.get({
                    id: $routeParams.id
                }, function (entity) {
                    afterLoadingEntity(entity);
                });
            }
            // Création d'une nouvelle entité
            else if ($routeParams.new) {
                HistorySrvc.add(gettext("Nouvelle notice"));
                $scope.entity = new RecordSrvc();
                $scope.entity.properties = [];

                Principal.identity().then(function (currentUser) {
                    if (angular.isDefined(currentUser)) {
                        $scope.entity.library = $scope.sel2Libraries.find(function (v) {
                            return v.identifier === currentUser.library;
                        });
                    }
                });

                afterLoadingEntity($scope.entity);
                openForm();
            }
        }

        function loadOptions() {
            $q.all([NumaHopInitializationSrvc.loadDocPropertyTypes(), NumaHopInitializationSrvc.loadLibraries()])
                .then(function (data) {
                    var propertyTypes = data[0];
                    $scope.sel2Libraries = data[1];
                    $scope.options.propertyType.list = {};
                    _.each(propertyTypes, function (type) {
                        $scope.options.propertyType.list[type.identifier] = type.label;
                    });
                    $scope.options.propertyType.dc = [];
                    $scope.options.propertyType.dcq = [];
                    $scope.options.propertyType.custom = [];
                    _.each(propertyTypes, function (type) {
                        switch (type.superType) {
                            case "DC": NumahopEditService.insertBasedOnRank($scope.options.propertyType.dc, type);
                                break;
                            case "DCQ": NumahopEditService.insertBasedOnRank($scope.options.propertyType.dcq, type);
                                break;
                            default: NumahopEditService.insertBasedOnRank($scope.options.propertyType.custom, type);
                        }
                    });
                    loadEntity();
                });
        }

        /****************************************************************/
        /** Actions *****************************************************/
        /****************************************************************/
        function deleteEntity(entity) {
            ModalSrvc.confirmDeletion(gettextCatalog.getString("la notice {{title}}", entity))
                .then(function () {

                    entity.$delete(function (value) {
                        MessageSrvc.addSuccess(gettext("La notice {{title}} a été supprimée"), value);

                        var removed = ListTools.findAndRemoveItemFromList(entity, $scope.pagination.items);
                        if (removed) {
                            $scope.pagination.totalItems--;
                        }
                        else {
                            ListTools.findAndRemoveItemFromList(entity, $scope.newEntities);
                        }
                        $scope.backToList();
                    });
                });
        }
        function backToList() {
            $scope.loaded = false;
            // supprimer tous les paramètres
            $location.search({});
            $location.path("/document/record");
        }
        /****************************************************************/
        /** Propriétés **************************************************/
        /****************************************************************/
        function addProperty(entity, type) {
            if (angular.isUndefined(entity.properties)) {
                entity.record[type] = [];
            }
            var property;
            switch (type) {
                case "dc":
                    property = {
                        "_name": "propertyFormDC" + $scope.indices[type]++,
                        "type": undefined,
                        "record": { identifier: entity.identifier }
                    };
                    break;
                case "dcq":
                    property = {
                        "_name": "propertyFormDCQ" + $scope.indices[type]++,
                        "type": undefined,
                        "record": { identifier: entity.identifier }
                    };
                    break;
                case "custom":
                    property = {
                        "_name": "propertyFormCUSTOM" + $scope.indices[type]++,
                        "type": undefined,
                        "record": { identifier: entity.identifier }
                    };
                    break;
            }

            entity.record[type].push(property);
            entity.properties.push(property);
        }
        function deleteProperty(entity, type, property) {
            removeElement(entity.properties, property);
            removeElement(entity.record[type], property);
        }
        function removeElement(elements, element) {
            var idx = elements.indexOf(element);
            if (idx >= 0) {
                elements.splice(idx, 1);
            }
        }
        function getPropertyType(property) {
            if (angular.isUndefined(property.identifier)) {
                return gettext("Nouvelle propriété");
            } else {
                return gettext(property.type.label);
            }
        }
        function selectDocUnit() {
            var params = { multiple: false };
            if ($scope.entity.docUnit) {
                params.disabled = $scope.entity.docUnit.identifier;
            }
            if ($scope.entity.library) {
                params.library = $scope.entity.library.identifier;
            }
            ModalSrvc.selectDocUnit(params)
                .then(function (selection) {
                    if (selection.length > 0) {
                        $scope.entity.docUnit = selection[0];
                    }
                });
        }

        /****************************************************************/
        /** Fonctions ***************************************************/
        /****************************************************************/
        function cancel() {
            $scope.unlock($scope.entity);
            if ($scope.entityForm) {
                $scope.entityForm.$cancel();
            }
        }

        /** Sauvegarde une entité **/
        function saveEntity(entity) {
            delete $scope.errors;

            $timeout(function () {
                var creation = angular.isUndefined(entity.identifier) || entity.identifier === null;
                mergePropertiesBeforeSave(entity);

                entity.$save({},
                    function (value) {

                        // warning language pour export cines.
                        if (value.errors && value.errors[0] && value.errors[0].code === 'RECORD_LANGUAGE_UNKNOWN') {
                            MessageSrvc.addWarn("{{msge}} ", { msge: value.errors[0].message }, false, 10000);
                            value.errors = [];
                        }
                        MessageSrvc.addSuccess(gettext("La notice {{name}} a été sauvegardée"), { name: value.title });
                        initPropertiesBasedOnTypeAndRank(value);
                        onSuccess(value);

                        // si création, on ajoute à la liste, sinon, on essaye de MAJ les infos dans la colonne du milieu
                        if (creation) {
                            $scope.clearSelection();
                            NumahopEditService.addNewEntityToList(value, $scope.newEntities, $scope.pagination.items, ["title"]);
                            $location.search({ id: value.identifier });
                        } else {
                            $scope.unlock(entity);
                            NumahopEditService.updateMiddleColumn($scope.entity, ["title"],
                                $scope.pagination.items, $scope.newEntities);
                        }
                    },
                    function (response) {
                        if (response.data.type !== "PgcnLockException") {
                            $scope.errors = _.chain(response.data.errors)
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

        /**
         * Validation workflow (notice)
         */
        function validateRecord() {
            if (angular.isDefined($scope.entity.docUnit)) {
                WorkflowSrvc.process({ docUnitId: $scope.entity.docUnit.identifier, key: 'VALIDATION_NOTICES' }).$promise
                    .then(function () {
                        MessageSrvc.addSuccess(gettextCatalog.getString("L'étape {{name}} a été validée"), { name: $scope.semCodes['workflow.VALIDATION_NOTICES'] });
                        $scope.canRecordBeValidated = false;
                    });
            }
        }

        /**
         * Duplication d'une entité
         * 
         * @param {any} entity 
         */
        function duplicate(entity) {
            entity.$duplicate()
                .then(function (dupl) {
                    $scope.clearSelection();
                    NumahopEditService.addNewEntityToList(dupl, $scope.newEntities, $scope.pagination.items, ["title"]);
                    $location.search({ id: dupl.identifier });
                });
        }

        // Gestion de l'entité renvoyée par le serveur
        function onSuccess(value) {
            $scope.entity = value;

            HistorySrvc.add(gettextCatalog.getString("Notice {{value}}", $scope.entity));

            displayMessages($scope.entity);
            initForms();
        }
        // Ouverture du formulaire et des sous formulaires
        function openForm() {
            $timeout(function () {
                if (angular.isDefined($scope.entityForm)) {
                    $scope.entityForm.$show();
                }
            });
        }
        function initForms() {
            $scope.indices = {};
            initForm($scope.entity.record.dc, "propertyFormDC", "dc");
            initForm($scope.entity.record.dcq, "propertyFormDCQ", "dcq");
            initForm($scope.entity.record.custom, "propertyFormCUSTOM", "custom");
        }
        function initForm(elements, formPrefix, indexName) {
            _.each(elements, function (element, idx) {
                element._name = formPrefix + idx;
            });
            $scope.indices[indexName] = angular.isDefined(elements) ? elements.length : 0;
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

        // Initialisation une fois qu'on a reçu toutes les données du serveur
        function loadAll(value) {
            onSuccess(value);
            $scope.loaded = true;
            if (angular.isDefined(value.docUnit)) {
                WorkflowSrvc.canProcess({ docUnit: value.docUnit.identifier, key: 'VALIDATION_NOTICES' }).$promise
                    .then(function (result) {
                        if (result.value) {
                            $scope.canRecordBeValidated = true;
                        }
                    });
            }

        }

        function afterLoadingEntity(entity) {
            initPropertiesBasedOnTypeAndRank(entity);
            loadAll(entity);
        }

        function initPropertiesBasedOnTypeAndRank(entity) {
            entity.record = {};
            entity.record.dc = [];
            entity.record.dcq = [];
            entity.record.custom = [];
            _.each(entity.properties, function (property) {
                switch (property.type.superType) {
                    case "DC": NumahopEditService.insertBasedOnRank(entity.record.dc, property, "weightedRank");
                        break;
                    case "DCQ": NumahopEditService.insertBasedOnRank(entity.record.dcq, property, "weightedRank");
                        break;
                    default: NumahopEditService.insertBasedOnRank(entity.record.custom, property, "weightedRank");
                }
            });
        }

        function mergePropertiesBeforeSave(entity) {
            var newProperties = [];
            // DC
            newProperties = addNewProperties(newProperties, entity.record.dc, entity.properties);
            // DCQ
            newProperties = addNewProperties(newProperties, entity.record.dcq, entity.properties);
            // Custom
            newProperties = addNewProperties(newProperties, entity.record.custom, entity.properties);

            _.each(newProperties, function (property) {
                entity.properties.push(property);
            });
            delete entity.properties.record;
        }

        function addNewProperties(newProperties, listToSearch, propertyList) {
            for (var i in listToSearch) {
                var alreadyHere = false;
                for (var j in propertyList) {
                    if (propertyList[j].identifier === listToSearch[i].identifier) {
                        alreadyHere = true;
                        break;
                    }
                }
                if (!alreadyHere) {
                    newProperties.push(listToSearch[i]);
                }
            }
            return newProperties;
        }

        function setPropertyFieldType(type, property) {
            if (type.identifier === "description") {
                property._size = "textarea";
            } else {
                property._size = "";
            }
        }
    }
})();
