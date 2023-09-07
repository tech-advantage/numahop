package fr.progilone.pgcn.service.document.conditionreport;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReport;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReportAttachment;
import fr.progilone.pgcn.repository.document.conditionreport.ConditionReportAttachmentRepository;
import fr.progilone.pgcn.repository.document.conditionreport.ConditionReportRepository;
import fr.progilone.pgcn.service.storage.FileStorageManager;
import fr.progilone.pgcn.util.SetIdAndReturnsArgumentAt;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class ConditionReportAttachmentServiceTest {

    private static final String REPORT_DIR = FileUtils.getTempDirectoryPath() + "/pgcn_test";

    @Mock
    private ConditionReportAttachmentRepository conditionReportAttachmentRepository;
    @Mock
    private ConditionReportRepository conditionReportRepository;
    @Mock
    private FileStorageManager fm;

    private ConditionReportAttachmentService service;

    @BeforeEach
    public void setUp() {
        service = new ConditionReportAttachmentService(conditionReportAttachmentRepository, conditionReportRepository, fm);
        ReflectionTestUtils.setField(service, "reportDir", REPORT_DIR);
    }

    @BeforeAll
    public static void init() throws IOException {
        FileUtils.forceMkdir(new File(REPORT_DIR));
    }

    @AfterAll
    public static void clean() {
        FileUtils.deleteQuietly(new File(REPORT_DIR));
    }

    @Test
    public void testDelete() {
        final ConditionReportAttachment attachment = getConditionReportAttachment();
        when(conditionReportAttachmentRepository.findByIdentifier(attachment.getIdentifier())).thenReturn(attachment);

        service.delete(attachment.getIdentifier());
        verify(conditionReportAttachmentRepository).deleteById(attachment.getIdentifier());
    }

    @Test
    public void testFindByIdentifier() {
        final ConditionReportAttachment attachment = getConditionReportAttachment();
        when(conditionReportAttachmentRepository.findByIdentifier(attachment.getIdentifier())).thenReturn(attachment);

        final ConditionReportAttachment actual = service.findByIdentifier(attachment.getIdentifier());
        assertSame(attachment, actual);
    }

    @Test
    public void testFindByReport() {
        final String reportId = "cc5fee39-5108-4806-922f-492c8212681c";
        final ConditionReport report = new ConditionReport();
        report.setIdentifier(reportId);
        final List<ConditionReportAttachment> attachments = Collections.singletonList(new ConditionReportAttachment());

        when(conditionReportAttachmentRepository.findByReportIdentifier(reportId)).thenReturn(attachments);

        final List<ConditionReportAttachment> actual = service.findByReport(reportId);
        assertSame(attachments, actual);
    }

    @Test
    public void testUploadAttachment() throws UnsupportedEncodingException {
        // attachment
        final String attachmentId = "a8b18606-7302-4d66-bc9a-dae281ea0362";
        final String name = "Lorem_ipsum.txt";
        final String data = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras hendrerit elit ut ex dictum, quis bibendum nisi mollis. In posuere imperdiet lorem tempus suscipit.";
        final MockMultipartFile file = new MockMultipartFile(name, name, "text/plain", data.getBytes(StandardCharsets.UTF_8));
        // condition report
        final String reportId = "4cffb3f6-8322-4640-870b-b0452b00659c";
        final ConditionReport report = new ConditionReport();
        report.setIdentifier(reportId);

        when(conditionReportRepository.findById(reportId)).thenReturn(Optional.of(report));
        when(conditionReportAttachmentRepository.save(any(ConditionReportAttachment.class))).then(new SetIdAndReturnsArgumentAt(0, attachmentId));
        when(fm.copyInputStreamToFile(any(InputStream.class), anyString(), any(AbstractDomainObject.class), anyString())).thenReturn(new File(REPORT_DIR));

        final List<ConditionReportAttachment> actual = service.uploadAttachment(Collections.singletonList(file), reportId);

        assertEquals(1, actual.size());
        final ConditionReportAttachment actualAttachment = actual.get(0);

        assertEquals(report, actualAttachment.getReport());
        assertEquals(file.getSize(), actualAttachment.getFileSize().longValue());
        assertEquals(name, actualAttachment.getOriginalFilename());
        verify(fm).copyInputStreamToFile(any(InputStream.class), eq(REPORT_DIR), any(AbstractDomainObject.class), eq(actualAttachment.getOriginalFilename()));
    }

    @Test
    public void testDownloadAttachmentFile() {
        final ConditionReportAttachment attachment = getConditionReportAttachment();
        service.downloadAttachmentFile(attachment);

        verify(fm).retrieveFile(eq(REPORT_DIR), eq(attachment.getReport()), eq(attachment.getOriginalFilename()));
    }

    private ConditionReportAttachment getConditionReportAttachment() {
        final ConditionReport report = new ConditionReport();
        report.setIdentifier("fe8a33b6-88dc-4ea6-b0f4-c63ff8fa3097");

        final String attachmentId = "a8b18606-7302-4d66-bc9a-dae281ea0362";
        final ConditionReportAttachment attachment = new ConditionReportAttachment();
        attachment.setIdentifier(attachmentId);
        attachment.setOriginalFilename("test.txt");
        attachment.setReport(report);
        return attachment;
    }

}
