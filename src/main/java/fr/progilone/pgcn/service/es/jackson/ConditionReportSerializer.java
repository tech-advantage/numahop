package fr.progilone.pgcn.service.es.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReport;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReportDetail;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReport_;

import java.io.IOException;
import java.util.Comparator;
import java.util.Optional;

public class ConditionReportSerializer extends StdSerializer<ConditionReport> {

    public ConditionReportSerializer() {
        super(ConditionReport.class);
    }

    @Override
    public void serialize(final ConditionReport report, final JsonGenerator jgen, final SerializerProvider serializerProvider) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField(ConditionReport_.identifier.getName(), report.getIdentifier());
        jgen.writeStringField(ConditionReport_.docUnitId.getName(), report.getDocUnitId());
        jgen.writeStringField("docUnitPgcnId", report.getDocUnitPgcnId());
        jgen.writeStringField("docUnitLabel", report.getDocUnitLabel());
        jgen.writeObjectField("docUnitCondReportType", report.getDocUnitCondReportType());

        // Dernier détail
        final Optional<ConditionReportDetail> detOpt = report.getDetails().stream().max(Comparator.comparing(ConditionReportDetail::getPosition));
        if (detOpt.isPresent()) {
            jgen.writeObjectField(ConditionReport_.details.getName(), detOpt.get());
        }
        jgen.writeEndObject();
    }
}
