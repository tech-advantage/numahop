package fr.progilone.pgcn.domain.administration.omeka;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Listes attachées à la conf Omeka.
 *
 */
@Entity
@Table(name = OmekaList.TABLE_NAME)
public class OmekaList extends AbstractDomainObject {

    public static final String TABLE_NAME = "conf_lists_omeka";

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "list_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ListType type;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "conf_omeka")
    private OmekaConfiguration confOmeka;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public ListType getType() {
        return type;
    }

    public void setType(final ListType type) {
        this.type = type;
    }

    public OmekaConfiguration getConfOmeka() {
        return confOmeka;
    }

    public void setConfOmeka(final OmekaConfiguration confOmeka) {
        this.confOmeka = confOmeka;
    }

    public enum ListType {
        COLLECTION,
        ITEM
    }

}
