package fr.progilone.pgcn.domain.document;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;

import fr.progilone.pgcn.domain.AbstractDomainObject;

/**
 * Created by lebouchp on 10/02/2017.
 */
@Entity
@Table(name = Check.TABLE_NAME)
@JsonSubTypes({@JsonSubTypes.Type(name = "doc_check", value = Check.class)})
public class Check extends AbstractDomainObject {

    public static final String TABLE_NAME = "doc_check";

    @Column(name = "err_label")
    @Enumerated(EnumType.STRING)
    private ErrorLabel errorLabel;

    @Column(name = "err_type")
    @Enumerated(EnumType.STRING)
    private ErrorType errorType;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "page")
    private DocPage page;

    public DocPage getPage() {
        return page;
    }

    public void setPage(final DocPage page) {
        this.page = page;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public void setErrorType(final ErrorType errorType) {
        this.errorType = errorType;
    }

    public ErrorLabel getErrorLabel() {
        return errorLabel;
    }

    public void setErrorLabel(final ErrorLabel errorLabel) {
        this.errorLabel = errorLabel;
    }

    public enum ErrorType {
        MAJOR,
        MINOR
    }

    public enum ErrorLabel {
        // major
        UNREADABLE("Fichier illisible", ErrorType.MAJOR),
        BAD_NAME("Non-conformité du nommage", ErrorType.MAJOR),
        BAD_HIERARCHY("Non-conformité de l'organisation", ErrorType.MAJOR),
        MISSING_PAGE("Page manquante", ErrorType.MAJOR),
        TRUNCATED_INFORMATION("Troncature d'information", ErrorType.MAJOR),
        BAD_RESOLUTION("Non-respect de la résolution", ErrorType.MAJOR),
        BAD_THRESHOLD("Non-respect du seuil", ErrorType.MAJOR),
        WRONG_FORMAT("Format non respecté", ErrorType.MAJOR),
        FOREIGN_BODIES("Corps étrangers sur l'image", ErrorType.MAJOR),
        BAD_METADATA("Format de métadonnées non respecté", ErrorType.MAJOR),
        INCONSISTENT_METADATA("Incohérence entre les métadonnées et les fichiers", ErrorType.MAJOR),
        ANOTHER_MAJ("Autre erreur majeure", ErrorType.MAJOR),
        // minor
        BLURRED_IMAGE("Image floue", ErrorType.MINOR),
        SLANT_IMAGE("Image inclinée", ErrorType.MINOR),
        HALO_ON_IMAGE("Halo sur l'image", ErrorType.MINOR),
        SHADOW_ON_IMAGE("Ombre portée", ErrorType.MINOR),
        GEOMETRICAL_ANOMALY("Distorsions géométriques", ErrorType.MINOR),
        CHROMATIC_ANOMALY("Dérive de la chromie", ErrorType.MINOR),
        WRONG_FRAMING("Cadrage inadapté", ErrorType.MINOR),
        WRONG_ORDER("Ordre des vues non respecté", ErrorType.MINOR),
        BAD_OCR("Non conformité du texte ocr", ErrorType.MINOR),
        ANOTHER_MIN("Autre erreur mineure", ErrorType.MINOR);

        ErrorLabel(final String label, final ErrorType type) {
            this.label = label;
            this.type = type;
        }

        private String label;
        private ErrorType type;

        public ErrorType getType() {
            return type;
        }

        @JsonIgnore
        public String getLabel() {
            return label;
        }
    }
}
