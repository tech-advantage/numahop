(function () {
    'use strict';

    angular.module('numaHopApp.service').factory('LotSrvc', function ($resource, codeSrvc, CONFIGURATION, gettextCatalog) {
        var service = $resource(
            CONFIGURATION.numahop.url + 'api/rest/lot/:id',
            {
                id: '@identifier',
            },
            {
                dto: {
                    method: 'GET',
                    isArray: false,
                    params: {
                        dto: true,
                    },
                },
                dtoComplete: {
                    method: 'GET',
                    isArray: false,
                    params: {
                        dto: true,
                        complete: true,
                    },
                },
                findByProjectIds: {
                    method: 'GET',
                    isArray: false,
                    params: {
                        filterByProjects: '',
                    },
                },
                deleteSelection: {
                    method: 'POST',
                    params: {
                        delete: true,
                    },
                },
                search: {
                    method: 'GET',
                    isArray: false,
                    params: {
                        search: true,
                        size: 50,
                    },
                },
                postSearch: {
                    method: 'POST',
                    isArray: false,
                    params: {
                        search: true,
                        size: 50,
                    },
                },
                simpleDto: {
                    method: 'GET',
                    isArray: true,
                    params: {
                        simpleDto: true,
                    },
                },
                suggest: {
                    method: 'GET',
                    isArray: true,
                    params: {
                        suggest: true,
                    },
                },
                findSimpleByProject: {
                    method: 'GET',
                    isArray: true,
                    params: {
                        simpleByProject: true,
                    },
                },
                findSimpleByProjectForDocUnit: {
                    method: 'GET',
                    isArray: true,
                    params: {
                        simpleForDocUnit: true,
                    },
                },
                unlinkProject: {
                    method: 'POST',
                    params: {
                        unlinkProject: true,
                    },
                },
                addToProject: {
                    method: 'POST',
                    isArray: true,
                    params: {
                        project: true,
                    },
                },
                validate: {
                    method: 'POST',
                    params: {
                        validate: true,
                    },
                },
                loadLots: {
                    method: 'GET',
                    isArray: true,
                    params: {
                        widget: true,
                    },
                },
                closeLot: {
                    method: 'POST',
                    isArray: true,
                    params: {
                        cloturelot: true,
                    },
                },
                uncloseLot: {
                    method: 'POST',
                    isArray: true,
                    params: {
                        decloturelot: true,
                    },
                },
            }
        );

        service.config = {
            status: [
                { identifier: 'CREATED', label: codeSrvc['lot.status.CREATED'] },
                { identifier: 'PENDING', label: codeSrvc['lot.status.PENDING'] },
                { identifier: 'ONGOING', label: codeSrvc['lot.status.ONGOING'] },
                { identifier: 'CLOSED', label: codeSrvc['lot.status.CLOSED'] },
                { identifier: 'CANCELED', label: codeSrvc['lot.status.CANCELED'] },
            ],
            fileFormat: [
                { identifier: 'JP2', label: gettextCatalog.getString('JP2 (JPEG-2000 File Format Syntax)') },
                { identifier: 'JPEG', label: gettextCatalog.getString('JPEG (Joint Photographic Experts Group JFIF format)') },
                { identifier: 'JPG', label: gettextCatalog.getString('JPG (Joint Photographic Experts Group JFIF format)') },
                { identifier: 'PNG', label: gettextCatalog.getString('PNG (Portable Network Graphics)') },
                { identifier: 'GIF', label: gettextCatalog.getString('GIF (Graphics Interchange Format)') },
                { identifier: 'SVG', label: gettextCatalog.getString('SVG (Scalable Vector Graphic)') },
                { identifier: 'TIFF', label: gettextCatalog.getString('TIFF (Tagged Image File Format)') },
                { identifier: 'TIF', label: gettextCatalog.getString('TIF (Tagged Image File Format)') },
                { identifier: 'PDF', label: gettextCatalog.getString('PDF') },
            ],
            colorspace: {
                sRGB: gettextCatalog.getString('profil sRGB'),
                'Adobe RGB': gettextCatalog.getString('profil Adobe RGB (1998)'),
            },
            boolean: {
                true: gettextCatalog.getString('Oui'),
                false: gettextCatalog.getString('Non'),
            },
            category: {
                PROVIDER: gettextCatalog.getString('Prestataire'),
                OTHER: gettextCatalog.getString('Utilisateur'),
            },
            type: {
                PHYSICAL: gettextCatalog.getString('Physique'),
                DIGITAL: gettextCatalog.getString('Numérique'),
            },
        };

        return service;
    });
})();
