package fr.progilone.pgcn.domain.administration;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Configuration des connexions Internet Archive
 *
 * @author jbrunet
 *         Créé le 18 avr. 2017
 */
@Entity
@Table(name = InternetArchiveCollection.TABLE_NAME)
public class InternetArchiveCollection extends AbstractDomainObject {

    public static final String TABLE_NAME = "conf_collection_ia";

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "conf_ia")
    private InternetArchiveConfiguration confIa;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public InternetArchiveConfiguration getConfIa() {
        return confIa;
    }

    public void setConfIa(final InternetArchiveConfiguration confIa) {
        this.confIa = confIa;
    }
}
