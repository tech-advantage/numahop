(function () {
    'use strict';

    angular.module('numaHopApp.controller').controller('ImportEditCtrl', ImportEditCtrl);

    function ImportEditCtrl(
        $filter,
        $http,
        $httpParamSerializer,
        $q,
        $location,
        $routeParams,
        $scope,
        $timeout,
        codeSrvc,
        CONFIGURATION,
        CSVMappingSrvc,
        ErreurSrvc,
        gettext,
        gettextCatalog,
        ImportSrvc,
        ImportedDocUnitSrvc,
        ImportReportSrvc,
        LotSrvc,
        MappingSrvc,
        MessageSrvc,
        ModalSrvc,
        NumaHopInitializationSrvc,
        Principal,
        ProjectSrvc,
        USER_ROLES,
        WebsocketSrvc,
        Z3950Srvc
    ) {
        var editCtrl = this;
        editCtrl.changeFileFormat = changeFileFormat;
        editCtrl.changeMapping = changeMapping;
        editCtrl.changeProject = changeProject;
        editCtrl.checkOaiPmhQuery = checkOaiPmhQuery;
        editCtrl.checkOaiPmhServer = checkOaiPmhServer;
        editCtrl.code = codeSrvc;
        editCtrl.deleteImportReport = deleteImportReport;
        editCtrl.getMessage = ErreurSrvc.getMessage;
        editCtrl.getPage = getPage;
        editCtrl.importChildren = importChildren;
        editCtrl.hasImportFile = hasImportFile;
        editCtrl.importDocUnits = importDocUnits;
        editCtrl.importFiles = importFiles;
        editCtrl.importOaiPmh = importOaiPmh;
        editCtrl.importZ3950 = importZ3950;
        editCtrl.isImportDisabled = isImportDisabled;
        editCtrl.openImport = openImport;
        editCtrl.setFiles = setFiles;
        editCtrl.updateFilter = updateFilter;
        editCtrl.updateProcess = updateProcess;

        // Définition des listes
        editCtrl.options = {
            dedupProcess: {
                REPLACE: gettextCatalog.getString("Remplacer l'unité documentaire trouvée par celle du fichier"),
                // "ADD": gettextCatalog.getString("Ajouter l'unité documentaire provenant du fichier"),
                IGNORE: gettextCatalog.getString("Ignorer l'unité documentaire provenant du fichier"),
            },
            defaultProcess: {
                ADD: gettextCatalog.getString("Ajouter l'unité documentaire provenant du fichier"),
                IGNORE: gettextCatalog.getString("Ignorer l'unité documentaire provenant du fichier"),
            },
            docStates: {
                data: [{ identifier: 'AVAILABLE' }, { identifier: 'NOT_AVAILABLE' }, { identifier: 'DELETED' }],
                text: function (status) {
                    return codeSrvc['import.DocUnit.State.' + status.identifier] || status.identifier;
                },
                placeholder: gettextCatalog.getString("Statut de l'unité documentaire"),
                'allow-clear': true,
                multiple: true,
            },
            format: {
                data: {
                    DC: gettextCatalog.getString('Dublin Core'),
                    // "DCQ": gettextCatalog.getString("Dublin Core qualifié"),
                    EAD: gettextCatalog.getString('EAD'),
                    OAIPMH: gettextCatalog.getString('OAI-PMH'),
                    MARC: gettextCatalog.getString('MARC: ISO-2709'),
                    MARCJSON: gettextCatalog.getString('MARC: JSON'),
                    MARCXML: gettextCatalog.getString('MARC: XML'),
                    CSV: gettextCatalog.getString('CSV'),
                },
                disabled: isFormatDisabled,
            },
            marc_encoding: {
                _NULL_: '',
                ANSEL: gettextCatalog.getString('ANSEL (MARC-8)'),
                ISO_5426: gettextCatalog.getString('ISO-5426'),
                ISO_6937: gettextCatalog.getString('ISO-6937'),
                ISO_8859_1: gettextCatalog.getString('ISO-8859-1'),
                UTF_8: gettextCatalog.getString('UTF-8'),
            },
            dc_encoding: {
                DC_OAI_MAPPING: gettextCatalog.getString('OAI_DC'),
                DC_RDF_MAPPING: gettextCatalog.getString('RDF'),
            },
            mappingMARC: {
                text: function (mapping) {
                    var text = mapping.label;
                    if (editCtrl.isSuperAdmin) {
                        if (mapping.library) {
                            text += ' (' + mapping.library.name + ')';
                        } else {
                            text += ' ()';
                        }
                    }
                    return text;
                },
                placeholder: gettextCatalog.getString('Mapping MARC'),
                'allow-clear': false,
            },
            mappingEAD: {
                text: function (mapping) {
                    var text = mapping.label;
                    if (editCtrl.isSuperAdmin) {
                        if (mapping.library) {
                            text += ' (' + mapping.library.name + ')';
                        } else {
                            text += ' ()';
                        }
                    }
                    return text;
                },
                placeholder: gettextCatalog.getString('Mapping EAD'),
                'allow-clear': false,
            },
            mappingCSV: {
                text: function (mapping) {
                    var text = mapping.label;
                    if (editCtrl.isSuperAdmin) {
                        if (mapping.library) {
                            text += ' (' + mapping.library.name + ')';
                        } else {
                            text += ' ()';
                        }
                    }
                    return text;
                },
                placeholder: gettextCatalog.getString('Mapping CSV'),
                'allow-clear': false,
            },
            lots: {
                text: 'label',
                placeholder: gettextCatalog.getString('Lot'),
                trackby: 'identifier',
                refresh: function ($select) {
                    editCtrl.lotsSelect = $select;
                    // Gestion du cas où la liste est réinitialisée manuellement (search est indéfini)
                    if (angular.isUndefined($select.search)) {
                        return $q.when([]);
                    }
                    var searchParams = {
                        page: 0,
                        search: $select.search,
                        active: true,
                        statuses: 'CREATED',
                    };
                    if (editCtrl.report.project) {
                        searchParams['projects'] = editCtrl.report.project.identifier;
                    }
                    return LotSrvc.search(searchParams);
                },
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
                        active: true,
                    };
                    return ProjectSrvc.search(searchParams);
                },
                'refresh-delay': 300,
                'allow-clear': true,
            },
            status: {
                PENDING: gettextCatalog.getString('En attente'),
                PRE_IMPORTING: gettextCatalog.getString('Pré-import des unités documentaires'),
                DEDUPLICATING: gettextCatalog.getString('Recherche de doublons'),
                USER_VALIDATION: gettextCatalog.getString('Validation utilisateur'),
                IMPORTING: gettextCatalog.getString('Import des unités documentaires en cours'),
                INDEXING: gettextCatalog.getString('Indexation des unités documentaires en cours'),
                COMPLETED: gettextCatalog.getString('Terminé'),
                FAILED: gettextCatalog.getString('Échec'),
            },
            type: {
                // 1 notice dans l'import = 1 notice PGCN
                SIMPLE: {
                    code: 'SIMPLE',
                    label: gettextCatalog.getString('Simple'),
                },
                // n notices dans l'import = 1 notice PGCN
                SIMPLE_MULTI_NOTICE: {
                    code: 'SIMPLE_MULTI_NOTICE',
                    label: gettextCatalog.getString('Hiérarchie de composants dans une seule notice'),
                },
                // n notices dans l'import = n notice PGCN avec relation parent / enfant
                SIMPLE_MULTI_MULTI_NOTICE: {
                    code: 'SIMPLE_MULTI_MULTI_NOTICE',
                    label: gettextCatalog.getString('Hiérarchie de composants dans des notices mères / filles'),
                },
                // 1 notice dans l'import = n notices PGCN
                HIERARCHY_IN_SINGLE_NOTICE: {
                    code: 'HIERARCHY_IN_SINGLE_NOTICE',
                    label: gettextCatalog.getString("Parent et enfants issus d'une seule notice"),
                },
                // n notices dans 2 imports = n notices PGCN
                HIERARCHY_IN_MULTIPLE_IMPORT: {
                    code: 'HIERARCHY_IN_MULTIPLE_IMPORT',
                    label: gettextCatalog.getString("Parents et enfants issus d'imports différents"),
                },
            },
        };
        // Statut en attente d'un résultat de la part du serveur
        editCtrl.runningStatus = {
            PENDING: true,
            PRE_IMPORTING: true,
            DEDUPLICATING: true,
            USER_VALIDATION: false,
            IMPORTING: true,
            INDEXING: true,
            COMPLETED: false,
            FAILED: false,
        };
        // Options de pagination
        editCtrl.pagination = {
            currentPage: 0,
            itemsByPage: 15,
            displayedPages: 0,
        };
        // Options d'import
        editCtrl.import = {
            validation: true,
            dedup: true,
            dedupProcess: 'REPLACE',
            defaultProcess: 'ADD',
        };
        editCtrl.filter = {}; // Filtre saisi par l'utilisateur dans l'interface
        var currentFilter = {}; // Filtre appliqué pour la recherche
        editCtrl.loaded = false;
        editCtrl.errors = { fileFormat: [], mapping: [], mappingChildren: [] }; // Messages d'erreurs affichés sous les champs
        editCtrl.cache = { hasImportFiles: {} }; // Cache (existence du fichier d'import)

        init();

        /** Initialisation du contrôleur */
        function init() {
            var id = $routeParams.id;
            var format = $routeParams.format;
            var encoding = $routeParams.encoding;
            var source = $routeParams.source; // source de l'import (Z39.50)
            var lib = Principal.library();

            editCtrl.isSuperAdmin = Principal.isInRole(USER_ROLES.SUPER_ADMIN);
            editCtrl.library = lib ? { identifier: lib } : null;
            editCtrl.parent = $routeParams.parent; // identifiant de l'import parent
            editCtrl.sourceSrvc = !id && angular.isDefined(source);
            editCtrl.oaipmh = !id && source === 'oaipmh';
            editCtrl.z3950 = !id && source === 'z3950';

            // Désabonnement à la destruction du scope
            $scope.$on('$destroy', unsubscribeImport);

            // Chargement des données: bibliothèques, mappings, import
            $q.all([NumaHopInitializationSrvc.loadLibraries()]).then(function (data) {
                editCtrl.options.libraries = data[0];
                // Chargement
                var promise1 = loadMappings();
                var promise2 = loadCSVMappings();

                // Édition
                if (angular.isDefined(id)) {
                    loadImportReport(id);
                }
                // Création
                else {
                    $q.all([promise1, promise2]).then(function () {
                        createImportReport(format, encoding);
                    });
                }
            });

            // Rafraichissement des messages d'erreurs
            $scope.$watch('editCtrl.report.fileFormat', function (value) {
                watchMandatory(value, editCtrl.errors.fileFormat, 'IMPORT_FORMAT_MANDATORY');
            });
            $scope.$watch('editCtrl.report.mapping', function (value) {
                watchMandatory(value, editCtrl.errors.mapping, 'IMPORT_MAPPING_MANDATORY');
            });
            $scope.$watch('editCtrl.report.mappingChildren', function (value) {
                watchMandatory(value, editCtrl.errors.mappingChildren, 'IMPORT_MAPPING_MANDATORY');
            });
        }

        /**
         * Réinitialise la liste d'erreurs, et ajoute l'erreur mandatoryCode si la watchedValue n'est pas définie
         *
         * @param {any} watchedValue
         * @param {any} errorList
         * @param {any} mandatoryCode
         */
        function watchMandatory(watchedValue, errorList, mandatoryCode) {
            if (!editCtrl.isCreation) {
                return;
            }
            errorList.splice(0, errorList.length);

            if (!watchedValue) {
                errorList.push({ code: mandatoryCode });
            }
        }

        /** Création d'un nouvel import */
        function createImportReport(format, encoding) {
            editCtrl.loaded = false;
            editCtrl.isCreation = true;
            delete editCtrl.files;

            editCtrl.report = new ImportReportSrvc();
            editCtrl.report.type = 'SIMPLE';

            if (!editCtrl.sourceSrvc) {
                if (!editCtrl.parent) {
                    editCtrl.report.type = ImportSrvc.getParam('type', 'SIMPLE');
                }
                editCtrl.report.fileFormat = format ? format.toUpperCase() : ImportSrvc.getParam('format', 'MARC');
                editCtrl.report.dataEncoding = encoding ? encoding.toUpperCase() : ImportSrvc.getParam('encoding', 'ISO_5426');
                editCtrl.report.joinExpression = ImportSrvc.getParam('join', '');

                editCtrl.changeFileFormat(editCtrl.report.fileFormat, true);

                if (editCtrl.report.fileFormat !== 'DC') {
                    selectMapping(editCtrl.report.fileFormat, ImportSrvc.getParam('mapping'), ImportSrvc.getParam('mappingChildren'));
                    editCtrl.changeMapping(editCtrl.report.mapping);
                } else {
                    editCtrl.report.additionnalMapping = ImportSrvc.getParam('mapping', '');
                    editCtrl.library = ImportSrvc.getParam('library');
                }
            } else if (editCtrl.oaipmh) {
                editCtrl.report.baseUrl = ImportSrvc.getParam('baseUrl', editCtrl.report.baseUrl);
                editCtrl.report.from = ImportSrvc.getParam('from', editCtrl.report.from);
                editCtrl.report.to = ImportSrvc.getParam('to', editCtrl.report.to);
                editCtrl.report.set = ImportSrvc.getParam('set', editCtrl.report.set);
            }

            editCtrl.report.project = ImportSrvc.getParam('project', editCtrl.report.project);
            editCtrl.report.lot = ImportSrvc.getParam('lot', editCtrl.report.lot);
            editCtrl.import.validation = ImportSrvc.getParam('validation', editCtrl.import.validation);
            editCtrl.import.dedup = ImportSrvc.getParam('dedup', editCtrl.import.dedup);
            editCtrl.import.dedupProcess = ImportSrvc.getParam('dedupProcess', editCtrl.import.dedupProcess);
            editCtrl.report.archivable = ImportSrvc.getParam('archivable', editCtrl.report.archivable);
            editCtrl.report.distributable = ImportSrvc.getParam('archivable', editCtrl.report.distributable);

            clearPagination();

            editCtrl.accTitle = true;
            editCtrl.loaded = true;
            // Affichage pour un temps limité à l'ouverture
            MessageSrvc.initPanel();
        }

        /** Chargement d'un import */
        function loadImportReport(identifier) {
            editCtrl.loaded = false;
            editCtrl.isCreation = false;

            editCtrl.report = ImportReportSrvc.get({
                id: identifier,
            });
            return editCtrl.report.$promise
                .then(function (report) {
                    clearPagination();

                    // Initialisation des champs
                    editCtrl.library = report.library;

                    // Synchro de editCtrl.files avec le rapport reçu
                    editCtrl.files = _.map(report.files, function (file) {
                        return {
                            name: file.originalFilename,
                            size: file.fileSize,
                        };
                    });
                    // Suivi de la progression de l'import
                    if (editCtrl.runningStatus[editCtrl.report.status]) {
                        subscribeImport(editCtrl.report);
                    }
                    // Filtrage des éléments importés
                    if (editCtrl.report.status === 'COMPLETED') {
                        editCtrl.filter.docStates = currentFilter.docStates = [{ identifier: 'AVAILABLE' }];
                    }
                    // Traduction du statut dans le message
                    if (editCtrl.report.message) {
                        _.chain(editCtrl.options.status)
                            .pairs()
                            .each(function (p) {
                                var code = p[0],
                                    trad = p[1];
                                if (trad) {
                                    editCtrl.report.message = editCtrl.report.message.replace(new RegExp('\\b' + code + '\\b', 'g'), '<b>"' + trad + '"</b>');
                                }
                            });
                    }
                    return report;
                })
                .then(function () {
                    editCtrl.accTitle = false;
                    editCtrl.loaded = true;
                    // Affichage pour un temps limité à l'ouverture
                    MessageSrvc.initPanel();
                });
        }

        /** Suppression de l'import */
        function deleteImportReport(report) {
            var reportMsg = {
                name: report.files[0].originalFilename,
                start: moment(report.start).format('L HH:MM'),
            };

            ModalSrvc.confirmDeletion(gettextCatalog.getString("l'import {{name}} du {{start}}", reportMsg))
                .then(function () {
                    return ImportReportSrvc.delete({}, report).$promise;
                })
                .then(function (data) {
                    MessageSrvc.addSuccess(gettext("L'import {{name}} du {{start}} a été correctement supprimé"), reportMsg);
                    // Synchro du scope parent
                    $scope.$emit('editCtrl:synchroDeletion', data);
                });
        }

        /** Chargement de la liste de mappings */
        function loadMappings() {
            var result = editCtrl.isSuperAdmin ? MappingSrvc.query({ usable: true }) : MappingSrvc.query({ usable: true, library: editCtrl.library.identifier });

            return result.$promise.then(function (data) {
                editCtrl.options.mappingEAD.data = getMappings(data, 'EAD');
                editCtrl.options.mappingMARC.data = getMappings(data, 'MARC');
                return data;
            });
        }

        /** Renvoie les mappings triés d'un type donné */
        function getMappings(mappings, type) {
            return _.chain(mappings)
                .filter(function (d) {
                    return d.type === type;
                })
                .sortBy('label')
                .value();
        }

        function loadCSVMappings() {
            var result = editCtrl.isSuperAdmin ? CSVMappingSrvc.query({ usable: true }) : CSVMappingSrvc.query({ usable: true, library: editCtrl.library.identifier });

            editCtrl.options.mappingCSV.data = result;
            return result.$promise.then(function (data) {
                return _.sortBy(data, 'label');
            });
        }

        /**
         * Sélection du mapping
         */
        function selectMapping(format, defaultMapping, defaultMappingChildren) {
            MessageSrvc.clearMessages(MessageSrvc.level.WARN);
            delete editCtrl.report.mapping;
            delete editCtrl.report.mappingChildren;

            var currentMappings;

            if (format === 'MARC' || format === 'MARCJSON' || format === 'MARCXML' || editCtrl.z3950) {
                currentMappings = editCtrl.options.mappingMARC.data;
            } else if (format === 'EAD') {
                currentMappings = editCtrl.options.mappingEAD.data;
            } else if (format === 'CSV') {
                currentMappings = editCtrl.options.mappingCSV.data;
            }

            if (angular.isDefined(currentMappings)) {
                if (currentMappings.length === 0) {
                    MessageSrvc.addWarn(gettextCatalog.getString("Aucun mapping n'est défini pour le format sélectionné"));
                } else {
                    // Mapping
                    if (defaultMapping) {
                        editCtrl.report.mapping = _.find(currentMappings, function (m) {
                            return m.identifier === defaultMapping;
                        });
                    }
                    if (!editCtrl.report.mapping) {
                        editCtrl.report.mapping = currentMappings[0];
                    }
                    // Mapping pério
                    if (defaultMappingChildren) {
                        editCtrl.report.mappingChildren = _.find(currentMappings, function (m) {
                            return m.identifier === defaultMappingChildren;
                        });
                    }
                    if (!editCtrl.report.mappingChildren) {
                        editCtrl.report.mappingChildren = currentMappings[0];
                    }
                }
            }
            // onchange mapping
            editCtrl.changeMapping(editCtrl.report.mapping);
        }

        /** Sélection des fichiers à uploader */
        function setFiles(element) {
            if (element.files.length > 0) {
                $scope.$apply(function () {
                    // Turn the FileList object into an Array
                    editCtrl.files = _.map(element.files, angular.identity);
                });
            }
        }

        /**
         * Changement de format
         *
         * @param {any} format
         */
        function changeFileFormat(format, dontChangeMapping) {
            // Sélection de l'encodage
            if (format === 'DC') {
                editCtrl.report.dataEncoding = 'DC_OAI_MAPPING';
            }
            // Sélection multi-fichiers
            var element = angular.element('input#importFiles0');
            if (format === 'MARC' || format === 'MARCJSON' || format === 'MARCXML') {
                element.attr('multiple', true);
            } else {
                element.removeAttr('multiple');
            }
            // Sélection du mapping
            if (!dontChangeMapping) {
                selectMapping(format);
            }
        }

        /**
         * Changement de mapping principal
         *
         * @param {any} mapping
         */
        function changeMapping(mapping) {
            editCtrl.report.joinExpression = mapping ? mapping.joinExpression : '';
        }

        /**
         * Changement de projet
         */
        function changeProject() {
            delete editCtrl.report.lot;

            if (editCtrl.lotsSelect) {
                delete editCtrl.lotsSelect.selected;
                delete editCtrl.lotsSelect.search;
                editCtrl.lotsSelect.items = [];
                // editCtrl.lotsSelect.activate(false, true);
                editCtrl.lotsSelect.activeIndex = 0;
                editCtrl.lotsSelect.open = false;
            }
        }

        /** Désactivation d'un format */
        function isFormatDisabled(format) {
            if (editCtrl.report.type === 'HIERARCHY_IN_SINGLE_NOTICE') {
                return format !== 'MARC' && format !== 'MARCJSON' && format !== 'MARCXML';
            } else if (editCtrl.report.type === 'SIMPLE_MULTI_NOTICE' || editCtrl.report.type === 'SIMPLE_MULTI_MULTI_NOTICE') {
                return format !== 'EAD';
            } else if (editCtrl.parent || editCtrl.report.type === 'HIERARCHY_IN_MULTIPLE_IMPORT') {
                return format !== 'MARC' && format !== 'MARCJSON' && format !== 'MARCXML' && format !== 'CSV';
            }
            return false;
        }

        /** Activation du bouton d'import */
        function isImportDisabled() {
            return !(
                (editCtrl.report.mapping || editCtrl.report.fileFormat === 'DC') && // mapping renseigné
                (editCtrl.report.type !== 'SIMPLE_MULTI_NOTICE' || editCtrl.report.mappingChildren) && // mapping pério renseigné
                (editCtrl.report.type !== 'SIMPLE_MULTI_MULTI_NOTICE' || editCtrl.report.mappingChildren) && // mapping pério renseigné
                (editCtrl.report.type !== 'HIERARCHY_IN_SINGLE_NOTICE' || editCtrl.report.mappingChildren) && // mapping pério renseigné
                (editCtrl.z3950 || (editCtrl.report.fileFormat && editCtrl.files && editCtrl.files.length))
            ); // format et fichier renseigné
        }

        /** Démarrage de l'import */
        function importFiles() {
            var url = CONFIGURATION.numahop.url + 'api/rest/import';

            // Paramètres du formulaire
            var formData = new FormData();
            ImportSrvc.clearParams();

            // Type
            setFormValue('type', editCtrl.report.type, formData);
            // Format
            setFormValue('format', editCtrl.report.fileFormat.toUpperCase(), formData);
            // Encodage
            if (editCtrl.report.fileFormat === 'MARC' && editCtrl.report.dataEncoding !== '_NULL_') {
                setFormValue('encoding', editCtrl.report.dataEncoding, formData);
            }
            // Bibliothèque
            if (editCtrl.report.fileFormat === 'DC') {
                formData.append('library', editCtrl.library.identifier);
                ImportSrvc.setParam('library', editCtrl.library);
            } else {
                setFormValue('library', editCtrl.report.mapping.library.identifier, formData);
            }
            // Mapping
            switch (editCtrl.report.type) {
                case 'SIMPLE_MULTI_NOTICE':
                    formData.append('prop_order', 'BY_CREATION'); // Ordre des propriétés par date de création
                    setFormValue('mapping', editCtrl.report.mapping.identifier, formData);
                    setFormValue('mappingChildren', editCtrl.report.mappingChildren.identifier, formData);
                    break;
                case 'SIMPLE_MULTI_MULTI_NOTICE':
                    formData.append('prop_order', 'BY_CREATION'); // Ordre des propriétés par date de création
                    setFormValue('mapping', editCtrl.report.mapping.identifier, formData);
                    setFormValue('mappingChildren', editCtrl.report.mappingChildren.identifier, formData);
                    break;
                case 'HIERARCHY_IN_SINGLE_NOTICE':
                    setFormValue('mapping', editCtrl.report.mapping.identifier, formData);
                    setFormValue('mappingChildren', editCtrl.report.mappingChildren.identifier, formData);
                    break;
                case 'HIERARCHY_IN_MULTIPLE_IMPORT':
                    setFormValue('mapping', editCtrl.report.mapping.identifier, formData);
                    setFormValue('join', editCtrl.report.joinExpression, formData); // join côté parent
                    break;
                case 'SIMPLE':
                default:
                    if (editCtrl.report.fileFormat !== 'DC') {
                        setFormValue('mapping', editCtrl.report.mapping.identifier, formData);
                    } else {
                        setFormValue('mapping', editCtrl.report.additionnalMapping, formData);
                    }
                    if (editCtrl.report.joinExpression) {
                        setFormValue('join', editCtrl.report.joinExpression, formData); // join côté enfant
                    }
                    break;
            }
            // Import parent
            if (editCtrl.parent) {
                formData.append('parent', editCtrl.parent);
            }
            // Projet
            if (editCtrl.report.project) {
                formData.append('project', editCtrl.report.project.identifier);
                ImportSrvc.setParam('project', editCtrl.report.project);
            }
            if (editCtrl.report.archivable) {
                formData.append('archivable', editCtrl.report.archivable);
                ImportSrvc.setParam('archivable', editCtrl.report.archivable);
            }
            if (editCtrl.report.distributable) {
                formData.append('distributable', editCtrl.report.distributable);
                ImportSrvc.setParam('distributable', editCtrl.report.distributable);
            }

            // Lot
            if (editCtrl.report.lot) {
                formData.append('lot', editCtrl.report.lot.identifier);
                ImportSrvc.setParam('lot', editCtrl.report.lot);
            }
            // Paramètres d'exécution
            setFormValue('validation', !!editCtrl.import.validation, formData); // étape de validation par l'utilisateur
            // Dédoublonnage des unités documentaires importées
            setFormValue('dedup', !!editCtrl.import.dedup, formData);
            // Traitement par défaut des doublons
            if (editCtrl.import.dedup) {
                setFormValue('dedupProcess', editCtrl.import.dedupProcess, formData);
            }

            // Emplacement du/des fichiers à importer
            _.each(editCtrl.files, function (file) {
                formData.append('file', file);
            });

            var config = {
                transformRequest: angular.identity,
                headers: {
                    'Content-Type': undefined,
                },
            };
            $http
                .post(url, formData, config)
                .success(function (data) {
                    MessageSrvc.addSuccess(gettext("L'import est en cours d'exécution"));
                    $location.search({ id: data.identifier }); // màj url
                    // Synchro du scope parent
                    $scope.$emit('editCtrl:synchro', data);

                    $timeout(function () {
                        loadImportReport(data.identifier);
                    });
                })
                .error(function (data) {
                    MessageSrvc.addError(gettext('Échec lors du téléversement du fichier'));
                    $timeout(function () {
                        loadImportReport(data.identifier);
                    });
                });
        }

        /**
         * Définition d'un valeur dans le formulaire de lancement de l'import
         *
         * @param {any} param
         * @param {any} value
         * @param {any} formData
         */
        function setFormValue(param, value, formData) {
            if (value) {
                formData.append(param, value);
            }
            ImportSrvc.setParam(param, value);
        }

        /** Recherche Z39.50 */
        function importZ3950() {
            return ModalSrvc.open('scripts/app/document/z3950/modalZ3950Search.html', {}, 'lg', 'ModalZ3950SearchCtrl').then(function (z3950record) {
                var params = {};
                ImportSrvc.clearParams();
                // Bibliothèque
                params.library = editCtrl.report.mapping.library.identifier;
                ImportSrvc.setParam('library', params.library);
                // Mapping
                params.mapping = editCtrl.report.mapping.identifier;
                ImportSrvc.setParam('mapping', params.mapping);
                // Projet
                if (editCtrl.report.project) {
                    params.project = editCtrl.report.project.identifier;
                    ImportSrvc.setParam('project', editCtrl.report.project);
                }
                // Lot
                if (editCtrl.report.lot) {
                    params.lot = editCtrl.report.lot.identifier;
                    ImportSrvc.setParam('lot', editCtrl.report.lot);
                }
                // Paramètres d'exécution
                params.validation = !!editCtrl.import.validation; // étape de validation par l'utilisateur
                ImportSrvc.setParam('validation', params.validation);
                // dédoublonnage des un.doc. importées
                params.dedup = !!editCtrl.import.dedup;
                ImportSrvc.setParam('dedup', params.dedup);
                if (editCtrl.import.dedup) {
                    params.dedupProcess = editCtrl.import.dedupProcess;
                    ImportSrvc.setParam('dedupProcess', params.dedupProcess);
                }
                // Import de la notice
                Z3950Srvc.import(params, z3950record).$promise.then(function (data) {
                    MessageSrvc.addSuccess(gettext("L'import est en cours d'exécution"));
                    $location.search({ id: data.identifier }); // màj url

                    // Synchro du scope parent
                    $scope.$emit('editCtrl:synchro', data);

                    $timeout(function () {
                        loadImportReport(data.identifier);
                    });
                });
            });
        }

        /** Import des unités documentaires après validation par l'utilisateur */
        function importDocUnits(report) {
            var params = {
                dedupProcess: editCtrl.import.dedupProcess,
                defaultProcess: editCtrl.import.defaultProcess,
            };
            ImportSrvc.importReport(params, report).$promise.then(function (data) {
                MessageSrvc.addSuccess(gettext("L'import est en cours d'exécution"));
                // Synchro du scope parent
                $scope.$emit('editCtrl:synchro', data);
                loadImportReport(data.identifier);
            });
        }

        /** WebSocket du suivi de la progression de l'import */
        function subscribeImport(report) {
            // Définition du callback sur un appel du websocket
            var onWebsocketResp = function (data) {
                // Annulation de la vérification du statut
                if (angular.isDefined(editCtrl.subscrCheckPromise)) {
                    $timeout.cancel(editCtrl.subscrCheckPromise);
                    delete editCtrl.subscrCheckPromise;
                }
                // Désabonnement quand le traitement est terminé
                if (!editCtrl.runningStatus[data.status] && editCtrl.subscriber) {
                    unsubscribeImport();
                }
                // Mise à jour du statut
                return updateProgress(data, report);
            };

            // Websocket de suivi
            var subscriberPromise = WebsocketSrvc.subscribeObject(report.identifier, onWebsocketResp);

            // Une fois la connexion établie
            subscriberPromise
                // initialisation de l'abonnement en cours
                .then(function (s) {
                    editCtrl.subscriber = s;
                })
                .then(function () {
                    // Mise à jour du rapport et désabonnement au websocket après 5s sans réponse
                    // (cas où l'import se termine avant la finalisation de l'abonnement au websocket)
                    if (editCtrl.runningStatus[report.status]) {
                        // Vérification du statut de l'import si pas de contact du serveur dans les 5s
                        editCtrl.subscrCheckPromise = $timeout(function () {
                            ImportReportSrvc.getStatus({ id: report.identifier }).$promise.then(function (data) {
                                // Désabonnement
                                if (!editCtrl.runningStatus[data.status]) {
                                    unsubscribeImport();
                                }
                                // Mise à jour du statut
                                updateProgress(data, report);
                            });
                        }, 5000);
                    }
                });
        }

        /** Désabonnement au websocket de suivi de l'import */
        function unsubscribeImport() {
            if (editCtrl.subscriber) {
                WebsocketSrvc.unsubscribeObject(editCtrl.subscriber);
                delete editCtrl.subscriber;
            }
        }

        /** Suivi de la progression de l'import */
        function updateProgress(data, report) {
            // Il s'agit bien du rapport en cours
            if (data.identifier === report.identifier) {
                $timeout(function () {
                    var forceReload = !editCtrl.runningStatus[data.status];
                    var updated = report.status !== data.status;

                    if (editCtrl.runningStatus[report.status]) {
                        if (!report.start && angular.isDefined(data.start)) {
                            report.start = data.start;
                        }
                        if (angular.isDefined(data.end)) {
                            report.end = data.end;
                        }
                        if (updated) {
                            delete report._nbProcessed;
                        }
                        if (angular.isDefined(data.processed) && data.processed > (report._nbProcessed || 0)) {
                            report._nbProcessed = data.processed;
                        }
                        report.nbImp = data.nbImp;
                        report.status = data.status;
                    }
                    if (forceReload || updated) {
                        // Synchro du scope parent
                        $scope.$emit('editCtrl:synchro', report);
                        if (forceReload) {
                            loadImportReport(data.identifier);
                        }
                    }
                });
            }
        }

        /**
         * Vérifie l'existence d'un fichier d'import
         *
         *
         * @param {any} report
         * @returns
         */
        function hasImportFile(report, file) {
            if (report.identifier) {
                if (angular.isUndefined(editCtrl.cache.hasImportFiles[report.identifier])) {
                    editCtrl.cache.hasImportFiles[report.identifier] = ImportReportSrvc.get({ id: report.identifier, hasfile: true }).$promise.then(function (data) {
                        editCtrl.cache.hasImportFiles[report.identifier] = data;
                        return data;
                    });
                }
                return editCtrl.cache.hasImportFiles[report.identifier][file];
            }
            return false;
        }

        /** Chargement d'une page d'unités documentaires importées */
        function getPage(tableState) {
            // Dernière page atteinte
            if (editCtrl.pagination.isLastPage) {
                return;
            }

            var tblPagination = tableState.pagination;
            // This is NOT the page number, but the index of item in the list that you want to use to display the table.
            var start = tblPagination.start || 0;
            // Number of entries showed per page.
            var number = tblPagination.number || editCtrl.pagination.itemsByPage;
            editCtrl.pagination.currentPage = (start - (start % number)) / number;

            // Chargement des données importées
            var params = {
                report: editCtrl.report.identifier,
                page: editCtrl.pagination.currentPage,
                size: editCtrl.pagination.itemsByPage,
                errors: !!editCtrl.filter.errors,
                duplicates: !!editCtrl.filter.duplicates,
            };
            if (currentFilter.docStates) {
                params.state = _.pluck(currentFilter.docStates, 'identifier');
            }
            editCtrl.pagination.loading = true;
            ImportedDocUnitSrvc.get(params).$promise.then(function (page) {
                editCtrl.pagination.displayedPages = page.totalPages;
                editCtrl.pagination.isLastPage = !!page.last;
                tblPagination.numberOfPages = page.totalPages;
                editCtrl.report.totalDocs = page.totalElements;

                if (start === 0) {
                    editCtrl.report.content = enrichContent(page.content);
                } else {
                    var lastElt = editCtrl.report.content[editCtrl.report.content.length - 1];
                    editCtrl.report.content = editCtrl.report.content.concat(enrichContent(page.content, lastElt));
                }
                editCtrl.pagination.loading = false;
            });
        }

        /**
         * Ajoute les parents dans le cas de l'import séparé d'enfants
         *
         * @param {any} content liste d'ud importées, triées par parent
         * @returns
         */
        function enrichContent(content, lastElt) {
            var i, doc;
            // On a un rapport parent, qui contient les ud parentes
            if (editCtrl.report.parentReport) {
                i = 0;
                var lastParent;

                while (i < content.length) {
                    doc = content[i];

                    // l'ud parente n'est pas affichée => on la rajoute (pour l'affichage)
                    if (!lastParent || lastParent !== doc.parentDocUnit) {
                        var parent = {
                            docUnit: {
                                identifier: doc.parentDocUnit,
                                forcelink: true,
                            },
                            docUnitLabel: doc.parentDocUnitLabel,
                            docUnitPgcnId: doc.parentDocUnitPgcnId,
                        };
                        content.splice(i, 0, parent);
                        i++;
                    }

                    lastParent = doc.parentDocUnit;
                    i++;
                }
            }
            // les ud sont regroupées par groupCode
            else {
                i = 0;
                var lastGroup;
                if (lastElt && lastElt.groupCode) {
                    lastGroup = lastElt.groupCode;
                }
                while (i < content.length) {
                    doc = content[i];
                    // Marquage de changement de groupement
                    doc._first = doc.groupCode ? !lastGroup || lastGroup !== doc.groupCode : !!lastGroup;

                    lastGroup = doc.groupCode;
                    i++;
                }
            }
            return content;
        }

        /** Réinitialistion de la pagination */
        function clearPagination() {
            editCtrl.pagination = {
                currentPage: 0,
                itemsByPage: 15,
                displayedPages: 0,
            };
            if (editCtrl.report) {
                delete editCtrl.report.content;
            }
        }

        /** Mise à jour du filtre courant et rafraichissement des unités documentaires*/
        function updateFilter(newFilter) {
            currentFilter = angular.copy(newFilter);
            clearPagination();
            getPage({ pagination: editCtrl.pagination });
        }

        /** Mise à jour de l'action à effectuer */
        function updateProcess(impDoc) {
            var process = impDoc.process ? impDoc.process : false;
            ImportedDocUnitSrvc.save({ process: process }, { identifier: impDoc.identifier });
        }

        /**
         * Création d'un rapport dont celui-ci est parent
         *
         * @param {any} report
         */
        function importChildren(report) {
            // Appel du contrôleur parent
            $scope.$emit('editCtrl:create', report);
        }

        /**
         * Ouverture d'un import particulier
         *
         * @param {any} report
         */
        function openImport(report) {
            $scope.$emit('editCtrl:synchro', report);
            $location.search({ id: report.identifier });
        }

        /**
         * Appel "ListIdentifiers" sur le serveur OAI-PMH et récupération du nombre de résultats
         */
        function checkOaiPmhQuery() {
            editCtrl._oaiPmhLoading = true;

            var params = {
                listIdentifiers: true,
                baseUrl: editCtrl.report.baseUrl,
            };
            if (editCtrl.report.from) {
                params.from = editCtrl.report.from;
            }
            if (editCtrl.report.to) {
                params.to = editCtrl.report.to;
            }
            if (editCtrl.report.set) {
                params.set = editCtrl.report.set;
            }
            $http
                .get('api/rest/oaipmh?' + $httpParamSerializer(params))
                .then(function (response) {
                    ModalSrvc.displayData({
                        title: gettextCatalog.getString('Serveur OAI-PMH'),
                        data: gettextCatalog.getString('Cette requête renvoie {{count}} résultat(s)', { count: $filter('number')(response.data.completeListSize) }),
                    });
                })
                .finally(function () {
                    editCtrl._oaiPmhLoading = false;
                });
        }

        /**
         * Appel "Identify" sur le serveur OAI-PMH
         */
        function checkOaiPmhServer() {
            editCtrl._oaiPmhLoading = true;

            var params = {
                identify: true,
                baseUrl: editCtrl.report.baseUrl,
            };
            $http
                .get('api/rest/oaipmh?' + $httpParamSerializer(params))
                .then(function (response) {
                    ModalSrvc.displayData({
                        title: gettextCatalog.getString('Réponse du service OAI-PMH'),
                        raw: $filter('json')(response.data),
                    });
                })
                .finally(function () {
                    editCtrl._oaiPmhLoading = false;
                });
        }

        /**
         * Import OAI-PMH
         */
        function importOaiPmh() {
            var url = CONFIGURATION.numahop.url + 'api/rest/oaipmh';

            // Paramètres du formulaire
            var formData = new FormData();
            ImportSrvc.clearParams();

            // Bibliothèque
            formData.append('library', editCtrl.library.identifier);
            ImportSrvc.setParam('library', editCtrl.library);

            setFormValue('baseUrl', editCtrl.report.baseUrl, formData);
            setFormValue('from', editCtrl.report.from, formData);
            setFormValue('to', editCtrl.report.to, formData);
            setFormValue('set', editCtrl.report.set, formData);

            // Projet
            if (editCtrl.report.project) {
                formData.append('project', editCtrl.report.project.identifier);
                ImportSrvc.setParam('project', editCtrl.report.project);
            }
            // Lot
            if (editCtrl.report.lot) {
                formData.append('lot', editCtrl.report.lot.identifier);
                ImportSrvc.setParam('lot', editCtrl.report.lot);
            }
            // Paramètres d'exécution
            setFormValue('validation', !!editCtrl.import.validation, formData); // étape de validation par l'utilisateur
            // Dédoublonnage des unités documentaires importées
            setFormValue('dedup', !!editCtrl.import.dedup, formData);
            // Traitement par défaut des doublons
            if (editCtrl.import.dedup) {
                setFormValue('dedupProcess', editCtrl.import.dedupProcess, formData);
            }

            var config = {
                transformRequest: angular.identity,
                headers: {
                    'Content-Type': undefined,
                },
            };
            $http
                .post(url, formData, config)
                .success(function (data) {
                    MessageSrvc.addSuccess(gettext("L'import est en cours d'exécution"));
                    $location.search({ id: data.identifier }); // màj url

                    // Synchro du scope parent
                    $scope.$emit('editCtrl:synchro', data);

                    $timeout(function () {
                        loadImportReport(data.identifier);
                    });
                })
                .error(function (data) {
                    MessageSrvc.addError(gettext('Échec lors du téléversement du fichier'));
                    $timeout(function () {
                        loadImportReport(data.identifier);
                    });
                });
        }
    }
})();
