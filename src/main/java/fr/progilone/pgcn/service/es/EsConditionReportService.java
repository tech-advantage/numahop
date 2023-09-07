package fr.progilone.pgcn.service.es;

import fr.progilone.pgcn.domain.document.DocUnit.State;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReport;
import fr.progilone.pgcn.domain.dto.document.conditionreport.ConditionReportSearchDTO;
import fr.progilone.pgcn.domain.dto.document.conditionreport.ConditionReportValueDTO;
import fr.progilone.pgcn.domain.es.conditionreport.EsConditionReport;
import fr.progilone.pgcn.repository.document.conditionreport.ConditionReportRepository;
import fr.progilone.pgcn.repository.es.EsConditionReportRepository;
import fr.progilone.pgcn.repository.es.helper.EsSearchOperation;
import fr.progilone.pgcn.repository.es.helper.EsSort;
import fr.progilone.pgcn.repository.es.helper.SearchResultPage;
import fr.progilone.pgcn.service.util.transaction.TransactionService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

@Service
public class EsConditionReportService extends AbstractElasticsearchOperations<ConditionReport, EsConditionReport> {

    private final ConditionReportRepository conditionReportRepository;
    private final EsConditionReportRepository esConditionReportRepository;

    @Autowired
    public EsConditionReportService(final ConditionReportRepository conditionReportRepository,
                                    final EsConditionReportRepository esConditionReportRepository,
                                    final TransactionService transactionService,
                                    final ElasticsearchOperations elasticsearchOperations,
                                    @Value("${elasticsearch.bulk_size}") final Integer bulkSize) {
        super(transactionService, elasticsearchOperations, bulkSize, EsConditionReport.class, conditionReportRepository, esConditionReportRepository);
        this.conditionReportRepository = conditionReportRepository;
        this.esConditionReportRepository = esConditionReportRepository;
    }

    /**
     * Recherche de constats d'état
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
        final SearchResultPage<EsConditionReport> pageOfResults = esConditionReportRepository.search(searches, libraries, fuzzy, filters, PageRequest.of(page, size, sort), facet);
        // DTOs
        return pageOfResults.map(r -> {
            final ConditionReportSearchDTO dto = new ConditionReportSearchDTO();
            dto.setDocUnitId(r.getDocUnitId());
            dto.setDocUnitLabel(r.getDocUnitLabel());
            dto.setDocUnitPgcnId(r.getDocUnitPgcnId());
            if (r.getDetails() != null) {
                dto.setDate(r.getDetails().getDate());
                dto.setType(r.getDetails().getType().name());
                dto.setProperties(r.getDetails().getDescriptions().stream().map(d -> {
                    final ConditionReportValueDTO valueDto = new ConditionReportValueDTO();
                    valueDto.setComment(d.getComment());
                    if (d.getProperty() != null) {
                        valueDto.setPropertyCode(d.getProperty().getCode());
                        valueDto.setPropertyId(d.getProperty().getIdentifier());
                        valueDto.setPropertyOrder(d.getProperty().getOrder());
                        if (d.getProperty().getType() != null) {
                            valueDto.setPropertyType(d.getProperty().getType().name());
                        }
                    }
                    if (d.getValue() != null) {
                        valueDto.setValue(d.getValue().getLabel());
                    }
                    return valueDto;
                }).collect(Collectors.toList()));
            }
            return dto;
        });
    }

    @Override
    protected EsConditionReport convertToEsObject(final ConditionReport domainObject) {
        return EsConditionReport.from(domainObject);
    }

    @Override
    protected List<String> findAllIdentifiersToIndex() {
        return conditionReportRepository.findAllIdentifierByDocUnitState(State.AVAILABLE);
    }
}
