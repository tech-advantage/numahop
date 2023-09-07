package fr.progilone.pgcn.web.rest.ftpconfiguration;

import static fr.progilone.pgcn.web.rest.administration.security.AuthorizationConstants.*;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.dto.ftpconfiguration.FTPConfigurationDTO;
import fr.progilone.pgcn.domain.dto.ftpconfiguration.SimpleFTPConfigurationDTO;
import fr.progilone.pgcn.domain.ftpconfiguration.FTPConfiguration;
import fr.progilone.pgcn.exception.PgcnException;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.service.ftpconfiguration.FTPConfigurationService;
import fr.progilone.pgcn.service.ftpconfiguration.ui.UIFTPConfigurationService;
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
@RequestMapping(value = "/api/rest/ftpconfiguration")
public class FTPConfigurationController extends AbstractRestController {

    private final FTPConfigurationService ftpConfigurationService;
    private final UIFTPConfigurationService uiFTPConfigurationService;
    private final LibraryAccesssHelper libraryAccesssHelper;

    @Autowired
    public FTPConfigurationController(final FTPConfigurationService ftpConfigurationService,
                                      final UIFTPConfigurationService uiFTPConfigurationService,
                                      final LibraryAccesssHelper libraryAccesssHelper) {
        this.ftpConfigurationService = ftpConfigurationService;
        this.uiFTPConfigurationService = uiFTPConfigurationService;
        this.libraryAccesssHelper = libraryAccesssHelper;
    }

    @RequestMapping(method = RequestMethod.POST)
    @Timed
    @RolesAllowed({FTP_HAB1})
    public ResponseEntity<FTPConfigurationDTO> create(final HttpServletRequest request, @RequestBody final FTPConfigurationDTO ftpConfiguration) throws PgcnException,
                                                                                                                                                 PgcnTechnicalException {
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur, pour la conf à importer
        if (ftpConfiguration.getLibrary() == null || !libraryAccesssHelper.checkLibrary(request, ftpConfiguration.getLibrary().getIdentifier())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Création
        final FTPConfigurationDTO savedConfiguration = uiFTPConfigurationService.create(ftpConfiguration);
        return new ResponseEntity<>(savedConfiguration, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Timed
    @RolesAllowed({FTP_HAB2})
    public ResponseEntity<FTPConfigurationDTO> delete(final HttpServletRequest request, @PathVariable final String id) {
        // Chargement
        final FTPConfiguration conf = ftpConfigurationService.getOne(id);
        // Non trouvé
        if (conf == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur
        if (!libraryAccesssHelper.checkLibrary(request, conf, FTPConfiguration::getLibrary)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Suppression
        ftpConfigurationService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"search"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({FTP_HAB0})
    public ResponseEntity<Page<SimpleFTPConfigurationDTO>> search(final HttpServletRequest request,
                                                                  @RequestParam(value = "search", required = false) final String search,
                                                                  @RequestParam(value = "libraries", required = false) final List<String> libraries,
                                                                  @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                                                  @RequestParam(value = "size", required = false, defaultValue = "10") final Integer size) {
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);
        return new ResponseEntity<>(uiFTPConfigurationService.search(search, filteredLibraries, page, size), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({FTP_HAB0})
    public ResponseEntity<FTPConfigurationDTO> getById(final HttpServletRequest request, @PathVariable final String id) {
        // Chargement
        final FTPConfigurationDTO conf = uiFTPConfigurationService.getOne(id);
        // Non trouvé
        if (conf == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur
        if (conf.getLibrary() == null || !libraryAccesssHelper.checkLibrary(request, conf.getLibrary().getIdentifier())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Réponse
        return createResponseEntity(conf);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"project"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({FTP_HAB0})
    public ResponseEntity<List<SimpleFTPConfigurationDTO>> getByProjectId(final HttpServletRequest request, @RequestParam(value = "project") final String project) {
        final List<String> libraries = libraryAccesssHelper.getLibraryFilter(request, null);
        final List<SimpleFTPConfigurationDTO> configuration = uiFTPConfigurationService.getAllByProjectId(project, libraries);
        return createResponseEntity(configuration);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    @Timed
    @RolesAllowed({FTP_HAB1})
    public ResponseEntity<FTPConfigurationDTO> update(final HttpServletRequest request, @RequestBody final FTPConfigurationDTO configuration) throws PgcnTechnicalException {

        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur, pour la conf à importer
        if (configuration.getLibrary() == null || !libraryAccesssHelper.checkLibrary(request, configuration.getLibrary().getIdentifier())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Chargement du conf existant
        final FTPConfiguration dbConfiguration = ftpConfigurationService.getOne(configuration.getIdentifier());
        // Non trouvé
        if (dbConfiguration == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur, pour la conf existant
        if (!libraryAccesssHelper.checkLibrary(request, dbConfiguration, FTPConfiguration::getLibrary)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Mise à jour
        final FTPConfigurationDTO savedConfiguration = uiFTPConfigurationService.update(configuration);
        return new ResponseEntity<>(savedConfiguration, HttpStatus.OK);
    }
}
