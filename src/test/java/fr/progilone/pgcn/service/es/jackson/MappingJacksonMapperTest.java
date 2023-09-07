package fr.progilone.pgcn.service.es.jackson;

import static org.junit.jupiter.api.Assertions.*;

import fr.progilone.pgcn.domain.document.DocPropertyType;
import fr.progilone.pgcn.domain.exchange.Mapping;
import fr.progilone.pgcn.domain.exchange.MappingRule;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MappingJacksonMapperTest {

    private MappingJacksonMapper mapper;

    @BeforeEach
    public void setUp() {
        mapper = new MappingJacksonMapper();
    }

    @Test
    public void testMapToString() throws IOException {
        final Mapping mapping = new Mapping();
        mapping.setLabel("Le petit prince");
        mapping.setType(Mapping.Type.MARC);
        mapping.setJoinExpression("\\995$k");

        final MappingRule rule = new MappingRule();
        mapping.addRule(rule);

        rule.setDocUnitField("label");
        rule.setBibRecordField("title");
        final DocPropertyType property = new DocPropertyType();
        property.setLabel("Titre");
        rule.setProperty(property);
        rule.setExpressionConf("subfieldsAdd('a')\n" + " subfieldsAdd('t', '. ')\n"
                               + " concatWithSepCodes('x', 'y', 'z')");
        rule.setExpression("[subfields(\\604), concatWithSep(\\604)].join(\" \")");
        rule.setCondition("titleFilter('a', 'b', 'd', 'f', 'g')");
        rule.setPosition(10);
        rule.setDefaultRule(false);

        final String actual = mapper.mapToString(mapping);
        assertEquals(MAPPING_JSON, actual);
    }

    @Test
    public void testMapToMapping() throws IOException {
        final Mapping actual = mapper.mapToMapping(MAPPING_JSON);

        assertNotNull(actual);
        assertEquals("Le petit prince", actual.getLabel());
        assertEquals(Mapping.Type.MARC, actual.getType());
        assertEquals("\\995$k", actual.getJoinExpression());

        assertEquals(1, actual.getRules().size());
        final MappingRule actualRule = actual.getRules().get(0);
        assertEquals("label", actualRule.getDocUnitField());
        assertEquals("title", actualRule.getBibRecordField());
        assertNotNull(actualRule.getProperty());
        assertEquals("Titre", actualRule.getProperty().getLabel());
        assertEquals("subfieldsAdd('a')\n" + " subfieldsAdd('t', '. ')\n"
                     + " concatWithSepCodes('x', 'y', 'z')",
                     actualRule.getExpressionConf());
        assertEquals("[subfields(\\604), concatWithSep(\\604)].join(\" \")", actualRule.getExpression());
        assertEquals("titleFilter('a', 'b', 'd', 'f', 'g')", actualRule.getCondition());
        assertEquals(10, actualRule.getPosition().intValue());
        assertEquals(false, actualRule.isDefaultRule());
    }

    private static final String MAPPING_JSON = "{\"label\":\"Le petit prince\",\"type\":\"MARC\",\"joinExpression\":\"\\\\995$k\",\"rules\":[{\"docUnitField\":\"label\",\"bibRecordField\":\"title\",\"property\":\"Titre\",\"expressionConf\":\"subfieldsAdd('a')\\n subfieldsAdd('t', '. ')\\n concatWithSepCodes('x', 'y', 'z')\",\"expression\":\"[subfields(\\\\604), concatWithSep(\\\\604)].join(\\\" \\\")\",\"conditionConf\":null,\"condition\":\"titleFilter('a', 'b', 'd', 'f', 'g')\",\"position\":10,\"defaultRule\":false}]}";
}
