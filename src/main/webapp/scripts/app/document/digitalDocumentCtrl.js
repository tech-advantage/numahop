(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('DigitalDocumentCtrl', DigitalDocumentCtrl);

    function DigitalDocumentCtrl($routeParams, $location, $scope, DigitalDocumentSrvc, NumahopUrlService, MessageSrvc, PageCheckSrvc, gettextCatalog, codeSrvc) {
        $scope.url_viewer = undefined;
        $scope.getThumbnailForDocument = getThumbnailForDocument;
        $scope.getThumbnail = getThumbnail;
        $scope.thumbnails = [];
        $scope.filenames = [];
        $scope.selectImageForModal = selectImageForModal;
        $scope.options = {};
        $scope.rejected = false;
        $scope.accDocUnit = false;

        $scope.digitalDocument = undefined;

        $scope.select = {};
        $scope.select.selectedErrors = [];
        $scope.options.errors = PageCheckSrvc.checkErrors;
        $scope.data = {};
        $scope.data.validation = false;
        $scope.data.filesWithErrors = [];

        init();

        function init() {
            if (angular.isDefined($routeParams.validation)) {
                $scope.data.validation = $routeParams.validation === 'true';
            }
            var params = {
                id: $routeParams.id,
                validation: $scope.data.validation,
            };

            DigitalDocumentSrvc.get(params, function (value) {
                $scope.digitalDocument = value;
                if (value.status === 'REJECTED') {
                    MessageSrvc.addFailure(gettextCatalog.getString('Statut du document : {{status}}', { status: codeSrvc['digitalDocument.' + value.status] }), {}, true);
                    $scope.rejected = true;
                } else {
                    MessageSrvc.addInfo(gettextCatalog.getString('Statut du document : {{status}}', { status: codeSrvc['digitalDocument.' + value.status] }), {}, true);
                }

                DigitalDocumentSrvc.getFilesWithErrors(params, function (value) {
                    $scope.data.filesWithErrors = value;
                });
                // Affichage pour un temps limité à l'ouverture
                MessageSrvc.initPanel();
            });
            DigitalDocumentSrvc.getFilenames(params, function (filenames) {
                $scope.filenames = filenames;
            });
            PageCheckSrvc.getErrors(params, function (errors) {
                $scope.select.selectedErrors = _.filter($scope.options.errors, function (error) {
                    return _.contains(errors, error.key);
                });
            });
            $scope.checksLink = NumahopUrlService.getUrlForTypeAndParameters('checks', params);

            /* parametres visionneuse */
            var typeView = 'ThumbnailsView';
            var _page = 0;
            if (angular.isDefined($routeParams.page)) {
                _page = $routeParams.page;
            }
            if (angular.isDefined($routeParams.typeView)) {
                typeView = $routeParams.typeView;
            }
            $scope.url_viewer = 'scripts/app/viewer/mirador.html?idDocument=' + params.id + '&typeView=' + typeView + '&page=' + _page;
        }

        function goToPageCheck(page) {
            saveErrors();

            var params;
            if (angular.isDefined(page)) {
                params = {
                    id: $routeParams.id,
                    typeView: 'BookView',
                    page: page,
                };
            } else {
                params = {
                    id: $routeParams.id,
                    typeView: 'BookView',
                    page: 0,
                };
            }
            $location.path('/document/checks').search(params);
        }
        $scope.goToPageCheck = goToPageCheck;

        function selectImageForModal(number) {
            $scope.selectedImage = getView(number);
        }

        function getThumbnail(pageNumber) {
            var id = $routeParams.id;
            return getThumbnailForDocument(id, pageNumber);
        }

        function getView(pageNumber) {
            var id = $routeParams.id;
            var url = '/api/rest/digitaldocument/' + id + '?view=true&pageNumber=' + (pageNumber + 1);
            return url;
        }

        function getThumbnailForDocument(id, pageNumber) {
            var url = '/api/rest/digitaldocument/' + id + '?thumbnail=true&pageNumber=' + pageNumber;
            return url;
        }

        function getClassesForPage(index) {
            if (_.contains($scope.data.filesWithErrors, index + 1)) {
                return 'error-thumbnail';
            }
        }
        $scope.getClassesForPage = getClassesForPage;

        function reject() {
            var params = {
                id: $routeParams.id,
            };
            DigitalDocumentSrvc.reject(params, _.pluck($scope.select.selectedErrors, 'key'), function () {
                $location.path('/dashboard');
                $location.search('');
                MessageSrvc.addSuccess(gettext('Document numérique rejeté'));
            });
        }
        $scope.reject = reject;

        function accept() {
            var params = {
                id: $routeParams.id,
            };
            DigitalDocumentSrvc.accept(params, {}, function () {
                $location.path('/dashboard');
                $location.search('');
                MessageSrvc.addSuccess(gettextCatalog.getString('Document numérique validé'));
            });
        }
        $scope.accept = accept;

        function saveErrors() {
            var params = {
                id: $routeParams.id,
            };
            var body = {
                failedChecks: _.pluck($scope.select.selectedErrors, 'key'),
                checkNotes: $scope.data.checkNotes,
            };
            PageCheckSrvc.setErrors(params, body, function (value) {
                if (value.minorErrorRateExceeded) {
                    MessageSrvc.addWarn(gettextCatalog.getString("Taux d'erreur dépassé par rapport à la taille de l'échantillon considéré"));
                }
            });
        }
    }
})();
