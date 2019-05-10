package fr.progilone.pgcn.domain.dto.check;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.progilone.pgcn.exception.PgcnTechnicalException;

/**
 * Découpage des noms de fichiers pour les contrôles
 *
 * @author jbrunet
 * Créé le 9 mars 2017
 */
public class SplitFilename {

    private String library;
    private String prefix;
    private Integer number;
    private String extension;

    public SplitFilename(final String library, final String prefix, final Integer number, final String extension) {
        this.library = library;
        this.prefix = prefix;
        this.number = number;
        this.extension = extension;
    }

    public String getLibrary() {
        return library;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getExtension() {
        return extension;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(final Integer number) {
        this.number = number;
    }

    /**
     * Sépare une chaîne de type BIBLIOTHEQUE_RAD1_RAD2_RADN_315.jpg
     * en un SplitFilename
     *
     * <p>Ex : sc_002156_04564_0646_view_001_L_M.jpg</p>
     * <p><b>result : </b>
     * <ul>
     * <li>library : sc</li>
     * <li>prefix : 002156_04564_0646_view</li>
     * <li>number : 1</li>
     * <li>extension : jpg</li>
     * </ul>
     * </p>
     *
     * @param filename
     * @param splitNames
     *         : map remplie sous la forme filename => SplitFilename
     * @return
     */
    public static SplitFilename split(final String filename,
                                      final Map<String, Optional<SplitFilename>> splitNames,
                                      final boolean bibPrefixMandatory,
                                      final String seqSeparator,
                                      final boolean isPdfDelivery) throws PgcnTechnicalException {
        // check cache
        if (filename == null) {
            throw new PgcnTechnicalException("[Livraison] Nom de fichier nul");
        }
        
        final Optional<SplitFilename> cache = splitNames.get(filename);
        if (cache != null) {
            if (cache.isPresent()) {
                // on l'a déjà, donc pas besoin d'aller plus loin !
                return cache.get();
            } else {
                throw new PgcnTechnicalException("[Livraison] Nom de fichier mal construit : " + filename);
            }
        }
        // check point (il faut au moins un ".")
        final String[] nameAndExtension = filename.split("\\.");
        if (nameAndExtension.length < 2) {
            splitNames.put(filename, Optional.empty());
            throw new PgcnTechnicalException("[Livraison] Pas d'extension : " + filename);
        }
        // !! on peut avoir plusieurs points.
        final StringBuilder rebuildedName = new StringBuilder();
        for (int i = 0; i < nameAndExtension.length - 1; i++) {
            if (i == 0) {
                rebuildedName.append(nameAndExtension[i]);
            } else {
                rebuildedName.append(".").append(nameAndExtension[i]);
            }
        }

        // check separator
        final String seqSep = Pattern.quote(seqSeparator);
        final String[] splitName = rebuildedName.toString().split(seqSep);
        final int minNbParts = bibPrefixMandatory ? 3 : 2;
        if (splitName.length < minNbParts) {
            splitNames.put(filename, Optional.empty());
            throw new PgcnTechnicalException("[Livraison] Mauvais format de nom de fichier : " + filename);
        }

        int number = -1;
        final String prefix;
        if (isPdfDelivery) {
            // pas de sequence sur 1 pdf à priori.
            prefix = String.join(seqSeparator, Arrays.copyOfRange(splitName, 1, splitName.length));
            number = 0;
        } else {
            prefix = String.join(seqSeparator, Arrays.copyOfRange(splitName, 1, splitName.length - 1));
            // check file sequence number
            number = -1;
            for (int position = splitName.length - 1; position > 0; position--) {
                if (StringUtils.isNumeric(splitName[position])) {
                    number = NumberUtils.toInt(splitName[position]);
                    break;
                }
            }
            if (number < 0) {
                splitNames.put(filename, Optional.empty());
                throw new PgcnTechnicalException("[Livraison] Impossible de trouver le numéro de séquence : " + filename);
            }
        }

        final SplitFilename splitFilename = new SplitFilename(splitName[0], prefix, number, nameAndExtension[nameAndExtension.length - 1]);
        splitNames.put(filename, Optional.of(splitFilename));
        return splitFilename;
    }
}
