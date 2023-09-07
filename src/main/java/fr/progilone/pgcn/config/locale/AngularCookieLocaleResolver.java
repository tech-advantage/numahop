package fr.progilone.pgcn.config.locale;

import fr.progilone.pgcn.config.AsyncConfiguration;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.util.WebUtils;

/**
 * Angular cookie saved the locale with a double quote (%22en%22).
 * So the default CookieLocaleResolver#StringUtils.parseLocaleString(localePart)
 * is not able to parse the locale.
 *
 * This class will check if a double quote has been added, if so it will remove it.
 */
public class AngularCookieLocaleResolver extends CookieLocaleResolver {

    private static final Logger LOG = LoggerFactory.getLogger(AsyncConfiguration.class);

    /**
     * Constant <code>QUOTE="%22"</code>
     */
    public static final String QUOTE = "%22";

    /**
     * {@inheritDoc}
     */
    @Override
    public Locale resolveLocale(final HttpServletRequest request) {
        parseAngularCookieIfNecessary(request);
        return (Locale) request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocaleContext resolveLocaleContext(final HttpServletRequest request) {
        parseAngularCookieIfNecessary(request);
        return new TimeZoneAwareLocaleContext() {

            @Override
            public Locale getLocale() {
                return (Locale) request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME);
            }

            @Override
            public TimeZone getTimeZone() {
                return (TimeZone) request.getAttribute(TIME_ZONE_REQUEST_ATTRIBUTE_NAME);
            }
        };
    }

    private void parseAngularCookieIfNecessary(final HttpServletRequest request) {
        if (request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME) == null) {
            // Retrieve and parse cookie value.
            final Cookie cookie = WebUtils.getCookie(request, DEFAULT_COOKIE_NAME);
            Locale locale = null;
            TimeZone timeZone = null;
            if (cookie != null) {
                String value = cookie.getValue();

                // Remove the double quote
                value = StringUtils.replace(value, QUOTE, "");

                String localePart = value;
                String timeZonePart = null;
                final int spaceIndex = localePart.indexOf(' ');
                if (spaceIndex != -1) {
                    localePart = value.substring(0, spaceIndex);
                    timeZonePart = value.substring(spaceIndex + 1);
                }
                locale = !"-".equals(localePart) ? StringUtils.parseLocaleString(localePart.replace('-', '_'))
                                                 : null;
                if (timeZonePart != null) {
                    timeZone = StringUtils.parseTimeZoneString(timeZonePart);
                }
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Parsed cookie value [" + cookie.getValue()
                              + "] into locale '"
                              + locale
                              + "'"
                              + (timeZone != null ? " and time zone '" + timeZone.getID()
                                                    + "'"
                                                  : ""));
                }
            }
            request.setAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME,
                                 locale != null ? locale
                                                : determineDefaultLocale(request));

            request.setAttribute(TIME_ZONE_REQUEST_ATTRIBUTE_NAME,
                                 timeZone != null ? timeZone
                                                  : determineDefaultTimeZone(request));
        }
    }

    String quote(final String string) {
        return QUOTE + string
               + QUOTE;
    }
}
