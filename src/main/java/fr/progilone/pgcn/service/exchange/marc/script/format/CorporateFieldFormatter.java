package fr.progilone.pgcn.service.exchange.marc.script.format;

import org.marc4j.converter.CharConverter;

/**
 * Formatter collectivité
 * <p>
 * Created by Sebastien on 01/12/2016.
 */
public class CorporateFieldFormatter extends SubfieldsFormatter {

    public static final String SCRIPT_NAME = "corporate";

    public CorporateFieldFormatter(final String code, final CharConverter charConverter) {
        super(code, charConverter);
        add('a');
        add('b', ". ");
        addGroup(" (", ")", "; ", "c");
        addGroup(" (", ")", "; ", "d", "; ", "e", "; ", "f");
        add('g');
        add('h');
        add('4', ". ");
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
