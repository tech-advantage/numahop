(function () {
    'use strict';

    angular.module('numaHopApp.service')
        .factory('WorkflowSrvc', function ($resource, codeSrvc, CONFIGURATION) {
            var service = $resource(CONFIGURATION.numahop.url + 'api/rest/workflow/:id', {
                id: '@identifier'
            }, {
                    dto: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            dto: true
                        }
                    },
                    search: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            search: true,
                            size: 50
                        }
                    },
                    isDone: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            isDone: true
                        }
                    },
                    isWorkflowStarted: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            isWorkflowStarted: true
                        }
                    },
                    isRejectDefinitive: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            isRejectDefinitive: true
                        }
                    },
                    isCheckStarted: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            isCheckStarted: true
                        }
                    },
                    isWaitingForRedelivering: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            isWaitingRedelivering: true
                        }
                    },
                    isReportToValidate: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            canReportBeValidated: true
                        }
                    },
                    canProcess: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            canProcess: true
                        }
                    },
                    process: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            process: true
                        }
                    },
                    resetToNumWaiting: {
                        method: 'POST',
                        isArray: false,
                        params: {
                            resetNumWaiting: true
                        }
                    },
                    massValidateCondReports: {
                        method: 'POST',
                        isArray: false,
                        params: {
                            'massValidate': true
                        } 
                    },
                    massValidateRecords: {
                        method: 'POST',
                        isArray: false,
                        params: {
                            'massValidateRecords': true
                        }
                    },
                    endAllDocWorkflows: {
                        method: 'POST',
                        isArray: false,
                        params: {
                            endAllDocWorkflows: true
                        } 
                    },
                    validDocWorkflowState: {
                        method: 'POST',
                        isArray: false,
                        params: {
                            validDocWorkflowState: true
                        } 
                    },
                    reinitDocWorkflowState: {
                        method: 'POST',
                        isArray: false,
                        params: {
                            reinitDocWorkflowState: true
                        } 
                    }
                });

            service.states = [
                "INITIALISATION_DOCUMENT",
                "GENERATION_BORDEREAU",
                "VALIDATION_CONSTAT_ETAT",
                "VALIDATION_BORDEREAU_CONSTAT_ETAT",
                "CONSTAT_ETAT_AVANT_NUMERISATION",
                "NUMERISATION_EN_ATTENTE",
                "CONSTAT_ETAT_APRES_NUMERISATION",
                "LIVRAISON_DOCUMENT_EN_COURS",
                "RELIVRAISON_DOCUMENT_EN_COURS",
                "CONTROLES_AUTOMATIQUES_EN_COURS",
                "CONTROLE_QUALITE_EN_COURS",
                "PREREJET_DOCUMENT",
                "PREVALIDATION_DOCUMENT",
                "VALIDATION_DOCUMENT",
                "VALIDATION_NOTICES",
                "RAPPORT_CONTROLES",
                "ARCHIVAGE_DOCUMENT",
                "DIFFUSION_DOCUMENT",
                "DIFFUSION_DOCUMENT_OMEKA",
                "DIFFUSION_DOCUMENT_DIGITAL_LIBRARY",
                "DIFFUSION_DOCUMENT_LOCALE",
                "CLOTURE_DOCUMENT"];
            
            service.statesToForceValid = [
                "VALIDATION_CONSTAT_ETAT",
                "CONTROLE_QUALITE_EN_COURS",
                "PREREJET_DOCUMENT",
                "PREVALIDATION_DOCUMENT",
                "VALIDATION_DOCUMENT",
                "VALIDATION_NOTICES",
                "RAPPORT_CONTROLES",
                "ARCHIVAGE_DOCUMENT",
                "DIFFUSION_DOCUMENT",
                "DIFFUSION_DOCUMENT_OMEKA",
                "DIFFUSION_DOCUMENT_DIGITAL_LIBRARY",
                "DIFFUSION_DOCUMENT_LOCALE",
                "CLOTURE_DOCUMENT"];
            
            service.statesToForceReinit = [
                "VALIDATION_CONSTAT_ETAT",
                "PREREJET_DOCUMENT",
                "PREVALIDATION_DOCUMENT",
                "VALIDATION_DOCUMENT",
                "VALIDATION_NOTICES"];

            service.status = [
                "NOT_STARTED",
                "PENDING",
                "CANCELED",
                "FAILED",
                "TO_WAIT",
                "TO_SKIP",
                "WAITING",
                "WAITING_NEXT_COMPLETED",
                "SKIPPED",
                "FINISHED"];
            
            

            service.getConfigStatus = function () {
                return _.map(service.status, function (st) {
                    return {
                        identifier: st,
                        label: codeSrvc['workflow.status.' + st] || st
                    };
                });
            };
            service.getConfigWorkflow = function () {
                return _.map(service.states, function (state) {
                    return {
                        identifier: state,
                        label: codeSrvc['workflow.model.' + state] || state
                    };
                });
            };
            service.getAdminStatesToValid = function () {
                return _.map(service.statesToForceValid, function (state) {
                    return {
                        identifier: state,
                        label: codeSrvc['workflow.model.' + state] || state
                    };
                });
            };
            service.getAdminStatesToReinit = function () {
                return _.map(service.statesToForceReinit, function (state) {
                    return {
                        identifier: state,
                        label: codeSrvc['workflow.model.' + state] || state
                    };
                });
            };
            return service;
        });

    angular.module('numaHopApp.service')
        .factory('WorkflowModelSrvc', function ($resource, CONFIGURATION) {
            var service = $resource(CONFIGURATION.numahop.url + 'api/rest/workflow_model/:id', {
                id: '@identifier'
            }, {
                    dto: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            dto: true
                        }
                    },
                    search: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            search: true,
                            size: 50
                        }
                    },
                    models: {
                        method: 'GET',
                        isArray: true,
                        params: {
                            models: true
                        }
                    }
                });
            return service;
        });

    angular.module('numaHopApp.service')
        .factory('WorkflowGroupSrvc', function ($resource, CONFIGURATION) {
            var service = $resource(CONFIGURATION.numahop.url + 'api/rest/workflow_group/:id', {
                id: '@identifier'
            }, {
                    dto: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            dto: true
                        }
                    },
                    search: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            search: true,
                            size: 50
                        }
                    },
                    groups: {
                        method: 'GET',
                        isArray: true,
                        params: {
                            groups: true
                        }
                    }
                });
            return service;
        });
})();