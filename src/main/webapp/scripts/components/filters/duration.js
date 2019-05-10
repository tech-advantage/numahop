(function () {
    "use strict";

    angular.module('numaHopApp.filter')
        .filter("duration", function (gettextCatalog) {

            /**
             * Formattage d'une durée exprimée en secondes
             * @param {*} input 
             */
            function filter(input, format) {
                var dur = Number(input); // input = durée en secondes
                if (!angular.isNumber(dur)) {
                    return "";
                }

                var result;
                switch (format) {
                    case "day":
                        result = parseDurationDays(Math.round(dur / 3600 / 24), {});
                        break;
                    case "hour":
                        result = parseDurationHours(Math.round(dur / 3600), {});
                        break;
                    case "minute":
                        result = parseDurationMinutes(Math.round(dur / 60), {});
                        break;
                    case "second":
                    default:
                        result = parseDurationSeconds(dur, {});
                        break;
                }

                return formatDurationArray(result);
            }

            /**
             * @param {*} dur en secondes
             */
            function parseDurationSeconds(dur, result) {
                // secondes
                var reste = dur % 60;
                result.seconds = reste;

                // minutes
                dur = (dur - reste) / 60;
                if (dur > 0) {
                    parseDurationMinutes(dur, result);
                }

                return result;
            }

            /**
             * @param {*} dur en minutes
             */
            function parseDurationMinutes(dur, result) {
                // minutes
                var reste = dur % 60;
                result.minutes = reste;

                // heures
                dur = (dur - reste) / 60;
                if (dur > 0) {
                    parseDurationHours(dur, result);
                }

                return result;
            }

            /**
             * @param {*} dur en heures
             */
            function parseDurationHours(dur, result) {
                // heures
                var reste = dur % 24;
                result.hours = reste;

                // jours
                dur = (dur - reste) / 24;
                if (dur > 0) {
                    parseDurationDays(dur, result);
                }

                return result;
            }

            /**
             * @param {*} dur en heures
             */
            function parseDurationDays(dur, result) {
                result.days = dur;
                return result;
            }

            /**
             * Formattage d'une durée
             * @param {*} durArr objet avec les champs (optionnels) seconds, minutes, hours, days
             */
            function formatDurationArray(durArr) {
                var fmtStr = [];

                if (durArr.days) {
                    fmtStr.push(durArr.days + gettextCatalog.getString("j"));
                }
                if (durArr.hours) {
                    fmtStr.push(durArr.hours + gettextCatalog.getString("h"));
                }
                if (durArr.minutes) {
                    fmtStr.push(durArr.minutes + gettextCatalog.getString("min"));
                }
                if (durArr.seconds) {
                    fmtStr.push(durArr.seconds + gettextCatalog.getString("s"));
                }
                return fmtStr.length > 0 ? fmtStr.join(" ") : "0";
            }

            return filter;
        });
})();