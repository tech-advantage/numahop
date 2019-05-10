package fr.progilone.pgcn.service.exchange.template;

import fr.progilone.pgcn.domain.exchange.template.Name;
import fr.progilone.pgcn.domain.exchange.template.Template;
import fr.progilone.pgcn.domain.library.Library;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class VelocityEngineServiceTest {

    @Mock
    private TemplateService templateService;

    private VelocityEngineService service;

    @Before
    public void setUp() {
        //        Velocity.reset(); // Velocity 2.0
        service = new VelocityEngineService(templateService);
        service.initialize();
    }

    @Test
    public void testAll() throws IOException {
        testGenerateDocumentFromPgcnResourceLoader();
        testGenerateDocumentFromDefaultResourceLoader();
        testEvaluateExpression();
    }

    //    @Test
    public void testGenerateDocumentFromPgcnResourceLoader() throws IOException {
        final String libraryId = "d58abec2-b3dc-407e-bc7c-67804152c4ee";
        final Library library = new Library();
        library.setIdentifier(libraryId);

        final Template template = new Template();
        final ByteArrayInputStream in = new ByteArrayInputStream("Votre nouveau mot de passe est $password.".getBytes(StandardCharsets.UTF_8));

        final HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("password", "HawmEgCighochey");

        when(templateService.findTemplate(Name.ConditionReport, libraryId)).thenReturn(Collections.singletonList(template));
        when(templateService.getContentStream(template)).thenReturn(in);

        final String actual = service.generateDocument(Name.ConditionReport, library, parameters);

        assertEquals("Votre nouveau mot de passe est HawmEgCighochey.", actual);
    }

    //    @Test
    public void testGenerateDocumentFromDefaultResourceLoader() throws IOException {
        final String libraryId = "d58abec2-b3dc-407e-bc7c-67804152c4ee";
        final Library library = new Library();
        library.setIdentifier(libraryId);

        final HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("password", "HawmEgCighochey");

        final String actual = service.generateDocument(Name.ReinitPassword, library, parameters);

        assertEquals("_SUBJECT_Réinitialisation de votre mot de passe\n"
                     + "_BODY_Bonjour,\n"
                     + "\n"
                     + "Nous avons pris en compte votre demande de réinitialisation de mot de passe.\n"
                     + "Vous pouvez désormais vous connecter à l'aide de votre identifiant $login et de votre mot de passe HawmEgCighochey.\n"
                     + "\n"
                     + "Cordialement,\n"
                     + "NumaHOP, Plate-forme de gestion de contenus numérisés\n", actual.replaceAll("\r\n", "\n"));
    }

    //    @Test
    public void testEvaluateExpression() throws IOException {
        final String expression = "Votre nouveau mot de passe est $password.";

        final HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("password", "HawmEgCighochey");

        final String actual = service.evaluateExpression(expression, parameters);

        assertEquals("Votre nouveau mot de passe est HawmEgCighochey.", actual);
    }
}
