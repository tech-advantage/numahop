package fr.progilone.pgcn.service.es.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReportDetail;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReportDetail_;

import java.io.IOException;

public class ConditionReportDetailSerializer extends StdSerializer<ConditionReportDetail> {

    public ConditionReportDetailSerializer() {
        super(ConditionReportDetail.class);
    }

    @Override
    public void serialize(final ConditionReportDetail detail, final JsonGenerator jgen, final SerializerProvider serializerProvider) throws
                                                                                                                                     IOException {
        jgen.writeStartObject();
        jgen.writeStringField(ConditionReportDetail_.identifier.getName(), detail.getIdentifier());
        jgen.writeObjectField(ConditionReportDetail_.type.getName(), detail.getType());
        jgen.writeObjectField(ConditionReportDetail_.date.getName(), detail.getDate());
        jgen.writeObjectField(ConditionReportDetail_.descriptions.getName(), detail.getDescriptions());
        jgen.writeStringField(ConditionReportDetail_.additionnalDesc.getName(), detail.getAdditionnalDesc());
        jgen.writeStringField(ConditionReportDetail_.bindingDesc.getName(), detail.getBindingDesc());
        jgen.writeStringField(ConditionReportDetail_.bodyDesc.getName(), detail.getBodyDesc());
        jgen.writeNumberField("sortedType", detail.getSortedType());
        jgen.writeEndObject();
    }
}
