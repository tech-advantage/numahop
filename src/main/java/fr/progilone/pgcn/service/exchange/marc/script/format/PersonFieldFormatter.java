package fr.progilone.pgcn.service.exchange.marc.script.format;

import fr.progilone.pgcn.service.exchange.marc.MarcMappingEvaluationService;
import org.marc4j.converter.CharConverter;

/**
 * Formatter nom de personne
 * <p>
 * Created by Sebastien on 22/11/2016.
 */
public class PersonFieldFormatter extends SubfieldsFormatter {

    public static final String SCRIPT_NAME = "person";

    public PersonFieldFormatter(final String code, final CharConverter charConverter) {
        super(code, charConverter);
        add('a');
        add('b', ", ");
        addGroup(" (", ")", "; ", "c", "; ", "f");
        add('d');
        add('D');
        add('4', ". ");
        addTransliteration('4', "FUNCTION");
    }

    @Override
    public String getConfigScript() {
        return "def " + SCRIPT_NAME + "Filter = {\n"
               + "      String... codes -> script." + getCode() + ".setFilterCodes((char[])codes)\n"
               + "}\n"
               + "def " + SCRIPT_NAME + "Transliterate = {\n"
               + "      String code, String type -> script." + getCode() + ".addTransliteration((char)code, type)\n"
               + "}\n";
    }

    @Override
    public String getInitScript() {
        return "def " + SCRIPT_NAME + " = {\n"
               + "      DataField field -> script." + getCode() + ".format(field)\n"
               + "}\n"
               + "if(binding.hasVariable('" + MarcMappingEvaluationService.BINDING_FN_TRANSLITERATE + "')) {\n"
               + "  script." + getCode() + ".setTransliterate(" + MarcMappingEvaluationService.BINDING_FN_TRANSLITERATE + ".&getValue)\n"
               + "}\n";
    }
}
