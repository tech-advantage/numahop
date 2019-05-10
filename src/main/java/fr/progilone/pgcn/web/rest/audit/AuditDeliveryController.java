package fr.progilone.pgcn.web.rest.audit;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.delivery.Delivery;
import fr.progilone.pgcn.domain.dto.audit.AuditDeliveryRevisionDTO;
import fr.progilone.pgcn.service.audit.AuditDeliveryService;
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

import static fr.progilone.pgcn.web.rest.delivery.security.AuthorizationConstants.*;

@RestController
@RequestMapping(value = "/api/rest/audit/delivery")
public class AuditDeliveryController {

    private final AccessHelper accessHelper;
    private final AuditDeliveryService auditDeliveryService;

    @Autowired
    public AuditDeliveryController(final AccessHelper accessHelper, final AuditDeliveryService auditDeliveryService) {
        this.accessHelper = accessHelper;
        this.auditDeliveryService = auditDeliveryService;
    }

    @RequestMapping(method = RequestMethod.GET, params = {"from"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({DEL_HAB0})
    public ResponseEntity<List<AuditDeliveryRevisionDTO>> getRevisions(
        @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "from") final LocalDate fromDate,
        @RequestParam(value = "library", required = false) final List<String> libraries,
        @RequestParam(value = "project", required = false) final List<String> projects,
        @RequestParam(value = "lot", required = false) final List<String> lots,
        @RequestParam(value = "status", required = false) final List<Delivery.DeliveryStatus> status) {

        // Chargement
        List<AuditDeliveryRevisionDTO> revisions = auditDeliveryService.getRevisions(fromDate, libraries, projects, lots, status);
        // Droits d'accès
        revisions = filterDTOs(revisions, AuditDeliveryRevisionDTO::getIdentifier);
        // Réponse
        return new ResponseEntity<>(revisions, HttpStatus.OK);
    }

    private <T> List<T> filterDTOs(final Collection<T> dtos, final Function<T, String> getIdentifierFn) {
        final Collection<Delivery> okDeliverys = accessHelper.filterDeliveries(dtos.stream().map(getIdentifierFn).collect(Collectors.toList()));
        return dtos.stream()
                   .filter(dto -> okDeliverys.stream().anyMatch(delivery -> StringUtils.equals(getIdentifierFn.apply(dto), delivery.getIdentifier())))
                   .collect(Collectors.toList());
    }
}
