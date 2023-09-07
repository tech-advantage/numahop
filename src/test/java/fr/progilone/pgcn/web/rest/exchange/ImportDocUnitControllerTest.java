package fr.progilone.pgcn.web.rest.exchange;

import static fr.progilone.pgcn.util.SecurityRequestPostProcessors.roles;
import static fr.progilone.pgcn.web.rest.exchange.security.AuthorizationConstants.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.progilone.pgcn.domain.exchange.ImportReport;
import fr.progilone.pgcn.domain.exchange.ImportedDocUnit;
import fr.progilone.pgcn.service.exchange.ImportDocUnitService;
import fr.progilone.pgcn.service.exchange.ImportReportService;
import fr.progilone.pgcn.util.TestConverterFactory;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
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
public class ImportDocUnitControllerTest {

    @Mock
    private ImportDocUnitService importDocUnitService;
    @Mock
    private ImportReportService importReportService;
    @Mock
    private LibraryAccesssHelper libraryAccesssHelper;

    private MockMvc restMockMvc;

    private final RequestPostProcessor allPermissions = roles(EXC_HAB0, EXC_HAB2);

    @BeforeEach
    public void setUp() {
        final ImportedDocUnitController controller = new ImportedDocUnitController(importDocUnitService, importReportService, libraryAccesssHelper);

        final FormattingConversionService convService = new DefaultFormattingConversionService();
        convService.addConverter(String.class, ImportReport.class, TestConverterFactory.getConverter(ImportReport.class));
        this.restMockMvc = MockMvcBuilders.standaloneSetup(controller).setConversionService(convService).build();

        when(libraryAccesssHelper.checkLibrary(any(HttpServletRequest.class), any(), any(), any())).thenReturn(true);
    }

    @Test
    public void testFindAll() throws Exception {
        final ImportReport report = new ImportReport();
        report.setIdentifier("ABCD-0001");
        final ImportedDocUnit units = new ImportedDocUnit();
        units.setIdentifier("UNIT-001");
        final Page<ImportedDocUnit> page = new PageImpl<>(Collections.singletonList(units));

        when(importReportService.findByIdentifier(report.getIdentifier())).thenReturn(report);
        when(importDocUnitService.findByImportReport(eq(report), eq(0), eq(10), any(), anyBoolean(), anyBoolean())).thenReturn(page);

        // test findAllActive
        this.restMockMvc.perform(get("/api/rest/impdocunit").param("report", report.getIdentifier())
                                                            .param("state", "AVAILABLE")
                                                            .accept(MediaType.APPLICATION_JSON)
                                                            .with(allPermissions))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("content[0].identifier").value(units.getIdentifier()));
    }

    @Test
    public void testUpdate() throws Exception {
        final ImportedDocUnit units = new ImportedDocUnit();
        units.setIdentifier("UNIT-001");

        when(importDocUnitService.findByIdentifier(units.getIdentifier())).thenReturn(units);

        // test update
        this.restMockMvc.perform(post("/api/rest/impdocunit/" + units.getIdentifier()).param("process", "ADD").contentType(MediaType.APPLICATION_JSON).with(allPermissions))
                        .andExpect(status().isOk());

        verify(importDocUnitService).updateProcess(units.getIdentifier(), ImportedDocUnit.Process.ADD);
    }
}
