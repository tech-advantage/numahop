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
 * Classe représentant un creator d'un objet d'export IA
 */
@Entity
@Table(name = ArchiveCreator.TABLE_NAME)
@JsonSubTypes({@JsonSubTypes.Type(name = "doc_archive_creator", value = ArchiveCreator.class)})
public class ArchiveCreator extends AbstractDomainObject {

    public static final String TABLE_NAME = "doc_archive_creator";

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
