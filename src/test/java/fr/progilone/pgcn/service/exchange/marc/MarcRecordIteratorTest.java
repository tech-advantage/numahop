package fr.progilone.pgcn.service.exchange.marc;

import org.junit.Assert;
import org.junit.Test;
import org.marc4j.MarcReader;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by Sebastien on 23/11/2016.
 */
public class MarcRecordIteratorTest {

    @Test
    public void test() {
        final String[] values = {"progilone", "lyon", "anaconda", "poney", "caillou"};
        final MarcReader testReader = new MarcReaderTest(values);
        final Iterator<Record> iterator = new MarcRecordIterator(testReader);

        int idx = 0;
        while (iterator.hasNext()) {
            final Record record = iterator.next();

            final String data = ((ControlField) record.getVariableField("001")).getData();
            Assert.assertEquals(values[idx], data);

            idx++;
        }
        Assert.assertEquals(values.length, idx);
    }

    private static final class MarcReaderTest implements MarcReader {

        private static final MarcFactory MARC_FACTORY = MarcFactory.newInstance();

        private final Iterator<String> iterator;

        private MarcReaderTest(final String... identifiers) {
            this.iterator = Arrays.asList(identifiers).iterator();
        }

        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        @Override
        public Record next() {
            final String identifier = this.iterator.next();
            Record record = MARC_FACTORY.newRecord();
            record.addVariableField(MARC_FACTORY.newControlField("001", identifier));
            return record;
        }
    }
}
