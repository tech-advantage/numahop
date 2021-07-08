(function () {
	'use strict';

	angular.module('numaHopApp.controller')
		.controller('WorkflowAllOperationsCtrl', WorkflowAllOperationsCtrl);

	function WorkflowAllOperationsCtrl($routeParams, $location, WorkflowSrvc, codeSrvc, gettextCatalog, MessageSrvc) {

		var workflowCtrl = this;

		workflowCtrl.init = init;
		workflowCtrl.doesWorkflowExist = doesWorkflowExist;
		workflowCtrl.code = codeSrvc;
		workflowCtrl.validate = validate;
		workflowCtrl.reject = reject;
		workflowCtrl.getStatusClass = getStatusClass;

        /**
         * Initialisation du contrôleur
         * 
         * @param {any} parentCtrl 
         */
		function init(parentCtrl) {
			workflowCtrl.parent = parentCtrl;
			workflowCtrl.currentTab = parentCtrl.tabs.WORKFLOW;
			workflowCtrl.lot = parentCtrl.docUnit.lot;
			workflowCtrl.docUnitId = $routeParams.identifier;
			workflowCtrl.radical = parentCtrl.docUnit.digitalId;

			loadWorkflow(workflowCtrl.docUnitId);
		}


        /**************************************
         *              Workflow              *
         **************************************/

        /**
         * Chargement du workflow
         * 
         * @param {any} docUnit identifier
         */
		function loadWorkflow(identifier) {
			workflowCtrl.loaded = false;
			workflowCtrl.workflow = WorkflowSrvc.get({ docUnit: identifier });

			workflowCtrl.workflow.$promise
				.then(function (workflow) {
					workflowCtrl.loaded = true;
					_.each(workflow.states, function (state) {
						state.authorized = false;
						if (state.status === 'PENDING') {
							WorkflowSrvc.canProcess({ docUnit: identifier, key: state.key }).$promise
								.then(function (result) {
									if (result.value) {
										state.authorized = true;
									}
								});
						}
					});
					workflowCtrl.sortedStates = sortStates(workflow.states);
				});
		}

		function doesWorkflowExist() {
			if (angular.isUndefined(workflowCtrl.workflow)) {
				return false;
			}
			return true;
		}

		function validate(state) {
			var params;
			if (state.key === "LIVRAISON_DOCUMENT_EN_COURS" 
			        || state.key === "RELIVRAISON_DOCUMENT_EN_COURS") {
				params = {
					new: true,
					lot: workflowCtrl.lot.identifier
				};
				$location.path("/delivery/delivery").search(params);
			} else if (state.key === "CONTROLE_QUALITE_EN_COURS") {
				params = {
					radical: workflowCtrl.radical
				};
				$location.path("/checks/checks").search(params);
			} else {
				WorkflowSrvc.process({ docUnitId: workflowCtrl.docUnitId, key: state.key }).$promise
					.then(function () {
						MessageSrvc.addSuccess(gettextCatalog.getString("L'étape {{name}} a été validée"), { name: workflowCtrl.code['workflow.' + state.key] });
						loadWorkflow(workflowCtrl.docUnitId);
					});
			}
		}

		function reject() {
			MessageSrvc.addWarn(gettextCatalog.getString("L'opération demandée n'est pas disponible actuellement"), {}, true);
		}

		function getStatusClass(status) {
			switch (status) {
				case "NOT_STARTED": // Tâche non commencée
					return "label-not-started";
				case "PENDING":     // Tâche en cours
					return "label-pending";
				case "FINISHED":    // Tâche accomplie 
					return "label-success";
				case "FAILED":      // Tâche échouée
					return "label-failure";
				case "TO_WAIT":     // Tâche qui sera à attendre
				case "WAITING":     // En attente d'action système
					return "label-waiting";
				case "CANCELED":    // Tâche annulée 
				case "TO_SKIP":     // Tâche qui ne sera pas accomplie (optionnelle uniquement)
				case "SKIPPED":     // Tâche optionnelle qui a été passée
				default:
					return "label-ignore";
			}
		}

        /**
         * Ordonne les étapes
         */
		function sortStates(states) {
			var sortedStates = [];
			sortedStates.push(_.where(states, { key: "INITIALISATION_DOCUMENT" })[0]);
			sortedStates.push(_.where(states, { key: "GENERATION_BORDEREAU" })[0]);
			sortedStates.push(_.where(states, { key: "VALIDATION_CONSTAT_ETAT" })[0]);
			sortedStates.push(_.where(states, { key: "VALIDATION_BORDEREAU_CONSTAT_ETAT" })[0]);
			sortedStates.push(_.where(states, { key: "CONSTAT_ETAT_AVANT_NUMERISATION" })[0]);
			sortedStates.push(_.where(states, { key: "NUMERISATION_EN_ATTENTE" })[0]);
			sortedStates.push(_.where(states, { key: "CONSTAT_ETAT_APRES_NUMERISATION" })[0]);
			sortedStates.push(_.where(states, { key: "LIVRAISON_DOCUMENT_EN_COURS" })[0]);
			var relivStates = _.where(states, { key: "RELIVRAISON_DOCUMENT_EN_COURS" });
			relivStates = _.sortBy(relivStates, function(relivState){ return new Date( relivState.startDate ) }).reverse();
			if (relivStates.length > 0) {
			    sortedStates.push(relivStates[0]); 
			}
			sortedStates.push(_.where(states, { key: "CONTROLES_AUTOMATIQUES_EN_COURS" })[0]);
			sortedStates.push(_.where(states, { key: "CONTROLE_QUALITE_EN_COURS" })[0]);
			sortedStates.push(_.where(states, { key: "PREREJET_DOCUMENT" })[0]);
			sortedStates.push(_.where(states, { key: "PREVALIDATION_DOCUMENT" })[0]);
			sortedStates.push(_.where(states, { key: "VALIDATION_DOCUMENT" })[0]);
			sortedStates.push(_.where(states, { key: "VALIDATION_NOTICES" })[0]);
			sortedStates.push(_.where(states, { key: "RAPPORT_CONTROLES" })[0]);
			sortedStates.push(_.where(states, { key: "ARCHIVAGE_DOCUMENT" })[0]);
			sortedStates.push(_.where(states, { key: "DIFFUSION_DOCUMENT" })[0]);
			var diffOmekaStates = _.where(states, { key: "DIFFUSION_DOCUMENT_OMEKA" }); 
			if (diffOmekaStates.length > 0) {
			    sortedStates.push(diffOmekaStates[0]);
			}
			sortedStates.push(_.where(states, { key: "DIFFUSION_DOCUMENT_DIGITAL_LIBRARY" })[0]);
			var diffLocalStates = _.where(states, { key: "DIFFUSION_DOCUMENT_LOCALE" }); 
            if (diffLocalStates.length > 0) {
                sortedStates.push(diffLocalStates[0]);
            }
			sortedStates.push(_.where(states, { key: "CLOTURE_DOCUMENT" })[0]);
			return sortedStates;
		}
	}
})();