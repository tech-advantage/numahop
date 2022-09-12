package fr.progilone.pgcn.service.exchange.internetarchive;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.exchange.internetarchive.InternetArchiveReport;
import fr.progilone.pgcn.service.es.EsInternetArchiveReportService;

/**
 * Gestion des opération IA asynchrone, notamment pour gérer l'indexation post-traitement
 * qui doit s'effectuer une fois la transaction du traitement principale commitée
 */
@Service
public class InternetArchiveServiceAsync {

    private static final Logger LOG = LoggerFactory.getLogger(InternetArchiveServiceAsync.class);

    private final EsInternetArchiveReportService esIaReportService;
    private final InternetArchiveService internetArchiveService;

    @Autowired
    public InternetArchiveServiceAsync(final EsInternetArchiveReportService esIaReportService,
                                       final InternetArchiveService internetArchiveService) {
        this.esIaReportService = esIaReportService;
        this.internetArchiveService = internetArchiveService;
    }

    /**
     * Pas de controle user : A reserver aux traitements automatiques
     */
    @Async
    public void createItem(final String docUnitId, final boolean automaticExport) {
        
        final InternetArchiveReport report = internetArchiveService.createItem(docUnitId, automaticExport);
        if(report != null){
            esIaReportService.indexAsync(report.getIdentifier());
        }
    }

    @Async
    public void createItem(final DocUnit docUnit, final InternetArchiveItemDTO item, final String userId) {
        
        final InternetArchiveReport report = internetArchiveService.createItem(docUnit, item, false, userId);
        if(report != null){
            esIaReportService.indexAsync(report.getIdentifier());
        }
    }

    /**
     * Lanceur de l'export automatique vers Archive.
     */
    @Scheduled(cron = "${cron.internetArchiveExport}")
    public void automaticInternetArchiveExport() {
        LOG.info("Lancement du Job internetArchiveExport...");
        final List<String> docsToExport = internetArchiveService.findDocUnitsReadyForArchiveExport();
        docsToExport.forEach(docId -> {
            LOG.info("Debut export vers ARCHIVE - DocUnit[{}]", docId);
            createItem(docId, true);
            LOG.info("Fin export vers ARCHIVE - DocUnit[{}]", docId);
        });
    }
}
