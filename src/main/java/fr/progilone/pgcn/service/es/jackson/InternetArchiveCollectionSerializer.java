package fr.progilone.pgcn.service.es.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import fr.progilone.pgcn.domain.administration.InternetArchiveCollection;
import fr.progilone.pgcn.domain.administration.InternetArchiveCollection_;

import java.io.IOException;

public class InternetArchiveCollectionSerializer extends StdSerializer<InternetArchiveCollection> {

    public InternetArchiveCollectionSerializer() {
        super(InternetArchiveCollection.class);
    }

    @Override
    public void serialize(final InternetArchiveCollection iaCollection, final JsonGenerator jgen, final SerializerProvider serializerProvider) throws
                                                                                                                                               IOException {
        jgen.writeStartObject();
        jgen.writeStringField(InternetArchiveCollection_.identifier.getName(), iaCollection.getIdentifier());
        jgen.writeStringField(InternetArchiveCollection_.name.getName(), iaCollection.getName());
        jgen.writeEndObject();
    }
}
