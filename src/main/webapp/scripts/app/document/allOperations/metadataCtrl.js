(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('MetadataCtrl', MetadataCtrl);

    function MetadataCtrl($http, $q, $location, $routeParams, $scope, $timeout, ImagesMetadataSrvc, CONFIGURATION, gettext, gettextCatalog, ListTools, MessageSrvc, DocUnitBaseService, ValidationSrvc, USER_ROLES) {
        var metaCtrl = this;

        metaCtrl.addMetadata = addMetadata;
        metaCtrl.cancel = cancel;
        metaCtrl.deleteMetadata = deleteMetadata;
        metaCtrl.displayBoolean = DocUnitBaseService.displayBoolean;
        metaCtrl.filterPropertiesList = filterPropertiesList;
        metaCtrl.onchangeMetadata = onchangeMetadata;
        metaCtrl.openForm = openForm;
        metaCtrl.saveMetadata = saveMetadata;

        metaCtrl.metadataList = [];
        metaCtrl.metadataPropertiesRef = [];
        metaCtrl.validation = ValidationSrvc;

        metaCtrl.booleanObj = [
            { value: true, text: gettextCatalog.getString('Oui') },
            { value: false, text: gettextCatalog.getString('Non') },
        ];

        metaCtrl.init = function (parentCtrl) {
            metaCtrl.parent = parentCtrl;
            metaCtrl.docUnit = parentCtrl.docUnitId;

            ImagesMetadataSrvc.query().$promise.then(function (metadataProperties) {
                metaCtrl.metadataProperties = metadataProperties;
                metaCtrl.metadataPropertiesRef = metadataProperties;
            });

            getMetaValues();
        };

        function addMetadata() {
            metaCtrl.metadataList.push({
                docUnitId: metaCtrl.docUnit,
                metadata: null,
                value: null,
            });
            openForm();
        }

        function getMetaValues() {
            ImagesMetadataSrvc.getMetaValues({
                docUnitId: metaCtrl.docUnit,
            }).$promise.then(function (metaList) {
                updateMetaObj(metaList);
            });
        }

        function cancel() {
            if ($scope.entityForm) {
                $scope.entityForm.$cancel();
            }
            getMetaValues();
        }

        function deleteMetadata(metadata) {
            var index = metaCtrl.metadataList.indexOf(metadata);
            metaCtrl.metadataList.splice(index, 1);
        }

        function onchangeMetadata(data, meta) {
            meta.metadata = data;
        }

        // Ouverture du formulaire et des sous formulaires
        function openForm() {
            $timeout(function () {
                if (angular.isDefined($scope.entityForm)) {
                    $scope.entityForm.$show();
                }
            });
        }

        function saveMetadata(metaList) {
            $timeout(function () {
                _.forEach(metaList, function (meta) {
                    meta.metadataId = meta.metadata.identifier;
                });

                ImagesMetadataSrvc.saveValues(metaList).$promise.then(function (savedMetaList) {
                    updateMetaObj(savedMetaList);
                    MessageSrvc.addSuccess(gettext('Sauvegarde des métadonnées terminées'));
                });
            });
        }

        function updateMetaObj(updatedList) {
            metaCtrl.metadataList = _.map(updatedList, function (meta) {
                meta.docUnitId = meta.docUnit.identifier;
                if (meta.value === 'true' || meta.value === 'false') {
                    meta.value = meta.value === 'true';
                }
                return meta;
            });
        }

        function filterPropertiesList(data) {
            return _.filter(metaCtrl.metadataPropertiesRef, function (property) {
                if (property.repeat || (data && data.identifier === property.identifier)) return true;

                //check if every prop is used
                return !_.find(metaCtrl.metadataList, function (meta) {
                    return meta.metadata && meta.metadata.identifier === property.identifier;
                });
            });
        }
    }
})();
