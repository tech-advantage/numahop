package fr.progilone.pgcn.service.exchange.template;

import fr.opensagres.xdocreport.document.images.IImageProvider;
import fr.progilone.pgcn.domain.exchange.template.Name;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class OdtEngineServiceTest {

    @Mock
    private MessageService odtEngineTranslator;
    @Mock
    private TemplateService templateService;

    private OdtEngineService service;

    @Before
    public void setUp() {
        service = new OdtEngineService(odtEngineTranslator, templateService);
        service.initialize();
    }

    @Ignore
    @Test
    public void testGenerateDocumentFromPgcnResourceLoader() throws PgcnTechnicalException, IOException {
        final String libraryId = "d58abec2-b3dc-407e-bc7c-67804152c4ee";
        final Library library = new Library();
        library.setIdentifier(libraryId);

        final HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("someone", "Mickey");
        parameters.put("signature", "Progilone");

        final Map<String, IImageProvider> imageParams = Collections.emptyMap();

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        service.generateDocumentODT(Name.ConditionReport, library, parameters, imageParams, out);

        FileUtils.writeByteArrayToFile(new File("C:\\Users\\Sébastien\\Desktop\\test.odt"), out.toByteArray());
    }

    //    //    @Test
    //    public void testGenerateDocumentFromDefaultResourceLoader() throws IOException {
    //        final String libraryId = "d58abec2-b3dc-407e-bc7c-67804152c4ee";
    //        final Library library = new Library();
    //        library.setIdentifier(libraryId);
    //
    //        final HashMap<String, Object> parameters = new HashMap<>();
    //        parameters.put("password", "HawmEgCighochey");
    //
    //        final String actual = service.generateDocument(Name.ReinitPassword, library, parameters);
    //
    //        assertEquals("_SUBJECT_Réinitialisation de votre mot de passe\n"
    //                     + "_BODY_Bonjour,\n"
    //                     + "\n"
    //                     + "Nous avons pris en compte votre demande de réinitialisation de mot de passe.\n"
    //                     + "Vous pouvez désormais vous connecter à l'aide de votre identifiant $login et de votre mot de passe HawmEgCighochey.\n"
    //                     + "\n"
    //                     + "Cordialement,\n"
    //                     + "NumaHOP, Plate-forme de gestion de contenus numérisés\n", actual.replaceAll("\r\n", "\n"));
    //    }
    //
    //    //    @Test
    //    public void testEvaluateExpression() throws IOException {
    //        final String expression = "Votre nouveau mot de passe est $password.";
    //
    //        final HashMap<String, Object> parameters = new HashMap<>();
    //        parameters.put("password", "HawmEgCighochey");
    //
    //        final String actual = service.evaluateExpression(expression, parameters);
    //
    //        assertEquals("Votre nouveau mot de passe est HawmEgCighochey.", actual);
    //    }
}
