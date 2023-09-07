(function () {
    'use strict';

    // http://lorenzofox3.github.io/smart-table-website/#examples-section
    angular.module('smart-table').directive('stPaginationScroll', [
        '$timeout',
        function (timeout) {
            return {
                require: 'stTable',
                link: function (scope, element, attr, ctrl) {
                    var itemByPage = 20;
                    var pagination = ctrl.tableState().pagination;
                    var lengthThreshold = 50;
                    var timeThreshold = 400;
                    var handler = function () {
                        //call next page
                        ctrl.slice(pagination.start + itemByPage, itemByPage);
                    };
                    var promise = null;
                    var lastRemaining = 9999;

                    var container;
                    // Ajout pour préciser le container scrollable à surveiller
                    if (attr.stPaginationScroll) {
                        container = angular.element(attr.stPaginationScroll);
                    }
                    // Si non précisé, le scroll se fait sur l'élément parent
                    if (container.length === 0) {
                        container = angular.element(element.parent());
                    }

                    container.bind('scroll', function () {
                        var remaining = container[0].scrollHeight - (container[0].clientHeight + container[0].scrollTop);

                        //if we have reached the threshold and we scroll down
                        if (remaining < lengthThreshold && remaining - lastRemaining < 0) {
                            //if there is already a timer running which has no expired yet we have to cancel it and restart the timer
                            if (promise !== null) {
                                timeout.cancel(promise);
                            }
                            promise = timeout(function () {
                                handler();

                                //scroll a bit up
                                // container[0].scrollTop -= 500;

                                promise = null;
                            }, timeThreshold);
                        }
                        lastRemaining = remaining;
                    });
                },
            };
        },
    ]);
})();
