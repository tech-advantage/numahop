(function() {
    'use strict';

    angular.module("numaHopApp.controller")
        .controller("ModalIntegrateToProjectCtrl", function($scope, $q, $uibModalInstance, $location, options, gettext, DtoService,
        NumaHopInitializationSrvc, ProjectSrvc, MessageSrvc, ModalSrvc, gettextCatalog, LotSrvc, DocUnitSrvc, TrainSrvc, DocUnitBaseService) {
            var mainCtrl = this;
            _.extend(mainCtrl, options);

            mainCtrl.options = options;
            mainCtrl.addToNewProject = addToNewProject;
            mainCtrl.onSelectProject = onSelectProject;
            mainCtrl.confirm = confirm;
            mainCtrl.cancel = cancel;

            mainCtrl.canRemove = canRemove;

            init();

            /** Initialisation */
            function init() {
                loadOptions();
                filteredProjects();
                // si preselection d'un projet
                if (angular.isDefined(mainCtrl.options.proj)) {
                    mainCtrl.project = mainCtrl.options.proj;
                    mainCtrl.onSelectProject(mainCtrl.project);
                }
            }

            /**
             * addToNewProject - Création d'un nouveau projet à partir de tous les exemplaires sélectionnés
             **/
            function addToNewProject() {
                DtoService.addDocs(mainCtrl.options.docs);
                $uibModalInstance.close();
                $location.path("/project/project").search({ id: null, mode: "edit", display: "ok"});
            }

            function loadOptions() {
                $q.all([NumaHopInitializationSrvc.loadProjects()])
                    .then(function(data) {
                        mainCtrl.sel2Projects = data[0];
                });
            }

            function filteredProjects() {
                var searchParams = { searchProject : ""};
                searchParams["statuses"] = ["CREATED","ONGOING"];
                mainCtrl.filteredProjects = ProjectSrvc.searchProject(searchParams);
                return mainCtrl.filteredProjects;
            }

            function filterLots() {
                mainCtrl.lot = null;
                var searchParams = {
                    project : mainCtrl.project.identifier
                };
                LotSrvc.findSimpleByProjectForDocUnit(searchParams, function(value) {
                    mainCtrl.filteredLots = value;
                });
                
                if (angular.isDefined(mainCtrl.options.lot)) {
                    mainCtrl.lot = mainCtrl.options.lot;  
                }
            }

            function filterTrains() {
                mainCtrl.train = null;
                var searchParams = {
                    project : mainCtrl.project.identifier
                };
                TrainSrvc.findSimpleByProject(searchParams, function(value){
                    mainCtrl.filteredTrains = value;
                });
                if (angular.isDefined(mainCtrl.options.train)) {
                    mainCtrl.train = mainCtrl.options.train;  
                }
            }

            function canRemove(item){
                if(item != null){
                    return _.contains(["CREATED"], item.status);
                }
                return true;
            }

            function confirm() {
                var params = {
                    project: mainCtrl.project.identifier
                };
                params.lot=mainCtrl.lot?mainCtrl.lot.identifier:"";
                params.train=mainCtrl.train?mainCtrl.train.identifier:"";
                var body = _.pluck(mainCtrl.options.docs, "identifier");
                DocUnitSrvc.addToProjectAndLot(params, body, function() {
                    $uibModalInstance.close();
                });
            }

            function onSelectProject(project) {
                filterLots();
                filterTrains();
            }

            function cancel() {
                $uibModalInstance.close();
            }
        });
})();
