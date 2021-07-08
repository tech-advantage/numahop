package fr.progilone.pgcn.service.exchange.ead.script;

import fr.progilone.pgcn.config.ScriptEngineConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static fr.progilone.pgcn.service.exchange.ead.EadMappingEvaluationService.*;

/**
 * Created by Sebastien on 06/12/2016.
 */
public abstract class AbstractScriptTest {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractScriptTest.class);

    private static final ScriptEngine SCRIPT_ENGINE = new ScriptEngineConfiguration().getGroovyScriptEngine();

    /**
     * @param statement
     * @param userBindings
     * @param customScript
     * @return
     * @throws ScriptException
     */
    protected Object evalUserScript(final String statement, final Map<String, Object> userBindings, final CustomScript customScript) throws
                                                                                                                                     ScriptException {
        return evalUserScript(statement, null, userBindings, customScript);
    }

    /**
     * @param statement
     * @param userBindings
     * @return
     * @throws ScriptException
     */
    protected Object evalUserScript(final String statement,
                                    final String configStatement,
                                    final Map<String, Object> userBindings,
                                    final CustomScript customScript) throws ScriptException {

        // Initialisation du script custom
        final Map<String, CustomScript> customScripts = new HashMap<>();
        customScripts.put(customScript.getCode(), customScript);

        final Bindings bindings = SCRIPT_ENGINE.createBindings();
        bindings.put(BINDING_SCRIPT, customScripts);
        bindings.putAll(userBindings);

        // Configuration du script custom
        if (configStatement != null) {
            SCRIPT_ENGINE.eval(StringUtils.joinWith("\n", customScript.getConfigScript(), configStatement), bindings);
        }

        // Évaluation du script custom
        StringBuilder userScript = new StringBuilder();
        Arrays.stream(customScript.getScriptImport()).forEach(imp -> userScript.append(imp).append('\n'));
        userScript.append(customScript.getInitScript()).append('\n');
        userScript.append(statement);

        LOG.debug("Évaluation du script:\n{}", userScript.toString());
        return SCRIPT_ENGINE.eval(userScript.toString(), bindings);
    }
}
