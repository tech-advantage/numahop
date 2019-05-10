(function () {
    'use strict';

    angular.module('numaHopApp').factory('AuthServerProvider',
        function loginService($http, localStorageService, Tracker) {
            return {
                login: function (credentials) {
                    var data = 'j_username=' + encodeURIComponent(credentials.username)
                        + '&j_password='
                        + encodeURIComponent(credentials.password)
                        + '&remember-me='
                        + credentials.rememberMe
                        + '&submit=Login';
                    return $http.post('app/authentication', data, {
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded'
                        }
                    }).success(function (response) {
                        return response;
                    });
                },
                logout: function () {
                    Tracker.disconnect();
                    // logout from the server
                    $http.post('api_int/logout').success(function (response) {
                        localStorageService.clearAll();
                        // to get a new csrf token call the api
                        $http.get('api_int/account');
                        return response;
                    });
                },
                getToken: function () {
                    var token = localStorageService.get('token');
                    return token;
                },
                hasValidToken: function () {
                    var token = this.getToken();
                    return !!token;
                }
            };
        });
})();