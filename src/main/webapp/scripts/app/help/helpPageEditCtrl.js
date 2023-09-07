(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('HelpPageEditCtrl', HelpPageEditCtrl);

    function HelpPageEditCtrl($routeParams, $scope, $sce, $timeout, HelpPageSrvc, ModalSrvc, MessageSrvc, gettext) {
        $scope.loaded = false;
        $scope.save = save;
        $scope.remove = remove;
        $scope.cancel = cancel;
        $scope.setViewMode = setViewMode;
        $scope.explicitlyTrustedContentHtml = undefined;
        $scope.viewModes = {
            VIEW: 'view', // Visualisation, Édition rapide
            EDIT: 'edit',
        }; // Création, Modification
        $scope.viewMode = $routeParams.mode || $scope.viewModes.VIEW;
        $scope.helpPageTypes = [
            {
                code: undefined,
                label: '',
            },
        ].concat($scope.filtersAvailable.helpPageTypes);
        $scope.helpPages = [
            {
                title: '',
                identifier: undefined,
            },
        ].concat($scope.helpPages);

        loadHelpPage();

        function loadHelpPage() {
            if (angular.isDefined($routeParams.id)) {
                HelpPageSrvc.get(
                    {
                        id: $routeParams.id,
                    },
                    function (helpPage) {
                        $scope.loaded = true;
                        initPage(helpPage);
                    }
                );
            } else if ($scope.viewMode === $scope.viewModes.EDIT) {
                $scope.loaded = true;
                initPage(new HelpPageSrvc());
            } else {
                $scope.helpPage = undefined;
                $scope.loaded = false;
            }
        }

        function setViewMode(mode) {
            $scope.viewMode = mode;
            if ($scope.viewMode === $scope.viewModes.EDIT) {
                destroySummerNote();
                initSummerNote();
            }
        }

        function initPage(helpPage) {
            $scope.helpPage = helpPage;
            if ($scope.viewMode === $scope.viewModes.EDIT) {
                destroySummerNote();
                initSummerNote();
            } else {
                $scope.explicitlyTrustedContentHtml = $sce.trustAsHtml(helpPage.content);
            }
            // Affichage pour un temps limité à l'ouverture
            MessageSrvc.initPanel($scope);
        }

        /**
         * Initialisation de l'éditeur
         */
        function initSummerNote() {
            $timeout(function () {
                $('#summernote').summernote({
                    lang: 'fr-FR',
                    toolbar: [
                        ['style', ['style']],
                        ['font', ['bold', 'italic', 'underline', 'clear']],
                        ['fontname', ['fontname']],
                        ['fontsize', ['fontsize']],
                        ['color', ['color']],
                        ['para', ['ul', 'ol', 'paragraph']],
                        ['height', ['height']],
                        ['table', ['table']],
                        ['insert', ['link', 'picture', 'hr']],
                        [
                            'view',
                            [
                                /* 'fullscreen' , 'codeview' */
                            ],
                        ], // remove fullScreen and codeview button
                        ['help', ['help']],
                    ],
                    dialogsInBody: true,
                    height: '400px',
                    onInit: function () {
                        $('.note-editing-area').css('min-height', '300px');
                    },
                });
                if (angular.isDefined($scope.helpPage.content)) {
                    $('#summernote').summernote('code', $scope.helpPage.content);
                } else {
                    $('#summernote').summernote('code', '');
                }
            });
        }

        function destroySummerNote() {
            $('#summernote').summernote('destroy');
        }
        function save() {
            // Récupération du contenu de l'éditeur
            $scope.helpPage.content = $('#summernote').summernote('code');
            if (angular.isUndefined($scope.helpPage.type) || $scope.helpPage.type === null) {
                $scope.helpPage.type = 'CUSTOM';
            }
            if (angular.isDefined($scope.helpPage.parent) && $scope.helpPage.parent !== null) {
                if (angular.isDefined($scope.helpPage.parent.identifier)) {
                    $scope.helpPage.module = $scope.helpPage.parent.module;
                    $scope.helpPage.type = $scope.helpPage.parent.type;
                    $scope.helpPage.parent.parent = undefined;
                } else {
                    $scope.helpPage.parent = undefined;
                }
            }
            $scope.helpPage.$save().then(function (page) {
                setViewMode($scope.viewModes.VIEW);
                $scope.initFilters();
                $scope.doFilter();
                initPage(page);
            });
        }
        function cancel() {
            setViewMode($scope.viewModes.VIEW);
            $scope.doFilter();
        }
        function remove(helpPage) {
            ModalSrvc.confirmDeletion(helpPage.title).then(function () {
                helpPage.$delete(function (value, responseHeaders) {
                    MessageSrvc.addSuccess(gettext("La page d'aide a été supprimée"));
                    setViewMode($scope.viewModes.VIEW);
                    $scope.helpPage = undefined;
                    $scope.initFilters();
                    $scope.doFilter();
                    $scope.loaded = false;
                });
            });
        }
    }
})();
