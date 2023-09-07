(function () {
    'use strict';

    angular.module('numaHopApp.component').component('numaAlertAuditEntity', {
        bindings: {
            columns: '<', // colonnes du tableau, liste d'objets {name, field}
            filter: '<', // filtres à passé lors du chargement des données
            prefix: '@statusPrefix', // préfixes pour récupérer le libellé du statut dans codeSrvc
            setCount: '&', // fonction appelée avec en paramètre le nombre de résultats récupérés
            type: '@entityType', // type de donnée
            provider: '&', // optionnel, fonction de chargement des données
        },
        controller: 'NumaAlertAuditEntityCtrl',
        templateUrl: '/scripts/components/components/numaAlertAuditEntity/numaAlertAuditEntity.html',
    });
})();
