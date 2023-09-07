(function () {
    'use strict';

    angular.module('numaHopApp.service').factory('WorkflowHandleSrvc', function (codeSrvc, WorkflowSrvc) {
        /******************************
         * Fonctions avec un appel REST
         ******************************/

        var isWorkflowStarted = function (doc) {
            return WorkflowSrvc.isWorkflowStarted({ doc: doc });
        };

        var isRejectDefinitive = function (doc) {
            return WorkflowSrvc.isRejectDefinitive({ doc: doc }).$promise;
        };

        /**
         * Retourne vrai si le contrôle est en cours
         */
        var isCheckStarted = function (doc) {
            return WorkflowSrvc.isCheckStarted({ doc: doc });
        };

        /**
         * Retourne vrai si le doc est en attente de relivraison
         */
        var isWaitingForRedelivering = function (doc) {
            return WorkflowSrvc.isWaitingForRedelivering({ doc: doc });
        };

        /**
         * Retourne vrai si le constat peut etre validé.
         */
        var isReportToValidate = function (doc) {
            return WorkflowSrvc.isReportToValidate({ doc: doc }).$promise;
        };

        /****************************************************
         * Fonctions sans appel basé sur le workflow récupéré
         ****************************************************/
        /**
         * Vérification par statut
         */
        var stateStatusIsDone = function (status) {
            if (status === 'FINISHED' || status === 'CANCELED') {
                //|| status === 'SKIPPED'
                return true;
            }
            return false;
        };
        /**
         * Vérification de l'étape en cours
         */
        var stateStatusIsPending = function (status) {
            if (status === 'PENDING') {
                return true;
            }
            return false;
        };
        /**
         * Vérification de l'étape ignorée
         */
        var stateStatusIsSkipped = function (status) {
            if (status === 'SKIPPED') {
                return true;
            }
            return false;
        };
        /**
         * Vérification d'un état
         */
        var isStateDone = function (workflow, key) {
            if (angular.isUndefined(workflow) || !workflow) {
                return false;
            }
            var result = true;
            _.chain(workflow.states)
                // Turn the states object into an Array
                .map(angular.identity)
                // Filtrage par clé
                .filter(function (state) {
                    return state.key === key;
                })
                .each(function (state) {
                    if (!stateStatusIsDone(state.status)) {
                        result = false;
                    }
                });
            return result;
        };
        /**
         * Vérification du fait que la tâche est en attente
         */
        var isStatePending = function (workflow, key) {
            if (angular.isUndefined(workflow) || !workflow) {
                return false;
            }
            var result = false;
            _.chain(workflow.states)
                // Turn the states object into an Array
                .map(angular.identity)
                // Filtrage par clé
                .filter(function (state) {
                    return state.key === key;
                })
                .each(function (state) {
                    if (stateStatusIsPending(state.status)) {
                        result = true;
                    }
                });
            return result;
        };
        /**
         * Vérification du fait que la tâche est en attente ou ignorée.
         */
        var isStatePendingOrSkipped = function (workflow, key) {
            if (angular.isUndefined(workflow) || !workflow) {
                return false;
            }
            var result = false;
            _.chain(workflow.states)
                // Turn the states object into an Array
                .map(angular.identity)
                // Filtrage par clé
                .filter(function (state) {
                    return state.key === key;
                })
                .each(function (state) {
                    if (stateStatusIsPending(state.status) || stateStatusIsSkipped(state.status)) {
                        result = true;
                    }
                });
            return result;
        };
        /**
         * Fonctions d'utilité pour tester la réalisation des états
         */
        var isConstatValidated = function (workflow) {
            return isStateDone(workflow, 'VALIDATION_CONSTAT_ETAT');
        };
        var isConstatConfirmed = function (workflow) {
            return isStateDone(workflow, 'VALIDATION_BORDEREAU_CONSTAT_ETAT');
        };
        var isConstatBeforeNumValidated = function (workflow) {
            return isStateDone(workflow, 'CONSTAT_ETAT_AVANT_NUMERISATION');
        };
        var isConstatAfterNumValidated = function (workflow) {
            return isStateDone(workflow, 'CONSTAT_ETAT_APRES_NUMERISATION');
        };
        var canConstatBeDeleted = function (workflow) {
            return !isConstatBeforeNumValidated(workflow) && !isConstatConfirmed(workflow) && !isConstatValidated(workflow) && !isConstatAfterNumValidated(workflow);
        };
        var canRecordBeValidated = function (workflow) {
            return isStatePending(workflow, 'VALIDATION_NOTICES');
        };
        var canCondReportBeValidated = function (workflow) {
            return (
                isStatePendingOrSkipped(workflow, 'VALIDATION_CONSTAT_ETAT') ||
                isStatePendingOrSkipped(workflow, 'VALIDATION_BORDEREAU_CONSTAT_ETAT') ||
                isStatePendingOrSkipped(workflow, 'CONSTAT_ETAT_AVANT_NUMERISATION') ||
                isStatePendingOrSkipped(workflow, 'CONSTAT_ETAT_APRES_NUMERISATION')
            );
        };

        /**
         * Gestion des types possibles par étape
         *
         */
        var getStateTypeForStateKey = function (key) {
            var list = [];
            switch (key) {
                case 'ARCHIVAGE_DOCUMENT':
                case 'CONSTAT_ETAT_APRES_NUMERISATION':
                case 'CONSTAT_ETAT_AVANT_NUMERISATION':
                case 'DIFFUSION_DOCUMENT':
                case 'DIFFUSION_DOCUMENT_OMEKA':
                case 'DIFFUSION_DOCUMENT_DIGITAL_LIBRARY':
                case 'DIFFUSION_DOCUMENT_LOCALE':
                case 'PREREJET_DOCUMENT':
                case 'PREVALIDATION_DOCUMENT':
                case 'VALIDATION_CONSTAT_ETAT':
                case 'VALIDATION_BORDEREAU_CONSTAT_ETAT':
                    list.push({ code: 'REQUIRED', label: codeSrvc['workflow.type.REQUIRED'] });
                    list.push({ code: 'TO_SKIP', label: codeSrvc['workflow.type.TO_SKIP'] });
                    break;
                case 'CLOTURE_DOCUMENT':
                case 'INITIALISATION_DOCUMENT':
                    list.push({ code: 'OTHER', label: codeSrvc['workflow.type.OTHER'] });
                    break;
                case 'CONTROLES_AUTOMATIQUES_EN_COURS':
                case 'LIVRAISON_DOCUMENT_EN_COURS':
                case 'RELIVRAISON_DOCUMENT_EN_COURS':
                case 'NUMERISATION_EN_ATTENTE':
                case 'RAPPORT_CONTROLES':
                    list.push({ code: 'TO_WAIT', label: codeSrvc['workflow.type.TO_WAIT'] });
                    break;
                case 'CONTROLE_QUALITE_EN_COURS':
                case 'VALIDATION_DOCUMENT':
                case 'VALIDATION_NOTICES':
                    list.push({ code: 'REQUIRED', label: codeSrvc['workflow.type.REQUIRED'] });
                    break;
                case 'GENERATION_BORDEREAU':
                    list.push({ code: 'TO_SKIP', label: codeSrvc['workflow.type.TO_SKIP'] });
                    break;
            }
            return list;
        };

        return {
            isConstatValidated: isConstatValidated,
            isConstatConfirmed: isConstatConfirmed,
            isConstatBeforeNumValidated: isConstatBeforeNumValidated,
            isConstatAfterNumValidated: isConstatAfterNumValidated,
            canConstatBeDeleted: canConstatBeDeleted,
            getStateTypeForStateKey: getStateTypeForStateKey,
            isWorkflowStarted: isWorkflowStarted,
            isCheckStarted: isCheckStarted,
            isRejectDefinitive: isRejectDefinitive,
            canRecordBeValidated: canRecordBeValidated,
            canCondReportBeValidated: canCondReportBeValidated,
            isReportToValidate: isReportToValidate,
            isWaitingForRedelivering: isWaitingForRedelivering,
        };
    });
})();
