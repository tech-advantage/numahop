package fr.progilone.pgcn.service.es.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import fr.progilone.pgcn.domain.administration.CinesPAC;
import fr.progilone.pgcn.domain.administration.CinesPAC_;

import java.io.IOException;

public class CinesPACSerializer extends StdSerializer<CinesPAC> {

    public CinesPACSerializer() {
        super(CinesPAC.class);
    }

    @Override
    public void serialize(final CinesPAC cinesPAC, final JsonGenerator jgen, final SerializerProvider serializerProvider) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField(CinesPAC_.identifier.getName(), cinesPAC.getIdentifier());
        jgen.writeStringField(CinesPAC_.name.getName(), cinesPAC.getName());
        jgen.writeEndObject();
    }
}
