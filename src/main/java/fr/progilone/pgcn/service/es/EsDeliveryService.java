package fr.progilone.pgcn.service.es;

import fr.progilone.pgcn.domain.AbstractDomainObject_;
import fr.progilone.pgcn.domain.delivery.Delivery;
import fr.progilone.pgcn.repository.delivery.DeliveryRepository;
import fr.progilone.pgcn.repository.es.EsDeliveryRepository;
import fr.progilone.pgcn.repository.es.helper.EsSearchOperation;
import fr.progilone.pgcn.repository.es.helper.EsSort;
import fr.progilone.pgcn.service.util.transaction.TransactionService;
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
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EsDeliveryService extends AbstractElasticsearchOperations<Delivery> {

    private static final Logger LOG = LoggerFactory.getLogger(EsDeliveryService.class);

    private final DeliveryRepository deliveryRepository;
    private final EsDeliveryRepository esDeliveryRepository;
    private final TransactionService transactionService;

    @Value("${elasticsearch.bulk_size}")
    private Integer bulkSize;

    @Autowired
    public EsDeliveryService(final DeliveryRepository deliveryRepository,
                             final EsDeliveryRepository esDeliveryRepository,
                             final TransactionService transactionService) {
        this.deliveryRepository = deliveryRepository;
        this.esDeliveryRepository = esDeliveryRepository;
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
    public Page<Delivery> search(final String[] rawSearches,
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
        return esDeliveryRepository.search(searches, libraries, fuzzy, filters, new PageRequest(page, size, sort), facet);
    }

    /**
     * Indexation de la notice dans le moteur de recherche
     *
     * @param identifier
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public Delivery index(final String identifier) {
        final Delivery report = deliveryRepository.findOne(identifier);
        if (report != null) {
            esDeliveryRepository.indexEntity(report);
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
    public Iterable<Delivery> index(final List<String> identifiers) {
        final List<Delivery> reports = deliveryRepository.findByIdentifierIn(identifiers);
        final List<Delivery> filteredDetails = reports.stream().filter(det -> StringUtils.isNotEmpty(det.getLotId())).collect(Collectors.toList());

        if (!reports.isEmpty()) {
            esDeliveryRepository.indexEntities(filteredDetails);
        }
        return Collections.emptyList();
    }

    /**
     * Suppression de la notice du moteur de recherche
     *
     * @param report
     */
    @Override
    public void delete(final Delivery report) {
        esDeliveryRepository.deleteEntity(report);
    }

    @Override
    public void delete(final Collection<Delivery> deliveries) {
        esDeliveryRepository.deleteEntities(deliveries);
    }

    /**
     * Réindexation de toutes les notices disponibles
     *
     * @param index
     * @return
     */
    public long reindex(final String index) {
        long nbImported = 0;
        Page<Delivery> pageOfObjects = null;    // Chargement des objets par page de bulkSize éléments

        do {
            final TransactionStatus status = transactionService.startTransaction(true);

            // Chargement des objets
            final Pageable pageable = pageOfObjects == null ?
                                      new PageRequest(0, bulkSize, Sort.Direction.ASC, AbstractDomainObject_.identifier.getName()) :
                                      pageOfObjects.nextPageable();
            pageOfObjects = deliveryRepository.findAll(pageable);

            // Traitement des unités documentaires
            final List<Delivery> entities = pageOfObjects.getContent();
            esDeliveryRepository.index(index, entities);

            transactionService.commitTransaction(status);

            nbImported += entities.size();
            LOG.trace("{} / {} éléments indexés", nbImported, pageOfObjects.getTotalElements());

        } while (pageOfObjects.hasNext());

        return nbImported;
    }
}
