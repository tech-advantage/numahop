package fr.progilone.pgcn.service.exchange.marc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import fr.progilone.pgcn.config.ScriptEngineConfiguration;
import fr.progilone.pgcn.domain.exchange.Mapping;
import fr.progilone.pgcn.domain.exchange.MappingRule;
import fr.progilone.pgcn.service.administration.TransliterationService;
import fr.progilone.pgcn.service.exchange.marc.mapping.CompiledMapping;
import java.util.HashMap;
import java.util.Map;
import javax.script.ScriptEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Created by Sebastien on 23/11/2016.
 */
@ExtendWith(MockitoExtension.class)
public class MarcMappingEvaluationServiceTest {

    private static final MarcFactory MARC_FACTORY = MarcFactory.newInstance();
    // on ne mock pas le moteur de script, on utilise sa configuration réelle dans l'application
    private static final ScriptEngine SCRIPT_ENGINE = new ScriptEngineConfiguration().getGroovyScriptEngine();
    // private static final ScriptEngine SCRIPT_ENGINE = new GroovyScriptEngineImpl();

    @Mock
    private TransliterationService transliterationService;

    private MarcMappingEvaluationService service;

    @BeforeEach
    public void setUp() {
        service = new MarcMappingEvaluationService(SCRIPT_ENGINE, transliterationService);
    }

    // Exécution d'un script groovy
    @Test
    public void testExprString() {
        final String expression = "rec702_4.toLowerCase() + ', ' + (rec702_c.length() >= 4 ? rec702_c.substring(2, 4) : 'null')";
        final CompiledMapping compiledMapping = getCompiledMapping(expression, null);
        assertEquals(1, compiledMapping.getCompiledRules().size());

        final Map<String, Object> bindings = new HashMap<>();
        bindings.put("rec702_4", "NEW-YORK");
        bindings.put("rec702_c", "0123");

        final Object actual = service.evalExpression(compiledMapping.getCompiledRules().get(0), bindings);

        assertEquals("new-york, 23", actual);
    }

    // Exécution d'un script groovy
    @Test
    public void testExprRegex() {
        final String expression = "def test = (marc_327_a =~ /^((?:\\d{4})?(?:.*?\\])?)/)\n"
                                  + "marc_945_b + (test.find() && !test.group().isEmpty() ? \" - \" + test.group() : \"\")\n";
        final CompiledMapping compiledMapping = getCompiledMapping(expression, null);
        assertEquals(1, compiledMapping.getCompiledRules().size());

        final Map<String, Object> bindings = new HashMap<>();
        bindings.put("marc_327_a", "1958. [1] Le programme national du PCF - Février 1958 (4 p.)");
        bindings.put("marc_945_b", "COL4°2022(1955-1960)");

        final Object actual = service.evalExpression(compiledMapping.getCompiledRules().get(0), bindings);

        assertEquals("COL4°2022(1955-1960) - 1958. [1]", actual);
    }

    // Évaluation d'une expression utilisant des GString: ${maString}
    @Test
    public void testExprGString() {
        final String expression = "\"${\\606$a} - ${\\606$z}\"";
        final CompiledMapping compiledMapping = getCompiledMapping(expression, null);
        assertEquals(1, compiledMapping.getCompiledRules().size());

        final Map<String, Object> bindings = new HashMap<>();
        bindings.put("marc_606_a", "Les Arts bibliographiques");
        bindings.put("marc_606_z", "1995");
        final Object actual = service.evalExpression(compiledMapping.getCompiledRules().get(0), bindings);

        assertEquals("Les Arts bibliographiques - 1995", String.valueOf(actual));
    }

    // Évaluation d'une expression utilisant des GString: $maString
    @Test
    public void testExprGString2() {
        final String expression = "\"$\\606$a - $\\606$z\"";
        final CompiledMapping compiledMapping = getCompiledMapping(expression, null);
        assertEquals(1, compiledMapping.getCompiledRules().size());

        final Map<String, Object> bindings = new HashMap<>();
        bindings.put("marc_606_a", "Les Arts bibliographiques");
        bindings.put("marc_606_z", "1995");
        final Object actual = service.evalExpression(compiledMapping.getCompiledRules().get(0), bindings);

        assertEquals("Les Arts bibliographiques - 1995", String.valueOf(actual));
    }

    // Évaluation d'une expression portant sur un champ xx
    @Test
    public void testExprXX() {
        final String expression = "[\\6xx$a, \\6xx$y, \\6xx$z].grep().join(' - ')";
        final CompiledMapping compiledMapping = getCompiledMapping(expression, null);
        assertEquals(1, compiledMapping.getCompiledRules().size());

        final Map<String, Object> bindings = new HashMap<>();
        bindings.put("marc_6xx_a", "Les Arts bibliographiques");
        bindings.put("marc_6xx_y", "");
        bindings.put("marc_6xx_z", "1995");
        final Object actual = service.evalExpression(compiledMapping.getCompiledRules().get(0), bindings);

        assertEquals("Les Arts bibliographiques - 1995", actual);
    }

    // Appel de code java à partir d'un script groovy
    // Désactivé car non autorisé avec les règles de sécurité du moteur de script
    @Disabled
    @Test
    public void testExprJavaClass() {
        final DataField fld702 = MARC_FACTORY.newDataField("702", ' ', ' ');
        fld702.addSubfield(MARC_FACTORY.newSubfield('a', "Benson"));
        fld702.addSubfield(MARC_FACTORY.newSubfield('b', "Rowland S."));
        final String expression = "new fr.progilone.pgcn.service.exchange.marc.formatter.PersonFieldFormatter("
                                  + "(char)'a', (char)'b', (char)'c', (char)'d', (char)'f', (char)'D', (char)'4')"
                                  + ".format(field)";
        final CompiledMapping compiledMapping = getCompiledMapping(expression, null);
        assertEquals(1, compiledMapping.getCompiledRules().size());

        final Map<String, Object> bindings = new HashMap<>();
        bindings.put("field", fld702);
        final Object actual = service.evalExpression(compiledMapping.getCompiledRules().get(0), bindings);

        assertEquals("Benson, Rowland S.", actual);
    }

    @Test
    public void testExprGroovy() {
        final DataField fld200 = MARC_FACTORY.newDataField("200", ' ', ' ');
        fld200.addSubfield(MARC_FACTORY.newSubfield('a', "Les Arts bibliographiques"));
        fld200.addSubfield(MARC_FACTORY.newSubfield('b', "Texte imprimé"));
        fld200.addSubfield(MARC_FACTORY.newSubfield('e', "l'oeuvre et l'image"));

        final String expression = "subfields(\\200)";
        final String condition = "exists(record, '200')";

        final CompiledMapping compiledMapping = getCompiledMapping(expression, condition);
        assertEquals(1, compiledMapping.getCompiledRules().size());

        final Map<String, Object> bindings = new HashMap<>();
        bindings.put("marc_200", fld200);
        final Object actual = service.evalExpression(compiledMapping.getCompiledRules().get(0), bindings);

        assertEquals("Les Arts bibliographiques Texte imprimé l'oeuvre et l'image", actual);
    }

    @Test
    public void testEvalCondition() {
        // 1: plantage à la compilation de la condition
        CompiledMapping compiledMapping = getCompiledMapping(null, "invalid..$404 condition");
        assertEquals(1, compiledMapping.getCompiledRules().size());

        boolean actual = service.evalCondition(compiledMapping.getCompiledRules().get(0), new HashMap<>());
        assertFalse(actual);

        // 2: pas de condition
        compiledMapping = getCompiledMapping(null, null);
        assertEquals(1, compiledMapping.getCompiledRules().size());

        actual = service.evalCondition(compiledMapping.getCompiledRules().get(0), new HashMap<>());
        assertTrue(actual);

        // 3: condition = null
        compiledMapping = getCompiledMapping(null, "null");
        assertEquals(1, compiledMapping.getCompiledRules().size());

        actual = service.evalCondition(compiledMapping.getCompiledRules().get(0), new HashMap<>());
        assertFalse(actual);

        // 4: condition = boolean
        compiledMapping = getCompiledMapping(null, "1 == 1");
        assertEquals(1, compiledMapping.getCompiledRules().size());

        actual = service.evalCondition(compiledMapping.getCompiledRules().get(0), new HashMap<>());
        assertTrue(actual);

        // 5: condition = objet
        compiledMapping = getCompiledMapping(null, "new String('toto fait du vélo')");
        assertEquals(1, compiledMapping.getCompiledRules().size());

        actual = service.evalCondition(compiledMapping.getCompiledRules().get(0), new HashMap<>());
        assertTrue(actual);

        // 6: condition = string
        compiledMapping = getCompiledMapping(null, "\\200$7");
        assertEquals(1, compiledMapping.getCompiledRules().size());

        final Map<String, Object> bindings = new HashMap<>();
        bindings.put("marc_200_7", "");
        actual = service.evalCondition(compiledMapping.getCompiledRules().get(0), bindings);
        System.out.println(actual);
        assertFalse(actual);

        // 999: plantage à l'évaluation de la condition
        compiledMapping = getCompiledMapping(null, "'toto'.substring(14)");
        assertEquals(1, compiledMapping.getCompiledRules().size());

        actual = service.evalCondition(compiledMapping.getCompiledRules().get(0), new HashMap<>());
        assertFalse(actual);
    }

    @Test
    public void testSecurity() {
        final String dangerousExpression = "new File('.').listFiles()";

        final CompiledMapping compiledMapping = getCompiledMapping(dangerousExpression, null);
        assertEquals(1, compiledMapping.getCompiledRules().size());

        final Object actual = service.evalExpression(compiledMapping.getCompiledRules().get(0), new HashMap<>());
        assertNull(actual); // l'expression a lancé une exception et n'a pas été évaluée
    }

    @Test
    public void testTransliterationInCondition() {
        final String condition = "'cornichon'.equals(transliterate.getFunction('test'))";

        final CompiledMapping compiledMapping = getCompiledMapping(null, condition);
        assertEquals(1, compiledMapping.getCompiledRules().size());

        when(transliterationService.getFunction("test")).thenReturn("cornichon");

        final boolean actual = service.evalCondition(compiledMapping.getCompiledRules().get(0), new HashMap<>());
        assertTrue(actual);
    }

    @Test
    public void testTransliterationInExpression() {
        final String expression = "transliterate.getFunction('070')";

        final CompiledMapping compiledMapping = getCompiledMapping(expression, null);
        assertEquals(1, compiledMapping.getCompiledRules().size());

        when(transliterationService.getFunction("070")).thenReturn("Auteur");

        final Object actual = service.evalExpression(compiledMapping.getCompiledRules().get(0), new HashMap<>());
        assertEquals("Auteur", actual);
    }

    @Test
    public void testMultipleExpressions() {
        final DataField fld605 = MARC_FACTORY.newDataField("605", ' ', ' ');
        fld605.addSubfield(MARC_FACTORY.newSubfield('a', "Les Arts bibliographiques"));
        fld605.addSubfield(MARC_FACTORY.newSubfield('b', "Texte imprimé"));
        fld605.addSubfield(MARC_FACTORY.newSubfield('e', "l'oeuvre et l'image"));
        fld605.addSubfield(MARC_FACTORY.newSubfield('x', "Cornichon"));
        fld605.addSubfield(MARC_FACTORY.newSubfield('x', "Patate"));
        fld605.addSubfield(MARC_FACTORY.newSubfield('y', "Tartiflette"));

        final String expression = "[subfields(\\605), concatWithSep(\\605)].join(\" \")";
        final String expressionConf = "subfieldsAdd('a')\n" + "subfieldsAdd('i', '. ')\n"
                                      + "subfieldsAddGroup(' (', ')', ' ; ', 'n')\n"
                                      + "concatWithSepCodes('x', 'y', 'z')\n"
                                      + "concatWithSepInner(', ')\n"
                                      + "concatWithSepOuter(' // ')\n";

        final CompiledMapping compiledMapping = getCompiledMappingExpression(expression, expressionConf);
        assertEquals(1, compiledMapping.getCompiledRules().size());

        final Map<String, Object> bindings = new HashMap<>();
        bindings.put("marc_605", fld605);
        final Object actual = service.evalExpression(compiledMapping.getCompiledRules().get(0), bindings);

        assertEquals("Les Arts bibliographiques Cornichon, Patate // Tartiflette", actual);
    }

    @Test
    public void testMultipleExpressions2() {
        final DataField fld605 = MARC_FACTORY.newDataField("605", ' ', ' ');
        fld605.addSubfield(MARC_FACTORY.newSubfield('a', "Les Arts bibliographiques"));

        final String expression = "[subfields(\\6xx), concatWithSep(\\6xx)].join(\" \").trim()";
        final String expressionConf = "subfieldsAdd('a')\n" + "concatWithSepCodes('x', 'y', 'z')\n";

        final CompiledMapping compiledMapping = getCompiledMappingExpression(expression, expressionConf);
        assertEquals(1, compiledMapping.getCompiledRules().size());

        final Map<String, Object> bindings = new HashMap<>();
        bindings.put("marc_6xx", fld605);
        final Object actual = service.evalExpression(compiledMapping.getCompiledRules().get(0), bindings);

        assertEquals("Les Arts bibliographiques", actual);
    }

    @Test
    public void testConditionMissingVar() {
        final String condition = "missing_var == 'test'";
        final CompiledMapping compiledMapping = getCompiledMapping(null, condition);
        assertEquals(1, compiledMapping.getCompiledRules().size());

        final Map<String, Object> bindings = new HashMap<>();
        // bindings.put("missing_var", "test");

        final Object actual = service.evalCondition(compiledMapping.getCompiledRules().get(0), bindings);

        assertFalse((boolean) actual);
    }

    private CompiledMapping getCompiledMapping(final String expression, final String condition) {
        final MappingRule rule = new MappingRule();
        rule.setExpression(expression);
        rule.setCondition(condition);

        final Mapping mapping = new Mapping();
        mapping.addRule(rule);

        return service.compileMapping(mapping, null);
    }

    private CompiledMapping getCompiledMappingExpression(final String expression, final String conf) {
        final MappingRule rule = new MappingRule();
        rule.setExpression(expression);
        rule.setExpressionConf(conf);

        final Mapping mapping = new Mapping();
        mapping.addRule(rule);

        return service.compileMapping(mapping, null);
    }
}
