package fr.progilone.pgcn.service.es.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.domain.lot.Lot_;

import java.io.IOException;

public class LotSerializer extends StdSerializer<Lot> {

    public LotSerializer() {
        super(Lot.class);
    }

    @Override
    public void serialize(final Lot lot, final JsonGenerator jgen, final SerializerProvider serializerProvider) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField(Lot_.identifier.getName(), lot.getIdentifier());
        jgen.writeStringField(Lot_.projectId.getName(), lot.getProjectId());
        jgen.writeStringField(Lot_.label.getName(), lot.getLabel());
        jgen.writeObjectField(Lot_.provider.getName(), lot.getProvider());
        jgen.writeBooleanField(Lot_.active.getName(), lot.isActive());
        jgen.writeObjectField(Lot_.status.getName(), lot.getStatus());
        jgen.writeObjectField(Lot_.type.getName(), lot.getType());
        jgen.writeStringField(Lot_.requiredFormat.getName(), lot.getRequiredFormat());
        jgen.writeEndObject();
    }
}
