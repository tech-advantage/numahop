(function () {
    'use strict';

    angular.module('numaHopApp.service')
        .factory('FTPConfigurationSrvc', function ($resource, CONFIGURATION) {
            var service = $resource(CONFIGURATION.numahop.url + 'api/rest/ftpconfiguration/:id', {
                id: '@identifier'
            }, {
                    dto: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            dto: true
                        }
                    },
                    search: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            search: true,
                            size: 50
                        }
                    }
                });
            return service;
        });
    
    angular.module('numaHopApp.service')
    .factory('ExportFTPConfigurationSrvc', function ($resource, CONFIGURATION) {
        var service = $resource(CONFIGURATION.numahop.url + 'api/rest/exportftpconfiguration/:id', {
            id: '@identifier'
        }, {
                dto: {
                    method: 'GET',
                    isArray: false,
                    params: {
                        dto: true
                    }
                },
                search: {
                    method: 'GET',
                    isArray: false,
                    params: {
                        search: true,
                        size: 50
                    }
                }
            });
        return service;
    });

    angular.module('numaHopApp.service')
        .factory('SFTPConfigurationSrvc', function ($resource, CONFIGURATION) {
            var service = $resource(CONFIGURATION.numahop.url + 'api/rest/conf_sftp/:id', {
                id: '@identifier'
            }, {
                    dto: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            dto: true
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
                    pacs: {
                        method: 'GET',
                        isArray: true,
                        params: {
                            pacs: true
                        }
                    },
                    uploadDpdi: {
                        method: 'POST',
                        transformRequest: angular.identity,
                        headers: {
                            'Content-Type': undefined,
                            enctype: 'multipart/form-data'
                        },
                        isArray: false //,                    
                        //                    params: {
                        //                        upload: true
                        //                    }
                    }
                });
            return service;
        });

    angular.module('numaHopApp.service')
        .factory('IAConfigurationSrvc', function ($resource, CONFIGURATION) {
            var service = $resource(CONFIGURATION.numahop.url + 'api/rest/conf_internet_archive/:id', {
                id: '@identifier'
            }, {
                    dto: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            dto: true
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
                    collections: {
                        method: 'GET',
                        isArray: true,
                        params: {
                            collections: true
                        }
                    },
                    configurations: {
                        method: 'GET',
                        isArray: true,
                        params: {
                            configurations: true
                        }
                    }
                });
            return service;
        });
    
    angular.module('numaHopApp.service')
    .factory('OmekaConfigurationSrvc', function ($resource, CONFIGURATION) {
        var service = $resource(CONFIGURATION.numahop.url + 'api/rest/conf_omeka/:id', {
            id: '@identifier'
        }, {
                dto: {
                    method: 'GET',
                    isArray: false,
                    params: {
                        dto: true
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
                omekacollections: {
                    method: 'GET',
                    isArray: true,
                    params: {
                        collections: true
                    }
                },
                configurations: {
                    method: 'GET',
                    isArray: true,
                    params: {
                        configurations: true
                    }
                },
                omekaitems: {
                    method: 'GET',
                    isArray: true,
                    params: {
                        items: true
                    }
                }
                
            });
        return service;
    });


    angular.module('numaHopApp.service')
        .factory('LibraryParameterSrvc', function ($resource, CONFIGURATION) {
            var service = $resource(CONFIGURATION.numahop.url + 'api/rest/libraryparameter/:id', {
                id: '@identifier'
            }, {
                    dto: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            dto: true
                        }
                    },
                    cinesDefaultValues: {
                        method: 'GET',
                        isArray: false,
                        params: {
                            cinesdefaultvalues: true
                        }
                    }
                });
            return service;
        });

})();