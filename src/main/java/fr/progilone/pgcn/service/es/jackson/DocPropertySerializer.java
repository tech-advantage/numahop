package fr.progilone.pgcn.service.es.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import fr.progilone.pgcn.domain.document.DocProperty;
import fr.progilone.pgcn.domain.document.DocProperty_;

import java.io.IOException;

public class DocPropertySerializer extends StdSerializer<DocProperty> {

    public DocPropertySerializer() {
        super(DocProperty.class);
    }

    @Override
    public void serialize(final DocProperty property, final JsonGenerator jgen, final SerializerProvider serializerProvider) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField(DocProperty_.identifier.getName(), property.getIdentifier());
        jgen.writeStringField(DocProperty_.value.getName(), property.getValue());
        jgen.writeObjectField(DocProperty_.type.getName(), property.getType());
        jgen.writeStringField(DocProperty_.language.getName(), property.getLanguage());
        jgen.writeObjectField(DocProperty_.rank.getName(), property.getRank());
        jgen.writeEndObject();
    }
}
