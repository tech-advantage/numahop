package fr.progilone.pgcn.service.exchange.ead.mapping;

import fr.progilone.pgcn.domain.exchange.MappingRule;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.script.CompiledScript;
import org.apache.commons.lang3.StringUtils;

public final class CompiledMappingRule {

    /**
     * Règle de mapping initiale
     */
    private MappingRule rule;
    private CompiledStatement conditionStatement;
    private CompiledStatement expressionStatement;

    public CompiledMappingRule(final MappingRule rule) {
        this.rule = rule;

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
     * @return Tous les {@link RuleKey} présents dans la condition et l'expression
     */
    public Set<RuleKey> getRuleKeys() {
        final Set<RuleKey> ruleKeys = new HashSet<>();
        if (hasCondition()) {
            ruleKeys.addAll(conditionStatement.getRuleKeys());
        }
        if (hasExpression()) {
            ruleKeys.addAll(expressionStatement.getRuleKeys());
        }
        return ruleKeys;
    }

    /**
     * Filtrage des champs:
     * - les champs présents dans les expressions sont repris tels quels
     * - les champs présents dans les conditions sont repris s'ils ne sont pas trouvés exactement dans les expressions
     *
     * @return
     */
    public Set<RuleKey> getBindableRuleKeys() {
        final Set<RuleKey> ruleKeys = new HashSet<>();
        if (hasExpression()) {
            ruleKeys.addAll(expressionStatement.getRuleKeys());
        }
        if (hasCondition()) {
            conditionStatement.getRuleKeys()
                              .stream()
                              .filter(key -> expressionStatement == null || expressionStatement.getRuleKeys()
                                                                                               .stream()
                                                                                               // les tags sont identiques
                                                                                               .noneMatch(exprKey -> Objects.equals(exprKey, key)))
                              .forEach(ruleKeys::add);
        }
        return ruleKeys;
    }

    /**
     * true si cette règle n'a pas besoin de définir de bindings pour être évaluée
     *
     * @return
     */
    public boolean isConstant() {
        return (conditionStatement == null || conditionStatement.getRuleKeys().isEmpty()) && (expressionStatement == null || expressionStatement.getRuleKeys().isEmpty());
    }

    /**
     * true si la partie expression de cette règle n'a pas besoin de définir de bindings pour être évaluée
     *
     * @return
     */
    public boolean isConstantExpression() {
        return expressionStatement == null || expressionStatement.getRuleKeys().isEmpty();
    }

    @Override
    public String toString() {
        return "CompiledMappingRule{" + "rule="
               + rule
               + '}';
    }
}
