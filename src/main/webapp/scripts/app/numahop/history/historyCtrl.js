(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('HistoryCtrl', function ($rootScope, $location, $log, $route, $scope, HistorySrvc) {
        $scope.history = HistorySrvc.getLatest;

        // Initialisation de l'historique
        addCurrentRoute($route.current);

        // Mise à jour de l'historique au changement de route
        var globalEvent = $rootScope.$on('$routeChangeSuccess', function (event, next, current) {
            addCurrentRoute(next);
        });

        // Réinitialisation de l'historique à la destruction du controleur
        $scope.$on('$destroy', function () {
            HistorySrvc.reinit();

            if (globalEvent) {
                globalEvent();
                globalEvent = null;
            }
        });

        /** Ajout de la route actuelle à l'historique **/
        function addCurrentRoute(route) {
            if (angular.isDefined(route.title)) {
                HistorySrvc.add(route.title, route.originalPath, $location.absUrl());
            } else {
                $log.debug('[HistoryCtrl] Pas de champ "title" défini pour la route ' + route.originalPath);
            }
        }
    });
})();
