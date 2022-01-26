package fr.progilone.pgcn.web.rest.project;

import static fr.progilone.pgcn.domain.project.Project.ProjectStatus.CANCELED;
import static fr.progilone.pgcn.domain.project.Project.ProjectStatus.CLOSED;
import static fr.progilone.pgcn.web.rest.project.security.AuthorizationConstants.PROJ_HAB0;
import static fr.progilone.pgcn.web.rest.project.security.AuthorizationConstants.PROJ_HAB1;
import static fr.progilone.pgcn.web.rest.project.security.AuthorizationConstants.PROJ_HAB3;
import static fr.progilone.pgcn.web.rest.project.security.AuthorizationConstants.PROJ_HAB4;
import static fr.progilone.pgcn.web.rest.project.security.AuthorizationConstants.PROJ_HAB5;
import static fr.progilone.pgcn.web.rest.project.security.AuthorizationConstants.PROJ_HAB6;
import static fr.progilone.pgcn.web.rest.project.security.AuthorizationConstants.PROJ_HAB7;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import fr.progilone.pgcn.domain.dto.audit.AuditProjectRevisionDTO;
import fr.progilone.pgcn.domain.dto.project.ProjectDTO;
import fr.progilone.pgcn.domain.dto.project.SimpleProjectDTO;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.domain.project.Project.ProjectStatus;
import fr.progilone.pgcn.exception.PgcnException;
import fr.progilone.pgcn.service.es.EsProjectService;
import fr.progilone.pgcn.service.project.ProjectService;
import fr.progilone.pgcn.service.project.ui.UIProjectService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import fr.progilone.pgcn.web.util.AccessHelper;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;

@RestController
@RequestMapping(value = "/api/rest/project")
public class ProjectController extends AbstractRestController {

    private final AccessHelper accessHelper;
    private final EsProjectService esProjectService;
    private final LibraryAccesssHelper libraryAccesssHelper;
    private final ProjectService projectService;
    private final UIProjectService uiProjectService;

    @Autowired
    public ProjectController(final ProjectService projectService,
                             final UIProjectService uiProjectService,
                             final AccessHelper accessHelper,
                             final EsProjectService esProjectService,
                             final LibraryAccesssHelper libraryAccesssHelper) {
        this.projectService = projectService;
        this.uiProjectService = uiProjectService;
        this.accessHelper = accessHelper;
        this.esProjectService = esProjectService;
        this.libraryAccesssHelper = libraryAccesssHelper;
    }

    @RequestMapping(method = RequestMethod.POST)
    @Timed
    @RolesAllowed({PROJ_HAB0})
    public ResponseEntity<ProjectDTO> create(final HttpServletRequest request, @RequestBody final ProjectDTO project) throws PgcnException {
        // Droit d'accès à la bibliothèque
        if (!libraryAccesssHelper.checkLibrary(request, project.getLibrary().getIdentifier())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final ProjectDTO savedProject = uiProjectService.create(project);
        esProjectService.indexAsync(savedProject.getIdentifier());  // Moteur de recherche
        return new ResponseEntity<>(savedProject, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Timed
    @RolesAllowed({PROJ_HAB4})
    public ResponseEntity<ProjectDTO> delete(final HttpServletRequest request, @PathVariable final String id) {
        // Droits d'accès
        if (!accessHelper.checkProject(id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        projectService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, params = {"delete"})
    @Timed
    @RolesAllowed({PROJ_HAB4})
    public void delete(final HttpServletResponse response, @RequestBody final List<ProjectDTO> projects) {
        // Droits d'accès
        final Collection<Project> filteredProjects =
            accessHelper.filterProjects(projects.stream().map(ProjectDTO::getIdentifier).collect(Collectors.toList()));
        uiProjectService.delete(filteredProjects);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST, params = {"idDocs"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> addDocUnits(@PathVariable final String id, @RequestBody final List<String> idDocs) {
        // Droits d'accès
        if (!accessHelper.checkProject(id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        uiProjectService.addDocUnits(id, idDocs);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST, params = {"idLibraries"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({PROJ_HAB3})
    public ResponseEntity<?> addLibraries(@PathVariable final String id, @RequestBody final List<String> idLibraries) {
        // Droits d'accès
        if (!accessHelper.checkProject(id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        uiProjectService.addLibraries(id, idLibraries);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"search"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({PROJ_HAB7})
    public ResponseEntity<Page<SimpleProjectDTO>> search(final HttpServletRequest request,
                                                         @RequestParam(value = "search", required = false) final String search,
                                                         @RequestParam(value = "initiale", required = false) final String initiale,
                                                         @RequestParam(value = "active", required = false, defaultValue = "true") final boolean active,
                                                         @RequestParam(value = "libraries", required = false) final List<String> libraries,
                                                         @RequestParam(value = "status", required = false) final List<ProjectStatus> status,
                                                         @RequestParam(value = "provider", required = false) final List<String> providers,
                                                         @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                                         @RequestParam(value = "size", required = false, defaultValue = "" + Integer.MAX_VALUE) final
                                                             Integer size) {
        // Droits d'accès
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);
        return new ResponseEntity<>(uiProjectService.search(search, initiale, active, filteredLibraries, status, providers, page, size),
                                    HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"searchProject"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({PROJ_HAB7})
    public ResponseEntity<List<SimpleProjectDTO>> searchProject(final HttpServletRequest request,
                                                                @RequestParam(value = "searchProject", required = false) final String searchProject,
                                                                @RequestParam(value = "initiale", required = false) final String initiale,
                                                                @RequestParam(value = "active", required = false, defaultValue = "true")
                                                                final boolean active,
                                                                @RequestParam(value = "libraries", required = false) final List<String> libraries,
                                                                @RequestParam(value = "statuses", required = false)
                                                                final List<ProjectStatus> statuses) {
        // Droits d'accès
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);
        return new ResponseEntity<>(uiProjectService.loadCreatedProjects(searchProject, initiale, filteredLibraries, statuses, active),
                                    HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"widget", "from"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({PROJ_HAB7})
    public ResponseEntity<List<AuditProjectRevisionDTO>> getProjectsForWidget (final HttpServletRequest request,
                                                                        @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "from") final LocalDate fromDate,
                                                                        @RequestParam(value = "library", required = false) final List<String> libraries,
                                                                        @RequestParam(value = "status", required = false) final List<Project.ProjectStatus> statuses) {
        // Droits d'accès
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);
        // Chargement
        final List<AuditProjectRevisionDTO> revisions = uiProjectService.getProjectsForWidget(fromDate, filteredLibraries, statuses);
        // Réponse
        return new ResponseEntity<>(revisions, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({PROJ_HAB7})
    public ResponseEntity<ProjectDTO> getById(@PathVariable final String id) {
        // Droits d'accès
        if (!accessHelper.checkProject(id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final ProjectDTO project = uiProjectService.getOne(id);
        return createResponseEntity(project);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"dto"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({PROJ_HAB7})
    public ResponseEntity<Collection<SimpleProjectDTO>> findAllActiveDTO(final HttpServletRequest request) {
        Collection<SimpleProjectDTO> simpleProjectDTOs = uiProjectService.findAllActiveDTO();
        // Droits d'accès
        //simpleProjectDTOs = libraryAccesssHelper.filterObjectsByLibrary(request, simpleProjectDTOs, dto -> dto.getLibrary().getIdentifier());
        // controle suppl pour le user
        final Collection<SimpleProjectDTO> filteredProjects = simpleProjectDTOs.stream().filter(proj -> accessHelper.checkProject(proj.getIdentifier()))
                                                                                        .collect(Collectors.toList());
        return createResponseEntity(filteredProjects);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"dto", "libraries"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Collection<SimpleProjectDTO>> findAllActiveDTO(final HttpServletRequest request, @RequestParam final List<String> libraries) {
        Collection<SimpleProjectDTO> simpleProjectDTOs = uiProjectService.findAllActiveByLibraryIn(libraries);
        simpleProjectDTOs = libraryAccesssHelper.filterObjectsByLibrary(request, simpleProjectDTOs, dto -> dto.getLibrary().getIdentifier());
        return createResponseEntity(simpleProjectDTOs);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"dto2"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({PROJ_HAB7})
    public ResponseEntity<Collection<SimpleProjectDTO>> findAllDTO(final HttpServletRequest request) {
        Collection<SimpleProjectDTO> simpleProjectDTOs = uiProjectService.findAllDTO();
        // Droits d'accès
        simpleProjectDTOs = libraryAccesssHelper.filterObjectsByLibrary(request, simpleProjectDTOs, dto -> dto.getLibrary().getIdentifier());
        // controle suppl pour le user
        final Collection<SimpleProjectDTO> filteredProjects = simpleProjectDTOs.stream().filter(proj -> accessHelper.checkProject(proj.getIdentifier()))
                                                                                        .collect(Collectors.toList());
        return createResponseEntity(filteredProjects);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"dto2", "libraries"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Collection<SimpleProjectDTO>> findAllDTO(final HttpServletRequest request, @RequestParam final List<String> libraries) {
        Collection<SimpleProjectDTO> simpleProjectDTOs = uiProjectService.findAllByLibraryIn(libraries);
        simpleProjectDTOs = libraryAccesssHelper.filterObjectsByLibrary(request, simpleProjectDTOs, dto -> dto.getLibrary().getIdentifier());
        return createResponseEntity(simpleProjectDTOs);
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    @Timed
    @RolesAllowed({PROJ_HAB1})
    public ResponseEntity<ProjectDTO> update(final HttpServletRequest request, @RequestBody final ProjectDTO projectDTO) throws PgcnException {
        // Droits d'accès
        if (!accessHelper.checkProject(projectDTO.getIdentifier())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // PROJ_HAB5 (suspendre) => désactiver le projet, cloturer le projet
        final boolean canDisableProj = request.isUserInRole(PROJ_HAB5);
        // PROJ_HAB6 (annuler)
        final boolean canCancelProj = request.isUserInRole(PROJ_HAB6);

        if (!canDisableProj || !canCancelProj) {
            final Project project = projectService.findByIdentifier(projectDTO.getIdentifier());

            // Le projet est désactivé ou cloturé
            if (!canDisableProj && (project.isActive() && !projectDTO.isActive() || (project.getStatus() != CLOSED
                                                                                     && StringUtils.equals(projectDTO.getStatus(), "CLOSED")))) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            // Le projet est annulé
            if (!canCancelProj && project.getStatus() != CANCELED && StringUtils.equals(projectDTO.getStatus(), "CANCELED")) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        final ProjectDTO savedProject = uiProjectService.update(projectDTO);
        esProjectService.indexAsync(savedProject.getIdentifier());  // Moteur de recherche
        return new ResponseEntity<>(savedProject, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST, params = {"cancelProj"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({PROJ_HAB6})
    public ResponseEntity<ProjectDTO> cancelProject(@PathVariable final String id, @RequestBody final ProjectDTO projectDTO) {
        // Droits d'accès
        if (!accessHelper.checkProject(id) || !StringUtils.equals(projectDTO.getStatus(), "CANCELED")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final ProjectDTO canceled = uiProjectService.cancelProject(projectDTO);
        return new ResponseEntity<>(canceled, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST, params = {"suspendProj"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({PROJ_HAB5})
    public ResponseEntity<ProjectDTO> suspendProject(@PathVariable final String id, @RequestBody final ProjectDTO projectDTO) {
        // Droits d'accès
        if (!accessHelper.checkProject(id) || !StringUtils.equals(projectDTO.getStatus(), "PENDING")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final ProjectDTO suspended = uiProjectService.suspendProject(projectDTO);
        return new ResponseEntity<>(suspended, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST, params = {"reactivProj"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({PROJ_HAB5})
    public ResponseEntity<ProjectDTO> reactivateProject(@PathVariable final String id, @RequestBody final ProjectDTO projectDTO) {
        // Droits d'accès
        if (!accessHelper.checkProject(id) || !StringUtils.equals(projectDTO.getStatus(), "PENDING")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final ProjectDTO activated = uiProjectService.reactivateProject(projectDTO);
        return new ResponseEntity<>(activated, HttpStatus.OK);
    }
}
