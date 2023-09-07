package fr.progilone.pgcn.config;

import groovy.lang.GroovyClassLoader;
import java.util.List;
import javax.script.ScriptEngine;
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

/**
 * Created by Sebastien on 23/11/2016.
 */
@Configuration
public class ScriptEngineConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(ScriptEngineConfiguration.class);

    @Bean(name = "groovy")
    public ScriptEngine getGroovyScriptEngine() {
        LOG.info("Initialisation du moteur de script...");
        // version pas sécurisée
        // final ScriptEngineManager factory = new ScriptEngineManager();
        // return factory.getEngineByName("groovy");

        // version sécurisée (filtrage par listes blanches)
        final SecureASTCustomizer secure = new SecureASTCustomizer();
        secure.setPackageAllowed(false);
        secure.setMethodDefinitionAllowed(false);
        secure.setClosuresAllowed(true);
        // imports
        secure.setAllowedImports(List.of());
        // static imports
        secure.setAllowedStaticImports(List.of());
        // star imports
        secure.setAllowedStarImports(List.of("org.marc4j.marc.*"));
        // static star imports
        secure.setAllowedStaticStarImports(List.of());
        // tokens
        // secure.setTokensWhitelist(Arrays.asList(Types.PLUS));
        // constant types
        secure.setAllowedConstantTypesClasses(List.of(Boolean.TYPE,
                                                      Character.TYPE,
                                                      Integer.TYPE,
                                                      char[].class,
                                                      int[].class,
                                                      Boolean.class,
                                                      Character.class,
                                                      Object.class,
                                                      String.class,
                                                      String[].class,
                                                      // Classes marc4j
                                                      ControlField.class,
                                                      DataField.class,
                                                      Leader.class,
                                                      Record.class,
                                                      Subfield.class,
                                                      VariableField.class));
        // receivers
        secure.setAllowedReceiversClasses(List.of(Object.class, List.class, String.class));

        final CompilerConfiguration config = new CompilerConfiguration();
        config.addCompilationCustomizers(new ImportCustomizer(), secure);

        final GroovyClassLoader classLoader = new GroovyClassLoader(Thread.currentThread().getContextClassLoader(), config);
        return new GroovyScriptEngineImpl(classLoader);
    }
}
