package fr.progilone.pgcn.service.exchange.ead;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import fr.progilone.pgcn.config.ScriptEngineConfiguration;
import fr.progilone.pgcn.domain.exchange.Mapping;
import fr.progilone.pgcn.domain.exchange.MappingRule;
import fr.progilone.pgcn.domain.jaxb.ead.Unitid;
import fr.progilone.pgcn.service.administration.TransliterationService;
import fr.progilone.pgcn.service.exchange.ead.mapping.CompiledMapping;
import java.util.HashMap;
import java.util.Map;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Created by Sébastien on 17/05/2017.
 */
@ExtendWith(MockitoExtension.class)
public class EadMappingEvaluationServiceTest {

    // on ne mock pas le moteur de script, on utilise sa configuration réelle dans l'application
    private static final ScriptEngine SCRIPT_ENGINE = new ScriptEngineConfiguration().getGroovyScriptEngine();
    // private static final ScriptEngine SCRIPT_ENGINE = new GroovyScriptEngineImpl();

    @Mock
    private TransliterationService transliterationService;
    private EadMappingEvaluationService service;

    @BeforeEach
    public void setUp() {
        service = new EadMappingEvaluationService(SCRIPT_ENGINE, transliterationService);
    }

    @Test
    public void testEvalExpression0() {
        final String expression = "\\did.unitid.content";
        final CompiledMapping compiledMapping = getCompiledMapping(expression, null);
        assertEquals(1, compiledMapping.getCompiledRules().size());

        final Map<String, Object> bindings = new HashMap<>();
        bindings.put("var_did_unitid_content", "Hello you !");

        final Object actual = service.evalExpression(compiledMapping.getCompiledRules().get(0), bindings);
        assertEquals("Hello you !", String.valueOf(actual));
    }

    @Test
    public void testEvalExpression1() {
        final String expression = "\\{did.unitid.content}.substring(6, 9)";
        final CompiledMapping compiledMapping = getCompiledMapping(expression, null);
        assertEquals(1, compiledMapping.getCompiledRules().size());

        final Map<String, Object> bindings = new HashMap<>();
        bindings.put("var_did_unitid_content", "Hello you !");

        final Object actual = service.evalExpression(compiledMapping.getCompiledRules().get(0), bindings);
        assertEquals("you", String.valueOf(actual));
    }

    @Test
    public void testEvalExpression2() {
        final String expression = "\\all:did.unitid.content";
        final CompiledMapping compiledMapping = getCompiledMapping(expression, null);
        assertEquals(1, compiledMapping.getCompiledRules().size());

        final Map<String, Object> bindings = new HashMap<>();
        bindings.put("var_all_did_unitid_content", "Hello you !");

        final Object actual = service.evalExpression(compiledMapping.getCompiledRules().get(0), bindings);
        assertEquals("Hello you !", String.valueOf(actual));
    }

    @Test
    public void testEvalCondition0() {
        final String condition = "\\did.unitid.content == \"Hello you !\"";
        final CompiledMapping compiledMapping = getCompiledMapping(null, condition);
        assertEquals(1, compiledMapping.getCompiledRules().size());

        final Map<String, Object> bindings = new HashMap<>();
        bindings.put("var_did_unitid_content", "Hello you !");

        final boolean actual = service.evalCondition(compiledMapping.getCompiledRules().get(0), bindings);
        assertTrue(actual);
    }

    @Test
    public void testEvalCondition1() {
        final String condition = "\\{did.unitid.content}.equals(\"Hello you !\")";
        final CompiledMapping compiledMapping = getCompiledMapping(null, condition);
        assertEquals(1, compiledMapping.getCompiledRules().size());

        final Map<String, Object> bindings = new HashMap<>();
        bindings.put("var_did_unitid_content", "Hello you !");

        final boolean actual = service.evalCondition(compiledMapping.getCompiledRules().get(0), bindings);
        assertTrue(actual);
    }

    @Test
    public void testSecurity() throws ScriptException {
        final String dangerousExpression = "new File('.').listFiles()";

        final CompiledMapping compiledMapping = getCompiledMapping(dangerousExpression, null);
        assertEquals(1, compiledMapping.getCompiledRules().size());

        final Object actual = service.evalExpression(compiledMapping.getCompiledRules().get(0), new HashMap<>());
        assertNull(actual); // l'expression a lancé une exception et n'a pas été évaluée
    }

    @Test
    public void test2165() {
        final String expression = "\"BSG_\" + text(\\did.unitid).toUpperCase().replaceAll('[.)\\\\s]', '').replaceAll('[(]','_')";

        final CompiledMapping compiledMapping = getCompiledMapping(expression, null);
        assertEquals(1, compiledMapping.getCompiledRules().size());

        final Unitid unitid = new Unitid();
        unitid.getContent().add("Ms. 1086");
        final Map<String, Object> bindings = new HashMap<>();
        bindings.put("var_did_unitid", unitid);

        final Object actual = service.evalExpression(compiledMapping.getCompiledRules().get(0), bindings);
        assertEquals("BSG_MS1086", String.valueOf(actual));
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

    private CompiledMapping getCompiledMapping(String expression, String condition) {
        final MappingRule rule = new MappingRule();
        rule.setExpression(expression);
        rule.setCondition(condition);

        Mapping mapping = new Mapping();
        mapping.addRule(rule);

        return service.compileMapping(mapping);
    }
}
