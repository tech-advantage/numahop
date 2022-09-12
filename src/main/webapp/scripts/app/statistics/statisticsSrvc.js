(function () {
    'use strict';

    angular.module('numaHopApp.service')
        .factory('StatisticsSrvc', function ($httpParamSerializer, $resource, codeSrvc, CONFIGURATION) {
            var service = $resource(CONFIGURATION.numahop.url + 'api/rest/statistics/:stype', {
                stype: '@stype'
            }, {
                    docPublished: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            stype: "docunit",
                            doc_published: true
                        }
                    },
                    docRejected: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            stype: "docunit",
                            doc_rejected: true
                        }
                    },
                    docUnitArchive: {
                        method: 'GET',
                        isArray: true,
                        params: {
                            stype: "docunit",
                            archive: true
                        }
                    },
                    docUnitAverage: {
                        method: 'GET',
                        isArray: true,
                        params: {
                            stype: "docunit",
                            average: true
                        }
                    },
                    docUnitCheckDelay: {
                        method: 'GET',
                        isArray: true,
                        params: {
                            stype: "docunit",
                            checkdelay: true
                        }
                    },
                    docUnitExport: {
                        method: 'GET',
                        isArray: true,
                        params: {
                            stype: "docunit",
                            export: true
                        }
                    },
                    docUnitList: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            stype: "docunit",
                            count: true
                        }
                    },
                    docUnitTypes: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            stype: "docunit",
                            doc_types: true
                        }
                    },
                    getDeliveryGroupByStatus: {
                        method: 'GET',
                        isArray: true,
                        params: {
                            deliveryGroupByStatus: true
                        }
                    },
                    getDocUnitsGroupByStatus: {
                        method: 'GET',
                        isArray: true,
                        params: {
                            docunitGroupByStatus: true
                        }
                    },
                    getDocUnitStatusRatio: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            stype: "docunit",
                            countStatus: true
                        }
                    },
                    getLotGroupByStatus: {
                        method: 'GET',
                        isArray: true,
                        params: {
                            lotGroupByStatus: true
                        }
                    },
                    getProjectGroupByStatus: {
                        method: 'GET',
                        isArray: true,
                        params: {
                            projectGroupByStatus: true
                        }
                    },
                    getTrainGroupByStatus: {
                        method: 'GET',
                        isArray: true,
                        params: {
                            trainGroupByStatus: true
                        }
                    },
                    getUsersGroupByLibrary: {
                        method: 'GET',
                        isArray: true,
                        params: {
                            userGroupByLibrary: true
                        }
                    },
                    lotProgress: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            lotProgress: true
                        }
                    },
                    projectList: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            projectList: true
                        }
                    },
                    projectProgress: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            projectProgress: true
                        }
                    },
                    providerDelivery: {
                        method: 'GET',
                        isArray: true,
                        params: {
                            stype: "delivery",
                            provider_delivery: true
                        }
                    },
                    providerTrain: {
                        method: 'GET',
                        isArray: true,
                        params: {
                            provider_train: true
                        }
                    },
                    workflowDelivery: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            stype: "workflow",
                            wdelivery: true
                        }
                    },
                    workflowDocUnit: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            stype: "workflow",
                            wdocunit: true
                        }
                    },
                    workflowDocUnitPending: {
                        method: 'GET',
                        isArray: true,
                        params: {
                            stype: "workflow",
                            wdocunitpending: true
                        }
                    },
                    workflowControl: {
                        method: 'GET',
                        isArray: true,
                        params: {
                            stype: "workflow",
                            wcontrol: true
                        }
                    },
                    workflowProfileActivity: {
                        method: 'GET',
                        isArray: true,
                        params: {
                            stype: "workflow",
                            wprofile_activity: true
                        }
                    },
                    workflowState: {
                        method: 'GET',
                        isArray: true,
                        params: {
                            stype: "workflow",
                            wstate: true
                        }
                    },
                    workflowUser: {
                        method: 'GET',
                        isArray: true,
                        params: {
                            stype: "workflow",
                            wuser: true
                        }
                    },
                    workflowUserActivity: {
                        method: 'GET',
                        isArray: true,
                        params: {
                            stype: "workflow",
                            wuser_activity: true
                        }
                    }
                });

            service.getExportUrl = function (params, type) {
                var baseUrl = "api/rest/statistics/";
                if (type) {
                    baseUrl += type + "/";
                }
                baseUrl += "csv?";
                baseUrl += $httpParamSerializer(params);
                return baseUrl;
            };

            /**
             * Libellé à partir du code
             * @param {*} code
             * @param {*} svcName
             */
            service.getLabel = function (svcName, code) {
                switch (svcName) {
                    case "getProjectGroupByStatus":
                        return codeSrvc["project." + code] || code;
                    case "getLotGroupByStatus":
                        return codeSrvc["lot.status." + code] || code;
                    case "getTrainGroupByStatus":
                        return codeSrvc["train.status." + code] || code;
                    case "getDeliveryGroupByStatus":
                        return codeSrvc["delivery." + code] || code;
                    case "getDocUnitsGroupByStatus":
                        return codeSrvc["workflow." + code] || code;
                    default:
                        return code;
                }
            };
            return service;
        });
})();
