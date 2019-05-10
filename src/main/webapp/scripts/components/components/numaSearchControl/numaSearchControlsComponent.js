/**
 * Ce composant mutualise le chargement des données des composants numaSearchControl
 */
(function () {
    "use strict";

    angular.module('numaHopApp.component')
        .component("numaSearchControls", {
            controller: "NumaSearchControlsCtrl",
            transclude: true,
            templateUrl: "/scripts/components/components/numaSearchControl/numaSearchControls.html"
        });
})();