package fr.progilone.pgcn.web.rest.exchange;

import static fr.progilone.pgcn.web.rest.document.security.AuthorizationConstants.*;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.administration.MailboxConfiguration;
import fr.progilone.pgcn.domain.administration.SftpConfiguration;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.dto.document.BibliographicRecordDcDTO;
import fr.progilone.pgcn.domain.exchange.cines.CinesReport;
import fr.progilone.pgcn.domain.util.CustomUserDetails;
import fr.progilone.pgcn.exception.PgcnException;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.security.SecurityUtils;
import fr.progilone.pgcn.service.administration.MailboxConfigurationService;
import fr.progilone.pgcn.service.exchange.cines.CinesRequestHandlerService;
import fr.progilone.pgcn.service.exchange.cines.ExportCinesService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import fr.progilone.pgcn.web.rest.administration.security.AuthorizationConstants;
import fr.progilone.pgcn.web.util.AccessHelper;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
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

/**
 * Contrôleur gérant l'export d'unités documentaires
 * <p>
 * Created by Sébastien on 23/12/2016.
 */
@RestController
@RequestMapping(value = "/api/rest/export/cines")
public class ExportCinesController extends AbstractRestController {

    private final CinesRequestHandlerService cinesRequestHandlerService;
    private final ExportCinesService exportCinesService;
    private final MailboxConfigurationService mailboxConfigurationService;
    private final LibraryAccesssHelper libraryAccesssHelper;
    private final AccessHelper accessHelper;

    @Autowired
    public ExportCinesController(final CinesRequestHandlerService cinesRequestHandlerService,
                                 final ExportCinesService exportCinesService,
                                 final MailboxConfigurationService mailboxConfigurationService,
                                 final LibraryAccesssHelper libraryAccesssHelper,
                                 final AccessHelper accessHelper) {
        this.cinesRequestHandlerService = cinesRequestHandlerService;
        this.exportCinesService = exportCinesService;
        this.mailboxConfigurationService = mailboxConfigurationService;
        this.libraryAccesssHelper = libraryAccesssHelper;
        this.accessHelper = accessHelper;
    }

    /**
     * Récupération des données enregistrées lors du précédent export
     */
    @RequestMapping(value = "/{identifier}", method = RequestMethod.GET, params = {"export"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(DOC_UNIT_HAB0)
    public ResponseEntity<BibliographicRecordDcDTO> getDcById(@PathVariable final String identifier) {
        if (!accessHelper.checkBibliographicRecord(identifier)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return createResponseEntity(exportCinesService.getExportData(identifier));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST, params = {"save"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({DOC_UNIT_HAB4})
    public ResponseEntity<?> saveExportData(@PathVariable("id") final String identifier, @RequestBody final BibliographicRecordDcDTO metaDc) {
        if (!accessHelper.checkDocUnit(identifier)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        exportCinesService.save(identifier, metaDc);
        return new ResponseEntity<>(HttpStatus.OK);

    }

    /**
     * Génération du répertoire d'export et upload sur le serveur CINES (avec les métadonnées Dublin Core).
     */
    @RequestMapping(method = RequestMethod.POST,
                    params = {"send",
                              "dc"},
                    produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({DOC_UNIT_HAB4})
    public ResponseEntity<CinesReport> exportDocUnitToCinesWithDc(final HttpServletRequest request,
                                                                  @RequestParam(value = "docUnit") final String docUnitId,
                                                                  @RequestParam(value = "conf", required = false) final SftpConfiguration conf,
                                                                  @RequestParam(value = "reversion", required = false, defaultValue = "false") final boolean reversion,
                                                                  @RequestBody final BibliographicRecordDcDTO metaDc) {
        return exportCinesService.exportDocUnitToCines(request, docUnitId, metaDc, reversion, false, conf);
    }

    /**
     * Génération du répertoire d'export et upload sur le serveur CINES (avec les métadonnées EAD).
     */
    @RequestMapping(method = RequestMethod.POST,
                    params = {"send",
                              "ead"},
                    produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({DOC_UNIT_HAB4})
    public ResponseEntity<CinesReport> exportDocUnitToCinesWithEad(final HttpServletRequest request,
                                                                   @RequestParam(value = "docUnit") final String docUnitId,
                                                                   @RequestParam(value = "conf", required = false) final SftpConfiguration conf,
                                                                   @RequestParam(value = "reversion", required = false, defaultValue = "false") final boolean reversion) {
        return exportCinesService.exportDocUnitToCines(request, docUnitId, null, reversion, true, conf);
    }

    /**
     * Vérification des boites mail, à la recherche d'accusés réception, de notifications de rejet ou de certificats d'archivage
     */
    @RequestMapping(method = RequestMethod.GET, params = {"check_mailbox"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({DOC_UNIT_HAB4})
    public ResponseEntity<?> checkMailbox(final HttpServletRequest request, @RequestParam(value = "conf", required = false) MailboxConfiguration conf)
                                                                                                                                                       throws PgcnTechnicalException {
        Set<MailboxConfiguration> confs = null;

        // Vérification de la bibliothèque de l'utilisateur sur la configuration mail
        if (conf != null) {
            conf = mailboxConfigurationService.findOne(conf.getIdentifier());
            if (!libraryAccesssHelper.checkLibrary(request, conf, MailboxConfiguration::getLibrary)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            confs = Collections.singleton(conf);
        }
        // Vérification des boites mail auxquelles a accès l'utilisateur
        else {
            final CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
            // L'utilisateur est super admin => on vérifie toutes les boites mails
            if (request.isUserInRole(AuthorizationConstants.SUPER_ADMIN)) {
                confs = mailboxConfigurationService.findAll(true);
            }
            // Sinon on ne vérifie que les boites mails liées à la bibliothèque de l'utilisateur
            else if (currentUser != null && currentUser.getLibraryId() != null) {
                confs = mailboxConfigurationService.findByLibrary(currentUser.getLibraryId(), true);
            }
            // pas de conf chargées => accès non autorisé
            if (confs == null) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            // pas de conf trouvées
            if (confs.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
        // Vérification des boites mails
        cinesRequestHandlerService.updateExportedDocUnits(confs);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_XML_VALUE,
                    params = {"aip",
                              "docUnit"})
    @RolesAllowed({DOC_UNIT_HAB4})
    public ResponseEntity<?> getAip(final HttpServletRequest request, final HttpServletResponse response, @RequestParam(value = "docUnit") final String docUnitId)
                                                                                                                                                                   throws PgcnTechnicalException {

        final File aip = cinesRequestHandlerService.retrieveAip(docUnitId);
        // Non trouvé
        if (aip == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            writeResponseForDownload(response, aip, MediaType.APPLICATION_XML_VALUE, "aip.xml");
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @RequestMapping(method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_XML_VALUE,
                    params = {"sip",
                              "docUnit"})
    @RolesAllowed({DOC_UNIT_HAB4})
    public ResponseEntity<?> getSip(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    @RequestParam(value = "docUnit") final String docUnitId,
                                    @RequestParam(value = "cinesStatus") final String status) throws PgcnTechnicalException {

        final File sip = cinesRequestHandlerService.retrieveSip(docUnitId, StringUtils.equals(status, "FAILED"));

        // Non trouvé
        if (sip == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            writeResponseForDownload(response, sip, MediaType.APPLICATION_XML_VALUE, "sip.xml");
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @RequestMapping(method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_XML_VALUE,
                    params = {"mets",
                              "docUnit"})
    @RolesAllowed({DOC_UNIT_HAB4})
    public ResponseEntity<?> getMets(final HttpServletRequest request,
                                     final HttpServletResponse response,
                                     @RequestParam(value = "docUnit") final String docUnitId,
                                     @RequestParam(value = "cinesStatus") final String status) throws PgcnTechnicalException {

        final File mets = cinesRequestHandlerService.retrieveMets(docUnitId, StringUtils.equals(status, "FAILED"));
        // Non trouvé
        if (mets == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            writeResponseForDownload(response, mets, MediaType.APPLICATION_XML_VALUE, "mets.xml");
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    /**
     * Déclenche l'export CINES de la liste de documents spécifiés.
     */
    @RolesAllowed({DOC_UNIT_HAB4})
    @RequestMapping(method = RequestMethod.GET, params = {"mass_export"})
    @Timed
    public ResponseEntity<?> massExport(final HttpServletRequest request, @RequestParam(name = "docs") final List<String> docs) throws PgcnTechnicalException {
        // droits d'accès à l'ud
        final Collection<DocUnit> filteredDocUnits = accessHelper.filterDocUnits(docs);
        if (filteredDocUnits.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        for (final DocUnit docUnit : filteredDocUnits) {
            exportCinesService.exportDocUnitToCines(request, docUnit.getIdentifier());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Reprise d'historique des controles.
     */
    @RequestMapping(value = "/regenerateMets", method = RequestMethod.GET)
    @Timed
    @RolesAllowed(AuthorizationConstants.SUPER_ADMIN)
    public ResponseEntity<?> regenerateMets(final HttpServletRequest request, @RequestParam(name = "library") final String libraryId) throws PgcnException {

        exportCinesService.regenerateMetsforArchivedDocUnits(libraryId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
