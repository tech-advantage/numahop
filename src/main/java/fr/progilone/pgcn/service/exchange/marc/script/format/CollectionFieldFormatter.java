package fr.progilone.pgcn.service.exchange.marc.script.format;

import org.marc4j.converter.CharConverter;

/**
 * Formatter nom de personne
 * <p>
 * Created by Sebastien on 22/11/2016.
 */
public class CollectionFieldFormatter extends SubfieldsFormatter {

    public static final String SCRIPT_NAME = "collection";

    /**
     * cf. manuel unimarc BNF, champ collection 225
     * http://www.bnf.fr/fr/professionnels/anx_formats/a.unimarc_manuel_format_bibliographique.html
     */
    public CollectionFieldFormatter(final String code, final CharConverter charConverter) {
        super(code, charConverter);
        add('a');
        add('d', " = ");
        add('e', ": ");
        add('f', " / ");
        addGroup(". ", null, ", ", "h", ", ", "i");
        add('v', "; ");
        add('x', ", ");
    }

    @Override
    public String getConfigScript() {
        return "def " + SCRIPT_NAME
               + "Filter = {\n"
               + "      String... codes -> script."
               + getCode()
               + ".setFilterCodes((char[])codes)\n"
               + "}\n";
    }

    @Override
    public String getInitScript() {
        return "def " + SCRIPT_NAME
               + " = {\n"
               + "      DataField field -> script."
               + getCode()
               + ".format(field)\n"
               + "}\n";
    }
}
