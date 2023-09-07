(function () {
    'use strict';

    angular.module('numaHopApp.service').factory('DeliverySrvc', function ($filter, $resource, CONFIGURATION, gettextCatalog) {
        var service = $resource(
            CONFIGURATION.numahop.url + 'api/rest/delivery/:id',
            {
                id: '@identifier',
            },
            {
                search: {
                    method: 'GET',
                    isArray: false,
                    params: {
                        search: true,
                        size: 50,
                    },
                },
                findAll: {
                    method: 'GET',
                    isArray: false,
                    params: {
                        dto: '',
                    },
                },
                findByProjectIdsLotsIds: {
                    method: 'GET',
                    isArray: true,
                    params: {
                        filterByProjectsLots: '',
                    },
                },
                deliver: {
                    method: 'POST',
                    params: {
                        deliver: true,
                    },
                },
                predeliver: {
                    method: 'GET',
                    isArray: false,
                    params: {
                        predeliver: true,
                    },
                },
                getActiveCheckConfig: {
                    method: 'GET',
                    isArray: false,
                    params: {
                        checkConfig: true,
                    },
                },
                getDigitalDocuments: {
                    method: 'GET',
                    isArray: true,
                    params: {
                        digitalDocuments: true,
                    },
                },
                getSample: {
                    method: 'GET',
                    isArray: false,
                    params: {
                        deliverySample: true,
                    },
                },
                duplicate: {
                    method: 'GET',
                    isArray: false,
                    params: {
                        duplicate: true,
                    },
                },
                getDeliveryReport: {
                    method: 'GET',
                    isArray: false,
                    params: {
                        deliveryReport: true,
                    },
                },
                getDeliveryStatus: {
                    method: 'GET',
                    isArray: false,
                    params: {
                        delivstatus: true,
                    },
                },
                getDeliveryForViewer: {
                    method: 'GET',
                    isArray: false,
                    params: {
                        forViewer: true,
                    },
                },
                loadDeliveries: {
                    method: 'GET',
                    isArray: true,
                    params: {
                        widget: true,
                    },
                },
                loadSampledDeliveries: {
                    method: 'GET',
                    isArray: true,
                    params: {
                        sampledWidget: true,
                    },
                },
                detachDigitalDoc: {
                    method: 'POST',
                    isArray: false,
                    params: {
                        detachDoc: true,
                    },
                },
                getPreviousCheckSlips: {
                    method: 'GET',
                    isArray: true,
                    params: {
                        previouscheckslip: true,
                    },
                },
                getDeliveryProgress: {
                    method: 'GET',
                    isArray: false,
                    params: {
                        delivprogress: true,
                    },
                },
                getDiskUsageInfos: {
                    method: 'GET',
                    isArray: false,
                    params: {
                        diskspace: true,
                        widget: true,
                    },
                },
            }
        );

        service.config = {
            payment: {
                PAID: gettextCatalog.getString('Payé'),
                UNPAID: gettextCatalog.getString('Non payé'),
            },
            sampleMode: {
                NO_SAMPLE: gettextCatalog.getString("Pas d'échantillonnage"),
                SAMPLE_DOC_DELIV: gettextCatalog.getString('Documents dans la livraison'),
                SAMPLE_PAGE_ONE_DOC: gettextCatalog.getString('Pages dans chaque document'),
                SAMPLE_PAGE_ALL_DOC: gettextCatalog.getString('Pages dans tous les documents'),
            },
            status: [
                { identifier: 'SAVED', label: gettextCatalog.getString('Sauvegardé') },
                { identifier: 'DELIVERING', label: gettextCatalog.getString('En cours de livraison') },
                { identifier: 'DELIVERING_ERROR', label: gettextCatalog.getString('Erreur de livraison') },
                { identifier: 'TO_BE_CONTROLLED', label: gettextCatalog.getString('À contrôler') },
                { identifier: 'AUTOMATICALLY_REJECTED', label: gettextCatalog.getString('Rejeté automatiquement') },
                { identifier: 'TREATED', label: gettextCatalog.getString('Traité') },
                { identifier: 'CLOSED', label: gettextCatalog.getString('Clôturé') },
            ],
        };
        return service;
    });
})();
