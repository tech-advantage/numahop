(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('MappingCtrl', MappingCtrl);

    function MappingCtrl($filter, $http, $q, $location, $routeParams, $scope, AuditSrvc, CONFIGURATION, gettext, gettextCatalog,
        HistorySrvc, LibrarySrvc, MappingRuleEadSrvc, MappingRuleMarcSrvc, MappingSrvc, MessageSrvc, ModalSrvc, Principal, USER_ROLES) {

        var mainCtrl = this;
        mainCtrl.mappingModified = mappingModified;
        /** Mapping */
        mainCtrl.create = create;
        mainCtrl.delete = del;
        mainCtrl.edit = edit;
        mainCtrl.getBibfieldLabel = getBibfieldLabel;
        mainCtrl.getDocfieldLabel = getDocfieldLabel;
        mainCtrl.importNewMapping = importNewMapping;
        mainCtrl.importMapping = importMapping;
        mainCtrl.reload = reload;
        mainCtrl.save = save;
        mainCtrl.duplicate = duplicate;
        mainCtrl.restore = restore;
        /** Règles */
        mainCtrl.addRule = addRule;
        mainCtrl.copyRule = copyRule;
        mainCtrl.delRule = delRule;
        mainCtrl.downRule = downRule;
        mainCtrl.editRule = editRule;
        mainCtrl.deleteRuleFilters = deleteRuleFilters;
        mainCtrl.filterRule = filterRule;
        mainCtrl.setRuleFilters = setRuleFilters;
        mainCtrl.sortRule = sortRule;
        mainCtrl.upRule = upRule;

        /* Listes ui-select */
        mainCtrl.uioptions = {
            libraries: {
                text: "name",
                placeholder: gettextCatalog.getString("Bibliothèque"),
                trackby: "identifier",
                // Chargement avec mise en cache du résultat
                refresh: function () {
                    if (!mainCtrl.uioptions.libraries.data) {
                        mainCtrl.uioptions.libraries.data = LibrarySrvc.query({ dto: true });
                        return mainCtrl.uioptions.libraries.data.$promise;
                    }
                    else {
                        return $q.when(mainCtrl.uioptions.libraries.data);
                    }
                },
                'refresh-delay': 0, // pas de refresh-delay, car on lit les données en cache après le 1er chargement
                'allow-clear': true
            }
        };


        init();

        /** Initialisation du contrôleur */
        function init() {
            HistorySrvc.add(gettextCatalog.getString("Configuration des mappings"));

            mainCtrl.isSuperAdmin = Principal.isInRole(USER_ROLES.SUPER_ADMIN);
            mainCtrl.rwCheckLib = Principal.isInRole(USER_ROLES.ADMINISTRATION_LIB);    // mapping des autres bib en lecture seule
            mainCtrl.library = Principal.library();
            mainCtrl.ruleFiltersTooltip = getRuleFiltersTooltip();

            if ($routeParams.type) {
                mainCtrl.type = $routeParams.type.toUpperCase();
                loadMappings(mainCtrl.type);
            }

            // Message d'avertissement si l'utilisateur quitte la page alors que des modifications sont en cours
            $scope.$on("$locationChangeStart", checkModificationsOnLocationChange);
        }

        /** Chargement des mappings existants */
        function loadMappings(type) {
            if (mainCtrl.type) {
                mainCtrl.mappings = MappingSrvc.query({ type: type });
                mainCtrl.mappings.$promise
                    .then(updateMappingLibraries);
            }
        }
        function updateMappingLibraries(mappings) {
            mainCtrl.libraries = _.chain(mappings)
                .pluck("library")
                .uniq(false, function (l) {
                    return l.identifier;
                })
                .value();
        }
        /** Le mapping a été modifié */
        function mappingModified() {
            mainCtrl.modified = true;
        }
        /**
         * Contrôle si des modifications sont en cours, et alerte l'utilisateur
         * 
         * @param {any} event 
         * @returns promise
         */
        function checkModifications(ignoreLastRestore) {
            // on ne confirme pas lors du passage d'une restauration à une autre
            if (mainCtrl.modified || (!ignoreLastRestore && mainCtrl.restored)) {
                return ModalSrvc.confirmAction(gettextCatalog.getString("continuer alors que vous avez des modifications non sauvegardées"))
                    .then(function () {
                        // Annulation des modifications en cours
                        delete mainCtrl.activeRule;
                        mainCtrl.modified = false;
                        mainCtrl.restored = false;
                    });
            }
            else {
                return $q.when();
            }
        }
        /**
         * Contrôle si des modifications sont en cours, et alerte l'utilisateur
         * sur un évènement ($locationChangeStart)
         * 
         * @param {any} event 
         */
        function checkModificationsOnLocationChange(event) {
            // Dans le cas d'une restauration sans modification, on prévient l'utilisateur s'il change de page
            if (mainCtrl.modified || mainCtrl.restored) {
                var url = $location.url();

                // Annulation de l'action en cours, dans l'attente d'une confirmation de l'utilisateur
                event.preventDefault();

                ModalSrvc.confirmAction(gettextCatalog.getString("continuer alors que vous avez des modifications non sauvegardées"))
                    .then(function () {
                        // Annulation des modifications
                        delete mainCtrl.activeRule;
                        mainCtrl.modified = false;
                        mainCtrl.restored = false;

                        // on force le changement de page
                        $location.url(url);
                    });
            }
        }
        /** Création d'un mapping */
        function create() {
            checkModifications().then(function () {
                mainCtrl.editedMapping = new MappingSrvc();
                mainCtrl.editedMapping.type = mainCtrl.type;
                mainCtrl.editedMapping.rules = [];
                if (mainCtrl.library) {
                    mainCtrl.editedMapping.library = _.find(mainCtrl.uioptions.libraries.data, function (lib) { return lib.identifier = mainCtrl.library; });
                }

                loadRevisions();
                delete mainCtrl.activeRule;
                mainCtrl.rw = Principal.isInRole(USER_ROLES.MAP_HAB1);
                mainCtrl.modified = false;
                mainCtrl.loaded = true;
            });
        }
        /** Édition d'un mapping */
        function edit(mapping) {
            checkModifications()
                .then(function () {
                    mainCtrl.loaded = false;

                    mainCtrl.editedMapping = MappingSrvc.get({ id: mapping.identifier });
                    loadRevisions(mapping);

                    return mainCtrl.editedMapping.$promise;
                })
                .then(function () {
                    delete mainCtrl.activeRule;
                    mainCtrl.rw = mainCtrl.isSuperAdmin || (Principal.isInRole(USER_ROLES.MAP_HAB1) && (!mainCtrl.rwCheckLib || mapping.library.identifier === mainCtrl.library));
                    mainCtrl.modified = false;
                    mainCtrl.restored = false;
                    mainCtrl.loaded = true;
                });
        }
        /** Rechargement d'un mapping */
        function reload(mapping) {
            // pas de confirmation pour le rechargement
            delete mainCtrl.activeRule;
            mainCtrl.modified = false;
            mainCtrl.restored = false;

            if (angular.isDefined(mapping.identifier)) {
                edit(mapping);
            }
            else {
                create();
            }
        }
        /** Suppression d'un mapping */
        function del(mapping) {
            if (!mapping) {
                return;
            }
            ModalSrvc.confirmDeletion(gettextCatalog.getString("Le mapping {{label}}", mapping))
                .then(function () {
                    return mapping.$delete();
                })
                .then(function () {
                    MessageSrvc.addSuccess(gettext("Le mapping {{label}} a été supprimé"), mapping);
                    loadMappings(mainCtrl.type);

                    delete mainCtrl.activeRule;
                    delete mainCtrl.editedMapping;
                    mainCtrl.modified = false;
                    mainCtrl.restored = false;
                    mainCtrl.loaded = false;
                });
        }
        /** Duplication d'un mapping */
        function duplicate(mapping) {
            if (!mapping) {
                return;
            }
            checkModifications().then(function () {
                var params = { id: mapping.identifier };
                if (mainCtrl.library) {
                    params.library = mainCtrl.library;
                }
                MappingSrvc.duplicate(params, function (newMapping) {
                    loadMappings(mainCtrl.type);
                    edit(newMapping);
                });
            });
        }
        /** Restauration d'un mapping */
        function restore(mapping, rev) {
            if (!mapping || !rev) {
                return;
            }
            checkModifications(true)
                .then(function () {
                    mainCtrl.loaded = false;
                    return AuditSrvc.get({ type: "mapping", id: mapping.identifier, rev: rev.id }).$promise;
                })
                .then(function (restoredMapping) {
                    copyRestoredMapping(restoredMapping, mainCtrl.editedMapping);

                    mainCtrl.restored = true;
                    mainCtrl.loaded = true;
                });
        }
        /**
         * Copie de infos de auditMapping (AuditSrvc) dans mapping (MappingSrvc)
         * 
         * @param {any} auditMapping 
         * @param {any} mapping 
         */
        function copyRestoredMapping(auditMapping, mapping) {
            // On conserve le n° de version des règles existantes
            _.each(auditMapping.rules, function (auditRule) {
                var existingRule = _.find(mapping.rules, function (mappingRule) {
                    return mappingRule.identifier === auditRule.identifier;
                });
                if (angular.isDefined(existingRule)) {
                    auditRule.version = existingRule.version;
                }
            });
            // Recopie des données
            mapping.label = auditMapping.label;
            mapping.library = auditMapping.library;
            mapping.joinExpression = auditMapping.joinExpression;
            mapping.rules = auditMapping.rules;
        }
        /** Sauvegarde d'un mapping */
        function save(mapping) {
            if (!mapping) {
                return;
            }
            var isCreation = !mapping.identifier;

            // Tri des règles de mapping
            mapping.rules = $filter("orderBy")(mapping.rules, sortRule);
            mapping.$save()
                .then(function () {
                    afterSave(mapping, isCreation);
                });
        }

        function afterSave(mapping, isCreation) {
            MessageSrvc.addSuccess(gettext("Le mapping {{label}} a été sauvegardé"), mapping);
            delete mainCtrl.activeRule;
            mainCtrl.modified = false;
            mainCtrl.restored = false;

            // Mise à jour de la liste des révisions
            loadRevisions(mapping);

            // Mise à jour de la liste des DTOs
            var editedDto;

            if (isCreation) {
                editedDto = _.pick(mapping, "identifier", "label", "library", "type");
                mainCtrl.mappings.push(editedDto);
            }
            else {
                editedDto = _.find(mainCtrl.mappings, function (m) {
                    return m.identifier === mapping.identifier;
                });
                if (angular.isDefined(editedDto)) {
                    editedDto.label = mapping.label;
                    editedDto.library = mapping.library;
                    editedDto.type = mapping.type;
                }
            }
            updateMappingLibraries(mainCtrl.mappings);
        }

        /** Chargement de la liste des révisions */
        function loadRevisions(mapping) {
            if (mapping && (mainCtrl.isSuperAdmin || !mainCtrl.rwCheckLib || mapping.library.identifier === mainCtrl.library)) {
                mainCtrl.revisions = AuditSrvc.query({ type: "mapping", id: mapping.identifier });
            }
            else {
                delete mainCtrl.revisions;
            }
        }

        /** Ajout d'une nouvelle règle */
        function addRule(mapping) {
            return copyRule(false, mapping);
        }
        /** Copie d'une règle existante */
        function copyRule(rule, mapping) {
            var copyOfRule = rule
                ? _.pick(rule, "bibRecordField", "condition", "conditionConf", "docUnitField", "expression", "expressionConf", "property")
                : {};
            var options = { rule: copyOfRule, type: mainCtrl.type };
            return ModalSrvc.open("scripts/app/configuration/mapping/modalEditRule.html", options, "lg", "ModalEditRuleCtrl")
                .then(function (edRule) {
                    edRule.position = 999999;
                    mapping.rules.push(edRule);
                    mainCtrl.activeRule = edRule;
                    mainCtrl.modified = true;
                    return edRule;
                });
        }
        /** Suppression d'une règle existante */
        function delRule(rule, mapping) {
            mainCtrl.modified = true;

            var idx = mapping.rules.indexOf(rule);
            if (idx >= 0) {
                mapping.rules.splice(idx, 1);
            }
        }
        /** Suppression d'une règle existante */
        function editRule(rule, mapping) {
            var options = { rule: angular.copy(rule), type: mainCtrl.type };
            return ModalSrvc.open("scripts/app/configuration/mapping/modalEditRule.html", options, "lg", "ModalEditRuleCtrl")
                .then(function (edRule) {
                    mainCtrl.activeRule = edRule;
                    mainCtrl.modified = true;

                    var idx = mapping.rules.indexOf(rule);
                    if (idx >= 0) {
                        mapping.rules.splice(idx, 1, edRule);   // remplacement de la règle originale par la règle éditée
                    }
                    return rule;
                });
        }

        /**
         * Déplacement des règles de mapping
         * 
         * @param {any} rule 
         * @param {any} rules 
         */
        function downRule(rule, rules) {
            moveRule(rule, rules, "down");
        }

        function upRule(rule, rules) {
            moveRule(rule, rules, "up");
        }

        function moveRule(rule, rules, direction) {
            var position = rule.position || 0;
            var sortBy = direction === "up" ? function (r) { return -(r.position || 0); } : "position";
            var find = direction === "up" ? function (r) { return r.position < position; } : function (r) { return r.position > position; };

            var next = _.chain(rules)
                .filter(function (r) {
                    return rule.property ? r.property && rule.property.identifier === r.property.identifier
                        : rule.docUnitField ? rule.docUnitField === r.docUnitField
                            : rule.bibRecordField ? rule.bibRecordField === r.bibRecordField
                                : false;
                })
                .sortBy(sortBy)
                .find(find)
                .value();

            if (angular.isDefined(next)) {
                rule.position = next.position;
                next.position = position;

                mainCtrl.activeRule = rule;
                mainCtrl.modified = true;
            }
        }

        /**
         * Filtrage des règles de mapping
         * 
         * @param {any} mapping 
         */
        function setRuleFilters() {
            ModalSrvc.getValueTextarea(
                gettextCatalog.getString("Filtrage des règles de mapping"),
                gettextCatalog.getString("Filtre"),
                mainCtrl.ruleFilters && mainCtrl.ruleFilters.length ? mainCtrl.ruleFilters.join(", ") : ""

            ).then(function (filter) {
                // Filtres
                var getFields = mainCtrl.type === "MARC" ? MappingRuleMarcSrvc.getFields : mainCtrl.type === "EAD" ? MappingRuleEadSrvc.getFields : null;
                delete mainCtrl.ruleFilters;

                if (getFields) {
                    mainCtrl.ruleFilters = getFields(filter, true);
                }
                // Tooltip
                mainCtrl.ruleFiltersTooltip = getRuleFiltersTooltip();
            });
        }

        /**
         * Suppression du filtrage des règles de mapping
         * 
         * @param {any} mapping 
         */
        function deleteRuleFilters() {
            delete mainCtrl.ruleFilters;
            mainCtrl.ruleFiltersTooltip = getRuleFiltersTooltip();
        }

        /**
         * Filtrage des règles de mapping
         * 
         * @param {any} rule
         */
        function filterRule(rule) {
            // Un filtrage est appliqué sur les règles de mapping
            if (mainCtrl.ruleFilters && mainCtrl.ruleFilters.length) {
                var getFields = mainCtrl.type === "MARC" ? MappingRuleMarcSrvc.getFields : mainCtrl.type === "EAD" ? MappingRuleEadSrvc.getFields : null;

                if (getFields) {
                    var fields = [];
                    if (rule.condition) {
                        fields = fields.concat(getFields(rule.condition, false));
                    }
                    if (rule.expression) {
                        fields = fields.concat(getFields(rule.expression, false));
                    }
                    return _.some(fields, function (f) {
                        return _.some(mainCtrl.ruleFilters, function (r) {
                            return f === r || (mainCtrl.type === "MARC" && f.substr(0, r.length) === r) || (mainCtrl.type === "EAD" && f.indexOf(r) >= 0);
                        });
                    });
                }
                return false;
            }
            return true;
        }

        /**
         * Tooltip du filtre des règles de filtrage
         * 
         * @returns 
         */
        function getRuleFiltersTooltip() {
            if (mainCtrl.ruleFilters && mainCtrl.ruleFilters.length > 0) {
                return gettextCatalog.getString("Filtres: {{filters}}", { filters: mainCtrl.ruleFilters.join(", ") });
            }
            else {
                return gettextCatalog.getString("Filtrer les mappings");
            }
        }

        /** Tri des règles */
        function sortRule(rule) {
            var key;
            var ppty = rule.property;
            if (ppty) {
                var rank = ppty.rank < 10 ? "0" + ppty.rank : ppty.rank;
                key = (ppty.superType === "DC" ? '1' : ppty.superType === "DCQ" ? '2' : '3') + "#" + rank;
            }
            else if (rule.docUnitField) {
                key = '4#' + rule.docUnitField;
            }
            else if (rule.bibRecordField) {
                key = '5#' + rule.bibRecordField;
            }
            else {
                key = "9#";
            }
            key += "#" + (rule.defaultRule ? "9999" : rule.position || "0000");
            return key;
        }

        /**
         * Libellé d'un champ d'une notice bibliographique
         * 
         * @param {any} field 
         */
        function getBibfieldLabel(field) {
            if (!field) {
                return;
            }
            var found = _.find(MappingSrvc.bibRecordFields, function (f) {
                return f.code === field;
            });
            return found ? found.label : "";
        }

        /**
         * Libellé d'un champ d'une unité documentaire
         * 
         * @param {any} field 
         */
        function getDocfieldLabel(field) {
            if (!field) {
                return;
            }
            var found = _.find(MappingSrvc.docUnitFields, function (f) {
                return f.code === field;
            });
            return found ? found.label : "";
        }

        /**
         * Import d'un nouveau mapping à partir d'un fichier JSON
         */
        function importNewMapping() {
            var library;
            if (mainCtrl.editedMapping) {
                library = mainCtrl.editedMapping.library;
            }
            if (!library) {
                library = mainCtrl.library;
            }
            if (!library && mainCtrl.uioptions.libraries.data.length > 0) {
                library = mainCtrl.uioptions.libraries.data[0];
            }
            uploadMapping(null, library ? library.identifier : null)
                .then(function (mapping) {
                    mapping.library = library;
                    afterSave(mapping, true);
                    reload(mapping);
                });
        }

        /**
         * Mise à jour d'un mapping à partir d'un fichier JSON
         */
        function importMapping(mapping) {
            uploadMapping(mapping.identifier)
                .then(function (mapping) {
                    afterSave(mapping, false);
                    reload(mapping);
                });
        }

        /**
         * Upload d'un mapping
         * 
         * @param {any} formData 
         * @param {any} mappingId si renseigné => mise à jour du mapping existant
         * @param {any} libraryId si renseigné => création d'un nouveau mapping
         */
        function uploadMapping(mappingId, libraryId) {
            return ModalSrvc.selectFile({ accept: ".json" })
                .then(function (files) {
                    var url = CONFIGURATION.numahop.url + 'api/rest/mapping';
                    if (mappingId) {
                        url += "/" + mappingId;
                    }

                    var formData = new FormData();
                    formData.append("import", true);

                    if (libraryId) {
                        formData.append("library", libraryId);
                    }

                    // Emplacement du/des fichiers à importer
                    _.each(files, function (file) {
                        formData.append("file", file);
                    });

                    var config = {
                        transformRequest: angular.identity,
                        headers: {
                            'Content-Type': undefined
                        }
                    };
                    return $http.post(url, formData, config)
                        .then(function (response) {
                            return response.data;
                        });
                });
        }
    }
})();