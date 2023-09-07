/**
 * Contrôle de recherche
 */
(function () {
    'use strict';

    angular.module('numaHopApp.component').component('numaSearchControl', {
        require: {
            controlCtrl: '^numaSearchControls',
        },
        bindings: {
            controlModel: '=', // modèle
            isFirst: '<', // certains contrôles sont cachés pour le 1er champ de recherche
            onSearch: '&', // lancement de la recherche depuis le champ
            onRemove: '&', // suppression du champ de recherche
            target: '<', // type des données recherchées
        },
        controller: 'NumaSearchControlCtrl',
        templateUrl: '/scripts/components/components/numaSearchControl/numaSearchControl.html',
    });
})();
