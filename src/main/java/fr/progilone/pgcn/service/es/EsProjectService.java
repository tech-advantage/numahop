package fr.progilone.pgcn.service.es;

import fr.progilone.pgcn.domain.es.project.EsProject;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.repository.es.EsProjectRepository;
import fr.progilone.pgcn.repository.es.helper.EsSearchOperation;
import fr.progilone.pgcn.repository.es.helper.EsSort;
import fr.progilone.pgcn.repository.project.ProjectRepository;
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
public class EsProjectService extends AbstractElasticsearchOperations<Project, EsProject> {

    private final ProjectRepository projectRepository;
    private final EsProjectRepository esProjectRepository;

    @Autowired
    public EsProjectService(final ProjectRepository projectRepository,
                            final EsProjectRepository esProjectRepository,
                            final TransactionService transactionService,
                            final ElasticsearchOperations elasticsearchOperations,
                            @Value("${elasticsearch.bulk_size}") final Integer bulkSize) {
        super(transactionService, elasticsearchOperations, bulkSize, EsProject.class, projectRepository, esProjectRepository);
        this.projectRepository = projectRepository;
        this.esProjectRepository = esProjectRepository;
    }

    /**
     * Recherche d'unités documentaires
     */
    public Page<EsProject> search(final String[] rawSearches,
                                  final String[] rawFilters,
                                  final List<String> libraries,
                                  final boolean fuzzy,
                                  final Integer page,
                                  final Integer size,
                                  final String[] rawSorts,
                                  final boolean facet) {
        final EsSearchOperation[] searches = EsSearchOperation.fromRawSearches(rawSearches);
        final EsSearchOperation[] filters = EsSearchOperation.fromRawFilters(rawFilters);
        final Sort sort = EsSort.fromRawSorts(rawSorts, SearchEntity.PROJECT);
        return esProjectRepository.search(searches, libraries, fuzzy, filters, PageRequest.of(page, size, sort), facet);
    }

    @Override
    protected EsProject convertToEsObject(final Project domainObject) {
        return EsProject.from(domainObject);
    }

    @Override
    protected List<String> findAllIdentifiersToIndex() {
        return projectRepository.findAllIdentifiers();
    }
}
