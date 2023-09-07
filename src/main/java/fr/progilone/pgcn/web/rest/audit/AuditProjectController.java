package fr.progilone.pgcn.web.rest.audit;

import static fr.progilone.pgcn.web.rest.project.security.AuthorizationConstants.*;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.dto.audit.AuditProjectRevisionDTO;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.service.audit.AuditProjectService;
import fr.progilone.pgcn.web.util.AccessHelper;
import jakarta.annotation.security.RolesAllowed;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/rest/audit/project")
public class AuditProjectController {

    private final AccessHelper accessHelper;
    private final AuditProjectService auditProjectService;

    @Autowired
    public AuditProjectController(final AccessHelper accessHelper, final AuditProjectService auditProjectService) {
        this.accessHelper = accessHelper;
        this.auditProjectService = auditProjectService;
    }

    @RequestMapping(method = RequestMethod.GET, params = {"from"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({PROJ_HAB7})
    public ResponseEntity<List<AuditProjectRevisionDTO>> getRevisions(@DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "from") final LocalDate fromDate,
                                                                      @RequestParam(value = "library", required = false) final List<String> libraries,
                                                                      @RequestParam(value = "status", required = false) List<Project.ProjectStatus> status) {

        // Chargement
        List<AuditProjectRevisionDTO> revisions = auditProjectService.getRevisions(fromDate, libraries, status);
        // Droits d'accès
        revisions = filterDTOs(revisions, AuditProjectRevisionDTO::getIdentifier);
        // Réponse
        return new ResponseEntity<>(revisions, HttpStatus.OK);
    }

    private <T> List<T> filterDTOs(final Collection<T> dtos, final Function<T, String> getIdentifierFn) {
        final Collection<Project> okProjects = accessHelper.filterProjects(dtos.stream().map(getIdentifierFn).collect(Collectors.toList()));
        return dtos.stream().filter(dto -> okProjects.stream().anyMatch(pj -> StringUtils.equals(getIdentifierFn.apply(dto), pj.getIdentifier()))).collect(Collectors.toList());
    }
}
