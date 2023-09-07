package fr.progilone.pgcn.domain.dto.library;

/**
 * DTO représentant une valeur de paramétrage du cines
 *
 * @author jbrunet
 *         Créé le 24 févr. 2017
 */
public class LibraryParameterValueCinesDTO extends AbstractLibraryParameterValueDTO {

    private String type;
    private String value;

    public LibraryParameterValueCinesDTO() {
        super();
    }

    public final String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public final String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
