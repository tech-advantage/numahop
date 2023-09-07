package fr.progilone.pgcn.domain.document;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import fr.progilone.pgcn.domain.AbstractDomainObject;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Classe représentant une propriété d'une unité documentaire
 *
 * @author jbrunet
 */
@Entity
@Table(name = DocProperty.TABLE_NAME)
@JsonSubTypes({@JsonSubTypes.Type(name = "doc_property", value = DocProperty.class)})
public class DocProperty extends AbstractDomainObject implements Comparable<DocProperty> {

    public static final String TABLE_NAME = "doc_property";

    /**
     * Valeur
     */
    @Column(name = "value", columnDefinition = "text")
    private String value;

    /**
     * Type de propriété
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "type")
    private DocPropertyType type;

    /**
     * Notice rattachée
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "record")
    private BibliographicRecord record;

    /**
     * Langue
     */
    @Column(name = "language")
    private String language;

    /**
     * Rang
     */
    @Column(name = "rank", nullable = false)
    private Integer rank;

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public DocPropertyType getType() {
        return type;
    }

    public void setType(final DocPropertyType type) {
        this.type = type;
    }

    public BibliographicRecord getRecord() {
        return record;
    }

    public void setRecord(final BibliographicRecord record) {
        this.record = record;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(final String language) {
        this.language = language;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(final Integer rank) {
        this.rank = rank;
    }

    @Override
    public int compareTo(final DocProperty property) {
        if (property != null) {
            if (this.rank != null) {
                return this.rank.compareTo(property.getRank());
            }
            return -1;
        }
        return 1;
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
