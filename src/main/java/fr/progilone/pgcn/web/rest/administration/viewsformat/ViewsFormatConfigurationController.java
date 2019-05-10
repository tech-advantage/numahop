package fr.progilone.pgcn.web.rest.administration.viewsformat;

import static fr.progilone.pgcn.web.rest.administration.security.AuthorizationConstants.IMG_FORMAT_HAB0;
import static fr.progilone.pgcn.web.rest.administration.security.AuthorizationConstants.IMG_FORMAT_HAB1;
import static fr.progilone.pgcn.web.rest.checkconfiguration.security.AuthorizationConstants.CHECK_HAB1;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;

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

import com.codahale.metrics.annotation.Timed;

import fr.progilone.pgcn.domain.dto.administration.viewsFormat.SimpleViewsFormatConfigurationDTO;
import fr.progilone.pgcn.domain.dto.administration.viewsFormat.ViewsFormatConfigurationDTO;
import fr.progilone.pgcn.domain.dto.checkconfiguration.CheckConfigurationDTO;
import fr.progilone.pgcn.exception.PgcnException;
import fr.progilone.pgcn.service.administration.viewsformat.UIViewsFormatConfigurationService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;

@RestController
@RequestMapping(value = "/api/rest/viewsformat")
public class ViewsFormatConfigurationController extends AbstractRestController {
    
    private final UIViewsFormatConfigurationService uiViewsFormatConfigurationService;
    private final LibraryAccesssHelper libraryAccesssHelper;
    
    @Autowired
    public ViewsFormatConfigurationController(final UIViewsFormatConfigurationService uiViewsFormatConfigurationService,
                                        final LibraryAccesssHelper libraryAccesssHelper) {
        this.uiViewsFormatConfigurationService = uiViewsFormatConfigurationService;
        this.libraryAccesssHelper = libraryAccesssHelper;
    }
    
    @RequestMapping(method = RequestMethod.GET, params = {"search"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({IMG_FORMAT_HAB0})
    public ResponseEntity<Page<SimpleViewsFormatConfigurationDTO>> search(final HttpServletRequest request,
                                                                    @RequestParam(value = "search", required = false) final String search,
                                                                    @RequestParam(value = "libraries", required = false) final List<String> libraries,
                                                                    @RequestParam(value = "page", required = false, defaultValue = "0")
                                                                    final Integer page,
                                                                    @RequestParam(value = "size", required = false, defaultValue = "10")
                                                                    final Integer size) {
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);
        return new ResponseEntity<>(uiViewsFormatConfigurationService.search(search, filteredLibraries, page, size), HttpStatus.OK);
    }
    
    @RequestMapping(method = RequestMethod.POST)
    @Timed
    @RolesAllowed({IMG_FORMAT_HAB1})
    public ResponseEntity<ViewsFormatConfigurationDTO> create(final HttpServletRequest request,
                                                        @RequestBody final ViewsFormatConfigurationDTO formatConfiguration) throws PgcnException {
        final ViewsFormatConfigurationDTO savedConfiguration = uiViewsFormatConfigurationService.create(formatConfiguration);
        return new ResponseEntity<>(savedConfiguration, HttpStatus.CREATED);
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    @Timed
    @RolesAllowed({CHECK_HAB1})
    public ResponseEntity<ViewsFormatConfigurationDTO> update(final HttpServletRequest request,
                                                        @RequestBody final ViewsFormatConfigurationDTO configuration) throws PgcnException {
        final ViewsFormatConfigurationDTO savedConfiguration = uiViewsFormatConfigurationService.update(configuration);
        return new ResponseEntity<>(savedConfiguration, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Timed
    @RolesAllowed({IMG_FORMAT_HAB1})
    public ResponseEntity<CheckConfigurationDTO> delete(final HttpServletRequest request, @PathVariable final String id) {
        uiViewsFormatConfigurationService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @RequestMapping(method = RequestMethod.GET, params = {"project"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({IMG_FORMAT_HAB0})
    public ResponseEntity<List<SimpleViewsFormatConfigurationDTO>> getByProjectId(final HttpServletRequest request,
                                                                            @RequestParam(value = "project") final String projectId) {
        final List<SimpleViewsFormatConfigurationDTO> configurations = uiViewsFormatConfigurationService.getAllByProjectId(projectId);
        return createResponseEntity(configurations);
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({IMG_FORMAT_HAB0})
    public ResponseEntity<ViewsFormatConfigurationDTO> getById(final HttpServletRequest request, @PathVariable final String id) {
        // Chargement
        final ViewsFormatConfigurationDTO conf = uiViewsFormatConfigurationService.getOne(id);
        // Non trouvé
        if (conf == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(conf, HttpStatus.OK);
    }
    
}
