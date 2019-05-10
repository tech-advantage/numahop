package fr.progilone.pgcn.service.exchange.marc.script.test;

import fr.progilone.pgcn.service.exchange.marc.script.CustomScript;
import org.marc4j.converter.CharConverter;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.VariableField;

/**
 * Test l'existence s'un champ ou d'un sous-champ dans une notice MARC
 * <p>
 * Created by Sebastien on 06/12/2016.
 */
public class ExistsCondition extends CustomScript {

    public static final String SCRIPT_NAME = "exists";

    public ExistsCondition(final String code, final CharConverter charConverter) {
        super(code, charConverter);
    }

    /**
     * Test l'existant d'un champ et/ou d'un sous-champ dans la notice MARC
     *
     * @param record
     * @param tag
     * @param code
     * @return
     */
    public boolean test(Record record, String tag, Character code) {
        final VariableField field = record.getVariableField(tag);
        // Le champ existe
        return field != null
               && (code == null
                   // Le sous-champ existe
                   || (field instanceof DataField && ((DataField) field).getSubfield(code) != null));
    }

    /**
     * Imports nécessaires à l'exécution du script
     *
     * @return
     */
    @Override
    public String[] getScriptImport() {
        return new String[] {"import org.marc4j.marc.Record"};
    }

    @Override
    public String getInitScript() {
        return "def " + SCRIPT_NAME + " = {\n"
               + "      Record record, String tag, String code = null -> \n"
               + "          return script." + getCode() + ".test(record, tag, (Character)code)\n"
               + "}\n";
    }
}
