(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('EditMetadataModalCtrl', EditMetadataModalCtrl);

    function EditMetadataModalCtrl($uibModalInstance, ImagesMetadataSrvc, options) {
        var mainCtrl = this;

        mainCtrl.title = 'Nouvelle Métadonnées';

        mainCtrl.ok = ok;
        mainCtrl.cancel = cancel;

        mainCtrl.metadataPropertyTypes = ImagesMetadataSrvc.metadataPropertyTypes;
        mainCtrl.metadataProperty = {
            label: '',
            repeat: 'false',
            type: {},
            iptcTag: '',
            xmpTag: '',
        };

        init();

        /** Initialisation */
        function init() {
            if (options && options.metaToEdit != null) {
                mainCtrl.metadataProperty.label = options.metaToEdit.label;
                mainCtrl.metadataProperty.repeat = options.metaToEdit.repeat.toString();
                mainCtrl.metadataProperty.type = options.metaToEdit.type;
                mainCtrl.metadataProperty.iptcTag = options.metaToEdit.iptcTag;
                mainCtrl.metadataProperty.xmpTag = options.metaToEdit.xmpTag;
            }
        }

        function cancel() {
            $uibModalInstance.dismiss('cancel');
        }

        function ok(value) {
            console.log(value);
            $uibModalInstance.close(value);
        }
    }
})();
