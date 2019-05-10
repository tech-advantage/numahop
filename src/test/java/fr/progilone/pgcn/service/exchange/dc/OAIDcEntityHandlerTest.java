package fr.progilone.pgcn.service.exchange.dc;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Created by Sébastien on 16/05/2017.
 */
public class OAIDcEntityHandlerTest {

    @Test
    public void testOaiStandard() throws IOException, JAXBException, ParserConfigurationException, SAXException {
        test(OAI_STANDARD, "EL033_P_1965_067");
    }

    @Test
    public void testOaiCalames() throws IOException, JAXBException, ParserConfigurationException, SAXException {
        test(OAI_CALAMES, "Ms. 126 [cote]");
    }

    public void test(final String xml, final String expectedIdentifier) throws
                                                                        IOException,
                                                                        JAXBException,
                                                                        ParserConfigurationException,
                                                                        SAXException {
        final File tmpFile = new File(FileUtils.getTempDirectory(), "OaiDcEntityHandlerTest_test_" + System.currentTimeMillis() + ".xml");
        try (final FileWriter writer = new FileWriter(tmpFile)) {
            IOUtils.write(xml.getBytes(), writer, StandardCharsets.UTF_8);
        }
        try {
            final List<String> identifiers = new ArrayList<>();

            new OAIDcEntityHandler(dc -> {
                dc.getAny()
                  .stream()
                  .filter(any -> StringUtils.equals(any.getName().getLocalPart(), "identifier"))
                  .findAny()
                  .map(any -> any.getValue().getContent().get(0))
                  .ifPresent(identifiers::add);
            }).parse(tmpFile);

            assertEquals(1, identifiers.size());
            assertEquals(expectedIdentifier, identifiers.get(0));
        } finally {
            FileUtils.deleteQuietly(tmpFile);
        }
    }

    // https://www.openarchives.org/OAI/openarchivesprotocol.html
    private static final String OAI_STANDARD =
        "<oai_dc:dc xmlns:oai_dc=\"http://www.openarchives.org/OAI/2.0/oai_dc/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd\">\n"
        + "  <dc:title>Demain Jean Lecanuet un homme neuf... une France en marche</dc:title>\n"
        + "  <dc:creator />\n"
        + "  <dc:subject>LECANUET Jean, France, Elections présidentielles, Ve République</dc:subject>\n"
        + "  <dc:description>Election présidentielle 1965, 1er tour, Affiche</dc:description>\n"
        + "  <dc:publisher>Sciences Po</dc:publisher>\n"
        + "  <dc:contributor />\n"
        + "  <dc:date>1965-12-05</dc:date>\n"
        + "  <dc:type>text</dc:type>\n"
        + "  <dc:format>116x38 cm</dc:format>\n"
        + "  <dc:identifier>EL033_P_1965_067</dc:identifier>\n"
        + "  <dc:source>EL033</dc:source>\n"
        + "  <dc:language>fra</dc:language>\n"
        + "  <dc:relation>archives électorales du CEVIPOF</dc:relation>\n"
        + "  <dc:coverage />\n"
        + "  <dc:rights>Attribution-NonCommercial-NoDerivs</dc:rights>\n"
        + "</oai_dc:dc>";

    // Format spécifique calames:
    // -> les déclarations des namespaces sont incorrectes (dc) ou absentes (oai_dc)
    // -> oai_dc (namespace) est utilisé au lieu de oai_dc:dc (élément dc du namespace oai_dc)
    private static final String OAI_CALAMES = "<ListRecords>\n"
                                              + "  <record>\n"
                                              + "    <header>\n"
                                              + "      <identifier>oai:oaicalames.abes.fr:BSGA10185</identifier>\n"
                                              + "      <datestamp />\n"
                                              + "      <setSpec>751059806</setSpec>\n"
                                              + "    </header>\n"
                                              + "    <metadata>\n"
                                              + "      <oai_dc>\n"
                                              + "        <dc:title xmlns:dc=\"http://purl.org/dc/elements/1.1/title\">Sacramentaire de Senlis</dc:title>\n"
                                              + "        <dc:identifier xmlns:dc=\"http://purl.org/dc/elements/1.1/identifier\">Ms. 126 [cote]</dc:identifier>\n"
                                              + "        <dc:relation xmlns:dc=\"http://purl.org/dc/elements/1.1/relation\">RCR établissement : FR-751059806</dc:relation>\n"
                                              + "        <dc:relation xmlns:dc=\"http://purl.org/dc/elements/1.1/relation\">Fonds ou collection : Manuscrits</dc:relation>\n"
                                              + "        <dc:contributor xmlns:dc=\"http://purl.org/dc/elements/1.1/contributor\">Chapitre cathédral (Arras)</dc:contributor>\n"
                                              + "        <dc:contributor xmlns:dc=\"http://purl.org/dc/elements/1.1/contributor\">Cathédrale Notre-Dame de Senlis</dc:contributor>\n"
                                              + "        <dc:subject xmlns:dc=\"http://purl.org/dc/elements/1.1/subject\">Liturgie -- Calendrier</dc:subject>\n"
                                              + "        <dc:subject xmlns:dc=\"http://purl.org/dc/elements/1.1/subject\">Étienne (saint ; ....-0037?)</dc:subject>\n"
                                              + "        <dc:subject xmlns:dc=\"http://purl.org/dc/elements/1.1/subject\">Jean l'Évangéliste (saint ; ....-006.?)</dc:subject>\n"
                                              + "        <dc:subject xmlns:dc=\"http://purl.org/dc/elements/1.1/subject\">Thomas Becket (saint ; 1120-1170)</dc:subject>\n"
                                              + "        <dc:subject xmlns:dc=\"http://purl.org/dc/elements/1.1/subject\">Rieul de Senlis (saint ; 0350?-0450?)</dc:subject>\n"
                                              + "        <dc:subject xmlns:dc=\"http://purl.org/dc/elements/1.1/subject\">Nazaire (saint)</dc:subject>\n"
                                              + "        <dc:subject xmlns:dc=\"http://purl.org/dc/elements/1.1/subject\">Celse (saint)</dc:subject>\n"
                                              + "        <dc:subject xmlns:dc=\"http://purl.org/dc/elements/1.1/subject\">Pantaléon (02..?-0305? ; saint)</dc:subject>\n"
                                              + "        <dc:subject xmlns:dc=\"http://purl.org/dc/elements/1.1/subject\">Germain (saint ; 0378?-0448)</dc:subject>\n"
                                              + "        <dc:subject xmlns:dc=\"http://purl.org/dc/elements/1.1/subject\">Priscille (sainte)</dc:subject>\n"
                                              + "        <dc:subject xmlns:dc=\"http://purl.org/dc/elements/1.1/subject\">Brice (saint)</dc:subject>\n"
                                              + "        <dc:subject xmlns:dc=\"http://purl.org/dc/elements/1.1/subject\">Procès</dc:subject>\n"
                                              + "        <dc:subject xmlns:dc=\"http://purl.org/dc/elements/1.1/subject\">Brullé (veuve)</dc:subject>\n"
                                              + "        <dc:subject xmlns:dc=\"http://purl.org/dc/elements/1.1/subject\">Guillot, Pierre</dc:subject>\n"
                                              + "        <dc:subject xmlns:dc=\"http://purl.org/dc/elements/1.1/subject\">Vic-le-Comte (Puy-de-Dôme) -- Sainte-Chapelle</dc:subject>\n"
                                              + "        <dc:format xmlns:dc=\"http://purl.org/dc/elements/1.1/format\">Importance matérielle : 214 feuillets</dc:format>\n"
                                              + "        <dc:format xmlns:dc=\"http://purl.org/dc/elements/1.1/format\">Dimensions : 265 × 175 mm</dc:format>\n"
                                              + "        <dc:format xmlns:dc=\"http://purl.org/dc/elements/1.1/format\">Particularités physiques : Parchemin. - Peinture représentant le Christ en croix (fol. 100). Grandes initiales en couleur (fol. 99)</dc:format>\n"
                                              + "        <dc:date xmlns:dc=\"http://purl.org/dc/elements/1.1/date\">1175/1199</dc:date>\n"
                                              + "        <dc:language xmlns:dc=\"http://purl.org/dc/elements/1.1/language\">lat</dc:language>\n"
                                              + "        <dc:description xmlns:dc=\"http://purl.org/dc/elements/1.1/description\">Sacramentaire écrit à Arras et adapté à l'usage de Senlis. - A peut-être appartenu à la cathédrale d'Arras avant de passer à celle de Senlis (cf. Gameson, 2007). - Calendrier pour toute l'année (fol. 3). — Oraisons ou collectes des messes de toute l'année : 1o. des fêtes du temps et de quelques saints : S. Étienne, S. Jean, évangéliste, S. Thomas de Canterbury, S. Rieul, S. Nazaire, S. Celse et S. Pantaléon, S. Germain d'Auxerre, Ste Prisce, S. Brice (fol. 9) ; — 2o. des Préfaces (fol. 99) ; — 3o. du Canon (fol. 102) ; — 4o. des fêtes des saints, 3 février-21 décembre (fol. 109) ; quelques feuillets manquent au commencement ; — 5o. des fêtes du Commun (fol. 168 vo). - Les deux feuillets liminaires du commencement du volume sont formés d'un fragment de jugement du prévôt de..... dans un procès entre une veuve Brullé et un nommé Pierre Guillot (1652). — Les deux feuillets liminaires de la fin sont formés d'un fragment de registre, contenant la copie d'actes relatifs à la fondation d'une église collégiale à Vic-le-Comte, au diocèse de Clermont (1530).</dc:description>\n"
                                              + "        <dc:rights xmlns:dc=\"http://purl.org/dc/elements/1.1/rights\">Consultation en salle de lecture de la Réserve, sur justificatif de recherche, soumise à autorisation préalable. - Tout usage public de reproductions de documents conservés à la Bibliothèque Sainte-Geneviève est soumis à autorisation préalable.</dc:rights>\n"
                                              + "      </oai_dc>\n"
                                              + "    </metadata>\n"
                                              + "  </record>\n"
                                              + "</ListRecords>";
}
