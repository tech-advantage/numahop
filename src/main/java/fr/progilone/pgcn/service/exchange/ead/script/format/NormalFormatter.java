package fr.progilone.pgcn.service.exchange.ead.script.format;

import fr.progilone.pgcn.service.exchange.ead.EadCParser;
import fr.progilone.pgcn.service.exchange.ead.script.CustomScript;

import java.util.List;

import static fr.progilone.pgcn.service.exchange.ead.EadCParser.*;

/**
 * Si un objet définit l'attribut "normal", il est affiché; sinon son contenu est affiché.
 * <p>
 * Created by Sébastien on 17/05/2017.
 */
public class NormalFormatter extends CustomScript {

    public static final String SCRIPT_NAME = "normal";

    public NormalFormatter(final String code) {
        super(code);
    }

    /**
     * Formattage d'un objet
     *
     * @param o
     * @return
     */
    public String format(final Object o) {
        if (o == null) {
            return "";
        }
        List<?> values = EadCParser.getObjectValues(o, ATTR_NORMAL);
        if (values.isEmpty()) {
            values = EadCParser.getObjectValues(o, ATTR_CONTENT);
        }
        return values.stream().map(String::valueOf).reduce((a, b) -> a + " " + b).orElse("");
    }

    /**
     * Formattage d'une liste d'objets
     *
     * @param list
     * @return
     */
    public String format(final List<?> list) {
        return list.stream().map(this::format).reduce((a, b) -> a + " " + b).orElse("");
    }

    @Override
    public String getInitScript() {
        return "def " + SCRIPT_NAME + " = {\n" + "      Object o -> script." + getCode() + ".format(o)\n" + "}\n";
    }
}
