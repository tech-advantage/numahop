package fr.progilone.pgcn.service.es;

import fr.progilone.pgcn.domain.AbstractDomainObject_;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReport;
import fr.progilone.pgcn.domain.dto.document.conditionreport.ConditionReportSearchDTO;
import fr.progilone.pgcn.repository.document.conditionreport.ConditionReportRepository;
import fr.progilone.pgcn.repository.es.EsConditionReportRepository;
import fr.progilone.pgcn.repository.es.helper.EsSearchOperation;
import fr.progilone.pgcn.repository.es.helper.EsSort;
import fr.progilone.pgcn.service.document.mapper.ConditionReportMapper;
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
public class EsConditionReportService extends AbstractElasticsearchOperations<ConditionReport> {

    private static final Logger LOG = LoggerFactory.getLogger(EsConditionReportService.class);
    private static final ConditionReportMapper MAPPER = ConditionReportMapper.INSTANCE;

    private final ConditionReportRepository conditionReportRepository;
    private final EsConditionReportRepository esConditionReportRepository;
    private final EsDocUnitService esDocUnitService;
    private final TransactionService transactionService;

    @Value("${elasticsearch.bulk_size}")
    private Integer bulkSize;

    @Autowired
    public EsConditionReportService(final ConditionReportRepository conditionReportRepository,
                                    final EsConditionReportRepository esConditionReportRepository,
                                    final EsDocUnitService esDocUnitService,
                                    final TransactionService transactionService) {
        this.conditionReportRepository = conditionReportRepository;
        this.esConditionReportRepository = esConditionReportRepository;
        this.esDocUnitService = esDocUnitService;
        this.transactionService = transactionService;
    }

    /**
     * Recherche de constats d'état
     *
     * @param rawSearches
     * @param rawFilters
     * @param libraries
     * @param fuzzy
     * @param page
     * @param size
     * @param rawSorts
     * @param facet
     * @return
     */
    public Page<ConditionReportSearchDTO> search(final String[] rawSearches,
                                                 final String[] rawFilters,
                                                 final List<String> libraries,
                                                 final boolean fuzzy,
                                                 final Integer page,
                                                 final Integer size,
                                                 final String[] rawSorts,
                                                 final boolean facet) {
        // Recherche
        final EsSearchOperation[] searches = EsSearchOperation.fromRawSearches(rawSearches);
        final EsSearchOperation[] filters = EsSearchOperation.fromRawFilters(rawFilters);
        final Sort sort = EsSort.fromRawSorts(rawSorts, SearchEntity.CONDREPORT);
        final Page<ConditionReport> pageOfResults =
            esConditionReportRepository.search(searches, libraries, fuzzy, filters, new PageRequest(page, size, sort), facet);

        // Recherche des UD parentes
        final List<ConditionReport> reports = pageOfResults.getContent();
        final List<String> ids = reports.stream().map(ConditionReport::getDocUnitId).collect(Collectors.toList());

        esDocUnitService.findByIds(ids)
                        .forEach(doc -> reports.stream()
                                               .filter(rep -> StringUtils.equals(rep.getDocUnitId(), doc.getIdentifier()))
                                               .forEach(rep -> rep.setDocUnit(doc)));
        // DTOs
        return pageOfResults.map(MAPPER::reportToSearchDTO);
    }

    /**
     * Indexation de la notice dans le moteur de recherche
     *
     * @param identifier
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public ConditionReport index(final String identifier) {
        final ConditionReport report = conditionReportRepository.findOne(identifier);
        if (report != null) {
            esConditionReportRepository.indexEntity(report);
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
    public Iterable<ConditionReport> index(final List<String> identifiers) {
        final List<ConditionReport> reports = conditionReportRepository.findByIdentifierInWithDetails(identifiers);
        final List<ConditionReport> filteredDetails =
            reports.stream().filter(det -> StringUtils.isNotEmpty(det.getDocUnitId())).collect(Collectors.toList());

        if (!reports.isEmpty()) {
            esConditionReportRepository.indexEntities(filteredDetails);
        }
        return Collections.emptyList();
    }

    /**
     * Suppression de la notice du moteur de recherche
     *
     * @param report
     */
    @Override
    public void delete(final ConditionReport report) {
        esConditionReportRepository.deleteEntity(report);
    }

    /**
     * Suppression des notices du moteur de recherche
     *
     * @param reports
     */
    @Override
    public void delete(final Collection<ConditionReport> reports) {
        esConditionReportRepository.deleteEntities(reports);
    }

    /**
     * Réindexation de toutes les notices disponibles
     *
     * @param index
     * @return
     */
    public long reindex(final String index) {
        long nbImported = 0;
        Page<ConditionReport> pageOfObjects = null;    // Chargement des objets par page de bulkSize éléments

        do {
            final TransactionStatus status = transactionService.startTransaction(true);

            // Chargement des objets
            final Pageable pageable = pageOfObjects == null ?
                                      new PageRequest(0, bulkSize, Sort.Direction.ASC, AbstractDomainObject_.identifier.getName()) :
                                      pageOfObjects.nextPageable();
            pageOfObjects = conditionReportRepository.findAllByDocUnitState(DocUnit.State.AVAILABLE, pageable);

            // Traitement des unités documentaires
            final List<ConditionReport> entities = pageOfObjects.getContent();
            esConditionReportRepository.index(index, entities);

            transactionService.commitTransaction(status);

            nbImported += entities.size();
            LOG.trace("{} / {} éléments indexés", nbImported, pageOfObjects.getTotalElements());

        } while (pageOfObjects.hasNext());

        return nbImported;
    }
}
