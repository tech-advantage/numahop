package fr.progilone.pgcn.config;

import groovy.lang.GroovyClassLoader;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;
import org.codehaus.groovy.jsr223.GroovyScriptEngineImpl;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Leader;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.VariableField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.script.ScriptEngine;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Sebastien on 23/11/2016.
 */
@Configuration
public class ScriptEngineConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(MetricsConfiguration.class);

    @Bean(name = "groovy")
    public ScriptEngine getGroovyScriptEngine() {
        LOG.info("Initialisation du moteur de script...");
        // version pas sécurisée
        //        final ScriptEngineManager factory = new ScriptEngineManager();
        //        return factory.getEngineByName("groovy");

        // version sécurisée (filtrage par listes blanches)
        final SecureASTCustomizer secure = new SecureASTCustomizer();
        secure.setPackageAllowed(false);
        secure.setMethodDefinitionAllowed(false);
        secure.setClosuresAllowed(true);
        // imports
        secure.setImportsWhitelist(Collections.emptyList());
        // static imports
        secure.setStaticImportsWhitelist(Collections.emptyList());
        // star imports
        secure.setStarImportsWhitelist(Collections.singletonList("org.marc4j.marc.*"));
        // static star imports
        secure.setStaticStarImportsWhitelist(Collections.emptyList());
        // tokens
        // secure.setTokensWhitelist(Arrays.asList(Types.PLUS));
        // constant types
        secure.setConstantTypesClassesWhiteList(Arrays.asList(Boolean.TYPE, Character.TYPE, Integer.TYPE, char[].class, int[].class,
                                                              Boolean.class, Character.class, Object.class, String.class, String[].class,
                                                              // Classes marc4j
                                                              ControlField.class, DataField.class, Leader.class,
                                                              Record.class, Subfield.class, VariableField.class));
        // receivers
        secure.setReceiversClassesWhiteList(Arrays.asList(Object.class, List.class, String.class));

        final CompilerConfiguration config = new CompilerConfiguration();
        config.addCompilationCustomizers(new ImportCustomizer(), secure);

        GroovyClassLoader classLoader = new GroovyClassLoader(Thread.currentThread().getContextClassLoader(), config);
        return new GroovyScriptEngineImpl(classLoader);
    }
}
