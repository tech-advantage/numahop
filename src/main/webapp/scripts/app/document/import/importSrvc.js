(function () {
    'use strict';

    angular.module('numaHopApp.service').factory('ImportSrvc', ImportSrvc);
    angular.module('numaHopApp.service').factory('ImportedDocUnitSrvc', ImportedDocUnitSrvc);
    angular.module('numaHopApp.service').factory('ImportReportSrvc', ImportReportSrvc);

    /** Service gérant l'import d'unités documentaires */
    function ImportSrvc(CONFIGURATION, $resource) {
        var service = $resource(
            CONFIGURATION.numahop.url + 'api/rest/import',
            {},
            {
                importReport: {
                    method: 'POST',
                    isArray: false,
                    params: {
                        import: true,
                    },
                },
            }
        );
        // derniers paramètres d'import saisis
        var importParams = {};

        function clearParams() {
            importParams = {};
        }
        function getParam(param, defaultValue) {
            return angular.isDefined(importParams[param]) ? importParams[param] : defaultValue;
        }
        function setParam(param, value) {
            importParams[param] = value;
        }

        service.clearParams = clearParams;
        service.getParam = getParam;
        service.setParam = setParam;
        return service;
    }

    /** Service gérant les unités documentaires importées */
    function ImportedDocUnitSrvc(CONFIGURATION, $resource) {
        return $resource(CONFIGURATION.numahop.url + 'api/rest/impdocunit/:id', { id: '@identifier' }, {});
    }

    /** Service gérant les rapports d'exécution des imports */
    function ImportReportSrvc(CONFIGURATION, $resource) {
        var service = $resource(
            CONFIGURATION.numahop.url + 'api/rest/importreport/:id',
            { id: '@identifier' },
            {
                getStatus: {
                    method: 'GET',
                    isArray: false,
                    params: {
                        status: true,
                    },
                },
                search: {
                    method: 'GET',
                    isArray: false,
                    params: {
                        search: true,
                        size: 50,
                    },
                },
            }
        );

        /** Formattage d'un rapport d'exécution pour affichage sous forme d'une chaine de caractères */
        service.toString = function (report, dtFmt) {
            var str;

            if (angular.isDefined(report)) {
                if (report.start) {
                    var dt = moment(report.start).format(dtFmt || 'LT');
                    str = '[' + dt + '] <b>' + report.files[0].originalFilename + ' - ' + (report.runByFullname || report.runBy) + '</b>';
                    if (report.nbImp) {
                        str += '&nbsp;<span class="badge badge-default btn-sem2">' + report.nbImp + '</span>';
                    }
                    return str;
                } else if (report.identifier) {
                    str = '<b>' + report.files[0].originalFilename + ' - ' + (report.runByFullname || report.runBy) + '</b>';
                    if (report.nbImp) {
                        str += '&nbsp;<span class="badge badge-default btn-sem2">' + report.nbImp + '</span>';
                    }
                    return str;
                } else {
                    return '';
                }
            }
            return '';
        };
        return service;
    }
})();
