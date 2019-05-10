package fr.progilone.pgcn.web.rest.audit;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.dto.audit.AuditLotRevisionDTO;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.service.audit.AuditLotService;
import fr.progilone.pgcn.web.util.AccessHelper;
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

import javax.annotation.security.RolesAllowed;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static fr.progilone.pgcn.web.rest.lot.security.AuthorizationConstants.*;

@RestController
@RequestMapping(value = "/api/rest/audit/lot")
public class AuditLotController {

    private final AccessHelper accessHelper;
    private final AuditLotService auditLotService;

    @Autowired
    public AuditLotController(final AccessHelper accessHelper, final AuditLotService auditLotService) {
        this.accessHelper = accessHelper;
        this.auditLotService = auditLotService;
    }

    @RequestMapping(method = RequestMethod.GET, params = {"from"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({LOT_HAB3})
    public ResponseEntity<List<AuditLotRevisionDTO>> getRevisions(
        @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "from") final LocalDate fromDate,
        @RequestParam(value = "library", required = false) final List<String> libraries,
        @RequestParam(value = "project", required = false) final List<String> projects,
        @RequestParam(value = "status", required = false) List<Lot.LotStatus> status) {

        // Chargement
        List<AuditLotRevisionDTO> revisions = auditLotService.getRevisions(fromDate, libraries, projects, status);
        // Droits d'accès
        revisions = filterDTOs(revisions, AuditLotRevisionDTO::getIdentifier);
        // Réponse
        return new ResponseEntity<>(revisions, HttpStatus.OK);
    }

    private <T> List<T> filterDTOs(final Collection<T> dtos, final Function<T, String> getIdentifierFn) {
        final Collection<Lot> okLots = accessHelper.filterLots(dtos.stream().map(getIdentifierFn).collect(Collectors.toList()));
        return dtos.stream()
                   .filter(dto -> okLots.stream().anyMatch(lot -> StringUtils.equals(getIdentifierFn.apply(dto), lot.getIdentifier())))
                   .collect(Collectors.toList());
    }
}
