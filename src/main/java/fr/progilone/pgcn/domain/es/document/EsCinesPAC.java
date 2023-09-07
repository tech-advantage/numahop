package fr.progilone.pgcn.domain.es.document;

import fr.progilone.pgcn.domain.administration.CinesPAC;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

public class EsCinesPAC {

    @Field(type = FieldType.Keyword)
    private String identifier;

    @Field(type = FieldType.Keyword)
    private String name;

    public static EsCinesPAC from(final CinesPAC cinesPAC) {
        final EsCinesPAC esCinesPAC = new EsCinesPAC();
        esCinesPAC.setIdentifier(cinesPAC.getIdentifier());
        esCinesPAC.setName(cinesPAC.getName());
        return esCinesPAC;
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
