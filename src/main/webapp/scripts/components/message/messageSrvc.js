(function () {
    'use strict';

    angular.module('numaHopApp.service')
        .factory('MessageSrvc', function ($rootScope, $timeout, gettextCatalog) {
            var messages = [];
            var level = {
                SUCCESS: "success",
                INFO: "info",
                WARN: "warning",
                ERROR: "danger",
                FAILURE: "failure"
            };

            function addMessage(message, params, lvl, noDeletion, duration) {
                if (angular.isString(message) && message.trim() !== '') {
                    var content = gettextCatalog.getString(message, params);
                    var newMessage = findMessage(content, lvl);

                    if (angular.isUndefined(newMessage)) {
                        newMessage = {
                            level: lvl,
                            content: content,
                            timestamp: new Date(),
                            count: 1
                        };
                        messages.unshift(newMessage);
                    } else {
                        if (angular.isDefined(newMessage.delayedDeletion)) {
                            $timeout.cancel(newMessage.delayedDeletion);
                        }
                        newMessage.timestamp = new Date();
                        newMessage.count++;
                    }

                    if (noDeletion !== true) {
                        var timeout = (duration && duration!==null) ? duration : 5000;
                        newMessage.delayedDeletion = $timeout(function () {
                            deleteMessage(newMessage);
                        }, timeout);
                    }
                    if (lvl === level.ERROR || lvl === level.FAILURE) {
                        $rootScope.$semInfos.show();
                    }
                }
            }
            function addSuccess(message, params, noDeletion, duration) {
                addMessage(message, params, level.SUCCESS, noDeletion, duration);
            }
            function addFailure(message, params, noDeletion, duration) {
                addMessage(message, params, level.FAILURE, noDeletion, duration);
            }
            function addInfo(message, params, noDeletion, duration) {
                addMessage(message, params, level.INFO, noDeletion, duration);
            }
            function addWarn(message, params, noDeletion, duration) {
                addMessage(message, params, level.WARN, noDeletion, duration);
            }
            function addError(message, params) {
                addMessage(message, params, level.ERROR, true);
            }
            function clearMessages(lvl) {
                if (angular.isDefined(lvl)) {
                    if (!angular.isArray(lvl)) {
                        lvl = [lvl];
                    }
                    messages = _(messages).reject(function (msg) {
                        return lvl.indexOf(msg.level) >= 0;
                    });
                } else {
                    messages = _(messages).filter(function (msg) {
                        return _(msg).has("delayedDeletion");
                    });
                }
            }
            function deleteMessage(message) {
                var idx = messages.indexOf(message);
                if (idx >= 0) {
                    messages.splice(idx, 1);
                }
            }
            function findMessage(message, lvl) {
                return _.find(messages, function (msg) {
                    return msg.level === lvl && msg.content === message;
                });
            }
            function getMessages(lvl) {
                if (angular.isDefined(lvl)) {
                    return _.filter(messages, function (msg) {
                        return msg.level === lvl;
                    });
                } else {
                    return messages;
                }
            }
            function initPanel() {
                if ($rootScope.hasPermanentMessages()) {
                    $rootScope.$semInfos.show();
                    $timeout(function () {
                        $rootScope.$semInfos.hide();
                    }, 4000);
                }
            }

            // clear error messages on location changes
            $rootScope.$on('$locationChangeStart', function (event, next, current) {
                clearMessages();
            });

            return {
                addSuccess: addSuccess,
                addFailure: addFailure,
                addInfo: addInfo,
                addWarn: addWarn,
                addError: addError,
                clearMessages: clearMessages,
                deleteMessage: deleteMessage,
                getMessages: getMessages,
                level: level,
                initPanel: initPanel
            };
        });
})();
