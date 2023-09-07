package fr.progilone.pgcn.service.exchange.marc;

import static org.junit.jupiter.api.Assertions.*;

import fr.progilone.pgcn.domain.exchange.DataEncoding;
import fr.progilone.pgcn.service.exchange.marc.mapping.MarcKey;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.converter.impl.Iso5426ToUnicode;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.VariableField;

/**
 * Created by Sébastien on 22/12/2016.
 */
public class MarcUtilsTest {

    private static final MarcFactory MARC_FACTORY = MarcFactory.newInstance();

    @Test
    public void testConvert() {
        final String iso5426Str = "revue trimestrielle consacrÂee Áa la littÂerature contemporaine, Áa la technique et aux arts du livre";
        final String actual = MarcUtils.convert(iso5426Str, new Iso5426ToUnicode());
        assertEquals("revue trimestrielle consacrée à la littérature contemporaine, à la technique et aux arts du livre", actual);
    }

    /**
     * Tester un fichier unimarc pour vérifier sa validité. Renseigner le chemin
     * Laisser en @Disabled
     *
     * @throws FileNotFoundException
     */
    @Test
    @Disabled
    public void checkUniMarcValidity() throws FileNotFoundException {
        InputStream in = new FileInputStream("progilone-notices\\perio.uni");
        MarcReader reader = new MarcStreamReader(in);

        while (reader.hasNext()) {
            Record record = reader.next();
            System.out.println(record.toString());
        }
    }

    @Test
    public void testGetRecordInXml() {
        final Record record = MARC_FACTORY.newRecord();
        final DataField fld200 = MARC_FACTORY.newDataField("200", ' ', ' ');
        fld200.addSubfield(MARC_FACTORY.newSubfield('a', "Harry Potter contre attaque"));
        record.addVariableField(fld200);

        final String actual = MarcUtils.getRecordInXml(record, DataEncoding.UTF_8);
        final String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<marc:collection xmlns:marc=\"http://www.loc.gov/MARC21/slim\">"
                                + "<marc:record>"
                                + "<marc:leader>00000nam a2200000 a 4500</marc:leader>"
                                + "<marc:datafield tag=\"200\" ind1=\" \" ind2=\" \">"
                                + "<marc:subfield code=\"a\">Harry Potter contre attaque</marc:subfield>"
                                + "</marc:datafield>"
                                + "</marc:record>"
                                + "</marc:collection>";
        assertNotNull(actual);
        assertEquals(expected.trim(), actual.trim());
    }

    @Test
    public void testGetRecord() {
        final String marcxml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<marc:collection xmlns:marc=\"http://www.loc.gov/MARC21/slim\">"
                               + "<marc:record>"
                               + "<marc:leader>00000nam a2200000 a 4500</marc:leader>"
                               + "<marc:datafield tag=\"200\" ind1=\" \" ind2=\" \">"
                               + "<marc:subfield code=\"a\">Harry Potter contre attaque</marc:subfield>"
                               + "</marc:datafield>"
                               + "</marc:record>"
                               + "</marc:collection>";
        final Record actual = MarcUtils.getRecord(marcxml);

        assertNotNull(actual);
        final List<VariableField> field = actual.getVariableFields("200");
        assertEquals(1, field.size());
        final List<Subfield> subfields = ((DataField) field.get(0)).getSubfields('a');
        assertEquals(1, subfields.size());
        assertEquals("Harry Potter contre attaque", subfields.get(0).getData());
    }

    @Test
    public void testGetSubfieldFirstValue() {
        final Record record = MARC_FACTORY.newRecord();
        final DataField fld200 = MARC_FACTORY.newDataField("200", ' ', ' ');
        fld200.addSubfield(MARC_FACTORY.newSubfield('a', "Harry Potter contre attaque"));
        record.addVariableField(fld200);

        final String actualValue = MarcUtils.getSubfieldFirstValue(record, "200", 'a');
        assertEquals("Harry Potter contre attaque", actualValue);
    }

    @Test
    public void testSplitRecordByItems() {
        final MarcFactory marcFactory = MarcFactory.newInstance();

        // itemTags = null
        Record record = marcFactory.newRecord();
        List<Record> actual = MarcUtils.splitRecordByMarcKeys(record, null);

        assertEquals(1, actual.size());
        assertSame(record, actual.get(0));

        // itemTags = vide
        record = marcFactory.newRecord();
        actual = MarcUtils.splitRecordByMarcKeys(record, Collections.emptyList());

        assertEquals(1, actual.size());
        assertSame(record, actual.get(0));

        // itemTags = 995, 1 item
        record = marcFactory.newRecord();
        record.addVariableField(marcFactory.newDataField("200", ' ', ' ', "a", "Dictionnaire universel fran&#xE7;ois et latin"));
        record.addVariableField(marcFactory.newDataField("995", ' ', ' ', "9", "97397", "k", "BULAC RES MON Fol 1351"));

        actual = MarcUtils.splitRecordByMarcKeys(record, Collections.singletonList(new MarcKey("995")));

        assertEquals(1, actual.size());
        Record actualRecord = actual.get(0);
        assertEquals(1, actualRecord.getVariableFields("995").size());
        assertEquals(1, ((DataField) actualRecord.getVariableField("995")).getSubfields('9').size());
        assertEquals(1, ((DataField) actualRecord.getVariableField("995")).getSubfields('k').size());
        assertEquals("97397", ((DataField) actualRecord.getVariableField("995")).getSubfield('9').getData());
        assertEquals("BULAC RES MON Fol 1351", ((DataField) actualRecord.getVariableField("995")).getSubfield('k').getData());

        // itemTags = 995, 2 items
        record = marcFactory.newRecord();
        record.addVariableField(marcFactory.newDataField("200", ' ', ' ', "a", "Dictionnaire universel fran&#xE7;ois et latin"));
        record.addVariableField(marcFactory.newDataField("995", ' ', ' ', "9", "97396", "k", "BULAC RES MON Fol 1350"));
        record.addVariableField(marcFactory.newDataField("995", ' ', ' ', "9", "97397", "k", "BULAC RES MON Fol 1351"));

        actual = MarcUtils.splitRecordByMarcKeys(record, Collections.singletonList(new MarcKey("995")));

        assertEquals(2, actual.size());

        actualRecord = actual.get(0);
        assertEquals(1, actualRecord.getVariableFields("200").size());
        assertEquals("Dictionnaire universel fran&#xE7;ois et latin", ((DataField) actualRecord.getVariableField("200")).getSubfield('a').getData());

        assertEquals(1, actualRecord.getVariableFields("995").size());
        assertEquals(1, ((DataField) actualRecord.getVariableField("995")).getSubfields('9').size());
        assertEquals(1, ((DataField) actualRecord.getVariableField("995")).getSubfields('k').size());
        assertEquals("97396", ((DataField) actualRecord.getVariableField("995")).getSubfield('9').getData());
        assertEquals("BULAC RES MON Fol 1350", ((DataField) actualRecord.getVariableField("995")).getSubfield('k').getData());

        actualRecord = actual.get(1);
        assertEquals(1, actualRecord.getVariableFields("200").size());
        assertEquals("Dictionnaire universel fran&#xE7;ois et latin", ((DataField) actualRecord.getVariableField("200")).getSubfield('a').getData());

        assertEquals(1, actualRecord.getVariableFields("995").size());
        assertEquals(1, ((DataField) actualRecord.getVariableField("995")).getSubfields('9').size());
        assertEquals(1, ((DataField) actualRecord.getVariableField("995")).getSubfields('k').size());
        assertEquals("97397", ((DataField) actualRecord.getVariableField("995")).getSubfield('9').getData());
        assertEquals("BULAC RES MON Fol 1351", ((DataField) actualRecord.getVariableField("995")).getSubfield('k').getData());

        // itemTags = 327, 945
        record = marcFactory.newRecord();
        record.addVariableField(marcFactory.newDataField("327", ' ', ' ', "a", "1958. [1] Le programme national du PCF - Février 1958 (4 p.)"));
        record.addVariableField(marcFactory.newDataField("327", ' ', ' ', "a", "[2] Une nouvelle lettre de M. Thorez à G. Mollet - Février 1958 (1 f.)"));
        record.addVariableField(marcFactory.newDataField("945", ' ', ' ', "b", "COL4°2022(1955-1960)"));

        actual = MarcUtils.splitRecordByMarcKeys(record, Arrays.asList(new MarcKey("327"), new MarcKey("945")));

        assertEquals(2, actual.size());

        actualRecord = actual.get(0);
        assertEquals(1, actualRecord.getVariableFields("327").size());
        assertEquals("1958. [1] Le programme national du PCF - Février 1958 (4 p.)", ((DataField) actualRecord.getVariableField("327")).getSubfield('a').getData());
        assertEquals(1, actualRecord.getVariableFields("945").size());
        assertEquals("COL4°2022(1955-1960)", ((DataField) actualRecord.getVariableField("945")).getSubfield('b').getData());

        actualRecord = actual.get(1);
        assertEquals(1, actualRecord.getVariableFields("327").size());
        assertEquals("[2] Une nouvelle lettre de M. Thorez à G. Mollet - Février 1958 (1 f.)", ((DataField) actualRecord.getVariableField("327")).getSubfield('a').getData());
        assertEquals(1, actualRecord.getVariableFields("945").size());
        assertEquals("COL4°2022(1955-1960)", ((DataField) actualRecord.getVariableField("945")).getSubfield('b').getData());

        // itemTags = 327, 945
        record = marcFactory.newRecord();
        record.addVariableField(marcFactory.newDataField("327", ' ', ' ', "a", "1958. [1] Le programme national du PCF - Février 1958 (4 p.)"));
        record.addVariableField(marcFactory.newDataField("327", ' ', ' ', "a", "[2] Une nouvelle lettre de M. Thorez à G. Mollet - Février 1958 (1 f.)"));
        record.addVariableField(marcFactory.newDataField("945", ' ', ' ', "b", "COL4°2022(1955-1960)"));

        actual = MarcUtils.splitRecordByMarcKeys(record, Arrays.asList(new MarcKey("327"), new MarcKey("945")));

        assertEquals(2, actual.size());

        actualRecord = actual.get(0);
        assertEquals(1, actualRecord.getVariableFields("327").size());
        assertEquals("1958. [1] Le programme national du PCF - Février 1958 (4 p.)", ((DataField) actualRecord.getVariableField("327")).getSubfield('a').getData());
        assertEquals(1, actualRecord.getVariableFields("945").size());
        assertEquals("COL4°2022(1955-1960)", ((DataField) actualRecord.getVariableField("945")).getSubfield('b').getData());

        actualRecord = actual.get(1);
        assertEquals(1, actualRecord.getVariableFields("327").size());
        assertEquals("[2] Une nouvelle lettre de M. Thorez à G. Mollet - Février 1958 (1 f.)", ((DataField) actualRecord.getVariableField("327")).getSubfield('a').getData());
        assertEquals(1, actualRecord.getVariableFields("945").size());
        assertEquals("COL4°2022(1955-1960)", ((DataField) actualRecord.getVariableField("945")).getSubfield('b').getData());

        // itemTags = 327$a, 945$b
        record = marcFactory.newRecord();
        record.addVariableField(marcFactory.newDataField("327",
                                                         ' ',
                                                         ' ',
                                                         "a",
                                                         "1958. [1] Le programme national du PCF - Février 1958 (4 p.)",
                                                         "a",
                                                         "[2] Une nouvelle lettre de M. Thorez à G. Mollet - Février 1958 (1 f.)",
                                                         "b",
                                                         "test 1",
                                                         "b",
                                                         "test 2"));
        record.addVariableField(marcFactory.newDataField("327", ' ', ' ', "a", "1960. [1]Bienvenue à N. Khrouchtchev - avril 1960 (2 p.)", "b", "test 3"));
        record.addVariableField(marcFactory.newDataField("945", ' ', ' ', "b", "COL4°2022(1955-1960)"));

        actual = MarcUtils.splitRecordByMarcKeys(record, Arrays.asList(new MarcKey("327", 'a'), new MarcKey("945", 'b')));

        assertEquals(3, actual.size());

        // #0
        actualRecord = actual.get(0);
        assertEquals(1, actualRecord.getVariableFields("327").size());
        DataField fld327 = (DataField) actualRecord.getVariableField("327");
        assertEquals("1958. [1] Le programme national du PCF - Février 1958 (4 p.)", fld327.getSubfield('a').getData());
        List<Subfield> subfld327b = fld327.getSubfields('b');
        assertEquals(2, subfld327b.size());
        assertEquals("test 1", subfld327b.get(0).getData());
        assertEquals("test 2", subfld327b.get(1).getData());

        assertEquals(1, actualRecord.getVariableFields("945").size());
        assertEquals("COL4°2022(1955-1960)", ((DataField) actualRecord.getVariableField("945")).getSubfield('b').getData());

        // #1
        actualRecord = actual.get(1);
        assertEquals(1, actualRecord.getVariableFields("327").size());

        fld327 = (DataField) actualRecord.getVariableField("327");
        assertEquals("[2] Une nouvelle lettre de M. Thorez à G. Mollet - Février 1958 (1 f.)", fld327.getSubfield('a').getData());
        subfld327b = fld327.getSubfields('b');
        assertEquals(2, subfld327b.size());
        assertEquals("test 1", subfld327b.get(0).getData());
        assertEquals("test 2", subfld327b.get(1).getData());

        assertEquals(1, actualRecord.getVariableFields("945").size());
        assertEquals("COL4°2022(1955-1960)", ((DataField) actualRecord.getVariableField("945")).getSubfield('b').getData());

        // #2
        actualRecord = actual.get(2);
        assertEquals(1, actualRecord.getVariableFields("327").size());

        fld327 = (DataField) actualRecord.getVariableField("327");
        assertEquals("1960. [1]Bienvenue à N. Khrouchtchev - avril 1960 (2 p.)", fld327.getSubfield('a').getData());
        subfld327b = fld327.getSubfields('b');
        assertEquals(1, subfld327b.size());
        assertEquals("test 3", subfld327b.get(0).getData());

        assertEquals(1, actualRecord.getVariableFields("945").size());
        assertEquals("COL4°2022(1955-1960)", ((DataField) actualRecord.getVariableField("945")).getSubfield('b').getData());
    }

    @Test
    public void test170901() {
        final MarcFactory marcFactory = MarcFactory.newInstance();

        // itemTags = 966$a, 995$f avec 2 champs 995 dont un sans $f
        final Record record = marcFactory.newRecord();
        record.addVariableField(marcFactory.newDataField("966", ' ', ' ', "a", "0001013848"));
        record.addVariableField(marcFactory.newDataField("995", ' ', ' ', "k", "12°020.082", "r", "RC", "o", "L4"));
        record.addVariableField(marcFactory.newDataField("995", ' ', ' ', "f", "00000001704148", "k", "FLM.12°0289", "r", "TR", "o", "L7"));

        final List<Record> actual = MarcUtils.splitRecordByMarcKeys(record, Arrays.asList(new MarcKey("966", 'a'), new MarcKey("995", 'f')));

        assertEquals(1, actual.size());

        // #1
        Record actualRecord = actual.get(0);
        assertEquals(1, actualRecord.getVariableFields("966").size());
        DataField fld966 = (DataField) actualRecord.getVariableField("966");
        assertEquals("0001013848", fld966.getSubfield('a').getData());

        assertEquals(1, actualRecord.getVariableFields("995").size());
        DataField fld995 = (DataField) actualRecord.getVariableField("995");
        assertEquals("00000001704148", fld995.getSubfield('f').getData());
    }

    @Test
    public void test171201() {
        final MarcFactory marcFactory = MarcFactory.newInstance();

        // itemTags = 966$a, 2 champs 995 dont sans $f
        final Record record = marcFactory.newRecord();
        record.addVariableField(marcFactory.newDataField("966", ' ', ' ', "a", "0001029174"));
        record.addVariableField(marcFactory.newDataField("995", ' ', ' ', "k", "SDN.F.272(d)", "r", "RC"));
        record.addVariableField(marcFactory.newDataField("995", ' ', ' ', "k", "SDN.F.272(e)", "r", "RC"));

        final List<Record> actual = MarcUtils.splitRecordByMarcKeys(record, Arrays.asList(new MarcKey("966", 'a'), new MarcKey("995", 'f')));

        assertEquals(1, actual.size());

        // #0
        Record actualRecord = actual.get(0);
        assertEquals(1, actualRecord.getVariableFields("966").size());
        DataField fld966 = (DataField) actualRecord.getVariableField("966");
        assertEquals("0001029174", fld966.getSubfield('a').getData());

        assertEquals(0, actualRecord.getVariableFields("995").size());
    }

    @Test
    public void test180319() {
        final MarcFactory marcFactory = MarcFactory.newInstance();

        // itemTags = 930$a + 852$h + 852$v
        final Record record = marcFactory.newRecord();
        record.addVariableField(marcFactory.newDataField("930", ' ', ' ', "a", "xxx-930-a"));
        record.addVariableField(marcFactory.newDataField("852",
                                                         ' ',
                                                         ' ',
                                                         "h",
                                                         "4 AE SUP 125 RES A1",
                                                         "v",
                                                         "ANNEE.1863-1865;Vol.N.4-5-6 A1",
                                                         "h",
                                                         "4 AE SUP 125 RES A2",
                                                         "v",
                                                         "ANNEE.1863-1865;Vol.N.4-5-6 A2"));
        record.addVariableField(marcFactory.newDataField("852", ' ', ' ', "h", "4 AE SUP 125 RES B", "v", "ANNEE.1860-1862;Vol.N.1-2-3"));

        final List<Record> actual = MarcUtils.splitRecordByMarcKeys(record, Arrays.asList(new MarcKey("930", 'a'), new MarcKey("852", 'h'), new MarcKey("852", 'v')));

        assertEquals(3, actual.size());

        // #0
        Record actualRecord = actual.get(0);
        DataField fld852 = (DataField) actualRecord.getVariableField("852");
        assertNotNull(fld852);
        DataField fld930 = (DataField) actualRecord.getVariableField("930");
        assertNotNull(fld930);
        assertEquals("4 AE SUP 125 RES A1", fld852.getSubfield('h').getData());
        assertEquals("ANNEE.1863-1865;Vol.N.4-5-6 A1", fld852.getSubfield('v').getData());
        assertEquals("xxx-930-a", fld930.getSubfield('a').getData());

        // #1
        actualRecord = actual.get(1);
        fld852 = (DataField) actualRecord.getVariableField("852");
        assertNotNull(fld852);
        fld930 = (DataField) actualRecord.getVariableField("930");
        assertNotNull(fld930);
        assertEquals("4 AE SUP 125 RES A2", fld852.getSubfield('h').getData());
        assertEquals("ANNEE.1863-1865;Vol.N.4-5-6 A2", fld852.getSubfield('v').getData());
        assertEquals("xxx-930-a", fld930.getSubfield('a').getData());

        // #2
        actualRecord = actual.get(2);
        fld852 = (DataField) actualRecord.getVariableField("852");
        assertNotNull(fld852);
        fld930 = (DataField) actualRecord.getVariableField("930");
        assertNotNull(fld930);
        assertEquals("4 AE SUP 125 RES B", fld852.getSubfield('h').getData());
        assertEquals("ANNEE.1860-1862;Vol.N.1-2-3", fld852.getSubfield('v').getData());
        assertEquals("xxx-930-a", fld930.getSubfield('a').getData());
    }
}
