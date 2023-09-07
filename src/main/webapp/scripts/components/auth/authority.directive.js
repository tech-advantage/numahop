(function () {
    'use strict';

    angular
        .module('numaHopApp')
        .directive('hasAnyRole', [
            'Principal',
            function (Principal) {
                return {
                    restrict: 'A',
                    link: function (scope, element, attrs) {
                        var roles = attrs.hasAnyRole.replace(/\s+/g, '').split(',');

                        var setVisible = function () {
                                element.removeClass('hidden');
                            },
                            setHidden = function () {
                                element.addClass('hidden');
                            },
                            defineVisibility = function (reset) {
                                var result;
                                if (reset) {
                                    setVisible();
                                }

                                result = Principal.isInAnyRole(roles);
                                if (result) {
                                    setVisible();
                                } else {
                                    setHidden();
                                }
                            };

                        if (roles.length > 0) {
                            defineVisibility(true);
                        }
                    },
                };
            },
        ])
        .directive('hasRole', [
            'Principal',
            function (Principal) {
                return {
                    restrict: 'A',
                    link: function (scope, element, attrs) {
                        var role = attrs.hasRole.replace(/\s+/g, '');

                        var setVisible = function () {
                                element.removeClass('hidden');
                            },
                            setHidden = function () {
                                element.addClass('hidden');
                            },
                            defineVisibility = function (reset) {
                                var result;
                                if (reset) {
                                    setVisible();
                                }

                                result = Principal.isInRole(role);
                                if (result) {
                                    setVisible();
                                } else {
                                    setHidden();
                                }
                            };

                        if (role.length > 0) {
                            defineVisibility(true);
                        }
                    },
                };
            },
        ]);
})();
