package fr.progilone.pgcn.web.rest.exchange;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.exchange.DataEncoding;
import fr.progilone.pgcn.domain.exchange.FileFormat;
import fr.progilone.pgcn.domain.exchange.ImportReport;
import fr.progilone.pgcn.domain.exchange.ImportedDocUnit;
import fr.progilone.pgcn.service.exchange.ImportReportService;
import fr.progilone.pgcn.service.exchange.csv.ImportCSVService;
import fr.progilone.pgcn.service.exchange.dc.ImportDcService;
import fr.progilone.pgcn.service.exchange.ead.ImportEadService;
import fr.progilone.pgcn.service.exchange.marc.ImportMarcService;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static fr.progilone.pgcn.web.rest.exchange.security.AuthorizationConstants.*;

/**
 * Contrôleur gérant l'import d'unités documentaires
 * <p>
 * Created by Sebastien on 22/11/2016.
 */
@RestController
@RequestMapping(value = "/api/rest/import")
public class ImportController {

    private static final Logger LOG = LoggerFactory.getLogger(ImportController.class);

    private final ImportDcService importDcService;
    private final ImportEadService importEadService;
    private final ImportMarcService importMarcService;
    private final ImportReportService importReportService;
    private final ImportCSVService importCSVService;
    private final LibraryAccesssHelper libraryAccesssHelper;

    @Autowired
    public ImportController(final ImportDcService importDcService,
                            final ImportEadService importEadService,
                            final ImportMarcService importMarcService,
                            final ImportReportService importReportService,
                            final ImportCSVService importCSVService,
                            final LibraryAccesssHelper libraryAccesssHelper) {
        this.importDcService = importDcService;
        this.importEadService = importEadService;
        this.importMarcService = importMarcService;
        this.importReportService = importReportService;
        this.importCSVService = importCSVService;
        this.libraryAccesssHelper = libraryAccesssHelper;
    }

    /**
     * Import d'un fichier de notices
     *
     * @param request
     *         injection de HttpServletRequest
     * @param type
     *         type d'import
     * @param files
     *         fichiers issus du formulaire
     * @param fileFormat
     *         format du fichier
     * @param dataEncoding
     *         encodage du fichier
     * @param libraryId
     *         identifiant de la bibliothèque pour laquelle s'effectue l'import
     * @param mappingId
     *         identifiant du mapping à utiliser pour importer le fichier, ou mapping DC (constante)
     * @param mappingChildrenId
     *         optionnel, identifiant du mapping à utiliser pour importer les exemplaires des périodiques
     * @param projectId
     *         identifiant du projet auquel rattacher les notices importées
     * @param lotId
     *         identifiant du lot auquel rattacher les notices importées
     * @param join
     *         critère identifiant la notice parente
     * @param stepValidation
     *         étape de validation par l'utilisateur;
     *         si false: les unités documentaires sont disponibles dès la fin de l'import,
     *         et les actions de dédoublonnages sont appliquées automatiquement
     * @param stepDeduplication
     *         étape de recherche de doublons
     * @param defaultDedupProcess
     *         action à effectuer en cas de dédoublonnage
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, params = "mapping", produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({EXC_HAB2})
    public ResponseEntity<ImportReport> importFile(final HttpServletRequest request,
                                                   @RequestParam(value = "file") final List<MultipartFile> files,
                                                   @RequestParam(value = "type", defaultValue = "SIMPLE") final ImportReport.Type type,
                                                   @RequestParam(value = "format") final FileFormat fileFormat,
                                                   @RequestParam(value = "encoding", required = false, defaultValue = "UTF_8")
                                                   final DataEncoding dataEncoding,
                                                   @RequestParam(value = "mapping") final String mappingId,
                                                   @RequestParam(value = "mappingChildren", required = false) final String mappingChildrenId,
                                                   @RequestParam(value = "parent", required = false) final String parentReportId,
                                                   @RequestParam(value = "library") final String libraryId,
                                                   @RequestParam(value = "project", required = false) final String projectId,
                                                   @RequestParam(value = "lot", required = false) final String lotId,
                                                   @RequestParam(value = "join", required = false) final String join,
                                                   @RequestParam(value = "validation", defaultValue = "false") final boolean stepValidation,
                                                   @RequestParam(value = "dedup", defaultValue = "false") final boolean stepDeduplication,
                                                   @RequestParam(value = "dedupProcess", required = false)
                                                   final ImportedDocUnit.Process defaultDedupProcess,
                                                   @RequestParam(value = "archivable", defaultValue = "false") final boolean archivable,
                                                   @RequestParam(value = "distributable", defaultValue = "false") final boolean distributable,
                                                   @RequestParam(value = "prop_order", required = false, defaultValue = "BY_PROPERTY_TYPE")
                                                   final BibliographicRecord.PropertyOrder propertyOrder) {
        // Vérification des droits d'accès par rapport à la bibliothèque demandée
        if (!libraryAccesssHelper.checkLibrary(request, libraryId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        // Création du rapport d'import
        final ImportReport importReport;
        switch (fileFormat) {
            case CSV:
                importReport = importReportService.createCSVImportReport(files,
                                                                         type,
                                                                         fileFormat,
                                                                         dataEncoding,
                                                                         libraryId,
                                                                         projectId,
                                                                         lotId,
                                                                         mappingId,
                                                                         parentReportId,
                                                                         join);
                break;
            case DC:
            case DCQ:
                importReport =
                    importReportService.createDCImportReport(files, type, fileFormat, dataEncoding, libraryId, projectId, lotId, mappingId, join);
                break;
            default:
                importReport = importReportService.createImportReport(files,
                                                                      type,
                                                                      fileFormat,
                                                                      dataEncoding,
                                                                      libraryId,
                                                                      projectId,
                                                                      lotId,
                                                                      mappingId,
                                                                      mappingChildrenId,
                                                                      parentReportId,
                                                                      join);
        }

        // Copie locale des fichiers à importer
        final List<File> importFiles = new ArrayList<>();

        for (final MultipartFile file : files) {
            final String fileName = new File(file.getOriginalFilename()).getName();

            LOG.info("Préparation de l'import du fichier {} (format {}, encodage {}, taille {})", fileName, fileFormat, dataEncoding, file.getSize());

            if (file.getSize() > 0) {
                try (InputStream in = file.getInputStream()) {
                    final File importFile = importReportService.saveImportFile(in, importReport, fileName);
                    importFiles.add(importFile);
                    LOG.debug("Copie de {} vers le fichier {}", fileName, importFile.getAbsolutePath());

                } catch (final IOException e) {
                    LOG.error(e.getMessage(), e);
                    importReportService.failReport(importReport, e);
                    return new ResponseEntity<>(importReport, HttpStatus.INTERNAL_SERVER_ERROR);
                }
                // Multi-import limité au format MARC
                if (fileFormat != FileFormat.MARC && fileFormat != FileFormat.MARCJSON && fileFormat != FileFormat.MARCXML) {
                    break;
                }

            } else {
                LOG.warn("L'import du fichier {} (format {}, encodage {}) est annulé car il est vide", fileName, fileFormat, dataEncoding);
            }
        }
        // Les fichiers sont ok pour l'import ?
        if (importFiles.isEmpty()) {
            importReportService.failReport(importReport, "Aucune donnée à importer: " + importReport.getFilesAsString());
            return new ResponseEntity<>(importReport, HttpStatus.BAD_REQUEST);
        }

        // Import des fichiers (asynchrone)
        try {
            switch (fileFormat) {
                case DC:
                case DCQ:
                    importDcService.importDcAsync(importFiles.get(0),
                                                  fileFormat,
                                                  libraryId,
                                                  mappingId,
                                                  importReport,
                                                  stepValidation,
                                                  stepDeduplication,
                                                  stepDeduplication ? defaultDedupProcess : null,
                                                  archivable,
                                                  distributable);
                    break;
                case EAD:
                    importEadService.importEadAsync(importFiles.get(0),
                                                    mappingId,
                                                    mappingChildrenId,
                                                    importReport,
                                                    stepValidation,
                                                    stepDeduplication,
                                                    stepDeduplication ? defaultDedupProcess : null,
                                                    propertyOrder,
                                                    archivable,
                                                    distributable);
                    break;
                case MARC:
                case MARCJSON:
                case MARCXML:
                    importMarcService.importMarcAsync(importFiles,
                                                      fileFormat,
                                                      dataEncoding,
                                                      mappingId,
                                                      mappingChildrenId,
                                                      parentReportId,
                                                      join,
                                                      importReport,
                                                      stepValidation,
                                                      stepDeduplication,
                                                      stepDeduplication ? defaultDedupProcess : null,
                                                      archivable,
                                                      distributable);
                    break;
                case CSV:
                    importCSVService.importCSVAsync(importFiles.get(0),
                                                    fileFormat,
                                                    mappingId,
                                                    parentReportId,
                                                    join,
                                                    importReport,
                                                    stepValidation,
                                                    stepDeduplication,
                                                    stepDeduplication ? defaultDedupProcess : null,
                                                    archivable,
                                                    distributable);
                    break;
                default:
                    LOG.error("Le format de fichier {} n'est pas supporté", fileFormat);
                    importReportService.failReport(importReport, "Le format de fichier " + fileFormat + " n'est pas supporté");
                    return new ResponseEntity<>(importReport, HttpStatus.BAD_REQUEST);
            }
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
            importReportService.failReport(importReport, e);
            return new ResponseEntity<>(importReport, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(importReport, HttpStatus.OK);
    }

    /**
     * Traitement des unités documentaire pré-importées
     *
     * @param report
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, params = "import", produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({EXC_HAB2})
    public ResponseEntity<ImportReport> importPreimportedDocUnits(final HttpServletRequest request,
                                                                  @RequestBody final ImportReport report,
                                                                  @RequestParam(name = "defaultProcess", defaultValue = "ADD")
                                                                  final ImportedDocUnit.Process defaultProcess,
                                                                  @RequestParam(name = "dedupProcess", defaultValue = "ADD")
                                                                  final ImportedDocUnit.Process defaultDedupProcess) {
        final ImportReport dbReport = importReportService.findByIdentifier(report.getIdentifier());
        // Non trouvé
        if (dbReport == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur
        if (!libraryAccesssHelper.checkLibrary(request, dbReport, ImportReport::getLibrary)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Poursuite de l'import
        final ImportReport runningReport = importReportService.setReportStatus(report, ImportReport.Status.IMPORTING);
        try {
            importMarcService.processPreimportedDocUnitsAsync(runningReport, defaultProcess, defaultDedupProcess);
            return new ResponseEntity<>(runningReport, HttpStatus.OK);

        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
            importReportService.failReport(runningReport, e);
            return new ResponseEntity<>(runningReport, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
