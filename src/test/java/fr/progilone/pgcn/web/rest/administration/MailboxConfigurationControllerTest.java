package fr.progilone.pgcn.web.rest.administration;

import fr.progilone.pgcn.domain.administration.MailboxConfiguration;
import fr.progilone.pgcn.domain.dto.administration.MailboxConfigurationDTO;
import fr.progilone.pgcn.domain.dto.library.SimpleLibraryDTO;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.service.administration.MailboxConfigurationService;
import fr.progilone.pgcn.util.TestConverterFactory;
import fr.progilone.pgcn.util.TestUtil;
import fr.progilone.pgcn.web.util.AccessHelper;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.stubbing.answers.ReturnsArgumentAt;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static fr.progilone.pgcn.util.SecurityRequestPostProcessors.*;
import static fr.progilone.pgcn.web.rest.administration.security.AuthorizationConstants.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Mockito.anyListOf;
import static org.mockito.Mockito.anyVararg;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by Sebastien on 30/12/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class MailboxConfigurationControllerTest {

    @Mock
    private AccessHelper accessHelper;
    @Mock
    private LibraryAccesssHelper libraryAccesssHelper;
    @Mock
    private MailboxConfigurationService mailboxConfigurationService;

    private MockMvc restMockMvc;

    private final RequestPostProcessor allPermissions = roles(SFTP_HAB0, SFTP_HAB1, SFTP_HAB2);

    @Before
    public void setUp() throws Exception {
        final MailboxConfigurationController controller =
            new MailboxConfigurationController(mailboxConfigurationService, accessHelper, libraryAccesssHelper);

        final FormattingConversionService convService = new DefaultFormattingConversionService();
        convService.addConverter(String.class, Library.class, TestConverterFactory.getConverter(Library.class));
        this.restMockMvc = MockMvcBuilders.standaloneSetup(controller).setConversionService(convService).build();

        when(libraryAccesssHelper.checkLibrary(any(HttpServletRequest.class), any(), any(), anyVararg())).thenReturn(true);
        when(libraryAccesssHelper.checkLibrary(any(HttpServletRequest.class), any(Library.class))).thenReturn(true);
        when(libraryAccesssHelper.filterObjectsByLibrary(any(HttpServletRequest.class),
                                                         anyCollectionOf(MailboxConfiguration.class),
                                                         any())).thenAnswer(new ReturnsArgumentAt(1));
    }

    @Test
    public void testCreate() throws Exception {
        final MailboxConfiguration cConfigurationMail = getCConfigurationMail("ABCD-1234");
        when(mailboxConfigurationService.save(any(MailboxConfiguration.class))).thenReturn(cConfigurationMail);

        this.restMockMvc.perform(post("/api/rest/conf_mail").contentType(TestUtil.APPLICATION_JSON_UTF8)
                                                            .content(TestUtil.convertObjectToJsonBytes(cConfigurationMail))
                                                            .with(allPermissions))
                        .andExpect(status().isCreated())
                        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                        .andExpect(jsonPath("identifier").value(cConfigurationMail.getIdentifier()))
                        .andExpect(jsonPath("label").value(cConfigurationMail.getLabel()))
                        .andExpect(jsonPath("library.identifier").value(cConfigurationMail.getLibrary().getIdentifier()));
    }

    @Test
    public void testDelete() throws Exception {
        final MailboxConfiguration cConfigurationMail = getCConfigurationMail("ABCD-1235");
        final String identifier = cConfigurationMail.getIdentifier();

        when(mailboxConfigurationService.findOne(cConfigurationMail.getIdentifier())).thenReturn(cConfigurationMail);

        // test delete
        this.restMockMvc.perform(delete("/api/rest/conf_mail/{id}", identifier).contentType(TestUtil.APPLICATION_JSON_UTF8).with(allPermissions))
                        .andExpect(status().isOk());

        verify(mailboxConfigurationService).delete(identifier);
    }

    @Test
    public void testFindAll() throws Exception {
        final List<MailboxConfigurationDTO> configurationMails = new ArrayList<>();
        configurationMails.add(getSimpleMappingDto("ABCD-1236"));

        when(mailboxConfigurationService.search(null, null, true)).thenReturn(configurationMails);

        // test findAllActive
        this.restMockMvc.perform(get("/api/rest/conf_mail").accept(TestUtil.APPLICATION_JSON_UTF8).with(allPermissions))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                        .andExpect(jsonPath("$[0].identifier").value(configurationMails.iterator().next().getIdentifier()));
    }

    @Test
    public void testFindByLibrary() throws Exception {
        final List<MailboxConfigurationDTO> configurationMails = new ArrayList<>();
        final MailboxConfigurationDTO cConfigurationMail = getSimpleMappingDto("ABCD-1237");
        configurationMails.add(cConfigurationMail);
        Library library = new Library();
        library.setIdentifier(cConfigurationMail.getLibrary().getIdentifier());

        when(mailboxConfigurationService.search(isNull(String.class), anyListOf(String.class), eq(true))).thenReturn(configurationMails);

        // test findAllActive
        this.restMockMvc.perform(get("/api/rest/conf_mail").param("library", cConfigurationMail.getLibrary().getIdentifier())
                                                           .accept(TestUtil.APPLICATION_JSON_UTF8)
                                                           .with(allPermissions))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                        .andExpect(jsonPath("$[0].identifier").value(cConfigurationMail.getIdentifier()));
    }

    @Test
    public void getById() throws Exception {
        final MailboxConfiguration cConfigurationMail = getCConfigurationMail("ABCD-1238");
        when(mailboxConfigurationService.findOne(cConfigurationMail.getIdentifier())).thenReturn(cConfigurationMail) // ok
                                                                                     .thenReturn(null);             // ko

        // test ok
        this.restMockMvc.perform(get("/api/rest/conf_mail/{id}", cConfigurationMail.getIdentifier()).accept(TestUtil.APPLICATION_JSON_UTF8)
                                                                                                    .with(allPermissions))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                        .andExpect(jsonPath("identifier").value(cConfigurationMail.getIdentifier()));

        // test ko
        this.restMockMvc.perform(get("/api/rest/conf_mail/{identifier}", cConfigurationMail.getIdentifier()).accept(TestUtil.APPLICATION_JSON_UTF8)
                                                                                                            .with(allPermissions))
                        .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdate() throws Exception {
        final MailboxConfiguration cConfigurationMail = getCConfigurationMail("ABCD-1239");

        // prepare update
        final String identifier = cConfigurationMail.getIdentifier();
        final MailboxConfiguration savedStat = getCConfigurationMail("ABCD-1239");
        savedStat.setLabel("New label");

        when(mailboxConfigurationService.findOne(cConfigurationMail.getIdentifier())).thenReturn(cConfigurationMail);
        when(mailboxConfigurationService.save(cConfigurationMail)).thenReturn(savedStat);

        // test update
        this.restMockMvc.perform(post("/api/rest/conf_mail/" + identifier).contentType(TestUtil.APPLICATION_JSON_UTF8)
                                                                          .content(TestUtil.convertObjectToJsonBytes(cConfigurationMail))
                                                                          .with(allPermissions))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                        .andExpect(jsonPath("identifier").value(savedStat.getIdentifier()))
                        .andExpect(jsonPath("label").value(savedStat.getLabel()))
                        .andExpect(jsonPath("library.identifier").value(savedStat.getLibrary().getIdentifier()));
    }

    private MailboxConfiguration getCConfigurationMail(final String identifier) {
        final Library library = new Library();
        library.setIdentifier("LIBRARY-001");

        final MailboxConfiguration cConfigurationMail = new MailboxConfiguration();
        cConfigurationMail.setIdentifier(identifier);
        cConfigurationMail.setLabel("Chou-fleur");
        cConfigurationMail.setLibrary(library);
        return cConfigurationMail;
    }

    private MailboxConfigurationDTO getSimpleMappingDto(final String identifier) {
        final MailboxConfigurationDTO dto = new MailboxConfigurationDTO();
        dto.setIdentifier(identifier);
        dto.setLabel("Chou-fleur");
        final SimpleLibraryDTO lib = new SimpleLibraryDTO.Builder().setIdentifier("LIBRARY-001").build();
        dto.setLibrary(lib);
        return dto;
    }
}
