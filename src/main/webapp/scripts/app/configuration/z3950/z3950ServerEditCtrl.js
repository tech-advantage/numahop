(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('Z3950ServerEditCtrl',
        function ($location, $q, $routeParams, $scope, $timeout,
            codeSrvc, gettext, gettextCatalog, HistorySrvc, MessageSrvc, ModalSrvc, ListTools, ValidationSrvc, Z3950ServerSrvc, DocUnitBaseService) {

            // Initialisation
            $scope.semCodes = codeSrvc;
            $scope.validation = ValidationSrvc;

            $scope.openForm = openForm;
            $scope.saveZ3950Server = saveZ3950Server;
            $scope.displayBoolean = DocUnitBaseService.displayBoolean;

            $scope.displayDataFormat = displayDataFormat;
            $scope.displayDataEncoding = displayDataEncoding;
            $scope.displayRecordType = displayRecordType;

            $scope.options = {
                boolean: {
                    "true": gettextCatalog.getString('Oui'),
                    "false": gettextCatalog.getString('Non')
                },
                dataFormat: {
                    "INTERMARC": gettextCatalog.getString("Intermarc"),
                    "UNIMARC": gettextCatalog.getString("Unimarc")
                },
                dataEncoding: {
                    "_NULL_": "",
                    "ANSEL": gettextCatalog.getString("ANSEL (MARC-8)"),
                    "ISO_5426": gettextCatalog.getString("ISO-5426"),
                    "ISO_6937": gettextCatalog.getString("ISO-6937"),
                    "ISO_8859_1": gettextCatalog.getString("ISO-8859-1"),
                    "UTF_8": gettextCatalog.getString("UTF-8")
                },
                recordType: {
                    "BIBLIOGRAPHIC": gettextCatalog.getString("Bibliographique"),
                    "AUTHORITY": gettextCatalog.getString("Autorité")
                }
            };

            // Gestion des vues
            $scope.viewModes = {
                VIEW: "view",     // Visualisation, Édition rapide
                EDIT: "edit"
            };   // Création, Modification
            $scope.editModes = {
                CREATE: "create",
                UPDATE: "update"
            };
            $scope.viewMode = $routeParams.mode || $scope.viewModes.VIEW;

            function displayDataFormat(dataFormat) {
                return $scope.options.dataFormat[dataFormat] || dataFormat;
            }
            function displayDataEncoding(dataEncoding) {
                return $scope.options.dataEncoding[dataEncoding] || dataEncoding;
            }
            function displayRecordType(recordType) {
                return $scope.options.recordType[recordType] || recordType;
            }

            // Chargement du serveur

            /** Initialisation */
            delete $scope.editMode;

            // Chargement d'un serveur existant
            if (angular.isDefined($routeParams.id)) {
                Z3950ServerSrvc.get({
                    id: $routeParams.id
                }, function (value, responseHeaders) {
                    $scope.editMode = $scope.editModes.UPDATE;
                    onSuccess(value, responseHeaders);
                    $scope.loaded = true;
                });
            }
            // Création d'un nouveau serveur
            else if (angular.isDefined($routeParams.new)) {
                HistorySrvc.add(gettextCatalog.getString("Nouveau Serveur Z39.50"));
                $scope.editMode = $scope.editModes.CREATE;
                $scope.z3950Server = new Z3950ServerSrvc();
                $scope.z3950Server.active = true;
                openForm();
                $scope.loaded = true;
            }



            // CRUD
            $scope.onaftersave = function () {
                var deferred = $q.defer();

                $timeout(function () {
                    var savePromise = saveZ3950Server($scope.z3950Server);
                    savePromise.then(function (value) {
                        deferred.resolve(value);
                    }).catch(function (value) {
                        deferred.reject(value);
                    });

                    if ($scope.viewMode === $scope.viewModes.EDIT) {
                        savePromise.then(function (value, responseHeaders) {
                            $scope.setViewMode($scope.viewModes.VIEW);
                        }).catch(function (value) {
                            openForm();
                        });
                    }
                });

                return deferred.promise;
            };
            function saveZ3950Server(z3950Server) {
                delete $scope.errors;
                $timeout(function () {

                z3950Server.$save({},
                    function (value, responseHeaders) {
                        addNewZ3950ServerToList(value, $scope.newServers, $scope.servers);
                        MessageSrvc.addSuccess(gettext("Le serveur {{name}} a été sauvegardé"), { name: value.name });
                        onSuccess(value, responseHeaders);
                        $scope.z3950Server = value;
                        $scope.$parent.updateZ3950Server(value.identifier, value);
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

            $scope.delete = function (z3950Server) {
                ModalSrvc.confirmDeletion(z3950Server.name)
                    .then(function () {

                        z3950Server.$delete(function (value, responseHeaders) {
                            MessageSrvc.addSuccess(gettext("Le serveur {{name}} a été supprimé"), { name: z3950Server.name });
                            ListTools.findAndRemoveItemFromLists(z3950Server, $scope.newServers, $scope.displayedServers,
                                $scope.servers);
                            $scope.backToList();
                        });
                    });
            };

            // Controls
            $scope.cancel = function () {
                $scope.z3950ServerForm.$cancel();
            };

            $scope.backToList = function () {
                $scope.loaded = false;
                // supprimer tous les paramètres
                $location.search({});
                $location.path("/administration/appconfiguration/z3950Server");
            };

            // Gestion du serveur Z39.50 renvoyée par le serveur
            function onSuccess(value, responseHeaders) {
                var z3950Server = "";
                if (value.name) {
                    z3950Server += value.name + " ";
                }
                HistorySrvc.add(gettextCatalog.getString("Serveur: {{server}}", { server: z3950Server.trim() }));

                $scope.z3950Server = value;
            }
            // Ouverture du formulaire et des sous formulaires
            function openForm() {
                $timeout(function () {
                    if (angular.isDefined($scope.z3950ServerForm)) {
                        $scope.z3950ServerForm.$show();
                    }
                });
            }

            function addNewZ3950ServerToList(z3950Server, newServers, servers) {
                var found = _.find(newServers, function (b) {
                    return b.identifier === z3950Server.identifier;
                });
                if (found) {
                    /** Si ce serveur existe déjà, ne rien faire */
                    return;
                }
                found = _.find(servers, function (b) {
                    return b.identifier === z3950Server.identifier;
                });
                if (found) {
                    /** Si ce serveur existe déjà, ne rien faire */
                    return;
                }

                var newZ3950Server = {
                    "_selected": true,
                    "identifier": z3950Server.identifier,
                    "name": z3950Server.name
                };
                var i = 0;
                for (i = 0; i < newServers.length; i++) {
                    var b = newServers[i];
                    if (b.name.localeCompare(z3950Server.name) > 0) {
                        break;
                    }
                }
                newServers.splice(i, 0, newZ3950Server);
            }

            $scope.showAdd = function (index, collection) {
                return index === (collection.length - 1);
            };
        });
})();
