package fr.progilone.pgcn.service.exchange.ead;

import fr.progilone.pgcn.domain.jaxb.ead.C;
import fr.progilone.pgcn.domain.jaxb.ead.Eadheader;
import fr.progilone.pgcn.domain.jaxb.ead.Physdesc;
import fr.progilone.pgcn.domain.jaxb.ead.Title;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Created by Sébastien on 16/05/2017.
 */
public class EadCParserTest {

    // Bonne construction de la hiérarchie
    @Test
    public void test01() throws ParserConfigurationException, JAXBException, SAXException, IOException {
        final String xml = "<c id=\"toplevel-001\">" + "<c id=\"sublevel-001-a\"></c>" + "<c id=\"sublevel-001-b\"></c>" + "</c>";
        final C c = EadTestUtils.getCFromXml(xml);

        final EadCParser eadCParser = new EadCParser(null, c);

        assertEquals("toplevel-001", eadCParser.getRoot().getId());

        assertEquals(2, eadCParser.getcLeaves().size());
        assertTrue(eadCParser.getcLeaves()
                             .stream()
                             .allMatch(child -> "sublevel-001-a".equals(child.getId()) || "sublevel-001-b".equals(child.getId())));

        assertEquals(2, eadCParser.getParentMap().size());
        assertEquals("toplevel-001", eadCParser.getParentMap().get("sublevel-001-a").getId());
        assertEquals("toplevel-001", eadCParser.getParentMap().get("sublevel-001-b").getId());
    }

    // Attribut de C
    @Test
    public void test02() throws ParserConfigurationException, JAXBException, SAXException, IOException {
        final String xml = "<c id=\"toplevel-001\">" + "</c>";
        final C c = EadTestUtils.getCFromXml(xml);
        final EadCParser eadCParser = new EadCParser(null, c);

        // existe
        List<?> actual = eadCParser.getValues(c, "id");
        assertEquals(1, actual.size());
        assertEquals("toplevel-001", actual.get(0));

        // existe pas
        actual = eadCParser.getValues(c, "toto");
        assertTrue(actual.isEmpty());
    }

    // Attribut de Did
    @Test
    public void test03() throws ParserConfigurationException, JAXBException, SAXException, IOException {
        final String xml = "<c id=\"toplevel-001\"><did id=\"test-did\"></did></c>";
        final C c = EadTestUtils.getCFromXml(xml);

        // existe
        List<?> actual = new EadCParser(null, c).getValues(c, "did.id");
        assertEquals(1, actual.size());
        assertEquals("test-did", actual.get(0));

        // existe pas
        actual = new EadCParser(null, c).getValues(c, "did.toto");
        assertTrue(actual.isEmpty());
    }

    // Objets sous Did
    @Test
    public void test04() throws ParserConfigurationException, JAXBException, SAXException, IOException {
        final String xml = "<c id=\"toplevel-001\"><did id=\"test-did\"><unitid type=\"division\">f. 97r - 166</unitid></did></c>";
        final C c = EadTestUtils.getCFromXml(xml);

        // existe
        List<?> actual = new EadCParser(null, c).getValues(c, "did.unitid.content");
        assertEquals(1, actual.size());
        assertEquals("f. 97r - 166", actual.get(0));

        // existe pas
        actual = new EadCParser(null, c).getValues(c, "did.unitid.toto");
        assertTrue(actual.isEmpty());
    }

    // JAXBElement sous Did
    @Test
    public void test05() throws ParserConfigurationException, JAXBException, SAXException, IOException {
        final String xml = "<c id=\"toplevel-001\"><did id=\"test-did\"><physdesc><title>Le petit prince</title>test</physdesc></did></c>";
        final C c = EadTestUtils.getCFromXml(xml);

        List<?> actual = new EadCParser(null, c).getValues(c, "did.physdesc.content");
        assertEquals(2, actual.size());

        assertThat(actual.get(0), instanceOf(Title.class));
        final Title title = (Title) actual.get(0);
        assertEquals(1, title.getContent().size());
        assertEquals("Le petit prince", title.getContent().get(0));

        assertEquals("test", actual.get(1));

        actual = new EadCParser(null, c).getValues(c, "did.physdesc.title.content");
        assertEquals(1, actual.size());
        assertEquals("Le petit prince", actual.get(0));
    }

    // Objets dans une hiérarchie
    @Test
    public void test06() throws ParserConfigurationException, JAXBException, SAXException, IOException {
        final String xml = "<c id=\"toplevel-001\">"
                           + "<did id=\"test-did\">"
                           + "<unitid type=\"division\">test top 001</unitid>"
                           + "<physdesc>physdesc top 001</physdesc>"
                           + "</did>"
                           + "<c id=\"sublevel-001-a\">"
                           + "<did id=\"test-did\"><unitid type=\"division\">test sub a</unitid></did>"
                           + "</c>"
                           + "</c>";
        final C c = EadTestUtils.getCFromXml(xml);

        final EadCParser eadCParser = new EadCParser(null, c);
        assertEquals(1, eadCParser.getcLeaves().size());

        final C subC = eadCParser.getcLeaves().get(0);

        // existe dans sublevel
        List<?> actual = eadCParser.getValues(subC, "did.unitid.content");
        assertEquals(1, actual.size());
        assertEquals("test sub a", actual.get(0));

        // existe uniquement dans toplevel
        actual = eadCParser.getValues(subC, "did.physdesc.content");
        assertEquals(1, actual.size());
        assertEquals("physdesc top 001", actual.get(0));

        // recherche de l'objet physdesc
        actual = eadCParser.getValues(subC, "did.physdesc");
        assertEquals(1, actual.size());
        assertThat(actual.get(0), instanceOf(Physdesc.class));
    }

    // Objets dans une hiérarchie, remonter toute la hiérarchié
    @Test
    public void test06b() throws ParserConfigurationException, JAXBException, SAXException, IOException {
        final String xml = "<c id=\"toplevel-001\">"
                           + "<did id=\"test-did1\"><unitid type=\"division\">test top 001</unitid></did>"
                           + "<c id=\"sublevel-001-a\">"
                           + "<did id=\"test-did2\"><unitid type=\"division\">test sub a</unitid></did>"
                           + "<c id=\"sublevel-001-a-a\">"
                           + "<did id=\"test-did3\"><unitid type=\"division\">test sub sub a</unitid></did>"
                           + "</c>"
                           + "<c id=\"sublevel-001-a-b\">"
                           + "<did id=\"test-did4\"><unitid type=\"division\">test sub sub a</unitid></did>"
                           + "</c>"
                           + "</c>"
                           + "<c id=\"sublevel-002-a\">"
                           + "<did id=\"test-did5\"></did>"
                           + "<c id=\"sublevel-002-a-a\">"
                           + "<did id=\"test-did6\"><unitid type=\"division\">test sub sub b</unitid></did>"
                           + "</c>"
                           + "</c>"
                           + "</c>";
        final C c = EadTestUtils.getCFromXml(xml);

        final EadCParser eadCParser = new EadCParser(null, c);
        assertEquals(3, eadCParser.getcLeaves().size());

        // cette branche a la ppté renseignée sur tous ses noeuds
        C subC = eadCParser.getcLeaves().stream().filter(l -> l.getId().equals("sublevel-001-a-b")).findAny().orElse(null);
        assertNotNull(subC);

        // existe dans le dernier niveau uniquement
        List<?> actual = eadCParser.getValues(subC, "did.unitid.content");
        assertEquals(1, actual.size());
        assertEquals("test sub sub a", actual.get(0));

        // recherche multi-niveaux
        actual = eadCParser.getValues(subC, "all:did.unitid.content");
        assertEquals(3, actual.size());
        assertEquals("test top 001", actual.get(0));
        assertEquals("test sub a", actual.get(1));
        assertEquals("test sub sub a", actual.get(2));

        // cette branche n'a pas la ppté renseignée sur tous ses noeuds
        subC = eadCParser.getcLeaves().stream().filter(l -> l.getId().equals("sublevel-002-a-a")).findAny().orElse(null);
        assertNotNull(subC);

        // existe dans le dernier niveau uniquement
        actual = eadCParser.getValues(subC, "did.unitid.content");
        assertEquals(1, actual.size());
        assertEquals("test sub sub b", actual.get(0));

        // recherche multi-niveaux
        actual = eadCParser.getValues(subC, "all:did.unitid.content");
        assertEquals(2, actual.size());
        assertEquals("test top 001", actual.get(0));
        assertEquals("test sub sub b", actual.get(1));
    }

    // Élément précis de la hiérarchie
    @Test
    public void test07() throws ParserConfigurationException, JAXBException, SAXException, IOException {
        final String xml = "<c id=\"toplevel-001\">"
                           + "  <did id=\"test-did\">"
                           + "    <unittitle>Recueil</unittitle>"
                           + "    <physdesc>physdesc top 001</physdesc>"
                           + "  </did>"
                           + "  <c id=\"sublevel-001-a\">"
                           + "    <did id=\"test-did\">"
                           + "      <unittitle>الشرح على الجرومية لأسرار العربية</unittitle>"
                           + "    </did>"
                           + "  </c>"
                           + "  <c id=\"sublevel-001-b\">"
                           + "    <did id=\"test-did\">"
                           + "      <unittitle>al-Šarḥ ‘alá al-Ǧurūmiyyaẗ li-asrār al-‘arabiyyaẗ</unittitle>"
                           + "    </did>"
                           + "  </c>"
                           + "</c>";
        final C c = EadTestUtils.getCFromXml(xml);

        final EadCParser eadCParser = new EadCParser(null, c);
        assertEquals(2, eadCParser.getcLeaves().size());

        final C subC = eadCParser.getcLeaves().get(0);

        List<?> actual = eadCParser.getValues(subC, "root.did.unittitle.content");
        assertEquals(1, actual.size());
        assertEquals("Recueil", actual.get(0));

        actual = eadCParser.getValues(subC, "did.unittitle.content");
        assertEquals(1, actual.size());
        assertEquals("الشرح على الجرومية لأسرار العربية", actual.get(0));
    }

    // valeur de l'entête EAD
    @Test
    public void test08() throws ParserConfigurationException, JAXBException, SAXException, IOException {
        final String xml = "<ead>\n"
                           + "  <eadheader>\n"
                           + "    <profiledesc>\n"
                           + "      <langusage>Instrument de recherche rédigé en<language>français</language></langusage>\n"
                           + "    </profiledesc>\n"
                           + "  </eadheader>\n"
                           + "  <archdesc level=\"class\" id=\"ligeo-31164\">"
                           + "    <dsc type=\"in-depth\">"
                           + "      <c id=\"ligeo-31165\"></c>"
                           + "    </dsc>"
                           + "  </archdesc>"
                           + "</ead>\n";

        final Eadheader header = EadTestUtils.getEadheaderFromXml(xml);

        final EadCParser eadCParser = new EadCParser(header, new C());
        final List<?> values = eadCParser.getValues(null, "eadheader.profiledesc.langusage.language.content");
        assertEquals(1, values.size());
        assertEquals("français", values.get(0));
    }
}
