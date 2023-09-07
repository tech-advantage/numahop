(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('ModalConfigureCsvExportCtrl', ModalConfigureCsvExportCtrl);

    function ModalConfigureCsvExportCtrl($uibModalInstance, DocPropertyTypeSrvc, gettextCatalog, options) {
        var mainCtrl = this;
        _.extend(mainCtrl, options);

        mainCtrl.options = options;
        mainCtrl.confirm = confirm;
        mainCtrl.cancel = cancel;
        mainCtrl.checkAll = checkAll;
        mainCtrl.isValid = isValid;
        mainCtrl.selectSeparator = selectSeparator;
        mainCtrl.options = {
            dc: {
                text: 'label',
                placeholder: gettextCatalog.getString('Dublin Core'),
                trackby: 'identifier',
                multiple: true,
                'allow-clear': true,
            },
            dcq: {
                text: 'label',
                placeholder: gettextCatalog.getString('Dublin Core Qualified'),
                trackby: 'identifier',
                multiple: true,
                'allow-clear': true,
            },
            custom: {
                text: 'label',
                placeholder: gettextCatalog.getString('Champs personnalisés'),
                trackby: 'identifier',
                multiple: true,
                'allow-clear': true,
            },
            docUnitFields: {
                text: 'label',
                placeholder: gettextCatalog.getString('Unité documentaire'),
                trackby: 'code',
                multiple: true,
                'allow-clear': true,
                data: [
                    { code: 'pgcnId', label: gettextCatalog.getString('PGCN Id'), rank: 1 },
                    { code: 'label', label: gettextCatalog.getString('Libellé'), rank: 2 },
                    { code: 'type', label: gettextCatalog.getString('Type'), rank: 3 },
                    { code: 'collectionIA', label: gettextCatalog.getString('Collection IA'), rank: 4 },
                    { code: 'rights', label: gettextCatalog.getString('Droits'), rank: 5 },
                    { code: 'planClassementPAC', label: gettextCatalog.getString('Pac'), rank: 6 },
                ],
            },
            bibRecordFields: {
                text: 'label',
                placeholder: gettextCatalog.getString('Notice bibliographique'),
                trackby: 'code',
                multiple: true,
                'allow-clear': true,
                data: [
                    { code: 'title', label: gettextCatalog.getString('Titre de la notice'), rank: 1 },
                    { code: 'sigb', label: gettextCatalog.getString('SIGB'), rank: 2 },
                    { code: 'sudoc', label: gettextCatalog.getString('SUDOC'), rank: 3 },
                    { code: 'calames', label: gettextCatalog.getString('Calames'), rank: 4 },
                    { code: 'docElectronique', label: gettextCatalog.getString('Document électronique'), rank: 5 },
                ],
            },
            physFields: {
                text: 'label',
                placeholder: gettextCatalog.getString('Document physique'),
                trackby: 'code',
                multiple: true,
                'allow-clear': true,
                data: [
                    { code: 'name', label: gettextCatalog.getString('Nom'), rank: 1 },
                    { code: 'totalPage', label: gettextCatalog.getString('Nombre de pages'), rank: 2 },
                    { code: 'digitalId', label: gettextCatalog.getString('Radical'), rank: 3 },
                ],
            },
            encoding: [
                { code: 'ISO-8859-15', label: gettextCatalog.getString('ISO-8859-15 (Excel)') },
                { code: 'UTF-8', label: gettextCatalog.getString('UTF-8') },
            ],
            separator: [
                { code: '\t', label: gettextCatalog.getString('Tabulation') },
                { code: ',', label: gettextCatalog.getString('Virgule') },
                { code: ';', label: gettextCatalog.getString('Point-virgule') },
                { code: ' ', label: gettextCatalog.getString('Espace') },
                { code: '__OTHER__', label: gettextCatalog.getString('Autre: ') },
            ],
        };
        mainCtrl.export = {
            dc: [],
            dcq: [],
            custom: [],
            docfield: [],
            bibfield: [],
            physfield: [],
            separatorOpt: mainCtrl.options.separator[2],
            encoding: 'ISO-8859-15',
        };

        init();

        function init() {
            loadOptions().then(function () {
                mainCtrl.loaded = true;

                // $timeout(function() {
                //     angular.element("sep-2").checked(true);
                // });
            });
        }

        function loadOptions() {
            return DocPropertyTypeSrvc.dto().$promise.then(function (properties) {
                mainCtrl.docProperties = _.chain(properties).sortBy('rank').groupBy('superType').value();
                mainCtrl.options.dc.data = mainCtrl.docProperties.DC || [];
                mainCtrl.options.dcq.data = mainCtrl.docProperties.DCQ || [];
                mainCtrl.options.custom.data = mainCtrl.docProperties.CUSTOM || [];
            });
        }

        function checkAll(field) {
            var options;
            switch (field) {
                case 'dc':
                case 'dcq':
                case 'custom':
                    options = field;
                    break;
                case 'docfield':
                    options = 'docUnitFields';
                    break;
                case 'bibfield':
                    options = 'bibRecordFields';
                    break;
                case 'physfield':
                    options = 'physFields';
                    break;
            }
            mainCtrl.export[field] = mainCtrl.options[options].data;
        }

        function selectSeparator(code) {
            mainCtrl.export.separatorOpt = _.find(mainCtrl.options.separator, function (sep) {
                return sep.code === code;
            });
        }

        function isValid() {
            return (
                mainCtrl.export.dc.length > 0 ||
                mainCtrl.export.dcq.length > 0 ||
                mainCtrl.export.custom.length > 0 ||
                mainCtrl.export.docfield.length > 0 ||
                mainCtrl.export.bibfield.length > 0 ||
                mainCtrl.export.physfield.length > 0
            );
        }

        function confirm() {
            if (!isValid()) {
                mainCtrl.message = gettextCatalog.getString('Veuillez sélectionner les données à exporter');
                return;
            }

            var params = {};
            params.field = _.chain(mainCtrl.export.dc).union(mainCtrl.export.dcq).union(mainCtrl.export.custom).pluck('identifier').value();
            params.docfield = _.pluck(mainCtrl.export.docfield, 'code');
            params.bibfield = _.pluck(mainCtrl.export.bibfield, 'code');
            params.physfield = _.pluck(mainCtrl.export.physfield, 'code');
            params.encoding = mainCtrl.export.encoding;

            if (mainCtrl.export.separatorOpt.code !== '__OTHER__') {
                params.separator = mainCtrl.export.separatorOpt.code;
            } else {
                params.separator = mainCtrl.export.separator;
            }

            $uibModalInstance.close(params);
        }

        function cancel() {
            $uibModalInstance.dismiss();
        }
    }
})();
