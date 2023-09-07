package fr.progilone.pgcn.domain.document;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.library.Library;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

// Hibernate
@Entity
@Table(name = BibliographicRecord.TABLE_NAME)
// Jackson
@JsonSubTypes({@JsonSubTypes.Type(name = "doc_bibliographic_record", value = BibliographicRecord.class)})
public class BibliographicRecord extends AbstractDomainObject {

    public static final String TABLE_NAME = "doc_bibliographic_record";

    /**
     * Titre récupéré des propriétés
     */
    @Column(name = "title", columnDefinition = "text")
    private String title;

    /**
     * URL vers le SIGB
     */
    @Column(name = "sigb")
    private String sigb;

    /**
     * identifiant SUDOC
     */
    @Column
    private String sudoc;

    /**
     * identifiant Calames
     */
    @Column
    private String calames;

    /**
     * URL document électronique
     */
    @Column
    private String docElectronique;

    /**
     * Liste des propriétés de la notice (DC, DCq, Custom)
     */
    @OneToMany(mappedBy = "record", orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private final Set<DocProperty> properties = new HashSet<>();

    /**
     * Unité documentaire rattachée
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "doc_unit")
    private DocUnit docUnit;

    /**
     * Bibliothèque de rattachement
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "library")
    private Library library;

    /**
     * Type de tri des propriétés
     */
    @Column(name = "property_order", nullable = false)
    @Enumerated(EnumType.STRING)
    private PropertyOrder propertyOrder = PropertyOrder.BY_PROPERTY_TYPE;

    public DocUnit getDocUnit() {
        return docUnit;
    }

    public void setDocUnit(final DocUnit docUnit) {
        this.docUnit = docUnit;
    }

    public Library getLibrary() {
        return library;
    }

    public void setLibrary(final Library library) {
        this.library = library;
    }

    public Set<DocProperty> getProperties() {
        return properties;
    }

    public void setProperties(final Set<DocProperty> properties) {
        this.properties.clear();
        if (properties != null) {
            properties.forEach(this::addProperty);
        }
    }

    public void addProperty(final DocProperty property) {
        if (property != null) {
            this.properties.add(property);
            property.setRecord(this);
        }
    }

    public String getSigb() {
        return sigb;
    }

    public void setSigb(final String sigb) {
        this.sigb = sigb;
    }

    public String getSudoc() {
        return sudoc;
    }

    public void setSudoc(final String sudoc) {
        this.sudoc = sudoc;
    }

    public String getCalames() {
        return calames;
    }

    public void setCalames(final String calames) {
        this.calames = calames;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getDocElectronique() {
        return docElectronique;
    }

    public void setDocElectronique(final String docElectronique) {
        this.docElectronique = docElectronique;
    }

    public PropertyOrder getPropertyOrder() {
        return propertyOrder;
    }

    public void setPropertyOrder(final PropertyOrder propertyOrder) {
        this.propertyOrder = propertyOrder;
    }

    // On redéfinit la lastModifiedDate pour l'indexation
    @Transient
    public LocalDateTime getGeneralLastModifiedDate() {
        return Stream.concat(Stream.of(getLastModifiedDate()), this.properties.stream().map(AbstractDomainObject::getLastModifiedDate)).max(LocalDateTime::compareTo).orElse(null);
    }

    public enum PropertyOrder {
        /**
         * Les propriétés sont ordonnées suivant l'ordre de création
         */
        BY_CREATION,
        /**
         * Les propriétés sont ordonnées suivant l'ordre des types de propriété
         */
        BY_PROPERTY_TYPE
    }
}
