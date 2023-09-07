(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('ModalZ3950SearchCtrl', ModalZ3950SearchCtrl);

    function ModalZ3950SearchCtrl($uibModalInstance, options, gettext, MessageSrvc, Z3950ServerSrvc, Z3950Srvc) {
        var mainCtrl = this;
        mainCtrl.cancel = cancel;
        mainCtrl.newSearch = newSearch;
        mainCtrl.importZ3950 = importZ3950;
        mainCtrl.isValidSearch = isValidSearch;
        mainCtrl.onchangeTarget = onchangeTarget;
        mainCtrl.search = search;

        mainCtrl.pagination = {
            currentPage: 1,
            itemsPerPage: 10,
            totalItems: 0,
            totalPages: 0,
            loaded: true,
        };
        mainCtrl.searchParameters = {};
        mainCtrl.searchResults = null;

        init();

        /** Initialisation du contrôleur */
        function init() {
            _.extend(mainCtrl, options);

            // Chargement des servers Z39.50
            mainCtrl.searchParameters.targets = Z3950ServerSrvc.dto();
        }
        /** Recherche */
        function search(searchParameters) {
            if (!mainCtrl.pagination.loaded) {
                return;
            }

            mainCtrl.pagination.loaded = false;
            MessageSrvc.clearMessages();

            var activeTargets = getActiveTargets();
            var idsOfTargets = _.pluck(activeTargets, 'identifier');
            var fields = angular.copy(searchParameters);
            delete fields.targets;

            return Z3950Srvc.search({ server: idsOfTargets, page: mainCtrl.pagination.currentPage - 1, size: mainCtrl.pagination.itemsPerPage }, fields)
                .$promise.then(function (page) {
                    mainCtrl.searchResults = page.content;
                    mainCtrl.pagination.totalItems = page.totalElements;
                    mainCtrl.pagination.totalPages = page.totalPages;

                    mainCtrl.pageResults = _.filter(mainCtrl.searchResults, function (searchResult, index) {
                        return index < mainCtrl.pagination.itemsPerPage;
                    });
                })
                .finally(function () {
                    mainCtrl.pagination.loaded = true;
                });
        }
        /** Recherche avec de nouveaux paramètres */
        function newSearch(searchParameters, event) {
            if ((!event || event.type !== 'keypress' || event.keyCode === 13) && mainCtrl.isValidSearch(searchParameters)) {
                mainCtrl.pagination.currentPage = 1;
                mainCtrl.pagination.totalItems = 0;
                mainCtrl.pagination.totalPages = 0;
                mainCtrl.search(searchParameters);
            }
        }
        /** Validation des paramètres de recherche */
        function isValidSearch(searchParameters) {
            var existsActiveTargets = _.some(searchParameters.targets, function (target) {
                return target.active;
            });
            //Au moins un champ rempli et une cible de recherche renseignée
            return (searchParameters.title || searchParameters.isbn || searchParameters.issn || searchParameters.author) && existsActiveTargets && mainCtrl.pagination.loaded;
        }
        /** Cible de recherche courante */
        function getActiveTargets() {
            return _.filter(mainCtrl.searchParameters.targets, function (currentTarget) {
                return currentTarget.active;
            });
        }
        /** Activation de la cible sélectionnée */
        function onchangeTarget(target) {
            if (target.active) {
                var activeTargets = _.filter(mainCtrl.searchParameters.targets, function (currentTarget) {
                    return currentTarget.active && currentTarget.identifier !== target.identifier;
                });
                _.each(activeTargets, function (activeTarget) {
                    activeTarget.active = false;
                });
            }
        }
        /** Sélection de la notice à importer */
        function importZ3950(z3950record) {
            $uibModalInstance.close(z3950record);
        }
        /** Annulation de la modale */
        function cancel() {
            $uibModalInstance.dismiss('cancel');
        }
    }
})();
