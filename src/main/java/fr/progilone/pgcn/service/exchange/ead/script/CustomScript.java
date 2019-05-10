package fr.progilone.pgcn.service.exchange.ead.script;

import fr.progilone.pgcn.service.exchange.ead.script.format.NormalFormatter;
import fr.progilone.pgcn.service.exchange.ead.script.format.TextFormatter;

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

    protected CustomScript(final String code) {
        this.code = code;
    }

    public static Map<String, Class<? extends CustomScript>> getScripts() {
        if (scripts.isEmpty()) {
            scripts.put(NormalFormatter.SCRIPT_NAME, NormalFormatter.class);
            scripts.put(TextFormatter.SCRIPT_NAME, TextFormatter.class);
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
}
