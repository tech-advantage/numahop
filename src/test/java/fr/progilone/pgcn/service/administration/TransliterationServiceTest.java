package fr.progilone.pgcn.service.administration;

import fr.progilone.pgcn.domain.administration.Transliteration;
import fr.progilone.pgcn.repository.administration.TransliterationRepository;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TransliterationServiceTest {

    private static final Transliteration.TransliterationId ID =
        new Transliteration.TransliterationId(Transliteration.Type.FUNCTION, "1fe32276-cce3-4a13-8ae0-2c6d4f27e3bb");

    private static Transliteration transliteration;

    @Mock
    private TransliterationRepository transliterationRepository;

    private TransliterationService service;

    @BeforeClass
    public static void initClass() {
        transliteration = new Transliteration();
        transliteration.setCode(ID.getCode());
        transliteration.setType(ID.getType());
        transliteration.setValue("Clint Eastwood");
    }

    @Before
    public void setUp() {
        service = new TransliterationService(transliterationRepository);
        when(transliterationRepository.findOne(ID)).thenReturn(transliteration);
    }

    @Test
    public void testGetValue() {
        // unknown code
        final String unknownCode = "8428a96c-f6fa-4e7c-b932-9d1c9449d7d1";
        String actual = service.getValue(Transliteration.Type.FUNCTION, unknownCode);
        assertEquals(unknownCode, actual);

        // ok
        actual = service.getValue(ID.getType(), ID.getCode());
        assertEquals(transliteration.getValue(), actual);
    }

    @Test
    public void testGetValueString() {
        // unknown type
        final String value = "Andromède";
        String actual = service.getValue("GALAXY", value);
        assertEquals(value, actual);

        // ok
        actual = service.getValue(ID.getType().name(), ID.getCode());
        assertEquals(transliteration.getValue(), actual);
    }

    @Test
    public void testGetFunction() {
        final String actual = service.getFunction(ID.getCode());
        assertEquals(transliteration.getValue(), actual);
    }
}
