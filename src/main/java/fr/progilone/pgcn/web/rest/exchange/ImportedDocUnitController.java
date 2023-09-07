package fr.progilone.pgcn.web.rest.exchange;

import static fr.progilone.pgcn.web.rest.exchange.security.AuthorizationConstants.*;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.exchange.ImportReport;
import fr.progilone.pgcn.domain.exchange.ImportedDocUnit;
import fr.progilone.pgcn.service.exchange.ImportDocUnitService;
import fr.progilone.pgcn.service.exchange.ImportReportService;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Sébastien on 15/12/2016.
 */
@RestController
@RequestMapping(value = "/api/rest/impdocunit")
public class ImportedDocUnitController {

    private final ImportDocUnitService importDocUnitService;
    private final ImportReportService importReportService;
    private final LibraryAccesssHelper libraryAccesssHelper;

    @Autowired
    public ImportedDocUnitController(final ImportDocUnitService importDocUnitService,
                                     final ImportReportService importReportService,
                                     final LibraryAccesssHelper libraryAccesssHelper) {
        this.importDocUnitService = importDocUnitService;
        this.importReportService = importReportService;
        this.libraryAccesssHelper = libraryAccesssHelper;
    }

    @RequestMapping(method = RequestMethod.GET, params = {"report"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({EXC_HAB0})
    public ResponseEntity<Page<ImportedDocUnit>> findByImportReport(final HttpServletRequest request,
                                                                    @RequestParam(value = "report") ImportReport report,
                                                                    @RequestParam(value = "page", defaultValue = "0") int page,
                                                                    @RequestParam(value = "size", defaultValue = "10") int size,
                                                                    @RequestParam(value = "state", required = false) List<DocUnit.State> states,
                                                                    @RequestParam(value = "errors", defaultValue = "false") final boolean withErrors,
                                                                    @RequestParam(value = "duplicates", defaultValue = "false") final boolean withDuplicates) {
        final ImportReport importReport = importReportService.findByIdentifier(report.getIdentifier());
        // Non trouvé
        if (importReport == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur
        if (!libraryAccesssHelper.checkLibrary(request, importReport, ImportReport::getLibrary)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Recherche
        return new ResponseEntity<>(importDocUnitService.findByImportReport(report, page, size, states, withErrors, withDuplicates), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", params = {"process"}, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({EXC_HAB2})
    public ResponseEntity<?> updateProcess(final HttpServletRequest request,
                                           @PathVariable(name = "id") final String identifier,
                                           @RequestParam(value = "process", defaultValue = "false") String processStr) {
        final ImportedDocUnit importedDocUnit = importDocUnitService.findByIdentifier(identifier);
        // Non trouvé
        if (importedDocUnit == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur
        if (!libraryAccesssHelper.checkLibrary(request,
                                               importedDocUnit,
                                               imp -> imp.getDocUnit() != null ? imp.getDocUnit().getLibrary()
                                                                               : null)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Mise à jour
        ImportedDocUnit.Process process = StringUtils.equalsIgnoreCase(processStr, "false") ? null
                                                                                            : ImportedDocUnit.Process.valueOf(processStr);
        importDocUnitService.updateProcess(identifier, process);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
