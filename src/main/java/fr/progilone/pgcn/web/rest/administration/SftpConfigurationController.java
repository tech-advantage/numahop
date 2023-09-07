package fr.progilone.pgcn.web.rest.administration;

import static fr.progilone.pgcn.web.rest.administration.security.AuthorizationConstants.SFTP_HAB0;
import static fr.progilone.pgcn.web.rest.administration.security.AuthorizationConstants.SFTP_HAB1;
import static fr.progilone.pgcn.web.rest.administration.security.AuthorizationConstants.SFTP_HAB2;
import static fr.progilone.pgcn.web.rest.document.security.AuthorizationConstants.DOC_UNIT_HAB0;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.administration.CinesPAC;
import fr.progilone.pgcn.domain.administration.SftpConfiguration;
import fr.progilone.pgcn.domain.dto.administration.SftpConfigurationDTO;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.service.administration.CinesPACService;
import fr.progilone.pgcn.service.administration.SftpConfigurationService;
import fr.progilone.pgcn.service.exchange.ssh.SftpService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import fr.progilone.pgcn.web.util.AccessHelper;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Sébastien on 30/12/2016.
 */
@RestController
@RequestMapping(value = "/api/rest/conf_sftp")
public class SftpConfigurationController extends AbstractRestController {

    private static final Logger LOG = LoggerFactory.getLogger(SftpConfigurationController.class);

    private final SftpConfigurationService sftpConfigurationService;
    private final SftpService sftpService;
    private final LibraryAccesssHelper libraryAccesssHelper;
    private final CinesPACService cinesPACService;
    private final AccessHelper accessHelper;

    @Autowired
    public SftpConfigurationController(final SftpConfigurationService sftpConfigurationService,
                                       final SftpService sftpService,
                                       final LibraryAccesssHelper libraryAccesssHelper,
                                       final CinesPACService cinesPACService,
                                       final AccessHelper accessHelper) {
        this.sftpConfigurationService = sftpConfigurationService;
        this.sftpService = sftpService;
        this.libraryAccesssHelper = libraryAccesssHelper;
        this.cinesPACService = cinesPACService;
        this.accessHelper = accessHelper;
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({SFTP_HAB1})
    public ResponseEntity<SftpConfiguration> create(final HttpServletRequest request, @RequestBody final SftpConfiguration conf) throws PgcnTechnicalException {
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur, pour la conf à importer
        if (!libraryAccesssHelper.checkLibrary(request, conf, SftpConfiguration::getLibrary)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Création
        return new ResponseEntity<>(sftpConfigurationService.save(conf), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Timed
    @RolesAllowed({SFTP_HAB2})
    public ResponseEntity<?> delete(final HttpServletRequest request, @PathVariable final String id) {
        // Chargement
        final SftpConfiguration conf = sftpConfigurationService.findOne(id);
        // Non trouvé
        if (conf == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur
        if (!libraryAccesssHelper.checkLibrary(request, conf, SftpConfiguration::getLibrary)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Suppression
        sftpConfigurationService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET,
                    params = {"pacs",
                              "library"},
                    produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({SFTP_HAB0,
                   DOC_UNIT_HAB0})
    public ResponseEntity<Collection<CinesPAC>> findPACS(final HttpServletRequest request,
                                                         @RequestParam(name = "library", required = false) String libraryId,
                                                         @RequestParam(name = "project", required = false) final String projectId) {
        // L'usager est autorisé à accéder aux infos de la bibliothèque ou les infos du projet
        if ((StringUtils.isNotBlank(libraryId) && !libraryAccesssHelper.checkLibrary(request, libraryId)) && (StringUtils.isNotBlank(projectId) && !accessHelper.checkProject(
                                                                                                                                                                              projectId))) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Chargement des configurationSftp
        final Collection<CinesPAC> pacs = cinesPACService.findAllForLibrary(libraryId);
        // Réponse
        return new ResponseEntity<>(pacs, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET,
                    params = {"pacs",
                              "configuration"},
                    produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({SFTP_HAB0,
                   DOC_UNIT_HAB0})
    public ResponseEntity<Collection<CinesPAC>> findConfigurationPACS(final HttpServletRequest request,
                                                                      @RequestParam(name = "configuration", required = false) String configurationId) {
        // Chargement des configurationSftp
        final Collection<CinesPAC> pacs = cinesPACService.findAllForConfiguration(configurationId);
        // Réponse
        return new ResponseEntity<>(pacs, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    // @RolesAllowed({SFTP_HAB0})
    public ResponseEntity<Collection<SftpConfigurationDTO>> findAll(final HttpServletRequest request, @RequestParam(name = "active", required = false) Boolean active) {
        // Chargement des configurationSftp
        Collection<SftpConfigurationDTO> confs = sftpConfigurationService.findAllDto(active);
        // Filtrage des configurationSftp par rapport à la bibliothèque de l'utilisateur, pour les non-admin
        confs = libraryAccesssHelper.filterObjectsByLibrary(request, confs, dto -> dto.getLibrary().getIdentifier());
        // Réponse
        return new ResponseEntity<>(confs, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"library"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({SFTP_HAB0})
    public ResponseEntity<Set<SftpConfigurationDTO>> findByLibrary(final HttpServletRequest request,
                                                                   @RequestParam(value = "library") Library library,
                                                                   @RequestParam(name = "active", required = false) Boolean active) {
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur
        if (!libraryAccesssHelper.checkLibrary(request, library)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Réponse
        return new ResponseEntity<>(sftpConfigurationService.findDtoByLibrary(library, active), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"search"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({SFTP_HAB0})
    public ResponseEntity<Page<SftpConfigurationDTO>> search(final HttpServletRequest request,
                                                             @RequestParam(value = "search", required = false) final String search,
                                                             @RequestParam(value = "libraries", required = false) final List<String> libraries,
                                                             @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                                             @RequestParam(value = "size", required = false, defaultValue = "10") final Integer size) {
        // Recherche suivant les droits de l'utilisateur
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);
        // Recherche
        final Page<SftpConfigurationDTO> results = sftpConfigurationService.search(search, filteredLibraries, page, size);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({SFTP_HAB0})
    public ResponseEntity<SftpConfiguration> getById(final HttpServletRequest request, @PathVariable final String id) {
        // Chargement
        final SftpConfiguration conf = sftpConfigurationService.findOne(id);
        // Non trouvé
        if (conf == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur
        if (!libraryAccesssHelper.checkLibrary(request, conf, SftpConfiguration::getLibrary)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Réponse
        return new ResponseEntity<>(conf, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", params = {"init"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({SFTP_HAB0})
    public ResponseEntity<Map<?, ?>> initConf(final HttpServletRequest request, @PathVariable final String id) {
        // Chargement
        final SftpConfiguration conf = sftpConfigurationService.findOne(id);
        // Non trouvé
        if (conf == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur
        if (!libraryAccesssHelper.checkLibrary(request, conf, SftpConfiguration::getLibrary)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final Optional<String> error = sftpService.initConnection(conf);

        return error.<ResponseEntity<Map<?, ?>>> map(s -> new ResponseEntity<>(ArrayUtils.toMap(new String[][] {{"message",
                                                                                                                 s}}), HttpStatus.EXPECTATION_FAILED))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.OK));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({SFTP_HAB1})
    public ResponseEntity<SftpConfiguration> udpate(final HttpServletRequest request, @RequestBody final SftpConfiguration conf) throws PgcnTechnicalException {

        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur, pour la conf à importer
        if (!libraryAccesssHelper.checkLibrary(request, conf, SftpConfiguration::getLibrary)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Chargement du conf existant
        final SftpConfiguration dbConfigurationSFTP = sftpConfigurationService.findOne(conf.getIdentifier());
        // Non trouvé
        if (dbConfigurationSFTP == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur, pour la conf existant
        if (!libraryAccesssHelper.checkLibrary(request, dbConfigurationSFTP, SftpConfiguration::getLibrary)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Mise à jour
        return new ResponseEntity<>(sftpConfigurationService.save(conf), HttpStatus.OK);
    }

    /**
     * Chargement des PACs depuis le fichier dpdi.
     *
     * @param request
     * @param idSftpConf
     * @param file
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.POST, params = {"upload"}, headers = "content-type=multipart/*", produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({SFTP_HAB1})
    public ResponseEntity<SftpConfiguration> uploadPacs(final HttpServletRequest request,
                                                        @PathVariable("id") final String idSftpConf,
                                                        @RequestParam(name = "file") final List<MultipartFile> files) {

        // Chargement conf existante.
        final SftpConfiguration configSFTP = sftpConfigurationService.findOne(idSftpConf);
        // droits d'accès à la conf
        if (!libraryAccesssHelper.checkLibrary(request, configSFTP.getLibrary())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        if (files.isEmpty()) {
            LOG.warn("Le fichier ne contient aucune donnée valide pour la mise à jour de la conf sftp {}", idSftpConf);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            sftpConfigurationService.getPpdiPacs(configSFTP, files.get(0));
            return new ResponseEntity<>(configSFTP, HttpStatus.OK);
        }
    }
}
