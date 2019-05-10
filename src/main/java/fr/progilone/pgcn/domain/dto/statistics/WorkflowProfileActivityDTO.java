package fr.progilone.pgcn.domain.dto.statistics;

import com.google.common.collect.Ordering;
import com.opencsv.bean.CsvBindByName;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;

import javax.annotation.Nullable;
import java.time.LocalDateTime;

public class WorkflowProfileActivityDTO implements Comparable<WorkflowProfileActivityDTO> {

    private static final Ordering<WorkflowProfileActivityDTO> ORDER_DTO;

    static {
        Ordering<WorkflowProfileActivityDTO> orderLib = Ordering.natural().nullsFirst().onResultOf(WorkflowProfileActivityDTO::getLibraryName);
        Ordering<WorkflowProfileActivityDTO> orderRole = Ordering.natural().nullsFirst().onResultOf(WorkflowProfileActivityDTO::getRoleLabel);
        Ordering<WorkflowProfileActivityDTO> orderPj = Ordering.natural().nullsFirst().onResultOf(WorkflowProfileActivityDTO::getProjectName);
        Ordering<WorkflowProfileActivityDTO> orderLot = Ordering.natural().nullsFirst().onResultOf(WorkflowProfileActivityDTO::getLotLabel);
        Ordering<WorkflowProfileActivityDTO> orderDoc = Ordering.natural().nullsFirst().onResultOf(WorkflowProfileActivityDTO::getDocPgcnId);
        ORDER_DTO = orderLib.compound(orderRole).compound(orderPj).compound(orderLot).compound(orderDoc);
    }

    /* Bibliothèque */
    private String libraryIdentifier;

    @CsvBindByName(column = "01. Bibliothèque")
    private String libraryName;

    private String roleIdentifier;

    @CsvBindByName(column = "02. Profil")
    private String roleLabel;

    private String projectIdentifier;

    @CsvBindByName(column = "03. Projet")
    private String projectName;

    private String lotIdentifier;

    @CsvBindByName(column = "04. Lot")
    private String lotLabel;

    private String docIdentifier;

    @CsvBindByName(column = "05. PGCN ID")
    private String docPgcnId;

    @CsvBindByName(column = "06. Étape")
    private WorkflowStateKey state;

    @CsvBindByName(column = "07. Début action")
    private LocalDateTime startDate;

    @CsvBindByName(column = "08. Fin action")
    private LocalDateTime endDate;

    @CsvBindByName(column = "09. Durée(s)")
    private Long duration;

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

    public String getRoleIdentifier() {
        return roleIdentifier;
    }

    public void setRoleIdentifier(final String roleIdentifier) {
        this.roleIdentifier = roleIdentifier;
    }

    public String getRoleLabel() {
        return roleLabel;
    }

    public void setRoleLabel(final String roleLabel) {
        this.roleLabel = roleLabel;
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

    public String getDocIdentifier() {
        return docIdentifier;
    }

    public void setDocIdentifier(final String docIdentifier) {
        this.docIdentifier = docIdentifier;
    }

    public String getDocPgcnId() {
        return docPgcnId;
    }

    public void setDocPgcnId(final String docPgcnId) {
        this.docPgcnId = docPgcnId;
    }

    public WorkflowStateKey getState() {
        return state;
    }

    public void setState(final WorkflowStateKey state) {
        this.state = state;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(final LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(final LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(final Long duration) {
        this.duration = duration;
    }

    @Override
    public int compareTo(@Nullable final WorkflowProfileActivityDTO o) {
        return ORDER_DTO.compare(this, o);
    }
}
