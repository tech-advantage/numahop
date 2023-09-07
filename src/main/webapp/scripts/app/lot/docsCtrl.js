(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('LotDocsCtrl', LotDocsCtrl);

    function LotDocsCtrl($http, $httpParamSerializer, $location, codeSrvc, DocUnitBaseService, DocUnitSrvc, WorkflowLotHandleSrvc, FileSaver, gettext, gettextCatalog, MessageSrvc, ModalSrvc) {
        var docCtrl = this;
        docCtrl.addDocUnits = addDocUnits;
        docCtrl.changeItem = changeItem;
        docCtrl.checkAll = checkAll;
        docCtrl.deleteItem = deleteItem;
        docCtrl.deleteMultipleItems = deleteMultipleItems;
        docCtrl.exportCSV = exportCSV;
        docCtrl.init = loadDocUnits;
        docCtrl.semCodes = codeSrvc;
        docCtrl.uncheckAll = uncheckAll;

        docCtrl.selectedIds = [];
        docCtrl.options = DocUnitBaseService.options;

        docCtrl.isLotLocked = WorkflowLotHandleSrvc.isLotLocked;

        /**
         * Chargement de la liste des unités documentaires appartenant à ce lot
         *
         * @param {any} lotId
         */
        function loadDocUnits(lotId, lot) {
            docCtrl.lotId = lotId;
            docCtrl.lot = lot;
            docCtrl.docUnits = DocUnitSrvc.query({ lot: lotId });
        }

        function changeItem(item) {
            var index = docCtrl.selectedIds.indexOf(item.identifier);
            if (index >= 0 && !item.checked) {
                docCtrl.selectedIds.splice(index, 1);
            } else if (item.checked) {
                docCtrl.selectedIds.push(item.identifier);
            }
        }

        /**
         * deleteItem - Suppression d'un exemplaire
         *
         * @param  {Item} item l'exemplaire
         */
        function deleteItem(item) {
            DocUnitSrvc.removeLot(item).$promise.then(function () {
                loadDocUnits(docCtrl.lotId, docCtrl.lot);
            });
        }

        function addDocUnits() {
            var params = {
                callback: '/lot/all_operations?id=' + docCtrl.lotId,
                action: 'add_to_lot',
                toLot: docCtrl.lotId,
            };
            if (docCtrl.lot.project) {
                params.toProject = docCtrl.lot.project.identifier;
            }
            $location.path('/document/docunit_list').search(params);
        }

        function exportCSV() {
            ModalSrvc.configureCsvExport().then(function (params) {
                params.docUnit = docCtrl.selectedIds;
                var url = 'api/rest/export/csv?' + $httpParamSerializer(params);

                // on met la réponse dans un arraybuffer pour conserver l'encodage original dans le fichier sauvegardé
                $http.get(url, { responseType: 'arraybuffer' }).then(function (response) {
                    var filename = 'lot-' + docCtrl.lot.label.replace(/\W+/g, '_') + '.csv';
                    var blob = new Blob([response.data], { type: response.headers('content-type') });
                    FileSaver.saveAs(blob, filename);
                });
            });
        }

        /**
         * deleteMultipleItems - Suppression de tous les exemplaires sélectionnés
         *
         * @param  {Array} identifiers la liste des identifiants des exemplaires
         */
        function deleteMultipleItems() {
            deleteAllItemFromArray(docCtrl.selectedIds);
        }

        function deleteAllItemFromArray(itemArray) {
            MessageSrvc.clearMessages();
            ModalSrvc.confirmDeletion(gettextCatalog.getPlural(itemArray.length, "l'unité documentaire sélectionnée", 'les {{n}} unités documentaires sélectionnées', { n: itemArray.length })).then(function () {
                DocUnitSrvc.removeAllFromLot({}, itemArray, function () {
                    MessageSrvc.addSuccess(gettext('Les unités documentaires ont bien été retirées du lot'));
                    loadDocUnits(docCtrl.lotId, docCtrl.lot);
                });
                uncheckAll();
            });
        }

        function checkAll() {
            _.each(docCtrl.docUnits, function (item) {
                if (docCtrl.selectedIds.indexOf(item.identifier) < 0) {
                    docCtrl.selectedIds.push(item.identifier);
                    item.checked = true;
                }
            });
        }

        function uncheckAll() {
            docCtrl.selectedIds = [];
            _.each(docCtrl.docUnits, function (item) {
                item.checked = false;
            });
        }
    }
})();
