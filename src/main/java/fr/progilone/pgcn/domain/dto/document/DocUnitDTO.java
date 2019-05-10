package fr.progilone.pgcn.domain.dto.document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.DocUnit.ProgressStatus;
import fr.progilone.pgcn.domain.document.DocUnit.RightsEnum;
import fr.progilone.pgcn.domain.dto.AbstractVersionedDTO;
import fr.progilone.pgcn.domain.dto.administration.CinesPACDTO;
import fr.progilone.pgcn.domain.dto.administration.InternetArchiveCollectionDTO;
import fr.progilone.pgcn.domain.dto.administration.omeka.OmekaListDTO;
import fr.progilone.pgcn.domain.dto.check.AutomaticCheckResultDTO;
import fr.progilone.pgcn.domain.dto.exchange.CinesReportDTO;
import fr.progilone.pgcn.domain.dto.exchange.InternetArchiveReportDTO;
import fr.progilone.pgcn.domain.dto.library.LibraryDTO;
import fr.progilone.pgcn.domain.dto.lot.SimpleLotDTO;
import fr.progilone.pgcn.domain.dto.ocrlangconfiguration.OcrLanguageDTO;
import fr.progilone.pgcn.domain.dto.project.SimpleProjectDTO;
import fr.progilone.pgcn.domain.dto.workflow.DocUnitWorkflowDTO;

/**
 * DTO représentant une unité documentaire est ses dépendences
 *
 * @author jbrunet
 * @see DocUnit
 */
public class DocUnitDTO extends AbstractVersionedDTO {

    private String identifier;
    private LibraryDTO library;
    private SimpleProjectDTO project;
    private SimpleLotDTO lot;
    private Set<SimpleDigitalDocumentDTO> digitalDocuments;
    private Set<SimplePhysicalDocumentDTO> physicalDocuments;
    private List<DocUnitBibliographicRecordDTO> records;
    private String pgcnId;
    private String label;
    private String type;
    private InternetArchiveCollectionDTO collectionIA;
    private String arkUrl;
    private Boolean archivable;
    private Boolean distributable;
    private RightsEnum rights;
    private LocalDate embargo;
    private Integer checkDelay;
    private LocalDate checkEndTime;
    private CinesPACDTO planClassementPAC;
    private Integer cinesVersion;
    private DocUnit.CondReportType condReportType;
    private String digitizingNotes;
    private String cancelingComment;
    private String state;
    private OmekaListDTO omekaCollection;
    private OmekaListDTO omekaItem;
    private Boolean foundRefAuthor;
    private ProgressStatus progressStatus;
    private LocalDate requestDate;
    private LocalDate answerDate;
    private String omekaExportStatus;
    private LocalDateTime omekaExportDate;
    private OcrLanguageDTO activeOcrLanguage;
    
    // Retours de contrôles
    private List<AutomaticCheckResultDTO> automaticCheckResults;
    private List<CinesReportDTO> cinesReports;
    private List<InternetArchiveReportDTO> iaReports;
    // FIXME temporaire pour le remplissage dans le cas où l'on a un seul document physique
    private String digitalId;

    // Info de l'unité documentaire parente
    private String parentIdentifier;
    private String parentPgcnId;
    private String parentLabel;
    // Info sur les enfants
    private int nbChildren;
    // Info sur la fratrie
    private int nbSiblings;

    /**
     * Existence d'un fichier EAD
     */
    private boolean eadExport;
    /**
     * Ajout des infos du nombre de pages
     **/
    private int total;
    /**
     * Ajout des infos de création
     */
    private String createdBy;
    private LocalDateTime createdDate;
    /**
     * Ajout des infos de modifications
     */
    private String lastModifiedBy;
    private LocalDateTime lastModifiedDate;
    
    /**
     * Workflow lié
     */
    private DocUnitWorkflowDTO workflow;

    public DocUnitDTO() {
    }

    public final String getIdentifier() {
        return identifier;
    }

    public final void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public final LibraryDTO getLibrary() {
        return library;
    }

    public final void setLibrary(final LibraryDTO library) {
        this.library = library;
    }

    public final SimpleProjectDTO getProject() {
        return project;
    }

    public final void setProject(final SimpleProjectDTO project) {
        this.project = project;
    }

    public SimpleLotDTO getLot() {
        return lot;
    }

    public void setLot(final SimpleLotDTO lot) {
        this.lot = lot;
    }

    public final List<DocUnitBibliographicRecordDTO> getRecords() {
        return records;
    }

    public final void setRecords(final List<DocUnitBibliographicRecordDTO> records) {
        this.records = records;
    }

    public final String getPgcnId() {
        return pgcnId;
    }

    public final void setPgcnId(final String pgcnId) {
        this.pgcnId = pgcnId;
    }

    public final String getType() {
        return type;
    }

    public final void setType(final String type) {
        this.type = type;
    }

    public final Boolean getArchivable() {
        return archivable;
    }

    public final void setArchivable(final Boolean archivable) {
        this.archivable = archivable;
    }

    public final Boolean getDistributable() {
        return distributable;
    }

    public final void setDistributable(final Boolean distributable) {
        this.distributable = distributable;
    }

    public final RightsEnum getRights() {
        return rights;
    }

    public final void setRights(final RightsEnum rights) {
        this.rights = rights;
    }

    public final LocalDate getEmbargo() {
        return embargo;
    }

    public final void setEmbargo(final LocalDate embargo) {
        this.embargo = embargo;
    }

    public final Integer getCheckDelay() {
        return checkDelay;
    }

    public final void setCheckDelay(final Integer checkDelay) {
        this.checkDelay = checkDelay;
    }

    public final LocalDate getCheckEndTime() {
        return checkEndTime;
    }

    public final void setCheckEndTime(final LocalDate checkEndTime) {
        this.checkEndTime = checkEndTime;
    }

    public DocUnit.CondReportType getCondReportType() {
        return condReportType;
    }

    public void setCondReportType(final DocUnit.CondReportType condReportType) {
        this.condReportType = condReportType;
    }

    public final String getLabel() {
        return label;
    }

    public final void setLabel(final String label) {
        this.label = label;
    }

    public Set<SimpleDigitalDocumentDTO> getDigitalDocuments() {
        return digitalDocuments;
    }

    public InternetArchiveCollectionDTO getCollectionIA() {
        return collectionIA;
    }

    public void setCollectionIA(final InternetArchiveCollectionDTO collectionIA) {
        this.collectionIA = collectionIA;
    }

    public CinesPACDTO getPlanClassementPAC() {
        return planClassementPAC;
    }

    public void setPlanClassementPAC(final CinesPACDTO planClassementPAC) {
        this.planClassementPAC = planClassementPAC;
    }

    public void setDigitalDocuments(final Set<SimpleDigitalDocumentDTO> digitalDocuments) {
        if (digitalDocuments == null) {
            this.digitalDocuments = null;
        } else {
            if (this.digitalDocuments == null) {
                this.digitalDocuments = new HashSet<>();
            }
            digitalDocuments.forEach(this::addDigitalDocument);
        }
    }

    public void addDigitalDocument(final SimpleDigitalDocumentDTO digitalDocument) {
        if (digitalDocument != null) {
            digitalDocument.setDocUnit(this);
            this.digitalDocuments.add(digitalDocument);
        }
    }

    public List<AutomaticCheckResultDTO> getAutomaticCheckResults() {
        return automaticCheckResults;
    }

    public void setAutomaticCheckResults(final List<AutomaticCheckResultDTO> checkResults) {
        this.automaticCheckResults = checkResults;
    }

    public List<CinesReportDTO> getCinesReports() {
        return cinesReports;
    }

    public void setCinesReports(final List<CinesReportDTO> cinesReports) {
        this.cinesReports = cinesReports;
    }

    public List<InternetArchiveReportDTO> getIaReports() {
        return iaReports;
    }

    public void setIaReports(final List<InternetArchiveReportDTO> iaReports) {
        this.iaReports = iaReports;
    }

    public String getDigitalId() {
        return digitalId;
    }

    public void setDigitalId(final String digitalId) {
        this.digitalId = digitalId;
    }

    public Set<SimplePhysicalDocumentDTO> getPhysicalDocuments() {
        return physicalDocuments;
    }

    public void setPhysicalDocuments(final Set<SimplePhysicalDocumentDTO> physicalDocuments) {
        this.physicalDocuments = physicalDocuments;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(final LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(final String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(final LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(final int total) {
        this.total = total;
    }

    public boolean isEadExport() {
        return eadExport;
    }

    public void setEadExport(final boolean eadExport) {
        this.eadExport = eadExport;
    }

    public String getParentIdentifier() {
        return parentIdentifier;
    }

    public void setParentIdentifier(final String parentIdentifier) {
        this.parentIdentifier = parentIdentifier;
    }

    public String getParentPgcnId() {
        return parentPgcnId;
    }

    public void setParentPgcnId(final String parentPgcnId) {
        this.parentPgcnId = parentPgcnId;
    }

    public String getParentLabel() {
        return parentLabel;
    }

    public void setParentLabel(final String parentLabel) {
        this.parentLabel = parentLabel;
    }

    public int getNbChildren() {
        return nbChildren;
    }

    public void setNbChildren(final int nbChildren) {
        this.nbChildren = nbChildren;
    }

    public int getNbSiblings() {
        return nbSiblings;
    }

    public void setNbSiblings(final int nbSiblings) {
        this.nbSiblings = nbSiblings;
    }

    public String getArkUrl() {
        return arkUrl;
    }

    public void setArkUrl(final String arkUrl) {
        this.arkUrl = arkUrl;
    }

    public DocUnitWorkflowDTO getWorkflow() {
        return workflow;
    }

    public void setWorkflow(final DocUnitWorkflowDTO workflow) {
        this.workflow = workflow;
    }
    
    public Integer getCinesVersion() {
        return cinesVersion;
    }

    public void setCinesVersion(final Integer cinesVersion) {
        this.cinesVersion = cinesVersion;
    }

    /**
     * @return the digitizingNote
     */
    public String getDigitizingNotes() {
        return digitizingNotes;
    }

    /**
     * @param digitizingNote the digitizingNote to set
     */
    public void setDigitizingNotes(final String digitizingNotes) {
        this.digitizingNotes = digitizingNotes;
    }

    public String getCancelingComment() {
        return cancelingComment;
    }

    public void setCancelingComment(final String cancelingComment) {
        this.cancelingComment = cancelingComment;
    }

    public String getState() {
        return state;
    }

    public void setState(final String state) {
        this.state = state;
    }

    public OmekaListDTO getOmekaCollection() {
        return omekaCollection;
    }

    public void setOmekaCollection(final OmekaListDTO omekaCollection) {
        this.omekaCollection = omekaCollection;
    }

    public OmekaListDTO getOmekaItem() {
        return omekaItem;
    }

    public void setOmekaItem(final OmekaListDTO omekaItem) {
        this.omekaItem = omekaItem;
    }

    public Boolean getFoundRefAuthor() {
        return foundRefAuthor;
    }

    public void setFoundRefAuthor(final Boolean foundRefAuthor) {
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

    public String getOmekaExportStatus() {
        return omekaExportStatus;
    }

    public void setOmekaExportStatus(final String omekaExportStatus) {
        this.omekaExportStatus = omekaExportStatus;
    }
    
    public LocalDateTime getOmekaExportDate() {
        return omekaExportDate;
    }

    public void setOmekaExportDate(final LocalDateTime omekaExportDate) {
        this.omekaExportDate = omekaExportDate;
    }

    public OcrLanguageDTO getActiveOcrLanguage() {
        return activeOcrLanguage;
    }

    public void setActiveOcrLanguage(final OcrLanguageDTO activeOcrLanguage) {
        this.activeOcrLanguage = activeOcrLanguage;
    }
}
