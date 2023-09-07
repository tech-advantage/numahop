(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('SemFieldCtrl', SemFieldCtrl);

    function SemFieldCtrl($scope, $parse, $q, ErreurSrvc, MessageSrvc) {
        $scope.beforesaveWrapper = beforesaveWrapper;
        $scope.onchangeWrapper = onchangeWrapper;
        $scope.getText = getText;
        $scope.filterData = filterData;
        $scope.refreshData = refreshData;

        init();

        /** Initialisation de la directive */
        function init() {
            if (angular.isDefined($scope.label)) {
                $scope.defaultcolsm = 'col-xs-8';
            } else {
                $scope.defaultcolsm = 'col-xs-12';
            }
            // enregistrement du beforesave
            if (angular.isArray($scope.register)) {
                $scope.register.push($scope.beforesaveWrapper);
            }

            // erreurs du serveur
            $scope.$watch('errors', function (value) {
                var errors = $parse(value)();

                if (angular.isArray(errors) && errors.length > 0) {
                    $scope.errMsg = _.map(errors, function (err) {
                        if (angular.isUndefined(err.message) || err.message === null) {
                            err.message = ErreurSrvc.getMessage(err.code);
                        }
                        return err.message || err.code;
                    }).join('<br/>');
                } else {
                    delete $scope.errMsg;
                }
            });
        }
        /** Wrapper pour le onbeforesave (qui devra être appelé manuellement à la validation du formulaire) */
        function beforesaveWrapper() {
            MessageSrvc.clearMessages(MessageSrvc.level.ERROR);

            // custom onbeforesave
            var validation = $scope.onbeforesave({
                value: $scope.model,
            });
            $q.when(validation).then(function (message) {
                $scope.errMsg = message || false;
            });
            return validation;
        }
        /** Wrapper pour le onchange */
        function onchangeWrapper(value) {
            // mise à jour du model manuelle pour le uiselect
            if ($scope.type === 'uiselect') {
                $scope.model = value;
            }
            // custom onchange
            return $scope.onchange({
                value: value,
            });
        }
        /** Affichage d'un élément de la liste (UiSelect) */
        function getText(d) {
            if (!d) {
                return '';
            }
            var optionData = $scope.optionData;

            if (angular.isString(optionData.text)) {
                return d[optionData.text];
            } else if (angular.isFunction(optionData.text)) {
                return optionData.text(d);
            } else {
                return d;
            }
        }
        /* chargement async */
        function refreshData($select) {
            var refresh = $scope.optionData.refresh($select);
            if (!refresh) {
                return;
            }
            var promise = angular.isDefined(refresh.$promise) ? refresh.$promise : refresh;

            return promise.then(function (result) {
                var values = angular.isArray(result) ? result : angular.isArray(result.content) ? result.content : [];
                $scope.optionData.data = values;
            });
        }

        /* Filtrage de la liste de valeurs */
        function filterData(value, index, array) {
            return angular.isDefined($scope.optionData.filter) ? $scope.optionData.filter(value, index, array) : true;
        }
    }
})();
