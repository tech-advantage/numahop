(function () {
    'use strict';

    angular.module('numaHopApp.service').factory('TrainSrvc', function ($resource, codeSrvc, CONFIGURATION) {
        var service = $resource(
            CONFIGURATION.numahop.url + 'api/rest/train/:id',
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
                    isArray: true,
                    params: {
                        filterByProjects: true,
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
            }
        );

        service.config = {
            status: {
                CREATED: codeSrvc['train.status.CREATED'],
                IN_PREPARATION: codeSrvc['train.status.IN_PREPARATION'],
                IN_DIGITIZATION: codeSrvc['train.status.IN_DIGITIZATION'],
                RECEIVING_PHYSICAL_DOCUMENTS: codeSrvc['train.status.RECEIVING_PHYSICAL_DOCUMENTS'],
                CLOSED: codeSrvc['train.status.CLOSED'],
                CANCELED: codeSrvc['train.status.CANCELED'],
            },
        };
        return service;
    });
})();
