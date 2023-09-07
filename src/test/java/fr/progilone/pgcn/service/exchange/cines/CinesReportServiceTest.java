package fr.progilone.pgcn.service.exchange.cines;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.exchange.cines.CinesReport;
import fr.progilone.pgcn.domain.exchange.cines.CinesReport.Status;
import fr.progilone.pgcn.repository.exchange.cines.CinesReportRepository;
import fr.progilone.pgcn.service.workflow.WorkflowService;
import fr.progilone.pgcn.util.SetIdAndReturnsArgumentAt;
import fr.progilone.pgcn.web.websocket.WebsocketService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.internal.stubbing.answers.ReturnsArgumentAt;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Created by Sébastien on 02/01/2017.
 */
@ExtendWith(MockitoExtension.class)
public class CinesReportServiceTest {

    @Mock
    private CinesReportRepository cinesReportRepository;

    @Mock
    private WebsocketService websocketService;

    @Mock
    private WorkflowService workflowService;

    private CinesReportService service;

    @BeforeEach
    public void setUp() {
        service = new CinesReportService(cinesReportRepository, websocketService, workflowService);

        // retourne le report, avec un identifiant
        final ReturnsArgumentAt setIdCatcher = new SetIdAndReturnsArgumentAt(0, "EXPORT-CINES-001");
        when(cinesReportRepository.save(any(CinesReport.class))).thenAnswer(setIdCatcher);
    }

    @Test
    public void testCreateCinesReport() {
        final DocUnit docUnit = new DocUnit();
        final CinesReport actual = service.createCinesReport(docUnit);

        verify(cinesReportRepository).save(actual);
        verify(websocketService).sendObject(eq(actual.getIdentifier()), anyMap());

        assertEquals(docUnit, actual.getDocUnit());
        assertEquals(CinesReport.Status.EXPORTING, actual.getStatus());
    }

    @Test
    public void testSetReportSending() {
        final CinesReport report = new CinesReport();
        final CinesReport actual = service.setReportSending(report);

        verify(cinesReportRepository).save(report);
        verify(websocketService).sendObject(eq(report.getIdentifier()), anyMap());

        assertEquals(CinesReport.Status.SENDING, actual.getStatus());
        assertNotNull(actual.getDateSent());
    }

    @Test
    public void testSetReportSent() {
        final CinesReport report = new CinesReport();
        final CinesReport actual = service.setReportSent(report);

        verify(cinesReportRepository).save(report);
        verify(websocketService).sendObject(eq(report.getIdentifier()), anyMap());

        assertEquals(CinesReport.Status.SENT, actual.getStatus());
        assertNotNull(actual.getDateSent());
    }

    @Test
    public void testSetReportArReceived() {
        final CinesReport report = new CinesReport();
        final CinesReport actual = service.setReportArReceived(report, LocalDateTime.now());

        verify(cinesReportRepository).save(report);
        verify(websocketService).sendObject(eq(report.getIdentifier()), anyMap());

        assertEquals(CinesReport.Status.AR_RECEIVED, actual.getStatus());
        assertNotNull(actual.getDateAr());
    }

    @Test
    public void testSetReportRejected() {
        final CinesReport report = new CinesReport();
        final String motive = "Invalid format";
        final DocUnit doc = new DocUnit();
        // doc.setIdentifier("doc_unit_id_test");
        report.setDocUnit(doc);
        final CinesReport actual = service.setReportRejected(report, LocalDateTime.now(), motive);

        verify(cinesReportRepository).save(report);
        verify(websocketService).sendObject(eq(report.getIdentifier()), anyMap());

        assertEquals(CinesReport.Status.REJECTED, actual.getStatus());
        assertEquals(motive, actual.getRejectionMotive());
        assertNotNull(actual.getDateRejection());
    }

    @Test
    public void testSetReportArchived() {
        final CinesReport report = new CinesReport();
        final DocUnit doc = new DocUnit();
        doc.setIdentifier("doc_unit_id_test");
        report.setDocUnit(doc);
        final String certificate = "<certificate>Bien reçu le 02/01/2017 à 10:23</certificate>";

        final CinesReport actual = service.setReportArchived(report, LocalDateTime.now(), certificate);

        verify(cinesReportRepository).save(report);
        verify(websocketService).sendObject(eq(report.getIdentifier()), anyMap());

        assertEquals(CinesReport.Status.ARCHIVED, actual.getStatus());
        assertEquals(certificate, report.getCertificate());
        assertNotNull(actual.getDateArchived());
    }

    @Test
    public void testSetReportFailed() {
        final CinesReport report = new CinesReport();
        report.setStatus(CinesReport.Status.EXPORTING);
        report.setIdentifier("EXPORT-CINES-001");
        final String message = "la prise est débranchée";

        final CinesReport mockReport = new CinesReport();
        mockReport.setIdentifier("EXPORT-CINES-001");
        mockReport.setStatus(Status.EXPORTING);
        when(cinesReportRepository.findByIdentifier(any(String.class))).thenReturn(mockReport);

        final CinesReport actual = service.failReport(report, message);

        verify(cinesReportRepository).save(report);
        verify(websocketService).sendObject(eq(report.getIdentifier()), anyMap());

        assertEquals(CinesReport.Status.FAILED, actual.getStatus());
        assertEquals("Arrêt imprévu du traitement au statut EXPORTING avec l'erreur: la prise est débranchée", actual.getMessage());
    }
}
