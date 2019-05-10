(function () {
    'use strict';

    angular.module("numaHopApp.controller")
        .controller("ModalCreateProjectCtrl", ModalCreateProjectCtrl);

    function ModalCreateProjectCtrl($scope, $uibModalInstance, gettextCatalog, $location, $timeout,
        MessageSrvc, DocUnitBaseService, NumahopEditService, ProjectSrvc, NumaHopInitializationSrvc) {

        var mainCtrl = this;

        mainCtrl.close = close;
        mainCtrl.createProject = createProject;

        mainCtrl.options = {
        };

        mainCtrl.project = {
            active: true
        };

        init();

        function init() {
            NumaHopInitializationSrvc.loadLibraries()
                .then(function (data) {
                    mainCtrl.options.sel2Libraries = data;
                    if (mainCtrl.options.sel2Libraries.length === 1) {
                        mainCtrl.project.library = mainCtrl.options.sel2Libraries[0];
                    }
                    mainCtrl.loaded = true;
                });
        }

        function close() {
            $uibModalInstance.dismiss("cancel");
        }

        function createProject() {
            ProjectSrvc.save(mainCtrl.project).$promise.then(function (project) {
                $uibModalInstance.close(project);
            });
        }

    }

})();
