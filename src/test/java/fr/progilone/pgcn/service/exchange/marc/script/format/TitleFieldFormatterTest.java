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
public class TitleFieldFormatterTest extends AbstractScriptTest {

    private static final MarcFactory MARC_FACTORY = MarcFactory.newInstance();

    @Test
    public void testNull() {
        final TitleFieldFormatter fmt = new TitleFieldFormatter("test", null);
        final String actual = fmt.format((DataField)null);
        assertNull(actual);
    }

    @Test
    public void test() {
        TitleFieldFormatter fmt = new TitleFieldFormatter("test", null);

        final DataField fld = MARC_FACTORY.newDataField("200", ' ', ' ');
        fld.addSubfield(MARC_FACTORY.newSubfield('a', "sub a1"));
        fld.addSubfield(MARC_FACTORY.newSubfield('a', "sub a2"));
        fld.addSubfield(MARC_FACTORY.newSubfield('a', "sub a3"));
        fld.addSubfield(MARC_FACTORY.newSubfield('b', "sub b"));
        fld.addSubfield(MARC_FACTORY.newSubfield('c', "sub c"));
        fld.addSubfield(MARC_FACTORY.newSubfield('d', "sub d"));
        fld.addSubfield(MARC_FACTORY.newSubfield('e', "sub e"));
        fld.addSubfield(MARC_FACTORY.newSubfield('f', "sub f"));
        fld.addSubfield(MARC_FACTORY.newSubfield('g', "sub g"));
        fld.addSubfield(MARC_FACTORY.newSubfield('h', "sub h"));
        fld.addSubfield(MARC_FACTORY.newSubfield('i', "sub i"));

        final String actual = fmt.format(fld);

        assertEquals("sub a1; sub a2; sub a3 [sub b]. sub c = sub d : sub e /sub f; sub g. sub h, sub i", actual);
    }

    @Test
    public void testGroovy() throws ScriptException {
        final DataField fld200 = MARC_FACTORY.newDataField("200", ' ', ' ');
        fld200.addSubfield(MARC_FACTORY.newSubfield('a', "Les Arts bibliographiques"));
        fld200.addSubfield(MARC_FACTORY.newSubfield('b', "Texte imprimé"));
        fld200.addSubfield(MARC_FACTORY.newSubfield('e', "l'oeuvre et l'image"));
        fld200.addSubfield(MARC_FACTORY.newSubfield('e',
                                                    "revue trimestrielle consacrée à la littérature contemporaine, à la technique et aux arts du livre"));
        final String expression = "title(marc_200)";

        final Map<String, Object> bindings = new HashMap<>();
        bindings.put("marc_200", fld200);

        final Object actual = evalUserScript(expression, bindings, new TitleFieldFormatter("test", null));

        assertEquals(
            "Les Arts bibliographiques [Texte imprimé] : l'oeuvre et l'image : revue trimestrielle consacrée à la littérature contemporaine, à la technique et aux arts du livre",
            actual);
    }

    @Test
    public void testGroovy2() throws ScriptException {
        final DataField fld200 = MARC_FACTORY.newDataField("200", ' ', ' ');
        fld200.addSubfield(MARC_FACTORY.newSubfield('a', "Les Arts bibliographiques"));
        fld200.addSubfield(MARC_FACTORY.newSubfield('b', "Texte imprimé"));
        fld200.addSubfield(MARC_FACTORY.newSubfield('e', "l'oeuvre et l'image"));
        fld200.addSubfield(MARC_FACTORY.newSubfield('e',
                                                    "revue trimestrielle consacrée à la littérature contemporaine, à la technique et aux arts du livre"));

        final String configuration = "titleFilter('a', 'b')";
        final String expression = "title(marc_200)";

        final Map<String, Object> bindings = new HashMap<>();
        bindings.put("marc_200", fld200);

        final Object actual = evalUserScript(expression, configuration, bindings, new TitleFieldFormatter("test", null));

        assertEquals("Les Arts bibliographiques [Texte imprimé]", actual);
    }
}
