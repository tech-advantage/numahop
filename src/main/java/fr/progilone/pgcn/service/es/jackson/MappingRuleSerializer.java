package fr.progilone.pgcn.service.es.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import fr.progilone.pgcn.domain.exchange.MappingRule;

import java.io.IOException;

public class MappingRuleSerializer extends StdSerializer<MappingRule> {

    public MappingRuleSerializer() {
        super(MappingRule.class);
    }

    @Override
    public void serialize(final MappingRule rule, final JsonGenerator jgen, final SerializerProvider serializerProvider) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField("docUnitField", rule.getDocUnitField());
        jgen.writeStringField("bibRecordField", rule.getBibRecordField());
        if (rule.getProperty() != null) {
            jgen.writeStringField("property", rule.getProperty().getLabel());
        }
        jgen.writeStringField("expressionConf", rule.getExpressionConf());
        jgen.writeStringField("expression", rule.getExpression());
        jgen.writeStringField("conditionConf", rule.getConditionConf());
        jgen.writeStringField("condition", rule.getCondition());
        jgen.writeObjectField("position", rule.getPosition());
        jgen.writeBooleanField("defaultRule", rule.isDefaultRule());
        jgen.writeEndObject();
    }
}
