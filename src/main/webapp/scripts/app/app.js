(function () {
    'use strict';

    /* App Modules */
    angular.module('numaHopApp.utils', ['gettext']);
    angular.module('numaHopApp.controller', ['angularFileUpload', 'gettext', 'ngRoute', 'numaHopApp.utils', 'truncate', 'ui.bootstrap', 'pdf']);
    angular.module('numaHopApp.service', ['gettext', 'http-auth-interceptor', 'ngResource', 'ngRoute', 'numaHopApp.utils', 'truncate', 'LocalStorageModule']);
    angular.module('numaHopApp.directive', ['gettext']);
    angular.module('numaHopApp.component', []);
    angular.module('numaHopApp.filter', []);

    angular.module('numaHopApp', [
        'numaHopApp.controller',
        'numaHopApp.service',
        'numaHopApp.directive',
        'numaHopApp.component',
        'numaHopApp.utils',
        'numaHopApp.filter',
        'ui.router',
        'angular-loading-bar',
        'chart.js',
        'frapontillo.bootstrap-switch',
        'gettext',
        'LocalStorageModule',
        'ngAnimate',
        'ngAria',
        'ngCookies',
        'ngDragDrop',
        'ngSanitize',
        'smart-table',
        'tmh.dynamicLocale',
        'truncate',
        'ui.bootstrap',
        'ui.select',
        'ui.timepicker',
        'xeditable',
        'adf',
        'angular-svg-round-progressbar',
        'ui.bootstrap.contextMenu',
        'ngFileSaver',
        'sem.toggle-informations',
        'sem.toggle-filters',
        'angular-clipboard',
    ]);

    angular
        .module('numaHopApp')
        .config(function ($routeProvider, $httpProvider, $sceDelegateProvider, $compileProvider, gettext, tmhDynamicLocaleProvider, CONFIGURATION, USER_ROLES, cfpLoadingBarProvider, localStorageServiceProvider) {
            //enable CSRF
            $httpProvider.defaults.xsrfCookieName = 'CSRF-TOKEN';
            $httpProvider.defaults.xsrfHeaderName = 'X-CSRF-TOKEN';

            $httpProvider.defaults.cache = false;
            if (!$httpProvider.defaults.headers.get) {
                $httpProvider.defaults.headers.get = {};
            }
            // disable IE ajax request caching
            $httpProvider.defaults.headers.get['If-Modified-Since'] = '0';

            if (angular.isUndefined($httpProvider.defaults.transformRequest)) {
                $httpProvider.defaults.transformRequest = [];
            }
            $httpProvider.defaults.transformRequest.unshift(function (data) {
                if (angular.isUndefined(data)) {
                    return data;
                }
                ObjectTools.cleanObjectErrors(data);
                return JSOG.encode(data);
            });
            if (angular.isUndefined($httpProvider.defaults.transformResponse)) {
                $httpProvider.defaults.transformResponse = [];
            }
            $httpProvider.defaults.transformResponse.push(function (data) {
                if (angular.isUndefined(data)) {
                    return data;
                }
                return JSOG.decode(data);
            });

            // Temps avant que la loading bar ne s'affiche (en ms)
            cfpLoadingBarProvider.latencyThreshold = 200;
            // On n'affiche pas le spinner de la loading bar
            cfpLoadingBarProvider.includeSpinner = true;

            $routeProvider
                .when('/error', {
                    templateUrl: 'scripts/app/main/error.html',
                    access: {
                        authorizedRoles: [USER_ROLES.all],
                    },
                })
                .when('/account', {
                    // controller: function($location, Principal) {
                    //     if (angular.isDefined(Principal.identifier())) {
                    //         $location.path("/user/user").search({
                    //             id: Principal.identifier()
                    //         });
                    //     }
                    // },
                    // template: '',
                    templateUrl: 'scripts/app/user/account.html',
                    controller: 'AccountCtrl',
                    controllerAs: 'mainCtrl',
                    title: gettext('Mon profil'),
                    access: {
                        authorizedRoles: [USER_ROLES.all],
                    },
                })
                .when('/password', {
                    templateUrl: 'scripts/app/account/password/password.html',
                    controller: 'PasswordController',
                    access: {
                        authorizedRoles: [USER_ROLES.all],
                    },
                })
                .when('/login', {
                    templateUrl: 'scripts/app/account/login/login.html',
                    controller: 'LoginController',
                    access: {
                        authorizedRoles: [USER_ROLES.all],
                    },
                })
                .when('/logout', {
                    templateUrl: 'scripts/app/account/login/login.html',
                    controller: 'LogoutController',
                })
                .otherwise({
                    redirectTo: '/dashboard',
                });

            $sceDelegateProvider.resourceUrlWhitelist([
                // Allow same origin resource loads.
                'self',
                // allow semantheque-sid
                CONFIGURATION.numahop.url + '**',
                // Allow loading from our assets domain. Notice the
                // difference between * and **.
            ]);

            tmhDynamicLocaleProvider.localeLocationPattern('i18n/angular-i18n/angular-locale_{{locale}}.js');
            // tmhDynamicLocaleProvider.useCookieStorage('NG_TRANSLATE_LANG_KEY');
            localStorageServiceProvider.setPrefix('numahop');

            //Pour que la fonction naturalSort trie sans tenir compte de la casse
            naturalSort.insensitive = true;

            // On met un prérequis sur toutes les routes : que l'utilisateur soit chargé
            var originalWhen = $routeProvider.when;
            $routeProvider.when = function (path, route) {
                route.resolve || (route.resolve = {});
                angular.extend(route.resolve, {
                    CurrentUser: /*@ngInject*/ function (Principal) {
                        return Principal.identity();
                    },
                });

                return originalWhen.call($routeProvider, path, route);
            };
        })
        .run(function ($cookies, editableOptions, editableThemes, uibTimepickerConfig, LocaleService) {
            //Initialisation de la locale à partir du cookie ou FR sinon
            LocaleService.initLocale();

            // xeditable theme
            editableOptions.theme = 'numahop';
            editableThemes.numahop = _.extend(editableThemes.bs3, {
                buttonsClass: 'btn-sm',
                // Les templates de validation et cancel sont inversés pour inverser l'ordre des boutons Enregistrer et Annuler à l'écran
                cancelTpl: '<button type="submit" tooltip="{{\'Enregistrer\' | translate }}" tooltip-append-to-body="false" class="btn btn-primary"><span class="glyphicon-regular floppy-save"></span></button>',
                submitTpl:
                    '<button type="button" tooltip="{{\'Annuler\' | translate }}" tooltip-append-to-body="false" class="btn btn-sem4 btn-default" ng-click="$form.$cancel()"><span class="glyphicon-regular unshare"></span></button>',
                errorTpl: '<div class="editable-error help-block" ng-show="$error" ng-bind-html="$error"></div>',
            });

            // bootstrap timepicker
            uibTimepickerConfig.showMeridian = false;
        })
        .run(function ($location, $rootScope, $timeout, AuthenticationSharedService, gettext, MessageSrvc, Principal, USER_ROLES, WebsocketSrvc, VersionCheckerService) {
            $rootScope.isAuthenticated = AuthenticationSharedService.isAuthenticated;
            $rootScope.isAuthorized = AuthenticationSharedService.isAuthorized;
            $rootScope.isLoginPage = isLoginPage;
            $rootScope.deleteMessage = MessageSrvc.deleteMessage;
            $rootScope.location = $location;
            $rootScope.userRoles = USER_ROLES;
            $rootScope.newVersionAvailable = false;
            //        VersionCheckerService.checkNewVersionAvailable('%build-date%');

            $rootScope.getPermanentMessages = function (level) {
                return _.filter(MessageSrvc.getMessages(level), function (msg) {
                    return angular.isUndefined(msg.delayedDeletion);
                });
            };

            $rootScope.hasPermanentMessages = function (level) {
                return !!_.find(MessageSrvc.getMessages(level), function (msg) {
                    return angular.isUndefined(msg.delayedDeletion);
                });
            };

            $rootScope.getTemporaryMessages = function (level) {
                return _.filter(MessageSrvc.getMessages(level), function (msg) {
                    return angular.isDefined(msg.delayedDeletion);
                });
            };

            $rootScope.$on('$routeChangeStart', function (event, next) {
                if (angular.isDefined(next.access)) {
                    AuthenticationSharedService.valid(next.access.authorizedRoles, event);
                }
            });

            // Call when the client is confirmed
            $rootScope.$on('event:auth-loginConfirmed', function (data) {
                setVersion();
                // On s'abonne au websocket pour recevoir les notifications
                WebsocketSrvc.connect();
                WebsocketSrvc.subscribeNotification();
                if (isLoginPage()) {
                    var search = $location.search();
                    if (angular.isDefined(search.redirect)) {
                        $location.path(search.redirect).search('redirect', null).replace();
                    } else {
                        $location.path('/dashboard').replace();
                    }
                }
            });

            // Call when the 401 response is returned by the server
            $rootScope.$on('event:auth-loginRequired', function (rejection) {
                redirectToLogin();
            });

            // Call when the 403 response is returned by the server
            $rootScope.$on('event:auth-notAuthorized', function (rejection) {
                $timeout(function () {
                    $rootScope.errorMessage = gettext("Vous n'avez pas les droits requis pour accéder à cette fonctionnalité");
                    $location.path('/error').search({}).replace();
                });
            });

            // Call when the user logs out
            $rootScope.$on('event:auth-loginCancelled', function () {
                WebsocketSrvc.disconnect();
                redirectToLogin();
            });

            function isLoginPage() {
                return $location.path() === '/login';
            }

            function redirectToLogin() {
                Principal.authenticate(null);
                var path = $location.path();
                if (path !== '/' && path !== '' && path !== '/login' && path !== '/logout') {
                    $location.path('/login').search('redirect', path).replace();
                } else {
                    $location.path('/login').replace();
                }
            }

            function setVersion() {
                VersionCheckerService.getVersion().then(function (version) {
                    $rootScope.version = version;
                });
            }
        });
})();
