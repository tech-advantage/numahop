package fr.progilone.pgcn.service.exchange.omeka;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.DocUnit.ExportStatus;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import fr.progilone.pgcn.service.workflow.WorkflowService;


@Service
public class OmekaRequestHandlerService {

    
    private static final Logger LOG = LoggerFactory.getLogger(OmekaRequestHandlerService.class);

    private final OmekaService omekaService;
    private final WorkflowService workflowService;


    @Autowired
    public OmekaRequestHandlerService(final OmekaService omekaService, final WorkflowService workflowService) {
        this.omekaService = omekaService;
        this.workflowService = workflowService;
    }
    
    
    /**
     * Lanceur de l'export automatique vers Omeka.
     */
    @Transactional
    @Scheduled(cron = "${cron.omekaExport}")
    public void automaticOmekaExport() {
        LOG.info("Lancement du Job omekaExport...");
        final List<DocUnit> docsToExport = omekaService.findDocUnitsReadyForOmekaExport();
        docsToExport.forEach(doc -> {
            LOG.info("Debut export vers OMEKA - DocUnit[{}]", doc.getIdentifier());
            if (omekaService.exportDocToOmeka(doc, false, false, false)) {
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
