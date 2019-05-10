package fr.progilone.pgcn.service.exchange.ead;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

import javax.script.ScriptEngine;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xml.sax.SAXException;

import fr.progilone.pgcn.config.ScriptEngineConfiguration;
import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.DocProperty;
import fr.progilone.pgcn.domain.document.DocPropertyType;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.exchange.Mapping;
import fr.progilone.pgcn.domain.exchange.MappingRule;
import fr.progilone.pgcn.domain.jaxb.ead.C;
import fr.progilone.pgcn.domain.jaxb.ead.Eadheader;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.service.administration.TransliterationService;
import fr.progilone.pgcn.service.exchange.ead.mapping.CompiledMapping;
import fr.progilone.pgcn.util.TestUtil;

/**
 * Created by Sébastien on 17/05/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class EadToDocUnitConvertServiceTest {

    @Mock
    private TransliterationService transliterationService;
    private EadMappingEvaluationService mappingEvaluationService;
    private EadToDocUnitConvertService service;

    @Before
    public void setUp() {
        final ScriptEngine engine = // new GroovyScriptEngineImpl();
            new ScriptEngineConfiguration().getGroovyScriptEngine();
        mappingEvaluationService = new EadMappingEvaluationService(engine, transliterationService);
        service = new EadToDocUnitConvertService(mappingEvaluationService);
    }

    @Test
    public void testConvertExpression() throws ParserConfigurationException, JAXBException, SAXException, IOException {
        final String xml = "<c id=\"toplevel-001\">"
                           + "<did id=\"test-did\">"
                           + "<unitid type=\"division\">test top 001</unitid>"
                           + "<physdesc>Colonel Moutarde</physdesc>"
                           + "</did>"
                           + "<c id=\"sublevel-001-a\">"
                           + "<did id=\"test-did\"><unitid type=\"division\">test sub a</unitid></did>"
                           + "</c>"
                           + "</c>";
        final C c = EadTestUtils.parseXml(xml).getRight();
        final EadCParser eadCParser = new EadCParser(null, c);

        // Mapping
        final Mapping mapping = getMapping();
        final MappingRule rule = mapping.getRules().get(0);
        rule.setExpression("\\did.physdesc.content + ' (' + \\id + ')'");

        // Compilation du mapping
        final CompiledMapping compiledMapping = mappingEvaluationService.compileMapping(mapping);

        final DocUnit docUnit = service.convert(eadCParser.getcLeaves().get(0),
                                                eadCParser,
                                                compiledMapping,
                                                getPropertyTypes(),
                                                BibliographicRecord.PropertyOrder.BY_PROPERTY_TYPE);

        assertSame(mapping.getLibrary(), docUnit.getLibrary());
        assertEquals(1, docUnit.getRecords().size());
        final BibliographicRecord bibRecord = docUnit.getRecords().iterator().next();
        assertEquals(1, bibRecord.getProperties().size());

        final Iterator<DocProperty> iterator = bibRecord.getProperties().iterator();
        final DocProperty property = iterator.next();
        assertEquals("Colonel Moutarde (sublevel-001-a)", property.getValue());
    }

    @Test
    public void testConvertExpressionCondition() throws ParserConfigurationException, JAXBException, SAXException, IOException {
        final C c = EadTestUtils.parseXml(EAD_SAMPLE).getRight();
        final EadCParser eadCParser = new EadCParser(null, c);

        // Mapping
        final Mapping mapping = getMapping();
        final MappingRule rule = mapping.getRules().get(0);
        rule.setExpression("\\scopecontent.p.persname.normal");
        rule.setCondition("\\scopecontent.p.persname.role==\"sujet\"");

        // Compilation du mapping
        final CompiledMapping compiledMapping = mappingEvaluationService.compileMapping(mapping);

        final DocUnit docUnit = service.convert(eadCParser.getcLeaves().get(0),
                                                eadCParser,
                                                compiledMapping,
                                                getPropertyTypes(),
                                                BibliographicRecord.PropertyOrder.BY_PROPERTY_TYPE);

        assertSame(mapping.getLibrary(), docUnit.getLibrary());
        assertEquals(1, docUnit.getRecords().size());
        final BibliographicRecord bibRecord = docUnit.getRecords().iterator().next();
        assertEquals(11, bibRecord.getProperties().size());
    }

    @Test
    public void testConvertFormatterNormal() throws ParserConfigurationException, JAXBException, SAXException, IOException {
        final String xml = "<c id=\"toplevel-001\">"
                           + "<scopecontent>\n"
                           + "<p>Sacramentaire écrit à <geogname role=\"lieu de production\" normal=\"Arras (Pas-de-Calais)\" source=\"Sudoc\" authfilenumber=\"027250121\">Arras</geogname>.\n"
                           + "A peut-être appartenu à la <corpname source=\"Sudoc\" authfilenumber=\"080490875\" role=\"390\">cathédrale d'Arras</corpname> avant de passer à celle de Senlis (cf. Gameson, 2007).</p>"
                           + "</scopecontent>\n"
                           + "</c>";
        final C c = EadTestUtils.parseXml(xml).getRight();
        final EadCParser eadCParser = new EadCParser(null, c);

        // Mapping
        final Mapping mapping = getMapping();
        final MappingRule rule = mapping.getRules().get(0);
        rule.setExpression(
            "normal(\\scopecontent.p.geogname) + ', ' + \\scopecontent.p.geogname.content + ' - ' + normal(\\{scopecontent.p.corpname})");

        // Compilation du mapping
        final CompiledMapping compiledMapping = mappingEvaluationService.compileMapping(mapping);

        final DocUnit docUnit = service.convert(eadCParser.getcLeaves().get(0),
                                                eadCParser,
                                                compiledMapping,
                                                getPropertyTypes(),
                                                BibliographicRecord.PropertyOrder.BY_PROPERTY_TYPE);

        assertSame(mapping.getLibrary(), docUnit.getLibrary());
        assertEquals(1, docUnit.getRecords().size());
        final BibliographicRecord bibRecord = docUnit.getRecords().iterator().next();
        assertEquals(1, bibRecord.getProperties().size());

        final Iterator<DocProperty> iterator = bibRecord.getProperties().iterator();
        final DocProperty property = iterator.next();
        assertEquals("Arras (Pas-de-Calais), Arras - cathédrale d'Arras", property.getValue());
    }

    @Test
    public void testConvertFormatterText() throws ParserConfigurationException, JAXBException, SAXException, IOException {
        final C c = EadTestUtils.parseXml(EAD_SAMPLE).getRight();
        final EadCParser eadCParser = new EadCParser(null, c);

        // Mapping
        final Mapping mapping = getMapping();
        final MappingRule rule = mapping.getRules().get(0);
        rule.setExpression("text(\\scopecontent)");

        // Compilation du mapping
        final CompiledMapping compiledMapping = mappingEvaluationService.compileMapping(mapping);

        final DocUnit docUnit = service.convert(eadCParser.getcLeaves().get(0),
                                                eadCParser,
                                                compiledMapping,
                                                getPropertyTypes(),
                                                BibliographicRecord.PropertyOrder.BY_PROPERTY_TYPE);

        assertSame(mapping.getLibrary(), docUnit.getLibrary());
        assertEquals(1, docUnit.getRecords().size());
        final BibliographicRecord bibRecord = docUnit.getRecords().iterator().next();
        assertEquals(1, bibRecord.getProperties().size());

        final Iterator<DocProperty> iterator = bibRecord.getProperties().iterator();
        final DocProperty property = iterator.next();
        assertEquals(
            "Sacramentaire écrit à Arras et adapté à l'usage de Senlis. - A peut-être appartenu à la cathédrale d'Arras avant de passer à celle de Senlis (cf. Gameson, 2007). "
            + "- Calendrier pour toute l'année (fol. 3). — Oraisons ou collectes des messes de toute l'année : 1o. des fêtes du temps et de quelques saints : "
            + "S. Étienne, S. Jean, évangéliste, S. Thomas de Canterbury, S. Rieul, S. Nazaire, S. Celse et S. Pantaléon, S. Germain d'Auxerre, Ste Prisce, S. Brice (fol. 9) ;"
            + " — 2o. des Préfaces (fol. 99) ; — 3o. du Canon (fol. 102) ; — 4o. des fêtes des saints, 3 février-21 décembre (fol. 109) ; quelques feuillets manquent au commencement ;"
            + " — 5o. des fêtes du Commun (fol. 168 vo). - Les deux feuillets liminaires du commencement du volume sont formés d'un fragment de jugement du prévôt de..... "
            + "dans un procès entre une veuve Brullé et un nommé Pierre Guillot (1652). — Les deux feuillets liminaires de la fin sont formés d'un fragment de registre, "
            + "contenant la copie d'actes relatifs à la fondation d'une église collégiale à Vic-le-Comte, au diocèse de Clermont (1530).",
            property.getValue());
    }

    @Test
    public void testConvertFormatterTextWithMultipleTexts() throws ParserConfigurationException, JAXBException, SAXException, IOException {
        final C c = EadTestUtils.parseXml(EAD_SAMPLE_2).getRight();
        final EadCParser eadCParser = new EadCParser(null, c);

        // Mapping
        final Mapping mapping = getMapping();
        final MappingRule rule = mapping.getRules().get(0);
        rule.setExpression("text(\\did.unittitle)");

        // Compilation du mapping
        final CompiledMapping compiledMapping = mappingEvaluationService.compileMapping(mapping);

        final DocUnit docUnit = service.convert(eadCParser.getcLeaves().get(0),
                                                eadCParser,
                                                compiledMapping,
                                                getPropertyTypes(),
                                                BibliographicRecord.PropertyOrder.BY_PROPERTY_TYPE);

        assertSame(mapping.getLibrary(), docUnit.getLibrary());
        assertEquals(1, docUnit.getRecords().size());

        final BibliographicRecord bibRecord = docUnit.getRecords().iterator().next();
        assertEquals(2, bibRecord.getProperties().size());
        TestUtil.checkCollectionContainsSameElements(Arrays.asList("الشرح على الجرومية لأسرار العربية , زين الدين محمد بن جبريل",
                                                                   "al-Šarḥ ‘alá al-Ǧurūmiyyaẗ li-asrār al-‘arabiyyaẗ de Zayn al-Dīn Muḥammad ibn Ǧibrīl"),
                                                     bibRecord.getProperties().stream().map(DocProperty::getValue).collect(Collectors.toList()));
    }

    @Test
    public void testConvertFormatterTextWithChronlist() throws ParserConfigurationException, JAXBException, SAXException, IOException {
        final String xml = "<c id=\"toplevel-001\">"
                           + "<did id=\"test-did\">"
                           + "<unitid type=\"division\">test top 001</unitid>"
                           + "<physdesc>Colonel Moutarde</physdesc>"
                           + "</did>"
                           + "<controlaccess>"
                           + "<chronlist>"
                           + "<chronitem>"
                           + "<date type=\"entree\" normal=\"21/04/2016\">2016-21-04</date>"
                           + "<event>don\t\t\t\tnuméro <num type=\"numéro d'entrée\">2016-001</num> via <persname role=\"vendeur\" source=\"annuaire_producteurs\">Odile Rudelle</persname></event>"
                           + "</chronitem>"
                           + "<chronitem>"
                           + "<date type=\"entree\" normal=\"été 2017\">2017</date>"
                           + "<eventgrp>"
                           + "<event>Musilac</event>"
                           + "<event>Tout le monde dehors</event>"
                           + "</eventgrp>"
                           + "</chronitem>"
                           + "</chronlist>"
                           + "</controlaccess>"
                           + "<c id=\"sublevel-001-a\">"
                           + "<did id=\"test-did\"><unitid type=\"division\">test sub a</unitid></did>"
                           + "</c>"
                           + "</c>";
        final C c = EadTestUtils.parseXml(xml).getRight();
        final EadCParser eadCParser = new EadCParser(null, c);

        // Mapping
        final Mapping mapping = getMapping();
        final MappingRule rule = mapping.getRules().get(0);
        rule.setExpression("text(\\controlaccess.chronlist.chronitem)");
        rule.setExpressionConf("textSetNormal(true)");

        // Compilation du mapping
        final CompiledMapping compiledMapping = mappingEvaluationService.compileMapping(mapping);

        final DocUnit docUnit = service.convert(eadCParser.getcLeaves().get(0),
                                                eadCParser,
                                                compiledMapping,
                                                getPropertyTypes(),
                                                BibliographicRecord.PropertyOrder.BY_PROPERTY_TYPE);

        assertSame(mapping.getLibrary(), docUnit.getLibrary());
        assertEquals(1, docUnit.getRecords().size());
        final BibliographicRecord bibRecord = docUnit.getRecords().iterator().next();
        assertEquals(2, bibRecord.getProperties().size());

        final Iterator<DocProperty> iterator = bibRecord.getProperties().iterator();
        DocProperty property = iterator.next();
        assertEquals("21/04/2016 don numéro 2016-001 via Odile Rudelle", property.getValue());
        property = iterator.next();
        assertEquals("été 2017 Musilac Tout le monde dehors", property.getValue());
    }

    @Test
    public void testConvertWithMultipleValues() throws ParserConfigurationException, JAXBException, SAXException, IOException {
        final C c = EadTestUtils.parseXml(EAD_SAMPLE).getRight();
        final EadCParser eadCParser = new EadCParser(null, c);

        // Mapping
        final Mapping mapping = getMapping();
        final MappingRule rule = mapping.getRules().get(0);
        rule.setExpression("normal(\\scopecontent.p.persname)");
        rule.setCondition("(\\scopecontent.p.persname.role in ['070', '100', '330', 'sujet'])");

        // Compilation du mapping
        final CompiledMapping compiledMapping = mappingEvaluationService.compileMapping(mapping);

        final DocUnit docUnit = service.convert(eadCParser.getcLeaves().get(0),
                                                eadCParser,
                                                compiledMapping,
                                                getPropertyTypes(),
                                                BibliographicRecord.PropertyOrder.BY_PROPERTY_TYPE);

        assertSame(mapping.getLibrary(), docUnit.getLibrary());
        assertEquals(1, docUnit.getRecords().size());

        final BibliographicRecord bibRecord = docUnit.getRecords().iterator().next();
        assertEquals(11, bibRecord.getProperties().size());
        TestUtil.checkCollectionContainsSameElements(Arrays.asList("Rieul de Senlis (saint ; 0350?-0450?)",
                                                                   "Jean l'Évangéliste (saint ; ....-006.?)",
                                                                   "Thomas Becket (saint ; 1120-1170)",
                                                                   "Nazaire (saint)",
                                                                   "Celse (saint)",
                                                                   "Pantaléon (02..?-0305? ; saint)",
                                                                   "Germain (saint ; 0378?-0448)",
                                                                   "Priscille (sainte)",
                                                                   "Brice (saint)",
                                                                   "Brullé (veuve)",
                                                                   "Guillot, Pierre"),
                                                     bibRecord.getProperties().stream().map(DocProperty::getValue).collect(Collectors.toList()));

    }

    @Test
    public void testConvertWithGString() throws ParserConfigurationException, JAXBException, SAXException, IOException {
        final C c = EadTestUtils.parseXml(EAD_SAMPLE).getRight();
        final EadCParser eadCParser = new EadCParser(null, c);

        // Mapping
        final Mapping mapping = getMapping();
        final MappingRule rule = mapping.getRules().get(0);
        rule.setExpression("\"BSG, $\\did.unitid.content\"");
        rule.setCondition("\"$\\did.unitid.type\" == \"cote\"");

        // Compilation du mapping
        final CompiledMapping compiledMapping = mappingEvaluationService.compileMapping(mapping);

        final DocUnit docUnit = service.convert(eadCParser.getcLeaves().get(0),
                                                eadCParser,
                                                compiledMapping,
                                                getPropertyTypes(),
                                                BibliographicRecord.PropertyOrder.BY_PROPERTY_TYPE);

        assertSame(mapping.getLibrary(), docUnit.getLibrary());
        assertEquals(1, docUnit.getRecords().size());

        final BibliographicRecord bibRecord = docUnit.getRecords().iterator().next();
        assertEquals(1, bibRecord.getProperties().size());

        final DocProperty property = bibRecord.getProperties().iterator().next();
        assertEquals("BSG, Ms. 126", property.getValue());
    }

    @Test
    public void testConvertExpressionAbstract() throws ParserConfigurationException, JAXBException, SAXException, IOException {
        final String xml = "<c id=\"toplevel-001\">"
                           + "<did>"
                           + "<abstract label=\"Abstract\">Four manuscript survey maps and "
                           + "one plat map depicting areas of Orange County and "
                           + "attributed to the noted surveyor and judge Richard Egan. "
                           + "One map is dated 1878 and 1879 by Egan. The other maps "
                           + "are undated and unsigned but it is likely that he drew "
                           + "them during these years. These maps primarily depict "
                           + "subdivisions of non-rancho tracts of land occupying what "
                           + "is now Orange County, with the addition of some "
                           + "topographical details.</abstract> "
                           + "</did> "
                           + "</c>";
        final C c = EadTestUtils.parseXml(xml).getRight();
        final EadCParser eadCParser = new EadCParser(null, c);

        // Mapping
        final Mapping mapping = getMapping();
        final MappingRule rule = mapping.getRules().get(0);
        rule.setExpression("text(\\did.abstract)");

        // Compilation du mapping
        final CompiledMapping compiledMapping = mappingEvaluationService.compileMapping(mapping);

        final DocUnit docUnit = service.convert(eadCParser.getcLeaves().get(0),
                                                eadCParser,
                                                compiledMapping,
                                                getPropertyTypes(),
                                                BibliographicRecord.PropertyOrder.BY_PROPERTY_TYPE);

        assertSame(mapping.getLibrary(), docUnit.getLibrary());
        assertEquals(1, docUnit.getRecords().size());
        final BibliographicRecord bibRecord = docUnit.getRecords().iterator().next();
        assertEquals(1, bibRecord.getProperties().size());

        final Iterator<DocProperty> iterator = bibRecord.getProperties().iterator();
        final DocProperty property = iterator.next();
        assertEquals("Four manuscript survey maps and "
                     + "one plat map depicting areas of Orange County and "
                     + "attributed to the noted surveyor and judge Richard Egan. "
                     + "One map is dated 1878 and 1879 by Egan. The other maps "
                     + "are undated and unsigned but it is likely that he drew "
                     + "them during these years. These maps primarily depict "
                     + "subdivisions of non-rancho tracts of land occupying what "
                     + "is now Orange County, with the addition of some "
                     + "topographical details.", property.getValue());
    }

    @Test
    public void testConvertExpressionArrangement() throws ParserConfigurationException, JAXBException, SAXException, IOException {
        final String xml = "<c id=\"toplevel-001\">"
                           + "<scopecontent>"
                           + "<p>This series consists of newspaper clippings and research "
                           + "notes of Fred Reed, pertaining to the Champlain "
                           + "Transportation Company, its vessels, and the vessels' crew "
                           + "members. Several of the folders of chronological clippings "
                           + "include subjects, such as the move of the Ticonderoga "
                           + "(1954-1955) and the sale of the Champlain Transportation "
                           + "Company (1966). A number of clippings under \"Persons\" are "
                           + "obituaries. Two folders under the subseries \"Notes\" contain "
                           + "handwritten notes by Fred Reed broadly pertaining to the "
                           + "history of the Champlain Transportation Company, including "
                           + "a chronology, a list of crew members, and information about "
                           + "the Company's vessels.</p>"
                           + "<arrangement>"
                           + "<p>Organized into three subseries: "
                           + "<list type=\"simple\">"
                           + "<item>Clippings--chronological</item>"
                           + "<item>Clippings--persons</item>"
                           + "<item>Notes</item>"
                           + "</list>"
                           + "</p>"
                           + "<p>\"Clippings-persons\" is arranged alphabetically by "
                           + "surname, and \"Notes\" alphabetically by subject.</p>"
                           + "</arrangement>"
                           + "</scopecontent>"
                           + "</c>";
        final C c = EadTestUtils.parseXml(xml).getRight();
        final EadCParser eadCParser = new EadCParser(null, c);

        // Mapping
        final Mapping mapping = getMapping();
        final MappingRule rule = mapping.getRules().get(0);
        rule.setExpression("text(\\scopecontent.arrangement)");

        // Compilation du mapping
        final CompiledMapping compiledMapping = mappingEvaluationService.compileMapping(mapping);

        final DocUnit docUnit = service.convert(eadCParser.getcLeaves().get(0),
                                                eadCParser,
                                                compiledMapping,
                                                getPropertyTypes(),
                                                BibliographicRecord.PropertyOrder.BY_PROPERTY_TYPE);

        assertSame(mapping.getLibrary(), docUnit.getLibrary());
        assertEquals(1, docUnit.getRecords().size());
        final BibliographicRecord bibRecord = docUnit.getRecords().iterator().next();
        assertEquals(1, bibRecord.getProperties().size());

        final Iterator<DocProperty> iterator = bibRecord.getProperties().iterator();
        final DocProperty property = iterator.next();
        assertEquals(
            "Organized into three subseries: Clippings--chronological Clippings--persons Notes - \"Clippings-persons\" is arranged alphabetically by surname, and \"Notes\" alphabetically by subject.",
            property.getValue());
    }

    @Test
    public void testConvertExpressionOdd() throws ParserConfigurationException, JAXBException, SAXException, IOException {
        final String xml = "<c id=\"toplevel-001\">"
                           + "<odd>"
                           + "<list type=\"simple\">"
                           + "<item>Department of Economic Affairs: Industrial Policy "
                           + "Group: Registered Files (1-IG and 2-IG Series) <ref "
                           + "actuate=\"onrequest\" target=\"ew26\" show=\"new\">EW 26</ref>"
                           + "</item>"
                           + "<item>Department of Economic Affairs: Industrial "
                           + "Division and Industrial Policy Division: Registered "
                           + "Files (IA Series) <ref actuate=\"onrequest\" "
                           + "target=\"ew27\" show=\"new\">EW 27</ref>"
                           + "</item>"
                           + "</list>"
                           + "</odd> "
                           + "</c>";
        final C c = EadTestUtils.parseXml(xml).getRight();
        final EadCParser eadCParser = new EadCParser(null, c);

        // Mapping
        final Mapping mapping = getMapping();
        final MappingRule rule = mapping.getRules().get(0);
        rule.setExpression("text(\\odd)");

        // Compilation du mapping
        final CompiledMapping compiledMapping = mappingEvaluationService.compileMapping(mapping);

        final DocUnit docUnit = service.convert(eadCParser.getcLeaves().get(0),
                                                eadCParser,
                                                compiledMapping,
                                                getPropertyTypes(),
                                                BibliographicRecord.PropertyOrder.BY_PROPERTY_TYPE);

        assertSame(mapping.getLibrary(), docUnit.getLibrary());
        assertEquals(1, docUnit.getRecords().size());
        final BibliographicRecord bibRecord = docUnit.getRecords().iterator().next();
        assertEquals(1, bibRecord.getProperties().size());

        final Iterator<DocProperty> iterator = bibRecord.getProperties().iterator();
        final DocProperty property = iterator.next();
        assertEquals("Department of Economic Affairs: Industrial Policy Group: Registered Files (1-IG and 2-IG Series) "
                     + "EW 26 Department of Economic Affairs: Industrial Division and Industrial Policy Division: "
                     + "Registered Files (IA Series) EW 27", property.getValue());
    }

    @Test
    public void testConvertExpressionHeaderLanguage() throws ParserConfigurationException, JAXBException, SAXException, IOException {
        final Pair<Eadheader, C> data = EadTestUtils.parseXml(EAD_SAMPLE);
        final EadCParser eadCParser = new EadCParser(data.getLeft(), data.getRight());

        // Mapping
        final Mapping mapping = getMapping();
        final MappingRule rule = mapping.getRules().get(0);
        rule.setExpression("\\eadheader.profiledesc.langusage.language.content");

        // Compilation du mapping
        final CompiledMapping compiledMapping = mappingEvaluationService.compileMapping(mapping);

        final DocUnit docUnit = service.convert(eadCParser.getcLeaves().get(0),
                                                eadCParser,
                                                compiledMapping,
                                                getPropertyTypes(),
                                                BibliographicRecord.PropertyOrder.BY_PROPERTY_TYPE);

        assertSame(mapping.getLibrary(), docUnit.getLibrary());
        assertEquals(1, docUnit.getRecords().size());
        final BibliographicRecord bibRecord = docUnit.getRecords().iterator().next();
        assertEquals(1, bibRecord.getProperties().size());

        final Iterator<DocProperty> iterator = bibRecord.getProperties().iterator();
        final DocProperty property = iterator.next();
        assertEquals("français", property.getValue());
    }

    @Test
    public void testConvertExpressionHeaderPublisher() throws ParserConfigurationException, JAXBException, SAXException, IOException {
        final Pair<Eadheader, C> data = EadTestUtils.parseXml(EAD_SAMPLE);
        final EadCParser eadCParser = new EadCParser(data.getLeft(), data.getRight());

        // Mapping
        final Mapping mapping = getMapping();
        final MappingRule rule = mapping.getRules().get(0);
        rule.setExpression("\\eadheader.filedesc.publicationstmt.publisher.content");

        // Compilation du mapping
        final CompiledMapping compiledMapping = mappingEvaluationService.compileMapping(mapping);

        final DocUnit docUnit = service.convert(eadCParser.getcLeaves().get(0),
                                                eadCParser,
                                                compiledMapping,
                                                getPropertyTypes(),
                                                BibliographicRecord.PropertyOrder.BY_PROPERTY_TYPE);

        assertSame(mapping.getLibrary(), docUnit.getLibrary());
        assertEquals(1, docUnit.getRecords().size());
        final BibliographicRecord bibRecord = docUnit.getRecords().iterator().next();
        assertEquals(1, bibRecord.getProperties().size());

        final Iterator<DocProperty> iterator = bibRecord.getProperties().iterator();
        final DocProperty property = iterator.next();
        assertEquals("Agence Bibliographique de l'Enseignement supérieur", property.getValue());
    }

    @Test
    public void testConvertExpressionRelatedMaterial() throws ParserConfigurationException, JAXBException, SAXException, IOException {
        final String xml = "<c id=\"toplevel-001\">"
                           + "<relatedmaterial>"
                           + "<head>Related Correspondence</head>"
                           + "<p>Researchers should note that a significant amount of the "
                           + "correspondence between Franklin Wigglethorpe and Nellie "
                           + "Forbush is extant. In addition to the incoming letters in "
                           + "this collection from Mr. Wigglethorpe to Miss Forbush, the "
                           + "letters written to Mr. Wigglethorpe by Miss Forbush are "
                           + "available to researchers at the Mainline University Special "
                           + "Collections Library.</p>"
                           + "<archref><origination><persname>Wigglethorpe, "
                           + "Franklin.</persname></origination>"
                           + "<unittitle>Franklin Wigglethorpe Papers, <unitdate "
                           + "type=\"inclusive\">1782-1809.</unitdate></unittitle>"
                           + "<unitid>MSS 00143</unitid>"
                           + "</archref>"
                           + "<p>An online guide to the Wigglethorpe Papers is available.</p>"
                           + "</relatedmaterial>"
                           + "</c>";
        final EadCParser eadCParser = new EadCParser(null, EadTestUtils.parseXml(xml).getRight());

        // Mapping
        final Mapping mapping = getMapping();
        final MappingRule rule = mapping.getRules().get(0);
        rule.setExpression("text(\\relatedmaterial)");

        // Compilation du mapping
        final CompiledMapping compiledMapping = mappingEvaluationService.compileMapping(mapping);

        final DocUnit docUnit = service.convert(eadCParser.getcLeaves().get(0),
                                                eadCParser,
                                                compiledMapping,
                                                getPropertyTypes(),
                                                BibliographicRecord.PropertyOrder.BY_PROPERTY_TYPE);

        assertSame(mapping.getLibrary(), docUnit.getLibrary());
        assertEquals(1, docUnit.getRecords().size());
        final BibliographicRecord bibRecord = docUnit.getRecords().iterator().next();
        assertEquals(1, bibRecord.getProperties().size());

        final Iterator<DocProperty> iterator = bibRecord.getProperties().iterator();
        final DocProperty property = iterator.next();
        assertEquals(
            "Researchers should note that a significant amount of the correspondence between Franklin Wigglethorpe and Nellie Forbush is extant. In addition to the incoming letters in this collection from Mr. Wigglethorpe to Miss Forbush, the letters written to Mr. Wigglethorpe by Miss Forbush are available to researchers at the Mainline University Special Collections Library. Wigglethorpe, Franklin. Franklin Wigglethorpe Papers, 1782-1809. MSS 00143 An online guide to the Wigglethorpe Papers is available.",
            property.getValue());
    }

    @Test
    public void testConvertExpressionSeparatedMaterial() throws ParserConfigurationException, JAXBException, SAXException, IOException {
        final String xml = "<c id=\"toplevel-001\">"
                           + "<separatedmaterial>"
                           + "<head>Materials Cataloged Separately</head>"
                           + "<p>Photographs have been transferred to Pictorial Collections "
                           + "of The Bancroft Library.</p> "
                           + "</separatedmaterial>"
                           + "</c>";
        final EadCParser eadCParser = new EadCParser(null, EadTestUtils.parseXml(xml).getRight());

        // Mapping
        final Mapping mapping = getMapping();
        final MappingRule rule = mapping.getRules().get(0);
        rule.setExpression("text(\\separatedmaterial)");

        // Compilation du mapping
        final CompiledMapping compiledMapping = mappingEvaluationService.compileMapping(mapping);

        final DocUnit docUnit = service.convert(eadCParser.getcLeaves().get(0),
                                                eadCParser,
                                                compiledMapping,
                                                getPropertyTypes(),
                                                BibliographicRecord.PropertyOrder.BY_PROPERTY_TYPE);

        assertSame(mapping.getLibrary(), docUnit.getLibrary());
        assertEquals(1, docUnit.getRecords().size());
        final BibliographicRecord bibRecord = docUnit.getRecords().iterator().next();
        assertEquals(1, bibRecord.getProperties().size());

        final Iterator<DocProperty> iterator = bibRecord.getProperties().iterator();
        final DocProperty property = iterator.next();
        assertEquals("Photographs have been transferred to Pictorial Collections of The Bancroft Library.", property.getValue());
    }

    @Test
    public void testConvertExpressionGenreForm1() throws ParserConfigurationException, JAXBException, SAXException, IOException {
        final String xml = "<c id=\"toplevel-001\">"
                           + "<controlaccess>"
                           + "<head>Physical Characteristics of Materials in the Collection:</head>"
                           + "<genreform encodinganalog=\"655$a\" source=\"gmgpc\" type=\"type de document\" normal=\"ARCHI\">Architectural drawings</genreform>"
                           + "<genreform encodinganalog=\"655$a\" source=\"gmgpc\">Photographs</genreform>"
                           + "</controlaccess>"
                           + "</c>";
        final EadCParser eadCParser = new EadCParser(null, EadTestUtils.parseXml(xml).getRight());

        // Mapping
        final Mapping mapping = getMapping();
        final MappingRule rule = mapping.getRules().get(0);
        rule.setExpression("normal(\\controlaccess.genreform)");
        rule.setCondition("\\controlaccess.genreform.type == \"type de document\"");

        // Compilation du mapping
        final CompiledMapping compiledMapping = mappingEvaluationService.compileMapping(mapping);

        final DocUnit docUnit = service.convert(eadCParser.getcLeaves().get(0),
                                                eadCParser,
                                                compiledMapping,
                                                getPropertyTypes(),
                                                BibliographicRecord.PropertyOrder.BY_PROPERTY_TYPE);

        assertSame(mapping.getLibrary(), docUnit.getLibrary());
        assertEquals(1, docUnit.getRecords().size());
        final BibliographicRecord bibRecord = docUnit.getRecords().iterator().next();
        assertEquals(1, bibRecord.getProperties().size());

        final Iterator<DocProperty> iterator = bibRecord.getProperties().iterator();
        final DocProperty property = iterator.next();
        assertEquals("ARCHI", property.getValue());
    }

    @Test
    public void testConvertExpressionGenreForm2() throws ParserConfigurationException, JAXBException, SAXException, IOException {
        final String xml = "<c id=\"toplevel-001\">"
                           + "<did>"
                           + "<unittitle>Diaries, </unittitle>"
                           + "<unitdate type=\"inclusive\">1820-1864</unitdate>"
                           + "<physdesc><extent>14 </extent><genreform type=\"type de document\">diaries</genreform> "
                           + "bound in <physfacet type=\"cover material\">red leather</physfacet>"
                           + "</physdesc>"
                           + "</did>"
                           + "</c>";
        final EadCParser eadCParser = new EadCParser(null, EadTestUtils.parseXml(xml).getRight());

        // Mapping
        final Mapping mapping = getMapping();
        final MappingRule rule = mapping.getRules().get(0);
        rule.setExpression("normal(\\did.physdesc.genreform)");
        rule.setCondition("\\did.physdesc.genreform.type == \"type de document\"");

        // Compilation du mapping
        final CompiledMapping compiledMapping = mappingEvaluationService.compileMapping(mapping);

        final DocUnit docUnit = service.convert(eadCParser.getcLeaves().get(0),
                                                eadCParser,
                                                compiledMapping,
                                                getPropertyTypes(),
                                                BibliographicRecord.PropertyOrder.BY_PROPERTY_TYPE);

        assertSame(mapping.getLibrary(), docUnit.getLibrary());
        assertEquals(1, docUnit.getRecords().size());
        final BibliographicRecord bibRecord = docUnit.getRecords().iterator().next();
        assertEquals(1, bibRecord.getProperties().size());

        final Iterator<DocProperty> iterator = bibRecord.getProperties().iterator();
        final DocProperty property = iterator.next();
        assertEquals("diaries", property.getValue());
    }

    @Test
    public void testConvertExpressionRoot() throws ParserConfigurationException, JAXBException, SAXException, IOException {
        // Mapping
        final Mapping mapping = getMapping();
        final MappingRule rule = mapping.getRules().get(0);
        rule.setExpression("text(\\root.did.unittitle)");
        rule.setCondition("\\root != \\c");

        // Compilation du mapping
        final CompiledMapping compiledMapping = mappingEvaluationService.compileMapping(mapping);

        // #1 - pas de hiérarchie
        EadCParser eadCParser = new EadCParser(null, EadTestUtils.parseXml(EAD_SAMPLE).getRight());
        DocUnit docUnit = service.convert(eadCParser.getcLeaves().get(0),
                                          eadCParser,
                                          compiledMapping,
                                          getPropertyTypes(),
                                          BibliographicRecord.PropertyOrder.BY_PROPERTY_TYPE);

        assertSame(mapping.getLibrary(), docUnit.getLibrary());
        assertEquals(1, docUnit.getRecords().size());
        BibliographicRecord bibRecord = docUnit.getRecords().iterator().next();
        assertEquals(0, bibRecord.getProperties().size());

        // #2 - hiérarchie
        eadCParser = new EadCParser(null, EadTestUtils.parseXml(EAD_SAMPLE_2).getRight());
        docUnit = service.convert(eadCParser.getcLeaves().get(0),
                                  eadCParser,
                                  compiledMapping,
                                  getPropertyTypes(),
                                  BibliographicRecord.PropertyOrder.BY_PROPERTY_TYPE);

        assertSame(mapping.getLibrary(), docUnit.getLibrary());
        assertEquals(1, docUnit.getRecords().size());
        bibRecord = docUnit.getRecords().iterator().next();
        assertEquals(1, bibRecord.getProperties().size());

        final Iterator<DocProperty> iterator = bibRecord.getProperties().iterator();
        final DocProperty property = iterator.next();
        assertEquals("Recueil", property.getValue());
    }

    private Mapping getMapping() {
        final Mapping mapping = new Mapping();
        mapping.setLabel("Test");
        mapping.setLibrary(new Library());
        mapping.setType(Mapping.Type.EAD);
        final MappingRule rule = new MappingRule();
        mapping.addRule(rule);
        final DocPropertyType propertyType = new DocPropertyType();
        propertyType.setIdentifier("actor");
        propertyType.setRank(2);
        rule.setProperty(propertyType);
        return mapping;
    }

    private Map<String, DocPropertyType> getPropertyTypes() {
        final Map<String, DocPropertyType> propertyTypes = new HashMap<>();
        final DocPropertyType ppty = new DocPropertyType();
        ppty.setIdentifier("actor");
        propertyTypes.put(ppty.getIdentifier(), ppty);
        return propertyTypes;
    }

    private static final String EAD_SAMPLE = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
                                             + "<ead>\n"
                                             + "    <eadheader langencoding=\"iso639-2b\" scriptencoding=\"iso15924\" countryencoding=\"iso3166-1\" repositoryencoding=\"iso15511\" dateencoding=\"iso8601\" relatedencoding=\"dc\" findaidstatus=\"provisoire\">\n"
                                             + "        <eadid countrycode=\"fr\" mainagencycode=\"341720001\" identifier=\"FRCGMBPF-751052116-01a\" encodinganalog=\"identifier\">FRCGMBPF-751052116-01a.xml</eadid>\n"
                                             + "        <filedesc>\n"
                                             + "            <titlestmt>\n"
                                             + "                <titleproper encodinganalog=\"title\">Catalogue général des manuscrits des bibliothèques publiques de France</titleproper>\n"
                                             + "                <subtitle encodinganalog=\"coverage\">Paris</subtitle>\n"
                                             + "                <author encodinganalog=\"creator\">Charles Kohler</author>\n"
                                             + "                <sponsor>Ministère chargé de la Culture et Ministère chargé de l'Enseignement supérieur</sponsor>\n"
                                             + "            </titlestmt>\n"
                                             + "            <publicationstmt>\n"
                                             + "                <publisher encodinganalog=\"publisher\">Agence Bibliographique de l'Enseignement supérieur</publisher>\n"
                                             + "                <date type=\"date_de_la_première_édition\" encodinganalog=\"date\" normal=\"1893/1896\">1893-1896</date>\n"
                                             + "                <date type=\"date_de_la_version_électronique\" encodinganalog=\"date\" normal=\"2007-12-16\">16 décembre 2007</date>\n"
                                             + "            </publicationstmt>\n"
                                             + "            <notestmt>\n"
                                             + "                <note>\n"
                                             + "                    <p>Autre(s) fichier(s) lié(s) à cette institution: <extref actuate=\"onrequest\" href=\"FRCGMBPF-751052116-01b.xml\">Paris (Ms 1251-2500)</extref><extref actuate=\"onrequest\" href=\"FRCGMBPF-751052116-01c.xml\">Paris (Ms 2501-3414)</extref><extref actuate=\"onrequest\" href=\"FRCGMBPF-751052116-02a.xml\">Départements. Tome XLV (Ms. 3415-3585)</extref></p>\n"
                                             + "                </note>\n"
                                             + "            </notestmt>\n"
                                             + "        </filedesc>\n"
                                             + "        <profiledesc>\n"
                                             + "            <creation>Créé par conversion rétrospective de l'édition imprimée : Catalogue général des manuscrits de bibliothèques publiques de France. Paris. Bibliothèque Sainte-Geneviève. Paris, Plon-Nourrit, 1893-1896.<lb />Numérisation et rendu en mode texte réalisés par la société AIS (Paris, France). Encodage effectué selon la DTD EAD-2002 par la société ArchProteus (Vancouver, Canada) le 16 décembre 2007</creation>\n"
                                             + "            <langusage>Catalogue rédigé en <language langcode=\"fre\" encodinganalog=\"language\">français</language></langusage>\n"
                                             + "        </profiledesc>\n"
                                             + "    </eadheader>\n"
                                             + "    <archdesc level=\"collection\">\n"
                                             + "        <did>\n"
                                             + "            <repository>\n"
                                             + "                <corpname source=\"Répertoire_des_Centres_de_Ressources\" authfilenumber=\"751059806\" normal=\"Bibliothèque Sainte-Geneviève\">Bibliothèque Sainte-Geneviève</corpname>\n"
                                             + "                <address>\n"
                                             + "                    <addressline>10, place du Panthéon</addressline>\n"
                                             + "                    <addressline>75005 Paris</addressline>\n"
                                             + "                    <addressline>Tél. : 01 44 41 97 97</addressline>\n"
                                             + "                    <addressline>Fax : 01 44 41 97 96</addressline>\n"
                                             + "                    <addressline>Mail : reserve.bsg@univ-paris1.fr</addressline>\n"
                                             + "                    <addressline>Site web : http://www-bsg.univ-paris1.fr</addressline>\n"
                                             + "                </address>\n"
                                             + "            </repository>\n"
                                             + "            <unittitle>Manuscrits</unittitle>\n"
                                             + "        </did>\n"
                                             + "        <accessrestrict>\n"
                                             + "            <p>Consultation en salle de lecture de la Réserve, sur justificatif de recherche, soumise à autorisation préalable.</p>\n"
                                             + "        </accessrestrict>\n"
                                             + "        <userestrict>\n"
                                             + "            <p>Tout usage public de reproductions de documents conservés à la Bibliothèque Sainte-Geneviève est soumis à autorisation préalable.</p>\n"
                                             + "        </userestrict>\n"
                                             + "        <prefercite>\n"
                                             + "            <p>Bibliothèque Sainte-Geneviève, Ms. X, fol. Y</p>\n"
                                             + "        </prefercite>\n"
                                             + "        <dsc>\n"
                                             + "            <c id=\"BSGA10185\" level=\"otherlevel\" otherlevel=\"notice\">\n"
                                             + "                <did>\n"
                                             + "                    <unitid type=\"cote\">Ms. 126</unitid>\n"
                                             + "                    <unitid type=\"ancienne_cote\">BB. l. in-fol. 35</unitid>\n"
                                             + "                    <unitid type=\"ancienne_cote\">BB. 31 (rouge)</unitid>\n"
                                             + "                    <unittitle>\n"
                                             + "                        <title role=\"titre\">Sacramentaire de Senlis</title>\n"
                                             + "                    </unittitle>\n"
                                             + "                    <unitdate era=\"ce\" calendar=\"gregorian\" normal=\"1175/1199\">Fin du XII<emph render=\"super\">e</emph> siècle</unitdate>\n"
                                             + "                    <langmaterial>\n"
                                             + "                        <language langcode=\"lat\">latin</language>\n"
                                             + "                    </langmaterial>\n"
                                             + "                    <physdesc>\n"
                                             + "                        <physfacet type=\"support\">Parchemin</physfacet>\n"
                                             + "                        <extent>214 feuillets</extent>\n"
                                             + "                        <physfacet type=\"illustration\">Peinture représentant le Christ en croix (fol. 100). Grandes initiales en couleur (fol. 99)</physfacet>\n"
                                             + "                        <dimensions unit=\"mm\">265 × 175 mm</dimensions>\n"
                                             + "                    </physdesc>\n"
                                             + "                </did>\n"
                                             + "                <altformavail>\n"
                                             + "                    <p>Document numérisé et disponible en ligne.</p>\n"
                                             + "                </altformavail>\n"
                                             + "                <daogrp>\n"
                                             + "                    <daoloc role=\"vignette\" title=\"Accéder au document numérisé \" href=\"https://archive.org/download/MS126/BSG_MS126_0021.jpg\" />\n"
                                             + "                    <daoloc role=\"rebond\" title=\"Accéder au document numérisé \" href=\"https://archive.org/details/MS126\" />\n"
                                             + "                </daogrp>\n"
                                             + "                <altformavail>\n"
                                             + "                    <p>Des éléments de décor du manuscrit ont été numérisés par l’IRHT. Ils sont consultables dans la <extref href=\"http://bvmm.irht.cnrs.fr/resultRecherche/resultRecherche/7987\">Bibliothèque virtuelle des manuscrits médiévaux</extref>.</p>\n"
                                             + "                </altformavail>\n"
                                             + "                <scopecontent>\n"
                                             + "                    <p>Sacramentaire écrit à <geogname role=\"lieu de production\" normal=\"Arras (Pas-de-Calais)\" source=\"Sudoc\" authfilenumber=\"027250121\">Arras</geogname> et adapté à l'usage de <geogname normal=\"Senlis (Oise)\" source=\"Sudoc\" authfilenumber=\"027251535\" role=\"lieu de production\">Senlis</geogname>.</p>\n"
                                             + "                    <p>A peut-être appartenu à la <corpname normal=\"Chapitre cathédral (Arras)\" source=\"Sudoc\" authfilenumber=\"080490875\" role=\"390\">cathédrale d'Arras</corpname> avant de passer à celle de Senlis (cf. Gameson, 2007).</p>\n"
                                             + "                    <p>\n"
                                             + "                        <subject normal=\"Liturgie -- Calendrier\" source=\"Sudoc\" authfilenumber=\"032514891\">Calendrier</subject> pour toute l'année (fol. 3). — Oraisons ou collectes des messes de toute l'année : 1<emph render=\"super\">o</emph>. des fêtes du temps et de quelques saints : <persname normal=\"Étienne (saint ; ....-0037?)\" source=\"Sudoc\" authfilenumber=\"03126915X\" role=\"pas_sujet\">S. Étienne</persname>, <persname normal=\"Jean l'Évangéliste (saint ; ....-006.?)\" source=\"Sudoc\" authfilenumber=\"027562611\" role=\"sujet\">S. Jean, évangéliste</persname>, <persname normal=\"Thomas Becket (saint ; 1120-1170)\" source=\"Sudoc\" authfilenumber=\"034398805\" role=\"sujet\">S. Thomas de Canterbury</persname>, <persname role=\"sujet\" normal=\"Rieul de Senlis (saint ; 0350?-0450?)\">S. Rieul</persname>, <persname normal=\"Nazaire (saint)\" source=\"Sudoc\" authfilenumber=\"175208492\" role=\"sujet\">S. Nazaire</persname>, <persname normal=\"Celse (saint)\" source=\"Sudoc\" authfilenumber=\"175208484\" role=\"sujet\">S. Celse</persname> et <persname normal=\"Pantaléon (02..?-0305? ; saint)\" source=\"Sudoc\" authfilenumber=\"081437560\" role=\"sujet\">S. Pantaléon</persname>, <persname normal=\"Germain (saint ; 0378?-0448)\" source=\"Sudoc\" authfilenumber=\"027946916\" role=\"sujet\">S. Germain d'Auxerre</persname>, <persname normal=\"Priscille (sainte)\" source=\"Sudoc\" authfilenumber=\"17515936X\" role=\"sujet\">S<emph render=\"super\">te</emph> Prisce</persname>, <persname normal=\"Brice (saint)\" source=\"Sudoc\" authfilenumber=\"175617376\" role=\"sujet\">S. Brice</persname> (fol. 9) ; — 2<emph render=\"super\">o</emph>. des Préfaces (fol. 99) ; — 3<emph render=\"super\">o</emph>. du Canon (fol. 102) ; — 4<emph render=\"super\">o</emph>. des fêtes des saints, 3 février-21 décembre (fol. 109) ; quelques feuillets manquent au commencement ; — 5<emph render=\"super\">o</emph>. des fêtes du Commun (fol. 168 v<emph render=\"super\">o</emph>).</p>\n"
                                             + "                    <p>Les deux feuillets liminaires du commencement du volume sont formés d'un fragment de jugement du prévôt de..... dans un <subject normal=\"Procès\" source=\"Sudoc\" authfilenumber=\"027776298\">procès</subject> entre une <persname role=\"sujet\" normal=\"Brullé (veuve)\">veuve Brullé</persname> et un nommé <persname role=\"sujet\" normal=\"Guillot, Pierre\">Pierre Guillot</persname> (1652). — Les deux feuillets liminaires de la fin sont formés d'un fragment de registre, contenant la copie d'actes relatifs à la fondation d'une église collégiale à <geogname normal=\"Vic-le-Comte (Puy-de-Dôme) -- Sainte-Chapelle\" source=\"Sudoc\" authfilenumber=\"056591721\" role=\"sujet\">Vic-le-Comte</geogname>, au diocèse de Clermont (1530).</p>\n"
                                             + "                </scopecontent>\n"
                                             + "                <custodhist>\n"
                                             + "                    <p>« Ex libris S. Genovefae Paris., 1753 »</p>\n"
                                             + "                </custodhist>\n"
                                             + "                <bibliography>\n"
                                             + "                    <head>Notices</head>\n"
                                             + "                    <bibref>LEROQUAIS (Victor), <emph render=\"italic\">Les sacramentaires et missels manuscrits des bibliothèques publiques de France</emph>, Paris, 1924, I, n° 132, p. 272-274.</bibref>\n"
                                             + "                </bibliography>\n"
                                             + "                <bibliography>\n"
                                             + "                    <head>Etudes et citations</head>\n"
                                             + "                    <bibref>CORBIN (Solange), <emph render=\"italic\">Les notations neumatiques</emph>, thèse, Paris, 1957, p. 440.</bibref>\n"
                                             + "                    <bibref>Eg. I. Strubbe, L. Voet, <emph render=\"italic\">De chronologie van de middleleeuwen en de modern tijden in de Nederlanden</emph>, Anvers ; Amsterdam, 1960.</bibref>\n"
                                             + "                    <bibref>\n"
                                             + "                        <emph render=\"italic\">Répertoire de manuscrits médiévaux contenant des notations musicales</emph>, dir. Solange Corbin, I : Bibliothèque Sainte-Geneviève, par Madeleine Bernard, Paris, 1965, p. 33, 129, pl. 7.</bibref>\n"
                                             + "                    <bibref>CANAL (José M.), « Intorno a S. Fulberto de Chartres († 1028) », <emph render=\"italic\">Ephemerides liturgicae</emph>, 80, 1966, p. 211-225.</bibref>\n"
                                             + "                    <bibref>BABOIS-AUBOYNEAU (Agnès), <emph render=\"italic\">L'illustration des sacramentaires et missels de l'an 1000 aux années 1150</emph>, I : Synthèse. II : Annexes et catalogue. III : Illustrations, Poitiers, 1995, p. 63.</bibref>\n"
                                             + "                    <bibref>SATTLER (Veronika), <emph render=\"italic\">Zwischen Andachtsbuch und Aventiure : Der Neufville-Vitasse-Paslter</emph>, Hamburg, Kovac, 2006 (mention t.1, p. 63-64).</bibref>\n"
                                             + "                    <bibref>GAMESON (Richard), « The earliest books of Arras cathedral », <emph render=\"italic\">Scriptorium</emph>, 61, 2007-2, p.233-285.</bibref>\n"
                                             + "                </bibliography>\n"
                                             + "                <controlaccess>\n"
                                             + "                    <corpname role=\"390\" normal=\"Cathédrale Notre-Dame de Senlis\">Cathédrale Notre-Dame (Senlis)</corpname>\n"
                                             + "                </controlaccess>\n"
                                             + "            </c>\n"
                                             + "        </dsc>\n"
                                             + "    </archdesc>\n"
                                             + "</ead>";

    private static final String EAD_SAMPLE_2 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
                                               + "<c id=\"Calames-2016441942142334\" level=\"\">\n"
                                               + "    <did>\n"
                                               + "        <unitid type=\"cote\">MS.ARA.15</unitid>\n"
                                               + "        <unitid type=\"ancienne_cote\">690</unitid>\n"
                                               + "        <unittitle>Recueil</unittitle>\n"
                                               + "        <physdesc>\n"
                                               + "            <physfacet type=\"support\">Papier</physfacet>\n"
                                               + "            <extent>169 feuillets et   2 cahiers</extent>\n"
                                               + "            <physfacet type=\"écriture\">Ecriture Maghrébine</physfacet>\n"
                                               + "            <physfacet type=\"codicologie\">Les feuillets sont numérotés de 1 à 166, les feuillets avant les n°13, 43 et 104 ne sont pas numérotés ; f.65v et f.97r en blanc pour marquer la séparation entre les différentes parties du recueil</physfacet>\n"
                                               + "            <dimensions>230 x 170 mm</dimensions>\n"
                                               + "            <physfacet type=\"reliure\">Reliure restaurée : européenne ; l'ancienne était orientale</physfacet>\n"
                                               + "        </physdesc>\n"
                                               + "    </did>\n"
                                               + "    <scopecontent>\n"
                                               + "        <p>2 cahiers sont associés à ce recueil. </p>\n"
                                               + "        <p>Le 1<emph render=\"super\">er</emph> cahier a été numéroté comme étant la suite du recueil : 167 - 181</p>\n"
                                               + "    </scopecontent>\n"
                                               + "    <c id=\"Calames-2016441942142335\">\n"
                                               + "        <did>\n"
                                               + "            <unitid type=\"division\">f. 1-65</unitid>\n"
                                               + "            <unittitle>\n"
                                               + "                <title role=\"titre\" type=\"non-latin originel\"> الشرح على الجرومية لأسرار العربية </title> , <persname role=\"070\" normal=\"زين الدين محمد بن جبريل\" source=\"Sudoc\" authfilenumber=\"192925598\">زين الدين محمد بن جبريل </persname></unittitle>\n"
                                               + "            <langmaterial>\n"
                                               + "                <language langcode=\"ara\" scriptcode=\"Arab\">Arabe</language>\n"
                                               + "            </langmaterial>\n"
                                               + "            <unittitle>\n"
                                               + "                <title role=\"titre\">al-Šarḥ ‘alá al-Ǧurūmiyyaẗ    li-asrār al-‘arabiyyaẗ </title> de <persname role=\"070\" normal=\"Ǧibrīl, Zayn al-Dīn Muḥammad Ibn (1518)\" source=\"Sudoc\" authfilenumber=\"192925598\">Zayn al-Dīn Muḥammad ibn Ǧibrīl</persname></unittitle>\n"
                                               + "            <unitdate era=\"ce\" calendar=\"gregorian\" normal=\"18650312\"> 14 شوال  [Šawwāl] 1281 (12 mars 1865)</unitdate>\n"
                                               + "        </did>\n"
                                               + "    </c>\n"
                                               + "</c>";
}
