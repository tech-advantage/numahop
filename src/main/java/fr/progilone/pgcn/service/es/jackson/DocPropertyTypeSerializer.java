package fr.progilone.pgcn.service.es.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import fr.progilone.pgcn.domain.document.DocPropertyType;
import fr.progilone.pgcn.domain.document.DocPropertyType_;

import java.io.IOException;

public class DocPropertyTypeSerializer extends StdSerializer<DocPropertyType> {

    public DocPropertyTypeSerializer() {
        super(DocPropertyType.class);
    }

    @Override
    public void serialize(final DocPropertyType propertyType, final JsonGenerator jgen, final SerializerProvider serializerProvider) throws
                                                                                                                                     IOException {
        jgen.writeStartObject();
        jgen.writeStringField(DocPropertyType_.identifier.getName(), propertyType.getIdentifier());
        jgen.writeEndObject();
    }
}
