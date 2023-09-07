package fr.progilone.pgcn.service.exchange.marc.mapping;

import fr.progilone.pgcn.domain.exchange.MappingRule;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.script.CompiledScript;
import org.apache.commons.lang3.StringUtils;
import org.marc4j.converter.CharConverter;

/**
 * Created by Sebastien on 24/11/2016.
 */
public final class CompiledMappingRule {

    /**
     * Règle de mapping initiale
     */
    private MappingRule rule;
    private String ruleCondition;
    private String ruleExpression;
    private CompiledStatement conditionStatement;
    private CompiledStatement expressionStatement;

    public CompiledMappingRule(final String expression) {
        this.ruleExpression = expression;

        if (StringUtils.isNotBlank(expression)) {
            this.expressionStatement = new CompiledStatement(expression, null);
        }
    }

    public CompiledMappingRule(final MappingRule rule) {
        this.rule = rule;
        this.ruleCondition = rule.getCondition();
        this.ruleExpression = rule.getExpression();

        if (StringUtils.isNotBlank(rule.getCondition())) {
            this.conditionStatement = new CompiledStatement(rule.getCondition(), rule.getConditionConf());
        }
        if (StringUtils.isNotBlank(rule.getExpression())) {
            this.expressionStatement = new CompiledStatement(rule.getExpression(), rule.getExpressionConf());
        }
    }

    public CompiledStatement getConditionStatement() {
        return conditionStatement;
    }

    public CompiledStatement getExpressionStatement() {
        return expressionStatement;
    }

    public MappingRule getRule() {
        return rule;
    }

    public String getRuleCondition() {
        return ruleCondition;
    }

    public String getRuleExpression() {
        return ruleExpression;
    }

    public boolean hasCondition() {
        return this.conditionStatement != null;
    }

    public boolean hasExpression() {
        return this.expressionStatement != null;
    }

    public void setCompiledCondition(final CompiledScript compiledCondition) {
        if (hasCondition()) {
            this.conditionStatement.setCompiledScript(compiledCondition);
        }
    }

    public void setCompiledExpression(final CompiledScript compiledExpression) {
        if (hasExpression()) {
            this.expressionStatement.setCompiledScript(compiledExpression);
        }
    }

    /**
     * @return Tous les {@link MarcKey} présents dans la condition et l'expression
     */
    public Set<MarcKey> getMarcKeys() {
        final Set<MarcKey> marcKeys = new HashSet<>();
        if (hasCondition()) {
            marcKeys.addAll(conditionStatement.getMarcKeys());
        }
        if (hasExpression()) {
            marcKeys.addAll(expressionStatement.getMarcKeys());
        }
        return marcKeys;
    }

    /**
     * Filtrage des champs MARC:
     * - les champs présents dans les expressions sont repris tels quels
     * - les champs présents dans les conditions sont repris:
     * -> s'ils ne sont pas trouvés exactement dans les expressions
     * -> si une expression n'utilise pas une notation plus générique (par ex: expression 6xx et condition 606, la condition 606 est ignorée)
     *
     * @return
     */
    public Set<MarcKey> getBindableMarcKeys() {
        final Set<MarcKey> marcKeys = new HashSet<>();
        if (hasExpression()) {
            marcKeys.addAll(expressionStatement.getMarcKeys());
        }
        if (hasCondition()) {
            conditionStatement.getMarcKeys()
                              .stream()
                              .filter(key -> expressionStatement == null || expressionStatement.getMarcKeys()
                                                                                               .stream()
                                                                                               // les tags sont identiques
                                                                                               .noneMatch(exprKey -> Objects.equals(exprKey, key)
                                                                                                                     // le tag de l'expression est
                                                                                                                     // plus générique que celui de la
                                                                                                                     // condition
                                                                                                                     || (exprKey.isXX() && exprKey.getTag().charAt(0) == key
                                                                                                                                                                            .getTag()
                                                                                                                                                                            .charAt(0))))
                              .forEach(marcKeys::add);
        }
        return marcKeys;
    }

    /**
     * true si cette règle n'a pas besoin de définir de bindings pour être évaluée
     *
     * @return
     */
    public boolean isConstant() {
        return (conditionStatement == null || conditionStatement.getMarcKeys().isEmpty()) && (expressionStatement == null || expressionStatement.getMarcKeys().isEmpty());
    }

    /**
     * true si la partie expression de cette règle n'a pas besoin de définir de bindings pour être évaluée
     *
     * @return
     */
    public boolean isConstantExpression() {
        return expressionStatement == null || expressionStatement.getMarcKeys().isEmpty();
    }

    /**
     * Initialisation des scripts personnalisés pour la condition et l'expression
     *
     * @param charConverter
     */
    public void initialize(final CharConverter charConverter) {
        if (hasCondition()) {
            conditionStatement.initialize(charConverter);
        }
        if (hasExpression()) {
            expressionStatement.initialize(charConverter);
        }
    }

    @Override
    public String toString() {
        return "CompiledMappingRule{" + "rule="
               + rule
               + '}';
    }
}
