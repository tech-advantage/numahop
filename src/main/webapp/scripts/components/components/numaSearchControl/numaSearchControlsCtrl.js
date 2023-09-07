(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('NumaSearchControlsCtrl', NumaSearchControlsCtrl);

    function NumaSearchControlsCtrl(
        $q,
        CondreportDescPropertySrvc,
        CondreportDescValueSrvc,
        DocUnitBaseService,
        DeliverySrvc,
        DocPropertyTypeSrvc,
        ExportCinesSrvc,
        ExportInternetArchiveSrvc,
        gettextCatalog,
        LotSrvc,
        NumaHopInitializationSrvc,
        ProjectSrvc,
        TrainSrvc,
        WorkflowSrvc
    ) {
        var ctrl = this;
        ctrl.reportValueConfig = reportValueConfig;

        /**
         * Initialisation du contrôleur
         */
        ctrl.$onInit = function () {
            ctrl.loaded = false;
            // config
            ctrl.uiconfig = {
                _cache: {}, // mise en cache des listes déroulante
                boolean: DocUnitBaseService.options.boolean,
                cinesStatus: ExportCinesSrvc.config.status,
                delvMethod: DeliverySrvc.config.method,
                delvPayment: DeliverySrvc.config.payment,
                delvStatus: DeliverySrvc.config.status,
                fileFormat: LotSrvc.config.fileFormat,
                iaStatus: ExportInternetArchiveSrvc.config.status,
                lotStatus: LotSrvc.config.status,
                projectStatus: ProjectSrvc.config.status,
                rights: DocUnitBaseService.options.rights,
                trainStatus: TrainSrvc.config.status,
                workflowState: WorkflowSrvc.getConfigWorkflow(),
                /**
                 * modèle de config pour les listes de valeurs
                 * refresh est défini dans reportValueConfig
                 */
                reportValue: {
                    text: 'label',
                    placeholder: gettextCatalog.getString('Valeur'),
                    trackby: 'identifier',
                    'refresh-delay': 300,
                    'allow-clear': true,
                },
                projects: {
                    text: 'name',
                    placeholder: gettextCatalog.getString('Projet'),
                    trackby: 'identifier',
                    refresh: function ($select) {
                        var searchParams = {
                            page: 0,
                            search: $select.search,
                            active: false,
                        };
                        return ProjectSrvc.search(searchParams);
                    },
                    'refresh-delay': 300,
                    'allow-clear': true,
                },
                lots: {
                    text: 'label',
                    placeholder: gettextCatalog.getString('Lot'),
                    trackby: 'identifier',
                    refresh: function ($select) {
                        var searchParams = {
                            page: 0,
                            search: $select.search,
                            active: false,
                        };
                        return LotSrvc.search(searchParams);
                    },
                    'refresh-delay': 300,
                    'allow-clear': true,
                },
            };
            // config asynchrones
            loadLists().then(function (lists) {
                ctrl.uiconfig = _.extend(ctrl.uiconfig, lists);
                ctrl.loaded = true;
            });
        };

        /**
         * Pré-chargement des valeurs des listes déroulantes
         */
        function loadLists() {
            return $q
                .all([
                    CondreportDescPropertySrvc.query(),
                    DocPropertyTypeSrvc.query(),
                    NumaHopInitializationSrvc.loadCollections(),
                    NumaHopInitializationSrvc.loadLibraries(),
                    NumaHopInitializationSrvc.loadPACS(),
                    NumaHopInitializationSrvc.loadProviders(),
                ])
                .then(function (data) {
                    var lists = {};
                    // Constats d'état: propriétés
                    lists.reportProperties = data[0];
                    // Types de propriété
                    lists.properties = data[1];
                    // IA
                    lists.collectionIA = _.chain(data[2])
                        .map(function (ia) {
                            _.each(ia.collections, function (coll) {
                                coll.name = ia.label + ' / ' + coll.name;
                            });
                            return ia.collections;
                        })
                        .flatten()
                        .sortBy('name')
                        .value();
                    // Biblothèques
                    lists.libraries = data[3];
                    // PAC
                    lists.planClassementPAC = _.chain(data[4])
                        .map(function (pac) {
                            _.each(pac.pacs, function (p) {
                                p.name = pac.label + ' / ' + p.name;
                            });
                            return pac.pacs;
                        })
                        .flatten()
                        .sortBy('name')
                        .value();
                    // Providers
                    lists.provider = _.sortBy(data[5], 'fullName');

                    return lists;
                });
        }

        /**
         * Configuration des listes déroulantes de valeur de descriptions, avec mise en cache des valeurs
         *
         * @param {any} getterIndex fonction renvoyant l'index actuellement sélectionné; permet de rafraichir dynamiquement la liste de valeurs associées
         * @param {any} consumerValues callback appelé une fois la liste de valeurs chargées
         */
        function reportValueConfig(getterIndex, consumerValues) {
            var config = angular.copy(ctrl.uiconfig.reportValue);

            config.refresh = function ($select) {
                // Pas de liste de valeurs à charger
                var propertyId = getterIndex().uiselectKey;
                if (!propertyId) {
                    return $q.when([]);
                }

                // Gestion du cas où la liste est réinitialisée manuellement (search est indéfini)
                if (angular.isUndefined($select.search)) {
                    return $q.when([]);
                }

                // Chargement de la liste en cache
                if (angular.isUndefined(ctrl.uiconfig._cache[propertyId])) {
                    ctrl.uiconfig._cache[propertyId] = CondreportDescValueSrvc.query({ property: propertyId });
                }
                if (consumerValues) {
                    ctrl.uiconfig._cache[propertyId].$promise.then(consumerValues);
                }
                return ctrl.uiconfig._cache[propertyId];
            };
            return config;
        }
    }
})();
