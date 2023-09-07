package fr.progilone.pgcn.service.exchange;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.exchange.ImportReport;
import fr.progilone.pgcn.domain.exchange.ImportedDocUnit;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.repository.exchange.ImportedDocUnitRepository;
import fr.progilone.pgcn.service.document.DocUnitService;
import fr.progilone.pgcn.service.document.DocUnitValidationService;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.internal.matchers.CapturingMatcher;
import org.mockito.internal.stubbing.answers.ReturnsArgumentAt;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Created by Sebastien on 08/12/2016.
 */
@ExtendWith(MockitoExtension.class)
public class ImportDocUnitServiceTest {

    @Mock
    private DocUnitService docUnitService;
    @Mock
    private DocUnitValidationService docUnitValidationService;
    @Mock
    private ImportedDocUnitRepository importedDocUnitRepository;

    private ImportDocUnitService service;

    @BeforeEach
    public void setUp() {
        service = new ImportDocUnitService(docUnitService, docUnitValidationService, importedDocUnitRepository);
    }

    @Test
    public void testSave() throws PgcnTechnicalException {
        final ImportedDocUnit impDocUnit = new ImportedDocUnit();
        final DocUnit docUnit = new DocUnit();
        final BibliographicRecord record = new BibliographicRecord();
        docUnit.addRecord(record);
        impDocUnit.initDocUnitFields(docUnit);

        when(importedDocUnitRepository.save(impDocUnit)).thenReturn(impDocUnit);

        final ImportedDocUnit actual = service.save(impDocUnit);

        verify(docUnitService).save(docUnit, false);
        verify(importedDocUnitRepository).save(impDocUnit);
        assertSame(impDocUnit, actual);
    }

    @Test
    public void testCreateOk() throws PgcnTechnicalException {
        final ImportedDocUnit impDocUnit = new ImportedDocUnit();
        final DocUnit docUnit = new DocUnit();
        final BibliographicRecord record = new BibliographicRecord();
        docUnit.addRecord(record);
        impDocUnit.initDocUnitFields(docUnit);

        when(importedDocUnitRepository.save(any(ImportedDocUnit.class))).then(new ReturnsArgumentAt(0));

        final ImportedDocUnit actual = service.create(impDocUnit);

        verify(docUnitService).save(docUnit, false);
        verify(importedDocUnitRepository).save(impDocUnit);
        assertSame(impDocUnit, actual);
    }

    /**
     * Tentative d'import d'une UD invalide
     *
     * @throws PgcnTechnicalException
     */
    @Test
    public void testCreateKo1() throws PgcnTechnicalException {
        final ImportedDocUnit impDocUnit = new ImportedDocUnit();
        final DocUnit docUnit = new DocUnit();
        final BibliographicRecord record = new BibliographicRecord();
        docUnit.addRecord(record);
        impDocUnit.initDocUnitFields(docUnit);

        doThrow(new PgcnValidationException(docUnit, new PgcnError.Builder().setCode(PgcnErrorCode.DOC_UNIT_LABEL_MANDATORY).build())).when(docUnitValidationService)
                                                                                                                                      .validate(docUnit);

        final CapturingMatcher<ImportedDocUnit> matcher = new CapturingMatcher<>();
        when(importedDocUnitRepository.save(argThat(matcher))).then(new ReturnsArgumentAt(0));

        try {
            service.create(impDocUnit);
            fail("testCreate should have thrown PgcnValidationException");

        } catch (final PgcnValidationException e) {
            final ImportedDocUnit actualImp = matcher.getLastValue();
            assertNotSame(impDocUnit, actualImp);
            assertEquals(1, actualImp.getMessages().size());
            assertEquals(PgcnErrorCode.DOC_UNIT_LABEL_MANDATORY.name(), actualImp.getMessages().iterator().next().getCode());
        }
    }

    /**
     * Tentative d'import d'une UD dont le PgcnId est déjà présent au statut non dispo
     *
     * @throws PgcnTechnicalException
     */
    @Test
    public void testCreateKo2() throws PgcnTechnicalException {
        final ImportedDocUnit impDocUnit = new ImportedDocUnit();
        final DocUnit docUnit = new DocUnit();
        docUnit.setPgcnId("XXX-000");

        final BibliographicRecord record = new BibliographicRecord();
        docUnit.addRecord(record);
        impDocUnit.initDocUnitFields(docUnit);

        doThrow(new PgcnValidationException(docUnit, new PgcnError.Builder().setCode(PgcnErrorCode.DOC_UNIT_DUPLICATE_PGCN_ID).build())).when(docUnitValidationService)
                                                                                                                                        .validate(docUnit);

        final CapturingMatcher<ImportedDocUnit> matcher = new CapturingMatcher<>();
        when(importedDocUnitRepository.save(argThat(matcher))).then(new ReturnsArgumentAt(0));

        try {
            final ImportedDocUnit actual = service.create(impDocUnit);

            verify(docUnitService).deleteByPgcnIdAndState(docUnit.getPgcnId(), DocUnit.State.NOT_AVAILABLE);
            verify(docUnitService).save(docUnit, false);
            verify(importedDocUnitRepository).save(impDocUnit);
            assertSame(impDocUnit, actual);

        } catch (final PgcnValidationException e) {
            fail("unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testFindByImportReportWithoutFilters() {
        final ImportReport report = new ImportReport();
        final int page = 0;
        final int size = 10;

        final List<String> content = Collections.singletonList("6b378e41-8083-4b11-b83c-57a20f7fb432");
        final Page<String> pageOfIds = new PageImpl<>(content);
        final List<ImportedDocUnit> impDocUnits = Collections.singletonList(new ImportedDocUnit());

        when(importedDocUnitRepository.findIdentifiersByImportReport(eq(report), any(Pageable.class))).thenReturn(pageOfIds);
        when(importedDocUnitRepository.findByIdentifiersIn(eq(pageOfIds.getContent()), any(Sort.class))).thenReturn(impDocUnits);

        final Page<ImportedDocUnit> actual = service.findByImportReport(report, PageRequest.of(page, size));

        assertEquals(1, actual.getContent().size());
    }

    @Test
    public void testFindByImportReportWithoutStatus() {
        final ImportReport report = new ImportReport();
        final int page = 0;
        final int size = 10;

        final List<String> content = Collections.singletonList("6b378e41-8083-4b11-b83c-57a20f7fb432");
        final Page<String> pageOfIds = new PageImpl<>(content);
        final List<ImportedDocUnit> impDocUnits = Collections.singletonList(new ImportedDocUnit());

        when(importedDocUnitRepository.findIdentifiersByImportReport(eq(report), any(), anyBoolean(), anyBoolean(), any(Pageable.class))).thenReturn(pageOfIds);
        when(importedDocUnitRepository.findByIdentifiersIn(eq(pageOfIds.getContent()), any(Sort.class))).thenReturn(impDocUnits);

        final Page<ImportedDocUnit> actual = service.findByImportReport(report, page, size, null, false, false);

        assertEquals(1, actual.getContent().size());
    }

    @Test
    public void testFindByImportReportWithStatus() {
        final ImportReport report = new ImportReport();
        final int page = 0;
        final int size = 10;

        final List<String> content = Collections.singletonList("6b378e41-8083-4b11-b83c-57a20f7fb432");
        final Page<String> pageOfIds = new PageImpl<>(content);
        final List<ImportedDocUnit> impDocUnits = Collections.singletonList(new ImportedDocUnit());

        when(importedDocUnitRepository.findIdentifiersByImportReport(eq(report), any(), anyBoolean(), anyBoolean(), any(Pageable.class))).thenReturn(pageOfIds);
        when(importedDocUnitRepository.findByIdentifiersIn(eq(pageOfIds.getContent()), any(Sort.class))).thenReturn(impDocUnits);

        final Page<ImportedDocUnit> actual = service.findByImportReport(report, page, size, Collections.singletonList(DocUnit.State.AVAILABLE), false, false);

        assertEquals(1, actual.getContent().size());
    }

    @Test
    public void testFindByImportReportWithNoResult() {
        final ImportReport report = new ImportReport();
        final int page = 0;
        final int size = 10;

        when(importedDocUnitRepository.findIdentifiersByImportReport(eq(report), any(), anyBoolean(), anyBoolean(), any(Pageable.class))).thenReturn(new PageImpl<>(Collections
                                                                                                                                                                               .emptyList()));

        final Page<ImportedDocUnit> actual = service.findByImportReport(report, page, size, null, false, false);

        assertTrue(actual.getContent().isEmpty());
        verify(importedDocUnitRepository, never()).findByIdentifiersIn(any(), any(Sort.class));
    }

}
