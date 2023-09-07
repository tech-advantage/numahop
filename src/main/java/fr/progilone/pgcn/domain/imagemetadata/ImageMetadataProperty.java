package fr.progilone.pgcn.domain.imagemetadata;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import fr.progilone.pgcn.domain.AbstractDomainObject;
import jakarta.persistence.*;

/**
 * Classe représentant les metadonnées configurées
 *
 */
// Hibernate
@Entity
@Table(name = ImageMetadataProperty.TABLE_NAME)
@JsonSubTypes({@JsonSubTypes.Type(name = "image", value = ImageMetadataProperty.class)})
public class ImageMetadataProperty extends AbstractDomainObject {

    public static final String TABLE_NAME = "image_metadata_properties";

    @Column(name = "label")
    private String label;

    @Column(name = "meta_repeat")
    private boolean repeat;

    @Column(name = "meta_type")
    @Enumerated(EnumType.STRING)
    private metaType type;

    @Column(name = "iptc_tag")
    private String iptcTag;

    @Column(name = "xmp_tag")
    private String xmpTag;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public metaType getType() {
        return type;
    }

    public void setType(metaType type) {
        this.type = type;
    }

    public String getIptcTag() {
        return iptcTag;
    }

    public void setIptcTag(String iptcTag) {
        this.iptcTag = iptcTag;
    }

    public String getXmpTag() {
        return xmpTag;
    }

    public void setXmpTag(String xmpTag) {
        this.xmpTag = xmpTag;
    }

    /**
     * type de metadonnée
     */
    public enum metaType {
        /**
         * Chaine de caractères
         */
        STRING,
        /**
         * Entier
         */
        INTEGER,
        /**
         * Réel
         */
        REAL,
        /**
         * Booléen
         */
        BOOLEAN,
        /**
         * Date
         */
        DATE
    }
}
