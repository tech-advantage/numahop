package fr.progilone.pgcn.web.rest.exchange.z3950;

import static fr.progilone.pgcn.web.rest.exchange.security.AuthorizationConstants.*;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.dto.exchange.Z3950RecordDTO;
import fr.progilone.pgcn.domain.exchange.DataEncoding;
import fr.progilone.pgcn.domain.exchange.FileFormat;
import fr.progilone.pgcn.domain.exchange.ImportReport;
import fr.progilone.pgcn.domain.exchange.ImportedDocUnit;
import fr.progilone.pgcn.exception.PgcnBusinessException;
import fr.progilone.pgcn.service.exchange.ImportReportService;
import fr.progilone.pgcn.service.exchange.marc.ImportMarcService;
import fr.progilone.pgcn.service.exchange.z3950.Z3950Service;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contrôleur gérant l'import Z3950
 */
@RestController
@RequestMapping(value = "/api/rest/z3950")
public class Z3950Controller extends AbstractRestController {

    private static final Logger LOG = LoggerFactory.getLogger(Z3950Controller.class);

    private final LibraryAccesssHelper libraryAccesssHelper;
    private final ImportMarcService importMarcService;
    private final ImportReportService importReportService;
    private final Z3950Service z3950Service;

    @Autowired
    public Z3950Controller(final LibraryAccesssHelper libraryAccesssHelper,
                           final ImportMarcService importMarcService,
                           final ImportReportService importReportService,
                           final Z3950Service z3950Service) {
        this.libraryAccesssHelper = libraryAccesssHelper;
        this.importMarcService = importMarcService;
        this.importReportService = importReportService;
        this.z3950Service = z3950Service;
    }

    /**
     * Recherche de notices Z39.50
     *
     * @param fields
     *            Champs pour la recherche
     * @param servers
     *            Cibles de recherches
     * @return
     * @throws fr.progilone.pgcn.exception.PgcnBusinessException
     */
    @RequestMapping(method = RequestMethod.POST, params = {"server"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({EXC_HAB3})
    public ResponseEntity<Page<Z3950RecordDTO>> search(@RequestBody final Map<String, String> fields,
                                                       @RequestParam(value = "server") final List<String> servers,
                                                       @RequestParam(value = "page", defaultValue = "0") final int page,
                                                       @RequestParam(value = "size", defaultValue = "10") final int size) throws PgcnBusinessException {
        return new ResponseEntity<>(z3950Service.search(fields, servers, page, size), HttpStatus.OK);
    }

    /**
     * Import d'une notice issue d'une recherche Z39.50
     *
     * @param z3950ResultDTO
     *            résultat de recherche Z39.50 à importer
     * @param libraryId
     *            identifiant de la bibliothèque pour laquelle s'effectue l'import
     * @param mappingId
     *            identifiant du mapping à utiliser pour importer le fichier
     * @param stepValidation
     *            étape de validation par l'utilisateur;
     *            si false: les unités documentaires sont disponibles dès la fin de l'import,
     *            et les actions de dédoublonnages sont appliquées automatiquement
     * @param stepDeduplication
     *            étape de recherche de doublons
     * @param defaultDedupProcess
     *            action à effectuer en cas de dédoublonnage
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, params = "mapping", produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({EXC_HAB2})
    public ResponseEntity<ImportReport> importFile(final HttpServletRequest request,
                                                   @RequestBody final Z3950RecordDTO z3950ResultDTO,
                                                   @RequestParam(value = "library") final String libraryId,
                                                   @RequestParam(value = "project", required = false) final String projectId,
                                                   @RequestParam(value = "lot", required = false) final String lotId,
                                                   @RequestParam(value = "mapping") final String mappingId,
                                                   @RequestParam(value = "validation", defaultValue = "false") final boolean stepValidation,
                                                   @RequestParam(value = "dedup", defaultValue = "false") final boolean stepDeduplication,
                                                   @RequestParam(value = "dedupProcess", required = false) final ImportedDocUnit.Process defaultDedupProcess) {
        // Vérification des droits d'accès par rapport à la bibliothèque demandée
        if (!libraryAccesssHelper.checkLibrary(request, libraryId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final ImportReport importReport = createImportReport(z3950ResultDTO, libraryId, projectId, lotId, mappingId);
        final String marcXml = z3950ResultDTO.getMarcXml(); // UTF-8
        final String z3950Name = z3950ResultDTO.getZ3950Server().getName();

        if (marcXml != null && !marcXml.isEmpty()) {
            LOG.info("Import depuis le serveur Z39.50 {} (format {}, encodage {}, taille {})",
                     z3950Name,
                     importReport.getFileFormat(),
                     importReport.getDataEncoding(),
                     importReport.getFiles().get(0).getFileSize());

            try {
                // Copie du fichier importé sur le serveur
                final File importFile;
                try (InputStream in = new ByteArrayInputStream(marcXml.getBytes(StandardCharsets.UTF_8))) {
                    importFile = importReportService.saveImportFile(in, importReport, importReport.getFiles().get(0).getOriginalFilename());
                    LOG.debug("Copie depuis le serveur Z39.50 {} vers le fichier {}", z3950Name, importFile.getAbsolutePath());
                }
                importMarcService.importMarcXmlAsync(Collections.singletonList(importFile),
                                                     mappingId,
                                                     importReport,
                                                     stepValidation,
                                                     stepDeduplication,
                                                     stepDeduplication ? defaultDedupProcess
                                                                       : null,
                                                     false,
                                                     false);
                return new ResponseEntity<>(importReport, HttpStatus.OK);

            } catch (final Exception e) {
                LOG.error(e.getMessage(), e);
                importReportService.failReport(importReport, e);
                return new ResponseEntity<>(importReport, HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } else {
            LOG.info("L'import depuis le serveur Z39.50 {} (format {}, encodage {}) est annulé car il est vide", z3950Name, FileFormat.MARCXML, importReport.getDataEncoding());
            importReportService.failReport(importReport,
                                           "Aucun contenu reçu du serveur Z39.50 " + z3950ResultDTO.getZ3950Server().getName()
                                                         + "("
                                                         + z3950ResultDTO.getZ3950Server().getIdentifier()
                                                         + ")");
            return new ResponseEntity<>(importReport, HttpStatus.NO_CONTENT);
        }
    }

    private ImportReport createImportReport(final Z3950RecordDTO z3950RecordDTO, final String libraryId, final String projectId, final String lotId, final String mappingId) {

        final String fileName = "Z3950-" + z3950RecordDTO.getZ3950Server().getName()
                                + ".marcxml";
        final Long fileSize = (long) z3950RecordDTO.getMarcXml().length();
        return importReportService.createSimpleImportReport(fileName, fileSize, FileFormat.MARCXML, DataEncoding.UTF_8, libraryId, projectId, lotId, mappingId);
    }
}
