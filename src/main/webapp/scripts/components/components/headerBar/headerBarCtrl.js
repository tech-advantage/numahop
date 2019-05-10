(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('HeaderBarController', HeaderBarController);

    function HeaderBarController(NumahopUrlService) {
        var ctrl = this;

        init();

        function init() {
            setShow();
            setDisplay();
            setLinks();
        }

        function setShow() {
            ctrl.showDelivery = ctrl.delivery && ctrl.delivery.identifier;
            ctrl.showDocUnit = !!ctrl.docUnit;
            ctrl.showLibrary = !!ctrl.library;
            ctrl.showLot = !!ctrl.lot;
            ctrl.showProject = !!ctrl.project;
            ctrl.showTrain = !!ctrl.train;
        }

        function setDisplay() {
            if (ctrl.showDelivery) {
                ctrl.displayDelivery = ctrl.delivery.label;
            }
            if (ctrl.showDocUnit) {
                ctrl.displayDocUnit = ctrl.docUnit.label;
            }
            if (ctrl.showLibrary) {
                ctrl.displayLibrary = ctrl.library.name;
            }
            if (ctrl.showLot) {
                ctrl.displayLot = ctrl.lot.label;
            }
            if (ctrl.showProject) {
                ctrl.displayProject = ctrl.project.name;
            }
            if (ctrl.showTrain) {
                ctrl.displayTrain = ctrl.train.label;
            }
        }

        function setLinks() {
            if (ctrl.delivery) {
                ctrl.linkDelivery = NumahopUrlService.getUrlForTypeAndParameters("delivery", { id: ctrl.delivery.identifier });
            }
            if (ctrl.docUnit) {
                ctrl.linkDocUnit = NumahopUrlService.getUrlForTypeAndParameters("docunit", { id: ctrl.docUnit.identifier });
            }
            if (ctrl.library) {
                ctrl.linkLibrary = NumahopUrlService.getUrlForTypeAndParameters("library", { id: ctrl.library.identifier });
            }
            if (ctrl.lot) {
                ctrl.linkLot = NumahopUrlService.getUrlForTypeAndParameters("lot", { id: ctrl.lot.identifier });
            }
            if (ctrl.project) {
                ctrl.linkProject = NumahopUrlService.getUrlForTypeAndParameters("project", { id: ctrl.project.identifier });
            }
            if (ctrl.train) {
                ctrl.linkTrain = NumahopUrlService.getUrlForTypeAndParameters("train", { id: ctrl.train.identifier });
            }

        }
    }
})();