package fr.progilone.pgcn.domain.document;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import fr.progilone.pgcn.domain.AbstractDomainObject;
import jakarta.persistence.*;

/**
 * Classe représentant un entête personnalisé d'un objet d'export IA
 */
@Entity
@Table(name = ArchiveHeader.TABLE_NAME)
@JsonSubTypes({@JsonSubTypes.Type(name = "doc_archive_header", value = ArchiveHeader.class)})
public class ArchiveHeader extends AbstractDomainObject {

    public static final String TABLE_NAME = "doc_archive_header";

    /**
     * Valeur
     */
    @Column(name = "value", columnDefinition = "text")
    private String value;

    /**
     * Type
     */
    @Column(name = "type", columnDefinition = "text")
    private String type;

    /**
     * Notice rattachée
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "item")
    private ArchiveItem item;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public ArchiveItem getItem() {
        return item;
    }

    public void setItem(ArchiveItem item) {
        this.item = item;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
