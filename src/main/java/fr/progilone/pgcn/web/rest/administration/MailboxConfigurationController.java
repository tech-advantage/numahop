package fr.progilone.pgcn.web.rest.administration;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.administration.MailboxConfiguration;
import fr.progilone.pgcn.domain.dto.administration.MailboxConfigurationDTO;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.service.administration.MailboxConfigurationService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import fr.progilone.pgcn.web.util.AccessHelper;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;
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

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;

import static fr.progilone.pgcn.web.rest.administration.security.AuthorizationConstants.*;

/**
 * Created by Sébastien on 30/12/2016.
 */
@RestController
@RequestMapping(value = "/api/rest/conf_mail")
public class MailboxConfigurationController extends AbstractRestController {

    private final MailboxConfigurationService mailboxConfigurationService;
    private final AccessHelper accessHelper;
    private final LibraryAccesssHelper libraryAccesssHelper;

    @Autowired
    public MailboxConfigurationController(final MailboxConfigurationService mailboxConfigurationService,
                                          final AccessHelper accessHelper,
                                          final LibraryAccesssHelper libraryAccesssHelper) {
        this.mailboxConfigurationService = mailboxConfigurationService;
        this.accessHelper = accessHelper;
        this.libraryAccesssHelper = libraryAccesssHelper;
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({MAIL_HAB1})
    public ResponseEntity<MailboxConfiguration> create(final HttpServletRequest request, @RequestBody final MailboxConfiguration conf) throws
                                                                                                                                       PgcnTechnicalException {
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur, pour le conf à importer
        if (!libraryAccesssHelper.checkLibrary(request, conf, MailboxConfiguration::getLibrary)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Création
        return new ResponseEntity<>(mailboxConfigurationService.save(conf), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Timed
    @RolesAllowed({MAIL_HAB2})
    public ResponseEntity<?> delete(final HttpServletRequest request, @PathVariable final String id) {
        // Chargement
        final MailboxConfiguration conf = mailboxConfigurationService.findOne(id);
        // Non trouvé
        if (conf == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur
        if (!libraryAccesssHelper.checkLibrary(request, conf, MailboxConfiguration::getLibrary)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Suppression
        mailboxConfigurationService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({MAIL_HAB0})
    public ResponseEntity<Collection<MailboxConfigurationDTO>> search(final HttpServletRequest request,
                                                                      @RequestParam(value = "search", required = false) String search,
                                                                      @RequestParam(value = "library", required = false) List<String> libraries,
                                                                      @RequestParam(value = "active", required = false, defaultValue = "true")
                                                                          boolean active) {

        Collection<MailboxConfigurationDTO> mailboxes = mailboxConfigurationService.search(search, libraries, active);
        // Filtrage des mailbox par rapport à la bibliothèque de l'utilisateur, pour les non-admin
        mailboxes = libraryAccesssHelper.filterObjectsByLibrary(request, mailboxes, dto -> dto.getLibrary().getIdentifier());
        // Réponse
        return new ResponseEntity<>(mailboxes, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({MAIL_HAB0})
    public ResponseEntity<MailboxConfiguration> getById(final HttpServletRequest request, @PathVariable final String id) {
        // Chargement
        final MailboxConfiguration conf = mailboxConfigurationService.findOne(id);
        // Non trouvé
        if (conf == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur
        if (!libraryAccesssHelper.checkLibrary(request, conf, MailboxConfiguration::getLibrary)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Réponse
        return new ResponseEntity<>(conf, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({MAIL_HAB1})
    public ResponseEntity<MailboxConfiguration> udpate(final HttpServletRequest request, @RequestBody final MailboxConfiguration conf) throws
                                                                                                                                       PgcnTechnicalException {

        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur, pour le conf à importer
        if (!libraryAccesssHelper.checkLibrary(request, conf, MailboxConfiguration::getLibrary)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Chargement du conf existant
        final MailboxConfiguration dbConfigurationMail = mailboxConfigurationService.findOne(conf.getIdentifier());
        // Non trouvé
        if (dbConfigurationMail == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur, pour le conf existant
        if (!libraryAccesssHelper.checkLibrary(request, dbConfigurationMail, MailboxConfiguration::getLibrary)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Mise à jour
        return new ResponseEntity<>(mailboxConfigurationService.save(conf), HttpStatus.OK);
    }
}
