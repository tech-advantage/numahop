package fr.progilone.pgcn.service.exchange.csv;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.List;

import static com.opencsv.CSVWriter.*;

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

            } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
                throw new PgcnTechnicalException(e.getMessage());
            }
        }
    }
}
