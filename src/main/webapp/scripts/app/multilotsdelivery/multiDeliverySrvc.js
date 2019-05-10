(function () {
    'use strict';

    angular.module('numaHopApp.service')
        .factory('MultiDeliverySrvc', function ($resource, CONFIGURATION) {
            var service = $resource(CONFIGURATION.numahop.url + 'api/rest/multidelivery/:id', {
                id: '@identifier'
            }, {
                    search: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            search: true,
                            size: 50
                        }
                    },
                    findAll: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            'dto': ""
                        }
                    },
                    findByProjectIdsLotsIds: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            'filterByProjectsLots': ""
                        }
                    },
                    deliver: {
                        method: 'POST',
                        params: {
                            deliver: true
                        }
                    },
                    predeliver: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            predeliver: true
                        }
                    },
                    getActiveCheckConfig: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            checkConfig: true
                        }
                    },
                    getDigitalDocuments: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            digitalDocuments: true
                        }
                    },
                    getSample: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            deliverySample: true
                        }
                    },
                    getDeliveryReport: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            deliveryReport: true
                        }
                    },
                    getDeliveryStatus: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            delivstatus: true
                        }
                    },
                    getDeliveryForViewer: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            forViewer: true
                        }
                    },
                    loadDeliveries: {
                        method: 'GET',
                        isArray: true,
                        params: {
                            widget: true
                        }
                    },
                    detachDigitalDoc: {
                        method: 'POST',
                        isArray: false,
                        params: {
                            detachDoc: true
                        }
                    },
                    getPreviousCheckSlips: {
                        method: 'GET',
                        isArray: true,
                        params: {
                            previouscheckslip: true
                        }
                    }
                });

            return service;
        });
})();