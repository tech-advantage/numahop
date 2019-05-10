(function () {
	'use strict';

	angular.module('numaHopApp.service')
		.factory('NumaHopStatusService', function () {

			var isDigitalDocAvailable = function (digitalDoc) {
				if (digitalDoc.status === "CREATING" || digitalDoc.status === "DELIVERING") {
					return false;
				}
				return true;
			};

			var isDeliveryLocked = function (delivery) {
				if (delivery.status === "DELIVERING") {
					return true;
				}
				return false;
			};

			return {
				isDigitalDocAvailable: isDigitalDocAvailable,
				isDeliveryLocked: isDeliveryLocked
			};
		});
})();
