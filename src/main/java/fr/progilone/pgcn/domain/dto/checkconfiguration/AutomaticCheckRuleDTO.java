package fr.progilone.pgcn.domain.dto.checkconfiguration;

import fr.progilone.pgcn.domain.dto.AbstractVersionedDTO;
import fr.progilone.pgcn.domain.dto.check.AutomaticCheckTypeDTO;

public class AutomaticCheckRuleDTO extends AbstractVersionedDTO {

    private String identifier;
    private AutomaticCheckTypeDTO automaticCheckType;
    private Boolean active;
    private Boolean blocking;
    private Boolean readOnly = false;

    public AutomaticCheckRuleDTO(final String identifier, 
                                 final AutomaticCheckTypeDTO automaticCheckType, final Boolean active, final Boolean blocking, final Boolean readOnly) {

        this.identifier = identifier;
        this.automaticCheckType = automaticCheckType;
        this.active = active;
        this.blocking = blocking;
        this.readOnly = readOnly;
    }

    public AutomaticCheckRuleDTO() {

    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }


    public AutomaticCheckTypeDTO getAutomaticCheckType() {
        return automaticCheckType;
    }

    public void setAutomaticCheckType(AutomaticCheckTypeDTO automaticCheckType) {
        this.automaticCheckType = automaticCheckType;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getBlocking() {
        return blocking;
    }

    public void setBlocking(Boolean blocking) {
        this.blocking = blocking;
    }

    public Boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }

}
