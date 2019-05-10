(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('TrainDocsCtrl', TrainDocsCtrl);

    function TrainDocsCtrl($location, DocUnitBaseService, gettextCatalog, MessageSrvc, ModalSrvc, PhysicalDocumentSrvc) {

        var docCtrl = this;
        docCtrl.addDocUnits = addDocUnits;
        docCtrl.canRemoveFromTrain = DocUnitBaseService.canRemoveTrain;
        docCtrl.changeItem = changeItem;
        docCtrl.checkAll = checkAll;
        docCtrl.init = init;
        docCtrl.displayDocStatus = displayDocStatus;
        docCtrl.removeItem = removeItem;
        docCtrl.removeMultipleItems = removeMultipleItems;
        docCtrl.uncheckAll = uncheckAll;

        docCtrl.selectedIds = [];

        var docStatus = {
            CREATED: gettextCatalog.getString("Créé"),
            SELECTED: gettextCatalog.getString("Sélectionné"),
            GATHERED: gettextCatalog.getString("Prélevé"),
            STATE_CHECK_REALISED: gettextCatalog.getString("Constat d'état réalisé"),
            IN_DIGITIZATION: gettextCatalog.getString("En cours de numérisation"),
            TO_CHECK: gettextCatalog.getString("A contrôler"),
            TO_SHELVE: gettextCatalog.getString("A ranger"),
            REINTEGRATED: gettextCatalog.getString("Réintégré")
        };

        function init(trainId, train) {
            docCtrl.trainId = trainId;
            docCtrl.train = train;
            loadDigitalDocuments(trainId);
        }

        function displayDocStatus(status) {
            return docStatus[status];
        }

        function loadDigitalDocuments(trainId) {
            var params = {
                train: trainId
            };
            PhysicalDocumentSrvc.query(params, function (value) {
                docCtrl.physDocs = value;
            });
        }

        function addDocUnits() {
            var params = {
                callback: "/train/all_operations?id=" + docCtrl.trainId,
                action: "add_to_train",
                toTrain: docCtrl.trainId
            };
            if (docCtrl.train.project) {
                params.toProject = docCtrl.train.project.identifier;
            }
            $location.path("/document/docunit_list").search(params);
        }

        /**
         * removeMultipleItems - Retrait du projet de tous les exemplaires sélectionnés
         *
         * @param  {Array} identifiers la liste des identifiants des exemplaires
         */
        function removeMultipleItems() {
            removeAllItemFromArray(docCtrl.selectedIds);
        }

        function removeAllItemFromArray(itemArray) {
            MessageSrvc.clearMessages();
            ModalSrvc.confirmAction(gettextCatalog.getPlural(itemArray.length,
                "retirer l'unité documentaire sélectionnée du train", "retirer les {{n}} unités documentaires sélectionnées du train",
                { n: itemArray.length }))
                .then(function () {
                    var docUnits = _.pluck(docCtrl.physDocs, "docUnit");

                    _.each(itemArray, function (id) {
                        var item = _.findWhere(docUnits, { identifier: id });

                        DocUnitBaseService.removeTrain(item, function () {
                            MessageSrvc.addSuccess(gettextCatalog.getString("l'unité documentaire {{doc}} a été retirée du train.", { doc: item.pgcnId }));
                            docCtrl.physDocs = _.filter(docCtrl.physDocs, function (doc) {
                                return doc.docUnit.identifier !== id;
                            });
                        });
                    });

                    uncheckAll();
                });
        }

        /**
         * removeItem - Retrait d'un exemplaire du projet
         *
         * @param  {Item} item l'exemplaire
         */
        function removeItem(item) {
            var itemToRemove = [];
            itemToRemove.push(item.identifier);
            removeAllItemFromArray(itemToRemove);
        }

        function checkAll() {
            _.each(docCtrl.physDocs, function (item) {
                var id = item.docUnit.identifier;
                if (docCtrl.selectedIds.indexOf(id) < 0) {
                    docCtrl.selectedIds.push(id);
                    item.checked = true;
                }
            });
        }

        function uncheckAll() {
            docCtrl.selectedIds = [];
            _.each(docCtrl.physDocs, function (item) {
                item.checked = false;
            });
        }

        function changeItem(item) {
            var id = item.docUnit.identifier;
            var index = docCtrl.selectedIds.indexOf(id);
            if (index >= 0 && !item.checked) {
                docCtrl.selectedIds.splice(index, 1);
            } else if (item.checked) {
                docCtrl.selectedIds.push(id);
            }
        }
    }
})();