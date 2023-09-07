package fr.progilone.pgcn.domain.dto.check;

import fr.progilone.pgcn.domain.check.AutomaticCheckType;
import fr.progilone.pgcn.domain.check.AutomaticCheckType.AutoCheckType;
import fr.progilone.pgcn.domain.dto.AbstractDTO;

/**
 * Type de contrôles automatiques
 *
 * @author jbrunet
 *         Créé le 2 mars 2017
 */
public class AutomaticCheckTypeDTO extends AbstractDTO {

    private String identifier;
    private String label;
    private AutomaticCheckType.AutoCheckType type;
    private Integer order;
    private boolean active;

    public String getIdentifier() {
        return identifier;
    }

    public AutoCheckType getType() {
        return type;
    }

    public void setType(final AutoCheckType type) {
        this.type = type;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(final Integer order) {
        this.order = order;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

}
