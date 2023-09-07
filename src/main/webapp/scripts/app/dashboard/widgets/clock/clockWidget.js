(function () {
    'use strict';

    angular
        .module('numaHopApp')
        .config(function (dashboardProvider, gettext) {
            dashboardProvider.widget('clock', {
                title: gettext('Horloge'),
                category: gettext('Divers'),
                description: gettext("Affiche l'heure courante"),
                templateUrl: 'scripts/app/dashboard/widgets/clock/clockWidget.html',
                controller: 'ClockWidgetCtrl',
                controllerAs: 'mainCtrl',
                resolve: /* @ngInject */ {},
                authority: 'W_CLOCK',
            });
        })
        .controller('ClockWidgetCtrl', function ($interval) {
            var mainCtrl = this;
            mainCtrl.radius = 125;
            mainCtrl.stroke = 15;

            mainCtrl.getStyle = function () {
                var transform = 'translateY(-50%) ' + 'translateX(-50%)';

                return {
                    top: '50%',
                    bottom: 'auto',
                    left: '50%',
                    transform: transform,
                    '-moz-transform': transform,
                    '-webkit-transform': transform,
                    'font-size': mainCtrl.radius / 3.5 + 'px',
                    height: '40px',
                };
            };

            var getPadded = function (val) {
                return val < 10 ? '0' + val : val;
            };

            $interval(function () {
                var date = new Date();
                var hours = date.getHours();
                var minutes = date.getMinutes();
                var seconds = date.getSeconds();

                mainCtrl.hours = hours;
                mainCtrl.minutes = minutes;
                mainCtrl.seconds = seconds;
                mainCtrl.time = getPadded(hours) + ':' + getPadded(minutes) + ':' + getPadded(seconds);
            }, 1000);
        });
})();
