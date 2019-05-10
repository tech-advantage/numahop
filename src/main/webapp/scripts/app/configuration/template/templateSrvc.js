(function () {
    'use strict';

    angular.module('numaHopApp.service')
        .factory('TemplateSrvc', TemplateSrvc);

    function TemplateSrvc(CONFIGURATION, $http, $q, $resource, gettextCatalog) {
        var service = $resource(CONFIGURATION.numahop.url + 'api/rest/template/:id', { id: '@identifier' });

        service.engines = {
            "Velocity": {
                format: gettextCatalog.getString("Fichier texte"),
                class: "fa fa-file-text-o"
            },
            "XDocReport_ODT": {
                format: gettextCatalog.getString("Fichier OpenDocument Text (odt)"),
                class: "fa fa-file-word-o"
            }
        };
        service.templateNames = [{
            code: "ConditionReport",
            label: gettextCatalog.getString("Constat d'état"),
            engine: "XDocReport_ODT"
        }, {
            code: "ReinitPassword",
            label: gettextCatalog.getString("Courriel de réinitialisation du mot de passe"),
            engine: "Velocity"
        }, {
            code: "UserCreation",
            label: gettextCatalog.getString("Courriel de création d'un utilisateur"),
            engine: "Velocity"
        }];

        /** 
         * Affichage du libellé correspodant au code du template
         * 
         * @param {any} code 
         * @returns 
         */
        function displayTemplateName(code) {
            var found = _.find(service.templateNames, function (name) {
                return name.code === code;
            });
            return found ? found.label : code;
        }

        /**
         * Format attendu pour un template
         * 
         * @param {any} code 
         * @returns 
         */
        function getFormat(code) {
            var found = _.find(service.templateNames, function (name) {
                return name.code === code;
            });
            if (found) {
                return service.engines[found.engine];
            }
        }

        /**
         * Téléversement du fichier de template
         * 
         */
        function uploadTemplate(template, files) {
            var url = CONFIGURATION.numahop.url + "api/rest/template/" + template.identifier;

            var formData = new FormData();
            formData.append("upload", "true");

            _.each(files, function (file) {
                formData.append("file", file);
            });

            var config = {
                transformRequest: angular.identity,
                headers: {
                    'Content-Type': undefined
                }
            };
            var defer = $q.defer();

            $http.post(url, formData, config)
                .success(function (data, status) {
                    defer.resolve(data);
                })
                .error(function (data, status) {
                    defer.reject(data);
                });
            return defer.promise;
        }

        service.getFormat = getFormat;
        service.displayTemplateName = displayTemplateName;
        service.uploadTemplate = uploadTemplate;
        return service;
    }
})();
