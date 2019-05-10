package fr.progilone.pgcn.service.exchange.marc.script.test;

import fr.progilone.pgcn.service.exchange.marc.script.AbstractScriptTest;
import org.junit.Test;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;

import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Sebastien on 06/12/2016.
 */
public class ExistsConditionTest extends AbstractScriptTest {

    private static final MarcFactory MARC_FACTORY = MarcFactory.newInstance();

    @Test
    public void test() {
        ExistsCondition condition = new ExistsCondition("test", null);

        final DataField fld = MARC_FACTORY.newDataField("200", ' ', ' ');
        fld.addSubfield(MARC_FACTORY.newSubfield('a', "titre"));
        final Record record = MARC_FACTORY.newRecord();
        record.addVariableField(fld);

        boolean actual = condition.test(record, "700", null);
        assertFalse(actual);

        actual = condition.test(record, "700", 'a');
        assertFalse(actual);

        actual = condition.test(record, "200", null);
        assertTrue(actual);

        actual = condition.test(record, "200", 'b');
        assertFalse(actual);

        actual = condition.test(record, "200", 'a');
        assertTrue(actual);
    }

    @Test
    public void testGroovy1() throws ScriptException {
        final DataField fld = MARC_FACTORY.newDataField("200", ' ', ' ');
        fld.addSubfield(MARC_FACTORY.newSubfield('a', "titre"));
        final Record record = MARC_FACTORY.newRecord();
        record.addVariableField(fld);

        final String expression = "exists(record, '200')";

        final Map<String, Object> bindings = new HashMap<>();
        bindings.put("record", record);

        final Object actual = evalUserScript(expression, bindings, new ExistsCondition("test", null));

        assertTrue((boolean) actual);
    }

    @Test
    public void testGroovy2() throws ScriptException {
        final DataField fld = MARC_FACTORY.newDataField("200", ' ', ' ');
        fld.addSubfield(MARC_FACTORY.newSubfield('a', "titre"));
        final Record record = MARC_FACTORY.newRecord();
        record.addVariableField(fld);

        final String expression = "exists(record, '200', 'a')";

        final Map<String, Object> bindings = new HashMap<>();
        bindings.put("record", record);

        final Object actual = evalUserScript(expression, bindings, new ExistsCondition("test", null));

        assertTrue((boolean) actual);
    }
}
