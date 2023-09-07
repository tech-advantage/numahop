(function () {
    'use strict';

    angular.module('numaHopApp.directive').directive('activeMenu', function (gettextCatalog, tmhDynamicLocale) {
        return {
            restrict: 'A',
            link: function (scope, element, attrs, controller) {
                var language = attrs.activeMenu;

                scope.$watch(
                    function () {
                        return gettextCatalog.currentLanguage;
                    },
                    function (selectedLanguage) {
                        if (language === selectedLanguage) {
                            tmhDynamicLocale.set(language);
                            element.addClass('active');
                        } else {
                            element.removeClass('active');
                        }
                    }
                );
            },
        };
    });

    angular.module('numaHopApp.directive').directive('passwordStrengthBar', function () {
        return {
            replace: true,
            restrict: 'E',
            template:
                '<div id="strength">' +
                '<small translate>Force du mot de passe :</small>' +
                '<ul id="strengthBar">' +
                '<li class="point"></li><li class="point"></li><li class="point"></li><li class="point"></li><li class="point"></li>' +
                '</ul>' +
                '</div>',
            link: function (scope, iElement, attr) {
                var strength = {
                    colors: ['#F00', '#F90', '#FF0', '#9F0', '#0F0'],
                    mesureStrength: function (p) {
                        var _force = 0;
                        var _regex = /[$-/:-?{-~!"^_`\[\]]/g; // "

                        var _lowerLetters = /[a-z]+/.test(p);
                        var _upperLetters = /[A-Z]+/.test(p);
                        var _numbers = /[0-9]+/.test(p);
                        var _symbols = _regex.test(p);

                        var _flags = [_lowerLetters, _upperLetters, _numbers, _symbols];
                        var _passedMatches = $.grep(_flags, function (el) {
                            return el === true;
                        }).length;

                        _force += 2 * p.length + (p.length >= 10 ? 1 : 0);
                        _force += _passedMatches * 10;

                        // penality (short password)
                        _force = p.length <= 6 ? Math.min(_force, 10) : _force;

                        // penality (poor variety of characters)
                        _force = _passedMatches === 1 ? Math.min(_force, 10) : _force;
                        _force = _passedMatches === 2 ? Math.min(_force, 20) : _force;
                        _force = _passedMatches === 3 ? Math.min(_force, 40) : _force;

                        return _force;
                    },
                    getColor: function (s) {
                        var idx = 0;
                        if (s <= 10) {
                            idx = 0;
                        } else if (s <= 20) {
                            idx = 1;
                        } else if (s <= 30) {
                            idx = 2;
                        } else if (s <= 40) {
                            idx = 3;
                        } else {
                            idx = 4;
                        }

                        return {
                            idx: idx + 1,
                            col: this.colors[idx],
                        };
                    },
                };
                scope.$watch(attr.passwordToCheck, function (password) {
                    if (password) {
                        var c = strength.getColor(strength.mesureStrength(password));
                        iElement.removeClass('ng-hide');
                        iElement
                            .find('ul')
                            .children('li')
                            .css({
                                background: '#DDD',
                            })
                            .slice(0, c.idx)
                            .css({
                                background: c.col,
                            });
                    }
                });
            },
        };
    });

    angular.module('numaHopApp.directive').directive('passwordValidation', function (ValidationSrvc) {
        return {
            require: 'ngModel',
            link: function (scope, element, attrs, formCtrl) {
                function myValidation(value) {
                    console.log(value);
                    if (ValidationSrvc.passwordCompliante(value) === true) {
                        console.log('ok');
                        formCtrl.$setValidity('compliante', true);
                    } else {
                        console.log('nok');
                        formCtrl.$setValidity('compliante', false);
                    }
                    return value;
                }
                formCtrl.$parsers.push(myValidation);
            },
        };
    });

    angular.module('numaHopApp.directive').directive('showValidation', function () {
        return {
            restrict: 'A',
            require: 'form',
            link: function (scope, element, attrs, formCtrl) {
                element.find('.form-group').each(function () {
                    var $formGroup = $(this);
                    var $inputs = $formGroup.find('input[ng-model],textarea[ng-model],select[ng-model]');

                    if ($inputs.length > 0) {
                        $inputs.each(function () {
                            var $input = $(this);
                            scope.$watch(
                                function () {
                                    return $input.hasClass('ng-invalid') && $input.hasClass('ng-dirty');
                                },
                                function (isInvalid) {
                                    $formGroup.toggleClass('has-error', isInvalid);
                                }
                            );
                        });
                    }
                });
            },
        };
    });

    angular.module('numaHopApp.directive').directive('whenScrolled', function () {
        return function (scope, elm, attr) {
            var raw = elm[0];

            elm.bind('scroll', function () {
                if (Math.ceil(raw.scrollTop + raw.offsetHeight) >= raw.scrollHeight) {
                    scope.$apply(attr.whenScrolled);
                }
            });
        };
    });

    angular.module('numaHopApp.directive').directive('semAutoHideInfos', function ($document, $window) {
        return {
            restrict: 'A',
            link: function (scope, element) {
                element.data('infos', true);
                angular.element($document[0].body).on('click', function (e) {
                    var inMenu = angular.element(e.target).inheritedData('infos');
                    if (inMenu) {
                        var parent = e.target.parentElement;
                        if ((parent.tagName === 'A' && parent.href) || parent.className.indexOf('sem-auto-hide') > -1) {
                            scope.$semInfos.hide();
                        }
                    } else {
                        scope.$semInfos.hide();
                    }
                });
            },
        };
    });

    angular.module('numaHopApp.directive').directive('semAutoHideFilters', function ($document, $window) {
        return {
            restrict: 'A',
            link: function (scope, element) {
                element.data('filters', true);
                angular.element($document[0].body).on('click', function (e) {
                    var inMenu = angular.element(e.target).inheritedData('filters') || angular.element(e.target).parents('.sem-filter').length > 0;
                    if (inMenu) {
                        var parent = e.target.parentElement;
                        if ((parent.tagName === 'A' && parent.href) || parent.className.indexOf('sem-auto-hide') > -1) {
                            scope.$semFilters.hide();
                        }
                    } else {
                        scope.$semFilters.hide();
                    }
                });
            },
        };
    });

    angular.module('numaHopApp.directive').directive('selectOnFocus', function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                element.on('focus', function () {
                    this.select();
                });
            },
        };
    });

    angular.module('numaHopApp.directive').directive('disableAutoClose', function () {
        // directive for disabling the default
        // close on 'click' behavior
        return {
            link: function ($scope, $element) {
                $element.on('click', function ($event) {
                    $event.stopPropagation();
                });
            },
        };
    });

    angular.module('numaHopApp.directive').directive('ngBindCompileHtml', function ($compile) {
        return function (scope, element, attrs) {
            scope.$watch(
                function (scope) {
                    return scope.$eval(attrs.ngBindCompileHtml);
                },
                function (value) {
                    element.html(value);
                    $compile(element.contents())(scope);
                }
            );
        };
    });
})();
