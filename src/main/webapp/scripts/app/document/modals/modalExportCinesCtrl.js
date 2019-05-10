(function () {
    'use strict';

    angular.module("numaHopApp.controller")
        .controller("ModalExportCinesCtrl", ModalExportCinesCtrl);

    function ModalExportCinesCtrl($uibModalInstance, gettextCatalog, options, ExportCinesSrvc, MessageSrvc) {

        var mainCtrl = this;
        mainCtrl.validDatas = validDatas;
        mainCtrl.errFields = [];
        mainCtrl.invalidDate = undefined;
        mainCtrl.addProperty = addProperty;
        mainCtrl.removeProperty = removeProperty;
        mainCtrl.close = close;
        mainCtrl.exportCines = exportCines;
        mainCtrl.saveCines = saveCines;
        mainCtrl.majAndReversion = "maj";

        mainCtrl.dcFields = [
            { "code": "title", "rank": 1, "label": gettextCatalog.getString("Title"), "mandatory": true },
            { "code": "creator", "rank": 2, "label": gettextCatalog.getString("Creator"), "mandatory": true },
            { "code": "subject", "rank": 3, "label": gettextCatalog.getString("Subject"), "mandatory": true },
            { "code": "description", "rank": 4, "label": gettextCatalog.getString("Description"), "mandatory": true },
            { "code": "publisher", "rank": 5, "label": gettextCatalog.getString("Publisher"), "mandatory": true },
            { "code": "contributor", "rank": 6, "label": gettextCatalog.getString("Contributor") },
            { "code": "date", "rank": 7, "label": gettextCatalog.getString("Date") },
            { "code": "type", "rank": 8, "label": gettextCatalog.getString("Type"), "mandatory": true },
            { "code": "format", "rank": 9, "label": gettextCatalog.getString("Format"), "mandatory": true },
            { "code": "identifier", "rank": 10, "label": gettextCatalog.getString("Identifier"),  "mandatory": true},
            { "code": "source", "rank": 11, "label": gettextCatalog.getString("Source") },
            { "code": "language", "rank": 12, "label": gettextCatalog.getString("Language"), "mandatory": true },
            { "code": "relation", "rank": 13, "label": gettextCatalog.getString("Relation") },
            { "code": "coverage", "rank": 14, "label": gettextCatalog.getString("Coverage") },
            { "code": "rights", "rank": 15, "label": gettextCatalog.getString("Rights"), "mandatory": true }
        ];
        mainCtrl.meta = {
            dc: true,
            ead: false
        };

        init();

        /**
         * Initialisation du contrôleur
         */
        function init() {
            mainCtrl.meta.ead = options.ead;
            mainCtrl.meta.dc = !mainCtrl.metaEad;
            mainCtrl.version = options.version;
            mainCtrl.alertPlanPac = options.planClassmt === null
                || angular.isUndefined(options.planClassmt.identifier);

            // Chargement des champs Dublin Core de la notice
            ExportCinesSrvc.export({ id: options.identifier }).$promise
                .then(function (dc) {
                    mainCtrl.dc = {};

                    _.each(mainCtrl.dcFields, function (field) {
                        // Ajout des champs vides
                        if (dc[field.code].length === 0) {
                            dc[field.code].push("");
                        }
                        // Création d'objet, pour qu'angular gère les valeurs correctement
                        mainCtrl.dc[field.code] = _.map(dc[field.code], function (v) {
                            return { value: v };
                        });
                    });
                    mainCtrl.majAndReversion = 'maj';
                    mainCtrl.loaded = true;
                });
        }


        /**
         * Ajout d'une propriété
         * 
         * @param {any} dc 
         */
        function addProperty(dc) {
            mainCtrl.dc[dc.code].push({ value: "" });
        }

        /**
         * Suppression d'une propriété existante
         * 
         * @param {any} dc 
         * @param {any} property 
         */
        function removeProperty(dc, property) {
            var idx = mainCtrl.dc[dc.code].indexOf(property);
            if (idx >= 0) {
                mainCtrl.dc[dc.code].splice(idx, 1);
            }
        }


        /**
         * Fermeture/Annulation de la fenêtre modale
         */
        function close() {
            $uibModalInstance.dismiss("cancel");
        }


        /**
         * Validation champs obligatoires.
         */
        function validDatas() {

            var mandatories = _.filter(mainCtrl.dcFields, function (l) {
                return l.mandatory === true;
            });
            var mandatoryCodes = _.map(mandatories, function (i) {
                return i.code;
            });

            mainCtrl.errFields = [];
            _.each(mainCtrl.dc, function (values, key) {
                if (_.contains(mandatoryCodes, key)
                    && (!values[0].value || values[0].value.length === 0)) {
                    mainCtrl.errFields.push(key);
                }
            });
            return mainCtrl.errFields.length === 0;
        }

        /**
         * Validation sommaire du champ date.
         */
        function checkIsDateValid() {
            var propDate = _.find(mainCtrl.dc, function (values, key) {
                return 'date' === key;
            });

            if (angular.isDefined(propDate) && '' !== propDate[0].value && 's.d.' !== propDate[0].value) {
                var regex = /^([0-9]{2})[0-9-/ x]*$/;
                var dtString = propDate[0].value;
                if (!regex.test(dtString)) {
                    mainCtrl.invalidDate = "La date saisie est invalide. Caractères autorisés: 0-9 x / - et l'espace";
                    return false;
                }
            }
            return true;
        }


        /**
         * Fermeture/Validation de la fenêtre modale
         */
        function exportCines() {
            var result = { ead: mainCtrl.meta.ead, reversion: mainCtrl.majAndReversion !== 'maj' };

            // on exporte si tous les champs obligatoires sont renseignes.
            if (validDatas() && checkIsDateValid() && mainCtrl.meta.dc) {
                // Retransformation du modèle, et filtrage des champs dc vides
                result.dc = _.chain(mainCtrl.dc)
                    .mapObject(function (values, key) {
                        return _.chain(values)
                            .map(function (val) {
                                return val.value;
                            })
                            .filter(function (val) {
                                return !!val;
                            })
                            .value();
                    })
                    .pick(function (values) {
                        return values.length > 0;
                    })
                    .value();

                $uibModalInstance.close(result);
            }
        }

        function saveCines() {
            close();

            var result = {};

            if (mainCtrl.meta.dc) {
                // Retransformation du modèle, et filtrage des champs dc vides
                result.dc = _.chain(mainCtrl.dc)
                    .mapObject(function (values, key) {
                        return _.chain(values)
                            .map(function (val) {
                                return val.value;
                            })
                            .filter(function (val) {
                                return !!val;
                            })
                            .value();
                    })
                    .pick(function (values) {
                        return values.length > 0;
                    })
                    .value();
            }

            ExportCinesSrvc.save({
                id: options.identifier
            }, result.dc, function () {
                MessageSrvc.addSuccess(
                    gettextCatalog.getString("Les données d'export CINES pour {{label}} ont été sauvegardées"),
                    { label: result.dc.title[0] });
            });
        }
    }
})();