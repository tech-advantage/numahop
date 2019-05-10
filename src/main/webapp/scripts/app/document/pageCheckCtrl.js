(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('PageCheckCtrl', PageCheckCtrl);

    function PageCheckCtrl($routeParams, $location, $scope, DigitalDocumentSrvc, PageCheckSrvc, MessageSrvc) {

        $scope.url_viewer = undefined;
        $scope.typeView = 'ImageView';
        $scope.options = {};
        $scope.options.errors = PageCheckSrvc.checkErrors;
        $scope.select = {};
        $scope.select.selectedErrors = [];
        $scope.data = {};
        $scope.data.digitizingNotes = "";
        $scope.data.checkNotes = "";
        $scope.data.pagesToControl = [];
        $scope.data.totalPages = 0;

        $scope.data.validation = false;
        $scope.loaded = false;
        $scope.complete = false;
        $scope.accDocUnit = false;

        init();

        function init() {

            /* Parametres visionneuse */
            if (angular.isDefined($routeParams.typeView)) {
                $scope.typeView = $routeParams.typeView;
            }

            if (angular.isDefined($routeParams.validation)) {
                $scope.data.validation = $routeParams.validation === "true";
            }
            if (angular.isDefined($routeParams.page)) {
                $scope.currentPage = $routeParams.page;
            } else {
                $scope.currentPage = 0;
            }

            var params = {
                id: $routeParams.id
            };

            DigitalDocumentSrvc.get(params, function (value) {
                $scope.digitalDocument = value;
                loadPage($scope.currentPage, $scope.typeView);
                if ($scope.data.validation) {
                    DigitalDocumentSrvc.getFilesWithErrors(params, function (value) {
                        $scope.data.pagesToControl = value;
                        $scope.data.totalPages = value.length;
                    });
                } else {
                    $scope.data.totalPages = $scope.digitalDocument.nbPages;
                }
            });

        }

        function groupErrorLabels(error) {
            return error.isMajor ? "Erreurs majeures" : "Erreurs mineures";
        }
        $scope.groupErrorLabels = groupErrorLabels;

        function loadPage(pageNumber, typeView) {
            $scope.loaded = false;
            $scope.select.selectedErrors = [];
            var params = {
                id: $routeParams.id,
                pageNumber: pageNumber + 1
            };

            if (!typeView) {
                typeView = 'BookView';
            }
            $scope.url_viewer = "scripts/app/viewer/mirador.html?idDocument=" + params.id + '&typeView=' + typeView + '&page=' + params.pageNumber;

            DigitalDocumentSrvc.getPage(params, function (page) {
                $scope.data.checkNotes = page.checkNotes;
                PageCheckSrvc.getErrorsForPage(params, function (errors) {
                    $scope.select.selectedErrors = _.filter($scope.options.errors, function (error) {
                        return _.contains(errors, error.key);
                    });
                    $scope.loaded = true;
                    // Affichage pour un temps limité à l'ouverture
                    MessageSrvc.initPanel();
                });
            });
        }

        function nextPage() {
            var params = {
                id: $routeParams.id,
                pageNumber: $scope.currentPage + 1
            };
            var body = {
                failedChecks: _.pluck($scope.select.selectedErrors, "key"),
                checkNotes: $scope.data.checkNotes
            };
            PageCheckSrvc.setErrorsForPage(params, body, function (value) {
                if (value.minorErrorRateExceeded) {
                    MessageSrvc.addWarn("Taux d'erreur dépassé par rapport à la taille de l'échantillon considéré");
                }
                if (isLastPage()) {
                    $scope.complete = true;
                } else {
                    $scope.currentPage++;
                    $location.search("page", $scope.currentPage);
                    loadPage($scope.currentPage);
                }
            });
        }
        $scope.nextPage = nextPage;

        function previousPage() {
            $scope.currentPage--;
            $location.search("page", $scope.currentPage);
            loadPage($scope.currentPage);
        }
        $scope.previousPage = previousPage;

        function isFirstPage() {
            return $scope.currentPage === 0;
        }
        $scope.isFirstPage = isFirstPage;

        function isLastPage() {
            return $scope.digitalDocument && $scope.currentPage + 1 === $scope.data.totalPages;
        }
        $scope.isLastPage = isLastPage;

        function reject() {
            var params = {
                id: $routeParams.id
            };
            DigitalDocumentSrvc.reject(params, {}, function () {
                $location.path("/checks/checks");
                $location.search("");
            });
        }
        $scope.reject = reject;

        function accept() {
            var params = {
                id: $routeParams.id
            };
            DigitalDocumentSrvc.accept(params, {}, function () {
                $location.path("/checks/checks");
                $location.search("");
            });
        }
        $scope.accept = accept;

        function done() {
            $location.path("/document/digital");
        }
        $scope.done = done;

        function back() {
            $location.path("/document/digital");
        }
        $scope.back = back;

    }
})();
