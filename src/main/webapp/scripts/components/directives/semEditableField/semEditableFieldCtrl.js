(function () {
    "use strict";

    angular.module('numaHopApp.controller')
        .controller('SemEditableFieldCtrl', function ($scope, $element, $attrs, $transclude, $parse, ErreurSrvc, gettextCatalog, MessageSrvc) {

            if (angular.isDefined($scope.label)) {
                $scope.defaultcolsm = 'col-xs-8';
            } else {
                $scope.defaultcolsm = 'col-xs-12';
            }

            /** affichage de la valeur du select */
            $scope.displaySelectModel = function () {
                if (angular.isDefined($scope.model) && $scope.model !== null) {
                    var key = angular.isObject($scope.model) ? $scope.model.identifier : String($scope.model);

                    var found = _.find($scope.options, function (option) {
                        return option.identifier === key;
                    });
                    if (found) {
                        if (angular.isDefined($scope.displayProperty)) {
                            return found[$scope.displayProperty];
                        } else {
                            return found.label;
                        }
                    }
                    else {
                        if (angular.isDefined($scope.displayProperty)) {
                            return $scope.model[$scope.displayProperty];
                        } else {
                            return $scope.model.label;
                        }
                    }
                }
                return $scope.defaultDisplay;
            };
            /** affichage de la valeur du select2 */
            $scope.displaySelect2Model = function () {
                if (angular.isDefined($scope.model) && $scope.model !== null) {
                    if (_.isArray($scope.model)) {
                        if ($scope.model.length > 0) {
                            return _($scope.model).map(function (element) {
                                return $scope.options.formatSelection(element);
                            }).join(", ");
                        }
                    } else {
                        return $scope.options.formatSelection($scope.model);
                    }
                }
                return $scope.defaultDisplay;
            };
            /** affichage de la valeur du uiselect */
            $scope.displayUiselectModel = function () {
                if (angular.isDefined($scope.model) && $scope.model !== null) {
                    if (_.isArray($scope.model)) {
                        if ($scope.model.length > 0) {
                            if (angular.isFunction($scope.options.text)) {
                                return _.map($scope.model, $scope.options.text).join(", ");
                            } else {
                                return _.pluck($scope.model, $scope.options.text).join(", ");
                            }
                        }
                    } else {
                        var value = angular.isFunction($scope.options.text) ? $scope.options.text($scope.model) : $scope.model[$scope.options.text];
                        if (value) {
                            return value;
                        }
                    }
                }
                return $scope.defaultDisplay;
            };

            /** Wrapper pour le onbeforesave: garde une copie du modèle avant l'enregistrement */
            $scope.beforesaveWrapper = function ($data) {
                MessageSrvc.clearMessages(MessageSrvc.level.ERROR);

                // copie du modèle avant mise à jour
                $scope.oldModel = angular.copy($scope.model);
                // custom onbeforesave
                return $scope.onbeforesave({
                    $data: $data
                });
            };
            /** Wrapper pour le onaftersave: restaure la copie du modèle en cas d'erreur d'enregistrement sur le serveur */
            $scope.aftersaveWrapper = function () {
                // custom onaftersave
                var promise = $scope.onaftersave();
                // reset model if necessary
                if (angular.isDefined(promise) && angular.isFunction(promise.catch)) {
                    promise.then(function () {
                        delete $scope.oldModel;
                    }).catch(function () {
                        $scope.model = $scope.oldModel;
                    });
                }
                return promise;
            };
            /** Wrapper pour le oncancel */
            $scope.oncancelWrapper = function () {
                MessageSrvc.clearMessages(MessageSrvc.level.ERROR);
                return $scope.onchange({
                    '$data': '$CANCEL_FIELD'
                });
            };
            /** Wrapper pour le onchange */
            $scope.onchangeWrapper = function ($data) {
                // mise à jour du modèle du champ sem-field à partir du modèle interne de xeditable ($data)
                if ($scope.isModeEdition) {
                    this.$editable.save(); // this = scope du champ xeditable
                }

                // custom onchange
                return $scope.onchange({
                    $data: $data
                });
            };

            /** Affichage du champ (ou du bloc) si le champ n'est pas en lecture seule */
            $scope.showEditableForm = function () {
                if ($scope.readonly !== true) {
                    var form = ($scope.refForm || $scope[$attrs.semForm]);

                    if (angular.isDefined(form)) {
                        var promise = $scope.formCancelAll();

                        if (angular.isDefined(promise) && angular.isFunction(promise.catch)) {
                            promise.then(function () {
                                form.$show();
                            });
                        } else {
                            form.$show();
                        }
                    }
                }
            };

            /** Initialisation de la directive */
            var init = function () {
                $scope.defaultDisplay = gettextCatalog.getString("Non renseigné");

                var content = $transclude();
                $scope.transcluding = content.text().trim().length > 0;

                // erreurs du serveur
                $scope.$watch("error", function (value) {
                    var error = $parse(value)();
                    var scope = $element.find("[main-field]").scope();

                    if (angular.isDefined(scope) && angular.isDefined(scope.$editable)) {
                        if (angular.isArray(error) && error.length > 0) {
                            var errMsg = _.map(error, function (err) {
                                if (angular.isUndefined(err.message) || err.message === null) {
                                    err.message = ErreurSrvc.getMessage(err.code);
                                }
                                return err.message || err.code;
                            }).join("<br/>");
                            scope.$editable.setError(errMsg);
                        } else {
                            scope.$editable.setError("");
                        }
                    }
                });
            };

            init();
        });
})();