package fr.progilone.pgcn.domain.document.conditionreport;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.google.common.base.MoreObjects;

import fr.progilone.pgcn.domain.AbstractDomainObject;

/**
 * Propriété d'état de la reliure
 */
@Entity
@Table(name = DescriptionProperty.TABLE_NAME)
@JsonSubTypes({@JsonSubTypes.Type(name = "doc_condreport_desc_property", value = DescriptionProperty.class)})
public class DescriptionProperty extends AbstractDomainObject {

    public static final String TABLE_NAME = "doc_condreport_desc_property";

    /**
     * Type de propriété
     */
    public enum Type {
        BINDING,
        DESCRIPTION,
        NUMBERING,
        VIGILANCE
    }

    /**
     * Libellé de la propriété
     */
    @Column(name = "label", nullable = false)
    private String label;

    /**
     * Code de la propriété
     */
    @Column(name = "code", nullable = false)
    private String code;

    /**
     * Type de la propriété
     */
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    /**
     * Configuration par défaut: commentaire saisissable pour ce champ
     */
    @Column(name = "allow_comment")
    private boolean allowComment = false;

    /**
     * Configuration spécifique par bibliothèque,
     * écrase les configurations par défaut
     */
    @OneToMany(mappedBy = "descProperty", orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private final Set<PropertyConfiguration> configurations = new HashSet<>();
    
    
    /**
     * numero d'ordre
     */
    @Column(name = "sort_order")
    private int order;
    

    public Type getType() {
        return type;
    }

    public void setType(final Type type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public boolean isAllowComment() {
        return allowComment;
    }

    public void setAllowComment(final boolean allowComment) {
        this.allowComment = allowComment;
    }

    public Set<PropertyConfiguration> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(final Set<PropertyConfiguration> configurations) {
        this.configurations.clear();
        if (configurations != null) {
            configurations.forEach(this::addConfiguration);
        }
    }

    public void addConfiguration(final PropertyConfiguration configuration) {
        if (configuration != null) {
            this.configurations.add(configuration);
        }
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(final int order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("label", label).toString();
    }
}
