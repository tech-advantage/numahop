package fr.progilone.pgcn.domain.document;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.administration.CinesPAC;
import fr.progilone.pgcn.domain.administration.InternetArchiveCollection;
import fr.progilone.pgcn.domain.administration.omeka.OmekaList;
import fr.progilone.pgcn.domain.check.AutomaticCheckResult;
import fr.progilone.pgcn.domain.exchange.cines.CinesReport;
import fr.progilone.pgcn.domain.exchange.internetarchive.InternetArchiveReport;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.domain.ocrlangconfiguration.OcrLanguage;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.domain.workflow.DocUnitWorkflow;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.hibernate.Hibernate;
import org.hibernate.LazyInitializationException;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe représentant une unité documentaire
 *
 * @author Jonathan
 */
// Hibernate
@Entity
@Table(name = DocUnit.TABLE_NAME)
// Jackson
@JsonSubTypes({@JsonSubTypes.Type(name = "doc_unit", value = DocUnit.class)})
// Audit
@AuditTable(value = DocUnit.AUDIT_TABLE_NAME)
public class DocUnit extends AbstractDomainObject {

    private static final Logger LOG = LoggerFactory.getLogger(DocUnit.class);

    public static final String TABLE_NAME = "doc_unit";
    public static final String AUDIT_TABLE_NAME = "aud_doc_unit";

    /**
     * Bibliothèque de rattachement
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "library")
    private Library library;

    /**
     * Projet de rattachement
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project")
    private Project project;

    /**
     * Lot de rattachement
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot")
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED) // l'id du lot (champ de docunit) est audité, mais pas le lot lui-même
    private Lot lot;

    /**
     * Liste des propriétés de l'unité documentaire regroupés dans une notice (DC, DCq, Custom)
     */
    @OneToMany(mappedBy = "docUnit", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private final Set<BibliographicRecord> records = new HashSet<>();

    @OneToOne(mappedBy = "docUnit", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private ExportData exportData;

    @OneToOne(mappedBy = "docUnit", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private ArchiveItem archiveItem;

    /**
     * Liste des documents physiques rattachés
     */
    @OneToMany(mappedBy = "docUnit", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private final Set<PhysicalDocument> physicalDocuments = new HashSet<>();

    /**
     * Liste des documents numériques rattachés
     */
    @OneToMany(mappedBy = "docUnit", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private final Set<DigitalDocument> digitalDocuments = new HashSet<>();

    /**
     * Liste des résultats de contrôles automatiques associés
     */
    @OneToMany(mappedBy = "docUnit", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private final Set<AutomaticCheckResult> automaticCheckResults = new HashSet<>();

    /**
     * identifiant PGCN
     */
    @Column(name = "pgcn_id", unique = true)
    private String pgcnId;

    /**
     * Label
     */
    @Column(name = "label")
    private String label;

    /**
     * Type (monographie, etc...)
     */
    @Column(name = "type")
    private String type;

    /**
     * Nom de la collection Internet Archive pour la diffusion
     */
    @ManyToOne
    @JoinColumn(name = "collection_ia")
    private InternetArchiveCollection collectionIA;

    /**
     * Collections Omeka pour la diffusion.
     */
    @ManyToOne
    @JoinColumn(name = "collection_omeka")
    private OmekaList omekaCollection;

    /**
     * Items Omeka pour la diffusion.
     */
    @ManyToOne
    @JoinColumn(name = "items_omeka")
    private OmekaList omekaItem;

    /**
     * Identifiant ARK
     */
    @Column(name = "ark_url")
    private String arkUrl;

    /**
     * Plan de classement PAC
     */
    @ManyToOne
    @JoinColumn(name = "classement_pac")
    private CinesPAC planClassementPAC;

    /**
     * Numéro de livraison PAC
     */
    @Column(name = "cines_version")
    private Integer cinesVersion;

    /**
     * Possibilité d'archivage
     */
    @Column(name = "archivable")
    private boolean archivable;

    /**
     * Possibilité de diffusion
     */
    @Column(name = "distributable")
    private boolean distributable;

    /**
     * Droits
     */
    @Column
    @Enumerated(EnumType.STRING)
    private RightsEnum rights;

    /**
     * Date d'embargo
     */
    @Column
    private LocalDate embargo;

    /**
     * Délai de contrôle après livraison (en jours)
     */
    @Column(name = "check_delay")
    private Integer checkDelay;

    /**
     * Date de fin de contrôle prévue
     */
    @Column(name = "check_end_time")
    private LocalDate checkEndTime;

    /**
     * Type de constat d'état
     */
    @Column(name = "condreport_type")
    @Enumerated(EnumType.STRING)
    private CondReportType condReportType;

    /**
     * Unité documentaire parente
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent")
    private DocUnit parent;

    /**
     * Liste des unités documentaires enfants
     */
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private final List<DocUnit> children = new ArrayList<>();

    /**
     * Unités documentaires soeurs
     */
    @ManyToOne(fetch = FetchType.LAZY,
               cascade = {CascadeType.MERGE,
                          CascadeType.PERSIST})
    @JoinColumn(name = "sibling")
    private DocSibling sibling;

    /**
     * État de l'unité documentaire
     */
    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    private State state = State.AVAILABLE;

    @Column(name = "canceling_comment")
    private String cancelingComment;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "workflow")
    private DocUnitWorkflow workflow;

    /**
     * Notes de numérisation.
     */
    @Column(name = "digitizing_notes")
    private String digitizingNotes;

    /**
     * Reference de l'auteur ou ayant-droits trouvee: oui/non.
     */
    @Column(name = "found_ref_author")
    private boolean foundRefAuthor;

    /**
     * Etat de la demande.
     */
    @Column(name = "progress_request_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ProgressStatus progressStatus = ProgressStatus.NOT_AVAILABLE;

    /**
     * Date de la demande.
     */
    @Column(name = "request_date")
    private LocalDate requestDate;

    /**
     * Date de la réponse.
     */
    @Column(name = "answer_date")
    private LocalDate answerDate;

    /**
     * Infos export Omeka
     */
    @Column(name = "omeka_exp_status")
    @Enumerated(EnumType.STRING)
    private ExportStatus omekaExportStatus;

    @Column(name = "omeka_exp_date")
    private LocalDateTime omekaExportDate;

    /**
     * Infos export local
     */
    @Column(name = "local_exp_status")
    @Enumerated(EnumType.STRING)
    private ExportStatus localExportStatus;

    @Column(name = "local_exp_date")
    private LocalDateTime localExportDate;

    /**
     * Infos diffusion bibliothèque numérique
     */
    @Column(name = "dig_lib_export_status")
    @Enumerated(EnumType.STRING)
    private ExportStatus digLibExportStatus;

    @Column(name = "dig_lib_export_date")
    private LocalDateTime digLibExportDate;

    /** langage selectionné pour ocr */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "active_ocr_lang")
    private OcrLanguage activeOcrLanguage;

    @OneToMany(mappedBy = "docUnit", fetch = FetchType.LAZY)
    private Set<CinesReport> cinesReports;

    @OneToMany(mappedBy = "docUnit", fetch = FetchType.LAZY)
    private Set<InternetArchiveReport> iaReports;

    @Column(name = "image_height")
    private Integer imageHeight;

    @Column(name = "image_width")
    private Integer imageWidth;

    public Library getLibrary() {
        return library;
    }

    public void setLibrary(final Library library) {
        this.library = library;
    }

    public String getPgcnId() {
        return pgcnId;
    }

    public void setPgcnId(final String pgcnId) {
        this.pgcnId = pgcnId;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public InternetArchiveCollection getCollectionIA() {
        if (collectionIA != null) {
            return collectionIA;
        }
        // TODO: gérer l'initialisation au niveau du service, ou au moins pas dans le getter
        try {
            if (!Hibernate.isInitialized(lot)) {
                Hibernate.initialize(lot);
            }
            if (getLot() == null) {
                return null;
            }
            return getLot().getCollectionIA();
        } catch (final LazyInitializationException e) {
            LOG.warn("Problème d'initialisation: {}", e.getMessage());
            return null;
        }
    }

    public void setCollectionIA(final InternetArchiveCollection collectionIA) {
        this.collectionIA = collectionIA;
    }

    public boolean isArchivable() {
        return archivable;
    }

    public void setArchivable(final boolean archivable) {
        this.archivable = archivable;
    }

    public boolean isDistributable() {
        return distributable;
    }

    public void setDistributable(final boolean distributable) {
        this.distributable = distributable;
    }

    public RightsEnum getRights() {
        return rights;
    }

    public void setRights(final RightsEnum rights) {
        this.rights = rights;
    }

    public LocalDate getEmbargo() {
        return embargo;
    }

    public void setEmbargo(final LocalDate embargo) {
        this.embargo = embargo;
    }

    public Integer getCheckDelay() {
        return checkDelay;
    }

    public void setCheckDelay(final Integer checkDelay) {
        this.checkDelay = checkDelay;
    }

    public LocalDate getCheckEndTime() {
        return checkEndTime;
    }

    public void setCheckEndTime(final LocalDate checkEndTime) {
        this.checkEndTime = checkEndTime;
    }

    public CinesPAC getPlanClassementPAC() {
        if (planClassementPAC != null) {
            return planClassementPAC;
        }
        // TODO: gérer l'initialisation au niveau du service, ou au moins pas dans le getter
        try {
            if (!Hibernate.isInitialized(lot)) {
                Hibernate.initialize(lot);
            }
            if (getLot() == null) {
                return null;
            }
            return getLot().getPlanClassementPAC();
        } catch (final LazyInitializationException e) { // NOSONAR pour pas pourrir les logs car c'est qd mm souvent que ....
            LOG.warn("Problème d'initialisation: {}", e.getMessage());
            return null;
        }
    }

    public void setPlanClassementPAC(final CinesPAC planClassementPAC) {
        this.planClassementPAC = planClassementPAC;
    }

    public Set<BibliographicRecord> getRecords() {
        return records;
    }

    public void setRecords(final Set<BibliographicRecord> records) {
        this.records.clear();
        if (records != null) {
            records.forEach(this::addRecord);
        }
    }

    public void addRecord(final BibliographicRecord record) {
        if (record != null) {
            this.records.add(record);
            record.setDocUnit(this);
        }
    }

    public Set<PhysicalDocument> getPhysicalDocuments() {
        return physicalDocuments;
    }

    public void setPhysicalDocuments(final Set<PhysicalDocument> physicalDocuments) {
        this.physicalDocuments.clear();
        if (physicalDocuments != null) {
            physicalDocuments.forEach(this::addPhysicalDocument);
        }
    }

    public void addPhysicalDocument(final PhysicalDocument physicalDocument) {
        if (physicalDocument != null) {
            this.physicalDocuments.add(physicalDocument);
            physicalDocument.setDocUnit(this);
        }
    }

    public Set<DigitalDocument> getDigitalDocuments() {
        return digitalDocuments;
    }

    public void setDigitalDocuments(final Set<DigitalDocument> digitalDocuments) {
        this.digitalDocuments.clear();
        if (digitalDocuments != null) {
            digitalDocuments.forEach(this::addDigitalDocument);
        }
    }

    public void addDigitalDocument(final DigitalDocument digitalDocument) {
        if (digitalDocument != null) {
            this.digitalDocuments.add(digitalDocument);
            digitalDocument.setDocUnit(this);
        }
    }

    public Set<AutomaticCheckResult> getAutomaticCheckResults() {
        return automaticCheckResults;
    }

    public void setAutomaticCheckResults(final Set<AutomaticCheckResult> results) {
        this.automaticCheckResults.clear();
        if (results != null) {
            results.forEach(this::addAutomaticCheckResult);
        }
    }

    public void addAutomaticCheckResult(final AutomaticCheckResult result) {
        if (result != null) {
            this.automaticCheckResults.add(result);
            result.setDocUnit(this);
        }
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

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public State getState() {
        return state;
    }

    public void setState(final State state) {
        this.state = state;
    }

    public String getCancelingComment() {
        return cancelingComment;
    }

    public void setCancelingComment(final String cancelingComment) {
        this.cancelingComment = cancelingComment;
    }

    public CondReportType getCondReportType() {
        return condReportType;
    }

    public void setCondReportType(final CondReportType condReportType) {
        this.condReportType = condReportType;
    }

    public DocUnit getParent() {
        return parent;
    }

    public void setParent(final DocUnit parent) {
        this.parent = parent;
    }

    public List<DocUnit> getChildren() {
        return children;
    }

    public void setChildren(final List<DocUnit> children) {
        this.children.clear();
        if (children != null) {
            children.forEach(this::addChild);
        }
    }

    public void addChild(final DocUnit child) {
        if (child != null) {
            this.children.add(child);
        }
    }

    public DocUnitWorkflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(final DocUnitWorkflow workflow) {
        this.workflow = workflow;
    }

    public DocSibling getSibling() {
        return sibling;
    }

    public void setSibling(final DocSibling sibling) {
        this.sibling = sibling;
    }

    public String getArkUrl() {
        return arkUrl;
    }

    public void setArkUrl(final String arkUrl) {
        this.arkUrl = arkUrl;
    }

    public Integer getCinesVersion() {
        return cinesVersion;
    }

    public void setCinesVersion(final Integer cinesVersion) {
        this.cinesVersion = cinesVersion;
    }

    public ExportData getExportData() {
        return exportData;
    }

    public void setExportData(final ExportData exportData) {
        this.exportData = exportData;
    }

    public ArchiveItem getArchiveItem() {
        return archiveItem;
    }

    public void setArchiveItem(final ArchiveItem archiveItem) {
        this.archiveItem = archiveItem;
    }

    /**
     * @return the digitizingNotes
     */
    public String getDigitizingNotes() {
        return digitizingNotes;
    }

    /**
     * @param digitizingNotes
     *            the digitizingNotes to set
     */
    public void setDigitizingNotes(final String digitizingNotes) {
        this.digitizingNotes = digitizingNotes;
    }

    public boolean isFoundRefAuthor() {
        return foundRefAuthor;
    }

    public void setFoundRefAuthor(final boolean foundRefAuthor) {
        this.foundRefAuthor = foundRefAuthor;
    }

    public ProgressStatus getProgressStatus() {
        return progressStatus;
    }

    public void setProgressStatus(final ProgressStatus progressStatus) {
        this.progressStatus = progressStatus;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(final LocalDate requestDate) {
        this.requestDate = requestDate;
    }

    public LocalDate getAnswerDate() {
        return answerDate;
    }

    public void setAnswerDate(final LocalDate answerDate) {
        this.answerDate = answerDate;
    }

    public ExportStatus getOmekaExportStatus() {
        return omekaExportStatus;
    }

    public void setOmekaExportStatus(final ExportStatus omekaExportStatus) {
        this.omekaExportStatus = omekaExportStatus;
    }

    public LocalDateTime getOmekaExportDate() {
        return omekaExportDate;
    }

    public void setOmekaExportDate(final LocalDateTime omekaExportDate) {
        this.omekaExportDate = omekaExportDate;
    }

    public ExportStatus getLocalExportStatus() {
        return localExportStatus;
    }

    public void setLocalExportStatus(final ExportStatus localExportStatus) {
        this.localExportStatus = localExportStatus;
    }

    public LocalDateTime getLocalExportDate() {
        return localExportDate;
    }

    public void setLocalExportDate(final LocalDateTime localExportDate) {
        this.localExportDate = localExportDate;
    }

    public ExportStatus getDigLibExportStatus() {
        return digLibExportStatus;
    }

    public void setDigLibExportStatus(final ExportStatus digLibExportStatus) {
        this.digLibExportStatus = digLibExportStatus;
    }

    public LocalDateTime getDigLibExportDate() {
        return digLibExportDate;
    }

    public void setDigLibExportDate(final LocalDateTime digLibExportDate) {
        this.digLibExportDate = digLibExportDate;
    }

    public OmekaList getOmekaCollection() {
        if (omekaCollection != null) {
            return omekaCollection;
        }
        // TODO: gérer l'initialisation au niveau du service, ou au moins pas dans le getter
        try {
            if (!Hibernate.isInitialized(lot)) {
                Hibernate.initialize(lot);
            }
            if (getLot() == null) {
                return null;
            }
            return getLot().getOmekaCollection();
        } catch (final LazyInitializationException e) { // NOSONAR pour pas pourrir les logs car c'est qd mm souvent que ....
            LOG.warn("Problème d'initialisation: {}", e.getMessage());
            return null;
        }
    }

    public void setOmekaCollection(final OmekaList omekaCollection) {
        this.omekaCollection = omekaCollection;
    }

    public OmekaList getOmekaItem() {
        if (omekaItem != null) {
            return omekaItem;
        }
        // TODO: gérer l'initialisation au niveau du service, ou au moins pas dans le getter
        try {
            if (!Hibernate.isInitialized(lot)) {
                Hibernate.initialize(lot);
            }
            if (getLot() == null) {
                return null;
            }
            return getLot().getOmekaItem();
        } catch (final LazyInitializationException e) { // NOSONAR pour pas pourrir les logs car c'est qd mm souvent que ....
            LOG.warn("Problème d'initialisation: {}", e.getMessage());
            return null;
        }
    }

    public void setOmekaItem(final OmekaList omekaItem) {
        this.omekaItem = omekaItem;
    }

    public enum CondReportType {
        /**
         * Mono-feuillet
         */
        MONO_PAGE,
        /**
         * Multi-feuillet
         */
        MULTI_PAGE
    }

    /**
     * Gestion droits sur le doc.
     */
    public enum RightsEnum {
        /**
         * Droit à vérifier (défaut)
         */
        TO_CHECK,
        /**
         * Libre de droits
         */
        FREE,
        /**
         * Sous droits
         */
        RESTRICTED,
        /**
         * Sous droits avec accord de l'auteur
         */
        RESTRICTED_WITH_AUTHORIZATION
    }

    /**
     * Statuts d'import de cette unité documentaire
     */
    public enum State {
        // l'unité documentaire est visible dans l'application
        AVAILABLE,
        // l'unité documentaire n'est pas disponible dans l'application (import en cours, ...)
        NOT_AVAILABLE,
        // l'unité documentaire a été supprimée (utilisé pour les recherches d'ud importées)
        DELETED,
        // l'unite documentaire est annulée ou rattachée à un projet annulé
        CANCELED,
        // UD archivée suite cloture lot|projet.
        CLOSED
    }

    /**
     * Etat de la demande de diffusion.
     */
    public enum ProgressStatus {
        // Non disponible
        NOT_AVAILABLE,
        // Demandé
        REQUESTED,
        // Validé
        VALIDATED,
        // Refusé
        REFUSED
    }

    public enum ExportStatus {
        NONE,
        IN_PROGRESS,
        SENT,
        FAILED
    }

    public OcrLanguage getActiveOcrLanguage() {
        return activeOcrLanguage;
    }

    public void setActiveOcrLanguage(final OcrLanguage activeOcrLanguage) {
        this.activeOcrLanguage = activeOcrLanguage;
    }

    public Set<CinesReport> getCinesReports() {
        return cinesReports;
    }

    public void setCinesReports(final Set<CinesReport> cinesReports) {
        this.cinesReports = cinesReports;
    }

    public Set<InternetArchiveReport> getIaReports() {
        return iaReports;
    }

    public void setIaReports(final Set<InternetArchiveReport> iaReports) {
        this.iaReports = iaReports;
    }

    public Integer getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(Integer imageHeight) {
        this.imageHeight = imageHeight;
    }

    public Integer getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(Integer imageWidth) {
        this.imageWidth = imageWidth;
    }
}
