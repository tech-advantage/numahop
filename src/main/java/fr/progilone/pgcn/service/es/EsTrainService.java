package fr.progilone.pgcn.service.es;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.progilone.pgcn.domain.AbstractDomainObject_;
import fr.progilone.pgcn.domain.train.Train;
import fr.progilone.pgcn.repository.es.EsTrainRepository;
import fr.progilone.pgcn.repository.es.helper.EsSearchOperation;
import fr.progilone.pgcn.repository.es.helper.EsSort;
import fr.progilone.pgcn.repository.train.TrainRepository;
import fr.progilone.pgcn.service.util.transaction.TransactionService;

@Service
public class EsTrainService extends AbstractElasticsearchOperations<Train> {

    private static final Logger LOG = LoggerFactory.getLogger(EsTrainService.class);

    private final TrainRepository trainRepository;
    private final EsTrainRepository esTrainRepository;
    private final TransactionService transactionService;

    @Value("${elasticsearch.bulk_size}")
    private Integer bulkSize;

    @Autowired
    public EsTrainService(final TrainRepository trainRepository,
                          final EsTrainRepository esTrainRepository,
                          final TransactionService transactionService) {
        this.trainRepository = trainRepository;
        this.esTrainRepository = esTrainRepository;
        this.transactionService = transactionService;
    }

    /**
     * Recherche d'unités documentaires
     *  @param rawSearches
     * @param rawFilters
     * @param libraries
     * @param fuzzy
     * @param page
     * @param size
     * @param rawSorts
     * @param facet
     */
    public Page<Train> search(final String[] rawSearches,
                              final String[] rawFilters,
                              final List<String> libraries,
                              final boolean fuzzy,
                              final Integer page,
                              final Integer size,
                              final String[] rawSorts,
                              final boolean facet) {
        final EsSearchOperation[] searches = EsSearchOperation.fromRawSearches(rawSearches);
        final EsSearchOperation[] filters = EsSearchOperation.fromRawFilters(rawFilters);
        final Sort sort = EsSort.fromRawSorts(rawSorts, SearchEntity.TRAIN);
        return esTrainRepository.search(searches, libraries, fuzzy, filters, new PageRequest(page, size, sort), facet);
    }

    /**
     * Indexation de la notice dans le moteur de recherche
     *
     * @param identifier
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public Train index(final String identifier) {
        final Train report = trainRepository.findOne(identifier);
        if (report != null) {
            esTrainRepository.indexEntity(report);
            return report;
        }
        return null;
    }

    /**
     * Indexation de la notice dans le moteur de recherche
     *
     * @param identifiers
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public Iterable<Train> index(final List<String> identifiers) {
        final List<Train> reports = trainRepository.findByIdentifierIn(identifiers);
        final List<Train> filteredDetails = reports.stream().filter(det -> StringUtils.isNotEmpty(det.getProjectId())).collect(Collectors.toList());

        if (!reports.isEmpty()) {
            esTrainRepository.indexEntities(filteredDetails);
        }
        return Collections.emptyList();
    }

    /**
     * Suppression de la notice du moteur de recherche
     *
     * @param report
     */
    @Override
    public void delete(final Train report) {
        esTrainRepository.deleteEntity(report);
    }

    /**
     * Suppression de plusieurs notices du moteur de recherche
     *
     * @param entities
     */
    @Override
    public void delete(final Collection<Train> entities) {
        esTrainRepository.deleteEntities(entities);
    }

    /**
     * Réindexation de toutes les notices disponibles
     *
     * @param index
     * @return
     */
    public long reindex(final String index) {
        
        long nbImported = 0;
        final AtomicReference<Page<Train>> pageRef = new AtomicReference<>();
        do {
            final int result = transactionService.executeInNewTransactionWithReturn(() -> {

                // Chargement des objets
                final Pageable pageable = pageRef.get() == null ?
                                          new PageRequest(0, bulkSize, Sort.Direction.ASC, AbstractDomainObject_.identifier.getName()) :
                                              pageRef.get().nextPageable();
                final Page<Train> pageOfObjects = trainRepository.findAll(pageable);
    
                // Traitement des trains
                final List<Train> entities = pageOfObjects.getContent();
                esTrainRepository.index(index, entities);

                pageRef.set(pageOfObjects);
                return entities.size();
            });

            nbImported += result;
            LOG.trace("{} / {} éléments indexés", nbImported, pageRef.get().getTotalElements());

        } while (pageRef.get() != null && pageRef.get().hasNext());

        return nbImported;
    }
}
