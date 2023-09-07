(function () {
    'use strict';

    angular.module('numaHopApp.service').factory('ValidationSrvc', function (gettextCatalog) {
        /**
         *  Composition de fonctions de validation
         *  var composedValidationFn = compose(required, customValidation);
         * **/
        var compose = function () {
            var fns = arguments;

            return function (value) {
                for (var i = 0; i < fns.length; i++) {
                    var result = fns[i].call(this, value);

                    if (angular.isDefined(result)) {
                        return result;
                    }
                }
            };
        };

        var isDefined = function (value) {
            return angular.isDefined(value) && value !== null && value !== '' && (typeof value !== 'string' || value.trim() !== '') && (!angular.isArray(value) || value.length);
        };

        function convertToNumber(value) {
            if (!isDefined(value)) {
                return;
            }
            if (!angular.isNumber(value)) {
                return Number(value);
            } else {
                return value;
            }
        }

        var isNumber = function (value) {
            if (!isDefined(value)) {
                return;
            }
            value = convertToNumber(value);
            if (isNaN(value)) {
                return gettextCatalog.getString('Ce champ est de type numérique');
            }
        };

        var passwordCompliante = function (value) {
            if (!isDefined(value)) {
                return gettextCatalog.getString('Ce champ est obligatoire');
            }

            var regEx = new RegExp('^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*])(?=.{8,})');
            if (!regEx.test(value)) {
                return gettextCatalog.getString("Le mot de passe n'est pas conforme. (12 caractères, au moins 1 majuscule, 1 minuscule, 1 chiffre et un caractère spécial).");
            }
            return true;
        };

        var isInteger = compose(isNumber, function (value) {
            if (!isDefined(value)) {
                return;
            }
            value = convertToNumber(value);
            if (value % 1 !== 0) {
                return gettextCatalog.getString('Un nombre entier est attendu');
            }
        });

        // Le champ est obligatoire
        var required = function (value) {
            if (!isDefined(value)) {
                return gettextCatalog.getString('Ce champ est obligatoire');
            }
        };

        // Le champ doit être renseigné
        var nonEmpty = function (value) {
            if (_.isEmpty(value)) {
                return gettextCatalog.getString('Ce champ doit être renseigné');
            }
        };

        // Le champ est un nombre positif ou nul
        var dgte0 = compose(isNumber, function (value) {
            value = convertToNumber(value);
            if (value < 0) {
                return gettextCatalog.getString('Un nombre positif ou nul est attendu');
            }
        });

        // Le champ est un nombre entier positif ou nul
        var gte0 = compose(isInteger, function (value) {
            value = convertToNumber(value);
            if (value < 0) {
                return gettextCatalog.getString('Un nombre entier positif ou nul est attendu');
            }
        });

        // Le champ est un nombre strictement positif
        var dgt0 = compose(isNumber, function (value) {
            value = convertToNumber(value);
            if (value <= 0) {
                return gettextCatalog.getString('Un nombre positif est attendu');
            }
        });

        // Le champ est un nombre entier strictement positif
        var gt0 = compose(isInteger, function (value) {
            value = convertToNumber(value);
            if (value <= 0) {
                return gettextCatalog.getString('Un nombre positif est attendu');
            }
        });

        // La date (time / date ou datetime) overlappe avec une autre date
        var overlap = function (date, otherdates) {
            if (!isDefined(date) || !isDefined(otherdates)) {
                return;
            }
            var overlapping = _.find(otherdates, function (otherdate) {
                return date.start <= otherdate.end && date.end >= otherdate.start;
            });
            if (angular.isDefined(overlapping)) {
                return gettextCatalog.getString('Date en collision avec {{start}} - {{end}}', moment(overlapping.start).format('HH:mm'), moment(overlapping.end).format('HH:mm'));
            }
        };

        // Le champ est un nombre entier entre 1 et 5
        var intervalle = compose(isInteger, function (value) {
            value = convertToNumber(value);
            if (value < 1 || value > 5) {
                return gettextCatalog.getString('Un entier entre 1 et 5 est attendu');
            }
        });

        return {
            compose: compose,
            gte0: gte0,
            gt0: gt0,
            dgte0: dgte0,
            dgt0: dgt0,
            nonEmpty: nonEmpty,
            required: required,
            overlap: overlap,
            intervalle: intervalle,
            passwordCompliante: passwordCompliante,
        };
    });
})();
