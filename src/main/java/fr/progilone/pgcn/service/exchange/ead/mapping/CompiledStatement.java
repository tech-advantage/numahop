package fr.progilone.pgcn.service.exchange.ead.mapping;

import fr.progilone.pgcn.service.exchange.ead.script.CustomScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.CompiledScript;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Représente le script lié à une condition ou une expression
 */
public class CompiledStatement {

    private static final Logger LOG = LoggerFactory.getLogger(CompiledStatement.class);
    // Cette regex extrait les sous-chaines de la forme \<field>, en exluant les sous-chaînes de la forme \\<quelque_chose>,
    // représentant des champs d'une notice EAD
    private static final Pattern EXPRESSION_PATTERN = Pattern.compile("(?<!\\\\)(?:\\\\)(?<field>(?:[\\w.:]+|(?:\\{)[\\w.:]+(?:})))");

    /**
     * Champs utilisés dans ce script
     */
    private final Set<RuleKey> ruleKeys = new HashSet<>();
    /**
     * Script de configuration de ce statement
     */
    private String configScript;
    /**
     * Script saisi dans le mapping
     */
    private String userScript;
    /**
     * Script d'initialisation, nécessaire pour l'exécution de userScript
     */
    private String initScript;
    /**
     * Script principal compilé
     */
    private CompiledScript compiledScript;
    /**
     * {@link CustomScript} utilisés dans ce script
     */
    private final List<CustomScript> customScripts = new ArrayList<>();

    public CompiledStatement(final String statement, final String configStatement) {
        this.configScript = configStatement;
        this.userScript = statement;
        this.ruleKeys.addAll(getRuleKeys(statement));
        initialize();
    }

    public Set<RuleKey> getRuleKeys() {
        return ruleKeys;
    }

    public String getConfigScript() {
        return configScript;
    }

    public String getInitScript() {
        return initScript;
    }

    public String getUserScript() {
        return userScript;
    }

    public List<CustomScript> getCustomScripts() {
        return customScripts;
    }

    public CompiledScript getCompiledScript() {
        return compiledScript;
    }

    public void setCompiledScript(final CompiledScript compiledScript) {
        this.compiledScript = compiledScript;
    }

    /**
     * Initialisation des scripts personnalisés
     *
     * @return
     */
    private void initialize() {
        if (this.userScript != null) {
            final Set<String> scriptsImports = new HashSet<>();
            final List<String> initScripts = new ArrayList<>();

            // Ajout des customScripts
            CustomScript.getScripts().forEach((scriptName, fmtClass) -> {
                // le script peut être multilignes: (?s).* matche tous les caractères, y compris les sauts de ligne
                if (this.userScript.matches("^(?s).*\\b" + scriptName + "\\((?s).*$")) {
                    try {
                        final CustomScript customScript = fmtClass.getConstructor(String.class).newInstance(scriptName.toLowerCase());
                        scriptsImports.addAll(Arrays.asList(customScript.getScriptImport()));
                        initScripts.add(customScript.getInitScript());
                        this.customScripts.add(customScript);
                    } catch (ReflectiveOperationException e) {
                        LOG.error(e.getMessage(), e);
                    }
                }
            });
            // Construction du script d'initialisation
            if (!scriptsImports.isEmpty() || !initScripts.isEmpty()) {
                final StringBuilder scrBuilder = new StringBuilder();
                scriptsImports.forEach(scr -> scrBuilder.append(scr).append('\n'));
                initScripts.forEach(scr -> scrBuilder.append(scr).append('\n'));
                this.initScript = scrBuilder.toString();
            }
        }
    }

    /**
     * Recherche les RuleKey présents dans le statement passé en paramètre (expression ou condition d'une règle de mapping)
     *
     * @param statement
     * @return
     */
    private Set<RuleKey> getRuleKeys(final String statement) {
        if (statement == null) {
            return Collections.emptySet();
        }
        final Set<RuleKey> innerRuleKeys = new HashSet<>();
        final Matcher matcher = EXPRESSION_PATTERN.matcher(statement);

        while (matcher.find()) {
            String field = matcher.group("field");
            // variable entre accolades
            if (field.startsWith("{")) {
                field = field.substring(1, field.length() - 1);
            }
            innerRuleKeys.add(new RuleKey(field));
        }
        return innerRuleKeys;
    }
}
