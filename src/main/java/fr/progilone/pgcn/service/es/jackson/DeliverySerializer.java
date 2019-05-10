package fr.progilone.pgcn.service.es.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import fr.progilone.pgcn.domain.delivery.Delivery;
import fr.progilone.pgcn.domain.delivery.Delivery_;

import java.io.IOException;

public class DeliverySerializer extends StdSerializer<Delivery> {

    public DeliverySerializer() {
        super(Delivery.class);
    }

    @Override
    public void serialize(final Delivery delivery, final JsonGenerator jgen, final SerializerProvider serializerProvider) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField(Delivery_.identifier.getName(), delivery.getIdentifier());
        jgen.writeStringField(Delivery_.lotId.getName(), delivery.getLotId());
        jgen.writeStringField(Delivery_.label.getName(), delivery.getLabel());
        jgen.writeObjectField(Delivery_.status.getName(), delivery.getStatus());
        jgen.writeObjectField(Delivery_.payment.getName(), delivery.getPayment());
        jgen.writeObjectField(Delivery_.status.getName(), delivery.getStatus());
        jgen.writeObjectField(Delivery_.method.getName(), delivery.getMethod());
        jgen.writeObjectField(Delivery_.receptionDate.getName(), delivery.getReceptionDate());
        jgen.writeNumberField(Delivery_.documentCount.getName(), delivery.getDocuments().size());
        jgen.writeEndObject();
    }
}
