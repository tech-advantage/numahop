package fr.progilone.pgcn.service.exchange.csv;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import fr.progilone.pgcn.domain.document.DocPropertyType;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.exchange.CSVMapping;
import fr.progilone.pgcn.domain.exchange.CSVMappingRule;
import fr.progilone.pgcn.domain.exchange.FileFormat;
import fr.progilone.pgcn.domain.exchange.ImportReport;
import fr.progilone.pgcn.domain.exchange.ImportedDocUnit;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.service.document.DocPropertyTypeService;
import fr.progilone.pgcn.service.document.DocUnitService;
import fr.progilone.pgcn.service.es.EsBibliographicRecordService;
import fr.progilone.pgcn.service.es.EsDocUnitService;
import fr.progilone.pgcn.service.exchange.AbstractImportService;
import fr.progilone.pgcn.service.exchange.CSVMappingService;
import fr.progilone.pgcn.service.exchange.DeduplicationService;
import fr.progilone.pgcn.service.exchange.ImportDocUnitService;
import fr.progilone.pgcn.service.exchange.ImportReportService;
import fr.progilone.pgcn.service.exchange.marc.DocUnitWrapper;
import fr.progilone.pgcn.service.util.transaction.TransactionService;
import fr.progilone.pgcn.service.util.transaction.TransactionalJobRunner;
import fr.progilone.pgcn.web.websocket.WebsocketService;

/**
 * Created by Sebastien on 22/11/2016.
 */
@Service
public class ImportCSVService extends AbstractImportService {

    private static final Logger LOG = LoggerFactory.getLogger(ImportCSVService.class);

    private final DocPropertyTypeService docPropertyTypeService;
    private final ImportDocUnitService importDocUnitService;
    private final ImportReportService importReportService;
    private final CSVMappingService mappingService;
    private final TransactionService transactionService;
    private final WebsocketService websocketService;
    private final CSVToDocUnitConvertService csvToDocUnitConvertService;

    @Autowired
    public ImportCSVService(final DeduplicationService deduplicationService,
                            final DocPropertyTypeService docPropertyTypeService,
                            final DocUnitService docUnitService,
                            final EsDocUnitService esDocUnitService,
                            final EsBibliographicRecordService esBibliographicRecordService,
                            final ImportDocUnitService importDocUnitService,
                            final ImportReportService importReportService,
                            final CSVMappingService mappingService,
                            final TransactionService transactionService,
                            final WebsocketService websocketService,
                            final CSVToDocUnitConvertService csvToDocUnitConvertService) {
        super(deduplicationService,
              docUnitService,
              esDocUnitService,
              esBibliographicRecordService,
              importDocUnitService,
              importReportService,
              transactionService,
              websocketService);
        this.docPropertyTypeService = docPropertyTypeService;
        this.importDocUnitService = importDocUnitService;
        this.importReportService = importReportService;
        this.mappingService = mappingService;
        this.transactionService = transactionService;
        this.websocketService = websocketService;
        this.csvToDocUnitConvertService = csvToDocUnitConvertService;
    }

    /**
     * Import asynchrone d'un flux de notices au format MARC
     *
     * @param importFile
     *         Fichier de notices à importer
     * @param fileFormat
     *         Format du fichier (MARC, MARCJSON, MARCXML)
     * @param report
     *         Rapport d'exécution de cet import
     * @param stepValidation
     *         Étape de validation par l'utilisateur
     * @param stepDeduplication
     *         Étape de dédoublonnage
     * @param defaultDedupProcess
     * @param archivable
     * @param distributable
     */
    @Async
    public void importCSVAsync(final File importFile,
                               final FileFormat fileFormat,
                               final String mappingId,
                               final String parentReportId,
                               final String parentKeyExpr,
                               ImportReport report,
                               final boolean stepValidation,
                               final boolean stepDeduplication,
                               final ImportedDocUnit.Process defaultDedupProcess,
                               final boolean archivable,
                               final boolean distributable) {
        try {
            /* Pré-import */
            LOG.info("Pré-import du fichier {} {}", fileFormat, importFile.getAbsolutePath());
            report = importCSVRecords(importFile, report, mappingId, parentKeyExpr, archivable, distributable);

            /* Poursuite du traitement des unités documentaires pré-importées: recherche de doublons, validation utilisateur, import */
            importDocUnit(report, parentReportId, stepValidation, stepDeduplication, defaultDedupProcess);
            LOG.info("Le fichier {} {} est traité avec le statut {}", fileFormat, importFile.getAbsolutePath(), report.getStatus());

        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
            importReportService.failReport(report, e);
        }
    }

    /**
     * Import d'un flux de notices à partir d'un fichier
     *
     * @param importFile
     * @param report
     * @param mappingId
     * @param parentKeyExpr
     */
    private ImportReport importCSVRecords(final File importFile,
    										final ImportReport report,
    										final String mappingId,
    										final String parentKeyExpr,
    										final boolean archivable,
    										final boolean distributable) throws PgcnTechnicalException {

        try (final FileReader in = new FileReader(importFile)) {
            return importRecord(in, report, mappingId, parentKeyExpr, archivable, distributable);

        } catch (final Exception e) {
            throw new PgcnTechnicalException(e);
        }
    }

    /**
     * Import de notices à partir d'un {@link Reader}
     *
     * @param in
     * @param importReport
     * @param mappingId
     * @param parentKeyExpr
     */
    private ImportReport importRecord(final Reader in,
    								  final ImportReport importReport,
    								  final String mappingId,
    								  final String parentKeyExpr,
    								  final boolean archivable,
									  final boolean distributable) throws PgcnTechnicalException {
        final TransactionStatus status = transactionService.startTransaction(true);

        // Chargement du mapping
        final CSVMapping mapping = mappingService.findOne(mappingId);
        if (mapping == null) {
            throw new PgcnTechnicalException("Il n'existe pas de mapping avec l'identifiant " + mappingId);
        }

        // Record iterator
        final CSVParser parser;
        try {
            parser = CSVFormat.DEFAULT.withIgnoreSurroundingSpaces().withTrim().parse(in);
        } catch (final IOException e) {
            throw new PgcnTechnicalException(e);
        }
        // Lecture de l'entête
        final Iterator<CSVRecord> recordIterator = parser.iterator();
        final CSVRecord header = recordIterator.next();
        final Map<Integer, String> entetes = new HashMap<>();
        final Map<String, String> propertyNames = new HashMap<>();

        for (int i = 0; i < header.size(); i++) {
            final String key = header.get(i);
            if (key.startsWith("dc:")) {
                propertyNames.put(key.substring(3), key.substring(3));
                entetes.put(i, key);

            } else if (key.equals(parentKeyExpr)) {
                entetes.put(i, key);
            } else {
                // champs custom
                final String keyRule = mapping.getRules().stream()
                                    .filter(r -> StringUtils.equals(key, r.getCsvField()))
                                    .map(CSVMappingRule::getProperty)
                                    .map(DocPropertyType::getIdentifier)
                                    .findAny().orElse(null);
                                    //.ifPresent(keyRule -> propertyNames.add(keyRule));
                if (keyRule != null) {
                    propertyNames.put(keyRule, key);
                    entetes.put(i, key);
                }

            }
        }

        // Chargement des types de propriété - Map : valeur définie dans le mapping (colonne) / docPropertyType
        final Map<String, DocPropertyType> propertyTypes = new HashMap<>();
        for (final String property : propertyNames.keySet()) {
            if (docPropertyTypeService.findOne(property) != null) {
                propertyTypes.put(propertyNames.get(property), docPropertyTypeService.findOne(property));
            }
        }


        // Résumé d'exécution
        importReport.setCsvMapping(mapping);   // lien avec le mapping qui vient d'être chargé
        final ImportReport runningReport = importReportService.startReport(importReport);

        transactionService.commitTransaction(status);

        // Création des unités documentaires pré-importées à partir des notices
        new TransactionalJobRunner<CSVRecord>(transactionService)
            // Configuration du job
            .setCommit(BULK_SIZE).setMaxThreads(Runtime.getRuntime().availableProcessors())
            // Traitement principal
            .forEach((record) -> {
                try {
                    // Conversion du record en unité documentaire
                    final DocUnitWrapper wrapper = csvToDocUnitConvertService.convert(record, mapping, header, parentKeyExpr, propertyTypes, entetes, archivable, distributable);

                    // Sauvegarde
                    final ImportedDocUnit imp = new ImportedDocUnit();
                    imp.initDocUnitFields(wrapper.getDocUnit());
                    imp.setParentKey(wrapper.getParentKey());
                    imp.setReport(runningReport);

                    // Recopie des infos du parent
                    @SuppressWarnings("ConstantConditions")
                    final DocUnit parent = imp.getDocUnit().getParent();
                    if (parent != null) {
                        imp.setParentDocUnit(parent.getIdentifier());
                        imp.setParentDocUnitPgcnId(parent.getPgcnId());
                        imp.setParentDocUnitLabel(parent.getLabel());
                    }

                    importDocUnitService.create(imp);

                    synchronized (runningReport) {
                        runningReport.incrementNbImp(1);
                    }
                    return true;
                }
                // L'erreur est catchée, et comptabilisée
                catch (final Exception e) {
                    LOG.error(e.getMessage(), e);
                    return false;
                }
            }).onProgress((nb) -> {
            final Map<String, Object> statusMap = importReportService.getStatus(runningReport);
            statusMap.put("processed", nb);
            websocketService.sendObject(runningReport.getIdentifier(), statusMap);

        }).process(recordIterator);

        return runningReport;
    }
}
