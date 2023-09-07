package fr.progilone.pgcn.service.project.ui;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.dto.audit.AuditProjectRevisionDTO;
import fr.progilone.pgcn.domain.dto.project.ProjectDTO;
import fr.progilone.pgcn.domain.dto.project.SimpleProjectDTO;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.domain.lot.Lot.LotStatus;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.domain.project.Project.ProjectStatus;
import fr.progilone.pgcn.domain.train.Train.TrainStatus;
import fr.progilone.pgcn.domain.util.CustomUserDetails;
import fr.progilone.pgcn.exception.PgcnBusinessException;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.security.SecurityUtils;
import fr.progilone.pgcn.service.document.DocUnitService;
import fr.progilone.pgcn.service.library.LibraryService;
import fr.progilone.pgcn.service.project.ProjectService;
import fr.progilone.pgcn.service.project.mapper.ProjectMapper;
import fr.progilone.pgcn.service.project.mapper.SimpleProjectMapper;
import fr.progilone.pgcn.service.project.mapper.UIProjectMapper;
import fr.progilone.pgcn.service.util.ErrorThrowerService;
import fr.progilone.pgcn.service.util.transaction.VersionValidationService;
import fr.progilone.pgcn.service.workflow.WorkflowService;
import fr.progilone.pgcn.web.util.AccessHelper;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service dédié à les gestion des vues des projets
 */
@Service
public class UIProjectService {

    private static final Logger LOG = LoggerFactory.getLogger(UIProjectService.class);

    private final ProjectService projectService;
    private final DocUnitService docUnitService;
    private final LibraryService libraryService;
    private final UIProjectMapper uiProjectMapper;
    private final WorkflowService workflowService;
    private final AccessHelper accessHelper;

    @Autowired
    public UIProjectService(final ProjectService projectService,
                            final DocUnitService docUnitService,
                            final LibraryService libraryService,
                            final UIProjectMapper uiProjectMapper,
                            final WorkflowService workflowService,
                            final AccessHelper accessHelper) {
        this.projectService = projectService;
        this.docUnitService = docUnitService;
        this.libraryService = libraryService;
        this.uiProjectMapper = uiProjectMapper;
        this.workflowService = workflowService;
        this.accessHelper = accessHelper;
    }

    @Transactional
    public ProjectDTO create(final ProjectDTO request) throws PgcnValidationException {
        final Project project = new Project();
        project.setStatus(ProjectStatus.CREATED);
        uiProjectMapper.mapInto(request, project);
        defaultValues(project);
        try {
            final Project savedProject = projectService.save(project);
            final Project projectWithProperties = projectService.findByIdentifierWithDependencies(savedProject.getIdentifier());
            return ProjectMapper.INSTANCE.projectToProjectDTO(projectWithProperties);
        } catch (final PgcnBusinessException e) {
            e.getErrors().forEach(semanthequeError -> request.addError(buildError(semanthequeError.getCode())));
            throw new PgcnValidationException(request);
        }
    }

    private void defaultValues(final Project project) {
        final CustomUserDetails deets = SecurityUtils.getCurrentUser();
        if (deets != null && !deets.isSuperuser()
            && deets.getLibraryId() != null) {
            final Library lib = libraryService.findByIdentifier(deets.getLibraryId());
            project.setLibrary(lib);
        }
    }

    /**
     * Mise à jour d'un projet
     *
     * @param request
     *            un objet contenant les informations necessaires à l'enregistrement d'un projet
     * @return le projet nouvellement créé ou mis à jour
     * @throws PgcnValidationException
     */
    @Transactional
    public ProjectDTO update(final ProjectDTO request) throws PgcnValidationException {
        final Project project = projectService.findByIdentifierWithDependencies(request.getIdentifier());

        // Contrôle d'accès concurrents
        VersionValidationService.checkForStateObject(project, request);

        uiProjectMapper.mapInto(request, project);
        try {
            final Project savedProject = projectService.save(project);
            final Project projectWithProperties = projectService.findByIdentifierWithDependencies(savedProject.getIdentifier());
            return ProjectMapper.INSTANCE.projectToProjectDTO(projectWithProperties);
        } catch (final PgcnBusinessException e) {
            e.getErrors().forEach(semanthequeError -> request.addError(buildError(semanthequeError.getCode())));
            throw new PgcnValidationException(request);
        }
    }

    /**
     * Annulation projet.
     *
     * @param dto
     * @return
     */
    @Transactional
    public ProjectDTO cancelProject(final ProjectDTO dto) {

        final Project project = projectService.findByIdentifierWithLightDependencies(dto.getIdentifier());
        final LocalDate cancelDate = LocalDate.now();

        project.getLots().forEach(lot -> {
            // termine les workflows et pose les statuts 'CANCELED' sur les docs.
            workflowService.endWorkflowForCancelingProject(lot.getDocUnits());
            final List<DocUnit> pendingDus = lot.getDocUnits().stream().filter(du -> du.getWorkflow() != null && !du.getWorkflow().isDone()).collect(Collectors.toList());
            if (pendingDus.isEmpty()) {
                // Tous les documents du lots sont finis, on annule aussi le lot du coup
                lot.setRealEndDate(cancelDate);
                lot.setStatus(LotStatus.CANCELED);
                lot.setActive(false);
                // voir si besoin du save?
                // lotService.save(lot);
                LOG.info("Mise a jour du lot {} => CANCELED", lot.getLabel());
            }

        });

        final List<Lot> pendingLots = project.getLots().stream().filter(lp -> !LotStatus.CANCELED.equals(lp.getStatus())).collect(Collectors.toList());
        if (pendingLots.isEmpty()) {
            // Tous les lots sont annulés, on annnule aussi le projet et les trains éventuels
            project.setRealEndDate(cancelDate);
            project.setStatus(ProjectStatus.CANCELED);
            project.setCancelingComment(dto.getCancelingComment());
            project.setActive(false);
            project.getTrains().forEach(train -> {
                train.setActive(false);
                train.setStatus(TrainStatus.CANCELED);
            });
            projectService.save(project);
            LOG.info("Mise à jour du project {} => CANCELED", project.getName());
        }

        // on recharge le projet
        final Project projectWithProperties = projectService.findByIdentifierWithDependencies(dto.getIdentifier());
        return ProjectMapper.INSTANCE.projectToProjectDTO(projectWithProperties);
    }

    @Transactional
    public ProjectDTO suspendProject(final ProjectDTO dto) {

        final Project project = projectService.findByIdentifierWithLightDependencies(dto.getIdentifier());
        project.setStatus(ProjectStatus.PENDING);
        project.getLots().forEach(lot -> {
            lot.setStatus(LotStatus.PENDING);
        });
        projectService.save(project);
        LOG.info("Mise à jour du project {} => SUSPENDU", project.getName());

        // on recharge le projet
        final Project projectWithProperties = projectService.findByIdentifierWithDependencies(dto.getIdentifier());
        return ProjectMapper.INSTANCE.projectToProjectDTO(projectWithProperties);
    }

    @Transactional
    public ProjectDTO reactivateProject(final ProjectDTO dto) {

        final Project project = projectService.findByIdentifierWithLightDependencies(dto.getIdentifier());
        // il faut retrouver le statut des lots avant la mise en attente..
        project.getLots().forEach(lot -> {
            final Long nbPendingDocs = lot.getDocUnits().stream().filter(du -> workflowService.isWorkflowRunning(du.getIdentifier())).count();
            if (nbPendingDocs == 0) {
                // aucun workflow demarré sur les dus du lot.
                lot.setStatus(LotStatus.CREATED);
            } else {
                lot.setStatus(LotStatus.ONGOING);
            }
        });
        // ... et en déduire celui du projet.
        final Long nbprocessingLots = project.getLots().stream().filter(lot -> LotStatus.ONGOING.equals(lot.getStatus())).count();
        if (nbprocessingLots == 0) {
            project.setStatus(ProjectStatus.CREATED);
        } else {
            project.setStatus(ProjectStatus.ONGOING);
        }
        projectService.save(project);
        LOG.info("Mise à jour du project {} => REACTIVE", project.getName());

        // on recharge le projet
        final Project projectWithProperties = projectService.findByIdentifierWithDependencies(dto.getIdentifier());
        return ProjectMapper.INSTANCE.projectToProjectDTO(projectWithProperties);
    }

    private PgcnError buildError(final PgcnErrorCode pgcnErrorCode) {
        final PgcnError.Builder builder = new PgcnError.Builder();
        switch (pgcnErrorCode) {
            case PROJECT_DUPLICATE_NAME:
                builder.setCode(pgcnErrorCode).setField("name");
                break;
            default:
                break;
        }
        return builder.build();
    }

    @Transactional
    public List<SimpleProjectDTO> loadCreatedProjects(final String searchProject,
                                                      final String initiale,
                                                      final List<String> libraries,
                                                      final List<ProjectStatus> statuses,
                                                      final boolean active) {

        final List<Project> projects = projectService.loadCreatedProjects(searchProject, initiale, libraries, statuses, active);
        return projects.stream().map(SimpleProjectMapper.INSTANCE::projectToSimpleProjectDTO).collect(Collectors.toList());
    }

    @Transactional
    public void addDocUnits(final String idProjet, final List<String> idDocs) {
        final Project project = projectService.findByIdentifierWithDependencies(idProjet);
        final List<DocUnit> docUnitSet = docUnitService.canDocUnitsBeAdded(idDocs);
        final Set<DocUnit> docUnits = docUnitService.findAllById(idDocs);

        docUnits.forEach(docUnit -> {
            if (docUnitSet.contains(docUnit)) {
                project.addDocUnit(docUnit);
            } else {
                ErrorThrowerService.addAndThrow(project, Collections.singletonList(PgcnErrorCode.DOC_UNIT_IN_PROJECT));
            }
        });
        projectService.save(project);
    }

    @Transactional
    public void addLibraries(final String idProjet, final List<String> idLibraries) {
        final Project project = projectService.findByIdentifierWithDependencies(idProjet);
        final List<Library> librariesSet = libraryService.findAll(idLibraries);

        librariesSet.forEach(library -> {
            if (!librariesSet.contains(library)) {
                project.addLibrary(library);
            } else {
                ErrorThrowerService.addAndThrow(project, Collections.singletonList(PgcnErrorCode.LIBRARY_IN_PROJECT));
            }
        });
        projectService.save(project);
    }

    @Transactional(readOnly = true)
    public ProjectDTO getOne(final String id) {
        final Project project = projectService.findByIdentifier(id);
        if (project == null) {
            return null;
        }

        final ProjectDTO toReturn = ProjectMapper.INSTANCE.projectToProjectDTO(project);
        toReturn.setOtherProviders(new ArrayList<>());

        // for (final Lot lot : project.getLots()) {
        // if (lot.getProvider() != null
        // && toReturn.getOtherProviders()
        // .stream()
        // .noneMatch(dto -> StringUtils.equals(dto.getIdentifier(), lot.getProvider().getIdentifier()))
        // && !Objects.equals(lot.getProvider(), project.getProvider())) {
        //
        // toReturn.addOtherProvider(UserMapper.INSTANCE.userToSimpleUserDTO(lot.getProvider()));
        // }
        // }
        return toReturn;
    }

    @Transactional
    public void delete(final Collection<Project> projects) throws PgcnBusinessException {
        projectService.delete(projects);
    }

    @Transactional(readOnly = true)
    public List<SimpleProjectDTO> findAllActiveDTO() {
        final List<Project> projects = projectService.findAllByActive(true);
        return projects.stream().map(SimpleProjectMapper.INSTANCE::projectToSimpleProjectDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SimpleProjectDTO> findAllDTO() {
        final List<Project> projects = projectService.findAll();
        return projects.stream().map(SimpleProjectMapper.INSTANCE::projectToSimpleProjectDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SimpleProjectDTO> findAllActiveByLibraryIn(final List<String> libraries) {
        final List<Project> projects = projectService.findAllByActiveAndLibraryIn(libraries);
        return projects.stream().map(SimpleProjectMapper.INSTANCE::projectToSimpleProjectDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SimpleProjectDTO> findAllByLibraryIn(final List<String> libraries) {
        final List<Project> projects = projectService.findAllByLibraryIn(libraries);
        return projects.stream().map(SimpleProjectMapper.INSTANCE::projectToSimpleProjectDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<SimpleProjectDTO> search(final String search,
                                         final String initiale,
                                         final boolean active,
                                         final List<String> libraries,
                                         final List<ProjectStatus> status,
                                         final List<String> providers,
                                         final Integer page,
                                         final Integer size) {
        final Page<Project> projects = projectService.search(search, initiale, libraries, status, providers, active, page, size);
        return projects.map(SimpleProjectMapper.INSTANCE::projectToSimpleProjectDTO);
    }

    @Transactional(readOnly = true)
    public List<AuditProjectRevisionDTO> getProjectsForWidget(final LocalDate fromDate, final List<String> libraries, final List<Project.ProjectStatus> statuses) {

        final List<Project> projects = projectService.findProjectsForWidget(fromDate, libraries, statuses);
        final List<AuditProjectRevisionDTO> revs = new ArrayList<>();

        projects.stream().filter(proj -> accessHelper.checkProject(proj.getIdentifier())).forEach(proj -> {
            final AuditProjectRevisionDTO dto = new AuditProjectRevisionDTO();
            dto.setIdentifier(proj.getIdentifier());
            dto.setName(proj.getName());
            dto.setStatus(proj.getStatus());
            dto.setTimestamp(proj.getStartDate().atStartOfDay().toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli());
            revs.add(dto);
        });
        return revs;
    }
}
