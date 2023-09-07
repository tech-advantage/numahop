(function () {
    'use strict';

    angular.module('numaHopApp.service').factory('ExportHandlerSrvc', function ($q, FileSaver, gettext, ExportFTPConfigurationSrvc, DocUnitSrvc, MessageSrvc, DateUtils, ModalSrvc, $httpParamSerializer, $http) {
        const DOCUNIT_URL = 'api/rest/docunit?';

        var _exportFTPPropertyTypes = {
            exportMaster: 'MASTER',
            exportView: 'VIEW',
            exportMets: 'METS',
            exportAipSip: 'AIP',
            exportPdf: 'PDF',
            exportThumb: 'THUMBNAIL',
            exportAlto: 'ALTO',
        };
        var service = {
            massExport: massExport,
        };

        /**
         * Déclenche un export manuel
         *
         * exportConfig: {
         *     projectId: string // identifiant du projet
         *     pgcnId: string // identifiant pgcnId (pour la génération du nom de fichier)
         *     types: string[] // types d'exports demandés
         *     ftpExport: true si export via ftp
         * }
         *
         * @param docUnitId l'identifiant de l'unité documentaire
         * @param exportConfig objet de configuration pour le traitement de la requête
         * @returns {*} la promesse résultant de la requête au service d'export
         */
        function massExport(docUnitId, exportConfig) {
            if (!docUnitId) throw Error('Mass export impossible : identifiant de l unité de documentation non définie');

            if (!exportConfig) throw Error('Mass export impossible : configuration manquante');

            var params = buildParams(docUnitId, exportConfig.types);
            var deferred = $q.defer();

            var reqPromise;
            if (params) {
                reqPromise = $q.when(params);
            } else {
                // Si les exports n'ont pas pu être trouvés, on passe par la configuration d'export ftp associée au projet
                reqPromise = ExportFTPConfigurationSrvc.findByProject({ project: exportConfig.projectId, fullConfig: true }).$promise.then(function (config) {
                    return mapFTPConfigurationToExportParams(docUnitId, config);
                });
            }

            reqPromise
                .then(function (exportParams) {
                    //FIXME Use a service instead of $http directly in controller
                    $http
                        .get(DOCUNIT_URL + $httpParamSerializer(exportParams), { responseType: 'arraybuffer' })
                        .then(function (response) {
                            var headers = response.headers;
                            saveExport(response, headers('content-type'), exportParams.export_ftp, exportConfig.pgcnId);
                            deferred.resolve();
                        })
                        .catch(function (err) {
                            MessageSrvc.addWarn(gettext("L'export FTP est terminé mais une ou plusieurs unités documentaires sont en erreurs."));
                            return deferred.reject(err);
                        });
                })
                .catch(function (err) {
                    return deferred.reject(err);
                });

            return deferred.promise;
        }

        /* Privates */

        /**
         * Construction des paramètres pour la requête de l'export
         *
         * @param docUnitId
         * @param types
         * @returns {{types: ([]|*[]|*), docs}|null}
         */
        function buildParams(docUnitId, types) {
            if (!types) {
                return null;
            }
            var ftpExportValue = types.ftpExport === 'true';
            var params = {
                docs: docUnitId,
                types: types.exportTypes,
            };
            if (ftpExportValue) {
                // export ftp
                params.export_ftp = true;
            } else {
                // telechargement local
                params.export = true;
            }
            return params;
        }

        /**
         * Convertit les valeurs reçues de la configuration en paramètres de la requête au service d'export
         *
         * config: {
         *     identifier: string,
         *     active: true,
         *     address: string,
         *     exportAipSip: boolean,
         *     exportMaster: boolean,
         *     exportMets: boolean,
         *     exportPdf: boolean,
         *     exportThumb: boolean,
         *     exportView: boolean,
         *     label: string,
         *     library: Library,
         *     login: string,
         *     password: string,
         *     port: number,
         *     storageServer: string,
         *     version: string
         * }
         *
         * Mode d'export associés:
         * 'MASTER'
         * 'METS'
         * 'AIP'
         * 'VIEW'
         * 'PDF'
         * 'THUMBNAIL'
         *
         * @param configs
         * @returns {{types: ([]|*[]|*), docs}|null|{docs}}
         */
        function mapFTPConfigurationToExportParams(docUnitId, configs) {
            if (!configs || !configs.length || !configs[0]) {
                return { docs: docUnitId };
            }
            // On utilise la première configuration retournée
            var config = configs[0];

            var exportTypes = Object.keys(_exportFTPPropertyTypes)
                .filter(function (key) {
                    return config.hasOwnProperty(key) && config[key];
                })
                .map(function (key) {
                    return _exportFTPPropertyTypes[key];
                });

            var exports = {
                exportTypes: exportTypes,
            };
            // Activation du ftp en fonction du statut de la configuration
            if (!config.active) {
                exports.ftpExport = 'true';
            }
            return buildParams(docUnitId, exports);
        }

        /**
         * Sauvegarde du fichier d'export
         *
         * @param response la réponse de la requête de l'export
         * @param contentType le type de contenu de la réponse
         * @param ftpExport mode ftp
         * @param pgcnId identifiant pgcn
         */
        function saveExport(response, contentType, ftpExport, pgcnId) {
            if (ftpExport) {
                MessageSrvc.addSuccess(gettext('[' + (pgcnId || '') + "] L'export FTP est en cours"));
            } else {
                const PGCN_NAME = pgcnId ? '_' + pgcnId : '';
                var filename = DateUtils.getFormattedDateYearMonthDayHourMinSec(new Date()) + PGCN_NAME + '_export.zip';
                var blob = new Blob([response.data], { type: contentType });
                FileSaver.saveAs(blob, filename);
            }
        }

        return service;
    });
})();
