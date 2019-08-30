package fr.progilone.pgcn.web.rest.document;

import static fr.progilone.pgcn.web.rest.checkconfiguration.security.AuthorizationConstants.CHECK_HAB3;
import static fr.progilone.pgcn.web.rest.checkconfiguration.security.AuthorizationConstants.CHECK_HAB4;
import static fr.progilone.pgcn.web.rest.document.security.AuthorizationConstants.DOC_UNIT_HAB0;
import static fr.progilone.pgcn.web.rest.document.security.AuthorizationConstants.DOC_UNIT_HAB2;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.dto.checkconfiguration.CheckConfigurationDTO;
import fr.progilone.pgcn.domain.dto.document.DigitalDocumentDTO;
import fr.progilone.pgcn.domain.dto.document.SimpleDigitalDocumentDTO;
import fr.progilone.pgcn.domain.dto.document.SimpleDocPageDTO;
import fr.progilone.pgcn.domain.dto.document.SimpleListDigitalDocumentDTO;
import fr.progilone.pgcn.domain.dto.sample.SampleDTO;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.service.document.DigitalDocumentService;
import fr.progilone.pgcn.service.document.ViewerService;
import fr.progilone.pgcn.service.document.ui.UIDigitalDocumentService;
import fr.progilone.pgcn.service.sample.SampleService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import fr.progilone.pgcn.web.util.AccessHelper;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;
import fr.progilone.pgcn.web.util.WorkflowAccessHelper;
import fr.progilone.pgcn.web.util.WorkflowUserAccessHelper;

@RestController
@RequestMapping(value = "/api/rest/digitaldocument")
public class DigitalDocumentController extends AbstractRestController {

    private final UIDigitalDocumentService uiDigitalDocumentService;
    private final DigitalDocumentService digitalDocumentService;
    private final ViewerService viewerService;
    private final AccessHelper accessHelper;
    private final LibraryAccesssHelper libraryAccesssHelper;
    private final WorkflowAccessHelper workflowAccessHelper;
    private final WorkflowUserAccessHelper workflowUserAccessHelper;
    private final SampleService sampleService;
    

    @Autowired
    public DigitalDocumentController(final UIDigitalDocumentService uiDigitalDocumentService,
                                     final DigitalDocumentService digitalDocumentService,
                                     final ViewerService viewerService,
                                     final AccessHelper accessHelper,
                                     final LibraryAccesssHelper libraryAccesssHelper,
                                     final WorkflowAccessHelper workflowAccessHelper,
                                     final WorkflowUserAccessHelper workflowUserAccessHelper,
                                     final SampleService sampleService) {
        this.uiDigitalDocumentService = uiDigitalDocumentService;
        this.digitalDocumentService = digitalDocumentService;
        this.viewerService = viewerService;
        this.accessHelper = accessHelper;
        this.libraryAccesssHelper = libraryAccesssHelper;
        this.workflowAccessHelper = workflowAccessHelper;
        this.workflowUserAccessHelper = workflowUserAccessHelper;
        this.sampleService = sampleService;
    }

    /**
     * Retourne le thumbnail correspondant à l'identifiant de doc numérique et à la page donnée
     *
     * @param request
     * @param response
     * @param identifier
     * @param pageNumber
     * @return
     * @throws PgcnTechnicalException
     */
    @RequestMapping(value = "/{identifier}", params = {"thumbnail"}, method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    @Timed
    public ResponseEntity<?> getThumbnailById(final HttpServletRequest request,
                                              final HttpServletResponse response,
                                              @PathVariable final String identifier,
                                              @RequestParam final int pageNumber) throws PgcnTechnicalException {
        if (!accessHelper.checkDigitalDocument(identifier)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final File thumbnail = digitalDocumentService.getThumbnail(identifier, pageNumber);
        writeResponseForDownload(response, thumbnail, MediaType.IMAGE_JPEG_VALUE, thumbnail.getName());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Retourne le format VIEW correspondant à l'identifiant de doc numérique et à la page donnée
     *
     * @param request
     * @param response
     * @param identifier
     * @param pageNumber
     * @return
     * @throws PgcnTechnicalException
     */
    @RequestMapping(value = "/{identifier}", params = {"view"}, method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    @Timed
    @RolesAllowed(CHECK_HAB3)
    public ResponseEntity<?> getViewById(final HttpServletRequest request,
                                         final HttpServletResponse response,
                                         @PathVariable final String identifier,
                                         @RequestParam final int pageNumber) throws PgcnTechnicalException {
        if (!accessHelper.checkDigitalDocument(identifier)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final File thumbnail = digitalDocumentService.getView(identifier, pageNumber);
        writeResponseForDownload(response, thumbnail, MediaType.IMAGE_JPEG_VALUE, thumbnail.getName());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Récupération d'un {@link DigitalDocumentDTO} à partir de son identifiant
     *
     * @param request
     * @param identifier
     * @return
     */
    @RequestMapping(value = "/{identifier}", method = RequestMethod.GET)
    @Timed
    @RolesAllowed(DOC_UNIT_HAB0)
    public ResponseEntity<DigitalDocumentDTO> getByIdWithPageCount(final HttpServletRequest request, @PathVariable final String identifier) {
        if (!accessHelper.checkDigitalDocument(identifier)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final DigitalDocumentDTO dddto = uiDigitalDocumentService.getDigitalDocumentWithNumberOfPages(identifier);
        return createResponseEntity(dddto);
    }

    /**
     * Recuperation de la config de controle  du document.
     *
     */
    @RequestMapping(value = "/{identifier}", method = RequestMethod.GET, params = {"docUnit"})
    @Timed
    public ResponseEntity<CheckConfigurationDTO> getConfigurationCheck(final HttpServletRequest request, @PathVariable final String identifier) {
        if (!accessHelper.checkDigitalDocument(identifier)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(digitalDocumentService.getConfigurationCheck(identifier), HttpStatus.OK);
    }

    @RequestMapping(value = "/{identifier}", method = RequestMethod.GET, params = {"deliverynotes"})
    @Timed
    public ResponseEntity<Map<String, String>> getDeliveryNotes(final HttpServletRequest request, @PathVariable final String identifier) {
        if (!accessHelper.checkDigitalDocument(identifier)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final Map<String,String> m = new HashMap<>();
        m.put("deliveryNotes", digitalDocumentService.getDeliveryNotes(identifier));
        return new ResponseEntity<>(m, HttpStatus.OK);
    }

    /**
     * Retourne le nom de fichier correspondant à la page du document passé en identifiant
     *
     * @param request
     * @param identifier
     * @param pageNumber
     * @return
     */
    @RequestMapping(value = "/{identifier}", method = RequestMethod.GET, params = {"filename"})
    @Timed
    public ResponseEntity<String> getFilename(final HttpServletRequest request, @PathVariable final String identifier, @RequestParam final int pageNumber) {
        if (!accessHelper.checkDigitalDocument(identifier)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final String filename = digitalDocumentService.getFilename(identifier, pageNumber);
        return new ResponseEntity<>(filename, HttpStatus.OK);
    }
    
    /**
     * Renvoie le taille/nom du master pdf.
     *
     * @param request
     * @param identifier
     * @return
     */
    @RequestMapping(value = "/{identifier}", method = RequestMethod.GET, params = "masterPdfInfos")
    @Timed
    @RolesAllowed(DOC_UNIT_HAB0)
    public ResponseEntity<String[]> getMasterPdfInfos(final HttpServletRequest request, @PathVariable final String identifier) {
        if (!accessHelper.checkDigitalDocument(identifier)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final String names[] = digitalDocumentService.getMasterPdfName(identifier);
        return new ResponseEntity<>(names, HttpStatus.OK);
    }

    /**
     * @param request
     * @param identifier
     * @return
     * @throws PgcnTechnicalException
     */
    @RequestMapping(value = "/{identifier}", method = RequestMethod.GET, params = "metadata")
    @Timed
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<Map<String, Map<String, String>>> getMetadataForFiles(final HttpServletRequest request,
                                                                                @PathVariable final String identifier) throws PgcnTechnicalException {

        final Map<String, Map<String, String>> mdForFiles = viewerService.getMetadatasForFiles(identifier);
        return new ResponseEntity<>(mdForFiles, HttpStatus.OK);
    }


    @RequestMapping(value = "/{identifier}", method = RequestMethod.GET, params = "samplemetadata")
    @Timed
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<Map<String, Map<String, String>>> getMetadataForSample(final HttpServletRequest request,
                                                                                @PathVariable final String identifier) throws PgcnTechnicalException {

        final Map<String, Map<String, String>> mdForFiles = viewerService.getMetadatasForSample(identifier);
        return new ResponseEntity<>(mdForFiles, HttpStatus.OK);
    }

    /**
     * Retourne la liste des noms de fichier en erreur
     *
     * @param request
     * @param identifier
     * @return
     */
    @RequestMapping(value = "/{identifier}", method = RequestMethod.GET, params = "filenamesErrors")
    @Timed
    public ResponseEntity<List<String>> getFilenamesWithErrors(final HttpServletRequest request, @PathVariable final String identifier) {
        if (!accessHelper.checkDigitalDocument(identifier)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final List<String> names = digitalDocumentService.getFilenamesWithErrors(identifier);
        return new ResponseEntity<>(names, HttpStatus.OK);
    }

    /**
     * Retourne la liste des pages en erreur
     *
     * @param request
     * @param identifier
     * @return
     */
    @RequestMapping(value = "/{identifier}", method = RequestMethod.GET, params = "filesErrors")
    @Timed
    public ResponseEntity<List<Integer>> getFileNumbersWithErrors(final HttpServletRequest request, @PathVariable final String identifier) {
        if (!accessHelper.checkDigitalDocument(identifier)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final List<Integer> numbers = digitalDocumentService.getFileNumbersWithErrors(identifier);
        return new ResponseEntity<>(numbers, HttpStatus.OK);
    }

    /**
     * Clôture la validation de contrôle (validé / rejeté)
     *
     * @param request
     * @param identifier
     * @param checksOK
     * @return
     */
    @RequestMapping(value = "/{identifier}", method = RequestMethod.POST, params = {"checksOK", "sampling"})
    @Timed
    @RolesAllowed(CHECK_HAB4)
    public ResponseEntity<?> endChecks(final HttpServletRequest request, @PathVariable final String identifier, @RequestParam final boolean checksOK, @RequestParam final boolean sampling) {

        if (sampling) {
            // Droits echantillon
            final SampleDTO sample = sampleService.getOne(identifier);
            final List<String> forbiddenIds = new ArrayList<>();

            sample.getDocuments().forEach(doc-> {
                final String docId = doc.getIdentifier();
                if (!accessHelper.checkDigitalDocument(docId)) {
                    forbiddenIds.add(docId);
                } else {
                    // Droit par rapport au workflow
                    final DocUnit docUnit = digitalDocumentService.findDocUnitByIdentifier(docId);
                    if(!workflowAccessHelper.canCheckBePerformed(docUnit.getIdentifier())
                            || !workflowUserAccessHelper.canCurrentUserProcessCheck(docUnit.getIdentifier()) ) {
                        forbiddenIds.add(docUnit.getIdentifier());
                    }
                }
            });
            // 1 docUnit refusee => refus du sample.
            if (CollectionUtils.isNotEmpty(forbiddenIds)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            // process de cloture
            uiDigitalDocumentService.endChecksForSampling(identifier, checksOK);

        } else {
            // Droits DocUnit.
            if (!accessHelper.checkDigitalDocument(identifier)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            // Droit par rapport au workflow
            final DocUnit docUnit = digitalDocumentService.findDocUnitByIdentifier(identifier);
            if(!workflowAccessHelper.canCheckBePerformed(docUnit.getIdentifier())) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            if(!workflowUserAccessHelper.canCurrentUserProcessCheck(docUnit.getIdentifier())) {
               return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            // process de cloture
            uiDigitalDocumentService.endChecks(identifier, checksOK);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Mise à jour des documents
     *
     * @param request
     * @param dto
     * @return
     */
    @RequestMapping(value = "/{identifier}", method = RequestMethod.POST)
    @Timed
    @RolesAllowed(DOC_UNIT_HAB2)
    public ResponseEntity<DigitalDocumentDTO> update(final HttpServletRequest request, @RequestBody final DigitalDocumentDTO dto) {
        if (!accessHelper.checkDigitalDocument(dto.getIdentifier())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return createResponseEntity(uiDigitalDocumentService.update(dto));
    }

    /**
     * Récupération de la liste des documents à contrôler
     *
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, params = "toCheck")
    @Timed
    @RolesAllowed(DOC_UNIT_HAB0)
    public ResponseEntity<Collection<SimpleDigitalDocumentDTO>> getAllDocumentsToCheck(final HttpServletRequest request) {
        final Collection<SimpleDigitalDocumentDTO> digitalDocuments = uiDigitalDocumentService.getAllDocumentsToCheck();
        final List<SimpleDigitalDocumentDTO> filteredDocs = filterDocsDTOs(digitalDocuments, SimpleDigitalDocumentDTO::getIdentifier);
        return new ResponseEntity<>(filteredDocs, HttpStatus.OK);
    }

    /**
     * Récupération d'une page
     *
     * @param request
     * @param identifier
     * @param pageNumber
     * @return
     */
    @RequestMapping(value = "/{identifier}", method = RequestMethod.GET, params = "page")
    @Timed
    public ResponseEntity<SimpleDocPageDTO> getPage(final HttpServletRequest request,
                                                    @PathVariable final String identifier,
                                                    @RequestParam final int pageNumber) {
        if (!accessHelper.checkDigitalDocument(identifier)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return createResponseEntity(uiDigitalDocumentService.getPage(identifier, pageNumber));
    }

    /**
     * Recherche filtrée.
     *
     * @param request
     * @param search
     * @param status
     * @param libraries
     * @param projects
     * @param lots
     * @param trains
     * @param deliveries
     * @param page
     * @param dateFrom
     * @param dateTo
     * @param dateLimitFrom
     * @param dateLimitTo
     * @param searchPgcnId
     * @param searchTitre
     * @param searchRadical
     * @param searchPageFrom
     * @param searchPageTo
     * @param searchPageCheckFrom
     * @param searchPageCheckTo
     * @param size
     * @param sorts
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, params = {"search"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(DOC_UNIT_HAB0)
    public ResponseEntity<Page<SimpleListDigitalDocumentDTO>> search(final HttpServletRequest request,
                                                                     @RequestParam(value = "search", required = false) final String search,
                                                                     @RequestParam(value = "status", required = false) final List<String> status,
                                                                     @RequestParam(value = "libraries", required = false)
                                                                     final List<String> libraries,
                                                                     @RequestParam(value = "projects", required = false) final List<String> projects,
                                                                     @RequestParam(value = "lots", required = false) final List<String> lots,
                                                                     @RequestParam(value = "trains", required = false) final List<String> trains,
                                                                     @RequestParam(value = "deliveries", required = false) final List<String> deliveries,
                                                                     @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                                                     @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "dateFrom", required = false)
                                                                     final LocalDate dateFrom,
                                                                     @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "dateTo", required = false)
                                                                     final LocalDate dateTo,
                                                                     @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "dateLimitFrom", required = false)
                                                                     final LocalDate dateLimitFrom,
                                                                     @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "dateLimitTo", required = false)
                                                                     final LocalDate dateLimitTo,
                                                                     @RequestParam(value = "searchPgcnId", required = false) final String searchPgcnId,
                                                                     @RequestParam(value = "searchTitre", required = false) final String searchTitre,
                                                                     @RequestParam(value = "searchRadical", required = false) final String searchRadical,
                                                                     @RequestParam(value = "fileFormats", required = false) final List<String> searchFileFormats,
                                                                     @RequestParam(value = "maxAngles", required = false) final List<String> searchMaxAngles,
                                                                     @RequestParam(value = "searchPageFrom", required = false) final Integer searchPageFrom,
                                                                     @RequestParam(value = "searchPageTo", required = false) final Integer searchPageTo,
                                                                     @RequestParam(value = "searchPageCheckFrom", required = false) final Integer searchPageCheckFrom,
                                                                     @RequestParam(value = "searchPageCheckTo", required = false) final Integer searchPageCheckTo,
                                                                     @RequestParam(value = "searchMinSize", required = false) final Double searchMinSize,
                                                                     @RequestParam(value = "searchMaxSize", required = false) final Double searchMaxSize,
                                                                     @RequestParam(value = "validated", required = false) final boolean validated,
                                                                     @RequestParam(value = "size", required = false, defaultValue = "" + Integer.MAX_VALUE) final Integer size,
                                                                     @RequestParam(value = "sorts", required = false) final List<String> sorts) {
        // projects et lots sont déjà filtrés.
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);
        return createResponseEntity(uiDigitalDocumentService.search(search, status, filteredLibraries, projects, lots, trains, deliveries,
                                                                    dateFrom, dateTo, dateLimitFrom, dateLimitTo,
                                                                    searchPgcnId, searchTitre, searchRadical, searchFileFormats, searchMaxAngles,
                                                                    searchPageFrom, searchPageTo, searchPageCheckFrom, searchPageCheckTo,
                                                                    searchMinSize, searchMaxSize, validated, page, size, sorts));
    }


    /**
     * Filtrage d'une liste de LotDTO sur les droits d'accès de l'utilisateur.
     *
     * @param docs
     * @return
     */
    private <T> List<T> filterDocsDTOs(final Collection<T> docs, final Function<T, String> getIdentifierFn) {
        return accessHelper.filterDigitalDocuments(docs.stream().map(getIdentifierFn).collect(Collectors.toList()))
                           .stream()
                           // Correspondance doc autorisé => docDTO
                           .map(doc -> docs.stream().filter(d -> StringUtils.equals(getIdentifierFn.apply(d), doc.getIdentifier())).findAny())
                           .filter(Optional::isPresent)
                           .map(Optional::get)
                           .collect(Collectors.toList());
    }
}
