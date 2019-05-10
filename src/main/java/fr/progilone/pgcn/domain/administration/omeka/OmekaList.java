package fr.progilone.pgcn.domain.administration.omeka;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

import fr.progilone.pgcn.domain.AbstractDomainObject;

/**
 * Listes attachées à la conf Omeka.
 *
 */
@Entity
@Table(name = OmekaList.TABLE_NAME)
public class OmekaList extends AbstractDomainObject {
    
    public static final String TABLE_NAME = "conf_lists_omeka";
    
    @Column(name = "name", nullable = false)
    @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
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
