package fr.progilone.pgcn.domain.dto.statistics;

import com.google.common.collect.Ordering;
import com.opencsv.bean.CsvBindByName;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import jakarta.annotation.Nullable;
import java.time.LocalDateTime;

public class WorkflowUserActivityDTO implements Comparable<WorkflowUserActivityDTO> {

    private static final Ordering<WorkflowUserActivityDTO> orderDto;

    static {
        Ordering<WorkflowUserActivityDTO> orderLib = Ordering.natural().nullsFirst().onResultOf(WorkflowUserActivityDTO::getLibraryName);
        Ordering<WorkflowUserActivityDTO> orderUser = Ordering.natural().nullsFirst().onResultOf(WorkflowUserActivityDTO::getUserFullName);
        Ordering<WorkflowUserActivityDTO> orderPj = Ordering.natural().nullsFirst().onResultOf(WorkflowUserActivityDTO::getProjectName);
        Ordering<WorkflowUserActivityDTO> orderLot = Ordering.natural().nullsFirst().onResultOf(WorkflowUserActivityDTO::getLotLabel);
        Ordering<WorkflowUserActivityDTO> orderDoc = Ordering.natural().nullsFirst().onResultOf(WorkflowUserActivityDTO::getDocPgcnId);
        orderDto = orderLib.compound(orderUser).compound(orderPj).compound(orderLot).compound(orderDoc);
    }

    /* Bibliothèque */
    private String libraryIdentifier;

    @CsvBindByName(column = "01. Bibliothèque")
    private String libraryName;

    /* Utilisateur */
    private String userIdentifier;

    @CsvBindByName(column = "02. Login")
    private String userLogin;

    @CsvBindByName(column = "03. Nom")
    private String userFullName;

    private String roleIdentifier;

    @CsvBindByName(column = "04. Profil")
    private String roleLabel;

    private String projectIdentifier;

    @CsvBindByName(column = "05. Projet")
    private String projectName;

    private String lotIdentifier;

    @CsvBindByName(column = "06. Lot")
    private String lotLabel;

    private String docIdentifier;

    @CsvBindByName(column = "07. PGCN ID")
    private String docPgcnId;

    @CsvBindByName(column = "08. Étape")
    private WorkflowStateKey state;

    @CsvBindByName(column = "09. Début action")
    private LocalDateTime startDate;

    @CsvBindByName(column = "10. Fin action")
    private LocalDateTime endDate;

    @CsvBindByName(column = "11. Durée(s)")
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

    public String getUserIdentifier() {
        return userIdentifier;
    }

    public void setUserIdentifier(final String userIdentifier) {
        this.userIdentifier = userIdentifier;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(final String userLogin) {
        this.userLogin = userLogin;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(final String userFullName) {
        this.userFullName = userFullName;
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
    public int compareTo(@Nullable final WorkflowUserActivityDTO o) {
        return orderDto.compare(this, o);
    }
}
