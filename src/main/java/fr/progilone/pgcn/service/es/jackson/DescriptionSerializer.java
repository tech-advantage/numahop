package fr.progilone.pgcn.service.es.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import fr.progilone.pgcn.domain.document.conditionreport.Description;
import fr.progilone.pgcn.domain.document.conditionreport.Description_;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class DescriptionSerializer extends StdSerializer<Description> {

    public DescriptionSerializer() {
        super(Description.class);
    }

    @Override
    public void serialize(final Description desc, final JsonGenerator jgen, final SerializerProvider serializerProvider) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField(Description_.identifier.getName(), desc.getIdentifier());
        jgen.writeObjectField(Description_.property.getName(), desc.getProperty());
        jgen.writeObjectField(Description_.value.getName(), desc.getValue());

        if (StringUtils.isNotBlank(desc.getComment())) {
            jgen.writeStringField(Description_.comment.getName(), desc.getComment());
        }
        jgen.writeEndObject();
    }
}
