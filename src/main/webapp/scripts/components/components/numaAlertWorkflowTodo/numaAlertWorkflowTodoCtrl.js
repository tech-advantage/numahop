(function() {
    'use strict';

    angular.module('numaHopApp.controller').controller('NumaAlertWorkflowTodoCtrl', NumaAlertWorkflowTodoCtrl);

    function NumaAlertWorkflowTodoCtrl(codeSrvc, StatisticsSrvc) {
        var ctrl = this;
        ctrl.code = codeSrvc;
        ctrl.params = {
            page : 0,
            size : 500,
            mine : true, // filtrage sur les alertes de l'utilisateur connecté
            project_active : true,
            status: "PENDING"
        };

        /**
         * Suivi des modifs extérieures
         *
         * @param {*}
         *            changes
         */
        ctrl.$onChanges = function() {
            // Rafraichissement des actions
            ctrl.params.project = _.pluck(ctrl.projects, "identifier");
            ctrl.params.lot = _.pluck(ctrl.lots, "identifier");
            ctrl.loadWorkflowDocUnit();
        }

        /**
         * Chargement des workflowDocUnit en cours.
         *
         */
        ctrl.loadWorkflowDocUnit = function() {

            StatisticsSrvc.workflowDocUnitPending(ctrl.params).$promise.then(function(result) {
                // Map [état du workflow, unité documentaire], filtrée sur les états en cours
                var pendingStatByStep = {};
                _.each(result, function(doc) {
                    for(var state in doc.workflowStateKeys){
                        if (!pendingStatByStep[doc.workflowStateKeys[state]]) {
                            pendingStatByStep[doc.workflowStateKeys[state]] = [];
                        }
                        pendingStatByStep[doc.workflowStateKeys[state]].push(doc);
                    }
                });
                ctrl.pendingStatByStep = pendingStatByStep;
            });
        }
    }
})();
