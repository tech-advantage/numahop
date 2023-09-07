package fr.progilone.pgcn.web.rest.audit;

import static fr.progilone.pgcn.web.rest.train.security.AuthorizationConstants.*;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.dto.audit.AuditTrainRevisionDTO;
import fr.progilone.pgcn.domain.train.Train;
import fr.progilone.pgcn.service.audit.AuditTrainService;
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
@RequestMapping(value = "/api/rest/audit/train")
public class AuditTrainController {

    private final AccessHelper accessHelper;
    private final AuditTrainService auditTrainService;

    @Autowired
    public AuditTrainController(final AccessHelper accessHelper, final AuditTrainService auditTrainService) {
        this.accessHelper = accessHelper;
        this.auditTrainService = auditTrainService;
    }

    @RequestMapping(method = RequestMethod.GET, params = {"from"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({TRA_HAB3})
    public ResponseEntity<List<AuditTrainRevisionDTO>> getRevisions(@DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "from") final LocalDate fromDate,
                                                                    @RequestParam(value = "library", required = false) final List<String> libraries,
                                                                    @RequestParam(value = "project", required = false) final List<String> projects,
                                                                    @RequestParam(value = "status", required = false) List<Train.TrainStatus> status) {

        // Chargement
        List<AuditTrainRevisionDTO> revisions = auditTrainService.getRevisions(fromDate, libraries, projects, status);
        // Droits d'accès
        revisions = filterDTOs(revisions, AuditTrainRevisionDTO::getIdentifier);
        // Réponse
        return new ResponseEntity<>(revisions, HttpStatus.OK);
    }

    private <T> List<T> filterDTOs(final Collection<T> dtos, final Function<T, String> getIdentifierFn) {
        final Collection<Train> okTrains = accessHelper.filterTrains(dtos.stream().map(getIdentifierFn).collect(Collectors.toList()));
        return dtos.stream().filter(dto -> okTrains.stream().anyMatch(train -> StringUtils.equals(getIdentifierFn.apply(dto), train.getIdentifier()))).collect(Collectors.toList());
    }
}
