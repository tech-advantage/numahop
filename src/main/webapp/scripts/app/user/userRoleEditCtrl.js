(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('UserRoleEditCtrl', UserRoleEditCtrl);

    function UserRoleEditCtrl($filter, $location, $q, $routeParams, $scope, $timeout, UserAuthorizationSrvc,
        UserRoleSrvc, gettext, gettextCatalog, HistorySrvc, codeSrvc,
        ListTools, MessageSrvc, ModalSrvc, ValidationSrvc) {
        $scope.validation = ValidationSrvc;
        $scope.code = codeSrvc;

        // Gestion des vues

        $scope.filterBy = {};
        $scope.orderBy = ["module", "code"];

        // toggle switch label ON/OFF
        $scope.onLabel = gettextCatalog.getString("Activé");
        $scope.offLabel = gettextCatalog.getString("Désactivé");
        $scope.openForm = openForm;
        $scope.saveRole = saveRole;

        init();


        /** Initialisation */
        function init() {
            $scope.loaded = false;
            var deferRoles = $q.defer();

            // Filtre
            if (angular.isDefined($routeParams.module)) {
                $scope.filterBy.module = $routeParams.module;
            }

            // Chargement du rôle
            if (angular.isDefined($routeParams.id)) {
                $scope.role = UserRoleSrvc.get({
                    id: $routeParams.id

                }, function (value, responseHeaders) {
                    onSuccess(value, responseHeaders);
                    $scope.loaded = true;
                    deferRoles.resolve(value);

                }, function (httpResponse) {
                    deferRoles.reject(httpResponse);
                });
            }
            // Création d'un nouveau rôle
            else if (angular.isDefined($routeParams.new)) {
                HistorySrvc.add(gettextCatalog.getString("Nouveau profil"));

                $scope.role = new UserRoleSrvc();
                $scope.role.authorizations = [];
                openForm();
                deferRoles.resolve($scope.role);
                $scope.loaded = true;
            }

            // Chargement des habilitations
            $scope.authorizations = UserAuthorizationSrvc.dto(function (values) {
                // champs supplémentaires pour l'affichage
                _(values).each(function (value) {
                    value._activated = false;
                });
                // extraction des modules
                $scope.modules = _.chain(values)
                    .uniq(false, "module")
                    .map(function (element) {
                        return { code: element.module, label: codeSrvc[element.module] };
                    }).value();
            });

            // Initialisation des habilitations
            $q.all([deferRoles.promise, $scope.authorizations.$promise])
                .then(function () {
                    // Activation / désactivation des autorisations
                    _.each($scope.role.authorizations, function (roleAuth) {
                        var matchingAuth =
                            _.find($scope.authorizations, function (auth) {
                                return auth.identifier === roleAuth.identifier;
                            });
                        if (angular.isDefined(matchingAuth)) {
                            matchingAuth._activated = true;
                        }
                    });

                    if (!$scope.roleForm.$visible) {
                        $scope.displayedAuthorizations = $filter("filter")($scope.authorizations, { _activated: true });

                        // Autorisations en lecture seule
                        _.each($scope.authorizations, function (auth) {
                            auth._readOnly = isReadOnly(auth);
                        });
                    }
                });
        }

        function displayMessages(entity) {
            // On commence par vider les messages précédents...
            MessageSrvc.clearMessages();
            // ... puis on affiche les infos de modification ...
            if (angular.isDefined(entity.lastModifiedDate)) {
                var dateModif = new Date(entity.lastModifiedDate);
                MessageSrvc.addInfo(gettext("Dernière modification le {{date}} par {{author}}"),
                    { date: dateModif.toLocaleString(), author: entity.lastModifiedBy }, true);
            }
            // ... puis on affiche les infos de création ...
            if (angular.isDefined(entity.createdDate)) {
                var dateCreated = new Date(entity.createdDate);
                MessageSrvc.addInfo(gettext("Créé le {{date}}"),
                    { date: dateCreated.toLocaleString() }, true);
            }
            // Affichage pour un temps limité à l'ouverture
            MessageSrvc.initPanel();
        }

        $scope.updateAuthorizations = function () {
            $scope.displayedAuthorizations = $filter("filter")($scope.authorizations, { _activated: true });
            _.each($scope.authorizations, function (auth) {
                auth._readOnly = isReadOnly(auth);
            });
        };

        // CRUD
        function saveRole(role) {
            delete $scope.errors;
            $timeout(function () {

                role.authorizations = _.filter($scope.authorizations, function (a) { return a._activated; });

                role.$save({},
                    function (value, responseHeaders) {
                        MessageSrvc.addSuccess(gettext("Le profil [{{code}}] {{label}} a été sauvegardé"), value);
                        addNewRoleToList(value, $scope.newRoles, $scope.displayedRoles);
                        onSuccess(value, responseHeaders);
                        $scope.updateRole(value.identifier, value);
                        $scope.updateAuthorizations();
                        $location.search({ id: value.identifier }); // suppression des paramètres
                    },
                    function (response) {
                        $scope.errors = _.chain(response.data.errors)
                            .groupBy("field")
                            .mapObject(function (list) {
                                return _.pluck(list, "code");
                            })
                            .value();

                        openForm();
                    });
            });
        }
        $scope.delete = function (role) {
            ModalSrvc.confirmDeletion(gettextCatalog.getString("le profil {{label}}", role))
                .then(function () {

                    role.$delete(function (value, responseHeaders) {
                        MessageSrvc.addSuccess(gettext("Le profil [{{code}}] {{label}} a été supprimé"), role);
                        ListTools.findAndRemoveItemFromLists(role, $scope.newRoles, $scope.displayedRoles, $scope.roles);
                        $scope.backToList();
                    });
                });
        };

        // Controls
        $scope.cancel = function () {
            $scope.roleForm.$cancel();
        };
        $scope.backToList = function () {
            $scope.loaded = false;

            var params = {};
            if ($scope.filterBy.module) {
                params.module = $scope.filterBy.module;
            }
            $location.path("/user/role").search(params);
        };

        // Gestion du rôle renvoyée par le serveur
        function onSuccess(value, responseHeaders) {
            var role = "";
            if (value.code) {role += value.code + " / ";}
            if (value.label) {role += value.label + " ";}
            HistorySrvc.add(gettextCatalog.getString("Profil: {{role}}", { role: role.trim() }));
            displayMessages($scope.role);
        }
        // Ouverture du formulaire et des sous formulaires
        $scope.openForm = openForm;
        function openForm() {
            $timeout(function () {
                if (angular.isDefined($scope.roleForm)) {
                    $scope.roleForm.$show();
                }
            });
        }

        function addNewRoleToList(role, newRoles, roles) {
            var found = _.find(newRoles, function (p) {
                return p.identifier === role.identifier;
            });
            if (found) {
                /** Si cette catégorie existe déjà, ne rien faire */
                return;
            }
            found = _.find(roles, function (p) {
                return p.identifier === role.identifier;
            });
            if (found) {
                /** Si cette catégorie existe déjà, ne rien faire */
                return;
            }

            var newRole = {
                _selected: true,
                identifier: role.identifier,
                label: role.label
            };
            var i = 0;
            for (i = 0; i < newRoles.length; i++) {
                var p = newRoles[i];
                if (p.label.localeCompare(role.label) > 0) {
                    break;
                }
            }
            newRoles.splice(i, 0, newRole);
        }
        function isReadOnly(auth) {
            // le droit est activé: tous les droits dépendants doivent être désactivés
            if (auth._activated) {
                return auth.dependencies.length > 0
                    && _.chain(auth.dependencies)
                        .map(function (dep) {
                            return _.find($scope.authorizations, function (oth) {
                                return oth.code === dep;
                            });
                        })
                        .filter(angular.isDefined)
                        .some(function (dep) {
                            return dep._activated;
                        })
                        .value();
            }
            // le droit est désactivé: tous les droits requis doivent être activés
            else {
                return auth.requirements.length > 0
                    && _.chain(auth.requirements)
                        .map(function (req) {
                            return _.find($scope.authorizations, function (oth) {
                                return oth.code === req;
                            });
                        })
                        .filter(angular.isDefined)
                        .some(function (req) {
                            return !req._activated;
                        })
                        .value();
            }
        }
        $scope.updateAuth = function (auth) {
            // Mise à jour des champs lecture seule des prérequis et des dépendances
            _.chain()
                .union(auth.dependencies, auth.requirements)
                .map(function (lnk) {
                    return _.find($scope.authorizations, function (oth) {
                        return oth.code === lnk;
                    });
                })
                .filter(angular.isDefined)
                .each(function (lnk) {
                    lnk._readOnly = isReadOnly(lnk);
                });
        };
    }
})();
