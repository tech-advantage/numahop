(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('ProjectTrainsCtrl', ProjectTrainsCtrl);

    function ProjectTrainsCtrl(TrainSrvc) {
        var trainCtrl = this;
        trainCtrl.init = loadTrains;

        function loadTrains(projectId) {
            trainCtrl.projectId = projectId;
            trainCtrl.trains = TrainSrvc.query({ project: projectId });
        }
    }
})();
