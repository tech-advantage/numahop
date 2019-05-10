(function () {
    'use strict';

    angular.module('numaHopApp.service')
        .factory('UserSrvc', function ($resource, CONFIGURATION) {
            var service = $resource(CONFIGURATION.numahop.url + 'api/rest/user/:id', {
                id: '@identifier'
            }, {
                    changePassword: {
                        method: 'POST',
                        params: {
                            change_password: true
                        }
                    },
                    changeLang: {
                        method: 'POST',
                        params: {
                            change_lang: true
                        }
                    },
                    dto: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            dto: true
                        }
                    },
                    deleteSignature: {
                        method: 'DELETE',
                        isArray: false,
                        params: {
                            signature: true
                        }
                    },
                    hasSignature: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            signexists: true
                        }
                    },
                    search: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            search: true,
                            size: 50
                        }
                    },
                    duplicate: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            'duplicate': true
                        }
                    },
                    providers: {
                        method: 'GET',
                        isArray: true,
                        params: {
                            'providers': true
                        }
                    }
                });

            service.toString = function (borrower) {
                var text = "";
                if (borrower) {
                    if (borrower.surname) { text += borrower.surname + " "; }
                    if (borrower.firstname) { text += borrower.firstname + " "; }
                }
                return text.trim();
            };
            service.toStringWithBirthdate = function (borrower) {
                return service.toString(borrower, true);
            };
            return service;
        });

    angular.module('numaHopApp.service')
        .factory('UserAuthorizationSrvc', function ($resource, CONFIGURATION) {
            return $resource(CONFIGURATION.numahop.url + 'api/rest/authorization/:id', {
                id: '@identifier'
            }, {
                    dto: {
                        method: 'GET',
                        isArray: true,
                        params: {
                            dto: true
                        }
                    }
                });
        });

    angular.module('numaHopApp.service')
        .factory('UserRoleSrvc', function ($resource, CONFIGURATION) {
            return $resource(CONFIGURATION.numahop.url + 'api/rest/role/:id', {
                id: '@identifier'
            }, {
                    search: {
                        method: 'GET',
                        isArray: true,
                        params: {
                            search: true
                        }
                    }
                });
        });
})();