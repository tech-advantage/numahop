(function () {
    'use strict';

    angular.module('numaHopApp.service').factory('MappingSrvc', MappingSrvc);

    function MappingSrvc($resource, CONFIGURATION, gettextCatalog) {
        var service = $resource(
            CONFIGURATION.numahop.url + 'api/rest/mapping/:id',
            { id: '@identifier' },
            {
                duplicate: {
                    method: 'GET',
                    isArray: false,
                    params: {
                        duplicate: true,
                    },
                },
            }
        );

        service.docUnitFields = [
            { code: null, label: '', rank: 0 },
            { code: 'pgcnId', label: gettextCatalog.getString('PGCN Id'), rank: 1 },
            { code: 'label', label: gettextCatalog.getString('Libellé'), rank: 2 },
            { code: 'type', label: gettextCatalog.getString('Type'), rank: 3 },
            { code: 'collectionIA', label: gettextCatalog.getString('Collection IA'), rank: 4 },
            { code: 'rights', label: gettextCatalog.getString('Droits'), rank: 5 },
            { code: 'planClassementPAC', label: gettextCatalog.getString('Pac'), rank: 6 },
            { code: 'imageHeight', label: gettextCatalog.getString('Hauteur définition image'), rank: 7 },
            { code: 'imageWidth', label: gettextCatalog.getString('Largeur définition image'), rank: 8 },
            { code: 'digitalId', label: gettextCatalog.getString('Radical'), rank: 9 },
            { code: 'archivable', label: gettextCatalog.getString('Archivable'), rank: 10 },
            { code: 'distributable', label: gettextCatalog.getString('Diffusable'), rank: 11 },
            { code: 'condReportType', label: gettextCatalog.getString("Type de constat d'état"), rank: 12 },
            { code: 'omekaCollection', label: gettextCatalog.getString('Collection Omeka'), rank: 13 },
            { code: 'omekaItem', label: gettextCatalog.getString("Type d'item Omeka"), rank: 14 },
        ];

        service.bibRecordFields = [
            { code: null, label: '', rank: 0 },
            { code: 'title', label: gettextCatalog.getString('Titre de la notice'), rank: 1 },
            { code: 'sigb', label: gettextCatalog.getString('SIGB'), rank: 2 },
            { code: 'sudoc', label: gettextCatalog.getString('SUDOC'), rank: 3 },
            { code: 'calames', label: gettextCatalog.getString('Calames'), rank: 4 },
            { code: 'docElectronique', label: gettextCatalog.getString('Document électronique'), rank: 5 },
        ];

        return service;
    }
})();
