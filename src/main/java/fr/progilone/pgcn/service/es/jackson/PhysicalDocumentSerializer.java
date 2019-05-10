package fr.progilone.pgcn.service.es.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import fr.progilone.pgcn.domain.document.PhysicalDocument;
import fr.progilone.pgcn.domain.document.PhysicalDocument_;

import java.io.IOException;

public class PhysicalDocumentSerializer extends StdSerializer<PhysicalDocument> {

    public PhysicalDocumentSerializer() {
        super(PhysicalDocument.class);
    }

    @Override
    public void serialize(final PhysicalDocument doc, final JsonGenerator jgen, final SerializerProvider serializerProvider) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField(PhysicalDocument_.identifier.getName(), doc.getIdentifier());
        jgen.writeStringField(PhysicalDocument_.digitalId.getName(), doc.getDigitalId());
        jgen.writeObjectField(PhysicalDocument_.totalPage.getName(), doc.getTotalPage());
        jgen.writeEndObject();
    }
}
