package fr.progilone.pgcn.service.exchange.ead.script.format;

import fr.progilone.pgcn.domain.jaxb.ead.ObjectFactory;
import fr.progilone.pgcn.domain.jaxb.ead.Persname;
import fr.progilone.pgcn.domain.jaxb.ead.Unittitle;
import fr.progilone.pgcn.service.exchange.ead.script.AbstractScriptTest;
import org.junit.Test;

import javax.script.ScriptException;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class TextFormatterTest extends AbstractScriptTest {

    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    @Test
    public void testGroovyTextContent() throws ScriptException {
        final Unittitle unittitle = OBJECT_FACTORY.createUnittitle();
        final List<Serializable> content = unittitle.getContent();

        content.add("l'auteur est ");

        final Persname persname = OBJECT_FACTORY.createPersname();
        persname.getContent().add("Victor Hugo");
        content.add(new JAXBElement<>(new QName("urn:isbn:1-931666-22-9", "persname"), Persname.class, null, persname));

        final String expression = "text(var_test)";

        final Map<String, Object> bindings = new HashMap<>();
        bindings.put("var_test", unittitle);

        final Object actual = evalUserScript(expression, null, bindings, new TextFormatter("test"));

        assertEquals("l'auteur est Victor Hugo", actual);
    }
}
