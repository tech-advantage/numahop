package fr.progilone.pgcn.service.es;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.DocUnit.State;
import fr.progilone.pgcn.domain.document.PhysicalDocument;
import fr.progilone.pgcn.domain.es.document.EsDocUnit;
import fr.progilone.pgcn.repository.document.DocUnitRepository;
import fr.progilone.pgcn.repository.es.EsDocUnitRepository;
import fr.progilone.pgcn.repository.es.helper.EsSearchOperation;
import fr.progilone.pgcn.repository.es.helper.EsSort;
import fr.progilone.pgcn.service.document.PhysicalDocumentService;
import fr.progilone.pgcn.service.util.transaction.TransactionService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EsDocUnitService extends AbstractElasticsearchOperations<DocUnit, EsDocUnit> {

    private final DocUnitRepository docUnitRepository;
    private final EsDocUnitRepository esDocUnitRepository;
    private final PhysicalDocumentService physicalDocumentService;

    @Autowired
    public EsDocUnitService(final DocUnitRepository docUnitRepository,
                            final EsDocUnitRepository esDocUnitRepository,
                            final PhysicalDocumentService physicalDocumentService,
                            final TransactionService transactionService,
                            final ElasticsearchOperations elasticsearchOperations,
                            @Value("${elasticsearch.bulk_size}") final Integer bulkSize) {
        super(transactionService, elasticsearchOperations, bulkSize, EsDocUnit.class, docUnitRepository, esDocUnitRepository);
        this.docUnitRepository = docUnitRepository;
        this.esDocUnitRepository = esDocUnitRepository;
        this.physicalDocumentService = physicalDocumentService;
    }

    /**
     * Recherche d'unités documentaires
     */
    public Page<EsDocUnit> search(final String[] rawSearches,
                                  final String[] rawFilters,
                                  final List<String> libraries,
                                  final boolean fuzzy,
                                  final Integer page,
                                  final Integer size,
                                  final String[] rawSorts,
                                  final boolean facet) {
        final EsSearchOperation[] searches = EsSearchOperation.fromRawSearches(rawSearches);
        final EsSearchOperation[] filters = EsSearchOperation.fromRawFilters(rawFilters);
        final Sort sort = EsSort.fromRawSorts(rawSorts, SearchEntity.DOCUNIT);
        return esDocUnitRepository.search(searches, libraries, fuzzy, filters, PageRequest.of(page, size, sort), facet);
    }

    /**
     * Suggestion de résultats
     */
    public List<Map<String, Object>> suggestDocUnits(final String text, final int size, final List<String> libraries) {
        return esDocUnitRepository.suggest(text, size, libraries);
    }

    /**
     * Recherche par identifiants
     */
    public Iterable<EsDocUnit> findByIds(final List<String> identifiers) {
        return esDocUnitRepository.findAllById(identifiers);
    }

    /**
     * Indexation asynchrone à partir de l'id d'un {@link fr.progilone.pgcn.domain.document.PhysicalDocument}
     */
    @Async
    @Transactional(readOnly = true)
    public void indexPhysicalDocumentAsync(final String physDocId) {
        final PhysicalDocument physDoc = physicalDocumentService.findByIdentifier(physDocId);
        index(physDoc.getDocUnit().getIdentifier());
    }

    @Override
    protected EsDocUnit convertToEsObject(final DocUnit domainObject) {
        return EsDocUnit.from(domainObject);
    }

    @Override
    protected List<String> findAllIdentifiersToIndex() {
        return docUnitRepository.findAllIdentifierByState(State.AVAILABLE);
    }
}
