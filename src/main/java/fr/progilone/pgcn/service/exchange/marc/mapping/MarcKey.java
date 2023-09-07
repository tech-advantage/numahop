package fr.progilone.pgcn.service.exchange.marc.mapping;

import java.util.Objects;

/**
 * Couple tag$code d'un champ Marc
 */
public class MarcKey {

    private final String tag;
    private final Character code;

    public MarcKey(final String tag) {
        this.tag = tag;
        this.code = null;
    }

    public MarcKey(final String tag, final Character code) {
        this.tag = tag;
        this.code = code;
    }

    public String getTag() {
        return tag;
    }

    public Character getCode() {
        return code;
    }

    /**
     * Génère un nom de variable valide pour ce tag$code
     *
     * @return
     */
    public String getVariableName() {
        return "marc_" + tag
               + (code != null ? "_" + code
                               : "");
    }

    /**
     * Renvoie la regex correspondant à cette {@link MarcKey}
     *
     * @return
     */
    public String getRegex() {
        return code == null ? "\\\\" + tag
                              + "\\b(?!\\$)"    // \tag est en fin de mot, non suivi du caractère $
                            : "\\\\" + tag
                              + "\\$"
                              + code;
    }

    /**
     * Ce {@link MarcKey} représente-t-il un champ générique de type 6xx ?
     *
     * @return
     */
    public boolean isXX() {
        return this.tag != null && this.tag.endsWith("xx");
    }

    @Override
    public String toString() {
        return tag + (code != null ? "$" + code
                                   : "");
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final MarcKey marcKey = (MarcKey) o;
        return Objects.equals(tag, marcKey.tag) && Objects.equals(code, marcKey.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tag, code);
    }
}
