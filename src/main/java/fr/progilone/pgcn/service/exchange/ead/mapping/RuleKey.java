package fr.progilone.pgcn.service.exchange.ead.mapping;

import java.util.Objects;

/**
 * Clé champ / sous-champ d'une donnée EAD
 * <p>
 * Created by Sébastien on 17/05/2017.
 */
public class RuleKey {

    /**
     * Champ de l'objet concerné par la règle
     */
    private final String field;

    public RuleKey(final String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }

    /**
     * Nom de la variable utilisée dans les bindings des scripts groovy
     *
     * @return
     */
    public String getVariableName() {
        return "var_" + field.replaceAll("[.:]", "_");
    }

    /**
     * Expression régulière permettant de rechercher la variable représentée par cette RuleKey dans un script
     *
     * @return
     */
    public String getRegex() {
        String regex = field.replaceAll("\\.", "\\\\.");
        // le nom du champ est précédé d'un \, est eventuellement entre accolades {...}, et la fin de mot n'est pas suivie d'un point
        regex = "\\\\\\{?" + regex
                + "\\b(?!\\.)\\}?";
        return regex;
    }

    @Override
    public String toString() {
        return field;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final RuleKey ruleKey = (RuleKey) o;
        return Objects.equals(field, ruleKey.field);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field);
    }
}
