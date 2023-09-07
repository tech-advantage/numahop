package fr.progilone.pgcn.web.rest.exchange;

import static fr.progilone.pgcn.web.rest.exchange.security.AuthorizationConstants.EXC_HAB0;
import static fr.progilone.pgcn.web.rest.exchange.security.AuthorizationConstants.EXC_HAB1;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.exchange.ImportReport;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.service.exchange.ImportReportService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contrôleur gérant les rapports d'exécution d'imports d'unités documentaires
 * <p>
 * Created by Sébastien on 15/12/2016.
 */
@RestController
@RequestMapping(value = "/api/rest/importreport")
public class ImportReportController extends AbstractRestController {

    private final ImportReportService importReportService;
    private final LibraryAccesssHelper libraryAccesssHelper;

    @Autowired
    public ImportReportController(final ImportReportService importReportService, final LibraryAccesssHelper libraryAccesssHelper) {
        this.importReportService = importReportService;
        this.libraryAccesssHelper = libraryAccesssHelper;
    }

    @RequestMapping(method = RequestMethod.GET, params = {"search"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({EXC_HAB0})
    public ResponseEntity<Page<ImportReport>> search(final HttpServletRequest request,
                                                     @RequestParam(value = "search", required = false) final String search,
                                                     @RequestParam(value = "users", required = false) final List<String> users,
                                                     @RequestParam(value = "status", required = false) final List<ImportReport.Status> status,
                                                     @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                                                     @RequestParam(value = "size", required = false, defaultValue = "10") final Integer size) {
        final List<String> libraries = libraryAccesssHelper.getLibraryFilter(request, null);
        return new ResponseEntity<>(importReportService.search(search, users, status, libraries, page, size), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({EXC_HAB0})
    public ResponseEntity<Page<ImportReport>> findAll(final HttpServletRequest request,
                                                      @RequestParam(value = "page", defaultValue = "0") final int page,
                                                      @RequestParam(value = "size", defaultValue = "10") final int size,
                                                      @RequestParam(value = "library", required = false) final Library library) {
        final List<String> libIds = library != null ? Collections.singletonList(library.getIdentifier())
                                                    : null;
        final List<String> filteredLibraries = libraryAccesssHelper.getLibraryFilter(request, libIds);

        // Chargement de la page de résultat
        return new ResponseEntity<>(importReportService.findAllByLibraryIn(filteredLibraries, page, size), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({EXC_HAB0})
    public ResponseEntity<ImportReport> findOne(final HttpServletRequest request, @PathVariable("id") final String identifier) {
        final ImportReport importReport = importReportService.findByIdentifier(identifier);
        // Non trouvé
        if (importReport == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur
        if (!libraryAccesssHelper.checkLibrary(request, importReport, ImportReport::getLibrary)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Réponse
        return new ResponseEntity<>(importReport, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", params = {"status"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({EXC_HAB0})
    public ResponseEntity<Map<String, Object>> getStatus(final HttpServletRequest request, @PathVariable("id") final String identifier) {
        final ImportReport importReport = importReportService.findByIdentifier(identifier);
        // Non trouvé
        if (importReport == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur
        if (!libraryAccesssHelper.checkLibrary(request, importReport, ImportReport::getLibrary)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Réponse
        final Map<String, Object> response = importReportService.getStatus(importReport);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Suppression d'un rapport
     *
     * @param id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Timed
    @RolesAllowed({EXC_HAB1})
    public ResponseEntity<?> delete(final HttpServletRequest request, @PathVariable final String id) {
        // Chargement
        final ImportReport importReport = importReportService.findByIdentifier(id);
        // Non trouvé
        if (importReport == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur
        if (!libraryAccesssHelper.checkLibrary(request, importReport, ImportReport::getLibrary)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Suppression
        importReportService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, params = {"hasfile"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({EXC_HAB0})
    public ResponseEntity<Map<?, ?>> hasImportFile(final HttpServletRequest request, @PathVariable("id") final String identifier) {
        final ImportReport importReport = importReportService.findByIdentifier(identifier);
        // Non trouvé
        if (importReport == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur
        if (!libraryAccesssHelper.checkLibrary(request, importReport, ImportReport::getLibrary)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Réponse
        final Map<String, Boolean> result = importReport.getFiles()
                                                        .stream()
                                                        // Regroupement par originalFilename
                                                        .collect(Collectors.toMap(ImportReport.ImportedFile::getOriginalFilename,
                                                                                  // Test d'existence du fichier
                                                                                  f -> {
                                                                                      final File importFile = importReportService.getImportFile(importReport,
                                                                                                                                                f.getOriginalFilename());
                                                                                      return importFile != null && importFile.exists();
                                                                                  }));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, params = {"file"}, produces = MediaType.TEXT_PLAIN_VALUE)
    @RolesAllowed({EXC_HAB0})
    public ResponseEntity<?> downloadImportFile(final HttpServletRequest request,
                                                final HttpServletResponse response,
                                                @PathVariable("id") final String reportId,
                                                @RequestParam("file") final String originalFilename) throws PgcnTechnicalException {
        final ImportReport importReport = importReportService.findByIdentifier(reportId);
        // Non trouvé
        if (importReport == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur
        if (!libraryAccesssHelper.checkLibrary(request, importReport, ImportReport::getLibrary)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Réponse
        final File importFile = importReportService.getImportFile(importReport, originalFilename);
        // Non trouvé
        if (importFile == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            writeResponseForDownload(response, importFile, MediaType.TEXT_PLAIN_VALUE, originalFilename);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
