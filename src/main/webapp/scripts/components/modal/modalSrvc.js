(function () {
    'use strict';

    angular.module('numaHopApp.service').factory('ModalSrvc', function ($uibModal) {
        var open = function (modalTemplate, options, size, ctrl, backdrop, modalClass) {
            if (angular.isUndefined(ctrl)) {
                ctrl = 'ModalCtrl';
            }
            if (angular.isUndefined(backdrop)) {
                backdrop = true;
            }
            var modalInstance = $uibModal.open({
                templateUrl: modalTemplate,
                controller: ctrl,
                controllerAs: 'mainCtrl',
                size: size,
                animation: true,
                backdrop: backdrop,
                windowClass: modalClass,
                resolve: {
                    options: function () {
                        return options;
                    },
                },
            });

            return modalInstance.result;
        };

        var selectExportTypes = function () {
            return open('scripts/components/modal/common/selectExportTypes.html', null, 'sm', 'ModalSelectExportTypesCtrl', null);
        };

        var configureCsvExport = function (options, size) {
            if (!size) {
                size = 'md';
            }
            return open('scripts/components/modal/common/configureCsvExport.html', options, size, 'ModalConfigureCsvExportCtrl', null);
        };

        var confirmDeletion = function (label, size, precision) {
            var options = {
                label: label,
                precision: precision,
            };
            return open('scripts/components/modal/common/deleteWarning.html', options, size);
        };

        var confirmAdd = function (label, size) {
            var options = {
                label: label,
            };
            return open('scripts/components/modal/common/addWarning.html', options, size);
        };

        var displayData = function (options, size) {
            if (!size) {
                size = 'md';
            }
            return open('scripts/components/modal/common/displayData.html', options, size);
        };

        var integrateToProject = function (docs, size, proj, lot, train) {
            if (!size) {
                size = 'sm';
            }
            var options = {
                docs: docs,
            };
            if (proj) {
                options.proj = proj;
            }
            if (lot) {
                options.lot = lot;
            }
            if (train) {
                options.train = train;
            }
            return open('scripts/components/modal/common/integrateToProject.html', options, size, 'ModalIntegrateToProjectCtrl', null);
        };

        var integrateToTrain = function (docs, proj, size) {
            if (!size) {
                size = 'sm';
            }
            var options = {
                docs: docs,
                proj: proj,
            };
            return open('scripts/components/modal/common/integrateToTrain.html', options, size, 'ModalIntegrateToTrainCtrl', null);
        };

        var selectBibRecord = function (options, size) {
            if (!size) {
                size = 'lg';
            }
            if (!options) {
                options = {};
            }
            return open('scripts/app/document/modals/selectBibRecord.html', options, size, 'ModalSelectBibRecordCtrl', null);
        };

        var selectDocUnit = function (options, size) {
            if (!size) {
                size = 'lg';
            }
            if (!options) {
                options = {};
            }
            return open('scripts/app/document/modals/selectDocUnit.html', options, size, 'ModalSelectDocUnitCtrl', null);
        };

        var selectFile = function (options, size) {
            if (!size) {
                size = 'md';
            }
            if (!options) {
                options = {};
            }
            return open('scripts/components/modal/common/selectFile.html', options, size, 'ModalSelectFileCtrl', null);
        };

        var exportCines = function (version, identifier, ead, planClassmt, size) {
            if (!size) {
                size = 'lg';
            }
            var options = {
                version: version,
                identifier: identifier,
                ead: ead,
                planClassmt: planClassmt,
            };
            return open('scripts/app/document/modals/exportCines.html', options, size, 'ModalExportCinesCtrl', null);
        };

        var createItemInternetArchive = function (identifier, size) {
            if (!size) {
                size = 'lg';
            }
            var options = {
                docUnit: identifier,
            };
            return open('scripts/app/document/modals/createItemInternetArchive.html', options, size, 'ModalCreateItemInternetArchiveCtrl', null);
        };

        var createProject = function () {
            return open('scripts/components/modal/common/createProject.html', {}, 'md', 'ModalCreateProjectCtrl', null);
        };

        var confirmCancelWithComment = function (label, minCommentLength, size) {
            var options = {
                label: label,
                minCommentLength: minCommentLength || 0,
            };
            return open('scripts/components/modal/common/cancelWithComment.html', options, size);
        };

        var confirmAction = function (label, size) {
            var options = {
                label: label,
            };
            return open('scripts/components/modal/common/confirmAction.html', options, size);
        };
        var confirmModSub = function (label, size) {
            var options = {
                label: label,
            };
            return open('scripts/components/modal/common/confirmModSub.html', options, size);
        };

        var confirmCancel = function (size) {
            var options = {};
            return open('scripts/components/modal/common/confirmCancel.html', options, size);
        };

        var confirmNotFound = function (date) {
            var options = { date: date };
            return open('scripts/components/modal/common/confirmNotFound.html', options, 'md');
        };

        var newOpeningDate = function (date, size) {
            var options = {
                newOpeningDate: {
                    startDate: date,
                },
            };
            return open('scripts/components/modal/common/newOpeningDate.html', options, size, 'calendarModalCtrl', 'static');
        };

        var newClosingDate = function (date, size) {
            var options = {
                newClosingDate: {
                    startDate: date,
                },
            };
            return open('scripts/components/modal/common/newClosingDate.html', options, size, 'calendarModalCtrl');
        };

        var confirmModification = function (label, size) {
            var options = {
                label: label,
            };
            return open('scripts/components/modal/common/currentSessionWarning.html', options, size);
        };

        var getValueTextarea = function (title, label, value, size) {
            var options = {
                label: label,
                title: title,
                value: value,
            };
            return open('scripts/components/modal/common/getValueTextarea.html', options, size);
        };

        var getRevisions = function (type, identifier, title, displayRevision) {
            var options = {
                identifier: identifier,
                displayRevision: displayRevision,
                title: title,
                type: type,
            };
            return open('scripts/components/modal/common/getRevisions.html', options, 'md', 'ModalGetRevisionsCtrl');
        };

        var modalDeleteDocUnitResults = function (entities, size) {
            var options = {
                entities: entities,
            };
            return open('scripts/app/document/modals/deleteDocUnitResults.html', options, size, 'modalDeleteDocUnitResultsCtrl');
        };

        var modalUnlinkDocUnitResults = function (entities, size) {
            var options = {
                entities: entities,
            };
            return open('scripts/app/document/modals/unlinkDocUnitResults.html', options, size, 'modalUnlinkDocUnitResultsCtrl');
        };

        var modalUpdateDocUnitResults = function (entities, size) {
            var options = {
                entities: entities,
            };
            return open('scripts/app/document/modals/updateDocUnitResults.html', options, size, 'modalUpdateDocUnitResultsCtrl');
        };

        var editModelState = function (state, groups) {
            var options = {
                state: state,
                groups: groups,
            };
            return open('scripts/app/workflow/modals/editModelState.html', options, 'lg', 'ModalEditModelStateCtrl', null);
        };

        var updateRecords = function () {
            var options = {};
            return open('scripts/app/document/modals/updateRecords.html', options, 'lg', 'ModalUpdateRecordsCtrl');
        };

        var checkTextOcr = function (page, txtValue) {
            var options = {
                page: page,
                txtValue: txtValue,
            };
            return open('scripts/components/modal/common/checkTextOcr.html', options, 'md', 'ModalCheckTextOcrCtrl', null, 'modal_ocr');
        };

        var confirmCheckTerminated = function (nbMinErr, nbMajErr, minErrRateExceeded, majErrRateExceeded) {
            var options = {
                nbMinErr: nbMinErr,
                nbMajErr: nbMajErr,
                minErrRateExceeded: minErrRateExceeded,
                majErrRateExceeded: majErrRateExceeded,
            };
            return open('scripts/components/modal/common/confirmCheckTerminated.html', options, 'md', 'ModalConfirmCheckTerminatedCtrl', null);
        };

        var confirmAcceptReject = function (title, label1, label2, labelAction, size) {
            var options = {
                title: title,
                label1: label1,
                label2: label2,
                labelAction: labelAction,
            };
            return open('scripts/components/modal/common/definitiveRejectWarning.html', options, size);
        };

        return {
            createProject: createProject,
            configureCsvExport: configureCsvExport,
            confirmModSub: confirmModSub,
            confirmAction: confirmAction,
            confirmCancelWithComment: confirmCancelWithComment,
            confirmCancel: confirmCancel,
            confirmDeletion: confirmDeletion,
            confirmAdd: confirmAdd,
            confirmModification: confirmModification,
            confirmNotFound: confirmNotFound,
            displayData: displayData,
            editModelState: editModelState,
            getValueTextarea: getValueTextarea,
            getRevisions: getRevisions,
            open: open,
            newOpeningDate: newOpeningDate,
            newClosingDate: newClosingDate,
            createItemInternetArchive: createItemInternetArchive,
            exportCines: exportCines,
            modalDeleteDocUnitResults: modalDeleteDocUnitResults,
            modalUpdateDocUnitResults: modalUpdateDocUnitResults,
            modalUnlinkDocUnitResults: modalUnlinkDocUnitResults,
            integrateToProject: integrateToProject,
            integrateToTrain: integrateToTrain,
            selectBibRecord: selectBibRecord,
            selectDocUnit: selectDocUnit,
            selectFile: selectFile,
            selectExportTypes: selectExportTypes,
            updateRecords: updateRecords,
            checkTextOcr: checkTextOcr,
            confirmCheckTerminated: confirmCheckTerminated,
            confirmAcceptReject: confirmAcceptReject,
        };
    });
})();
