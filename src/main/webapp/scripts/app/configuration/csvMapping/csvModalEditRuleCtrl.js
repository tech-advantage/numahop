(function () {
    'use strict';

    angular
        .module('numaHopApp.controller')
        .controller('CSVModalEditRuleCtrl', function ($uibModalInstance, MappingSrvc, DocPropertyTypeSrvc, CondreportDescPropertySrvc, CondreportPropertyConfSrvc, gettextCatalog, options, ImagesMetadataSrvc) {
            var mainCtrl = this;
            mainCtrl.ok = ok;
            mainCtrl.cancel = cancel;
            mainCtrl.populateRule = populateRule;

            /** Configuration des listes */
            mainCtrl.config = {
                docUnitFields: MappingSrvc.docUnitFields,
                bibRecordFields: MappingSrvc.bibRecordFields,
            };

            init();

            /** Initialisation du contrôleur */
            function init() {
                _.extend(mainCtrl, options);

                // Chargement des types de propriété
                mainCtrl.config.properties = loadDocPropertyTypes();

                loadCondReportProperties();
                loadMetadataProperties();

                mainCtrl.title = mainCtrl.rule.identifier ? gettextCatalog.getString('Édition de la règle') : gettextCatalog.getString('Nouvelle règle');
            }
            /** Chargement des types de propriété */
            function loadDocPropertyTypes() {
                var result = DocPropertyTypeSrvc.query({ customOnly: 'true' });
                result.$promise.then(function (propTypes) {
                    propTypes.unshift({ label: '', rank: 0 }); // sélection vide
                    return propTypes;
                });
                return result;
            }

            /** Chargement des types de propriété */
            function loadCondReportProperties() {
                CondreportDescPropertySrvc.getAllWithFakes({ library: options.library.identifier }).$promise.then(function (descProp) {
                    _.map(descProp, function (prop) {
                        var type = _.find(CondreportPropertyConfSrvc.types, function (catType) {
                            return prop.type === catType.code;
                        });
                        prop.category = type.label;
                    });

                    mainCtrl.config.condReportFields = descProp;
                    return descProp;
                });
            }

            /** Chargement des types de propriété */
            function loadMetadataProperties() {
                ImagesMetadataSrvc.query().$promise.then(function (metadataProperties) {
                    //Display only the repeatable rules
                    var rules = options.mapping.rules;

                    _.forEach(rules, function (rule) {
                        if (rule.metadataField != null) {
                            var metaPropToRemove = _.find(metadataProperties, function (property) {
                                return property.label === rule.metadataField.label;
                            });
                            //if metaToRemove = this one we would like to modify, we have to display it
                            if (mainCtrl.rule.metadataField && metaPropToRemove.identifier === mainCtrl.rule.metadataField.identifier) {
                                metaPropToRemove = null;
                            }
                            if (metaPropToRemove != null && !metaPropToRemove.repeat) {
                                var index = metadataProperties.indexOf(metaPropToRemove);
                                metadataProperties.splice(index, 1);
                            }
                        }
                    });

                    mainCtrl.config.metadataFields = metadataProperties;
                    mainCtrl.config.metadataFields.unshift({ label: '' });
                    return metadataProperties;
                });
            }

            function populateRule(ppty) {
                if (ppty.superType == 'CUSTOM') {
                    mainCtrl.rule.csvField = ppty.label;
                } else {
                    mainCtrl.rule.csvField = ppty.identifier;
                }
            }

            /** Validation de la fenêtre modale */
            function ok(value) {
                if (value.property && !value.property.identifier) {
                    value.property = null;
                }
                if (!value.condReportField || (value.condReportField && !value.condReportField.identifier)) {
                    value.condReportField = null;
                    value.condReport = null;
                } else {
                    value.condReport = value.condReportField.identifier;
                }
                if (!value.metadataField || (value.metadataField && !value.metadataField.identifier)) {
                    value.metadataField = null;
                    value.metadata = null;
                } else {
                    value.metadata = value.metadataField.identifier;
                }
                $uibModalInstance.close(value);
            }

            /** Annulation de la fenêtre modale */
            function cancel() {
                $uibModalInstance.dismiss('cancel');
            }
        });
})();
