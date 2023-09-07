package fr.progilone.pgcn.util;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import fr.progilone.pgcn.exception.PgcnException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.exception.message.PgcnList;
import java.io.IOException;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;

/**
 * Utility class for testing REST controllers.
 */
public class TestUtil {

    private static final ObjectMapper mapper = createJacksonObjectMapper();

    private static ObjectMapper createJacksonObjectMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.USE_EQUALITY_FOR_OBJECT_ID, true);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);    // 1970-01-01T00:00:00.000+0000
        mapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
        mapper.findAndRegisterModules();
        return mapper;
    }

    /**
     * Convert an object to JSON byte array.
     *
     * @param object
     *            the object to convert
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
        assertEquals(expectedCodes.length, errors.size());

        for (final PgcnErrorCode expectedCode : expectedCodes) {
            assertTrue(errors.stream().map(err -> {
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
            return errors.stream().filter(Objects::nonNull).anyMatch(error -> StringUtils.equals(errorCode, error.getCode().name()));
        }
    }

    public static <T> void checkArrayContainsSameElements(final T[] expected, final T[] actuals) {
        assertNotNull(actuals);
        assertEquals(expected.length, actuals.length);

        for (final T element : expected) {
            assertTrue(Stream.of(actuals).anyMatch(element::equals));
        }
    }

    public static <T> void checkCollectionContainsSameElements(final Collection<T> expected, final Collection<T> actuals) {
        assertNotNull(actuals);
        assertEquals(expected.size(), actuals.size());
        expected.forEach(exp -> assertTrue(actuals.stream().anyMatch(exp::equals)));
    }
}
