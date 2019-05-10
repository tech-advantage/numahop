(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('LotAllOperationsCtrl', LotAllOperationsCtrl);

    function LotAllOperationsCtrl($location, $routeParams, $scope, LotSrvc) {

        $scope.changeTab = changeTab;
        $scope.getBack = getBack;
        
        /**
        * Liste des onglets
        */
        $scope.tabs = {
            DOC: "doc",
            DELIVERY: "delivery"
        };

        $scope.taburl = {
            doc: null,
            delivery: null
        };

        init();

        /** Initialisation */
        function init() {
            $scope.lotId = $routeParams.id;
            loadLotDocs();
            setTab($scope.tabs.DOC);
        }

        function loadLotDocs() {
            if (angular.isDefined($scope.lotId)) {
                $scope.lot = LotSrvc.get({ id: $scope.lotId });
                $scope.lot.$promise.then(function(entity){
                    $scope.loaded = true;
                });
            }
        }

        /** Charge l'onglet tab */
        function setTab(tab) {
            // selected tab
            $scope.currentTab = tab;
            // tabs URL
            $scope.taburl.doc = $scope.currentTab === $scope.tabs.DOC ? 'scripts/app/lot/docs.html' : null;
            $scope.taburl.delivery = $scope.currentTab === $scope.tabs.DELIVERY ? 'scripts/app/lot/deliveries.html' : null;
        }

        /** Changement d'onglet + rafraichissement des données */
        function changeTab(tab) {
            if ($scope.currentTab !== tab) {
                setTab(tab);
            }
        }

        function getBack() {
            var search = { id: $scope.lotId };
            $location.path("/lot/lot/").search(search);
        }
    }
})();
