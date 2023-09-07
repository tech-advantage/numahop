package fr.progilone.pgcn.domain.exchange.cines;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

// Hibernate
@Entity
@Table(name = CinesLanguageCode.TABLE_NAME)
public class CinesLanguageCode extends AbstractDomainObject {

    public static final String TABLE_NAME = "conf_cines_lang_code";

    @Column(name = "label")
    private String label;

    /**
     * Etat
     */
    @Column(name = "active")
    private boolean active;

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

}
