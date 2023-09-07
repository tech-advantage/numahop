(function () {
    'use strict';

    /**
     * Champ d'un formulaire xeditable
     * Un clic sur le champ ouvre tout le formulaire
     *
     * Exemple:
     * <form editable-form name="mailboxForm" onaftersave="editCtrl.saveMailbox()" ng-if="editCtrl.loaded">
     *      <numa-editable-field title="Libellé" placeholder="Libellé" form="mailboxForm" model="editCtrl.box.label"></numa-editable-field>
     *      <numa-editable-field title="Bibliothèque" type="select" placeholder="Bibliothèque" form="mailboxForm" model="editCtrl.box.library"
     *                       config="{ data: mainCtrl.options.libraries,
     *                                 display: 'name',
     *                                 query: 'o.name for o in $ctrl.config.data track by o.identifier' }"></numa-editable-field>
     *
     *      <span ng-show="mailboxForm.$visible">
     *          <button type="submit" class="btn btn-primary" ng-disabled="mailboxForm.$waiting">Save</button>
     *          <button type="button" class="btn btn-default" ng-disabled="mailboxForm.$waiting" ng-click="mailboxForm.$cancel()">Cancel</button>
     *      </span>
     * </form>
     */
    angular.module('numaHopApp.component').component('numaEditableField', {
        bindings: {
            config: '<', // configuration des listes, sélecteurs, ...
            defaultText: '@', // texte par défaut si pas de valeur renseignée
            errors: '@', // erreurs remontées du serveur
            form: '<', // formulaire xeditable parent
            id: '@', // identifiant unique de ce champ
            link: '<', // configuration du lien
            maxlength: '@', // longueur max
            model: '=', // modèle
            onbeforesave: '&', // validation du champ avant sauvegarde
            numaOnchange: '&', // modification apportée à un champ
            mandatory: '<', // champ obligatoire (affichage uniquement, ajouter un contrôle sur onbeforesave)
            numaReadonly: '<', // champ en lecture seule
            passwordType: '<', // pour afficher les règles pour le mot de passe
            placeholder: '@', // placeholder
            title: '@', // libellé du champ
            tooltip: '@', // tooltip
            tooltipFn: '&', // tooltip dynamique
            type: '@', // type de champ
            value: '@', // à utiliser au lieu de model pour type = readonly
        },
        controller: 'NumaEditableFieldCtrl',
        templateUrl: '/scripts/components/components/numaEditableField/numaEditableField.html',
    });
})();
