package fr.progilone.pgcn.service.es;

import java.time.Duration;
import java.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Gestion des opérations elasticsearch, création d'index, de mapping en utilisant les alias
 */
@Service
public class IndexManagerService {

    private static final Logger LOG = LoggerFactory.getLogger(IndexManagerService.class);

    private final EsDocUnitService esDocUnitService;
    private final EsConditionReportService esConditionReportService;
    private final EsProjectService esProjectService;
    private final EsLotService esLotService;
    private final EsTrainService esTrainService;
    private final EsDeliveryService esDeliveryService;

    @Autowired
    public IndexManagerService(final EsDocUnitService esDocUnitService,
                               final EsConditionReportService esConditionReportService,
                               final EsProjectService esProjectService,
                               final EsLotService esLotService,
                               final EsTrainService esTrainService,
                               final EsDeliveryService esDeliveryService) {
        this.esDocUnitService = esDocUnitService;
        this.esConditionReportService = esConditionReportService;
        this.esProjectService = esProjectService;
        this.esLotService = esLotService;
        this.esTrainService = esTrainService;
        this.esDeliveryService = esDeliveryService;
    }

    /**
     * Job de réindexation
     */
    @Scheduled(cron = "${cron.rebuildIndex}")
    public void rebuildIndexCron() {
        LOG.debug("Lancement du cronjob rebuildIndexCron...");
        indexAsync();
    }

    /**
     * Indexation de la totalité des unités documentaires, sans interruption du moteur de recherche
     */
    @Async
    public void indexAsync() {
        LOG.info("Début de l'indexation");
        final LocalTime start = LocalTime.now();

        try {
            // Indexation des objets dans le nouvel index
            final long nbDocUnits = esDocUnitService.reindex();
            final long nbReports = esConditionReportService.reindex();
            final long nbProjects = esProjectService.reindex();
            final long nbLots = esLotService.reindex();
            final long nbTrains = esTrainService.reindex();
            final long nbDeliveries = esDeliveryService.reindex();

            final Duration duration = Duration.between(start, LocalTime.now());
            LOG.info("Fin de l'indexation après {} secondes : {} unités documentaires, {} constats d'état, {} projets, {} lots, {} trains, {} livraisons",
                     duration.getSeconds(),
                     nbDocUnits,
                     nbReports,
                     nbProjects,
                     nbLots,
                     nbTrains,
                     nbDeliveries);

        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
