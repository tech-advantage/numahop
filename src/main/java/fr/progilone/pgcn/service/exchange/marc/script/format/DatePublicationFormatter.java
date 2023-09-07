package fr.progilone.pgcn.service.exchange.marc.script.format;

import fr.progilone.pgcn.service.exchange.marc.script.CustomScript;
import java.util.Optional;
import org.marc4j.converter.CharConverter;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Subfield;

/**
 * Formatter pour la date de publication du 100$a
 *
 * @author jbrunet
 *         Créé le 20 févr. 2017
 */
public class DatePublicationFormatter extends CustomScript {

    public static final String SCRIPT_NAME = "datepublication";
    private static final String SEPARATOR_ISO8601_YEAR = "/";
    private static final String SEPARATOR_ISO8601_DATE = "-";
    private static final String SEPARATOR_OTHER_DATE = "|";

    private int startPosition = 9;
    private int endPosition = 16;
    private char subfield = 'a';
    private int typePosition = 8;

    public DatePublicationFormatter(final String code, final CharConverter charConverter) {
        super(code, charConverter);
    }

    @Override
    public String[] getScriptImport() {
        return new String[] {"import org.marc4j.marc.DataField"};
    }

    @Override
    public String getConfigScript() {
        return "def " + SCRIPT_NAME
               + "Filter = {\n"
               + "      String code -> script."
               + getCode()
               + ".setSubfield((char)code)\n"
               + "}\n"
               + "def "
               + SCRIPT_NAME
               + "PositionDebut = {\n"
               + "      int start -> script."
               + getCode()
               + ".setDateStartPosition(start)\n"
               + "}\n"
               + "def "
               + SCRIPT_NAME
               + "PositionFin = {\n"
               + "      int end -> script."
               + getCode()
               + ".setDateEndPosition(end)\n"
               + "}\n"
               + "def "
               + SCRIPT_NAME
               + "PositionTypeDate = {\n"
               + "      int position -> script."
               + getCode()
               + ".setDateTypePosition(position)\n"
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

    /**
     * Sélection du sous-champ (défaut a)
     *
     * @param subfield
     */
    public void setSubfield(char subfield) {
        this.subfield = subfield;
    }

    /**
     * Sélection de la position de départ de la date
     *
     * @param start
     */
    public void setDateStartPosition(int start) {
        this.startPosition = start;
    }

    /**
     * Position de fin de la date
     *
     * @param end
     */
    public void setDateEndPosition(int end) {
        this.endPosition = end;
    }

    public void setDateTypePosition(int type) {
        this.typePosition = type;
    }

    /**
     * Formattage du field au format ISO 8601 approché
     * La seule différence avec le format ISO est la possibilité
     * de XX pour les données inconnues
     * Ex: 198X pour une année entre 1980 et 1989
     *
     * @param field
     * @return
     */
    public String format(final DataField field) {
        if (field == null)
            return null;
        // On récupère le champ qui nous intéresse
        Optional<Subfield> subfieldForDate = field.getSubfields().stream().filter(sub -> subfield == sub.getCode()).findFirst();
        if (!subfieldForDate.isPresent())
            return null;
        Subfield subFieldForDatePub = subfieldForDate.get();
        String baseDateValueData = subFieldForDatePub.getData();
        // On vérifie que la longeur de la chaîne récupérée est au moins égale à la position de fin attendue
        if (baseDateValueData == null || baseDateValueData.length() <= endPosition)
            return null;
        // le type de publication doit aussi faire partie de la chaîne
        if (baseDateValueData.length() <= typePosition)
            return null;
        // Tout est bon, on peut récupérer les informations pour la conversion
        char typeDate = baseDateValueData.charAt(typePosition);
        // Remplacement des espaces et autres vides
        baseDateValueData = baseDateValueData.replaceAll("[ #]", "X");
        // Génération de la date dans un format IS0 approché
        StringBuilder sb = new StringBuilder();
        switch (typeDate) {
            case 'g':
            case 'f':
            case 'b':
            case 'a':
                sb.append(baseDateValueData.substring(startPosition, startPosition + 4))
                  .append(SEPARATOR_ISO8601_YEAR)
                  .append(baseDateValueData.substring(startPosition + 4, endPosition + 1));
                break;
            // monographie date publication et impression
            case 'k':
                // monographie avec date édition/diffusion et production : choix de l'édition
            case 'i':
                // monographie avec date publication et copyright : choix de la date de publication
            case 'h':
                // reproduction : date de la reproduction
            case 'e':
                sb.append(baseDateValueData.substring(startPosition, startPosition + 4))
                  .append(SEPARATOR_OTHER_DATE)
                  .append(baseDateValueData.substring(startPosition + 4, endPosition + 1));
                break;
            // Blancs sur les positions suivantes
            case 'd':
            case 'c':
                sb.append(baseDateValueData.substring(startPosition, startPosition + 4));
                break;
            case 'j':
                sb.append(baseDateValueData.substring(startPosition, startPosition + 4))
                  .append(SEPARATOR_ISO8601_DATE)
                  .append(baseDateValueData.substring(startPosition + 4, startPosition + 6))
                  .append(SEPARATOR_ISO8601_DATE)
                  .append(baseDateValueData.substring(startPosition + 6, endPosition + 1));
                break;
            case 'u':
                sb.append("s.d.");
                break;
            default:
                return null;
        }
        return sb.toString();
    }
}
