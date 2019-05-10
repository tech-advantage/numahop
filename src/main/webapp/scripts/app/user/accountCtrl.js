(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('AccountCtrl', AccountCtrl);

    function AccountCtrl($scope, $timeout, gettextCatalog, MessageSrvc, Principal, UserSrvc) {

        var mainCtrl = this;
        mainCtrl.getCategoryLabel = getCategoryLabel;

        mainCtrl.options = {
            boolean: {
                true: gettextCatalog.getString('Oui'),
                false: gettextCatalog.getString('Non')
            },
            category: {
                "PROVIDER": gettextCatalog.getString('Prestataire'),
                "OTHER": gettextCatalog.getString('Utilisateur')
            }
        };

        $timeout(init);

        function init() {
            mainCtrl.userId = Principal.identifier();

            if (angular.isDefined(mainCtrl.userId)) {
                mainCtrl.user = UserSrvc.get({ id: mainCtrl.userId });

                mainCtrl.user.$promise.then(function () {
                    mainCtrl.canEdit = $scope.isAuthorized('USER-HAB6');
                    if (!mainCtrl.canEdit) {
                        MessageSrvc.addWarn('Pour éditer vos informations, faites appel à un administrateur.');
                    }
                    mainCtrl.loaded = true;
                });
            }
        }

        function getCategoryLabel(category) {
            return mainCtrl.options.category[category] || category;
        }
    }
})();