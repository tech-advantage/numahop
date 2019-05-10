package fr.progilone.pgcn.web.rest.exchange;

import fr.progilone.pgcn.domain.document.DocPropertyType;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.service.document.DocUnitService;
import fr.progilone.pgcn.service.exchange.dc.DocUnitToCSVService;
import fr.progilone.pgcn.service.exchange.dc.DocUnitToJenaService;
import fr.progilone.pgcn.service.exchange.ead.ExportEadService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import fr.progilone.pgcn.web.util.AccessHelper;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static fr.progilone.pgcn.web.rest.document.security.AuthorizationConstants.*;

/**
 * Contrôleur gérant l'export d'unités documentaires
 * <p>
 * Created by Sébastien on 23/12/2016.
 */
@RestController
@RequestMapping(value = "/api/rest/export")
public class ExportController extends AbstractRestController {

    private static final Logger LOG = LoggerFactory.getLogger(ExportController.class);

    private final DocUnitService docUnitService;
    private final DocUnitToCSVService docUnitToCSVService;
    private final DocUnitToJenaService docUnitToJenaService;
    private final ExportEadService exportEadService;
    private final AccessHelper accessHelper;
    private final LibraryAccesssHelper libraryAccesssHelper;

    @Autowired
    public ExportController(final DocUnitService docUnitService,
                            final DocUnitToCSVService docUnitToCSVService,
                            final DocUnitToJenaService docUnitToJenaService,
                            final ExportEadService exportEadService,
                            final AccessHelper accessHelper,
                            final LibraryAccesssHelper libraryAccesssHelper) {
        this.docUnitService = docUnitService;
        this.docUnitToCSVService = docUnitToCSVService;
        this.docUnitToJenaService = docUnitToJenaService;
        this.exportEadService = exportEadService;
        this.accessHelper = accessHelper;
        this.libraryAccesssHelper = libraryAccesssHelper;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/csv", produces = "text/csv", params = {"docUnit"})
    @RolesAllowed({DOC_UNIT_HAB4})
    public void exportDocUnitToCsv(final HttpServletRequest request,
                                   final HttpServletResponse response,
                                   @RequestParam(value = "docUnit") final List<String> docUnitIds,
                                   @RequestParam(value = "field", required = false) final List<String> fields,
                                   @RequestParam(value = "docfield", required = false) final List<String> docUnitFields,
                                   @RequestParam(value = "bibfield", required = false) final List<String> bibFields,
                                   @RequestParam(value = "physfield", required = false) final List<String> physFields,
                                   @RequestParam(value = "encoding", defaultValue = "ISO-8859-15") final String encoding,
                                   @RequestParam(value = "separator", defaultValue = ";") final char separator) throws PgcnTechnicalException {

        // droits d'accès à l'ud
        final Collection<DocUnit> docUnits = accessHelper.filterDocUnits(docUnitIds);
        if (docUnits.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        // Export du CSV
        else {
            try {
                writeResponseHeaderForDownload(response, "text/csv; charset=" + encoding, null, "export_doc_unit.csv");
                docUnitToCSVService.convertFromList(response.getOutputStream(),
                                                    docUnits.stream().map(DocUnit::getIdentifier).collect(Collectors.toList()),
                                                    fields,
                                                    docUnitFields,
                                                    bibFields,
                                                    physFields,
                                                    encoding,
                                                    separator);

            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
                throw new PgcnTechnicalException(e);
            }
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/csv", produces = "text/csv", params = {"lot"})
    @RolesAllowed({DOC_UNIT_HAB4})
    public void exportLotToCsv(final HttpServletRequest request,
                               final HttpServletResponse response,
                               @RequestParam(value = "lot") final String lotId,
                               @RequestParam(value = "field", required = false) final List<String> fields,
                               @RequestParam(value = "docfield", required = false) final List<String> docUnitFields,
                               @RequestParam(value = "bibfield", required = false) final List<String> bibFields,
                               @RequestParam(value = "physfield", required = false) final List<String> physFields,
                               @RequestParam(value = "encoding", defaultValue = "ISO-8859-15") final String encoding,
                               @RequestParam(value = "separator", defaultValue = ";") final char separator) throws PgcnTechnicalException {

        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur
        if (!accessHelper.checkLot(lotId)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
        }
        // Export
        else {
            try {
                writeResponseHeaderForDownload(response, "text/csv; charset=" + encoding, null, "export_lot.csv");
                docUnitToCSVService.convertLotFromList(response.getOutputStream(),
                                                       lotId,
                                                       fields,
                                                       docUnitFields,
                                                       bibFields,
                                                       physFields,
                                                       encoding,
                                                       separator);

            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
                throw new PgcnTechnicalException(e);
            }
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/rdfxml", produces = MediaType.TEXT_XML_VALUE)
    @RolesAllowed({DOC_UNIT_HAB4})
    public void exportDocUnitToRdfxml(final HttpServletRequest request,
                                      final HttpServletResponse response,
                                      @RequestParam(value = "type", required = false, defaultValue = "DC")
                                      final DocPropertyType.DocPropertySuperType superType,
                                      @RequestParam(value = "docUnit") final String docUnitId) throws PgcnTechnicalException {

        final DocUnit docUnit = docUnitService.findOneWithAllDependencies(docUnitId);
        // Non trouvé
        if (docUnit == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
        }
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur
        else if (!libraryAccesssHelper.checkLibrary(request, docUnit, DocUnit::getLibrary)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
        }
        // Export
        else {
            try {
                writeResponseHeaderForDownload(response, MediaType.TEXT_XML_VALUE, null, getExportName(docUnit, superType.name()));
                docUnitToJenaService.convert(response.getOutputStream(), docUnit, superType);

            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
                throw new PgcnTechnicalException(e);
            }
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/ead", produces = MediaType.APPLICATION_XML_VALUE)
    @RolesAllowed({DOC_UNIT_HAB4})
    public void getEad(final HttpServletResponse response, @RequestParam(value = "docUnit") final String docUnitId) throws PgcnTechnicalException {
        final File ead = exportEadService.retrieveEad(docUnitId);
        // Non trouvé
        if (ead == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
        } else {
            final DocUnit docUnit = docUnitService.findOne(docUnitId);
            writeResponseForDownload(response, ead, MediaType.APPLICATION_XML_VALUE, getExportName(docUnit, "ead"));
        }
    }

    private String getExportName(final DocUnit docUnit, final String type) {
        return "docunit-" + type.toLowerCase() + "-" + docUnit.getPgcnId().replaceAll("[\\W+]", "_") + ".xml";
    }
}
