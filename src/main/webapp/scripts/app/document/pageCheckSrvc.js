(function () {
    'use strict';

    angular.module('numaHopApp.service')
        .factory('PageCheckSrvc', function ($resource, CONFIGURATION) {
            var service = $resource(CONFIGURATION.numahop.url + 'api/rest/check/:id', {
                id: '@identifier'
            }, {
                    errorTypes: {
                        method: 'GET',
                        isArray: true,
                        params: {
                            'errors': true
                        }
                    },
                    setErrorsForPage: {
                        method: 'POST',
                        isArray: false,
                        params: {
                            'seterrors': true
                        }
                    },
                    setErrorsForSampledPage: {
                        method: 'POST',
                        isArray: false,
                        params: {
                            'setsamplederrors': true
                        }
                    },
                    getErrorsForPage: {
                        method: 'GET',
                        isArray: true,
                        params: {
                            'geterrors': true
                        }
                    },
                    getErrorsForSampledPage: {
                        method: 'GET',
                        isArray: true,
                        params: {
                            'getsamplederrors': true
                        }
                    },
                    setErrors: {
                        method: 'POST',
                        isArray: false,
                        params: {
                            'setglobalerrors': true
                        }
                    },
                    setErrorsForSample: {
                        method: 'POST',
                        isArray: false,
                        params: {
                            'setsampledglobalerrors': true
                        }
                    },
                    getErrors: {
                        method: 'GET',
                        isArray: true,
                        params: {
                            'getglobalerrors': true
                        }
                    },
                    getErrorsForSample: {
                        method: 'GET',
                        isArray: true,
                        params: {
                            'getsampledglobalerrors': true
                        }
                    },
                    getDocumentErrors: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            'getdocumentallerrors': true
                        }
                    },
                    getSampleErrors: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            'getsampleallerrors': true
                        }
                    },
                    getSummary: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            'summaryresults': true
                        }
                    }
                });
            service.checkErrors = [
                { key: "FOREIGN_BODIES", value: "Corps étrangers sur l'image", isMajor: true },
                { key: "UNREADABLE", value: "Fichier illisible", isMajor: true },
                { key: "BAD_METADATA", value: "Format de métadonnées non respecté", isMajor: true },
                { key: "WRONG_FORMAT", value: "Format non respecté", isMajor: true },
                { key: "INCONSISTENT_METADATA", value: "Incohérence entre les métadonnées et les fichiers", isMajor: true },
                { key: "BAD_HIERARCHY", value: "Non-conformité de l'organisation", isMajor: true },
                { key: "BAD_NAME", value: "Non-conformité du nommage", isMajor: true },
                { key: "BAD_RESOLUTION", value: "Non-respect de la résolution", isMajor: true },
                { key: "BAD_THRESHOLD", value: "Non-respect du seuil", isMajor: true },
                { key: "MISSING_PAGE", value: "Page manquante", isMajor: true },
                { key: "TRUNCATED_INFORMATION", value: "Troncature d'information", isMajor: true },
                { key: "ANOTHER_MAJ", value: "Autre erreur", isMajor: true },
                { key: "WRONG_FRAMING", value: "Cadrage inadapté", isMajor: false },
                { key: "CHROMATIC_ANOMALY", value: "Dérive de la chromie", isMajor: false },
                { key: "GEOMETRICAL_ANOMALY", value: "Distorsions géométriques", isMajor: false },
                { key: "HALO_ON_IMAGE", value: "Halo sur l'image", isMajor: false },
                { key: "BLURRED_IMAGE", value: "Image floue", isMajor: false },
                { key: "SLANT_IMAGE", value: "Image inclinée", isMajor: false },
                { key: "BAD_OCR", value: "Non conformité du texte ocr", isMajor: false },
                { key: "SHADOW_ON_IMAGE", value: "Ombre portée", isMajor: false },
                { key: "WRONG_ORDER", value: "Ordre des vues non respecté", isMajor: false },
                { key: "ANOTHER_MIN", value: "Autre erreur", isMajor: false }
            ];

            return service;
        });
})();