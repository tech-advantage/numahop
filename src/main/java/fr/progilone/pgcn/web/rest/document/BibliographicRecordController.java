package fr.progilone.pgcn.web.rest.document;

import static fr.progilone.pgcn.web.rest.document.security.AuthorizationConstants.DOC_UNIT_HAB0;
import static fr.progilone.pgcn.web.rest.document.security.AuthorizationConstants.DOC_UNIT_HAB1;
import static fr.progilone.pgcn.web.rest.document.security.AuthorizationConstants.DOC_UNIT_HAB2;
import static fr.progilone.pgcn.web.rest.document.security.AuthorizationConstants.DOC_UNIT_HAB3;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.dto.document.BibliographicRecordDTO;
import fr.progilone.pgcn.domain.dto.document.BibliographicRecordDcDTO;
import fr.progilone.pgcn.domain.dto.document.BibliographicRecordMassUpdateDTO;
import fr.progilone.pgcn.domain.dto.document.SimpleBibliographicRecordDTO;
import fr.progilone.pgcn.domain.dto.document.SimpleListBibliographicRecordDTO;
import fr.progilone.pgcn.exception.PgcnException;
import fr.progilone.pgcn.exception.PgcnLockException;
import fr.progilone.pgcn.service.LockService;
import fr.progilone.pgcn.service.document.BibliographicRecordService;
import fr.progilone.pgcn.service.document.ui.UIBibliographicRecordService;
import fr.progilone.pgcn.service.es.EsBibliographicRecordService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import fr.progilone.pgcn.web.util.AccessHelper;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;
import fr.progilone.pgcn.web.util.WorkflowAccessHelper;

@RestController
@RequestMapping(value = "/api/rest/bibliographicrecord")
public class BibliographicRecordController extends AbstractRestController {

    private final AccessHelper accessHelper;
    private final LibraryAccesssHelper libraryAccesssHelper;
    private final BibliographicRecordService bibliographicRecordService;
    private final UIBibliographicRecordService uiBibliographicRecordService;
    private final EsBibliographicRecordService esBibliographicRecordService;
    private final LockService lockService;
    private final WorkflowAccessHelper workflowAccessHelper;

    @Autowired
    public BibliographicRecordController(final AccessHelper accessHelper,
                                         final LibraryAccesssHelper libraryAccesssHelper,
                                         final BibliographicRecordService bibliographicRecordService,
                                         final UIBibliographicRecordService uiBibliographicRecordService,
                                         final EsBibliographicRecordService esBibliographicRecordService,
                                         final LockService lockService,
                                         final WorkflowAccessHelper workflowAccessHelper) {
        this.accessHelper = accessHelper;
        this.libraryAccesssHelper = libraryAccesssHelper;
        this.uiBibliographicRecordService = uiBibliographicRecordService;
        this.bibliographicRecordService = bibliographicRecordService;
        this.esBibliographicRecordService = esBibliographicRecordService;
        this.lockService = lockService;
        this.workflowAccessHelper = workflowAccessHelper;
    }

    @RequestMapping(method = RequestMethod.POST)
    @Timed
    @RolesAllowed(DOC_UNIT_HAB1)
    public ResponseEntity<BibliographicRecordDTO> create(@RequestBody final BibliographicRecordDTO record) throws PgcnException {
        final BibliographicRecordDTO savedRecord = uiBibliographicRecordService.create(record);
        esBibliographicRecordService.indexAsync(savedRecord.getIdentifier());
        return new ResponseEntity<>(savedRecord, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, params = {"duplicate"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({DOC_UNIT_HAB1})
    public ResponseEntity<BibliographicRecordDTO> duplicate(@PathVariable final String id) throws PgcnException {
        // Droits d'accès
        if (!accessHelper.checkBibliographicRecord(id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final BibliographicRecordDTO record = uiBibliographicRecordService.duplicate(id);
        esBibliographicRecordService.indexAsync(record.getIdentifier());
        return new ResponseEntity<>(record, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{identifier}", method = RequestMethod.DELETE)
    @Timed
    @RolesAllowed(DOC_UNIT_HAB3)
    public ResponseEntity<?> delete(@PathVariable final String identifier) {
        // Droits d'accès
        if (!accessHelper.checkBibliographicRecord(identifier)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Droit par rapport au workflow
        final DocUnit doc = bibliographicRecordService.findDocUnitByIdentifier(identifier);
        if (doc != null && !workflowAccessHelper.canRecordBeModified(doc.getIdentifier())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        bibliographicRecordService.delete(identifier);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, params = {"delete"})
    @ResponseStatus(HttpStatus.OK)
    @Timed
    @RolesAllowed(DOC_UNIT_HAB3)
    public void delete(@RequestBody final List<String> ids) {
        // Droits d'accès
        final Collection<BibliographicRecord> filteredRecords = accessHelper.filterBibliographicRecords(ids);
        final Collection<BibliographicRecord> filteredByWorkflow = new ArrayList<>();
        filteredRecords.forEach(record -> {
            // Droit par rapport au workflow
            final DocUnit doc = bibliographicRecordService.findDocUnitByIdentifier(record.getIdentifier());
            if (workflowAccessHelper.canRecordBeModified(doc.getIdentifier())) {
                filteredByWorkflow.add(record);
            }
        });
        uiBibliographicRecordService.delete(filteredByWorkflow.stream().map(BibliographicRecord::getIdentifier).collect(Collectors.toList()));
    }

    @RequestMapping(method = RequestMethod.GET, params = {"search"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({DOC_UNIT_HAB0})
    public ResponseEntity<Page<SimpleBibliographicRecordDTO>> search(final HttpServletRequest request,
                                                                     @RequestParam(value = "search", required = false) final String search,
                                                                     @RequestParam(value = "libraries", required = false)
                                                                     final List<String> libraries,
                                                                     @RequestParam(value = "projects", required = false) final List<String> projects,
                                                                     @RequestParam(value = "lots", required = false) final List<String> lots,
                                                                     @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                                     @RequestParam(value = "lastModifiedDateFrom", required = false)
                                                                     final LocalDate lastModifiedDateFrom,
                                                                     @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                                     @RequestParam(value = "lastModifiedDateTo", required = false)
                                                                     final LocalDate lastModifiedDateTo,
                                                                     @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                                     @RequestParam(value = "createdDateFrom", required = false)
                                                                     final LocalDate createdDateFrom,
                                                                     @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                                     @RequestParam(value = "createdDateTo", required = false)
                                                                     final LocalDate createdDateTo,
                                                                     @RequestParam(value = "orphan", required = false) final Boolean orphan,
                                                                     @RequestParam(value = "page", required = false, defaultValue = "0")
                                                                     final Integer page,
                                                                     @RequestParam(value = "size", required = false, defaultValue = "10")
                                                                     final Integer size,
                                                                     @RequestParam(value = "sorts", required = false) final List<String> sorts) {
        // Droits d'accès
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);
        return new ResponseEntity<>(uiBibliographicRecordService.search(search,
                                                                        filteredLibraries,
                                                                        projects,
                                                                        lots,
                                                                        lastModifiedDateFrom,
                                                                        lastModifiedDateTo,
                                                                        createdDateFrom,
                                                                        createdDateTo,
                                                                        orphan,
                                                                        page,
                                                                        size,
                                                                        sorts), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"searchAsList"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({DOC_UNIT_HAB0})
    public ResponseEntity<Page<SimpleListBibliographicRecordDTO>> searchAsList(final HttpServletRequest request,
                                                                               @RequestParam(value = "searchAsList", required = false)
                                                                               final String search,
                                                                               @RequestParam(value = "libraries", required = false)
                                                                               final List<String> libraries,
                                                                               @RequestParam(value = "projects", required = false)
                                                                               final List<String> projects,
                                                                               @RequestParam(value = "lots", required = false)
                                                                               final List<String> lots,
                                                                               @RequestParam(value = "trains", required = false)
                                                                               final List<String> trains,
                                                                               @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                                               @RequestParam(value = "lastModifiedDateFrom", required = false)
                                                                               final LocalDate lastModifiedDateFrom,
                                                                               @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                                               @RequestParam(value = "lastModifiedDateTo", required = false)
                                                                               final LocalDate lastModifiedDateTo,
                                                                               @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                                               @RequestParam(value = "createdDateFrom", required = false)
                                                                               final LocalDate createdDateFrom,
                                                                               @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                                               @RequestParam(value = "createdDateTo", required = false)
                                                                               final LocalDate createdDateTo,
                                                                               @RequestParam(value = "orphan", required = false) final Boolean orphan,
                                                                               @RequestParam(value = "page", required = false, defaultValue = "0")
                                                                               final Integer page,
                                                                               @RequestParam(value = "size",
                                                                                             required = false,
                                                                                             defaultValue = "" + Integer.MAX_VALUE)
                                                                               final Integer size,
                                                                               @RequestParam(value = "sorts", required = false)
                                                                               final List<String> sorts) {
        // Droits d'accès
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libraries);
        return new ResponseEntity<>(uiBibliographicRecordService.searchAsList(search,
                                                                              filteredLibraries,
                                                                              projects,
                                                                              lots,
                                                                              trains,
                                                                              lastModifiedDateFrom,
                                                                              lastModifiedDateTo,
                                                                              createdDateFrom,
                                                                              createdDateTo,
                                                                              orphan,
                                                                              page,
                                                                              size,
                                                                              sorts), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"dto"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(DOC_UNIT_HAB0)
    public ResponseEntity<List<SimpleBibliographicRecordDTO>> findAll() {
        final List<SimpleBibliographicRecordDTO> dtos = uiBibliographicRecordService.findAllSimpleDTO();
        // Droits d'accès
        final List<SimpleBibliographicRecordDTO> filteredDtos = filterRecordsDTOs(dtos, SimpleBibliographicRecordDTO::getIdentifier);
        return new ResponseEntity<>(filteredDtos, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"all_operations"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(DOC_UNIT_HAB0)
    public ResponseEntity<List<SimpleBibliographicRecordDTO>> findAllOperations(@RequestParam("identifier") final String docUnitId) {
        if (!accessHelper.checkDocUnit(docUnitId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(uiBibliographicRecordService.findAllSimpleDTOForDocUnit(docUnitId), HttpStatus.OK);
    }

    @RequestMapping(value = "/{identifier}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(DOC_UNIT_HAB0)
    public ResponseEntity<BibliographicRecordDTO> getById(@PathVariable final String identifier) {
        // Droits d'accès
        if (!accessHelper.checkBibliographicRecord(identifier)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return createResponseEntity(uiBibliographicRecordService.getOne(identifier));
    }

    @RequestMapping(value = "/{identifier}", method = RequestMethod.GET, params = {"dc"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(DOC_UNIT_HAB0)
    public ResponseEntity<BibliographicRecordDcDTO> getDcById(@PathVariable final String identifier) {
        if (!accessHelper.checkBibliographicRecord(identifier)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return createResponseEntity(uiBibliographicRecordService.getOneDc(identifier));
    }

    @RequestMapping(value = "/{identifier}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @Timed
    @RolesAllowed(DOC_UNIT_HAB2)
    public ResponseEntity<BibliographicRecordDTO> update(final HttpServletRequest request, @RequestBody final BibliographicRecordDTO recordDTO) throws
                                                                                                                                                PgcnException,
                                                                                                                                                PgcnLockException {
        // Droits d'accès
        if (!accessHelper.checkBibliographicRecord(recordDTO.getIdentifier()) 
                || !libraryAccesssHelper.checkLibrary(request, recordDTO.getLibrary().getIdentifier())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final BibliographicRecord record = bibliographicRecordService.getOne(recordDTO.getIdentifier());
        // Non trouvé
        if (record == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Verrou
        lockService.checkLock(record);  // throws PgcnLockException
        // # 1864 : pas de controle de droit / workflow

        // Mise à jour
        final BibliographicRecordDTO savedRecord = uiBibliographicRecordService.update(recordDTO);
        esBibliographicRecordService.indexAsync(savedRecord.getIdentifier());
        return new ResponseEntity<>(savedRecord, HttpStatus.OK);
    }

    @RequestMapping(params = {"update"}, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @Timed
    @RolesAllowed(DOC_UNIT_HAB2)
    public void update(@RequestBody final BibliographicRecordMassUpdateDTO updates) throws PgcnException {
        // Droits d'accès aux notices
        final Collection<BibliographicRecord> filteredRecords = accessHelper.filterBibliographicRecords(updates.getRecordIds());
        // #1864 : pas de controle de droit / workflow.
        final Collection<BibliographicRecord> filteredByWorkflow = new ArrayList<>(filteredRecords);

        updates.setRecordIds(filteredByWorkflow.stream().map(BibliographicRecord::getIdentifier).collect(Collectors.toList()));
        final List<BibliographicRecord> savedRecords = bibliographicRecordService.update(updates);
        esBibliographicRecordService.indexAsync(savedRecords.stream().map(BibliographicRecord::getIdentifier).collect(Collectors.toList()));
    }

    @RequestMapping(value = "/{identifier}", method = RequestMethod.GET, params = {"lock"})
    @Timed
    @RolesAllowed(DOC_UNIT_HAB2)
    public void lock(final HttpServletResponse response, @PathVariable final String identifier) throws PgcnLockException {
        final BibliographicRecord record = bibliographicRecordService.getOne(identifier);
        // pas trouvé
        if (record == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        // pas autorisé
        if (!accessHelper.checkDocUnit(identifier)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        lockService.acquireLock(record);
    }

    @RequestMapping(value = "/{identifier}", method = RequestMethod.GET, params = {"unlock"})
    @Timed
    @RolesAllowed(DOC_UNIT_HAB2)
    public void unlock(final HttpServletResponse response, @PathVariable final String identifier) throws PgcnLockException {
        final BibliographicRecord record = bibliographicRecordService.getOne(identifier);
        // pas trouvé
        if (record == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        // pas autorisé
        if (!accessHelper.checkDocUnit(identifier)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        lockService.releaseLock(record);
    }

    /**
     * Filtrage d'une liste de LotDTO sur les droits d'accès de l'utilisateur.
     *
     * @param records
     * @return
     */
    private <T> List<T> filterRecordsDTOs(final Collection<T> records, final Function<T, String> getIdentifierFn) {
        return accessHelper.filterBibliographicRecords(records.stream().map(getIdentifierFn).collect(Collectors.toList()))
                           .stream()
                           // Correspondance record autorisé => recordDTO
                           .map(doc -> records.stream().filter(d -> StringUtils.equals(getIdentifierFn.apply(d), doc.getIdentifier())).findAny())
                           .filter(Optional::isPresent)
                           .map(Optional::get)
                           .collect(Collectors.toList());
    }
}
