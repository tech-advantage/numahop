(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('MailboxCtrl', MailboxCtrl);

    function MailboxCtrl($routeParams, $scope, $timeout, MailboxSrvc, NumaHopInitializationSrvc, NumahopStorageService, StringTools) {

        var mainCtrl = this;
        mainCtrl.create = create;
        mainCtrl.getFirstLetter = getFirstLetter;
        mainCtrl.edit = edit;
        mainCtrl.reinitFilters = reinitFilters;
        mainCtrl.search = search;

        mainCtrl.options = { libraries: [] };
        mainCtrl.filters = { libraries: [] };
        mainCtrl.loaded = true;
        mainCtrl.newBoxes = [];

        var FILTER_STORAGE_SERVICE_KEY = "conf_mail";

        init();

        /**
         * Initialisation du contrôleur
         */
        function init() {
            initFilters();
            loadLibraries()
                .then(function () {
                    return search();
                })
                .then(function () {
                    if (angular.isDefined($routeParams.id)) {
                        select($routeParams.id);
                    }
                    refreshTemplate();
                });
        }

        /**
         * Initialisation des critères de filtrage à partir du localstorage
         * 
         */
        function initFilters() {
            var filters = NumahopStorageService.getFilter(FILTER_STORAGE_SERVICE_KEY);
            if (filters) {
                mainCtrl.filters = filters;
            }
        }

        /**
         * Chargement de la liste des bibliothèques
         * 
         * @returns 
         */
        function loadLibraries() {
            return NumaHopInitializationSrvc.loadLibraries()
                .then(function (libs) {
                    mainCtrl.options.libraries = libs;
                });
        }

        /**
         * Création d'une nouvelle configuration
         * 
         */
        function create() {
            mainCtrl.editedBox = {};
            setSelection();
            refreshTemplate();
        }

        /**
         * Édition d'une configuration existante
         * 
         * @param {any} box 
         */
        function edit(box) {
            mainCtrl.editedBox = box;
            setSelection(box);
            // Écran d'édition
            refreshTemplate();
        }

        function refreshTemplate() {
            return $timeout(function () {
                mainCtrl.configurationInclude = null;
                $scope.$apply();
                mainCtrl.configurationInclude = "scripts/app/configuration/mailbox/mailboxEdit.html";
            });
        }

        /**
         * Recherche des configurations
         * 
         * @param {any} event 
         * @returns 
         */
        function search(event) {
            // Si la recherche est lancée depuis le clavier => touche entrée
            if (event && event.type === "keypress" && event.keyCode !== 13) {
                return;
            }
            mainCtrl.loaded = false;
            NumahopStorageService.saveFilter(FILTER_STORAGE_SERVICE_KEY, mainCtrl.filters);

            var params = {
                library: _.pluck(mainCtrl.filters.libraries, "identifier"),
                search: mainCtrl.filters.search,
                active: !mainCtrl.filters.inactive
            };
            mainCtrl.mailboxes = MailboxSrvc.query(params);

            return mainCtrl.mailboxes.$promise.then(function () {
                if (mainCtrl.editedBox) {
                    select(mainCtrl.editedBox.identifier);
                }
                mainCtrl.loaded = true;
            });
        }

        /**
         * Sélection d'une configuration à partir de son identifiant
         * 
         * @param {any} id 
         */
        function select(id) {
            if (id) {
                var found = _.find(mainCtrl.mailboxes, function (box) {
                    return box.identifier === id;
                });
                if (angular.isDefined(found)) {
                    edit(found);
                }
                else {
                    edit({ identifier: id });
                }
            }
            else {
                setSelection();
            }
        }

        /**
         * Mise à jour des flags _selected
         * 
         * @param {any} selBox 
         */
        function setSelection(selBox) {
            _.each(mainCtrl.mailboxes, function (box) { delete box._selected; });
            if (selBox) {
                selBox._selected = true;
            }
        }

        /**
         * Réinitialisation du filtre de recherche
         * 
         */
        function reinitFilters() {
            mainCtrl.filters = { libraries: [] };
            search();
        }

        /**
         * Retourne la 1e lettre de la configuration, pour le tri de la liste des résultats
         * 
         * @param {any} conf 
         * @returns 
         */
        function getFirstLetter(conf) {
            return StringTools.getFirstLetter(conf.label, "OTHER");
        }
    }
})();