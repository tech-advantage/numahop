package fr.progilone.pgcn.service.document;

import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.DocProperty;
import fr.progilone.pgcn.domain.document.DocPropertyType;
import fr.progilone.pgcn.domain.dto.document.BibliographicRecordDcDTO;
import fr.progilone.pgcn.domain.dto.document.BibliographicRecordMassUpdateDTO;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.repository.document.BibliographicRecordRepository;
import fr.progilone.pgcn.service.es.EsBibliographicRecordService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.matchers.CapturingMatcher;
import org.mockito.internal.stubbing.answers.ReturnsArgumentAt;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Sébastien on 10/07/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class BibliographicRecordServiceTest {

    @Mock
    private BibliographicRecordRepository bibliographicRecordRepository;
    @Mock
    private EsBibliographicRecordService esBibliographicRecordService;
    @Mock
    private DocPropertyService docPropertyService;
    @Mock
    private DocPropertyTypeService docPropertyTypeService;

    private BibliographicRecordService service;

    @Before
    public void setUp() {
        service =
            new BibliographicRecordService(bibliographicRecordRepository, esBibliographicRecordService, docPropertyService, docPropertyTypeService);
    }

    @Test
    public void testBibliographicRecordToDcDTO() {
        final DocPropertyType titleType = new DocPropertyType();
        titleType.setIdentifier("title");
        titleType.setSuperType(DocPropertyType.DocPropertySuperType.DC);

        final DocProperty title = new DocProperty();
        title.setRank(0);
        title.setType(titleType);
        title.setValue("Lonely planet: Islande");

        final BibliographicRecord record = new BibliographicRecord();
        record.addProperty(title);

        final BibliographicRecordDcDTO dto = service.bibliographicRecordToDcDTO(record);

        assertEquals(1, dto.getTitle().size());
        assertEquals(title.getValue(), dto.getTitle().get(0));
    }

    @Test
    public void testDuplicate() {
        final BibliographicRecord bib = new BibliographicRecord();
        bib.setIdentifier("e6f9fa01-bf00-41b3-aee6-697e7bd4e0a3");
        bib.setTitle("Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit");
        bib.setSigb("Fusce ullamcorper ligula eget mauris bibendum sollicitudin");
        bib.setSudoc("Nulla non aliquet quam");
        bib.setCalames("ut consequat justo");
        bib.setDocElectronique("Mauris et urna sodales");
        bib.setLibrary(new Library());

        final DocProperty property = new DocProperty();
        property.setIdentifier("0228b4ab-3509-4e17-8045-562a80dd4144");
        property.setValue("vestibulum nisl nec");
        property.setType(new DocPropertyType());
        property.setLanguage("FR");
        property.setRank(14450);
        bib.addProperty(property);

        when(bibliographicRecordRepository.findOneWithDependencies(bib.getIdentifier())).thenReturn(bib);
        when(bibliographicRecordRepository.save(any(BibliographicRecord.class))).then(new ReturnsArgumentAt(0));

        final BibliographicRecord actual = service.duplicate(bib.getIdentifier());

        assertNull(actual.getIdentifier());
        assertEquals(bib.getTitle(), actual.getTitle());
        assertEquals(bib.getSigb(), actual.getSigb());
        assertEquals(bib.getSudoc(), actual.getSudoc());
        assertEquals(bib.getCalames(), actual.getCalames());
        assertEquals(bib.getDocElectronique(), actual.getDocElectronique());
        assertSame(bib.getLibrary(), actual.getLibrary());

        assertEquals(1, actual.getProperties().size());
        final DocProperty actualProperty = actual.getProperties().iterator().next();

        assertNull(actualProperty.getIdentifier());
        assertEquals(property.getValue(), actualProperty.getValue());
        assertSame(property.getType(), actualProperty.getType());
        assertEquals(property.getLanguage(), actualProperty.getLanguage());
        assertEquals(property.getRank(), actualProperty.getRank());
    }

    @Test
    public void testMassUpdate() {
        final BibliographicRecordMassUpdateDTO updates = new BibliographicRecordMassUpdateDTO();

        // empty update
        service.update(updates);

        // update
        final BibliographicRecordMassUpdateDTO.Update fieldUpd = new BibliographicRecordMassUpdateDTO.Update();
        fieldUpd.setType("sigb");
        fieldUpd.setValue("Le petit prince");
        final BibliographicRecordMassUpdateDTO.Update propertyUpd = new BibliographicRecordMassUpdateDTO.Update();
        propertyUpd.setType("description");
        propertyUpd.setValue("Le Petit Prince est une œuvre de langue française, la plus connue d'Antoine de Saint-Exupéry.");

        final String recordId = "d622c66a-51b1-40d0-8291-8198beb953bb";
        updates.setRecordIds(Collections.singletonList(recordId));
        updates.setFields(Collections.singletonList(fieldUpd));
        updates.setProperties(Collections.singletonList(propertyUpd));

        final DocPropertyType descType = new DocPropertyType();
        descType.setIdentifier("description");
        final DocPropertyType chineseType = new DocPropertyType();
        chineseType.setIdentifier("誰帶禮物是永遠歡迎的");

        // record avec 2 champs et 2 propriétés renseignées
        final BibliographicRecord record = new BibliographicRecord();
        record.setIdentifier(recordId);
        record.setSigb("What is a version 4 UUID?");
        record.setCalames("Aliquam hendrerit eu ligula eget interdum.");
        final DocProperty description = new DocProperty();
        description.setType(descType);
        description.setValue("test de la mise à jour d'une sélection de notices");
        record.addProperty(description);
        final DocProperty chinois = new DocProperty();
        chinois.setType(chineseType);
        chinois.setValue("最長的路徑是一個人獨自行走");
        record.addProperty(chinois);

        when(bibliographicRecordRepository.findAll(updates.getRecordIds())).thenReturn(Collections.singletonList(record));
        when(docPropertyTypeService.findAll()).thenReturn(Collections.singletonList(descType));

        service.update(updates);

        final CapturingMatcher<DocProperty> propertyMatcher = new CapturingMatcher<>();

        verify(docPropertyService).save(argThat(propertyMatcher));
        verify(bibliographicRecordRepository).save(record);

        final List<DocProperty> updateProperties = propertyMatcher.getAllValues();
        assertEquals(1, updateProperties.size());
        final DocProperty updatedProperty = updateProperties.get(0);
        assertEquals("description", updatedProperty.getType().getIdentifier());
        assertEquals(propertyUpd.getValue(), updatedProperty.getValue());

        assertEquals(fieldUpd.getValue(), record.getSigb());
        assertEquals("Aliquam hendrerit eu ligula eget interdum.", record.getCalames());

        assertEquals(2, record.getProperties().size());
    }
}
