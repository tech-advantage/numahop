package fr.progilone.pgcn.service.exchange.marc.script;

import fr.progilone.pgcn.service.exchange.marc.MarcUtils;
import fr.progilone.pgcn.service.exchange.marc.script.format.CollectionFieldFormatter;
import fr.progilone.pgcn.service.exchange.marc.script.format.CorporateFieldFormatter;
import fr.progilone.pgcn.service.exchange.marc.script.format.DatePublicationFormatter;
import fr.progilone.pgcn.service.exchange.marc.script.format.PersonFieldFormatter;
import fr.progilone.pgcn.service.exchange.marc.script.format.SubfieldsFormatter;
import fr.progilone.pgcn.service.exchange.marc.script.format.TitleFieldFormatter;
import fr.progilone.pgcn.service.exchange.marc.script.format.ConcatWithSepFormatter;
import fr.progilone.pgcn.service.exchange.marc.script.test.ExistsCondition;
import org.marc4j.converter.CharConverter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sebastien on 06/12/2016.
 */
public abstract class CustomScript {

    private static final Map<String, Class<? extends CustomScript>> scripts = Collections.synchronizedMap(new HashMap<>());

    /**
     * Code unique, permettant d'identifier ce script
     */
    private final String code;

    /**
     * Le convertisseur utilisé pour décoder les données MARC
     */
    private final CharConverter charConverter;

    protected CustomScript(final String code, final CharConverter charConverter) {
        this.code = code;
        this.charConverter = charConverter;
    }

    public static Map<String, Class<? extends CustomScript>> getScripts() {
        if (scripts.isEmpty()) {
            scripts.put(CollectionFieldFormatter.SCRIPT_NAME, CollectionFieldFormatter.class);
            scripts.put(CorporateFieldFormatter.SCRIPT_NAME, CorporateFieldFormatter.class);
            scripts.put(PersonFieldFormatter.SCRIPT_NAME, PersonFieldFormatter.class);
            scripts.put(SubfieldsFormatter.SCRIPT_NAME, SubfieldsFormatter.class);
            scripts.put(TitleFieldFormatter.SCRIPT_NAME, TitleFieldFormatter.class);
            scripts.put(DatePublicationFormatter.SCRIPT_NAME, DatePublicationFormatter.class);
            scripts.put(ConcatWithSepFormatter.SCRIPT_NAME, ConcatWithSepFormatter.class);

            scripts.put(ExistsCondition.SCRIPT_NAME, ExistsCondition.class);
        }
        return scripts;
    }

    /**
     * Code unique identifiant ce script
     *
     * @return
     */
    public final String getCode() {
        return code;
    }

    /**
     * Imports nécessaires à l'exécution du script
     *
     * @return
     */
    public String[] getScriptImport() {
        // return new String[]{"import org.marc4j.marc.*", "import fr.progilone.pgcn.service.exchange.marc.formatter.*"};
        return new String[] {};
    }

    /**
     * Script servant à configurer ce {@link CustomScript}
     * Il est exécuté à la création de cet objet
     *
     * @return
     */
    public String getConfigScript() {
        return null;
    }

    /**
     * Définition d'une closure Groovy permettant d'appeler ce script simplement à partir des règles de mapping
     * <p>
     * Les script sont bindés dans une Map<String, CustomScript>, dont la clé est l'identifiant du formatter.
     *
     * @return
     */
    public abstract String getInitScript();

    /**
     * Conversion d'une chaîne de caractères issue d'une notice MARC
     *
     * @param data
     * @return
     */
    public String convert(final String data) {
        return MarcUtils.convert(data, charConverter);
    }
}
