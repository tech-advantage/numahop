(function () {
    'use strict';

    angular.module('numaHopApp.controller')
        .controller('ProjectStateEditWidgetCtrl', ProjectStateEditWidgetCtrl);

    function ProjectStateEditWidgetCtrl($q, config, gettextCatalog, LibrarySrvc, ProjectSrvc) {

        var mainCtrl = this;
        mainCtrl.isConfigured = isConfigured;

        mainCtrl.options = {
            libraries: {
                text: "name",
                placeholder: gettextCatalog.getString("Bibliothèque"),
                trackby: "identifier",
                // Chargement avec mise en cache du résultat
                refresh: function ($select) {
                    if (!mainCtrl.options.libraries.data) {
                        mainCtrl.options.libraries.data = LibrarySrvc.query({ dto: true });
                        return mainCtrl.options.libraries.data.$promise
                            .then(function (lib) {
                                return _.map(lib, function (l) {
                                    return _.pick(l, "identifier", "name");
                                });
                            });
                    }
                    else {
                        return $q.when(mainCtrl.options.libraries.data);
                    }
                },
                'refresh-delay': 0, // pas de refresh-delay, car on lit les données en cache après le 1er chargement
                'allow-clear': true,
                multiple: true
            },
            status: {
                text: "label",
                placeholder: gettextCatalog.getString("Statut"),
                trackby: "identifier",
                data: getStatus(),
                multiple: true,
                'allow-clear': true
            }
        };


        init();

        /**
         * Initialisation du contrôleur
         */
        function init() {
            config.from = config.from || 10;
        }

        /**
         * Le widget est-il configuré
         * @return {Boolean} retourne vrai si le widget est configuré correctement, faux sinon
         */
        function isConfigured() {
            return angular.isDefined(config.from);
        }

        function getStatus() {
            return _.chain(ProjectSrvc.config.status)
                .pairs()
                .map(function (p) {
                    return {
                        identifier: p[0],
                        label: p[1]
                    };
                })
                .value();
        }
    }
})();
