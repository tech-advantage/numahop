package fr.progilone.pgcn.domain.imagemetadata;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.document.DocUnit;
import jakarta.persistence.*;

/**
 * Classe représentant les valeurs des metadonnées configurées liées aux UDs
 *
 */
// Hibernate
@Entity
@Table(name = ImageMetadataValue.TABLE_NAME)
@JsonSubTypes({@JsonSubTypes.Type(name = "image_metadata_values", value = ImageMetadataValue.class)})
public class ImageMetadataValue extends AbstractDomainObject {

    public static final String TABLE_NAME = "image_metadata_values";

    @ManyToOne(optional = false)
    @JoinColumn(name = "doc_unit")
    private DocUnit docUnit;

    @ManyToOne(optional = false)
    @JoinColumn(name = "metadata")
    private ImageMetadataProperty metadata;

    @Column(name = "value")
    private String value;

    public DocUnit getDocUnit() {
        return docUnit;
    }

    public void setDocUnit(DocUnit docUnit) {
        this.docUnit = docUnit;
    }

    public ImageMetadataProperty getMetadata() {
        return metadata;
    }

    public void setMetadata(ImageMetadataProperty metadata) {
        this.metadata = metadata;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
