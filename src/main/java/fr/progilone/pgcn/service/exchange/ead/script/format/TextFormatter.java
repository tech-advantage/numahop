package fr.progilone.pgcn.service.exchange.ead.script.format;

import fr.progilone.pgcn.domain.jaxb.ead.P;
import fr.progilone.pgcn.service.exchange.ead.EadCParser;
import fr.progilone.pgcn.service.exchange.ead.script.CustomScript;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static fr.progilone.pgcn.service.exchange.ead.EadCParser.*;

/**
 * Ce formatter extrait les données textuelles d'un objet, en supprimant les données de mise en forme
 * <p>
 * Created by Sébastien on 18/05/2017.
 */
public class TextFormatter extends CustomScript {

    private static final Logger LOG = LoggerFactory.getLogger(TextFormatter.class);

    public static final String SCRIPT_NAME = "text";

    private boolean normal = false; // utilisation de l'attribut "normal" si il existe

    public TextFormatter(final String code) {
        super(code);
    }

    /**
     * Formattage d'un objet
     * Parcourt la hiérarchie d'objets à la recherche de valeurs textuelles
     *
     * @param o
     * @return
     */
    public String format(final Object o) {
        try {
            final List<Object> nextValues =
                EadCParser.getNextValues(o).stream().map(EadCParser::unwrapJAXBElement).filter(Objects::nonNull).collect(Collectors.toList());
            final StringBuilder formattedValue = new StringBuilder();
            Object lastValue = null;

            for (final Object val : nextValues) {
                if (lastValue != null) {
                    final String sep =
                        // entre 2 paragraphes: tiret
                        lastValue instanceof P && val instanceof P ? " - "
                        // entre un élément texte et un objet: rien du tout
                                                                   : (lastValue instanceof String ^ val instanceof String) ? ""
                                                                     // sinon un espace
                                                                                                                           : " ";
                    formattedValue.append(sep);
                }
                lastValue = val;

                if (val instanceof String) {
                    formattedValue.append(val);
                } else {
                    // On récupère en priorité l'attribut "normal"
                    if (normal && PropertyUtils.isReadable(val, EadCParser.ATTR_NORMAL)) {
                        final Object normalValue = PropertyUtils.getSimpleProperty(val, ATTR_NORMAL);

                        if (normalValue != null) {
                            formattedValue.append(normalValue);
                            continue;
                        }
                    }
                    // Si l'attribut "normal" n'existe pas ou qu'il n'est pas renseigné, on poursuit le formattage avec val
                    formattedValue.append(format(val));
                }
            }
            // suppression des espaces répétés
            return StringUtils.normalizeSpace(formattedValue.toString().replaceAll("\\s+", " "));

        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            LOG.error(e.getMessage(), e);
            return "";
        }
    }

    /**
     * Formattage d'une liste d'objets
     *
     * @param list
     * @return
     */
    public String format(final List<?> list) {
        return list.stream().map(this::format).reduce((a, b) -> a + "\n" + b).orElse("");
    }

    @Override
    public String getConfigScript() {
        return "def " + SCRIPT_NAME + "SetNormal = {\n" + "      boolean normal -> script." + getCode() + ".setNormal(normal)\n" + "}\n";
    }

    @Override
    public String getInitScript() {
        return "def " + SCRIPT_NAME + " = {\n" + "      Object o -> script." + getCode() + ".format(o)\n" + "}\n";
    }

    /**
     * Utiliser l'attribut "normal" si il est présent, au lieu du contenu
     *
     * @return
     */
    public void setNormal(final boolean normal) {
        this.normal = normal;
    }
}
