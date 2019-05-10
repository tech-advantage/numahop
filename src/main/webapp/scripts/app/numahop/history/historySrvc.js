(function () {
    'use strict';

    angular.module('numaHopApp.service')
        .factory('HistorySrvc', function ($filter, $location, $route, $window, gettext, gettextCatalog) {

            var config = {
                size: 10,
                titleLength: 30
            };
            var history;

            /** Ajouter **/
            function add(label, route, url) {
                // set default values
                if (angular.isUndefined(route)) {
                    route = $route.current.originalPath;
                }
                if (angular.isUndefined(url)) {
                    url = $location.absUrl();
                }
                // clean history
                if (history.length >= config.size) {
                    history.splice(1, history.length - config.size + 1);
                }
                // get existing entry
                var entry = popEntry(route);
                if (angular.isDefined(entry)) {
                    entry.label = label;
                    entry.route = route;
                    entry.url = url;
                }
                // or add a new one
                else {
                    entry = {
                        label: label,
                        route: route,
                        url: url
                    };
                }
                // updates
                history.push(entry);
                setWindowTitle(label);
            }
            function get() {
                return history;
            }
            function getLatest(nb) {
                var result;
                if (angular.isUndefined(nb) || nb >= history.length) {
                    result = get();
                }
                else {
                    result = [history[0]];
                    for (var i = history.length - nb + 1; i < history.length; i++) {
                        result.push(history[i]);
                    }
                }
                return result.length > 1 ? result : [];
            }
            function reinit() {
                history = [{ label: gettext("Historique") }];
            }
            function setWindowTitle(label) {
                var title = gettextCatalog.getString(label);
                title = $filter('characters')(title, config.titleLength);
                $window.document.title = title;
            }
            function popEntry(route) {
                var foundIdx = -1;
                var foundEntry;

                for (var i = 1; i < history.length; i++) {
                    if (angular.isDefined(route) && angular.equals(history[i].route, route)) {
                        foundIdx = i;
                        foundEntry = history[i];
                        break;
                    }
                }
                if (foundIdx >= 0) {
                    history.splice(foundIdx, 1);
                }
                return foundEntry;
            }

            // initialisation
            reinit();

            return {
                add: add,
                get: get,
                getLatest: getLatest,
                reinit: reinit
            };
        });
})();
