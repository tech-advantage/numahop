(function() {
    'use strict';

    angular.module("numaHopApp.controller")
        .controller("ModalIntegrateToTrainCtrl", function($scope, $uibModalInstance, options, DocUnitSrvc, TrainSrvc) {
            var mainCtrl = this;
            _.extend(mainCtrl, options);

            mainCtrl.options = options;
            mainCtrl.confirm = confirm;
            mainCtrl.cancel = cancel;
            init();

            /** Initialisation */
            function init() {
                if (angular.isDefined(mainCtrl.options.proj)) {
                    mainCtrl.project = mainCtrl.options.proj;
                }    
                loadOptions();
            }

            function loadOptions() {
                var searchParams = {
                        project : mainCtrl.project.identifier
                };
                TrainSrvc.findSimpleByProject(searchParams, function(value) {
                    mainCtrl.filteredTrains = value;
                });
                if (angular.isDefined(mainCtrl.options.train)) {
                    mainCtrl.train = mainCtrl.options.train;  
                }
            }

            function confirm() {
                var params = {
                    train: mainCtrl.train.identifier
                };
                var body = _.pluck(_.pluck(mainCtrl.options.docs, "docUnit"), "identifier");
                DocUnitSrvc.addToTrain(params, body, function() {
                    $uibModalInstance.close();
                });
            }

            function cancel() {
                $uibModalInstance.close();
            }
        });
})();
