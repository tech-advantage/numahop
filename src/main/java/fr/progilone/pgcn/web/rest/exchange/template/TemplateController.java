package fr.progilone.pgcn.web.rest.exchange.template;

import static fr.progilone.pgcn.web.rest.exchange.security.AuthorizationConstants.*;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.exchange.template.Template;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.service.exchange.template.TemplateService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/api/rest/template")
@RolesAllowed(TPL_HAB0)
public class TemplateController extends AbstractRestController {

    private static final Logger LOG = LoggerFactory.getLogger(TemplateController.class);

    private final LibraryAccesssHelper libraryAccesssHelper;
    private final TemplateService templateService;

    @Autowired
    public TemplateController(final LibraryAccesssHelper libraryAccesssHelper, final TemplateService templateService) {
        this.libraryAccesssHelper = libraryAccesssHelper;
        this.templateService = templateService;
    }

    /**
     * Création d'un template
     *
     * @param request
     * @param template
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Template> create(final HttpServletRequest request, @RequestBody final Template template) {
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur, pour le template à importer
        if (!libraryAccesssHelper.checkLibrary(request, template, Template::getLibrary)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Création
        return new ResponseEntity<>(templateService.save(template), HttpStatus.CREATED);
    }

    /**
     * Suppression d'une pièce jointe
     *
     * @param identifier
     */
    @RequestMapping(value = "/{identifier}", method = RequestMethod.DELETE)
    @Timed
    public void delete(final HttpServletRequest request, final HttpServletResponse response, @PathVariable final String identifier) {
        final Template template = templateService.findByIdentifier(identifier);
        // non trouvé
        if (template == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        // droits d'accès au template
        if (!libraryAccesssHelper.checkLibrary(request, template.getLibrary())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        templateService.delete(identifier);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Recherche d'un template par moteur et par bibliothèque
     *
     * @param request
     * @param library
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Collection<Template>> findTemplates(final HttpServletRequest request, @RequestParam(name = "library", required = false) final Library library) {
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur
        if (library != null && !libraryAccesssHelper.checkLibrary(request, library)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Recherche
        Collection<Template> templates;
        // Par moteur
        if (library == null) {
            templates = templateService.findTemplate();
            // Filtrage des templates par rapport à la bibliothèque de l'utilisateur, pour les non-admin
            templates = libraryAccesssHelper.filterObjectsByLibrary(request, templates, tpl -> tpl.getLibrary().getIdentifier());
        }
        // Par moteur + bibliothèque
        else {
            templates = templateService.findTemplate(library);
        }
        return new ResponseEntity<>(templates, HttpStatus.OK);
    }

    /**
     * Téléchargement d'un template
     *
     * @param request
     * @param response
     * @param templateId
     * @return
     * @throws PgcnTechnicalException
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, params = {"download"}, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Timed
    public void downloadTemplate(final HttpServletRequest request, final HttpServletResponse response, @PathVariable("id") final String templateId) throws PgcnTechnicalException {

        final Template template = templateService.findByIdentifier(templateId);
        // Non trouvé
        if (template == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        // droits d'accès au template
        if (!libraryAccesssHelper.checkLibrary(request, template.getLibrary())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        // Réponse
        final File importFile = templateService.getTemplateFile(template);
        // Non trouvé
        if (importFile == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        } else {
            writeResponseForDownload(response, importFile, MediaType.APPLICATION_OCTET_STREAM_VALUE, template.getOriginalFilename());
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Téléversement d'une liste de fichiers
     *
     * @param request
     * @param templateId
     * @param files
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.POST, params = {"upload"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Template> uploadAttachments(final HttpServletRequest request,
                                                      @PathVariable("id") final String templateId,
                                                      @RequestParam(name = "file") final List<MultipartFile> files) {
        final Template template = templateService.findByIdentifier(templateId);
        // Non trouvé
        if (template == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // droits d'accès au template
        if (!libraryAccesssHelper.checkLibrary(request, template.getLibrary())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        if (files.isEmpty()) {
            LOG.warn("Aucun fichier n'a été reçu pour le mise à jour du template {}", templateId);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            final Template savedTemplate = templateService.save(template, files.get(0));
            return new ResponseEntity<>(savedTemplate, HttpStatus.OK);
        }
    }

    /**
     * Mise à jour d'un template
     *
     * @param request
     * @param template
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Template> udpate(final HttpServletRequest request, @RequestBody final Template template) {
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur, pour le template à mettre à jour
        if (!libraryAccesssHelper.checkLibrary(request, template, Template::getLibrary)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Chargement du mapping existant
        final Template dbTemplate = templateService.findByIdentifier(template.getIdentifier());
        // Non trouvé
        if (dbTemplate == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur, pour le template existant
        if (!libraryAccesssHelper.checkLibrary(request, dbTemplate, Template::getLibrary)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Mise à jour
        return new ResponseEntity<>(templateService.save(template), HttpStatus.OK);
    }
}
