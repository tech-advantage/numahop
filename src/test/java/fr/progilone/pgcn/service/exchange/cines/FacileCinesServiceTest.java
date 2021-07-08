package fr.progilone.pgcn.service.exchange.cines;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import fr.progilone.pgcn.domain.jaxb.facile.FacileResponse;
import fr.progilone.pgcn.domain.jaxb.facile.ValidatorType;
import fr.progilone.pgcn.service.check.FacileCinesService;

/**
 * Created by Jonathan on 10/02/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class FacileCinesServiceTest {

    private static final String VALIDATOR_FORMAT_PNG = "PNG";
    private static final String FACILE_TEST_URL = "https://facile.cines.fr/xml";
    private static final String RESOURCE_FOLDER = "src/test/resources/facile";
    private static final String FILE_PREFIX = "BSG_DELTA_000";
    private static final String FILE_SUFFIX = ".png";

    private FacileCinesService service;

    @Before
    public void setUp() {
        service = new FacileCinesService();
        ReflectionTestUtils.setField(service, "facileApiUrl", FACILE_TEST_URL);
    }

    @Ignore
    @Test
    public void testCheckFilesAgainstFacile() {
        // ignoré car chronophage - mais ça fonctionne !
        
        final List<File> files = getTestListFiles();
        final List<ValidatorType> results = service.checkFilesAgainstFacile(files.toArray(new File[files.size()]));
        results.forEach(this::validateResult);
    }

    @Test
    public void marshallValidatorType() {
        final FacileResponse test = new FacileResponse();
        test.setFileName("test");
        test.setValid(false);
        test.setWellformed(true);
        final StringWriter writer = new StringWriter();
        try {
            final JAXBContext context = JAXBContext.newInstance(fr.progilone.pgcn.domain.jaxb.facile.FacileResponse.class,
                                                                fr.progilone.pgcn.domain.jaxb.facile.ObjectFactory.class);
            final Marshaller marshaller = context.createMarshaller();
            marshaller.marshal(test, writer);
            final String result = writer.toString();
            assertTrue(result.contains("test"));

            // UnMarshall
            final Unmarshaller unmarshaller = context.createUnmarshaller();
            final FacileResponse response = (FacileResponse) unmarshaller.unmarshal(new StringReader(result));

            // Check
            assertEquals(test.isValid(), response.isValid());
            assertEquals(test.isWellformed(), response.isWellformed());

        } catch (final JAXBException e) {
            fail("Erreur lors du marshalling");
        } finally {
            try {
                writer.close();
            } catch (final IOException e) {
                fail(e.getMessage());
            }
        }
    }

    /**
     * Le résultat le plus important pour l'archivage sur la plateforme PAC se situe dans la balise <;valid>.<br>
     * Elle doît être positionnée à « true » pour que le document soit archivable. {@see https://facile.cines.fr/}
     *
     * @param validator
     */
    private void validateResult(final ValidatorType validator) {
        assertTrue(validator.isValid());
        assertEquals(VALIDATOR_FORMAT_PNG, validator.getFormat());
    }

    private List<File> getTestListFiles() {
        final File baseDirectory = new File(RESOURCE_FOLDER);
        final List<File> files = new ArrayList<File>();
        for (int i = 1; i < 6; i++) {
            final File file = new File(baseDirectory, FILE_PREFIX + i + FILE_SUFFIX);
            files.add(file);
        }
        return files;
    }
}
