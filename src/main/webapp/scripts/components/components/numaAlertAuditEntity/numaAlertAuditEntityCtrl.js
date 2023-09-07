(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('NumaAlertAuditEntityCtrl', NumaAlertAuditEntityCtrl);

    function NumaAlertAuditEntityCtrl(codeSrvc, AuditSrvc) {
        var ctrl = this;
        ctrl.getStatusLabel = getStatusLabel;
        ctrl.showState = true;

        init();

        function init() {
            var params = {
                type: ctrl.type,
            };
            _.chain(ctrl.filter)
                .mapObject(function (value, key) {
                    switch (key) {
                        case 'from':
                            return moment()
                                .add(-(value || 0), 'day')
                                .format('YYYY-MM-DD');
                        case 'library':
                        case 'lot':
                        case 'project':
                            return angular.isArray(value) ? _.pluck(value, 'identifier') : [];
                        case 'state':
                        case 'status':
                            if (angular.isArray(value)) {
                                ctrl.showState = value.length !== 1;
                                return _.pluck(value, 'identifier');
                            } else {
                                return [];
                            }
                        case '__jsogObjectId':
                        case 'alreadyCheckedForErrors':
                            return;
                        default:
                            return value;
                    }
                })
                .each(function (value, key) {
                    params[key] = value;
                });

            // service exterieur au composant
            var providerResponse = ctrl.provider({ params: params });
            // sinon service d'audit
            if (angular.isUndefined(providerResponse)) {
                providerResponse = AuditSrvc.query(params).$promise;
            }
            // résultat
            providerResponse.then(function (result) {
                ctrl.revisions = result;
                ctrl.setCount({ value: ctrl.revisions.length });
            });
        }

        function getStatusLabel(status) {
            return codeSrvc[ctrl.prefix + '.' + status] || status;
        }
    }
})();
