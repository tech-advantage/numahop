(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('RelationsAllOperationsCtrl', RelationsAllOperationsCtrl);

    function RelationsAllOperationsCtrl($location, DocUnitSrvc, gettext, MessageSrvc, ModalSrvc) {

        var relCtrl = this;
        relCtrl.addChild = addChild;
        relCtrl.addSibling = addSibling;
        relCtrl.init = init;
        relCtrl.newChild = newChild;
        relCtrl.removeChild = removeChild;
        relCtrl.removeParent = removeParent;
        relCtrl.removeSibling = removeSibling;
        relCtrl.selectParent = selectParent;


        /**
         * Initialisation du contrôleur, appelée au chargement de la vue
         */
        function init(docUnit) {
            relCtrl.docUnit = docUnit;
            // UD enfants
            if (docUnit.nbChildren > 0) {
                relCtrl.children = DocUnitSrvc.query({ parent: docUnit.identifier });
            }
            else {
                relCtrl.children = [];
            }
            // UD soeurs
            if (docUnit.nbSiblings > 0) {
                relCtrl.siblings = DocUnitSrvc.query({ sibling: docUnit.identifier });
            }
            else {
                relCtrl.siblings = [];
            }
        }

        /**
         * Ajout d'un lien vers une unité documentaire enfant
         * 
         */
        function addChild() {
            var params = { multiple: true, disabled: getDocUnitIds() };
            if (relCtrl.docUnit.library) {
                params.library = relCtrl.docUnit.library.identifier;
            }
            ModalSrvc.selectDocUnit(params)
                .then(function (selection) {
                    DocUnitSrvc.save({ parent: relCtrl.docUnit.identifier, addchild: true }, _.pluck(selection, "identifier")).$promise
                        .then(function (data) {
                            MessageSrvc.addSuccess(gettext("L'unité documentaire {{pgcnId}} {{label}} a été sauvegardée"), relCtrl.docUnit);
                            relCtrl.children = relCtrl.children.concat(selection);
                            relCtrl.docUnit.nbChildren = data.nbChildren;
                        });
                });
        }

        /**
         * Création d'une nouvelle unité documentaire enfant
         * 
         */
        function newChild() {
            $location.path("/document/docunit").search({ new: true, parent: relCtrl.docUnit.identifier });
        }

        /**
         * Suppression du lien vers cette unité documentaire fille
         * 
         * @param {any} child 
         */
        function removeChild(child) {
            DocUnitSrvc.save({ parent: relCtrl.docUnit.identifier, child: child.identifier, removechild: true }, {}).$promise
                .then(function (data) {
                    MessageSrvc.addSuccess(gettext("L'unité documentaire {{pgcnId}} {{label}} a été sauvegardée"), relCtrl.docUnit);
                    var idx = relCtrl.children.indexOf(child);
                    if (idx >= 0) {
                        relCtrl.children.splice(idx, 1);
                        relCtrl.docUnit.nbChildren = data.nbChildren;
                    }
                });
        }

        /**
         * Sélection d'un unité documentaire parente
         * 
         */
        function selectParent() {
            var params = { multiple: false, disabled: getDocUnitIds() };
            if (relCtrl.docUnit.library) {
                params.library = relCtrl.docUnit.library.identifier;
            }
            ModalSrvc.selectDocUnit(params)
                .then(function (selection) {
                    if (selection.length > 0) {
                        relCtrl.docUnit.parentIdentifier = selection[0].identifier;
                        relCtrl.docUnit.parentLabel = selection[0].label;
                        relCtrl.docUnit.parentPgcnId = selection[0].pgcnId;

                        relCtrl.docUnit.$save().then(function () {
                            MessageSrvc.addSuccess(gettext("L'unité documentaire {{pgcnId}} {{label}} a été sauvegardée"), relCtrl.docUnit);
                        });
                    }
                });
        }

        /**
         * Suppression du lien vers l'unité documentaire parente
         */
        function removeParent() {
            relCtrl.docUnit.parentIdentifier = null;
            relCtrl.docUnit.parentLabel = null;
            relCtrl.docUnit.parentPgcnId = null;

            relCtrl.docUnit.$save().then(function () {
                MessageSrvc.addSuccess(gettext("L'unité documentaire {{pgcnId}} {{label}} a été sauvegardée"), relCtrl.docUnit);
            });
        }

        /**
         * Ajout d'un lien vers une unité documentaire soeur
         * 
         */
        function addSibling() {
            var params = { multiple: true, disabled: getDocUnitIds() };
            if (relCtrl.docUnit.library) {
                params.library = relCtrl.docUnit.library.identifier;
            }
            ModalSrvc.selectDocUnit(params)
                .then(function (selection) {
                    DocUnitSrvc.save({ siblingid: relCtrl.docUnit.identifier, addsibling: true }, _.pluck(selection, "identifier")).$promise
                        .then(function (data) {
                            MessageSrvc.addSuccess(gettext("L'unité documentaire {{pgcnId}} {{label}} a été sauvegardée"), relCtrl.docUnit);
                            relCtrl.siblings = relCtrl.siblings.concat(selection);
                            relCtrl.docUnit.nbSiblings = data.nbSiblings;
                        });
                });
        }

        /**
         * Suppression du lien vers cette unité documentaire soeur
         * 
         * @param {any} child 
         */
        function removeSibling(sibling) {
            DocUnitSrvc.save({ siblingid: relCtrl.docUnit.identifier, removesibling: sibling.identifier }, {}).$promise
                .then(function (data) {
                    MessageSrvc.addSuccess(gettext("L'unité documentaire {{pgcnId}} {{label}} a été sauvegardée"), relCtrl.docUnit);
                    var idx = relCtrl.siblings.indexOf(sibling);
                    if (idx >= 0) {
                        relCtrl.siblings.splice(idx, 1);
                        relCtrl.docUnit.nbSiblings = data.nbSiblings;
                    }
                });
        }

        /**
         * Renvoie tous les identifiants d'unités documentaires déjà liées à l'unité courante
         * 
         * @returns 
         */
        function getDocUnitIds() {
            var ids = _.chain(relCtrl.children).union(relCtrl.siblings).pluck("identifier").value();
            ids.push(relCtrl.docUnit.identifier);
            if (relCtrl.docUnit.parentIdentifier) {
                ids.push(relCtrl.docUnit.parentIdentifier);
            }
            return ids;
        }
    }
})();