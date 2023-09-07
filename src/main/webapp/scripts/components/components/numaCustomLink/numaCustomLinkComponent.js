(function () {
    'use strict';

    angular.module('numaHopApp.component').component('numaCustomLink', {
        bindings: {
            linkType: '@numaLinkType',
            linkParameters: '<numaLinkParameters',
            imageClass: '@numaImageClass',
            tooltip: '@numaTooltip',
        },
        templateUrl: '/scripts/components/components/numaCustomLink/template.html',
        controller: function ($scope, NumahopUrlService) {
            var ctrl = this;

            ctrl.$onChanges = function (changesObj) {
                if (changesObj.linkParameters) {
                    ctrl.entityLink = NumahopUrlService.getUrlForTypeAndParameters(ctrl.linkType, angular.copy(changesObj.linkParameters.currentValue));
                }
            };
        },
    });
})();
