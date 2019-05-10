(function () {
    'use strict';

    angular.module('numaHopApp.service').factory('DeliveryConfSrvc', DeliveryConfSrvc);

    function DeliveryConfSrvc(CONFIGURATION, $resource) {
        var service = $resource(CONFIGURATION.numahop.url + 'api/rest/delivery_configuration/:id', { id: '@identifier' }, {});
        return service;
    }

})();
