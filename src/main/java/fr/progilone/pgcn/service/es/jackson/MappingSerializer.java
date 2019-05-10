package fr.progilone.pgcn.service.es.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import fr.progilone.pgcn.domain.exchange.Mapping;

import java.io.IOException;

public class MappingSerializer extends StdSerializer<Mapping> {

    public MappingSerializer() {
        super(Mapping.class);
    }

    @Override
    public void serialize(final Mapping mapping, final JsonGenerator jgen, final SerializerProvider serializerProvider) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField("label", mapping.getLabel());
        jgen.writeObjectField("type", mapping.getType());
        jgen.writeStringField("joinExpression", mapping.getJoinExpression());
        jgen.writeObjectField("rules", mapping.getRules());
        jgen.writeEndObject();
    }
}
