(function () {
    'use strict';

    angular.module('numaHopApp.service').factory('Principal', function Principal($q, $rootScope, Account, DashboardSrvc) {
        var _identity,
            _authenticated = false,
            _promiseLoading;

        return {
            isIdentityResolved: function () {
                return angular.isDefined(_identity);
            },
            isAuthenticated: function () {
                return _authenticated;
            },
            isInRole: function (role) {
                if (!_authenticated || !_identity || !_identity.roles) {
                    return false;
                }

                return _identity.roles.indexOf(role) !== -1;
            },
            isInAnyRole: function (roles) {
                if (!_authenticated || !_identity.roles) {
                    return false;
                }

                for (var i = 0; i < roles.length; i++) {
                    if (this.isInRole(roles[i])) {
                        return true;
                    }
                }

                return false;
            },
            authenticate: function (identity) {
                _identity = identity;
                _authenticated = identity !== null;
                $rootScope.account = _identity;
            },
            identifier: function () {
                if (_authenticated && _identity) {
                    return _identity.identifier;
                }
            },
            login: function () {
                if (_authenticated && _identity) {
                    return _identity.login;
                }
            },
            library: function () {
                if (_authenticated && _identity) {
                    return _identity.library;
                }
            },
            identity: function (force) {
                if (force === true) {
                    _identity = undefined;
                    _promiseLoading = undefined;
                }
                var deferred = $q.defer();
                // check and see if we have retrieved the identity data from the server.
                // if we have, reuse it by immediately resolving
                if (angular.isDefined(_identity)) {
                    deferred.resolve(_identity);
                    return deferred.promise;
                }
                if (angular.isDefined(_promiseLoading)) {
                    return _promiseLoading;
                }

                // retrieve the identity data from the server, update the identity object, and then resolve.
                _promiseLoading = Account.get().$promise;
                _promiseLoading
                    .then(function (account) {
                        _identity = account;
                        _authenticated = true;
                        _promiseLoading = undefined;
                        $rootScope.account = _identity;
                        DashboardSrvc.setDashboard(account.dashboard);
                        deferred.resolve(_identity);
                    })
                    .catch(function () {
                        _identity = null;
                        _authenticated = false;
                        _promiseLoading = undefined;
                        $rootScope.account = _identity;
                        DashboardSrvc.setDashboard(undefined);
                        deferred.resolve(_identity);
                    });
                return deferred.promise;
            },
        };
    });
})();
