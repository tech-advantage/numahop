package fr.progilone.pgcn.domain.document;

import static fr.progilone.pgcn.service.es.EsConstant.ANALYZER_CI_AI;
import static fr.progilone.pgcn.service.es.EsConstant.ANALYZER_CI_AS;
import static fr.progilone.pgcn.service.es.EsConstant.ANALYZER_KEYWORD;
import static fr.progilone.pgcn.service.es.EsConstant.ANALYZER_PHRASE;
import static fr.progilone.pgcn.service.es.EsConstant.SUBFIELD_CI_AI;
import static fr.progilone.pgcn.service.es.EsConstant.SUBFIELD_CI_AS;
import static fr.progilone.pgcn.service.es.EsConstant.SUBFIELD_PHRASE;
import static fr.progilone.pgcn.service.es.EsConstant.SUBFIELD_RAW;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.Hibernate;
import org.hibernate.LazyInitializationException;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.MultiField;

import com.fasterxml.jackson.annotation.JsonSubTypes;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.CompletionContext;
import fr.progilone.pgcn.domain.administration.CinesPAC;
import fr.progilone.pgcn.domain.administration.InternetArchiveCollection;
import fr.progilone.pgcn.domain.administration.omeka.OmekaList;
import fr.progilone.pgcn.domain.check.AutomaticCheckResult;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.domain.ocrlangconfiguration.OcrLanguage;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.domain.workflow.DocUnitWorkflow;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;

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
// Elasticsearch
@Document(indexName = "#{elasticsearchIndexName}", type = DocUnit.ES_TYPE, createIndex = false)
public class DocUnit extends AbstractDomainObject {

    private static final Logger LOG = LoggerFactory.getLogger(DocUnit.class);

    public static final String TABLE_NAME = "doc_unit";
    public static final String ES_TYPE = "doc_unit";
    public static final String AUDIT_TABLE_NAME = "aud_doc_unit";

    /**
     * Bibliothèque de rattachement
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "library")
    @Field(type = FieldType.Object)
    private Library library;

    /**
     * Projet de rattachement
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project")
    private Project project;

    @Column(name = "project", insertable = false, updatable = false)
    @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
    private String projectId;

    /**
     * Lot de rattachement
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot")
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED) // l'id du lot (champ de docunit) est audité, mais pas le lot lui-même
    private Lot lot;

    @Column(name = "lot", insertable = false, updatable = false)
    @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
    private String lotId;

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
    @Field(type = FieldType.Object)
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
    @MultiField(mainField = @Field(type = FieldType.String),
                otherFields = {@InnerField(type = FieldType.String, suffix = SUBFIELD_RAW, index = FieldIndex.not_analyzed),
                               @InnerField(type = FieldType.String,
                                           suffix = SUBFIELD_CI_AI,
                                           indexAnalyzer = ANALYZER_CI_AI,
                                           searchAnalyzer = ANALYZER_CI_AI),
                               @InnerField(type = FieldType.String,
                                           suffix = SUBFIELD_CI_AS,
                                           indexAnalyzer = ANALYZER_CI_AS,
                                           searchAnalyzer = ANALYZER_CI_AS),
                               @InnerField(type = FieldType.String,
                                           suffix = SUBFIELD_PHRASE,
                                           indexAnalyzer = ANALYZER_PHRASE,
                                           searchAnalyzer = ANALYZER_PHRASE)})
    private String pgcnId;

    /**
     * Label
     */
    @Column(name = "label")
    @MultiField(mainField = @Field(type = FieldType.String),
                otherFields = {@InnerField(type = FieldType.String, suffix = SUBFIELD_RAW, index = FieldIndex.not_analyzed),
                               @InnerField(type = FieldType.String,
                                           suffix = SUBFIELD_CI_AI,
                                           indexAnalyzer = ANALYZER_CI_AI,
                                           searchAnalyzer = ANALYZER_CI_AI),
                               @InnerField(type = FieldType.String,
                                           suffix = SUBFIELD_CI_AS,
                                           indexAnalyzer = ANALYZER_CI_AS,
                                           searchAnalyzer = ANALYZER_CI_AS),
                               @InnerField(type = FieldType.String,
                                           suffix = SUBFIELD_PHRASE,
                                           indexAnalyzer = ANALYZER_PHRASE,
                                           searchAnalyzer = ANALYZER_PHRASE)})
    private String label;

    /**
     * Type (monographie, etc...)
     */
    @Column(name = "type")
    @Field(type = FieldType.String, analyzer = ANALYZER_KEYWORD)
    private String type;

    /**
     * Nom de la collection Internet Archive pour la diffusion
     */
    @ManyToOne
    @JoinColumn(name = "collection_ia")
    @Field(type = FieldType.Object)
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
    @Field(type = FieldType.String, analyzer = ANALYZER_KEYWORD)
    private String arkUrl;

    /**
     * Plan de classement PAC
     */
    @ManyToOne
    @JoinColumn(name = "classement_pac")
    @Field(type = FieldType.Object)
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
    @Field(type = FieldType.Boolean)
    private boolean archivable;

    /**
     * Possibilité de diffusion
     */
    @Column(name = "distributable")
    @Field(type = FieldType.Boolean)
    private boolean distributable;

    /**
     * Droits
     */
    @Column
    @Enumerated(EnumType.STRING)
    @Field(type = FieldType.String, analyzer = ANALYZER_KEYWORD)
    private RightsEnum rights;

    /**
     * Date d'embargo
     */
    @Column
    @Field(type = FieldType.Date)
    private LocalDate embargo;

    /**
     * Délai de contrôle après livraison (en jours)
     */
    @Column(name = "check_delay")
    @Field(type = FieldType.Integer)
    private Integer checkDelay;

    /**
     * Date de fin de contrôle prévue
     */
    @Column(name = "check_end_time")
    @Field(type = FieldType.Date)
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
    @Field(type = FieldType.Object, ignoreFields = {"parent", "children"})
    private DocUnit parent;

    /**
     * Liste des unités documentaires enfants
     */
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    @Field(type = FieldType.Object, ignoreFields = {"parent", "children"})
    private final List<DocUnit> children = new ArrayList<>();

    /**
     * Unités documentaires soeurs
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "sibling")
    @Field(type = FieldType.String, analyzer = ANALYZER_KEYWORD)
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
    @Field(type = FieldType.Boolean)
    private boolean foundRefAuthor;

    /**
     * Etat de la demande.
     */
    @Column(name = "progress_request_status", nullable = false)
    @Enumerated(EnumType.STRING)
    @Field(type = FieldType.String, analyzer = ANALYZER_KEYWORD)
    private ProgressStatus progressStatus = ProgressStatus.NOT_AVAILABLE;

    /**
     * Date de la demande.
     */
    @Column(name = "request_date")
    @Field(type = FieldType.Date)
    private LocalDate requestDate;

    /**
     * Date de la réponse.
     */
    @Column(name = "answer_date")
    @Field(type = FieldType.Date)
    private LocalDate answerDate;
    
    /**
     *  Infos export Omeka
     */
    @Column(name = "omeka_exp_status")
    @Enumerated(EnumType.STRING)
    private ExportStatus omekaExportStatus;
    
    @Column(name = "omeka_exp_date")
    private LocalDateTime omekaExportDate;
    
    /**
     *  Infos export local
     */
    @Column(name = "local_exp_status")
    @Enumerated(EnumType.STRING)
    private ExportStatus localExportStatus;
    
    @Column(name = "local_exp_date")
    private LocalDateTime localExportDate;
    
    
    /** langage selectionné pour ocr */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "active_ocr_lang")
    private OcrLanguage activeOcrLanguage;

    /**
     * Champ de suggestion du moteur de recherche, pas géré par hibernate
     */
    @Transient
    @Mapping(mappingPath = "/config/elasticsearch/mapping_doc_unit_suggestion.json")
    private CompletionContext suggest;

    @Transient
    @Field(type = FieldType.Integer)
    private final int nbDigitalDocuments = 0;

    @Transient
    @Field(type = FieldType.String, analyzer = ANALYZER_KEYWORD)
    private List<WorkflowStateKey> workflowStateKeys;

    @Transient
    @Field(type = FieldType.Date)
    private LocalDate latestDeliveryDate;

    @Transient
    @Field(type = FieldType.Long)
    private Long masterSize;
    

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
        } catch (final LazyInitializationException e) { //NOSONAR pour pas pourrir les logs car c'est qd mm souvent que ....
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

    public CompletionContext getSuggest() {
        return suggest;
    }

    public void setSuggest(final CompletionContext suggest) {
        this.suggest = suggest;
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
     *         the digitizingNotes to set
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

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(final String projectId) {
        this.projectId = projectId;
    }

    public String getLotId() {
        return lotId;
    }

    public void setLotId(final String lotId) {
        this.lotId = lotId;
    }

    public int getNbDigitalDocuments() {
        return this.digitalDocuments.size();
    }

    public List<WorkflowStateKey> getWorkflowStateKeys() {
        return workflowStateKeys;
    }

    public void setWorkflowStateKeys(final List<WorkflowStateKey> workflowStateKeys) {
        this.workflowStateKeys = workflowStateKeys;
    }

    public LocalDate getLatestDeliveryDate() {
        return latestDeliveryDate;
    }

    public void setLatestDeliveryDate(final LocalDate latestDeliveryDate) {
        this.latestDeliveryDate = latestDeliveryDate;
    }

    public Long getMasterSize() {
        return masterSize;
    }

    public void setMasterSize(final Long masterSize) {
        this.masterSize = masterSize;
    }
    

    public OmekaList getOmekaCollection() {
        return omekaCollection;
    }

    public void setOmekaCollection(final OmekaList omekaCollection) {
        this.omekaCollection = omekaCollection;
    }

    public OmekaList getOmekaItem() {
        return omekaItem;
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
        AVAILABLE,
        // l'unité documentaire est visible dans l'application
        NOT_AVAILABLE,
        // l'unité documentaire n'est pas disponible dans l'application (import en cours, ...)
        DELETED,
        // l'unité documentaire a été supprimée (utilisé pour les recherches d'ud importées)
        CANCELED,       
        // l'unite documentaire est annulée ou rattachée à un projet annulé
        CLOSED        
        // UD archivée suite cloture lot|projet.
    }

    /**
     * Etat de la demande de diffusion.
     */
    public enum ProgressStatus {
        NOT_AVAILABLE,
        // Non disponible
        REQUESTED,
        // Demandé
        VALIDATED,
        // Validé
        REFUSED        // Refusé
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
}
