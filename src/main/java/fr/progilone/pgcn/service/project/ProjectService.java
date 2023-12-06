package fr.progilone.pgcn.service.project;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.domain.lot.Lot.LotStatus;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.domain.project.Project.ProjectStatus;
import fr.progilone.pgcn.domain.train.Train.TrainStatus;
import fr.progilone.pgcn.exception.PgcnBusinessException;
import fr.progilone.pgcn.exception.PgcnListValidationException;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.exception.message.PgcnList;
import fr.progilone.pgcn.repository.project.ProjectRepository;
import fr.progilone.pgcn.repository.project.helper.ProjectSearchBuilder;
import fr.progilone.pgcn.service.document.DocUnitService;
import fr.progilone.pgcn.service.es.EsProjectService;
import fr.progilone.pgcn.service.exchange.ImportReportService;
import fr.progilone.pgcn.service.util.SortUtils;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectService {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectService.class);

    private final DocUnitService docUnitService;
    private final ImportReportService importReportService;
    private final EsProjectService esProjectService;
    private final ProjectRepository projectRepository;

    public ProjectService(final DocUnitService docUnitService,
                          final ImportReportService importReportService,
                          final EsProjectService esProjectService,
                          final ProjectRepository projectRepository) {
        this.docUnitService = docUnitService;
        this.esProjectService = esProjectService;
        this.projectRepository = projectRepository;
        this.importReportService = importReportService;
    }

    @Transactional
    public void delete(final Collection<Project> projects) throws PgcnListValidationException {
        // Validation des suppressions
        for (final Project project : projects) {
            validateDelete(project);
        }
        final List<Project> invalidObjects = projects.stream().filter(pj -> !pj.getErrors().isEmpty()).collect(Collectors.toList());
        if (!invalidObjects.isEmpty()) {
            throw new PgcnListValidationException(invalidObjects);
        }

        // Nettoyage
        importReportService.setProjectNull(projects.stream().map(Project::getIdentifier).collect(Collectors.toList()));
        // Suppression
        projectRepository.deleteAll(projects);
        // Moteur de recherche
        esProjectService.deleteAsync(projects.stream().map(Project::getIdentifier).collect(Collectors.toSet()));
    }

    /**
     * Suppression d'un projet depuis son identifiant
     *
     * @param identifier
     *            l'identifiant d'un project
     * @throws PgcnValidationException
     *             si la suppression du projet échoue
     */
    @Transactional
    public void delete(final String identifier) throws PgcnValidationException {
        // Validation de la suppression
        final Project project = findByIdentifierWithDependencies(identifier);
        final PgcnList<PgcnError> errors = validateDelete(project);
        if (!errors.isEmpty()) {
            throw new PgcnValidationException(project, errors);
        }
        // Nettoyage
        importReportService.setProjectNull(Collections.singletonList(identifier));
        // Suppression
        projectRepository.deleteById(identifier);
        // Moteur de recherche
        esProjectService.deleteAsync(project.getIdentifier());
    }

    @Transactional(readOnly = true)
    public List<Project> findAll(final Iterable<String> ids) {
        if (IterableUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return projectRepository.findByIdentifierIn(ids, null);
    }

    @Transactional(readOnly = true)
    public List<Project> findAllByActive(final boolean active) {
        return projectRepository.findAllByActive(active);
    }

    @Transactional(readOnly = true)
    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Project getOneByName(final String name) {
        return projectRepository.findOneByNameWithDependencies(StringUtils.lowerCase(name));
    }

    @Transactional(readOnly = true)
    public Project findByIdentifier(final String id) {
        return projectRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public Project findByIdentifierWithDependencies(final String identifier) {
        return projectRepository.findOneWithDependencies(identifier);
    }

    @Transactional(readOnly = true)
    public Project findByIdentifierWithLightDependencies(final String identifier) {
        return projectRepository.findOneWithLightDependencies(identifier);
    }

    @Transactional
    public Project save(final Project project) throws PgcnValidationException, PgcnBusinessException {
        final Project savedProject = projectRepository.save(project);
        for (final DocUnit docUnit : project.getDocUnits()) {
            docUnitService.saveWithoutValidation(docUnit);
        }
        return savedProject;
    }

    @Transactional(readOnly = true)
    public List<Project> findProjectsForWidget(final LocalDate fromDate, final List<String> libraries, final List<Project.ProjectStatus> statuses) {

        return projectRepository.findProjectsForWidget(fromDate, libraries, statuses);
    }

    /**
     * Recherche de projets
     */
    @Transactional(readOnly = true)
    public Page<Project> search(final String search,
                                final String initiale,
                                final List<String> libraries,
                                final List<Project.ProjectStatus> status,
                                final List<String> providers,
                                final boolean active,
                                final Integer page,
                                final Integer size) {
        final Pageable pageRequest = PageRequest.of(page, size);
        return projectRepository.search(new ProjectSearchBuilder().setSearch(search)
                                                                  .setInitiale(initiale)
                                                                  .setStatuses(status)
                                                                  .setLibraries(libraries)
                                                                  .setProviders(providers)
                                                                  .setActive(active), pageRequest);
    }

    /**
     * Liste de projets
     */
    @Transactional(readOnly = true)
    public List<Project> loadCreatedProjects(final String searchProject,
                                             final String initiale,
                                             final List<String> libraries,
                                             final List<Project.ProjectStatus> statuses,
                                             final boolean active) {
        final Page<Project> results = projectRepository.search(new ProjectSearchBuilder().setSearch(searchProject)
                                                                                         .setInitiale(initiale)
                                                                                         .setStatuses(statuses)
                                                                                         .setLibraries(libraries)
                                                                                         .setActive(active), PageRequest.of(0, 10));
        return results.getContent();
    }

    @Transactional(readOnly = true)
    public Project findOneWithFTPConfiguration(final String identifier) {
        return projectRepository.findOneWithFTPConfiguration(identifier);
    }

    @Transactional(readOnly = true)
    public Project findOneWithExportFTPConfiguration(final String identifier) {
        return projectRepository.findOneWithExportFTPConfiguration(identifier);
    }

    /**
     * Retourne l'ensemble des projets pour les statistiques
     */
    @Transactional(readOnly = true)
    public Page<Project> findAllForStatistics(final String search,
                                              final List<String> libraries,
                                              final List<String> projects,
                                              final LocalDate fromDate,
                                              final LocalDate toDate,
                                              final Integer page,
                                              final Integer size,
                                              final List<String> sorts) {
        final Sort sort = SortUtils.getSort(sorts);
        final Pageable pageRequest = PageRequest.of(page, size, sort);
        return projectRepository.search(new ProjectSearchBuilder().setSearch(search).setLibraries(libraries).setProjects(projects).setFrom(fromDate).setTo(toDate), pageRequest);
    }

    /**
     * Récupère le projet auquel appartient la docUnit
     */
    @Transactional(readOnly = true)
    public Project findByDocUnitIdentifier(final String identifier) {
        return projectRepository.findOneByDocUnitId(identifier);
    }

    /**
     * Récupère le projet auquel appartient le lot
     */
    @Transactional(readOnly = true)
    public Project findByLotId(final String identifier) {
        return projectRepository.findOneByLotId(identifier);
    }

    /**
     * Recherche les projets par bibliothèque, groupés par statut
     *
     * @return liste de map avec 2 clés: statut et décompte
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getProjectGroupByStatus(final List<String> libraries) {
        final List<Object[]> results = CollectionUtils.isNotEmpty(libraries) ? projectRepository.getProjectGroupByStatus(libraries)
                                                                             : projectRepository.getProjectGroupByStatus();    // status, count
        return results.stream().map(res -> {
            final Map<String, Object> resMap = new HashMap<>();
            resMap.put("status", res[0]);
            resMap.put("count", res[1]);
            return resMap;
        }).collect(Collectors.toList());
    }

    /**
     * Recherche les projets clôtures par librairie depuis un delai en jours.
     */
    @Transactional(readOnly = true)
    public List<Project> getClosedProjectsByLibrary(final String libraryIdentifier, final int delay) {

        final LocalDate dateTo = LocalDate.now().minusDays(delay);
        final List<Project> projects = projectRepository.getClosedProjectsByLibrary(libraryIdentifier, dateTo);
        return projects;
    }

    /**
     * Validation de la suppression d'un projet
     *
     * @param project
     * @throws PgcnValidationException
     */
    private PgcnList<PgcnError> validateDelete(final Project project) {
        final PgcnList<PgcnError> errors = new PgcnList<>();
        final PgcnError.Builder builder = new PgcnError.Builder();

        // Lots
        if (CollectionUtils.isNotEmpty(project.getLots())) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.PROJECT_DEL_EXITS_LOTS).build());
        }
        // Trains
        if (CollectionUtils.isNotEmpty(project.getTrains())) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.PROJECT_DEL_EXITS_TRAINS).build());
        }
        // Unités documentaires
        if (CollectionUtils.isNotEmpty(project.getDocUnits())) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.PROJECT_DEL_EXITS_DOCUNITS).build());
        }

        // TODO: platform ?

        if (!errors.isEmpty()) {
            project.setErrors(errors);
        }
        return errors;
    }

    @Transactional(readOnly = true)
    public List<Project> findAllByActiveAndLibraryIn(final List<String> libraries) {
        return projectRepository.findAllByActiveAndLibraryIdentifierIn(true, libraries);
    }

    @Transactional(readOnly = true)
    public List<Project> findAllByLibraryIn(final List<String> libraries) {
        return projectRepository.findAllByLibraryIdentifierIn(libraries);
    }

    @Transactional(readOnly = true)
    public List<Project> findAllByLibraryIdentifier(final String library) {
        return projectRepository.findAllByLibraryIdentifier(library);
    }

    @Transactional
    public void setFilesProjectArchived(final String projectId) {
        final Project project = findByIdentifier(projectId);
        project.setFilesArchived(true);
        save(project);
    }

    /**
     * Close le projet si les conditions sont reunies.
     */
    @Transactional
    public void checkAndCloseProject(final Project project) {
        // Lots non cloturés
        final List<Lot> processingLots = project.getLots().stream().filter(lp -> !LotStatus.CLOSED.equals(lp.getStatus())).collect(Collectors.toList());

        // Documents non attachés à un lot
        final List<DocUnit> processingDocs = docUnitService.findAllByProjectId(project.getIdentifier()).stream().filter(doc -> doc.getLot() == null).collect(Collectors.toList());
        if (processingLots.isEmpty() && processingDocs.isEmpty()) {
            // Tous les lots sont finis, on termine aussi le projet et les trains du coup
            project.setRealEndDate(LocalDate.now());
            project.setStatus(ProjectStatus.CLOSED);
            project.setActive(false);
            project.getTrains().forEach(train -> {
                train.setActive(false);
                train.setStatus(TrainStatus.CLOSED);
            });
            save(project);
            LOG.info("CLOTURE PROJET : Mise à jour du project {} => status : CLOSED", project.getName());
        }
    }

    /**
     * Décloture le projet et ses trains éventuels.
     */
    @Transactional
    public void declotureProject(final Project project) {

        // Tous les lots sont finis, on termine aussi le projet et les trains du coup
        project.setRealEndDate(null);
        project.setStatus(ProjectStatus.ONGOING);
        project.setActive(true);
        project.getTrains().forEach(train -> {
            train.setActive(true);
            train.setStatus(TrainStatus.CREATED);
        });
        save(project);
        LOG.info("DECLOTURE PROJET : Mise à jour du project {} => status : ONGOING", project.getName());
    }

}
