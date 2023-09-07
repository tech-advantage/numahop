package fr.progilone.pgcn.domain.document;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import fr.progilone.pgcn.domain.AbstractDomainObject;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Classe représentant un type propriété d'une notice
 *
 * @author jbrunet
 */
@Entity
@Table(name = DocPropertyType.TABLE_NAME)
@JsonSubTypes({@JsonSubTypes.Type(name = "doc_property_type", value = DocPropertyType.class)})
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class DocPropertyType extends AbstractDomainObject {

    public static final String TABLE_NAME = "doc_property_type";

    /**
     * Label
     */
    @Column(name = "label")
    private String label;

    /**
     * Supertype de propriété
     */
    @Column(name = "supertype")
    @Enumerated(EnumType.STRING)
    private DocPropertySuperType superType;

    @OneToMany(mappedBy = "type", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private final Set<DocProperty> docProperties = new HashSet<>();

    /**
     * Rang
     */
    @Column(name = "rank", nullable = false)
    private Integer rank;

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public DocPropertySuperType getSuperType() {
        return superType;
    }

    public void setSuperType(final DocPropertySuperType superType) {
        this.superType = superType;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(final Integer rank) {
        this.rank = rank;
    }

    public Set<DocProperty> getDocProperties() {
        return docProperties;
    }

    public enum DocPropertySuperType {
        DC, /* champs du dublin core */
        DCQ, /* champs du dublin core qualified */
        CUSTOM, /* champs ajoutés (général) */
        CUSTOM_CINES, /* champs ajoutés (sp. CINES) */
        CUSTOM_ARCHIVE, /* champs ajoutés (sp. Archive) */
        CUSTOM_OMEKA /* champs ajoutés (sp. Omeka) */
    }
}
