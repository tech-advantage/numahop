(function () {
    'use strict';

    angular
        .module('numaHopApp')
        .config(function (dashboardProvider, gettext) {
            dashboardProvider.widget('projectDetails', {
                /* Recopie de la clé dans le widget, necessaire car le support des catégories lors de l'ajout des widgets
            est mal fait : la fonction qui crée les catégories (createCategories) depuis les widgets "perd" la clé,
            voir le template widget-add.html */
                key: 'projectDetails',
                title: gettext("Détail d'un projet"),
                category: gettext('Projets'),
                description: gettext("Documents contrôlés d'un projet"),
                templateUrl: 'scripts/app/dashboard/widgets/projectDetails/projectDetailsWidget.html',
                controller: 'ProjectDetailsWidgetCtrl',
                controllerAs: 'mainCtrl',
                resolve: /* @ngInject */ {
                    dataProject: function ($q, ProjectSrvc, config) {
                        if (config.project) {
                            return ProjectSrvc.get({ id: config.project.identifier })
                                .$promise // Gestion des erreurs serveur
                                .catch(function (e) {
                                    if (e.status === 403) {
                                        // projet non trouvé => widget non configuré
                                        delete config.project;
                                    } else {
                                        return $q.reject(e);
                                    }
                                });
                        }
                    },
                    dataDocUnits: function (DocUnitSrvc, config) {
                        if (config.project) {
                            return DocUnitSrvc.query({ project: config.project.identifier }).$promise;
                        }
                    },
                },
                edit: {
                    templateUrl: 'scripts/app/dashboard/widgets/projectDetails/projectDetailsEditWidget.html',
                    controller: 'ProjectDetailsEditWidgetCtrl',
                    controllerAs: 'mainCtrl',
                    resolve: /* @ngInject */ {
                        projects: function (NumaHopInitializationSrvc) {
                            return NumaHopInitializationSrvc.loadProjects();
                        },
                    },
                },
                authority: 'W_PROJECT_DETAILS',
            });
        })
        .controller('ProjectDetailsWidgetCtrl', function ($q, config, dataDocUnits, dataProject) {
            var mainCtrl = this;
            mainCtrl.isConfigured = isConfigured;

            /**
             * Un objet pour la barre de progression
             */
            mainCtrl.progressBar = { color: '#ff5555' };

            /**
             * Unités documentaires
             */
            mainCtrl.docunits = dataDocUnits;

            /**
             * Projet en cours de visualisation
             */
            mainCtrl.project = dataProject;

            init();

            /**
             * Construteur
             */
            function init() {
                if (isConfigured() && angular.isDefined(mainCtrl.docunits) && angular.isDefined(mainCtrl.project)) {
                    $q.all([mainCtrl.docunits, mainCtrl.project]).then(function () {
                        // data
                        mainCtrl.maxSize = mainCtrl.docunits.length;
                        mainCtrl.size = 1;
                        if (mainCtrl.size <= (mainCtrl.maxSize * 75) / 100) {
                            mainCtrl.progressBar.color = '#4de74d';
                        } else if (mainCtrl.size < mainCtrl.maxSize) {
                            mainCtrl.progressBar.color = '#ffa255';
                        } else {
                            mainCtrl.progressBar.color = '#ff5555';
                        }
                    });
                }
            }

            /**
             * Le widget est-il configuré
             * @return {Boolean} retourne vrai si le widget est configuré correctement, faux sinon
             */
            function isConfigured() {
                if (config.project) {
                    return true;
                }
                return false;
            }
        });
})();
