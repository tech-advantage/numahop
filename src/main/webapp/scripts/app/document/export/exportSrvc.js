(function () {
    'use strict';

    angular.module('numaHopApp.service').factory('ExportSrvc', ExportSrvc);
    angular.module('numaHopApp.service').factory('ExportCinesSrvc', ExportCinesSrvc);
    angular.module('numaHopApp.service').factory('ExportInternetArchiveSrvc', ExportInternetArchiveSrvc);
    angular.module('numaHopApp.service').factory('ExportOmekaSrvc', ExportOmekaSrvc);
    angular.module('numaHopApp.service').factory('ExportDigitalLibrarySrvc', ExportDigitalLibrarySrvc);

    function ExportSrvc(CONFIGURATION, $resource) {
        return $resource(
            CONFIGURATION.numahop.url + 'api/rest/export/:svc',
            { svc: '@svc' },
            {
                toCines: {
                    method: 'POST',
                    isArray: false,
                    params: {
                        svc: 'cines',
                        send: true,
                    },
                },
                toOmeka: {
                    method: 'POST',
                    isArray: false,
                    params: {
                        svc: 'omeka',
                        sendomeka: true,
                    },
                },
                toDigitalLibrary: {
                    method: 'POST',
                    isArray: false,
                    params: {
                        svc: 'digitalLibrary',
                        send: true,
                    },
                },
                getAip: {
                    method: 'GET',
                    isArray: false,
                    headers: { Accept: 'application/xml' },
                    responseType: 'arraybuffer',
                    params: {
                        svc: 'cines',
                        aip: true,
                    },
                    transformResponse: function (data) {
                        var xml;
                        if (data) {
                            xml = new Blob([data], {
                                type: 'application/xml',
                            });
                        }
                        return {
                            response: xml,
                        };
                    },
                },
                getSip: {
                    method: 'GET',
                    isArray: false,
                    headers: { Accept: 'application/xml' },
                    responseType: 'arraybuffer',
                    params: {
                        svc: 'cines',
                        sip: true,
                    },
                    transformResponse: function (data) {
                        var xml;
                        if (data) {
                            xml = new Blob([data], {
                                type: 'application/xml',
                            });
                        }
                        return {
                            response: xml,
                        };
                    },
                },
                getMets: {
                    method: 'GET',
                    isArray: false,
                    headers: { Accept: 'application/xml' },
                    responseType: 'arraybuffer',
                    params: {
                        svc: 'cines',
                        mets: true,
                    },
                    transformResponse: function (data) {
                        var xml;
                        if (data) {
                            xml = new Blob([data], {
                                type: 'application/xml',
                            });
                        }
                        return {
                            response: xml,
                        };
                    },
                },
                csv: {
                    method: 'GET',
                    isArray: false,
                    params: {
                        svc: 'csv',
                    },
                },
            }
        );
    }

    function ExportCinesSrvc($resource, codeSrvc, CONFIGURATION) {
        var service = $resource(
            CONFIGURATION.numahop.url + 'api/rest/export/cines/:id',
            {
                id: '@identifier',
            },
            {
                export: {
                    method: 'GET',
                    isArray: false,
                    params: {
                        export: true,
                    },
                },
                massExport: {
                    method: 'GET',
                    isArray: false,
                    params: {
                        mass_export: true,
                    },
                },
                save: {
                    method: 'POST',
                    isArray: false,
                    params: {
                        save: true,
                    },
                },
            }
        );

        service.config = {
            status: [
                { identifier: 'EXPORTING', label: codeSrvc['EXPORTING'] },
                { identifier: 'SENDING', label: codeSrvc['SENDING'] },
                { identifier: 'SENT', label: codeSrvc['SENT'] },
                { identifier: 'AR_RECEIVED', label: codeSrvc['AR_RECEIVED'] },
                { identifier: 'REJECTED', label: codeSrvc['REJECTED'] },
                { identifier: 'ARCHIVED', label: codeSrvc['ARCHIVED'] },
                { identifier: 'FAILED', label: codeSrvc['FAILED'] },
            ],
        };
        return service;
    }

    function ExportOmekaSrvc($resource, codeSrvc, CONFIGURATION) {
        var service = $resource(
            CONFIGURATION.numahop.url + 'api/rest/export/omeka/:id',
            {
                id: '@identifier',
            },
            {
                prepare: {
                    method: 'GET',
                    isArray: false,
                    params: {
                        prepare: true,
                    },
                },
                create: {
                    method: 'POST',
                    isArray: false,
                    params: {
                        create: true,
                    },
                },
                save: {
                    method: 'POST',
                    isArray: false,
                    params: {
                        save: true,
                    },
                },
                massExport: {
                    method: 'GET',
                    isArray: false,
                    params: {
                        mass_export: true,
                    },
                },
            }
        );

        service.config = {
            status: [
                { identifier: 'NONE', label: codeSrvc['NONE'] },
                { identifier: 'IN_PROGRESS', label: codeSrvc['IN_PROGRESS'] },
                { identifier: 'SENT', label: codeSrvc['SENT'] },
                { identifier: 'FAILED', label: codeSrvc['FAILED'] },
            ],
        };
        return service;
    }

    function ExportInternetArchiveSrvc($resource, codeSrvc, CONFIGURATION) {
        var service = $resource(
            CONFIGURATION.numahop.url + 'api/rest/internet_archive/:id',
            {
                id: '@identifier',
            },
            {
                prepare: {
                    method: 'GET',
                    isArray: false,
                    params: {
                        prepare_item: true,
                    },
                },
                create: {
                    method: 'POST',
                    isArray: false,
                    params: {
                        create: true,
                    },
                },
                save: {
                    method: 'POST',
                    isArray: false,
                    params: {
                        save: true,
                    },
                },
                massExport: {
                    method: 'GET',
                    isArray: false,
                    params: {
                        mass_export: true,
                    },
                },
            }
        );

        service.config = {
            status: [
                { identifier: 'EXPORTING', label: codeSrvc['EXPORTING'] },
                { identifier: 'SENDING', label: codeSrvc['SENDING'] },
                { identifier: 'SENT', label: codeSrvc['SENT'] },
                { identifier: 'ARCHIVED', label: codeSrvc['ARCHIVED'] },
                { identifier: 'FAILED', label: codeSrvc['FAILED'] },
            ],
        };
        return service;
    }

    function ExportDigitalLibrarySrvc($resource, codeSrvc, CONFIGURATION) {
        var service = $resource(
            CONFIGURATION.numahop.url + 'api/rest/export/digitalLibrary/:id',
            {
                id: '@identifier',
            },
            {
                massExport: {
                    method: 'GET',
                    isArray: false,
                    params: {
                        mass_export: true,
                    },
                },
            }
        );

        service.config = {
            status: [
                { identifier: 'EXPORTING', label: codeSrvc['EXPORTING'] },
                { identifier: 'SENDING', label: codeSrvc['SENDING'] },
                { identifier: 'SENT', label: codeSrvc['SENT'] },
                { identifier: 'ARCHIVED', label: codeSrvc['ARCHIVED'] },
                { identifier: 'FAILED', label: codeSrvc['FAILED'] },
            ],
        };
        return service;
    }
})();
