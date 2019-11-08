(function () {
    'use strict';

    angular.module('numaHopApp.service')
        .factory('DocUnitBaseService', DocUnitBaseService);

    function DocUnitBaseService(gettextCatalog, DocUnitSrvc) {

        var service = this;

        // Définition des listes déroulantes
        service.options = {
            boolean: {
                "true": gettextCatalog.getString('Oui'),
                "false": gettextCatalog.getString('Non')
            },
            booleanObj: [
                { value: true, text: gettextCatalog.getString('Oui') },
                { value: false, text: gettextCatalog.getString('Non') }
            ],
            condreportTypes: [{
                identifier: "MONO_PAGE",
                label: gettextCatalog.getString("Monofeuillet")
            }, {
                identifier: "MULTI_PAGE",
                label: gettextCatalog.getString("Multifeuillet")
            }],
            rights: {
                "TO_CHECK": gettextCatalog.getString("Droits à vérifier"),
                "FREE": gettextCatalog.getString('Libre de droits'),
                "RESTRICTED": gettextCatalog.getString('Sous droits'),
                "RESTRICTED_WITH_AUTHORIZATION": gettextCatalog.getString('Sous droits avec accord de l\'auteur')
            },
            progressStatuses: {
                "NOT_AVAILABLE": gettextCatalog.getString("Inconnu"),
                "REQUESTED": gettextCatalog.getString("Demandée"),
                "VALIDATED": gettextCatalog.getString("Validée"),
                "REFUSED": gettextCatalog.getString("Refusée")
            }
        };

        function removeProject(docUnit, callback) {
            DocUnitSrvc.removeProject(docUnit, function () {
                callback();
            });
        }
        function removeLot(docUnit, callback) {
            DocUnitSrvc.removeLot(docUnit, function () {
                callback();
            });
        }
        function removeTrain(docUnit, callback) {
            DocUnitSrvc.removeTrain(docUnit, function () {
                callback();
            });
        }

        /**
        * Une unité documentaire peut être retirée d'un projet si:
        * * elle n'est pas attachée à un lot
        * * le statut du projet est Créé (ie. le workflow n'est pas démarré)
        */
        function canRemoveProject(docUnit) {
            return !docUnit.lot
                && docUnit.project
                && _.contains(["CREATED"], docUnit.project.status);
        }

        /**
        * Une unité documentaire peut être retirée d'un lot si:
        * * le statut du lot est Créé (ie. le workflow n'est pas démarré)
        */
        function canRemoveLot(docUnit) {
            return docUnit.lot && _.contains(["CREATED"], docUnit.lot.status);
        }

        /**
        * Une unité documentaire peut être retirée d'un train si:
        * * le statut du train est Créé (ie. le workflow n'est pas démarré)
        */
        function canRemoveTrain(docUnit) {
            return docUnit.train && _.contains(["CREATED"], docUnit.train.status);
        }

        /**
         * Libellé de la valeur booléenne passée en paramètre
         * 
         * @param {any} value 
         * @returns 
         */
        function displayBoolean(value) {
            var found = _.find(service.options.booleanObj, function (b) {
                return b.value === value;
            });
            if (angular.isDefined(found)) {
                return found.text;
            }
        }

        function displayCondreportType(value) {
            var found = _.find(service.options.condreportTypes, function (b) {
                return b.identifier === value;
            });
            if (angular.isDefined(found)) {
                return found.label;
            }
        }

        function displayRight(value) {
            return service.options.rights[value] || value;
        }
        
        function displayProgressStatus(value) {
            return service.options.progressStatuses[value] || value;
        }

        service.canRemoveProject = canRemoveProject;
        service.canRemoveLot = canRemoveLot;
        service.canRemoveTrain = canRemoveTrain;
        service.displayBoolean = displayBoolean;
        service.displayCondreportType = displayCondreportType;
        service.displayRight = displayRight;
        service.removeProject = removeProject;
        service.removeLot = removeLot;
        service.removeTrain = removeTrain;
        service.displayProgressStatus = displayProgressStatus;

        return service;
    }
})();
