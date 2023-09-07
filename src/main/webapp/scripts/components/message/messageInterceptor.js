(function () {
    'use strict';

    angular
        .module('numaHopApp')
        .factory('messageInterceptor', function ($q, $rootScope) {
            var isRestCall = function (config) {
                var startAt = config.url.charAt(0) === '/' ? 1 : 0;
                return config && config.url && config.url.substr(startAt, 9) === 'api/rest/';
            };
            return {
                request: function (config) {
                    if (isRestCall(config)) {
                        $rootScope.$broadcast('event:messageInterceptor-httpOk');
                    }
                    return config;
                },
                responseError: function (response) {
                    if (isRestCall(response.config) && (response.status === 404 || response.status === 405 || response.status === 409 || response.status === 412 || response.status === 417 || response.status === 500)) {
                        $rootScope.$broadcast('event:messageInterceptor-httpError', response);
                    }
                    return $q.reject(response);
                },
            };
        })
        .config(function ($httpProvider) {
            $httpProvider.interceptors.push('messageInterceptor');
        })
        .run(function ($rootScope, gettext, MessageSrvc, ErreurSrvc) {
            $rootScope.$on('event:messageInterceptor-httpOk', function (event, args) {
                MessageSrvc.clearMessages(MessageSrvc.level.ERROR);
            });
            $rootScope.$on('event:messageInterceptor-httpError', function (event, args) {
                var alerts;
                // 412: precondition failed
                if (args.status === 412) {
                    // PgcnLockException
                    if (args.data.type === 'PgcnLockException') {
                        MessageSrvc.addError(gettext("Cet élément est verrouillé par {{name}} jusqu'à {{date}}"), {
                            name: args.data.lockedByName,
                            date: moment(args.data.lockedDate).add(60, 'minute').format('LT'),
                        });
                    }
                    // SemanthequeListValidationException
                    else if (angular.isArray(args.data)) {
                        alerts = _.chain(args.data)
                            .pluck('errors')
                            .flatten()
                            .uniq('code')
                            .map(function (element) {
                                return angular.isDefined(element) ? '<li><span class="glyphicon-halflings glyphicon-exclamation-sign"></span> ' + ErreurSrvc.getMessage(element.code, element.complements) + '</li>' : '';
                            })
                            .value()
                            .join('');
                        if (angular.isDefined(alerts) && alerts.length > 0) {
                            MessageSrvc.addError(gettext("L'enregistrement n'a pas été effectué : {{details}}"), {
                                details: alerts,
                            });
                        } else {
                            MessageSrvc.addError(gettext("L'enregistrement n'a pas été effectué. Veuillez corriger les erreurs du formulaire."));
                        }
                    }
                    // SemanthequeValidationException
                    else if (angular.isDefined(args.data.errors) && args.data.errors.length > 0) {
                        alerts = args.data.errors;
                        alerts.details = _.chain(alerts)
                            .filter(function (element) {
                                return !_.isUndefined(element);
                            })
                            .uniq('code')
                            .map(function (element) {
                                return '<li><span class="glyphicon-halflings glyphicon-exclamation-sign"></span> ' + ErreurSrvc.getMessage(element.code, element.complements) + '</li>';
                            })
                            .value()
                            .join('');
                        if (alerts.details) {
                            alerts.details = "<ul class='no-padding'>" + alerts.details + '</ul>';
                        }
                        MessageSrvc.addError(gettext("L'enregistrement n'a pas été effectué : {{details}}"), alerts);
                    } else {
                        MessageSrvc.addError(gettext("L'enregistrement n'a pas été effectué. Veuillez corriger les erreurs du formulaire."));
                    }
                } else if (args.status === 409) {
                    MessageSrvc.addError(gettext("Cet objet est lié à d'autres objets, il est donc impossible de le supprimer."));
                } else if (args.status === 404) {
                    MessageSrvc.addError(gettext("Cet objet n'existe pas."));
                } else {
                    if (_.isArray(args.data.errors)) {
                        args.details = _.uniq(
                            _(args.data.errors).filter(function (element) {
                                return !_.isUndefined(element);
                            })
                        )
                            .map(function (element) {
                                return '<div><span class="glyphicon-halflings glyphicon-exclamation-sign"></span> ' + ErreurSrvc.getMessage(element.code, element.complements) + '</div>';
                            })
                            .join('');

                        var message;

                        if (args.details) {
                            message = args.details;
                        } else if (args.data.message) {
                            message = args.data.message;
                        } else {
                            message = 'Une erreur inconnue est survenue.';
                        }

                        if (angular.isDefined(args.data.level) && args.data.level === 'WARNING') {
                            MessageSrvc.addWarn(gettext(message));
                        } else {
                            MessageSrvc.addError(gettext(message));
                        }
                    } else if (args.data.exception === 'org.springframework.orm.ObjectOptimisticLockingFailureException') {
                        MessageSrvc.addError(
                            gettext(
                                "<div>Vous avez tenté de modifier un objet que quelqu'un d'autre a modifié en même temps que vous !</div><div><b>Vos modifications n'ont pas été enregistrées.</b></div><div>Rechargez la page pour mettre à jour les informations, vos saisie seront perdues.</div>"
                            ),
                            args
                        );
                    } else if (args.status !== 417) {
                        // On ne log pas les 417 qui arrivent jusqu'ici car ce sont les requêtes de téléchargement de fichier en erreur et elles sont traitées ailleurs
                        MessageSrvc.addError(gettext('Une erreur inattendue est survenue :<br/>{{statusText}}.<br/>Vous pouvez recharger la page. Si le problème persiste, contactez votre administrateur.'), args);
                    }
                }
            });
        });
})();
