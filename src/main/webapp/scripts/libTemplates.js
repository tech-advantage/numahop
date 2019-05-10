(function() {
    'use strict';

    // override bootstrap-ui templates
    angular.module('numaHopApp.directive')
           .config(function($provide) {
                // accordion
                $provide.decorator("uibAccordionGroupDirective", function($delegate) {
                    var directive = $delegate[0];
                    directive.templateUrl = '/scripts/components/directives/accordion/accordion-group.html';
                    return $delegate;
                });

                // datepicker
                $provide.decorator("uibDaypickerDirective", function($delegate) {
                    var directive = $delegate[0];
                    directive.templateUrl = '/scripts/components/directives/datepicker/day.html';
                    return $delegate;
                });
                $provide.decorator("uibMonthpickerDirective", function($delegate) {
                    var directive = $delegate[0];
                    directive.templateUrl = '/scripts/components/directives/datepicker/month.html';
                    return $delegate;
                });
                $provide.decorator("uibYearpickerDirective", function($delegate) {
                    var directive = $delegate[0];
                    directive.templateUrl = '/scripts/components/directives/datepicker/year.html';
                    return $delegate;
                });
           });
})();
