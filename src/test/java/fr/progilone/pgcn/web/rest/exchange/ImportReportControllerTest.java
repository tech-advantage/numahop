package fr.progilone.pgcn.web.rest.exchange;

import static fr.progilone.pgcn.util.SecurityRequestPostProcessors.roles;
import static fr.progilone.pgcn.web.rest.exchange.security.AuthorizationConstants.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.progilone.pgcn.domain.exchange.ImportReport;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.service.exchange.ImportReportService;
import fr.progilone.pgcn.util.TestConverterFactory;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Created by Sébastien on 22/12/2016.
 */
@ExtendWith(MockitoExtension.class)
public class ImportReportControllerTest {

    @Mock
    private ImportReportService importReportService;
    @Mock
    private LibraryAccesssHelper libraryAccesssHelper;

    private MockMvc restMockMvc;

    private final RequestPostProcessor allPermissions = roles(EXC_HAB0, EXC_HAB1);

    @BeforeEach
    public void setUp() {
        final ImportReportController controller = new ImportReportController(importReportService, libraryAccesssHelper);

        final FormattingConversionService convService = new DefaultFormattingConversionService();
        convService.addConverter(String.class, Library.class, TestConverterFactory.getConverter(Library.class));
        this.restMockMvc = MockMvcBuilders.standaloneSetup(controller).setConversionService(convService).build();
    }

    @Test
    public void testDelete() throws Exception {
        final ImportReport report = getImportReport("ABCD-1235");
        final String identifier = report.getIdentifier();

        when(importReportService.findByIdentifier(report.getIdentifier())).thenReturn(null, report);
        when(libraryAccesssHelper.checkLibrary(any(HttpServletRequest.class), any(ImportReport.class), any())).thenReturn(false, true);

        // 404
        this.restMockMvc.perform(delete("/api/rest/importreport/{id}", identifier).contentType(MediaType.APPLICATION_JSON).with(allPermissions)).andExpect(status().isNotFound());

        // 403
        this.restMockMvc.perform(delete("/api/rest/importreport/{id}", identifier).contentType(MediaType.APPLICATION_JSON).with(allPermissions)).andExpect(status().isForbidden());

        // test delete
        this.restMockMvc.perform(delete("/api/rest/importreport/{id}", identifier).contentType(MediaType.APPLICATION_JSON).with(allPermissions)).andExpect(status().isOk());

        verify(importReportService).delete(identifier);
    }

    @Test
    public void testFindAllByLibrary() throws Exception {
        final ImportReport report = getImportReport("ABCD-1236");
        final Page<ImportReport> page = new PageImpl<>(Collections.singletonList(report));

        when(importReportService.findAllByLibraryIn(any(), eq(0), eq(10))).thenReturn(page);

        // test delete
        this.restMockMvc.perform(get("/api/rest/importreport").param("library", "BNF").accept(MediaType.APPLICATION_JSON).with(allPermissions))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("content[0].identifier").value(report.getIdentifier()));
    }

    @Test
    public void findOne() throws Exception {
        final ImportReport report = getImportReport("ABCD-1238");
        when(importReportService.findByIdentifier(report.getIdentifier())).thenReturn(null, report);
        when(libraryAccesssHelper.checkLibrary(any(HttpServletRequest.class), any(ImportReport.class), any())).thenReturn(false, true);

        // 404
        this.restMockMvc.perform(get("/api/rest/importreport/{id}", report.getIdentifier()).accept(MediaType.APPLICATION_JSON).with(allPermissions))
                        .andExpect(status().isNotFound());

        // 403
        this.restMockMvc.perform(get("/api/rest/importreport/{id}", report.getIdentifier()).accept(MediaType.APPLICATION_JSON).with(allPermissions))
                        .andExpect(status().isForbidden());

        // test findAllActive
        this.restMockMvc.perform(get("/api/rest/importreport/{id}", report.getIdentifier()).accept(MediaType.APPLICATION_JSON).with(allPermissions))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("identifier").value(report.getIdentifier()));
    }

    @Test
    public void testGetById_notFound() throws Exception {
        final String identifier = "AAA";
        when(importReportService.findByIdentifier(identifier)).thenReturn(null);

        this.restMockMvc.perform(get("/api/rest/importreport/{identifier}", identifier).accept(MediaType.APPLICATION_JSON).with(allPermissions)).andExpect(status().isNotFound());
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
        this.restMockMvc.perform(get("/api/rest/importreport/{id}", report.getIdentifier()).param("status", "true").accept(MediaType.APPLICATION_JSON).with(allPermissions))
                        .andExpect(status().isNotFound());

        // 403
        this.restMockMvc.perform(get("/api/rest/importreport/{id}", report.getIdentifier()).param("status", "true").accept(MediaType.APPLICATION_JSON).with(allPermissions))
                        .andExpect(status().isForbidden());

        // test getStatus
        this.restMockMvc.perform(get("/api/rest/importreport/{id}", report.getIdentifier()).param("status", "true").accept(MediaType.APPLICATION_JSON).with(allPermissions))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("status").value("OK"));
    }

    private ImportReport getImportReport(final String identifier) {
        final ImportReport report = new ImportReport();
        report.setIdentifier(identifier);
        return report;
    }
}
