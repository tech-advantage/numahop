package fr.progilone.pgcn.service.exchange.marc.script.format;

import fr.progilone.pgcn.service.exchange.marc.script.AbstractScriptTest;
import org.junit.Test;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;

import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by Sébastien on 29/06/2017.
 */
public class ConcatWithSepFormatterTest extends AbstractScriptTest {

    private static final MarcFactory MARC_FACTORY = MarcFactory.newInstance();

    @Test
    public void testNull() {
        final ConcatWithSepFormatter fmt = new ConcatWithSepFormatter("test", null);
        final String actual = fmt.format(null);
        assertNull(actual);
    }

    @Test
    public void testEmpty() {
        final DataField fld600 = MARC_FACTORY.newDataField("600", ' ', ' ');
        fld600.addSubfield(MARC_FACTORY.newSubfield('a', "a"));

        final ConcatWithSepFormatter fmt = new ConcatWithSepFormatter("test", null);
        fmt.setCodes(new char[]{'x', 'y', 'z'});

        final String actual = fmt.format(fld600);
        assertEquals("", actual);
    }

    @Test
    public void test1() {
        final DataField fld600 = MARC_FACTORY.newDataField("600", ' ', ' ');
        fld600.addSubfield(MARC_FACTORY.newSubfield('x', "x1"));
        fld600.addSubfield(MARC_FACTORY.newSubfield('x', "x2"));
        fld600.addSubfield(MARC_FACTORY.newSubfield('y', "y1"));
        fld600.addSubfield(MARC_FACTORY.newSubfield('z', "z1"));
        fld600.addSubfield(MARC_FACTORY.newSubfield('z', "z2"));
        fld600.addSubfield(MARC_FACTORY.newSubfield('z', "z3"));

        final ConcatWithSepFormatter fmt = new ConcatWithSepFormatter("test", null);
        fmt.setCodes(new char[]{'x', 'y', 'z'});

        final String actual = fmt.format(fld600);
        assertEquals("x1 -- x2 - y1 - z1 -- z2 -- z3", actual);
    }

    @Test
    public void test2() {
        final DataField fld600 = MARC_FACTORY.newDataField("600", ' ', ' ');
        fld600.addSubfield(MARC_FACTORY.newSubfield('x', "x1_2"));
        fld600.addSubfield(MARC_FACTORY.newSubfield('x', "x2_2"));
        fld600.addSubfield(MARC_FACTORY.newSubfield('z', "z1_2"));
        fld600.addSubfield(MARC_FACTORY.newSubfield('z', "z2_2"));
        fld600.addSubfield(MARC_FACTORY.newSubfield('z', "z3_2"));

        final ConcatWithSepFormatter fmt = new ConcatWithSepFormatter("test", null);
        fmt.setCodes(new char[]{'x', 'y', 'z'});

        final String actual = fmt.format(fld600);
        assertEquals("x1_2 -- x2_2 - z1_2 -- z2_2 -- z3_2", actual);
    }

    @Test
    public void testGroovy() throws ScriptException {
        final DataField fld602 = MARC_FACTORY.newDataField("602", ' ', ' ');
        fld602.addSubfield(MARC_FACTORY.newSubfield('x', "x2"));
        fld602.addSubfield(MARC_FACTORY.newSubfield('y', "y1"));
        fld602.addSubfield(MARC_FACTORY.newSubfield('z', "z1"));
        fld602.addSubfield(MARC_FACTORY.newSubfield('z', "z2"));
        final String expression = "concatWithSep(marc_602)";

        final Map<String, Object> bindings = new HashMap<>();
        bindings.put("marc_602", fld602);

        final ConcatWithSepFormatter fmt = new ConcatWithSepFormatter("test", null);
        fmt.setCodes(new char[]{'x', 'y', 'z'});

        final Object actual = evalUserScript(expression, bindings, fmt);

        assertEquals("x2 - y1 - z1 -- z2", actual);
    }

    @Test
    public void testGroovy2() throws ScriptException {
        final DataField fld602 = MARC_FACTORY.newDataField("602", ' ', ' ');
        fld602.addSubfield(MARC_FACTORY.newSubfield('x', "x1"));
        fld602.addSubfield(MARC_FACTORY.newSubfield('y', "y1"));
        fld602.addSubfield(MARC_FACTORY.newSubfield('x', "x2"));
        fld602.addSubfield(MARC_FACTORY.newSubfield('y', "y2"));
        fld602.addSubfield(MARC_FACTORY.newSubfield('z', "z1"));
        fld602.addSubfield(MARC_FACTORY.newSubfield('z', "z2"));
        final String expression = "concatWithSep(marc_602)";

        final Map<String, Object> bindings = new HashMap<>();
        bindings.put("marc_602", fld602);

        final ConcatWithSepFormatter fmt = new ConcatWithSepFormatter("test", null);
        fmt.setCodes(new char[]{'x', 'y', 'z'});

        final Object actual = evalUserScript(expression, bindings, fmt);

        assertEquals("x1 - y1 - x2 - y2 - z1 -- z2", actual);
    }
}
