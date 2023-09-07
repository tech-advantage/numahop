package fr.progilone.pgcn.domain.library;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

/**
 * Classe représentant les paramètres pour l'export Cines
 *
 * @author jbrunet
 *         Créé le 23 févr. 2017
 */
@Entity
@Table(name = LibraryParameterValueCines.TABLE_NAME)
public class LibraryParameterValueCines extends AbstractLibraryParameterValue {

    public static final String TABLE_NAME = "lib_parameter_value_cines";

    /**
     * Type de paramètre
     */
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private LibraryParameterValueCinesType type;

    /**
     * Valeur correspondante
     */
    @Column(name = "value", columnDefinition = "text")
    private String value;

    public LibraryParameterValueCinesType getType() {
        return type;
    }

    public void setType(final LibraryParameterValueCinesType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    /**
     * Champ à remplir si non trouvés
     *
     * @author jbrunet
     *         Créé le 27 févr. 2017
     */
    public enum LibraryParameterValueCinesType {
        TITLE_DEFAULT_VALUE, /* Valeur par défaut pour le champ title */
        CREATOR_DEFAULT_VALUE, /* Valeur par défaut pour le champ creator */
        SUBJECT_DEFAULT_VALUE, /* Valeur par défaut pour le champ subject */
        PUBLISHER_DEFAULT_VALUE, /* Valeur par défaut pour le champ publisher */
        DESCRIPTION_DEFAULT_VALUE, /* Valeur par défaut pour le champ description */
        TYPE_DEFAULT_VALUE, /* Valeur par défaut pour le champ type */
        FORMAT_DEFAULT_VALUE, /* Valeur par défaut pour le champ format */
        LANGUAGE_DEFAULT_VALUE, /* Valeur par défaut pour le champ language */
        RIGHTS_DEFAULT_VALUE, /* Valeur par défaut pour le champ rights */
    }
}
