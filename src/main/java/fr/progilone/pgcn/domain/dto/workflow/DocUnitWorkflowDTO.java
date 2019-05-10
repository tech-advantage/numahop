package fr.progilone.pgcn.domain.dto.workflow;

import java.time.LocalDateTime;
import java.util.List;

import fr.progilone.pgcn.domain.dto.AbstractVersionedDTO;

public class DocUnitWorkflowDTO extends AbstractVersionedDTO {

    private String identifier;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<DocUnitStateDTO> states;

    public DocUnitWorkflowDTO() {

    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public List<DocUnitStateDTO> getStates() {
        return states;
    }

    public void setStates(List<DocUnitStateDTO> states) {
        this.states = states;
    }
}
