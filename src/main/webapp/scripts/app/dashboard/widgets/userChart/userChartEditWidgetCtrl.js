(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('UserChartEditWidgetCtrl', UserChartEditWidgetCtrl);

    function UserChartEditWidgetCtrl($q, config, gettextCatalog, LibrarySrvc) {
        var mainCtrl = this;
        mainCtrl.isConfigured = isConfigured;

        mainCtrl.config = {
            dataFormats: [
                {
                    identifier: 'doughnut',
                    label: gettextCatalog.getString('Anneau'),
                },
                {
                    identifier: 'pie',
                    label: gettextCatalog.getString('Camembert'),
                },
                {
                    identifier: 'hbar',
                    label: gettextCatalog.getString('Histogramme'),
                },
                {
                    identifier: 'list',
                    label: gettextCatalog.getString('Liste'),
                },
            ],
            libraries: {
                text: 'name',
                placeholder: gettextCatalog.getString('Bibliothèque'),
                trackby: 'identifier',
                // Chargement avec mise en cache du résultat
                refresh: function ($select) {
                    if (!mainCtrl.config.libraries.data) {
                        mainCtrl.config.libraries.data = LibrarySrvc.query({ dto: true });
                        return mainCtrl.config.libraries.data.$promise.then(function (lib) {
                            return _.map(lib, function (l) {
                                return _.pick(l, 'identifier', 'name');
                            });
                        });
                    } else {
                        return $q.when(mainCtrl.config.libraries.data);
                    }
                },
                'refresh-delay': 0, // pas de refresh-delay, car on lit les données en cache après le 1er chargement
                'allow-clear': true,
                multiple: true,
            },
        };

        /**
         * Le widget est-il configuré
         * @return {Boolean} retourne vrai si le widget est configuré correctement, faux sinon
         */
        function isConfigured() {
            return angular.isDefined(config.format);
        }
    }
})();
