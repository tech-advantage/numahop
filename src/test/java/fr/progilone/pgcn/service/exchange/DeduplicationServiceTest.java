package fr.progilone.pgcn.service.exchange;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.DocProperty;
import fr.progilone.pgcn.domain.document.DocPropertyType;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.repository.document.DocUnitRepository;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DeduplicationServiceTest {

    @Mock
    private DocUnitRepository docUnitRepository;

    private DeduplicationService service;

    @BeforeEach
    public void setUp() {
        service = new DeduplicationService(docUnitRepository);
    }

    @Test
    public void testLookupDuplicates() {
        final DocUnit docUnit = new DocUnit();
        docUnit.setIdentifier("e36d6653-9e52-4b2e-b812-3b9bb2c735c1");
        docUnit.setPgcnId("DOC-TEST-001");

        final DocPropertyType type = new DocPropertyType();
        type.setIdentifier("identifier");
        final DocProperty property = new DocProperty();
        property.setType(type);
        property.setValue("5d8ea5fd-ed7d-4af6-88ca-3a5c26b1ee10");
        final BibliographicRecord record = new BibliographicRecord();
        record.addProperty(property);
        docUnit.addRecord(record);

        final DocUnit duplDocUnit = new DocUnit();
        duplDocUnit.setIdentifier("769b03e2-769f-4af3-b262-c61d836964e5");

        final DocUnit duplDocUnit2 = new DocUnit();
        duplDocUnit2.setIdentifier("30e62d6b-cfd2-4c02-80e6-879a2aa1ad31");

        final List<DocUnit> duplicates = new ArrayList<>();
        duplicates.add(duplDocUnit);

        when(docUnitRepository.findAllByPgcnId(docUnit.getPgcnId())).thenReturn(duplicates);
        when(docUnitRepository.searchDuplicates(eq(docUnit), any())).thenReturn(Collections.singletonList(duplDocUnit2));

        final Collection<DocUnit> actual = service.lookupDuplicates(docUnit);

        assertEquals(2, actual.size());
        final Iterator<DocUnit> iterator = actual.iterator();
        DocUnit actualUd = iterator.next();
        assertEquals(duplDocUnit.getIdentifier(), actualUd.getIdentifier());
        actualUd = iterator.next();
        assertEquals(duplDocUnit2.getIdentifier(), actualUd.getIdentifier());
    }
}
