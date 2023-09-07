(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('ModalGetRevisionsCtrl', function ($uibModalInstance, options, AuditSrvc, gettextCatalog) {
        var mainCtrl = this;
        _.extend(mainCtrl, options);

        mainCtrl.close = close;
        mainCtrl._displayRevision = _displayRevision;

        init();

        function init() {
            if (mainCtrl.identifier && mainCtrl.type) {
                mainCtrl.revisions = AuditSrvc.query({ type: mainCtrl.type, id: mainCtrl.identifier });
                mainCtrl.revisions.$promise.then(function (revs) {
                    _.each(revs, function (rev) {
                        rev.date = moment(rev.timestamp).format('L LT');
                    });
                });
            }
        }

        function _displayRevision(rev) {
            if (mainCtrl.displayRevision) {
                return mainCtrl.displayRevision(rev);
            } else {
                return gettextCatalog.getString('Révision #{{rev}} par {{username}} le {{date}}', rev);
            }
        }

        function close() {
            $uibModalInstance.close();
        }
    });
})();
