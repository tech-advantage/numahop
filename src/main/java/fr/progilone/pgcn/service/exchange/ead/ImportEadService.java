package fr.progilone.pgcn.service.exchange.ead;

import static fr.progilone.pgcn.domain.document.BibliographicRecord.*;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import fr.progilone.pgcn.domain.document.DocPropertyType;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.exchange.ImportReport;
import fr.progilone.pgcn.domain.exchange.ImportedDocUnit;
import fr.progilone.pgcn.domain.exchange.Mapping;
import fr.progilone.pgcn.domain.jaxb.ead.C;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.service.document.DocPropertyTypeService;
import fr.progilone.pgcn.service.document.DocUnitService;
import fr.progilone.pgcn.service.es.EsBibliographicRecordService;
import fr.progilone.pgcn.service.es.EsDocUnitService;
import fr.progilone.pgcn.service.exchange.AbstractImportService;
import fr.progilone.pgcn.service.exchange.DeduplicationService;
import fr.progilone.pgcn.service.exchange.ImportDocUnitService;
import fr.progilone.pgcn.service.exchange.ImportReportService;
import fr.progilone.pgcn.service.exchange.MappingService;
import fr.progilone.pgcn.service.exchange.ead.mapping.CompiledMapping;
import fr.progilone.pgcn.service.util.transaction.TransactionService;
import fr.progilone.pgcn.web.websocket.WebsocketService;

/**
 * Created by Sébastien on 16/05/2017.
 */
@Service
public class ImportEadService extends AbstractImportService {

    private static final Logger LOG = LoggerFactory.getLogger(ImportEadService.class);

    private final DocPropertyTypeService docPropertyTypeService;
    private final EadToDocUnitConvertService convertService;
    private final EadMappingEvaluationService mappingEvaluationService;
    private final ExportEadService exportEadService;
    private final ImportDocUnitService importDocUnitService;
    private final ImportReportService importReportService;
    private final MappingService mappingService;
    private final TransactionService transactionService;
    private final WebsocketService websocketService;

    @Autowired
    public ImportEadService(final DeduplicationService deduplicationService,
                            final DocPropertyTypeService docPropertyTypeService,
                            final DocUnitService docUnitService,
                            final EadToDocUnitConvertService convertService,
                            final EadMappingEvaluationService mappingEvaluationService,
                            final ExportEadService exportEadService,
                            final EsDocUnitService esDocUnitService,
                            final EsBibliographicRecordService esBibliographicRecordService,
                            final ImportDocUnitService importDocUnitService,
                            final ImportReportService importReportService,
                            final MappingService mappingService,
                            final TransactionService transactionService,
                            final WebsocketService websocketService) {
        super(deduplicationService,
              docUnitService,
              esDocUnitService,
              esBibliographicRecordService,
              importDocUnitService,
              importReportService,
              transactionService,
              websocketService);
        this.convertService = convertService;
        this.docPropertyTypeService = docPropertyTypeService;
        this.mappingEvaluationService = mappingEvaluationService;
        this.exportEadService = exportEadService;
        this.importDocUnitService = importDocUnitService;
        this.importReportService = importReportService;
        this.mappingService = mappingService;
        this.transactionService = transactionService;
        this.websocketService = websocketService;
    }

    /**
     * Import asynchrone d'un flux de notices Dublin Core
     *
     * @param importFile
     *         Fichier de notices à importer
     * @param mappingId
     *         Identifiant du mapping
     * @param mappingChildId
     *         Identifiant du mapping enfant
     * @param report
     *         Rapport d'exécution de cet import
     * @param stepValidation
     *         Étape de validation par l'utilisateur
     * @param stepDeduplication
     *         Étape de dédoublonnage
     * @param defaultDedupProcess
     * @param propertyOrder
     */
    @Async
    public void importEadAsync(final File importFile,
                               final String mappingId,
                               final String mappingChildId,
                               ImportReport report,
                               final boolean stepValidation,
                               final boolean stepDeduplication,
                               final ImportedDocUnit.Process defaultDedupProcess,
                               final PropertyOrder propertyOrder,
                               final boolean archivable,
							   final boolean distributable) {
        try {
            /* Pré-import */
            LOG.info("Pré-import du fichier EAD {}", importFile.getAbsolutePath());
            report = importEadRecords(importFile, mappingId, mappingChildId, report, propertyOrder, archivable, distributable);

            /* Poursuite du traitement des unités documentaires pré-importées: recherche de doublons, validation utilisateur, import */
            importDocUnit(report, null, stepValidation, stepDeduplication, defaultDedupProcess);
            LOG.info("Le fichier EAD {} est traité avec le statut {}", importFile.getAbsolutePath(), report.getStatus());

        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
            importReportService.failReport(report, e);
        }
    }

    /**
     * Import des notices à partir d'un fichier RDFXML contenant les Description Dublin Core
     *
     * @param importFile
     * @param mappingId
     * @param mappingChildId
     * @param report
     * @param propertyOrder
     * @return
     * @throws PgcnTechnicalException
     * @see fr.progilone.pgcn.domain.jaxb.rdf.RDF
     */
    private ImportReport importEadRecords(final File importFile,
                                          final String mappingId,
                                          final String mappingChildId,
                                          final ImportReport report,
                                          final PropertyOrder propertyOrder,
                                          final boolean archivable,
                                          final boolean distributable) throws PgcnTechnicalException {
        try {
            final TransactionStatus status = transactionService.startTransaction(true);

            // Chargement du mapping
            final Mapping mapping = mappingService.findOne(mappingId);
            if (mapping == null) {
                throw new PgcnTechnicalException("Il n'existe pas de mapping avec l'identifiant " + mappingId);
            }
            // Pré-compilation du mapping
            final CompiledMapping compiledMapping = mappingEvaluationService.compileMapping(mapping);

            // Mapping des composants EAD enfant
            final Mapping mappingChild;
            final CompiledMapping compiledMappingChild;

            if (mappingChildId != null) {
                // Chargement du mapping
                mappingChild = mappingService.findOne(mappingChildId);
                if (mappingChild == null) {
                    throw new PgcnTechnicalException("Il n'existe pas de mapping avec l'identifiant " + mappingChildId + " (composants enfant)");
                }
                // Pré-compilation du mapping
                compiledMappingChild = mappingEvaluationService.compileMapping(mappingChild);

            } else {
                mappingChild = null;
                compiledMappingChild = null;
            }

            // Chargement des types de propriété
            final Map<String, DocPropertyType> propertyTypes =
                docPropertyTypeService.findAll().stream().collect(Collectors.toMap(DocPropertyType::getIdentifier, Function.identity()));

            // Résumé d'exécution
            report.setMapping(mapping);   // lien avec le mapping qui vient d'être chargé
            report.setMappingChildren(mappingChild);
            final ImportReport runningReport = importReportService.startReport(report);

            transactionService.commitTransaction(status);

            // Initialisation de la transaction
            LOG.debug("Initialisation de l'import");
            final TransactionManager tMgr = new TransactionManager(false);

            tMgr.setProgressJob((nb) -> {
                final Map<String, Object> statusMap = importReportService.getStatus(runningReport);
                statusMap.put("processed", nb);
                websocketService.sendObject(runningReport.getIdentifier(), statusMap);
            });

            // SIMPLE: 1 composant EAD enfant = 1 notice dans PGCN
            if (mappingChild == null) {
                getSimpleEadCEntityHandler(compiledMapping, propertyTypes, propertyOrder, runningReport, tMgr, archivable, distributable)
                    // Lecture du fichier d'entrée
                    .parse(importFile);
            }
            // SIMPLE_MULTI_NOTICE: 1 composant EAD parent + composants enfant = 1 notice dans PGCN
            else {
                getMultipleEadCEntityHandler(compiledMapping, compiledMappingChild, propertyTypes, propertyOrder, runningReport, tMgr)
                    // Lecture du fichier d'entrée
                    .parse(importFile);
            }

            // Fermeture finale de la transaction
            tMgr.close();

            return runningReport;

        } catch (final Exception e) {
            throw new PgcnTechnicalException(e);
        }
    }

    /**
     * Lecture du fichier EAD
     * Traitement de type SIMPLE: 1 composant EAD enfant = 1 notice dans PGCN
     *
     * @param compiledMapping
     * @param propertyTypes
     * @param propertyOrder
     * @param runningReport
     * @param tMgr
     * @return
     */
    private EadCEntityHandler getSimpleEadCEntityHandler(final CompiledMapping compiledMapping,
                                                         final Map<String, DocPropertyType> propertyTypes,
                                                         final PropertyOrder propertyOrder,
                                                         final ImportReport runningReport,
                                                         final TransactionManager tMgr,
                                                         final boolean archivable,
                                                         final boolean distributable) {
        return new EadCEntityHandler((eadheader, rootC) -> {
            final EadCParser eadCParser = new EadCParser(eadheader, rootC);

            for (final C c : eadCParser.getcLeaves()) {
                try {
                    // Conversion du XML en unité documentaire
                    final DocUnit docUnit = convertService.convert(c, eadCParser, compiledMapping, propertyTypes, propertyOrder);
                    docUnit.setArchivable(archivable);
                    docUnit.setDistributable(distributable);
                    
                    // Sauvegarde
                    final ImportedDocUnit imp = new ImportedDocUnit();
                    imp.initDocUnitFields(docUnit);
                    imp.setReport(runningReport);
                    final ImportedDocUnit savedUnit = importDocUnitService.create(imp);

                    // Sauvegarde du fichier EAD sur le serveur
                    @SuppressWarnings("ConstantConditions")
                    final String docUnitId = savedUnit.getDocUnit().getIdentifier();
                    exportEadService.exportEad(docUnitId, eadheader, eadCParser.getBranch(c));

                    synchronized (runningReport) {
                        runningReport.incrementNbImp(1);
                    }

                } catch (final Exception e) {
                    LOG.error(e.getMessage(), e);
                }
                // Gestion de la transaction
                finally {
                    synchronized (tMgr) {
                        tMgr.update();
                    }
                }
            }
        });
    }

    /**
     * Lecture du fichier EAD
     * Traitement de type SIMPLE_MULTI_NOTICE: 1 composant EAD parent + composants enfant = 1 notice dans PGCN
     *
     * @param compiledMapping
     * @param compiledMappingChild
     * @param propertyTypes
     * @param propertyOrder
     * @param runningReport
     * @param tMgr
     * @return
     */
    private EadCEntityHandler getMultipleEadCEntityHandler(final CompiledMapping compiledMapping,
                                                           final CompiledMapping compiledMappingChild,
                                                           final Map<String, DocPropertyType> propertyTypes,
                                                           final PropertyOrder propertyOrder,
                                                           final ImportReport runningReport,
                                                           final TransactionManager tMgr) {
        return new EadCEntityHandler((eadheader, rootC) -> {
            final EadCParser eadCParser = new EadCParser(eadheader, rootC);

            try {
                // Conversion du XML en unité documentaire
                final DocUnit docUnit = convertService.convert(rootC, eadCParser, compiledMapping, propertyTypes, propertyOrder);

                // Sauvegarde
                final ImportedDocUnit imp = new ImportedDocUnit();
                imp.initDocUnitFields(docUnit);
                imp.setReport(runningReport);
                final ImportedDocUnit savedUnit = importDocUnitService.create(imp);

                // Dans le cas SIMPLE_MULTI_NOTICE, on n'importe les pptés de l'élément C racine qu'une seule fois => on empêche le parser d'aller rechercher les pptés dans rootC
                if (propertyOrder == PropertyOrder.BY_CREATION) {
                    eadCParser.getParentMap()
                              .entrySet()
                              .stream()
                              .filter(e -> Objects.equals(e.getValue(), rootC))
                              .map(Map.Entry::getKey)
                              .collect(Collectors.toList())
                              .forEach(k -> eadCParser.getParentMap().remove(k));
                }

                // Ajout des composants enfant, mappés avec le sous-mapping
                for (final C c : eadCParser.getcLeaves()) {
                    if (!Objects.equals(c, rootC)) {
                        final DocUnit docUnitChild = convertService.convert(c, eadCParser, compiledMappingChild, propertyTypes, propertyOrder);
                        docUnitChild.setParent(docUnit);
                        // Sauvegarde
                        final ImportedDocUnit impChild = new ImportedDocUnit();
                        impChild.initDocUnitFields(docUnitChild);
                        impChild.setReport(runningReport);
                        impChild.setParentDocUnit(savedUnit.getIdentifier());
                        final ImportedDocUnit savedUnitChild = importDocUnitService.create(impChild);
                    }
                }

                // Sauvegarde du fichier EAD sur le serveur
                @SuppressWarnings("ConstantConditions")
                final String docUnitId = savedUnit.getDocUnit().getIdentifier();
                exportEadService.exportEad(docUnitId, eadheader, rootC);

                synchronized (runningReport) {
                    runningReport.incrementNbImp(1);
                }

            } catch (final Exception e) {
                LOG.error(e.getMessage(), e);
            }
            // Gestion de la transaction
            finally {
                synchronized (tMgr) {
                    tMgr.update();
                }
            }
        });
    }

    /**
     * Gestion des transactions avec commit tous les x appels
     */
    private class TransactionManager {

        private static final long COMMIT = 100L; // Committe tous les x éléments

        private final boolean readonly; // transaction en lecture seule
        private Consumer<Long> progressJob = lg -> {
        };// Permet de suivre la progression du job

        private TransactionStatus status = null;
        private long nbProcessed = 0;

        public TransactionManager(final boolean readonly) {
            this.readonly = readonly;
            this.status = transactionService.startTransaction(readonly);
        }

        /**
         * {@link Consumer} de suivi de la progession de l'import
         *
         * @param progressJob
         */
        public void setProgressJob(final Consumer<Long> progressJob) {
            this.progressJob = progressJob;
        }

        /**
         * Commit de la transaction courante tous les COMMIT appels
         *
         * @return
         */
        public void update() {
            nbProcessed++;

            if (nbProcessed > 0 && nbProcessed % COMMIT == 0) {
                transactionService.commitTransaction(status);
                LOG.debug("{} enregistrements traités", nbProcessed);
                progressJob.accept(nbProcessed);

                status = transactionService.startTransaction(readonly);
            }
        }

        /**
         * Commit final de la transaction courante
         */
        public void close() {
            transactionService.commitTransaction(status);
        }
    }
}
