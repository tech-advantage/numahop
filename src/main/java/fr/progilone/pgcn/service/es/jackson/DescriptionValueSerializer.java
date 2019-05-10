package fr.progilone.pgcn.service.es.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import fr.progilone.pgcn.domain.document.conditionreport.DescriptionValue;
import fr.progilone.pgcn.domain.document.conditionreport.DescriptionValue_;

import java.io.IOException;

public class DescriptionValueSerializer extends StdSerializer<DescriptionValue> {

    public DescriptionValueSerializer() {
        super(DescriptionValue.class);
    }

    @Override
    public void serialize(final DescriptionValue value, final JsonGenerator jgen, final SerializerProvider serializerProvider) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField(DescriptionValue_.identifier.getName(), value.getIdentifier());
        jgen.writeStringField(DescriptionValue_.label.getName(), value.getLabel());
        jgen.writeEndObject();
    }
}
