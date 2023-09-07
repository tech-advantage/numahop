package fr.progilone.pgcn.web.rest.administration;

import static fr.progilone.pgcn.util.SecurityRequestPostProcessors.roles;
import static fr.progilone.pgcn.web.rest.administration.security.AuthorizationConstants.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.progilone.pgcn.domain.administration.MailboxConfiguration;
import fr.progilone.pgcn.domain.dto.administration.MailboxConfigurationDTO;
import fr.progilone.pgcn.domain.dto.library.SimpleLibraryDTO;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.service.administration.MailboxConfigurationService;
import fr.progilone.pgcn.util.TestConverterFactory;
import fr.progilone.pgcn.util.TestUtil;
import fr.progilone.pgcn.web.util.AccessHelper;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.internal.stubbing.answers.ReturnsArgumentAt;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Created by Sebastien on 30/12/2016.
 */
@ExtendWith(MockitoExtension.class)
public class MailboxConfigurationControllerTest {

    @Mock
    private AccessHelper accessHelper;
    @Mock
    private LibraryAccesssHelper libraryAccesssHelper;
    @Mock
    private MailboxConfigurationService mailboxConfigurationService;

    private MockMvc restMockMvc;

    private final RequestPostProcessor allPermissions = roles(SFTP_HAB0, SFTP_HAB1, SFTP_HAB2);

    @BeforeEach
    public void setUp() throws Exception {
        final MailboxConfigurationController controller = new MailboxConfigurationController(mailboxConfigurationService, accessHelper, libraryAccesssHelper);

        final FormattingConversionService convService = new DefaultFormattingConversionService();
        convService.addConverter(String.class, Library.class, TestConverterFactory.getConverter(Library.class));
        this.restMockMvc = MockMvcBuilders.standaloneSetup(controller).setConversionService(convService).build();

    }

    @Test
    public void testCreate() throws Exception {
        final MailboxConfiguration cConfigurationMail = getCConfigurationMail("ABCD-1234");
        when(mailboxConfigurationService.save(any(MailboxConfiguration.class))).thenReturn(cConfigurationMail);
        when(libraryAccesssHelper.checkLibrary(any(HttpServletRequest.class), any(), any(), any())).thenReturn(true);

        this.restMockMvc.perform(post("/api/rest/conf_mail").contentType(MediaType.APPLICATION_JSON)
                                                            .content(TestUtil.convertObjectToJsonBytes(cConfigurationMail))
                                                            .with(allPermissions))
                        .andExpect(status().isCreated())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("identifier").value(cConfigurationMail.getIdentifier()))
                        .andExpect(jsonPath("label").value(cConfigurationMail.getLabel()))
                        .andExpect(jsonPath("library.identifier").value(cConfigurationMail.getLibrary().getIdentifier()));
    }

    @Test
    public void testDelete() throws Exception {
        final MailboxConfiguration cConfigurationMail = getCConfigurationMail("ABCD-1235");
        final String identifier = cConfigurationMail.getIdentifier();

        when(libraryAccesssHelper.checkLibrary(any(HttpServletRequest.class), any(), any(), any())).thenReturn(true);

        when(mailboxConfigurationService.findOne(cConfigurationMail.getIdentifier())).thenReturn(cConfigurationMail);

        // test delete
        this.restMockMvc.perform(delete("/api/rest/conf_mail/{id}", identifier).contentType(MediaType.APPLICATION_JSON).with(allPermissions)).andExpect(status().isOk());

        verify(mailboxConfigurationService).delete(identifier);
    }

    @Test
    public void testFindAll() throws Exception {
        final List<MailboxConfigurationDTO> configurationMails = new ArrayList<>();
        configurationMails.add(getSimpleMappingDto("ABCD-1236"));

        when(mailboxConfigurationService.search(null, null, true)).thenReturn(configurationMails);
        when(libraryAccesssHelper.filterObjectsByLibrary(any(HttpServletRequest.class), any(), any())).thenAnswer(new ReturnsArgumentAt(1));

        // test findAllActive
        this.restMockMvc.perform(get("/api/rest/conf_mail").accept(MediaType.APPLICATION_JSON).with(allPermissions))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$[0].identifier").value(configurationMails.iterator().next().getIdentifier()));
    }

    @Test
    public void testFindByLibrary() throws Exception {
        final List<MailboxConfigurationDTO> configurationMails = new ArrayList<>();
        final MailboxConfigurationDTO cConfigurationMail = getSimpleMappingDto("ABCD-1237");
        configurationMails.add(cConfigurationMail);
        final Library library = new Library();
        library.setIdentifier(cConfigurationMail.getLibrary().getIdentifier());

        when(mailboxConfigurationService.search(isNull(), any(), eq(true))).thenReturn(configurationMails);
        when(libraryAccesssHelper.filterObjectsByLibrary(any(HttpServletRequest.class), any(), any())).thenAnswer(new ReturnsArgumentAt(1));

        // test findAllActive
        this.restMockMvc.perform(get("/api/rest/conf_mail").param("library", cConfigurationMail.getLibrary().getIdentifier())
                                                           .accept(MediaType.APPLICATION_JSON)
                                                           .with(allPermissions))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$[0].identifier").value(cConfigurationMail.getIdentifier()));
    }

    @Test
    public void getById() throws Exception {
        final MailboxConfiguration cConfigurationMail = getCConfigurationMail("ABCD-1238");
        when(mailboxConfigurationService.findOne(cConfigurationMail.getIdentifier())).thenReturn(cConfigurationMail) // ok
                                                                                     .thenReturn(null);             // ko
        when(libraryAccesssHelper.checkLibrary(any(HttpServletRequest.class), any(), any(), any())).thenReturn(true);

        // test ok
        this.restMockMvc.perform(get("/api/rest/conf_mail/{id}", cConfigurationMail.getIdentifier()).accept(MediaType.APPLICATION_JSON).with(allPermissions))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("identifier").value(cConfigurationMail.getIdentifier()));

        // test ko
        this.restMockMvc.perform(get("/api/rest/conf_mail/{identifier}", cConfigurationMail.getIdentifier()).accept(MediaType.APPLICATION_JSON).with(allPermissions))
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
        when(libraryAccesssHelper.checkLibrary(any(HttpServletRequest.class), any(), any(), any())).thenReturn(true);

        // test update
        this.restMockMvc.perform(post("/api/rest/conf_mail/" + identifier).contentType(MediaType.APPLICATION_JSON)
                                                                          .content(TestUtil.convertObjectToJsonBytes(cConfigurationMail))
                                                                          .with(allPermissions))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
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
