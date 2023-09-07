(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('ModalCreateItemInternetArchiveCtrl', ModalCreateItemInternetArchiveCtrl);

    function ModalCreateItemInternetArchiveCtrl($scope, $uibModalInstance, options, gettextCatalog, $location, $timeout, MessageSrvc, DocUnitBaseService, NumahopEditService, ExportInternetArchiveSrvc) {
        var mainCtrl = this;
        mainCtrl.preventDefault = NumahopEditService.preventDefault;
        mainCtrl.docUnit = options.docUnit;
        mainCtrl.close = close;
        mainCtrl.exportIA = exportIA;
        mainCtrl.saveIA = saveIA;
        mainCtrl.addSubject = addSubject;
        mainCtrl.removeSubject = removeSubject;
        mainCtrl.addCollection = addCollection;
        mainCtrl.removeCollection = removeCollection;
        mainCtrl.addHeader = addHeader;
        mainCtrl.removeHeader = removeHeader;
        mainCtrl.addCoverage = addCoverage;
        mainCtrl.removeCoverage = removeCoverage;
        mainCtrl.addContributor = addContributor;
        mainCtrl.removeContributor = removeContributor;
        mainCtrl.addCreator = addCreator;
        mainCtrl.removeCreator = removeCreator;
        mainCtrl.addLanguage = addLanguage;
        mainCtrl.removeLanguage = removeLanguage;

        mainCtrl.options = DocUnitBaseService.options;
        mainCtrl.options.mediatypes = [
            { id: 1, name: 'texts' },
            { id: 2, name: 'image' },
            { id: 3, name: 'collection' },
            { id: 4, name: 'autre' },
        ];

        init();

        /**
         * Récupération des informations pour l'envoi
         */
        function init() {
            /** Préparation **/
            mainCtrl.item = ExportInternetArchiveSrvc.prepare(
                {
                    id: mainCtrl.docUnit,
                },
                function (item) {
                    afterLoadingEntity(item);
                }
            );
        }
        function afterLoadingEntity(value) {
            mainCtrl.item = value;
            if (angular.isUndefined(mainCtrl.item.mediatype)) {
                mainCtrl.item.mediatype = 'texts';
            }
            mainCtrl.item._mediatype = _.find(mainCtrl.options.mediatypes, function (m) {
                return m.name === mainCtrl.item.mediatype;
            });
            if (!mainCtrl.item.subjects) {
                mainCtrl.item.subjects = [];
            }
            $timeout(function () {
                if (angular.isDefined(mainCtrl.itemForm)) {
                    mainCtrl.itemForm.$show();
                }
            });
        }

        /**
         * Gestion des sujets
         */
        function addSubject(subject) {
            if (angular.isDefined(subject)) {
                if (mainCtrl.item.subjects.indexOf(subject) === -1) {
                    mainCtrl.item.subjects.push(subject);
                    mainCtrl.item.newSubject = '';
                }
            }
        }
        function removeSubject(subject) {
            if (angular.isDefined(subject)) {
                var index = mainCtrl.item.subjects.indexOf(subject);
                if (index > -1) {
                    mainCtrl.item.subjects.splice(index, 1);
                }
            }
        }

        /**
         * Gestion des collections
         */
        function addCollection() {
            if (angular.isDefined(mainCtrl.item.newCollection)) {
                if (mainCtrl.item.collections.indexOf(mainCtrl.item.newCollection) === -1) {
                    mainCtrl.item.collections.push(mainCtrl.item.newCollection);
                    mainCtrl.item.newCollection = '';
                }
            }
        }
        function removeCollection(collection) {
            if (angular.isDefined(collection)) {
                var index = mainCtrl.item.collections.indexOf(collection);
                if (index > -1) {
                    mainCtrl.item.collections.splice(index, 1);
                }
            }
        }

        /**
         * Gestion des champs personnalisés
         */
        function addHeader() {
            if (angular.isDefined(mainCtrl.item.newHeaderType) && angular.isDefined(mainCtrl.item.newHeaderValue)) {
                var itemFound = _.find(mainCtrl.item.customHeaders, function (item) {
                    return item.type === mainCtrl.item.newHeaderType;
                });
                if (angular.isDefined(itemFound)) {
                    itemFound.value = mainCtrl.item.newHeaderValue;
                    mainCtrl.item.newHeaderType = '';
                    mainCtrl.item.newHeaderValue = '';
                } else {
                    var item = {};
                    item.type = mainCtrl.item.newHeaderType;
                    item.value = mainCtrl.item.newHeaderValue;
                    mainCtrl.item.customHeaders.push(item);
                    mainCtrl.item.newHeaderType = '';
                    mainCtrl.item.newHeaderValue = '';
                }
            }
        }
        function removeHeader(type) {
            if (angular.isDefined(type)) {
                var index = mainCtrl.item.customHeaders.indexOf(type);
                if (index > -1) {
                    mainCtrl.item.customHeaders.splice(index, 1);
                }
            }
        }

        /**
         * Gestion des coverages
         */
        function addCoverage(coverage) {
            if (angular.isDefined(coverage)) {
                if (mainCtrl.item.coverages.indexOf(coverage) === -1) {
                    mainCtrl.item.coverages.push(coverage);
                    mainCtrl.item.newCoverage = '';
                }
            }
        }
        function removeCoverage(coverage) {
            if (angular.isDefined(coverage)) {
                var index = mainCtrl.item.coverages.indexOf(coverage);
                if (index > -1) {
                    mainCtrl.item.coverages.splice(index, 1);
                }
            }
        }

        /**
         * Gestion des contributors
         */
        function addContributor(contributor) {
            if (angular.isDefined(contributor)) {
                if (mainCtrl.item.contributors.indexOf(contributor) === -1) {
                    mainCtrl.item.contributors.push(contributor);
                    mainCtrl.item.newContributor = '';
                }
            }
        }
        function removeContributor(contributor) {
            if (angular.isDefined(contributor)) {
                var index = mainCtrl.item.contributors.indexOf(contributor);
                if (index > -1) {
                    mainCtrl.item.contributors.splice(index, 1);
                }
            }
        }

        /**
         * Gestion des creators
         */
        function addCreator(creator) {
            if (angular.isDefined(creator)) {
                if (mainCtrl.item.creators.indexOf(creator) === -1) {
                    mainCtrl.item.creators.push(creator);
                    mainCtrl.item.newCreator = '';
                }
            }
        }
        function removeCreator(creator) {
            if (angular.isDefined(creator)) {
                var index = mainCtrl.item.creators.indexOf(creator);
                if (index > -1) {
                    mainCtrl.item.creators.splice(index, 1);
                }
            }
        }

        /**
         * Gestion des languages
         */
        function addLanguage(langauge) {
            if (angular.isDefined(langauge)) {
                if (mainCtrl.item.languages.indexOf(langauge) === -1) {
                    mainCtrl.item.languages.push(langauge);
                    mainCtrl.item.newLangauge = '';
                }
            }
        }
        function removeLanguage(langauge) {
            if (angular.isDefined(langauge)) {
                var index = mainCtrl.item.languages.indexOf(langauge);
                if (index > -1) {
                    mainCtrl.item.languages.splice(index, 1);
                }
            }
        }

        function setMediaTypeOfItemBeforeSaving(item) {
            mainCtrl.item.mediatype = item._mediatype.name;
        }

        /**
         * Fermeture de la fenêtre
         */
        function close() {
            $uibModalInstance.dismiss('cancel');
        }

        /**
         * Lancement de l'export
         */
        function exportIA() {
            close();

            setMediaTypeOfItemBeforeSaving(mainCtrl.item);
            ExportInternetArchiveSrvc.create(
                {
                    id: mainCtrl.docUnit,
                },
                mainCtrl.item,
                function () {
                    MessageSrvc.addSuccess(gettextCatalog.getString("L'unité documentaire {{label}} va être envoyée vers Internet Archive"), { label: mainCtrl.item.identifier });
                }
            );
        }

        /**
         * Sauvegarde des données IA
         */
        function saveIA() {
            close();

            setMediaTypeOfItemBeforeSaving(mainCtrl.item);
            ExportInternetArchiveSrvc.save(
                {
                    id: mainCtrl.docUnit,
                },
                mainCtrl.item,
                function () {
                    MessageSrvc.addSuccess(gettextCatalog.getString("Les données d'export IA pour l'unité documentaire {{label}} ont été sauvegardées"), { label: mainCtrl.item.archiveIdentifier });
                }
            );
        }
    }
})();
