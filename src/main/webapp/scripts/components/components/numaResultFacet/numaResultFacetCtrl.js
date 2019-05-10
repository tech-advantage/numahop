(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('NumaResultFacetCtrl', NumaResultFacetCtrl);

    function NumaResultFacetCtrl(codeSrvc, gettextCatalog) {
        var ctrl = this;
        ctrl.clearFacet = clearFacet;
        ctrl.filterFacet = filterFacet;
        ctrl.$onInit = onInit;

        var fields = getFields();
        var values = getValues();

        function onInit() {
            initFacets();
            selectFacets();
        }

        /**
         * Initialisation des modèles internes à partir de ctrl.aggs
         */
        function initFacets() {
            if (ctrl.aggs) {
                ctrl.facets = _.chain(ctrl.aggs).keys().map(buildFacet).sortBy("code").value();

                _.each(ctrl.aggs, function (facets, facetCode) {
                    _.each(facets, function (facet) {
                        translateFacet(facetCode, facet);
                    });
                });
            }
        }

        /**
         * Sélection initiale des facettes à partir du ctrl.filters
         */
        function selectFacets() {
            if (ctrl.filters && ctrl.aggs) {
                _.each(ctrl.filters, function (filter) {
                    var pos = filter.indexOf("=");
                    var facetCode = filter.substring(0, pos);
                    var facet = filter.substring(pos + 1);

                    if (ctrl.aggs[facetCode]) {
                        ctrl.aggs[facetCode]._selected = true;

                        var found = _.find(ctrl.aggs[facetCode], function (f) {
                            return f.title === facet;
                        });
                        if (angular.isDefined(found)) {
                            found._selected = true;
                        }
                    }
                });
            }
        }

        /**
         * Construction d'un objet représentant le type de facette à partir d'un code
         * @param {*} facetCode 
         */
        function buildFacet(facetCode) {
            var field = _.find(fields, function (f) {
                return f.code === facetCode;
            });
            return {
                id: facetCode.replace(/\W+/g, "_"),
                code: facetCode,
                label: field ? field.label : facetCode,
                pos: field ? field.pos : 9999
            };
        }

        /**
         * Traduction du libellé de la facette
         * @param {*} facetCode 
         * @param {*} facet 
         */
        function translateFacet(facetCode, facet) {
            var value = _.find(values, function (v) {
                return v.code === facetCode + "-" + facet.title;
            });
            if (angular.isDefined(value)) {
                facet.translation = value.label;
            }
        }

        /**
         * Sélection d'une facette
         * @param {*} facetCode 
         * @param {*} value 
         */
        function filterFacet(facetCode, value) {
            if (angular.isUndefined(ctrl.filters)) {
                ctrl.filters = [];
            }
            var filter = facetCode + "=" + value.title;

            if (ctrl.filters.indexOf(filter) < 0) {
                ctrl.filters.push(filter);
                ctrl.aggs[facetCode]._selected = true;

                value._selected = true;

                ctrl.onChange({ $filter: ctrl.filters });
            }
        }

        /**
         * Désélection d'une facette ou d'un type de facettes
         * @param {*} facetCode 
         * @param {*} value 
         * @param {*} event 
         */
        function clearFacet(facetCode, value, event) {
            if (event) {
                event.stopPropagation();
            }
            if (angular.isDefined(value)) {
                ctrl.filters = _.without(ctrl.filters, facetCode + "=" + value.title);

                delete value._selected;
                var valSelected = _.some(ctrl.aggs[facetCode], function (val) {
                    return val._selected;
                });
                if (!valSelected) {
                    delete ctrl.aggs[facetCode]._selected;
                }
            }
            else {
                ctrl.filters = _.filter(ctrl.filters, function (pFacet) {
                    return pFacet.substring(0, facetCode.length + 1) !== facetCode + "=";
                });

                delete ctrl.aggs[facetCode]._selected;
                _.each(ctrl.aggs[facetCode], function (val) {
                    delete val._selected;
                });
            }

            ctrl.onChange({ $filter: ctrl.filters });
        }

        function getFields() {
            return [
                // UD
                { code: "DOCUNIT:type", label: gettextCatalog.getString("Type"), pos: 1 },
                { code: "DOCUNIT:archivable", label: gettextCatalog.getString("Archivable"), pos: 2 },
                { code: "DOCUNIT:distributable", label: gettextCatalog.getString("Diffusable"), pos: 3 },
                { code: "RECORD_PROPERTY:contributor", label: gettextCatalog.getString("DC: Contributor"), pos: 10 },
                { code: "RECORD_PROPERTY:coverage", label: gettextCatalog.getString("DC: Coverage"), pos: 11 },
                { code: "RECORD_PROPERTY:creator", label: gettextCatalog.getString("DC: Creator"), pos: 12 },
                { code: "RECORD_PROPERTY:language", label: gettextCatalog.getString("DC: Language"), pos: 13 },
                { code: "RECORD_PROPERTY:publisher", label: gettextCatalog.getString("DC: Publisher"), pos: 14 },
                { code: "RECORD_PROPERTY:relation", label: gettextCatalog.getString("DC: Relation"), pos: 15 },
                { code: "RECORD_PROPERTY:rights", label: gettextCatalog.getString("DC: Rights"), pos: 16 },
                { code: "RECORD_PROPERTY:subject", label: gettextCatalog.getString("DC: Subject"), pos: 17 },
                { code: "RECORD_PROPERTY:type", label: gettextCatalog.getString("DC: Type"), pos: 18 },
                // Constats d'état
                { code: "CONDREPORT:details.type", label: gettextCatalog.getString("Étape"), pos: 1 },
                { code: "CONDREPORT:docUnitCondReportType", label: gettextCatalog.getString("Type"), pos: 1 },
                // Projets
                { code: "PROJECT:status", label: gettextCatalog.getString("Statut"), pos: 1 },
                { code: "PROJECT:active", label: gettextCatalog.getString("Actif"), pos: 2 },
                { code: "PROJECT:provider.fullName", label: gettextCatalog.getString("Prestataire"), pos: 3 },
                // Lots
                { code: "LOT:status", label: gettextCatalog.getString("Statut"), pos: 1 },
                { code: "LOT:type", label: gettextCatalog.getString("Type"), pos: 2 },
                { code: "LOT:requiredFormat", label: gettextCatalog.getString("Format demandé"), pos: 3 },
                { code: "LOT:provider.fullName", label: gettextCatalog.getString("Prestataire"), pos: 4 },
                { code: "LOT:active", label: gettextCatalog.getString("Actif"), pos: 5 },
                // Trains
                { code: "TRAIN:status", label: gettextCatalog.getString("Statut"), pos: 1 },
                { code: "TRAIN:active", label: gettextCatalog.getString("Actif"), pos: 2 },
                { code: "TRAIN:providerSendingDate", label: gettextCatalog.getString("Date d'envoi"), pos: 3 },
                { code: "TRAIN:returnDate", label: gettextCatalog.getString("Date de retour"), pos: 4 },
                // Livraisons
                { code: "DELIVERY:status", label: gettextCatalog.getString("Statut"), pos: 1 },
                { code: "DELIVERY:payment", label: gettextCatalog.getString("Paiement"), pos: 2 },
                { code: "DELIVERY:method", label: gettextCatalog.getString("Mode de livraison"), pos: 3 },
                { code: "DELIVERY:receptionDate", label: gettextCatalog.getString("Date de réception"), pos: 4 },
                { code: "DELIVERY:documentCount", label: gettextCatalog.getString("Nombre de documents"), pos: 5 },
            ];
        }
        function getValues() {
            return [
                // UD
                { code: "DOCUNIT:archivable-0", label: gettextCatalog.getString("Non") },
                { code: "DOCUNIT:archivable-1", label: gettextCatalog.getString("Oui") },
                { code: "DOCUNIT:distributable-0", label: gettextCatalog.getString("Non") },
                { code: "DOCUNIT:distributable-1", label: gettextCatalog.getString("Oui") },
                // Constats d'état
                { code: "CONDREPORT:details.type-LIBRARY_LEAVING", label: gettextCatalog.getString('État initial') },
                { code: "CONDREPORT:details.type-PROVIDER_RECEPTION", label: gettextCatalog.getString('État constaté par le prestataire') },
                { code: "CONDREPORT:details.type-DIGITALIZATION", label: gettextCatalog.getString('État constaté au retour') },
                { code: "CONDREPORT:details.type-LIBRARY_BACK", label: gettextCatalog.getString('État constaté pour le départ pour une reprise de numérisation') },
                { code: "CONDREPORT:docUnitCondReportType-MONO_PAGE", label: gettextCatalog.getString("Monofeuillet") },
                { code: "CONDREPORT:docUnitCondReportType-MULTI_PAGE", label: gettextCatalog.getString("Multifeuillet") },
                // Projets
                { code: "PROJECT:status-CREATED", label: gettextCatalog.getString('Créé') },
                { code: "PROJECT:status-ONGOING", label: gettextCatalog.getString('En cours') },
                { code: "PROJECT:status-PENDING", label: gettextCatalog.getString('En attente') },
                { code: "PROJECT:status-CANCELED", label: gettextCatalog.getString('Annulé') },
                { code: "PROJECT:status-CLOSED", label: gettextCatalog.getString('Clôturé') },
                { code: "PROJECT:active-0", label: gettextCatalog.getString("Non") },
                { code: "PROJECT:active-1", label: gettextCatalog.getString("Oui") },
                // Lots
                { code: "LOT:status-CREATED", label: codeSrvc["lot.status.CREATED"] },
                { code: "LOT:status-PENDING", label: codeSrvc["lot.status.PENDING"] },
                { code: "LOT:status-ONGOING", label: codeSrvc["lot.status.ONGOING"] },
                { code: "LOT:status-CLOSED", label: codeSrvc["lot.status.CLOSED"] },
                { code: "LOT:status-CANCELED", label: codeSrvc["lot.status.CANCELED"] },
                { code: "LOT:active-0", label: gettextCatalog.getString("Non") },
                { code: "LOT:active-1", label: gettextCatalog.getString("Oui") },
                { code: "LOT:type-PHYSICAL", label: gettextCatalog.getString("Physique") },
                { code: "LOT:type-DIGITAL", label: gettextCatalog.getString("Numérique") },
                { code: "LOT:requiredFormat-JP2", label: gettextCatalog.getString('JP2 (JPEG-2000 File Format Syntax)') },
                { code: "LOT:requiredFormat-JPEG", label: gettextCatalog.getString('JPEG (Joint Photographic Experts Group JFIF format)') },
                { code: "LOT:requiredFormat-JPG", label: gettextCatalog.getString('JPG') },
                { code: "LOT:requiredFormat-PNG", label: gettextCatalog.getString('PNG (Portable Network Graphics)') },
                { code: "LOT:requiredFormat-GIF", label: gettextCatalog.getString('GIF (Graphics Interchange Format)') },
                { code: "LOT:requiredFormat-SVG", label: gettextCatalog.getString('SVG (Scalable Vector Graphic)') },
                { code: "LOT:requiredFormat-TIFF", label: gettextCatalog.getString('TIFF (Tagged Image File Format)') },
                { code: "LOT:requiredFormat-TIF", label: gettextCatalog.getString('TIF (Tagged Image File Format)') },
                { code: "LOT:requiredFormat-PDF", label: gettextCatalog.getString('PDF') },
                // Trains
                { code: "TRAIN:active-0", label: gettextCatalog.getString("Non") },
                { code: "TRAIN:active-1", label: gettextCatalog.getString("Oui") },
                { code: "TRAIN:status-CREATED", label: codeSrvc["train.status.CREATED"] },
                { code: "TRAIN:status-IN_PREPARATION", label: codeSrvc["train.status.IN_PREPARATION"] },
                { code: "TRAIN:status-IN_DIGITIZATION", label: codeSrvc["train.status.IN_DIGITIZATION"] },
                { code: "TRAIN:status-RECEIVING_PHYSICAL_DOCUMENTS", label: codeSrvc["train.status.RECEIVING_PHYSICAL_DOCUMENTS"] },
                { code: "TRAIN:status-CLOSED", label: codeSrvc["train.status.CLOSED"] },
                { code: "TRAIN:status-CANCELED", label: codeSrvc["train.status.CANCELED"] },
                // Livraisons
                { code: "DELIVERY:status-SAVED", label: gettextCatalog.getString('Sauvegardé') },
                { code: "DELIVERY:status-DELIVERING", label: gettextCatalog.getString('En cours de livraison') },
                { code: "DELIVERY:status-TO_BE_CONTROLLED", label: gettextCatalog.getString('À contrôler') },
                { code: "DELIVERY:status-VALIDATED", label: gettextCatalog.getString('Validé') },
                { code: "DELIVERY:status-REJECTED", label: gettextCatalog.getString('Rejeté') },
                { code: "DELIVERY:status-AUTOMATICALLY_REJECTED", label: gettextCatalog.getString('Rejeté automatiquement') },
                { code: "DELIVERY:status-TREATED", label: gettextCatalog.getString('Traité') },
                { code: "DELIVERY:method-FTP", label: gettextCatalog.getString('FTP') },
                { code: "DELIVERY:method-DISK", label: gettextCatalog.getString('Disque') },
                { code: "DELIVERY:method-OTHER", label: gettextCatalog.getString('Autre') },
                { code: "DELIVERY:payment-PAID", label: gettextCatalog.getString('Payé') },
                { code: "DELIVERY:payment-UNPAID", label: gettextCatalog.getString('Non payé') }
            ];
        }
    }
})();