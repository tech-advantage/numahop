(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('DocPropertyTypeCtrl', DocPropertyTypeCtrl);

    function DocPropertyTypeCtrl(DocPropertyTypeSrvc, gettext, gettextCatalog, HistorySrvc, MessageSrvc, ModalSrvc) {

        var mainCtrl = this;
        mainCtrl.create = create;
        mainCtrl.delete = deleteType;
        mainCtrl.downType = downType;
        mainCtrl.save = save;
        mainCtrl.upType = upType;

        mainCtrl.superTypes = [{
            code: "DC",
            label: gettextCatalog.getString("Dublin Core"),
            editable: false
        }, {
            code: "DCQ",
            label: gettextCatalog.getString("Dublin Core Qualifié"),
            editable: false
        }, {
            code: "CUSTOM",
            label: gettextCatalog.getString("Personnalisé"),
            editable: true
        }, {
            code: "CUSTOM_CINES",
            label: gettextCatalog.getString("Personnalisé CINES"),
            editable: true
        }, {
            code: "CUSTOM_ARCHIVE",
            label: gettextCatalog.getString("Personnalisé INTERNET ARCHIVE"),
            editable: true
        } , {
            code: "CUSTOM_OMEKA",
            label: gettextCatalog.getString("Personnalisé OMEKA"),
            editable: true
        }];

        init();

        /**
         * Initialisation du contrôleur: chargement de la liste des types personnalisés
         */
        function init() {
            HistorySrvc.add(gettext("Types de propriétés personnalisés"));
            mainCtrl.loaded = false;

            loadTypes();
        }

        /**
         * Chargement des types de propriété
         */
        function loadTypes() {
            DocPropertyTypeSrvc.query().$promise
                .then(function (values) {
                    mainCtrl.types = _.groupBy(values, "superType");
                    mainCtrl.loaded = true;
                });
        }

        /**
         * Création d'un nouveau type personnalisé
         */
        function create(superType) {
            var types = mainCtrl.types[superType.code];
            if (!types) {
                types = [];
            }

            var newType = new DocPropertyTypeSrvc();
            newType.superType = superType.code;
            newType.label = "Nouveau champ " + superType.label;

            var maxRank = _.chain(types).pluck("rank").max().value();
            newType.rank = isFinite(maxRank) ? maxRank + 1 : 1;

            newType.$save().then(function () {
                types.push(newType);
                if (!mainCtrl.types[superType.code]) {
                   loadTypes(); 
                }
                MessageSrvc.addSuccess(gettext("Le type {{label}} a été créé"), newType);
            });
            
            
            
        }

        /**
         * Suppression d'un type personnalisé
         * 
         * @param {any} type 
         * @returns 
         */
        function deleteType(type, superType) {
            var types = mainCtrl.types[superType.code];
            if (!types) {
                return;
            }

            ModalSrvc.confirmDeletion(gettextCatalog.getString("Le type de propriété {{label}}", type))
                .then(function () {
                    return type.$delete();
                })
                .then(function () {
                    MessageSrvc.addSuccess(gettext("Le type {{label}} a été supprimé"), type);

                    var idx = types.indexOf(type);
                    if (idx >= 0) {
                        types.splice(idx, 1);
                    }
                });
        }

        /**
         * Sauvegarde (création / mise à jour) d'un type personnalisé
         * 
         * @param {any} type 
         * @returns 
         */
        function save(type) {
            if (!type) {
                return;
            }

            type.$save()
                .then(function () {
                    MessageSrvc.addSuccess(gettext("Le type {{label}} a été sauvegardé"), type);
                });
            // .catch(function (response) {
            //     if (response.data.errors) {
            //         mainCtrl.errors = response.data.errors;
            //     }
            // })
        }

        /**
         * Déplacement des types
         * 
         * @param {any} type 
         * @param {any} types 
         */
        function downType(type, superType) {
            moveType(type, superType, "down");
        }

        function upType(type, superType) {
            moveType(type, superType, "up");
        }

        function moveType(type, superType, direction) {
            var types = mainCtrl.types[superType.code];
            if (!types) {
                return;
            }

            var rank = type.rank || 0;
            var sortBy = direction === "up" ? function (r) { return -(r.rank || 0); } : "rank";
            var find = direction === "up" ? function (r) { return r.rank < rank; } : function (r) { return r.rank > rank; };
            var next = _.chain(types).sortBy(sortBy).find(find).value();
            
            if (angular.isDefined(next)) {
                type.rank = next.rank;
                next.rank = rank;

                type.$save();
                next.$save();
            }
        }
    }
})();
