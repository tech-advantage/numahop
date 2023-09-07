(function () {
    'use strict';

    angular.module('numaHopApp.service').factory('ImagesMetadataSrvc', ImagesMetadataSrvc);

    function ImagesMetadataSrvc($resource, CONFIGURATION, gettextCatalog) {
        var service = $resource(
            CONFIGURATION.numahop.url + 'api/rest/imagemetadata/:id',
            { id: '@identifier' },
            {
                saveList: {
                    method: 'POST',
                    isArray: true,
                    params: {
                        saveList: true,
                    },
                },
                saveValues: {
                    method: 'POST',
                    isArray: true,
                    params: {
                        saveValues: true,
                    },
                },
                getMetaValues: {
                    method: 'GET',
                    isArray: true,
                    params: {
                        getMetaValues: true,
                    },
                },
            }
        );

        service.metadataPropertyTypes = [
            {
                label: 'Chaine de caractère',
                code: 'STRING',
            },
            {
                label: 'Booléen',
                code: 'BOOLEAN',
            },
            {
                label: 'Réel',
                code: 'REAL',
            },
            {
                label: 'Entier',
                code: 'INTEGER',
            },
            {
                label: 'Date',
                code: 'DATE',
            },
        ];

        return service;
    }
})();
