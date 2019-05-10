(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('FiltersController', FiltersController);

    function FiltersController() {

        var self = this;

        self.isAvailable = function (data) {
            return angular.isDefined(data);
        };
    }
})();