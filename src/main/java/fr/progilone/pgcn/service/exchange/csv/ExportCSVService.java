package fr.progilone.pgcn.service.exchange.csv;

import static com.opencsv.CSVWriter.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import fr.progilone.pgcn.exception.PgcnTechnicalException;

@Service
public class ExportCSVService {

    /**
     * Export d'une liste de bean dans un flux de sortie CSV
     *
     * @param beans
     * @param out
     * @param encoding
     * @param separator
     * @param <T>
     * @throws IOException
     * @throws PgcnTechnicalException
     */
    public <T extends Comparable<T>> void exportOrderedBeans(final List<T> beans, final OutputStream out, final String encoding, final char separator) throws
                                                                                                                                                IOException,
                                                                                                                                                PgcnTechnicalException {
        Collections.sort(beans);

        try (final Writer writer = new OutputStreamWriter(out, encoding)) {
            final StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(writer).withSeparator(separator)
                                                                                          .withQuotechar(DEFAULT_QUOTE_CHARACTER)
                                                                                          .withEscapechar(DEFAULT_ESCAPE_CHARACTER)
                                                                                          .withLineEnd(RFC4180_LINE_END)
                                                                                          .withOrderedResults(true)
                                                                                          .build();
            try {
                beanToCsv.write(beans);

            } catch (final CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
                throw new PgcnTechnicalException(e.getMessage());
            }
        }
    }

    public String getFormatedValues(final List<String> values, final String emptyFieldValue, final String csvRepeatedFieldSep) throws IOException {

        if (values == null || values.isEmpty()) {
            return emptyFieldValue;
        }

        final StringBuilder sb = new StringBuilder();
        values.forEach(val -> {
            sb.append(deleteUnwantedCrLf(val))
              .append(csvRepeatedFieldSep);
        });
        final String aggreg = sb.toString();
        if (aggreg.endsWith(csvRepeatedFieldSep)) {
            return aggreg.substring(0, aggreg.length() - 1);
        } else {
            return aggreg;
        }
    }

    /**
     * tentative de fix pb saut de ligne
     * notamment pour qq caracteres arabes entre crochets !!
     *
     * @param value
     * @return
     */
    private String deleteUnwantedCrLf(final String value) {
        return (value.replace("\n", " ")).replace("\r", "");
    }
}
