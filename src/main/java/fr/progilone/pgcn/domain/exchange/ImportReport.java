package fr.progilone.pgcn.domain.exchange;

import com.google.common.base.MoreObjects;
import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.domain.project.Project;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Rapport d'exécution d'un import
 * <p>
 * Created by Sebastien on 08/12/2016.
 */
@Entity
@Table(name = ImportReport.TABLE_NAME)
public class ImportReport extends AbstractDomainObject {

    public static final String TABLE_NAME = "exc_import_report";
    public static final String TABLE_NAME_FILE = "exc_import_file";

    /**
     * Statuts d'exécution de l'import
     */
    public enum Status {
        PENDING,
        // en attente de traitement
        PRE_IMPORTING,
        // pré-import des unités documentaires
        DEDUPLICATING,
        // recherche de doublons
        USER_VALIDATION,
        // attente d'une validation de l'utilisateur avant d'importer les unités documentaires
        IMPORTING,
        // indexation des entités importées
        INDEXING,
        // l'import s'est terminé avec succès
        COMPLETED,
        // Échec de l'import
        FAILED
    }

    /**
     * Types d'import
     */
    public enum Type {
        // pas de relation parent / enfant, 1 import -> 1 notice PGCN
        SIMPLE,
        // pas de relation parent / enfant, n imports -> 1 notice PGCN
        SIMPLE_MULTI_NOTICE,
        // parent / enfants dans la même notice, 1 import -> n notices PGCN
        HIERARCHY_IN_SINGLE_NOTICE,
        // parent / enfant issus de 2 imports différents, n imports -> n notices PGCN
        HIERARCHY_IN_MULTIPLE_IMPORT
    }

    /**
     * Bibliothèque qui réalise l'import
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "library", nullable = false)
    private Library library;

    /**
     * Projet dans lequel seront rattachées les unités documentaires importées
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project")
    private Project project;

    /**
     * Lot dans lequel seront rattachées les unités documentaires importées
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot")
    private Lot lot;

    /**
     * Mapping utilisé pour l'import
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mapping")
    private Mapping mapping;

    /**
     * Mapping utilisé pour l'import des périodiques
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mapping_children")
    private Mapping mappingChildren;

    /**
     * Mapping utilisé pour l'import CSV
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "csv_mapping")
    private CSVMapping csvMapping;

    /**
     * Autre type de mapping (DC par ex.)
     */
    @Column(name = "add_mapping")
    private String additionnalMapping;

    /**
     * Type d'import
     */
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    /**
     * Format du fichier d'import
     */
    @Column(name = "file_format")
    @Enumerated(EnumType.STRING)
    private FileFormat fileFormat;

    /**
     * Encodage du fichier d'import
     */
    @Column(name = "data_encoding")
    @Enumerated(EnumType.STRING)
    private DataEncoding dataEncoding;

    /**
     * Import des notices parentes
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent")
    private ImportReport parentReport;

    /**
     * Expression identifiant le parent
     */
    @Column(name = "join_expression", columnDefinition = "text")
    private String joinExpression;

    /**
     * Liste des fichiers importés
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = TABLE_NAME_FILE, joinColumns = @JoinColumn(name = "report"))
    private final List<ImportedFile> files = new ArrayList<>();

    /**
     * Date de début de l'import
     */
    @Column(name = "start_import")
    private LocalDateTime start;

    /**
     * Date de fin de l'import
     */
    @Column(name = "end_import")
    private LocalDateTime end;

    /**
     * Login de l'utilisateur ayant lancé l'import
     */
    @Column(name = "run_by")
    private String runBy;

    /**
     * Statut de l'import
     */
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    /**
     * Message détaillant le résultat de l'import
     */
    @Column(name = "message", columnDefinition = "text")
    private String message;

    /**
     * Nombre d'unités documentaires importées
     */
    @Column(name = "nb_imp")
    private Integer nbImp;

    /**
     * Unités documentaires importées
     */
    @OneToMany(mappedBy = "report", fetch = FetchType.LAZY)
    private final List<ImportedDocUnit> docUnits = new ArrayList<>();

    public Library getLibrary() {
        return library;
    }

    public void setLibrary(final Library library) {
        this.library = library;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(final Project project) {
        this.project = project;
    }

    public Lot getLot() {
        return lot;
    }

    public void setLot(final Lot lot) {
        this.lot = lot;
    }

    public Mapping getMapping() {
        return mapping;
    }

    public void setMapping(final Mapping mapping) {
        this.mapping = mapping;
    }

    public Mapping getMappingChildren() {
        return mappingChildren;
    }

    public void setMappingChildren(final Mapping mappingChildren) {
        this.mappingChildren = mappingChildren;
    }

    public CSVMapping getCsvMapping() {
        return csvMapping;
    }

    public void setCsvMapping(CSVMapping csvMapping) {
        this.csvMapping = csvMapping;
    }

    public String getAdditionnalMapping() {
        return additionnalMapping;
    }

    public void setAdditionnalMapping(final String additionnalMapping) {
        this.additionnalMapping = additionnalMapping;
    }

    public Type getType() {
        return type;
    }

    public void setType(final Type type) {
        this.type = type;
    }

    public FileFormat getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(final FileFormat fileFormat) {
        this.fileFormat = fileFormat;
    }

    public DataEncoding getDataEncoding() {
        return dataEncoding;
    }

    public void setDataEncoding(final DataEncoding dataEncoding) {
        this.dataEncoding = dataEncoding;
    }

    public List<ImportedFile> getFiles() {
        return files;
    }

    public void setFiles(final List<ImportedFile> files) {
        this.files.clear();
        if (files != null) {
            files.forEach(this::addFile);
        }
    }

    public void addFile(final ImportedFile file) {
        if (file != null) {
            this.files.add(file);
        }
    }

    public String getFilesAsString() {
        return files.stream().map(ImportedFile::getOriginalFilename).reduce((a, b) -> a + ", " + b).orElse("");
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(final LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(final LocalDateTime end) {
        this.end = end;
    }

    public String getRunBy() {
        return runBy;
    }

    public void setRunBy(final String runBy) {
        this.runBy = runBy;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(final Status status) {
        this.status = status;
    }

    public ImportReport getParentReport() {
        return parentReport;
    }

    public void setParentReport(final ImportReport parentReport) {
        this.parentReport = parentReport;
    }

    public String getJoinExpression() {
        return joinExpression;
    }

    public void setJoinExpression(final String joinExpression) {
        this.joinExpression = joinExpression;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public List<ImportedDocUnit> getDocUnits() {
        return docUnits;
    }

    public void setDocUnits(final List<ImportedDocUnit> docUnits) {
        this.docUnits.clear();
        if (docUnits != null) {
            docUnits.forEach(this::addDocUnit);
        }
    }

    public void addDocUnit(final ImportedDocUnit docUnit) {
        if (docUnit != null) {
            this.docUnits.add(docUnit);
        }
    }

    public Integer getNbImp() {
        return nbImp;
    }

    public void setNbImp(Integer nbImp) {
        this.nbImp = nbImp;
    }

    public void incrementNbImp(final int incImp) {
        if (nbImp == null || nbImp == 0) {
            nbImp = incImp;
        } else {
            nbImp += incImp;
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("files", files)
                          .add("fileFormat", fileFormat)
                          .add("dataEncoding", dataEncoding)
                          .add("start", start)
                          .add("end", end)
                          .add("runBy", runBy)
                          .add("status", status)
                          .add("nbImp", nbImp)
                          .toString();
    }

    /**
     * Fichier importé
     */
    @Embeddable
    public static class ImportedFile {

        public ImportedFile() {
        }

        public ImportedFile(final String originalFilename, final Long fileSize) {
            this.originalFilename = originalFilename;
            this.fileSize = fileSize;
        }

        /**
         * Nom du fichier importé
         */
        @Column(name = "original_filename", nullable = false)
        private String originalFilename;

        /**
         * Taille du fichier importé
         */
        @Column(name = "file_size")
        private Long fileSize;

        public String getOriginalFilename() {
            return originalFilename;
        }

        public void setOriginalFilename(final String originalFilename) {
            this.originalFilename = originalFilename;
        }

        public Long getFileSize() {
            return fileSize;
        }

        public void setFileSize(final Long fileSize) {
            this.fileSize = fileSize;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this).add("originalFilename", originalFilename).add("fileSize", fileSize).toString();
        }
    }
}
