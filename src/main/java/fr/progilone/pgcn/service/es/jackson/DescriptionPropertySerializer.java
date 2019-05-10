package fr.progilone.pgcn.service.es.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import fr.progilone.pgcn.domain.document.conditionreport.DescriptionProperty;
import fr.progilone.pgcn.domain.document.conditionreport.DescriptionProperty_;

import java.io.IOException;

public class DescriptionPropertySerializer extends StdSerializer<DescriptionProperty> {

    public DescriptionPropertySerializer() {
        super(DescriptionProperty.class);
    }

    @Override
    public void serialize(final DescriptionProperty property, final JsonGenerator jgen, final SerializerProvider serializerProvider) throws
                                                                                                                                     IOException {
        jgen.writeStartObject();
        jgen.writeStringField(DescriptionProperty_.identifier.getName(), property.getIdentifier());
        jgen.writeEndObject();
    }
}
