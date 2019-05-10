package fr.progilone.pgcn.service.exchange.internetarchive;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.exchange.internetarchive.InternetArchiveReport;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import fr.progilone.pgcn.service.es.EsInternetArchiveReportService;
import fr.progilone.pgcn.service.workflow.WorkflowService;

/**
 * Gestion des opération IA asynchrone, notamment pour gérer l'indexation post-traitement
 * qui doit s'effectuer une fois la transaction du traitement principale commitée
 */
@Service
public class InternetArchiveServiceAsync {

    private static final Logger LOG = LoggerFactory.getLogger(InternetArchiveServiceAsync.class);

    private final EsInternetArchiveReportService esIaReportService;
    private final InternetArchiveService internetArchiveService;
    private final WorkflowService workflowService;

    @Autowired
    public InternetArchiveServiceAsync(final EsInternetArchiveReportService esIaReportService,
                                       final InternetArchiveService internetArchiveService,
                                       final WorkflowService workflowService) {
        this.esIaReportService = esIaReportService;
        this.internetArchiveService = internetArchiveService;
        this.workflowService = workflowService;
    }

    /**
     * Pas de controle user : A reserver aux traitements automatiques
     * 
     * @param docUnit
     * @param item
     */
    @Async
    public void createItem(final DocUnit docUnit, final InternetArchiveItemDTO item) {
        final InternetArchiveReport report = internetArchiveService.createItem(docUnit, item);
        if (workflowService.isStateRunning(docUnit.getIdentifier(), WorkflowStateKey.DIFFUSION_DOCUMENT)) {
            workflowService.processAutomaticState(docUnit.getIdentifier(), WorkflowStateKey.DIFFUSION_DOCUMENT);
        }
        esIaReportService.indexAsync(report.getIdentifier());
    }

    @Async
    public void createItem(final DocUnit docUnit, final InternetArchiveItemDTO item, final String userId) {
        final InternetArchiveReport report = internetArchiveService.createItem(docUnit, item);
        if (workflowService.isStateRunning(docUnit.getIdentifier(), WorkflowStateKey.DIFFUSION_DOCUMENT)) {
            workflowService.processState(docUnit.getIdentifier(), WorkflowStateKey.DIFFUSION_DOCUMENT, userId);
        }
        esIaReportService.indexAsync(report.getIdentifier());
    }

    /**
     * Lanceur de l'export automatique vers Archive.
     */
    @Transactional
    @Scheduled(cron = "${cron.internetArchiveExport}")
    public void automaticInternetArchiveExport() {
        LOG.info("Lancement du Job internetArchiveExport...");
        final List<DocUnit> docsToExport = internetArchiveService.findDocUnitsReadyForArchiveExport();
        docsToExport.forEach(doc -> {
            LOG.info("Debut export vers ARCHIVE - DocUnit[{}]", doc.getIdentifier());
            final InternetArchiveItemDTO item = internetArchiveService.prepareItem(doc);
            createItem(doc, item);

            LOG.info("Fin export vers ARCHIVE - DocUnit[{}]", doc.getIdentifier());
        });
    }
}
