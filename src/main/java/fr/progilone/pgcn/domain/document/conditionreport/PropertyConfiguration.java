package fr.progilone.pgcn.domain.document.conditionreport;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.library.Library;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

/**
 * Paramétrage d'une propriété de constat d'état, par bibliothèque
 */
@Entity
@Table(name = PropertyConfiguration.TABLE_NAME)
@JsonSubTypes({@JsonSubTypes.Type(name = "doc_condreport_property_config", value = PropertyConfiguration.class)})
public class PropertyConfiguration extends AbstractDomainObject {

    public static final String TABLE_NAME = "doc_condreport_property_config";
    public static final String TABLE_NAME_TYPE = "doc_condreport_property_config_type";

    public enum InternalProperty {
        BINDING_DESC,
        BODY_DESC,
        DIMENSION
    }

    /**
     * la propriété associée est obligatoire
     */
    @Column(name = "required")
    private boolean required = false;

    /**
     * saisie d'une description succinte pour la propriété associée
     */
    @Column(name = "allow_comment")
    private boolean allowComment = false;

    @Column(name = "show_on_creation")
    private boolean showOnCreation = true;

    /**
     * types de constats d'état sur lesquels ce champ apparait à la création
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = TABLE_NAME_TYPE, joinColumns = @JoinColumn(name = "conf"))
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private final Set<DocUnit.CondReportType> types = new HashSet<>();

    /**
     * Bib concernée
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "library")
    private Library library;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "desc_property")
    private DescriptionProperty descProperty;

    @Column(name = "internal_prop")
    @Enumerated(EnumType.STRING)
    private InternalProperty internalProperty;

    public boolean isRequired() {
        return required;
    }

    public void setRequired(final boolean required) {
        this.required = required;
    }

    public boolean isAllowComment() {
        return allowComment;
    }

    public void setAllowComment(final boolean allowComment) {
        this.allowComment = allowComment;
    }

    public boolean isShowOnCreation() {
        return showOnCreation;
    }

    public void setShowOnCreation(final boolean showOnCreation) {
        this.showOnCreation = showOnCreation;
    }

    public Set<DocUnit.CondReportType> getTypes() {
        return types;
    }

    public void setTypes(final Set<DocUnit.CondReportType> types) {
        this.types.clear();
        if (types != null) {
            types.forEach(this::addType);
        }
    }

    private void addType(final DocUnit.CondReportType type) {
        if (type != null) {
            this.types.add(type);
        }
    }

    public Library getLibrary() {
        return library;
    }

    public void setLibrary(final Library library) {
        this.library = library;
    }

    public DescriptionProperty getDescProperty() {
        return descProperty;
    }

    public void setDescProperty(final DescriptionProperty descProperty) {
        this.descProperty = descProperty;
    }

    public InternalProperty getInternalProperty() {
        return internalProperty;
    }

    public void setInternalProperty(final InternalProperty internalProperty) {
        this.internalProperty = internalProperty;
    }
}
