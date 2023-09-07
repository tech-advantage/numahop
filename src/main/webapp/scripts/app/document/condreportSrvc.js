(function () {
    'use strict';

    angular.module('numaHopApp.service').factory('CondreportSrvc', function ($resource, CONFIGURATION, gettextCatalog) {
        var service = $resource(
            CONFIGURATION.numahop.url + 'api/rest/condreport/:id',
            { id: '@identifier' },
            {
                search: {
                    method: 'POST',
                    isArray: false,
                    params: {
                        search: true,
                    },
                },
                downloadSlip: {
                    method: 'GET',
                    isArray: false,
                    params: {
                        csv: true,
                    },
                },
                getSummary: {
                    method: 'GET',
                    isArray: true,
                    params: {
                        summary: true,
                    },
                },
                propagate: {
                    method: 'POST',
                    isArray: true,
                    params: {
                        propagate: true,
                    },
                },
            }
        );
        service.types = {
            LIBRARY_LEAVING: {
                code: 'LIBRARY_LEAVING',
                label: gettextCatalog.getString('État initial'),
                description: gettextCatalog.getString('État initial'),
                pos: 1,
            },
            DIGITALIZATION: {
                code: 'DIGITALIZATION',
                label: gettextCatalog.getString('État constaté par le prestataire avant numérisation'),
                description: gettextCatalog.getString('État constaté par le prestataire'),
                pos: 2,
            },
            LIBRARY_BACK: {
                code: 'LIBRARY_BACK',
                label: gettextCatalog.getString('État constaté par le prestataire après numérisation'),
                description: gettextCatalog.getString('État constaté par le prestataire après numérisation'),
                pos: 3,
            },
            LIBRARY_RETURN: {
                code: 'LIBRARY_RETURN',
                label: gettextCatalog.getString('État constaté au retour'),
                description: gettextCatalog.getString('État constaté au retour '),
                pos: 4,
            },
            LIBRARY_NEW_DIGIT: {
                code: 'LIBRARY_NEW_DIGIT',
                label: gettextCatalog.getString('État constaté pour le départ pour une reprise de numérisation'),
                description: gettextCatalog.getString('État constaté pour le départ pour une reprise de numérisation'),
                pos: 5,
            },
        };
        return service;
    });

    angular.module('numaHopApp.service').factory('CondreportDetailSrvc', function ($resource, CONFIGURATION) {
        var service = $resource(
            CONFIGURATION.numahop.url + 'api/rest/condreport_detail/:id',
            { id: '@identifier' },
            {
                confirmInitialValid: {
                    method: 'POST',
                    isArray: false,
                    params: {
                        confirmvalid: true,
                    },
                },
            }
        );
        return service;
    });

    angular.module('numaHopApp.service').factory('CondreportAttachmentSrvc', function ($resource, CONFIGURATION) {
        return $resource(CONFIGURATION.numahop.url + 'api/rest/condreport_attachment/:id', {
            id: '@identifier',
        });
    });
})();
