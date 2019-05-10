package fr.progilone.pgcn.service.es.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import fr.progilone.pgcn.domain.exchange.internetarchive.InternetArchiveReport;
import fr.progilone.pgcn.domain.exchange.internetarchive.InternetArchiveReport_;

import java.io.IOException;

public class InternetArchiveReportSerializer extends StdSerializer<InternetArchiveReport> {

    public InternetArchiveReportSerializer() {
        super(InternetArchiveReport.class);
    }

    @Override
    public void serialize(final InternetArchiveReport report, final JsonGenerator jgen, final SerializerProvider serializerProvider) throws
                                                                                                                                     IOException {
        jgen.writeStartObject();
        jgen.writeStringField(InternetArchiveReport_.identifier.getName(), report.getIdentifier());
        jgen.writeStringField(InternetArchiveReport_.docUnitId.getName(), report.getDocUnitId());
        jgen.writeObjectField(InternetArchiveReport_.dateSent.getName(), report.getDateSent());
        jgen.writeObjectField(InternetArchiveReport_.status.getName(), report.getStatus());
        jgen.writeEndObject();
    }
}
