package fr.progilone.pgcn.service.exchange.marc.script.format;

import org.marc4j.converter.CharConverter;

/**
 * Created by Sebastien on 02/12/2016.
 */
public class TitleFieldFormatter extends SubfieldsFormatter {

    public static final String SCRIPT_NAME = "title";

    public TitleFieldFormatter(final String code, final CharConverter charConverter) {
        super(code, charConverter);
        add('a', "; ");
        addGroup(" [", "]", "; ", "b");
        add('c', ". ");
        add('d', " = ");
        add('e', " : ");
        add('f', " /");
        add('g', "; ");
        addGroup(". ", null, ", ", "h", ", ", "i");
        add('r');
        add('7');
    }

    @Override
    public String getConfigScript() {
        return "def " + SCRIPT_NAME + "Filter = {\n"
               + "      String... codes -> script." + getCode() + ".setFilterCodes((char[])codes)\n"
               + "}\n";
    }

    @Override
    public String getInitScript() {
        return "def " + SCRIPT_NAME + " = {\n"
               + "      DataField field -> script." + getCode() + ".format(field)\n"
               + "}\n";
    }
}
