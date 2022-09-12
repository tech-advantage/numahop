(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('CondreportCtrl', CondreportCtrl);

    function CondreportCtrl($http, $q, $location, $routeParams, $scope, $timeout, CondreportSrvc, CondreportAttachmentSrvc,
        CondreportDetailSrvc, CondreportDescPropertySrvc, CondreportDescValueSrvc, CondreportPropertyConfSrvc,
        WorkflowHandleSrvc, CONFIGURATION, gettext, gettextCatalog, ListTools, MessageSrvc, ModalSrvc, USER_ROLES) {

        var repCtrl = this;

        repCtrl.getTypeList = getTypeList;
        repCtrl.init = init;
        // Gestion des constats d'état (général)
        repCtrl.newReport = newReport;
        repCtrl.saveReport = saveReport;
        repCtrl.validateCondReport = validateCondReport;
        repCtrl.confirmValidatedCondReport = confirmValidatedCondReport;
        // Gestion des constats d'état (détails)
        repCtrl.createDetail = createDetail;
        repCtrl.deleteDetail = deleteDetail;
        repCtrl.filterDesc = filterDesc;
        repCtrl.isDetailSelected = isDetailSelected;
        repCtrl.isInitDetailSelected = isInitDetailSelected;
        repCtrl.saveDetail = saveDetail;
        repCtrl.selectDetail = selectDetail;
        // Gestion des descriptions (dans le détail des constats d'état)
        repCtrl.addDescription = addDescription;
        repCtrl.changeDescriptionValue = changeDescriptionValue;
        repCtrl.deleteDescription = deleteDescription;
        repCtrl.descriptionPropertyConfig = descriptionPropertyConfig;
        repCtrl.descriptionValueConfig = descriptionValueConfig;
        repCtrl.editReportForm = editReportForm;
        repCtrl.filterByType = filterByType;
        repCtrl.getDescriptionPos = getDescriptionPos;
        repCtrl.isDescriptionValueRequired = isDescriptionValueRequired;
        repCtrl.isDescriptionCommentVisible = isDescriptionCommentVisible;
        repCtrl.isDescriptionValueVisible = isDescriptionValueVisible;
        repCtrl.getAttributeLabel = getAttributeLabel;
        repCtrl.getEditableType = getEditableType;
        // Gestion des pièces jointes
        repCtrl.addAttachments = addAttachments;
        repCtrl.deleteAttachment = deleteAttachment;
        // Gestion de l'état du workflow
        repCtrl.isStateValidated = isStateValidated;
        repCtrl.isUserPresta = false;
        repCtrl.canValidateCondReport = canValidateCondReport;
        repCtrl.canConfirmCondReport = canConfirmCondReport;
        repCtrl.canPropagate = canPropagate;

        repCtrl.isUserUnauthorized = isUserUnauthorized;
        repCtrl.isPrestaUnauthorized = isPrestaUnauthorized;
        repCtrl.initCantModifAndIsReportValidated = initCantModifAndIsReportValidated;


        // listes déroulantes
        repCtrl.config = {
            _cache: {}, // mise en cache des listes déroulante
            /**
             * modèle de config pour les listes de propriétés
             * refresh est défini dans descriptionPropertyConfig
             */
            descriptionProperty: {
                display: "label",
                placeholder: gettextCatalog.getString("Propriété"),
                trackby: "identifier",
                'refresh-delay': 300,
                'allow-clear': true
            },
            /**
             * modèle de config pour les listes de valeurs
             * refresh est défini dans descriptionValueConfig
             */
            descriptionValue: {
                display: "label",
                placeholder: gettextCatalog.getString("Valeur"),
                trackby: "identifier",
                'refresh-delay': 300,
                'allow-clear': true
            }
        };

        repCtrl.types = CondreportSrvc.types;
        repCtrl.propTypes = CondreportPropertyConfSrvc.types;

        /**
         * Configuration des listes déroulantes de valeur de descriptions
         *
         * @param type
         * @param handler objet partagé entre la liste des propriétés et la liste des valeurs,
         * permettant de réinitialiser la liste des propriétés au changement de valeur,
         * et de charger les bonnes valeurs depuis le serveur
         */
        function descriptionPropertyConfig(handler, type) {
            var config = angular.copy(repCtrl.config.descriptionProperty);

            config.refresh = function ($select) {
                // Pas de liste de valeurs à charger
                if (!type) {
                    return $q.when([]);
                }
                handler.prepertySelect = $select;

                // Gestion du cas où la liste est réinitialisée manuellement (search est indéfini)
                if (angular.isUndefined($select.search)) {
                    return $q.when([]);
                }

                // Chargement de la liste en cache
                if (angular.isUndefined(repCtrl.config._cache["__descProperty__"])) {
                    repCtrl.config._cache["__descProperty__"] = CondreportDescPropertySrvc.queryAll();
                }
                return repCtrl.config._cache["__descProperty__"]
                    .then(function (values) {
                        return _.filter(values, function (v) {
                            return v.type === type;
                        });
                    });
            };
            return config;
        }

        /**
         * Configuration des listes déroulantes de valeur de descriptions
         *
         * @param description objet description auquel appartient la valeur
         * @param handler objet partagé entre la liste des propriétés et la liste des valeurs,
         * permettant de réinitialiser la liste des propriétés au changement de valeur,
         * et de charger les bonnes valeurs depuis le serveur
         */
        function descriptionValueConfig(handler, description) {
            var config = angular.copy(repCtrl.config.descriptionValue);

            config.refresh = function ($select) {
                // Pas de liste de valeurs à charger
                var property = handler.value || description.property;
                if (!property || angular.isString(property)) {
                    return $q.when([]);
                }
                handler.valueSelect = $select;

                // Gestion du cas où la liste est réinitialisée manuellement (search est indéfini)
                if (angular.isUndefined($select.search)) {
                    return $q.when([]);
                }

                // Chargement de la liste en cache
                if (angular.isUndefined(repCtrl.config._cache[property.identifier])) {
                    repCtrl.config._cache[property.identifier] = CondreportDescValueSrvc.query({ property: property.identifier });
                }
                return repCtrl.config._cache[property.identifier];
            };
            return config;
        }

        /**
         * Initialisation du contrôleur
         *
         * @param {any} parentCtrl
         */
        function init(parentCtrl) {
            repCtrl.parent = parentCtrl;
            repCtrl.currentTab = parentCtrl.tabs.CONDREPORT;
            repCtrl.docUnitId = $routeParams.identifier;
            repCtrl.formRO = !$scope.isAuthorized(USER_ROLES.COND_REPORT_HAB2);

            repCtrl.isUserPresta = parentCtrl.isUserPresta;
            repCtrl.loginUser = parentCtrl.user.login;

            loadConditionReport(parentCtrl.docUnit);
        }


        /**************************************
         *               Report               *
         **************************************/

        /**
         * Chargement du constat d'état
         *
         * @param {any} docUnit
         */
        function loadConditionReport(docUnit) {
            repCtrl.loaded = false;
            repCtrl.report = CondreportSrvc.get({ docUnit: docUnit.identifier });

            reportAfterload(repCtrl.report.$promise)

                .then(function (report) {

                    // Conf des propriétés
                    var propPromise = loadPropertyConf();
                    // Détails
                    var detailPromise = loadConditionReportDetails(report.identifier);
                    // Pièces jointes
                    loadConditionReportAttachments(report.identifier);

                    return $q.all([propPromise, detailPromise]);
                })
                .then(function () {
                    // sélection du dernier constat d'état
                    selectLastDetail();
                })
                .then(function () {
                    repCtrl.loaded = true;
                });
        }

        /**
         * Création du constat d'état
         *
         */
        function newReport() {
            repCtrl.loaded = false;
            repCtrl.report = CondreportSrvc.save({ docUnit: repCtrl.docUnitId }, {});

            reportAfterload(repCtrl.report.$promise)
                // Chargement des constats détaillés
                .then(function (report) {
                    // Détails
                    var detailPromise = loadConditionReportDetails(report.identifier);
                    // Pièces jointes
                    repCtrl.attachments = [];

                    return detailPromise;
                })
                // Chargement du dernier constat détaillé
                .then(function (details) {
                    if (details.length > 0) {
                        var detail = getLastDetail();
                        return loadConditionReportDetail(detail.identifier);
                    }
                    return $q.reject();
                })
                // Initialisation du constat: ajout de toutes les propriétés
                .then(initNewDetail)
                // Initialisation de l'affichage
                .then(function (detail) {
                    initConditionReportDetail(detail);

                    repCtrl.loaded = true;
                    openForm("detailForm");
                });
        }

        /**
         * Actions à effectuer à la réception du constat d'état général:
         * chargement de la liste des contats détaillés
         *
         * @param {any} reportPromise
         * @returns
         */
        function reportAfterload(reportPromise) {
            return reportPromise
                .then(function (report) {
                    // Lien de téléchargement du constat d'état
                    repCtrl.parent._condreport_identifier = report.identifier;

                    if (!report.identifier) {
                        delete repCtrl.details;
                        delete repCtrl.attachments;
                        repCtrl.loaded = true;
                        return $q.reject();
                    }
                    return report;
                });
        }

        /**
         * Sauvegarde du constat d'état (champs généraux)
         *
         */
        function saveReport() {
            $timeout(function () {
                repCtrl.report.$save()
                    .then(function () {
                        MessageSrvc.addSuccess(gettext("Le constat d'état a été sauvegardé"));
                    })
                    .catch(function () {
                        openForm('reportForm');
                    });
            });
        }

		/**
         * Liste des types d'état non encore créés
         *
         * @returns
         */
        function getTypeList() {
            if (!repCtrl._typeList) {
                if (repCtrl.isUserPresta) {
                    repCtrl._typeList = _.filter(repCtrl.types, function (l) {
                        return l.code === "DIGITALIZATION" || l.code === "LIBRARY_BACK";
                    });
                } else {
                    repCtrl._typeList = _.filter(repCtrl.types, function (l) {
                        return l.code !== "DIGITALIZATION" && l.code !== "LIBRARY_BACK";
                    });
                    repCtrl._typeList.splice(0, 1);
                }
            }
            return repCtrl._typeList;
        }


        /**************************************
         *               Detail               *
         **************************************/

        function initCantModifAndIsReportValidated() {
            repCtrl.cantModif = isUserUnauthorized() || isPrestaUnauthorized();
            repCtrl.isReportValidated = isStateValidated(repCtrl.currentDetail.type);
        }

        /**
         * Création d'un nouveau constat d'état à partir du dernier saisi
         *
         * @param {any} type
         */
        function createDetail(type) {
            CondreportDetailSrvc.save({ type: type, detail: getLastDetail().identifier }, {}).$promise
                .then(function (newDetail) {
                    MessageSrvc.addSuccess(gettext("Le constat d'état a été sauvegardé"));
                    repCtrl.details.push(newDetail);

                    repCtrl.currentDetail = newDetail;
                    repCtrl.currentDescriptions = copyDescriptions(newDetail);
                    initCantModifAndIsReportValidated();
                });
        }

        /**
         * Suppression du constat d'état
         *
         * @param {any} detail
         */
        function deleteDetail(detail) {
            ModalSrvc.confirmDeletion(gettextCatalog.getString("le constat d'état {{type}}", { type: repCtrl.types[detail.type].label }))
                .then(function () {
                    detail.$delete(function (value) {
                        MessageSrvc.addSuccess(gettext("Le constat d'état {{type}} a été supprimé"), { type: repCtrl.types[value.type].label });

                        ListTools.findAndRemoveItemFromList(detail, repCtrl.details);
                        // sélection du dernier constat d'état
                        selectLastDetail();
                    });
                });
        }

        function canValidateCondReport() {

            if (repCtrl.currentDetail && repCtrl.currentDetail.type === 'LIBRARY_LEAVING') {
                if (repCtrl.isStateValidated('LIBRARY_LEAVING')) {
                    return false;
                } else {
                    return !repCtrl.isUserPresta && repCtrl.parent.canValidateCondReport;
                }
            } else {
                return repCtrl.isUserPresta && repCtrl.parent.canValidateCondReport;
            }
        }

        function canConfirmCondReport() {
            return repCtrl.isUserPresta
                        && repCtrl.isStateValidated('LIBRARY_LEAVING')
                        && !repCtrl.isStateValidated('PROVIDER_RECEPTION');
        }

        function canPropagate() {
            return repCtrl.parent.docUnit.nbChildren > 0;
        }

        /**
         * Enregistrement suivi d'une validation workflow du constat.
         */
        function validateCondReport(propagate) {

            var stateToValidate;
            switch (repCtrl.currentDetail.type) {
                case "LIBRARY_LEAVING":
                    stateToValidate = 'VALIDATION_CONSTAT_ETAT';
                    break;
                case "PROVIDER_RECEPTION":
                case "DIGITALIZATION":
                    stateToValidate = 'CONSTAT_ETAT_AVANT_NUMERISATION';
                    break;
                case "LIBRARY_BACK":
                    stateToValidate = 'CONSTAT_ETAT_APRES_NUMERISATION';
                    break;
                default: return false;
            }

            $timeout(function () {
                    repCtrl.report.$save()
                    .then(function () {
                        repCtrl.parent.validateCondReport(stateToValidate);
                        $location.path("/document/all_operations/" + repCtrl.docUnitId).search({ tab: 'CONDREPORT' });
                    })
                    .then(function () {
                        if (propagate) {
                            // creation constats sur les UD filles
                            CondreportSrvc.propagate({ docUnit: repCtrl.docUnitId, id: repCtrl.currentDetail.identifier}, {}).$promise
                                .then(function (results) {
                                    _.each(results, function(res) {
                                        MessageSrvc.addSuccess(gettextCatalog.getString(res));
                                    });
                            });
                        }
                    })
                    .catch(function () {
                        openForm('reportForm');
                    });
            });

        }

        function confirmValidatedCondReport() {
            $timeout(function () {

                repCtrl.currentDetail.provWriterName = repCtrl.parent.user.firstname;
                repCtrl.currentDetail.provWriterfunction = repCtrl.parent.user.function;

               CondreportDetailSrvc.confirmInitialValid({ type: repCtrl.currentDetail.type, id: repCtrl.currentDetail.identifier }, repCtrl.currentDetail)
                   .$promise
                     .then(function(newDetail) {

                        repCtrl.currentDetail = newDetail;
                        repCtrl.currentDescriptions = copyDescriptions(newDetail);

                        repCtrl.parent.validateCondReport('VALIDATION_BORDEREAU_CONSTAT_ETAT');
                        MessageSrvc.addSuccess(gettext("Le constat d'état a été validé"));
                        $location.path("/document/all_operations/" + repCtrl.parent.docUnit.identifier).search({ tab: 'CONDREPORT' });
                    })
                    .catch(function () {
                        openForm('reportForm');
                    });

            });
        }

        /**
         * Récupère les états du constat d'état reportId
         *
         * @param {any} reportId
         * @returns
         */
        function loadConditionReportDetails(reportId) {
            repCtrl.details = CondreportDetailSrvc.query({ report: reportId });
            return repCtrl.details.$promise;
        }

        /**
         * Récupère l'état detailId
         *
         * @param {any} detailId
         * @returns
         */
        function loadConditionReportDetail(detailId) {
            repCtrl.currentDetail = CondreportDetailSrvc.get({ id: detailId });
            return repCtrl.currentDetail.$promise;
        }

        /**
         * Sauvegarde du détail d'un constat d'état
         *
         */
        function saveDetail(stateToValidate) {
            MessageSrvc.clearMessages();

            $timeout(function () {
                if (!repCtrl.currentDetail.report) {
                    repCtrl.currentDetail.report = { identifier: repCtrl.report.identifier };
                }
                // Mise à jour des descriptions
                repCtrl.currentDetail.descriptions = _.filter(repCtrl.currentDescriptions, function (d) {
                    return d.property && !d.property.fake;
                });

                repCtrl.currentDetail.$save()
                    .then(function (det) {
                        MessageSrvc.addSuccess(gettext("Le constat d'état a été sauvegardé"));
                        repCtrl.currentDescriptions = copyDescriptions(repCtrl.currentDetail);

                    })
                    .catch(function (response) {
                        var descErrors;
                        if (response.data.errors) {
                            descErrors = _.chain(response.data.errors)
                                .filter(function (e) {
                                    return e.code === "CONDREPORT_DETAIL_DESC_EMPTY";
                                })
                                .map(function (e) {
                                    var p = CondreportDescPropertySrvc.getFakeProperty(e.field);
                                    return p ? p.label : e.field;
                                })
                                .value();
                        }
                        if (descErrors && descErrors.length > 0) {
                            MessageSrvc.addError(gettext("<b>Champs non renseignés: </b><br/>{{errors}}"), { errors: descErrors.join("<br/>") });
                        }
                        openForm('detailForm');
                    });
            });
        }

        /**
         * Récupération du dernier constat d'état
         *
         * @param {any} detail
         */
        function getLastDetail() {
            return _.max(repCtrl.details, "position");
        }

        /**
         * Fonction de filtrage par type des descriptions
         *
         * @param {any} type
         * @returns
         */
        function filterDesc(type) {
            return function (value) {
                return value.property ? value.property.type === type : value._type === type;
            };
        }

        /**
         * Sélection du dernier détail de constat d'état,
         * au réinitialisation de la sélection sinon
         *
         */
        function selectLastDetail() {
            // sélection du dernier constat d'état
            if (repCtrl.details.length > 0) {
                selectDetail(getLastDetail());
            }
            else {
                delete repCtrl.currentDetail;
                delete repCtrl.currentDescriptions;
            }
        }

        /**
         * Sélection d'un détail de constat d'état
         *
         * @param {any} detail
         */
        function selectDetail(detail) {
            if (!isDetailSelected(detail)) {
                closeForm("detailForm")
                    .then(function () {
                        return loadConditionReportDetail(detail.identifier);
                    })
                    .then(function (detail) {
                        initConditionReportDetail(detail);
                        repCtrl.currentDescriptions = copyDescriptions(detail);
                    });
            }
        }

        function initConditionReportDetail(detail) {
            // Remplacement du détail dans la liste par celui récupéré du serveur
            var idx = -1;
            _.find(repCtrl.details, function (d, i) {
                if (d.identifier === detail.identifier) {
                    idx = i;
                    return true;
                }
                return false;
            });
            if (idx >= 0) {
                repCtrl.details.splice(idx, 1, detail);
            }
        }

		/**
         * Le détail du constat d'état est-il sélectionné ?
         *
         * @param {any} detail
         * @returns
         */
        function isDetailSelected(detail) {
            return repCtrl.currentDetail && detail.identifier === repCtrl.currentDetail.identifier;
        }

        /**
         * Le détail initial est-il sélectionné ?
         */
        function isInitDetailSelected() {
            return repCtrl.currentDetail && repCtrl.details.length && repCtrl.details[0].identifier === repCtrl.currentDetail.identifier;
        }

        function isUserUnauthorized() {
            return repCtrl.currentDetail
                        && (repCtrl.currentDetail.type === 'DIGITALIZATION' || repCtrl.currentDetail.type === 'LIBRARY_BACK')
                        && !repCtrl.isUserPresta;
        }

        function isPrestaUnauthorized() {
            return repCtrl.isUserPresta && repCtrl.currentDetail
                    && (repCtrl.currentDetail.createdBy !== repCtrl.loginUser
                            || (repCtrl.currentDetail.type !== 'DIGITALIZATION' && repCtrl.currentDetail.type !== 'LIBRARY_BACK'));
        }

        /**
         * Retourne le label pour l'attribut (libellé) de la description donnée
         *
         * @param description la description
         * @returns {*} le libellé
         */
        function getAttributeLabel(description) {
            return description && (description.label || (description.property && description.property.label));
        }

        /**
         * Retourne le type de champ editable à utiliser
         *
         * Pour les libellés des descriptions:
         *  - uiselect : Liste déroulante si en mode création (ajout d'une propriété de description)
         *  ou lien hypertexte lorsque le form n'est pas affiché
         *  - readonly : Label affiché quand il s'agit d'un champ déjà créé
         *
         * @param description la description
         * @param detailForm le form
         * @returns {string} le type de champ editable
         */
        function getEditableType(description, detailForm) {
            return (description && detailForm && !detailForm.$visible || description.creationMode) ?
                'uiselect' : 'readonly';
        }

        /**
         * Initialisation du detail du constat d'état
         *
         * @param {any} detail
         */
        function initNewDetail(detail) {
            var confPromise = loadPropertyConf();
            var propPromise = CondreportDescPropertySrvc.query().$promise;

            return $q.all([confPromise, propPromise])
                .then(function (data) {
                    var confs = data[0];
                    var descProperties = data[1];
                    var type = repCtrl.parent.docUnit.condReportType;

                    detail.descriptions = _.chain(descProperties)
                        .filter(function (p) {
                            var conf = _.find(confs, function (c) {
                                return (c.descPropertyId && c.descPropertyId === p.identifier);
                            });
                            return !conf || (conf.showOnCreation && (!type || conf.types.indexOf(type) >= 0));
                        })
                        .map(function (p) {
                            return { property: p };
                        })
                        .value();

                    repCtrl.currentDescriptions = copyDescriptions(detail);

                    // ppté internes
                    _.chain(CondreportDescPropertySrvc.fakeProperties)
                        .filter(function (p) {
                            var conf = _.find(confs, function (c) {
                                return (c.internalProperty && c.internalProperty === p.identifier);
                            });
                            return !conf || conf.showOnCreation;
                        })
                        .map(function (p) {
                            return { property: p };
                        })
                        .each(function (p) {
                            repCtrl.currentDescriptions.push(p);
                        });

                    return detail;
                });
        }


        /**************************************
         *             Description            *
         **************************************/

		/**
         * Création d'une copie de la liste des descriptions de reliures,
         * qui sert pour l'affichage
         *
         * @param {any} detail
         * @returns
         */
        function copyDescriptions(detail) {
            var copyOfDescriptions = angular.copy(detail.descriptions);
            // Création des "fausses" descriptions
            if (detail.dim1 || detail.dim2 || detail.dim3) {
                copyOfDescriptions.push({ property: CondreportDescPropertySrvc.getFakeProperty("DIMENSION") });
            }
            if (detail.bodyDesc) {
                copyOfDescriptions.push({ property: CondreportDescPropertySrvc.getFakeProperty("BODY_DESC") });
            }
            if (detail.bindingDesc) {
                copyOfDescriptions.push({ property: CondreportDescPropertySrvc.getFakeProperty("BINDING_DESC") });
            }

            return copyOfDescriptions;
        }

        /**
         * Filtrage des propriétés
         *
         */
        function filterByType(type) {
            return function (description) {
                return description.property ? description.property.type === type : description.type === type;
            };
        }

        /**
         * Position de la description de reliures
         *
         * @param {any} desc
         */
        function getDescriptionPos(description) {
            var property = description.property;
            // Si pas de property ou si fake, placer à la fin;
            return (!property || property.fake) ? 9999 : property.order; //+ property.label;
        }

        /**
         * Ajout d'une description de reliures au constat d'état en cours d'édition
         *
         * Passage en mode creation pour afficher le menu déroulant
         * au lieu du label pour le libellé de description
         *
         */
        function addDescription(type) {
            var newDescription = { detail: repCtrl.currentDetail, creationMode: true };
            if (type) {
                newDescription.type = type;
            }
            repCtrl.currentDescriptions.push(newDescription);
        }

        /**
         * Suppression d'une description de reliure au constat d'état en cours d'édition
         *
         */
        function deleteDescription(description) {
            var idx = repCtrl.currentDescriptions.indexOf(description);
            if (idx >= 0) {
                repCtrl.currentDescriptions.splice(idx, 1);
            }
            var ppty = description.property;
            if (ppty) {
                // Suppression des "fausses" descriptions du document
                if (ppty.identifier === "DIMENSION") {
                    repCtrl.currentDetail.dim1 = 0;
                    repCtrl.currentDetail.dim2 = 0;
                    repCtrl.currentDetail.dim3 = 0;
                }
                else if (ppty.identifier === "BODY_DESC") {
                    repCtrl.currentDetail.bodyDesc = null;
                }
                else if (ppty.identifier === "BINDING_DESC") {
                    repCtrl.currentDetail.bindingDesc = null;
                }
            }
        }

        /**
         * Au changement de la propriété sélectionnée, on réinitialise la liste des valeurs
         *
         * @param {any} description
         * @param {any} newValue    nouvelle valeur prise par "propriété"
         * @param {any} handler     objet partagé entre le champ propriété et le champ valeur
         */
        function changeDescriptionValue(handler, description) {
            return function (newValue) {
                delete description.value;
                handler.value = newValue;

                if (handler.valueSelect) {
                    delete handler.valueSelect.selected;
                    delete handler.valueSelect.search;
                    handler.valueSelect.items = [];
                    // handler.valueSelect.activate(false, true);
                    handler.valueSelect.activeIndex = 0;
                    handler.valueSelect.open = false;
                }
            };
        }

        /**
         * La valeur est obligatoire
         *
         * @param {any} handler
         * @param {any} description
         * @returns
         */
        function isDescriptionValueRequired(handler, description) {
            var property = handler.value || description.property;
            if (!property) {
                return false;
            }
            var conf = _.find(repCtrl.propConf, function (conf) {
                return conf.descPropertyId === property.identifier;
            });
            return angular.isDefined(conf) && conf.required;
        }

        /**
         * Affichage de la valeur d'un état de reliure
         *
         * @param {any} handler
         * @param {any} description
         * @param {any} form
         * @returns
         */
        function isDescriptionValueVisible(handler, description, form) {
            var property = handler.value || description.property;
            if (!property) {
                return false;
            }
            var conf = _.find(repCtrl.propConf, function (conf) {
                return conf.descPropertyId === property.identifier;
            });
            var allowComment = conf ? conf.allowComment : property.allowComment;
            // Si c'est la liste des valeurs n'a pas d'options, on la cache
            var hasValueItems = handler.valueSelect && handler.valueSelect.items && handler.valueSelect.items.length;

            return (form.$visible && !!hasValueItems)
                || (!form.$visible && (!!description.value || !allowComment || !description.comment));
        }

        /**
         * Affichage du commentaire d'un état de reliure
         *
         * @param {any} handler
         * @param {any} description
         * @param {any} form
         * @returns
         */
        function isDescriptionCommentVisible(handler, description, form) {
            var property = handler.value || description.property;
            if (!property) {
                return false;
            }
            var conf = _.find(repCtrl.propConf, function (conf) {
                return conf.descPropertyId === property.identifier;
            });
            var allowComment = conf ? conf.allowComment : property.allowComment;
            return allowComment && (form.$visible || description.comment);
        }

        /**************************************
         *           Pièces jointes           *
         **************************************/

        /**
         * Ajout des pièces jointes sélectionnées
         *
         * @param {any} element
         */
        function addAttachments(element) {
            if (element.files.length > 0) {
                $scope.$apply(function () {
                    var newAttachments = _.chain(element.files)
                        // Turn the FileList object into an Array
                        .map(angular.identity)
                        // Filtrage des doublons
                        .filter(function (f) {
                            return _.every(repCtrl.attachments, function (oth) {
                                return oth.name !== f.name || oth.size !== f.size;
                            });
                        })
                        .value();

                    // Upload des pièces jointes
                    saveAttachments(newAttachments);
                });
            }
        }

        /**
        * Sauvegarde des pièces jointes
        */
        function saveAttachments(files) {
            var url = CONFIGURATION.numahop.url + 'api/rest/condreport_attachment';

            var formData = new FormData();
            formData.append("report", repCtrl.report.identifier);
            _.each(files, function (file) {
                formData.append("file", file);
            });

            var config = {
                transformRequest: angular.identity,
                headers: {
                    'Content-Type': undefined
                }
            };
            $http.post(url, formData, config)
                .success(function (data) {
                    MessageSrvc.addSuccess(gettext("Les pièces jointes ont été téléversées"));
                    if (!repCtrl.attachments) {
                        repCtrl.attachments = data;
                    }
                    else {
                        repCtrl.attachments = repCtrl.attachments.concat(data);
                    }
                })
                .error(function () {
                    MessageSrvc.addError(gettext("Échec lors du téléversement des pièces jointes"));
                });
        }

        /**
         * Suppression d'une pièce jointe
         *
         * @param {any} att
        * */
        function deleteAttachment(att) {
            ModalSrvc.confirmDeletion(gettextCatalog.getString("la pièce jointe {{originalFilename}}", att))
                .then(function () {
                    CondreportAttachmentSrvc.delete({}, att).$promise
                        .then(function () {
                            MessageSrvc.addSuccess(gettext("La pièce jointe {{originalFilename}} a été supprimée"), att);
                            ListTools.findAndRemoveItemFromList(att, repCtrl.attachments);
                        });
                });
        }

        /**
         * Chargement des pièces jointes du constat d'état
         *
         * @param {any} reportId
         * @returns
         */
        function loadConditionReportAttachments(reportId) {
            repCtrl.attachments = CondreportAttachmentSrvc.query({ report: reportId });
            return repCtrl.attachments.$promise;
        }

        function loadPropertyConf() {
            repCtrl.propConf = CondreportPropertyConfSrvc.query({ library: repCtrl.parent.docUnit.library.identifier, project: repCtrl.parent.docUnit.project.identifier });
            return repCtrl.propConf.$promise;
        }

        /**************************************
         *              workflow              *
         **************************************/
        function isStateValidated(type) {
            var workflow = repCtrl.parent.docUnit.workflow;
            switch (type) {
                case "LIBRARY_LEAVING": return WorkflowHandleSrvc.isConstatValidated(workflow);
                case "PROVIDER_RECEPTION": return WorkflowHandleSrvc.isConstatConfirmed(workflow);
                case "DIGITALIZATION": return WorkflowHandleSrvc.isConstatBeforeNumValidated(workflow);
                case "LIBRARY_BACK": return WorkflowHandleSrvc.isConstatAfterNumValidated(workflow);
                default: return false;
            }
        }

        /**************************************
         *               Divers               *
         **************************************/

        /**
         * Ouverture du formulaire
         *
         * @param {any} name
         */
        function openForm(name) {
            return $timeout(function () {
                if (angular.isDefined($scope[name])) {
                    $scope[name].$show();
                }
            });
        }

        /**
         * Fermeture du formulaire
         *
         * @param {any} name
         */
        function closeForm(name) {
            return $timeout(function () {
                if (angular.isDefined($scope[name]) && $scope[name].$visible) {
                    $scope[name].$cancel();
                }
            });
        }

        /**
         * Ouverture du formulaire du constat d'état en mode édition
         *
         */
        function editReportForm() {
            // Ouverture de l'accordion
            repCtrl.reportAcc = true;
            angular.element("#report-collapse").addClass("collapse in");

            // Édition du formulaire
            openForm("reportForm");
        }
    }
})();
