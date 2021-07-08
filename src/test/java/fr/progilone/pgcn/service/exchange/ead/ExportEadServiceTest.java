package fr.progilone.pgcn.service.exchange.ead;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;
import org.xml.sax.SAXException;

import fr.progilone.pgcn.domain.jaxb.ead.C;
import fr.progilone.pgcn.domain.jaxb.ead.Eadheader;
import fr.progilone.pgcn.service.storage.FileStorageManager;

/**
 * Created by Sébastien on 16/05/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class ExportEadServiceTest {

    private static final String TEST_DIR = FileUtils.getTempDirectoryPath() + "/pgcn_test/upload/ead";
    
    @Mock
    private FileStorageManager fm;

    private ExportEadService service;

    @Before
    public void setUp() throws Exception {
        
        service = new ExportEadService(fm);
        ReflectionTestUtils.setField(service, "workingDir", TEST_DIR);
    }

    @Test
    public void testWrite() throws IOException, JAXBException, SAXException, ParserConfigurationException {
        final Pair<Eadheader, C> parsedXml = EadTestUtils.parseXml(SAMPLE_EAD);
        final EadCParser eadCParser = new EadCParser(parsedXml.getLeft(), parsedXml.getRight());
        
        final GetInputStreamAnswer answer = new GetInputStreamAnswer();
        doAnswer(answer).when(fm).copyInputStreamToFileWithOtherDirs(any(InputStream.class), any(File.class), Arrays.asList(anyString()), anyString(), eq(true), eq(true));

        final C c = eadCParser.getcLeaves().get(1);
        service.exportEad("test", eadCParser.getEadheader(), eadCParser.getBranch(c));

        final String xmlResult = answer.getData();

        assertTrue(xmlResult.contains("<ead "));
        assertTrue(xmlResult.contains("<eadheader"));
        assertTrue(xmlResult.contains("id=\"ligeo-31382\""));
        assertFalse(xmlResult.contains("id=\"ligeo-31383\""));
        assertTrue(xmlResult.contains("id=\"ligeo-31384\""));
    }
    

    /**
     * Answer permettant de récupérer dans une String le contenu d'un InputStream
     */
    private static class GetInputStreamAnswer implements Answer {

        private String data;

        @Override
        public Object answer(final InvocationOnMock invocation) throws Throwable {
            final InputStream in = invocation.getArgumentAt(0, InputStream.class);
            data = IOUtils.toString(in, StandardCharsets.UTF_8);
            return null;
        }

        public String getData() {
            return data;
        }
    }

    private static final String SAMPLE_EAD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                                             + "<!DOCTYPE ead PUBLIC \"+//ISBN 1-931666-00-8//DTD ead.dtd (Encoded Archival Description (EAD) Version 2002)//EN\" \"/ead.dtd\">\n"
                                             + "<ead>\n"
                                             + "  <eadheader>\n"
                                             + "    <eadid>A000_31381</eadid>\n"
                                             + "    <filedesc>\n"
                                             + "      <titlestmt>\n"
                                             + "        <titleproper>Association française de science politique - Fonds Jean-Luc Parodi (fonds AFSP 2)</titleproper>\n"
                                             + "      </titlestmt>\n"
                                             + "      <publicationstmt>\n"
                                             + "        <publisher>Archive</publisher>\n"
                                             + "        <address/>\n"
                                             + "      </publicationstmt>\n"
                                             + "    </filedesc>\n"
                                             + "    <profiledesc>\n"
                                             + "      <creation>Cet instrument de recherche a été généré avec la suite Ligeo Archives<date normal=\"2017/2017\">16/05/2017</date></creation>\n"
                                             + "      <langusage>Instrument de recherche rédigé en<language>français</language></langusage>\n"
                                             + "    </profiledesc>\n"
                                             + "  </eadheader>\n"
                                             + "  <archdesc id=\"ligeo-31381\">\n"
                                             + "    <did>\n"
                                             + "      <unittitle>Association française de science politique - Fonds Jean-Luc Parodi (fonds AFSP 2)</unittitle>\n"
                                             + "      <unitid>15 J</unitid>\n"
                                             + "      <physdesc>\n"
                                             + "        <extent unit=\"ml\" type=\"longueur\"/>\n"
                                             + "      </physdesc>\n"
                                             + "      <unitdate normal=\"1980/1999\" label=\"ligeo-date-automatique\">1980-1999</unitdate>\n"
                                             + "    </did>\n"
                                             + "    <custodhist>\n"
                                             + "      <p>2017-03-03</p>\n"
                                             + "      <p>Fonds AFSP, initialement déposé dans les locaux de l’AFSP, 13, Rue de l’Université, Bâtiment René Rémond, Bureau 561, 75007 Paris. Le classement des archives dont l’AFSP est la détentrice légale a été organisé autour de trois principales séries cotées de la manière suivante : le Fonds historique (série 1 AFSP) qui conservent les documents relatifs à l’administration et aux activités scientifiques de l’association de 1949 à 1979 ; le Fonds Jean-Luc Parodi (série 2 AFSP) qui conserve les papiers déposés par Jean-Luc Parodi , Secrétaire général de l’association de 1980 à 1999 ; le Fonds contemporain (série 3 AFSP) qui collectent les documents relatifs au fonctionnement courant de l’association depuis 2000. Ces archives vivantes sont entreposées dans les locaux de l’association et servent à l’administration quotidienne de cette dernière. Cette dernière série n’est pas consultable. Ces trois fonds ont été classés par l’équipe administrative de l’AFSP en 2008.</p>\n"
                                             + "    </custodhist>\n"
                                             + "    <physloc>Bâtiment Ligeo &gt; Rez-de-chaussée &gt; Salle des entrées</physloc>\n"
                                             + "    <origination>\n"
                                             + "      <corpname authfilenumber=\"31023\">Association française de science politique (AFSP)</corpname>\n"
                                             + "    </origination>\n"
                                             + "    <phystech type=\"etat_materiel\"/>\n"
                                             + "    <accessrestrict>\n"
                                             + "      <legalstatus>Archives privées</legalstatus>\n"
                                             + "    </accessrestrict>\n"
                                             + "    <acqinfo>\n"
                                             + "      <chronlist>\n"
                                             + "        <chronitem>\n"
                                             + "          <date type=\"entree\" normal=\"2017-03-03\">2017-03-03</date>\n"
                                             + "          <event>don\t\t\t\tnuméro<num type=\"numéro d'entrée\">2017-049</num>via<persname role=\"vendeur\" source=\"annuaire_producteurs\">Association française de science politique (AFSP)</persname></event>\n"
                                             + "        </chronitem>\n"
                                             + "      </chronlist>\n"
                                             + "    </acqinfo>\n"
                                             + "    <dsc type=\"in-depth\">\n"
                                             + "      <c altrender=\"ligeo-branche-standard\" id=\"ligeo-31382\">\n"
                                             + "        <did>\n"
                                             + "          <unittitle>Fonds AFSP 2</unittitle>\n"
                                             + "          <unitid type=\"ligeo-cote-automatique\">15 J 1 à 60</unitid>\n"
                                             + "          <unitdate label=\"ligeo-date-automatique\" normal=\"1961/1999\">1961/1999</unitdate>\n"
                                             + "        </did>\n"
                                             + "        <c altrender=\"ligeo-article-standard\" id=\"ligeo-31383\" level=\"file\">\n"
                                             + "          <did>\n"
                                             + "            <unittitle>Procès-verbaux des réunions du Conseil d’Administration et des Assemblées générales de l’AFSP [28 novembre 1980 (AG et liste d’émargement du scrutin de renouvellement partiel du Conseil d’Administration), 28 novembre 1980, 4 mai 1981, 23 octobre 1981 (AG), 19 novembre 1981, 22 mars 1982, 1er février 1983, 10 janvier 1985, 8 mars 1985, 28 octobre 1985, 18 décembre 1985, 17 novembre 1986, 20 mars 1987, 15 septembre 1987, 19 octobre 1987, 30 octobre 1987, 25 janvier 1988, 23 février 1988 (dossier vide), 22 avril 1988 (dossier vide), 26 septembre 1988 (dossier vide).<lb/>7 novembre 1988, 14 février 1989, 23 novembre 1989, 23 octobre 1991, 28 novembre 1991, 20 mai 1992, 11 février 1993, 28 janvier 1994, 25 mars 1994, 27 avril 1994, 14 septembre 1994, 17 octobre 1994, 19 décembre 1994, 24 janvier 1995, 12 avril 1995, 4 mai 1995, 25 septembre 1995, 13 novembre 1995, 9 janvier 1996, 20 février 1996, 16 avril 1996, 5 juin 1996, 28 juin 199620 septembre 1996, 22 novembre 1996, 24 février 1997, 21 avril 1997, 17 juin 1997, 1er octobre 1997, 27 janvier 1998, 23 avril 1998, 23 juin 1998, 27 mai 1998, 29 septembre 1998, 24 novembre 1998, 20 janvier 1999, 3 mars 1999, 17 mai 1999, 22 juin 1999, 21 septembre 1999, 25 octobre 1999]</unittitle>\n"
                                             + "            <unitid>15 J 1</unitid>\n"
                                             + "            <unitid type=\"ancienne_cote\">2 AFSP 1 et 1 bis</unitid>\n"
                                             + "            <physdesc>\n"
                                             + "              <extent>0.1</extent>\n"
                                             + "              <extent type=\"ligeo-recolement\">0.1</extent>\n"
                                             + "            </physdesc>\n"
                                             + "            <unitdate normal=\"1980/1999\" label=\"ligeo-date-automatique\" min=\"1980-01-01\" max=\"1999-12-31\" size=\"7304\">1980-1999</unitdate>\n"
                                             + "          </did>\n"
                                             + "        </c>\n"
                                             + "        <c altrender=\"ligeo-article-standard\" id=\"ligeo-31384\" level=\"file\">\n"
                                             + "          <did>\n"
                                             + "            <unittitle>Activités scientifiques de l’association en 1980 (Matinée du 1er février 1980 : à propos du livre de René Rémond, La Règle et le consentement ; Matinée du 30 mai 1980 : à propos du livre collectif La Sagesse et le désordre. France 1980 ; Journée d’études « Regards sur la science politique française », Paris, 19 juin 1980 : correspondance diverse, programme et rapports de Pierre Favre et Albert Mabileau ;  Journée d’études «  Ecologisme et politique », Paris, 26 septembre 1980 : rapports de Daniel Boy, Alexander Nicolon, Alain-Gérard Slama et Claude-Marie Vadrot ; Matinée du 17 octobre 1980 : à propos du livre de Serge Moscovici, Psychologie des minorités actives ; Colloque «  Techniques institutionnelles et fonctionnement des systèmes politiques : réflexion sur les exemples français et italien », Paris, 6-7 novembre 1980 : rapports de Pierre Avril, François Goguel,  Jean-Luc Parodi, Gianfraco Pasquino, Umberto Pototschnig et Gustavo Zagrebelsky).</unittitle>\n"
                                             + "            <unitid>15 J 2</unitid>\n"
                                             + "            <unitid type=\"ancienne_cote\">2 AFSP 2</unitid>\n"
                                             + "            <physdesc>\n"
                                             + "              <extent type=\"ligeo-recolement\" unit=\"ml\"/>\n"
                                             + "              <extent type=\"ligeo-recolement\">0.1</extent>\n"
                                             + "            </physdesc>\n"
                                             + "            <unitdate normal=\"1980/1980\" label=\"ligeo-date-automatique\" min=\"1980-01-01\" max=\"1980-12-31\" size=\"365\">1980</unitdate>\n"
                                             + "          </did>\n"
                                             + "        </c>\n"
                                             + "      </c>\n"
                                             + "    </dsc>\n"
                                             + "  </archdesc>\n"
                                             + "</ead>\n";
}
