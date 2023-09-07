(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('ProjectDocsCtrl', ProjectDocsCtrl);

    function ProjectDocsCtrl($location, DocUnitBaseService, DocUnitSrvc, gettextCatalog, MessageSrvc, WorkflowLotHandleSrvc, ModalSrvc) {
        var docCtrl = this;
        docCtrl.addDocUnits = addDocUnits;
        docCtrl.canRemoveFromProject = DocUnitBaseService.canRemoveProject;
        docCtrl.changeItem = changeItem;
        docCtrl.checkAll = checkAll;
        docCtrl.getPage = getPage;
        docCtrl.init = loadDocUnits;
        docCtrl.isDocUnitLocked = isDocUnitLocked;
        docCtrl.removeItem = removeItem;
        docCtrl.removeMultipleItems = removeMultipleItems;
        docCtrl.uncheckAll = uncheckAll;

        var PAGE_START = 1;
        docCtrl.selectedIds = [];

        /**
         * Objet de pagination
         * @type {Object}
         */
        docCtrl.pagination = {
            items: [],
            totalItems: 0,
            busy: false,
            page: PAGE_START,
            size: 50,
        };

        function loadDocUnits(projectId) {
            docCtrl.projectId = projectId;

            getPage().then(function () {
                docCtrl.loaded = true;
            });
        }

        function getPage() {
            docCtrl.pagination.busy = true;
            let params = {};
            params['page'] = docCtrl.pagination.page - 1;
            params['project'] = docCtrl.projectId;
            return DocUnitSrvc.searchAllForProject(params).$promise.then(handlePageOfItems);
        }

        /**
         * handlePageOfItems - Gestion d'une page d'entités
         *
         * @param  {type} pageOfReserves la page avec les entités
         */
        function handlePageOfItems(pageOfItems) {
            docCtrl.pagination.totalItems = pageOfItems.totalElements;
            docCtrl.pagination.totalPages = pageOfItems.totalPages;
            docCtrl.pagination.items = pageOfItems.content;

            _.each(docCtrl.pagination.items, function (item) {
                let findedIndex = _.find(docCtrl.selectedIds, selectedId => {
                    return item.identifier === selectedId;
                });
                item.checked = !!findedIndex;
            });

            docCtrl.pagination.busy = false;
        }

        function addDocUnits() {
            $location.path('/document/docunit_list').search({
                callback: '/project/all_operations?id=' + docCtrl.projectId,
                action: 'add_to_project',
                toProject: docCtrl.projectId,
            });
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
            ModalSrvc.confirmAction(
                gettextCatalog.getPlural(itemArray.length, "retirer l'unité documentaire sélectionnée du projet", 'retirer les {{n}} unités documentaires sélectionnées du projet', { n: itemArray.length })
            ).then(function () {
                _.each(itemArray, function (id) {
                    var item = _.findWhere(docCtrl.pagination.items, { identifier: id });

                    DocUnitBaseService.removeProject(item, function () {
                        MessageSrvc.addSuccess(gettextCatalog.getString("l'unité documentaire {{doc}} a été retirée du projet.", { doc: item.pgcnId }));
                        docCtrl.pagination.items = _.without(docCtrl.pagination.items, _.findWhere(docCtrl.pagination.items, { identifier: id }));
                        if (docCtrl.pagination.items.length === 0) {
                            location.reload();
                        }
                    });
                });
                uncheckAll();
            });
        }

        /**
         * Vérifie si les items dans la sélection peuvent être supprimés
         */
        function isDocUnitLocked() {
            _.each(docCtrl.selectedIds, function (id) {
                var item = _.findWhere(docCtrl.pagination.items, { identifier: id });
                if (WorkflowLotHandleSrvc.isDocFromLotLocked(item)) {
                    return true;
                }
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
            _.each(docCtrl.pagination.items, function (item) {
                if (docCtrl.selectedIds.indexOf(item.identifier) < 0) {
                    docCtrl.selectedIds.push(item.identifier);
                    item.checked = true;
                }
            });
        }

        function uncheckAll() {
            docCtrl.selectedIds = [];
            _.each(docCtrl.pagination.items, function (item) {
                item.checked = false;
            });
        }

        function changeItem(item) {
            var index = docCtrl.selectedIds.indexOf(item.identifier);
            if (index >= 0 && !item.checked) {
                docCtrl.selectedIds.splice(index, 1);
            } else if (item.checked) {
                docCtrl.selectedIds.push(item.identifier);
            }
        }
    }
})();
