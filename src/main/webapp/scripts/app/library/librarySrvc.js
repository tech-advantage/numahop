(function () {
    'use strict';

    angular.module('numaHopApp.service')
        .factory('LibrarySrvc', function ($resource, CONFIGURATION) {
            var service = $resource(CONFIGURATION.numahop.url + 'api/rest/library/:id', {
                id: '@identifier'
            }, {
                    dto: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            dto: true
                        }
                    },
                    deleteLogo: {
                        method: 'DELETE',
                        isArray: false,
                        params: {
                            logo: true
                        }
                    },
                    hasLogo: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            logoexists: true
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
                    simpleDto: {
                        method: 'GET',
                        isArray: true,
                        params: {
                            simpleDto: true
                        }
                    },
                    suggest: {
                        method: 'GET',
                        isArray: true,
                        params: {
                            'suggest': true
                        }
                    },
                    users: {
                        method: 'GET',
                        isArray: true,
                        params: {
                            'users': true
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
            return service;
        });
})();