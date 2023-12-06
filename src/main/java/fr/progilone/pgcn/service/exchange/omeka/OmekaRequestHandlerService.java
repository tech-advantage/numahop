package fr.progilone.pgcn.service.exchange.omeka;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.DocUnit.ExportStatus;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import fr.progilone.pgcn.repository.document.DocUnitRepository;
import fr.progilone.pgcn.service.administration.omeka.OmekaConfigurationService;
import fr.progilone.pgcn.service.library.LibraryService;
import fr.progilone.pgcn.service.util.transaction.TransactionService;
import fr.progilone.pgcn.service.util.transaction.TransactionalJobRunner;
import fr.progilone.pgcn.service.workflow.WorkflowService;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class OmekaRequestHandlerService {

    private static final Logger LOG = LoggerFactory.getLogger(OmekaRequestHandlerService.class);

    private final OmekaService omekaService;
    private final WorkflowService workflowService;
    private final OmekaConfigurationService omekaConfigurationService;
    private final LibraryService libraryService;
    private final TransactionService transactionService;
    private final DocUnitRepository docUnitRepository;

    @Autowired
    public OmekaRequestHandlerService(final OmekaService omekaService,
                                      final WorkflowService workflowService,
                                      final OmekaConfigurationService omekaConfigurationService,
                                      final LibraryService libraryService,
                                      final TransactionService transactionService,
                                      final DocUnitRepository docUnitRepository) {
        this.omekaService = omekaService;
        this.workflowService = workflowService;
        this.omekaConfigurationService = omekaConfigurationService;
        this.libraryService = libraryService;
        this.transactionService = transactionService;
        this.docUnitRepository = docUnitRepository;
    }

    /**
     * Lanceur de l'export automatique vers Omeka.
     */
    @Scheduled(cron = "${cron.omekaExport}")
    public void automaticOmekaExport() {
        LOG.info("Lancement du Job omekaExport...");

        final List<Library> libraries = libraryService.findAllByActive(true);
        libraries.stream().filter(lib -> CollectionUtils.isNotEmpty(omekaConfigurationService.findByLibraryAndActive(lib, true))).forEach(lib -> {
            final List<String> docsToExport = omekaService.findDocUnitsReadyForOmekaExport(lib);
            final AtomicInteger i = new AtomicInteger(0);

            TransactionalJobRunner<String> job = new TransactionalJobRunner<>(docsToExport, transactionService);

            job.setCommit(1).setReadOnly(true).setElementName("Doc Unit Exporting Omeka").forEach(docId -> {
                boolean lastDoc = i.incrementAndGet() == docsToExport.size();
                DocUnit doc = docUnitRepository.getReferenceById(docId);

                LOG.info("Debut export vers OMEKA - DocUnit[{}]", doc.getIdentifier());

                if (omekaService.exportDocToOmeka(doc, true, lastDoc, i.get() == 1)) {
                    omekaService.updateDocUnitOmekaStatus(doc, ExportStatus.SENT);
                    if (workflowService.isStateRunning(doc.getIdentifier(), WorkflowStateKey.DIFFUSION_DOCUMENT_OMEKA)) {
                        workflowService.processAutomaticState(doc.getIdentifier(), WorkflowStateKey.DIFFUSION_DOCUMENT_OMEKA);
                    }
                } else {
                    omekaService.updateDocUnitOmekaStatus(doc, ExportStatus.FAILED);
                    if (workflowService.isStateRunning(doc.getIdentifier(), WorkflowStateKey.DIFFUSION_DOCUMENT_OMEKA)) {
                        workflowService.rejectAutomaticState(doc.getIdentifier(), WorkflowStateKey.DIFFUSION_DOCUMENT_OMEKA);
                    }
                }
                LOG.info("Fin export vers OMEKA - DocUnit[{}] - Statut: {}", doc.getIdentifier(), doc.getOmekaExportStatus().name());
                return true;
            }).process();
        });
    }

    /**
     * Export manuel.
     */
    @Async
    public void exportDocToOmeka(final DocUnit docUnit, final String userId, final boolean multiple, final boolean lastDoc, final boolean firstDoc) {
        if (omekaService.exportDocToOmeka(docUnit, multiple, lastDoc, firstDoc)) {
            if (workflowService.isStateRunning(docUnit.getIdentifier(), WorkflowStateKey.DIFFUSION_DOCUMENT_OMEKA)) {
                workflowService.processState(docUnit.getIdentifier(), WorkflowStateKey.DIFFUSION_DOCUMENT_OMEKA, userId);
            }
        } else {
            if (workflowService.isStateRunning(docUnit.getIdentifier(), WorkflowStateKey.DIFFUSION_DOCUMENT_OMEKA)) {
                workflowService.rejectState(docUnit.getIdentifier(), WorkflowStateKey.DIFFUSION_DOCUMENT_OMEKA, userId);
            }
        }

    }

}
