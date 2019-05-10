(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('DeliveryConfCtrl', DeliveryConfCtrl);

    function DeliveryConfCtrl($routeParams, $location, $scope, $timeout, LibrarySrvc) {

        var mainCtrl = this;
        mainCtrl.edit = edit;

        init();

        function init() {

            var params = {
                search: ""
            };

            var promise = LibrarySrvc.search(params).$promise;

            promise.then(function (libraries) {
                mainCtrl.libraries = libraries.content;
                if(mainCtrl.libraries.length === 1) {
                    select(mainCtrl.libraries[0].identifier);
                }
                refreshTemplate();
            });
        }

        /**
         * Édition d'une liste de valeurs
         * 
         * @param {any} library 
         */
        function edit(library, type) {
            mainCtrl.editedLibrary = library;
            mainCtrl.editedType = type;
            $location.search({ library: library.identifier });
            
            refreshTemplate(type);
        }

        function refreshTemplate(type) {
            return $timeout(function () {
                mainCtrl.configurationInclude = null;
                $scope.$apply();
                if (type === 'DELIVERY') {
                    mainCtrl.configurationInclude = "scripts/app/configuration/deliverySlip/deliveryConfEdit.html";
                } else if (type === 'CONTROL') {
                    mainCtrl.configurationInclude = "scripts/app/configuration/checkSlip/checkSlipConfEdit.html";
                } else if (type === 'CONDREPORT') {
                    mainCtrl.configurationInclude = "scripts/app/configuration/condreportSlip/condReportSlipConfEdit.html";
                }
                
            });
        }

        /**
         * Sélection d'une configuration à partir de son identifiant
         * 
         * @param {any} identifier 
         */
        function select(identifier) {
            if (identifier) {
                var found = _.find(mainCtrl.libraries, function (library) {
                    return library.identifier === identifier;
                });
                if (angular.isDefined(found)) {
                    edit(found);
                }
            }
            else {
                delete mainCtrl.editedLibrary;
            }
        }
    }
})();