package fr.progilone.pgcn.service.exchange;

import fr.progilone.pgcn.domain.exchange.DataEncoding;
import fr.progilone.pgcn.domain.exchange.FileFormat;
import fr.progilone.pgcn.domain.exchange.ImportReport;
import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.domain.util.CustomUserDetails;
import fr.progilone.pgcn.repository.document.DocUnitRepository;
import fr.progilone.pgcn.repository.exchange.ImportReportRepository;
import fr.progilone.pgcn.repository.exchange.ImportedDocUnitRepository;
import fr.progilone.pgcn.service.storage.FileStorageManager;
import fr.progilone.pgcn.service.util.transaction.StatefullTransactionalLoopService;
import fr.progilone.pgcn.service.util.transaction.TransactionService;
import fr.progilone.pgcn.web.websocket.WebsocketService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.stubbing.answers.ReturnsArgumentAt;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Created by Sebastien on 05/08/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class ImportReportServiceTest {

    @Mock
    private DocUnitRepository docUnitRepository;
    @Mock
    private FileStorageManager fm;
    @Mock
    private ImportReportRepository importReportRepository;
    @Mock
    private ImportedDocUnitRepository importedDocUnitRepository;
    @Mock
    private StatefullTransactionalLoopService transactionalLoopService;
    @Mock
    private TransactionService transactionService;
    @Mock
    private WebsocketService websocketService;

    private ImportReportService service;

    @Before
    public void setUp() {
        service = new ImportReportService(docUnitRepository,
                                          fm,
                                          importReportRepository,
                                          importedDocUnitRepository,
                                          transactionalLoopService,
                                          transactionService,
                                          websocketService);

        final CustomUserDetails customUserDetails = new CustomUserDetails(null, "tortor", null, null, null, null, false, User.Category.OTHER);
        final TestingAuthenticationToken authenticationToken =
            new TestingAuthenticationToken(customUserDetails, "Mauris quis imperdiet libero. Aenean porttitor sem ac nibh euismod congue.");
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        when(importReportRepository.save(any(ImportReport.class))).thenAnswer(new ReturnsArgumentAt(0));
    }

    @Test
    public void testCreateImportReport() {
        final String fileName = "filename.txt";
        final Long fileSize = 1024L;
        final FileFormat fileFormat = FileFormat.MARCXML;
        final DataEncoding dataEncoding = DataEncoding.UTF_8;
        final String libraryId = "library_bsg";
        final String projectId = "project_bsg";
        final String lotId = "lot_bsg";
        final String mappingId = "6b378e41-8083-4b11-b83c-57a20f7fb432";

        final ImportReport actual =
            service.createSimpleImportReport(fileName, fileSize, fileFormat, dataEncoding, libraryId, projectId, lotId, mappingId);

        assertNotNull(actual.getMapping());
        assertEquals(mappingId, actual.getMapping().getIdentifier());

        assertEquals(1, actual.getFiles().size());
        final ImportReport.ImportedFile actualFile = actual.getFiles().get(0);
        assertEquals(fileName, actualFile.getOriginalFilename());
        assertEquals(fileSize, actualFile.getFileSize());
        assertEquals(fileFormat, actual.getFileFormat());
        assertEquals(dataEncoding, actual.getDataEncoding());
        assertEquals(ImportReport.Status.PENDING, actual.getStatus());
        assertEquals("tortor", actual.getRunBy());
        assertEquals(libraryId, actual.getLibrary().getIdentifier());
        assertEquals(projectId, actual.getProject().getIdentifier());
        assertEquals(mappingId, actual.getMapping().getIdentifier());
    }

    @Test
    public void testStartReport() {
        final ImportReport report = new ImportReport();
        report.setIdentifier("Curabitur iaculis convallis dui");

        final ImportReport actual = service.startReport(report);

        assertNotNull(actual.getStart());
        assertEquals(ImportReport.Status.PRE_IMPORTING, actual.getStatus());
        verify(websocketService).sendObject(eq(report.getIdentifier()), anyMapOf(String.class, Object.class));
    }

    @Test
    public void testSetReportStatus() {
        final ImportReport report = new ImportReport();
        report.setIdentifier("Curabitur iaculis convallis dui");
        final ImportReport.Status newStatus = ImportReport.Status.DEDUPLICATING;

        final ImportReport actual = service.setReportStatus(report, newStatus);

        assertEquals(newStatus, actual.getStatus());
        verify(websocketService).sendObject(eq(report.getIdentifier()), anyMapOf(String.class, Object.class));
    }

    @Test
    public void testEndReport() {
        final ImportReport report = new ImportReport();
        report.setIdentifier("Curabitur iaculis convallis dui");

        final ImportReport actual = service.endReport(report);

        assertNotNull(actual.getStart());
        assertNotNull(actual.getEnd());
        assertEquals(ImportReport.Status.COMPLETED, actual.getStatus());
        verify(websocketService).sendObject(eq(report.getIdentifier()), anyMapOf(String.class, Object.class));
    }

    @Test
    public void testFailReport() {
        final ImportReport report = new ImportReport();
        report.setIdentifier("Curabitur iaculis convallis dui");
        report.setStatus(ImportReport.Status.PRE_IMPORTING);
        final String message = "Microsoft remplace son emoji de pistolet laser par un revolver";

        when(importReportRepository.findByIdentifier(report.getIdentifier())).thenReturn(report);

        final ImportReport actual = service.failReport(report, message);

        assertNotNull(actual.getStart());
        assertNotNull(actual.getEnd());
        assertEquals(ImportReport.Status.FAILED, actual.getStatus());
        assertNotNull(report.getMessage());
        verify(websocketService).sendObject(eq(report.getIdentifier()), anyMapOf(String.class, Object.class));
    }
}
