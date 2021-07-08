package fr.progilone.pgcn.service.exchange.marc;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.marc4j.MarcJsonReader;
import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.MarcXmlReader;
import org.marc4j.converter.CharConverter;
import org.marc4j.marc.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import fr.progilone.pgcn.domain.document.DocPropertyType;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.exchange.DataEncoding;
import fr.progilone.pgcn.domain.exchange.FileFormat;
import fr.progilone.pgcn.domain.exchange.ImportReport;
import fr.progilone.pgcn.domain.exchange.ImportedDocUnit;
import fr.progilone.pgcn.domain.exchange.Mapping;
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
import fr.progilone.pgcn.service.exchange.marc.mapping.CompiledMapping;
import fr.progilone.pgcn.service.exchange.marc.mapping.CompiledMappingRule;
import fr.progilone.pgcn.service.util.transaction.TransactionService;
import fr.progilone.pgcn.service.util.transaction.TransactionalJobRunner;
import fr.progilone.pgcn.web.websocket.WebsocketService;

/**
 * Created by Sebastien on 22/11/2016.
 */
@Service
public class ImportMarcService extends AbstractImportService {

    private static final Logger LOG = LoggerFactory.getLogger(ImportMarcService.class);

    private final DocPropertyTypeService docPropertyTypeService;
    private final ImportDocUnitService importDocUnitService;
    private final ImportReportService importReportService;
    private final MarcMappingEvaluationService mappingEvaluationService;
    private final MappingService mappingService;
    private final MarcToDocUnitConvertService convertService;
    private final TransactionService transactionService;
    private final WebsocketService websocketService;

    @Autowired
    public ImportMarcService(final DeduplicationService deduplicationService,
                             final DocPropertyTypeService docPropertyTypeService,
                             final DocUnitService docUnitService,
                             final EsDocUnitService esDocUnitService,
                             final EsBibliographicRecordService esBibliographicRecordService,
                             final ImportDocUnitService importDocUnitService,
                             final ImportReportService importReportService,
                             final MarcMappingEvaluationService mappingEvaluationService,
                             final MarcToDocUnitConvertService convertService,
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
        this.docPropertyTypeService = docPropertyTypeService;
        this.importDocUnitService = importDocUnitService;
        this.importReportService = importReportService;
        this.mappingEvaluationService = mappingEvaluationService;
        this.convertService = convertService;
        this.mappingService = mappingService;
        this.transactionService = transactionService;
        this.websocketService = websocketService;
    }

    /**
     * Import asynchrone d'un flux de notices au format MARC.
     *
     * @param importFiles
     *         Fichier de notices à importer
     * @param fileFormat
     *         Format du fichier (MARC, MARCJSON, MARCXML)
     * @param dataEncoding
     *         Encodage du fichier
     * @param mappingId
     *         Identifiant du mapping
     * @param mappingChildId
     *         Identifiant du mapping pour l'import des exemplaires de périodiques
     * @param report
     *         Rapport d'exécution de cet import
     * @param stepValidation
     *         Étape de validation par l'utilisateur
     * @param stepDeduplication
     *         Étape de dédoublonnage
     * @param defaultDedupProcess
     *         Gestion de l'import des doublons, dans le cas où l'import se fait directement sans validation par l'utilisateur
     * @param archivable
     * @param distributable         
     */
    @Async
    public void importMarcAsync(final List<File> importFiles,
                                final FileFormat fileFormat,
                                final DataEncoding dataEncoding,
                                final String mappingId,
                                final String mappingChildId,
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
            for (final File importFile : importFiles) {
                report = preimportFile(importFile, 
                						fileFormat, 
                						dataEncoding, 
                						mappingId, 
                						mappingChildId, 
                						parentKeyExpr, 
                						report, 
                						archivable, 
                						distributable);
            }
            /* Poursuite du traitement des unités documentaires pré-importées: recherche de doublons, validation utilisateur, import */
            importDocUnit(report, parentReportId, stepValidation, stepDeduplication, defaultDedupProcess);
            LOG.info("Les fichiers {} {} sont traités avec le statut {}", fileFormat, report.getFilesAsString(), report.getStatus());

        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
            importReportService.failReport(report, e);
        }
    }

    /**
     * Import asynchrone d'un flux de notices au format MARCXML
     *
     * @param importFiles
     * @param mappingId
     * @param report
     * @param stepValidation
     * @param stepDeduplication
     * @param defaultDedupProcess
     */
    @Async
    public void importMarcXmlAsync(final List<File> importFiles,
                                   final String mappingId,
                                   final ImportReport report,
                                   final boolean stepValidation,
                                   final boolean stepDeduplication,
                                   final ImportedDocUnit.Process defaultDedupProcess,
                                   final boolean archivable,
                                   final boolean distributable) {
        importMarcAsync(importFiles,
                        FileFormat.MARCXML,
                        DataEncoding.UTF_8,
                        mappingId,
                        null,
                        null,
                        null,
                        report,
                        stepValidation,
                        stepDeduplication,
                        defaultDedupProcess,
                        archivable,
                        distributable);
    }

    /**
     * Pré-import d'un fichier de notice MARC
     *
     * @param importFile
     * @param fileFormat
     * @param dataEncoding
     * @param mappingId
     * @param mappingChildId
     * @param report
     * @return
     * @throws PgcnTechnicalException
     */
    private ImportReport preimportFile(final File importFile,
                                       final FileFormat fileFormat,
                                       final DataEncoding dataEncoding,
                                       final String mappingId,
                                       final String mappingChildId,
                                       final String parentKeyExpr,
                                       final ImportReport report,
                                       final boolean archivable,
                                       final boolean distributable) throws PgcnTechnicalException {
    	
        LOG.info("Pré-import du fichier {} {}", fileFormat, importFile.getAbsolutePath());

        // MARC
        if (fileFormat == FileFormat.MARC) {
            final String encoding = dataEncoding == DataEncoding.UTF_8 ?
                                    StandardCharsets.UTF_8.name() :
                                    dataEncoding == DataEncoding.ISO_8859_1 ? StandardCharsets.ISO_8859_1.name() : null;
            return importMarcRecords(importFile,
                                     dataEncoding,
                                     mappingId,
                                     mappingChildId,
                                     parentKeyExpr,
                                     in -> new MarcStreamReader(in, encoding),
                                     report,
                                     archivable,
                                     distributable);
        }
        // MARC JSON
        else if (fileFormat == FileFormat.MARCJSON) {
            return importMarcRecords(importFile, 
            						 dataEncoding, 
            						 mappingId, 
            						 mappingChildId, 
            						 parentKeyExpr, 
            						 MarcJsonReader::new, 
            						 report, 
            						 archivable, 
            						 distributable);
        }
        // MARC XML
        else if (fileFormat == FileFormat.MARCXML) {
            return importMarcRecords(importFile, 
            						 dataEncoding, 
            						 mappingId, 
            						 mappingChildId, 
            						 parentKeyExpr, 
            						 MarcXmlReader::new, 
            						 report, 
            						 archivable, 
            						 distributable);
        }
        // Non géré
        else {
            throw new PgcnTechnicalException("Le format de fichier "
                                             + fileFormat
                                             + " n'est pas supporté (fichier "
                                             + importFile.getAbsolutePath()
                                             + ") "
                                             + "par le service ImportMarcService");
        }
    }

    /**
     * Import d'un flux de notices à partir d'un {@link MarcReader}
     *
     * @param importFile
     * @param dataEncoding
     * @param mappingId
     * @param mappingChildId
     * @param readerBuilder
     * @param report
     */
    private ImportReport importMarcRecords(final File importFile,
                                           final DataEncoding dataEncoding,
                                           final String mappingId,
                                           final String mappingChildId,
                                           final String parentKeyExpr,
                                           final Function<InputStream, MarcReader> readerBuilder,
                                           ImportReport report,
                                           final boolean archivable,
                                           final boolean distributable) throws PgcnTechnicalException {

        try (final InputStream fileIn = new FileInputStream(importFile); final InputStream in = new BufferedInputStream(fileIn)) {

            final MarcReader reader = readerBuilder.apply(in);
            final CharConverter charConverter = MarcUtils.getCharConverterToUnicode(dataEncoding);

            report = importRecord(reader, charConverter, mappingId, mappingChildId, parentKeyExpr, report, archivable, distributable);
            return report;

        } catch (final Exception e) {
            throw new PgcnTechnicalException(e);
        }
    }

    /**
     * Import de notices à partir d'un {@link MarcReader}
     *
     * @param reader
     * @param charConverter
     * @param mappingId
     * @param mappingChildId
     * @param importReport
     */
    private ImportReport importRecord(final MarcReader reader,
                                      final CharConverter charConverter,
                                      final String mappingId,
                                      final String mappingChildId,
                                      final String parentKeyExpr,
                                      final ImportReport importReport,
                                      final boolean archivable,
                                      final boolean distributable) throws PgcnTechnicalException {

        final TransactionStatus status = transactionService.startTransaction(true);

        // Chargement du mapping
        final Mapping mapping = mappingService.findOne(mappingId);
        if (mapping == null) {
            throw new PgcnTechnicalException("Il n'existe pas de mapping avec l'identifiant " + mappingId);
        }
        // Pré-compilation du mapping
        final CompiledMapping compiledMapping = mappingEvaluationService.compileMapping(mapping, charConverter);

        // Mapping des exemplaires de périodique
        final Mapping mappingChild;
        final CompiledMapping compiledMappingChild;

        if (mappingChildId != null) {
            // Chargement du mapping
            mappingChild = mappingService.findOne(mappingChildId);
            if (mappingChild == null) {
                throw new PgcnTechnicalException("Il n'existe pas de mapping avec l'identifiant " + mappingChildId + " (périodiques)");
            }
            // Pré-compilation du mapping
            compiledMappingChild = mappingEvaluationService.compileMapping(mappingChild, charConverter);
        } else {
            mappingChild = null;
            compiledMappingChild = null;
        }

        // Pré-compilation du script de calcul de la clé parente
        final CompiledMappingRule joinScript =
            StringUtils.isNotBlank(parentKeyExpr) ? mappingEvaluationService.compileMapping(parentKeyExpr, charConverter) : null;

        // Chargement des types de propriété
        final Map<String, DocPropertyType> propertyTypes =
            docPropertyTypeService.findAll().stream().collect(Collectors.toMap(DocPropertyType::getIdentifier, Function.identity()));

        // Résumé d'exécution
        importReport.setMapping(mapping);   // lien avec le mapping qui vient d'être chargé
        importReport.setMappingChildren(mappingChild);
        final ImportReport runningReport = importReportService.startReport(importReport);

        transactionService.commitTransaction(status);

        // Création des unités documentaires pré-importées à partir des notices MARC
        new TransactionalJobRunner<Record>(transactionService)
            // Configuration du job
            .setCommit(BULK_SIZE)
            // pas de parallélisation pour prévenir les transaction lock au moment de importDocUnitService.create
            .setMaxThreads(1)
            // Traitement principal
            .forEach((record) -> {
                try {
                    // Conversion du record en unité documentaire
                    final List<DocUnitWrapper> docUnits = convertService.convert(record, charConverter, compiledMapping, joinScript, propertyTypes);

                    // #HIERARCHY_IN_SINGLE_NOTICE: Conversion des exemplaires de la notice courante
                    if (mappingChild != null && !docUnits.isEmpty()) {
                        final DocUnitWrapper firstElement = docUnits.get(0);
                        final DocUnit parentDocUnit = firstElement.getDocUnit();

                        // un seul parent par notice
                        docUnits.clear();
                        docUnits.add(firstElement);

                        final List<DocUnitWrapper> childrenDocUnits =
                            convertService.convert(record, charConverter, compiledMappingChild, null, propertyTypes);
                        childrenDocUnits.forEach(u -> {
                            u.getDocUnit().setParent(parentDocUnit);
                            docUnits.add(u);
                        });
                    }

                    // Si plusieurs UD sont extraites de cette notices, on renseigne le code de regroupement
                    final String groupCode = docUnits.size() > 1 ? record.getControlNumber() : null;

                    // Sauvegarde des unités documentaires
                    for (final DocUnitWrapper docUnitWrapper : docUnits) {
                        // Sauvegarde
                        final ImportedDocUnit imp = new ImportedDocUnit();
                        final DocUnit createdUd = docUnitWrapper.getDocUnit();
                        createdUd.setArchivable(archivable);
                        createdUd.setDistributable(distributable);
                        
                        imp.initDocUnitFields(createdUd);
                        imp.setParentKey(docUnitWrapper.getParentKey());
                        imp.setReport(runningReport);
                        imp.setGroupCode(groupCode);

                        // Recopie des infos du parent
                        final DocUnit parent = imp.getDocUnit().getParent();
                        if (parent != null) {
                            imp.setParentDocUnit(parent.getIdentifier());
                            imp.setParentDocUnitPgcnId(parent.getPgcnId());
                            imp.setParentDocUnitLabel(parent.getLabel());
                        }

                        importDocUnitService.create(imp); // PgcnValidationException

                        synchronized (runningReport) {
                            runningReport.incrementNbImp(1);
                        }
                    }
                    return true;
                }
                // Erreur imprévue
                catch (final Exception e) {
                    LOG.error(e.getMessage(), e);
                    return false;
                }

            })
            // progression => websocket
            .onProgress((nb) -> {
                final Map<String, Object> statusMap = importReportService.getStatus(runningReport);
                statusMap.put("processed", nb);
                websocketService.sendObject(runningReport.getIdentifier(), statusMap);
            })
            // iterator
            .process(new MarcRecordIterator(reader));

        return runningReport;
    }
}
