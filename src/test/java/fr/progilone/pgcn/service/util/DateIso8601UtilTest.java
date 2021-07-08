package fr.progilone.pgcn.service.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import fr.progilone.pgcn.exception.PgcnTechnicalException;

/**
 * Test des conversions vers ISO-8601
 * @author jbrunet
 * Créé le 20 févr. 2017
 */
public class DateIso8601UtilTest {

    @Test
    public void validDate() throws PgcnTechnicalException {
        // Create
        final String validDate = "1850 1856";
        // Convert
        final String converted = DateIso8601Util.importedDateToIso8601(validDate);
        // Check
        assertEquals("1850/1856", converted);
    }
    
    @Test
    public void validDate2() throws PgcnTechnicalException {
        // Create
        final String validDate = "185X-1856";
        // Convert
        final String converted = DateIso8601Util.importedDateToIso8601(validDate);
        // Check
        assertEquals("1850/1856", converted);
    }
    
    @Test
    public void validDate3() throws PgcnTechnicalException {
        // Create
        final String validDate = "185X/1856";
        // Convert
        final String converted = DateIso8601Util.importedDateToIso8601(validDate);
        // Check
        assertEquals("1850/1856", converted);
    }
    
    @Test
    public void validDate4() throws PgcnTechnicalException {
        // Create
        final String invalidDate = "18XX|1856";
        final String validDate = "18XX-19";
        // Convert
        String converted = DateIso8601Util.importedDateToIso8601(invalidDate);
        assertNull(converted);
        converted = DateIso8601Util.importedDateToIso8601(validDate);
        assertEquals("18/19", converted);
    }
    
    @Test
    public void validDate5() throws PgcnTechnicalException {
        // Create
        final String validDate = "1859-06-25";
        // Convert
        final String converted = DateIso8601Util.importedDateToIso8601(validDate);
        // Check
        assertEquals("1859-06-25", converted);
    }
    
    @Test
    public void failDate() throws PgcnTechnicalException {
        // Create
        final String failDate = "1859--06";
        final String validDate = "1859-06";
        // Convert
        String converted = DateIso8601Util.importedDateToIso8601(failDate);
        // Check
        assertNull(converted);
        converted = DateIso8601Util.importedDateToIso8601(validDate);
        assertEquals("1859-06-01", converted);
    }
    
    @Test
    public void failDate2() throws PgcnTechnicalException {
        // Create
        final String failDate = "18XX:1989";
        // Convert
        final String converted = DateIso8601Util.importedDateToIso8601(failDate);
        // Check
        assertNull(converted);
    }
    
    @Test
    public void failDate3() throws PgcnTechnicalException {
        // Create
        final String failDate = "1XXX";
        // Convert
        final String converted = DateIso8601Util.importedDateToIso8601(failDate);
        // Check
        assertNull(converted);
    }
    
    @Test
    public void splitDatesInterval() throws PgcnTechnicalException {
        final String date_valid = "12/03/2017";
        final String dates_valid = "15-06-2018 30/06/2019";
        final String dates_invalid = "15/25/2018 40/2a/6666";
        final String dates_valid_annee = "06/06/1940 2015";
        final String date_mini = "19xx";
        final String dates_mini_ok = "156x-188x";
        final String dates_mini_ko = "15x-188xx";
        
        String result = DateIso8601Util.importedDateToIso8601(date_valid);
        assertNotNull(result);
        assertEquals("2017-03-12", result);
        
        result = DateIso8601Util.importedDateToIso8601(dates_valid);
        assertEquals("2018-06-15/2019-06-30", result);
        result = DateIso8601Util.importedDateToIso8601(dates_invalid);
        assertNull(result);
        result = DateIso8601Util.importedDateToIso8601(dates_valid_annee);
        assertNotNull(result);
        result = DateIso8601Util.importedDateToIso8601(date_mini);
        assertNotNull(result);
        
        result = DateIso8601Util.importedDateToIso8601(dates_mini_ok);
        assertNotNull(result);
        result = DateIso8601Util.importedDateToIso8601(dates_mini_ko);
        assertNull(result);
    }
}
