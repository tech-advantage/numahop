package fr.progilone.pgcn.service.exchange.marc.script.format;

import fr.progilone.pgcn.service.exchange.marc.script.AbstractScriptTest;
import org.junit.Test;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;

import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by Sebastien on 02/12/2016.
 */
public class CorporateFieldFormatterTest extends AbstractScriptTest {

    private static final MarcFactory MARC_FACTORY = MarcFactory.newInstance();

    @Test
    public void testNull() {
        final CorporateFieldFormatter fmt = new CorporateFieldFormatter("test", null);
        final String actual = fmt.format((DataField)null);
        assertNull(actual);
    }

    @Test
    public void test1() {
        CorporateFieldFormatter fmt = new CorporateFieldFormatter("test", null);

        final DataField fld = MARC_FACTORY.newDataField("200", ' ', ' ');
        fld.addSubfield(MARC_FACTORY.newSubfield('a', "nom"));
        fld.addSubfield(MARC_FACTORY.newSubfield('b', "subdivision"));
        fld.addSubfield(MARC_FACTORY.newSubfield('c', "qualification"));
        fld.addSubfield(MARC_FACTORY.newSubfield('d', "numéro"));
        fld.addSubfield(MARC_FACTORY.newSubfield('e', "lieu"));
        fld.addSubfield(MARC_FACTORY.newSubfield('f', "dates"));
        fld.addSubfield(MARC_FACTORY.newSubfield('4', "code de fonction"));

        final String actual = fmt.format(fld);

        assertEquals("nom. subdivision (qualification) (numéro; lieu; dates). code de fonction", actual);
    }

    @Test
    public void test2() {
        CorporateFieldFormatter fmt = new CorporateFieldFormatter("test", null);

        final DataField fld = MARC_FACTORY.newDataField("200", ' ', ' ');
        fld.addSubfield(MARC_FACTORY.newSubfield('a', "nom"));
        fld.addSubfield(MARC_FACTORY.newSubfield('b', "subdivision"));
        fld.addSubfield(MARC_FACTORY.newSubfield('c', "qualification"));
        fld.addSubfield(MARC_FACTORY.newSubfield('e', "lieu"));
        fld.addSubfield(MARC_FACTORY.newSubfield('f', "dates"));
        fld.addSubfield(MARC_FACTORY.newSubfield('4', "code de fonction"));

        final String actual = fmt.format(fld);

        assertEquals("nom. subdivision (qualification) (lieu; dates). code de fonction", actual);
    }

    @Test
    public void test3() {
        CorporateFieldFormatter fmt = new CorporateFieldFormatter("test", null);

        final DataField fld = MARC_FACTORY.newDataField("200", ' ', ' ');
        fld.addSubfield(MARC_FACTORY.newSubfield('a', "nom"));
        fld.addSubfield(MARC_FACTORY.newSubfield('b', "subdivision"));
        fld.addSubfield(MARC_FACTORY.newSubfield('c', "qualification"));
        fld.addSubfield(MARC_FACTORY.newSubfield('f', "dates"));
        fld.addSubfield(MARC_FACTORY.newSubfield('4', "code de fonction"));

        final String actual = fmt.format(fld);

        assertEquals("nom. subdivision (qualification) (dates). code de fonction", actual);
    }

    @Test
    public void testGroovy() throws ScriptException {
        final DataField fld712 = MARC_FACTORY.newDataField("712", ' ', ' ');
        fld712.addSubfield(MARC_FACTORY.newSubfield('a', "Association polytechnique"));
        fld712.addSubfield(MARC_FACTORY.newSubfield('c', "Paris"));
        fld712.addSubfield(MARC_FACTORY.newSubfield('4', "340"));
        final String expression = "corporate(marc_712)";

        final Map<String, Object> bindings = new HashMap<>();
        bindings.put("marc_712", fld712);

        final Object actual = evalUserScript(expression, bindings, new CorporateFieldFormatter("test", null));

        assertEquals("Association polytechnique (Paris). 340", actual);
    }

    @Test
    public void testGroovy2() throws ScriptException {
        final DataField fld712 = MARC_FACTORY.newDataField("712", ' ', ' ');
        fld712.addSubfield(MARC_FACTORY.newSubfield('a', "Association polytechnique"));
        fld712.addSubfield(MARC_FACTORY.newSubfield('c', "Paris"));
        fld712.addSubfield(MARC_FACTORY.newSubfield('4', "340"));

        final String configuration = "corporateFilter('4')";
        final String expression = "corporate(marc_712)";

        final Map<String, Object> bindings = new HashMap<>();
        bindings.put("marc_712", fld712);

        final Object actual = evalUserScript(expression, configuration, bindings, new CorporateFieldFormatter("test", null));

        assertEquals("340", actual);
    }
}
