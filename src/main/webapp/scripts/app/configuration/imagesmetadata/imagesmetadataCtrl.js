(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('ImagesMetadataCtrl', ImagesMetadataCtrl);

    function ImagesMetadataCtrl(ModalSrvc, ImagesMetadataSrvc, MessageSrvc, gettext) {
        var mainCtrl = this;

        mainCtrl.metadataList = [];
        mainCtrl.metaTypes = ImagesMetadataSrvc.metadataPropertyTypes;
        mainCtrl.modified = false;

        mainCtrl.addMetadata = addMetadata;
        mainCtrl.editMetadata = editMetadata;
        mainCtrl.removeMetadata = removeMetadata;
        mainCtrl.reload = reload;
        mainCtrl.save = save;

        init();

        /** Initialisation */
        function init() {
            loadMetadata();
        }

        function addMetadata() {
            return ModalSrvc.open('scripts/app/configuration/imagesmetadata/editMetadataModal.html', null, 'lg', 'EditMetadataModalCtrl').then(metadata => {
                mainCtrl.modified = true;
                mainCtrl.metadataList.push(metadata);
            });
        }

        function editMetadata(index, metaToEdit) {
            let options = { metaToEdit: metaToEdit };

            return ModalSrvc.open('scripts/app/configuration/imagesmetadata/editMetadataModal.html', options, 'lg', 'EditMetadataModalCtrl').then(metaEdited => {
                mainCtrl.modified = true;
                console.log(metaEdited);
                if (metaToEdit.identifier != null) {
                    metaToEdit.label = metaEdited.label;
                    metaToEdit.repeat = metaEdited.repeat;
                    metaToEdit.type = metaEdited.type;
                    metaToEdit.iptcTag = metaEdited.iptcTag;
                    metaToEdit.xmpTag = metaEdited.xmpTag;
                } else {
                    mainCtrl.metadataList[index] = metaEdited;
                }
            });
        }

        function loadMetadata() {
            return ImagesMetadataSrvc.query().$promise.then(values => {
                updateMetaType(values);
                mainCtrl.metadataList = values;
            });
        }

        function reload() {
            mainCtrl.modified = false;
            mainCtrl.metadataList = [];
            loadMetadata();
        }

        function removeMetadata(index) {
            mainCtrl.metadataList.splice(index, 1);
            mainCtrl.modified = true;
        }

        function save() {
            mainCtrl.metadataList.forEach(meta => {
                meta.type = meta.type.code;
            });

            ImagesMetadataSrvc.saveList(mainCtrl.metadataList).$promise.then(newMetaDataList => {
                mainCtrl.modified = false;
                updateMetaType(newMetaDataList);
                mainCtrl.metadataList = newMetaDataList;
                MessageSrvc.addSuccess(gettext('Sauvegarde des métadonnées terminées'));
            });
        }

        function updateMetaType(metadataList) {
            metadataList.forEach(meta => {
                if (meta.type != null) {
                    let type = _.find(mainCtrl.metaTypes, function (metaType) {
                        return metaType.code === meta.type;
                    });

                    if (type != null) {
                        meta.type = type;
                    }

                    meta.repeat = meta.repeat.toString();
                }
            });
        }
    }
})();
