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
 * 
 * @author jbrunet
 * Créé le 20 févr. 2017
 */
public class DatePublicationFormatterTest extends AbstractScriptTest {

    private static final MarcFactory MARC_FACTORY = MarcFactory.newInstance();

    @Test
    public void testNull() {
        final DatePublicationFormatter fmt = new DatePublicationFormatter("test", null);
        final String actual = fmt.format(null);
        assertNull(actual);
    }

    /**
     * Options par défaut (taille minimum : 17)
     */
    @Test
    public void testTooShort() {
        DatePublicationFormatter fmt = new DatePublicationFormatter("test", null);

        final DataField fld = MARC_FACTORY.newDataField("100", ' ', ' ');
        fld.addSubfield(MARC_FACTORY.newSubfield('a', "tropcourt"));
        fld.addSubfield(MARC_FACTORY.newSubfield('b', "inutilisé"));

        final String actual = fmt.format(fld);
        
        assertNull(actual);
    }

    /**
     * Test ok avec les paramètres par défaut
     */
    @Test
    public void testOkDefault() {
        DatePublicationFormatter fmt = new DatePublicationFormatter("test", null);

        final DataField fld = MARC_FACTORY.newDataField("100", ' ', ' ');
        fld.addSubfield(MARC_FACTORY.newSubfield('a', "########a19591962"));
        fld.addSubfield(MARC_FACTORY.newSubfield('b', "inutilisé"));

        final String actual = fmt.format(fld);

        assertEquals("1959/1962", actual);
    }

    @Test
    public void testFormat() {
        DatePublicationFormatter fmt = new DatePublicationFormatter("test", null);

        final DataField fld = MARC_FACTORY.newDataField("100", ' ', ' ');
        fld.addSubfield(MARC_FACTORY.newSubfield('a', "########k19  1962"));
        fld.addSubfield(MARC_FACTORY.newSubfield('b', "inutilisé"));

        final String actual = fmt.format(fld);

        assertEquals("19XX|1962", actual);
    }
    
    /**
     * Type u, date inconnue
     */
    @Test
    public void testFormat2() {
        DatePublicationFormatter fmt = new DatePublicationFormatter("test", null);

        final DataField fld = MARC_FACTORY.newDataField("100", ' ', ' ');
        fld.addSubfield(MARC_FACTORY.newSubfield('a', "########u        #re#"));
        fld.addSubfield(MARC_FACTORY.newSubfield('b', "inutilisé"));

        final String actual = fmt.format(fld);

        assertEquals("s.d.", actual);
    }
    
    /**
     * Type j, date précise
     */
    @Test
    public void testFormat3() {
        DatePublicationFormatter fmt = new DatePublicationFormatter("test", null);

        final DataField fld = MARC_FACTORY.newDataField("100", ' ', ' ');
        fld.addSubfield(MARC_FACTORY.newSubfield('a', "########j19820512#re#"));
        fld.addSubfield(MARC_FACTORY.newSubfield('b', "inutilisé"));

        final String actual = fmt.format(fld);

        assertEquals("1982-05-12", actual);
    }

    /**
     * Test basique avec configuration par défaut
     * 
     * @throws ScriptException
     */
    @Test
    public void testGroovy() throws ScriptException {
        final DataField fld100 = MARC_FACTORY.newDataField("100", ' ', ' ');
        fld100.addSubfield(MARC_FACTORY.newSubfield('a', "########j19820512#re#"));
        fld100.addSubfield(MARC_FACTORY.newSubfield('c', "Paris"));
        final String expression = "datepublication(marc_100)";

        final Map<String, Object> bindings = new HashMap<>();
        bindings.put("marc_100", fld100);

        final Object actual = evalUserScript(expression, bindings, new DatePublicationFormatter("test", null));

        assertEquals("1982-05-12", actual);
    }

    /**
     * Configuration personnalisée ( changement code, décalage de 6, changement de la position du type)
     * 
     * @throws ScriptException
     */
    @Test
    public void testGroovyWithConfiguration() throws ScriptException {
        final DataField fld100 = MARC_FACTORY.newDataField("100", ' ', ' ');
        fld100.addSubfield(MARC_FACTORY.newSubfield('b', "ttt198 1983#re#k"));
        fld100.addSubfield(MARC_FACTORY.newSubfield('c', "Paris"));

        final String configuration = "datepublicationFilter('b')\n"
                + "datepublicationPositionDebut(3)\n"
                + "datepublicationPositionFin(10)\n"
                + "datepublicationPositionTypeDate(15)";
        final String expression = "datepublication(marc_100)";

        final Map<String, Object> bindings = new HashMap<>();
        bindings.put("marc_100", fld100);

        final Object actual = evalUserScript(expression, configuration, bindings, new DatePublicationFormatter("test", null));

        assertEquals("198X|1983", actual);
    }
}
