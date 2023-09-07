(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('CondreportDescEditCtrl', CondreportDescEditCtrl);

    function CondreportDescEditCtrl(
        $location,
        $q,
        $route,
        $routeParams,
        $scope,
        $timeout,
        CondreportDescValueSrvc,
        CondreportPropertyConfSrvc,
        DocUnitBaseService,
        gettext,
        gettextCatalog,
        MessageSrvc,
        ModalSrvc,
        NumaHopInitializationSrvc,
        Principal,
        USER_ROLES,
        ValidationSrvc
    ) {
        var editCtrl = this;
        editCtrl.cancel = cancel;
        editCtrl.displayBoolean = DocUnitBaseService.displayBoolean;
        editCtrl.getTypeLabel = CondreportPropertyConfSrvc.getTypeLabel;
        editCtrl.init = loadValues;
        editCtrl.newValue = newValue;
        editCtrl.onchangeLibrary = onchangeLibrary;
        editCtrl.openForm = openForm;
        editCtrl.propertyChanged = propertyChanged;
        editCtrl.propertyConfChanged = propertyConfChanged;
        editCtrl.removeProperty = removeProperty;
        editCtrl.removeValue = removeValue;
        editCtrl.saveValues = saveValues;
        editCtrl.validation = ValidationSrvc;
        editCtrl.valueChanged = valueChanged;

        editCtrl.options = {
            boolean: DocUnitBaseService.options.booleanObj,
            types: {
                display: 'label',
                placeholder: gettextCatalog.getString("Type de constat d'état"),
                trackby: 'identifier',
                multiple: true,
                'allow-clear': true,
                data: DocUnitBaseService.options.condreportTypes,
            },
        };

        var updatedProperty = false;
        var updatedPropConf;
        var updatedValues = [];
        var deletedValues = [];

        /**
         * Chargement des valeurs de la propriété prop
         *
         * @param {any} prop
         * @returns
         */
        function loadValues(prop) {
            editCtrl.loaded = false;
            editCtrl.admin = Principal.isInRole(USER_ROLES.COND_REPORT_HAB6);

            if (prop) {
                editCtrl.prop = prop;

                editCtrl.isSuperAdmin = Principal.isInRole(USER_ROLES.SUPER_ADMIN);
                editCtrl.administrationLib = Principal.isInRole(USER_ROLES.ADMINISTRATION_LIB);
                editCtrl.library = Principal.library();
                editCtrl.internal = !!prop.fake;

                // Initialisation de la liste des bibliothèques
                var libPromise = NumaHopInitializationSrvc.loadLibraries().then(function (data) {
                    editCtrl.options.libraries = data;

                    // conf par défaut
                    var defaultLib = editCtrl.library ? editCtrl.library : data.length > 0 ? data[0].identifier : null;
                    if (defaultLib) {
                        editCtrl._propLibrary = _.find(editCtrl.options.libraries, function (lib) {
                            return lib.identifier === defaultLib;
                        });
                    }
                    return onchangeLibrary(editCtrl._propLibrary ? editCtrl._propLibrary : null);
                });

                // Initialisation de la liste des valeurs
                var valuePromise;
                // Création
                if (angular.isDefined($routeParams.new)) {
                    editCtrl.values = [];
                    editCtrl.isNew = true;
                    valuePromise = $q.when(editCtrl.values);
                }
                // Mise à jour
                else {
                    editCtrl.values = CondreportDescValueSrvc.query({ property: prop.identifier });
                    editCtrl.isNew = false;
                    valuePromise = editCtrl.values.$promise;
                }

                // Affichage/Ouverture du formalaire
                return $q.all([libPromise, valuePromise]).then(function (values) {
                    editCtrl.loaded = true;

                    if (editCtrl.isNew) {
                        openForm();
                    }
                });
            }
            return $q.when();
        }

        /**
         * Création d'une nouvelle valeur rattachée à la propriété en cours d'édition
         *
         */
        function newValue() {
            if (editCtrl.values) {
                var newValue = new CondreportDescValueSrvc();
                newValue.property = editCtrl.prop;

                editCtrl.values.push(newValue);
                updatedValues.push(newValue);
            }
        }

        /**
         * Suppression de la valeur
         *
         * @param {any} value
         */
        function removeValue(value) {
            removeFromList(value, editCtrl.values);
            removeFromList(value, updatedValues);

            if (value.identifier) {
                deletedValues.push(value);
            }
        }

        /**
         * Suppression d'un élément appartenant à une liste
         *
         * @param {any} value
         * @param {any} list
         */
        function removeFromList(value, list) {
            var idx = list.indexOf(value);
            if (idx >= 0) {
                list.splice(idx, 1);
            }
        }

        /**
         * Suppresion de la propriété en cours d'édition, et de toutes ses valeurs
         *
         */
        function removeProperty() {
            if (editCtrl.prop) {
                ModalSrvc.confirmDeletion(gettextCatalog.getString('la propriété {{label}}, ainsi que toutes ses valeurs', editCtrl.prop)).then(function () {
                    editCtrl.prop.$delete(function () {
                        MessageSrvc.addSuccess(gettext('la propriété {{label}}, ainsi que toutes ses valeurs ont été supprimées'), editCtrl.prop);
                        $location.search({});
                        $route.reload();
                    });
                });
            }
        }

        /**
         * Pointage de la modification de la propriété en cours d'édition
         */
        function propertyChanged() {
            if (!updatedProperty) {
                updatedProperty = true;
            }
        }

        /**
         * Pointage de la modification de la configuration d'une propriété
         */
        function propertyConfChanged() {
            updatedPropConf = editCtrl.propConf;
        }

        /**
         * Pointage des valeurs modifiées pour sauvegarde ultérieure
         *
         * @param {any} value
         */
        function valueChanged(value) {
            if (updatedValues.indexOf(value) < 0) {
                updatedValues.push(value);
            }
        }

        /**
         * Annulation de l'édition en cours
         *
         */
        function cancel() {
            updatedProperty = false;
            updatedPropConf = null;
            updatedValues = [];
            deletedValues = [];

            if (editCtrl.isNew) {
                $location.search({});
                $route.reload();
            } else {
                loadValues(editCtrl.prop).then(function () {
                    $scope.valuesForm.$cancel();
                });
            }
        }

        /**
         * Sauvegarde des modifications apportées dans l'édition en cours
         *
         * @returns
         */
        function saveValues() {
            if (!updatedProperty && !updatedPropConf && !deletedValues.length && !updatedValues.length) {
                return;
            }
            $timeout(function () {
                // Mise à jour de la propriété
                var propPromise;
                if (updatedProperty) {
                    propPromise = editCtrl.prop.$save();
                } else {
                    propPromise = $q.when();
                }

                propPromise
                    .then(function () {
                        var promises = [];
                        // Conf ppté
                        if (updatedPropConf && updatedPropConf.library) {
                            if (!updatedPropConf.internalProperty && !updatedPropConf.descPropertyId) {
                                updatedPropConf.descPropertyId = editCtrl.prop.identifier;
                            }
                            updatedPropConf.types = objToTypes(updatedPropConf._types);
                            promises.push(
                                updatedPropConf.$save().then(function (conf) {
                                    conf._types = typesToObj(conf.types);
                                })
                            );
                        }
                        // Suppresion des valeurs
                        _.each(deletedValues, function (value) {
                            promises.push(value.$delete());
                        });
                        // Mise à jour des valeurs
                        _.each(updatedValues, function (value) {
                            if (!value.property) {
                                value.property = editCtrl.prop;
                            }
                            promises.push(value.$save());
                        });
                        return $q.all(promises);
                    })
                    .then(function () {
                        MessageSrvc.addSuccess(gettext('Les modifications ont été sauvegardées'));
                        updatedProperty = false;
                        updatedPropConf = null;
                        updatedValues = [];
                        deletedValues = [];
                        editCtrl.isNew = false;
                        $location.search({ property: editCtrl.prop.identifier });
                    })
                    .catch(function (response) {
                        openForm();
                    });
            });
        }

        /**
         * Ouverture du formulaire d'édition
         */
        function openForm() {
            $timeout(function () {
                if (angular.isDefined($scope.valuesForm)) {
                    $scope.valuesForm.$show();
                }
            });
        }

        /**
         * Sélection d'une bibliothèque
         *
         * @param {any} newLib
         */
        function onchangeLibrary(newLib) {
            if (newLib && editCtrl.prop.identifier) {
                if (editCtrl.prop.fake) {
                    editCtrl.propConf = CondreportPropertyConfSrvc.get({ internal: editCtrl.prop.identifier, library: newLib.identifier });
                } else {
                    editCtrl.propConf = CondreportPropertyConfSrvc.get({ desc: editCtrl.prop.identifier, library: newLib.identifier });
                }
                return editCtrl.propConf.$promise.then(function (conf) {
                    conf._types = typesToObj(conf.types);
                    return conf;
                });
            } else {
                var defer = $q.defer();
                editCtrl.propConf = null;

                $timeout(function () {
                    editCtrl.propConf = new CondreportPropertyConfSrvc();
                    editCtrl.propConf.required = !!editCtrl.prop.required;
                    editCtrl.propConf.allowComment = !!editCtrl.prop.comment;
                    editCtrl.propConf.showOnCreation = true;
                    editCtrl.propConf.library = newLib;
                    editCtrl.propConf._types = editCtrl.options.types.data; // par défaut, on met tous les types
                    defer.resolve(editCtrl.propConf);
                });
                return defer.promise;
            }
        }

        /**
         * Conversion liste d'enum de type de constats vers les objets utilisés dans la liste de types
         *
         * @param {any} types
         * @returns
         */
        function typesToObj(types) {
            return _.map(types, function (type) {
                return _.find(editCtrl.options.types.data, function (d) {
                    return d.identifier === type;
                });
            });
        }

        /**
         * Conversion liste d'objets utilisés dans la liste de types vers les enum de type de constats
         *
         * @param {any} arr
         */
        function objToTypes(arr) {
            return _.pluck(arr, 'identifier');
        }
    }
})();
