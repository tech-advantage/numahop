(function () {
    'use strict';

    angular.module('numaHopApp.service').factory('CondreportDescPropertySrvc', CondreportDescPropertySrvc);
    angular.module('numaHopApp.service').factory('CondreportDescValueSrvc', CondreportDescValueSrvc);
    angular.module('numaHopApp.service').factory('CondreportPropertyConfSrvc', CondreportPropertyConfSrvc);

    function CondreportDescPropertySrvc(CONFIGURATION, $resource, gettextCatalog) {
        var service = $resource(CONFIGURATION.numahop.url + 'api/rest/condreport_desc_prop/:id', { id: '@identifier' }, {});

        var fakeProperties = [{
            identifier: "DIMENSION",
            label: gettextCatalog.getString("Dimensions du document (H/L/P, mm)"),
            type: "DESCRIPTION",
            fake: true
        }, {
            identifier: "BINDING_DESC",
            label: gettextCatalog.getString("Autres informations"),
            type: "BINDING",
            fake: true
        }, {
            identifier: "BODY_DESC",
            label: gettextCatalog.getString("Autres informations"),
            type: "VIGILANCE",
            fake: true
        }];

        service.getFakeProperty = function (id) {
            return _.find(fakeProperties, function (p) {
                return p.identifier === id;
            });
        };
        service.queryAll = function () {
            return service.query().$promise
                .then(function (properties) {
                    return properties.concat(fakeProperties);
                });
        };
        service.fakeProperties = fakeProperties;
        return service;
    }

    function CondreportDescValueSrvc(CONFIGURATION, $resource) {
        return $resource(CONFIGURATION.numahop.url + 'api/rest/condreport_desc_value/:id', { id: '@identifier' }, {});
    }

    function CondreportPropertyConfSrvc(CONFIGURATION, $resource, gettextCatalog) {
        var service = $resource(CONFIGURATION.numahop.url + 'api/rest/condreport_prop_conf/:id', { id: '@identifier' }, {});

        service.types = [
            { code: "DESCRIPTION", label: gettextCatalog.getString("Description du document") },
            { code: "NUMBERING", label: gettextCatalog.getString("Numérotation") },
            { code: "BINDING", label: gettextCatalog.getString("État de la reliure") },
            { code: "VIGILANCE", label: gettextCatalog.getString("Points de vigilance") }
        ];

        service.getType = function (code) {
            return find(code, service.types);
        };

        service.getTypeLabel = function (code) {
            var found = service.getType(code);
            return found ? found.label : code;
        };

        function find(code, list) {
            if (!code) {
                return;
            }
            var found = _.find(list, function (p) {
                return p.code === code;
            });
            return found;
        }

        return service;
    }
})();
