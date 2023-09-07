package fr.progilone.pgcn.service.es;

import fr.progilone.pgcn.domain.es.lot.EsLot;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.repository.es.EsLotRepository;
import fr.progilone.pgcn.repository.es.helper.EsSearchOperation;
import fr.progilone.pgcn.repository.es.helper.EsSort;
import fr.progilone.pgcn.repository.lot.LotRepository;
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
public class EsLotService extends AbstractElasticsearchOperations<Lot, EsLot> {

    private final LotRepository lotRepository;
    private final EsLotRepository esLotRepository;

    @Autowired
    public EsLotService(final LotRepository lotRepository,
                        final EsLotRepository esLotRepository,
                        final TransactionService transactionService,
                        final ElasticsearchOperations elasticsearchOperations,
                        @Value("${elasticsearch.bulk_size}") final Integer bulkSize) {
        super(transactionService, elasticsearchOperations, bulkSize, EsLot.class, lotRepository, esLotRepository);
        this.lotRepository = lotRepository;
        this.esLotRepository = esLotRepository;
    }

    /**
     * Recherche d'unités documentaires
     */
    public Page<EsLot> search(final String[] rawSearches,
                              final String[] rawFilters,
                              final List<String> libraries,
                              final boolean fuzzy,
                              final Integer page,
                              final Integer size,
                              final String[] rawSorts,
                              final boolean facet) {
        final EsSearchOperation[] searches = EsSearchOperation.fromRawSearches(rawSearches);
        final EsSearchOperation[] filters = EsSearchOperation.fromRawFilters(rawFilters);
        final Sort sort = EsSort.fromRawSorts(rawSorts, SearchEntity.LOT);
        return esLotRepository.search(searches, libraries, fuzzy, filters, PageRequest.of(page, size, sort), facet);
    }

    @Override
    protected EsLot convertToEsObject(final Lot domainObject) {
        return EsLot.from(domainObject);
    }

    @Override
    protected List<String> findAllIdentifiersToIndex() {
        return lotRepository.findAllIdentifiers();
    }
}
