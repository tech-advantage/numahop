package fr.progilone.pgcn.web.rest.document.conditionreport;

import static fr.progilone.pgcn.web.rest.document.security.AuthorizationConstants.*;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReport;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReportDetail;
import fr.progilone.pgcn.exception.PgcnException;
import fr.progilone.pgcn.service.document.conditionreport.ConditionReportDetailService;
import fr.progilone.pgcn.service.document.conditionreport.ConditionReportService;
import fr.progilone.pgcn.service.es.EsConditionReportService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import fr.progilone.pgcn.web.util.AccessHelper;
import fr.progilone.pgcn.web.util.WorkflowAccessHelper;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/rest/condreport_detail")
public class ConditionReportDetailController extends AbstractRestController {

    private final AccessHelper accessHelper;
    private final WorkflowAccessHelper workflowAccessHelper;
    private final ConditionReportService conditionReportService;
    private final ConditionReportDetailService conditionReportDetailService;
    private final EsConditionReportService esConditionReportService;

    @Autowired
    public ConditionReportDetailController(final AccessHelper accessHelper,
                                           final WorkflowAccessHelper workflowAccessHelper,
                                           final ConditionReportService conditionReportService,
                                           final ConditionReportDetailService conditionReportDetailService,
                                           final EsConditionReportService esConditionReportService) {
        this.accessHelper = accessHelper;
        this.workflowAccessHelper = workflowAccessHelper;
        this.conditionReportService = conditionReportService;
        this.conditionReportDetailService = conditionReportDetailService;
        this.esConditionReportService = esConditionReportService;
    }

    @RequestMapping(method = RequestMethod.POST,
                    params = {"type",
                              "detail"},
                    produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(COND_REPORT_HAB1)
    public ResponseEntity<ConditionReportDetail> create(@RequestParam(name = "type") final ConditionReportDetail.Type type,
                                                        @RequestParam(name = "detail") final String fromDetailId) throws PgcnException {
        // droits d'accès à l'ud
        final DocUnit docUnit = conditionReportDetailService.findDocUnitByIdentifier(fromDetailId);
        if (!accessHelper.checkDocUnit(docUnit.getIdentifier())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // création
        final ConditionReportDetail createdDetail = conditionReportDetailService.create(type, fromDetailId);
        if (createdDetail == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            // Indexation
            final ConditionReport report = conditionReportDetailService.findParentByIdentifier(createdDetail.getIdentifier());
            esConditionReportService.indexAsync(report.getIdentifier());

            return new ResponseEntity<>(createdDetail, HttpStatus.CREATED);
        }
    }

    @RequestMapping(value = "/{identifier}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(COND_REPORT_HAB3)
    public void delete(final HttpServletResponse response, @PathVariable final String identifier) {
        // droits d'accès à l'ud
        final DocUnit docUnit = conditionReportDetailService.findDocUnitByIdentifier(identifier);
        if (!accessHelper.checkDocUnit(docUnit.getIdentifier())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        // Droit par rapport au workflow
        final ConditionReportDetail detail = conditionReportDetailService.findByIdentifier(identifier);
        if (!workflowAccessHelper.canConstatDetailBeModified(docUnit.getIdentifier(), detail.getType())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        final ConditionReport report = conditionReportDetailService.findParentByIdentifier(identifier);
        // Suppression
        conditionReportDetailService.delete(identifier);
        // Indexation
        esConditionReportService.indexAsync(report.getIdentifier());

        response.setStatus(HttpServletResponse.SC_OK);
    }

    @RequestMapping(value = "/{identifier}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(COND_REPORT_HAB0)
    public ResponseEntity<ConditionReportDetail> findByIdentifier(@PathVariable final String identifier) {
        // droits d'accès à l'ud
        final DocUnit docUnit = conditionReportDetailService.findDocUnitByIdentifier(identifier);
        if (!accessHelper.checkDocUnit(docUnit.getIdentifier())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return createResponseEntity(conditionReportDetailService.findByIdentifier(identifier));
    }

    @RequestMapping(method = RequestMethod.GET, params = {"report"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(COND_REPORT_HAB0)
    public ResponseEntity<List<ConditionReportDetail>> findByConditionReport(@RequestParam(name = "report") final String reportId) {
        final DocUnit docUnit = conditionReportService.findDocUnitByIdentifier(reportId);
        // droits d'accès à l'ud
        if (!accessHelper.checkDocUnit(docUnit.getIdentifier())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return createResponseEntity(conditionReportDetailService.findByConditionReport(reportId));
    }

    @RequestMapping(value = "/{identifier}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(COND_REPORT_HAB2)
    public ResponseEntity<ConditionReportDetail> update(@RequestBody final ConditionReportDetail value) throws PgcnException {
        // droits d'accès à l'ud
        final DocUnit docUnit = conditionReportDetailService.findDocUnitByIdentifier(value.getIdentifier());
        if (!accessHelper.checkDocUnit(docUnit.getIdentifier())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Droit par rapport au workflow
        final ConditionReportDetail detail = conditionReportDetailService.findByIdentifier(value.getIdentifier());
        if (!workflowAccessHelper.canConstatDetailBeModified(docUnit.getIdentifier(), detail.getType())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Mise à jour
        final ConditionReportDetail savedDetail = conditionReportDetailService.save(value);
        // Indexation
        final ConditionReport report = conditionReportDetailService.findParentByIdentifier(savedDetail.getIdentifier());
        esConditionReportService.indexAsync(report.getIdentifier());

        return new ResponseEntity<>(savedDetail, HttpStatus.OK);
    }

    @RequestMapping(value = "/{identifier}", params = {"confirmvalid"}, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(COND_REPORT_HAB2)
    public ResponseEntity<ConditionReportDetail> confirmInitialValid(@RequestBody final ConditionReportDetail value) throws PgcnException {
        // droits d'accès à l'ud
        final DocUnit docUnit = conditionReportDetailService.findDocUnitByIdentifier(value.getIdentifier());
        if (!accessHelper.checkDocUnit(docUnit.getIdentifier())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        // Mise à jour
        final ConditionReportDetail savedDetail = conditionReportDetailService.updateProvWriter(value);
        // Indexation
        final ConditionReport report = conditionReportDetailService.findParentByIdentifier(savedDetail.getIdentifier());
        esConditionReportService.indexAsync(report.getIdentifier());

        return new ResponseEntity<>(savedDetail, HttpStatus.OK);
    }

}
