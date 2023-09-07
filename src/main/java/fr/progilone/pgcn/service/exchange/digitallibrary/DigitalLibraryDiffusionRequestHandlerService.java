package fr.progilone.pgcn.service.exchange.digitallibrary;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import fr.progilone.pgcn.service.workflow.WorkflowService;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class DigitalLibraryDiffusionRequestHandlerService {

    private static final Logger LOG = LoggerFactory.getLogger(DigitalLibraryDiffusionRequestHandlerService.class);

    @Value("${instance.libraries}")
    private String[] instanceLibraries;

    @Value("${services.digitalLibraryDiffusion.cache}")
    private String digitalLibraryDiffusionDir;

    private final DigitalLibraryDiffusionService digitalLibraryDiffusionService;
    private final WorkflowService workflowService;

    @Autowired
    public DigitalLibraryDiffusionRequestHandlerService(final DigitalLibraryDiffusionService digitalLibraryDiffusionService, final WorkflowService workflowService) {
        this.digitalLibraryDiffusionService = digitalLibraryDiffusionService;
        this.workflowService = workflowService;
    }

    /**
     * Initialisation d'un répertoire de stockage
     */
    public void initializeStorage(final String digitalLibraryDiffusionDir) {

        if (instanceLibraries != null) {
            // 1 disk space per library
            Arrays.asList(instanceLibraries).forEach(lib -> {
                try {
                    FileUtils.forceMkdir(new File(digitalLibraryDiffusionDir, lib));
                } catch (final IOException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            });
        }
    }

    /**
     * Lanceur de l'export automatique vers la bibliothèque numérique.
     */
    @Scheduled(cron = "${cron.digitalLibraryExport}")
    public void automaticDigitalLibraryExport() {
        LOG.info("Lancement du Job digitalLibraryExport...");
        final List<DocUnit> docsToExport = digitalLibraryDiffusionService.findDocUnitsReadyForDigitalLibraryExportByLibrary();
        int i = 0;
        boolean lastDoc = false;
        for (final DocUnit doc : docsToExport) {
            if (++i == docsToExport.size()) {
                lastDoc = true;
            }
            if (digitalLibraryDiffusionService.exportDocToDigitalLibrary(doc, true, i == 1, lastDoc)) {
                if (workflowService.isStateRunning(doc.getIdentifier(), WorkflowStateKey.DIFFUSION_DOCUMENT_DIGITAL_LIBRARY)) {
                    workflowService.processAutomaticState(doc.getIdentifier(), WorkflowStateKey.DIFFUSION_DOCUMENT_DIGITAL_LIBRARY);
                }
            } else {
                if (workflowService.isStateRunning(doc.getIdentifier(), WorkflowStateKey.DIFFUSION_DOCUMENT_DIGITAL_LIBRARY)) {
                    workflowService.rejectAutomaticState(doc.getIdentifier(), WorkflowStateKey.DIFFUSION_DOCUMENT_DIGITAL_LIBRARY);
                }
            }
        }
        LOG.info("Fin export vers la bibliothèque numérique");
    }

    /**
     * Export manuel.
     */
    @Async
    public void exportDocToDigitalLibrary(final DocUnit doc, final String userId, final boolean multiple, final boolean firstDoc, final boolean lastDoc) {
        if (digitalLibraryDiffusionService.exportDocToDigitalLibrary(doc, multiple, firstDoc, lastDoc)) {
            if (workflowService.isStateRunning(doc.getIdentifier(), WorkflowStateKey.DIFFUSION_DOCUMENT_DIGITAL_LIBRARY)) {
                workflowService.processState(doc.getIdentifier(), WorkflowStateKey.DIFFUSION_DOCUMENT_DIGITAL_LIBRARY, userId);
            }
        } else {
            if (workflowService.isStateRunning(doc.getIdentifier(), WorkflowStateKey.DIFFUSION_DOCUMENT_DIGITAL_LIBRARY)) {
                workflowService.rejectState(doc.getIdentifier(), WorkflowStateKey.DIFFUSION_DOCUMENT_DIGITAL_LIBRARY, userId);
            }
        }

    }
}
