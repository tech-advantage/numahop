package fr.progilone.pgcn.service.es;

import fr.progilone.pgcn.domain.delivery.Delivery;
import fr.progilone.pgcn.domain.es.delivery.EsDelivery;
import fr.progilone.pgcn.repository.delivery.DeliveryRepository;
import fr.progilone.pgcn.repository.es.EsDeliveryRepository;
import fr.progilone.pgcn.repository.es.helper.EsSearchOperation;
import fr.progilone.pgcn.repository.es.helper.EsSort;
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
public class EsDeliveryService extends AbstractElasticsearchOperations<Delivery, EsDelivery> {

    private final DeliveryRepository deliveryRepository;
    private final EsDeliveryRepository esDeliveryRepository;

    @Value("${elasticsearch.bulk_size}")
    private Integer bulkSize;

    @Autowired
    public EsDeliveryService(final DeliveryRepository deliveryRepository,
                             final EsDeliveryRepository esDeliveryRepository,
                             final TransactionService transactionService,
                             final ElasticsearchOperations elasticsearchOperations,
                             @Value("${elasticsearch.bulk_size}") final Integer bulkSize) {
        super(transactionService, elasticsearchOperations, bulkSize, EsDelivery.class, deliveryRepository, esDeliveryRepository);
        this.deliveryRepository = deliveryRepository;
        this.esDeliveryRepository = esDeliveryRepository;
    }

    /**
     * Recherche d'unités documentaires
     */
    public Page<EsDelivery> search(final String[] rawSearches,
                                   final String[] rawFilters,
                                   final List<String> libraries,
                                   final boolean fuzzy,
                                   final Integer page,
                                   final Integer size,
                                   final String[] rawSorts,
                                   final boolean facet) {
        final EsSearchOperation[] searches = EsSearchOperation.fromRawSearches(rawSearches);
        final EsSearchOperation[] filters = EsSearchOperation.fromRawFilters(rawFilters);
        final Sort sort = EsSort.fromRawSorts(rawSorts, SearchEntity.DELIVERY);
        return esDeliveryRepository.search(searches, libraries, fuzzy, filters, PageRequest.of(page, size, sort), facet);
    }

    @Override
    protected EsDelivery convertToEsObject(final Delivery domainObject) {
        return EsDelivery.from(domainObject);
    }

    @Override
    protected List<String> findAllIdentifiersToIndex() {
        return deliveryRepository.findAllIdentifier();
    }
}
