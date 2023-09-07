(function () {
    'use strict';

    angular.module('numaHopApp').config(function ($routeProvider, gettext, USER_ROLES) {
        $routeProvider
            .when('/document/docunit', {
                templateUrl: 'scripts/app/document/docUnits.html',
                controller: 'DocUnitCtrl',
                title: gettext('Unités documentaires'),
                reloadOnSearch: false,
                access: {
                    authorizedRoles: [USER_ROLES.DOC_UNIT_HAB0],
                },
            })
            .when('/document/docunit_list', {
                templateUrl: 'scripts/app/document/docUnitsList.html',
                controller: 'DocUnitListCtrl',
                controllerAs: 'mainCtrl',
                title: gettext('Gestion des unités documentaires'),
                access: {
                    authorizedRoles: [USER_ROLES.DOC_UNIT_HAB2],
                },
            })
            .when('/document/all_operations/:identifier', {
                templateUrl: 'scripts/app/document/allOperations/all_operations.html',
                controller: 'DocAllOperationsCtrl',
                controllerAs: 'mainCtrl',
                title: gettext("Détail d'une unité documentaire"),
                access: {
                    authorizedRoles: [USER_ROLES.DOC_UNIT_HAB0],
                },
            })
            .when('/document/record', {
                templateUrl: 'scripts/app/document/records.html',
                controller: 'RecordCtrl',
                title: gettext('Notices'),
                reloadOnSearch: false,
                access: {
                    authorizedRoles: [USER_ROLES.DOC_UNIT_HAB0],
                },
            })
            .when('/document/record_list', {
                templateUrl: 'scripts/app/document/recordList.html',
                controller: 'RecordListCtrl',
                controllerAs: 'mainCtrl',
                title: gettext('Gestion des notices'),
                reloadOnSearch: false,
                access: {
                    authorizedRoles: [USER_ROLES.DOC_UNIT_HAB2],
                },
            })
            .when('/document/digital', {
                templateUrl: 'scripts/app/document/digitalDocument.html',
                controller: 'DigitalDocumentCtrl',
                title: gettext('Documents numériques'),
                reloadOnSearch: false,
                access: {
                    authorizedRoles: [USER_ROLES.DOC_UNIT_HAB0],
                },
            })
            .when('/document/checks', {
                templateUrl: 'scripts/app/document/pageCheck.html',
                controller: 'PageCheckCtrl',
                title: gettext('Contrôles visuels'),
                reloadOnSearch: false,
                access: {
                    authorizedRoles: [USER_ROLES.DOC_UNIT_HAB0],
                },
            })
            .when('/document/condreport_list', {
                templateUrl: 'scripts/app/document/condreportList.html',
                controller: 'CondreportListCtrl',
                controllerAs: 'mainCtrl',
                title: gettext("Gestion des constats d'état"),
                access: {
                    authorizedRoles: [USER_ROLES.COND_REPORT_HAB2],
                },
            });
    });
})();
