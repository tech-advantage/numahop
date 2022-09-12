(function () {
    'use strict';

    angular.module('numaHopApp.service')
        .factory('NumahopEditService', NumahopEditService);

    function NumahopEditService(StringTools, VIEW_MODES) {

        var service = {};

        service.preventDefault = preventDefault;
        service.getFirstLetter = getFirstLetter;
        service.insertBasedOnRank = insertBasedOnRank;
        service.addNewEntityToList = addNewEntityToList;
        service.updateMiddleColumn = updateMiddleColumn;
        service.showFilterLibraries = showFilterLibraries;

        /** Empêcher la sauvegarde du formulaire lors d'un clic sur entrée */
        function preventDefault(event, viewMode) {
            if (viewMode === VIEW_MODES.EDIT) {
                event.preventDefault();
            }
        }

        /**
         * Gestion de la liste des nouvelles entités
         * Ajout des entités juste créées
         *
         * Liste de champs utilisés successivement pour la comparaison
         * EX : ["title", "id"] => comparaison par title et ssi identiques
         * alors comparaison par id
         *
         */
        function addNewEntityToList(entity, newEntities, entities, compareFields) {
            var found = _.find(newEntities, function (b) {
                return b.identifier === entity.identifier;
            });
            if (found) {
                /** Si cette entité existe déjà, ne rien faire */
                return;
            }
            found = _.find(entities, function (b) {
                return b.identifier === entity.identifier;
            });
            if (found) {
                /** Si cette entité existe déjà, ne rien faire */
                return;
            }

            /** Nouvelle entité **/
            var newEntity = {
                _selected: true
            };
            for (var k in entity) {
                if (entity.hasOwnProperty(k)) {
                    newEntity[k] = entity[k];
                }
            }
            var done = false;
            var isPlaced = false;
            for (var i = 0; i < newEntities.length; i++) {
                var entityFromList = newEntities[i];
                /** Comparaison sur tous les éléments à la suite tant qu'on a pas réussi à déterminer **/
                for (var j = 0; j < compareFields.length; j++) {
                    /** valeur de comparaison utilisée **/
                    var compareResult = entityFromList[compareFields[j]].localeCompare(entity[compareFields[j]]);
                    /** On a trouvé l'élément qui est après : on arrête le traitement **/
                    if (compareResult > 0) {
                        done = true;
                        break;
                    } else if (compareResult === 0) {
                        /** Dans ce cas, il faut utiliser l'élément suivant de détermination s'il est présent **/
                        /** On utilise quand même une variable en cas d'absence d'élément de détermination suivant **/
                        isPlaced = true;
                    } else if (compareResult < 0) {
                        /** L'élément est avant : on doit chercher plus loin **/
                        break;
                    }
                }
                /** On a trouvé la place (ou au moins du mieux qu'on pouvait), on arrête la recherche **/
                if (done || isPlaced) {
                    break;
                }
            }
            newEntities.splice(i, 0, newEntity);
        }

        /**
         * Mise à jour de la colonne du milieu au besoin (changement du label, etc..)
         *
         */
        function updateMiddleColumn(entity, items, newEntities) {
            if (entity.identifier) {
                _.union(items, newEntities).forEach(function (elt) {
                    if (elt.identifier === entity.identifier) {
                        for (var k in entity) {
                            if (entity.hasOwnProperty(k)) {
                                elt[k] = entity[k];
                            }
                        }
                        return;
                    }
                });
            }
        }

        /**
         * Récupère la première lettre du champ passé en paramètre, OTHER sinon
         */
        function getFirstLetter(entityField) {
            return StringTools.getFirstLetter(entityField, "OTHER");
        }

        /**
         * Gestion de la liste des propriétés, ajout en fonction du paramètre de comparaison
         */
        function insertBasedOnRank(propertyList, property, param) {
            var i = 0;
            if (angular.isUndefined(param)) {
                param = "rank";
            }
            for (i = 0; i < propertyList.length; i++) {
                var b = propertyList[i];
                /** valeur de comparaison utilisée **/
                if (b[param] > property[param]) {
                    break;
                }
            }
            propertyList.splice(i, 0, property);
        }

        /**
         * Retourne vrai si le filtre doit être affiché
         */
        function showFilterLibraries(libraries) {
            return libraries && libraries.length > 1;
        }

        return service;
    }
})();
