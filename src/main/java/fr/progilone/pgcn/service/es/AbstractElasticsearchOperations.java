package fr.progilone.pgcn.service.es;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.service.util.transaction.TransactionService;
import fr.progilone.pgcn.service.util.transaction.TransactionalJobRunner;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractElasticsearchOperations<T extends AbstractDomainObject, U> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractElasticsearchOperations.class);

    private final TransactionService transactionService;
    private final ElasticsearchOperations elasticsearchOperations;
    private final Integer bulkSize;
    private final Class<?> classEs;
    private final JpaRepository<T, String> crudRepository;
    private final ElasticsearchRepository<U, String> esRepository;

    public AbstractElasticsearchOperations(final TransactionService transactionService,
                                           final ElasticsearchOperations elasticsearchOperations,
                                           final Integer bulkSize,
                                           final Class<?> classEs,
                                           final JpaRepository<T, String> crudRepository,
                                           final ElasticsearchRepository<U, String> esRepository) {
        this.transactionService = transactionService;
        this.elasticsearchOperations = elasticsearchOperations;
        this.bulkSize = bulkSize;
        this.classEs = classEs;
        this.crudRepository = crudRepository;
        this.esRepository = esRepository;
    }

    /**
     * Types d'éléments recherchés
     */
    public enum SearchEntity {
        CONDREPORT,
        DELIVERY,
        DOCUNIT,
        LOT,
        PROJECT,
        TRAIN
    }

    /**
     * Indexation d'une seule entité
     */
    @Transactional(readOnly = true)
    public void index(final String identifier) {
        crudRepository.findById(identifier).ifPresent(r -> esRepository.save(convertToEsObject(r)));
    }

    /**
     * Indexation de plusieurs entités
     */
    @Transactional(readOnly = true)
    public void index(final List<String> identifiers) {
        final List<U> filteredDetails = crudRepository.findAllById(identifiers).stream().map(this::convertToEsObject).collect(Collectors.toList());
        if (!filteredDetails.isEmpty()) {
            esRepository.saveAll(filteredDetails);
        }
    }

    /**
     * Suppression d'une entité de l'index de recherche
     */
    public void delete(final String identifier) {
        esRepository.deleteById(identifier);
    }

    /**
     * Suppression d'entités de l'index de recherche
     */
    public void delete(final Collection<String> identifiers) {
        esRepository.deleteAllById(identifiers);
    }

    /**
     * Indexation asynchrone
     */
    @Async
    @Transactional(readOnly = true)
    public void indexAsync(final String identifier) {
        index(identifier);
    }

    /**
     * Indexation asynchrone de plusieurs entités
     */
    @Async
    @Transactional(readOnly = true)
    public void indexAsync(final List<String> identifiers) {
        index(identifiers);
    }

    /**
     * Suppression asynchrone
     */
    @Async
    public void deleteAsync(final String identifier) {
        delete(identifier);
    }

    /**
     * Suppression asynchrone
     */
    @Async
    public void deleteAsync(final Collection<String> identifiers) {
        delete(identifiers);
    }

    protected abstract U convertToEsObject(T domainObject);

    protected abstract List<String> findAllIdentifiersToIndex();

    /**
     * Réindexation de toutes les unités documentaires disponibles
     */
    public long reindex() {

        LOG.debug("Début d'indexation de {}", classEs.getSimpleName());

        elasticsearchOperations.indexOps(classEs).delete();
        elasticsearchOperations.indexOps(classEs).createWithMapping();

        final List<String> idsToIdex = findAllIdentifiersToIndex();

        new TransactionalJobRunner<>(idsToIdex, transactionService).autoSetMaxThreads().setReadOnly(true).setCommit(bulkSize).forEachGroup(bulkSize, ids -> {
            esRepository.saveAll(crudRepository.findAllById(ids).stream().map(this::convertToEsObject).collect(Collectors.toList()));
            return true;
        }).process();

        LOG.debug("Fin d'indexation de {}", classEs.getSimpleName());

        return idsToIdex.size();
    }
}
