(function () {
    'use strict';

    angular.module("numaHopApp.controller")
        .controller("ModalConfigureDeliverySlipCtrl", ModalConfigureDeliverySlipCtrl);

    function ModalConfigureDeliverySlipCtrl($uibModalInstance, DocPropertyTypeSrvc, gettextCatalog, options) {

        var mainCtrl = this;
        _.extend(mainCtrl, options);

        mainCtrl.confirm = confirm;
        mainCtrl.cancel = cancel;
        mainCtrl.selectSeparator = selectSeparator;


        mainCtrl.options = {
            pgcnId: true,
            lot: true,
            train: true,
            radical: true,
            title: true,
            pageCount: true,
            date: true,
            encoding: [
                { code: "ISO-8859-15", label: gettextCatalog.getString("ISO-8859-15 (Excel)") },
                { code: "UTF-8", label: gettextCatalog.getString("UTF-8") }
            ],
            separator: [
                { code: "\t", label: gettextCatalog.getString("Tabulation") },
                { code: ",", label: gettextCatalog.getString("Virgule") },
                { code: ";", label: gettextCatalog.getString("Point-virgule") },
                { code: " ", label: gettextCatalog.getString("Espace") },
                { code: "__OTHER__", label: gettextCatalog.getString("Autre: ") }
            ]
        };

        mainCtrl.export = {
            separatorOpt: mainCtrl.options.separator[2],
            encoding: "ISO-8859-15"
        };

        init();


        function init() {
            mainCtrl.loaded = true;
        }

        function selectSeparator(code) {
            mainCtrl.export.separatorOpt = _.find(mainCtrl.options.separator, function (sep) {
                return sep.code === code;
            });
        }

        function confirm() {

            var params = {};
            params.fields = [];
            if(mainCtrl.options.pgcnId)
            {params.fields.push("pgcnId");}
            if(mainCtrl.options.lot)
            {params.fields.push("lot");}
            if(mainCtrl.options.train)
            {params.fields.push("train");}
            if(mainCtrl.options.radical)
            {params.fields.push("radical");}
            if(mainCtrl.options.title)
            {params.fields.push("title");}
            if(mainCtrl.options.pageCount)
            {params.fields.push("pageCount");}
            if(mainCtrl.options.date)
            {params.fields.push("date");}
            params.encoding = mainCtrl.export.encoding;

            if (mainCtrl.export.separatorOpt.code !== "__OTHER__") {
                params.separator = mainCtrl.export.separatorOpt.code;
            }
            else {
                params.separator = mainCtrl.export.separator;
            }

            $uibModalInstance.close(params);
        }

        function cancel() {
            $uibModalInstance.dismiss();
        }
    }
})();
