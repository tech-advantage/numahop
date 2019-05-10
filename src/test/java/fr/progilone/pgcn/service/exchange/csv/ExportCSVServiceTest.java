package fr.progilone.pgcn.service.exchange.csv;

import static com.opencsv.CSVWriter.RFC4180_LINE_END;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.output.WriterOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import com.opencsv.bean.CsvBindByName;

import fr.progilone.pgcn.exception.PgcnTechnicalException;

public class ExportCSVServiceTest {

    private ExportCSVService service;

    @Before
    public void setUp() {
        service = new ExportCSVService();
    }

    @Test
    public void testConvert() throws IOException, PgcnTechnicalException {
        final LocalDate dt = LocalDate.of(2018, 1, 1);
        final List<TestBean> beans = Arrays.asList(new TestBean("test-001", "bonne année", 14, dt),
                                                   new TestBean("test-002", "bonne galette", 6, dt.plusDays(5)));
        String actual;

        try (final StringWriter writer = new StringWriter(); final OutputStream out = new WriterOutputStream(writer, StandardCharsets.UTF_8)) {

            service.exportOrderedBeans(beans, out, StandardCharsets.UTF_8.name(), ',');

            writer.flush();
            actual = writer.getBuffer().toString();
        }
        
        final String expected = "\"1. IDENTIFIANT\",\"2. LIBELLÉ\",\"3. VALEUR\",\"4. DATE\""
                                + RFC4180_LINE_END
                                + "\"test-001\",\"bonne année\",\"14\",\"2018-01-01\""
                                + RFC4180_LINE_END
                                + "\"test-002\",\"bonne galette\",\"6\",\"2018-01-06\""
                                + RFC4180_LINE_END;
        assertEquals(expected, actual);
    }

    public static final class TestBean implements Comparable<TestBean> {

        @CsvBindByName(column = "1. Identifiant")
        private String identifier;
        @CsvBindByName(column = "2. Libellé")
        private String label;
        @CsvBindByName(column = "3. Valeur")
        private Integer value;
        @CsvBindByName(column = "4. Date")
        private LocalDate date;

        public TestBean(final String identifier, final String label, final Integer value, final LocalDate date) {
            this.identifier = identifier;
            this.label = label;
            this.value = value;
            this.date = date;
        }

        public String getIdentifier() {
            return identifier;
        }

        public void setIdentifier(final String identifier) {
            this.identifier = identifier;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(final String label) {
            this.label = label;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(final Integer value) {
            this.value = value;
        }

        public LocalDate getDate() {
            return date;
        }

        public void setDate(final LocalDate date) {
            this.date = date;
        }

        @Override
        public int compareTo(final TestBean o) {
            return StringUtils.compare(this.getLabel(), o.getLabel());
        }
    }
}
