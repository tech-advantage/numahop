package fr.progilone.pgcn.domain.administration;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Configuration des connexions Internet Archive
 *
 * @author jbrunet
 * Créé le 18 avr. 2017
 */
@Entity
@Table(name = InternetArchiveCollection.TABLE_NAME)
public class InternetArchiveCollection extends AbstractDomainObject {

    public static final String TABLE_NAME = "conf_collection_ia";

    @Column(name = "name", nullable = false)
    @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
    private String name;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "conf_ia")
    private InternetArchiveConfiguration confIa;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public InternetArchiveConfiguration getConfIa() {
        return confIa;
    }

    public void setConfIa(InternetArchiveConfiguration confIa) {
        this.confIa = confIa;
    }
}
