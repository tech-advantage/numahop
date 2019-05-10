(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('UserEditCtrl', UserEditCtrl);

    function UserEditCtrl($http, $location, $q, $routeParams, $scope, $timeout, codeSrvc, CONFIGURATION,
        DocUnitBaseService, gettext, gettextCatalog, HistorySrvc, ListTools, MessageSrvc, ModalSrvc,
        NumahopEditService, NumaHopInitializationSrvc, Principal, UserSrvc, ValidationSrvc) {

        $scope.backToList = backToList;
        $scope.cancel = cancel;
        $scope.deleteSignature = deleteSignature;
        $scope.setSignature = setSignature;
        $scope.changePassword = changePassword;
        $scope.delete = deleteUser;
        $scope.displayBoolean = DocUnitBaseService.displayBoolean;
        $scope.duplicate = duplicate;
        $scope.getCategoryLabel = getCategoryLabel;
        $scope.onCategoryChanged = onCategoryChanged;
        $scope.saveUser = saveUser;
        $scope.semCodes = codeSrvc;
        $scope.validation = ValidationSrvc;

        // Définition des listes déroulantes
        $scope.options = {
            boolean: DocUnitBaseService.options.booleanObj,
            categories: [{
                code: "PROVIDER",
                label: gettextCatalog.getString('Prestataire')
            }, {
                code: "OTHER",
                label: gettextCatalog.getString('Utilisateur')
            }],
            libraries: [],
            roles: []
        };

        $scope.accordions = {
            groups: false
        };

        init();


        /** Initialisation */
        function init() {
            loadUser();
            loadRoleSelect();
            loadLibrarySelect();
        }

        function loadRoleSelect() {
            NumaHopInitializationSrvc.loadRoles()
                .then(function (roles) {
                    $scope.options.roles = roles;
                });
        }

        function loadLibrarySelect() {
            return NumaHopInitializationSrvc.loadLibraries()
                .then(function (libs) {
                    $scope.options.libraries = libs;
                });
        }

        /****************************************************************/
        /** Services ****************************************************/
        /****************************************************************/
        function getCategoryLabel(category) {
            var catg = _.find($scope.options.categories, function (c) {
                return c.code === category;
            });
            return catg ? catg.label : category;
        }

        /****************************************************************/
        /** Mot de passe ************************************************/
        /****************************************************************/
        function changePassword(user) {
            UserSrvc.changePassword({ "id": user.identifier }, {},
                function (value) {
                    MessageSrvc.addInfo(gettext("Le mot de passe de l'utilisateur {{user}} est {{password}}"),
                        { user: $scope.user.login, password: value.password },
                        true);
                });
        }

        /****************************************************************/
        /** Actions *****************************************************/
        /****************************************************************/
        function deleteUser(user) {
            ModalSrvc.confirmDeletion(getDisplayName(user))
                .then(function () {
                    user.$delete(function (value) {
                        MessageSrvc.addSuccess(gettext("L'utilisateur {{fullname}} a été supprimé"), { fullname: getDisplayName(value) });
                        var removed = ListTools.findAndRemoveItemFromList(user, $scope.pagination.items);
                        if (removed) {
                            $scope.pagination.totalItems--;
                        }
                        else {
                            ListTools.findAndRemoveItemFromList(user, $scope.newUsers);
                        }
                        $scope.backToList();
                    });
                });
        }

        function duplicate() {
            if ($scope.user) {
                $scope.user._selected = false;
                var identifier = $scope.user.identifier;
                $scope.user = null;
                delete $scope._user_category;
                $location.path("/user/user").search({ id: identifier, mode: "edit", duplicate: true });
            }
        }

        function backToList() {
            // supprimer tous les paramètres
            $location.path("/user/user").search({});
        }

        function cancel() {
            if ($routeParams.new || $routeParams.duplicate) {
                backToList();
            }
            else {
                $scope.userForm.$cancel();
            }
        }

        /****************************************************************/
        /** Fonctions ***************************************************/
        /****************************************************************/
        // Retourne le nom à afficher pour un usager
        function getDisplayName(user) {
            var name = '';
            if (angular.isDefined(user.firstname)) {
                name += user.firstname;
            }
            if (angular.isDefined(user.surname)) {
                if (name.length > 0) {
                    name += ' ';
                }
                name += user.surname;
            }
            return name;
        }

        // Sauvegarde un usager
        function saveUser() {
            delete $scope.errors;

            $timeout(function () {
                var creation = !$scope.user.identifier;

                $scope.user.$save({},
                    function (value) {
                        MessageSrvc.addSuccess(gettext("L'utilisateur {{fullname}} a été sauvegardé"), { fullname: getDisplayName(value) });
                        onSuccess(value);

                        // si création, on ajoute à la liste, sinon, on essaye de MAJ les infos dans la colonne du milieu
                        if (creation) {
                            $scope.clearSelection();
                            NumahopEditService.addNewEntityToList(value, $scope.newUsers, $scope.pagination.items, ["surname", "firstname"]);
                            $location.search({ id: value.identifier }); // suppression des paramètres
                        } else {
                            NumahopEditService.updateMiddleColumn($scope.user, $scope.pagination.items, $scope.newUsers);
                        }
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

        // Gestion de l'usager renvoyé par le serveur
        function onSuccess(value) {
            $scope._user_category = value.category;

            HistorySrvc.add(gettextCatalog.getString("Utilisateur {{user}}", { user: getDisplayName($scope.user) }));
            displayMessages($scope.user);
        }

        // Ouverture du formulaire et des sous formulaires
        function openForm() {
            $timeout(function () {
                if (angular.isDefined($scope.userForm)) {
                    $scope.userForm.$show();
                }
            });
        }

        function displayMessages(entity) {
            // On commence par vider les messages précédents...
            MessageSrvc.clearMessages();
            // ... puis on affiche les infos de modification ...
            if (angular.isDefined(entity.lastModifiedDate)) {
                var dateModif = new Date(entity.lastModifiedDate);
                MessageSrvc.addInfo("Dernière modification le {{date}} par {{author}}",
                    { date: dateModif.toLocaleString(), author: entity.lastModifiedBy }, true);
            }
            // ... puis on affiche les infos de création ...
            if (angular.isDefined(entity.createdDate)) {
                var dateCreated = new Date(entity.createdDate);
                MessageSrvc.addInfo("Créé le {{date}}",
                    { date: dateCreated.toLocaleString() }, true);
            }
            // Affichage pour un temps limité à l'ouverture
            MessageSrvc.initPanel();
        }

        function afterLoadingUser(user) {
            onSuccess(user);

            $scope.formRO = !($scope.isAuthorized($scope.userRoles.USER_HAB2)
                || ($scope.isAuthorized($scope.userRoles.USER_HAB6) && user.identifier === Principal.identifier()));

            if ($scope.formRO && user.identifier === Principal.identifier()) {
                MessageSrvc.addWarn('Pour éditer vos informations, faites appel à un administrateur.');
            }
            $scope.loaded = true;

            $scope.hasSignature = {};
            if ($routeParams.id) {
                UserSrvc.hasSignature({}, user).$promise
                    .then(function (result) {
                        $scope.hasSignature = result[user.identifier];
                        refreshThumbnail();
                    });
            }
        }

        function loadUser() {
            $scope.hasSignature = false;
            $scope.loaded = false;

            if ('duplicate' in $routeParams && angular.isDefined($routeParams.id)) {
                // Duplication
                $scope.user = UserSrvc.duplicate({
                    id: $routeParams.id
                }, function (user) {
                    afterLoadingUser(user);
                    openForm();
                });
            } else if (angular.isDefined($routeParams.id)) {
                // Chargement usager
                $scope.user = UserSrvc.get({
                    id: $routeParams.id
                }, function (user) {
                    afterLoadingUser(user);
                });
            } else if (angular.isDefined($routeParams.new)) {
                // Création d'un nouvel usager
                HistorySrvc.add(gettext("Nouvel utilisateur"));
                $scope.user = new UserSrvc();
                $scope.user.active = true;
                $scope.user.category = "OTHER";
                afterLoadingUser($scope.user);
                openForm();
            }
        }

        function onCategoryChanged(category) {
            $scope._user_category = category;
        }

        /**
         * Sélection du signature de l'utilisateur
         */
        function setSignature(element) {
            if (element.files.length > 0) {
                $scope.$apply(function (scope) {
                    var signature = element.files[0];
                    var url = CONFIGURATION.numahop.url + 'api/rest/user/' + $scope.user.identifier;

                    var formData = new FormData();
                    formData.append("signature", true);
                    formData.append("file", signature);

                    var config = {
                        transformRequest: angular.identity,
                        headers: {
                            'Content-Type': undefined
                        }
                    };
                    $http.post(url, formData, config)
                        .success(function (data, status) {
                            MessageSrvc.addSuccess(gettext("Le signature a été mise à jour"));
                            $scope.hasSignature = true;
                            refreshThumbnail();
                        })
                        .error(function (data, status) {
                            MessageSrvc.addError(gettext("Échec lors du téléversement du signature"));
                        });
                });
            }
        }

        /**
         * Suppression du signature
         * 
         * @param {any} user 
         */
        function deleteSignature(user) {
            UserSrvc.deleteSignature({ id: user.identifier }).$promise
                .then(function () {
                    MessageSrvc.addSuccess(gettext("Le signature a été supprimée"));
                    $scope.hasSignature = false;
                    refreshThumbnail();
                })
                .catch(function () {
                    MessageSrvc.addError(gettext("Échec lors de la suppression du signature"));
                });

        }

        /**
         * Rafraichissement de l'aperçu du signature
         */
        function refreshThumbnail() {
            $scope._user_signature = "/api/rest/user/" + $scope.user.identifier + "?thumbnail=true&ts=" + new Date().getTime();
        }
    }
})();
