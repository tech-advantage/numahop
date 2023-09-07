package fr.progilone.pgcn.domain.es.library;

import fr.progilone.pgcn.domain.library.Library;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Classe métier permettant de gérer les bibliothèques.
 */
public class EsLibrary {

    @Field(type = FieldType.Keyword)
    private String identifier;

    /**
     * Nom de la bibliothèque
     */
    @Field(type = FieldType.Keyword)
    private String name;

    public static EsLibrary from(final Library library) {
        final EsLibrary esLibrary = new EsLibrary();
        esLibrary.setIdentifier(library.getIdentifier());
        esLibrary.setName(library.getName());
        return esLibrary;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

}
