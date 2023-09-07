package fr.progilone.pgcn.domain.es.document;

import fr.progilone.pgcn.domain.administration.InternetArchiveCollection;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Configuration des connexions Internet Archive
 */
public class EsInternetArchiveCollection {

    @Field(type = FieldType.Keyword)
    private String identifier;

    @Field(type = FieldType.Keyword)
    private String name;

    public static EsInternetArchiveCollection from(final InternetArchiveCollection collection) {
        final EsInternetArchiveCollection esCollection = new EsInternetArchiveCollection();
        esCollection.setIdentifier(collection.getIdentifier());
        esCollection.setName(collection.getName());
        return esCollection;
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
