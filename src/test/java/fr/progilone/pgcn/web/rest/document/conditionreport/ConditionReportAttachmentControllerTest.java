package fr.progilone.pgcn.web.rest.document.conditionreport;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReport;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReportAttachment;
import fr.progilone.pgcn.service.document.conditionreport.ConditionReportAttachmentService;
import fr.progilone.pgcn.service.document.conditionreport.ConditionReportService;
import fr.progilone.pgcn.util.TestConverterFactory;
import fr.progilone.pgcn.web.util.AccessHelper;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
public class ConditionReportAttachmentControllerTest {

    private static final String REPORT_DIR = FileUtils.getTempDirectoryPath() + "/pgcn_test";

    @Mock
    private AccessHelper accessHelper;
    @Mock
    private ConditionReportService conditionReportService;
    @Mock
    private ConditionReportAttachmentService conditionReportAttachmentService;

    private MockMvc restMockMvc;

    @BeforeAll
    public static void init() throws IOException {
        FileUtils.forceMkdir(new File(REPORT_DIR));
    }

    @AfterAll
    public static void clean() {
        FileUtils.deleteQuietly(new File(REPORT_DIR));
    }

    @BeforeEach
    public void setUp() {
        final ConditionReportAttachmentController controller = new ConditionReportAttachmentController(accessHelper, conditionReportService, conditionReportAttachmentService);
        final FormattingConversionService convService = new DefaultFormattingConversionService();
        convService.addConverter(String.class, ConditionReport.class, TestConverterFactory.getConverter(ConditionReport.class));
        this.restMockMvc = MockMvcBuilders.standaloneSetup(controller).setConversionService(convService).build();
    }

    @Test
    public void testFindByReport() throws Exception {
        final ConditionReportAttachment attachment = getConditionReportAttachment();
        final ConditionReport report = attachment.getReport();
        final String docUnitId = attachment.getReport().getDocUnit().getIdentifier();

        when(conditionReportService.findDocUnitByIdentifier(report.getIdentifier())).thenReturn(report.getDocUnit());
        when(accessHelper.checkDocUnit(eq(docUnitId))).thenReturn(false, true);
        when(conditionReportAttachmentService.findByReport(report.getIdentifier())).thenReturn(Collections.singletonList(attachment));

        // 403
        this.restMockMvc.perform(get("/api/rest/condreport_attachment").param("report", report.getIdentifier()).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isForbidden());

        // 200
        this.restMockMvc.perform(get("/api/rest/condreport_attachment").param("report", report.getIdentifier()).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$[0].identifier").value(attachment.getIdentifier()));
    }

    @Test
    public void testDelete() throws Exception {
        final ConditionReportAttachment attachment = getConditionReportAttachment();
        final String attachmentId = attachment.getIdentifier();
        final DocUnit docUnit = attachment.getReport().getDocUnit();

        when(conditionReportAttachmentService.findDocUnitByIdentifier(attachmentId)).thenReturn(docUnit);
        when(accessHelper.checkDocUnit(eq(docUnit.getIdentifier()))).thenReturn(false, true);

        // 403
        this.restMockMvc.perform(delete("/api/rest/condreport_attachment/{id}", attachmentId).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
        verify(conditionReportAttachmentService, never()).delete(attachmentId);

        // 200
        this.restMockMvc.perform(delete("/api/rest/condreport_attachment/{id}", attachmentId).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        verify(conditionReportAttachmentService).delete(attachmentId);
    }

    @Test
    public void testDownloadAttachment() throws Exception {
        final ConditionReportAttachment attachment = getConditionReportAttachment();
        final String attachmentId = attachment.getIdentifier();
        final DocUnit docUnit = attachment.getReport().getDocUnit();
        final File file = createTmpFile(attachmentId);

        when(conditionReportAttachmentService.findDocUnitByIdentifier(attachmentId)).thenReturn(docUnit);
        when(accessHelper.checkDocUnit(eq(docUnit.getIdentifier()))).thenReturn(false, true);
        when(conditionReportAttachmentService.findByIdentifier(attachmentId)).thenReturn(null, attachment);
        when(conditionReportAttachmentService.downloadAttachmentFile(attachment)).thenReturn(file);

        // 403
        this.restMockMvc.perform(get("/api/rest/condreport_attachment/{id}", attachmentId).param("file", "true").accept(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                        .andExpect(status().isForbidden());

        // 404
        this.restMockMvc.perform(get("/api/rest/condreport_attachment/{id}", attachmentId).param("file", "true").accept(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                        .andExpect(status().isNotFound());

        // 200
        this.restMockMvc.perform(get("/api/rest/condreport_attachment/{id}", attachmentId).param("file", "true").accept(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                        .andExpect(content().string("Duis ut risus finibus, blandit orci in, convallis dolor."));
    }

    @Test
    public void testDownloadThumbnail() throws Exception {
        final ConditionReportAttachment attachment = getConditionReportAttachment();
        final String attachmentId = attachment.getIdentifier();
        final DocUnit docUnit = attachment.getReport().getDocUnit();
        final File file = createTmpFile(attachmentId);

        when(conditionReportAttachmentService.findDocUnitByIdentifier(attachmentId)).thenReturn(docUnit);
        when(accessHelper.checkDocUnit(eq(docUnit.getIdentifier()))).thenReturn(false, true);
        when(conditionReportAttachmentService.findByIdentifier(attachmentId)).thenReturn(null, attachment);
        when(conditionReportAttachmentService.downloadAttachmentThumbnail(attachment)).thenReturn(file);

        // 403
        this.restMockMvc.perform(get("/api/rest/condreport_attachment/{id}", attachmentId).param("thumbnail", "true").accept(MediaType.IMAGE_PNG_VALUE))
                        .andExpect(status().isForbidden());

        // 404
        this.restMockMvc.perform(get("/api/rest/condreport_attachment/{id}", attachmentId).param("thumbnail", "true").accept(MediaType.IMAGE_PNG_VALUE))
                        .andExpect(status().isNotFound());

        // 200
        this.restMockMvc.perform(get("/api/rest/condreport_attachment/{id}", attachmentId).param("thumbnail", "true").accept(MediaType.IMAGE_PNG_VALUE))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.IMAGE_PNG_VALUE))
                        .andExpect(content().string("Duis ut risus finibus, blandit orci in, convallis dolor."));
    }

    @Test
    public void testUploadAttachments() throws Exception {
        final ConditionReportAttachment attachment = getConditionReportAttachment();
        final ConditionReport report = attachment.getReport();
        final DocUnit docUnit = attachment.getReport().getDocUnit();
        final List<ConditionReportAttachment> attachments = Collections.singletonList(attachment);
        final MockMultipartFile file = new MockMultipartFile("file",
                                                             "test.txt",
                                                             "text/plain",
                                                             "Duis ut risus finibus, blandit orci in, convallis dolor.".getBytes(StandardCharsets.UTF_8));

        when(conditionReportService.findDocUnitByIdentifier(report.getIdentifier())).thenReturn(report.getDocUnit());
        when(accessHelper.checkDocUnit(eq(docUnit.getIdentifier()))).thenReturn(false, true);
        when(conditionReportAttachmentService.uploadAttachment(any(), eq(report.getIdentifier()))).thenReturn(attachments);

        // 403
        this.restMockMvc.perform(multipart("/api/rest/condreport_attachment").file(file).param("report", report.getIdentifier()).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isForbidden());

        // 200
        this.restMockMvc.perform(multipart("/api/rest/condreport_attachment").file(file).param("report", report.getIdentifier()).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());
    }

    private File createTmpFile(final String id) throws IOException {
        final File file = new File(REPORT_DIR, id);
        FileUtils.writeStringToFile(file, "Duis ut risus finibus, blandit orci in, convallis dolor.", StandardCharsets.UTF_8);
        return file;
    }

    private ConditionReportAttachment getConditionReportAttachment() {
        final DocUnit docUnit = new DocUnit();
        docUnit.setIdentifier("139d6544-d809-4b35-8cf4-51df8cc3ad80");

        final ConditionReport report = new ConditionReport();
        report.setIdentifier("bd34e266-1c4e-41f5-8f6c-76a852931282");
        report.setDocUnit(docUnit);

        final ConditionReportAttachment attachment = new ConditionReportAttachment();
        attachment.setIdentifier("a3d29beb-5523-4d12-892f-97c691c35140");
        attachment.setReport(report);
        return attachment;
    }
}
