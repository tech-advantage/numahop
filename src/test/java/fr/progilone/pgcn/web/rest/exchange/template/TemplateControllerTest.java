package fr.progilone.pgcn.web.rest.exchange.template;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.progilone.pgcn.domain.exchange.template.Template;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.service.exchange.template.TemplateService;
import fr.progilone.pgcn.util.TestConverterFactory;
import fr.progilone.pgcn.util.TestUtil;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;
import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
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
public class TemplateControllerTest {

    private static final String TEMPLATE_DIR = FileUtils.getTempDirectoryPath() + "/pgcn_test";

    @Mock
    private LibraryAccesssHelper libraryAccesssHelper;
    @Mock
    private TemplateService templateService;

    private MockMvc restMockMvc;

    @BeforeEach
    public void setUp() {
        final TemplateController controller = new TemplateController(libraryAccesssHelper, templateService);
        final FormattingConversionService convService = new DefaultFormattingConversionService();
        convService.addConverter(String.class, Library.class, TestConverterFactory.getConverter(Library.class));
        this.restMockMvc = MockMvcBuilders.standaloneSetup(controller).setConversionService(convService).build();
    }

    @BeforeAll
    public static void init() throws IOException {
        FileUtils.forceMkdir(new File(TEMPLATE_DIR));
    }

    @AfterAll
    public static void clean() {
        FileUtils.deleteQuietly(new File(TEMPLATE_DIR));
    }

    @Test
    public void testCreate() throws Exception {
        final Template template = getTemplate();

        when(libraryAccesssHelper.checkLibrary(any(HttpServletRequest.class), any(), any(), any())).thenReturn(false, true);
        when(templateService.save(any(Template.class))).thenReturn(template);

        // 403
        this.restMockMvc.perform(post("/api/rest/template").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(new Template())))
                        .andExpect(status().isForbidden());

        // 201
        this.restMockMvc.perform(post("/api/rest/template").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(new Template())))
                        .andExpect(status().isCreated())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("identifier").value(template.getIdentifier()));
    }

    @Test
    public void testDelete() throws Exception {
        final Template template = getTemplate();
        final String identifier = template.getIdentifier();

        when(templateService.findByIdentifier(identifier)).thenReturn(null, template);
        when(libraryAccesssHelper.checkLibrary(any(HttpServletRequest.class), any(Library.class))).thenReturn(false, true);

        // 404
        this.restMockMvc.perform(delete("/api/rest/template/{id}", identifier).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
        verify(templateService, never()).delete(identifier);

        // 403
        this.restMockMvc.perform(delete("/api/rest/template/{id}", identifier).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
        verify(templateService, never()).delete(identifier);

        // 200
        this.restMockMvc.perform(delete("/api/rest/template/{id}", identifier).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        verify(templateService).delete(identifier);
    }

    @Test
    public void testFindTemplates() throws Exception {
        final Template template = getTemplate();
        final Library library = template.getLibrary();

        when(libraryAccesssHelper.checkLibrary(any(HttpServletRequest.class), any(Library.class))).thenReturn(false, true);
        when(templateService.findTemplate(library)).thenReturn(Collections.singletonList(template));

        // 403
        this.restMockMvc.perform(get("/api/rest/template").param("engine", "Velocity").param("library", library.getIdentifier()).contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isForbidden());

        // 200
        this.restMockMvc.perform(get("/api/rest/template").param("engine", "Velocity").param("library", library.getIdentifier()).contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$[0].identifier").value(template.getIdentifier()));
    }

    @Test
    public void testDownloadAttachment() throws Exception {
        final Template template = getTemplate();
        final String templateId = template.getIdentifier();
        final File file = createTmpFile(templateId);

        when(templateService.findByIdentifier(templateId)).thenReturn(null, template);
        when(libraryAccesssHelper.checkLibrary(any(HttpServletRequest.class), any(Library.class))).thenReturn(false, true);
        when(templateService.getTemplateFile(template)).thenReturn(file);

        // 404
        this.restMockMvc.perform(get("/api/rest/template/{id}", templateId).param("download", "true").accept(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                        .andExpect(status().isNotFound());

        // 403
        this.restMockMvc.perform(get("/api/rest/template/{id}", templateId).param("download", "true").accept(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                        .andExpect(status().isForbidden());

        // 200
        this.restMockMvc.perform(get("/api/rest/template/{id}", templateId).param("download", "true").accept(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                        .andExpect(content().string("Duis ut risus finibus, blandit orci in, convallis dolor."));
    }

    @Test
    public void testUploadAttachments() throws Exception {
        final Template template = getTemplate();
        final MockMultipartFile file = new MockMultipartFile("file",
                                                             "test.txt",
                                                             "text/plain",
                                                             "Duis ut risus finibus, blandit orci in, convallis dolor.".getBytes(StandardCharsets.UTF_8));

        when(templateService.findByIdentifier(template.getIdentifier())).thenReturn(null, template);
        when(templateService.save(template, file)).thenReturn(template);

        // 404
        this.restMockMvc.perform(multipart("/api/rest/template/{id}", template.getIdentifier()).file(file).param("upload", "true").accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound());

        // 403
        this.restMockMvc.perform(multipart("/api/rest/template/{id}", template.getIdentifier()).file(file).param("upload", "true").accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isForbidden());

        when(libraryAccesssHelper.checkLibrary(any(), any(Library.class))).thenReturn(true);

        // 200
        this.restMockMvc.perform(multipart("/api/rest/template/{id}", template.getIdentifier()).file(file).param("upload", "true").accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());
    }

    @Test
    public void testUpdate() throws Exception {
        final Template template = getTemplate();
        final Template dbTemplate = getTemplate();

        when(templateService.save(any(Template.class))).thenReturn(template);
        when(templateService.findByIdentifier(template.getIdentifier())).thenReturn(null, dbTemplate);
        when(libraryAccesssHelper.checkLibrary(any(HttpServletRequest.class), any(), any(), any())).thenReturn(false, true, true, false, true);

        // 403
        this.restMockMvc.perform(post("/api/rest/template/{id}", template.getIdentifier()).contentType(MediaType.APPLICATION_JSON)
                                                                                          .content(TestUtil.convertObjectToJsonBytes(template))).andExpect(status().isForbidden());

        // 404
        this.restMockMvc.perform(post("/api/rest/template/{id}", template.getIdentifier()).contentType(MediaType.APPLICATION_JSON)
                                                                                          .content(TestUtil.convertObjectToJsonBytes(template))).andExpect(status().isNotFound());

        // 403
        this.restMockMvc.perform(post("/api/rest/template/{id}", template.getIdentifier()).contentType(MediaType.APPLICATION_JSON)
                                                                                          .content(TestUtil.convertObjectToJsonBytes(template))).andExpect(status().isForbidden());

        // 200
        this.restMockMvc.perform(post("/api/rest/template/{id}", template.getIdentifier()).contentType(MediaType.APPLICATION_JSON)
                                                                                          .content(TestUtil.convertObjectToJsonBytes(template)))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("identifier").value(template.getIdentifier()));
    }

    private Template getTemplate() {
        final Library library = new Library();
        library.setIdentifier("ae0db278-4e83-4eab-81ef-b35b3e1ce2da");
        final Template template = new Template();
        template.setIdentifier("7ef8fdae-062d-4b03-bc0b-80eddb82b9ee");
        template.setLibrary(library);
        return template;
    }

    private File createTmpFile(final String id) throws IOException {
        final File file = new File(TEMPLATE_DIR, id);
        FileUtils.writeStringToFile(file, "Duis ut risus finibus, blandit orci in, convallis dolor.", StandardCharsets.UTF_8);
        return file;
    }
}
