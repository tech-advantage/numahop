package fr.progilone.pgcn.service.es;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.repository.es.EsProjectRepository;
import fr.progilone.pgcn.repository.es.helper.EsSearchOperation;
import fr.progilone.pgcn.repository.es.helper.EsSort;
import fr.progilone.pgcn.repository.project.ProjectRepository;
import fr.progilone.pgcn.service.util.transaction.TransactionService;

@Service
public class EsProjectService extends AbstractElasticsearchOperations<Project> {

    private static final Logger LOG = LoggerFactory.getLogger(EsProjectService.class);

    private final ProjectRepository projectRepository;
    private final EsProjectRepository esProjectRepository;
    private final TransactionService transactionService;

    @Value("${elasticsearch.bulk_size}")
    private Integer bulkSize;

    @Autowired
    public EsProjectService(final ProjectRepository projectRepository,
                            final EsProjectRepository esProjectRepository,
                            final TransactionService transactionService) {
        this.projectRepository = projectRepository;
        this.esProjectRepository = esProjectRepository;
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
    public Page<Project> search(final String[] rawSearches,
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
        return esProjectRepository.search(searches, libraries, fuzzy, filters, new PageRequest(page, size, sort), facet);
    }

    /**
     * Indexation de l'unité documentaire dans le moteur de recherche
     *
     * @param identifier
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public Project index(final String identifier) {
        final Project project = projectRepository.findOne(identifier);
        if (project != null) {
            return esProjectRepository.index(project);
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
    public Iterable<Project> index(final List<String> identifiers) {
        final List<Project> Projects = projectRepository.findByIdentifierIn(identifiers);
        if (!Projects.isEmpty()) {
            return esProjectRepository.save(Projects);
        }
        return Collections.emptyList();
    }

    /**
     * Suppression de l'unité documentaire du moteur de recherche
     *
     * @param project
     */
    @Override
    public void delete(final Project project) {
        esProjectRepository.delete(project);
    }

    @Override
    public void delete(final Collection<Project> projects) {
        esProjectRepository.delete(projects);
    }

    /**
     * Réindexation de toutes les unités documentaires disponibles
     *
     * @param index
     * @return
     */
    public long reindex(final String index) {
        
        long nbImported = 0;
        final AtomicReference<Page<Project>> pageRef = new AtomicReference<>();
        do {
            final int result = transactionService.executeInNewTransactionWithReturn(() -> {

                // Chargement des objets
                final Pageable pageable = pageRef.get() == null ?
                                          new PageRequest(0, bulkSize, Sort.Direction.ASC, AbstractDomainObject_.identifier.getName()) :
                                              pageRef.get().nextPageable();
                final Page<Project> pageOfObjects = projectRepository.findAll(pageable);
    
                // Traitement des projects
                final List<Project> entities = pageOfObjects.getContent();
                esProjectRepository.index(index, entities);

                pageRef.set(pageOfObjects);
                return entities.size();
            });

            nbImported += result;
            LOG.trace("{} / {} éléments indexés", nbImported, pageRef.get().getTotalElements());

        } while (pageRef.get() != null && pageRef.get().hasNext());

        return nbImported;
    }
}
