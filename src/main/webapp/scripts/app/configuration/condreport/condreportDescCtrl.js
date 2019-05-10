(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('CondreportDescCtrl', CondreportDescCtrl);

    function CondreportDescCtrl($routeParams, $location, $scope, $timeout, CondreportDescPropertySrvc, CondreportPropertyConfSrvc) {

        var mainCtrl = this;
        mainCtrl.create = create;
        mainCtrl.edit = edit;


        init();

        function init() {
            var promise = CondreportDescPropertySrvc.queryAll();
            mainCtrl.types = CondreportPropertyConfSrvc.types;

            promise.then(function (props) {
                mainCtrl.props = props;

                if (angular.isDefined($routeParams.property)) {
                    select($routeParams.property);
                }
                refreshTemplate();
            });
        }

        /**
         * Création d'une nouvelle propriété
         * 
         */
        function create(type) {
            mainCtrl.editedProp = new CondreportDescPropertySrvc();
            mainCtrl.editedProp.type = type.code;
            mainCtrl.props.push(mainCtrl.editedProp);
            $location.search({ new: true });
            refreshTemplate();
        }

        /**
         * Édition d'une liste de valeurs
         * 
         * @param {any} prop 
         */
        function edit(prop) {
            mainCtrl.editedProp = prop;
            $location.search({ property: prop.identifier });
            refreshTemplate();
        }

        function refreshTemplate() {
            return $timeout(function () {
                mainCtrl.configurationInclude = null;
                $scope.$apply();
                mainCtrl.configurationInclude = "scripts/app/configuration/condreport/condreportDescEdit.html";
            });
        }

        /**
         * Sélection d'une configuration à partir de son identifiant
         * 
         * @param {any} identifier 
         */
        function select(identifier) {
            if (identifier) {
                var found = _.find(mainCtrl.props, function (prop) {
                    return prop.identifier === identifier;
                });
                if (angular.isDefined(found)) {
                    edit(found);
                }
            }
            else {
                delete mainCtrl.editedProp;
            }
        }
    }
})();