(function () {
    'use strict';

    angular.module("numaHopApp.controller")
        .controller("ModalSelectBibRecordCtrl", ModalSelectBibRecordCtrl);

    function ModalSelectBibRecordCtrl($uibModalInstance, RecordSrvc, options) {

        var mainCtrl = this;
        mainCtrl.close = close;
        mainCtrl.getPage = getPage;
        mainCtrl.ok = ok;
        mainCtrl.search = search;
        mainCtrl.select = select;

        var PAGE_SIZE = 20;
        var PAGE_START = 1;

        /**
         * Objet de pagination
         * @type {Object}
         */
        mainCtrl.pagination = {
            items: [],
            totalItems: 0,
            busy: true,
            page: PAGE_START,
            size: PAGE_SIZE
        };

        mainCtrl.selection = [];

        init();

        /**
         * Initialisation du contrôleur
         * 
         */
        function init() {
            mainCtrl.multiple = !!options.multiple;
            mainCtrl.library = options.library;

            if (angular.isDefined(options.orphan)) {
                mainCtrl.orphan = options.orphan;
            }
            getPage();
        }

        /**
         * Chargement de la prochaine page d'items
         * @return {[type]} [description]
         */
        function getPage() {
            var params = getSearchParams();
            return RecordSrvc.search(params).$promise.then(handlePageOfItems);
        }

        /**
         * handlePageOfItems - Gestion d'une page d'entités
         *
         * @param  {type} pageOfReserves la page avec les entités
         */
        function handlePageOfItems(pageOfItems) {
            mainCtrl.pagination.totalItems = pageOfItems.totalElements;
            mainCtrl.pagination.totalPages = pageOfItems.totalPages;
            mainCtrl.pagination.busy = false;
            mainCtrl.pagination.items = pageOfItems.content;

            // Mise à jour de la sélection
            if (mainCtrl.selection.length > 0) {
                mainCtrl.selection = _.map(mainCtrl.selection, function (sel) {
                    var found = _.find(mainCtrl.pagination.items, function (it) {
                        return sel.identifier === it.identifier;
                    });
                    if (found) {
                        found._selected = true;
                        return found;
                    }
                    else {
                        return sel;
                    }
                });
            }
        }

        /**
         * Recherche d'entité
         */
        function search(event) {
            if (angular.isDefined(event) && event.type === "keypress" && event.keyCode !== 13) {
                return;
            }

            mainCtrl.pagination.busy = true;
            mainCtrl.pagination.items = [];
            mainCtrl.pagination.totalItems = 0;
            mainCtrl.pagination.page = PAGE_START;

            getPage();
        }

        /**
         * Construction des paramètres de la recherche en fonction des filtres sélectionnés
         * @return {[type]} [description]
         */
        function getSearchParams() {
            var params = {};
            params["page"] = mainCtrl.pagination.page - 1;
            params["size"] = PAGE_SIZE;
            params["search"] = mainCtrl.searchRequest || "";

            // Bibliothèque
            if (mainCtrl.library) {
                params["libraries"] = mainCtrl.library;
            }
            // Notices orphelines
            if (angular.isDefined(mainCtrl.orphan)) {
                params["orphan"] = mainCtrl.orphan;
            }
            return params;
        }

        function select(bibRecord) {
            // Toggle sélection
            if (bibRecord._selected) {
                delete bibRecord._selected;

                var idx = mainCtrl.selection.indexOf(bibRecord);
                if (idx >= 0) {
                    mainCtrl.selection.splice(idx, 1);
                }
            }
            else {
                bibRecord._selected = true;
                mainCtrl.selection.push(bibRecord);
            }
            // Sélection simple => réinitialisation de la liste de sélections
            if (!mainCtrl.multiple) {
                mainCtrl.selection = _.chain(mainCtrl.selection)
                    .map(function (sel) {
                        if (sel.identifier !== bibRecord.identifier) {
                            delete sel._selected;
                            return;
                        }
                        else {
                            return sel;
                        }
                    })
                    .filter(angular.isDefined)
                    .value();
            }
        }

        /**
         * Fermeture / Annulation
         */
        function close() {
            $uibModalInstance.dismiss("cancel");
        }

        /**
         * Fermeture / Sélection
         */
        function ok() {
            $uibModalInstance.close(mainCtrl.selection);
        }
    }
})();