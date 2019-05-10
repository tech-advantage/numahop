(function () {
	'use strict';

	angular.module('numaHopApp.component')
		.component('numaWorkflowLink', {
			bindings: {
				linkParameters: '<numaLinkParameters'
			},
			templateUrl: '/scripts/components/components/numaWorkflowLink/template.html',
			controller: function ($scope, NumahopUrlService, $httpParamSerializer) {
				var ctrl = this;

				ctrl.$onChanges = function (changesObj) {
					if (changesObj.linkParameters) {
						var parameters = angular.copy(changesObj.linkParameters.currentValue);
						var params, url;

						if (parameters.state === "LIVRAISON_DOCUMENT_EN_COURS") {
							params = {
							    new: true,      
								lot: parameters.item.infos.lot
							};
							url = "/#/delivery/delivery";

						} else if (parameters.state === "VALIDATION_DOCUMENT" || parameters.state === "CONTROLE_QUALITE_EN_COURS") {
						    params = {
		                                radical: parameters.item.infos.radical,
		                                reinitStatusFilter: true
		                    };
						    url = "/#/checks/checks";

						} else if (parameters.state === "VALIDATION_CONSTAT_ETAT") {
							params = {
								tab: 'CONDREPORT'
							};
							url = "/#/document/all_operations/" + parameters.item.docIdentifier;

						} else if (parameters.state === "VALIDATION_NOTICES") {
							if (parameters.item.infos.record) {
								params = {
									id: parameters.item.infos.record
								};
								url = "/#/document/record";

							} else {
								params = {
									tab: 'RECORD'
								};
								url = "/#/document/all_operations/" + parameters.item.docIdentifier;
							}
						} else if (parameters.state === "ARCHIVAGE_DOCUMENT") {
							params = {
								tab: 'ARCHIVE'
							};
							url = "/#/document/all_operations/" + parameters.item.docIdentifier;

						} else if (parameters.state === "DIFFUSION_DOCUMENT") {
							params = {
								tab: 'EXPORT'
							};
							url = "/#/document/all_operations/" + parameters.item.docIdentifier;
						}
						if (url) {
							var serializedParams = $httpParamSerializer(params);
							ctrl.entityLink = url + (serializedParams ? "?" + serializedParams : "");
						}

					}
				};
			}
		});
})();