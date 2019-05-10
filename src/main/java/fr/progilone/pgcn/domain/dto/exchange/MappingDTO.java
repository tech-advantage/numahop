package fr.progilone.pgcn.domain.dto.exchange;

import fr.progilone.pgcn.domain.dto.library.SimpleLibraryDTO;

import java.util.Objects;

/**
 * DTO d'un mapping, sans les règles
 * Created by Sebastien on 13/12/2016.
 */
public class MappingDTO {

    private String identifier;
    private String joinExpression;
    private String label;
    private SimpleLibraryDTO library;
    private String type;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public String getJoinExpression() {
        return joinExpression;
    }

    public void setJoinExpression(final String joinExpression) {
        this.joinExpression = joinExpression;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public SimpleLibraryDTO getLibrary() {
        return library;
    }

    public void setLibrary(final SimpleLibraryDTO library) {
        this.library = library;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final MappingDTO that = (MappingDTO) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }
}
