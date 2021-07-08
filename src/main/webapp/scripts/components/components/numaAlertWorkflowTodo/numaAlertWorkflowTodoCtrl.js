(function() {
    'use strict';

    angular.module('numaHopApp.controller').controller('NumaAlertWorkflowTodoCtrl', NumaAlertWorkflowTodoCtrl);

    function NumaAlertWorkflowTodoCtrl(codeSrvc, StatisticsSrvc) {
        var ctrl = this;
        ctrl.code = codeSrvc;
        ctrl.$onChanges = onChanges;
        ctrl.params = {
                      page : 0,
                      size : 500,
                      mine : true, // filtrage sur les alertes de l'utilisateur connecté
                      project_active : true,
                      status: "PENDING"
                  };

        /**
         * Chargement des workflowDocUnit en cours.
         * 
         */
        function loadWorkflowDocUnit() {

            StatisticsSrvc.workflowDocUnit(ctrl.params).$promise.then(function(result) {
                // Map [état du workflow, unité documentaire], filtrée sur les états en cours
                var pendingStatByStep = {};
                _.each(result.content, function(doc) {
                    _.chain(doc.workflow).filter(function(w) {
                        return w.status === "PENDING";
                    }).each(function(w) {
                        if (!pendingStatByStep[w.key]) {
                            pendingStatByStep[w.key] = [];
                        }
                        pendingStatByStep[w.key].push(doc);
                    });
                });
                ctrl.pendingStatByStep = pendingStatByStep;
            });
        }

        /**
         * Suivi des modifs extérieures
         * 
         * @param {*}
         *            changes
         */
        function onChanges(changes) {
            // Rafraichissement des actions
            ctrl.params.project = _.pluck(ctrl.projects, "identifier");
            ctrl.params.lot = _.pluck(ctrl.lots, "identifier");
            loadWorkflowDocUnit();
        }

    }
})();