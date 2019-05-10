package fr.progilone.pgcn.domain.dto.statistics;

import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;

public class StatisticsDocUnitStatusRatioDTO {

    /* Projet */
    private String projectIdentifier;
    private String projectName;
    /* Lot */
    private String lotIdentifier;
    private String lotLabel;
    /* Étape de l'UD */
    private WorkflowStateKey state;
    /**
     * Nombre d'UD au statut state
     */
    private long nbDocOnState;
    /**
     * Nombre d'UD dans le projet / lot
     */
    private long nbDoc;

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

    public WorkflowStateKey getState() {
        return state;
    }

    public void setState(final WorkflowStateKey state) {
        this.state = state;
    }

    public long getNbDocOnState() {
        return nbDocOnState;
    }

    public void setNbDocOnState(final long nbDocOnState) {
        this.nbDocOnState = nbDocOnState;
    }

    public long getNbDoc() {
        return nbDoc;
    }

    public void setNbDoc(final long nbDoc) {
        this.nbDoc = nbDoc;
    }
}
