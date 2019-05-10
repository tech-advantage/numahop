(function () {
    "use strict";

    /* Services */
    angular.module('numaHopApp.service')
        .factory('Account', function ($resource) {
            return $resource('api/rest/account', {}, {});
        });

    angular.module('numaHopApp.service')
        .factory('Password', function ($resource) {
            return $resource('api/rest/account/change_password', {}, {});
        });

    angular.module('numaHopApp.service')
        .factory('AuthenticationSharedService', function ($rootScope, $http, authService, Principal, USER_ROLES) {
            var service = {
                login: function (param) {
                    var data = "j_username=" + encodeURIComponent(param.username) + "&j_password=" + encodeURIComponent(param.password) + "&remember-me=" + param.rememberMe + "&submit=Login";
                    $http.post('api/authentication', data, {
                        headers: {
                            "Content-Type": "application/x-www-form-urlencoded"
                        },
                        ignoreAuthModule: 'ignoreAuthModule'
                    }).success(function (data, status, headers, config) {
                        // retrieve the logged account information
                        Principal.identity(true).then(function (account) {
                            authService.loginConfirmed(account);
                        });
                    }).error(function (data, status, headers, config) {
                        $rootScope.authenticationError = true;
                        Principal.authenticate(null);
                        // Forbidden: Invalid CSRF Token...
                        if (status === 403) {
                            // to get a new csrf token call the api
                            $http.get('api/rest/account');
                        }
                    });
                },
                valid: function (authorizedRoles, originalEvent) {
                    if (service.isAuthenticated()) {
                        if (!service.isAuthorized(authorizedRoles)) {
                            // user is not allowed
                            originalEvent.preventDefault();
                            $rootScope.$broadcast("event:auth-notAuthorized");
                        }
                    } else {
                        Principal.identity(false).then(function (account) {
                            if (Principal.isAuthenticated()) {
                                if (!service.isAuthorized(authorizedRoles)) {
                                    // user is not allowed
                                    originalEvent.preventDefault();
                                    $rootScope.$broadcast("event:auth-notAuthorized");
                                } else {
                                    $rootScope.$broadcast("event:auth-loginConfirmed");
                                }
                            } else {
                                originalEvent.preventDefault();
                                $rootScope.$broadcast('event:auth-loginRequired');
                            }
                        }).catch(function () {
                            originalEvent.preventDefault();
                            $rootScope.$broadcast('event:auth-loginRequired');
                        });
                    }
                },
                isAuthenticated: function () {
                    return Principal.isAuthenticated();
                },
                isAuthorized: function (authorizedRoles) {
                    if (!angular.isArray(authorizedRoles)) {
                        if (authorizedRoles === USER_ROLES.all) {
                            return true;
                        }
                        authorizedRoles = [authorizedRoles];
                    }
                    else if (authorizedRoles.indexOf(USER_ROLES.all) !== -1) {
                        return true;
                    }

                    return Principal.isInAnyRole(authorizedRoles);
                },
                logout: function () {
                    $rootScope.authenticationError = false;

                    $http.post('api/logout').success(function () {
                        Principal.authenticate(null);
                        authService.loginCancelled();
                        // to get a new csrf token call the api
                        $http.get('api/rest/account');
                    }).error(function () {
                        Principal.authenticate(null);
                        authService.loginCancelled();
                    });
                }
            };
            return service;
        });

    angular.module('numaHopApp.service')
        .factory('LocaleService', function (gettextCatalog, tmhDynamicLocale, $cookies, CONFIGURATION) {
            var currentLocale;
            return {
                changeLocale: function (locale) {
                    currentLocale = locale;
                    moment.locale(locale);
                    gettextCatalog.setCurrentLanguage(locale);
                    gettextCatalog.loadRemote("/i18n/" + locale + ".json");
                    tmhDynamicLocale.set(locale);
                },
                initLocale: function () {
                    var locale = $cookies['tmhDynamicLocale.locale'];
                    if (angular.isUndefined(locale)) {
                        locale = CONFIGURATION.numahop.defaultLocale;
                    }
                    locale = locale.replace(/\W/g, "");
                    gettextCatalog.baseLanguage = CONFIGURATION.numahop.baseLanguage;
                    this.changeLocale(locale);
                    currentLocale = locale;
                },
                getLocale: function () {
                    return currentLocale;
                }
            };
        });

    angular.module('numaHopApp.service')
        .factory('VersionCheckerService', function ($http, $q, $rootScope, $timeout) {
            var service = {
                checkNewVersionAvailable: function (version) {
                    $http.get('build-date.json?cachebuster=' + moment().format('X')).then(function (response) {
                        $rootScope.newVersionAvailable = (response.data.build !== version);
                        $rootScope.connexionOk = true;
                    }, function (response) {
                        $rootScope.newVersionAvailable = false;
                        $rootScope.connexionOk = false;
                    });
                    $timeout(service.checkNewVersionAvailable, 10000, true, version);
                },
                getVersion: function () {
                    var defer = $q.defer();
                    $http.get("/api/rest/numahop?build=true")
                        .then(function (response) {
                            defer.resolve(response.data);
                        }, function () {
                            defer.reject();
                        });
                    return defer.promise;
                }
            };
            return service;
        });
})();