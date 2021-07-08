package fr.progilone.pgcn.web.rest.document.conditionreport;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReport;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReportDetail;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReportDetail.Type;
import fr.progilone.pgcn.service.document.conditionreport.ConditionReportDetailService;
import fr.progilone.pgcn.service.document.conditionreport.ConditionReportService;
import fr.progilone.pgcn.service.es.EsConditionReportService;
import fr.progilone.pgcn.util.TestUtil;
import fr.progilone.pgcn.web.util.AccessHelper;
import fr.progilone.pgcn.web.util.WorkflowAccessHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class ConditionReportDetailControllerTest {

    @Mock
    private AccessHelper accessHelper;
    @Mock
    private WorkflowAccessHelper workflowAccessHelper;
    @Mock
    private ConditionReportService conditionReportService;
    @Mock
    private ConditionReportDetailService conditionReportDetailService;
    @Mock
    private EsConditionReportService esConditionReportService;

    private MockMvc restMockMvc;

    @Before
    public void setUp() {
        final ConditionReportDetailController controller = new ConditionReportDetailController(accessHelper, workflowAccessHelper, conditionReportService, conditionReportDetailService, esConditionReportService);
        this.restMockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testCreate() throws Exception {
        final ConditionReportDetail detail = getConditionReportDetail();
        final String detailId = detail.getIdentifier();
        final DocUnit docUnit = detail.getReport().getDocUnit();
        final ConditionReportDetail.Type type = ConditionReportDetail.Type.DIGITALIZATION;

        when(conditionReportDetailService.findDocUnitByIdentifier(detailId)).thenReturn(docUnit);
        when(accessHelper.checkDocUnit(eq(docUnit.getIdentifier()))).thenReturn(false, true);
        when(conditionReportDetailService.create(type, detailId)).thenReturn(detail);
        when(conditionReportDetailService.findParentByIdentifier(detail.getIdentifier())).thenReturn(detail.getReport());

        // 403
        this.restMockMvc.perform(post("/api/rest/condreport_detail").param("type", type.name())
                                                                    .param("detail", detailId)
                                                                    .contentType(TestUtil.APPLICATION_JSON_UTF8)).andExpect(status().isForbidden());
        verify(esConditionReportService, never()).indexAsync(anyString());

        // 201
        this.restMockMvc.perform(post("/api/rest/condreport_detail").param("type", type.name())
                                                                    .param("detail", detailId)
                                                                    .contentType(TestUtil.APPLICATION_JSON_UTF8))
                        .andExpect(status().isCreated())
                        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                        .andExpect(jsonPath("identifier").value(detailId));
        verify(esConditionReportService).indexAsync(detail.getReport().getIdentifier());
    }

    @Test
    public void testDelete() throws Exception {
        final ConditionReportDetail detail = getConditionReportDetail();
        final String detailId = detail.getIdentifier();
        final DocUnit docUnit = detail.getReport().getDocUnit();

        when(conditionReportDetailService.findDocUnitByIdentifier(detailId)).thenReturn(docUnit);
        when(accessHelper.checkDocUnit(eq(docUnit.getIdentifier()))).thenReturn(false, true);
        when(conditionReportDetailService.findParentByIdentifier(detail.getIdentifier())).thenReturn(detail.getReport());
        when(conditionReportDetailService.findByIdentifier(any(String.class))).thenReturn(detail);
        when(workflowAccessHelper.canConstatDetailBeModified(any(String.class), any(Type.class))).thenReturn(true);

        // 403
        this.restMockMvc.perform(delete("/api/rest/condreport_detail/{id}", detailId).contentType(TestUtil.APPLICATION_JSON_UTF8))
                        .andExpect(status().isForbidden());
        verify(conditionReportDetailService, never()).delete(detailId);
        verify(esConditionReportService, never()).indexAsync(anyString());

        // 200
        this.restMockMvc.perform(delete("/api/rest/condreport_detail/{id}", detailId).contentType(TestUtil.APPLICATION_JSON_UTF8))
                        .andExpect(status().isOk());
        verify(conditionReportDetailService).delete(detailId);
        verify(esConditionReportService).indexAsync(detail.getReport().getIdentifier());
    }

    @Test
    public void testFindByIdentifier() throws Exception {
        final ConditionReportDetail detail = getConditionReportDetail();
        final String detailId = detail.getIdentifier();
        final DocUnit docUnit = detail.getReport().getDocUnit();

        when(conditionReportDetailService.findDocUnitByIdentifier(detailId)).thenReturn(docUnit);
        when(accessHelper.checkDocUnit(eq(docUnit.getIdentifier()))).thenReturn(false, true);
        when(conditionReportDetailService.findByIdentifier(detailId)).thenReturn(detail);

        // 403
        this.restMockMvc.perform(get("/api/rest/condreport_detail/{identifier}", detailId).accept(TestUtil.APPLICATION_JSON_UTF8))
                        .andExpect(status().isForbidden());

        // 200
        this.restMockMvc.perform(get("/api/rest/condreport_detail/{identifier}", detailId).accept(TestUtil.APPLICATION_JSON_UTF8))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                        .andExpect(jsonPath("identifier").value(detailId));
    }

    @Test
    public void testFindByConditionReport() throws Exception {
        final ConditionReportDetail detail = getConditionReportDetail();
        final String detailId = detail.getIdentifier();
        final String reportId = detail.getReport().getIdentifier();
        final String docUnitId = detail.getReport().getDocUnit().getIdentifier();
        final List<ConditionReportDetail> details = Collections.singletonList(detail);

        when(conditionReportService.findDocUnitByIdentifier(detail.getReport().getIdentifier())).thenReturn(detail.getReport().getDocUnit());
        when(accessHelper.checkDocUnit(eq(docUnitId))).thenReturn(false, true);
        when(conditionReportDetailService.findByConditionReport(reportId)).thenReturn(details);

        // 403
        this.restMockMvc.perform(get("/api/rest/condreport_detail").param("report", reportId).accept(TestUtil.APPLICATION_JSON_UTF8))
                        .andExpect(status().isForbidden());

        // 200
        this.restMockMvc.perform(get("/api/rest/condreport_detail").param("report", reportId).accept(TestUtil.APPLICATION_JSON_UTF8))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                        .andExpect(jsonPath("$[0].identifier").value(detailId));
    }

    @Test
    public void testUpdate() throws Exception {
        final ConditionReportDetail detail = getConditionReportDetail();
        final String detailId = detail.getIdentifier();
        final DocUnit docUnit = detail.getReport().getDocUnit();

        when(conditionReportDetailService.findDocUnitByIdentifier(detailId)).thenReturn(docUnit);
        when(accessHelper.checkDocUnit(eq(docUnit.getIdentifier()))).thenReturn(false, true);
        when(conditionReportDetailService.save(any(ConditionReportDetail.class))).thenReturn(detail);
        when(conditionReportDetailService.findParentByIdentifier(detail.getIdentifier())).thenReturn(detail.getReport());
        when(conditionReportDetailService.findByIdentifier(any(String.class))).thenReturn(detail);
        when(workflowAccessHelper.canConstatDetailBeModified(any(String.class), any(Type.class))).thenReturn(true);

        // 403
        this.restMockMvc.perform(post("/api/rest/condreport_detail/{id}", detailId).contentType(TestUtil.APPLICATION_JSON_UTF8)
                                                                                   .content(TestUtil.convertObjectToJsonBytes(detail)))
                        .andExpect(status().isForbidden());
        verify(esConditionReportService, never()).indexAsync(anyString());

        // 200
        this.restMockMvc.perform(post("/api/rest/condreport_detail/{id}", detailId).contentType(TestUtil.APPLICATION_JSON_UTF8)
                                                                                   .content(TestUtil.convertObjectToJsonBytes(detail)))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                        .andExpect(jsonPath("identifier").value(detailId));
        verify(esConditionReportService).indexAsync(detail.getReport().getIdentifier());
    }

    private ConditionReportDetail getConditionReportDetail() {
        final DocUnit docUnit = new DocUnit();
        docUnit.setIdentifier("139d6544-d809-4b35-8cf4-51df8cc3ad80");

        final ConditionReport report = new ConditionReport();
        report.setIdentifier("6ad3b23d-2568-4bdc-b2c5-aa33702a52d5");
        report.setDocUnit(docUnit);

        final ConditionReportDetail detail = new ConditionReportDetail();
        detail.setIdentifier("6ad3b23d-2568-4bdc-b2c5-aa33702a52d5");
        detail.setReport(report);

        return detail;
    }
}
