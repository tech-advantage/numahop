package fr.progilone.pgcn.service.es;

import fr.progilone.pgcn.domain.es.train.EsTrain;
import fr.progilone.pgcn.domain.train.Train;
import fr.progilone.pgcn.repository.es.EsTrainRepository;
import fr.progilone.pgcn.repository.es.helper.EsSearchOperation;
import fr.progilone.pgcn.repository.es.helper.EsSort;
import fr.progilone.pgcn.repository.train.TrainRepository;
import fr.progilone.pgcn.service.util.transaction.TransactionService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

@Service
public class EsTrainService extends AbstractElasticsearchOperations<Train, EsTrain> {

    private final TrainRepository trainRepository;
    private final EsTrainRepository esTrainRepository;

    @Autowired
    public EsTrainService(final TrainRepository trainRepository,
                          final EsTrainRepository esTrainRepository,
                          final TransactionService transactionService,
                          final ElasticsearchOperations elasticsearchOperations,
                          @Value("${elasticsearch.bulk_size}") final Integer bulkSize) {
        super(transactionService, elasticsearchOperations, bulkSize, EsTrain.class, trainRepository, esTrainRepository);
        this.trainRepository = trainRepository;
        this.esTrainRepository = esTrainRepository;
    }

    /**
     * Recherche d'unités documentaires
     */
    public Page<EsTrain> search(final String[] rawSearches,
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
        return esTrainRepository.search(searches, libraries, fuzzy, filters, PageRequest.of(page, size, sort), facet);
    }

    @Override
    protected EsTrain convertToEsObject(final Train domainObject) {
        return EsTrain.from(domainObject);
    }

    @Override
    protected List<String> findAllIdentifiersToIndex() {
        return trainRepository.findAllIdentifiers();
    }

}
