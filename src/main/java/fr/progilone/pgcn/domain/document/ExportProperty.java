package fr.progilone.pgcn.domain.document;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import fr.progilone.pgcn.domain.AbstractDomainObject;

import javax.persistence.*;

/**
 * Classe représentant une propriété d'une unité documentaire
 *
 * @author jbrunet
 */
@Entity
@Table(name = ExportProperty.TABLE_NAME)
@JsonSubTypes({@JsonSubTypes.Type(name = "doc_export_property", value = ExportProperty.class)})
public class ExportProperty extends AbstractDomainObject implements Comparable<ExportProperty> {

    public static final String TABLE_NAME = "doc_export_property";

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
    private ExportData record;

    /**
     * Rang
     */
    @Column(name = "rank", nullable = false)
    private Integer rank;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public DocPropertyType getType() {
        return type;
    }

    public void setType(DocPropertyType type) {
        this.type = type;
    }

    public ExportData getRecord() {
        return record;
    }

    public void setRecord(ExportData record) {
        this.record = record;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    @Override
    public int compareTo(ExportProperty property) {
        if (property != null) {
            if (this.rank != null) {
                return this.rank.compareTo(property.getRank());
            }
            return -1;
        }
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
