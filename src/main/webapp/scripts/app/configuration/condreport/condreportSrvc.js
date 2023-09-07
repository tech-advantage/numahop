(function () {
    'use strict';

    angular.module('numaHopApp.service').factory('CondreportDescPropertySrvc', CondreportDescPropertySrvc);
    angular.module('numaHopApp.service').factory('CondreportDescValueSrvc', CondreportDescValueSrvc);
    angular.module('numaHopApp.service').factory('CondreportPropertyConfSrvc', CondreportPropertyConfSrvc);

    function CondreportDescPropertySrvc(CONFIGURATION, $resource, gettextCatalog) {
        var service = $resource(
            CONFIGURATION.numahop.url + 'api/rest/condreport_desc_prop/:id',
            { id: '@identifier' },
            {
                getAllWithFakes: {
                    method: 'GET',
                    isArray: true,
                    params: {
                        getAllWithFakes: true,
                    },
                },
            }
        );

        var fakeProperties = [
            {
                identifier: 'DIMENSION',
                label: gettextCatalog.getString('Dimensions du document (H/L/P, mm)'),
                type: 'DESCRIPTION',
                fake: true,
            },
            {
                identifier: 'BINDING_DESC',
                label: gettextCatalog.getString('Autres informations'),
                type: 'BINDING',
                fake: true,
            },
            {
                identifier: 'BODY_DESC',
                label: gettextCatalog.getString('Autres informations'),
                type: 'VIGILANCE',
                fake: true,
            },
            {
                identifier: 'INSURANCE',
                label: gettextCatalog.getString('Assurance'),
                type: 'DESCRIPTION',
                fake: true,
            },
            {
                identifier: 'NB_VIEW_BINDING',
                label: gettextCatalog.getString('Estimation du nombre de vues - Reliure'),
                type: 'DESCRIPTION',
                fake: true,
            },
            {
                identifier: 'NB_VIEW_BODY',
                label: gettextCatalog.getString("Estimation du nombre de vues - Corps d'ouvrage"),
                type: 'DESCRIPTION',
                fake: true,
            },
            {
                identifier: 'NB_VIEW_ADDITIONNAL',
                label: gettextCatalog.getString('Estimation du nombre de vues - Vues Supplémentaires'),
                type: 'DESCRIPTION',
                fake: true,
            },
            {
                identifier: 'ADDITIONNAL_DESC',
                label: gettextCatalog.getString('Synthèse'),
                type: 'DESCRIPTION',
                fake: true,
            },
        ];

        service.getFakeProperty = function (id) {
            return _.find(fakeProperties, function (p) {
                return p.identifier === id;
            });
        };
        service.queryAll = function () {
            return service.query().$promise.then(function (properties) {
                return properties.concat(fakeProperties);
            });
        };
        service.queryAllWithoutFakes = function () {
            return service.query().$promise.then(function (properties) {
                return properties;
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
            { code: 'DESCRIPTION', label: gettextCatalog.getString('Description du document') },
            { code: 'NUMBERING', label: gettextCatalog.getString('Numérotation') },
            { code: 'BINDING', label: gettextCatalog.getString('État de la reliure') },
            { code: 'VIGILANCE', label: gettextCatalog.getString('Points de vigilance') },
            { code: 'STATE', label: gettextCatalog.getString('État du document') },
            { code: 'TYPE', label: gettextCatalog.getString('Type de document') },
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
