package fr.progilone.pgcn.service.exchange.exportftp;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.exportftpconfiguration.ExportFTPConfiguration;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.workflow.DocUnitWorkflow;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import fr.progilone.pgcn.service.document.DocUnitService;
import fr.progilone.pgcn.service.document.ui.UIDocUnitService;
import fr.progilone.pgcn.service.exportftpconfiguration.ExportFTPConfigurationService;
import fr.progilone.pgcn.service.library.LibraryService;
import fr.progilone.pgcn.service.workflow.DocUnitWorkflowService;
import fr.progilone.pgcn.service.workflow.WorkflowService;

/**
 * Service gérant l'export local
 */
@Service
public class ExportFtpService {

    private static final Logger LOG = LoggerFactory.getLogger(ExportFtpService.class);

    private final ExportFTPConfigurationService configurationService;
    private final LibraryService libraryService;
    private final DocUnitWorkflowService docUnitWorkflowService;
    private final UIDocUnitService uiDocUnitService;
    private final WorkflowService workflowService;
    private final DocUnitService docUnitService;

    public ExportFtpService(final ExportFTPConfigurationService configurationService,
                            final LibraryService libraryService,
                            final DocUnitWorkflowService docUnitWorkflowService,
                            final UIDocUnitService uiDocUnitService,
                            final WorkflowService workflowService,
                            final DocUnitService docUnitService) {
        this.configurationService = configurationService;
        this.libraryService = libraryService;
        this.docUnitWorkflowService = docUnitWorkflowService;
        this.uiDocUnitService = uiDocUnitService;
        this.workflowService = workflowService;
        this.docUnitService = docUnitService;
    }

    @Scheduled(cron = "${cron.localExport}")
    @Transactional
    public void localExportCron() {

        LOG.info("Lancement du cronjob localExport ...");
        final List<DocUnit> docsToExport = findDocUnitsReadyForLocalExport();
        docsToExport.forEach(doc -> {
            LOG.info("Debut export local - DocUnit[{}]", doc.getPgcnId());
            try {
                if (exportDocToLocalFtp(doc)) {
                    updateDocUnitLocalExportStatus(doc, DocUnit.ExportStatus.SENT);
                    LOG.info("Fin export local - DocUnit[{}]", doc.getPgcnId());
                    if (workflowService.isStateRunning(doc.getIdentifier(), WorkflowStateKey.DIFFUSION_DOCUMENT_LOCALE)) {
                        workflowService.processAutomaticState(doc.getIdentifier(), WorkflowStateKey.DIFFUSION_DOCUMENT_LOCALE);
                    }
                } else {
                    updateDocUnitLocalExportStatus(doc, DocUnit.ExportStatus.FAILED);
                    LOG.error("Export local non effectué - DocUnit[{}]", doc.getPgcnId());
                    if (workflowService.isStateRunning(doc.getIdentifier(), WorkflowStateKey.DIFFUSION_DOCUMENT_LOCALE)) {
                        workflowService.rejectAutomaticState(doc.getIdentifier(), WorkflowStateKey.DIFFUSION_DOCUMENT_LOCALE);
                    }
                }
            } catch (final IOException e) {
                LOG.error("Fin export local avec erreur - DocUnit[{}] - Message {}", doc.getPgcnId(), e.getMessage());
            }
        });
    }

    private boolean exportDocToLocalFtp(final DocUnit doc) throws IOException {
        ExportFTPConfiguration ftpConfig = Optional.of(doc.getLot().getActiveExportFTPConfiguration())
            .orElse(doc.getProject().getActiveExportFTPConfiguration());

        if (ftpConfig != null) {
            final List<String> exportTypes = new ArrayList<>();
            if (ftpConfig.isExportAipSip()) {
                exportTypes.add("AIP");
            }
            if (ftpConfig.isExportMaster()) {
                exportTypes.add("MASTER");
            }
            if (ftpConfig.isExportMets()) {
                exportTypes.add("METS");
            }
            if (ftpConfig.isExportPdf()) {
                exportTypes.add("PDF");
            }
            if (ftpConfig.isExportThumb()) {
                exportTypes.add("THUMBNAIL");
            }
            if (ftpConfig.isExportView()) {
                exportTypes.add("VIEW");
            }
            if(ftpConfig.isExportAlto()) {
                exportTypes.add("ALTO");
            }

            if (!exportTypes.isEmpty()) {
                return uiDocUnitService.massExportToFtp(Arrays.asList(doc), exportTypes, doc.getLibrary());
            }
        } else {
            updateDocUnitLocalExportStatus(doc, DocUnit.ExportStatus.FAILED);
            LOG.warn("FTP Conf. introuvable => DocUnit[{}] - Export local impossible.", doc.getIdentifier());
            return false;
        }
        return false;
    }

    @Transactional(readOnly = true)
    private List<DocUnit> findDocUnitsReadyForLocalExport() {
        final List<DocUnit> docsToExport = new ArrayList<>();

        final List<Library> libraries = libraryService.findAllByActive(true);
        libraries.stream()
                 .filter(lib -> CollectionUtils.isNotEmpty(configurationService.findByLibraryAndActive(lib, true)))
                 .forEach(lib -> {
                     final List<DocUnit> localExportableDoc = docUnitWorkflowService.findDocUnitWorkflowsForLocalExport(lib.getIdentifier())
                                                                                    .stream()
                                                                                    .map(DocUnitWorkflow::getDocUnit)
                                                                                    .peek(doc -> doc.setLibrary(lib))
                                                                                    .collect(Collectors.toList());
                     docsToExport.addAll(localExportableDoc);
                 });
        return docsToExport;
    }

    @Transactional
    public void updateDocUnitLocalExportStatus(final DocUnit docUnit, final DocUnit.ExportStatus status) {
        docUnit.setLocalExportStatus(status);
        docUnit.setLocalExportDate(LocalDateTime.now());
        docUnitService.saveWithoutValidation(docUnit);
    }

}
