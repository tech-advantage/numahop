package fr.progilone.pgcn.service.es;

import fr.progilone.pgcn.domain.delivery.Delivery;
import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReport;
import fr.progilone.pgcn.domain.exchange.cines.CinesReport;
import fr.progilone.pgcn.domain.exchange.internetarchive.InternetArchiveReport;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.domain.train.Train;
import fr.progilone.pgcn.exception.PgcnUncheckedException;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.CustomMappingBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.mapping.ElasticsearchPersistentEntity;
import org.springframework.data.elasticsearch.core.query.AliasBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalTime;

/**
 * Gestion des opérations elasticsearch, création d'index, de mapping en utilisant les alias
 *
 * @see ElasticsearchTemplate
 */
@Service
public class IndexManagerService {

    private static final Logger LOG = LoggerFactory.getLogger(IndexManagerService.class);
    private static final String INDEX_FORMAT = "%1$s-%2$tY%2$tm%2$td-%2$tH%2$tM%2$tS";
    private static final String SETTING_PATH = "/config/elasticsearch/settings_pgcn.json";

    private final EsBibliographicRecordService esBibliographicRecordService;
    private final EsDocUnitService esDocUnitService;
    private final EsCinesReportService esCinesReportService;
    private final EsConditionReportService esConditionReportService;
    private final EsInternetArchiveReportService esIaReportService;
    private final EsProjectService esProjectService;
    private final EsLotService esLotService;
    private final EsTrainService esTrainService;
    private final EsDeliveryService esDeliveryService;
    private final ElasticsearchTemplate elasticsearchTemplate;

    @Value("${elasticsearch.index.name}")
    private String elasticsearchIndexName;
    // Classes indexées dans elasticsearch; elle doivent être annotées @Document
    private final Class[] indexedClasses = new Class[] {Delivery.class,
                                                        Train.class,
                                                        Lot.class,
                                                        Project.class,
                                                        BibliographicRecord.class,
                                                        ConditionReport.class,
                                                        CinesReport.class,
                                                        InternetArchiveReport.class,
                                                        DocUnit.class};

    @Autowired
    public IndexManagerService(final EsBibliographicRecordService esBibliographicRecordService,
                               final EsDocUnitService esDocUnitService,
                               final EsCinesReportService esCinesReportService,
                               final EsConditionReportService esConditionReportService,
                               final EsInternetArchiveReportService esIaReportService,
                               final EsProjectService esProjectService,
                               final EsLotService esLotService,
                               final EsTrainService esTrainService,
                               final EsDeliveryService esDeliveryService,
                               final ElasticsearchTemplate elasticsearchTemplate) {
        this.esBibliographicRecordService = esBibliographicRecordService;
        this.esDocUnitService = esDocUnitService;
        this.esCinesReportService = esCinesReportService;
        this.esConditionReportService = esConditionReportService;
        this.esIaReportService = esIaReportService;
        this.esProjectService = esProjectService;
        this.esLotService = esLotService;
        this.esTrainService = esTrainService;
        this.esDeliveryService = esDeliveryService;
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @PostConstruct
    public void init() {
        // Initialisation d'elasticsearch
        createIndexIfNotCreated(elasticsearchIndexName, indexedClasses);
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
        long nbDocUnits = 0, nbRecords = 0, nbReports = 0, nbCines = 0, nbIa = 0, nbProjects = 0, nbLots = 0, nbTrains = 0, nbDeliveries = 0;

        // Création du nouvel index
        final String newIndex = createIndex(elasticsearchIndexName, indexedClasses);

        if (newIndex != null) {
            try {
                // Indexation des objets dans le nouvel index
                nbDocUnits = esDocUnitService.reindex(newIndex);
                nbRecords = esBibliographicRecordService.reindex(newIndex);
                nbReports = esConditionReportService.reindex(newIndex);
                nbCines = esCinesReportService.reindex(newIndex);
                nbIa = esIaReportService.reindex(newIndex);
                nbProjects = esProjectService.reindex(newIndex);
                nbLots = esLotService.reindex(newIndex);
                nbTrains = esTrainService.reindex(newIndex);
                nbDeliveries = esDeliveryService.reindex(newIndex);

                // Modification de l'alias sur le nouvel index
                moveAlias(elasticsearchIndexName, newIndex);

                final Duration duration = Duration.between(start, LocalTime.now());
                LOG.info("Fin de l'indexation après {} secondes\n"
                         + "\tUnités documentaires: {}\n"
                         + "\tNotices: {}\n"
                         + "\tConstats d'état: {}\n"
                         + "\tArchivage CINES: {}\n"
                         + "\tExports IA: {}\n"
                         + "\tProjets: {}\n"
                         + "\tLots: {}\n"
                         + "\tTrains: {}\n"
                         + "\tLivraisons: {}",
                         duration.getSeconds(),
                         nbDocUnits,
                         nbRecords,
                         nbReports,
                         nbCines,
                         nbIa,
                         nbProjects,
                         nbLots,
                         nbTrains,
                         nbDeliveries);

            } catch (final Exception e) {
                LOG.error(e.getMessage(), e);
                deleteIndex(newIndex);
            }
        }
    }

    /**
     * Création de l'index en vue d'une réindexation; l'alias n'est pas modifié.
     * Lister les classes en mettant les enfants avant les parents
     *
     * @param alias
     * @param entityClasses
     * @return nom de l'index créé
     */
    private String createIndex(final String alias, final Class<?>... entityClasses) {
        final String index = String.format(INDEX_FORMAT, alias, System.currentTimeMillis());

        try {
            LOG.info("Création de l'index {}", index);
            // Index
            createIndexWithSettings(index);
            // Mappings
            for (final Class<?> entityClass : entityClasses) {
                putMapping(index, entityClass);
            }
            return index;

        } catch (ElasticsearchException exception) {
            LOG.error("Une erreur est survenue lors de la création de l'index {}: {}", index, exception.getDetailedMessage());
            deleteIndex(index);
            return null;
        } catch (Exception exception) {
            LOG.error("Une erreur est survenue lors de la création de l'index {}: {}", index, exception.getMessage());
            deleteIndex(index);
            return null;
        }
    }

    /**
     * Création initiale de l'index + mapping + alias, si l'alias n'existe pas déjà.
     * Lister les classes en mettant les enfants avant les parents
     *
     * @param alias
     * @param entityClasses
     */
    private void createIndexIfNotCreated(final String alias, final Class<?>... entityClasses) {
        final String index = String.format(INDEX_FORMAT, alias, System.currentTimeMillis());

        try {
            if (!elasticsearchTemplate.indexExists(alias)) {
                LOG.info("Création de l'index {} (alias {})", index, alias);
                // Index
                createIndexWithSettings(index);
                // Mappings
                for (final Class<?> entityClass : entityClasses) {
                    putMapping(index, entityClass);
                }
                // Alias
                elasticsearchTemplate.addAlias(new AliasBuilder().withIndexName(index).withAliasName(alias).build());
            } else {
                LOG.debug("L'alias {} a été trouvé", alias);
            }
        } catch (ElasticsearchException exception) {
            LOG.error("Une erreur est survenue lors de la création de l'index {} (alias {}): {}", index, alias, exception.getDetailedMessage());
            deleteIndex(index);
        }
    }

    /**
     * Création d'un index avec les settings récupérés sur la classe spécifiée
     *
     * @param index
     * @return
     */
    private void createIndexWithSettings(final String index) {
        final String settings = ElasticsearchTemplate.readFileFromClasspath(SETTING_PATH);
        if (StringUtils.isNotBlank(settings)) {
            elasticsearchTemplate.createIndex(index, settings);
        } else {
            elasticsearchTemplate.createIndex(index);
        }
    }

    /**
     * Suppression de l'index spécifié
     *
     * @param index
     */
    private void deleteIndex(final String index) {
        LOG.debug("Suppression de l'index {}", index);
        elasticsearchTemplate.deleteIndex(index);
    }

    /**
     * Suppression de l'alias courant, et positionnement de cet alias sur l'index toIndex
     *
     * @param alias
     * @param toIndex
     */
    private void moveAlias(final String alias, final String toIndex) {
        LOG.debug("Déplacement de l'alias {} sur l'index {}", alias, toIndex);
        // Suppression de l'index cible de l'alias
        elasticsearchTemplate.deleteIndex(alias);
        // Positionnement de l'alias sur le nouvel index
        elasticsearchTemplate.addAlias(new AliasBuilder().withIndexName(toIndex).withAliasName(alias).build());
    }

    /**
     * Création d'un mapping pour une classe, il s'agit de ElasticssearchTemplate.putMapping(Class<T> clazz), avec le paramètre index en plus
     *
     * @param index
     * @param clazz
     * @param <T>
     * @return
     */
    private <T> boolean putMapping(final String index, final Class<T> clazz) {
        final ElasticsearchPersistentEntity<T> persistentEntity = elasticsearchTemplate.getPersistentEntityFor(clazz);
        final XContentBuilder xContentBuilder;
        try {
            xContentBuilder = CustomMappingBuilder.buildMapping(clazz,
                                                                persistentEntity.getIndexType(),
                                                                persistentEntity.getIdProperty().getFieldName(),
                                                                persistentEntity.getParentType());
        } catch (Exception e) {
            throw new PgcnUncheckedException("Failed to build mapping for " + clazz.getSimpleName(), e);
        }
        return elasticsearchTemplate.putMapping(index, elasticsearchTemplate.getPersistentEntityFor(clazz).getIndexType(), xContentBuilder);
    }
}
