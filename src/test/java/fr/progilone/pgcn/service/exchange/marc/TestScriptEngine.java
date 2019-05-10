package fr.progilone.pgcn.service.exchange.marc;

import groovy.lang.GroovyShell;
import groovy.util.Eval;
import org.codehaus.groovy.jsr223.GroovyScriptEngineFactory;
import org.junit.Ignore;
import org.junit.Test;

import javax.script.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

/**
 * Created by Sebastien on 23/11/2016.
 */
@Ignore
public class TestScriptEngine {

    private static final int NB_WARM_UP = 10;
    private static final int NB_ITERATIONS = 10000;

    /*
     Résultats pour 1000

     # Java
     durée exécution: 26

     # Javascript (avec compilation)
     durée compilation: 300
     durée exécution: 4156

     # Groovy (sans compilation)
     durée exécution: 111

     # Groovy (avec compilation)
     durée compilation: 18
     durée exécution: 106

     # Groovy (GroovyScriptEngineFactory, avec compilation)
     durée compilation: 31
     durée exécution: 190

     # Groovy (eval)
     durée exécution: 12993

     # Groovy (shell)
     durée exécution: 8135
     */

    @Test
    public void testJava() {
        System.out.println("# Java");

        final IntConsumer testFn = i -> {
            String first = "HELLO";
            String second = "world";
            final String result = first.toLowerCase() + ' ' + second.toUpperCase();
            assertEquals("hello WORLD", result);
        };

        IntStream.range(1, NB_WARM_UP).forEach(testFn);
        LocalDateTime start = LocalDateTime.now();

        IntStream.range(1, NB_ITERATIONS).forEach(testFn);
        System.out.println("durée exécution: " + ChronoUnit.MILLIS.between(start, LocalDateTime.now()) + "\n");
    }

    @Ignore
    @Test
    public void testJs() throws ScriptException {
        System.out.println("# Javascript (avec compilation)");
        final ScriptEngineManager factory = new ScriptEngineManager();
        final ScriptEngine engine = factory.getEngineByName("JavaScript");

        LocalDateTime start = LocalDateTime.now();
        final CompiledScript script = ((Compilable) engine).compile("first.toLowerCase() + ' ' + second.toUpperCase()");
        System.out.println("durée compilation: " + ChronoUnit.MILLIS.between(start, LocalDateTime.now()));

        final IntConsumer testFn = i -> {
            try {
                final Bindings bindings = engine.createBindings();
                bindings.put("first", "HELLO");
                bindings.put("second", "world");
                final String result = (String) script.eval(bindings);
                assertEquals("hello WORLD", result);

            } catch (ScriptException e) {
                e.printStackTrace();
            }
        };

        IntStream.range(1, NB_WARM_UP).forEach(testFn);
        start = LocalDateTime.now();

        IntStream.range(1, NB_ITERATIONS).forEach(testFn);
        System.out.println("durée exécution: " + ChronoUnit.MILLIS.between(start, LocalDateTime.now()) + "\n");
    }

    @Test
    public void testGroovy1() throws ScriptException {
        System.out.println("# Groovy (sans compilation)");
        final ScriptEngineManager factory = new ScriptEngineManager();
        final ScriptEngine engine = factory.getEngineByName("groovy");

        final IntConsumer testFn = i -> {
            try {
                engine.put("first", "HELLO");
                engine.put("second", "world");
                String result = (String) engine.eval("first.toLowerCase() + ' ' + second.toUpperCase()");
                assertEquals("hello WORLD", result);

            } catch (ScriptException e) {
                e.printStackTrace();
            }
        };

        IntStream.range(1, NB_WARM_UP).forEach(testFn);
        LocalDateTime start = LocalDateTime.now();

        IntStream.range(1, NB_ITERATIONS).forEach(testFn);
        System.out.println("durée exécution: " + ChronoUnit.MILLIS.between(start, LocalDateTime.now()) + "\n");
    }

    @Test
    public void testGroovy2() throws ScriptException {
        System.out.println("# Groovy (avec compilation)");
        final ScriptEngineManager factory = new ScriptEngineManager();
        final ScriptEngine engine = factory.getEngineByName("groovy");

        LocalDateTime start = LocalDateTime.now();
        CompiledScript script = ((Compilable) engine).compile("first.toLowerCase() + ' ' + second.toUpperCase()");
        System.out.println("durée compilation: " + ChronoUnit.MILLIS.between(start, LocalDateTime.now()));

        final Bindings bindings = engine.createBindings();
        bindings.put("first", "HELLO");
        bindings.put("second", "world");

        final IntConsumer testFn = i -> {
            try {
                final String result = (String) script.eval(bindings);
                assertEquals("hello WORLD", result);

            } catch (ScriptException e) {
                e.printStackTrace();
            }
        };

        IntStream.range(1, NB_WARM_UP).forEach(testFn);
        start = LocalDateTime.now();

        IntStream.range(1, NB_ITERATIONS).forEach(testFn);
        System.out.println("durée exécution: " + ChronoUnit.MILLIS.between(start, LocalDateTime.now()) + "\n");
    }

    @Test
    public void testGroovy30() throws ScriptException {
        System.out.println("# Groovy (GroovyScriptEngineFactory, sans compilation)");
        final ScriptEngine engine = new GroovyScriptEngineFactory().getScriptEngine();  // identique à testGroovy2

        final IntConsumer testFn = i -> {
            try {
                engine.put("first", "HELLO");
                engine.put("second", "world");
                final String result = (String) engine.eval("first.toLowerCase() + ' ' + second.toUpperCase()");
                assertEquals("hello WORLD", result);

            } catch (ScriptException e) {
                e.printStackTrace();
            }
        };

        IntStream.range(1, NB_WARM_UP).forEach(testFn);
        LocalDateTime start = LocalDateTime.now();

        IntStream.range(1, NB_ITERATIONS).forEach(testFn);
        System.out.println("durée exécution: " + ChronoUnit.MILLIS.between(start, LocalDateTime.now()) + "\n");
    }

    @Test
    public void testGroovy31() throws ScriptException {
        System.out.println("# Groovy (GroovyScriptEngineFactory, avec compilation)");
        final ScriptEngine engine = new GroovyScriptEngineFactory().getScriptEngine();  // identique à testGroovy2

        LocalDateTime start = LocalDateTime.now();
        CompiledScript script = ((Compilable) engine).compile("first.toLowerCase() + ' ' + second.toUpperCase()");
        System.out.println("durée compilation: " + ChronoUnit.MILLIS.between(start, LocalDateTime.now()));

        final Bindings bindings = engine.createBindings();
        bindings.put("first", "HELLO");
        bindings.put("second", "world");

        final IntConsumer testFn = i -> {
            try {
                final String result = (String) script.eval(bindings);
                assertEquals("hello WORLD", result);

            } catch (ScriptException e) {
                e.printStackTrace();
            }
        };

        IntStream.range(1, NB_WARM_UP).forEach(testFn);
        start = LocalDateTime.now();

        IntStream.range(1, NB_ITERATIONS).forEach(testFn);
        System.out.println("durée exécution: " + ChronoUnit.MILLIS.between(start, LocalDateTime.now()) + "\n");
    }

    @Ignore
    @Test
    public void testGroovy4() throws ScriptException {
        System.out.println("# Groovy (eval)");
        final IntConsumer testFn = i -> {
            final String result = (String) Eval.xy("HELLO", "world", "x.toLowerCase() + ' ' + y.toUpperCase()");
            assertEquals("hello WORLD", result);
        };

        IntStream.range(1, NB_WARM_UP).forEach(testFn);
        LocalDateTime start = LocalDateTime.now();

        IntStream.range(1, NB_ITERATIONS).forEach(testFn);
        System.out.println("durée exécution: " + ChronoUnit.MILLIS.between(start, LocalDateTime.now()) + "\n");
    }

    @Ignore
    @Test
    public void testGroovy5() throws ScriptException {
        System.out.println("# Groovy (shell)");
        final GroovyShell shell = new GroovyShell();

        final IntConsumer testFn = i -> {
            shell.setProperty("first", "HELLO");
            shell.setProperty("second", "world");
            final String result = (String) shell.evaluate("first.toLowerCase() + ' ' + second.toUpperCase()");
            assertEquals("hello WORLD", result);
        };

        IntStream.range(1, NB_WARM_UP).forEach(testFn);
        LocalDateTime start = LocalDateTime.now();

        IntStream.range(1, NB_ITERATIONS).forEach(testFn);
        System.out.println("durée exécution: " + ChronoUnit.MILLIS.between(start, LocalDateTime.now()) + "\n");
    }
}
