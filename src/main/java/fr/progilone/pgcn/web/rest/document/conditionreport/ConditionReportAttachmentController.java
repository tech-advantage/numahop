package fr.progilone.pgcn.web.rest.document.conditionreport;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReportAttachment;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.service.document.conditionreport.ConditionReportAttachmentService;
import fr.progilone.pgcn.service.document.conditionreport.ConditionReportService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import fr.progilone.pgcn.web.util.AccessHelper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static fr.progilone.pgcn.web.rest.document.security.AuthorizationConstants.*;

@RestController
@RequestMapping(value = "/api/rest/condreport_attachment")
public class ConditionReportAttachmentController extends AbstractRestController {

    private static final Logger LOG = LoggerFactory.getLogger(ConditionReportAttachmentController.class);
    private static final String DOWNLOAD_THUMBNAIL_PNG = "thumbnail.png";
    private static final String DEFAULT_THUMBNAIL = "images/file.png";

    private final AccessHelper accessHelper;
    private final ConditionReportService conditionReportService;
    private final ConditionReportAttachmentService conditionReportAttachmentService;

    @Autowired
    public ConditionReportAttachmentController(final AccessHelper accessHelper,
                                               final ConditionReportService conditionReportService,
                                               final ConditionReportAttachmentService conditionReportAttachmentService) {
        this.accessHelper = accessHelper;
        this.conditionReportService = conditionReportService;
        this.conditionReportAttachmentService = conditionReportAttachmentService;
    }

    /**
     * Recherche des pièces jointes d'un rapport
     *
     * @param reportId
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, params = {"report"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(COND_REPORT_HAB0)
    public ResponseEntity<List<ConditionReportAttachment>> findByReport(@RequestParam(name = "report") final String reportId) {
        final DocUnit docUnit = conditionReportService.findDocUnitByIdentifier(reportId);
        // droits d'accès à l'ud
        if (!accessHelper.checkDocUnit(docUnit.getIdentifier())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final List<ConditionReportAttachment> attachments = conditionReportAttachmentService.findByReport(reportId);
        return new ResponseEntity<>(attachments, HttpStatus.OK);
    }

    /**
     * Suppression d'une pièce jointe
     *
     * @param identifier
     */
    @RequestMapping(value = "/{identifier}", method = RequestMethod.DELETE)
    @Timed
    @RolesAllowed(COND_REPORT_HAB2)
    public void delete(final HttpServletResponse response, @PathVariable final String identifier) {
        // droits d'accès à l'ud
        final DocUnit docUnit = conditionReportAttachmentService.findDocUnitByIdentifier(identifier);
        if (!accessHelper.checkDocUnit(docUnit.getIdentifier())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        conditionReportAttachmentService.delete(identifier);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Téléchargement d'une pièce jointe
     *
     * @param response
     * @param attachmentId
     * @return
     * @throws PgcnTechnicalException
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, params = {"file"}, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Timed
    @RolesAllowed(COND_REPORT_HAB0)
    public void downloadAttachment(final HttpServletResponse response, @PathVariable("id") final String attachmentId) throws PgcnTechnicalException {
        // droits d'accès à l'ud
        final DocUnit docUnit = conditionReportAttachmentService.findDocUnitByIdentifier(attachmentId);
        if (!accessHelper.checkDocUnit(docUnit.getIdentifier())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        final ConditionReportAttachment attachment = conditionReportAttachmentService.findByIdentifier(attachmentId);
        // Non trouvé
        if (attachment == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        // Réponse
        final File importFile = conditionReportAttachmentService.downloadAttachmentFile(attachment);
        // Non trouvé
        if (importFile == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        } else {
            writeResponseForDownload(response, importFile, MediaType.APPLICATION_OCTET_STREAM_VALUE, attachment.getOriginalFilename());
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Téléchargement d'une pièce jointe
     *
     * @param response
     * @param attachmentId
     * @return
     * @throws PgcnTechnicalException
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, params = {"thumbnail"}, produces = MediaType.IMAGE_PNG_VALUE)
    @Timed
    @RolesAllowed(COND_REPORT_HAB0)
    public void downloadThumbnail(final HttpServletResponse response, @PathVariable("id") final String attachmentId) throws PgcnTechnicalException {
        // droits d'accès à l'ud
        final DocUnit docUnit = conditionReportAttachmentService.findDocUnitByIdentifier(attachmentId);
        if (!accessHelper.checkDocUnit(docUnit.getIdentifier())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        final ConditionReportAttachment attachment = conditionReportAttachmentService.findByIdentifier(attachmentId);
        // Non trouvé
        if (attachment == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        // Réponse
        final File importFile = conditionReportAttachmentService.downloadAttachmentThumbnail(attachment);
        // Non trouvé
        if (importFile == null) {
            writeResponseHeaderForDownload(response, MediaType.IMAGE_PNG_VALUE, null, DOWNLOAD_THUMBNAIL_PNG);
            try (final InputStream defaultStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(DEFAULT_THUMBNAIL)) {
                IOUtils.copy(defaultStream, response.getOutputStream());
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        } else {
            final String filename = attachment.getOriginalFilename();
            final String mediaType;
            if (StringUtils.endsWithIgnoreCase(filename, ".jpg") || StringUtils.endsWithIgnoreCase(filename, ".jpeg")) {
                mediaType = MediaType.IMAGE_JPEG_VALUE;
            } else if (StringUtils.endsWithIgnoreCase(filename, ".gif")) {
                mediaType = MediaType.IMAGE_GIF_VALUE;
            } else {
                mediaType = MediaType.IMAGE_PNG_VALUE;
            }
            writeResponseForDownload(response, importFile, mediaType, filename);
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Téléversement d'une liste de fichiers
     *
     * @param files
     * @param reportId
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, params = "report", produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(COND_REPORT_HAB2)
    public ResponseEntity<List<ConditionReportAttachment>> uploadAttachments(@RequestParam(name = "file") final List<MultipartFile> files,
                                                                             @RequestParam(name = "report") final String reportId) {
        final DocUnit docUnit = conditionReportService.findDocUnitByIdentifier(reportId);
        // droits d'accès à l'ud
        if (!accessHelper.checkDocUnit(docUnit.getIdentifier())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        if (files.isEmpty()) {
            LOG.warn("Aucun fichier n'a été reçu pour le mise à jour du constat d'état {}", reportId);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            final List<ConditionReportAttachment> attachments = conditionReportAttachmentService.uploadAttachment(files, reportId);
            return new ResponseEntity<>(attachments, HttpStatus.OK);
        }
    }
}
