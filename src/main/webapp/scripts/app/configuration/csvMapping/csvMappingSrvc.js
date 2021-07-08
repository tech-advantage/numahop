(function () {
    'use strict';

    angular.module('numaHopApp.service')
        .factory('CSVMappingSrvc', CSVMappingSrvc);

    function CSVMappingSrvc($resource, CONFIGURATION, gettextCatalog) {
        var service = $resource(CONFIGURATION.numahop.url + 'api/rest/csvmapping/:id', { id: '@identifier' }, {
            duplicate: {
                method: 'GET',
                isArray: false,
                params: {
                    'duplicate': true
                }
            }
        });
        service.docUnitFields = [
            { code: null, label: "", rank: 0 },
            { code: "pgcnId", label: gettextCatalog.getString("PGCN Id"), rank: 1 },
            { code: "label", label: gettextCatalog.getString("Libellé"), rank: 2 },
            { code: "type", label: gettextCatalog.getString("Type"), rank: 3 },
            { code: "collectionIA", label: gettextCatalog.getString("Collection IA"), rank: 4 },
            { code: "rights", label: gettextCatalog.getString("Droits"), rank: 5 },
            { code: "planClassementPAC", label: gettextCatalog.getString("Pac"), rank: 6 },
            { code: "digitalId", label: gettextCatalog.getString("Radical"), rank: 7 },
            { code: "format", label: gettextCatalog.getString("Format"), rank: 9 }];
        
        service.bibRecordFields = [
            { code: null, label: "", rank: 0 },
            { code: "title", label: gettextCatalog.getString("Titre de la notice"), rank: 1 },
            { code: "sigb", label: gettextCatalog.getString("SIGB"), rank: 2 },
            { code: "sudoc", label: gettextCatalog.getString("SUDOC"), rank: 3 },
            { code: "calames", label: gettextCatalog.getString("Calames"), rank: 4 },
            { code: "docElectronique", label: gettextCatalog.getString("Document électronique"), rank: 5 }];
        
        return service;
    }
})();
