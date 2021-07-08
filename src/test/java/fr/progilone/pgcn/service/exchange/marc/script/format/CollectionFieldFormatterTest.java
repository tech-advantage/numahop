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
public class CollectionFieldFormatterTest extends AbstractScriptTest {

    private static final MarcFactory MARC_FACTORY = MarcFactory.newInstance();

    @Test
    public void testNull() {
        final CollectionFieldFormatter fmt = new CollectionFieldFormatter("test", null);
        final String actual = fmt.format((DataField)null);
        assertNull(actual);
    }

    @Test
    public void test1() {
        CollectionFieldFormatter fmt = new CollectionFieldFormatter("test", null);

        final DataField fld = MARC_FACTORY.newDataField("200", ' ', ' ');
        fld.addSubfield(MARC_FACTORY.newSubfield('a', "titre"));
        fld.addSubfield(MARC_FACTORY.newSubfield('d', "titre parallèle"));
        fld.addSubfield(MARC_FACTORY.newSubfield('e', "complément"));
        fld.addSubfield(MARC_FACTORY.newSubfield('f', "mention de resp"));
        fld.addSubfield(MARC_FACTORY.newSubfield('h', "numéro de partie"));
        fld.addSubfield(MARC_FACTORY.newSubfield('i', "titre de partie"));
        fld.addSubfield(MARC_FACTORY.newSubfield('v', "numérotation de vol"));
        fld.addSubfield(MARC_FACTORY.newSubfield('x', "0000-0000"));

        final String actual = fmt.format(fld);

        assertEquals("titre = titre parallèle: complément / mention de resp. numéro de partie, titre de partie; numérotation de vol, 0000-0000",
                     actual);
    }

    @Test
    public void test2() {
        CollectionFieldFormatter fmt = new CollectionFieldFormatter("test", null);

        final DataField fld = MARC_FACTORY.newDataField("200", ' ', ' ');
        fld.addSubfield(MARC_FACTORY.newSubfield('a', "titre"));
        fld.addSubfield(MARC_FACTORY.newSubfield('e', "complément"));
        fld.addSubfield(MARC_FACTORY.newSubfield('i', "titre de partie"));

        final String actual = fmt.format(fld);

        assertEquals("titre: complément. titre de partie",
                     actual);
    }

    @Test
    public void testGroovy() throws ScriptException {
        final DataField fld225 = MARC_FACTORY.newDataField("225", ' ', ' ');
        fld225.addSubfield(MARC_FACTORY.newSubfield('a', "Les Arts bibliographiques"));
        String expression = "collection(marc_225)";

        final Map<String, Object> bindings = new HashMap<>();
        bindings.put("marc_225", fld225);

        final Object actual = evalUserScript(expression, bindings, new CollectionFieldFormatter("test", null));

        assertEquals("Les Arts bibliographiques", actual);
    }

    @Test
    public void testGroovy2() throws ScriptException {
        final DataField fld225 = MARC_FACTORY.newDataField("200", ' ', ' ');
        fld225.addSubfield(MARC_FACTORY.newSubfield('a', "titre"));
        fld225.addSubfield(MARC_FACTORY.newSubfield('d', "titre parallèle"));
        fld225.addSubfield(MARC_FACTORY.newSubfield('e', "complément"));
        fld225.addSubfield(MARC_FACTORY.newSubfield('f', "mention de resp"));
        fld225.addSubfield(MARC_FACTORY.newSubfield('h', "numéro de partie"));
        fld225.addSubfield(MARC_FACTORY.newSubfield('i', "titre de partie"));
        fld225.addSubfield(MARC_FACTORY.newSubfield('v', "numérotation de vol"));
        fld225.addSubfield(MARC_FACTORY.newSubfield('x', "0000-0000"));

        final String configuration = "collectionFilter('a', 'e')";
        String expression = "collection(marc_225)";

        final Map<String, Object> bindings = new HashMap<>();
        bindings.put("marc_225", fld225);

        final Object actual = evalUserScript(expression, configuration, bindings, new CollectionFieldFormatter("test", null));

        assertEquals("titre: complément", actual);
    }
}
