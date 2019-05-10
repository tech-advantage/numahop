package fr.progilone.pgcn.service.es.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import fr.progilone.pgcn.domain.exchange.cines.CinesReport;
import fr.progilone.pgcn.domain.exchange.cines.CinesReport_;

import java.io.IOException;

public class CinesReportSerializer extends StdSerializer<CinesReport> {

    public CinesReportSerializer() {
        super(CinesReport.class);
    }

    @Override
    public void serialize(final CinesReport report, final JsonGenerator jgen, final SerializerProvider serializerProvider) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField(CinesReport_.identifier.getName(), report.getIdentifier());
        jgen.writeStringField(CinesReport_.docUnitId.getName(), report.getDocUnitId());
        jgen.writeObjectField(CinesReport_.dateSent.getName(), report.getDateSent());
        jgen.writeObjectField(CinesReport_.status.getName(), report.getStatus());
        jgen.writeEndObject();
    }
}
