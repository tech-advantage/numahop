package fr.progilone.pgcn.domain.dto.statistics;

import java.time.LocalDate;

import com.google.common.collect.Ordering;
import com.opencsv.bean.CsvBindByName;

import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;

public class StatisticsDocPublishedDTO implements Comparable<StatisticsDocPublishedDTO> {

    private static final Ordering<StatisticsDocPublishedDTO> ORDER_DTO;

    static {
        final Ordering<StatisticsDocPublishedDTO> orderLib = Ordering.natural().nullsFirst().onResultOf(StatisticsDocPublishedDTO::getLibraryName);
        final Ordering<StatisticsDocPublishedDTO> orderPj = Ordering.natural().nullsFirst().onResultOf(StatisticsDocPublishedDTO::getProjectName);
        final Ordering<StatisticsDocPublishedDTO> orderLot = Ordering.natural().nullsFirst().onResultOf(StatisticsDocPublishedDTO::getLotLabel);
        final Ordering<StatisticsDocPublishedDTO> orderDoc = Ordering.natural().nullsFirst().onResultOf(StatisticsDocPublishedDTO::getDocUnitPgcnId);

        ORDER_DTO = orderLib.compound(orderPj).compound(orderLot).compound(orderDoc);
    }

    private String libraryIdentifier;
    @CsvBindByName(column = "01. Bibliothèque")
    private String libraryName;

    private String projectIdentifier;
    @CsvBindByName(column = "02. Projet")
    private String projectName;

    private String lotIdentifier;
    @CsvBindByName(column = "03. Lot")
    private String lotLabel;

    private String docUnitIdentifier;
    @CsvBindByName(column = "04. PgcnId")
    private String docUnitPgcnId;
    @CsvBindByName(column = "05. Titre")
    private String docUnitLabel;
    @CsvBindByName(column = "06. Type de document")
    private String docUnitType;

    private String parentIdentifier;
    @CsvBindByName(column = "07. PgcnId parent")
    private String parentPgcnId;
    @CsvBindByName(column = "08. Titre parent")
    private String parentLabel;

    @CsvBindByName(column = "09. Diffusion")
    private WorkflowStateKey workflowState;
    
    @CsvBindByName(column = "10. Lien IA")
    private String linkIA;
    
    @CsvBindByName(column = "11. Url ark")
    private String urlArk;
    
    @CsvBindByName(column = "12. Regroupement")
    private String collection;
    @CsvBindByName(column = "13. Nombre de pages")
    private int nbPages;
    @CsvBindByName(column = "14. Date de publication")
    private LocalDate publicationDate;

    public String getLibraryIdentifier() {
        return libraryIdentifier;
    }

    public void setLibraryIdentifier(final String libraryIdentifier) {
        this.libraryIdentifier = libraryIdentifier;
    }

    public String getLibraryName() {
        return libraryName;
    }

    public void setLibraryName(final String libraryName) {
        this.libraryName = libraryName;
    }

    public String getProjectIdentifier() {
        return projectIdentifier;
    }

    public void setProjectIdentifier(final String projectIdentifier) {
        this.projectIdentifier = projectIdentifier;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(final String projectName) {
        this.projectName = projectName;
    }

    public String getLotIdentifier() {
        return lotIdentifier;
    }

    public void setLotIdentifier(final String lotIdentifier) {
        this.lotIdentifier = lotIdentifier;
    }

    public String getLotLabel() {
        return lotLabel;
    }

    public void setLotLabel(final String lotLabel) {
        this.lotLabel = lotLabel;
    }

    public String getDocUnitIdentifier() {
        return docUnitIdentifier;
    }

    public void setDocUnitIdentifier(final String docUnitIdentifier) {
        this.docUnitIdentifier = docUnitIdentifier;
    }

    public String getDocUnitPgcnId() {
        return docUnitPgcnId;
    }

    public void setDocUnitPgcnId(final String docUnitPgcnId) {
        this.docUnitPgcnId = docUnitPgcnId;
    }

    public String getDocUnitLabel() {
        return docUnitLabel;
    }

    public void setDocUnitLabel(final String docUnitLabel) {
        this.docUnitLabel = docUnitLabel;
    }

    public String getDocUnitType() {
        return docUnitType;
    }

    public void setDocUnitType(final String docUnitType) {
        this.docUnitType = docUnitType;
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

    public WorkflowStateKey getWorkflowState() {
        return workflowState;
    }

    public void setWorkflowState(final WorkflowStateKey workflowState) {
        this.workflowState = workflowState;
    }

    public String getLinkIA() {
        return linkIA;
    }

    public void setLinkIA(final String lienIA) {
        this.linkIA = lienIA;
    }

    public String getUrlArk() {
        return urlArk;
    }

    public void setUrlArk(final String urlArk) {
        this.urlArk = urlArk;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(final String collection) {
        this.collection = collection;
    }

    public int getNbPages() {
        return nbPages;
    }

    public void setNbPages(final int nbPages) {
        this.nbPages = nbPages;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(final LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    @Override
    public int compareTo(final StatisticsDocPublishedDTO o) {
        return ORDER_DTO.compare(this, o);
    }
}
