package fr.progilone.pgcn.domain.dto.statistics;

import com.google.common.collect.Ordering;
import com.opencsv.bean.CsvBindByName;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import jakarta.annotation.Nullable;

public class WorkflowStateProgressDTO implements Comparable<WorkflowStateProgressDTO> {

    private static final Ordering<WorkflowStateProgressDTO> orderDto;

    static {
        Ordering<WorkflowStateProgressDTO> orderLib = Ordering.natural().nullsFirst().onResultOf(WorkflowStateProgressDTO::getLibraryName);
        Ordering<WorkflowStateProgressDTO> orderWorkflow = Ordering.natural().nullsFirst().onResultOf(WorkflowStateProgressDTO::getWorkflowModelName);
        orderDto = orderLib.compound(orderWorkflow);
    }

    private String libraryIdentifier;

    @CsvBindByName(column = "1. Bibliothèque")
    private String libraryName;

    private String workflowModelIdentifier;

    @CsvBindByName(column = "2. Workflow")
    private String workflowModelName;

    /**
     * Étape de workflow
     */
    @CsvBindByName(column = "3. Étape")
    private WorkflowStateKey key;

    /**
     * Durée moyenne de l'étape, en secondes
     */
    @CsvBindByName(column = "4. Durée moyenne")
    private long avgDuration;

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

    public String getWorkflowModelIdentifier() {
        return workflowModelIdentifier;
    }

    public void setWorkflowModelIdentifier(final String workflowModelIdentifier) {
        this.workflowModelIdentifier = workflowModelIdentifier;
    }

    public String getWorkflowModelName() {
        return workflowModelName;
    }

    public void setWorkflowModelName(final String workflowModelName) {
        this.workflowModelName = workflowModelName;
    }

    public WorkflowStateKey getKey() {
        return key;
    }

    public void setKey(final WorkflowStateKey key) {
        this.key = key;
    }

    public long getAvgDuration() {
        return avgDuration;
    }

    public void setAvgDuration(final long avgDuration) {
        this.avgDuration = avgDuration;
    }

    @Override
    public int compareTo(@Nullable final WorkflowStateProgressDTO o) {
        return orderDto.compare(this, o);
    }
}
