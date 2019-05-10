(function () {
    "use strict";

    /**
     * @ngdoc directive
     * @name semRolesAllowed
     * @priority 1000
     *
     * @description
     * Définit la liste de rôles autorisés à visualiser l'élément courant (à partir de USER_ROLES)
     * sem-roles-allowed="UC80_HAB1_W, UC80_HAB1_W" est l'écriture simplifiée de: ng-if="isAuthorized([userRoles.UC80_HAB1_W, userRoles.UC80_HAB2_W])",
     * avec en plus un contrôle de l'existence des habilitations saisies dans USER_ROLES
     *
     * @example
       <example>
         <div id="controls" sem-roles-allowed=""></div>
       </example>
     */
    angular.module('numaHopApp.directive').directive('semRolesAllowed', function ($compile, $log, USER_ROLES) {
        return {
            restrict: 'A',
            priority: 1000, // ngIf = 600
            terminal: true,
            compile: function compile(tElement, tAttrs, transclude) {
                var rolesAllowed = tAttrs.semRolesAllowed.trim().split(/\s*[,;]\s*/);

                rolesAllowed = _.chain(rolesAllowed)
                    .map(function (role) {
                        if (_.has(USER_ROLES, role)) {
                            return "userRoles." + role;
                        }
                        $log.error("Role \"" + role + "\" does not exists in USER_ROLES; it will be ignored");
                        return null;
                    })
                    .reject(function (element) {
                        return element === null;
                    })
                    .value();

                tElement.attr("ng-if", "::isAuthorized([" + rolesAllowed.join(",") + "])")
                    .removeAttr("sem-roles-allowed");

                return {
                    post: function postLink(scope, iElement, iAttrs, controller) {
                        $compile(iElement)(scope);
                    }
                };
            }
        };
    });
})();