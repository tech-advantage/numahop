(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('AllOperationsCtrl', AllOperationsCtrl);

    function AllOperationsCtrl($location, $routeParams, $scope, codeSrvc, gettextCatalog, ModalSrvc, ProjectSrvc) {

        $scope.changeTab = changeTab;
        $scope.getBack = getBack;

        $scope.translate = {
            trainStatus: {
                CREATED: codeSrvc["train.status.CREATED"],
                IN_PREPARATION: codeSrvc["train.status.IN_PREPARATION"],
                IN_DIGITIZATION: codeSrvc["train.status.IN_DIGITIZATION"],
                RECEIVING_PHYSICAL_DOCUMENTS: codeSrvc["train.status.RECEIVING_PHYSICAL_DOCUMENTS"],
                CLOSED: codeSrvc["train.status.CLOSED"]
            },
            lotStatus: {
                CREATED: codeSrvc["lot.status.CREATED"],
                ONGOING: codeSrvc["lot.status.ONGOING"],
                PENDING: codeSrvc["lot.status.PENDING"],
                CANCELED: codeSrvc["lot.status.CANCELED"],
                CLOSED: codeSrvc["lot.status.CLOSED"]
            },
            lotType: {
                PHYSICAL: gettextCatalog.getString("Physique"),
                DIGITAL: gettextCatalog.getString("Numérique")
            },
            boolean: {
                true: gettextCatalog.getString("Oui"),
                false: gettextCatalog.getString("Non")
            }
        };

        /**
        * Liste des onglets
        */
        $scope.tabs = {
            LOT: "lot",
            DOC: "doc",
            TRAIN: "train"
        };

        $scope.taburl = {
            lot: null,
            doc: null,
            train: null
        };

        init();

        /** Initialisation */
        function init() {
            $scope.projectId = $routeParams.id;
            loadDocProject();
            setTab($scope.tabs.DOC);
        }

        function loadDocProject() {
            if (angular.isDefined($scope.projectId)) {
                $scope.project = ProjectSrvc.get({ id: $scope.projectId });
                $scope.project.$promise.then(function () {
                    $scope.loaded = true;
                });
            }
        }

        function getBack() {
            if (angular.isDefined($scope.projectId)) {
                $location.path("/project/project/").search({ id: $scope.projectId });
            }
        }

        /** Charge l'onglet tab */
        function setTab(tab) {
            // selected tab
            $scope.currentTab = tab;
            // tabs URL
            $scope.taburl.doc = $scope.currentTab === $scope.tabs.DOC ? 'scripts/app/project/docs.html' : null;
            $scope.taburl.lot = $scope.currentTab === $scope.tabs.LOT ? 'scripts/app/project/lots.html' : null;
            $scope.taburl.train = $scope.currentTab === $scope.tabs.TRAIN ? 'scripts/app/project/trains.html' : null;
        }

        /** Changement d'onglet + rafraichissement des données */
        function changeTab(tab) {
            if ($scope.currentTab !== tab) {
                setTab(tab);
            }
        }
    }
})();
