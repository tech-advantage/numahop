package fr.progilone.pgcn.service.exchange.marc.script.format;

import fr.progilone.pgcn.service.exchange.marc.MarcMappingEvaluationService;
import fr.progilone.pgcn.service.exchange.marc.script.CustomScript;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.marc4j.converter.CharConverter;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Subfield;

/**
 * Formatter de sous-champ, gérant les codes préfixés et les groupes de codes
 * <p>
 * Created by Sebastien on 01/12/2016.
 */
public class SubfieldsFormatter extends CustomScript {

    public static final String SCRIPT_NAME = "subfields";

    /**
     * Configuration de l'affichage des sous-champs
     */
    private final List<Config> formatCfg = Collections.synchronizedList(new ArrayList<>());
    /**
     * Séparateur de sous-champs par défaut
     */
    private String defaultSeparator = " ";
    /**
     * Liste de tags auxquels se limitera le formatteur
     */
    private char[] filterCodes = {};

    /**
     * Configuration des translitérations
     */
    private final Map<Character, String> transliterations = Collections.synchronizedMap(new HashMap<>());
    /**
     * Fonction de translitération
     */
    private BiFunction<String, String, String> transliterate;

    public SubfieldsFormatter(final String code, final CharConverter charConverter) {
        super(code, charConverter);
    }

    public String format(final DataField field) {
        if (field == null) {
            return null;
        }
        Stream<Subfield> subfields = field.getSubfields().stream();
        // Les sous-champs sont filtrés par rapport à la liste de codes filterCodes
        if (filterCodes.length > 0) {
            subfields = subfields.filter(sub -> ArrayUtils.contains(filterCodes, sub.getCode()));
        }
        // pas de config: on concatène tous les sous-champs en conservant leur ordre d'apparition
        if (formatCfg.isEmpty()) {
            final List<String> data = subfields.map(this::format).collect(Collectors.toList());
            return StringUtils.join(data, defaultSeparator);
        }
        // sinon on applique les règles
        else {
            final StringBuilder value = new StringBuilder();

            final SubfieldGroupConfig rootGroup = new SubfieldGroupConfig();
            rootGroup.setSubConfigs(formatCfg);
            rootGroup.apply(value, subfields.collect(Collectors.toList()));
            return value.toString();
        }
    }

    public String format(final Subfield subfield) {
        final String readValue = convert(subfield.getData());
        final String transType = transliterations.get(subfield.getCode());
        return transType != null && transliterate != null ? transliterate.apply(transType, readValue)
                                                          : readValue;
    }

    /**
     * Ajout d'un code sans préfixe
     *
     * @param code
     */
    public void add(final char code) {
        add(code, null);
    }

    /**
     * Ajout d'un couple préfixe + code
     *
     * @param code
     * @param prefix
     */
    public void add(final char code, final String prefix) {
        formatCfg.add(new SubfieldConfig(prefix, code, null));
    }

    /**
     * Ajout d'un couple préfixe + code
     *
     * @param code
     * @param prefix
     */
    public void add(final char code, final String prefix, final String suffix) {
        formatCfg.add(new SubfieldConfig(prefix, code, suffix));
    }

    /**
     * Ajout d'un groupe de sous-champs
     *
     * @param groupPrefix
     * @param groupSuffix
     * @param subfldCfg
     *            liste de préfixes et de codes
     */
    public void addGroup(final String groupPrefix, final String groupSuffix, String... subfldCfg) {
        final SubfieldGroupConfig group = new SubfieldGroupConfig(groupPrefix, groupSuffix);
        formatCfg.add(group);

        String prefix = null;
        for (final String cfg : subfldCfg) {
            final char c = cfg.charAt(0);

            // Ajout du code à la configuration du groupe
            if (Character.isLetterOrDigit(c)) {
                group.add(prefix, c);
                prefix = null;
            }
            // Sinon mise de côté du préfixe
            else {
                prefix = cfg;
            }
        }
    }

    public void setDefaultSeparator(final String defaultSeparator) {
        this.defaultSeparator = defaultSeparator;
    }

    public void setFilterCodes(final char[] filterCodes) {
        this.filterCodes = filterCodes;
    }

    public void addTransliteration(final char code, final String type) {
        this.transliterations.put(code, type);
    }

    public void setTransliterate(final BiFunction<String, String, String> transliterate) {
        this.transliterate = transliterate;
    }

    @Override
    public String[] getScriptImport() {
        return new String[] {"import org.marc4j.marc.DataField"};
    }

    @Override
    public String getConfigScript() {
        return "def " + SCRIPT_NAME
               + "Add = {\n"
               + "      String code, String prefix = null, String suffix = null -> script."
               + getCode()
               + ".add((char)code, prefix, suffix)\n"
               + "}\n"
               + "def "
               + SCRIPT_NAME
               + "AddGroup = {\n"
               + "      String prefix, String suffix, String... config -> script."
               + getCode()
               + ".addGroup(prefix, suffix, config)\n"
               + "}\n"
               + "def "
               + SCRIPT_NAME
               + "Filter = {\n"
               + "      String... tags -> script."
               + getCode()
               + ".setFilterCodes((char[])tags)\n"
               + "}\n"
               + "def "
               + SCRIPT_NAME
               + "Separator = {\n"
               + "      String separator -> script."
               + getCode()
               + ".setDefaultSeparator(separator)\n"
               + "}\n"
               + "def "
               + SCRIPT_NAME
               + "Transliterate = {\n"
               + "      String code, String type -> script."
               + getCode()
               + ".addTransliteration((char)code, type)\n"
               + "}\n";
    }

    @Override
    public String getInitScript() {
        return "def " + SCRIPT_NAME
               + " = {\n"
               + "      DataField field -> script."
               + getCode()
               + ".format(field)\n"
               + "}\n"
               + "if(binding.hasVariable('"
               + MarcMappingEvaluationService.BINDING_FN_TRANSLITERATE
               + "')) {\n"
               + "  script."
               + getCode()
               + ".setTransliterate("
               + MarcMappingEvaluationService.BINDING_FN_TRANSLITERATE
               + ".&getValue)\n"
               + "}\n";
    }

    @Override
    public String toString() {
        final SubfieldGroupConfig rootGroup = new SubfieldGroupConfig();
        rootGroup.setSubConfigs(formatCfg);
        return rootGroup.toString();
    }

    /**
     * Interface permettant de manipuler la configuration du formattage des sous-champs
     */
    private interface Config {

        /**
         * La configuration s'applique au code passé en paramètre
         *
         * @param code
         * @return
         */
        boolean match(char code);

        /**
         * Application de cette configuration de formattage, et ajout du résultat dans value
         *
         * @param value
         * @param subfields
         */
        void apply(StringBuilder value, List<Subfield> subfields);
    }

    /**
     * Élements de rendu d'un groupe de sous-champs
     */
    private final class SubfieldGroupConfig implements Config {

        private final String prefix;
        private final String suffix;
        private final List<Config> subConfigs = new ArrayList<>();

        private SubfieldGroupConfig() {
            this.prefix = null;
            this.suffix = null;
        }

        private SubfieldGroupConfig(final String prefix, final String suffix) {
            this.prefix = prefix;
            this.suffix = suffix;
        }

        public void setSubConfigs(final List<Config> subConfigs) {
            this.subConfigs.addAll(subConfigs);
        }

        public void add(final String prefix, final char code) {
            subConfigs.add(new SubfieldConfig(prefix, code, null));
        }

        @Override
        public boolean match(final char code) {
            return subConfigs.stream().anyMatch(s -> s.match(code));
        }

        @Override
        public void apply(final StringBuilder value, final List<Subfield> subfields) {
            // Config limitées à une seule application
            Set<Config> runOnce = new HashSet<>();
            final StringBuilder groupValue = new StringBuilder();

            subfields.forEach(subfield -> {
                // on récupère la config définie pour ce champ
                subConfigs.stream()
                          .filter(cfg -> cfg.match(subfield.getCode()))
                          .findAny()
                          // si il y en a une, on applique le formattage associé
                          .ifPresent(cfg -> {
                              // Sous-champs groupés
                              if (cfg instanceof SubfieldGroupConfig) {
                                  if (!runOnce.contains(cfg)) {
                                      runOnce.add(cfg);

                                      // Recherche tous les sous-champs faisant partie du groupe
                                      final List<Subfield> matchingSubfields = subfields.stream().filter(subfield2 -> cfg.match(subfield2.getCode())).collect(Collectors.toList());
                                      // Application des sous-config sur la liste des sous-champs correspodants
                                      cfg.apply(groupValue, matchingSubfields);
                                  }
                              }
                              // Sous-champ simple
                              else {
                                  cfg.apply(groupValue, Collections.singletonList(subfield));
                              }
                          });
            });
            if (groupValue.length() > 0) {
                if (value.length() > 0 && prefix != null) {
                    value.append(prefix);
                }
                value.append(groupValue);
                if (suffix != null) {
                    value.append(suffix);
                }
            }
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            if (prefix != null) {
                builder.append(prefix);
            }
            subConfigs.forEach(s -> builder.append(s.toString()));
            if (suffix != null) {
                builder.append(suffix);
            }
            return builder.toString();
        }
    }

    /**
     * Élements de rendu d'un sous-champ
     */
    private final class SubfieldConfig implements Config {

        private final String prefix;
        private final String suffix;
        private final char code;

        private SubfieldConfig(final String prefix, final char code, final String suffix) {
            this.prefix = prefix != null ? prefix
                                         : defaultSeparator;
            this.code = code;
            this.suffix = suffix;
        }

        @Override
        public boolean match(final char code) {
            return this.code == code;
        }

        @Override
        public void apply(final StringBuilder value, final List<Subfield> subfields) {
            boolean appendPrefix = value.length() > 0;

            for (final Subfield subfield : subfields) {
                if (appendPrefix) {
                    value.append(prefix);
                } else {
                    appendPrefix = true;
                }
                value.append(format(subfield));
                if (suffix != null) {
                    value.append(suffix);
                }
            }
        }

        @Override
        public String toString() {
            return prefix + code
                   + (suffix != null ? suffix
                                     : "");
        }
    }
}
