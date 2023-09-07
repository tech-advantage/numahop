package fr.progilone.pgcn.service.administration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import fr.progilone.pgcn.domain.administration.Transliteration;
import fr.progilone.pgcn.repository.administration.TransliterationRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TransliterationServiceTest {

    private static final Transliteration.TransliterationId ID = new Transliteration.TransliterationId(Transliteration.Type.FUNCTION, "1fe32276-cce3-4a13-8ae0-2c6d4f27e3bb");

    private static Transliteration transliteration;

    @Mock
    private TransliterationRepository transliterationRepository;

    private TransliterationService service;

    @BeforeAll
    public static void initClass() {
        transliteration = new Transliteration();
        transliteration.setCode(ID.getCode());
        transliteration.setType(ID.getType());
        transliteration.setValue("Clint Eastwood");
    }

    @BeforeEach
    public void setUp() {
        service = new TransliterationService(transliterationRepository);
    }

    @Test
    public void testGetValue() {
        // unknown code
        final String unknownCode = "8428a96c-f6fa-4e7c-b932-9d1c9449d7d1";
        String actual = service.getValue(Transliteration.Type.FUNCTION, unknownCode);
        assertEquals(unknownCode, actual);

        // ok
        when(transliterationRepository.findById(ID)).thenReturn(Optional.of(transliteration));
        actual = service.getValue(ID.getType(), ID.getCode());
        assertEquals(transliteration.getValue(), actual);
    }

    @Test
    public void testGetValueString() {
        when(transliterationRepository.findById(ID)).thenReturn(Optional.of(transliteration));
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
        when(transliterationRepository.findById(ID)).thenReturn(Optional.of(transliteration));
        final String actual = service.getFunction(ID.getCode());
        assertEquals(transliteration.getValue(), actual);
    }
}
