package fr.progilone.pgcn.service.es;

import static fr.progilone.pgcn.service.es.EsConstant.SUGGEST_CTX_GLOBAL;
import static fr.progilone.pgcn.service.es.EsConstant.SUGGEST_CTX_LIBRARY;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.progilone.pgcn.domain.AbstractDomainObject_;
import fr.progilone.pgcn.domain.CompletionContext;
import fr.progilone.pgcn.domain.delivery.DeliveredDocument;
import fr.progilone.pgcn.domain.document.DigitalDocument;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.PhysicalDocument;
import fr.progilone.pgcn.domain.exchange.cines.CinesReport;
import fr.progilone.pgcn.domain.exchange.internetarchive.InternetArchiveReport;
import fr.progilone.pgcn.domain.workflow.DocUnitState;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import fr.progilone.pgcn.repository.document.DocUnitRepository;
import fr.progilone.pgcn.repository.es.EsBibliographicRecordRepository;
import fr.progilone.pgcn.repository.es.EsCinesReportRepository;
import fr.progilone.pgcn.repository.es.EsDocUnitRepository;
import fr.progilone.pgcn.repository.es.EsInternetArchiveReportRepository;
import fr.progilone.pgcn.repository.es.helper.EsSearchOperation;
import fr.progilone.pgcn.repository.es.helper.EsSort;
import fr.progilone.pgcn.repository.exchange.cines.CinesReportRepository;
import fr.progilone.pgcn.repository.exchange.internetarchive.InternetArchiveReportRepository;
import fr.progilone.pgcn.service.document.PhysicalDocumentService;
import fr.progilone.pgcn.service.util.transaction.TransactionService;

@Service
public class EsDocUnitService extends AbstractElasticsearchOperations<DocUnit> {

    private static final Logger LOG = LoggerFactory.getLogger(EsDocUnitService.class);

    private final CinesReportRepository cinesReportRepository;
    private final DocUnitRepository docUnitRepository;
    private final EsBibliographicRecordRepository esBibliographicRecordRepository;
    private final EsDocUnitRepository esDocUnitRepository;
    private final EsCinesReportRepository esCinesReportRepository;
    private final EsInternetArchiveReportRepository esInternetArchiveReportRepository;
    private final InternetArchiveReportRepository iaReportRepository;
    private final PhysicalDocumentService physicalDocumentService;
    private final TransactionService transactionService;

    @Value("${elasticsearch.bulk_size}")
    private Integer bulkSize;

    @Autowired
    public EsDocUnitService(final CinesReportRepository cinesReportRepository,
                            final DocUnitRepository docUnitRepository,
                            final EsBibliographicRecordRepository esBibliographicRecordRepository,
                            final EsDocUnitRepository esDocUnitRepository,
                            final EsCinesReportRepository esCinesReportRepository,
                            final EsInternetArchiveReportRepository esInternetArchiveReportRepository,
                            final InternetArchiveReportRepository iaReportRepository,
                            final PhysicalDocumentService physicalDocumentService,
                            final TransactionService transactionService) {
        this.cinesReportRepository = cinesReportRepository;
        this.docUnitRepository = docUnitRepository;
        this.esBibliographicRecordRepository = esBibliographicRecordRepository;
        this.esDocUnitRepository = esDocUnitRepository;
        this.esCinesReportRepository = esCinesReportRepository;
        this.esInternetArchiveReportRepository = esInternetArchiveReportRepository;
        this.iaReportRepository = iaReportRepository;
        this.physicalDocumentService = physicalDocumentService;
        this.transactionService = transactionService;
    }

    /**
     * Recherche d'unités documentaires
     *
     * @param rawSearches
     * @param rawFilters
     * @param libraries
     * @param fuzzy
     * @param rawSorts
     * @param facet
     */
    public Page<DocUnit> search(final String[] rawSearches,
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
        return esDocUnitRepository.search(searches, libraries, fuzzy, filters, new PageRequest(page, size, sort), facet);
    }

    /**
     * Suggestion de résultats
     *
     * @param text
     * @param size
     * @return
     */
    public List<Map<String, Object>> suggestDocUnits(final String text, final int size, final List<String> libraries) {
        return esDocUnitRepository.suggest(text, size, libraries);
    }

    /**
     * Recherche par identifiants
     *
     * @param identifiers
     * @return
     */
    public List<DocUnit> findByIds(final List<String> identifiers) {
        return esDocUnitRepository.findByIds(identifiers);
    }

    /**
     * Indexation de l'unité documentaire dans le moteur de recherche
     *
     * @param identifier
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public DocUnit index(final String identifier) {
        final DocUnit docUnit = docUnitRepository.findOne(identifier);
        if (docUnit != null) {
            extendDocUnits(Collections.singletonList(docUnit));
            return esDocUnitRepository.index(docUnit);
        }
        return null;
    }

    /**
     * Indexation de plusieurs unités documentaires dans le moteur de recherche
     *
     * @param identifiers
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public Iterable<DocUnit> index(final List<String> identifiers) {
        if (CollectionUtils.isEmpty(identifiers)) {
            return Collections.emptyList();
        }
        final List<DocUnit> docUnits = docUnitRepository.findByIdentifierIn(identifiers);
        if (docUnits.isEmpty()) {
            return Collections.emptyList();
        }
        extendDocUnits(docUnits);

        return esDocUnitRepository.save(docUnits);
    }

    /**
     * Indexation asynchrone à partir de l'id d'un {@link fr.progilone.pgcn.domain.document.PhysicalDocument}
     *
     * @param physDocId
     */
    @Async
    @Transactional(readOnly = true)
    public void indexPhysicalDocumentAsync(final String physDocId) {
        final PhysicalDocument physDoc = physicalDocumentService.findByIdentifier(physDocId);
        index(physDoc.getDocUnit().getIdentifier());
    }

    /**
     * Suppression de l'unité documentaire du moteur de recherche
     *
     * @param docUnit
     */
    @Override
    @Transactional(readOnly = true)
    public void delete(final DocUnit docUnit) {
        final List<CinesReport> cinesReports = cinesReportRepository.findByDocUnitIdentifierOrderByLastModifiedDateDesc(docUnit.getIdentifier());
        esCinesReportRepository.deleteEntities(cinesReports);

        final List<InternetArchiveReport> iaReports = iaReportRepository.findByDocUnitIdentifierOrderByLastModifiedDateDesc(docUnit.getIdentifier());
        esInternetArchiveReportRepository.deleteEntities(iaReports);

        esBibliographicRecordRepository.deleteEntities(docUnit.getRecords());
        esDocUnitRepository.delete(docUnit);
    }

    @Override
    @Transactional(readOnly = true)
    public void delete(final Collection<DocUnit> docUnits) {
        docUnits.forEach(this::delete);
    }

    /**
     * Alimente les champs d'une unité documentaire spécifiques à l'indexation
     *
     * @param docUnits
     */
    @Transactional(readOnly = true)
    public void extendDocUnits(final List<DocUnit> docUnits) {
        docUnits.forEach(this::extendDocUnit);
    }

    /**
     * Alimente les champs d'une unité documentaire spécifiques à l'indexation
     *
     * @param docUnit
     */
    private void extendDocUnit(final DocUnit docUnit) {
        // état d'avancement (étape de workflow)
        if (docUnit.getWorkflow() != null) {
            final List<WorkflowStateKey> workflowStateKeys =
                docUnit.getWorkflow().getCurrentStates().stream().map(DocUnitState::getKey).collect(Collectors.toList());
            docUnit.setWorkflowStateKeys(workflowStateKeys);
        }
        // dernière date de livraison et taille des fichiers numériques (masters)
        docUnit.getDigitalDocuments()
               .stream()
               .filter(dlv -> dlv.getDeliveryDate() != null)
               .max(Comparator.comparing(DigitalDocument::getDeliveryDate))
               .ifPresent(dlv -> {
                   docUnit.setLatestDeliveryDate(dlv.getDeliveryDate());

                   final long totalLength =
                       dlv.getDeliveries().stream().map(DeliveredDocument::getTotalLength).filter(Objects::nonNull).mapToLong(l -> l).sum();
                   docUnit.setMasterSize(totalLength);
               });

        final CompletionContext suggestion = new CompletionContext(new String[] {docUnit.getLabel(), docUnit.getPgcnId()});
        docUnit.setSuggest(suggestion);

        // Payload
        final Map<String, Object> payload = new HashMap<>();
        payload.put("identifier", docUnit.getIdentifier());
        payload.put("label", docUnit.getLabel());
        payload.put("pgcnId", docUnit.getPgcnId());
        suggestion.setPayload(payload);

        // Contexte
        if (docUnit.getLibrary() != null) {
            suggestion.addContext(SUGGEST_CTX_LIBRARY, docUnit.getLibrary().getIdentifier());
        }
        suggestion.addContext(SUGGEST_CTX_LIBRARY, SUGGEST_CTX_GLOBAL);
    }

    /**
     * Réindexation de toutes les unités documentaires disponibles
     *
     * @param index
     * @return
     */
    public long reindex(final String index) {
        
        long nbImported = 0;
        final AtomicReference<Page<DocUnit>> pageRef = new AtomicReference<>();
        do {
            final int result = transactionService.executeInNewTransactionWithReturn(() -> {

                // Chargement des objets
                final Pageable pageable = pageRef.get() == null ?
                                          new PageRequest(0, bulkSize, Sort.Direction.ASC, AbstractDomainObject_.identifier.getName()) :
                                              pageRef.get().nextPageable();
                                          
                final Page<DocUnit> pageOfObjects = docUnitRepository.findAllByState(DocUnit.State.AVAILABLE, pageable);
    
                // Traitement des unités documentaires
                final List<DocUnit> entities = pageOfObjects.getContent();
                extendDocUnits(entities);
                esDocUnitRepository.index(index, entities);
                
                pageRef.set(pageOfObjects);
                return entities.size();
            });
            
            nbImported += result;
            LOG.trace("{} / {} éléments indexés", nbImported, pageRef.get().getTotalElements());

        } while (pageRef.get() != null && pageRef.get().hasNext());

        return nbImported;
    }
}
