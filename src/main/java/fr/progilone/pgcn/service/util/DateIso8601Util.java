package fr.progilone.pgcn.service.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Retranscrit une chaîne spécifique ISO 8601 approchant
 * en ISO 8601 valide.
 *
 * Ex : 185X 186X -> 1850 / 1869
 * Ex : 19XX -> 1900
 * Ex : 23/10/2017 -> 2017-10-23
 *
 * @author jbrunet
 *         Créé le 20 févr. 2017
 */
public final class DateIso8601Util {

    private static final Logger LOG = LoggerFactory.getLogger(DateIso8601Util.class);

    private static final String[] intervalSeparators = {"/",
                                                        "-",
                                                        " "};

    private static final String ISO_8601_DT_PATTERN = "yyyy-MM-dd";
    private static final String ISO_8601_YEAR_PATTERN = "yyyy";

    private DateIso8601Util() {
    }

    /**
     * Transformation d'une date au format pseudo ISO-8601
     * issue de l'importation vers une date au format ISO-8601 valide
     *
     * @param date
     * @return
     */
    public static String importedDateToIso8601(final String date) {
        final StringBuilder sb = new StringBuilder();

        if (StringUtils.isBlank(date) || date.toUpperCase().contains("XXX")) {
            LOG.trace("Erreur : la date '{}' est invalide.", date);
            return null;
        }

        final String[] dtInterval = DateIso8601Util.splitDatesInterval(date);
        if (ArrayUtils.isEmpty(dtInterval)) {
            return null;
        }

        if (dtInterval.length == 2 && dtInterval[1] != null) {
            sb.append(dtInterval[0]).append("/").append(dtInterval[1]);
        } else {
            sb.append(dtInterval[0]);
        }

        return sb.toString();
    }

    /**
     *
     *
     * @param toTest
     * @return
     */
    public static String[] splitDatesInterval(final String toTest) {

        final String[] splitDates = new String[2];

        for (final String sep : intervalSeparators) {

            final String[] splitted = StringUtils.split(toTest, sep);
            if (splitted == null || splitted.length > 2) {
                continue;

            } else if (splitted.length == 1) {
                // pas d'intervalle : 1 date à traiter
                splitted[0] = DateIso8601Util.detectReplaceGenerics(splitted[0], true);

                if (splitted[0].length() == 2) {
                    // Juste le siecle - pas de transformation
                    splitDates[0] = splitted[0];
                    break;

                } else {
                    final LocalDate dt = DateUtils.parseStringToLocalDate(splitted[0]);
                    String pattern = ISO_8601_DT_PATTERN;
                    if (splitted[0].length() == 4) {
                        pattern = ISO_8601_YEAR_PATTERN;
                    }
                    if (dt != null) {
                        final String s = dt.format(DateTimeFormatter.ofPattern(pattern));
                        if (DateUtils.isISO8601Compliant(s)) {
                            splitDates[0] = s;
                        } else {
                            splitDates[0] = null;
                        }
                    } else {
                        splitDates[0] = null;
                    }
                }

                // on continue desfois que...
                continue;

            } else {
                // Intervalle : ça peut etre ok, il faut tester la cohérence des 2 chaines
                splitted[0] = DateIso8601Util.detectReplaceGenerics(splitted[0], true);
                splitted[1] = DateIso8601Util.detectReplaceGenerics(splitted[1], false);

                if (splitted[0].length() == 2 && splitted[1].length() == 2) {
                    // Juste le siecle...
                    splitDates[0] = splitted[0];
                    splitDates[1] = splitted[1];
                    break;

                } else {
                    final LocalDate dt1 = DateUtils.parseStringToLocalDate(splitted[0]);
                    final LocalDate dt2 = DateUtils.parseStringToLocalDate(splitted[1]);
                    String pattern = ISO_8601_DT_PATTERN;

                    if (dt1 != null) {
                        if (splitted[0].length() == 4) {
                            pattern = ISO_8601_YEAR_PATTERN;
                        }
                        final String s1 = DateUtils.formatDateToString(dt1, pattern);
                        if (DateUtils.isISO8601Compliant(s1)) {
                            splitDates[0] = s1;
                        } else {
                            splitDates[0] = null;
                        }
                    }

                    if (dt2 != null) {
                        if (splitted[1].length() == 4) {
                            pattern = ISO_8601_YEAR_PATTERN;
                        }
                        final String s2 = DateUtils.formatDateToString(dt2, pattern);
                        if (DateUtils.isISO8601Compliant(s2)) {
                            splitDates[1] = s2;
                        } else {
                            splitDates[1] = null;
                        }
                    }

                    if (splitDates[0] != null && splitDates[1] != null) {
                        // à priori on est pas mal...
                        break;
                    }
                }

            }

        }
        if (splitDates[0] == null && splitDates[1] == null) {
            return null;
        } else {
            return splitDates;
        }
    }

    public static String detectReplaceGenerics(final String input, final boolean startDate) {

        String strDate = input.toUpperCase();
        if (startDate) {
            if (strDate.contains("XX")) {
                strDate = strDate.replace("XX", "");
            } else if (strDate.contains("X")) {
                strDate = strDate.replace('X', '0');
            }
        } else {
            if (strDate.contains("XX")) {
                strDate = strDate.replace("XX", "");
            } else if (strDate.contains("X")) {
                strDate = strDate.replace('X', '9');
            }
        }
        return strDate;
    }

}
