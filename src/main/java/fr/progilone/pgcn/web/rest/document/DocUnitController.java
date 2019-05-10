package fr.progilone.pgcn.web.rest.document;

import static fr.progilone.pgcn.web.rest.document.security.AuthorizationConstants.DOC_UNIT_HAB0;
import static fr.progilone.pgcn.web.rest.document.security.AuthorizationConstants.DOC_UNIT_HAB1;
import static fr.progilone.pgcn.web.rest.document.security.AuthorizationConstants.DOC_UNIT_HAB2;
import static fr.progilone.pgcn.web.rest.document.security.AuthorizationConstants.DOC_UNIT_HAB3;
import static fr.progilone.pgcn.web.rest.document.security.AuthorizationConstants.DOC_UNIT_HAB4;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import fr.progilone.pgcn.domain.dto.document.DocUnitDTO;
import fr.progilone.pgcn.domain.dto.document.DocUnitDeletedReportDTO;
import fr.progilone.pgcn.domain.dto.document.DocUnitMassUpdateDTO;
import fr.progilone.pgcn.domain.dto.document.DocUnitUpdateErrorDTO;
import fr.progilone.pgcn.domain.dto.document.SimpleDocUnitDTO;
import fr.progilone.pgcn.domain.dto.document.SimpleListDocUnitDTO;
import fr.progilone.pgcn.domain.dto.document.SummaryDocUnitDTO;
import fr.progilone.pgcn.domain.dto.document.SummaryDocUnitWithLotDTO;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.exception.PgcnException;
import fr.progilone.pgcn.exception.PgcnLockException;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.service.LockService;
import fr.progilone.pgcn.service.document.DocUnitService;
import fr.progilone.pgcn.service.document.ui.UIDocUnitService;
import fr.progilone.pgcn.service.es.EsDocUnitService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import fr.progilone.pgcn.web.util.AccessHelper;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;

@RestController
@RequestMapping(value = "/api/rest/docunit")
public class DocUnitController extends AbstractRestController {

    private static final Logger LOG = LoggerFactory.getLogger(DocUnitController.class);

    private final DocUnitService docUnitService;
    private final UIDocUnitService uiDocUnitService;
    private final EsDocUnitService esDocUnitService;
    private final AccessHelper accessHelper;
    private final LibraryAccesssHelper libraryAccesssHelper;
    private final LockService lockService;

    @Autowired
    public DocUnitController(final DocUnitService docUnitService,
                             final UIDocUnitService uiDocUnitService,
                             final EsDocUnitService esDocUnitService,
                             final AccessHelper accessHelper,
                             final LibraryAccesssHelper libraryAccesssHelper,
                             final LockService lockService) {
        super();
        this.docUnitService = docUnitService;
        this.uiDocUnitService = uiDocUnitService;
        this.esDocUnitService = esDocUnitService;
        this.accessHelper = accessHelper;
        this.libraryAccesssHelper = libraryAccesssHelper;
        this.lockService = lockService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @Timed
    @RolesAllowed(DOC_UNIT_HAB1)
    public ResponseEntity<DocUnitDTO> create(final HttpServletRequest request, @RequestBody final DocUnitDTO doc) throws PgcnException {
        if (doc.getLibrary() == null) {
            libraryAccesssHelper.setDefaultLibrary(doc::setLibrary);
        } else if (!libraryAccesssHelper.checkLibrary(request, doc.getLibrary().getIdentifier())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final DocUnitDTO docUnitDTO = uiDocUnitService.create(doc);
        esDocUnitService.indexAsync(docUnitDTO.getIdentifier());    // Moteur de recherche
        return new ResponseEntity<>(docUnitDTO, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{identifier}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    @Timed
    @RolesAllowed(DOC_UNIT_HAB3)
    public ResponseEntity<DocUnitDeletedReportDTO> delete(@PathVariable final String identifier) {
        if (!accessHelper.checkDocUnit(identifier)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final DocUnitDeletedReportDTO report = uiDocUnitService.delete(identifier);
        return createResponseEntity(report);
    }

    @RequestMapping(method = RequestMethod.POST, params = {"delete"})
    @ResponseStatus(HttpStatus.OK)
    @Timed
    @RolesAllowed(DOC_UNIT_HAB3)
    public ResponseEntity<Collection<DocUnitDeletedReportDTO>> delete(@RequestBody final List<String> ids) {
        // TODO access control
        final Collection<DocUnitDeletedReportDTO> entities = uiDocUnitService.delete(ids);
        return createResponseEntity(entities);
    }

    /**
     * Modification des UD en masse.
     *
     * @param values
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, params = {"updateselection"})
    @ResponseStatus(HttpStatus.OK)
    @Timed
    @RolesAllowed(DOC_UNIT_HAB2)
    public ResponseEntity<List<DocUnitUpdateErrorDTO>> updateSelection(@RequestBody final DocUnitMassUpdateDTO values) {

        if (values.getDocUnitIds().isEmpty()) {
            return createResponseEntity(Collections.emptyList());
        }
        // droits d'accès à l'ud
        final Collection<DocUnit> filteredDocUnits = accessHelper.filterDocUnits(values.getDocUnitIds());
        if (filteredDocUnits.isEmpty()) {
            LOG.debug("[Modification UD en masse] - Aucune DocUnit à modifier trouvée!");
            return createResponseEntity(Collections.emptyList());
        }
        final List<DocUnitUpdateErrorDTO> errorsDocUnits = uiDocUnitService.massUpdate(values, filteredDocUnits);
        return new ResponseEntity<>(errorsDocUnits, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, params = {"deleteDocUnitsProject"})
    @ResponseStatus(HttpStatus.OK)
    @Timed
    @RolesAllowed(DOC_UNIT_HAB3)
    public ResponseEntity<Collection<DocUnitDeletedReportDTO>> deleteDocUnitsProject(@RequestBody final List<String> ids) {
        // TODO access control
        final Collection<DocUnitDeletedReportDTO> entities = uiDocUnitService.deleteDocUnitsProject(ids);
        return createResponseEntity(entities);
    }

    @RequestMapping(method = RequestMethod.POST, params = {"removeAllFromLot"})
    @ResponseStatus(HttpStatus.OK)
    @Timed
    @RolesAllowed(DOC_UNIT_HAB3)
    public void removeAllFromLot(@RequestBody final List<String> ids) {
        // TODO access control
        uiDocUnitService.removeAllFromLot(ids);
    }

    @RequestMapping(method = RequestMethod.POST, params = {"unlink"})
    @ResponseStatus(HttpStatus.OK)
    @Timed
    @RolesAllowed(DOC_UNIT_HAB3)
    public ResponseEntity<Collection<DocUnitDeletedReportDTO>> unlink(final HttpServletResponse response, @RequestBody final List<String> ids) {
        // Droits d'accès : 1 probleme detecte, on bloque tout
        ids.forEach(id -> {
            if (!accessHelper.checkDocUnit(id)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        });
        final Collection<DocUnitDeletedReportDTO> errorEntities = uiDocUnitService.unlink(ids);
        return createResponseEntity(errorEntities);
    }

    @RequestMapping(method = RequestMethod.POST, params = {"createProjectFromDoc"})
    @ResponseStatus(HttpStatus.OK)
    @Timed
    @RolesAllowed(DOC_UNIT_HAB3)
    public void createProjectFromDoc(@RequestBody final List<DocUnitDTO> docs) {
        // TODO access control, aussi, vérifier que ce verbe sert à quelque chose
        uiDocUnitService.createProjectFromDoc(docs);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"search"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({DOC_UNIT_HAB0})
    public ResponseEntity<Page<SimpleDocUnitDTO>> search(final HttpServletRequest request,
                                                         @RequestParam(value = "search", required = false) final String search,
                                                         @RequestParam(value = "hasDigitalDocuments", defaultValue = "false", required = false)
                                                         final boolean hasDigitalDocuments,
                                                         @RequestParam(value = "active", defaultValue = "true", required = false)
                                                         final boolean active,
                                                         @RequestParam(value = "archived", defaultValue = "false", required = false)
                                                         final boolean archived,
                                                         @RequestParam(value = "nonArchived", defaultValue = "false", required = false)
                                                         final boolean nonArchived,
                                                         @RequestParam(value = "archivable", defaultValue = "false", required = false)
                                                         final boolean archivable,
                                                         @RequestParam(value = "nonArchivable", defaultValue = "false", required = false)
                                                         final boolean nonArchivable,
                                                         @RequestParam(value = "distributed", defaultValue = "false", required = false)
                                                         final boolean distributed,
                                                         @RequestParam(value = "nonDistributed", defaultValue = "false", required = false)
                                                         final boolean nonDistributed,
                                                         @RequestParam(value = "distributable", defaultValue = "false", required = false)
                                                         final boolean distributable,
                                                         @RequestParam(value = "nonDistributable", defaultValue = "false", required = false)
                                                         final boolean nonDistributable,
                                                         @RequestParam(value = "libraries", required = false) final List<String> libraries,
                                                         @RequestParam(value = "projects", required = false) final List<String> projects,
                                                         @RequestParam(value = "lots", required = false) final List<String> lots,
                                                         @RequestParam(value = "statuses", required = false) final List<String> statuses,
                                                         @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                         @RequestParam(value = "lastModifiedDateFrom", required = false)
                                                         final LocalDate lastModifiedDateFrom,
                                                         @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                         @RequestParam(value = "lastModifiedDateTo", required = false)
                                                         final LocalDate lastModifiedDateTo,
                                                         @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                         @RequestParam(value = "createdDateFrom", required = false) final LocalDate createdDateFrom,
                                                         @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                         @RequestParam(value = "createdDateTo", required = false) final LocalDate createdDateTo,
                                                         @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                                         @RequestParam(value = "size", required = false, defaultValue = "" + Integer.MAX_VALUE)
                                                         final Integer size,
                                                         @RequestParam(value = "sorts", required = false) final List<String> sorts) {
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);

        return new ResponseEntity<>(uiDocUnitService.search(search,
                                                            hasDigitalDocuments,
                                                            active,
                                                            archived,
                                                            nonArchived,
                                                            archivable,
                                                            nonArchivable,
                                                            distributed,
                                                            nonDistributed,
                                                            distributable,
                                                            nonDistributable,
                                                            filteredLibraries,
                                                            projects,
                                                            lots,
                                                            statuses,
                                                            lastModifiedDateFrom,
                                                            lastModifiedDateTo,
                                                            createdDateFrom,
                                                            createdDateTo,
                                                            page,
                                                            size,
                                                            sorts), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, params = {"searchAsList"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({DOC_UNIT_HAB0})
    public ResponseEntity<Page<SimpleListDocUnitDTO>> searchAsList(final HttpServletRequest request,
                                                                   @RequestBody final SearchRequest requestParams,
                                                                   @RequestParam(value = "page", required = false, defaultValue = "0")
                                                                   final Integer page,
                                                                   @RequestParam(value = "size",
                                                                                 required = false,
                                                                                 defaultValue = "" + Integer.MAX_VALUE) final Integer size,
                                                                   @RequestParam(value = "sorts", required = false) final List<String> sorts) {
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, requestParams.getLibraries());
        return new ResponseEntity<>(uiDocUnitService.searchAsList(requestParams.getSearch(),
                                                                  requestParams.isHasDigitalDocuments(),
                                                                  requestParams.isActive(),
                                                                  requestParams.isArchived(),
                                                                  requestParams.isNonArchived(),
                                                                  requestParams.isArchivable(),
                                                                  requestParams.isNonArchivable(),
                                                                  requestParams.isDistributed(),
                                                                  requestParams.isNonDistributed(),
                                                                  requestParams.isDistributable(),
                                                                  requestParams.isNonDistributable(),
                                                                  filteredLibraries,
                                                                  requestParams.getProjects(),
                                                                  requestParams.getLots(),
                                                                  requestParams.getStatuses(),
                                                                  requestParams.getLastModifiedDateFrom(),
                                                                  requestParams.getLastModifiedDateTo(),
                                                                  requestParams.getCreatedDateFrom(),
                                                                  requestParams.getCreatedDateTo(),
                                                                  requestParams.getFilter(),
                                                                  page,
                                                                  size,
                                                                  sorts), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"dto"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(DOC_UNIT_HAB0)
    public ResponseEntity<List<SimpleDocUnitDTO>> findAllDTO() {
        // TODO filter
        return new ResponseEntity<>(uiDocUnitService.findAllSimpleDTO(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{identifier}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(DOC_UNIT_HAB0)
    public ResponseEntity<DocUnitDTO> getById(@PathVariable final String identifier) {
        if (!accessHelper.checkDocUnit(identifier)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return createResponseEntity(uiDocUnitService.getOne(identifier));
    }

    @RequestMapping(value = "/{identifier}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @Timed
    @RolesAllowed(DOC_UNIT_HAB2)
    public ResponseEntity<DocUnitDTO> update(@RequestBody final DocUnitDTO doc) throws PgcnException, PgcnLockException {
        // Droits d'accès
        if (!accessHelper.checkDocUnit(doc.getIdentifier())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Non trouvé
        final DocUnit docUnit = docUnitService.findOne(doc.getIdentifier());
        if (docUnit == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Verrou
        lockService.checkLock(docUnit);  // throws PgcnLockException
        // Mise à jour
        final DocUnitDTO docUnitDTO = uiDocUnitService.update(doc);
        esDocUnitService.indexAsync(docUnitDTO.getIdentifier()); // Moteur de recherche
        return new ResponseEntity<>(docUnitDTO, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, params = {"projectAndLot"})
    @ResponseStatus(HttpStatus.OK)
    @Timed
    @RolesAllowed(DOC_UNIT_HAB2)
    public void setProjectLotAndTrain(@RequestBody final List<String> docs,
                                      @RequestParam(name = "project") final String project,
                                      @RequestParam(name = "lot", required = false) final String lot,
                                      @RequestParam(name = "train", required = false) final String train) {
        // TODO access control for docs, project, lot, train
        uiDocUnitService.setProjectAndLot(docs, project, lot, train);
    }

    /**
     * Retrait d'un {@link DocUnit} d'un {@link Project}
     *
     * @param identifier
     * @return
     */
    @RequestMapping(value = "/{identifier}", method = RequestMethod.POST, params = {"removeProject"})
    @ResponseStatus(HttpStatus.OK)
    @Timed
    @RolesAllowed(DOC_UNIT_HAB2)
    public ResponseEntity<?> removeFromProject(@PathVariable final String identifier) {
        if (!accessHelper.checkDocUnit(identifier)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        uiDocUnitService.removeFromProject(identifier);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{identifier}", method = RequestMethod.POST, params = {"removeLot"})
    @ResponseStatus(HttpStatus.OK)
    @Timed
    @RolesAllowed(DOC_UNIT_HAB2)
    public ResponseEntity<?> removeLot(@PathVariable final String identifier) {
        if (!accessHelper.checkDocUnit(identifier)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        uiDocUnitService.removeLot(identifier);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{identifier}", method = RequestMethod.POST, params = {"removeTrain"})
    @ResponseStatus(HttpStatus.OK)
    @Timed
    @RolesAllowed(DOC_UNIT_HAB2)
    public ResponseEntity<?> removeTrain(@PathVariable final String identifier) {
        if (!accessHelper.checkDocUnit(identifier)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        uiDocUnitService.removeTrain(identifier);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"project"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(DOC_UNIT_HAB0)
    public ResponseEntity<List<SummaryDocUnitWithLotDTO>> findAllForProject(@RequestParam(value = "project") final String projectId) {
        if (!accessHelper.checkProject(projectId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final List<SummaryDocUnitWithLotDTO> docs = uiDocUnitService.findAllForProject(projectId);
        return createResponseEntity(docs);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"lot"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(DOC_UNIT_HAB0)
    public ResponseEntity<List<SummaryDocUnitDTO>> findAllForLot(@RequestParam(value = "lot") final String lotId) {
        if (!accessHelper.checkLot(lotId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final List<SummaryDocUnitDTO> docs = uiDocUnitService.findAllForLot(lotId);
        return createResponseEntity(docs);
    }

    /**
     * Chargement des enfants d'une unité documentaire donnée
     *
     * @param parentId
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, params = {"parent"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(DOC_UNIT_HAB0)
    public ResponseEntity<List<DocUnitDTO>> getChildrenByParentId(@RequestParam(name = "parent") final String parentId) {
        if (!accessHelper.checkDocUnit(parentId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return createResponseEntity(uiDocUnitService.getChildren(parentId));
    }

    /**
     * Ajoute des enfants à une unité documentaire
     *
     * @param parentId
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, params = {"addchild"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(DOC_UNIT_HAB2)
    public ResponseEntity<DocUnitDTO> addChildren(@RequestParam(name = "parent") final String parentId, @RequestBody final List<String> children) {
        if (!accessHelper.checkDocUnit(parentId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        uiDocUnitService.addChildren(parentId, children);
        return new ResponseEntity<>(uiDocUnitService.getOne(parentId), HttpStatus.OK);
    }

    /**
     * Supprime un enfant sur une unité documentaire
     *
     * @param parentId
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, params = {"removechild"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(DOC_UNIT_HAB2)
    public ResponseEntity<DocUnitDTO> removeChildren(@RequestParam(name = "parent") final String parentId,
                                                     @RequestParam(name = "child") final String childId) {
        if (!accessHelper.checkDocUnit(parentId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        uiDocUnitService.removeChild(parentId, childId);
        return new ResponseEntity<>(uiDocUnitService.getOne(parentId), HttpStatus.OK);
    }

    /**
     * Chargement des soeurs d'une unité documentaire donnée
     *
     * @param id
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, params = {"sibling"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(DOC_UNIT_HAB0)
    public ResponseEntity<List<DocUnitDTO>> getSiblingsByDocUnitId(@RequestParam(name = "sibling") final String id) {
        if (!accessHelper.checkDocUnit(id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return createResponseEntity(uiDocUnitService.getSiblings(id));
    }

    /**
     * Ajoute des enfants à une unité documentaire
     *
     * @param id
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, params = {"addsibling"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(DOC_UNIT_HAB2)
    public ResponseEntity<DocUnitDTO> addSibling(@RequestParam(name = "siblingid") final String id, @RequestBody final List<String> siblingIds) {
        if (!accessHelper.checkDocUnit(id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        uiDocUnitService.addSibling(id, siblingIds);
        return new ResponseEntity<>(uiDocUnitService.getOne(id), HttpStatus.OK);
    }

    /**
     * Supprime un enfant sur une unité documentaire
     *
     * @param id
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, params = {"removesibling"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(DOC_UNIT_HAB2)
    public ResponseEntity<DocUnitDTO> removeSibling(@RequestParam(name = "siblingid") final String id,
                                                    @RequestParam(name = "removesibling") final String removeId) {
        if (!accessHelper.checkDocUnit(id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        uiDocUnitService.removeSibling(removeId);
        return new ResponseEntity<>(uiDocUnitService.getOne(id), HttpStatus.OK);
    }

    /**
     * Télécharge une archive ZIP contenant les fichiers demandés pour les unités documentaires demandées.
     *
     * @throws PgcnTechnicalException
     */
    @RequestMapping(method = RequestMethod.GET, params = {"export"})
    @Timed
    @RolesAllowed(DOC_UNIT_HAB4)
    public void massExport(final HttpServletResponse response,
                           @RequestParam(name = "docs") final List<String> docs,
                           @RequestParam(name = "types", defaultValue = "METS,VIEW") final List<String> exportTypes) throws PgcnTechnicalException {
        // droits d'accès à l'ud
        final Collection<DocUnit> filteredDocUnits = accessHelper.filterDocUnits(docs);
        if (filteredDocUnits.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        // export du modèle de doc d'import
        try {
            writeResponseHeaderForDownload(response, "application/zip", null, "mass_export.zip");

            // réponse
            uiDocUnitService.massExport(response.getOutputStream(),
                                        filteredDocUnits.stream().map(DocUnit::getIdentifier).collect(Collectors.toList()),
                                        exportTypes);

        } catch (final IOException e) {
            throw new PgcnTechnicalException(e);
        }
    }
    
    /**
     * Télécharge une archive ZIP contenant les fichiers demandés pour les unités documentaires demandées.
     *
     * @throws PgcnTechnicalException
     */
    @RequestMapping(method = RequestMethod.GET, params = {"export_ftp"})
    @Timed
    @RolesAllowed(DOC_UNIT_HAB4)
    public ResponseEntity<?> massExportFtp(final HttpServletResponse response,
                           @RequestParam(name = "docs") final List<String> docs,
                           @RequestParam(name = "types", defaultValue = "METS,VIEW") final List<String> exportTypes) throws PgcnTechnicalException {
        // droits d'accès à l'ud
        final Collection<DocUnit> filteredDocUnits = accessHelper.filterDocUnits(docs);
        if (filteredDocUnits.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        // recup library
        final Library firstLib = filteredDocUnits.iterator().next().getLibrary();
        boolean exported = false;
        try {
            exported = uiDocUnitService.massExportToFtp(filteredDocUnits.stream().map(DocUnit::getIdentifier).collect(Collectors.toList()), 
                                                        exportTypes, firstLib);
        } catch (final IOException e) {
            throw new PgcnTechnicalException(e);
        }
        if (exported) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        } 
    }

    @RequestMapping(value = "/{identifier}", method = RequestMethod.GET, params = {"lock"})
    @Timed
    @RolesAllowed(DOC_UNIT_HAB2)
    public void lock(final HttpServletResponse response, @PathVariable final String identifier) throws PgcnLockException {
        final DocUnit docUnit = docUnitService.findOne(identifier);
        // pas trouvé
        if (docUnit == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        // pas autorisé
        if (!accessHelper.checkDocUnit(identifier)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        lockService.acquireLock(docUnit);
    }

    @RequestMapping(value = "/{identifier}", method = RequestMethod.GET, params = {"unlock"})
    @Timed
    @RolesAllowed(DOC_UNIT_HAB2)
    public void unlock(final HttpServletResponse response, @PathVariable final String identifier) throws PgcnLockException {
        final DocUnit docUnit = docUnitService.findOne(identifier);
        // pas trouvé
        if (docUnit == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        // pas autorisé
        if (!accessHelper.checkDocUnit(identifier)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        lockService.releaseLock(docUnit);
    }

    @RequestMapping(value = "/{identifier}", method = RequestMethod.POST, params = {"inactiveDoc"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Timed
    @RolesAllowed(DOC_UNIT_HAB2)
    public ResponseEntity<DocUnitDTO> inactive(@PathVariable final String identifier, @RequestBody final DocUnitDTO doc) {
        if (!accessHelper.checkDocUnit(identifier)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final DocUnitDTO inactived = uiDocUnitService.inactiveDocUnit(doc);
        return new ResponseEntity<>(inactived, HttpStatus.OK);
    }

    private static final class SearchRequest {
        private String search;
        private boolean hasDigitalDocuments;
        private boolean active;
        private boolean archived;
        private boolean nonArchived;
        private boolean archivable;
        private boolean nonArchivable;
        private boolean distributed;
        private boolean nonDistributed;
        private boolean distributable;
        private boolean nonDistributable;
        private List<String> libraries;
        private List<String> projects;
        private List<String> lots;
        private List<String> statuses;
        private LocalDate lastModifiedDateFrom;
        private LocalDate lastModifiedDateTo;
        private LocalDate createdDateFrom;
        private LocalDate createdDateTo;
        private List<String> filter;

        public String getSearch() {
            return search;
        }

        public void setSearch(final String search) {
            this.search = search;
        }

        public boolean isHasDigitalDocuments() {
            return hasDigitalDocuments;
        }

        public void setHasDigitalDocuments(final boolean hasDigitalDocuments) {
            this.hasDigitalDocuments = hasDigitalDocuments;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(final boolean active) {
            this.active = active;
        }

        public boolean isArchived() {
            return archived;
        }

        public void setArchived(final boolean archived) {
            this.archived = archived;
        }

        public boolean isNonArchived() {
            return nonArchived;
        }

        public void setNonArchived(final boolean nonArchived) {
            this.nonArchived = nonArchived;
        }

        public boolean isArchivable() {
            return archivable;
        }

        public void setArchivable(final boolean archivable) {
            this.archivable = archivable;
        }

        public boolean isNonArchivable() {
            return nonArchivable;
        }

        public void setNonArchivable(final boolean nonArchivable) {
            this.nonArchivable = nonArchivable;
        }

        public boolean isDistributed() {
            return distributed;
        }

        public void setDistributed(final boolean distributed) {
            this.distributed = distributed;
        }

        public boolean isNonDistributed() {
            return nonDistributed;
        }

        public void setNonDistributed(final boolean nonDistributed) {
            this.nonDistributed = nonDistributed;
        }

        public boolean isDistributable() {
            return distributable;
        }

        public void setDistributable(final boolean distributable) {
            this.distributable = distributable;
        }

        public boolean isNonDistributable() {
            return nonDistributable;
        }

        public void setNonDistributable(final boolean nonDistributable) {
            this.nonDistributable = nonDistributable;
        }

        public List<String> getLibraries() {
            return libraries;
        }

        public void setLibraries(final List<String> libraries) {
            this.libraries = libraries;
        }

        public List<String> getProjects() {
            return projects;
        }

        public void setProjects(final List<String> projects) {
            this.projects = projects;
        }

        public List<String> getLots() {
            return lots;
        }

        public void setLots(final List<String> lots) {
            this.lots = lots;
        }

        public List<String> getStatuses() {
            return statuses;
        }

        public void setStatuses(final List<String> statuses) {
            this.statuses = statuses;
        }

        public LocalDate getLastModifiedDateFrom() {
            return lastModifiedDateFrom;
        }

        public void setLastModifiedDateFrom(final LocalDate lastModifiedDateFrom) {
            this.lastModifiedDateFrom = lastModifiedDateFrom;
        }

        public LocalDate getLastModifiedDateTo() {
            return lastModifiedDateTo;
        }

        public void setLastModifiedDateTo(final LocalDate lastModifiedDateTo) {
            this.lastModifiedDateTo = lastModifiedDateTo;
        }

        public LocalDate getCreatedDateFrom() {
            return createdDateFrom;
        }

        public void setCreatedDateFrom(final LocalDate createdDateFrom) {
            this.createdDateFrom = createdDateFrom;
        }

        public LocalDate getCreatedDateTo() {
            return createdDateTo;
        }

        public void setCreatedDateTo(final LocalDate createdDateTo) {
            this.createdDateTo = createdDateTo;
        }

        public List<String> getFilter() {
            return filter;
        }

        public void setFilter(final List<String> filter) {
            this.filter = filter;
        }
    }
}
