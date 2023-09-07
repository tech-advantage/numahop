package fr.progilone.pgcn.service.exchange.marc;

import static org.junit.jupiter.api.Assertions.*;

import fr.progilone.pgcn.config.ScriptEngineConfiguration;
import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.DocProperty;
import fr.progilone.pgcn.domain.document.DocPropertyType;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.exchange.Mapping;
import fr.progilone.pgcn.domain.exchange.MappingRule;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.service.administration.TransliterationService;
import fr.progilone.pgcn.service.exchange.marc.mapping.CompiledMapping;
import fr.progilone.pgcn.service.exchange.marc.mapping.CompiledMappingRule;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.script.ScriptEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.marc4j.converter.impl.Iso5426ToUnicode;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Created by Sebastien on 24/11/2016.
 */
@ExtendWith(MockitoExtension.class)
public class MarcToDocUnitConvertServiceTest {

    private static final MarcFactory MARC_FACTORY = MarcFactory.newInstance();

    @Mock
    private TransliterationService transliterationService;

    private MarcMappingEvaluationService mappingEvaluationService;
    private MarcToDocUnitConvertService service;

    @BeforeEach
    public void setUp() {
        final ScriptEngine engine = new ScriptEngineConfiguration().getGroovyScriptEngine();
        mappingEvaluationService = new MarcMappingEvaluationService(engine, transliterationService);
        service = new MarcToDocUnitConvertService(mappingEvaluationService);
    }

    // Test expression sans condition
    @Test
    public void testNoConditions() {
        // Notice
        Record record = MARC_FACTORY.newRecord();
        record.addVariableField(MARC_FACTORY.newControlField("001", "TRAV-J"));
        DataField fld035 = MARC_FACTORY.newDataField("035", ' ', ' ');
        fld035.addSubfield(MARC_FACTORY.newSubfield('a', "ppn0123456789"));
        record.addVariableField(fld035);
        fld035 = MARC_FACTORY.newDataField("035", ' ', ' ');
        fld035.addSubfield(MARC_FACTORY.newSubfield('a', "ppn999999"));
        record.addVariableField(fld035);
        final DataField fld702 = MARC_FACTORY.newDataField("702", ' ', ' ');
        fld702.addSubfield(MARC_FACTORY.newSubfield('a', "Travolta"));
        fld702.addSubfield(MARC_FACTORY.newSubfield('b', "John"));
        record.addVariableField(fld702);

        // Mapping
        final Mapping mapping = getMapping();
        mapping.getRules()
               .get(0)
               .setExpression("'(' + \\001 + ') ' " + "+ \\702$a.toUpperCase() + ', ' + \\702$b.toLowerCase() + ' - ' "
                              + "+ (\\035$a.replaceFirst(/^ppn(.*)$/, '$1') ?: 'non renseigné')");

        // Compilation du mapping
        final CompiledMapping compiledMapping = mappingEvaluationService.compileMapping(mapping, null);

        // script parent key
        final CompiledMappingRule joinExpression = mappingEvaluationService.compileMapping("\\001", null);

        // Test
        final List<DocUnitWrapper> docUnitWrappers = service.convert(record, null, compiledMapping, joinExpression, getPropertyTypes());

        assertEquals(1, docUnitWrappers.size());
        assertEquals("TRAV-J", docUnitWrappers.get(0).getParentKey());

        final DocUnit docUnit = docUnitWrappers.get(0).getDocUnit();

        assertSame(mapping.getLibrary(), docUnit.getLibrary());
        assertEquals(1, docUnit.getRecords().size());
        final BibliographicRecord bibRecord = docUnit.getRecords().iterator().next();
        assertEquals(2, bibRecord.getProperties().size());

        final Iterator<DocProperty> iterator = bibRecord.getProperties().iterator();
        DocProperty property = iterator.next();
        assertEquals("(TRAV-J) TRAVOLTA, john - 0123456789", property.getValue());
        assertNotNull(property.getRank());

        property = iterator.next();
        assertEquals("(TRAV-J) TRAVOLTA, john - 999999", property.getValue());
        assertNotNull(property.getRank());
    }

    // Test expression constante
    @Test
    public void testNoConditionsConstantExpression() {
        // Notice
        Record record = MARC_FACTORY.newRecord();
        record.addVariableField(MARC_FACTORY.newControlField("001", "TRAV-J"));

        // Mapping
        final Mapping mapping = getMapping();
        mapping.getRules().get(0).setExpression("'constant string'");

        // Compilation du mapping
        final CompiledMapping compiledMapping = mappingEvaluationService.compileMapping(mapping, null);

        // Test
        final List<DocUnitWrapper> docUnitWrappers = service.convert(record, null, compiledMapping, null, getPropertyTypes());

        assertEquals(1, docUnitWrappers.size());
        final DocUnit docUnit = docUnitWrappers.get(0).getDocUnit();

        assertSame(mapping.getLibrary(), docUnit.getLibrary());
        assertEquals(1, docUnit.getRecords().size());
        final BibliographicRecord bibRecord = docUnit.getRecords().iterator().next();
        assertEquals(1, bibRecord.getProperties().size());

        final Iterator<DocProperty> iterator = bibRecord.getProperties().iterator();
        DocProperty property = iterator.next();
        assertEquals("constant string", property.getValue());
    }

    // Test expression null
    @Test
    public void testNoConditionsNullExpression() {
        // Notice
        Record record = MARC_FACTORY.newRecord();
        record.addVariableField(MARC_FACTORY.newControlField("001", "TRAV-J"));

        // Mapping
        final Mapping mapping = getMapping();
        mapping.getRules().get(0).setExpression("null");

        // Compilation du mapping
        final CompiledMapping compiledMapping = mappingEvaluationService.compileMapping(mapping, null);

        // Test
        final List<DocUnitWrapper> docUnitWrappers = service.convert(record, null, compiledMapping, null, getPropertyTypes());

        assertEquals(1, docUnitWrappers.size());
        final DocUnit docUnit = docUnitWrappers.get(0).getDocUnit();

        assertSame(mapping.getLibrary(), docUnit.getLibrary());
        assertEquals(1, docUnit.getRecords().size());
        final BibliographicRecord bibRecord = docUnit.getRecords().iterator().next();
        assertEquals(0, bibRecord.getProperties().size());
    }

    // Test expression avec un formatter personnalisé
    @Test
    public void textCustomFormatter() {
        // Notice
        Record record = MARC_FACTORY.newRecord();
        final DataField fld702 = MARC_FACTORY.newDataField("702", ' ', ' ');
        fld702.addSubfield(MARC_FACTORY.newSubfield('a', "Benson"));
        fld702.addSubfield(MARC_FACTORY.newSubfield('b', "Rowland S."));
        record.addVariableField(fld702);

        // Mapping
        final Mapping mapping = getMapping();
        mapping.getRules().get(0).setExpression("person(\\702)");

        // Compilation du mapping
        final CompiledMapping compiledMapping = mappingEvaluationService.compileMapping(mapping, null);
        assertEquals(1, compiledMapping.getCompiledRules().size());

        final List<DocUnitWrapper> docUnitWrappers = service.convert(record, new Iso5426ToUnicode(), compiledMapping, null, getPropertyTypes());

        assertEquals(1, docUnitWrappers.size());
        final DocUnit docUnit = docUnitWrappers.get(0).getDocUnit();

        final BibliographicRecord bibRecord = docUnit.getRecords().iterator().next();
        final Iterator<DocProperty> iterator = bibRecord.getProperties().iterator();
        DocProperty property = iterator.next();
        assertEquals("Benson, Rowland S.", property.getValue());
    }

    // Test expression sur champ xx
    @Test
    public void textExpressionSubfields() {
        // Notice
        Record record = MARC_FACTORY.newRecord();
        final DataField fld606 = MARC_FACTORY.newDataField("606", ' ', ' ');
        fld606.addSubfield(MARC_FACTORY.newSubfield('a', "Benson"));
        fld606.addSubfield(MARC_FACTORY.newSubfield('c', "xxx"));
        fld606.addSubfield(MARC_FACTORY.newSubfield('b', "Rowland S."));
        record.addVariableField(fld606);

        // Mapping
        final Mapping mapping = getMapping();
        final MappingRule rule = mapping.getRules().get(0);
        rule.setExpressionConf("subfieldsAdd('a', ', ')");
        rule.setExpression("subfields(\\606)");

        // Compilation du mapping
        final CompiledMapping compiledMapping = mappingEvaluationService.compileMapping(mapping, null);
        assertEquals(1, compiledMapping.getCompiledRules().size());

        // Test
        final List<DocUnitWrapper> docUnitWrappers = service.convert(record, null, compiledMapping, null, getPropertyTypes());

        assertEquals(1, docUnitWrappers.size());
        final DocUnit docUnit = docUnitWrappers.get(0).getDocUnit();

        assertSame(mapping.getLibrary(), docUnit.getLibrary());
        assertEquals(1, docUnit.getRecords().size());
        final BibliographicRecord bibRecord = docUnit.getRecords().iterator().next();
        assertEquals(1, bibRecord.getProperties().size());

        final Iterator<DocProperty> iterator = bibRecord.getProperties().iterator();
        DocProperty property = iterator.next();
        assertEquals("Benson", property.getValue());
    }

    @Test
    public void testLeader() {
        // Notice
        Record record = MARC_FACTORY.newRecord();
        record.addVariableField(MARC_FACTORY.newControlField("001", "TRAV-J"));

        // Mapping
        final Mapping mapping = getMapping();
        mapping.getRules().get(0).setExpression("leader.getTypeOfRecord()");

        // Compilation du mapping
        final CompiledMapping compiledMapping = mappingEvaluationService.compileMapping(mapping, null);

        // Test
        final List<DocUnitWrapper> docUnitWrappers = service.convert(record, null, compiledMapping, null, getPropertyTypes());

        assertEquals(1, docUnitWrappers.size());
        final DocUnit docUnit = docUnitWrappers.get(0).getDocUnit();

        assertSame(mapping.getLibrary(), docUnit.getLibrary());
        assertEquals(1, docUnit.getRecords().size());
        final BibliographicRecord bibRecord = docUnit.getRecords().iterator().next();
        assertEquals(1, bibRecord.getProperties().size());

        final Iterator<DocProperty> iterator = bibRecord.getProperties().iterator();
        DocProperty property = iterator.next();
        assertEquals("a", property.getValue());
    }

    @Test
    public void testObjectFields() {
        // Notice
        Record record = MARC_FACTORY.newRecord();
        record.addVariableField(MARC_FACTORY.newControlField("001", "TRAV-J"));

        // Mapping
        final Mapping mapping = getMapping();
        final MappingRule rule = mapping.getRules().get(0);
        rule.setDocUnitField("rights");     // enum
        rule.setBibRecordField("title");    // string
        rule.setProperty(null);
        rule.setExpression("'FREE'");

        // Compilation du mapping
        final CompiledMapping compiledMapping = mappingEvaluationService.compileMapping(mapping, null);

        // Test
        final List<DocUnitWrapper> docUnitWrappers = service.convert(record, null, compiledMapping, null, getPropertyTypes());

        assertEquals(1, docUnitWrappers.size());
        final DocUnit docUnit = docUnitWrappers.get(0).getDocUnit();

        assertSame(mapping.getLibrary(), docUnit.getLibrary());
        assertEquals(DocUnit.RightsEnum.FREE, docUnit.getRights());
        assertEquals(1, docUnit.getRecords().size());

        final BibliographicRecord bibRecord = docUnit.getRecords().iterator().next();
        assertEquals("FREE", bibRecord.getTitle());
        assertTrue(bibRecord.getProperties().isEmpty());
    }

    // Test expression sur champ xx
    @Test
    public void textExpressionXXWithCondition1() {
        // Notice
        Record record = MARC_FACTORY.newRecord();
        final DataField fld606 = MARC_FACTORY.newDataField("606", ' ', ' ');
        fld606.addSubfield(MARC_FACTORY.newSubfield('a', "Benson"));
        fld606.addSubfield(MARC_FACTORY.newSubfield('b', "Rowland S."));
        record.addVariableField(fld606);
        final DataField fld607 = MARC_FACTORY.newDataField("607", ' ', ' ');
        fld607.addSubfield(MARC_FACTORY.newSubfield('a', "Mouse"));
        fld607.addSubfield(MARC_FACTORY.newSubfield('b', "Mickey"));
        record.addVariableField(fld607);
        final DataField fld610 = MARC_FACTORY.newDataField("610", ' ', ' ');
        fld610.addSubfield(MARC_FACTORY.newSubfield('a', "Boss"));
        fld610.addSubfield(MARC_FACTORY.newSubfield('b', "Hugo"));
        record.addVariableField(fld610);
        final DataField fld702 = MARC_FACTORY.newDataField("702", ' ', ' ');
        fld702.addSubfield(MARC_FACTORY.newSubfield('a', "Man"));
        fld702.addSubfield(MARC_FACTORY.newSubfield('b', "Super"));
        record.addVariableField(fld702);
        final DataField fld712 = MARC_FACTORY.newDataField("712", ' ', ' ');
        fld712.addSubfield(MARC_FACTORY.newSubfield('a', "Coca"));
        fld712.addSubfield(MARC_FACTORY.newSubfield('b', "Cola"));
        record.addVariableField(fld712);

        // Mapping
        final Mapping mapping = getMapping();
        final MappingRule rule = mapping.getRules().get(0);
        rule.setExpression("\\6xx$a + ' - ' + \\6xx$b + ' (' + \\702$b + ')'");
        // pour les champs 6xx différents de 610
        rule.setCondition("!\\610");

        // Compilation du mapping
        final CompiledMapping compiledMapping = mappingEvaluationService.compileMapping(mapping, null);
        assertEquals(1, compiledMapping.getCompiledRules().size());

        // Test
        final List<DocUnitWrapper> docUnitWrappers = service.convert(record, null, compiledMapping, null, getPropertyTypes());

        assertEquals(1, docUnitWrappers.size());
        final DocUnit docUnit = docUnitWrappers.get(0).getDocUnit();

        assertSame(mapping.getLibrary(), docUnit.getLibrary());
        assertEquals(1, docUnit.getRecords().size());
        final BibliographicRecord bibRecord = docUnit.getRecords().iterator().next();
        assertEquals(2, bibRecord.getProperties().size());

        final Iterator<DocProperty> iterator = bibRecord.getProperties().iterator();
        DocProperty property = iterator.next();
        assertEquals("Benson - Rowland S. (Super)", property.getValue());
        property = iterator.next();
        assertEquals("Mouse - Mickey (Super)", property.getValue());
    }

    // Test expression sur champ xx
    @Test
    public void textExpressionXXWithCondition2() {
        // Notice
        Record record = MARC_FACTORY.newRecord();
        DataField fld606 = MARC_FACTORY.newDataField("606", ' ', ' ');
        fld606.addSubfield(MARC_FACTORY.newSubfield('a', "Benson"));
        fld606.addSubfield(MARC_FACTORY.newSubfield('b', "Rowland S."));
        record.addVariableField(fld606);
        fld606 = MARC_FACTORY.newDataField("607", ' ', ' ');
        fld606.addSubfield(MARC_FACTORY.newSubfield('a', "Benson"));
        fld606.addSubfield(MARC_FACTORY.newSubfield('x', "test 2"));
        record.addVariableField(fld606);
        fld606 = MARC_FACTORY.newDataField("606", ' ', ' ');
        fld606.addSubfield(MARC_FACTORY.newSubfield('a', "Benson"));
        fld606.addSubfield(MARC_FACTORY.newSubfield('x', "test 3"));
        fld606.addSubfield(MARC_FACTORY.newSubfield('2', "rameau"));
        record.addVariableField(fld606);

        // Mapping
        final Mapping mapping = getMapping();
        final MappingRule rule = mapping.getRules().get(0);
        rule.setExpression("\\6xx$a + ' ' + \\6xx$x");
        // Si la notice ne possède pas de champ 607, alors aller chercher l'info dans les champs 6xx possédant un $x ou un $y, avec $2 == 'rameau'
        rule.setCondition("!exists(record, \\608) && (\\6xx$x || \\6xx$y) && \\6xx$2 == 'rameau'");

        // Compilation du mapping
        final CompiledMapping compiledMapping = mappingEvaluationService.compileMapping(mapping, null);
        assertEquals(1, compiledMapping.getCompiledRules().size());

        // Test
        final List<DocUnitWrapper> docUnitWrappers = service.convert(record, null, compiledMapping, null, getPropertyTypes());

        assertEquals(1, docUnitWrappers.size());
        final DocUnit docUnit = docUnitWrappers.get(0).getDocUnit();

        assertSame(mapping.getLibrary(), docUnit.getLibrary());
        assertEquals(1, docUnit.getRecords().size());
        final BibliographicRecord bibRecord = docUnit.getRecords().iterator().next();
        assertEquals(1, bibRecord.getProperties().size());

        final Iterator<DocProperty> iterator = bibRecord.getProperties().iterator();
        DocProperty property = iterator.next();
        assertEquals("Benson test 3", property.getValue());
    }

    // Test expression et condition sur le même champ
    @Test
    public void testConditions1() {
        // Notice
        Record record = MARC_FACTORY.newRecord();
        final DataField fld702a = MARC_FACTORY.newDataField("702", ' ', ' ');
        fld702a.addSubfield(MARC_FACTORY.newSubfield('a', "Travolta"));
        fld702a.addSubfield(MARC_FACTORY.newSubfield('b', "John"));
        fld702a.addSubfield(MARC_FACTORY.newSubfield('4', "160"));
        record.addVariableField(fld702a);
        final DataField fld702b = MARC_FACTORY.newDataField("702", ' ', ' ');
        fld702b.addSubfield(MARC_FACTORY.newSubfield('a', "Monroe"));
        fld702b.addSubfield(MARC_FACTORY.newSubfield('b', "Marylin"));
        fld702b.addSubfield(MARC_FACTORY.newSubfield('4', "450"));
        record.addVariableField(fld702b);
        final DataField fld702c = MARC_FACTORY.newDataField("702", ' ', ' ');
        fld702c.addSubfield(MARC_FACTORY.newSubfield('a', "Jarre"));
        fld702c.addSubfield(MARC_FACTORY.newSubfield('b', "Jean-Michel"));
        record.addVariableField(fld702c);

        // Mapping
        final Mapping mapping = getMapping();
        final MappingRule rule = mapping.getRules().get(0);
        rule.setCondition("\\702$4 != '160'");
        rule.setExpression("\\702$a.toUpperCase() + ', ' + \\702$b.toLowerCase()");

        // Compilation du mapping
        final CompiledMapping compiledMapping = mappingEvaluationService.compileMapping(mapping, null);

        // Test
        final List<DocUnitWrapper> docUnitWrappers = service.convert(record, null, compiledMapping, null, getPropertyTypes());

        assertEquals(1, docUnitWrappers.size());
        final DocUnit docUnit = docUnitWrappers.get(0).getDocUnit();

        assertSame(mapping.getLibrary(), docUnit.getLibrary());
        assertEquals(1, docUnit.getRecords().size());
        final BibliographicRecord bibRecord = docUnit.getRecords().iterator().next();
        assertEquals(2, bibRecord.getProperties().size());

        final Iterator<DocProperty> iterator = bibRecord.getProperties().iterator();
        DocProperty property = iterator.next();
        assertEquals("MONROE, marylin", property.getValue());
        property = iterator.next();
        assertEquals("JARRE, jean-michel", property.getValue());
    }

    // Test expression et condition sur des champs différents
    @Test
    public void testConditions2() {
        // Notice
        Record record = MARC_FACTORY.newRecord();
        final DataField fld702a = MARC_FACTORY.newDataField("702", ' ', ' ');
        fld702a.addSubfield(MARC_FACTORY.newSubfield('a', "[Deray]"));
        fld702a.addSubfield(MARC_FACTORY.newSubfield('b', "Odile"));
        fld702a.addSubfield(MARC_FACTORY.newSubfield('4', "160"));
        record.addVariableField(fld702a);
        final DataField fld702b = MARC_FACTORY.newDataField("702", ' ', ' ');
        fld702b.addSubfield(MARC_FACTORY.newSubfield('a', "Gravier"));
        fld702b.addSubfield(MARC_FACTORY.newSubfield('b', "Émile"));
        fld702b.addSubfield(MARC_FACTORY.newSubfield('4', "450"));
        record.addVariableField(fld702b);
        final DataField fld620a = MARC_FACTORY.newDataField("620", ' ', ' ');
        fld620a.addSubfield(MARC_FACTORY.newSubfield('d', "petit pois"));
        record.addVariableField(fld620a);
        final DataField fld620b = MARC_FACTORY.newDataField("620", ' ', ' ');
        fld620b.addSubfield(MARC_FACTORY.newSubfield('d', "carotte"));
        record.addVariableField(fld620b);
        final DataField fld620c = MARC_FACTORY.newDataField("620", ' ', ' ');
        fld620c.addSubfield(MARC_FACTORY.newSubfield('d', "pomme de terre"));
        record.addVariableField(fld620c);

        // Mapping
        final Mapping mapping = getMapping();
        final MappingRule rule = mapping.getRules().get(0);
        rule.setCondition("\\620$d != 'carotte'");
        rule.setExpression("\\702$a.toUpperCase().replaceAll('[\\\\[\\\\]]', '') + ', ' + \\702$b.toLowerCase()");

        // Compilation du mapping
        final CompiledMapping compiledMapping = mappingEvaluationService.compileMapping(mapping, null);

        // Test
        final List<DocUnitWrapper> docUnitWrappers = service.convert(record, null, compiledMapping, null, getPropertyTypes());

        assertEquals(1, docUnitWrappers.size());
        final DocUnit docUnit = docUnitWrappers.get(0).getDocUnit();

        assertSame(mapping.getLibrary(), docUnit.getLibrary());
        assertEquals(1, docUnit.getRecords().size());
        final BibliographicRecord bibRecord = docUnit.getRecords().iterator().next();
        assertEquals(2, bibRecord.getProperties().size());

        final Iterator<DocProperty> iterator = bibRecord.getProperties().iterator();
        DocProperty property = iterator.next();
        assertEquals("DERAY, odile", property.getValue());
        property = iterator.next();
        assertEquals("GRAVIER, émile", property.getValue());
    }

    @Test
    public void testExpression170831() {
        // Notice
        Record record = MARC_FACTORY.newRecord();
        final DataField f966 = MARC_FACTORY.newDataField("966", ' ', ' ');
        f966.addSubfield(MARC_FACTORY.newSubfield('a', "0001013848"));
        record.addVariableField(f966);
        final DataField f995_1 = MARC_FACTORY.newDataField("995", ' ', ' ');
        f995_1.addSubfield(MARC_FACTORY.newSubfield('k', "12°020.082"));
        record.addVariableField(f995_1);
        final DataField f995_2 = MARC_FACTORY.newDataField("995", ' ', ' ');
        f995_2.addSubfield(MARC_FACTORY.newSubfield('k', "FLM.12°0289"));
        record.addVariableField(f995_2);

        // Mapping
        final Mapping mapping = getMapping();
        final MappingRule rule = mapping.getRules().get(0);
        rule.setExpression("\"NEW_sc_${\\966$a}_$\\995$k\"");

        // Compilation du mapping
        final CompiledMapping compiledMapping = mappingEvaluationService.compileMapping(mapping, null);

        // Test
        final List<DocUnitWrapper> docUnitWrappers = service.convert(record, null, compiledMapping, null, getPropertyTypes());

        assertEquals(1, docUnitWrappers.size());
        final DocUnit docUnit = docUnitWrappers.get(0).getDocUnit();

        assertSame(mapping.getLibrary(), docUnit.getLibrary());
        assertEquals(1, docUnit.getRecords().size());
        final BibliographicRecord bibRecord = docUnit.getRecords().iterator().next();
        assertEquals(2, bibRecord.getProperties().size());

        final Iterator<DocProperty> iterator = bibRecord.getProperties().iterator();
        DocProperty property = iterator.next();
        assertEquals("NEW_sc_0001013848_12°020.082", property.getValue());
        property = iterator.next();
        assertEquals("NEW_sc_0001013848_FLM.12°0289", property.getValue());
    }

    @Test
    public void testExpression180319_1() {
        // Notice
        Record record = MARC_FACTORY.newRecord();
        final DataField f200 = MARC_FACTORY.newDataField("200", ' ', ' ');
        f200.addSubfield(MARC_FACTORY.newSubfield('a', "test champ absent"));
        record.addVariableField(f200);

        // Mapping
        final Mapping mapping = getMapping();
        final MappingRule rule = mapping.getRules().get(0);
        rule.setExpression("\"A_${\\181$c} B_$\\200$a\"");  // champ absent + champ présent

        // Compilation du mapping
        final CompiledMapping compiledMapping = mappingEvaluationService.compileMapping(mapping, null);

        // Test
        final List<DocUnitWrapper> docUnitWrappers = service.convert(record, null, compiledMapping, null, getPropertyTypes());

        assertEquals(1, docUnitWrappers.size());
        final DocUnit docUnit = docUnitWrappers.get(0).getDocUnit();

        assertSame(mapping.getLibrary(), docUnit.getLibrary());
        assertEquals(1, docUnit.getRecords().size());
        final BibliographicRecord bibRecord = docUnit.getRecords().iterator().next();
        assertEquals(1, bibRecord.getProperties().size());

        final Iterator<DocProperty> iterator = bibRecord.getProperties().iterator();
        DocProperty property = iterator.next();
        assertEquals("A_ B_test champ absent", property.getValue());
    }

    @Test
    public void testExpression180319_2() {
        // Notice
        Record record = MARC_FACTORY.newRecord();
        final DataField f200 = MARC_FACTORY.newDataField("200", ' ', ' ');
        f200.addSubfield(MARC_FACTORY.newSubfield('a', "test champ absent"));
        record.addVariableField(f200);

        // Mapping
        final Mapping mapping = getMapping();
        final MappingRule rule = mapping.getRules().get(0);
        rule.setExpression("'image'");
        rule.setCondition("\\181$c == 'image fixe'");   // champ absent uniquement

        // Compilation du mapping
        final CompiledMapping compiledMapping = mappingEvaluationService.compileMapping(mapping, null);

        // Test
        final List<DocUnitWrapper> docUnitWrappers = service.convert(record, null, compiledMapping, null, getPropertyTypes());

        assertEquals(1, docUnitWrappers.size());
        final DocUnit docUnit = docUnitWrappers.get(0).getDocUnit();

        assertSame(mapping.getLibrary(), docUnit.getLibrary());
        assertEquals(1, docUnit.getRecords().size());

        final BibliographicRecord bibRecord = docUnit.getRecords().iterator().next();
        assertTrue(bibRecord.getProperties().isEmpty());
    }

    private Mapping getMapping() {
        final Mapping mapping = new Mapping();
        mapping.setLabel("Test");
        mapping.setLibrary(new Library());
        mapping.setType(Mapping.Type.MARC);
        final MappingRule rule = new MappingRule();
        rule.setPosition(1000);
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
}
