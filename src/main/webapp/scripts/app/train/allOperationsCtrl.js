(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('TrainAllOperationsCtrl', TrainAllOperationsCtrl);

    function TrainAllOperationsCtrl($location, $routeParams, $scope, TrainSrvc) {

        $scope.changeTab = changeTab;
        $scope.getBack = getBack;

        /**
        * Liste des onglets
        */
        $scope.tabs = {
            DOC: "doc"
        };

        $scope.taburl = {
            doc: null
        };


        init();

        /** Initialisation */
        function init() {
            loadTrain();
            setTab($scope.tabs.DOC);
        }

        function loadTrain() {
            if (angular.isDefined($routeParams.id)) {
                $scope.trainId = $routeParams.id;
                $scope.train = TrainSrvc.get({ id: $routeParams.id });
                $scope.train.$promise.then(function () {
                    $scope.loaded = true;
                });
            }
        }

        /** Charge l'onglet tab */
        function setTab(tab) {
            // selected tab
            $scope.currentTab = tab;
            // tabs URL
            $scope.taburl.doc = $scope.currentTab === $scope.tabs.DOC ? 'scripts/app/train/docs.html' : null;
        }

        /** Changement d'onglet + rafraichissement des données */
        function changeTab(tab) {
            if ($scope.currentTab !== tab) {
                setTab(tab);
            }
        }

        function getBack() {
            $location.path("/train/train/").search({ id: $scope.train.identifier });
        }
    }
})();
