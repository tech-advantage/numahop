(function () {
    'use strict';

    angular.module('numaHopApp').factory('PasswordAdmin', function ($resource) {
        return $resource('api_int/account/change_password', {}, {});
    });

    angular.module('numaHopApp').factory('PasswordResetInitAdmin', function ($resource) {
        return $resource('api_int/account/reset_password/init', {}, {});
    });

    angular.module('numaHopApp').factory('PasswordResetFinishAdmin', function ($resource) {
        return $resource('api_int/account/reset_password/finish', {}, {});
    });
})();