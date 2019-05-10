(function () {
    'use strict';

    angular.module('numaHopApp')
        .controller('ConfigurationController', function ($scope, ConfigurationService) {
            ConfigurationService.get().then(function (configuration) {
                $scope.configuration = configuration;
            });
        });
})();