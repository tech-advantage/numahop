package fr.progilone.pgcn.service.util;

import com.fasterxml.jackson.databind.util.ISO8601Utils;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe pour convertir des dates java.util.date en date java.time (java 8) et inversement
 */
public final class DateUtils {

    private static final Logger LOG = LoggerFactory.getLogger(DateUtils.class);

    private static final Separator[] dateSeparators = {new Separator("/"),
                                                       new Separator("-"),
                                                       new Separator(".", "\\."),
                                                       new Separator(" "),
                                                       new Separator("")};
    private static final String DAY_PATTERN = "([0][1-9]|[1-2][0-9]|[3][0-1])";
    private static final String MONTH_PATTERN = "([0][1-9]|[1][0-2])";

    private DateUtils() {
    }

    /**
     * @param date
     *            une date de type java.time.LocalDate
     * @return Retourne une date de type java.util.Date
     */
    public static Date convertToDate(final LocalDate date) {
        if (date == null) {
            return null;
        }
        return Date.from(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * @param datetime
     *            une date de type java.time.LocalDate
     * @return Retourne une date de type java.util.Date
     */
    public static Date convertToDate(final LocalDateTime datetime) {
        if (datetime == null) {
            return null;
        }
        return Date.from(datetime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * @param date
     *            une date de type java.util.Date
     * @return Retourne une date de type java.time.LocalDate
     */
    public static LocalDate convertToLocalDate(final Date date) {
        if (date == null) {
            return null;
        }
        if (date instanceof java.sql.Date) {
            return ((java.sql.Date) date).toLocalDate();
        } else {
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
    }

    /**
     * @param date
     *            une date de type java.util.Date
     * @return Retourne une date de type java.time.LocalDateTime
     */
    public static LocalDateTime convertToLocalDateTime(final Date date) {
        if (date == null) {
            return null;
        }
        final Instant instant = Instant.ofEpochMilli(date.getTime());
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    /**
     * Calcule la durée entre 2 dates, avec l'unité précisée
     *
     * @param start
     * @param end
     * @param unit
     *            (valeur par défaut ChronoUnit.DAYS)
     * @return long
     */
    public static long getDuration(final Date start, final Date end, TemporalUnit unit) {
        if (start == null || end == null) {
            return 0L;
        }
        if (unit == null) {
            unit = ChronoUnit.DAYS;
        }
        final LocalDateTime lStart = start.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        final LocalDateTime lEnd = end.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return lStart.until(lEnd, unit);
    }

    /**
     * Vérifie si les intervalles [start1, end1] et [start2, end2] se chevauchent<br/>
     * Fonctionne pour les intervalles non bornés
     *
     * @param start1
     * @param end1
     * @param start2
     * @param end2
     * @return boolean
     */
    public static boolean checkOverlap(final Date start1, final Date end1, final Date start2, final Date end2) {
        boolean check = true;
        if (start1 != null && end2 != null) {
            check = start1.before(end2);
        }
        if (end1 != null && start2 != null) {
            check = check && end1.after(start2);
        }
        return check;
    }

    /**
     * Vérifie si une date est contenue dans [start, end[
     *
     * @param date
     *            la date
     * @param start
     *            borne inférieure, incluse
     * @param end
     *            borne supérieure, exclue
     * @return boolean
     */
    public static boolean isBetween(final Date date, final Date start, final Date end, final boolean includeStart, final boolean includeEnd) {
        if (date == null) {
            return false;
        } else {
            boolean check = true;

            if (start != null) {
                check = includeStart ? !start.after(date)
                                     : start.before(date);
            }
            if (end != null) {
                check = check && (includeEnd ? !end.before(date)
                                             : end.after(date));
            }
            return check;
        }
    }

    /**
     * Vérifie si une date est contenue dans ]start, end[ ou dans [start, end]
     *
     * @param date
     *            date, null retourne false
     * @param start
     *            borne inférieure, peut être null, considéré comme -infini
     * @param end
     *            borne supérieure, peut être null, considéré comme +infini
     * @param inclusive
     *            si les bornes sont considérés comme valables, ie l'intervalle est [start, end] et non plus ]start, end[
     * @return
     */
    public static boolean isBetween(final LocalDate date, LocalDate start, LocalDate end, final boolean inclusive) {
        if (date == null) {
            return false;
        }
        if (start == null) {
            start = LocalDate.MIN;
        }
        if (end == null) {
            end = LocalDate.MAX;
        }
        return inclusive ? !date.isBefore(start) && !date.isAfter(end)
                         : date.isAfter(start) && date.isBefore(end);
    }

    /**
     * Vérifie si une datetime est contenue dans ]start, end[ ou dans [start, end]
     *
     * @param datetime
     *            date, null retourne false
     * @param start
     *            borne inférieure, peut être null, considéré comme -infini
     * @param end
     *            borne supérieure, peut être null, considéré comme +infini
     * @param inclusive
     *            si les bornes sont considérés comme valables, ie l'intervalle est [start, end] et non plus ]start, end[
     * @return
     */
    public static boolean isBetween(final LocalDateTime datetime, LocalDateTime start, LocalDateTime end, final boolean inclusive) {
        if (datetime == null) {
            return false;
        }
        if (start == null) {
            start = LocalDateTime.MIN;
        }
        if (end == null) {
            end = LocalDateTime.MAX;
        }
        return inclusive ? !datetime.isBefore(start) && !datetime.isAfter(end)
                         : datetime.isAfter(start) && datetime.isBefore(end);
    }

    /**
     * Formattage d'une {@link Date}
     *
     * @param date
     * @param format
     * @return
     * @see SimpleDateFormat
     */
    public static String formatDateToString(final Date date, final String format) {
        if (date == null) {
            return null;
        } else {
            return new SimpleDateFormat(format).format(date);
        }
    }

    /**
     * Formattage d'une {@link LocalDate}
     *
     * @param date
     * @param format
     * @return
     * @see DateTimeFormatter
     */
    public static String formatDateToString(final LocalDate date, final String format) {
        if (date == null) {
            return null;
        } else {
            return DateTimeFormatter.ofPattern(format).format(date);
        }
    }

    /**
     * Formattage d'une {@link LocalDate}
     */
    public static String formatDateToIsoString(final TemporalAccessor date) {
        if (date == null) {
            return null;
        } else {
            return DateTimeFormatter.ISO_DATE.format(date);
        }
    }

    /**
     * Formattage d'une {@link LocalDateTime}
     *
     * @param time
     * @param format
     * @return
     * @see DateTimeFormatter
     */
    public static String formatDateToString(final LocalDateTime time, final String format) {
        if (time == null) {
            return null;
        } else {
            return DateTimeFormatter.ofPattern(format).format(time);
        }
    }

    /**
     * Parsing d'une {@link String} en {@link Date}
     *
     * @param text
     * @param format
     * @return
     */
    public static Date parseStringToDate(final String text, final String format) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        try {
            return new SimpleDateFormat(format).parse(text);
        } catch (final ParseException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Parsing d'une {@link String} en {@link LocalDate}
     *
     * @param text
     * @param format
     * @return
     */
    public static LocalDate parseStringToLocalDate(final String text, final String format) {
        if (StringUtils.isBlank(text) || StringUtils.isBlank(format)) {
            return null;
        }
        try {
            return LocalDate.parse(text, DateTimeFormatter.ofPattern(format));

        } catch (final DateTimeParseException e) {
            LOG.warn("texte: {}, format: {}, erreur: {}", text, format.toString(), e.getMessage());
            return null;
        }
    }

    /**
     * Parsing d'une {@link String} en {@link LocalDate}
     *
     * @param text
     * @return
     */
    public static LocalDate parseStringToLocalDate(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        text = text.trim();

        final DateAndFormat dateAndFormat = getDateFormat(text);
        if (dateAndFormat != null) {
            return parseStringToLocalDate(dateAndFormat.date, dateAndFormat.format);

        } else {
            return null;
        }
    }

    private static DateAndFormat getDateFormat(final String date) {
        for (final Separator sep : dateSeparators) {
            for (final DateFormat df : DateFormat.values()) {
                if (date.matches(replaceSeparator(df.template, sep.regexSeparator))) {
                    return new DateAndFormat(replaceSeparator(date + df.complement, sep.dateSeparator), replaceSeparator(df.format, sep.dateSeparator));
                }
            }
        }
        return null;
    }

    private static String replaceSeparator(final String template, final String sep) {
        return template.replace("{sep}", sep);
    }

    /**
     * Parsing d'une {@link String} en {@link LocalDateTime}
     *
     * @param text
     * @param format
     * @return
     */
    public static LocalDateTime parseStringToLocalDateTime(final String text, final String format) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        try {
            return LocalDateTime.parse(text, DateTimeFormatter.ofPattern(format));
        } catch (final DateTimeParseException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    public static boolean isISO8601Compliant(final String date) {
        if (StringUtils.isBlank(date)) {
            return false;
        }
        // On accepte l'année sur 4 chiffres.
        if (date.length() == 4) {
            try {
                final Integer i = Integer.valueOf(date);
            } catch (final NumberFormatException e) {
                return false;
            }
            return true;
        }

        final ParsePosition parsePos = new ParsePosition(0);
        try {
            ISO8601Utils.parse(date, parsePos);
        } catch (final ParseException e) {
            LOG.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    /**
     * Retourne true si les deux dates sont identiques
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean sameDate(final Date date1, final Date date2) {
        return Objects.equals(convertToLocalDate(date1), convertToLocalDate(date2));
    }

    /**
     * Différents formats de dates à reconnaitre. Attention l'ordre est important car on les identifie dans cet ordre, si une date peut matcher 2
     * pattern (ex 20121606 qui peut être 20/12/1606 ou 16/06/2012), l'ordre permet de savoir quel pattern sera choisi en premier (en l'occurence ça
     * sera 16/06/2012)
     */
    private enum DateFormat {

        /**
         * YYYY/MM/DD
         */
        YMD("yyyy{sep}MM{sep}dd",
            "\\d{4}{sep}" + MONTH_PATTERN
                                  + "{sep}"
                                  + DAY_PATTERN,
            ""),
        /**
         * DD/MM/YYYY
         */
        DMY("dd{sep}MM{sep}yyyy",
            DAY_PATTERN + "{sep}"
                                  + MONTH_PATTERN
                                  + "{sep}\\d{4}",
            ""),
        /**
         * YYYY/DD/MM
         */
        YDM("yyyy{sep}dd{sep}MM",
            "\\d{4}{sep}" + DAY_PATTERN
                                  + "{sep}"
                                  + MONTH_PATTERN,
            ""),
        /**
         * YYYY/MM
         */
        YM("yyyy{sep}MM{sep}dd", "\\d{4}{sep}" + MONTH_PATTERN, "{sep}01"),
        /**
         * YYYY
         */
        Y("yyyy{sep}MM{sep}dd", "\\d{4}", "{sep}01{sep}01"),
        /**
         * dd MMMM yyyy
         */
        DMY_LETTER("dd{sep}MMMM{sep}yyyy", DAY_PATTERN + "{sep}\\w*{sep}\\d{4}", "");

        private String format;
        private String template;
        private String complement;

        DateFormat(final String format, final String template, final String complement) {
            this.format = format;
            this.template = template;
            this.complement = complement;
        }
    }

    private static final class DateAndFormat {

        private final String date;
        private final String format;

        public DateAndFormat(final String date, final String format) {
            this.date = date;
            this.format = format;
        }
    }

    private static final class Separator {

        private final String dateSeparator;
        private final String regexSeparator;

        public Separator(final String dateSeparator, final String regexSeparator) {
            this.dateSeparator = dateSeparator;
            this.regexSeparator = regexSeparator;
        }

        public Separator(final String dateSeparator) {
            this.dateSeparator = dateSeparator;
            this.regexSeparator = dateSeparator;
        }
    }

}
