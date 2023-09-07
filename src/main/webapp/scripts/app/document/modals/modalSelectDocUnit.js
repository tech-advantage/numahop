(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('ModalSelectDocUnitCtrl', ModalSelectDocUnitCtrl);

    function ModalSelectDocUnitCtrl($uibModalInstance, DocUnitSrvc, options) {
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
            size: PAGE_SIZE,
        };

        mainCtrl.selection = [];

        init();

        /**
         * Initialisation du contrôleur
         *
         */
        function init() {
            mainCtrl.multiple = !!options.multiple;
            mainCtrl.disabled = options.disabled || [];
            mainCtrl.library = options.library;
            getPage();
        }

        /**
         * Chargement de la prochaine page d'items
         * @return {[type]} [description]
         */
        function getPage() {
            var params = {};
            params['page'] = mainCtrl.pagination.page - 1;
            params['size'] = PAGE_SIZE;

            var body = getSearchParams();

            return DocUnitSrvc.searchAsList(params, body).$promise.then(handlePageOfItems);
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
                    } else {
                        return sel;
                    }
                });
            }
            if (mainCtrl.disabled.length > 0) {
                _.each(mainCtrl.pagination.items, function (it) {
                    if (mainCtrl.disabled.indexOf(it.identifier) >= 0) {
                        it._disabled = true;
                    }
                });
            }
        }

        /**
         * Recherche d'entité
         */
        function search(event) {
            if (angular.isDefined(event) && event.type === 'keypress' && event.keyCode !== 13) {
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
            params['active'] = true;

            // Bibliothèque
            if (mainCtrl.library) {
                params['libraries'] = [mainCtrl.library];
            }

            params['search'] = mainCtrl.searchRequest || '';
            return params;
        }

        function select(docUnit) {
            if (mainCtrl.disabled.indexOf(docUnit.identifier) >= 0) {
                return;
            }
            // Toggle sélection
            if (docUnit._selected) {
                delete docUnit._selected;

                var idx = mainCtrl.selection.indexOf(docUnit);
                if (idx >= 0) {
                    mainCtrl.selection.splice(idx, 1);
                }
            } else {
                docUnit._selected = true;
                mainCtrl.selection.push(docUnit);
            }
            // Sélection simple => réinitialisation de la liste de sélections
            if (!mainCtrl.multiple) {
                mainCtrl.selection = _.chain(mainCtrl.selection)
                    .map(function (sel) {
                        if (sel.identifier !== docUnit.identifier) {
                            delete sel._selected;
                            return;
                        } else {
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
            $uibModalInstance.dismiss('cancel');
        }

        /**
         * Fermeture / Sélection
         */
        function ok() {
            $uibModalInstance.close(mainCtrl.selection);
        }
    }
})();
