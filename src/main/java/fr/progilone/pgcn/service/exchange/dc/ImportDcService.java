package fr.progilone.pgcn.service.exchange.dc;

import fr.progilone.pgcn.domain.document.DocPropertyType;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.exchange.FileFormat;
import fr.progilone.pgcn.domain.exchange.ImportReport;
import fr.progilone.pgcn.domain.exchange.ImportedDocUnit;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.service.document.DocPropertyTypeService;
import fr.progilone.pgcn.service.document.DocUnitService;
import fr.progilone.pgcn.service.es.EsBibliographicRecordService;
import fr.progilone.pgcn.service.es.EsDocUnitService;
import fr.progilone.pgcn.service.exchange.AbstractImportService;
import fr.progilone.pgcn.service.exchange.DeduplicationService;
import fr.progilone.pgcn.service.exchange.ImportDocUnitService;
import fr.progilone.pgcn.service.exchange.ImportReportService;
import fr.progilone.pgcn.service.library.LibraryService;
import fr.progilone.pgcn.service.util.transaction.TransactionService;
import fr.progilone.pgcn.web.websocket.WebsocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

/**
 * Created by Sébastien on 23/12/2016.
 */
@Service
public class ImportDcService extends AbstractImportService {

    private static final String MAPPING_DC_OAI = "DC_OAI_MAPPING";
    private static final String MAPPING_DC_RDF = "DC_RDF_MAPPING";

    private static final Logger LOG = LoggerFactory.getLogger(ImportDcService.class);

    private final DcToDocUnitConvertService convertService;
    private final DocPropertyTypeService docPropertyTypeService;
    private final ImportDocUnitService importDocUnitService;
    private final ImportReportService importReportService;
    private final LibraryService libraryService;
    private final TransactionService transactionService;

    @Autowired
    public ImportDcService(final DeduplicationService deduplicationService,
                           final DocUnitService docUnitService,
                           final EsDocUnitService esDocUnitService,
                           final EsBibliographicRecordService esBibliographicRecordService,
                           final ImportDocUnitService importDocUnitService,
                           final ImportReportService importReportService,
                           final TransactionService transactionService,
                           final WebsocketService websocketService,
                           final DcToDocUnitConvertService convertService,
                           final DocPropertyTypeService docPropertyTypeService,
                           final LibraryService libraryService) {
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
        this.importDocUnitService = importDocUnitService;
        this.importReportService = importReportService;
        this.libraryService = libraryService;
        this.transactionService = transactionService;
    }

    /**
     * Import asynchrone d'un flux de notices Dublin Core.
     *
     * @param importFile
     *         Fichier de notices à importer
     * @param fileFormat
     *         Format du fichier (DC)
     * @param mappingId
     *         Identifiant du mapping
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
    public void importDcAsync(final File importFile,
                              final FileFormat fileFormat,
                              final String libraryId,
                              final String mappingId,
                              ImportReport report,
                              final boolean stepValidation,
                              final boolean stepDeduplication,
                              final ImportedDocUnit.Process defaultDedupProcess,
                              final boolean archivable,
                              final boolean distributable) {
        try {
            /* Pré-import */
            LOG.info("Pré-import du fichier {} {}", fileFormat, importFile.getAbsolutePath());
            // DC
            if (fileFormat == FileFormat.DC) {
                final ImportReport fReport = report;
                report = transactionService.executeInNewTransactionWithReturnAndException(() -> {
                    return importDcRecords(importFile, libraryId, mappingId, fReport, archivable, distributable);
                });
            }
            // DCQ
            //            else if (fileFormat == FileFormat.DCQ) {
            //                report = importDcRecords(importFile, mappingId, report);
            //            }
            // Non géré
            else {
                throw new PgcnTechnicalException("Le format de fichier "
                                                 + fileFormat
                                                 + " n'est pas supporté (fichier "
                                                 + importFile.getAbsolutePath()
                                                 + ")"
                                                 + "par le service ImportDcService");
            }

            /* Poursuite du traitement des unités documentaires pré-importées: recherche de doublons, validation utilisateur, import */
            importDocUnit(report, null, stepValidation, stepDeduplication, defaultDedupProcess);
            LOG.info("Le fichier {} {} est traité avec le statut {}", fileFormat, importFile.getAbsolutePath(), report.getStatus());

        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
            importReportService.failReport(report, e);
        }
    }

    /**
     * Import des notices à partir d'un fichier RDFXML contenant les Description Dublin Core
     *
     * @param importFile
     * @param libraryId
     * @param mappingId
     * @param report
     * @return
     * @throws PgcnTechnicalException
     * @see fr.progilone.pgcn.domain.jaxb.rdf.RDF
     */
    private ImportReport importDcRecords(final File importFile, 
    									 final String libraryId, 
    									 String mappingId, 
    									 ImportReport report, 
    									 final boolean archivable,
    									 final boolean distributable) throws PgcnTechnicalException {
        try {
            // vérification bibliothèque
            final Library library = libraryService.findOne(libraryId);
            if (library == null) {
                throw new PgcnTechnicalException("Il n'existe pas de bibliothèque avec l'identifiant " + libraryId);
            }

            // Chargement des types de propriété
            final List<DocPropertyType> propertyTypes = docPropertyTypeService.findAll();

            // Résumé d'exécution
            // report.setMapping(mapping);   // lien avec le mapping qui vient d'être chargé
            final ImportReport runningReport = importReportService.startReport(report);

            // Vérification du type de mapping OAI_DC / RDF
            switch (mappingId) {
                case MAPPING_DC_OAI: {
                    new OAIDcEntityHandler(oaidc -> {
                        try {
                            // Conversion du XML en unité documentaire
                            final DocUnit docUnit = convertService.convert(oaidc, library, propertyTypes);
                            docUnit.setArchivable(archivable);
                            docUnit.setDistributable(distributable);

                            // Sauvegarde
                            final ImportedDocUnit imp = new ImportedDocUnit();
                            imp.initDocUnitFields(docUnit);
                            imp.setReport(runningReport);
                            importDocUnitService.create(imp);

                            synchronized (runningReport) {
                                runningReport.incrementNbImp(1);
                            }
                        } catch (Exception e) {
                            LOG.error(e.getMessage(), e);
                        }
                    }).parse(importFile);
                }
                break;

                case MAPPING_DC_RDF:
                default: {
                    new RdfDcEntityHandler((rdf, description) -> {
                        LOG.debug(description.getAbout());
                        try {
                            // Conversion du XML en unité documentaire
                            final DocUnit docUnit = convertService.convert(description, library, propertyTypes);
                            docUnit.setArchivable(archivable);
                            docUnit.setDistributable(distributable);

                            // Sauvegarde
                            final ImportedDocUnit imp = new ImportedDocUnit();
                            imp.initDocUnitFields(docUnit);
                            imp.setReport(runningReport);
                            importDocUnitService.create(imp);

                            synchronized (runningReport) {
                                runningReport.incrementNbImp(1);
                            }
                        } catch (Exception e) {
                            LOG.error(e.getMessage(), e);
                        }
                    }).parse(importFile);
                }
                break;
            }
            return runningReport;

        } catch (final Exception e) {
            throw new PgcnTechnicalException(e);
        }
    }
}
