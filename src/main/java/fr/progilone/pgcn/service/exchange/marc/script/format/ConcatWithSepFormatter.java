package fr.progilone.pgcn.service.exchange.marc.script.format;

import fr.progilone.pgcn.service.exchange.marc.script.CustomScript;
import org.apache.commons.lang3.ArrayUtils;
import org.marc4j.converter.CharConverter;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Subfield;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Formatter gérant les séparateurs entre sous-champs, en distinguant les sous-champs suivant qu'ils ont le même tag ou non
 * <p>
 * Created by Sébastien on 29/06/2017.
 */
public class ConcatWithSepFormatter extends CustomScript {

    public static final String SCRIPT_NAME = "concatWithSep";

    /**
     * Champs gérés par ce formatter
     */
    private char[] codes = {};

    /**
     * Séparateur des sous-champs de même tag;
     */
    private String sepInner = " -- ";

    /**
     * Séparateurs des sous-champs de tags différents
     */
    private String sepOuter = " - ";

    public ConcatWithSepFormatter(final String code, final CharConverter charConverter) {
        super(code, charConverter);
    }

    public void setCodes(final char[] codes) {
        this.codes = codes;
    }

    public void setSepInner(final String sepInner) {
        this.sepInner = sepInner;
    }

    public void setSepOuter(final String sepOuter) {
        this.sepOuter = sepOuter;
    }

    @Override
    public String getConfigScript() {
        return "def "
               + SCRIPT_NAME
               + "Codes = {\n"
               + "      String... codes -> script."
               + getCode()
               + ".setCodes((char[])codes)\n"
               + "}\n"
               + "def "
               + SCRIPT_NAME
               + "Inner = {\n"
               + "      String sep -> script."
               + getCode()
               + ".setSepInner(sep)\n"
               + "}\n"
               + "def "
               + SCRIPT_NAME
               + "Outer = {\n"
               + "      String sep -> script."
               + getCode()
               + ".setSepOuter(sep)\n"
               + "}\n";
    }

    @Override
    public String getInitScript() {
        return "def " + SCRIPT_NAME + " = {\n" + "      DataField field -> script." + getCode() + ".format(field)\n" + "}\n";
    }

    @Override
    public String[] getScriptImport() {
        return new String[] {"import org.marc4j.marc.DataField"};
    }

    public String format(final DataField field) {
        if (field == null) {
            return null;
        }
        final List<Subfield> subfields =
            field.getSubfields().stream().filter(sub -> ArrayUtils.contains(codes, sub.getCode())).collect(Collectors.toList());

        final StringBuilder buf = new StringBuilder();
        Character lastTag = null;

        for (final Subfield subfield : subfields) {
            if (lastTag != null) {
                buf.append(lastTag == subfield.getCode() ? sepInner : sepOuter);
            }
            buf.append(subfield.getData());
            lastTag = subfield.getCode();
        }
        return buf.length() > 0 ? buf.toString() : "";
    }
}
