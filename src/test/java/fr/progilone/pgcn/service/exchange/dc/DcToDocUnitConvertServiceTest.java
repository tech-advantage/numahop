package fr.progilone.pgcn.service.exchange.dc;

import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.DocPropertyType;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.jaxb.dc.ElementContainer;
import fr.progilone.pgcn.domain.jaxb.dc.ObjectFactory;
import fr.progilone.pgcn.domain.jaxb.dc.SimpleLiteral;
import fr.progilone.pgcn.domain.library.Library;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Sébastien on 07/07/2017.
 */
public class DcToDocUnitConvertServiceTest {

    private DcToDocUnitConvertService service;

    @Before
    public void setUp() {
        service = new DcToDocUnitConvertService();
    }

    @Test
    public void testConvert() {
        final ObjectFactory objectFactory = new ObjectFactory();
        final ElementContainer dc = objectFactory.createElementContainer();
        final Library library = new Library();
        library.setPrefix("PREFIX");
        final List<DocPropertyType> propertyTypes = getPropertyTypes();

        final SimpleLiteral identifier = new SimpleLiteral();
        identifier.getContent().add("PGCNID-001");
        dc.getAny().add(objectFactory.createIdentifier(identifier));

        final SimpleLiteral actor = new SimpleLiteral();
        actor.getContent().add("Sophie Marceau");
        dc.getAny().add(objectFactory.createCreator(actor));

        final SimpleLiteral title = new SimpleLiteral();
        final String titleStr = "« Journal du Voyage de l’Ambassade française en Perse par Mr Tancogne, jeune de langue à la suite . "
                                + "Route de Constantinople à Théran » Il s’agit d’une copie du journal de J. M. Tancoigne "
                                + "[au moment de l’expédition de l’ambassadeur Claud-Matthieu de Gardanne vers l’Iran]";
        title.getContent().add(titleStr);
        dc.getAny().add(objectFactory.createTitle(title));

        final DocUnit docUnit = service.convert(dc, library, propertyTypes);

        assertEquals(1, docUnit.getRecords().size());

        final BibliographicRecord record = docUnit.getRecords().iterator().next();
        assertEquals(3, record.getProperties().size());
        // creator
        assertTrue(record.getProperties()
                         .stream()
                         .anyMatch(p -> "creator".equals(p.getType().getIdentifier()) && "Sophie Marceau".equals(p.getValue())));
        // title
        assertTrue(record.getProperties().stream().anyMatch(p -> "title".equals(p.getType().getIdentifier()) && titleStr.equals(p.getValue())));
        assertEquals(docUnit.getLabel(), StringUtils.abbreviate(titleStr, 255));
        // identifier
        assertTrue(record.getProperties()
                         .stream()
                         .anyMatch(p -> "identifier".equals(p.getType().getIdentifier()) && "PGCNID-001".equals(p.getValue())));
        assertEquals(docUnit.getPgcnId(), "PREFIX, PGCNID-001");
    }

    private List<DocPropertyType> getPropertyTypes() {
        final List<DocPropertyType> docPropertyTypes = new ArrayList<>();

        DocPropertyType ppty = new DocPropertyType();
        ppty.setIdentifier("creator");
        ppty.setSuperType(DocPropertyType.DocPropertySuperType.DC);
        docPropertyTypes.add(ppty);

        ppty = new DocPropertyType();
        ppty.setIdentifier("title");
        ppty.setSuperType(DocPropertyType.DocPropertySuperType.DC);
        docPropertyTypes.add(ppty);

        ppty = new DocPropertyType();
        ppty.setIdentifier("identifier");
        ppty.setSuperType(DocPropertyType.DocPropertySuperType.DC);
        docPropertyTypes.add(ppty);

        return docPropertyTypes;
    }

}
