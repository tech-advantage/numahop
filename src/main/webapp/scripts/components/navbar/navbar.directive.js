(function () {
    "use strict";

    angular.module('numaHopApp.directive')
        .directive('activeLink', function ($location) {
            return {
                restrict: 'A',
                link: function (scope, element, attrs, controller) {
                    var activePaths = attrs.activeLink.trim().split(/\s*[,;]\s*/);

                    scope.$watch(function() { return $location.path(); },
                        function(path) {
                            for(var i=0; i< activePaths.length ; i++){
                                if (path.includes(activePaths[i])) {
                                    element.addClass("active");
                                    break;
                                } else {
                                    element.removeClass('active');
                                }
                            }
                        });
                }
            };
        });
})();