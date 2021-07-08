package fr.progilone.pgcn.service.exchange.marc.mapping;

import fr.progilone.pgcn.domain.exchange.MappingRule;
import fr.progilone.pgcn.util.TestUtil;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Created by Sebastien on 01/12/2016.
 */
public class CompiledMappingRuleTest {

    @Test
    public void testGetMarcKeys() {
        final String expression = "\\100, someFunction(\\712, '703') + \\200$a";
        final String condition = "\\702$4 != '160'";

        final MappingRule rule = new MappingRule();
        rule.setExpression(expression);
        rule.setCondition(condition);

        final CompiledMappingRule actual = new CompiledMappingRule(rule);

        assertEquals(condition, actual.getRule().getCondition());
        assertEquals(expression, actual.getRule().getExpression());

        TestUtil.checkCollectionContainsSameElements(Arrays.asList(new MarcKey("100"), new MarcKey("712"), new MarcKey("200", 'a')),
                                                     actual.getExpressionStatement().getMarcKeys());
        TestUtil.checkCollectionContainsSameElements(Arrays.asList(new MarcKey("100"),
                                                                   new MarcKey("712"),
                                                                   new MarcKey("200", 'a'),
                                                                   new MarcKey("702", '4')),
                                                     actual.getMarcKeys());
    }
}
