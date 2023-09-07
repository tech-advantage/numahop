(function () {
    'use strict';

    angular.module('numaHopApp').controller('LogsController', function ($scope, LogsService, $http, $httpParamSerializer, FileSaver, ModalSrvc) {
        $scope.loggers = LogsService.findAll();

        $scope.changeLevel = function (name, level) {
            LogsService.changeLevel({ name: name, level: level }, function () {
                $scope.loggers = LogsService.findAll();
            });
        };

        $scope.format = 'YYYY-MM-DD';
        $scope.dt = new Date();

        $scope.changeDate = function (value) {
            if (value) {
                $scope.dt = moment(new Date(value)).format($scope.format);
            }
        };
        $scope.changeDate();

        $scope.downloadLogFile = function downloadLogFile() {
            var params = [];
            params.logFile = true;
            if (!$scope.dt) {
                $scope.dt = new Date();
            }
            params.dtFile = moment($scope.dt).format($scope.format);

            var url = 'api/rest/downloadlogsfile?' + $httpParamSerializer(params);
            // pour conserver l'encodage original dans le fichier sauvegardé
            $http.get(url, { responseType: 'arraybuffer' }).then(
                function (response) {
                    var filename = 'logs_' + params.dtFile + '.txt';
                    var blob = new Blob([response.data], { type: response.headers('content-type') });
                    FileSaver.saveAs(blob, filename);
                },
                function () {
                    ModalSrvc.confirmNotFound($scope.dt);
                }
            );
        };
    });
})();
