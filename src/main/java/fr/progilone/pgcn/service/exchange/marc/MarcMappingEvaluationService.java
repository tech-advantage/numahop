package fr.progilone.pgcn.service.exchange.marc;

import fr.progilone.pgcn.domain.exchange.Mapping;
import fr.progilone.pgcn.service.administration.TransliterationService;
import fr.progilone.pgcn.service.exchange.marc.mapping.CompiledMapping;
import fr.progilone.pgcn.service.exchange.marc.mapping.CompiledMappingRule;
import fr.progilone.pgcn.service.exchange.marc.mapping.CompiledStatement;
import fr.progilone.pgcn.service.exchange.marc.mapping.MarcKey;
import fr.progilone.pgcn.service.exchange.marc.script.CustomScript;
import org.apache.commons.lang3.StringUtils;
import org.marc4j.converter.CharConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Sebastien on 23/11/2016.
 */
@Service
public class MarcMappingEvaluationService {

    private static final Logger LOG = LoggerFactory.getLogger(MarcMappingEvaluationService.class);

    // Binding des scripts personnalisés
    public static final String BINDING_SCRIPT = "script";
    // Binding de la notice courante
    public static final String BINDING_RECORD = "record";
    // Binding du leader de la notice courante
    public static final String BINDING_LEADER = "leader";
    // Binding de l'index du binding, pour dédupliquer les champs uniques
    public static final String BINDING_INDEX_ITEM = "idxitem";
    // Binding de la fonction de transliteration
    public static final String BINDING_FN_TRANSLITERATE = "transliterate";

    private final ScriptEngine engine;
    private final TransliterationService transliterationService;

    @Autowired
    public MarcMappingEvaluationService(@Qualifier("groovy") final ScriptEngine engine, final TransliterationService transliterationService) {
        this.engine = engine;
        this.transliterationService = transliterationService;
    }

    /**
     * Compilation des règles de mapping
     *
     * @param mapping
     * @param charConverter
     */
    public CompiledMapping compileMapping(final Mapping mapping, final CharConverter charConverter) {
        LOG.debug("Compilation des règles pour le mapping {} ...", mapping);
        final CompiledMapping compiledMapping = new CompiledMapping(mapping);
        compiledMapping.initialize(charConverter);

        for (final CompiledMappingRule rule : compiledMapping.getCompiledRules()) {
            // Condition
            if (rule.hasCondition()) {
                compileStatement(rule.getConditionStatement()).ifPresent(rule::setCompiledCondition);
            }
            // Expression
            if (rule.hasExpression()) {
                compileStatement(rule.getExpressionStatement()).ifPresent(rule::setCompiledExpression);
            }
        }
        return compiledMapping;
    }

    /**
     * Création d'une règle Groovy à la volée
     *
     * @param script
     * @param charConverter
     */
    public CompiledMappingRule compileMapping(final String script, final CharConverter charConverter) {
        LOG.debug("Compilation du script {} ...", script);
        final CompiledMappingRule compiledScript = new CompiledMappingRule(script);
        compiledScript.initialize(charConverter);

        // Expression
        if (compiledScript.hasExpression()) {
            compileStatement(compiledScript.getExpressionStatement()).ifPresent(compiledScript::setCompiledExpression);
        }
        return compiledScript;
    }

    /**
     * Évaluation de la condition d'une règle de mapping
     *
     * @param rule
     * @param bindings
     * @return un boolean résultat de l'évaluation de la condition,
     * true si pas de condition définie,
     * false si pb de compilation ou d'évaluation
     * @throws ScriptException
     */
    public boolean evalCondition(CompiledMappingRule rule, Map<String, Object> bindings) {
        try {
            // Pas de condition à appliquer
            if (!rule.hasCondition()) {
                return true;
            }
            // Condition compilée
            final CompiledStatement condition = rule.getConditionStatement();

            if (condition.getCompiledScript() != null) {
                // On rajoute les bindings des éventuels scripts
                final Map<String, CustomScript> scripts = getScriptBinding(condition);
                bindings.put(BINDING_SCRIPT, scripts);
                bindings.put(BINDING_FN_TRANSLITERATE, transliterationService);

                final Object test = eval(condition.getCompiledScript(), bindings);
                // Le test ne retourne rien => condition  = false
                if (test == null) {
                    return false;
                }
                // Le test retourne un boolean => condition = test
                else if (test instanceof Boolean) {
                    return ((Boolean) test);
                }
                // Le test retourne une string => condition = string non vide
                else if (test instanceof String) {
                    return StringUtils.isNotBlank((String) test);
                }
                // Le test retourne quelquechose, mais pas un boolean => condition = true
                else {
                    return true;
                }
            }
            // Condition, mais pas compilée
            else {
                LOG.warn("La condition de la règle de mapping {} n'a pas été compilée", rule);
                return false;
            }
        } catch (ScriptException e) {
            LOG.error("Une erreur s'est produite lors de l'évaluation de la condition.\n- bindings définis: {}\n- condition: {}",
                      StringUtils.join(bindings.keySet(), ", "),
                      rule.getRuleCondition());
            LOG.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * Évaluation de l'expression d'une règle de mapping
     *
     * @param rule
     * @param bindings
     * @return un objet résultat de l'évaluation de l'expression,
     * ou null si pas d'expression ou si une erreur s'est produite
     * @throws ScriptException
     */
    public Object evalExpression(CompiledMappingRule rule, Map<String, Object> bindings) {
        try {
            // Pas d'expression à évaluer
            if (!rule.hasExpression()) {
                LOG.warn("L'expression de la règle de mapping {} n'est pas définie", rule);
                return null;
            }
            // Expression compilée
            final CompiledStatement expression = rule.getExpressionStatement();

            if (expression.getCompiledScript() != null) {
                // On rajoute les bindings des éventuels scripts
                final Map<String, CustomScript> scripts = getScriptBinding(expression);
                bindings.put(BINDING_SCRIPT, scripts);
                bindings.put(BINDING_FN_TRANSLITERATE, transliterationService);

                return eval(expression.getCompiledScript(), bindings);
            }
            // Expression définie, mais pas compilée
            else {
                LOG.warn("L'expression de la règle de mapping {} n'a pas été compilée", rule);
                return null;
            }
        } catch (ScriptException e) {
            LOG.error("Une erreur s'est produite lors de l'évaluation de l'expression.\n- bindings définis: {}\n- expression: {}",
                      StringUtils.join(bindings.keySet(), ", "),
                      rule.getRuleExpression());
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Compilation du script
     *
     * @param compiledStatement
     * @return
     */
    private Optional<CompiledScript> compileStatement(final CompiledStatement compiledStatement) {
        if (compiledStatement.getUserScript() == null) {
            return Optional.empty();
        }
        // Configuration des scripts custom
        final Map<String, Object> bindings = new HashMap<>();
        bindings.put(BINDING_SCRIPT, getScriptBinding(compiledStatement));
        bindings.put(BINDING_FN_TRANSLITERATE, transliterationService);

        if (compiledStatement.getConfigScript() != null) {
            final List<String> scripts = compiledStatement.getCustomScripts()
                                                          .stream()
                                                          .map(CustomScript::getConfigScript)
                                                          .filter(Objects::nonNull)
                                                          .collect(Collectors.toList());
            scripts.add(compiledStatement.getConfigScript());
            eval(StringUtils.join(scripts, '\n'), bindings);
        }

        // Génération du script à partir de la règle de mapping saisie par l'utilisateur:
        // remplacement des notations \tag$code par des noms de variable valides
        String innerScript = compiledStatement.getUserScript();
        for (final MarcKey k : compiledStatement.getMarcKeys()) {
            innerScript = innerScript.replaceAll(k.getRegex(), k.getVariableName());
        }
        // Script d'initialisation éxécuté avant tout appel du script saisi par l'utilisateur
        if (compiledStatement.getInitScript() != null) {
            innerScript = compiledStatement.getInitScript() + innerScript;
        }
        // Compilation du innerScript
        return compile(innerScript);
    }

    /**
     * Compilation d'un script
     *
     * @param script
     * @return Optional vide si une exception se produit au cours de la compilation du script
     */
    private Optional<CompiledScript> compile(final String script) {
        try {
            LOG.trace("Compilation du script:\n{}", script);
            return Optional.of(((Compilable) engine).compile(script));
        } catch (ScriptException e) {
            LOG.error("Une erreur s'est produite lors de la compilation du script:\n{}", script);
            LOG.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Évaluation d'un script, sans précompilation
     *
     * @param script
     * @return Optional vide si une exception se produit au cours de la compilation du script
     */
    private Optional<Object> eval(final String script, Map<String, Object> bindings) {
        try {
            LOG.trace("Exécution du script avec les bindings [{}]:\n{}", StringUtils.join(bindings.keySet(), ", "), script);
            final Bindings engineBindings = engine.createBindings();
            engineBindings.putAll(bindings);
            return Optional.ofNullable(engine.eval(script, engineBindings));
        } catch (ScriptException e) {
            LOG.error("Une erreur s'est produite lors de l'évaluation du script:\n{}", script);
            LOG.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Évaluation d'un script compilé
     *
     * @param script
     * @param bindings
     * @return
     * @throws ScriptException
     */
    private Object eval(CompiledScript script, Map<String, Object> bindings) throws ScriptException {
        final Bindings engineBindings = engine.createBindings();
        engineBindings.putAll(bindings);
        return script.eval(engineBindings);
    }

    /**
     * Créé le binding des scripts pour la statement
     *
     * @param compiledStatement
     * @return
     */
    private Map<String, CustomScript> getScriptBinding(final CompiledStatement compiledStatement) {
        return compiledStatement.getCustomScripts().stream().collect(Collectors.toMap(CustomScript::getCode, Function.identity(), (a, b) -> a));
    }
}
