package fr.progilone.pgcn.service.exchange.marc.script.format;

import fr.progilone.pgcn.service.exchange.marc.MarcMappingEvaluationService;
import fr.progilone.pgcn.service.exchange.marc.script.AbstractScriptTest;
import org.junit.Assert;
import org.junit.Test;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;

import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by Sebastien on 22/11/2016.
 */
public class PersonFieldFormatterTest extends AbstractScriptTest {

    private static final MarcFactory MARC_FACTORY = MarcFactory.newInstance();

    @Test
    public void testNull() {
        final PersonFieldFormatter fmt = new PersonFieldFormatter("test", null);
        final String actual = fmt.format((DataField) null);
        assertNull(actual);
    }

    @Test
    public void testMapping() {
        PersonFieldFormatter formatter = new PersonFieldFormatter("test", null);
        formatter.setTransliterate((type, code) -> "Auteur");

        Record record = buildRecord("Benson", "Rowland S.", null, null, null, null, null);
        String actual = formatter.format((DataField) record.getVariableField("702"));
        Assert.assertEquals("Benson, Rowland S.", actual);

        record = buildRecord("Parker", "Theodore", "Spirit", null, null, null, null);
        actual = formatter.format((DataField) record.getVariableField("702"));
        Assert.assertEquals("Parker, Theodore (Spirit)", actual);

        record = buildRecord("Arundel", "Philip Howard", "Earl of", null, null, null, null);
        ((DataField) record.getVariableField("702")).addSubfield(MARC_FACTORY.newSubfield('c', "Saint"));
        actual = formatter.format((DataField) record.getVariableField("702"));
        Assert.assertEquals("Arundel, Philip Howard (Earl of; Saint)", actual);

        record = buildRecord("Vittorio Emmanuele", null, "Re d’Italia", null, "II", null, null);
        actual = formatter.format((DataField) record.getVariableField("702"));
        Assert.assertEquals("Vittorio Emmanuele II (Re d’Italia)", actual);

        record = buildRecord("François", null, "roi de France", "1494-1547", null, "1er", null);
        actual = formatter.format((DataField) record.getVariableField("702"));
        Assert.assertEquals("François 1er (roi de France; 1494-1547)", actual);

        record = buildRecord("Pan Painter", null, null, null, null, null, null);
        ((DataField) record.getVariableField("702")).addSubfield(MARC_FACTORY.newSubfield('a', "Jackie"));
        actual = formatter.format((DataField) record.getVariableField("702"));
        Assert.assertEquals("Pan Painter Jackie", actual);

        record = buildRecord("Callas", "Maria", null, null, null, null, "721");
        ((DataField) record.getVariableField("702")).addSubfield(MARC_FACTORY.newSubfield('4', "vso"));
        actual = formatter.format((DataField) record.getVariableField("702"));
        Assert.assertEquals("Callas, Maria. Auteur. Auteur", actual);

        record = buildRecord("Prévost", "François", null, "19..-....", null, null, null);
        ((DataField) record.getVariableField("702")).addSubfield(MARC_FACTORY.newSubfield('c', "archéologue"));
        actual = formatter.format((DataField) record.getVariableField("702"));
        Assert.assertEquals("Prévost, François (19..-....; archéologue)", actual);

        record = buildRecord("Barbey d'Aurevilly", "Jules", null, "1808-1889", null, null, null);
        actual = formatter.format((DataField) record.getVariableField("702"));
        Assert.assertEquals("Barbey d'Aurevilly, Jules (1808-1889)", actual);

        record = buildRecord("Jeanne de Chantal", null, "sainte", "1572-1641", null, null, null);
        actual = formatter.format((DataField) record.getVariableField("702"));
        Assert.assertEquals("Jeanne de Chantal (sainte; 1572-1641)", actual);

        record = buildRecord("Jeanne de Chantal", null, "sainte", "1572-1641", null, null, null);
        actual = formatter.format((DataField) record.getVariableField("702"));
        Assert.assertEquals("Jeanne de Chantal (sainte; 1572-1641)", actual);

        record = buildRecord("Antioche", "Adhémar", null, "1849-1918", null, null, "430");
        ((DataField) record.getVariableField("702")).addSubfield(MARC_FACTORY.newSubfield('c', "comte d'"));
        actual = formatter.format((DataField) record.getVariableField("702"));
        Assert.assertEquals("Antioche, Adhémar (1849-1918; comte d'). Auteur", actual);
    }

    private Record buildRecord(String data_a, String data_b, String data_c, String data_f, String data_d, String data_D, String data_4) {
        final Record record = MARC_FACTORY.newRecord();
        final DataField fld702 = MARC_FACTORY.newDataField("702", ' ', ' ');
        record.addVariableField(fld702);

        if (data_a != null) {
            fld702.addSubfield(MARC_FACTORY.newSubfield('a', data_a));
        }
        if (data_b != null) {
            fld702.addSubfield(MARC_FACTORY.newSubfield('b', data_b));
        }
        if (data_d != null) {
            fld702.addSubfield(MARC_FACTORY.newSubfield('d', data_d));
        }
        if (data_D != null) {
            fld702.addSubfield(MARC_FACTORY.newSubfield('D', data_D));
        }
        if (data_c != null) {
            fld702.addSubfield(MARC_FACTORY.newSubfield('c', data_c));
        }
        if (data_f != null) {
            fld702.addSubfield(MARC_FACTORY.newSubfield('f', data_f));
        }
        if (data_4 != null) {
            fld702.addSubfield(MARC_FACTORY.newSubfield('4', data_4));
        }
        return record;
    }

    @Test
    public void testGroovy() throws ScriptException {
        final DataField fld702 = MARC_FACTORY.newDataField("702", ' ', ' ');
        fld702.addSubfield(MARC_FACTORY.newSubfield('a', "Benson"));
        fld702.addSubfield(MARC_FACTORY.newSubfield('b', "Rowland S."));
        final String expression = "person(marc_702)";

        final Map<String, Object> bindings = new HashMap<>();
        bindings.put("marc_702", fld702);
        final Object actual = evalUserScript(expression, bindings, new PersonFieldFormatter("test", null));

        assertEquals("Benson, Rowland S.", actual);
    }

    @Test
    public void testGroovy2() throws ScriptException {
        final DataField fld702 = MARC_FACTORY.newDataField("702", ' ', ' ');
        fld702.addSubfield(MARC_FACTORY.newSubfield('a', "Benson"));
        fld702.addSubfield(MARC_FACTORY.newSubfield('b', "Rowland S."));

        final String configuration = "personFilter('a')";
        final String expression = "person(marc_702)";

        final Map<String, Object> bindings = new HashMap<>();
        bindings.put("marc_702", fld702);
        final Object actual = evalUserScript(expression, configuration, bindings, new PersonFieldFormatter("test", null));

        assertEquals("Benson", actual);
    }

    @Test
    public void testGroovy3() throws ScriptException {
        final DataField fld702 = MARC_FACTORY.newDataField("702", ' ', ' ');
        fld702.addSubfield(MARC_FACTORY.newSubfield('a', "Benson"));
        fld702.addSubfield(MARC_FACTORY.newSubfield('4', "520"));
        final String expression = "person(marc_702)";

        final Map<String, Object> bindings = new HashMap<>();
        bindings.put("marc_702", fld702);
        bindings.put(MarcMappingEvaluationService.BINDING_FN_TRANSLITERATE, new Object() {
            public String getValue(final String type, final String code) {
                return "Auteur";
            }
        });

        final Object actual = evalUserScript(expression, bindings, new PersonFieldFormatter("test", null));

        assertEquals("Benson. Auteur", actual);
    }
}
