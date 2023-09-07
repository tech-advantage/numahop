package fr.progilone.pgcn.service.exchange.marc.script.format;

import static org.junit.jupiter.api.Assertions.*;

import fr.progilone.pgcn.service.exchange.marc.MarcMappingEvaluationService;
import fr.progilone.pgcn.service.exchange.marc.script.AbstractScriptTest;
import java.util.HashMap;
import java.util.Map;
import javax.script.ScriptException;
import org.junit.jupiter.api.Test;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;

/**
 * Created by Sebastien on 02/12/2016.
 */
public class SubfieldsFormatterTest extends AbstractScriptTest {

    private static final MarcFactory MARC_FACTORY = MarcFactory.newInstance();

    @Test
    public void testNull() {
        final SubfieldsFormatter fmt = new SubfieldsFormatter("test", null);
        final String actual = fmt.format((DataField) null);
        assertNull(actual);
    }

    @Test
    public void test1() {
        final SubfieldsFormatter fmt = new SubfieldsFormatter("test", null);
        fmt.add('a');
        fmt.add('b', "; ");
        fmt.add('c', " /");
        fmt.addGroup(" (", ")", "d", " - ", "e");

        final DataField fld = MARC_FACTORY.newDataField("200", ' ', ' ');
        fld.addSubfield(MARC_FACTORY.newSubfield('a', "sub a"));
        fld.addSubfield(MARC_FACTORY.newSubfield('b', "sub b"));
        fld.addSubfield(MARC_FACTORY.newSubfield('c', "sub c"));
        fld.addSubfield(MARC_FACTORY.newSubfield('d', "sub d"));
        fld.addSubfield(MARC_FACTORY.newSubfield('e', "sub e"));

        final String actual = fmt.format(fld);

        assertEquals("sub a; sub b /sub c (sub d - sub e)", actual);
    }

    @Test
    public void test2() {
        final SubfieldsFormatter fmt = new SubfieldsFormatter("test", null);
        fmt.add('a');
        fmt.add('b', "; ");
        fmt.add('c', " /");
        fmt.addGroup(" (", ")", "d", " - ", "e");

        final DataField fld = MARC_FACTORY.newDataField("200", ' ', ' ');
        fld.addSubfield(MARC_FACTORY.newSubfield('c', "sub c"));
        fld.addSubfield(MARC_FACTORY.newSubfield('b', "sub b"));
        fld.addSubfield(MARC_FACTORY.newSubfield('e', "sub e"));
        fld.addSubfield(MARC_FACTORY.newSubfield('d', "sub d"));
        fld.addSubfield(MARC_FACTORY.newSubfield('a', "sub a"));

        final String actual = fmt.format(fld);

        assertEquals("sub c; sub b (sub e sub d) sub a", actual);
    }

    @Test
    public void test3() {
        final SubfieldsFormatter fmt = new SubfieldsFormatter("test", null);
        fmt.add('a');
        fmt.add('b', "; ");
        fmt.add('c', " /", "\\");
        fmt.addGroup(" (", ")", "d", " - ", "e");

        final DataField fld = MARC_FACTORY.newDataField("200", ' ', ' ');
        fld.addSubfield(MARC_FACTORY.newSubfield('b', "sub b"));
        fld.addSubfield(MARC_FACTORY.newSubfield('b', "sub b"));
        fld.addSubfield(MARC_FACTORY.newSubfield('c', "sub c"));
        fld.addSubfield(MARC_FACTORY.newSubfield('a', "sub a"));
        fld.addSubfield(MARC_FACTORY.newSubfield('e', "sub e"));
        fld.addSubfield(MARC_FACTORY.newSubfield('b', "sub b"));
        fld.addSubfield(MARC_FACTORY.newSubfield('e', "sub e"));
        fld.addSubfield(MARC_FACTORY.newSubfield('d', "sub d"));
        fld.addSubfield(MARC_FACTORY.newSubfield('a', "sub a"));

        final String actual = fmt.format(fld);

        assertEquals("sub b; sub b /sub c\\ sub a (sub e - sub e sub d); sub b sub a", actual);
    }

    @Test
    public void test4() {
        final SubfieldsFormatter fmt = new SubfieldsFormatter("test", null);

        final DataField fld = MARC_FACTORY.newDataField("200", ' ', ' ');
        fld.addSubfield(MARC_FACTORY.newSubfield('a', "sub a"));
        fld.addSubfield(MARC_FACTORY.newSubfield('c', "sub c"));
        fld.addSubfield(MARC_FACTORY.newSubfield('b', "sub b"));
        fld.addSubfield(MARC_FACTORY.newSubfield('d', "sub d"));
        fld.addSubfield(MARC_FACTORY.newSubfield('e', "sub e"));

        final String actual = fmt.format(fld);

        assertEquals("sub a sub c sub b sub d sub e", actual);
    }

    @Test
    public void testGroovy1() throws ScriptException {
        final DataField fld200 = MARC_FACTORY.newDataField("200", ' ', ' ');
        fld200.addSubfield(MARC_FACTORY.newSubfield('a', "Les Arts bibliographiques"));
        fld200.addSubfield(MARC_FACTORY.newSubfield('b', "Texte imprimé"));
        fld200.addSubfield(MARC_FACTORY.newSubfield('e', "l'oeuvre et l'image"));
        final String expression = "subfields(marc_200)";

        final Map<String, Object> bindings = new HashMap<>();
        bindings.put("marc_200", fld200);

        final Object actual = evalUserScript(expression, bindings, new SubfieldsFormatter("test", null));

        assertEquals("Les Arts bibliographiques Texte imprimé l'oeuvre et l'image", actual);
    }

    @Test
    public void testGroovy2() throws ScriptException {
        final DataField fld200 = MARC_FACTORY.newDataField("200", ' ', ' ');
        fld200.addSubfield(MARC_FACTORY.newSubfield('a', "Les Arts bibliographiques"));
        fld200.addSubfield(MARC_FACTORY.newSubfield('b', "Texte imprimé"));
        fld200.addSubfield(MARC_FACTORY.newSubfield('e', "l'oeuvre et l'image"));

        final String configuration = "subfieldsSeparator(', ')";
        final String expression = "subfields(marc_200)";

        final Map<String, Object> bindings = new HashMap<>();
        bindings.put("marc_200", fld200);

        final Object actual = evalUserScript(expression, configuration, bindings, new SubfieldsFormatter("test", null));

        assertEquals("Les Arts bibliographiques, Texte imprimé, l'oeuvre et l'image", actual);
    }

    @Test
    public void testGroovy3() throws ScriptException {
        final DataField fld200 = MARC_FACTORY.newDataField("200", ' ', ' ');
        fld200.addSubfield(MARC_FACTORY.newSubfield('a', "Les Arts bibliographiques"));
        fld200.addSubfield(MARC_FACTORY.newSubfield('b', "Texte imprimé"));
        fld200.addSubfield(MARC_FACTORY.newSubfield('f', "test f"));
        fld200.addSubfield(MARC_FACTORY.newSubfield('e', "l'oeuvre et l'image"));
        fld200.addSubfield(MARC_FACTORY.newSubfield('g', "test g"));

        final String configuration = "subfieldsAdd('a')\n" + "subfieldsAdd('b', ', ')\n"
                                     + "subfieldsAdd('e', ' - ', '#')\n"
                                     + "subfieldsAddGroup(' (', ')', ', ', 'f', ', ', 'g')";
        final String expression = "subfields(marc_200)";

        final Map<String, Object> bindings = new HashMap<>();
        bindings.put("marc_200", fld200);

        final Object actual = evalUserScript(expression, configuration, bindings, new SubfieldsFormatter("test", null));

        assertEquals("Les Arts bibliographiques, Texte imprimé (test f, test g) - l'oeuvre et l'image#", actual);
    }

    @Test
    public void testGroovyTransliterate() throws ScriptException {
        final DataField fld200 = MARC_FACTORY.newDataField("200", ' ', ' ');
        fld200.addSubfield(MARC_FACTORY.newSubfield('a', "Les Arts bibliographiques"));
        fld200.addSubfield(MARC_FACTORY.newSubfield('4', "070"));

        final String configuration = "subfieldsAdd('a')\n" + "subfieldsAdd('4', ', ')\n"
                                     + "subfieldsTransliterate('4', 'FUNCTION')\n";
        final String expression = "subfields(marc_200)";

        final Map<String, Object> bindings = new HashMap<>();
        bindings.put("marc_200", fld200);
        bindings.put(MarcMappingEvaluationService.BINDING_FN_TRANSLITERATE, new Object() {

            public String getValue(final String type, final String code) {
                return "Auteur";
            }
        });

        final Object actual = evalUserScript(expression, configuration, bindings, new SubfieldsFormatter("test", null));

        assertEquals("Les Arts bibliographiques, Auteur", actual);
    }
}
