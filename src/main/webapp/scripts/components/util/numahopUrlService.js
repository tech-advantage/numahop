(function () {
	'use strict';

	angular.module('numaHopApp.service')
		.factory('NumahopUrlService', NumahopUrlService);

	function NumahopUrlService($resource, gettext, CONFIGURATION, $httpParamSerializer) {

		var service = $resource(CONFIGURATION.numahop.url + 'app/rest/url/:id', {
			id: '@identifier'
		}, {
				search: {
					method: 'GET',
					isArray: false,
					params: {
						search: true
					}
				}
			});
		service.getUrlDocUnit = getUrlDocUnit;
		service.getUrlRecord = getUrlRecord;
		service.getUrlCreateProject = getUrlCreateProject;
		service.getUrlForTypeAndParameters = parameterizedUrl;

        /**
         * getUrl - Construction du lien vers le détail d'une unité doc
         *
         * @param  {doc} unité doc
         * @return {String} le lien
         */
		function getUrlDocUnit(doc) {
			var url = "/#/document/docunit?";
			return url + "id=" + doc.identifier;
		}
		function getUrlRecord(record) {
			var url = "/#/document/record?";
			return url + "id=" + record.identifier;
		}
		function getUrlCreateProject(doc) {
			var url = "/#/project/project?";
			return url + "id=" + doc.project.identifier;
		}
		function parameterizedUrl(type, parameters) {
			var url = "/#/";
			switch (type) {
				case "role": url = url + "user/role";
					break;
				case "record": url = url + "document/record";
					break;
				case "digital_document": url = url + "document/digital";
					break;
				case "viewer_document": url = url + "viewer/viewer";
					break;
				case "viewer_sample": url = url + "viewer/viewer";
					break;
				case "ftpConfiguration": url = url + "platformconfiguration/ftpconfiguration";
					break;
				case "exportftpConfiguration": url = url + "platformconfiguration/exportftpconfiguration";
                    break;	
				case "sftpConfiguration": url = url + "platformconfiguration/sftpconfiguration";
					break;
				case "iaConfiguration": url = url + "platformconfiguration/iaconfiguration";
					break;
				case "omekaConfiguration": url = url + "platformconfiguration/omekaconfiguration";
				    break;
                case "digitalLibraryConfiguration": url = url + "platformconfiguration/digitallibraryconfiguration";
                    break;
				case "checkConfiguration": url = url + "checkconfiguration/checkconfiguration";
					break;
				case "ocrlangConfiguration": url = url + "ocrlangconfiguration/ocrlangconfiguration";
                    break;	
				case "formatConfiguration": url = url + "administration/appconfiguration/viewsformat";
					break;
				case "viewsformat": url = url + "viewsformat/viewsformat";
					break;
				case "checks": url = url + "document/checks";
					break;
				case "workflowModel": url = url + "workflow/model";
					break;
				case "workflowGroup": url = url + "workflow/group";
					break;
				case "multiLotsDelivery": url = url + "multilotsdelivery/multidelivery";
					break;
				// les pages d'entités principales : de la forme {entity}/{entity}
				case "user":
				case "library":
				case "project":
				case "lot":
				case "train":
				case "delivery":
					url += type + "/" + type;
					break;
				case "docunit":
					// cas particulier : on redirige vers les détails
					url += "document/all_operations/" + parameters.id;
					delete parameters.id;
					break;
			}
			var serializedParams = $httpParamSerializer(parameters);
			return url + (serializedParams ? "?" + serializedParams : "");
		}

		return service;
	}
})();
