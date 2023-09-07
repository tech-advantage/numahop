(function () {
    'use strict';

    angular.module('numaHopApp.service').factory('DtoService', function () {
        var docs = [];
        var libraries = [];

        var addDocs = function (docsSelected) {
            docs = [];
            docs = docsSelected;
        };

        var getDocs = function () {
            return docs;
        };

        var addLibraries = function (librariesSelected) {
            libraries = [];
            libraries = librariesSelected;
        };

        var getLibraries = function () {
            return libraries;
        };

        return {
            addDocs: addDocs,
            addLibraries: addLibraries,
            getDocs: getDocs,
            getLibraries: getLibraries,
        };
    });
})();
