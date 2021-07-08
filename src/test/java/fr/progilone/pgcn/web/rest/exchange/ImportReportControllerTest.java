package fr.progilone.pgcn.web.rest.exchange;

import fr.progilone.pgcn.domain.exchange.ImportReport;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.service.exchange.ImportReportService;
import fr.progilone.pgcn.util.TestConverterFactory;
import fr.progilone.pgcn.util.TestUtil;
import fr.progilone.pgcn.web.util.AccessHelper;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static fr.progilone.pgcn.util.SecurityRequestPostProcessors.*;
import static fr.progilone.pgcn.web.rest.exchange.security.AuthorizationConstants.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by Sébastien on 22/12/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class ImportReportControllerTest {

    @Mock
    private ImportReportService importReportService;
    @Mock
    private LibraryAccesssHelper libraryAccesssHelper;

    private MockMvc restMockMvc;

    private final RequestPostProcessor allPermissions = roles(EXC_HAB0, EXC_HAB1);

    @Before
    public void setUp() {
        final ImportReportController controller = new ImportReportController(importReportService, libraryAccesssHelper);

        final FormattingConversionService convService = new DefaultFormattingConversionService();
        convService.addConverter(String.class, Library.class, TestConverterFactory.getConverter(Library.class));
        this.restMockMvc = MockMvcBuilders.standaloneSetup(controller).setConversionService(convService).build();

        //        when(libraryAccesssHelper.checkLibrary(any(HttpServletRequest.class), any(), any())).thenReturn(true);
        when(libraryAccesssHelper.checkLibrary(any(HttpServletRequest.class), any(Library.class))).thenReturn(true);
    }

    @Test
    public void testDelete() throws Exception {
        final ImportReport report = getImportReport("ABCD-1235");
        final String identifier = report.getIdentifier();

        when(importReportService.findByIdentifier(report.getIdentifier())).thenReturn(null, report);
        when(libraryAccesssHelper.checkLibrary(any(HttpServletRequest.class), any(ImportReport.class), any())).thenReturn(false, true);

        // 404
        this.restMockMvc.perform(delete("/api/rest/importreport/{id}", identifier).contentType(TestUtil.APPLICATION_JSON_UTF8).with(allPermissions))
                        .andExpect(status().isNotFound());

        // 403
        this.restMockMvc.perform(delete("/api/rest/importreport/{id}", identifier).contentType(TestUtil.APPLICATION_JSON_UTF8).with(allPermissions))
                        .andExpect(status().isForbidden());

        // test delete
        this.restMockMvc.perform(delete("/api/rest/importreport/{id}", identifier).contentType(TestUtil.APPLICATION_JSON_UTF8).with(allPermissions))
                        .andExpect(status().isOk());

        verify(importReportService).delete(identifier);
    }

    @Test
    public void testFindAllByLibrary() throws Exception {
        final ImportReport report = getImportReport("ABCD-1236");
        final Page<ImportReport> page = new PageImpl<>(Collections.singletonList(report));

        when(importReportService.findAllByLibraryIn(anyListOf(String.class), eq(0), eq(10))).thenReturn(page);
        when(libraryAccesssHelper.checkLibrary(any(HttpServletRequest.class), any(Library.class))).thenReturn(false, true);

        // test delete
        this.restMockMvc.perform(get("/api/rest/importreport").param("library", "BNF").accept(TestUtil.APPLICATION_JSON_UTF8).with(allPermissions))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                        .andExpect(jsonPath("content[0].identifier").value(report.getIdentifier()));
    }

    @Test
    public void findOne() throws Exception {
        final ImportReport report = getImportReport("ABCD-1238");
        when(importReportService.findByIdentifier(report.getIdentifier())).thenReturn(null, report);
        when(libraryAccesssHelper.checkLibrary(any(HttpServletRequest.class), any(ImportReport.class), any())).thenReturn(false, true);

        // 404
        this.restMockMvc.perform(get("/api/rest/importreport/{id}", report.getIdentifier()).accept(TestUtil.APPLICATION_JSON_UTF8)
                                                                                           .with(allPermissions)).andExpect(status().isNotFound());

        // 403
        this.restMockMvc.perform(get("/api/rest/importreport/{id}", report.getIdentifier()).accept(TestUtil.APPLICATION_JSON_UTF8)
                                                                                           .with(allPermissions)).andExpect(status().isForbidden());

        // test findAllActive
        this.restMockMvc.perform(get("/api/rest/importreport/{id}", report.getIdentifier()).accept(TestUtil.APPLICATION_JSON_UTF8)
                                                                                           .with(allPermissions))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                        .andExpect(jsonPath("identifier").value(report.getIdentifier()));
    }

    @Test
    public void testGetById_notFound() throws Exception {
        final String identifier = "AAA";
        when(importReportService.findByIdentifier(identifier)).thenReturn(null);

        this.restMockMvc.perform(get("/api/rest/importreport/{identifier}", identifier).accept(TestUtil.APPLICATION_JSON_UTF8).with(allPermissions))
                        .andExpect(status().isNotFound());
    }

    @Test
    public void testGetStatus() throws Exception {
        final ImportReport report = getImportReport("ABCD-1238");
        final Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");

        when(importReportService.findByIdentifier(report.getIdentifier())).thenReturn(null, report);
        when(libraryAccesssHelper.checkLibrary(any(HttpServletRequest.class), any(ImportReport.class), any())).thenReturn(false, true);
        when(importReportService.getStatus(report)).thenReturn(response);

        // 404
        this.restMockMvc.perform(get("/api/rest/importreport/{id}", report.getIdentifier()).param("status", "true")
                                                                                           .accept(TestUtil.APPLICATION_JSON_UTF8)
                                                                                           .with(allPermissions)).andExpect(status().isNotFound());

        // 403
        this.restMockMvc.perform(get("/api/rest/importreport/{id}", report.getIdentifier()).param("status", "true")
                                                                                           .accept(TestUtil.APPLICATION_JSON_UTF8)
                                                                                           .with(allPermissions)).andExpect(status().isForbidden());

        // test getStatus
        this.restMockMvc.perform(get("/api/rest/importreport/{id}", report.getIdentifier()).param("status", "true")
                                                                                           .accept(TestUtil.APPLICATION_JSON_UTF8)
                                                                                           .with(allPermissions))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                        .andExpect(jsonPath("status").value("OK"));
    }

    private ImportReport getImportReport(final String identifier) {
        final ImportReport report = new ImportReport();
        report.setIdentifier(identifier);
        return report;
    }
}
