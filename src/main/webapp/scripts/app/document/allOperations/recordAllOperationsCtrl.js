(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('RecordAllOperationsCtrl', function ($location, $q, $routeParams, $scope, $timeout, gettext,
            LockSrvc, MessageSrvc, ModalSrvc, NumahopEditService, NumaHopInitializationSrvc, RecordSrvc) {

            var self = this;
            self.addProperty = addProperty;
            self.browseRecord = browseRecord;
            self.cancelRecord = cancelRecord;
            self.deleteProperty = deleteProperty;
            self.getPropertyType = getPropertyType;
            self.loadRecord = loadRecord;
            self.newRecord = newRecord;
            self.saveEntity = saveEntity;
            self.selectRecord = selectRecord;

            /**
             * visibilité des types de propriétés
             */
            self.showType = {
                dc: true,
                dcq: true,
                custom: true,
                custom_cines: true,
                custom_archive: true,
                custom_omeka: true,
            };

            loadOptions();

            /**
             * Chargement des notice de l'unité documentaire courante
             * 
             * @param {any} parentCtrl 
             * @returns 
             */
            function loadRecord(parentCtrl) {
                self.parentCtrl = parentCtrl;
                self.currentTab = parentCtrl.tabs.RECORD;

                if (angular.isDefined($routeParams.newRecord)) {
                    self.entity = new RecordSrvc();
                    self.entity.docUnit = { identifier: parentCtrl.docUnitId };
                    if (parentCtrl.docUnit.library) {
                        self.entity.library = { identifier: parentCtrl.docUnit.library.identifier };
                    }
                    self.entity.properties = [];
                    self.records = [self.entity];
                    afterLoadingEntity(self.entity);
                    openForm();
                }
                else {
                    self.records = RecordSrvc.allOperations({ identifier: parentCtrl.docUnitId });
                    self.records.$promise
                        .then(function (records) {
                            // Chargement de la notice passée en paramètre
                            if ($routeParams.record) {
                                var found = _.find(records, function (r) {
                                    return r.identifier === $routeParams.record;
                                });
                                if (angular.isDefined(found)) {
                                    selectRecord(found.identifier);
                                    return;
                                }
                            }
                            // Chargement de la 1e notice
                            if (records.length >= 1) {
                                selectRecord(records[0].identifier);
                            }
                            else {
                                self.loaded = true;
                            }
                        });
                }
            }

            function loadOptions() {
                LockSrvc.applyOnCtrl(self, $scope, "entityForm", gettext("La notice est verrouillée par {{name}} jusqu'à {{date}}"));

                NumaHopInitializationSrvc.loadDocPropertyTypes()
                    .then(function (data) {
                        self.sel2DocProperties = data;
                        self.sel2DocProperties.list = {};
                        _.each(self.sel2DocProperties, function (type) {
                            self.sel2DocProperties.list[type.identifier] = type.label;
                        });
                        self.sel2DocProperties.dc = [];
                        self.sel2DocProperties.dcq = [];
                        self.sel2DocProperties.custom = [];
                        self.sel2DocProperties.custom_cines = [];
                        self.sel2DocProperties.custom_archive = [];
                        self.sel2DocProperties.custom_omeka = [];
                        
                        _.each(self.sel2DocProperties, function (type) {
                            switch (type.superType) {
                                case "DC": NumahopEditService.insertBasedOnRank(self.sel2DocProperties.dc, type);
                                    break;
                                case "DCQ": NumahopEditService.insertBasedOnRank(self.sel2DocProperties.dcq, type);
                                    break;
                                case "CUSTOM_CINES": NumahopEditService.insertBasedOnRank(self.sel2DocProperties.custom_cines, type);
                                    break;
                                case "CUSTOM_ARCHIVE": NumahopEditService.insertBasedOnRank(self.sel2DocProperties.custom_archive, type);
                                    break;
                                case "CUSTOM_OMEKA": NumahopEditService.insertBasedOnRank(self.sel2DocProperties.custom_omeka, type);
                                    break;
                                default: NumahopEditService.insertBasedOnRank(self.sel2DocProperties.custom, type);
                            }
                        });
                    });
            }

            /**
             * Chargement d'une notice
             * 
             * @param {any} recordId 
             */
            function selectRecord(recordId) {
                self.entity = RecordSrvc.get({ id: recordId });
                return self.entity.$promise.then(afterLoadingEntity);
            }

            /**
             * Annulation du formulaire d'édition de la notice
             * 
             */
            function cancelRecord() {
                self.unlock(self.entity);
                $scope.entityForm.$cancel();

                if (angular.isDefined($routeParams.newRecord)) {
                    if (self.entity) {
                        delete self.entity;
                    }
                    $location.search({ tab: self.currentTab.code });
                }
            }

            /**
             * Création d'une notice
             */
            function newRecord() {
                if (self.entity) {
                    delete self.entity;
                }
                $location.search({ newRecord: true, tab: self.currentTab.code });
            }

            /**
             * Sélection d'une notice existante
             */
            function browseRecord() {
                var params = { multiple: false, orphan: true };
                if (self.parentCtrl.docUnit && self.parentCtrl.docUnit.library) {
                    params.library = self.parentCtrl.docUnit.library.identifier;
                }
                ModalSrvc.selectBibRecord(params)
                    .then(function (selection) {
                        if (selection.length > 0) {
                            self.records = [selection[0]];
                            return selectRecord(selection[0].identifier);
                        }
                        else {
                            return $q.reject();
                        }
                    })
                    .then(function (selectedRecord) {
                        selectedRecord.docUnit = { identifier: self.parentCtrl.docUnitId };
                        saveEntity(selectedRecord);
                    });
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
                            "_name": "propertyFormDC" + self.indices[type]++,
                            "type": undefined,
                            "record": { identifier: entity.identifier }
                        };
                        break;
                    case "dcq":
                        property = {
                            "_name": "propertyFormDCQ" + self.indices[type]++,
                            "type": undefined,
                            "record": { identifier: entity.identifier }
                        };
                        break;
                    case "custom":
                        property = {
                            "_name": "propertyFormCUSTOM" + self.indices[type]++,
                            "type": undefined,
                            "record": { identifier: entity.identifier },
                            "rank": getCustomMaxRank(entity, "CUSTOM") + self.indices[type]++
                        };
                        break;
                    case "custom_cines":
                        property = {
                            "_name": "propertyFormCUSTOMCines" +self.indices[type]++,
                            "type": undefined,
                            "record": { identifier: entity.identifier },
                            "rank": getCustomMaxRank(entity, "CUSTOM_CINES") + self.indices[type]++
                        };
                        break;
                    case "custom_archive":
                        property = {
                            "_name": "propertyFormCUSTOMArchive" + self.indices[type]++,
                            "type": undefined,
                            "record": { identifier: entity.identifier },
                            "rank": getCustomMaxRank(entity, "CUSTOM_ARCHIVE") + self.indices[type]++
                        };
                        break;
                    case "custom_omeka":
                        property = {
                            "_name": "propertyFormCUSTOMOmeka" + self.indices[type]++,
                            "type": undefined,
                            "record": { identifier: entity.identifier },
                            "rank": getCustomMaxRank(entity, "CUSTOM_OMEKA") + self.indices[type]++
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
            
            /**
             * Pour conserver l'ordre de saisie des champs de type custom.
             * 
             * @param entity
             * @param type
             * @returns
             */
            function getCustomMaxRank(entity, type) {
                var maxRank = 0;
                var filtered = _.filter(entity.properties, function(elt) {return elt.type && elt.type.superType === type});
                if (filtered && filtered.length > 0) {
                   var obj = _.max(filtered, function(elt) {return elt.rank});
                   maxRank = obj.rank;
                } else {
                    switch (type) {
                        case "CUSTOM":
                            maxRank = 1000;
                            break;
                        case "CUSTOM_CINES":
                            maxRank = 2000;
                            break;
                        case "CUSTOM_ARCHIVE":
                            maxRank = 3000;
                            break;
                        case "CUSTOM_OMEKA":
                            maxRank = 3000;
                            break;
                        default:
                            break;
                    }
                }
                return maxRank;
            }

            function mergePropertiesBeforeSave(entity) {
                var newProperties = [];
                // DC
                newProperties = addNewProperties(newProperties, entity.dc, entity.properties);
                // DCQ
                newProperties = addNewProperties(newProperties, entity.dcq, entity.properties);
                // Custom
                newProperties = addNewProperties(newProperties, entity.custom, entity.properties);
                // Custom CINES
                newProperties = addNewProperties(newProperties, entity.custom_cines, entity.properties);
                // Custom ARCHIVE
                newProperties = addNewProperties(newProperties, entity.custom_archive, entity.properties);
                // Custom OMEKA
                newProperties = addNewProperties(newProperties, entity.custom_omeka, entity.properties);

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
            /****************************************************************/
            /** Actions *****************************************************/
            /****************************************************************/
            /** Sauvegarde une entité **/
            function saveEntity(entity) {
                delete self.errors;

                $timeout(function () {
                    mergePropertiesBeforeSave(entity);

                    entity.$save({},
                        function (value) {
                            // warning language pour export cines.
                            if (value.errors && value.errors[0] && value.errors[0].code === 'RECORD_LANGUAGE_UNKNOWN') {
                                MessageSrvc.addWarn("{{msg}}", { msg: value.errors[0].message }, false, 10000);
                                value.errors = [];
                            }
                            MessageSrvc.addSuccess(gettext("La notice {{name}} a été sauvegardée"), { name: value.title });
                            self.unlock(self.entity);
                            initPropertiesBasedOnTypeAndRank(value);
                            onSuccess(value);
                            $location.search({ tab: self.currentTab.code });
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

            /****************************************************************/
            /** Fonctions ***************************************************/
            /****************************************************************/
            // Gestion de l'entité renvoyée par le serveur
            function onSuccess(value) {
                self.entity = value;

                displayMessages();
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
                self.indices = {};
                initForm(self.entity.record.dc, "propertyFormDC", "dc");
                initForm(self.entity.record.dcq, "propertyFormDCQ", "dcq");
                initForm(self.entity.record.custom, "propertyFormCUSTOM", "custom");
                initForm(self.entity.record.custom, "propertyFormCUSTOMCines", "custom_cines");
                initForm(self.entity.record.custom, "propertyFormCUSTOMArchive", "custom_archive");
                initForm(self.entity.record.custom, "propertyFormCUSTOMOmeka", "custom_omeka");
            }
            function initForm(elements, formPrefix, indexName) {
                _.each(elements, function (element, idx) {
                    element._name = formPrefix + idx;
                });
                self.indices[indexName] = angular.isDefined(elements) ? elements.length : 0;
            }
            function displayMessages() {
                // On commence par vider les messages précédents...
                MessageSrvc.clearMessages();
                // Affichage pour un temps limité à l'ouverture
                MessageSrvc.initPanel();
            }

            // Initialisation une fois qu'on a reçu toutes les données du serveur
            function loadAll(value) {
                self.loaded = true;
                onSuccess(value);
            }

            function afterLoadingEntity(entity) {
                initPropertiesBasedOnTypeAndRank(entity);
                loadAll(entity);
                return entity;
            }

            function initPropertiesBasedOnTypeAndRank(entity) {
                entity.record = {};
                entity.record.dc = [];
                entity.record.dcq = [];
                entity.record.custom = [];
                entity.record.custom_cines = [];
                entity.record.custom_archive = [];
                entity.record.custom_omeka = [];
                
                _.each(entity.properties, function (property) {
                    switch (property.type.superType) {
                        case "DC": NumahopEditService.insertBasedOnRank(entity.record.dc, property, "weightedRank");
                            break;
                        case "DCQ": NumahopEditService.insertBasedOnRank(entity.record.dcq, property, "weightedRank");
                            break;
                        case "CUSTOM_CINES": NumahopEditService.insertBasedOnRank(entity.record.custom_cines, property, "weightedRank");
                            break;
                        case "CUSTOM_ARCHIVE": NumahopEditService.insertBasedOnRank(entity.record.custom_archive, property, "weightedRank");
                            break;
                        case "CUSTOM_OMEKA": NumahopEditService.insertBasedOnRank(entity.record.custom_omeka, property, "weightedRank");
                            break;
                        default: NumahopEditService.insertBasedOnRank(entity.record.custom, property, property, "weightedRank");
                    }
                });
            }
        });
})();