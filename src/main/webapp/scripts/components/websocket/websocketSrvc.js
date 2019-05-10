(function () {
    'use strict';

    angular.module('numaHopApp.service')
        .factory('WebsocketSrvc', function ($cookies, $http, $q, $location, $rootScope, codeSrvc, MessageSrvc, Principal) {
            var service = {};
            var stompClient = null;
            var subscriberNotification = null;
            var connected = $q.defer();
            var alreadyConnectedOnce = false;

            function sendActivity() {
                if (stompClient !== null && stompClient.connected) {
                    stompClient
                        .send('/topic/activity',
                            {},
                            angular.toJson({ 'page': $location.path() }));
                }
            }

            service.connect = function () {
                if (angular.isUndefined(stompClient) || stompClient === null) {
                    var socket = new SockJS('/websocket');
                    stompClient = Stomp.over(socket);
                    stompClient.debug = null;
                    var headers = {};
                    headers['X-CSRF-TOKEN'] = $cookies[$http.defaults.xsrfCookieName];
                    stompClient.connect(headers, function (frame) {
                        connected.resolve("success");
                        sendActivity();
                        if (!alreadyConnectedOnce) {
                            $rootScope.$on('$routeChangeStart', function (event) {
                                sendActivity();
                            });
                            alreadyConnectedOnce = true;
                        }
                    });
                }
            };
            service.subscribeNotification = function () {
                if (subscriberNotification === null) {
                    connected.promise.then(function () {
                        subscriberNotification = stompClient.subscribe("/topic/notification", function (data) {
                            var notif = angular.fromJson(data.body);
                            var message = codeSrvc[notif.code];
                            if (angular.isUndefined(notif.userLogin) || notif.userLogin === null || notif.userLogin === Principal.login()) {
                                if (notif.level === 'ERROR') {
                                    MessageSrvc.addError(message);
                                } else if (notif.level === 'WARN') {
                                    MessageSrvc.addWarn(message);
                                } else if (notif.level === 'SUCCESS') {
                                    MessageSrvc.addSuccess(message);
                                } else {
                                    MessageSrvc.addInfo(message);
                                }
                                // On force la mise à jour pour que le message s'affiche, sinon il ne s'affiche pas toujours...
                                $rootScope.$apply();
                            }
                        });
                    }, null, null);
                }
            };
            service.unsubscribeNotification = function () {
                if (subscriberNotification !== null) {
                    subscriberNotification.unsubscribe();
                    subscriberNotification = null;
                }
            };
            service.subscribeObject = function (identifier, callback) {
                var defer = $q.defer();
                connected.promise.then(function () {
                    // retourne le subscriber
                    var subscriber = stompClient.subscribe("/topic/object/" + identifier, function (data) {
                        var body = angular.fromJson(data.body);
                        callback(body);
                    });
                    defer.resolve(subscriber);
                }, null, null);
                return defer.promise;
            };
            service.unsubscribeObject = function (subscriber) {
                if (subscriber !== null) {
                    subscriber.unsubscribe();
                }
            };
            service.disconnect = function () {
                if (stompClient !== null && stompClient.connected) {
                    this.unsubscribeNotification();
                    stompClient.disconnect();
                    stompClient = null;
                    connected = $q.defer();
                }
            };
            return service;
        });
})();
