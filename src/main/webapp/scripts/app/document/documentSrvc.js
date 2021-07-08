(function () {
    'use strict';

    angular.module('numaHopApp.service')
        .factory('DocUnitSrvc', function ($resource, CONFIGURATION, codeSrvc) {
            var service = $resource(CONFIGURATION.numahop.url + 'api/rest/docunit/:id', {
                id: '@identifier'
            }, {
                    dto: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            dto: true
                        }
                    },
                    deleteSelection: {
                        method: 'POST',
                        isArray: true,
                        params: {
                            delete: true
                        }
                    },
                    deleteDocUnitsProject: {
                        method: 'POST',
                        isArray: true,
                        params: {
                            deleteDocUnitsProject: true
                        }
                    },
                    updateSelection: {
                        method: 'POST',
                        isArray: true,
                        params: {
                            updateselection: true
                        }
                    },
                    search: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            search: true,
                            size: 50
                        }
                    },
                    searchAsList: {
                        method: 'POST',
                        isArray: false,
                        params: {
                            searchAsList: true,
                            size: 50
                        }
                    },
                    searchAsMinList: {
                        method: 'POST',
                        isArray: true,
                        params: {
                            searchAsMinList: true
                        }
                    },
                    addToProjectAndLot: {
                        method: 'POST',
                        isArray: true,
                        params: {
                            'projectAndLot': true
                        }
                    },
                    addToTrain: {
                        method: 'POST',
                        isArray: true,
                        params: {
                            'setTrain': true
                        }
                    },
                    removeProject: {
                        method: 'POST',
                        params: {
                            'removeProject': true
                        }
                    },
                    removeLot: {
                        method: 'POST',
                        params: {
                            'removeLot': true
                        }
                    },
                    removeTrain: {
                        method: 'POST',
                        isArray: false,
                        params: {
                            'removeTrain': true
                        }
                    },
                    unlink: {
                        method: 'POST',
                        isArray: true,
                        params: {
                            'unlink': true
                        }
                    },
                    removeAllFromLot: {
                        method: 'POST',
                        isArray: false,
                        params: {
                            'removeAllFromLot': true
                        }
                    },
                    lock: {
                        method: "GET",
                        isArray: false,
                        params: {
                            lock: true
                        }
                    },
                    unlock: {
                        method: "GET",
                        isArray: false,
                        params: {
                            unlock: true
                        }
                    },
                    inactiveDocUnit: {
                        method: "POST",
                        isArray: false,
                        params: {
                            inactiveDoc: true
                        }
                    },
                    iaArchiveArkUrl: {
                        method: "GET",
                        isArray: false,
                        params: {
                            arkurl: true
                        }
                    }
                });

            service.filterStatuses = [
                "VALIDATION_CONSTAT_ETAT",
                "LIVRAISON_DOCUMENT_EN_COURS",
                "CONTROLE_QUALITE_EN_COURS",
                "PREREJET_DOCUMENT",
                "PREVALIDATION_DOCUMENT",
                "VALIDATION_DOCUMENT",
                "VALIDATION_NOTICES",
                "ARCHIVAGE_DOCUMENT",
                "DIFFUSION_DOCUMENT",
                "DIFFUSION_DOCUMENT_DIGITAL_LIBRARY",
                "DIFFUSION_DOCUMENT_LOCALE",
                "DIFFUSION_DOCUMENT_OMEKA",
                "CLOTURE_DOCUMENT"];

            service.getConfigFilterStatuses = function () {
                return _.map(service.filterStatuses, function (st) {
                    return {
                        identifier: st,
                        label: codeSrvc['workflow.' + st] || st
                    };
                });
            };

            return service;
        });

    angular.module('numaHopApp.service')
        .factory('DigitalDocumentSrvc', function ($resource, CONFIGURATION, codeSrvc) {
            var service = $resource(CONFIGURATION.numahop.url + 'api/rest/digitaldocument/:id', {
                id: '@identifier'
            }, {
                    getByDocUnit: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            docUnit: true
                        }
                    },
                    getFilename: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            filename: true
                        }
                    },
                    getMasterPdfInfos: {
                        method: 'GET',
                        isArray: true,
                        params: {
                            masterPdfInfos: true
                        }
                    },
                    getMetadataForFiles: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            metadata: true
                        }
                    },
                    getMetadataForSample: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            samplemetadata: true
                        }
                    },
                    getFilenamesWithErrors: {
                        method: 'GET',
                        isArray: true,
                        params: {
                            filenamesErrors: true
                        }
                    },
                    getFilesWithErrors: {
                        method: 'GET',
                        isArray: true,
                        params: {
                            filesErrors: true
                        }
                    },
                    reject: {
                        method: 'POST',
                        params: {
                            checksOK: false
                        }
                    },
                    accept: {
                        method: 'POST',
                        params: {
                            checksOK: true
                        }
                    },
                    getToCheck: {
                        method: 'GET',
                        isArray: true,
                        params: {
                            toCheck: true
                        }
                    },
                    getPage: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            page: true
                        }
                    },
                    search: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            search: "",
                            size: 50
                        }
                    },
                    getDeliveryNotes: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            deliverynotes: true
                        }
                    },
                    getPiecesNb: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            nbPieces: true
                        }
                    }
                });

            service.controlStatus = [
                "TO_CHECK",
                "CHECKING",
                "PRE_REJECTED",
                "REJECTED"];

            service.getConfigControlStatus = function () {
                return _.map(service.controlStatus, function (st) {
                    return {
                        identifier: st,
                        label: codeSrvc['digitalDocument.' + st] || st
                    };
                });
            };

            return service;
        });


    angular.module('numaHopApp.service')
        .factory('SampleSrvc', function ($resource, CONFIGURATION) {
            var service = $resource(CONFIGURATION.numahop.url + 'api/rest/sample/:id', {
                id: '@identifier'
            }, {});
            return service;
        });



    angular.module('numaHopApp.service')
        .factory('PhysicalDocumentSrvc', function ($resource, CONFIGURATION) {
            var service = $resource(CONFIGURATION.numahop.url + 'api/rest/physicaldocument/:id', {
                id: '@identifier'
            }, {
                loadByDocUnitIds : {
                    method: 'GET',
                    isArray: true,
                    params: {
                        trainDocUnits: true
                    }
                }
            });
            return service;
        });

    angular.module('numaHopApp.service')
        .factory('RecordSrvc', function ($resource, CONFIGURATION) {
            var service = $resource(CONFIGURATION.numahop.url + 'api/rest/bibliographicrecord/:id', {
                id: '@identifier'
            }, {
                    dto: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            dto: true
                        }
                    },
                    dc: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            dc: true
                        }
                    },
                    duplicate: {
                        method: "GET",
                        isArray: false,
                        params: {
                            duplicate: true
                        }
                    },
                    allOperations: {
                        method: 'GET',
                        isArray: true,
                        params: {
                            all_operations: true
                        }
                    },
                    deleteSelection: {
                        method: 'POST',
                        params: {
                            delete: true
                        }
                    },
                    search: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            search: true,
                            size: 50
                        }
                    },
                    searchAsList: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            searchAsList: true,
                            size: 50
                        }
                    },
                    simpleDto: {
                        method: 'GET',
                        isArray: true,
                        params: {
                            simpleDto: true
                        }
                    },
                    update: {
                        method: "POST",
                        params: {
                            update: true
                        }
                    },
                    lock: {
                        method: "GET",
                        isArray: false,
                        params: {
                            lock: true
                        }
                    },
                    unlock: {
                        method: "GET",
                        isArray: false,
                        params: {
                            unlock: true
                        }
                    }
                });
            return service;
        });
})();
