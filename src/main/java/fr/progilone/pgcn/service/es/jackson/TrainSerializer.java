package fr.progilone.pgcn.service.es.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import fr.progilone.pgcn.domain.train.Train;
import fr.progilone.pgcn.domain.train.Train_;

import java.io.IOException;

public class TrainSerializer extends StdSerializer<Train> {

    public TrainSerializer() {
        super(Train.class);
    }

    @Override
    public void serialize(final Train train, final JsonGenerator jgen, final SerializerProvider serializerProvider) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField(Train_.identifier.getName(), train.getIdentifier());
        jgen.writeStringField(Train_.projectId.getName(), train.getProjectId());
        jgen.writeStringField(Train_.label.getName(), train.getLabel());
        jgen.writeBooleanField(Train_.active.getName(), train.isActive());
        jgen.writeObjectField(Train_.status.getName(), train.getStatus());
        jgen.writeObjectField(Train_.providerSendingDate.getName(), train.getProviderSendingDate());
        jgen.writeObjectField(Train_.returnDate.getName(), train.getReturnDate());
        jgen.writeEndObject();
    }
}
