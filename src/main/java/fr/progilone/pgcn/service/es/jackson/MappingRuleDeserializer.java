package fr.progilone.pgcn.service.es.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import fr.progilone.pgcn.domain.document.DocPropertyType;
import fr.progilone.pgcn.domain.exchange.MappingRule;

import java.io.IOException;
import java.util.function.Consumer;

public class MappingRuleDeserializer extends StdDeserializer<MappingRule> {

    public MappingRuleDeserializer() {
        super(MappingRule.class);
    }

    @Override
    public MappingRule deserialize(final JsonParser jp, final DeserializationContext deserializationContext) throws
                                                                                                             IOException,
                                                                                                             JsonProcessingException {
        final MappingRule rule = new MappingRule();
        final TreeNode node = jp.getCodec().readTree(jp);

        setValueText((ValueNode) node.get("docUnitField"), rule::setDocUnitField);
        setValueText((ValueNode) node.get("bibRecordField"), rule::setBibRecordField);
        setValueProperty((ValueNode) node.get("property"), rule::setProperty);
        setValueText((ValueNode) node.get("expressionConf"), rule::setExpressionConf);
        setValueText((ValueNode) node.get("expression"), rule::setExpression);
        setValueText((ValueNode) node.get("conditionConf"), rule::setConditionConf);
        setValueText((ValueNode) node.get("condition"), rule::setCondition);
        setValueInteger((ValueNode) node.get("position"), rule::setPosition);
        setValueBoolean((ValueNode) node.get("defaultRule"), rule::setDefaultRule);

        return rule;
    }

    private void setValueBoolean(final ValueNode node, Consumer<Boolean> setter) {
        if (node != null && !(node instanceof NullNode)) {
            setter.accept(node.asBoolean());
        }
    }

    private void setValueInteger(final ValueNode node, Consumer<Integer> setter) {
        if (node != null && !(node instanceof NullNode)) {
            setter.accept(node.asInt());
        }
    }

    private void setValueText(final ValueNode node, Consumer<String> setter) {
        if (node != null && !(node instanceof NullNode)) {
            setter.accept(node.asText());
        }
    }

    private void setValueProperty(final ValueNode node, Consumer<DocPropertyType> setter) {
        if (node != null && !(node instanceof NullNode)) {
            final DocPropertyType docPropertyType = new DocPropertyType();
            docPropertyType.setLabel(node.asText());
            setter.accept(docPropertyType);
        }
    }
}
