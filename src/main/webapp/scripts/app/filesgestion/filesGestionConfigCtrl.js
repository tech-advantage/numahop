(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('FilesGestionConfigCtrl', FilesGestionConfigCtrl);

    function FilesGestionConfigCtrl($scope, $timeout, $q, gettext, gettextCatalog, MessageSrvc, NumaHopInitializationSrvc) {
        $scope.loaded = false;
        $scope.selectedLibrary = undefined;
        $scope.configurationInclude = 'scripts/app/filesgestion/filesGestionConfigEdit.html';

        /**
         * Liste des options pour les listes déroulantes
         */
        $scope.options = {
            libraries: [],
            typeDeclench: {
                PROJET: gettextCatalog.getString('Clôture du projet'),
                LOT: gettextCatalog.getString('Clôture du lot'),
            },
        };

        init();

        /** Initialisation */
        function init() {
            loadLibraries();
        }

        $scope.selectLibrary = function (library) {
            $scope.selectedLibrary = library;
            refreshTemplate();
        };

        function refreshTemplate() {
            return $timeout(function () {
                $scope.configurationInclude = null;
                $scope.$apply();
                $scope.configurationInclude = 'scripts/app/filesgestion/filesGestionConfigEdit.html';
            });
        }

        function loadLibraries() {
            $q.all([NumaHopInitializationSrvc.loadLibraries()]).then(function (data) {
                $scope.options.libraries = data[0];
                if ($scope.options.libraries.length === 1) {
                    $scope.selectLibrary($scope.options.libraries[0]);
                }
            });
        }
    }
})();
