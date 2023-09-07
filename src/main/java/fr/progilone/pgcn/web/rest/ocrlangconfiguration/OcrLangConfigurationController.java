package fr.progilone.pgcn.web.rest.ocrlangconfiguration;

import static fr.progilone.pgcn.web.rest.ocrlangconfiguration.security.AuthorizationConstants.OCR_LANG_HAB0;
import static fr.progilone.pgcn.web.rest.ocrlangconfiguration.security.AuthorizationConstants.OCR_LANG_HAB1;
import static fr.progilone.pgcn.web.rest.ocrlangconfiguration.security.AuthorizationConstants.OCR_LANG_HAB2;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.dto.ocrlangconfiguration.OcrLangConfigurationDTO;
import fr.progilone.pgcn.domain.dto.ocrlangconfiguration.SimpleOcrLangConfigDTO;
import fr.progilone.pgcn.exception.PgcnException;
import fr.progilone.pgcn.service.ocrlangconfiguration.ui.UIOcrLangConfigurationService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
@RequestMapping(value = "/api/rest/ocrlangconfiguration")
public class OcrLangConfigurationController extends AbstractRestController {

    private final LibraryAccesssHelper libraryAccesssHelper;
    private final UIOcrLangConfigurationService uiOcrLangConfigurationService;

    @Autowired
    public OcrLangConfigurationController(final LibraryAccesssHelper libraryAccesssHelper, final UIOcrLangConfigurationService uiOcrLangConfigurationService) {
        this.libraryAccesssHelper = libraryAccesssHelper;
        this.uiOcrLangConfigurationService = uiOcrLangConfigurationService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @Timed
    @RolesAllowed({OCR_LANG_HAB1})
    public ResponseEntity<OcrLangConfigurationDTO> create(final HttpServletRequest request, @RequestBody final OcrLangConfigurationDTO configuration) throws PgcnException {
        final OcrLangConfigurationDTO savedConfiguration = uiOcrLangConfigurationService.create(configuration);
        return new ResponseEntity<>(savedConfiguration, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Timed
    @RolesAllowed({OCR_LANG_HAB2})
    public ResponseEntity<?> delete(final HttpServletRequest request, @PathVariable final String id) {
        uiOcrLangConfigurationService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({OCR_LANG_HAB0})
    public ResponseEntity<OcrLangConfigurationDTO> getById(final HttpServletRequest request, @PathVariable final String id) {
        final OcrLangConfigurationDTO configuration = uiOcrLangConfigurationService.getOne(id);
        return createResponseEntity(configuration);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"search"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({OCR_LANG_HAB0})
    public ResponseEntity<Page<SimpleOcrLangConfigDTO>> search(final HttpServletRequest request,
                                                               @RequestParam(value = "search", required = false) final String search,
                                                               @RequestParam(value = "libraries", required = false) final List<String> libraries,
                                                               @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                                               @RequestParam(value = "size", required = false, defaultValue = "10") final Integer size) {

        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);
        return new ResponseEntity<>(uiOcrLangConfigurationService.search(search, filteredLibraries, page, size), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    @Timed
    @RolesAllowed({OCR_LANG_HAB1})
    public ResponseEntity<OcrLangConfigurationDTO> update(final HttpServletRequest request, @RequestBody final OcrLangConfigurationDTO configuration) throws PgcnException {
        return new ResponseEntity<>(uiOcrLangConfigurationService.update(configuration), HttpStatus.OK);
    }
}
