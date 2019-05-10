package fr.progilone.pgcn.domain.jaxb.adapters;

import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Adapter LocalDateTime pour JAXB
 */
public class LocalDateTimeXmlAdapter extends XmlAdapter<String, LocalDateTime> {

    @Override
    public LocalDateTime unmarshal(final String text) throws Exception {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        return LocalDateTime.parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @Override
    public String marshal(final LocalDateTime datetime) throws Exception {
        if (datetime == null) {
            return null;
        }
        return DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(datetime);
    }
}
