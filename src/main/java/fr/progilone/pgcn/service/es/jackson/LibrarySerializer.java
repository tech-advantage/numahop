package fr.progilone.pgcn.service.es.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.library.Library_;

import java.io.IOException;

public class LibrarySerializer extends StdSerializer<Library> {

    public LibrarySerializer() {
        super(Library.class);
    }

    @Override
    public void serialize(final Library library, final JsonGenerator jgen, final SerializerProvider serializerProvider) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField(Library_.identifier.getName(), library.getIdentifier());
        jgen.writeStringField(Library_.name.getName(), library.getName());
        jgen.writeEndObject();
    }
}
