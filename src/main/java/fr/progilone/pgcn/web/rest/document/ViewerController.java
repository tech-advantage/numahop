package fr.progilone.pgcn.web.rest.document;

import static fr.progilone.pgcn.web.rest.checkconfiguration.security.AuthorizationConstants.*;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.service.document.DigitalDocumentService;
import fr.progilone.pgcn.service.document.ViewerService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Viewer controller.
 *
 * @author Emmanuel RIZET
 */

@RestController
@RequestMapping(value = "/api/rest/viewer/document")
public class ViewerController extends AbstractRestController {

    private static final Logger LOG = LoggerFactory.getLogger(ViewerController.class);

    @Autowired
    private DigitalDocumentService digitalDocumentService;

    @Autowired
    private ViewerService viewerService;

    private static final int VIEW_WIDTH = 982;
    private static final int VIEW_HEIGHT = 1417;
    private static final int PRINT_WIDTH = 982;
    private static final int PRINT_HEIGHT = 1417;

    /*
     * Precos: Modèle d'URI
     * Collection {scheme}://{host}/{prefix}/collection/{name}
     * Manifest {scheme}://{host}/{prefix}/{identifier}/manifest
     * Sequence {scheme}://{host}/{prefix}/{identifier}/sequence/{name}
     * Canvas {scheme}://{host}/{prefix}/{identifier}/canvas/{name}
     * Annotation {scheme}://{host}/{prefix}/{identifier}/annotation/{name}
     * AnnotationList {scheme}://{host}/{prefix}/{identifier}/list/{name}
     * Range {scheme}://{host}/{prefix}/{identifier}/range/{name}
     * Layer {scheme}://{host}/{prefix}/{identifier}/layer/{name}
     * Content {scheme}://{host}/{prefix}/{identifier}/res/{name}.{format}
     */

    @RequestMapping(value = "/{identifier}/manifest", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(CHECK_HAB3)
    public ResponseEntity<?> getManifestViewer(final HttpServletRequest request, final HttpServletResponse response, @PathVariable final String identifier)
                                                                                                                                                            throws PgcnTechnicalException {

        final String manifest = viewerService.buildManifestViewer(identifier);
        return new ResponseEntity<>(manifest, HttpStatus.OK);
    }

    @RequestMapping(value = "/sample/{identifier}/manifest", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(CHECK_HAB3)
    public ResponseEntity<?> getSampledManifestViewer(final HttpServletRequest request, final HttpServletResponse response, @PathVariable final String identifier)
                                                                                                                                                                   throws PgcnTechnicalException {
        final String manifest = viewerService.buildSampledManifestViewer(identifier);
        return new ResponseEntity<>(manifest, HttpStatus.OK);
    }

    /**
     * Service de la resource de l'image => infos fichier.
     *
     * @param request
     * @param identifier
     * @return
     */
    @RequestMapping(value = "/{identifier}/{pageNumber}/info.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(CHECK_HAB3)
    public ResponseEntity<Map<String, Object>> getFileInfos(final HttpServletRequest request,
                                                            final HttpServletResponse response,
                                                            @PathVariable final String identifier,
                                                            @PathVariable final int pageNumber) throws PgcnTechnicalException {

        final Map<String, Object> info = viewerService.buildInfoFileViewer(identifier, pageNumber);
        return new ResponseEntity<>(info, HttpStatus.OK);
    }

    /**
     * Sert les images en format 'VIEW' / 'PRINT' / 'ZOOM'.
     *
     * @param request
     * @param response
     * @param identifier
     * @param pageNumber
     * @param dim1
     * @param dim2
     * @return
     * @throws PgcnTechnicalException
     */
    @RequestMapping(value = "/{identifier}/{pageNumber}/full/{dim1}/{dim2}/default.jpg", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    @Timed
    @RolesAllowed(CHECK_HAB3)
    public ResponseEntity<?> getDefault(final HttpServletRequest request,
                                        final HttpServletResponse response,
                                        @PathVariable final String identifier,
                                        @PathVariable final int pageNumber,
                                        @PathVariable final String dim1,
                                        @PathVariable final String dim2) throws PgcnTechnicalException {

        final String[] dims = dim1.split(",");
        int width = 0;
        int height = 0;
        if (StringUtils.isNotBlank(dims[0]) && StringUtils.isNumeric(dims[0])) {
            width = Integer.valueOf(dims[0]);
        }
        if (dims.length > 1 && StringUtils.isNotBlank(dims[1])
            && StringUtils.isNumeric(dims[1])) {
            height = Integer.valueOf(dims[1]);
        }
        File f = null;
        if (width > PRINT_WIDTH * 2 || height > PRINT_HEIGHT * 2) {
            f = digitalDocumentService.getZoom(identifier, pageNumber);
        } else if (width > VIEW_WIDTH || height > VIEW_HEIGHT) {
            f = digitalDocumentService.getPrint(identifier, pageNumber);
        } else {
            f = digitalDocumentService.getView(identifier, pageNumber);
        }
        // Au cas ou il ne resterait que les vignettes
        if (f == null) {
            f = digitalDocumentService.getThumbnail(identifier, pageNumber);
        }

        writeResponseForDownload(response, f, MediaType.IMAGE_JPEG_VALUE, f.getName());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Sert les images en format 'ZOOM'/'XTRA_ZOOM'.
     *
     * @param request
     * @param response
     * @param identifier
     * @param pageNumber
     * @param args1
     * @param args2
     * @param test
     * @return
     * @throws PgcnTechnicalException
     */
    @RequestMapping(value = "/{identifier}/{pageNumber}/{args1}/{args2}/{test}/default.jpg", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    @Timed
    @RolesAllowed(CHECK_HAB3)
    public ResponseEntity<?> getZooms(final HttpServletRequest request,
                                      final HttpServletResponse response,
                                      @PathVariable final String identifier,
                                      @PathVariable final int pageNumber,
                                      @PathVariable final String args1,
                                      @PathVariable final String args2) throws PgcnTechnicalException {

        final File f = digitalDocumentService.getZoomOrXtra(identifier, pageNumber, args2);
        writeResponseForDownload(response, f, MediaType.IMAGE_JPEG_VALUE, f.getName());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Sert les vignettes en format "thumb".
     *
     * @param request
     * @param response
     * @param identifier
     * @param pageNumber
     * @return
     * @throws PgcnTechnicalException
     */
    @RequestMapping(value = "/{identifier}/thumbnail/{pageNumber}/thumb.jpg", method = RequestMethod.GET)
    @Timed
    @RolesAllowed(CHECK_HAB3)
    public ResponseEntity<?> getThumbnail(final HttpServletRequest request,
                                          final HttpServletResponse response,
                                          @PathVariable final String identifier,
                                          @PathVariable final int pageNumber) throws PgcnTechnicalException {
        final File f = digitalDocumentService.getThumbnail(identifier, pageNumber);
        writeResponseForDownload(response, f, MediaType.IMAGE_JPEG_VALUE, f.getName());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Chargement du fichier "Master".
     *
     * @param request
     * @param response
     * @param identifier
     * @param pageNumber
     * @return
     * @throws PgcnTechnicalException
     */
    @RequestMapping(value = "/{identifier}/master/{pageNumber}", method = RequestMethod.GET)
    @Timed
    @RolesAllowed(CHECK_HAB3)
    public ResponseEntity<?> getMaster(final HttpServletRequest request,
                                       final HttpServletResponse response,
                                       @PathVariable final String identifier,
                                       @PathVariable final int pageNumber) throws PgcnTechnicalException {

        final File f = digitalDocumentService.getMaster(identifier, pageNumber);
        if (f != null) {
            try {
                writeResponseForDownload(response, f, new Tika().detect(f), f.getName());
            } catch (final IOException e) {
                throw new PgcnTechnicalException(e);
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Chargement du fichier "Master PDF".
     *
     * @param request
     * @param response
     * @param identifier
     * @return
     * @throws PgcnTechnicalException
     */
    @RequestMapping(value = "/{identifier}/master/", method = RequestMethod.GET)
    @Timed
    @RolesAllowed(CHECK_HAB3)
    public ResponseEntity<?> getPdfMaster(final HttpServletRequest request, final HttpServletResponse response, @PathVariable final String identifier)
                                                                                                                                                       throws PgcnTechnicalException {
        final File f = digitalDocumentService.getPdfMaster(identifier);
        if (f != null) {
            try {
                writeResponseForDownload(response, f, new Tika().detect(f), f.getName());
            } catch (final IOException e) {
                throw new PgcnTechnicalException(e);
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Chargement de la table de matières.
     *
     * @param request
     * @param response
     * @param identifier
     * @return
     * @throws PgcnTechnicalException
     */
    @RequestMapping(value = "/{identifier}/toc", method = RequestMethod.GET)
    @Timed
    @RolesAllowed(CHECK_HAB3)
    public ResponseEntity<?> getTableOfContent(final HttpServletRequest request, final HttpServletResponse response, @PathVariable final String identifier)
                                                                                                                                                            throws PgcnTechnicalException {

        final File f = viewerService.getTableOfContent(identifier);
        if (f != null) {
            try {
                writeResponseForDownload(response, f, new Tika().detect(f), f.getName());
            } catch (final IOException e) {
                throw new PgcnTechnicalException(e);
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
