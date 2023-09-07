(function () {
    'use strict';

    angular.module('numaHopApp.service').factory('ProjectSrvc', function ($resource, CONFIGURATION, gettextCatalog) {
        var service = $resource(
            CONFIGURATION.numahop.url + 'api/rest/project/:id',
            {
                id: '@identifier',
            },
            {
                dto: {
                    method: 'GET',
                    isArray: true,
                    params: {
                        dto: true,
                    },
                },
                dtoComplete: {
                    method: 'GET',
                    isArray: true,
                    params: {
                        dto2: true,
                    },
                },
                searchProject: {
                    method: 'GET',
                    isArray: true,
                    params: {
                        searchProject: true,
                    },
                },
                addDocUnits: {
                    method: 'POST',
                    params: {
                        idDocs: true,
                    },
                },
                addLibraries: {
                    method: 'POST',
                    params: {
                        idLibraries: true,
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
                loadProjects: {
                    method: 'GET',
                    isArray: true,
                    params: {
                        widget: true,
                    },
                },
                cancelProject: {
                    method: 'POST',
                    params: {
                        cancelProj: true,
                    },
                },
                suspendProject: {
                    method: 'POST',
                    params: {
                        suspendProj: true,
                    },
                },
                reactivateProject: {
                    method: 'POST',
                    params: {
                        reactivProj: true,
                    },
                },
            }
        );

        service.config = {
            status: {
                CREATED: gettextCatalog.getString('Créé'),
                ONGOING: gettextCatalog.getString('En cours'),
                PENDING: gettextCatalog.getString('En attente'),
                CANCELED: gettextCatalog.getString('Annulé'),
                CLOSED: gettextCatalog.getString('Clôturé'),
            },
        };
        return service;
    });
})();
