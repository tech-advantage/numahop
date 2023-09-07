package fr.progilone.pgcn.service.exchange.oaipmh;

import fr.progilone.pgcn.domain.document.DocPropertyType;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.exchange.ImportReport;
import fr.progilone.pgcn.domain.exchange.ImportedDocUnit;
import fr.progilone.pgcn.domain.jaxb.oaidc.OaiDcType;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.service.document.DocPropertyTypeService;
import fr.progilone.pgcn.service.document.DocUnitService;
import fr.progilone.pgcn.service.es.EsDocUnitService;
import fr.progilone.pgcn.service.exchange.AbstractImportService;
import fr.progilone.pgcn.service.exchange.DeduplicationService;
import fr.progilone.pgcn.service.exchange.ImportDocUnitService;
import fr.progilone.pgcn.service.exchange.ImportReportService;
import fr.progilone.pgcn.service.exchange.dc.DcToDocUnitConvertService;
import fr.progilone.pgcn.service.library.LibraryService;
import fr.progilone.pgcn.service.util.transaction.TransactionService;
import fr.progilone.pgcn.service.util.transaction.TransactionalJobRunner;
import fr.progilone.pgcn.web.websocket.WebsocketService;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

/**
 * Import d'unités documentaires à partir d'un service OAI-PMH
 */
@Service
public class ImportOaiPmhService extends AbstractImportService {

    private static final Logger LOG = LoggerFactory.getLogger(ImportOaiPmhService.class);
    private static final int BULK_SIZE = 100;

    private final DcToDocUnitConvertService convertService;
    private final DocPropertyTypeService docPropertyTypeService;
    private final ImportDocUnitService importDocUnitService;
    private final ImportReportService importReportService;
    private final LibraryService libraryService;
    private final OaiPmhService oaiPmhService;
    private final TransactionService transactionService;
    private final WebsocketService websocketService;

    protected ImportOaiPmhService(final DeduplicationService deduplicationService,
                                  final DocUnitService docUnitService,
                                  final EsDocUnitService esDocUnitService,
                                  final ImportDocUnitService importDocUnitService,
                                  final ImportReportService importReportService,
                                  final TransactionService transactionService,
                                  final WebsocketService websocketService,
                                  final DcToDocUnitConvertService convertService,
                                  final DocPropertyTypeService docPropertyTypeService,
                                  final ImportDocUnitService importDocUnitService1,
                                  final ImportReportService importReportService1,
                                  final LibraryService libraryService,
                                  final OaiPmhService oaiPmhService,
                                  final TransactionService transactionService1,
                                  final WebsocketService websocketService1) {
        super(deduplicationService, docUnitService, esDocUnitService, importDocUnitService, importReportService, transactionService, websocketService);
        this.convertService = convertService;
        this.docPropertyTypeService = docPropertyTypeService;
        this.importDocUnitService = importDocUnitService1;
        this.importReportService = importReportService1;
        this.libraryService = libraryService;
        this.oaiPmhService = oaiPmhService;
        this.transactionService = transactionService1;
        this.websocketService = websocketService1;
    }

    /**
     * Import des notices à partir d'un service OAI-PMH
     */
    @Async
    public void importOaiPmhAsync(final String libraryId,
                                  ImportReport report,
                                  final OaiPmhRequest oaiRequest,
                                  final boolean stepValidation,
                                  final boolean stepDeduplication,
                                  final ImportedDocUnit.Process defaultDedupProcess) {
        try {
            /* Pré-import */
            LOG.info("Pré-import OAI-PMH");
            report = preImportOaiPmh(libraryId, report, oaiRequest);

            /* Poursuite du traitement des unités documentaires pré-importées: recherche de doublons, validation utilisateur, import */
            importDocUnit(report, null, stepValidation, stepDeduplication, defaultDedupProcess);
            LOG.info("L'import OAI-PMH s'est achevé avec le statut {}", report.getStatus());

        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
            importReportService.failReport(report, e);
        }
    }

    /**
     * Pré-import des notices à partir d'un service OAI-PMH
     *
     * @param libraryId
     * @param report
     * @param oaiRequest
     * @return
     * @throws PgcnTechnicalException
     */
    private ImportReport preImportOaiPmh(final String libraryId, final ImportReport report, final OaiPmhRequest oaiRequest) throws PgcnTechnicalException {
        try {
            final TransactionStatus status = transactionService.startTransaction(true);

            // vérification bibliothèque
            final Library library = libraryService.findOne(libraryId);
            if (library == null) {
                throw new PgcnTechnicalException("Il n'existe pas de bibliothèque avec l'identifiant " + libraryId);
            }

            // Chargement des types de propriété
            final List<DocPropertyType> propertyTypes = docPropertyTypeService.findAll();

            // Résumé d'exécution
            // report.setMapping(mapping); // lien avec le mapping qui vient d'être chargé
            final ImportReport runningReport = importReportService.startReport(report);

            transactionService.commitTransaction(status);

            // Création des unités documentaires pré-importées à partir des notices OAI_DC
            final TransactionalJobRunner<OaiDcType> job = new TransactionalJobRunner<>(new OaiPmhRecordIterator(oaiPmhService, oaiRequest), transactionService);
            job
               // Configuration du job
               .setCommit(BULK_SIZE)
               // pas de parallélisation pour prévenir les transaction lock au moment de importDocUnitService.create
               // Traitement principal
               .forEach((oaidc) -> {
                   try {
                       // Conversion du XML en unité documentaire
                       final DocUnit docUnit = convertService.convert(oaidc, library, propertyTypes);

                       // Sauvegarde
                       final ImportedDocUnit imp = new ImportedDocUnit();
                       imp.initDocUnitFields(docUnit);
                       imp.setReport(runningReport);
                       importDocUnitService.create(imp);

                       synchronized (runningReport) {
                           runningReport.incrementNbImp(1);
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
               .process();

            return runningReport;

        } catch (final Exception e) {
            throw new PgcnTechnicalException(e);
        }
    }
}
