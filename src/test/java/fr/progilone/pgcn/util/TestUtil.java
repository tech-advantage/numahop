package fr.progilone.pgcn.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import fr.progilone.pgcn.exception.PgcnException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.exception.message.PgcnList;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.Assert.*;

/**
 * Utility class for testing REST controllers.
 */
public class TestUtil {

    /**
     * MediaType for JSON UTF8
     */
    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
                                                                        MediaType.APPLICATION_JSON.getSubtype(),
                                                                        Charset.forName("utf8"));

    private static final ObjectMapper mapper = createJacksonObjectMapper();

    private static ObjectMapper createJacksonObjectMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.USE_EQUALITY_FOR_OBJECT_ID, true);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);    // 1970-01-01T00:00:00.000+0000
        mapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
        return mapper;
    }

    /**
     * Convert an object to JSON byte array.
     *
     * @param object the object to convert
     * @return the JSON byte array
     * @throws IOException
     */
    public static byte[] convertObjectToJsonBytes(final Object object) throws IOException {
        return mapper.writeValueAsBytes(object);
    }

    public static void checkPgcnException(final PgcnException actualException, final PgcnErrorCode... expectedCodes) {

        checkPgcnErrors(actualException.getErrors(), expectedCodes);
    }

    public static void checkPgcnErrors(final Collection<PgcnError> errors, final PgcnErrorCode... expectedCodes) {
        assertNotNull(errors);
        assertEquals("Number of errors is different from expected", expectedCodes.length, errors.size());

        for (int i = 0; i < expectedCodes.length; i++) {
            final PgcnErrorCode expectedCode = expectedCodes[i];
            assertTrue("Error code " + expectedCode + " was expected", errors.stream().map(err -> {
                if (err != null) {
                    return err.getCode();
                }
                return null;
            }).filter(Objects::nonNull).anyMatch(expectedCode::equals));
        }
    }

    public static boolean containsError(final String errorCode, final PgcnList<PgcnError> errors) {
        if (errors == null) {
            return false;
        } else {
            return errors.stream()
                         .filter(Objects::nonNull)
                         .anyMatch(error -> StringUtils.equals(errorCode, error.getCode().name()));
        }
    }

    public static <T> void checkArrayContainsSameElements(final T[] expected, final T[] actuals) {
        assertNotNull(actuals);
        assertEquals(expected.length, actuals.length);

        for (int i = 0; i < expected.length; i++) {
            assertTrue("Element " + expected[i] + " was expected", Stream.of(actuals).anyMatch(expected[i]::equals));
        }
    }

    public static <T> void checkCollectionContainsSameElements(final Collection<T> expected, final Collection<T> actuals) {
        assertNotNull(actuals);
        assertEquals(expected.size(), actuals.size());
        expected.forEach(exp -> assertTrue("element " + exp + " was expected", actuals.stream().anyMatch(exp::equals)));
    }
}
