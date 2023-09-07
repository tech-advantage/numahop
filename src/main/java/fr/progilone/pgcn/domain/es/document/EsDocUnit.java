package fr.progilone.pgcn.domain.es.document;

import static fr.progilone.pgcn.service.es.EsConstant.*;

import fr.progilone.pgcn.domain.delivery.DeliveredDocument;
import fr.progilone.pgcn.domain.document.DigitalDocument;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.DocUnit.RightsEnum;
import fr.progilone.pgcn.domain.es.library.EsLibrary;
import fr.progilone.pgcn.domain.workflow.DocUnitState;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import fr.progilone.pgcn.service.es.EsConstant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.CompletionContext;
import org.springframework.data.elasticsearch.annotations.CompletionContext.ContextMappingType;
import org.springframework.data.elasticsearch.annotations.CompletionField;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.data.elasticsearch.core.suggest.Completion;

/**
 * Classe représentant une unité documentaire
 */
@Document(indexName = "#{@environment.getProperty('elasticsearch.index.name')}-doc-unit")
@Setting(settingPath = "config/elasticsearch/settings_pgcn.json", shards = 1, replicas = 0)
public class EsDocUnit {

    @Id
    @Field(type = FieldType.Keyword)
    private String identifier;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime createdDate;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime lastModifiedDate;

    /**
     * Bibliothèque de rattachement
     */
    @Field(type = FieldType.Object)
    private EsLibrary library;

    @Field(type = FieldType.Keyword)
    private String projectId;

    @Field(type = FieldType.Keyword)
    private String lotId;

    /**
     * Liste des documents physiques rattachés
     */
    @Field(type = FieldType.Object)
    private Set<EsPhysicalDocument> physicalDocuments;

    /**
     * identifiant PGCN
     */
    @Field(type = FieldType.Keyword)
    private String pgcnId;

    /**
     * Label
     */
    @MultiField(mainField = @Field(type = FieldType.Text),
                otherFields = {@InnerField(type = FieldType.Keyword, suffix = SUBFIELD_RAW),
                               @InnerField(type = FieldType.Text, suffix = SUBFIELD_CI_AI, analyzer = ANALYZER_CI_AI, searchAnalyzer = ANALYZER_CI_AI),
                               @InnerField(type = FieldType.Text, suffix = SUBFIELD_CI_AS, analyzer = ANALYZER_CI_AS, searchAnalyzer = ANALYZER_CI_AS),
                               @InnerField(type = FieldType.Text, suffix = SUBFIELD_PHRASE, analyzer = ANALYZER_PHRASE, searchAnalyzer = ANALYZER_PHRASE)})
    private String label;

    /**
     * Type (monographie, etc...)
     */
    @Field(type = FieldType.Keyword)
    private String type;

    /**
     * Identifiant ARK
     */
    @Field(type = FieldType.Keyword)
    private String arkUrl;

    /**
     * Possibilité d'archivage
     */
    @Field(type = FieldType.Boolean)
    private boolean archivable;

    /**
     * Possibilité de diffusion
     */
    @Field(type = FieldType.Boolean)
    private boolean distributable;

    /**
     * Nom de la collection Internet Archive pour la diffusion
     */
    @Field(type = FieldType.Object)
    private EsInternetArchiveCollection collectionIA;

    /**
     * Plan de classement PAC
     */
    @Field(type = FieldType.Object)
    private EsCinesPAC planClassementPAC;

    /**
     * Droits
     */
    @Field(type = FieldType.Keyword)
    private RightsEnum rights;

    /**
     * Date d'embargo
     */
    @Field(type = FieldType.Date)
    private LocalDate embargo;

    /**
     * Délai de contrôle après livraison (en jours)
     */
    @Field(type = FieldType.Integer)
    private Integer checkDelay;

    /**
     * Date de fin de contrôle prévue
     */
    @Field(type = FieldType.Date)
    private LocalDate checkEndTime;

    /**
     * Unité documentaire parente
     */
    @Field(type = FieldType.Object)
    private EsDocUnitSimple parent;

    /**
     * Liste des unités documentaires enfants
     */
    @Field(type = FieldType.Object)
    private List<EsDocUnitSimple> children;

    /**
     * Unités documentaires soeurs
     */
    @Field(type = FieldType.Keyword)
    private List<String> sibling;

    /**
     * Champ de suggestion du moteur de recherche, pas géré par hibernate
     */
    @CompletionField(contexts = {@CompletionContext(name = EsConstant.SUGGEST_CTX_LIBRARY, type = ContextMappingType.CATEGORY)})
    private Completion suggest;

    @Field(type = FieldType.Integer)
    private int nbDigitalDocuments;

    @Field(type = FieldType.Keyword)
    private List<WorkflowStateKey> workflowStateKeys;

    @Field(type = FieldType.Date)
    private LocalDate latestDeliveryDate;

    @Field(type = FieldType.Long)
    private Long masterSize;

    /**
     * Liste des propriétés de l'unité documentaire regroupés dans une notice (DC, DCq, Custom)
     */
    @Field(type = FieldType.Nested)
    private Set<EsBibliographicRecord> records;

    @Field(type = FieldType.Nested)
    private List<EsCinesReport> cinesReports;

    @Field(type = FieldType.Nested)
    private List<EsInternetArchiveReport> iaReports;

    public static EsDocUnit from(final DocUnit docUnit) {
        final EsDocUnit esDocUnit = new EsDocUnit();
        esDocUnit.setIdentifier(docUnit.getIdentifier());
        esDocUnit.setCreatedDate(docUnit.getCreatedDate());
        esDocUnit.setLastModifiedDate(docUnit.getLastModifiedDate());
        esDocUnit.setLibrary(EsLibrary.from(docUnit.getLibrary()));
        if (docUnit.getProject() != null) {
            esDocUnit.setProjectId(docUnit.getProject().getIdentifier());
        }
        if (docUnit.getLot() != null) {
            esDocUnit.setLotId(docUnit.getLot().getIdentifier());
        }
        esDocUnit.setPhysicalDocuments(docUnit.getPhysicalDocuments().stream().map(EsPhysicalDocument::from).collect(Collectors.toSet()));
        esDocUnit.setPgcnId(docUnit.getPgcnId());
        esDocUnit.setLabel(docUnit.getLabel());
        esDocUnit.setType(docUnit.getType());
        esDocUnit.setArkUrl(docUnit.getArkUrl());
        esDocUnit.setArchivable(docUnit.isArchivable());
        esDocUnit.setDistributable(docUnit.isDistributable());
        if (docUnit.getCollectionIA() != null) {
            esDocUnit.setCollectionIA(EsInternetArchiveCollection.from(docUnit.getCollectionIA()));
        }
        if (docUnit.getPlanClassementPAC() != null) {
            esDocUnit.setPlanClassementPAC(EsCinesPAC.from(docUnit.getPlanClassementPAC()));
        }
        esDocUnit.setRights(docUnit.getRights());
        esDocUnit.setEmbargo(docUnit.getEmbargo());
        esDocUnit.setCheckDelay(docUnit.getCheckDelay());
        esDocUnit.setCheckEndTime(docUnit.getCheckEndTime());
        if (docUnit.getParent() != null) {
            esDocUnit.setParent(EsDocUnitSimple.from(docUnit.getParent()));
        }
        esDocUnit.setChildren(docUnit.getChildren().stream().map(EsDocUnitSimple::from).collect(Collectors.toList()));
        if (docUnit.getSibling() != null) {
            esDocUnit.setSibling(docUnit.getSibling()
                                        .getDocUnits()
                                        .stream()
                                        .filter(sib -> !StringUtils.equals(sib.getIdentifier(), docUnit.getIdentifier()))
                                        .map(DocUnit::getIdentifier)
                                        .collect(Collectors.toList()));
        }
        esDocUnit.setNbDigitalDocuments(docUnit.getDigitalDocuments().size());
        if (docUnit.getWorkflow() != null) {
            final List<WorkflowStateKey> workflowStateKeys = docUnit.getWorkflow().getCurrentStates().stream().map(DocUnitState::getKey).collect(Collectors.toList());
            esDocUnit.setWorkflowStateKeys(workflowStateKeys);
        }
        // dernière date de livraison et taille des fichiers numériques (masters)
        esDocUnit.setMasterSize(0L);
        docUnit.getDigitalDocuments().stream().filter(dlv -> dlv.getDeliveryDate() != null).max(Comparator.comparing(DigitalDocument::getDeliveryDate)).ifPresent(dlv -> {
            esDocUnit.setLatestDeliveryDate(dlv.getDeliveryDate());
            final long totalLength = dlv.getDeliveries().stream().map(DeliveredDocument::getTotalLength).filter(Objects::nonNull).mapToLong(l -> l).sum();
            esDocUnit.setMasterSize(totalLength);
        });

        esDocUnit.setRecords(docUnit.getRecords().stream().map(EsBibliographicRecord::from).collect(Collectors.toSet()));
        esDocUnit.setCinesReports(docUnit.getCinesReports().stream().map(EsCinesReport::from).collect(Collectors.toList()));
        esDocUnit.setIaReports(docUnit.getIaReports().stream().map(EsInternetArchiveReport::from).collect(Collectors.toList()));

        final Completion suggestion = new Completion(List.of(docUnit.getLabel(), docUnit.getPgcnId()));
        final List<String> libIds = new ArrayList<>();
        libIds.add(SUGGEST_CTX_GLOBAL);
        if (docUnit.getLibrary() != null) {
            libIds.add(docUnit.getLibrary().getIdentifier());
        }
        suggestion.setContexts(Map.of(SUGGEST_CTX_LIBRARY, libIds));
        esDocUnit.setSuggest(suggestion);

        return esDocUnit;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(final LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(final LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public EsLibrary getLibrary() {
        return library;
    }

    public void setLibrary(final EsLibrary library) {
        this.library = library;
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

    public String getPgcnId() {
        return pgcnId;
    }

    public void setPgcnId(final String pgcnId) {
        this.pgcnId = pgcnId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getArkUrl() {
        return arkUrl;
    }

    public void setArkUrl(final String arkUrl) {
        this.arkUrl = arkUrl;
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

    public EsInternetArchiveCollection getCollectionIA() {
        return collectionIA;
    }

    public void setCollectionIA(final EsInternetArchiveCollection collectionIA) {
        this.collectionIA = collectionIA;
    }

    public EsCinesPAC getPlanClassementPAC() {
        return planClassementPAC;
    }

    public void setPlanClassementPAC(final EsCinesPAC planClassementPAC) {
        this.planClassementPAC = planClassementPAC;
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

    public EsDocUnitSimple getParent() {
        return parent;
    }

    public void setParent(final EsDocUnitSimple parent) {
        this.parent = parent;
    }

    public List<EsDocUnitSimple> getChildren() {
        return children;
    }

    public void setChildren(final List<EsDocUnitSimple> children) {
        this.children = children;
    }

    public List<String> getSibling() {
        return sibling;
    }

    public void setSibling(final List<String> sibling) {
        this.sibling = sibling;
    }

    public Completion getSuggest() {
        return suggest;
    }

    public void setSuggest(final Completion suggest) {
        this.suggest = suggest;
    }

    public int getNbDigitalDocuments() {
        return nbDigitalDocuments;
    }

    public void setNbDigitalDocuments(final int nbDigitalDocuments) {
        this.nbDigitalDocuments = nbDigitalDocuments;
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

    public Set<EsPhysicalDocument> getPhysicalDocuments() {
        return physicalDocuments;
    }

    public void setPhysicalDocuments(final Set<EsPhysicalDocument> physicalDocuments) {
        this.physicalDocuments = physicalDocuments;
    }

    public Set<EsBibliographicRecord> getRecords() {
        return records;
    }

    public void setRecords(final Set<EsBibliographicRecord> records) {
        this.records = records;
    }

    public List<EsCinesReport> getCinesReports() {
        return cinesReports;
    }

    public void setCinesReports(final List<EsCinesReport> cinesReports) {
        this.cinesReports = cinesReports;
    }

    public List<EsInternetArchiveReport> getIaReports() {
        return iaReports;
    }

    public void setIaReports(final List<EsInternetArchiveReport> iaReports) {
        this.iaReports = iaReports;
    }
}
