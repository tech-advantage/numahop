package fr.progilone.pgcn.web.rest.exportftpconfiguration;

import static fr.progilone.pgcn.web.rest.administration.security.AuthorizationConstants.EXP_FTP_HAB0;
import static fr.progilone.pgcn.web.rest.administration.security.AuthorizationConstants.EXP_FTP_HAB1;
import static fr.progilone.pgcn.web.rest.administration.security.AuthorizationConstants.EXP_FTP_HAB2;

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

import fr.progilone.pgcn.domain.dto.exportftpconfiguration.ExportFTPConfigurationDTO;
import fr.progilone.pgcn.domain.dto.exportftpconfiguration.SimpleExportFTPConfDTO;
import fr.progilone.pgcn.domain.exportftpconfiguration.ExportFTPConfiguration;
import fr.progilone.pgcn.exception.PgcnException;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.service.exportftpconfiguration.ExportFTPConfigurationService;
import fr.progilone.pgcn.service.exportftpconfiguration.UIExportFTPConfigurationService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;

@RestController
@RequestMapping(value = "/api/rest/exportftpconfiguration")
public class ExportFTPConfigurationController extends AbstractRestController {

    
    private final ExportFTPConfigurationService exportFtpConfigurationService;
    private final UIExportFTPConfigurationService uiExportFTPConfigurationService;
    private final LibraryAccesssHelper libraryAccesssHelper;

    @Autowired
    public ExportFTPConfigurationController(final ExportFTPConfigurationService exportFtpConfigurationService,
                                            final UIExportFTPConfigurationService uiExportFTPConfigurationService,
                                            final LibraryAccesssHelper libraryAccesssHelper) {
        this.exportFtpConfigurationService = exportFtpConfigurationService;
        this.uiExportFTPConfigurationService = uiExportFTPConfigurationService;
        this.libraryAccesssHelper = libraryAccesssHelper;
    }

    @RequestMapping(method = RequestMethod.POST)
    @Timed
    @RolesAllowed({EXP_FTP_HAB1})
    public ResponseEntity<ExportFTPConfigurationDTO> create(final HttpServletRequest request,
                                                      @RequestBody final ExportFTPConfigurationDTO confDto) throws
                                                                                                               PgcnException,
                                                                                                               PgcnTechnicalException {
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur, pour la conf à importer
        if (confDto.getLibrary() == null || !libraryAccesssHelper.checkLibrary(request, confDto.getLibrary().getIdentifier())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Création
        final ExportFTPConfigurationDTO savedConfiguration = uiExportFTPConfigurationService.create(confDto);
        return new ResponseEntity<>(savedConfiguration, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Timed
    @RolesAllowed({EXP_FTP_HAB2})
    public ResponseEntity<ExportFTPConfigurationDTO> delete(final HttpServletRequest request, @PathVariable final String id) {
        // Chargement
        final ExportFTPConfiguration conf = exportFtpConfigurationService.getOne(id);
        // Non trouvé
        if (conf == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur
        if (!libraryAccesssHelper.checkLibrary(request, conf, ExportFTPConfiguration::getLibrary)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Suppression
        exportFtpConfigurationService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"search"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({EXP_FTP_HAB0})
    public ResponseEntity<Page<SimpleExportFTPConfDTO>> search(final HttpServletRequest request,
                                                                  @RequestParam(value = "search", required = false) final String search,
                                                                  @RequestParam(value = "libraries", required = false) final List<String> libraries,
                                                                  @RequestParam(value = "page", required = false, defaultValue = "0")
                                                                  final Integer page,
                                                                  @RequestParam(value = "size", required = false, defaultValue = "10")
                                                                  final Integer size) {
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);
        return new ResponseEntity<>(uiExportFTPConfigurationService.search(search, filteredLibraries, page, size), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({EXP_FTP_HAB0})
    public ResponseEntity<ExportFTPConfigurationDTO> getById(final HttpServletRequest request, @PathVariable final String id) {
        // Chargement
        final ExportFTPConfigurationDTO conf = uiExportFTPConfigurationService.getOne(id);
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
    @RolesAllowed({EXP_FTP_HAB0})
    public ResponseEntity<List<SimpleExportFTPConfDTO>> getByProjectId(final HttpServletRequest request,
                                                                          @RequestParam(value = "project") final String project) {
        final List<String> libraries = libraryAccesssHelper.getLibraryFilter(request, null);
        //final List<SimpleFTPConfigurationDTO> configuration = 
        final List<SimpleExportFTPConfDTO> configuration = uiExportFTPConfigurationService.getAllByProjectId(project, libraries);
        return createResponseEntity(configuration);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    @Timed
    @RolesAllowed({EXP_FTP_HAB1})
    public ResponseEntity<ExportFTPConfigurationDTO> update(final HttpServletRequest request, 
                                                            @RequestBody final ExportFTPConfigurationDTO configuration) throws PgcnTechnicalException {

        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur, pour la conf à importer
        if (configuration.getLibrary() == null || !libraryAccesssHelper.checkLibrary(request, configuration.getLibrary().getIdentifier())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Chargement conf existante
        final ExportFTPConfiguration dbConfiguration = exportFtpConfigurationService.getOne(configuration.getIdentifier());
        // Non trouvé
        if (dbConfiguration == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur, pour la conf existant
        if (!libraryAccesssHelper.checkLibrary(request, dbConfiguration, ExportFTPConfiguration::getLibrary)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Mise à jour
        final ExportFTPConfigurationDTO savedConfiguration = uiExportFTPConfigurationService.update(configuration);
        return new ResponseEntity<>(savedConfiguration, HttpStatus.OK);
    }
    
}
