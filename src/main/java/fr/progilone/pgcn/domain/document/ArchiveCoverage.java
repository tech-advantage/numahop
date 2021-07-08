package fr.progilone.pgcn.domain.document;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonSubTypes;

import fr.progilone.pgcn.domain.AbstractDomainObject;

/**
 * Classe représentant un coverage d'un objet d'export IA
 */
@Entity
@Table(name = ArchiveCoverage.TABLE_NAME)
@JsonSubTypes({@JsonSubTypes.Type(name = "doc_archive_coverage", value = ArchiveCoverage.class)})
public class ArchiveCoverage extends AbstractDomainObject {

    public static final String TABLE_NAME = "doc_archive_coverage";

    /**
     * Valeur
     */
    @Column(name = "value", columnDefinition = "text")
    private String value;

    /**
     * Notice rattachée
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "item")
    private ArchiveItem item;

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public ArchiveItem getItem() {
        return item;
    }

    public void setItem(final ArchiveItem item) {
        this.item = item;
    }
}
