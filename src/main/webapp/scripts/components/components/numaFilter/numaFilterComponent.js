/**
 * Bouton + Box de filtrage d'une liste de résultats.
 * La box de filtrage ne définit pas de filtre, mais seulement l'élément graphique qui les contient.
 * Les filtres sont repris via transclude.
 *
 * Exemple avec un seul champ de filtrage:
 *
 * <numa-filter data-class="pull-left btn-filter">
 *     <numa-filter-field data-title="{{ ::'Étape' | translate }}">
 *         <sem-field sem-type="uiselect" sem-model="statCtrl.filters.workflow" sem-option-data="statCtrl.config.workflow"
 *                    sem-onchange="statCtrl.updateColumns(true)"></sem-field>
 *     </numa-filter-field>
 * </numa-filter>
 */
(function () {
    'use strict';

    angular.module('numaHopApp.component').component('numaFilter', {
        bindings: {
            class: '@',
        },
        controller: function ($scope, $semFilters) {
            var ctrl = this;
            ctrl.semFilters = $scope.$semFilters = $semFilters;
        },
        transclude: true,
        templateUrl: '/scripts/components/components/numaFilter/numaFilter.html',
    });
})();
